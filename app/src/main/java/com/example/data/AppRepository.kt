package com.example.data

import com.example.model.AdMobConfig
import com.example.model.AppPlugin
import com.example.model.BookingItem
import com.example.model.BookingStatus
import com.example.model.BookingType
import com.example.model.FleetVehicle
import com.example.model.KYCStatus
import com.example.model.KycApplication
import com.example.model.MasterVaultConfig
import com.example.model.PluginCategory
import com.example.model.TransactionItem
import com.example.model.TransactionType
import com.example.model.UserProfile
import com.example.model.UserRole
import com.example.model.WalletData
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update

class AppRepository {

  // Current logged in user profile
  private val _userProfile = MutableStateFlow(UserProfile())
  val userProfile: StateFlow<UserProfile> = _userProfile.asStateFlow()

  // User wallet
  private val _wallet = MutableStateFlow(WalletData())
  val wallet: StateFlow<WalletData> = _wallet.asStateFlow()

  // Transaction history
  private val _transactions = MutableStateFlow<List<TransactionItem>>(
    listOf(
      TransactionItem("tx_101", "Motor Hire: Royal Enfield Hunter 350", "Mobility", TransactionType.DEBIT, 450.0, "Today, 10:15 AM"),
      TransactionItem("tx_102", "AdMob Rewarded Video Reward", "Monetization", TransactionType.REWARD, 50.0, "Today, 09:30 AM", "Rewarded (+50 Coins)"),
      TransactionItem("tx_103", "Wallet Top-up via Razorpay UPI", "Financial", TransactionType.CREDIT, 1000.0, "Yesterday, 04:20 PM"),
      TransactionItem("tx_104", "Tirhkah Express Delivery (Khatla to Bawngkawn)", "Logistics", TransactionType.DEBIT, 95.0, "21 Aug 2026, 02:40 PM")
    )
  )
  val transactions: StateFlow<List<TransactionItem>> = _transactions.asStateFlow()

  // Dynamic Plugins List (10 Verticals)
  private val _plugins = MutableStateFlow<List<AppPlugin>>(
    listOf(
      AppPlugin("plugin_motor_hire", "Motor Hire", "Bike & Car Self-Drive Rental", "two_wheeler", PluginCategory.MOBILITY, isInstalled = true, isEnabled = true, badge = "POPULAR"),
      AppPlugin("plugin_tirhkah", "Tirhkah / Runner", "On-Demand Errand & Delivery", "local_shipping", PluginCategory.LOGISTICS, isInstalled = true, isEnabled = true, badge = "HOT"),
      AppPlugin("plugin_video_call", "Video Call Support", "Live RTC Help & Safety Video", "videocam", PluginCategory.COMMUNICATION, isInstalled = true, isEnabled = true, badge = "RTC LIVE"),
      AppPlugin("plugin_taxi", "Taxi Booking", "City Cabs & Outstation Rides", "local_taxi", PluginCategory.MOBILITY, isInstalled = true, isEnabled = true),
      AppPlugin("plugin_food", "Food Delivery", "Local Mizo Kitchens & Cafes", "restaurant", PluginCategory.COMMERCE, isInstalled = true, isEnabled = true),
      AppPlugin("plugin_ecommerce", "E-Commerce", "Fashion, Handlooms & Electronics", "shopping_bag", PluginCategory.COMMERCE, isInstalled = true, isEnabled = true),
      AppPlugin("plugin_grocery", "Grocery Express", "30-Min Fresh Doorstep Delivery", "storefront", PluginCategory.COMMERCE, isInstalled = true, isEnabled = true),
      AppPlugin("plugin_parcel", "Parcel Logistics", "Inter-District Shipping across Mizoram", "inventory_2", PluginCategory.LOGISTICS, isInstalled = true, isEnabled = true),
      AppPlugin("plugin_bills", "Bill Payments", "Power, Water, LPG & Mobile Recharge", "receipt_long", PluginCategory.SERVICES, isInstalled = true, isEnabled = true),
      AppPlugin("plugin_admob", "AdMob Monetization", "Dynamic Native & Rewarded Ads", "monetization_on", PluginCategory.MONETIZATION, isInstalled = true, isEnabled = true, badge = "ADS NETWORK"),
      AppPlugin("plugin_store", "Local Store", "Mizo shops and doorstep shopping", "storefront", PluginCategory.COMMERCE, isInstalled = true, isEnabled = true, isNew = true),
      AppPlugin("plugin_workshop", "Workshop", "Vehicle servicing and inspection", "build", PluginCategory.SERVICES, isInstalled = true, isEnabled = true, isNew = true),
      AppPlugin("plugin_mechanic", "Mechanic Rescue", "Roadside repair and breakdown help", "handyman", PluginCategory.MOBILITY, isInstalled = true, isEnabled = true, isNew = true),
      AppPlugin("plugin_medical", "Medical Care", "Doctor, pharmacy and medicine support", "medical_services", PluginCategory.SERVICES, isInstalled = true, isEnabled = true, isNew = true),
      AppPlugin("plugin_emergency", "Emergency Response", "SOS dispatch, ambulance and response", "emergency", PluginCategory.SERVICES, isInstalled = true, isEnabled = true, isNew = true, badge = "24/7"),
      AppPlugin("plugin_support_chat", "Support Desk", "Secure message, voice and video help", "support_agent", PluginCategory.COMMUNICATION, isInstalled = true, isEnabled = true, isNew = true)
    )
  )
  val plugins: StateFlow<List<AppPlugin>> = _plugins.asStateFlow()

