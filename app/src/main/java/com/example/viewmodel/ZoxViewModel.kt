package com.example.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.data.AppRepository
import com.example.model.BookingItem
import com.example.model.BookingType
import com.example.model.MasterVaultConfig
import com.example.model.UserRole
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

enum class AppTab {
  HOME,
  BOOKINGS,
  WALLET,
  PROFILE
}

data class ZoxUiState(
  val currentTab: AppTab = AppTab.HOME,
  val isAuthenticated: Boolean = true,
  val showOnboardingModal: Boolean = false,
  val isVideoCallActive: Boolean = false,
  val showRewardedAdModal: Boolean = false,
  val showInterstitialAdModal: Boolean = false,
  val showCityPickerModal: Boolean = false,
  val showMotorHireBookingModal: Boolean = false,
  val showTirhkahBookingModal: Boolean = false,
  val showPluginBookingModal: Boolean = false,
  val selectedPluginId: String? = null,
  val showAddMoneyModal: Boolean = false,
  val showKycUploadModal: Boolean = false,
  val showWebPortalModal: Boolean = false,
  val showPluginConfigModal: Boolean = false,
  val selectedPluginConfigId: String? = null,
  val showRouteVehicleModal: Boolean = false,
  val isMaintenanceBypassed: Boolean = false,
  val showMaintenanceBanner: Boolean = false,
  val lastRewardedCoinsEarned: Int = 0,
  val alertMessage: String? = null
)

