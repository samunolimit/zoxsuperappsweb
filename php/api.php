<?php
declare(strict_types=1);

header('Content-Type: application/json; charset=utf-8');
header('Access-Control-Allow-Origin: *');
header('Access-Control-Allow-Headers: Content-Type');
header('Access-Control-Allow-Methods: GET, POST, OPTIONS');

if ($_SERVER['REQUEST_METHOD'] === 'OPTIONS') {
    http_response_code(204);
    exit;
}

$dataDir = dirname(__DIR__) . '/data';
$dataFile = $dataDir . '/zox.json';
$moderatorsFile = $dataDir . '/moderators.json';
$authFile = $dataDir . '/auth.json';
$operationsFile = $dataDir . '/operations.json';
$initialBookings = [
    ['id' => 1, 'service' => 'Motor Hire', 'pickup' => 'Chanmari Hub', 'destination' => 'Chanmari Hub', 'amount' => 480, 'status' => 'ON THE WAY'],
    ['id' => 2, 'service' => 'Tirhkah Express', 'pickup' => 'Civil Hospital', 'destination' => 'Khatla South', 'amount' => 125, 'status' => 'COMPLETED'],
];

function bookings(string $file, string $dir, array $initial): array {
    if (!is_dir($dir)) mkdir($dir, 0775, true);
    if (!file_exists($file)) file_put_contents($file, json_encode($initial, JSON_PRETTY_PRINT));
    $decoded = json_decode((string) file_get_contents($file), true);
    return is_array($decoded) ? $decoded : $initial;
}
function reply(array $body, int $status = 200): never {
    http_response_code($status);
    echo json_encode($body);
    exit;
}
function stored(string $file, string $default = '[]'): array {
    if (!file_exists($file)) file_put_contents($file, $default);
    $value = json_decode((string) file_get_contents($file), true);
    return is_array($value) ? $value : [];
}
function saveStored(string $file, array $value): void { file_put_contents($file, json_encode($value, JSON_PRETTY_PRINT), LOCK_EX); }
function staffUser(string $authFile): ?array {
    $header = $_SERVER['HTTP_AUTHORIZATION'] ?? '';
    $token = preg_replace('/^Bearer\s+/i', '', $header);
    foreach (stored($authFile) as $session) if (hash_equals($session['token'], $token) && $session['expiresAt'] > time()) return $session;
    return null;
}

$route = parse_url($_SERVER['REQUEST_URI'] ?? '', PHP_URL_PATH) ?: '';
if ($route === '/api/health' || str_ends_with($route, '/health')) reply(['ok' => true, 'service' => 'zox-php-api']);