  // Master Infrastructure Vault Config
  private val _vaultConfig = MutableStateFlow(MasterVaultConfig())
  val vaultConfig: StateFlow<MasterVaultConfig> = _vaultConfig.asStateFlow()

  private val _pluginConfigs = MutableStateFlow(com.example.model.AppPluginConfigs())
  val pluginConfigs: StateFlow<com.example.model.AppPluginConfigs> = _pluginConfigs.asStateFlow()

  // KYC Application Queue for Admin Review
  private val _kycQueue = MutableStateFlow<List<KycApplication>>(
    listOf(
      KycApplication("kyc_01", "Lalremruata Ralte", "+91 9862345678", "Driving License", "MZ-01-2023-009841", "2026-08-22", KYCStatus.VERIFIED, "Approved by Super Admin"),
      KycApplication("kyc_02", "Zonunsanga Hnamte", "+91 94361 88392", "Driving License (LMV/MCWG)", "MZ-02-2024-001290", "2026-08-23", KYCStatus.PENDING_REVIEW, "Pending admin signature"),
      KycApplication("kyc_03", "Malsawmtluangi Varte", "+91 96123 99401", "Driving License", "MZ-01-2025-004381", "2026-08-23", KYCStatus.PENDING_REVIEW, "Photo submitted for motor hire eligibility"),
      KycApplication("kyc_04", "Lalthazuala Chhangte", "+91 98561 02938", "Aadhaar / Voter ID", "AADHAAR-8839-2019-3321", "2026-08-21", KYCStatus.REJECTED, "Unclear image of back side")
    )
  )
  val kycQueue: StateFlow<List<KycApplication>> = _kycQueue.asStateFlow()

