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
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AdminPanelSettings
import androidx.compose.material.icons.filled.Api
import androidx.compose.material.icons.filled.Build
import androidx.compose.material.icons.filled.CameraAlt
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Cloud
import androidx.compose.material.icons.filled.CloudSync
import androidx.compose.material.icons.filled.CreditCard
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Engineering
import androidx.compose.material.icons.filled.Extension
import androidx.compose.material.icons.filled.Key
import androidx.compose.material.icons.filled.Language
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Map
import androidx.compose.material.icons.filled.MonetizationOn
import androidx.compose.material.icons.filled.QrCode
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.Save
import androidx.compose.material.icons.filled.Security
import androidx.compose.material.icons.filled.Sms
import androidx.compose.material.icons.filled.Storage
import androidx.compose.material.icons.filled.Terminal
import androidx.compose.material.icons.filled.ThumbDown
import androidx.compose.material.icons.filled.ThumbUp
import androidx.compose.material.icons.filled.VerifiedUser
import androidx.compose.material.icons.filled.Videocam
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material.icons.filled.WifiTethering
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.ScrollableTabRow
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRowDefaults
import androidx.compose.material3.TabRowDefaults.tabIndicatorOffset
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
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
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.model.AppPlugin
import com.example.model.KYCStatus
import com.example.model.KycApplication
import com.example.model.MasterVaultConfig
import com.example.model.SystemMaintenanceConfig
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

@Composable
fun AdminDashboardScreen(
  plugins: List<AppPlugin>,
  vaultConfig: MasterVaultConfig,
  kycQueue: List<KycApplication>,
  routeVehicles: List<com.example.model.RouteVehicle>,
  onTogglePlugin: (pluginId: String, enable: Boolean) -> Unit,
  onInstallUninstallPlugin: (pluginId: String, install: Boolean) -> Unit,
  onConfigurePlugin: (pluginId: String) -> Unit,
  onConfigurePlugin: (pluginId: String) -> Unit,
  onSaveVaultConfig: (MasterVaultConfig) -> Unit,
  onApproveKyc: (applicationId: String) -> Unit,
  onRejectKyc: (applicationId: String, reason: String) -> Unit,
  onOpenWebPortal: () -> Unit = {},
  onOpenEnrollVehicle: () -> Unit = {}
) {
  var selectedTab by remember { mutableIntStateOf(0) }
  var localConfig by remember(vaultConfig) { mutableStateOf(vaultConfig) }

  val tabs = listOf(
    "🧩 Plugin Management & App Store",
    "🔐 Master Vault & DB",
    "🛠️ System Ops & Maintenance",
    "🌐 Web Console & Portal",
    "🪪 KYC Review Queue",
    "📈 Monetization Engine",
    "🚌 Route Vehicles (Counter)"
  )

  Column(
    modifier = Modifier
      .fillMaxSize()
      .background(ZoxDarkBackground)
      .testTag("admin_dashboard_screen")
  ) {
    // 1. Super Admin Header
    Row(
      modifier = Modifier
        .fillMaxWidth()
        .background(
          Brush.horizontalGradient(
            listOf(Color(0xFF2E1748), Color(0xFF1B142D))
          )
        )
        .padding(horizontal = 16.dp, vertical = 14.dp),
      horizontalArrangement = Arrangement.SpaceBetween,
      verticalAlignment = Alignment.CenterVertically
    ) {
      ZoxBrandHeader(logoSize = 34.dp, adminBadge = true)

      Box(
        modifier = Modifier
          .clip(RoundedCornerShape(8.dp))
          .background(Color(0xFF3B2056))
          .border(0.8.dp, ZoxOrangeAccent, RoundedCornerShape(8.dp))
          .padding(horizontal = 8.dp, vertical = 4.dp)
      ) {
        Text(
          text = "NODE: 9378160106",
          fontSize = 10.sp,
          fontWeight = FontWeight.Bold,
          color = ZoxOrangeAccent
        )
      }
    }

    // 2. Tab Navigation
    ScrollableTabRow(
      selectedTabIndex = selectedTab,
      containerColor = Color(0xFF181626),
      contentColor = ZoxOrangeAccent,
      indicator = { tabPositions ->
        TabRowDefaults.SecondaryIndicator(
          Modifier.tabIndicatorOffset(tabPositions[selectedTab]),
          color = ZoxOrangeAccent,
          height = 3.dp
        )
      },
      edgePadding = 12.dp
    ) {
      tabs.forEachIndexed { index, title ->
        Tab(
          selected = selectedTab == index,
          onClick = { selectedTab = index },
          text = {
            Text(
              text = title,
              fontSize = 12.sp,
              fontWeight = if (selectedTab == index) FontWeight.Bold else FontWeight.Normal,
              color = if (selectedTab == index) ZoxOrangeAccent else Color(0xFFA5A5BC)
            )
          }
        )
      }
    }

    // 3. Tab Content
    when (selectedTab) {
      0 -> PluginsControlCenterTab(
        plugins = plugins,
        onTogglePlugin = onTogglePlugin,
        onInstallUninstallPlugin = onInstallUninstallPlugin,
        onConfigurePlugin = onConfigurePlugin
      )
      1 -> MasterInfrastructureVaultTab(
        initialConfig = localConfig,
        onSaveConfig = { updated ->
          localConfig = updated
          onSaveVaultConfig(updated)
        }
      )
      2 -> SystemOpsMaintenanceTab(
        systemConfig = localConfig.system,
        onSaveSystemConfig = { updatedSys ->
          val updated = localConfig.copy(system = updatedSys)
          localConfig = updated
          onSaveVaultConfig(updated)
        }
      )
      3 -> AdminWebConsoleTab(
        vaultConfig = localConfig,
        onOpenFullWebPortal = onOpenWebPortal
      )
      4 -> KycQueueTab(
        kycQueue = kycQueue,
        onApproveKyc = onApproveKyc,
        onRejectKyc = onRejectKyc
      )
      5 -> MonetizationAnalyticsTab(
        admobConfig = localConfig.admob,
        onToggleAdMob = { enabled ->
          val updated = localConfig.copy(admob = localConfig.admob.copy(isEnabled = enabled))
          localConfig = updated
          onSaveVaultConfig(updated)
        }
      )
      6 -> RouteVehiclesTab(
        routeVehicles = routeVehicles,
        onEnrollClick = onOpenEnrollVehicle
      )
    }
  }
}

