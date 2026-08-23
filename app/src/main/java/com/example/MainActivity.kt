package com.example

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountBalanceWallet
import androidx.compose.material.icons.filled.DirectionsCar
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Snackbar
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.model.UserRole
import com.example.ui.components.AdMobInterstitialAdModal
import com.example.ui.components.AdMobRewardedVideoModal
import com.example.ui.screens.AddMoneyModal
import com.example.ui.screens.AuthScreen
import com.example.ui.screens.BookingsScreen
import com.example.ui.screens.CityPickerModal
import com.example.ui.screens.HomeScreen
import com.example.ui.screens.KycUploadModal
import com.example.ui.screens.MaintenanceModeScreen
import com.example.ui.screens.MotorHireBookingModal
import com.example.ui.screens.ProfileScreen
import com.example.ui.screens.TirhkahBookingModal
import com.example.ui.screens.VideoCallScreen
import com.example.ui.screens.WalletScreen
import com.example.ui.screens.WebPortalScreen
import com.example.ui.theme.MyApplicationTheme
import com.example.ui.theme.ZoxDarkBackground
import com.example.ui.theme.ZoxDarkSurface
import com.example.ui.theme.ZoxOrangeAccent
import com.example.ui.theme.ZoxPurpleContainer
import com.example.ui.theme.ZoxPurplePrimary
import com.example.viewmodel.AppTab
import com.example.viewmodel.ZoxViewModel

class MainActivity : ComponentActivity() {
  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)
    enableEdgeToEdge()
    setContent {
      MyApplicationTheme(darkTheme = true) {
        ZoxSuperApp()
      }
    }
  }
}

