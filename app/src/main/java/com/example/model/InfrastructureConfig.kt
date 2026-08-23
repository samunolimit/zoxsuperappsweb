package com.example.model

data class AdMobConfig(
  val isEnabled: Boolean = true,
  val appId: String = "ca-app-pub-3940256099942544~3347511713",
  val bannerUnitId: String = "ca-app-pub-3940256099942544/6300978111",
  val interstitialUnitId: String = "ca-app-pub-3940256099942544/1033173712",
  val rewardedUnitId: String = "ca-app-pub-3940256099942544/5224354917",
  val nativeUnitId: String = "ca-app-pub-3940256099942544/2247696110",
  val rewardedCoinRate: Int = 50,
  val interstitialFrequencyMinutes: Int = 3
)

data class ApiEndpointConfig(
  val baseUrl: String = "https://api.zoxsuperapp.internal/v2",
  val webSocketsUrl: String = "wss://realtime.zoxsuperapp.internal/socket",
  val timeoutSeconds: Int = 30,
  val apiVersion: String = "v2.4-enterprise"
)

data class DatabaseConfig(
  val provider: String = "PostgreSQL / Supabase",
  val hostOrUrl: String = "https://db.zoxsuperapp.supabase.co",
  val serviceRoleKey: String = "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.zox_vault_master_key_secure_prod",
  val poolSize: Int = 20,
  val sslMode: String = "require"
)

data class StorageConfig(
  val provider: String = "AWS S3 / Cloudflare R2",
  val bucketName: String = "zox-media-production-vault",
  val region: String = "ap-south-1 (Mumbai)",
  val accessKey: String = "AKIA_ZOX_ENTERPRISE_VAULT_PROD",
  val secretKey: String = "••••••••••••••••••••••••••••••••"
)

data class AgoraRtcConfig(
  val appId: String = "a1b2c3d4e5f67890zox_agora_live_rtc_app_id",
  val appCertificate: String = "9876543210fedcba_cert_zox_super_prod",
  val tokenServerUrl: String = "https://rtc-token.zoxsuperapp.internal/v1/token",
  val defaultChannel: String = "ZOX_GLOBAL_SUPPORT_ROOM",
  val e2eEncryptionEnabled: Boolean = true
)

data class GoogleMapsConfig(
  val androidApiKey: String = "",
  val iosApiKey: String = "",
  val placesDailyQuota: Int = 100000,
  val geocodingEnabled: Boolean = true,
  val isEnabled: Boolean = true
)

data class PaymentGatewayConfig(
  val razorpayKeyId: String = "rzp_live_ZOXSuperApps937816",
  val razorpayKeySecret: String = "••••••••••••••••••••••••••••••••",
  val merchantUpiVpa: String = "zoxsuperapp@okhdfcbank",
  val stripePublishableKey: String = "pk_live_51ZOX_Enterprises_Production",
  val isCodEnabled: Boolean = true
)

data class SmsProviderConfig(
  val provider: String = "Fast2SMS / Twilio Enterprise",
  val accountSid: String = "AC_ZOX_ENTERPRISE_SMS_GW_937816",
  val authToken: String = "••••••••••••••••••••••••••••••••",
  val senderId: String = "ZOXOTP",
  val defaultOtpTemplate: String = "Your ZOX Super App login OTP is {#var#}. Valid for 5 minutes."
)

data class TariffEngineConfig(
  val motorHireBaseHourlyRate: Double = 99.0,
  val tirhkahBaseRate: Double = 49.0,
  val tirhkahPerKmRate: Double = 12.0,
  val taxiBaseFare: Double = 80.0,
  val taxiPerKmFare: Double = 18.0,
  val platformFeePercent: Double = 5.0,
  val driverCommissionPercent: Double = 85.0
)

data class SystemMaintenanceConfig(
  val isMaintenanceMode: Boolean = false,
  val maintenanceReason: String = "Scheduled server vault optimization. Services will resume shortly.",
  val minAppVersion: String = "2.4.0",
  val currentAppVersion: String = "2.4.5",
  val isForceUpdateActive: Boolean = false
)

data class CompanyBankingConfig(
  val legalName: String = "ZOX Enterprises Pvt Ltd",
  val bankAccountNumber: String = "00000000000000",
  val ifscCode: String = "HDFC0000000",
  val upiVpaId: String = "zox.settlement@hdfc"
)

data class MasterVaultConfig(
  val admob: AdMobConfig = AdMobConfig(),
  val api: ApiEndpointConfig = ApiEndpointConfig(),
  val database: DatabaseConfig = DatabaseConfig(),
  val storage: StorageConfig = StorageConfig(),
  val agora: AgoraRtcConfig = AgoraRtcConfig(),
  val maps: GoogleMapsConfig = GoogleMapsConfig(),
  val payments: PaymentGatewayConfig = PaymentGatewayConfig(),
  val sms: SmsProviderConfig = SmsProviderConfig(),
  val tariff: TariffEngineConfig = TariffEngineConfig(),
  val system: SystemMaintenanceConfig = SystemMaintenanceConfig(),
  val banking: CompanyBankingConfig = CompanyBankingConfig()
)
