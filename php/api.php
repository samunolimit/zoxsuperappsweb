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
$providersFile = $dataDir . '/providers.json';
$adsFile = $dataDir . '/custom-ads.json';
$rewardsFile = $dataDir . '/rewards.json';
$passwordsFile = $dataDir . '/passwords.json';
$faresFile = $dataDir . '/fares.json';
$walletFile = $dataDir . '/wallet-ledger.json';
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
function passwordHash(string $password): string { return hash('sha256', $password); }
function staffUser(string $authFile): ?array {
    $header = $_SERVER['HTTP_AUTHORIZATION'] ?? $_SERVER['REDIRECT_HTTP_AUTHORIZATION'] ?? '';
    $token = preg_replace('/^Bearer\s+/i', '', $header);
    foreach (stored($authFile) as $session) if (!empty($session['token']) && !empty($session['expiresAt']) && hash_equals((string) $session['token'], (string) $token) && $session['expiresAt'] > time()) return $session;
    return null;
}

$route = parse_url($_SERVER['REQUEST_URI'] ?? '', PHP_URL_PATH) ?: '';
if ($route === '/api/health' || str_ends_with($route, '/health')) reply(['ok' => true, 'service' => 'zox-php-api']);

$input = json_decode((string) file_get_contents('php://input'), true);
$current = bookings($dataFile, $dataDir, $initialBookings);
$staff = staffUser($authFile);
if ($_SERVER['REQUEST_METHOD'] === 'POST' && ($route === '/api/auth/login-password' || str_ends_with($route, '/auth/login-password'))) {
    $phone = preg_replace('/\D/', '', (string) ($input['phone'] ?? '')); $password = (string) ($input['password'] ?? ''); $adminPhone = getenv('ADMIN_PHONE') ?: '9378160106'; $adminPassword = getenv('ADMIN_DEFAULT_PASSWORD') ?: 'Srenthlei16#'; $passwords = stored($passwordsFile, '{}');
    if (!preg_match('/^\d{10}$/', $phone) || $password === '') reply(['error' => 'Phone number and password are required'], 400);
    $valid = $phone === $adminPhone ? hash_equals($adminPassword, $password) : (($passwords[$phone] ?? '') === passwordHash($password));
    if (!$valid) reply(['error' => 'Invalid password. Use OTP login if you do not have a password.'], 401);
    $counterPhone = getenv('COUNTER_PHONE') ?: '1122334455'; $role = $phone === $adminPhone ? 'SUPER_ADMIN' : ($phone === $counterPhone ? 'COUNTER_STAFF' : ((bool) array_filter(stored($moderatorsFile), fn ($item) => $item['phone'] === $phone) ? 'MODERATOR' : 'CUSTOMER'));
    $token = bin2hex(random_bytes(32)); $sessions = stored($authFile); $sessions[] = ['token' => $token, 'phone' => $phone, 'role' => $role, 'expiresAt' => time() + 28800]; saveStored($authFile, $sessions); reply(['token' => $token, 'role' => $role, 'needsPasswordSetup' => false]);
}
if ($_SERVER['REQUEST_METHOD'] === 'POST' && ($route === '/api/auth/set-password' || str_ends_with($route, '/auth/set-password'))) {
    if (!$staff) reply(['error' => 'Login required'], 401);
    $password = (string) ($input['password'] ?? ''); if (strlen($password) < 8) reply(['error' => 'Password must be at least 8 characters'], 400);
    $passwords = stored($passwordsFile, '{}'); $passwords[$staff['phone']] = passwordHash($password); saveStored($passwordsFile, $passwords); reply(['ok' => true, 'message' => 'Password saved']);
}
if ($_SERVER['REQUEST_METHOD'] === 'GET' && $route === '/api/rewards') reply(['rewards' => array_merge(['enabled' => true, 'adPoints' => 50, 'bookingPoints' => 10, 'dailyCap' => 500, 'expiryDays' => 365, 'redemptionMinimum' => 1000, 'pointValueInRupees' => 1], stored($rewardsFile, '{}'))]);
if ($_SERVER['REQUEST_METHOD'] === 'PATCH' && ($route === '/api/admin/rewards' || str_ends_with($route, '/admin/rewards'))) {
    if (($staff['role'] ?? '') !== 'SUPER_ADMIN') reply(['error' => 'Super Admin access required'], 403);
    $currentRewards = array_merge(['enabled' => true, 'adPoints' => 50, 'bookingPoints' => 10, 'dailyCap' => 500, 'expiryDays' => 365, 'redemptionMinimum' => 1000, 'pointValueInRupees' => 1], stored($rewardsFile, '{}')); $updatedRewards = array_merge($currentRewards, $input ?: []);
    foreach (['adPoints', 'bookingPoints', 'dailyCap', 'expiryDays', 'redemptionMinimum', 'pointValueInRupees'] as $key) if (!is_numeric($updatedRewards[$key]) || $updatedRewards[$key] < 0) reply(['error' => 'Reward values must be valid non-negative numbers'], 400);
    $updatedRewards['enabled'] = !empty($updatedRewards['enabled']); $updatedRewards['updatedAt'] = date(DATE_ATOM); saveStored($rewardsFile, $updatedRewards); reply($updatedRewards);
}
if ($_SERVER['REQUEST_METHOD'] === 'GET' && $route === '/api/ads') reply(['ads' => array_values(array_filter(stored($adsFile), fn ($item) => !empty($item['enabled'])))]);
if ($_SERVER['REQUEST_METHOD'] === 'GET' && ($route === '/api/admin/ads' || str_ends_with($route, '/admin/ads'))) {
    if (($staff['role'] ?? '') !== 'SUPER_ADMIN') reply(['error' => 'Super Admin access required'], 403);
    reply(['ads' => stored($adsFile)]);
}
if ($_SERVER['REQUEST_METHOD'] === 'POST' && ($route === '/api/admin/ads' || str_ends_with($route, '/admin/ads'))) {
    if (($staff['role'] ?? '') !== 'SUPER_ADMIN') reply(['error' => 'Super Admin access required'], 403);
    $title = trim((string) ($input['title'] ?? '')); $body = trim((string) ($input['body'] ?? ''));
    if ($title === '' || $body === '') reply(['error' => 'Ad title and message are required'], 400);
    $ad = ['id' => bin2hex(random_bytes(12)), 'title' => $title, 'body' => $body, 'imageUrl' => (string) ($input['imageUrl'] ?? ''), 'targetUrl' => (string) ($input['targetUrl'] ?? ''), 'placement' => (string) ($input['placement'] ?? 'HOME'), 'enabled' => true, 'createdAt' => date(DATE_ATOM)];
    saveStored($adsFile, array_merge([$ad], stored($adsFile))); reply($ad, 201);
}
if ($_SERVER['REQUEST_METHOD'] === 'PATCH' && preg_match('#/(?:api/)?admin/ads/([^/]+)$#', $route, $match)) {
    if (($staff['role'] ?? '') !== 'SUPER_ADMIN') reply(['error' => 'Super Admin access required'], 403);
    $ads = stored($adsFile); foreach ($ads as $index => $ad) if ($ad['id'] === $match[1]) { $ads[$index] = array_merge($ad, $input ?: [], ['updatedAt' => date(DATE_ATOM)]); saveStored($adsFile, $ads); reply($ads[$index]); }
    reply(['error' => 'Ad not found'], 404);
}
if ($_SERVER['REQUEST_METHOD'] === 'DELETE' && preg_match('#/(?:api/)?admin/ads/([^/]+)$#', $route, $match)) {
    if (($staff['role'] ?? '') !== 'SUPER_ADMIN') reply(['error' => 'Super Admin access required'], 403);
    $ads = stored($adsFile); $next = array_values(array_filter($ads, fn ($item) => $item['id'] !== $match[1])); if (count($next) === count($ads)) reply(['error' => 'Ad not found'], 404); saveStored($adsFile, $next); reply(['ok' => true]);
}
if ($_SERVER['REQUEST_METHOD'] === 'POST' && ($route === '/api/providers/register' || str_ends_with($route, '/providers/register'))) {
    $allowed = ['workshop', 'mechanic', 'medical', 'emergency', 'support', 'store'];
    $service = strtolower((string) ($input['service'] ?? '')); $name = trim((string) ($input['name'] ?? '')); $phone = preg_replace('/\D/', '', (string) ($input['phone'] ?? '')); $location = trim((string) ($input['location'] ?? '')); $details = trim((string) ($input['details'] ?? ''));
    if (!in_array($service, $allowed, true) || $name === '' || !preg_match('/^\d{10}$/', $phone) || $location === '' || $details === '') reply(['error' => 'Service, name, 10-digit phone, location and details are required'], 400);
    $provider = ['id' => bin2hex(random_bytes(12)), 'service' => 'plugin_' . $service, 'name' => $name, 'phone' => $phone, 'location' => $location, 'details' => $details, 'status' => 'PENDING_REVIEW', 'createdAt' => date(DATE_ATOM)];
    saveStored($providersFile, array_merge([$provider], stored($providersFile))); reply($provider, 201);
}
if ($_SERVER['REQUEST_METHOD'] === 'POST' && str_starts_with($route, '/api/counter/')) {
    if (($staff['role'] ?? '') !== 'COUNTER_STAFF') reply(['error' => 'Counter Staff access required'], 403);
    $kind = basename($route);
    $types = ['vehicle-service-request' => 'VEHICLE_SERVICE_REQUEST', 'refund-request' => 'REFUND_REQUEST', 'incident' => (string) ($input['type'] ?? '')];
    if ($kind === 'fares') {
        $routeName = trim((string) ($input['route'] ?? '')); $vehicleType = trim((string) ($input['vehicleType'] ?? '')); $amount = (float) ($input['amount'] ?? 0);
        if ($routeName === '' || $vehicleType === '' || $amount <= 0) reply(['error' => 'Route, vehicle type and positive fare are required'], 400);
        $fares = array_values(array_filter(stored($faresFile), fn ($item) => $item['route'] !== $routeName || $item['vehicleType'] !== $vehicleType));
        $fare = ['id' => bin2hex(random_bytes(12)), 'route' => $routeName, 'vehicleType' => $vehicleType, 'amount' => round($amount, 2), 'updatedBy' => $staff['phone'], 'updatedAt' => date(DATE_ATOM)];
        saveStored($faresFile, array_merge([$fare], $fares)); reply($fare, 201);
    }
    if ($kind === 'ticket-booking') {
        if (empty($input['passenger']) || empty($input['vehicleId']) || empty($input['destination'])) reply(['error' => 'Passenger, vehicle and destination are required'], 400);
        $fare = array_values(array_filter(stored($faresFile), fn ($item) => $item['route'] === ($input['route'] ?? '') && $item['vehicleType'] === ($input['vehicleType'] ?? '')))[0] ?? null;
        $amount = (float) ($fare['amount'] ?? 0); $rate = (float) (getenv('COMMISSION_RATE') ?: 10); $commission = round($amount * $rate / 100, 2);
        if ($amount <= 0) reply(['error' => 'No fare configured for this route and vehicle'], 400);
        $ticket = ['id' => bin2hex(random_bytes(12)), 'service' => 'Route Passenger Ticket', 'passenger' => (string) $input['passenger'], 'route' => (string) ($input['route'] ?? ''), 'vehicleType' => (string) ($input['vehicleType'] ?? ''), 'vehicleId' => (string) $input['vehicleId'], 'destination' => (string) $input['destination'], 'amount' => $amount, 'commissionRate' => $rate, 'commission' => $commission, 'status' => 'BOOKED', 'bookedBy' => $staff['phone'], 'destinationReached' => false, 'createdAt' => date(DATE_ATOM)];
        saveStored($dataFile, array_merge([$ticket], $current));
        $ledger = stored($walletFile); $ledger[] = ['id' => bin2hex(random_bytes(12)), 'account' => $staff['phone'], 'type' => 'COMMISSION', 'ticketId' => $ticket['id'], 'amount' => $commission, 'status' => 'CREDITED', 'createdAt' => date(DATE_ATOM)]; saveStored($walletFile, $ledger);
        reply($ticket, 201);
    }
    if ($kind === 'vehicle-service-request' && (empty($input['vehicleId']) || empty($input['service']))) reply(['error' => 'Vehicle and service details are required'], 400);
    if ($kind === 'refund-request' && (empty($input['ticketId']) || empty($input['reason']))) reply(['error' => 'Ticket and refund reason are required'], 400);
    if ($kind === 'incident' && (!in_array($types[$kind], ['CRASH_REPORT', 'BREAKDOWN_REPORT', 'EMERGENCY', 'LOCATION_SHARE', 'STAFF_CONTACT'], true) || empty($input['vehicleId']) || empty($input['message']))) reply(['error' => 'Incident type, vehicle and message are required'], 400);
    $operation = ['id' => bin2hex(random_bytes(12)), 'type' => $types[$kind], 'vehicleId' => $input['vehicleId'] ?? null, 'ticketId' => $input['ticketId'] ?? null, 'message' => (string) ($input['service'] ?? $input['reason'] ?? $input['message'] ?? ''), 'location' => $input['location'] ?? null, 'refundFee' => (float) ($input['refundFee'] ?? 0), 'contactChannel' => $input['contactChannel'] ?? 'MESSAGE', 'status' => in_array($types[$kind], ['VEHICLE_SERVICE_REQUEST', 'REFUND_REQUEST'], true) ? 'PENDING_APPROVAL' : 'OPEN', 'createdBy' => $staff['phone'], 'createdAt' => date(DATE_ATOM)];
    saveStored($operationsFile, array_merge([$operation], stored($operationsFile))); reply($operation, 201);
}
if ($_SERVER['REQUEST_METHOD'] === 'GET' && ($route === '/api/counter/wallet' || str_ends_with($route, '/counter/wallet'))) {
    if (($staff['role'] ?? '') !== 'COUNTER_STAFF') reply(['error' => 'Counter Staff access required'], 403);
    $entries = array_values(array_filter(stored($walletFile), fn ($item) => $item['account'] === $staff['phone'] && $item['status'] === 'CREDITED'));
    reply(['account' => $staff['phone'], 'balance' => array_sum(array_column($entries, 'amount')), 'entries' => $entries]);
}
if ($_SERVER['REQUEST_METHOD'] === 'POST' && ($route === '/api/auth/request-pin' || str_ends_with($route, '/auth/request-pin'))) {
    $phone = (string) ($input['phone'] ?? '');
    $adminPhone = getenv('ADMIN_PHONE') ?: '9378160106';
    $counterPhone = getenv('COUNTER_PHONE') ?: '1122334455';
    $moderators = stored($moderatorsFile);
    if (!preg_match('/^\d{10}$/', $phone)) reply(['error' => 'Enter a valid 10-digit mobile number'], 400);
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
    $isStaff = $match['phone'] === $adminPhone || $match['phone'] === $counterPhone || (bool) array_filter(stored($moderatorsFile), fn ($item) => $item['phone'] === $match['phone']);
    $sessions[] = ['token' => $token, 'phone' => $match['phone'], 'role' => $match['phone'] === $adminPhone ? 'SUPER_ADMIN' : ($match['phone'] === $counterPhone ? 'COUNTER_STAFF' : ($isStaff ? 'MODERATOR' : 'CUSTOMER')), 'expiresAt' => time() + 28800];
    saveStored($authFile, $sessions);
    reply(['token' => $token, 'role' => $sessions[array_key_last($sessions)]['role']]);
}
if ($_SERVER['REQUEST_METHOD'] === 'GET' && ($route === '/api/admin/moderators' || str_ends_with($route, '/admin/moderators'))) {
    if ((staffUser($authFile)['role'] ?? '') !== 'SUPER_ADMIN') reply(['error' => 'Super Admin access required'], 403);
    reply(['moderators' => stored($moderatorsFile)]);
}
if ($_SERVER['REQUEST_METHOD'] === 'GET' && ($route === '/api/admin/providers' || str_ends_with($route, '/admin/providers'))) {
    if (!in_array($staff['role'] ?? '', ['SUPER_ADMIN', 'MODERATOR'], true)) reply(['error' => 'Admin or Moderator access required'], 403);
    reply(['providers' => stored($providersFile)]);
}
if ($_SERVER['REQUEST_METHOD'] === 'POST' && preg_match('#/(?:api/)?admin/providers/([^/]+)/approve$#', $route, $match)) {
    if (!in_array($staff['role'] ?? '', ['SUPER_ADMIN', 'MODERATOR'], true)) reply(['error' => 'Admin or Moderator access required'], 403);
    $providers = stored($providersFile);
    foreach ($providers as $index => $provider) if ($provider['id'] === $match[1]) { $providers[$index]['status'] = 'APPROVED'; $providers[$index]['approvedAt'] = date(DATE_ATOM); saveStored($providersFile, $providers); reply($providers[$index]); }
    reply(['error' => 'Provider application not found'], 404);
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
    if (($staff['role'] ?? '') === 'COUNTER_STAFF') reply(['error' => 'Use the Counter Desk panel for Counter operations'], 403);
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
    if (!$staff) reply(['error' => 'Login required'], 401);
    reply([
        'user' => ['name' => 'Lalremruata Ralte', 'city' => 'Aizawl, Mizoram', 'wallet' => 1450, 'coins' => 380],
        'metrics' => ['activeBookings' => count(array_filter($current, fn ($booking) => ($booking['status'] ?? '') !== 'COMPLETED')), 'totalBookings' => count($current)],
        'bookings' => $current,
    ]);
}

if ($_SERVER['REQUEST_METHOD'] === 'POST' && ($route === '/api/bookings' || str_ends_with($route, '/bookings'))) {
    if (!$staff) reply(['error' => 'Login required'], 401);
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
