package com.example.ui.screens

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
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
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Build
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.CloudSync
import androidx.compose.material.icons.filled.Engineering
import androidx.compose.material.icons.filled.Key
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Phone
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.Security
import androidx.compose.material.icons.filled.Storage
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material.icons.filled.WifiTethering
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import com.example.model.SystemMaintenanceConfig
import com.example.ui.components.ZoxBrandHeader
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
import kotlinx.coroutines.delay

@Composable
fun MaintenanceModeScreen(
  maintenanceConfig: SystemMaintenanceConfig,
  onAdminBypassSuccess: () -> Unit,
  onOpenWebPortal: () -> Unit,
  onEmergencyCall: (String) -> Unit
) {
  var showBypassDialog by remember { mutableStateOf(false) }
  var isCheckingPing by remember { mutableStateOf(false) }
  var pingStatusText by remember { mutableStateOf<String?>(null) }
  var pingLatencyMs by remember { mutableIntStateOf(34) }

  // Animated pulse transition for server node radar
  val infiniteTransition = rememberInfiniteTransition(label = "pulse")
  val rotationAngle by infiniteTransition.animateFloat(
    initialValue = 0f,
    targetValue = 360f,
    animationSpec = infiniteRepeatable(
      animation = tween( durationMillis = 4000, easing = LinearEasing),
      repeatMode = RepeatMode.Restart
    ),
    label = "radarRotation"
  )

  // Countdown timer simulation
  var countdownSeconds by remember { mutableIntStateOf(1845) }
  LaunchedEffect(Unit) {
    while (countdownSeconds > 0) {
      delay(1000)
      countdownSeconds--
    }
  }

  // Ping check simulation effect
  LaunchedEffect(isCheckingPing) {
    if (isCheckingPing) {
      delay(1200)
      isCheckingPing = false
      pingLatencyMs = (28..45).random()
      pingStatusText = "Server Nodes responding (Latency: ${pingLatencyMs}ms). Maintenance lock active."
    }
  }

  val formattedTime = remember(countdownSeconds) {
    val hrs = countdownSeconds / 3600
    val mins = (countdownSeconds % 3600) / 60
    val secs = countdownSeconds % 60
    String.format("%02d:%02d:%02d", hrs, mins, secs)
  }

  Box(
    modifier = Modifier
      .fillMaxSize()
      .background(ZoxDarkBackground)
      .statusBarsPadding()
      .navigationBarsPadding()
      .testTag("maintenance_mode_screen"),
    contentAlignment = Alignment.TopCenter
  ) {
    LazyColumn(
      modifier = Modifier
        .fillMaxWidth()
        .widthIn(max = 750.dp)
        .padding(horizontal = 20.dp, vertical = 16.dp),
      horizontalAlignment = Alignment.CenterHorizontally,
      verticalArrangement = Arrangement.spacedBy(18.dp)
    ) {
      // 1. Top Header Bar
      item {
        Row(
          modifier = Modifier
            .fillMaxWidth()
            .padding(bottom = 8.dp),
          horizontalArrangement = Arrangement.SpaceBetween,
          verticalAlignment = Alignment.CenterVertically
        ) {
          ZoxBrandHeader(logoSize = 34.dp)

          // Admin Bypass Trigger Pill
          Row(
            modifier = Modifier
              .clip(RoundedCornerShape(20.dp))
              .background(Color(0xFF2B1C40))
              .border(0.8.dp, ZoxOrangeAccent.copy(alpha = 0.6f), RoundedCornerShape(20.dp))
              .clickable { showBypassDialog = true }
              .padding(horizontal = 12.dp, vertical = 6.dp)
              .testTag("admin_bypass_button"),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(6.dp)
          ) {
            Icon(
              imageVector = Icons.Default.Key,
              contentDescription = "Admin Bypass",
              tint = ZoxOrangeAccent,
              modifier = Modifier.size(14.dp)
            )
            Text(
              text = "Vault Bypass",
              color = ZoxOrangeAccent,
              fontSize = 11.sp,
              fontWeight = FontWeight.Bold
            )
          }
        }
      }

      // 2. Central Maintenance Radar Visual Card
      item {
        Card(
          modifier = Modifier.fillMaxWidth(),
          shape = RoundedCornerShape(24.dp),
          colors = CardDefaults.cardColors(containerColor = ZoxDarkSurface),
          border = CardDefaults.outlinedCardBorder().copy(brush = Brush.verticalGradient(listOf(ZoxOrangeAccent, Color(0xFF332048))))
        ) {
          Column(
            modifier = Modifier
              .fillMaxWidth()
              .padding(24.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(16.dp)
          ) {
            // Animated Radar Circle
            Box(
              modifier = Modifier.size(110.dp),
              contentAlignment = Alignment.Center
            ) {
              Box(
                modifier = Modifier
                  .size(105.dp)
                  .clip(CircleShape)
                  .background(Color(0xFF26183C))
                  .border(2.dp, Color(0xFF4A2B78), CircleShape)
              )

              Icon(
                imageVector = Icons.Default.CloudSync,
                contentDescription = null,
                tint = ZoxOrangeAccent.copy(alpha = 0.35f),
                modifier = Modifier
                  .size(80.dp)
                  .rotate(rotationAngle)
              )

              Box(
                modifier = Modifier
                  .size(56.dp)
                  .clip(CircleShape)
                  .background(Brush.radialGradient(listOf(ZoxOrangeAccent, Color(0xFFD9480F))))
                  .border(2.dp, Color.White, CircleShape),
                contentAlignment = Alignment.Center
              ) {
                Icon(
                  imageVector = Icons.Default.Engineering,
                  contentDescription = "Engineering Maintenance",
                  tint = Color.White,
                  modifier = Modifier.size(30.dp)
                )
              }
            }

            // Title & Status Badge
            Row(
              modifier = Modifier
                .clip(RoundedCornerShape(16.dp))
                .background(ZoxWarning.copy(alpha = 0.18f))
                .border(1.dp, ZoxWarning, RoundedCornerShape(16.dp))
                .padding(horizontal = 12.dp, vertical = 5.dp),
              verticalAlignment = Alignment.CenterVertically,
              horizontalArrangement = Arrangement.spacedBy(6.dp)
            ) {
              Box(
                modifier = Modifier
                  .size(8.dp)
                  .clip(CircleShape)
                  .background(ZoxWarning)
              )
              Text(
                text = "SCHEDULED VAULT MAINTENANCE",
                fontSize = 10.sp,
                fontWeight = FontWeight.Bold,
                color = ZoxWarning,
                letterSpacing = 1.sp
              )
            }

            Text(
              text = "ZOX System Upgrades in Progress",
              fontSize = 20.sp,
              fontWeight = FontWeight.Black,
              color = Color.White,
              textAlign = TextAlign.Center
            )

            Text(
              text = maintenanceConfig.maintenanceReason,
              fontSize = 13.sp,
              color = Color(0xFFCCCCCC),
              textAlign = TextAlign.Center,
              lineHeight = 19.sp
            )

            // Countdown Timer Container
            Card(
              modifier = Modifier.fillMaxWidth(),
              shape = RoundedCornerShape(14.dp),
              colors = CardDefaults.cardColors(containerColor = Color(0xFF191726)),
              border = CardDefaults.outlinedCardBorder().copy(brush = Brush.horizontalGradient(listOf(Color(0xFF3B2E58), Color(0xFF292440))))
            ) {
              Row(
                modifier = Modifier
                  .fillMaxWidth()
                  .padding(horizontal = 16.dp, vertical = 12.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
              ) {
                Column {
                  Text(
                    text = "ESTIMATED RESTORATION ETA",
                    fontSize = 10.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color(0xFFA5A5BC),
                    letterSpacing = 0.8.sp
                  )
                  Text(
                    text = "Mizoram Cloud Primary Node Sync",
                    fontSize = 11.sp,
                    color = Color(0xFF7A7A92)
                  )
                }

                Text(
                  text = formattedTime,
                  fontSize = 20.sp,
                  fontWeight = FontWeight.Black,
                  color = ZoxOrangeAccent,
                  letterSpacing = 1.sp
                )
              }
            }
          }
        }
      }

      // 3. Cluster Node Health Matrix
      item {
        Card(
          modifier = Modifier.fillMaxWidth(),
          shape = RoundedCornerShape(18.dp),
          colors = CardDefaults.cardColors(containerColor = ZoxDarkCard)
        ) {
          Column(
            modifier = Modifier
              .fillMaxWidth()
              .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
          ) {
            Row(
              modifier = Modifier.fillMaxWidth(),
              horizontalArrangement = Arrangement.SpaceBetween,
              verticalAlignment = Alignment.CenterVertically
            ) {
              Text(
                text = "Cluster Infrastructure Status",
                fontSize = 14.sp,
                fontWeight = FontWeight.Bold,
                color = Color.White
              )

              Text(
                text = "Engine v${maintenanceConfig.currentAppVersion}",
                fontSize = 11.sp,
                color = ZoxPurplePrimary,
                fontWeight = FontWeight.SemiBold
              )
            }

            NodeStatusRow("PostgreSQL DB Master (Supabase ap-south-1)", "DATABASE MIGRATION", ZoxWarning)
            NodeStatusRow("Agora RTC Global Audio/Video Channel", "UPDATING CERTS", ZoxOrangeAccent)
            NodeStatusRow("Razorpay / UPI Payment Webhook Gateway", "DRAINED (SAFE)", Color(0xFF8E8EA8))
            NodeStatusRow("Fast2SMS / Twilio Telephony Nodes", "ONLINE / QUEUED", ZoxSuccess)
            NodeStatusRow("Mizoram Rider Dispatch & Geocoding", "MAINTENANCE LOCK", ZoxWarning)
          }
        }
      }

      // 4. Interactive Diagnostics & Web Portal Actions
      item {
        Row(
          modifier = Modifier.fillMaxWidth(),
          horizontalArrangement = Arrangement.spacedBy(10.dp)
        ) {
          // Re-check Ping Button
          Button(
            onClick = {
              isCheckingPing = true
              pingStatusText = null
            },
            modifier = Modifier
              .weight(1f)
              .height(48.dp)
              .testTag("ping_status_button"),
            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF26203B)),
            shape = RoundedCornerShape(12.dp),
            border = ButtonDefaults.outlinedButtonBorder.copy(brush = Brush.horizontalGradient(listOf(ZoxPurplePrimary, Color(0xFF432A75))))
          ) {
            if (isCheckingPing) {
              CircularProgressIndicator(
                modifier = Modifier.size(18.dp),
                color = ZoxOrangeAccent,
                strokeWidth = 2.dp
              )
            } else {
              Icon(
                imageVector = Icons.Default.Refresh,
                contentDescription = null,
                tint = Color.White,
                modifier = Modifier.size(16.dp)
              )
              Spacer(modifier = Modifier.width(6.dp))
              Text("Ping Server", fontSize = 12.sp, fontWeight = FontWeight.Bold, color = Color.White)
            }
          }

          // Launch Cloud Web Portal Button
          Button(
            onClick = onOpenWebPortal,
            modifier = Modifier
              .weight(1f)
              .height(48.dp)
              .testTag("open_web_portal_button"),
            colors = ButtonDefaults.buttonColors(containerColor = ZoxPurplePrimary),
            shape = RoundedCornerShape(12.dp)
          ) {
            Icon(
              imageVector = Icons.Default.WifiTethering,
              contentDescription = null,
              tint = Color.White,
              modifier = Modifier.size(16.dp)
            )
            Spacer(modifier = Modifier.width(6.dp))
            Text("Web Portal", fontSize = 12.sp, fontWeight = FontWeight.Bold, color = Color.White)
          }
        }
      }

      if (pingStatusText != null) {
        item {
          Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(10.dp),
            colors = CardDefaults.cardColors(containerColor = Color(0xFF1E2838))
          ) {
            Row(
              modifier = Modifier
                .fillMaxWidth()
                .padding(12.dp),
              verticalAlignment = Alignment.CenterVertically,
              horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
              Icon(
                imageVector = Icons.Default.CheckCircle,
                contentDescription = null,
                tint = ZoxSuccess,
                modifier = Modifier.size(16.dp)
              )
              Text(
                text = pingStatusText ?: "",
                fontSize = 11.sp,
                color = Color(0xFFC0E0FF)
              )
            }
          }
        }
      }

      // 5. Emergency SOS & Mizoram Direct Contact
      item {
        Card(
          modifier = Modifier.fillMaxWidth(),
          shape = RoundedCornerShape(16.dp),
          colors = CardDefaults.cardColors(containerColor = Color(0xFF26151E)),
          border = CardDefaults.outlinedCardBorder().copy(brush = Brush.horizontalGradient(listOf(ZoxError.copy(alpha = 0.5f), Color(0xFF381423))))
        ) {
          Column(
            modifier = Modifier
              .fillMaxWidth()
              .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(10.dp)
          ) {
            Row(
              verticalAlignment = Alignment.CenterVertically,
              horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
              Icon(
                imageVector = Icons.Default.Phone,
                contentDescription = null,
                tint = ZoxError,
                modifier = Modifier.size(18.dp)
              )
              Text(
                text = "Emergency Or Urgent Errand Inquiries?",
                fontSize = 13.sp,
                fontWeight = FontWeight.Bold,
                color = Color.White
              )
            }

            Text(
              text = "While automated app orders are paused during database migration, our 24/7 Mizoram Dispatch Team is on standby for emergency vehicle breakdown and medical transport assistance.",
              fontSize = 11.sp,
              color = Color(0xFFD6AAB4),
              lineHeight = 16.sp
            )

            Row(
              modifier = Modifier.fillMaxWidth(),
              horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
              Button(
                onClick = { onEmergencyCall("+919378160106") },
                modifier = Modifier
                  .weight(1f)
                  .height(42.dp),
                colors = ButtonDefaults.buttonColors(containerColor = ZoxError),
                shape = RoundedCornerShape(10.dp)
              ) {
                Text("Call ZOX HQ: 9378160106", fontSize = 11.sp, fontWeight = FontWeight.Bold)
              }

              Button(
                onClick = { onEmergencyCall("112") },
                modifier = Modifier
                  .weight(0.7f)
                  .height(42.dp),
                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF3D1924)),
                shape = RoundedCornerShape(10.dp)
              ) {
                Text("Mizoram 112", fontSize = 11.sp, fontWeight = FontWeight.Bold, color = Color(0xFFFFB3B3))
              }
            }
          }
        }
      }

      // App Version info
      item {
        Text(
          text = "ZOX Super Apps Mizoram • Platform v${maintenanceConfig.currentAppVersion} (Min: v${maintenanceConfig.minAppVersion})",
          fontSize = 10.sp,
          color = Color(0xFF6B6A82),
          textAlign = TextAlign.Center,
          modifier = Modifier.padding(top = 8.dp)
        )
      }
    }
  }

  // Super Admin Bypass PIN Dialog
  if (showBypassDialog) {
    AdminBypassModal(
      onDismiss = { showBypassDialog = false },
      onSuccess = {
        showBypassDialog = false
        onAdminBypassSuccess()
      }
    )
  }
}