class ZoxViewModel(
  private val repository: AppRepository = AppRepository()
) : ViewModel() {

  private val _uiState = MutableStateFlow(ZoxUiState())
  val uiState: StateFlow<ZoxUiState> = _uiState.asStateFlow()

  val userProfile = repository.userProfile
  val wallet = repository.wallet
  val transactions = repository.transactions
  val plugins = repository.plugins
  val vaultConfig = repository.vaultConfig
  val pluginConfigs = repository.pluginConfigs
  val kycQueue = repository.kycQueue
  val fleet = repository.fleet
  val bookings = repository.bookings
  val selectedCity = repository.selectedCity

  val routeVehicles = repository.routeVehicles

  fun setTab(tab: AppTab) {
    _uiState.value = _uiState.value.copy(currentTab = tab)
  }

  fun login(phone: String, role: UserRole) {
    repository.switchUserRole(role, phone)
    val isFirstTime = !listOf("9378160106", "9862345678", "1122334455").contains(phone)
    _uiState.value = _uiState.value.copy(
      isAuthenticated = true,
      showOnboardingModal = isFirstTime,
      currentTab = AppTab.HOME
    )
  }

  fun saveOnboardingData(name: String, email: String, address: String, emergencyContact: String) {
    repository.updateUserProfile(
      userProfile.value.copy(
        name = name,
        email = email,
        emergencyContact = emergencyContact
      )
    )
    _uiState.value = _uiState.value.copy(
      showOnboardingModal = false,
      alertMessage = "Profile setup complete! Welcome to ZOX."
    )
  }

  fun logout() {
    _uiState.value = _uiState.value.copy(isAuthenticated = false, currentTab = AppTab.HOME)
  }

  fun setCity(city: String) {
    repository.setCity(city)
    _uiState.value = _uiState.value.copy(showCityPickerModal = false)
  }

  fun togglePlugin(pluginId: String, enable: Boolean) {
    repository.togglePlugin(pluginId, enable)
  }

  fun installUninstallPlugin(pluginId: String, install: Boolean) {
    repository.installUninstallPlugin(pluginId, install)
  }

  fun saveVaultConfig(updated: MasterVaultConfig) {
    repository.updateVaultConfig(updated)
    _uiState.value = _uiState.value.copy(alertMessage = "Master Infrastructure Vault saved successfully to live runtime!")
  }

  fun approveKyc(applicationId: String) {
    repository.approveKyc(applicationId)
  }

  fun rejectKyc(applicationId: String, reason: String) {
    repository.rejectKyc(applicationId, reason)
  }

  fun submitKyc(docType: String, docNumber: String, requestedRole: UserRole, selfieUrl: String, vehicleDetails: String?) {
    repository.submitKycDocument(docType, docNumber, requestedRole, selfieUrl, vehicleDetails)
    _uiState.value = _uiState.value.copy(
      showKycUploadModal = false,
      alertMessage = "KYC Uploaded! Identity verification pending in Staff Queue."
    )
  }

  fun startVideoCall() {
    _uiState.value = _uiState.value.copy(isVideoCallActive = true)
  }

  fun endVideoCall() {
    _uiState.value = _uiState.value.copy(isVideoCallActive = false)
  }

  fun triggerRewardedAd() {
    _uiState.value = _uiState.value.copy(showRewardedAdModal = true)
  }

  fun dismissRewardedAd() {
    _uiState.value = _uiState.value.copy(showRewardedAdModal = false)
  }

  fun onRewardedAdCompleted(coins: Int) {
    if (!vaultConfig.value.admob.rewardsEnabled) {
      _uiState.value = _uiState.value.copy(showRewardedAdModal = false, alertMessage = "Rewards are currently disabled by Admin.")
      return
    }
    repository.rewardUserCoins(coins)
    _uiState.value = _uiState.value.copy(
      showRewardedAdModal = false,
      lastRewardedCoinsEarned = coins,
      alertMessage = "Success! Credited +$coins ZOX Loyalty Coins to your wallet."
    )
  }

  fun triggerInterstitialAd() {
    if (vaultConfig.value.admob.isEnabled) {
      _uiState.value = _uiState.value.copy(showInterstitialAdModal = true)
    }
  }

  fun dismissInterstitialAd() {
    _uiState.value = _uiState.value.copy(showInterstitialAdModal = false)
  }

  fun topUpWallet(amount: Double) {
    repository.topUpWallet(amount)
    _uiState.value = _uiState.value.copy(
      showAddMoneyModal = false,
      alertMessage = "Added ₹$amount to ZOX Wallet via Razorpay UPI."
    )
  }

  fun bookMotorHire(vehicleName: String, hours: Int, fare: Double, pickup: String) {
    repository.createBooking(
      type = BookingType.MOTOR_HIRE,
      title = "Motor Hire: $vehicleName",
      subtitle = "$hours Hours Self-Drive",
      pickup = pickup,
      dropoff = pickup,
      fare = fare
    )
    _uiState.value = _uiState.value.copy(
      showMotorHireBookingModal = false,
      alertMessage = "Motor Hire confirmed! Driver dispatched with vehicle."
    )
    triggerInterstitialAd()
  }

  fun bookTirhkah(itemDescription: String, pickup: String, dropoff: String, fare: Double) {
    repository.createBooking(
      type = BookingType.TIRHKAH_ERRAND,
      title = "Tirhkah: $itemDescription",
      subtitle = "Urgent Runner Delivery",
      pickup = pickup,
      dropoff = dropoff,
      fare = fare
    )
    _uiState.value = _uiState.value.copy(
      showTirhkahBookingModal = false,
      alertMessage = "Tirhkah errand booked! Nearest runner assigned."
    )
    triggerInterstitialAd()
  }

  fun toggleWebPortal(show: Boolean) {
    _uiState.value = _uiState.value.copy(showWebPortalModal = show)
  }

  fun setMaintenanceBypassed(bypassed: Boolean) {
    _uiState.value = _uiState.value.copy(
      isMaintenanceBypassed = bypassed,
      alertMessage = if (bypassed) "Admin Vault access granted via Master Bypass PIN!" else null
    )
  }

  fun toggleSystemMaintenance(enable: Boolean, reason: String = "Scheduled server vault optimization. Services will resume shortly.") {
    val current = vaultConfig.value
    val updated = current.copy(
      system = current.system.copy(
        isMaintenanceMode = enable,
        maintenanceReason = reason
      )
    )
    repository.updateVaultConfig(updated)
    _uiState.value = _uiState.value.copy(
      alertMessage = if (enable) "System-wide Maintenance Mode activated!" else "System Maintenance Mode disabled. Platform fully live!"
    )
  }

  fun dispatchWebBooking(title: String, pickup: String, fare: Double) {
    repository.createBooking(
      type = BookingType.MOTOR_HIRE,
      title = title,
      subtitle = "Dispatched from ZOX Web Cloud Console",
      pickup = pickup,
      dropoff = "Aizawl Destination",
      fare = fare
    )
    _uiState.value = _uiState.value.copy(
      alertMessage = "Web Order dispatched successfully! Live dispatch runner assigned."
    )
  }

  fun toggleCityPicker(show: Boolean) {
    _uiState.value = _uiState.value.copy(showCityPickerModal = show)
  }

  fun toggleMotorHireModal(show: Boolean) {
    _uiState.value = _uiState.value.copy(showMotorHireBookingModal = show)
  }

  fun toggleTirhkahModal(show: Boolean) {
    _uiState.value = _uiState.value.copy(showTirhkahBookingModal = show)
  }

  fun openPluginBooking(pluginId: String) {
    _uiState.value = _uiState.value.copy(showPluginBookingModal = true, selectedPluginId = pluginId)
  }

  fun closePluginBooking() {
    _uiState.value = _uiState.value.copy(showPluginBookingModal = false, selectedPluginId = null)
  }

  fun bookPluginService(pluginId: String, pickup: String, destination: String, fare: Double) {
    val plugin = plugins.value.firstOrNull { it.id == pluginId }
    repository.createBooking(
      type = when (pluginId) {
        "plugin_taxi" -> BookingType.TAXI_RIDE
        "plugin_food" -> BookingType.FOOD_DELIVERY
        else -> BookingType.TIRHKAH_ERRAND
      },
      title = "${plugin?.title ?: "ZOX Service"} booking",
      subtitle = plugin?.subtitle ?: "ZOX service request",
      pickup = pickup,
      dropoff = destination,
      fare = fare
    )
    closePluginBooking()
    _uiState.value = _uiState.value.copy(alertMessage = "${plugin?.title ?: "ZOX service"} request submitted successfully.")
  }

  fun toggleAddMoneyModal(show: Boolean) {
    _uiState.value = _uiState.value.copy(showAddMoneyModal = show)
  }

  fun toggleKycModal(show: Boolean) {
    _uiState.value = _uiState.value.copy(showKycUploadModal = show)
  }

  fun toggleRouteVehicleModal(show: Boolean) {
    _uiState.value = _uiState.value.copy(showRouteVehicleModal = show)
  }

  fun enrollRouteVehicle(vehicle: com.example.model.RouteVehicle) {
    repository.enrollRouteVehicle(vehicle)
    _uiState.value = _uiState.value.copy(
      showRouteVehicleModal = false,
      alertMessage = "Route Vehicle enrolled successfully! Counter Desk notified."
    )
    runExpiryAudit()
  }

  fun runExpiryAudit() {
    repository.runExpiryAudit()
  }

  fun toggleLanguage() {
    repository.toggleLanguage()
  }

  fun triggerAlert(message: String) {
    _uiState.value = _uiState.value.copy(alertMessage = message)
  }

  fun clearAlert() {
    _uiState.value = _uiState.value.copy(alertMessage = null)
  }

  fun togglePluginConfigModal(show: Boolean, pluginId: String? = null) {
    _uiState.value = _uiState.value.copy(showPluginConfigModal = show, selectedPluginConfigId = pluginId)
  }

  fun updatePluginConfigs(configs: com.example.model.AppPluginConfigs) {
    repository.updatePluginConfigs(configs)
    _uiState.value = _uiState.value.copy(showPluginConfigModal = false, alertMessage = "Plugin configurations updated successfully!")
  }
}