/**
 * TAB 1: Plugins and Super App Verticals Control Center
 */
@Composable
fun PluginsControlCenterTab(
  plugins: List<AppPlugin>,
  onTogglePlugin: (pluginId: String, enable: Boolean) -> Unit,
  onInstallUninstallPlugin: (pluginId: String, install: Boolean) -> Unit,
  onConfigurePlugin: (pluginId: String) -> Unit
) {
  LazyColumn(
    modifier = Modifier
      .fillMaxSize()
      .testTag("plugins_control_center_tab"),
    contentPadding = PaddingValues(top = 14.dp, bottom = 90.dp, start = 16.dp, end = 16.dp),
    verticalArrangement = Arrangement.spacedBy(12.dp)
  ) {
    item {
      Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = ZoxPurpleContainer),
        border = androidx.compose.foundation.BorderStroke(1.dp, ZoxPurplePrimary)
      ) {
        Row(
          modifier = Modifier
            .fillMaxWidth()
            .padding(14.dp),
          verticalAlignment = Alignment.CenterVertically,
          horizontalArrangement = Arrangement.spacedBy(10.dp)
        ) {
          Icon(Icons.Default.Extension, contentDescription = null, tint = ZoxOrangeAccent, modifier = Modifier.size(24.dp))
          Column {
            Text(
              text = "Live Plugin & Feature-Flag Engine",
              fontSize = 13.sp,
              fontWeight = FontWeight.Bold,
              color = Color.White
            )
            Text(
              text = "Disabling a vertical instantly hides its icon from all user screens in real-time.",
              fontSize = 11.sp,
              color = Color(0xFFD4D4E8)
            )
          }
        }
      }
    }

    items(plugins) { plugin ->
      Card(
        modifier = Modifier
          .fillMaxWidth()
          .testTag("plugin_item_${plugin.id}"),
        shape = RoundedCornerShape(14.dp),
        colors = CardDefaults.cardColors(
          containerColor = if (plugin.isEnabled) ZoxDarkCard else Color(0xFF191824)
        ),
        border = androidx.compose.foundation.BorderStroke(
          1.dp,
          if (plugin.isEnabled) Color(0xFF383556) else Color(0xFF292838)
        )
      ) {
        Row(
          modifier = Modifier
            .fillMaxWidth()
            .padding(14.dp),
          horizontalArrangement = Arrangement.SpaceBetween,
          verticalAlignment = Alignment.CenterVertically
        ) {
          Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(12.dp),
            modifier = Modifier.weight(1f)
          ) {
            Box(
              modifier = Modifier
                .size(42.dp)
                .clip(RoundedCornerShape(10.dp))
                .background(
                  if (plugin.isEnabled) ZoxPurplePrimary else Color(0xFF2C2A3C)
                ),
              contentAlignment = Alignment.Center
            ) {
              Icon(
                imageVector = Icons.Default.Extension,
                contentDescription = null,
                tint = if (plugin.isEnabled) ZoxOrangeAccent else Color(0xFF7A7996)
              )
            }

            Column {
              Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(6.dp)
              ) {
                Text(
                  text = plugin.title,
                  fontSize = 14.sp,
                  fontWeight = FontWeight.Bold,
                  color = if (plugin.isEnabled) Color.White else Color(0xFF8E8EA8)
                )
                if (plugin.badge != null) {
                  Box(
                    modifier = Modifier
                      .clip(RoundedCornerShape(4.dp))
                      .background(Color(0xFF382618))
                      .padding(horizontal = 4.dp, vertical = 1.dp)
                  ) {
                    Text(text = plugin.badge, fontSize = 7.sp, fontWeight = FontWeight.Bold, color = ZoxOrangeAccent)
                  }
                }
              }
              Text(
                text = "${plugin.category.name} • ${plugin.subtitle}",
                fontSize = 11.sp,
                color = Color(0xFFA0A0BA)
              )
            }
          }

          Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(6.dp)
          ) {
            Switch(
              checked = plugin.isEnabled,
              onCheckedChange = { isChecked ->
                onTogglePlugin(plugin.id, isChecked)
              },
              colors = SwitchDefaults.colors(
                checkedThumbColor = ZoxOrangeAccent,
                checkedTrackColor = ZoxPurplePrimary,
                uncheckedThumbColor = Color(0xFF7B7998),
                uncheckedTrackColor = Color(0xFF2A283C)
              ),
              modifier = Modifier.testTag("switch_plugin_${plugin.id}")
            )
          }
        }
      }
    }
  }
}

/**
 * TAB 2: Master Infrastructure, Database, Agora & Payment Gateways Vault
 */
