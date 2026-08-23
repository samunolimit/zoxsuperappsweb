package com.example.ui.screens

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Api
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Code
import androidx.compose.material.icons.filled.ContentCopy
import androidx.compose.material.icons.filled.Download
import androidx.compose.material.icons.filled.Language
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Map
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.QrCode
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.Send
import androidx.compose.material.icons.filled.Terminal
import androidx.compose.material.icons.filled.TwoWheeler
import androidx.compose.material.icons.filled.WifiTethering
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.ScrollableTabRow
import androidx.compose.material3.Surface
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRowDefaults
import androidx.compose.material3.TabRowDefaults.tabIndicatorOffset
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.google.android.gms.maps.model.CameraPosition
import com.google.android.gms.maps.model.LatLng
import com.google.maps.android.compose.GoogleMap
import com.google.maps.android.compose.MapProperties
import com.google.maps.android.compose.MapUiSettings
import com.google.maps.android.compose.Marker
import com.google.maps.android.compose.MarkerState
import com.google.maps.android.compose.rememberCameraPositionState
import androidx.compose.ui.window.Dialog
import com.example.model.BookingItem
import com.example.model.FleetVehicle
import com.example.model.MasterVaultConfig
import com.example.ui.components.ZoxBrandHeader
import com.example.ui.components.ZoxLogoEmblem
import com.example.ui.theme.ZoxDarkBackground
import com.example.ui.theme.ZoxDarkCard
import com.example.ui.theme.ZoxDarkSurface
import com.example.ui.theme.ZoxError
import com.example.ui.theme.ZoxOrangeAccent
import com.example.ui.theme.ZoxOrangeLight
import com.example.ui.theme.ZoxPurpleContainer
import com.example.ui.theme.ZoxPurplePrimary
import com.example.ui.theme.ZoxSuccess
import com.example.ui.theme.ZoxWarning
import kotlinx.coroutines.delay