$input = json_decode((string) file_get_contents('php://input'), true);
$current = bookings($dataFile, $dataDir, $initialBookings);
$staff = staffUser($authFile);
if ($_SERVER['REQUEST_METHOD'] === 'POST' && str_starts_with($route, '/api/counter/')) {
    if (($staff['role'] ?? '') !== 'COUNTER_STAFF') reply(['error' => 'Counter Staff access required'], 403);
    $kind = basename($route);
    $types = ['vehicle-service-request' => 'VEHICLE_SERVICE_REQUEST', 'refund-request' => 'REFUND_REQUEST', 'incident' => (string) ($input['type'] ?? '')];
    if ($kind === 'ticket-booking') {
        if (empty($input['passenger']) || empty($input['vehicleId']) || empty($input['destination'])) reply(['error' => 'Passenger, vehicle and destination are required'], 400);
        $ticket = ['id' => bin2hex(random_bytes(12)), 'service' => 'Route Passenger Ticket', 'passenger' => (string) $input['passenger'], 'vehicleId' => (string) $input['vehicleId'], 'destination' => (string) $input['destination'], 'amount' => (float) ($input['amount'] ?? 0), 'status' => 'BOOKED', 'bookedBy' => $staff['phone'], 'destinationReached' => false, 'createdAt' => date(DATE_ATOM)];
        saveStored($dataFile, array_merge([$ticket], $current)); reply($ticket, 201);
    }
    if ($kind === 'vehicle-service-request' && (empty($input['vehicleId']) || empty($input['service']))) reply(['error' => 'Vehicle and service details are required'], 400);
    if ($kind === 'refund-request' && (empty($input['ticketId']) || empty($input['reason']))) reply(['error' => 'Ticket and refund reason are required'], 400);
    if ($kind === 'incident' && (!in_array($types[$kind], ['CRASH_REPORT', 'BREAKDOWN_REPORT', 'EMERGENCY', 'LOCATION_SHARE', 'STAFF_CONTACT'], true) || empty($input['vehicleId']) || empty($input['message']))) reply(['error' => 'Incident type, vehicle and message are required'], 400);
    $operation = ['id' => bin2hex(random_bytes(12)), 'type' => $types[$kind], 'vehicleId' => $input['vehicleId'] ?? null, 'ticketId' => $input['ticketId'] ?? null, 'message' => (string) ($input['service'] ?? $input['reason'] ?? $input['message'] ?? ''), 'location' => $input['location'] ?? null, 'refundFee' => (float) ($input['refundFee'] ?? 0), 'contactChannel' => $input['contactChannel'] ?? 'MESSAGE', 'status' => in_array($types[$kind], ['VEHICLE_SERVICE_REQUEST', 'REFUND_REQUEST'], true) ? 'PENDING_APPROVAL' : 'OPEN', 'createdBy' => $staff['phone'], 'createdAt' => date(DATE_ATOM)];
    saveStored($operationsFile, array_merge([$operation], stored($operationsFile))); reply($operation, 201);
}
if ($_SERVER['REQUEST_METHOD'] === 'POST' && ($route === '/api/auth/request-pin' || str_ends_with($route, '/auth/request-pin'))) {
    $phone = (string) ($input['phone'] ?? '');
    $adminPhone = getenv('ADMIN_PHONE') ?: '9378160106';
    $counterPhone = getenv('COUNTER_PHONE') ?: '1122334455';
    $moderators = stored($moderatorsFile);
    if ($phone !== $adminPhone && $phone !== $counterPhone && !array_filter($moderators, fn ($item) => $item['phone'] === $phone)) reply(['error' => 'Account is not authorized for staff access'], 403);
    $requestId = bin2hex(random_bytes(16));
    $pin = getenv('ADMIN_PIN') ?: '123456';
    $requests = stored($authFile);
    $requests[] = ['requestId' => $requestId, 'phone' => $phone, 'pin' => $pin, 'expiresAt' => time() + 300];
    saveStored($authFile, $requests);
    reply(['requestId' => $requestId, 'message' => 'Authentication PIN requested. Check your configured delivery channel.', 'demoPin' => getenv('APP_ENV') === 'production' ? null : $pin]);
}
if ($_SERVER['REQUEST_METHOD'] === 'POST' && ($route === '/api/auth/verify-pin' || str_ends_with($route, '/auth/verify-pin'))) {
    $sessions = stored($authFile);
    $match = null;
    foreach ($sessions as $session) if (($session['requestId'] ?? '') === ($input['requestId'] ?? '') && $session['expiresAt'] > time() && hash_equals((string) $session['pin'], (string) ($input['pin'] ?? ''))) $match = $session;
    if (!$match) reply(['error' => 'Invalid or expired authentication PIN'], 401);
    $adminPhone = getenv('ADMIN_PHONE') ?: '9378160106';
    $counterPhone = getenv('COUNTER_PHONE') ?: '1122334455';
    $token = bin2hex(random_bytes(32));
    $sessions[] = ['token' => $token, 'phone' => $match['phone'], 'role' => $match['phone'] === $adminPhone ? 'SUPER_ADMIN' : ($match['phone'] === $counterPhone ? 'COUNTER_STAFF' : 'MODERATOR'), 'expiresAt' => time() + 28800];
    saveStored($authFile, $sessions);
    reply(['token' => $token, 'role' => $sessions[array_key_last($sessions)]['role']]);
}
if ($_SERVER['REQUEST_METHOD'] === 'GET' && ($route === '/api/admin/moderators' || str_ends_with($route, '/admin/moderators'))) {
    if ((staffUser($authFile)['role'] ?? '') !== 'SUPER_ADMIN') reply(['error' => 'Super Admin access required'], 403);
    reply(['moderators' => stored($moderatorsFile)]);
}
if ($_SERVER['REQUEST_METHOD'] === 'POST' && ($route === '/api/admin/moderators' || str_ends_with($route, '/admin/moderators'))) {
    if ((staffUser($authFile)['role'] ?? '') !== 'SUPER_ADMIN') reply(['error' => 'Super Admin access required'], 403);
    $phone = preg_replace('/\D/', '', (string) ($input['phone'] ?? ''));
    $name = trim((string) ($input['name'] ?? ''));
    $moderators = stored($moderatorsFile);
    if (!preg_match('/^\d{10}$/', $phone) || $name === '') reply(['error' => 'Name and 10-digit phone are required'], 400);
    if (array_filter($moderators, fn ($item) => $item['phone'] === $phone)) reply(['error' => 'Account already exists'], 409);
    $moderator = ['id' => bin2hex(random_bytes(12)), 'name' => $name, 'phone' => $phone, 'createdAt' => date(DATE_ATOM)];
    saveStored($moderatorsFile, array_merge([$moderator], $moderators));
    reply($moderator, 201);
}
if ($_SERVER['REQUEST_METHOD'] === 'DELETE' && preg_match('#/(?:api/)?admin/moderators/([^/]+)$#', $route, $match)) {
    if ((staffUser($authFile)['role'] ?? '') !== 'SUPER_ADMIN') reply(['error' => 'Super Admin access required'], 403);
    $moderators = stored($moderatorsFile);
    $next = array_values(array_filter($moderators, fn ($item) => $item['id'] !== $match[1]));
    if (count($next) === count($moderators)) reply(['error' => 'Moderator not found'], 404);
    saveStored($moderatorsFile, $next);
    reply(['ok' => true]);
}
if ($_SERVER['REQUEST_METHOD'] === 'GET' && ($route === '/api/admin/operations' || str_ends_with($route, '/admin/operations'))) {
    $staff = staffUser($authFile);
    if (!in_array($staff['role'] ?? '', ['SUPER_ADMIN', 'MODERATOR', 'COUNTER_STAFF'], true)) reply(['error' => 'Staff access required'], 403);
    reply(['bookings' => $current, 'operations' => stored($operationsFile)]);
}
if ($_SERVER['REQUEST_METHOD'] === 'POST' && ($route === '/api/admin/operations' || str_ends_with($route, '/admin/operations'))) {
    $staff = staffUser($authFile);
    if (!in_array($staff['role'] ?? '', ['SUPER_ADMIN', 'MODERATOR', 'COUNTER_STAFF'], true)) reply(['error' => 'Staff access required'], 403);
    $allowed = ['REQUEST', 'COMPLAINT', 'EMERGENCY', 'FEEDBACK', 'ANNOUNCEMENT', 'CASH_REQUEST', 'USER', 'VEHICLE_SERVICE_REQUEST', 'TICKET_BOOKING', 'REFUND_REQUEST', 'CRASH_REPORT', 'BREAKDOWN_REPORT', 'LOCATION_SHARE', 'STAFF_CONTACT'];
    $type = (string) ($input['type'] ?? '');
    $message = trim((string) ($input['message'] ?? ''));
    if (!in_array($type, $allowed, true) || $message === '') reply(['error' => 'Operation type and message are required'], 400);
    $operation = ['id' => bin2hex(random_bytes(12)), 'type' => $type, 'message' => $message, 'status' => 'OPEN', 'createdBy' => $staff['phone'], 'createdAt' => date(DATE_ATOM)];
    saveStored($operationsFile, array_merge([$operation], stored($operationsFile)));
    reply($operation, 201);
}
if ($_SERVER['REQUEST_METHOD'] === 'POST' && preg_match('#/(?:api/)?admin/operations/([^/]+)/approve$#', $route, $match)) {
    $staff = staffUser($authFile);
    if (!in_array($staff['role'] ?? '', ['SUPER_ADMIN', 'MODERATOR', 'COUNTER_STAFF'], true)) reply(['error' => 'Staff access required'], 403);
    $operations = stored($operationsFile);
    foreach ($operations as $index => $operation) if ($operation['id'] === $match[1] && in_array($operation['type'], ['CASH_REQUEST', 'VEHICLE_SERVICE_REQUEST', 'REFUND_REQUEST'], true)) {
        if (in_array($operation['type'], ['VEHICLE_SERVICE_REQUEST', 'REFUND_REQUEST'], true) && !in_array($staff['role'] ?? '', ['SUPER_ADMIN', 'MODERATOR'], true)) reply(['error' => 'Moderator or Super Admin approval required'], 403);
        $operations[$index]['status'] = 'APPROVED';
        $operations[$index]['approvedAt'] = date(DATE_ATOM);
        saveStored($operationsFile, $operations);
        reply($operations[$index]);
    }
    reply(['error' => 'Cash request not found'], 404);
}