@Composable
fun MasterInfrastructureVaultTab(
  initialConfig: MasterVaultConfig,
  onSaveConfig: (MasterVaultConfig) -> Unit
) {
  var config by remember(initialConfig) { mutableStateOf(initialConfig) }

  // Field states
  var admobAppId by remember { mutableStateOf(config.admob.appId) }
  var admobBannerId by remember { mutableStateOf(config.admob.bannerUnitId) }
  var admobInterstitialId by remember { mutableStateOf(config.admob.interstitialUnitId) }
  var admobRewardedId by remember { mutableStateOf(config.admob.rewardedUnitId) }
  var admobNativeId by remember { mutableStateOf(config.admob.nativeUnitId) }
  var rewardedCoinRate by remember { mutableStateOf(config.admob.rewardedCoinRate.toString()) }

  var apiBaseUrl by remember { mutableStateOf(config.api.baseUrl) }
  var apiWsUrl by remember { mutableStateOf(config.api.webSocketsUrl) }

  var dbHost by remember { mutableStateOf(config.database.hostOrUrl) }
  var dbServiceKey by remember { mutableStateOf(config.database.serviceRoleKey) }

  var s3Bucket by remember { mutableStateOf(config.storage.bucketName) }
  var s3AccessKey by remember { mutableStateOf(config.storage.accessKey) }

  var agoraAppId by remember { mutableStateOf(config.agora.appId) }
  var agoraTokenServer by remember { mutableStateOf(config.agora.tokenServerUrl) }

  var googleMapsKey by remember { mutableStateOf(config.maps.androidApiKey) }

  var razorpayKey by remember { mutableStateOf(config.payments.razorpayKeyId) }
  var upiVpa by remember { mutableStateOf(config.payments.merchantUpiVpa) }

  var smsProvider by remember { mutableStateOf(config.sms.provider) }
  var smsSenderId by remember { mutableStateOf(config.sms.senderId) }

  var motorBaseRate by remember { mutableStateOf(config.tariff.motorHireBaseHourlyRate.toString()) }
  var tirhkahBaseRate by remember { mutableStateOf(config.tariff.tirhkahBaseRate.toString()) }
  var platformFee by remember { mutableStateOf(config.tariff.platformFeePercent.toString()) }

  var isMaintenance by remember { mutableStateOf(config.system.isMaintenanceMode) }
  var minAppVersion by remember { mutableStateOf(config.system.minAppVersion) }

  LazyColumn(
    modifier = Modifier
      .fillMaxSize()
      .testTag("master_infrastructure_vault_tab"),
    contentPadding = PaddingValues(top = 14.dp, bottom = 90.dp, start = 16.dp, end = 16.dp),
    verticalArrangement = Arrangement.spacedBy(14.dp)
  ) {
    // Save Action Bar at Top
    item {
      Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
      ) {
        Column {
          Text(
            text = "Master Infrastructure Vault",
            fontSize = 17.sp,
            fontWeight = FontWeight.Bold,
            color = Color.White
          )
          Text(
            text = "Direct admin editable real-time platform configurations",
            fontSize = 11.sp,
            color = Color(0xFFA0A0BA)
          )
        }

        Button(
          onClick = {
            val updated = config.copy(
              admob = config.admob.copy(
                appId = admobAppId,
                bannerUnitId = admobBannerId,
                interstitialUnitId = admobInterstitialId,
                rewardedUnitId = admobRewardedId,
                nativeUnitId = admobNativeId,
                rewardedCoinRate = rewardedCoinRate.toIntOrNull() ?: 50
              ),
              api = config.api.copy(
                baseUrl = apiBaseUrl,
                webSocketsUrl = apiWsUrl
              ),
              database = config.database.copy(
                hostOrUrl = dbHost,
                serviceRoleKey = dbServiceKey
              ),
              storage = config.storage.copy(
                bucketName = s3Bucket,
                accessKey = s3AccessKey
              ),
              agora = config.agora.copy(
                appId = agoraAppId,
                tokenServerUrl = agoraTokenServer
              ),
              maps = config.maps.copy(
                androidApiKey = googleMapsKey
              ),
              payments = config.payments.copy(
                razorpayKeyId = razorpayKey,
                merchantUpiVpa = upiVpa
              ),
              sms = config.sms.copy(
                provider = smsProvider,
                senderId = smsSenderId
              ),
              tariff = config.tariff.copy(
                motorHireBaseHourlyRate = motorBaseRate.toDoubleOrNull() ?: 99.0,
                tirhkahBaseRate = tirhkahBaseRate.toDoubleOrNull() ?: 49.0,
                platformFeePercent = platformFee.toDoubleOrNull() ?: 5.0
              ),
              system = config.system.copy(
                isMaintenanceMode = isMaintenance,
                minAppVersion = minAppVersion
              )
            )
            onSaveConfig(updated)
          },
          colors = ButtonDefaults.buttonColors(
            containerColor = ZoxOrangeAccent,
            contentColor = Color.Black
          ),
          shape = RoundedCornerShape(10.dp),
          modifier = Modifier.testTag("save_vault_button")
        ) {
          Icon(Icons.Default.Save, contentDescription = null, modifier = Modifier.size(16.dp))
          Spacer(modifier = Modifier.width(4.dp))
          Text("SAVE VAULT", fontSize = 11.sp, fontWeight = FontWeight.Black)
        }
      }
    }

    // 1. AdMob Monetization Config Vault
    item {
      VaultSectionCard(
        title = "Google AdMob Monetization Engine",
        icon = Icons.Default.MonetizationOn
      ) {
        Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
          VaultTextField(label = "AdMob App ID", value = admobAppId, onValueChange = { admobAppId = it })
          VaultTextField(label = "Banner Unit ID", value = admobBannerId, onValueChange = { admobBannerId = it })
          VaultTextField(label = "Interstitial Unit ID", value = admobInterstitialId, onValueChange = { admobInterstitialId = it })
          VaultTextField(label = "Rewarded Video Unit ID", value = admobRewardedId, onValueChange = { admobRewardedId = it })
          VaultTextField(label = "Native Feed Unit ID", value = admobNativeId, onValueChange = { admobNativeId = it })
          VaultTextField(label = "Rewarded Coin Rate (Coins/Ad)", value = rewardedCoinRate, onValueChange = { rewardedCoinRate = it })
        }
      }
    }

    // 2. Self-Hosted Custom API & WebSockets
    item {
      VaultSectionCard(
        title = "Self-Hosted Custom API & Realtime WebSockets",
        icon = Icons.Default.Api
      ) {
        Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
          VaultTextField(label = "API Base URL (HTTPS)", value = apiBaseUrl, onValueChange = { apiBaseUrl = it })
          VaultTextField(label = "WebSockets Endpoint (WSS)", value = apiWsUrl, onValueChange = { apiWsUrl = it })
        }
      }
    }

    // 3. PostgreSQL / Supabase Database Strings
    item {
      VaultSectionCard(
        title = "Autonomous Database Connection Strings",
        icon = Icons.Default.Storage
      ) {
        Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
          VaultTextField(label = "Database Host / Supabase URL", value = dbHost, onValueChange = { dbHost = it })
          VaultTextField(label = "Service Role / Vault Master Key", value = dbServiceKey, onValueChange = { dbServiceKey = it }, isSecret = true)
        }
      }
    }

    // 4. Object Storage (AWS S3 / Cloudflare R2)
    item {
      VaultSectionCard(
        title = "Cloud Object Storage (S3 / R2 Bucket)",
        icon = Icons.Default.Cloud
      ) {
        Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
          VaultTextField(label = "Production Bucket Name", value = s3Bucket, onValueChange = { s3Bucket = it })
          VaultTextField(label = "Access Key ID", value = s3AccessKey, onValueChange = { s3AccessKey = it })
        }
      }
    }

    // 5. Agora Live RTC Video Call Engine
    item {
      VaultSectionCard(
        title = "Agora Live RTC Video Call Infrastructure",
        icon = Icons.Default.Videocam
      ) {
        Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
          VaultTextField(label = "Agora App ID", value = agoraAppId, onValueChange = { agoraAppId = it })
          VaultTextField(label = "Token Generator Server URL", value = agoraTokenServer, onValueChange = { agoraTokenServer = it })
        }
      }
    }

    // 6. Google Maps SDK & Routing
    item {
      VaultSectionCard(
        title = "Google Maps SDK & Geocoding",
        icon = Icons.Default.Map
      ) {
        VaultTextField(label = "Android Maps API Key", value = googleMapsKey, onValueChange = { googleMapsKey = it }, isSecret = true)
      }
    }

    // 7. Payment Gateways (Razorpay & UPI)
    item {
      VaultSectionCard(
        title = "Payment Gateways & Merchant UPI VPA",
        icon = Icons.Default.CreditCard
      ) {
        Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
          VaultTextField(label = "Razorpay Key ID", value = razorpayKey, onValueChange = { razorpayKey = it })
          VaultTextField(label = "Merchant UPI VPA (Auto-Settle)", value = upiVpa, onValueChange = { upiVpa = it })
        }
      }
    }

    // 8. SMS & OTP Credentials
    item {
      VaultSectionCard(
        title = "SMS & OTP Gateway Provider",
        icon = Icons.Default.Sms
      ) {
        Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
          VaultTextField(label = "Provider Name", value = smsProvider, onValueChange = { smsProvider = it })
          VaultTextField(label = "Sender ID (DLT Approved)", value = smsSenderId, onValueChange = { smsSenderId = it })
        }
      }
    }

    // 9. Tariff Engine (Mizoram Pricing)
    item {
      VaultSectionCard(
        title = "Dynamic Tariff Engine & Commission",
        icon = Icons.Default.MonetizationOn
      ) {
        Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
          VaultTextField(label = "Motor Hire Base Rate (₹/hr)", value = motorBaseRate, onValueChange = { motorBaseRate = it })
          VaultTextField(label = "Tirhkah Delivery Base (₹)", value = tirhkahBaseRate, onValueChange = { tirhkahBaseRate = it })
          VaultTextField(label = "Platform Fee (%)", value = platformFee, onValueChange = { platformFee = it })
        }
      }
    }

    // 10. System Maintenance & Version Control
    item {
      VaultSectionCard(
        title = "Global System Maintenance & Version Enforcer",
        icon = Icons.Default.Security
      ) {
        Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
          Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
          ) {
            Text(text = "Global Maintenance Mode", fontSize = 13.sp, color = Color.White)
            Switch(
              checked = isMaintenance,
              onCheckedChange = { isMaintenance = it },
              colors = SwitchDefaults.colors(
                checkedThumbColor = ZoxError,
                checkedTrackColor = Color(0xFF4A1A1E)
              )
            )
          }
          VaultTextField(label = "Minimum Enforced App Version", value = minAppVersion, onValueChange = { minAppVersion = it })
        }
      }
    }
  }
}