@Composable
fun WebPortalScreen(
  vaultConfig: MasterVaultConfig,
  fleet: List<FleetVehicle>,
  bookings: List<BookingItem>,
  onClose: () -> Unit,
  onQuickBook: (title: String, pickup: String, fare: Double) -> Unit
) {
  var activeWebTab by remember { mutableIntStateOf(0) }
  var isReloading by remember { mutableStateOf(false) }
  var showQrDialog by remember { mutableStateOf(false) }
  var copiedToast by remember { mutableStateOf(false) }

  val webTabs = listOf(
    "🌐 Web Dispatch",
    "🗺️ Fleet Radar Web",
    "⚡ REST API Sandbox",
    "💻 Live Server Logs",
    "📦 JSON Data Export"
  )

  LaunchedEffect(isReloading) {
    if (isReloading) {
      delay(800)
      isReloading = false
    }
  }

  LaunchedEffect(copiedToast) {
    if (copiedToast) {
      delay(2000)
      copiedToast = false
    }
  }

  Column(
    modifier = Modifier
      .fillMaxSize()
      .background(Color(0xFF0F0E17))
      .statusBarsPadding()
      .navigationBarsPadding()
      .testTag("web_portal_screen")
  ) {
    // 1. Web Browser Chrome Header
    Row(
      modifier = Modifier
        .fillMaxWidth()
        .background(Color(0xFF1B192B))
        .padding(horizontal = 12.dp, vertical = 8.dp),
      verticalAlignment = Alignment.CenterVertically,
      horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {
      IconButton(
        onClick = onClose,
        modifier = Modifier.size(36.dp).testTag("web_portal_back_button")
      ) {
        Icon(
          imageVector = Icons.Default.ArrowBack,
          contentDescription = "Return to Mobile View",
          tint = Color.White
        )
      }

      // Simulated Browser URL Bar
      Row(
        modifier = Modifier
          .weight(1f)
          .clip(RoundedCornerShape(20.dp))
          .background(Color(0xFF0D0C14))
          .border(0.8.dp, Color(0xFF3E3B5C), RoundedCornerShape(20.dp))
          .padding(horizontal = 12.dp, vertical = 6.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(6.dp)
      ) {
        Icon(
          imageVector = Icons.Default.Lock,
          contentDescription = "HTTPS Secure",
          tint = ZoxSuccess,
          modifier = Modifier.size(14.dp)
        )
        Text(
          text = "https://web.zoxapps.mizoram.in/portal/console",
          fontSize = 11.sp,
          color = Color(0xFFE2E2F0),
          fontFamily = FontFamily.Monospace,
          maxLines = 1,
          modifier = Modifier.weight(1f)
        )
        if (isReloading) {
          CircularProgressIndicator(
            modifier = Modifier.size(14.dp),
            color = ZoxOrangeAccent,
            strokeWidth = 2.dp
          )
        } else {
          IconButton(
            onClick = { isReloading = true },
            modifier = Modifier.size(20.dp)
          ) {
            Icon(
              imageVector = Icons.Default.Refresh,
              contentDescription = "Reload",
              tint = Color(0xFFA5A5BC),
              modifier = Modifier.size(14.dp)
            )
          }
        }
      }

      // QR Code Pair Button
      IconButton(
        onClick = { showQrDialog = true },
        modifier = Modifier.size(36.dp).testTag("web_qr_sync_button")
      ) {
        Icon(
          imageVector = Icons.Default.QrCode,
          contentDescription = "Pair Mobile with Desktop",
          tint = ZoxOrangeAccent
        )
      }
    }

    // 2. Web Sub-Navigation Tab Bar
    ScrollableTabRow(
      selectedTabIndex = activeWebTab,
      containerColor = Color(0xFF141320),
      contentColor = ZoxOrangeAccent,
      indicator = { tabPositions ->
        TabRowDefaults.SecondaryIndicator(
          Modifier.tabIndicatorOffset(tabPositions[activeWebTab]),
          color = ZoxOrangeAccent,
          height = 3.dp
        )
      },
      edgePadding = 12.dp
    ) {
      webTabs.forEachIndexed { index, title ->
        Tab(
          selected = activeWebTab == index,
          onClick = { activeWebTab = index },
          text = {
            Text(
              text = title,
              fontSize = 11.sp,
              fontWeight = if (activeWebTab == index) FontWeight.Bold else FontWeight.Normal,
              color = if (activeWebTab == index) ZoxOrangeAccent else Color(0xFFA0A0BA)
            )
          }
        )
      }
    }

    // 3. Tab Content Area
    Box(
      modifier = Modifier
        .fillMaxSize()
        .padding(horizontal = 16.dp, vertical = 12.dp),
      contentAlignment = Alignment.TopCenter
    ) {
      Box(
        modifier = Modifier
          .fillMaxWidth()
          .widthIn(max = 900.dp)
      ) {
        when (activeWebTab) {
          0 -> WebQuickDispatchView(fleet = fleet, onDispatch = onQuickBook)
          1 -> WebFleetRadarView(fleet = fleet, mapsConfig = vaultConfig.maps)
          2 -> WebRestApiSandboxView(vaultConfig = vaultConfig)
          3 -> WebServerLogsView()
          4 -> WebJsonExportView(vaultConfig = vaultConfig, fleet = fleet, bookings = bookings)
        }
      }
    }
  }

  // QR Sync Dialog
  if (showQrDialog) {
    WebQrSyncDialog(
      onDismiss = { showQrDialog = false }
    )
  }
}

// -------------------------------------------------------------
// PANEL 1: WEB QUICK DISPATCH
// -------------------------------------------------------------
@Composable
private fun WebQuickDispatchView(
  fleet: List<FleetVehicle>,
  onDispatch: (title: String, pickup: String, fare: Double) -> Unit
) {
  var pickupLocation by remember { mutableStateOf("Chanmari, Aizawl") }
  var dropoffLocation by remember { mutableStateOf("Zarkawt, Aizawl") }
  var selectedCategory by remember { mutableStateOf("Bike Rental") }
  var specialInstructions by remember { mutableStateOf("Urgent priority dispatch from Web Portal") }
  var estimatedFare by remember { mutableStateOf(120.0) }
  var orderSubmitted by remember { mutableStateOf(false) }

  LazyColumn(
    modifier = Modifier.fillMaxSize(),
    verticalArrangement = Arrangement.spacedBy(14.dp)
  ) {
    item {
      Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = ZoxDarkCard),
        border = CardDefaults.outlinedCardBorder().copy(brush = Brush.horizontalGradient(listOf(ZoxPurplePrimary, Color(0xFF3B2464))))
      ) {
        Column(
          modifier = Modifier.padding(18.dp),
          verticalArrangement = Arrangement.spacedBy(14.dp)
        ) {
          Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
          ) {
            Column {
              Text(
                text = "🌐 Web-Based Order Dispatcher",
                fontSize = 16.sp,
                fontWeight = FontWeight.Bold,
                color = Color.White
              )
              Text(
                text = "Dispatch fleet bookings directly from any Desktop / Web browser",
                fontSize = 11.sp,
                color = Color(0xFFA5A5BC)
              )
            }

            Box(
              modifier = Modifier
                .clip(RoundedCornerShape(8.dp))
                .background(ZoxSuccess.copy(alpha = 0.2f))
                .padding(horizontal = 8.dp, vertical = 4.dp)
            ) {
              Text(
                text = "WEB CLIENT v2.4",
                fontSize = 9.sp,
                fontWeight = FontWeight.Bold,
                color = ZoxSuccess
              )
            }
          }

          OutlinedTextField(
            value = pickupLocation,
            onValueChange = { pickupLocation = it },
            label = { Text("Pickup Address (Mizoram)") },
            modifier = Modifier.fillMaxWidth().testTag("web_pickup_input"),
            colors = OutlinedTextFieldDefaults.colors(
              focusedBorderColor = ZoxOrangeAccent,
              unfocusedBorderColor = Color(0xFF484568),
              focusedTextColor = Color.White,
              unfocusedTextColor = Color.White
            ),
            singleLine = true
          )

          OutlinedTextField(
            value = dropoffLocation,
            onValueChange = { dropoffLocation = it },
            label = { Text("Destination / Drop-off") },
            modifier = Modifier.fillMaxWidth().testTag("web_dropoff_input"),
            colors = OutlinedTextFieldDefaults.colors(
              focusedBorderColor = ZoxOrangeAccent,
              unfocusedBorderColor = Color(0xFF484568),
              focusedTextColor = Color.White,
              unfocusedTextColor = Color.White
            ),
            singleLine = true
          )

          // Service Type Chips
          Text(text = "Select Service Tier:", fontSize = 12.sp, color = Color(0xFFC4C4D8), fontWeight = FontWeight.SemiBold)
          Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
          ) {
            listOf("Bike Rental", "Tirhkah Runner", "City Taxi").forEach { cat ->
              val isSelected = selectedCategory == cat
              Box(
                modifier = Modifier
                  .weight(1f)
                  .clip(RoundedCornerShape(10.dp))
                  .background(if (isSelected) ZoxOrangeAccent else Color(0xFF201D32))
                  .border(0.8.dp, if (isSelected) ZoxOrangeAccent else Color(0xFF383552), RoundedCornerShape(10.dp))
                  .clickable {
                    selectedCategory = cat
                    estimatedFare = when (cat) {
                      "Bike Rental" -> 120.0
                      "Tirhkah Runner" -> 65.0
                      else -> 190.0
                    }
                  }
                  .padding(vertical = 10.dp),
                contentAlignment = Alignment.Center
              ) {
                Text(
                  text = cat,
                  fontSize = 11.sp,
                  fontWeight = FontWeight.Bold,
                  color = if (isSelected) Color.Black else Color.White
                )
              }
            }
          }

          // Summary & Dispatch Trigger
          Row(
            modifier = Modifier
              .fillMaxWidth()
              .clip(RoundedCornerShape(12.dp))
              .background(Color(0xFF171424))
              .padding(14.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
          ) {
            Column {
              Text(text = "ESTIMATED FARE", fontSize = 10.sp, color = Color(0xFFA5A5BC), fontWeight = FontWeight.Bold)
              Text(text = "₹$estimatedFare", fontSize = 18.sp, fontWeight = FontWeight.Black, color = ZoxOrangeAccent)
            }

            Button(
              onClick = {
                onDispatch("Web Booking: $selectedCategory ($pickupLocation)", pickupLocation, estimatedFare)
                orderSubmitted = true
              },
              colors = ButtonDefaults.buttonColors(containerColor = ZoxOrangeAccent),
              shape = RoundedCornerShape(10.dp),
              modifier = Modifier.testTag("web_dispatch_btn")
            ) {
              Icon(
                imageVector = Icons.Default.Send,
                contentDescription = null,
                tint = Color.Black,
                modifier = Modifier.size(16.dp)
              )
              Spacer(modifier = Modifier.width(6.dp))
              Text("Dispatch via Web", fontSize = 12.sp, fontWeight = FontWeight.Bold, color = Color.Black)
            }
          }

          if (orderSubmitted) {
            Row(
              modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(8.dp))
                .background(ZoxSuccess.copy(alpha = 0.2f))
                .padding(10.dp),
              verticalAlignment = Alignment.CenterVertically,
              horizontalArrangement = Arrangement.spacedBy(6.dp)
            ) {
              Icon(
                imageVector = Icons.Default.CheckCircle,
                contentDescription = null,
                tint = ZoxSuccess,
                modifier = Modifier.size(16.dp)
              )
              Text(
                text = "Dispatched! Synced to Mobile Super App & Active Bookings queue.",
                fontSize = 11.sp,
                color = Color.White
              )
            }
          }
        }
      }
    }
  }
}

