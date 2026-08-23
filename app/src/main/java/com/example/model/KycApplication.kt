package com.example.model

data class KycApplication(
  val id: String,
  val customerName: String,
  val phone: String,
  val documentType: String,
  val documentNumber: String,
  val submittedDate: String,
  val status: KYCStatus,
  val remarks: String = "",
  val vehicleCategory: String = "Two-Wheeler & Four-Wheeler (LMV)",
  val expiryDate: String = "2032-11-20",
  val requestedRole: UserRole = UserRole.CUSTOMER,
  val selfiePhotoUrl: String = "mock_selfie_url",
  val vehicleDetails: String? = null
)