@Composable
private fun NodeStatusRow(name: String, status: String, color: Color) {
  Row(
    modifier = Modifier
      .fillMaxWidth()
      .clip(RoundedCornerShape(8.dp))
      .background(Color(0xFF171524))
      .padding(horizontal = 12.dp, vertical = 8.dp),
    horizontalArrangement = Arrangement.SpaceBetween,
    verticalAlignment = Alignment.CenterVertically
  ) {
    Row(
      verticalAlignment = Alignment.CenterVertically,
      horizontalArrangement = Arrangement.spacedBy(8.dp),
      modifier = Modifier.weight(1f)
    ) {
      Box(
        modifier = Modifier
          .size(8.dp)
          .clip(CircleShape)
          .background(color)
      )
      Text(
        text = name,
        fontSize = 11.sp,
        color = Color(0xFFDDDDED),
        fontWeight = FontWeight.Medium
      )
    }

    Text(
      text = status,
      fontSize = 10.sp,
      fontWeight = FontWeight.Bold,
      color = color
    )
  }
}

@Composable
fun AdminBypassModal(
  onDismiss: () -> Unit,
  onSuccess: () -> Unit
) {
  var pinInput by remember { mutableStateOf("") }
  var isError by remember { mutableStateOf(false) }

  Dialog(onDismissRequest = onDismiss) {
    Surface(
      shape = RoundedCornerShape(20.dp),
      color = ZoxDarkSurface,
      modifier = Modifier
        .fillMaxWidth()
        .border(1.dp, ZoxPurplePrimary, RoundedCornerShape(20.dp))
    ) {
      Column(
        modifier = Modifier.padding(20.dp),
        verticalArrangement = Arrangement.spacedBy(14.dp),
        horizontalAlignment = Alignment.CenterHorizontally
      ) {
        Box(
          modifier = Modifier
            .size(48.dp)
            .clip(CircleShape)
            .background(ZoxPurpleContainer),
          contentAlignment = Alignment.Center
        ) {
          Icon(
            imageVector = Icons.Default.Lock,
            contentDescription = null,
            tint = ZoxOrangeAccent,
            modifier = Modifier.size(24.dp)
          )
        }

        Text(
          text = "Super Admin Vault Bypass",
          fontSize = 16.sp,
          fontWeight = FontWeight.Bold,
          color = Color.White
        )

        Text(
          text = "Enter Master Developer PIN to unlock live admin controls during scheduled maintenance. Default PIN is '1106' or '9378'.",
          fontSize = 12.sp,
          color = Color(0xFFA5A5BC),
          textAlign = TextAlign.Center
        )

        OutlinedTextField(
          value = pinInput,
          onValueChange = {
            if (it.length <= 6) {
              pinInput = it
              isError = false
            }
          },
          label = { Text("6-Digit Admin PIN") },
          placeholder = { Text("1106 / 9378") },
          isError = isError,
          singleLine = true,
          modifier = Modifier
            .fillMaxWidth()
            .testTag("admin_bypass_pin_input"),
          colors = OutlinedTextFieldDefaults.colors(
            focusedBorderColor = ZoxOrangeAccent,
            unfocusedBorderColor = Color(0xFF484568),
            focusedTextColor = Color.White,
            unfocusedTextColor = Color.White,
            focusedLabelColor = ZoxOrangeAccent
          )
        )

        if (isError) {
          Text(
            text = "Invalid Master PIN. Use '1106' or '9378'.",
            fontSize = 11.sp,
            color = ZoxError,
            fontWeight = FontWeight.SemiBold
          )
        }

        Row(
          modifier = Modifier.fillMaxWidth(),
          horizontalArrangement = Arrangement.spacedBy(10.dp)
        ) {
          Button(
            onClick = onDismiss,
            modifier = Modifier
              .weight(1f)
              .height(44.dp),
            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF2E2C44)),
            shape = RoundedCornerShape(10.dp)
          ) {
            Text("Cancel", fontSize = 12.sp, color = Color.White)
          }

          Button(
            onClick = {
              if (pinInput.trim() == "1106" || pinInput.trim() == "9378" || pinInput.trim() == "9378160106") {
                onSuccess()
              } else {
                isError = true
              }
            },
            modifier = Modifier
              .weight(1f)
              .height(44.dp)
              .testTag("confirm_bypass_button"),
            colors = ButtonDefaults.buttonColors(containerColor = ZoxOrangeAccent),
            shape = RoundedCornerShape(10.dp)
          ) {
            Text("Authorize", fontSize = 12.sp, fontWeight = FontWeight.Bold, color = Color.Black)
          }
        }
      }
    }
  }
}