@Composable
fun VaultSectionCard(
  title: String,
  icon: androidx.compose.ui.graphics.vector.ImageVector,
  content: @Composable () -> Unit
) {
  Card(
    modifier = Modifier.fillMaxWidth(),
    shape = RoundedCornerShape(16.dp),
    colors = CardDefaults.cardColors(containerColor = ZoxDarkCard),
    border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFF36344E))
  ) {
    Column(
      modifier = Modifier
        .fillMaxWidth()
        .padding(14.dp),
      verticalArrangement = Arrangement.spacedBy(10.dp)
    ) {
      Row(
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(8.dp)
      ) {
        Icon(icon, contentDescription = null, tint = ZoxOrangeAccent, modifier = Modifier.size(20.dp))
        Text(text = title, fontSize = 14.sp, fontWeight = FontWeight.Bold, color = Color.White)
      }
      content()
    }
  }
}

@Composable
fun VaultTextField(
  label: String,
  value: String,
  onValueChange: (String) -> Unit,
  isSecret: Boolean = false
) {
  OutlinedTextField(
    value = value,
    onValueChange = onValueChange,
    label = { Text(label, fontSize = 10.sp, color = Color(0xFFA0A0BA)) },
    singleLine = true,
    modifier = Modifier.fillMaxWidth(),
    colors = OutlinedTextFieldDefaults.colors(
      focusedBorderColor = ZoxOrangeAccent,
      unfocusedBorderColor = Color(0xFF454360),
      focusedTextColor = Color.White,
      unfocusedTextColor = Color.White,
      focusedContainerColor = Color(0xFF181724),
      unfocusedContainerColor = Color(0xFF181724)
    ),
    shape = RoundedCornerShape(10.dp)
  )
}

/**
 * TAB 3: KYC Driving License Verification Queue
 */
