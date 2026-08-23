const fs = require('node:fs');
const path = require('node:path');
const http = require('node:http');
const crypto = require('node:crypto');

const port = Number(process.env.PORT || 3000);
const webRoot = path.join(__dirname, '..', 'web-preview');
const dataDir = path.join(__dirname, '..', 'data');
const dataFile = path.join(dataDir, 'zox.json');
const moderatorsFile = path.join(dataDir, 'moderators.json');
fs.mkdirSync(dataDir, { recursive: true });

const initialBookings = [
  { id: 1, service: 'Motor Hire', pickup: 'Chanmari Hub', destination: 'Chanmari Hub', amount: 480, status: 'ON THE WAY' },
  { id: 2, service: 'Tirhkah Express', pickup: 'Civil Hospital', destination: 'Khatla South', amount: 125, status: 'COMPLETED' }
];
function readBookings() {
  if (!fs.existsSync(dataFile)) fs.writeFileSync(dataFile, JSON.stringify(initialBookings, null, 2));
  return JSON.parse(fs.readFileSync(dataFile, 'utf8'));
}
function writeBookings(bookings) { fs.writeFileSync(dataFile, JSON.stringify(bookings, null, 2)); }
function readModerators() {
  if (!fs.existsSync(moderatorsFile)) fs.writeFileSync(moderatorsFile, '[]');
  return JSON.parse(fs.readFileSync(moderatorsFile, 'utf8'));
}
function writeModerators(moderators) { fs.writeFileSync(moderatorsFile, JSON.stringify(moderators, null, 2)); }
const sessions = new Map();
const pinRequests = new Map();
const adminPhone = process.env.ADMIN_PHONE || '9378160106';
const adminPin = process.env.ADMIN_PIN;
function readJson(request) {
  return new Promise((resolve) => {
    let body = '';
    request.on('data', (chunk) => { body += chunk; });
    request.on('end', () => { try { resolve(JSON.parse(body || '{}')); } catch { resolve(null); } });
  });
}
function authUser(request) {
  const token = request.headers.authorization?.replace(/^Bearer\s+/i, '');
  return token ? sessions.get(token) : null;
}
function requireRole(request, roles) {
  const user = authUser(request);
  return user && roles.includes(user.role) ? user : null;
}
function sendJson(response, status, body) {
  response.writeHead(status, { 'Content-Type': 'application/json; charset=utf-8', 'Access-Control-Allow-Origin': '*' });
  response.end(JSON.stringify(body));
}
function sendPage(response) {
  response.writeHead(200, { 'Content-Type': 'text/html; charset=utf-8' });
  response.end(fs.readFileSync(path.join(webRoot, 'index.html')));
}

const server = http.createServer((request, response) => {
  if (request.method === 'POST' && request.url === '/api/auth/request-pin') {
    return readJson(request).then((input) => {
      if (!input?.phone || String(input.phone) !== adminPhone && !readModerators().some((item) => item.phone === String(input.phone))) return sendJson(response, 403, { error: 'Account is not authorized for staff access' });
      const requestId = crypto.randomUUID();
      const pin = process.env.NODE_ENV === 'production' ? null : (adminPin || '123456');
      pinRequests.set(requestId, { phone: String(input.phone), pin, expiresAt: Date.now() + 5 * 60 * 1000 });
      return sendJson(response, 200, { requestId, message: 'Authentication PIN requested. Check your configured delivery channel.', demoPin: process.env.NODE_ENV === 'production' ? undefined : pin });
    });
  }
  if (request.method === 'POST' && request.url === '/api/auth/verify-pin') {
    return readJson(request).then((input) => {
      const pending = pinRequests.get(input?.requestId);
      if (!pending || pending.expiresAt < Date.now() || String(input.pin) !== String(pending.pin)) return sendJson(response, 401, { error: 'Invalid or expired authentication PIN' });
      pinRequests.delete(input.requestId);
      const role = pending.phone === adminPhone ? 'SUPER_ADMIN' : 'MODERATOR';
      const token = crypto.randomBytes(32).toString('hex');
      sessions.set(token, { phone: pending.phone, role, createdAt: Date.now() });
      return sendJson(response, 200, { token, role });
    });
  }
  if (request.method === 'GET' && request.url === '/api/admin/moderators') {
    if (!requireRole(request, ['SUPER_ADMIN'])) return sendJson(response, 403, { error: 'Super Admin access required' });
    return sendJson(response, 200, { moderators: readModerators() });
  }
  if (request.method === 'POST' && request.url === '/api/admin/moderators') {
    if (!requireRole(request, ['SUPER_ADMIN'])) return sendJson(response, 403, { error: 'Super Admin access required' });
    return readJson(request).then((input) => {
      const phone = String(input?.phone || '').replace(/\D/g, '');
      const name = String(input?.name || '').trim();
      if (!/^\d{10}$/.test(phone) || !name) return sendJson(response, 400, { error: 'Name and 10-digit phone are required' });
      const moderators = readModerators();
      if (phone === adminPhone || moderators.some((item) => item.phone === phone)) return sendJson(response, 409, { error: 'Account already exists' });
      const moderator = { id: crypto.randomUUID(), name, phone, createdAt: new Date().toISOString() };
      writeModerators([moderator, ...moderators]);
      return sendJson(response, 201, moderator);
    });
  }
  if (request.method === 'DELETE' && request.url.startsWith('/api/admin/moderators/')) {
    if (!requireRole(request, ['SUPER_ADMIN'])) return sendJson(response, 403, { error: 'Super Admin access required' });
    const id = request.url.split('/').pop();
    const moderators = readModerators();
    const next = moderators.filter((item) => item.id !== id);
    if (next.length === moderators.length) return sendJson(response, 404, { error: 'Moderator not found' });
    writeModerators(next);
    return sendJson(response, 200, { ok: true });
  }
  if (request.method === 'GET' && request.url === '/api/health') return sendJson(response, 200, { ok: true, service: 'zox-api' });
  if (request.method === 'GET' && request.url === '/api/summary') {
    const bookings = readBookings();
    return sendJson(response, 200, {
      user: { name: 'Lalremruata Ralte', city: 'Aizawl, Mizoram', wallet: 1450, coins: 380 },
      metrics: { activeBookings: bookings.filter((booking) => booking.status !== 'COMPLETED').length, totalBookings: bookings.length },
      bookings
    });
  }
  if (request.method === 'POST' && request.url === '/api/bookings') {
    let body = '';
    request.on('data', (chunk) => { body += chunk; });
    request.on('end', () => {
      try {
        const input = JSON.parse(body);
        if (!input.service || !input.pickup || !input.destination || Number(input.amount) <= 0) return sendJson(response, 400, { error: 'Invalid booking' });
        const bookings = readBookings();
        const booking = { id: Date.now(), service: String(input.service), pickup: String(input.pickup), destination: String(input.destination), amount: Number(input.amount), status: 'CONFIRMED' };
        bookings.unshift(booking); writeBookings(bookings); sendJson(response, 201, booking);
      } catch (_error) { sendJson(response, 400, { error: 'Invalid JSON' }); }
    });
    return;
  }
  if (request.method === 'GET') return sendPage(response);
  sendJson(response, 404, { error: 'Not found' });
});

server.listen(port, () => console.log(`ZOX web app running at http://localhost:${port}`));