  // Fleet Vehicles available for Motor Hire
  private val _fleet = MutableStateFlow<List<FleetVehicle>>(
    listOf(
      FleetVehicle("v1", "Royal Enfield Hunter 350", "Cruiser Bike", 120.0, 950.0, "Manual 5-Speed", "Petrol", "2 Seats", 4.9, 142, tag = "MOST POPULAR"),
      FleetVehicle("v2", "Honda Activa 6G 110cc", "Scooter", 60.0, 480.0, "Automatic CVT", "Petrol", "2 Seats", 4.8, 389, tag = "ECONOMY"),
      FleetVehicle("v3", "Mahindra Thar 4x4 Hard-Top", "Off-Road SUV", 350.0, 2800.0, "Manual 4WD", "Diesel", "4 Seats", 5.0, 98, tag = "ADVENTURE 4X4"),
      FleetVehicle("v4", "Maruti Suzuki Swift VXi", "Hatchback", 180.0, 1450.0, "Manual 5-Speed", "Petrol", "5 Seats", 4.7, 210),
      FleetVehicle("v5", "Yamaha Aerox 155cc", "Maxi Scooter", 90.0, 720.0, "Automatic", "Petrol", "2 Seats", 4.9, 87, tag = "PREMIUM")
    )
  )
  val fleet: StateFlow<List<FleetVehicle>> = _fleet.asStateFlow()

  // Active & Past Bookings
  private val _bookings = MutableStateFlow<List<BookingItem>>(
    listOf(
      BookingItem(
        id = "ZOX-BK-9281",
        type = BookingType.MOTOR_HIRE,
        title = "Royal Enfield Hunter 350 (MZ-01-M-4829)",
        subtitle = "Self-Drive Rental • 4 Hours",
        pickupLocation = "Chanmari Hub, Aizawl",
        dropoffLocation = "Chanmari Hub, Aizawl",
        formattedDate = "Today, 11:00 AM - 03:00 PM",
        fare = 480.0,
        status = BookingStatus.ON_THE_WAY,
        driverOrAgentName = "Lalthanmawia (Fleet Hub Lead)",
        driverPhone = "+91 94361 28910",
        vehicleRegistration = "MZ-01-M-4829",
        otpCode = "4819"
      ),
      BookingItem(
        id = "ZOX-TR-4910",
        type = BookingType.TIRHKAH_ERRAND,
        title = "Tirhkah Express Runner (Documents & Medicine)",
        subtitle = "Urgent Delivery • 6.2 km",
        pickupLocation = "Civil Hospital, Dawrpui",
        dropoffLocation = "Khatla South, Near Taxation Dept",
        formattedDate = "22 Aug 2026, 03:30 PM",
        fare = 125.0,
        status = BookingStatus.COMPLETED,
        driverOrAgentName = "David Lalrinawma (Runner #12)",
        driverPhone = "+91 98628 33910"
      )
    )
  )
  val bookings: StateFlow<List<BookingItem>> = _bookings.asStateFlow()

  // Route Vehicles for Counter Desk
  private val _routeVehicles = MutableStateFlow<List<com.example.model.RouteVehicle>>(
    listOf(
      com.example.model.RouteVehicle(
        "rv_01", "MZ-01-A-1234", "Maxi Cab", "Aizawl - Lunglei", "Remsanga", "+91 9876543210", "Dingtea", "+91 8765432109",
        "2027-01-15", "2027-06-20", "2026-12-31", "2028-11-10", com.example.model.RouteVehicleStatus.ACTIVE
      ),
      com.example.model.RouteVehicle(
        "rv_02", "MZ-02-B-5678", "Bus", "Aizawl - Champhai", "Thanga", "+91 9988776655", "Ruata", "+91 8877665544",
        "2026-08-30", "2026-10-10", "2026-09-05", "2029-05-20", com.example.model.RouteVehicleStatus.EXPIRING_SOON
      ),
      com.example.model.RouteVehicle(
        "rv_03", "MZ-03-C-9012", "Regional Taxi", "Aizawl - Kolasib", "Siami", "+91 7766554433", "Pekhlua", "+91 6655443322",
        "2026-08-10", "2026-11-20", "2026-08-15", "2027-01-05", com.example.model.RouteVehicleStatus.EXPIRED_SUSPENDED
      )
    )
  )
  val routeVehicles: StateFlow<List<com.example.model.RouteVehicle>> = _routeVehicles.asStateFlow()

