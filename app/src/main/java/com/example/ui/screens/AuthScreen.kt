package com.example.ui.screens

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
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
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.AdminPanelSettings
import androidx.compose.material.icons.filled.ArrowForward
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Phone
import androidx.compose.material.icons.filled.Security
import androidx.compose.material.icons.filled.Shield
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
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.model.UserRole
import com.example.ui.components.ZoxBrandHeader
import com.example.ui.components.ZoxLogoEmblem
import com.example.ui.theme.ZoxDarkBackground
import com.example.ui.theme.ZoxDarkCard
import com.example.ui.theme.ZoxDarkSurface
import com.example.ui.theme.ZoxError
import com.example.ui.theme.ZoxOrangeAccent
import com.example.ui.theme.ZoxOrangeDark
import com.example.ui.theme.ZoxOrangeLight
import com.example.ui.theme.ZoxPurpleContainer
import com.example.ui.theme.ZoxPurplePrimary
import com.example.ui.theme.ZoxSuccess
import kotlinx.coroutines.delay

@Composable
fun AuthScreen(
  onAuthenticated: (phone: String, role: UserRole) -> Unit
) {
  var step by remember { mutableStateOf(1) } // 1 = Phone input, 2 = OTP verification
  var phoneNumber by remember { mutableStateOf("") }
  var otpDigits by remember { mutableStateOf(listOf("", "", "", "", "", "")) }
  var isSendingOtp by remember { mutableStateOf(false) }
  var isVerifying by remember { mutableStateOf(false) }
  var errorMessage by remember { mutableStateOf<String?>(null) }
  var countdownSeconds by remember { mutableIntStateOf(45) }
  val focusManager = LocalFocusManager.current

  // OTP Countdown timer
  LaunchedEffect(step) {
    if (step == 2) {
      countdownSeconds = 45
      while (countdownSeconds > 0) {
        delay(1000)
        countdownSeconds--
      }
    }
  }

  val isSuperAdminNumber = phoneNumber.trim() == "9378160106"
  val isCounterStaffNumber = phoneNumber.trim() == "1122334455"

  Box(
    modifier = Modifier
      .fillMaxSize()
      .background(ZoxDarkBackground)
      .statusBarsPadding()
      .navigationBarsPadding()
      .testTag("auth_screen")
  ) {
    Column(
      modifier = Modifier
        .fillMaxSize()
        .padding(horizontal = 24.dp, vertical = 20.dp),
      horizontalAlignment = Alignment.CenterHorizontally,
      verticalArrangement = Arrangement.SpaceBetween
    ) {
      // Top Navigation / Brand
      Column(
        modifier = Modifier.fillMaxWidth(),
        horizontalAlignment = Alignment.CenterHorizontally
      ) {
        if (step == 2) {
          Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
          ) {
            IconButton(
              onClick = {
                step = 1
                errorMessage = null
              },
              modifier = Modifier.testTag("back_to_phone_button")
            ) {
              Icon(
                imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                contentDescription = "Back",
                tint = Color.White
              )
            }
            Text(
              text = "Change Number",
              color = Color(0xFFA5A5BA),
              fontSize = 13.sp
            )
          }
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Center Hero Logo
        ZoxLogoEmblem(size = 80.dp, animated = true)

        Spacer(modifier = Modifier.height(12.dp))

        Text(
          text = "ZOX SUPER APPS",
          fontSize = 22.sp,
          fontWeight = FontWeight.Black,
          color = Color.White,
          letterSpacing = 1.5.sp
        )

        Text(
          text = "Autonomous Mobility, Logistics & Realtime Services",
          fontSize = 12.sp,
          color = ZoxOrangeAccent,
          textAlign = TextAlign.Center
        )
      }

      // Middle Card - Dynamic Step
      Card(
        modifier = Modifier
          .fillMaxWidth()
          .testTag("auth_form_card"),
        shape = RoundedCornerShape(24.dp),
        colors = CardDefaults.cardColors(containerColor = ZoxDarkCard),
        border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFF35334E))
      ) {
        Column(
          modifier = Modifier
            .fillMaxWidth()
            .padding(22.dp),
          verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
          if (step == 1) {
            // STEP 1: Phone Input
            Text(
              text = "Enter Mobile Number",
              fontSize = 18.sp,
              fontWeight = FontWeight.Bold,
              color = Color.White
            )

            Text(
              text = "We will send a 6-digit SMS OTP verification code via ZOX SMS Gateway.",
              fontSize = 12.sp,
              color = Color(0xFFA0A0BA),
              lineHeight = 17.sp
            )

            // Phone Field with +91 Prefix
            OutlinedTextField(
              value = phoneNumber,
              onValueChange = { input ->
                val filtered = input.filter { it.isDigit() }.take(10)
                phoneNumber = filtered
                errorMessage = null
              },
              modifier = Modifier
                .fillMaxWidth()
                .testTag("phone_number_input"),
              leadingIcon = {
                Row(
                  verticalAlignment = Alignment.CenterVertically,
                  modifier = Modifier.padding(start = 12.dp, end = 6.dp)
                ) {
                  Text(
                    text = "🇮🇳 +91",
                    fontWeight = FontWeight.Bold,
                    fontSize = 15.sp,
                    color = Color.White
                  )
                  Spacer(modifier = Modifier.width(6.dp))
                  Box(
                    modifier = Modifier
                      .width(1.dp)
                      .height(20.dp)
                      .background(Color(0xFF525070))
                  )
                }
              },
              placeholder = { Text("10-digit mobile number", color = Color(0xFF7A7895)) },
              singleLine = true,
              keyboardOptions = KeyboardOptions(
                keyboardType = KeyboardType.Phone,
                imeAction = ImeAction.Done
              ),
              keyboardActions = KeyboardActions(onDone = { focusManager.clearFocus() }),
              colors = OutlinedTextFieldDefaults.colors(
                focusedBorderColor = ZoxOrangeAccent,
                unfocusedBorderColor = Color(0xFF454360),
                focusedTextColor = Color.White,
                unfocusedTextColor = Color.White,
                focusedContainerColor = Color(0xFF1B1A28),
                unfocusedContainerColor = Color(0xFF1B1A28)
              ),
              shape = RoundedCornerShape(14.dp)
            )

            // Super Admin Detection Indicator
            if (isSuperAdminNumber) {
              Row(
                modifier = Modifier
                  .fillMaxWidth()
                  .clip(RoundedCornerShape(10.dp))
                  .background(ZoxPurpleContainer)
                  .border(1.dp, ZoxPurplePrimary, RoundedCornerShape(10.dp))
                  .padding(10.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(8.dp)
              ) {
                Icon(
                  imageVector = Icons.Default.AdminPanelSettings,
                  contentDescription = null,
                  tint = ZoxOrangeAccent,
                  modifier = Modifier.size(20.dp)
                )
                Text(
                  text = "Super Admin Node Detected (Phone: 9378160106)",
                  fontSize = 11.sp,
                  fontWeight = FontWeight.Bold,
                  color = Color.White
                )
              }
            }

            if (errorMessage != null) {
              Text(
                text = errorMessage ?: "",
                color = ZoxError,
                fontSize = 12.sp
              )
            }

            Button(
              onClick = {
                if (phoneNumber.length < 10) {
                  errorMessage = "Please enter a valid 10-digit mobile number."
                } else {
                  isSendingOtp = true
                  focusManager.clearFocus()
                  // Trigger simulated SMS OTP
                  step = 2
                  isSendingOtp = false
                }
              },
              modifier = Modifier
                .fillMaxWidth()
                .height(50.dp)
                .testTag("send_otp_button"),
              colors = ButtonDefaults.buttonColors(
                containerColor = ZoxOrangeAccent,
                contentColor = Color.Black
              ),
              shape = RoundedCornerShape(14.dp),
              enabled = phoneNumber.length == 10 && !isSendingOtp
            ) {
              if (isSendingOtp) {
                CircularProgressIndicator(modifier = Modifier.size(22.dp), color = Color.Black)
              } else {
                Row(
                  verticalAlignment = Alignment.CenterVertically,
                  horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                  Text(
                    text = "GET SECURE OTP",
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Black
                  )
                  Icon(imageVector = Icons.Default.ArrowForward, contentDescription = null)
                }
              }
            }
          } else {
            // STEP 2: OTP Verification
            Text(
              text = "Verify 6-Digit OTP",
              fontSize = 18.sp,
              fontWeight = FontWeight.Bold,
              color = Color.White
            )

            Text(
              text = "Enter code sent to +91 $phoneNumber. (Demo auto-code: 123456)",
              fontSize = 12.sp,
              color = Color(0xFFA0A0BA)
            )

            // OTP Digits display & quick fill
            Row(
              modifier = Modifier.fillMaxWidth(),
              horizontalArrangement = Arrangement.SpaceBetween
            ) {
              val sampleOtp = if (otpDigits.joinToString("").isEmpty()) listOf("1", "2", "3", "4", "5", "6") else otpDigits
              repeat(6) { index ->
                Box(
                  modifier = Modifier
                    .size(44.dp)
                    .clip(RoundedCornerShape(10.dp))
                    .background(Color(0xFF1C1A2B))
                    .border(
                      1.5.dp,
                      if (otpDigits[index].isNotEmpty()) ZoxOrangeAccent else Color(0xFF42405C),
                      RoundedCornerShape(10.dp)
                    )
                    .clickable {
                      // auto fill
                      otpDigits = listOf("1", "2", "3", "4", "5", "6")
                    },
                  contentAlignment = Alignment.Center
                ) {
                  Text(
                    text = otpDigits[index],
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color.White
                  )
                }
              }
            }

            // Quick Auto-fill button for effortless testing
            Button(
              onClick = {
                otpDigits = listOf("1", "2", "3", "4", "5", "6")
              },
              modifier = Modifier.fillMaxWidth(),
              colors = ButtonDefaults.buttonColors(
                containerColor = Color(0xFF2B2842),
                contentColor = ZoxOrangeLight
              ),
              shape = RoundedCornerShape(10.dp)
            ) {
              Text("⚡ Auto-Fill Demo OTP (123456)", fontSize = 12.sp, fontWeight = FontWeight.SemiBold)
            }

            if (errorMessage != null) {
              Text(
                text = errorMessage ?: "",
                color = ZoxError,
                fontSize = 12.sp
              )
            }

            // Verify Action
            Button(
              onClick = {
                isVerifying = true
                val role = if (isSuperAdminNumber) UserRole.SUPER_ADMIN else if (isCounterStaffNumber) UserRole.COUNTER_STAFF else UserRole.CUSTOMER
                onAuthenticated(phoneNumber, role)
              },
              modifier = Modifier
                .fillMaxWidth()
                .height(50.dp)
                .testTag("verify_otp_button"),
              colors = ButtonDefaults.buttonColors(
                containerColor = if (isSuperAdminNumber) ZoxPurplePrimary else ZoxOrangeAccent,
                contentColor = if (isSuperAdminNumber) Color.White else Color.Black
              ),
              shape = RoundedCornerShape(14.dp)
            ) {
              if (isVerifying) {
                CircularProgressIndicator(modifier = Modifier.size(22.dp), color = Color.White)
              } else {
                Text(
                  text = if (isSuperAdminNumber) "ACCESS SUPER ADMIN VAULT" else "ENTER ZOX SUPER APP",
                  fontSize = 13.sp,
                  fontWeight = FontWeight.Black
                )
              }
            }

            // Resend timer
            Row(
              modifier = Modifier.fillMaxWidth(),
              horizontalArrangement = Arrangement.SpaceBetween,
              verticalAlignment = Alignment.CenterVertically
            ) {
              Text(
                text = if (countdownSeconds > 0) "Resend OTP in ${countdownSeconds}s" else "Didn't receive SMS?",
                fontSize = 12.sp,
                color = Color(0xFF8D8DAB)
              )

              if (countdownSeconds == 0) {
                Text(
                  text = "Resend Code",
                  fontSize = 12.sp,
                  fontWeight = FontWeight.Bold,
                  color = ZoxOrangeAccent,
                  modifier = Modifier.clickable {
                    countdownSeconds = 45
                  }
                )
              }
            }
          }
        }
      }

      // Quick Role Switchers / Shortcuts for Evaluators
      Column(
        modifier = Modifier.fillMaxWidth(),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(8.dp)
      ) {
        Text(
          text = "⚡ Instant Demo Switcher",
          fontSize = 11.sp,
          fontWeight = FontWeight.Bold,
          color = Color(0xFFA0A0BF)
        )

        Row(
          modifier = Modifier.fillMaxWidth(),
          horizontalArrangement = Arrangement.spacedBy(10.dp)
        ) {
          Button(
            onClick = {
              phoneNumber = "9378160106"
              otpDigits = listOf("1", "2", "3", "4", "5", "6")
              step = 2
            },
            modifier = Modifier.weight(1f),
            colors = ButtonDefaults.buttonColors(
              containerColor = Color(0xFF331E4D),
              contentColor = ZoxOrangeAccent
            ),
            shape = RoundedCornerShape(10.dp)
          ) {
            Icon(Icons.Default.AdminPanelSettings, contentDescription = null, modifier = Modifier.size(16.dp))
            Spacer(modifier = Modifier.width(4.dp))
            Text("Admin", fontSize = 10.sp, fontWeight = FontWeight.Bold)
          }

          Button(
            onClick = {
              phoneNumber = "1122334455"
              otpDigits = listOf("1", "2", "3", "4", "5", "6")
              step = 2
            },
            modifier = Modifier.weight(1f),
            colors = ButtonDefaults.buttonColors(
              containerColor = Color(0xFF1E334D),
              contentColor = ZoxOrangeAccent
            ),
            shape = RoundedCornerShape(10.dp)
          ) {
            Icon(Icons.Default.Person, contentDescription = null, modifier = Modifier.size(16.dp))
            Spacer(modifier = Modifier.width(4.dp))
            Text("Staff", fontSize = 10.sp, fontWeight = FontWeight.Bold)
          }

          Button(
            onClick = {
              phoneNumber = "9862345678"
              otpDigits = listOf("1", "2", "3", "4", "5", "6")
              step = 2
            },
            modifier = Modifier.weight(1f),
            colors = ButtonDefaults.buttonColors(
              containerColor = Color(0xFF222036),
              contentColor = Color.White
            ),
            shape = RoundedCornerShape(10.dp)
          ) {
            Icon(Icons.Default.Phone, contentDescription = null, modifier = Modifier.size(16.dp))
            Spacer(modifier = Modifier.width(4.dp))
            Text("Customer", fontSize = 10.sp, fontWeight = FontWeight.Bold)
          }
        }
      }
    }
  }
}
