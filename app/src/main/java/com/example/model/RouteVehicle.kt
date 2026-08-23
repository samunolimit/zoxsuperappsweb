package com.example.model

enum class RouteVehicleStatus {
  ACTIVE,
  EXPIRING_SOON,
  EXPIRED_SUSPENDED
}

data class RouteVehicle(
  val id: String,
  val regNumber: String,
  val type: String, // Maxi Cab, Bus, Regional Taxi
  val routeName: String,
  val ownerName: String,
  val ownerPhone: String,
  val driverName: String,
  val driverPhone: String,
  val insuranceExpiry: String, // Format: YYYY-MM-DD
  val fcExpiry: String,
  val permitExpiry: String,
  val licenseExpiry: String,
  val status: RouteVehicleStatus = RouteVehicleStatus.ACTIVE
)