// -------------------------------------------------------------
// PANEL 2: LIVE FLEET RADAR MAP
// -------------------------------------------------------------
@Composable
private fun WebFleetRadarView(fleet: List<FleetVehicle>, mapsConfig: com.example.model.GoogleMapsConfig) {
  val mockDrivers = listOf(
    Pair("Lalhmangaiha (Activa MZ-01-N-1092)", "Chanmari Hub • Moving 24 km/h • Battery 94%"),
    Pair("Lalruatkima (Hunter 350 MZ-01-M-4829)", "Zarkawt Junction • Parked (Idle) • Fuel 85%"),
    Pair("Zoramthanga (Thar 4x4 MZ-01-T-9920)", "Bawngkawn Traffic • En Route • Fuel 78%"),
    Pair("Vanlalhruaia (Swift MZ-01-K-3341)", "Khatla Secretariat • Standby • Battery 99%"),
    Pair("Laldinpuia (Aerox 155 MZ-01-P-8821)", "Tuikual South • On Errand • Speed 31 km/h")
  )

  LazyColumn(
    modifier = Modifier.fillMaxSize(),
    verticalArrangement = Arrangement.spacedBy(12.dp)
  ) {
    item {
      Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = ZoxDarkCard)
      ) {
        Column(
          modifier = Modifier.padding(16.dp),
          verticalArrangement = Arrangement.spacedBy(10.dp)
        ) {
          Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
          ) {
            Text(
              text = "Live GPS Fleet Radar (Aizawl District)",
              fontSize = 14.sp,
              fontWeight = FontWeight.Bold,
              color = Color.White
            )

            Row(
              verticalAlignment = Alignment.CenterVertically,
              horizontalArrangement = Arrangement.spacedBy(6.dp)
            ) {
              Box(
                modifier = Modifier
                  .size(8.dp)
                  .clip(CircleShape)
                  .background(ZoxSuccess)
              )
              Text(
                text = "5 ONLINE",
                fontSize = 10.sp,
                fontWeight = FontWeight.Bold,
                color = ZoxSuccess
              )
            }
          }

          Text(
            text = "Real-time telemetry and geofencing stream connecting Web Dispatchers to active vehicles in Aizawl, Lunglei & Champhai.",
            fontSize = 11.sp,
            color = Color(0xFFA5A5BC)
          )

          if (mapsConfig.isEnabled && mapsConfig.androidApiKey.isNotBlank()) {
            val aizawl = LatLng(23.7271, 92.7176)
            val cameraPositionState = rememberCameraPositionState {
              position = CameraPosition.fromLatLngZoom(aizawl, 11f)
            }
            GoogleMap(
              modifier = Modifier
                .fillMaxWidth()
                .height(260.dp),
              cameraPositionState = cameraPositionState,
              properties = MapProperties(isBuildingEnabled = true),
              uiSettings = MapUiSettings(zoomControlsEnabled = true, mapToolbarEnabled = false)
            ) {
              mockDrivers.forEachIndexed { index, driver ->
                Marker(
                  state = MarkerState(LatLng(23.7271 + index * 0.012, 92.7176 + index * 0.009)),
                  title = driver.first,
                  snippet = driver.second
                )
              }
            }
          } else {
            Text(
              text = "Google Maps API key not configured. Add it under Admin > Master Vault > Google Maps SDK & Geocoding.",
              fontSize = 11.sp,
              color = ZoxWarning
            )
          }
        }
      }
    }

    items(mockDrivers) { driver ->
      Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = ZoxDarkSurface),
        border = CardDefaults.outlinedCardBorder().copy(brush = Brush.horizontalGradient(listOf(Color(0xFF3E3B5C), Color(0xFF221F36))))
      ) {
        Row(
          modifier = Modifier
            .fillMaxWidth()
            .padding(14.dp),
          verticalAlignment = Alignment.CenterVertically,
          horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
          Box(
            modifier = Modifier
              .size(40.dp)
              .clip(CircleShape)
              .background(ZoxPurpleContainer),
            contentAlignment = Alignment.Center
          ) {
            Icon(
              imageVector = Icons.Default.TwoWheeler,
              contentDescription = null,
              tint = ZoxOrangeAccent,
              modifier = Modifier.size(20.dp)
            )
          }

          Column(modifier = Modifier.weight(1f)) {
            Text(
              text = driver.first,
              fontSize = 12.sp,
              fontWeight = FontWeight.Bold,
              color = Color.White
            )
            Text(
              text = driver.second,
              fontSize = 10.sp,
              color = Color(0xFFB0B0C8)
            )
          }

          Box(
            modifier = Modifier
              .clip(RoundedCornerShape(6.dp))
              .background(Color(0xFF1E2838))
              .padding(horizontal = 8.dp, vertical = 4.dp)
          ) {
            Text(
              text = "TRACK GPS",
              fontSize = 9.sp,
              fontWeight = FontWeight.Bold,
              color = Color(0xFF70B4FF)
            )
          }
        }
      }
    }
  }
}

