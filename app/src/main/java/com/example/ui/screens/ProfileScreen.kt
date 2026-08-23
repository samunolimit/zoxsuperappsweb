package com.example.ui.screens

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
import androidx.compose.material.icons.filled.AccountBalanceWallet
import androidx.compose.material.icons.filled.AdminPanelSettings
import androidx.compose.material.icons.filled.Build
import androidx.compose.material.icons.filled.CreditCard
import androidx.compose.material.icons.filled.DirectionsCar
import androidx.compose.material.icons.filled.Emergency
import androidx.compose.material.icons.filled.FileUpload
import androidx.compose.material.icons.filled.HelpOutline
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Language
import androidx.compose.material.icons.filled.Logout
import androidx.compose.material.icons.filled.Phone
import androidx.compose.material.icons.filled.Security
import androidx.compose.material.icons.filled.Verified
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material.icons.filled.WifiTethering
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.Surface
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import com.example.model.KYCStatus
import com.example.model.UserProfile
import com.example.model.UserRole
import com.example.model.WalletData
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
fun ProfileScreen(
  userProfile: UserProfile,
  walletData: WalletData,
  activeBookingsCount: Int,
  onUploadKycClick: () -> Unit,
  onLanguageToggle: () -> Unit,
  onSwitchRoleClick: (UserRole) -> Unit,
  onLogoutClick: () -> Unit,
  onOpenWebPortal: () -> Unit = {}
) {
  var showSosDialog by remember { mutableStateOf(false) }

  LazyColumn(
    modifier = Modifier
      .fillMaxSize()
      .background(ZoxDarkBackground)
      .testTag("profile_screen"),
    contentPadding = PaddingValues(top = 16.dp, bottom = 90.dp, start = 16.dp, end = 16.dp),
    verticalArrangement = Arrangement.spacedBy(16.dp)
  ) {
    // 1. User Identification & Avatar Header
    item {
      Card(
        modifier = Modifier
          .fillMaxWidth()
          .testTag("user_profile_header_card"),
        shape = RoundedCornerShape(22.dp),
        colors = CardDefaults.cardColors(containerColor = ZoxDarkCard),
        border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFF383556))
      ) {
        Column(
          modifier = Modifier
            .fillMaxWidth()
            .padding(18.dp),
          verticalArrangement = Arrangement.spacedBy(14.dp)
        ) {
          Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(14.dp)
          ) {
            Box(
              modifier = Modifier
                .size(62.dp)
                .clip(CircleShape)
                .background(Brush.linearGradient(listOf(ZoxPurplePrimary, ZoxOrangeAccent))),
              contentAlignment = Alignment.Center
            ) {
              Text(
                text = userProfile.name.split(" ").mapNotNull { it.firstOrNull()?.toString() }.take(2).joinToString(""),
                fontSize = 22.sp,
                fontWeight = FontWeight.Black,
                color = Color.White
              )
            }

            Column(modifier = Modifier.weight(1f)) {
              Text(
                text = userProfile.name,
                fontSize = 18.sp,
                fontWeight = FontWeight.Bold,
                color = Color.White
              )
              Text(
                text = "+91 ${userProfile.phone}",
                fontSize = 13.sp,
                color = Color(0xFFA0A0BA)
              )
              Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                Text(
                  text = "ID: ${userProfile.id}",
                  fontSize = 11.sp,
                  fontWeight = FontWeight.SemiBold,
                  color = ZoxOrangeAccent
                )
                Box(
                  modifier = Modifier
                    .clip(RoundedCornerShape(4.dp))
                    .background(
                      when(walletData.loyaltyTier.name) {
                        "BRONZE" -> Color(0xFFCD7F32)
                        "SILVER" -> Color(0xFFC0C0C0)
                        "GOLD" -> Color(0xFFFFD700)
                        "DIAMOND" -> Color(0xFFb9f2ff)
                        else -> ZoxPurplePrimary
                      }
                    )
                    .padding(horizontal = 6.dp, vertical = 2.dp)
                ) {
                  Text(
                    text = walletData.loyaltyTier.badgeName,
                    fontSize = 10.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color.Black
                  )
                }
              }
            }
          }

          // Dynamic KYC Verification Badge
          Row(
            modifier = Modifier
              .fillMaxWidth()
              .clip(RoundedCornerShape(12.dp))
              .background(
                when (userProfile.kycStatus) {
                  KYCStatus.VERIFIED -> Color(0xFF143022)
                  KYCStatus.PENDING_REVIEW, KYCStatus.PENDING_STAFF_APPROVAL -> Color(0xFF352B16)
                  KYCStatus.NOT_SUBMITTED, KYCStatus.REJECTED -> Color(0xFF301824)
                }
              )
              .border(
                1.dp,
                when (userProfile.kycStatus) {
                  KYCStatus.VERIFIED -> ZoxSuccess.copy(alpha = 0.5f)
                  KYCStatus.PENDING_REVIEW, KYCStatus.PENDING_STAFF_APPROVAL -> ZoxWarning.copy(alpha = 0.5f)
                  KYCStatus.NOT_SUBMITTED, KYCStatus.REJECTED -> ZoxError.copy(alpha = 0.5f)
                },
                RoundedCornerShape(12.dp)
              )
              .padding(12.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
          ) {
            Row(
              verticalAlignment = Alignment.CenterVertically,
              horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
              Icon(
                imageVector = when (userProfile.kycStatus) {
                  KYCStatus.VERIFIED -> Icons.Default.Verified
                  KYCStatus.PENDING_REVIEW, KYCStatus.PENDING_STAFF_APPROVAL -> Icons.Default.Warning
                  KYCStatus.NOT_SUBMITTED, KYCStatus.REJECTED -> Icons.Default.FileUpload
                },
                contentDescription = null,
                tint = when (userProfile.kycStatus) {
                  KYCStatus.VERIFIED -> ZoxSuccess
                  KYCStatus.PENDING_REVIEW, KYCStatus.PENDING_STAFF_APPROVAL -> ZoxWarning
                  KYCStatus.NOT_SUBMITTED, KYCStatus.REJECTED -> ZoxOrangeAccent
                },
                modifier = Modifier.size(22.dp)
              )
              Column {
                Text(
                  text = when (userProfile.kycStatus) {
                    KYCStatus.VERIFIED -> "Role Verified (${userProfile.role.name.replace("_", " ")})"
                    KYCStatus.PENDING_REVIEW, KYCStatus.PENDING_STAFF_APPROVAL -> "Role Upgrade Pending"
                    KYCStatus.NOT_SUBMITTED -> "Upgrade / Request Role Change"
                    KYCStatus.REJECTED -> "Role Upgrade Rejected"
                  },
                  fontSize = 12.sp,
                  fontWeight = FontWeight.Bold,
                  color = Color.White
                )
                Text(
                  text = if (userProfile.kycStatus == KYCStatus.VERIFIED) "DL: ${userProfile.kycDocNumber}" else "Requires Live Selfie & Govt ID",
                  fontSize = 10.sp,
                  color = Color(0xFFA0A0BA)
                )
              }
            }

            if (userProfile.kycStatus != KYCStatus.VERIFIED && userProfile.kycStatus != KYCStatus.PENDING_STAFF_APPROVAL) {
              Button(
                onClick = onUploadKycClick,
                colors = ButtonDefaults.buttonColors(
                  containerColor = ZoxOrangeAccent,
                  contentColor = Color.Black
                ),
                shape = RoundedCornerShape(8.dp),
                contentPadding = PaddingValues(horizontal = 8.dp, vertical = 2.dp),
                modifier = Modifier.height(28.dp)
              ) {
                Text("UPLOAD", fontSize = 10.sp, fontWeight = FontWeight.Black)
              }
            }
          }
        }
      }
    }

    // 2. Financial Summary Quick Grid
    item {
      Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(10.dp)
      ) {
        Card(
          modifier = Modifier.weight(1f),
          shape = RoundedCornerShape(16.dp),
          colors = CardDefaults.cardColors(containerColor = ZoxDarkCard),
          border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFF33314A))
        ) {
          Column(
            modifier = Modifier.padding(14.dp),
            verticalArrangement = Arrangement.spacedBy(4.dp)
          ) {
            Text(text = "Wallet Balance", fontSize = 11.sp, color = Color(0xFFA0A0BA))
            Text(text = "₹${walletData.balanceInRupees.toInt()}", fontSize = 18.sp, fontWeight = FontWeight.Black, color = Color.White)
          }
        }

        Card(
          modifier = Modifier.weight(1f),
          shape = RoundedCornerShape(16.dp),
          colors = CardDefaults.cardColors(containerColor = ZoxDarkCard),
          border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFF33314A))
        ) {
          Column(
            modifier = Modifier.padding(14.dp),
            verticalArrangement = Arrangement.spacedBy(4.dp)
          ) {
            Text(text = "Reward Coins", fontSize = 11.sp, color = Color(0xFFA0A0BA))
            Text(text = "🪙 ${walletData.rewardCoins}", fontSize = 18.sp, fontWeight = FontWeight.Black, color = ZoxOrangeLight)
          }
        }

        Card(
          modifier = Modifier.weight(1f),
          shape = RoundedCornerShape(16.dp),
          colors = CardDefaults.cardColors(containerColor = ZoxDarkCard),
          border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFF33314A))
        ) {
          Column(
            modifier = Modifier.padding(14.dp),
            verticalArrangement = Arrangement.spacedBy(4.dp)
          ) {
            Text(text = "Active Rides", fontSize = 11.sp, color = Color(0xFFA0A0BA))
            Text(text = "$activeBookingsCount", fontSize = 18.sp, fontWeight = FontWeight.Black, color = ZoxPurplePrimary)
          }
        }
      }
    }

    // 3. Saved Addresses
    item {
      Text(
        text = "Saved Addresses (Mizoram)",
        fontSize = 15.sp,
        fontWeight = FontWeight.Bold,
        color = Color.White
      )
    }

    items(userProfile.savedAddresses) { addr ->
      Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(14.dp),
        colors = CardDefaults.cardColors(containerColor = ZoxDarkCard),
        border = androidx.compose.foundation.BorderStroke(0.8.dp, Color(0xFF33314A))
      ) {
        Row(
          modifier = Modifier
            .fillMaxWidth()
            .padding(12.dp),
          verticalAlignment = Alignment.CenterVertically,
          horizontalArrangement = Arrangement.spacedBy(10.dp)
        ) {
          Icon(Icons.Default.Home, contentDescription = null, tint = ZoxOrangeAccent, modifier = Modifier.size(20.dp))
          Column {
            Text(text = addr.label, fontSize = 13.sp, fontWeight = FontWeight.Bold, color = Color.White)
            Text(text = "${addr.addressLine}, ${addr.locality}", fontSize = 11.sp, color = Color(0xFFA0A0BA))
          }
        }
      }
    }

    // 4. App Preferences & Language Switch (English / Mizo)
    item {
      Text(
        text = "App Settings & Localization",
        fontSize = 15.sp,
        fontWeight = FontWeight.Bold,
        color = Color.White
      )
    }

    item {
      Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = ZoxDarkCard),
        border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFF33314A))
      ) {
        Column(modifier = Modifier.padding(14.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
          // Language Switcher
          Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
          ) {
            Row(
              verticalAlignment = Alignment.CenterVertically,
              horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
              Icon(Icons.Default.Language, contentDescription = null, tint = ZoxOrangeAccent)
              Column {
                Text(text = "App Language (Mizo / English)", fontSize = 13.sp, fontWeight = FontWeight.Bold, color = Color.White)
                Text(text = "Current: ${userProfile.language}", fontSize = 11.sp, color = Color(0xFFA0A0BA))
              }
            }

            Button(
              onClick = onLanguageToggle,
              colors = ButtonDefaults.buttonColors(containerColor = ZoxPurpleContainer, contentColor = ZoxOrangeAccent),
              shape = RoundedCornerShape(8.dp)
            ) {
              Text(text = if (userProfile.language == "English") "SWITCH TO MIZO" else "SWITCH TO ENGLISH", fontSize = 10.sp, fontWeight = FontWeight.Bold)
            }
          }

          Box(modifier = Modifier.fillMaxWidth().height(1.dp).background(Color(0xFF2E2C44)))

          // Web Cloud Portal Row
          Row(
            modifier = Modifier
              .fillMaxWidth()
              .clip(RoundedCornerShape(10.dp))
              .background(Color(0xFF231C38))
              .clickable { onOpenWebPortal() }
              .padding(12.dp)
              .testTag("profile_web_portal_item"),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
          ) {
            Row(
              verticalAlignment = Alignment.CenterVertically,
              horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
              Icon(Icons.Default.WifiTethering, contentDescription = null, tint = ZoxOrangeAccent)
              Column {
                Text(text = "ZOX Web Interface & Cloud Portal", fontSize = 13.sp, fontWeight = FontWeight.Bold, color = Color.White)
                Text(text = "Launch browser console (web.zoxapps.mizoram.in)", fontSize = 10.sp, color = Color(0xFFA5A5BC))
              }
            }
            Box(
              modifier = Modifier
                .clip(RoundedCornerShape(6.dp))
                .background(ZoxPurplePrimary)
                .padding(horizontal = 8.dp, vertical = 4.dp)
            ) {
              Text(text = "LAUNCH", fontSize = 9.sp, fontWeight = FontWeight.Bold, color = Color.White)
            }
          }

          Box(modifier = Modifier.fillMaxWidth().height(1.dp).background(Color(0xFF2E2C44)))

          // SOS Emergency Support Button
          Row(
            modifier = Modifier
              .fillMaxWidth()
              .clip(RoundedCornerShape(10.dp))
              .background(Color(0xFF3B1A1E))
              .clickable { showSosDialog = true }
              .padding(12.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
          ) {
            Row(
              verticalAlignment = Alignment.CenterVertically,
              horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
              Icon(Icons.Default.Emergency, contentDescription = null, tint = ZoxError)
              Column {
                Text(text = "24/7 SOS Emergency Support", fontSize = 13.sp, fontWeight = FontWeight.Bold, color = Color.White)
                Text(text = "Instant Mizoram Police (112) & ZOX Dispatch Help", fontSize = 10.sp, color = Color(0xFFE89EA8))
              }
            }
            Icon(Icons.Default.Phone, contentDescription = null, tint = ZoxError)
          }
        }
      }
    }

    // 5. Logout
    item {
      Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.End
      ) {
        Button(
          onClick = onLogoutClick,
          modifier = Modifier.fillMaxWidth().height(46.dp),
          colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF28263C), contentColor = ZoxError),
          shape = RoundedCornerShape(12.dp)
        ) {
          Icon(Icons.Default.Logout, contentDescription = null, modifier = Modifier.size(16.dp))
          Spacer(modifier = Modifier.width(4.dp))
          Text("LOGOUT", fontSize = 11.sp, fontWeight = FontWeight.Bold)
        }
      }
    }
  }

  // SOS Emergency Dialog
  if (showSosDialog) {
    Dialog(onDismissRequest = { showSosDialog = false }) {
      Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(containerColor = ZoxDarkSurface),
        border = androidx.compose.foundation.BorderStroke(1.dp, ZoxError)
      ) {
        Column(
          modifier = Modifier.padding(20.dp),
          horizontalAlignment = Alignment.CenterHorizontally,
          verticalArrangement = Arrangement.spacedBy(14.dp)
        ) {
          Box(
            modifier = Modifier.size(54.dp).clip(CircleShape).background(ZoxError.copy(alpha = 0.2f)),
            contentAlignment = Alignment.Center
          ) {
            Icon(Icons.Default.Emergency, contentDescription = null, tint = ZoxError, modifier = Modifier.size(32.dp))
          }

          Text(
            text = "ZOX Emergency SOS Alert",
            fontSize = 18.sp,
            fontWeight = FontWeight.Bold,
            color = Color.White
          )

          Text(
            text = "Broadcasting your live GPS coordinates (Chanmari, Aizawl) to Mizoram Highway Patrol and ZOX Rapid Response Unit.",
            fontSize = 12.sp,
            color = Color(0xFFA0A0BA),
            textAlign = androidx.compose.ui.text.style.TextAlign.Center
          )

          Button(
            onClick = { showSosDialog = false },
            modifier = Modifier.fillMaxWidth().height(46.dp),
            colors = ButtonDefaults.buttonColors(containerColor = ZoxError, contentColor = Color.White),
            shape = RoundedCornerShape(10.dp)
          ) {
            Text("CALL MIZORAM POLICE (112)", fontWeight = FontWeight.Black)
          }

          Button(
            onClick = { showSosDialog = false },
            modifier = Modifier.fillMaxWidth(),
            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF2A293E), contentColor = Color.White),
            shape = RoundedCornerShape(10.dp)
          ) {
            Text("Cancel Alert")
          }
        }
      }
    }
  }
}
