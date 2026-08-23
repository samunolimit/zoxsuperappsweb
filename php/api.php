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

$route = parse_url($_SERVER['REQUEST_URI'] ?? '', PHP_URL_PATH) ?: '';
if ($route === '/api/health' || str_ends_with($route, '/health')) reply(['ok' => true, 'service' => 'zox-php-api']);

$current = bookings($dataFile, $dataDir, $initialBookings);
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