// -------------------------------------------------------------
// PANEL 3: REST API & WEBHOOKS SANDBOX
// -------------------------------------------------------------
@Composable
private fun WebRestApiSandboxView(vaultConfig: MasterVaultConfig) {
  var selectedEndpoint by remember { mutableStateOf("GET /v2/fleet/available") }
  var isSending by remember { mutableStateOf(false) }
  var responseJson by remember {
    mutableStateOf(
      """
{
  "status": 200,
  "message": "OK",
  "data": {
    "node": "ZOX-MZR-CLUSTER-01",
    "vehicles_available": 5,
    "active_currency": "INR",
    "tariff_base_rate": 99.0
  }
}
      """.trimIndent()
    )
  }

  LaunchedEffect(isSending) {
    if (isSending) {
      delay(600)
      isSending = false
      responseJson = when (selectedEndpoint) {
        "GET /v2/fleet/available" -> """
{
  "status": 200,
  "timestamp": "2026-08-23T14:00:00Z",
  "fleet_count": 5,
  "vehicles": [
    { "id": "v1", "name": "Royal Enfield Hunter 350", "hourly_rate": 120.0, "status": "AVAILABLE" },
    { "id": "v2", "name": "Honda Activa 6G", "hourly_rate": 60.0, "status": "AVAILABLE" }
  ]
}
        """.trimIndent()
        "POST /v2/bookings/dispatch" -> """
{
  "status": 201,
  "booking_id": "ZOX-WEB-8849",
  "assigned_runner": "Lalhmangaiha",
  "eta_minutes": 8,
  "payment_status": "AUTHORIZED_UPI"
}
        """.trimIndent()
        "GET /v2/vault/health" -> """
{
  "status": 200,
  "maintenance_mode": ${vaultConfig.system.isMaintenanceMode},
  "database": "CONNECTED",
  "agora_rtc": "TOKEN_VALID",
  "sms_gateway": "ONLINE",
  "admob_enabled": ${vaultConfig.admob.isEnabled}
}
        """.trimIndent()
        else -> """
{
  "status": 200,
  "balance_inr": 2450.0,
  "loyalty_coins": 350
}
        """.trimIndent()
      }
    }
  }

  LazyColumn(
    modifier = Modifier.fillMaxSize(),
    verticalArrangement = Arrangement.spacedBy(14.dp)
  ) {
    item {
      Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = ZoxDarkCard)
      ) {
        Column(
          modifier = Modifier.padding(16.dp),
          verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
          Text(
            text = "⚡ Interactive Swagger & REST API Sandbox",
            fontSize = 15.sp,
            fontWeight = FontWeight.Bold,
            color = Color.White
          )

          Text(
            text = "Test ZOX Super App cloud endpoints in real-time from the web client interface.",
            fontSize = 11.sp,
            color = Color(0xFFA5A5BC)
          )

          // Endpoint Selector
          val endpoints = listOf(
            "GET /v2/fleet/available",
            "POST /v2/bookings/dispatch",
            "GET /v2/vault/health",
            "GET /v2/wallet/balance"
          )

          endpoints.forEach { ep ->
            val isSelected = selectedEndpoint == ep
            Row(
              modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(8.dp))
                .background(if (isSelected) Color(0xFF2D1E47) else Color(0xFF191726))
                .border(0.8.dp, if (isSelected) ZoxOrangeAccent else Color(0xFF34314E), RoundedCornerShape(8.dp))
                .clickable { selectedEndpoint = ep }
                .padding(horizontal = 12.dp, vertical = 8.dp),
              horizontalArrangement = Arrangement.SpaceBetween,
              verticalAlignment = Alignment.CenterVertically
            ) {
              Text(
                text = ep,
                fontSize = 11.sp,
                fontFamily = FontFamily.Monospace,
                fontWeight = FontWeight.Bold,
                color = if (isSelected) ZoxOrangeAccent else Color.White
              )

              if (isSelected) {
                Icon(
                  imageVector = Icons.Default.PlayArrow,
                  contentDescription = null,
                  tint = ZoxOrangeAccent,
                  modifier = Modifier.size(16.dp)
                )
              }
            }
          }

          // Execute Request Button
          Button(
            onClick = { isSending = true },
            modifier = Modifier
              .fillMaxWidth()
              .height(44.dp)
              .testTag("send_api_request_btn"),
            colors = ButtonDefaults.buttonColors(containerColor = ZoxPurplePrimary),
            shape = RoundedCornerShape(10.dp)
          ) {
            if (isSending) {
              CircularProgressIndicator(
                modifier = Modifier.size(16.dp),
                color = Color.White,
                strokeWidth = 2.dp
              )
            } else {
              Icon(
                imageVector = Icons.Default.Api,
                contentDescription = null,
                tint = Color.White,
                modifier = Modifier.size(16.dp)
              )
              Spacer(modifier = Modifier.width(6.dp))
              Text("Send Request (HTTP 200)", fontSize = 12.sp, fontWeight = FontWeight.Bold)
            }
          }

          // Response JSON Container
          Text(text = "LIVE CLOUD RESPONSE:", fontSize = 10.sp, fontWeight = FontWeight.Bold, color = Color(0xFFA5A5BC))
          Box(
            modifier = Modifier
              .fillMaxWidth()
              .clip(RoundedCornerShape(10.dp))
              .background(Color(0xFF0C0B12))
              .border(0.8.dp, Color(0xFF2C2942), RoundedCornerShape(10.dp))
              .padding(12.dp)
          ) {
            Text(
              text = responseJson,
              fontSize = 11.sp,
              color = Color(0xFF8CE99A),
              fontFamily = FontFamily.Monospace,
              lineHeight = 16.sp
            )
          }
        }
      }
    }
  }
}