@Composable
fun KycQueueTab(
  kycQueue: List<KycApplication>,
  onApproveKyc: (applicationId: String) -> Unit,
  onRejectKyc: (applicationId: String, reason: String) -> Unit
) {
  LazyColumn(
    modifier = Modifier
      .fillMaxSize()
      .testTag("kyc_queue_tab"),
    contentPadding = PaddingValues(top = 14.dp, bottom = 90.dp, start = 16.dp, end = 16.dp),
    verticalArrangement = Arrangement.spacedBy(12.dp)
  ) {
    item {
      Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
      ) {
        Column {
          Text(
            text = "Driving License Verification Queue",
            fontSize = 17.sp,
            fontWeight = FontWeight.Bold,
            color = Color.White
          )
          Text(
            text = "${kycQueue.count { it.status == KYCStatus.PENDING_REVIEW }} Submissions Pending Review",
            fontSize = 11.sp,
            color = ZoxOrangeAccent
          )
        }
      }
    }

    items(kycQueue) { kyc ->
      Card(
        modifier = Modifier
          .fillMaxWidth()
          .testTag("kyc_card_${kyc.id}"),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = ZoxDarkCard),
        border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFF383556))
      ) {
        Column(
          modifier = Modifier
            .fillMaxWidth()
            .padding(14.dp),
          verticalArrangement = Arrangement.spacedBy(10.dp)
        ) {
          Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
          ) {
            Column {
              Text(text = kyc.customerName, fontSize = 14.sp, fontWeight = FontWeight.Bold, color = Color.White)
              Text(text = "Phone: ${kyc.phone}", fontSize = 11.sp, color = Color(0xFFA0A0BA))
            }

            val statusColor = when (kyc.status) {
              KYCStatus.VERIFIED -> ZoxSuccess
              KYCStatus.PENDING_REVIEW, KYCStatus.PENDING_STAFF_APPROVAL -> ZoxWarning
              KYCStatus.NOT_SUBMITTED, KYCStatus.REJECTED -> ZoxError
            }

            Box(
              modifier = Modifier
                .clip(RoundedCornerShape(6.dp))
                .background(statusColor.copy(alpha = 0.2f))
                .padding(horizontal = 8.dp, vertical = 4.dp)
            ) {
              Text(
                text = kyc.status.name,
                fontSize = 9.sp,
                fontWeight = FontWeight.Black,
                color = statusColor
              )
            }
          }

          // Document Details
          Column(
            modifier = Modifier
              .fillMaxWidth()
              .clip(RoundedCornerShape(10.dp))
              .background(Color(0xFF191826))
              .padding(10.dp),
            verticalArrangement = Arrangement.spacedBy(4.dp)
          ) {
            Text(text = "Requested Role: ${kyc.requestedRole.name.replace("_", " ")}", fontSize = 12.sp, fontWeight = FontWeight.Bold, color = ZoxOrangeLight)
            Text(text = "Document: ${kyc.documentType}", fontSize = 11.sp, color = Color.White)
            Text(text = "DL No: ${kyc.documentNumber}", fontSize = 11.sp, fontWeight = FontWeight.Bold, color = ZoxOrangeLight)
            kyc.vehicleDetails?.let {
              Text(text = "Vehicle: $it", fontSize = 11.sp, color = Color.White)
            }
            Text(text = "Category: ${kyc.vehicleCategory} • Expiry: ${kyc.expiryDate}", fontSize = 10.sp, color = Color(0xFFA0A0BA))
            
            // Mock Selfie Preview
            Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(6.dp)) {
              Icon(Icons.Default.CameraAlt, contentDescription = null, tint = ZoxSuccess, modifier = Modifier.size(16.dp))
              Text(text = "Live Selfie Captured & Attached", fontSize = 10.sp, color = ZoxSuccess)
            }

            if (kyc.remarks.isNotEmpty()) {
              Text(text = "Notes: ${kyc.remarks}", fontSize = 10.sp, color = Color(0xFF8D8DA8))
            }
          }

          if (kyc.status == KYCStatus.PENDING_REVIEW || kyc.status == KYCStatus.PENDING_STAFF_APPROVAL) {
            Row(
              modifier = Modifier.fillMaxWidth(),
              horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
              Button(
                onClick = { onApproveKyc(kyc.id) },
                modifier = Modifier.weight(1f).height(40.dp),
                colors = ButtonDefaults.buttonColors(containerColor = ZoxSuccess, contentColor = Color.Black),
                shape = RoundedCornerShape(8.dp)
              ) {
                Icon(Icons.Default.ThumbUp, contentDescription = null, modifier = Modifier.size(16.dp))
                Spacer(modifier = Modifier.width(4.dp))
                Text("APPROVE DL", fontSize = 11.sp, fontWeight = FontWeight.Bold)
              }

              Button(
                onClick = { onRejectKyc(kyc.id, "Image blurred. Re-upload clear DL scan.") },
                modifier = Modifier.weight(1f).height(40.dp),
                colors = ButtonDefaults.buttonColors(containerColor = ZoxError, contentColor = Color.White),
                shape = RoundedCornerShape(8.dp)
              ) {
                Icon(Icons.Default.ThumbDown, contentDescription = null, modifier = Modifier.size(16.dp))
                Spacer(modifier = Modifier.width(4.dp))
                Text("REJECT", fontSize = 11.sp, fontWeight = FontWeight.Bold)
              }
            }
          }
        }
      }
    }
  }
}

/**
 * TAB 4: Monetization Engine & AdMob Analytics
 */
@Composable
fun MonetizationAnalyticsTab(
  admobConfig: com.example.model.AdMobConfig,
  onToggleAdMob: (Boolean) -> Unit
) {
  LazyColumn(
    modifier = Modifier
      .fillMaxSize()
      .testTag("monetization_analytics_tab"),
    contentPadding = PaddingValues(top = 14.dp, bottom = 90.dp, start = 16.dp, end = 16.dp),
    verticalArrangement = Arrangement.spacedBy(14.dp)
  ) {
    item {
      Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(18.dp),
        colors = CardDefaults.cardColors(containerColor = ZoxDarkCard),
        border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFF3A3556))
      ) {
        Row(
          modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp),
          horizontalArrangement = Arrangement.SpaceBetween,
          verticalAlignment = Alignment.CenterVertically
        ) {
          Column {
            Text(text = "Global AdMob Network Master Switch", fontSize = 14.sp, fontWeight = FontWeight.Bold, color = Color.White)
            Text(text = if (admobConfig.isEnabled) "All Ad slots actively rendering in super app" else "Ads disabled across all customer views", fontSize = 11.sp, color = Color(0xFFA0A0BA))
          }

          Switch(
            checked = admobConfig.isEnabled,
            onCheckedChange = onToggleAdMob,
            colors = SwitchDefaults.colors(checkedThumbColor = ZoxOrangeAccent, checkedTrackColor = ZoxPurplePrimary)
          )
        }
      }
    }

    // Revenue Simulator Stats
    item {
      Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(10.dp)
      ) {
        Card(
          modifier = Modifier.weight(1f),
          shape = RoundedCornerShape(14.dp),
          colors = CardDefaults.cardColors(containerColor = ZoxDarkCard)
        ) {
          Column(modifier = Modifier.padding(12.dp), verticalArrangement = Arrangement.spacedBy(4.dp)) {
            Text("Est. Ad Revenue (MTD)", fontSize = 10.sp, color = Color(0xFFA0A0BA))
            Text("₹42,850", fontSize = 18.sp, fontWeight = FontWeight.Black, color = ZoxSuccess)
          }
        }

        Card(
          modifier = Modifier.weight(1f),
          shape = RoundedCornerShape(14.dp),
          colors = CardDefaults.cardColors(containerColor = ZoxDarkCard)
        ) {
          Column(modifier = Modifier.padding(12.dp), verticalArrangement = Arrangement.spacedBy(4.dp)) {
            Text("Rewarded Video eCPM", fontSize = 10.sp, color = Color(0xFFA0A0BA))
            Text("$14.20", fontSize = 18.sp, fontWeight = FontWeight.Black, color = ZoxOrangeAccent)
          }
        }
      }
    }
  }
}

