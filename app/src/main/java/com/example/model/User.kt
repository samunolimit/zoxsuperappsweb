package com.example.model

enum class UserRole {
  CUSTOMER,
  SUPER_ADMIN,
  DRIVER,
  TIRHKAH_RUNNER,
  VEHICLE_OWNER,
  COUNTER_STAFF
}

enum class KYCStatus {
  NOT_SUBMITTED,
  PENDING_REVIEW,
  VERIFIED,
  REJECTED,
  PENDING_STAFF_APPROVAL
}

enum class LoyaltyTier(val badgeName: String, val minCoins: Int) {
  BRONZE("Bronze", 0),
  SILVER("Silver", 500),
  GOLD("Gold", 2000),
  DIAMOND("Diamond", 5000)
}

data class SavedAddress(
  val id: String,
  val label: String,
  val addressLine: String,
  val locality: String,
  val isDefault: Boolean = false
)

data class UserProfile(
  val id: String = "ZOX-IN-884920",
  val name: String = "Lalremruata Ralte",
  val phone: String = "9862345678",
  val email: String = "lalremruata@zoxapps.in",
  val role: UserRole = UserRole.CUSTOMER,
  val kycStatus: KYCStatus = KYCStatus.VERIFIED,
  val kycDocType: String = "Driving License",
  val kycDocNumber: String = "MZ-01-2023-009841",
  val city: String = "Aizawl, Mizoram",
  val language: String = "English", // "English" or "Mizo"
  val emergencyContact: String = "",
  val savedAddresses: List<SavedAddress> = listOf(
    SavedAddress("addr_1", "Home", "House #42, Upper Khatla, Near Secretariat", "Aizawl, Mizoram", true),
    SavedAddress("addr_2", "Office", "Millennium Centre, 3rd Floor, Dawrpui", "Aizawl, Mizoram", false),
    SavedAddress("addr_3", "Branch Office", "Venglai Main Road, Near DC Office", "Lunglei, Mizoram", false)
  )
)

data class WalletData(
  val balanceInRupees: Double = 1450.00,
  val rewardCoins: Int = 380,
  val isAutoTopupEnabled: Boolean = false
) {
  val loyaltyTier: LoyaltyTier
    get() = LoyaltyTier.values().sortedByDescending { it.minCoins }.firstOrNull { rewardCoins >= it.minCoins } ?: LoyaltyTier.BRONZE
}

enum class TransactionType {
  CREDIT,
  DEBIT,
  REWARD
}

data class TransactionItem(
  val id: String,
  val title: String,
  val category: String,
  val type: TransactionType,
  val amount: Double,
  val date: String,
  val status: String = "Completed",
  val paymentMethod: String = "ZOX Pay Wallet"
)