  // Selected Location (e.g. Lunglei / Aizawl, Mizoram)
  private val _selectedCity = MutableStateFlow("Aizawl, Mizoram")
  val selectedCity: StateFlow<String> = _selectedCity.asStateFlow()

  // Methods to manipulate state

  fun switchUserRole(role: UserRole, phone: String = "") {
    _userProfile.update {
      it.copy(
        role = role,
        phone = if (phone.isNotEmpty()) phone else if (role == UserRole.SUPER_ADMIN) "9378160106" else "9862345678",
        name = if (role == UserRole.SUPER_ADMIN) "Super Admin (Enterprise Node)" else "Lalremruata Ralte"
      )
    }
  }

  fun setCity(city: String) {
    _selectedCity.value = city
    _userProfile.update { it.copy(city = city) }
  }

  fun togglePlugin(pluginId: String, enable: Boolean) {
    _plugins.update { list ->
      list.map { if (it.id == pluginId) it.copy(isEnabled = enable) else it }
    }
  }

  fun installUninstallPlugin(pluginId: String, installed: Boolean) {
    _plugins.update { list ->
      list.map { if (it.id == pluginId) it.copy(isInstalled = installed, isEnabled = installed) else it }
    }
  }

  fun updateVaultConfig(updated: MasterVaultConfig) {
    _vaultConfig.value = updated
    // If admob is disabled in vault, also sync plugin toggle
    if (!updated.admob.isEnabled) {
      togglePlugin("plugin_admob", false)
    }
  }

  fun approveKyc(applicationId: String) {
    var requestedRole: UserRole? = null
    _kycQueue.update { list ->
      list.map { 
        if (it.id == applicationId) {
          requestedRole = it.requestedRole
          it.copy(status = KYCStatus.VERIFIED, remarks = "Approved by Staff on 23 Aug 2026") 
        } else it 
      }
    }
    // Also if it matches current user, update status and role
    _userProfile.update { 
      it.copy(
        kycStatus = KYCStatus.VERIFIED,
        role = requestedRole ?: it.role
      ) 
    }
  }

  fun rejectKyc(applicationId: String, reason: String) {
    _kycQueue.update { list ->
      list.map { if (it.id == applicationId) it.copy(status = KYCStatus.REJECTED, remarks = reason) else it }
    }
  }

  fun updateUserProfile(profile: UserProfile) {
    _userProfile.value = profile
  }

  fun submitKycDocument(docType: String, docNumber: String, requestedRole: UserRole, selfieUrl: String, vehicleDetails: String?) {
    val newApp = KycApplication(
      id = "kyc_${System.currentTimeMillis().toString().takeLast(4)}",
      customerName = _userProfile.value.name,
      phone = "+91 ${_userProfile.value.phone}",
      documentType = docType,
      documentNumber = docNumber,
      submittedDate = "2026-08-23",
      status = KYCStatus.PENDING_STAFF_APPROVAL,
      remarks = "Submitted for Role Upgrade (${requestedRole.name})",
      requestedRole = requestedRole,
      selfiePhotoUrl = selfieUrl,
      vehicleDetails = vehicleDetails
    )
    _kycQueue.update { listOf(newApp) + it }
    _userProfile.update {
      it.copy(
        kycStatus = KYCStatus.PENDING_STAFF_APPROVAL,
        kycDocType = docType,
        kycDocNumber = docNumber
      )
    }
  }

  fun rewardUserCoins(coinsEarned: Int) {
    _wallet.update { it.copy(rewardCoins = it.rewardCoins + coinsEarned) }
    val newTx = TransactionItem(
      id = "tx_${System.currentTimeMillis().toString().takeLast(5)}",
      title = "Rewarded Ad Bonus",
      category = "Monetization",
      type = TransactionType.REWARD,
      amount = coinsEarned.toDouble(),
      date = "Just now",
      status = "Credited (+${coinsEarned} ZOX Coins)",
      paymentMethod = "AdMob Rewards Engine"
    )
    _transactions.update { listOf(newTx) + it }
  }