/**
 * TAB 3: System Operations, Scheduled Maintenance & Version Enforcement
 */
@Composable
fun SystemOpsMaintenanceTab(
  systemConfig: SystemMaintenanceConfig,
  onSaveSystemConfig: (SystemMaintenanceConfig) -> Unit
) {
  var isMaintenanceMode by remember(systemConfig) { mutableStateOf(systemConfig.isMaintenanceMode) }
  var maintenanceReason by remember(systemConfig) { mutableStateOf(systemConfig.maintenanceReason) }
  var minAppVersion by remember(systemConfig) { mutableStateOf(systemConfig.minAppVersion) }
  var currentAppVersion by remember(systemConfig) { mutableStateOf(systemConfig.currentAppVersion) }
  var isForceUpdateActive by remember(systemConfig) { mutableStateOf(systemConfig.isForceUpdateActive) }
  var savedToast by remember { mutableStateOf(false) }

  LazyColumn(
    modifier = Modifier
      .fillMaxSize()
      .testTag("system_ops_maintenance_tab"),
    contentPadding = PaddingValues(top = 14.dp, bottom = 90.dp, start = 16.dp, end = 16.dp),
    verticalArrangement = Arrangement.spacedBy(14.dp)
  ) {
    // Master Maintenance Switch Card
    item {
      Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(18.dp),
        colors = CardDefaults.cardColors(containerColor = if (isMaintenanceMode) Color(0xFF331620) else ZoxDarkCard),
        border = androidx.compose.foundation.BorderStroke(
          1.2.dp,
          if (isMaintenanceMode) ZoxError else ZoxPurplePrimary
        )
      ) {
        Column(
          modifier = Modifier.padding(16.dp),
          verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
          Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
          ) {
            Row(
              verticalAlignment = Alignment.CenterVertically,
              horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
              Box(
                modifier = Modifier
                  .size(36.dp)
                  .clip(CircleShape)
                  .background(if (isMaintenanceMode) ZoxError.copy(alpha = 0.2f) else ZoxSuccess.copy(alpha = 0.2f)),
                contentAlignment = Alignment.Center
              ) {
                Icon(
                  imageVector = if (isMaintenanceMode) Icons.Default.Engineering else Icons.Default.CheckCircle,
                  contentDescription = null,
                  tint = if (isMaintenanceMode) ZoxError else ZoxSuccess,
                  modifier = Modifier.size(20.dp)
                )
              }

              Column {
                Text(
                  text = "Full Maintenance Mode",
                  fontSize = 15.sp,
                  fontWeight = FontWeight.Bold,
                  color = Color.White
                )
                Text(
                  text = if (isMaintenanceMode) "Active: Non-admin users locked with maintenance screen" else "Disabled: Super App running in normal operational mode",
                  fontSize = 11.sp,
                  color = if (isMaintenanceMode) Color(0xFFFFB4C0) else Color(0xFFA5A5BC)
                )
              }
            }

            Switch(
              checked = isMaintenanceMode,
              onCheckedChange = { isMaintenanceMode = it },
              colors = SwitchDefaults.colors(
                checkedThumbColor = ZoxError,
                checkedTrackColor = Color(0xFF63182A),
                uncheckedThumbColor = Color(0xFFA0A0BA),
                uncheckedTrackColor = Color(0xFF28253E)
              ),
              modifier = Modifier.testTag("admin_maintenance_toggle")
            )
          }

          if (isMaintenanceMode) {
            Text(
              text = "⚠️ When active, all mobile users attempting to browse services will be greeted by the Full-Screen Maintenance Gateway with cluster node status, ETA countdown, and emergency hotline access.",
              fontSize = 11.sp,
              color = Color(0xFFFFC2CC),
              lineHeight = 15.sp
            )
          }
        }
      }
    }

    // Maintenance Broadcast Reason
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
          Text(
            text = "User-Facing Maintenance Broadcast Message",
            fontSize = 13.sp,
            fontWeight = FontWeight.Bold,
            color = Color.White
          )

          OutlinedTextField(
            value = maintenanceReason,
            onValueChange = { maintenanceReason = it },
            label = { Text("Broadcast Reason / Announcement") },
            modifier = Modifier.fillMaxWidth(),
            maxLines = 3,
            colors = OutlinedTextFieldDefaults.colors(
              focusedBorderColor = ZoxOrangeAccent,
              unfocusedBorderColor = Color(0xFF484568),
              focusedTextColor = Color.White,
              unfocusedTextColor = Color.White
            )
          )

          // Quick Presets
          Text(text = "Quick Broadcast Presets:", fontSize = 11.sp, color = Color(0xFFA0A0BA))
          Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(6.dp)
          ) {
            listOf(
              "DB Optimization" to "Scheduled server vault optimization. Services will resume shortly.",
              "Agora RTC Upgrade" to "Upgrading live video/audio streaming clusters for Mizoram safety lines.",
              "Payment Gateway Maintenance" to "Razorpay & UPI nodes undergoing scheduled bank clearance update."
            ).forEach { (label, preset) ->
              Box(
                modifier = Modifier
                  .clip(RoundedCornerShape(8.dp))
                  .background(Color(0xFF221F34))
                  .border(0.6.dp, Color(0xFF403C60), RoundedCornerShape(8.dp))
                  .clickable { maintenanceReason = preset }
                  .padding(horizontal = 8.dp, vertical = 6.dp)
              ) {
                Text(text = label, fontSize = 9.sp, color = Color(0xFFDDDDED), fontWeight = FontWeight.SemiBold)
              }
            }
          }
        }
      }
    }

    // Version Enforcement & Force Update
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
            text = "App Version Gatekeeper & Force Update",
            fontSize = 13.sp,
            fontWeight = FontWeight.Bold,
            color = Color.White
          )

          Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(10.dp)
          ) {
            OutlinedTextField(
              value = minAppVersion,
              onValueChange = { minAppVersion = it },
              label = { Text("Minimum Version") },
              modifier = Modifier.weight(1f),
              colors = OutlinedTextFieldDefaults.colors(
                focusedBorderColor = ZoxOrangeAccent,
                unfocusedBorderColor = Color(0xFF484568),
                focusedTextColor = Color.White,
                unfocusedTextColor = Color.White
              ),
              singleLine = true
            )

            OutlinedTextField(
              value = currentAppVersion,
              onValueChange = { currentAppVersion = it },
              label = { Text("Current Release") },
              modifier = Modifier.weight(1f),
              colors = OutlinedTextFieldDefaults.colors(
                focusedBorderColor = ZoxOrangeAccent,
                unfocusedBorderColor = Color(0xFF484568),
                focusedTextColor = Color.White,
                unfocusedTextColor = Color.White
              ),
              singleLine = true
            )
          }

          Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
          ) {
            Column {
              Text(text = "Enforce Minimum Version Lock", fontSize = 12.sp, fontWeight = FontWeight.Bold, color = Color.White)
              Text(text = "Forces outdated APK installs to update before proceeding", fontSize = 10.sp, color = Color(0xFFA5A5BC))
            }

            Switch(
              checked = isForceUpdateActive,
              onCheckedChange = { isForceUpdateActive = it },
              colors = SwitchDefaults.colors(checkedThumbColor = ZoxOrangeAccent, checkedTrackColor = ZoxPurplePrimary)
            )
          }
        }
      }
    }

    // Save Maintenance Configuration Button
    item {
      Button(
        onClick = {
          val updated = systemConfig.copy(
            isMaintenanceMode = isMaintenanceMode,
            maintenanceReason = maintenanceReason,
            minAppVersion = minAppVersion,
            currentAppVersion = currentAppVersion,
            isForceUpdateActive = isForceUpdateActive
          )
          onSaveSystemConfig(updated)
          savedToast = true
        },
        modifier = Modifier
          .fillMaxWidth()
          .height(48.dp)
          .testTag("save_system_ops_button"),
        colors = ButtonDefaults.buttonColors(containerColor = if (isMaintenanceMode) ZoxError else ZoxOrangeAccent),
        shape = RoundedCornerShape(12.dp)
      ) {
        Icon(
          imageVector = Icons.Default.Save,
          contentDescription = null,
          tint = if (isMaintenanceMode) Color.White else Color.Black,
          modifier = Modifier.size(18.dp)
        )
        Spacer(modifier = Modifier.width(6.dp))
        Text(
          text = if (isMaintenanceMode) "Apply & Broadcast Maintenance Mode" else "Save Live System Ops State",
          fontSize = 13.sp,
          fontWeight = FontWeight.Bold,
          color = if (isMaintenanceMode) Color.White else Color.Black
        )
      }
    }
  }
}

