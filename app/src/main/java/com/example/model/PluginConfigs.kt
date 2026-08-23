package com.example.model

data class MotorHireConfig(
  val baseDailyRentalRate: Double = 1500.0,
  val securityDeposit: Double = 5000.0,
  val includedFreeKm: Int = 100,
  val extraKmCharge: Double = 12.0,
  val allowSelfPickup: Boolean = true,
  val allowInterDistrict: Boolean = false
)

data class TirhkahConfig(
  val baseDeliveryCharge: Double = 50.0,
  val perKmCharge: Double = 15.0,
  val surgeMultiplier: Float = 1.0f,
  val maxWeightLimit: Double = 20.0,
  val enablePeakHourSurge: Boolean = true,
  val instantAssignedDriver: Boolean = true
)

data class RouteVehiclesConfig(
  val platformConvenienceFee: Double = 20.0,
  val advanceBookingWindowDays: Int = 7,
  val maxSeatsPerTransaction: Int = 4
)

data class RevenueAdmobConfig(
  val merchantCommissionPercent: Float = 5.0f,
  val ownerCommissionPercent: Float = 10.0f,
  val driverCommissionPercent: Float = 8.0f,
  val admobRewardedVideoCoinsRate: Int = 10,
  val dailyAdFrequencyCap: Int = 5
)

data class AppPluginConfigs(
  val motorHireConfig: MotorHireConfig = MotorHireConfig(),
  val tirhkahConfig: TirhkahConfig = TirhkahConfig(),
  val routeVehiclesConfig: RouteVehiclesConfig = RouteVehiclesConfig(),
  val revenueAdmobConfig: RevenueAdmobConfig = RevenueAdmobConfig()
)
