const fs = require('node:fs');
const path = require('node:path');
const http = require('node:http');
const crypto = require('node:crypto');

const port = Number(process.env.PORT || 3000);
const webRoot = path.join(__dirname, '..', 'web-preview');
const dataDir = path.join(__dirname, '..', 'data');
const dataFile = path.join(dataDir, 'zox.json');
const moderatorsFile = path.join(dataDir, 'moderators.json');
const operationsFile = path.join(dataDir, 'operations.json');
const faresFile = path.join(dataDir, 'fares.json');
const walletFile = path.join(dataDir, 'wallet-ledger.json');
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
function readOperations() {
  if (!fs.existsSync(operationsFile)) fs.writeFileSync(operationsFile, '[]');
  return JSON.parse(fs.readFileSync(operationsFile, 'utf8'));
}
function writeOperations(operations) { fs.writeFileSync(operationsFile, JSON.stringify(operations, null, 2)); }
function readFares() { if (!fs.existsSync(faresFile)) fs.writeFileSync(faresFile, '[]'); return JSON.parse(fs.readFileSync(faresFile, 'utf8')); }
function writeFares(fares) { fs.writeFileSync(faresFile, JSON.stringify(fares, null, 2)); }
function readWalletLedger() { if (!fs.existsSync(walletFile)) fs.writeFileSync(walletFile, '[]'); return JSON.parse(fs.readFileSync(walletFile, 'utf8')); }
function writeWalletLedger(entries) { fs.writeFileSync(walletFile, JSON.stringify(entries, null, 2)); }
const sessions = new Map();
const pinRequests = new Map();
const adminPhone = process.env.ADMIN_PHONE || '9378160106';
const counterPhone = process.env.COUNTER_PHONE || '1122334455';
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
      if (!input?.phone || ![adminPhone, counterPhone].includes(String(input.phone)) && !readModerators().some((item) => item.phone === String(input.phone))) return sendJson(response, 403, { error: 'Account is not authorized for staff access' });
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
      const role = pending.phone === adminPhone ? 'SUPER_ADMIN' : pending.phone === counterPhone ? 'COUNTER_STAFF' : 'MODERATOR';
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
  if (request.method === 'GET' && request.url === '/api/admin/operations') {
    if (!requireRole(request, ['SUPER_ADMIN', 'MODERATOR', 'COUNTER_STAFF'])) return sendJson(response, 403, { error: 'Staff access required' });
    return sendJson(response, 200, { bookings: readBookings(), operations: readOperations() });
  }
  if (request.method === 'POST' && request.url === '/api/admin/operations') {
    if (!requireRole(request, ['SUPER_ADMIN', 'MODERATOR', 'COUNTER_STAFF'])) return sendJson(response, 403, { error: 'Staff access required' });
    return readJson(request).then((input) => {
      const allowedTypes = ['REQUEST', 'COMPLAINT', 'EMERGENCY', 'FEEDBACK', 'ANNOUNCEMENT', 'CASH_REQUEST', 'USER', 'VEHICLE_SERVICE_REQUEST', 'TICKET_BOOKING', 'REFUND_REQUEST', 'CRASH_REPORT', 'BREAKDOWN_REPORT', 'LOCATION_SHARE', 'STAFF_CONTACT'];
      if (!allowedTypes.includes(input?.type) || !String(input?.message || '').trim()) return sendJson(response, 400, { error: 'Operation type and message are required' });
      const user = authUser(request);
      const operation = { id: crypto.randomUUID(), type: input.type, message: String(input.message).trim(), status: 'OPEN', createdBy: user.phone, createdAt: new Date().toISOString() };
      const operations = readOperations(); operations.unshift(operation); writeOperations(operations);
      return sendJson(response, 201, operation);
    });
  }
  if (request.method === 'POST' && request.url === '/api/counter/vehicle-service-request') {
    if (!requireRole(request, ['COUNTER_STAFF'])) return sendJson(response, 403, { error: 'Counter Staff access required' });
    return readJson(request).then((input) => {
      if (!input?.vehicleId || !String(input.service || '').trim()) return sendJson(response, 400, { error: 'Vehicle and service details are required' });
      const user = authUser(request); const operations = readOperations();
      const item = { id: crypto.randomUUID(), type: 'VEHICLE_SERVICE_REQUEST', vehicleId: String(input.vehicleId), message: String(input.service).trim(), location: input.location || null, status: 'PENDING_APPROVAL', createdBy: user.phone, createdAt: new Date().toISOString() };
      operations.unshift(item); writeOperations(operations); return sendJson(response, 201, item);
    });
  }
  if (request.method === 'POST' && request.url === '/api/counter/fares') {
    if (!requireRole(request, ['COUNTER_STAFF'])) return sendJson(response, 403, { error: 'Counter Staff access required' });
    return readJson(request).then((input) => {
      const route = String(input?.route || '').trim(); const vehicleType = String(input?.vehicleType || '').trim(); const amount = Number(input?.amount);
      if (!route || !vehicleType || !Number.isFinite(amount) || amount <= 0) return sendJson(response, 400, { error: 'Route, vehicle type and positive fare are required' });
      const fare = { id: crypto.randomUUID(), route, vehicleType, amount: Math.round(amount * 100) / 100, updatedBy: authUser(request).phone, updatedAt: new Date().toISOString() };
      const fares = readFares().filter((item) => item.route !== route || item.vehicleType !== vehicleType); fares.unshift(fare); writeFares(fares);
      return sendJson(response, 201, fare);
    });
  }
  if (request.method === 'POST' && request.url === '/api/counter/ticket-booking') {
    if (!requireRole(request, ['COUNTER_STAFF'])) return sendJson(response, 403, { error: 'Counter Staff access required' });
    return readJson(request).then((input) => {
      if (!input?.passenger || !input?.destination || !input?.vehicleId || !input?.route || !input?.vehicleType) return sendJson(response, 400, { error: 'Passenger, route, vehicle type, vehicle and destination are required' });
      const user = authUser(request); const bookings = readBookings();
      const fare = readFares().find((item) => item.route === String(input.route) && item.vehicleType === String(input.vehicleType));
      const amount = Number(fare?.amount || 0); const commissionRate = Number(process.env.COMMISSION_RATE || 10); const commission = Math.round(amount * commissionRate) / 100;
      if (amount <= 0) return sendJson(response, 400, { error: 'No fare configured for this route and vehicle' });
      const ticket = { id: crypto.randomUUID(), service: 'Route Passenger Ticket', passenger: input.passenger, route: input.route, vehicleType: input.vehicleType, destination: input.destination, vehicleId: input.vehicleId, amount, commissionRate, commission, status: 'BOOKED', bookedBy: user.phone, destinationReached: false, createdAt: new Date().toISOString() };
      bookings.unshift(ticket); writeBookings(bookings);
      const ledger = readWalletLedger(); ledger.unshift({ id: crypto.randomUUID(), account: user.phone, type: 'COMMISSION', ticketId: ticket.id, amount: commission, status: 'CREDITED', createdAt: new Date().toISOString() }); writeWalletLedger(ledger);
      return sendJson(response, 201, ticket);
    });
  }
  if (request.method === 'POST' && request.url === '/api/counter/refund-request') {
    if (!requireRole(request, ['COUNTER_STAFF'])) return sendJson(response, 403, { error: 'Counter Staff access required' });
    return readJson(request).then((input) => {
      if (!input?.ticketId || !input?.reason) return sendJson(response, 400, { error: 'Ticket and refund reason are required' });
      const user = authUser(request); const operations = readOperations();
      const item = { id: crypto.randomUUID(), type: 'REFUND_REQUEST', ticketId: input.ticketId, message: String(input.reason), refundFee: Math.max(0, Number(input.refundFee || 0)), status: 'PENDING_APPROVAL', createdBy: user.phone, createdAt: new Date().toISOString() };
      operations.unshift(item); writeOperations(operations); return sendJson(response, 201, item);
    });
  }
  if (request.method === 'POST' && request.url === '/api/counter/incident') {
    if (!requireRole(request, ['COUNTER_STAFF'])) return sendJson(response, 403, { error: 'Counter Staff access required' });
    return readJson(request).then((input) => {
      const allowed = ['CRASH_REPORT', 'BREAKDOWN_REPORT', 'EMERGENCY', 'LOCATION_SHARE', 'STAFF_CONTACT'];
      if (!allowed.includes(input?.type) || !input?.vehicleId || !input?.message) return sendJson(response, 400, { error: 'Incident type, vehicle and message are required' });
      const user = authUser(request); const operations = readOperations();
      const item = { id: crypto.randomUUID(), type: input.type, vehicleId: String(input.vehicleId), message: String(input.message), location: input.location || null, contactChannel: input.contactChannel || 'MESSAGE', status: 'OPEN', createdBy: user.phone, createdAt: new Date().toISOString() };
      operations.unshift(item); writeOperations(operations); return sendJson(response, 201, item);
    });
  }
  if (request.method === 'POST' && request.url.startsWith('/api/admin/operations/') && request.url.endsWith('/approve')) {
    if (!requireRole(request, ['SUPER_ADMIN', 'MODERATOR', 'COUNTER_STAFF'])) return sendJson(response, 403, { error: 'Staff access required' });
    const id = request.url.split('/').slice(-2)[0];
    const operations = readOperations();
    const index = operations.findIndex((item) => item.id === id && ['CASH_REQUEST', 'VEHICLE_SERVICE_REQUEST', 'REFUND_REQUEST'].includes(item.type));
    if (index < 0) return sendJson(response, 404, { error: 'Approval request not found' });
    if (['VEHICLE_SERVICE_REQUEST', 'REFUND_REQUEST'].includes(operations[index].type) && !requireRole(request, ['SUPER_ADMIN', 'MODERATOR'])) return sendJson(response, 403, { error: 'Moderator or Super Admin approval required' });
    operations[index].status = 'APPROVED'; operations[index].approvedAt = new Date().toISOString();
    writeOperations(operations); return sendJson(response, 200, operations[index]);
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