/**
 * TAB 4: Admin Web Console & Cloud Portal Overview
 */
@Composable
fun AdminWebConsoleTab(
  vaultConfig: MasterVaultConfig,
  onOpenFullWebPortal: () -> Unit
) {
  LazyColumn(
    modifier = Modifier
      .fillMaxSize()
      .testTag("admin_web_console_tab"),
    contentPadding = PaddingValues(top = 14.dp, bottom = 90.dp, start = 16.dp, end = 16.dp),
    verticalArrangement = Arrangement.spacedBy(14.dp)
  ) {
    item {
      Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(18.dp),
        colors = CardDefaults.cardColors(containerColor = ZoxDarkCard),
        border = androidx.compose.foundation.BorderStroke(1.dp, ZoxPurplePrimary)
      ) {
        Column(
          modifier = Modifier.padding(18.dp),
          verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
          Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
          ) {
            Row(
              verticalAlignment = Alignment.CenterVertically,
              horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
              Box(
                modifier = Modifier
                  .size(38.dp)
                  .clip(CircleShape)
                  .background(ZoxPurpleContainer),
                contentAlignment = Alignment.Center
              ) {
                Icon(
                  imageVector = Icons.Default.Language,
                  contentDescription = null,
                  tint = ZoxOrangeAccent,
                  modifier = Modifier.size(22.dp)
                )
              }

              Column {
                Text(
                  text = "ZOX Cloud Web Portal",
                  fontSize = 15.sp,
                  fontWeight = FontWeight.Bold,
                  color = Color.White
                )
                Text(
                  text = "https://web.zoxapps.mizoram.in/portal",
                  fontSize = 11.sp,
                  color = ZoxOrangeAccent,
                  fontFamily = FontFamily.Monospace
                )
              }
            }

            Box(
              modifier = Modifier
                .clip(RoundedCornerShape(8.dp))
                .background(ZoxSuccess.copy(alpha = 0.2f))
                .padding(horizontal = 8.dp, vertical = 4.dp)
            ) {
              Text(
                text = "ONLINE (TLS 1.3)",
                fontSize = 9.sp,
                fontWeight = FontWeight.Bold,
                color = ZoxSuccess
              )
            }
          }

          Text(
            text = "The Web Interface allows operators, web consumers, and administrators to access live fleet dispatching, GPS radar monitoring, OpenAPI testing, server log streams, and database snapshots from any desktop browser.",
            fontSize = 12.sp,
            color = Color(0xFFA5A5BC),
            lineHeight = 17.sp
          )

          Button(
            onClick = onOpenFullWebPortal,
            modifier = Modifier
              .fillMaxWidth()
              .height(46.dp)
              .testTag("launch_web_portal_btn"),
            colors = ButtonDefaults.buttonColors(containerColor = ZoxPurplePrimary),
            shape = RoundedCornerShape(12.dp)
          ) {
            Icon(
              imageVector = Icons.Default.WifiTethering,
              contentDescription = null,
              tint = Color.White,
              modifier = Modifier.size(18.dp)
            )
            Spacer(modifier = Modifier.width(6.dp))
            Text(
              text = "Launch Web Console Browser Preview",
              fontSize = 12.sp,
              fontWeight = FontWeight.Bold,
              color = Color.White
            )
          }
        }
      }
    }

    // Web Capability Cards
    item {
      Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(10.dp)
      ) {
        Card(
          modifier = Modifier.weight(1f),
          shape = RoundedCornerShape(14.dp),
          colors = CardDefaults.cardColors(containerColor = ZoxDarkCard)
        ) {
          Column(modifier = Modifier.padding(12.dp), verticalArrangement = Arrangement.spacedBy(4.dp)) {
            Text("Web REST Endpoints", fontSize = 10.sp, color = Color(0xFFA0A0BA))
            Text("18 Active", fontSize = 16.sp, fontWeight = FontWeight.Bold, color = ZoxOrangeAccent)
          }
        }

        Card(
          modifier = Modifier.weight(1f),
          shape = RoundedCornerShape(14.dp),
          colors = CardDefaults.cardColors(containerColor = ZoxDarkCard)
        ) {
          Column(modifier = Modifier.padding(12.dp), verticalArrangement = Arrangement.spacedBy(4.dp)) {
            Text("WebSockets Latency", fontSize = 10.sp, color = Color(0xFFA0A0BA))
            Text("28ms", fontSize = 16.sp, fontWeight = FontWeight.Bold, color = ZoxSuccess)
          }
        }
      }
    }
  }
}


