const fs = require('node:fs');
const path = require('node:path');
const http = require('node:http');

const port = Number(process.env.PORT || 3000);
const webRoot = path.join(__dirname, '..', 'web-preview');
const dataDir = path.join(__dirname, '..', 'data');
const dataFile = path.join(dataDir, 'zox.json');
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
function sendJson(response, status, body) {
  response.writeHead(status, { 'Content-Type': 'application/json; charset=utf-8', 'Access-Control-Allow-Origin': '*' });
  response.end(JSON.stringify(body));
}
function sendPage(response) {
  response.writeHead(200, { 'Content-Type': 'text/html; charset=utf-8' });
  response.end(fs.readFileSync(path.join(webRoot, 'index.html')));
}

const server = http.createServer((request, response) => {
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