// -------------------------------------------------------------
// PANEL 4: LIVE SERVER LOGS TERMINAL
// -------------------------------------------------------------
@Composable
private fun WebServerLogsView() {
  val logs = listOf(
    "[14:02:11] [HTTP/2 200] GET /v2/fleet/available (28ms) - Node: ap-south-1",
    "[14:02:15] [WS/WSS] Client paired via Web QR Token: 9378160106",
    "[14:02:19] [AUTH] JWT verified for User: Lalremruata Ralte (Role: SUPER_ADMIN)",
    "[14:02:22] [AGORA-RTC] Channel 'ZOX_GLOBAL_SUPPORT_ROOM' token generated",
    "[14:02:25] [RAZORPAY] Webhook listener listening on /v2/webhooks/razorpay",
    "[14:02:29] [DB-POOL] Supabase PostgreSQL: 4 active connections / 20 pool",
    "[14:02:35] [ADMOB-ENGINE] Native feed ad impression delivered (eCPM: $2.40)"
  )

  LazyColumn(
    modifier = Modifier.fillMaxSize(),
    verticalArrangement = Arrangement.spacedBy(10.dp)
  ) {
    item {
      Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = Color(0xFF0E0D14)),
        border = CardDefaults.outlinedCardBorder().copy(brush = Brush.horizontalGradient(listOf(Color(0xFF2F2C47), Color(0xFF1E1C2E))))
      ) {
        Column(
          modifier = Modifier.padding(16.dp),
          verticalArrangement = Arrangement.spacedBy(10.dp)
        ) {
          Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
          ) {
            Row(
              verticalAlignment = Alignment.CenterVertically,
              horizontalArrangement = Arrangement.spacedBy(6.dp)
            ) {
              Box(
                modifier = Modifier
                  .size(8.dp)
                  .clip(CircleShape)
                  .background(ZoxSuccess)
              )
              Text(
                text = "LIVE CLOUD STDOUT CONSOLE",
                fontSize = 11.sp,
                fontWeight = FontWeight.Bold,
                color = Color.White
              )
            }

            Text(
              text = "STREAMING: 100% REALTIME",
              fontSize = 9.sp,
              color = ZoxOrangeAccent,
              fontWeight = FontWeight.Bold
            )
          }

          logs.forEach { logLine ->
            Text(
              text = logLine,
              fontSize = 11.sp,
              fontFamily = FontFamily.Monospace,
              color = if (logLine.contains("SUPER_ADMIN")) ZoxOrangeAccent else if (logLine.contains("200")) Color(0xFF8CE99A) else Color(0xFFC0BFE0),
              lineHeight = 16.sp
            )
          }
        }
      }
    }
  }
}