  fun redeemRewardCoins(points: Int, pointsValueInRupees: Double) {
    val cash = points * pointsValueInRupees
    _wallet.update { it.copy(rewardCoins = it.rewardCoins - points, balanceInRupees = it.balanceInRupees + cash) }
    _transactions.update { listOf(TransactionItem("tx_${System.currentTimeMillis().toString().takeLast(5)}", "Loyalty points redeemed", "Rewards", TransactionType.CREDIT, cash, "Just now", "${points} points converted to wallet cash")) + it }
  }

  fun topUpWallet(amount: Double) {
    _wallet.update { it.copy(balanceInRupees = it.balanceInRupees + amount) }
    val newTx = TransactionItem(
      id = "tx_${System.currentTimeMillis().toString().takeLast(5)}",
      title = "Wallet Top-up",
      category = "Deposit",
      type = TransactionType.CREDIT,
      amount = amount,
      date = "Just now",
      status = "Completed",
      paymentMethod = "Razorpay UPI Gateway"
    )
    _transactions.update { listOf(newTx) + it }
  }

  fun createBooking(
    type: BookingType,
    title: String,
    subtitle: String,
    pickup: String,
    dropoff: String,
    fare: Double
  ): BookingItem {
    val newBooking = BookingItem(
      id = "ZOX-${if (type == BookingType.MOTOR_HIRE) "BK" else "TR"}-${(1000..9999).random()}",
      type = type,
      title = title,
      subtitle = subtitle,
      pickupLocation = pickup,
      dropoffLocation = dropoff,
      formattedDate = "Today, ${(8..20).random()}:30 ${(if ((8..20).random() > 11) "PM" else "AM")}",
      fare = fare,
      status = BookingStatus.ASSIGNED,
      otpCode = "${(1000..9999).random()}"
    )
    _bookings.update { listOf(newBooking) + it }
    // Deduct fare
    _wallet.update { it.copy(balanceInRupees = (it.balanceInRupees - fare).coerceAtLeast(0.0)) }
    _transactions.update {
      listOf(
        TransactionItem(
          id = "tx_${System.currentTimeMillis().toString().takeLast(5)}",
          title = title,
          category = type.name,
          type = TransactionType.DEBIT,
          amount = fare,
          date = "Just now"
        )
      ) + it
    }
    return newBooking
  }

  fun toggleLanguage() {
    _userProfile.update {
      it.copy(language = if (it.language == "English") "Mizo" else "English")
    }
  }

  fun enrollRouteVehicle(vehicle: com.example.model.RouteVehicle) {
    _routeVehicles.update { listOf(vehicle) + it }
  }

  fun runExpiryAudit() {
    _routeVehicles.update { list ->
      list.map { v ->
        if (v.status == com.example.model.RouteVehicleStatus.EXPIRED_SUSPENDED) v
        else if (v.insuranceExpiry < "2026-08-23" || v.fcExpiry < "2026-08-23" || v.permitExpiry < "2026-08-23" || v.licenseExpiry < "2026-08-23") {
           v.copy(status = com.example.model.RouteVehicleStatus.EXPIRED_SUSPENDED)
        } else if (v.insuranceExpiry < "2026-09-23" || v.fcExpiry < "2026-09-23" || v.permitExpiry < "2026-09-23" || v.licenseExpiry < "2026-09-23") {
           v.copy(status = com.example.model.RouteVehicleStatus.EXPIRING_SOON)
        } else {
           v.copy(status = com.example.model.RouteVehicleStatus.ACTIVE)
        }
      }
    }
  }

  fun updatePluginConfigs(configs: com.example.model.AppPluginConfigs) {
    _pluginConfigs.value = configs
  }
}