@Composable
fun RouteVehiclesTab(
  routeVehicles: List<com.example.model.RouteVehicle>,
  onEnrollClick: () -> Unit
) {
  LazyColumn(
    modifier = Modifier
      .fillMaxSize()
      .testTag("route_vehicles_tab"),
    contentPadding = PaddingValues(top = 14.dp, bottom = 90.dp, start = 16.dp, end = 16.dp),
    verticalArrangement = Arrangement.spacedBy(14.dp)
  ) {
    item {
      Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
      ) {
        Column {
          Text("Route Vehicles Desk", fontSize = 17.sp, fontWeight = FontWeight.Bold, color = Color.White)
          Text("${routeVehicles.size} Enrolled Vehicles", fontSize = 11.sp, color = ZoxOrangeAccent)
        }
        Button(
          onClick = onEnrollClick,
          colors = ButtonDefaults.buttonColors(containerColor = ZoxPurplePrimary, contentColor = Color.White),
          shape = RoundedCornerShape(8.dp),
          modifier = Modifier.height(36.dp)
        ) {
          Icon(Icons.Default.CameraAlt, contentDescription = null, modifier = Modifier.size(14.dp))
          Spacer(modifier = Modifier.width(4.dp))
          Text("ENROLL NEW", fontSize = 10.sp, fontWeight = FontWeight.Bold)
        }
      }
    }

    item {
      Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = Color(0xFF2C1E1E)),
        border = androidx.compose.foundation.BorderStroke(1.dp, ZoxError)
      ) {
        Row(
          modifier = Modifier.fillMaxWidth().padding(12.dp),
          verticalAlignment = Alignment.CenterVertically,
          horizontalArrangement = Arrangement.spacedBy(10.dp)
        ) {
          Icon(Icons.Default.Warning, contentDescription = null, tint = ZoxError)
          Column {
            Text("Server Expiry Audit Monitor", fontSize = 12.sp, fontWeight = FontWeight.Bold, color = ZoxError)
            Text("Vehicles with expired documents are auto-suspended.", fontSize = 10.sp, color = Color.White)
          }
        }
      }
    }

    items(routeVehicles) { vehicle ->
      Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(14.dp),
        colors = CardDefaults.cardColors(containerColor = ZoxDarkCard),
        border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFF383556))
      ) {
        Column(modifier = Modifier.padding(14.dp), verticalArrangement = Arrangement.spacedBy(10.dp)) {
          Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
            Column {
              Text(vehicle.regNumber, fontSize = 16.sp, fontWeight = FontWeight.Black, color = Color.White)
              Text("${vehicle.type} • ${vehicle.routeName}", fontSize = 11.sp, color = ZoxOrangeLight)
            }
            val (statusText, statusColor, statusBg) = when(vehicle.status) {
              com.example.model.RouteVehicleStatus.ACTIVE -> Triple("ACTIVE", ZoxSuccess, ZoxSuccess.copy(alpha=0.2f))
              com.example.model.RouteVehicleStatus.EXPIRING_SOON -> Triple("EXPIRING SOON", ZoxWarning, ZoxWarning.copy(alpha=0.2f))
              com.example.model.RouteVehicleStatus.EXPIRED_SUSPENDED -> Triple("SUSPENDED", ZoxError, ZoxError.copy(alpha=0.2f))
            }
            Box(
              modifier = Modifier.clip(RoundedCornerShape(4.dp)).background(statusBg).padding(horizontal = 6.dp, vertical = 2.dp)
            ) {
              Text(statusText, fontSize = 9.sp, fontWeight = FontWeight.Bold, color = statusColor)
            }
          }

          Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
            Column(modifier = Modifier.weight(1f)) {
              Text("Owner: ${vehicle.ownerName}", fontSize = 10.sp, color = Color.White)
              Text("Ph: ${vehicle.ownerPhone}", fontSize = 10.sp, color = Color(0xFFA0A0BA))
            }
            Column(modifier = Modifier.weight(1f)) {
              Text("Driver: ${vehicle.driverName}", fontSize = 10.sp, color = Color.White)
              Text("Ph: ${vehicle.driverPhone}", fontSize = 10.sp, color = Color(0xFFA0A0BA))
            }
          }

          Column(
            modifier = Modifier.fillMaxWidth().clip(RoundedCornerShape(8.dp)).background(Color(0xFF191826)).padding(8.dp),
            verticalArrangement = Arrangement.spacedBy(4.dp)
          ) {
            Text("Documents & Expiry:", fontSize = 10.sp, fontWeight = FontWeight.Bold, color = Color.White)
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
               Text("Insurance", fontSize = 9.sp, color = Color(0xFFA0A0BA))
               Text(vehicle.insuranceExpiry, fontSize = 9.sp, color = if (vehicle.insuranceExpiry < "2026-08-23") ZoxError else Color.White)
            }
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
               Text("Fitness (FC)", fontSize = 9.sp, color = Color(0xFFA0A0BA))
               Text(vehicle.fcExpiry, fontSize = 9.sp, color = if (vehicle.fcExpiry < "2026-08-23") ZoxError else Color.White)
            }
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
               Text("Route Permit", fontSize = 9.sp, color = Color(0xFFA0A0BA))
               Text(vehicle.permitExpiry, fontSize = 9.sp, color = if (vehicle.permitExpiry < "2026-08-23") ZoxError else Color.White)
            }
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
               Text("Driver License", fontSize = 9.sp, color = Color(0xFFA0A0BA))
               Text(vehicle.licenseExpiry, fontSize = 9.sp, color = if (vehicle.licenseExpiry < "2026-08-23") ZoxError else Color.White)
            }
          }
        }
      }
    }
  }
}
