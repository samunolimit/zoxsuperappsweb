package com.example.model

enum class BookingType {
  MOTOR_HIRE,
  TIRHKAH_ERRAND,
  TAXI_RIDE,
  FOOD_DELIVERY
}

enum class BookingStatus {
  PENDING_DISPATCH,
  ASSIGNED,
  ON_THE_WAY,
  COMPLETED,
  CANCELLED
}

data class BookingItem(
  val id: String,
  val type: BookingType,
  val title: String,
  val subtitle: String,
  val pickupLocation: String,
  val dropoffLocation: String,
  val formattedDate: String,
  val fare: Double,
  val status: BookingStatus,
  val driverOrAgentName: String = "Lalthanmawia (ZOX Partner)",
  val driverPhone: String = "+91 94361 28910",
  val vehicleRegistration: String = "MZ-01-M-4829",
  val otpCode: String = "4819"
)

data class FleetVehicle(
  val id: String,
  val name: String,
  val category: String, // Scooter, Cruiser, Hatchback, 4x4 SUV
  val hourlyRate: Double,
  val dailyRate: Double,
  val transmission: String,
  val fuelType: String,
  val seats: String,
  val rating: Double,
  val totalTrips: Int,
  val isAvailable: Boolean = true,
  val tag: String? = null
)
