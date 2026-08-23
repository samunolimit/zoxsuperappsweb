package com.example.ui.screens

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
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
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CallEnd
import androidx.compose.material.icons.filled.Cameraswitch
import androidx.compose.material.icons.filled.FlashOn
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Mic
import androidx.compose.material.icons.filled.MicOff
import androidx.compose.material.icons.filled.Security
import androidx.compose.material.icons.filled.Videocam
import androidx.compose.material.icons.filled.VideocamOff
import androidx.compose.material.icons.filled.VolumeUp
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
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
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import com.example.model.AgoraRtcConfig
import com.example.ui.components.ZoxLogoEmblem
import com.example.ui.theme.ZoxDarkBackground
import com.example.ui.theme.ZoxError
import com.example.ui.theme.ZoxOrangeAccent
import com.example.ui.theme.ZoxPurpleContainer
import com.example.ui.theme.ZoxPurplePrimary
import com.example.ui.theme.ZoxSuccess
import kotlinx.coroutines.delay

@Composable
fun VideoCallScreen(
  agoraConfig: AgoraRtcConfig,
  callerName: String = "ZOX RTC Support Engineer",
  callerRole: String = "Technical Dispatch & Safety Lead",
  onEndCall: () -> Unit
) {
  var isAudioMuted by remember { mutableStateOf(false) }
  var isVideoMuted by remember { mutableStateOf(false) }
  var isFrontCamera by remember { mutableStateOf(true) }
  var isSpeakerOn by remember { mutableStateOf(true) }
  var callDurationSeconds by remember { mutableIntStateOf(0) }
  var showFeedbackModal by remember { mutableStateOf(false) }

  // Call duration timer
  LaunchedEffect(Unit) {
    while (true) {
      delay(1000)
      callDurationSeconds++
    }
  }

  val minutes = callDurationSeconds / 60
  val seconds = callDurationSeconds % 60
  val durationString = String.format("%02d:%02d", minutes, seconds)

  // Subtle pulsing animation for active remote video stream
  val infiniteTransition = rememberInfiniteTransition(label = "video_stream")
  val streamPulse by infiniteTransition.animateFloat(
    initialValue = 0.9f,
    targetValue = 1.0f,
    animationSpec = infiniteRepeatable(
      animation = tween(2000, easing = FastOutSlowInEasing),
      repeatMode = RepeatMode.Reverse
    ),
    label = "stream_pulse"
  )

  Box(
    modifier = Modifier
      .fillMaxSize()
      .background(Color(0xFF0C0B14))
      .testTag("video_call_screen")
  ) {
    // 1. Remote Full-Screen Video Simulation View
    Box(
      modifier = Modifier
        .fillMaxSize()
        .background(
          Brush.verticalGradient(
            listOf(
              Color(0xFF1E1A33),
              Color(0xFF141224),
              Color(0xFF0A0912)
            )
          )
        ),
      contentAlignment = Alignment.Center
    ) {
      Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(14.dp)
      ) {
        Box(
          modifier = Modifier
            .size(120.dp)
            .clip(CircleShape)
            .background(
              Brush.linearGradient(
                listOf(ZoxPurplePrimary, Color(0xFFE91E63), ZoxOrangeAccent)
              )
            )
            .border(3.dp, ZoxOrangeAccent.copy(alpha = streamPulse), CircleShape),
          contentAlignment = Alignment.Center
        ) {
          ZoxLogoEmblem(size = 76.dp, animated = true)
        }

        Text(
          text = callerName,
          fontSize = 20.sp,
          fontWeight = FontWeight.Bold,
          color = Color.White
        )
        Text(
          text = callerRole,
          fontSize = 13.sp,
          color = Color(0xFFA5A5BA)
        )

        Row(
          verticalAlignment = Alignment.CenterVertically,
          horizontalArrangement = Arrangement.spacedBy(6.dp),
          modifier = Modifier
            .clip(RoundedCornerShape(20.dp))
            .background(Color(0xFF231E38))
            .padding(horizontal = 12.dp, vertical = 6.dp)
        ) {
          Box(
            modifier = Modifier
              .size(8.dp)
              .clip(CircleShape)
              .background(ZoxSuccess)
          )
          Text(
            text = "Agora RTC • 1080p 60fps • 48kHz Stereo",
            fontSize = 11.sp,
            fontWeight = FontWeight.Medium,
            color = Color(0xFFDCDCE8)
          )
        }
      }
    }

    // 2. Top Header Overlay (Timer, Encryption & Agora Info)
    Row(
      modifier = Modifier
        .fillMaxWidth()
        .statusBarsPadding()
        .padding(16.dp),
      horizontalArrangement = Arrangement.SpaceBetween,
      verticalAlignment = Alignment.CenterVertically
    ) {
      // Encryption badge
      Row(
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(6.dp),
        modifier = Modifier
          .clip(RoundedCornerShape(8.dp))
          .background(Color.Black.copy(alpha = 0.6f))
          .border(0.8.dp, Color(0xFF4A4865), RoundedCornerShape(8.dp))
          .padding(horizontal = 10.dp, vertical = 6.dp)
      ) {
        Icon(
          imageVector = Icons.Default.Lock,
          contentDescription = null,
          tint = ZoxSuccess,
          modifier = Modifier.size(14.dp)
        )
        Text(
          text = "E2E Encrypted (TLS 1.3)",
          fontSize = 11.sp,
          fontWeight = FontWeight.SemiBold,
          color = Color.White
        )
      }

      // Duration Timer
      Box(
        modifier = Modifier
          .clip(RoundedCornerShape(8.dp))
          .background(ZoxPurpleContainer)
          .border(0.8.dp, ZoxPurplePrimary, RoundedCornerShape(8.dp))
          .padding(horizontal = 12.dp, vertical = 6.dp)
      ) {
        Text(
          text = durationString,
          fontSize = 13.sp,
          fontWeight = FontWeight.Black,
          color = ZoxOrangeAccent
        )
      }
    }

    // 3. Picture-in-Picture (PiP) Local Camera Preview
    Box(
      modifier = Modifier
        .align(Alignment.TopEnd)
        .statusBarsPadding()
        .padding(top = 64.dp, end = 16.dp)
        .size(width = 110.dp, height = 155.dp)
        .clip(RoundedCornerShape(16.dp))
        .background(if (isVideoMuted) Color(0xFF1E1E2C) else Color(0xFF261C3D))
        .border(1.5.dp, ZoxOrangeAccent, RoundedCornerShape(16.dp))
        .testTag("pip_local_video_preview"),
      contentAlignment = Alignment.Center
    ) {
      if (isVideoMuted) {
        Column(
          horizontalAlignment = Alignment.CenterHorizontally,
          verticalArrangement = Arrangement.spacedBy(4.dp)
        ) {
          Icon(
            imageVector = Icons.Default.VideocamOff,
            contentDescription = null,
            tint = Color(0xFFA5A5BA),
            modifier = Modifier.size(24.dp)
          )
          Text(text = "Cam Off", fontSize = 10.sp, color = Color(0xFFA5A5BA))
        }
      } else {
        Column(
          horizontalAlignment = Alignment.CenterHorizontally,
          verticalArrangement = Arrangement.Center,
          modifier = Modifier.fillMaxSize().padding(8.dp)
        ) {
          Box(
            modifier = Modifier
              .size(42.dp)
              .clip(CircleShape)
              .background(ZoxPurplePrimary),
            contentAlignment = Alignment.Center
          ) {
            Text(text = "YOU", fontSize = 11.sp, fontWeight = FontWeight.Bold, color = Color.White)
          }
          Spacer(modifier = Modifier.height(4.dp))
          Text(
            text = if (isFrontCamera) "Front Camera" else "Rear Camera",
            fontSize = 9.sp,
            color = Color(0xFFD4D4E8)
          )
        }
      }

      // Small mic status tag on PiP
      if (isAudioMuted) {
        Box(
          modifier = Modifier
            .align(Alignment.BottomEnd)
            .padding(6.dp)
            .size(20.dp)
            .clip(CircleShape)
            .background(ZoxError),
          contentAlignment = Alignment.Center
        ) {
          Icon(
            imageVector = Icons.Default.MicOff,
            contentDescription = null,
            tint = Color.White,
            modifier = Modifier.size(12.dp)
          )
        }
      }
    }

    // 4. Bottom Control Bar
    Column(
      modifier = Modifier
        .fillMaxWidth()
        .align(Alignment.BottomCenter)
        .navigationBarsPadding()
        .padding(bottom = 24.dp, start = 16.dp, end = 16.dp),
      horizontalAlignment = Alignment.CenterHorizontally,
      verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
      // Channel and network stats
      Text(
        text = "Channel: ${agoraConfig.defaultChannel} • Ping: 24ms",
        fontSize = 11.sp,
        color = Color(0xFF8E8EA8)
      )

      Row(
        modifier = Modifier
          .fillMaxWidth()
          .clip(RoundedCornerShape(32.dp))
          .background(Color(0xFF1B192A).copy(alpha = 0.92f))
          .border(1.dp, Color(0xFF3B3855), RoundedCornerShape(32.dp))
          .padding(horizontal = 14.dp, vertical = 12.dp),
        horizontalArrangement = Arrangement.SpaceEvenly,
        verticalAlignment = Alignment.CenterVertically
      ) {
        // Mic Toggle
        IconButton(
          onClick = { isAudioMuted = !isAudioMuted },
          modifier = Modifier
            .size(50.dp)
            .clip(CircleShape)
            .background(if (isAudioMuted) ZoxError else Color(0xFF2D2A45))
            .testTag("toggle_mic_button")
        ) {
          Icon(
            imageVector = if (isAudioMuted) Icons.Default.MicOff else Icons.Default.Mic,
            contentDescription = "Toggle Mic",
            tint = Color.White,
            modifier = Modifier.size(24.dp)
          )
        }

        // Camera Video Toggle
        IconButton(
          onClick = { isVideoMuted = !isVideoMuted },
          modifier = Modifier
            .size(50.dp)
            .clip(CircleShape)
            .background(if (isVideoMuted) ZoxError else Color(0xFF2D2A45))
            .testTag("toggle_video_button")
        ) {
          Icon(
            imageVector = if (isVideoMuted) Icons.Default.VideocamOff else Icons.Default.Videocam,
            contentDescription = "Toggle Camera",
            tint = Color.White,
            modifier = Modifier.size(24.dp)
          )
        }

        // Flip Camera
        IconButton(
          onClick = { isFrontCamera = !isFrontCamera },
          modifier = Modifier
            .size(50.dp)
            .clip(CircleShape)
            .background(Color(0xFF2D2A45))
            .testTag("flip_camera_button")
        ) {
          Icon(
            imageVector = Icons.Default.Cameraswitch,
            contentDescription = "Flip Camera",
            tint = Color.White,
            modifier = Modifier.size(24.dp)
          )
        }

        // Speaker Toggle
        IconButton(
          onClick = { isSpeakerOn = !isSpeakerOn },
          modifier = Modifier
            .size(50.dp)
            .clip(CircleShape)
            .background(if (isSpeakerOn) ZoxPurplePrimary else Color(0xFF2D2A45))
            .testTag("toggle_speaker_button")
        ) {
          Icon(
            imageVector = Icons.Default.VolumeUp,
            contentDescription = "Speaker",
            tint = Color.White,
            modifier = Modifier.size(24.dp)
          )
        }

        // End Call CTA
        IconButton(
          onClick = { showFeedbackModal = true },
          modifier = Modifier
            .size(54.dp)
            .clip(CircleShape)
            .background(ZoxError)
            .testTag("end_call_button")
        ) {
          Icon(
            imageVector = Icons.Default.CallEnd,
            contentDescription = "End Call",
            tint = Color.White,
            modifier = Modifier.size(28.dp)
          )
        }
      }
    }
  }

  // End Call Confirmation / Feedback Dialog
  if (showFeedbackModal) {
    Dialog(onDismissRequest = { showFeedbackModal = false }) {
      Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(containerColor = Color(0xFF1E1E2C))
      ) {
        Column(
          modifier = Modifier
            .fillMaxWidth()
            .padding(20.dp),
          horizontalAlignment = Alignment.CenterHorizontally,
          verticalArrangement = Arrangement.spacedBy(14.dp)
        ) {
          Box(
            modifier = Modifier
              .size(50.dp)
              .clip(CircleShape)
              .background(ZoxError.copy(alpha = 0.2f)),
            contentAlignment = Alignment.Center
          ) {
            Icon(
              imageVector = Icons.Default.CallEnd,
              contentDescription = null,
              tint = ZoxError,
              modifier = Modifier.size(26.dp)
            )
          }

          Text(
            text = "Disconnect Live Video Call?",
            fontSize = 17.sp,
            fontWeight = FontWeight.Bold,
            color = Color.White
          )

          Text(
            text = "Call session duration was $durationString with $callerName.",
            fontSize = 13.sp,
            color = Color(0xFFA0A0BA),
            textAlign = androidx.compose.ui.text.style.TextAlign.Center
          )

          Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(10.dp)
          ) {
            Button(
              onClick = { showFeedbackModal = false },
              modifier = Modifier.weight(1f),
              colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF33334A)),
              shape = RoundedCornerShape(10.dp)
            ) {
              Text("Resume", color = Color.White)
            }

            Button(
              onClick = {
                showFeedbackModal = false
                onEndCall()
              },
              modifier = Modifier.weight(1f),
              colors = ButtonDefaults.buttonColors(containerColor = ZoxError),
              shape = RoundedCornerShape(10.dp)
            ) {
              Text("End Call", color = Color.White, fontWeight = FontWeight.Bold)
            }
          }
        }
      }
    }
  }
}