@Composable
fun ZoxSuperApp(
  viewModel: ZoxViewModel = viewModel()
) {
  val uiState by viewModel.uiState.collectAsState()
  val userProfile by viewModel.userProfile.collectAsState()
  val wallet by viewModel.wallet.collectAsState()
  val transactions by viewModel.transactions.collectAsState()
  val plugins by viewModel.plugins.collectAsState()
  val vaultConfig by viewModel.vaultConfig.collectAsState()
  val kycQueue by viewModel.kycQueue.collectAsState()
  val routeVehicles by viewModel.routeVehicles.collectAsState()
  val fleet by viewModel.fleet.collectAsState()
  val bookings by viewModel.bookings.collectAsState()
  val selectedCity by viewModel.selectedCity.collectAsState()

  val snackbarHostState = remember { SnackbarHostState() }

  LaunchedEffect(uiState.alertMessage) {
    uiState.alertMessage?.let { msg ->
      snackbarHostState.showSnackbar(msg)
      viewModel.clearAlert()
    }
  }

  // 1. Authentication Flow Gate
  if (!uiState.isAuthenticated) {
    AuthScreen(
      onAuthenticated = { phone, role ->
        viewModel.login(phone, role)
      }
    )
    return
  }

  // 1.5. Full System Maintenance Mode Gate (Protects Non-Admin Users with Emergency Bypass)
  if (vaultConfig.system.isMaintenanceMode && userProfile.role != UserRole.SUPER_ADMIN && userProfile.role != UserRole.MODERATOR && !uiState.isMaintenanceBypassed) {
    MaintenanceModeScreen(
      maintenanceConfig = vaultConfig.system,
      onAdminBypassSuccess = { viewModel.setMaintenanceBypassed(true) },
      onOpenWebPortal = { viewModel.toggleWebPortal(true) },
      onEmergencyCall = { num ->
        viewModel.triggerAlert("Dialing emergency line: $num (24/7 Operations active)")
      }
    )
    return
  }

  // 2. Main Super App Shell
  Box(
    modifier = Modifier
      .fillMaxSize()
      .background(ZoxDarkBackground),
    contentAlignment = Alignment.Center
  ) {
    Scaffold(
      modifier = Modifier
        .fillMaxSize()
        .widthIn(max = 1000.dp)
        .background(ZoxDarkBackground)
        .testTag("super_app_scaffold"),
      bottomBar = {
        // Modern Bottom Navigation Bar with safe inset handling
        NavigationBar(
          modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(topStart = 20.dp, topEnd = 20.dp))
            .border(0.8.dp, Color(0xFF2E2C44), RoundedCornerShape(topStart = 20.dp, topEnd = 20.dp))
            .navigationBarsPadding()
            .testTag("bottom_nav_bar"),
          containerColor = Color(0xFF161524),
          tonalElevation = 8.dp
        ) {
          // HOME
          NavigationBarItem(
            selected = uiState.currentTab == AppTab.HOME,
            onClick = { viewModel.setTab(AppTab.HOME) },
            icon = {
              Icon(
                imageVector = Icons.Default.Home,
                contentDescription = "Home",
                modifier = Modifier.size(24.dp)
              )
            },
            label = { Text("Home", fontSize = 10.sp, fontWeight = FontWeight.SemiBold) },
            colors = NavigationBarItemDefaults.colors(
              selectedIconColor = Color.Black,
              selectedTextColor = ZoxOrangeAccent,
              indicatorColor = ZoxOrangeAccent,
              unselectedIconColor = Color(0xFFA0A0BA),
              unselectedTextColor = Color(0xFFA0A0BA)
            ),
            modifier = Modifier.testTag("nav_home")
          )

          // BOOKINGS
          NavigationBarItem(
            selected = uiState.currentTab == AppTab.BOOKINGS,
            onClick = { viewModel.setTab(AppTab.BOOKINGS) },
            icon = {
              Icon(
                imageVector = Icons.Default.DirectionsCar,
                contentDescription = "Bookings",
                modifier = Modifier.size(24.dp)
              )
            },
            label = { Text("Bookings", fontSize = 10.sp, fontWeight = FontWeight.SemiBold) },
            colors = NavigationBarItemDefaults.colors(
              selectedIconColor = Color.Black,
              selectedTextColor = ZoxOrangeAccent,
              indicatorColor = ZoxOrangeAccent,
              unselectedIconColor = Color(0xFFA0A0BA),
              unselectedTextColor = Color(0xFFA0A0BA)
            ),
            modifier = Modifier.testTag("nav_bookings")
          )

          // WALLET
          NavigationBarItem(
            selected = uiState.currentTab == AppTab.WALLET,
            onClick = { viewModel.setTab(AppTab.WALLET) },
            icon = {
              Icon(
                imageVector = Icons.Default.AccountBalanceWallet,
                contentDescription = "Wallet",
                modifier = Modifier.size(24.dp)
              )
            },
            label = { Text("Wallet", fontSize = 10.sp, fontWeight = FontWeight.SemiBold) },
            colors = NavigationBarItemDefaults.colors(
              selectedIconColor = Color.Black,
              selectedTextColor = ZoxOrangeAccent,
              indicatorColor = ZoxOrangeAccent,
              unselectedIconColor = Color(0xFFA0A0BA),
              unselectedTextColor = Color(0xFFA0A0BA)
            ),
            modifier = Modifier.testTag("nav_wallet")
          )

          // PROFILE
          NavigationBarItem(
            selected = uiState.currentTab == AppTab.PROFILE,
            onClick = { viewModel.setTab(AppTab.PROFILE) },
            icon = {
              Icon(
                imageVector = Icons.Default.Person,
                contentDescription = "Profile",
                modifier = Modifier.size(24.dp)
              )
            },
            label = { Text("Profile", fontSize = 10.sp, fontWeight = FontWeight.SemiBold) },
            colors = NavigationBarItemDefaults.colors(
              selectedIconColor = Color.Black,
              selectedTextColor = ZoxOrangeAccent,
              indicatorColor = ZoxOrangeAccent,
              unselectedIconColor = Color(0xFFA0A0BA),
              unselectedTextColor = Color(0xFFA0A0BA)
            ),
            modifier = Modifier.testTag("nav_profile")
          )

        }
      },
      snackbarHost = {
        SnackbarHost(hostState = snackbarHostState) { data ->
          Snackbar(
            snackbarData = data,
            containerColor = ZoxPurpleContainer,
            contentColor = Color.White,
            actionColor = ZoxOrangeAccent,
            shape = RoundedCornerShape(12.dp),
            modifier = Modifier.padding(16.dp)
          )
        }
      }
    ) { innerPadding ->
      Box(
        modifier = Modifier
          .fillMaxSize()
          .padding(innerPadding)
      ) {
        when (uiState.currentTab) {
          AppTab.HOME -> HomeScreen(
            userProfile = userProfile,
            walletData = wallet,
            selectedCity = selectedCity,
            plugins = plugins,
            vaultConfig = vaultConfig,
            fleet = fleet,
            onCityClick = { viewModel.toggleCityPicker(true) },
            onWalletClick = { viewModel.setTab(AppTab.WALLET) },
            onMotorHireClick = { viewModel.toggleMotorHireModal(true) },
            onTirhkahClick = { viewModel.toggleTirhkahModal(true) },
            onVideoCallClick = { viewModel.startVideoCall() },
            onWatchAdClick = { viewModel.triggerRewardedAd() },
            onPluginClick = { pluginId ->
              viewModel.setTab(AppTab.BOOKINGS)
            },
            onWebPortalClick = { viewModel.toggleWebPortal(true) }
          )
          AppTab.BOOKINGS -> BookingsScreen(
            bookings = bookings,
            onNewBookingClick = { viewModel.setTab(AppTab.HOME) }
          )
          AppTab.WALLET -> WalletScreen(
            wallet = wallet,
            transactions = transactions,
            isAdMobEnabled = vaultConfig.admob.isEnabled,
            onTopUpClick = { viewModel.toggleAddMoneyModal(true) },
            onWatchRewardedAdClick = { viewModel.triggerRewardedAd() }
          )
          AppTab.PROFILE -> ProfileScreen(
            userProfile = userProfile,
            walletData = wallet,
            activeBookingsCount = bookings.count { it.status != com.example.model.BookingStatus.COMPLETED },
            onUploadKycClick = { viewModel.toggleKycModal(true) },
            onLanguageToggle = { viewModel.toggleLanguage() },
            onSwitchRoleClick = { role ->
              viewModel.login(if (role == UserRole.SUPER_ADMIN) "9378160106" else "9862345678", role)
            },
            onLogoutClick = { viewModel.logout() },
            onOpenWebPortal = { viewModel.toggleWebPortal(true) }
          )
        }
      }
    }
  }

  // 3. Full-Screen RTC Video Call Overlay
  AnimatedVisibility(
    visible = uiState.isVideoCallActive,
    enter = slideInVertically(initialOffsetY = { it }) + fadeIn(),
    exit = slideOutVertically(targetOffsetY = { it }) + fadeOut()
  ) {
    VideoCallScreen(
      agoraConfig = vaultConfig.agora,
      callerName = "ZOX RTC Support Engineer",
      callerRole = "Technical Dispatch & Safety Lead",
      onEndCall = { viewModel.endVideoCall() }
    )
  }

  // 4. Rewarded Video Ad Modal
  if (uiState.showRewardedAdModal && vaultConfig.admob.isEnabled) {
    AdMobRewardedVideoModal(
      adMobConfig = vaultConfig.admob,
      onDismiss = { viewModel.dismissRewardedAd() },
      onRewardEarned = { coins -> viewModel.onRewardedAdCompleted(coins) }
    )
  }

  // 5. Interstitial Ad Modal
  if (uiState.showInterstitialAdModal && vaultConfig.admob.isEnabled) {
    AdMobInterstitialAdModal(
      adMobConfig = vaultConfig.admob,
      onDismiss = { viewModel.dismissInterstitialAd() }
    )
  }

  // 6. City Picker Modal
  if (uiState.showCityPickerModal) {
    CityPickerModal(
      currentCity = selectedCity,
      onCitySelected = { city -> viewModel.setCity(city) },
      onDismiss = { viewModel.toggleCityPicker(false) }
    )
  }

  // 7. Motor Hire Booking Modal
  if (uiState.showMotorHireBookingModal) {
    MotorHireBookingModal(
      fleet = fleet,
      onConfirmBooking = { vehicleName, hours, fare, pickup ->
        viewModel.bookMotorHire(vehicleName, hours, fare, pickup)
      },
      onDismiss = { viewModel.toggleMotorHireModal(false) }
    )
  }

  // 8. Tirhkah Errand Modal
  if (uiState.showTirhkahBookingModal) {
    TirhkahBookingModal(
      onConfirmBooking = { item, pickup, dropoff, fare ->
        viewModel.bookTirhkah(item, pickup, dropoff, fare)
      },
      onDismiss = { viewModel.toggleTirhkahModal(false) }
    )
  }

  // 9. Add Money Modal
  if (uiState.showAddMoneyModal) {
    AddMoneyModal(
      onTopUpConfirmed = { amount -> viewModel.topUpWallet(amount) },
      onDismiss = { viewModel.toggleAddMoneyModal(false) }
    )
  }

  // 10. KYC Upload Modal
  if (uiState.showKycUploadModal) {
    KycUploadModal(
      onSubmitKyc = { docType, docNumber, reqRole, selfieUrl, vehicleDet ->
        viewModel.submitKyc(docType, docNumber, reqRole, selfieUrl, vehicleDet)
      },
      onDismiss = { viewModel.toggleKycModal(false) }
    )
  }

  // 10.2 Route Vehicle Enrollment Modal
  if (uiState.showRouteVehicleModal) {
    com.example.ui.screens.RouteVehicleEnrollmentModal(
      onEnroll = { vehicle -> viewModel.enrollRouteVehicle(vehicle) },
      onDismiss = { viewModel.toggleRouteVehicleModal(false) }
    )
  }

  // 10.5 Onboarding Modal
  if (uiState.showOnboardingModal) {
    com.example.ui.screens.OnboardingModal(
      onSave = { name, email, address, emergencyContact ->
        viewModel.saveOnboardingData(name, email, address, emergencyContact)
      }
    )
  }

  // 11. Full-Screen Web Interface & Cloud Portal Browser
  if (uiState.showWebPortalModal) {
    WebPortalScreen(
      vaultConfig = vaultConfig,
      fleet = fleet,
      bookings = bookings,
      onClose = { viewModel.toggleWebPortal(false) },
      onQuickBook = { title, pickup, fare ->
        viewModel.dispatchWebBooking(title, pickup, fare)
      }
    )
  }
}

@Preview(showBackground = true, showSystemUi = true, name = "ZOX Super App")
@Composable
fun ZoxSuperAppPreview() {
  MyApplicationTheme(darkTheme = true) {
    ZoxSuperApp()
  }
}