if ($_SERVER['REQUEST_METHOD'] === 'GET' && ($route === '/api/summary' || str_ends_with($route, '/summary'))) {
    reply([
        'user' => ['name' => 'Lalremruata Ralte', 'city' => 'Aizawl, Mizoram', 'wallet' => 1450, 'coins' => 380],
        'metrics' => ['activeBookings' => count(array_filter($current, fn ($booking) => ($booking['status'] ?? '') !== 'COMPLETED'), 'totalBookings' => count($current)],
        'bookings' => $current,
    ]);
}

if ($_SERVER['REQUEST_METHOD'] === 'POST' && ($route === '/api/bookings' || str_ends_with($route, '/bookings'))) {
    $input = json_decode((string) file_get_contents('php://input'), true);
    if (!is_array($input) || empty($input['service']) || empty($input['pickup']) || empty($input['destination']) || (float) ($input['amount'] ?? 0) <= 0) {
        reply(['error' => 'Invalid booking'], 400);
    }
    $booking = ['id' => (int) (microtime(true) * 1000), 'service' => (string) $input['service'], 'pickup' => (string) $input['pickup'], 'destination' => (string) $input['destination'], 'amount' => (float) $input['amount'], 'status' => 'CONFIRMED'];
    array_unshift($current, $booking);
    file_put_contents($dataFile, json_encode($current, JSON_PRETTY_PRINT), LOCK_EX);
    reply($booking, 201);
}

reply(['error' => 'Not found'], 404);