// -------------------------------------------------------------
// PANEL 5: JSON DATA EXPORT & BACKUP
// -------------------------------------------------------------
@Composable
private fun WebJsonExportView(
  vaultConfig: MasterVaultConfig,
  fleet: List<FleetVehicle>,
  bookings: List<BookingItem>
) {
  var isExported by remember { mutableStateOf(false) }

  val exportedJson = remember(vaultConfig, fleet, bookings) {
    """
{
  "system_export_timestamp": "2026-08-23T14:05:00Z",
  "environment": "Mizoram Cloud Web & Mobile Production",
  "app_version": "${vaultConfig.system.currentAppVersion}",
  "maintenance_mode": ${vaultConfig.system.isMaintenanceMode},
  "fleet_total": ${fleet.size},
  "bookings_total": ${bookings.size},
  "active_apis": {
    "agora_app_id": "${vaultConfig.agora.appId}",
    "razorpay_merchant": "${vaultConfig.payments.merchantUpiVpa}",
    "admob_app_id": "${vaultConfig.admob.appId}"
  }
}
    """.trimIndent()
  }

  LazyColumn(
    modifier = Modifier.fillMaxSize(),
    verticalArrangement = Arrangement.spacedBy(14.dp)
  ) {
    item {
      Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = ZoxDarkCard)
      ) {
        Column(
          modifier = Modifier.padding(16.dp),
          verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
          Text(
            text = "📦 Export Full System Data (JSON Format)",
            fontSize = 15.sp,
            fontWeight = FontWeight.Bold,
            color = Color.White
          )

          Text(
            text = "Download a complete cloud snapshot of system configuration, active bookings, fleet roster, and vault parameters.",
            fontSize = 11.sp,
            color = Color(0xFFA5A5BC)
          )

          Box(
            modifier = Modifier
              .fillMaxWidth()
              .clip(RoundedCornerShape(10.dp))
              .background(Color(0xFF0D0C14))
              .padding(12.dp)
          ) {
            Text(
              text = exportedJson,
              fontSize = 10.sp,
              fontFamily = FontFamily.Monospace,
              color = Color(0xFFFFD43B),
              lineHeight = 15.sp
            )
          }

          Button(
            onClick = { isExported = true },
            modifier = Modifier
              .fillMaxWidth()
              .height(44.dp)
              .testTag("download_json_btn"),
            colors = ButtonDefaults.buttonColors(containerColor = ZoxOrangeAccent),
            shape = RoundedCornerShape(10.dp)
          ) {
            Icon(
              imageVector = Icons.Default.Download,
              contentDescription = null,
              tint = Color.Black,
              modifier = Modifier.size(16.dp)
            )
            Spacer(modifier = Modifier.width(6.dp))
            Text("Export & Download Snapshot", fontSize = 12.sp, fontWeight = FontWeight.Bold, color = Color.Black)
          }

          if (isExported) {
            Text(
              text = "Snapshot successfully copied and prepared for Web / Cloud storage download!",
              fontSize = 11.sp,
              color = ZoxSuccess,
              fontWeight = FontWeight.SemiBold
            )
          }
        }
      }
    }
  }
}

// -------------------------------------------------------------
// WEB QR SYNC MODAL
// -------------------------------------------------------------
@Composable
fun WebQrSyncDialog(onDismiss: () -> Unit) {
  Dialog(onDismissRequest = onDismiss) {
    Surface(
      shape = RoundedCornerShape(20.dp),
      color = ZoxDarkSurface,
      modifier = Modifier
        .fillMaxWidth()
        .border(1.dp, ZoxPurplePrimary, RoundedCornerShape(20.dp))
    ) {
      Column(
        modifier = Modifier.padding(22.dp),
        verticalArrangement = Arrangement.spacedBy(14.dp),
        horizontalAlignment = Alignment.CenterHorizontally
      ) {
        Box(
          modifier = Modifier
            .size(52.dp)
            .clip(CircleShape)
            .background(ZoxPurpleContainer),
          contentAlignment = Alignment.Center
        ) {
          Icon(
            imageVector = Icons.Default.QrCode,
            contentDescription = null,
            tint = ZoxOrangeAccent,
            modifier = Modifier.size(28.dp)
          )
        }

        Text(
          text = "Pair Desktop Browser Session",
          fontSize = 16.sp,
          fontWeight = FontWeight.Bold,
          color = Color.White
        )

        Text(
          text = "Scan or visit https://web.zoxapps.mizoram.in on your PC or Mac to control your ZOX Super App fleet from large web monitors.",
          fontSize = 12.sp,
          color = Color(0xFFA5A5BC),
          textAlign = TextAlign.Center
        )

        // Visual QR Representation Box
        Box(
          modifier = Modifier
            .size(160.dp)
            .clip(RoundedCornerShape(12.dp))
            .background(Color.White)
            .padding(12.dp),
          contentAlignment = Alignment.Center
        ) {
          Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
          ) {
            Icon(
              imageVector = Icons.Default.QrCode,
              contentDescription = "QR Code",
              tint = Color.Black,
              modifier = Modifier.size(110.dp)
            )
            Text(
              text = "ZOX-WEB-PAIR-2026",
              fontSize = 9.sp,
              fontWeight = FontWeight.Black,
              color = Color.Black
            )
          }
        }

        Button(
          onClick = onDismiss,
          modifier = Modifier
            .fillMaxWidth()
            .height(44.dp),
          colors = ButtonDefaults.buttonColors(containerColor = ZoxPurplePrimary),
          shape = RoundedCornerShape(10.dp)
        ) {
          Text("Done", fontSize = 12.sp, fontWeight = FontWeight.Bold)
        }
      }
    }
  }
}
