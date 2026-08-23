package com.example.ui.components

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
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
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CardGiftcard
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.LocalOffer
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.filled.Verified
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
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
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import com.example.model.AdMobConfig
import com.example.ui.theme.ZoxDarkCard
import com.example.ui.theme.ZoxDarkSurface
import com.example.ui.theme.ZoxDarkSurfaceVariant
import com.example.ui.theme.ZoxOrangeAccent
import com.example.ui.theme.ZoxOrangeDark
import com.example.ui.theme.ZoxPurpleContainer
import com.example.ui.theme.ZoxPurplePrimary
import com.example.ui.theme.ZoxSuccess
import kotlinx.coroutines.delay

/**
 * Dynamic AdMob Banner View (Hidden if AdMob is disabled by Admin)
 */
@Composable
fun AdMobBannerView(
  adMobConfig: AdMobConfig,
  modifier: Modifier = Modifier,
  adSlotName: String = "Home_Bottom_Adaptive"
) {
  if (!adMobConfig.isEnabled) return

  Surface(
    modifier = modifier
      .fillMaxWidth()
      .testTag("admob_banner_view"),
    color = Color(0xFF181726),
    shape = RoundedCornerShape(12.dp),
    border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFF2E2C44))
  ) {
    Row(
      modifier = Modifier
        .fillMaxWidth()
        .padding(horizontal = 12.dp, vertical = 8.dp),
      verticalAlignment = Alignment.CenterVertically,
      horizontalArrangement = Arrangement.SpaceBetween
    ) {
      Row(
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(10.dp),
        modifier = Modifier.weight(1f)
      ) {
        Box(
          modifier = Modifier
            .size(36.dp)
            .clip(RoundedCornerShape(8.dp))
            .background(Brush.linearGradient(listOf(ZoxPurplePrimary, ZoxOrangeAccent))),
          contentAlignment = Alignment.Center
        ) {
          Icon(
            imageVector = Icons.Default.LocalOffer,
            contentDescription = "Ad Sponsor",
            tint = Color.White,
            modifier = Modifier.size(20.dp)
          )
        }

        Column {
          Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(6.dp)
          ) {
            Box(
              modifier = Modifier
                .clip(RoundedCornerShape(4.dp))
                .background(Color(0xFF3B3958))
                .padding(horizontal = 4.dp, vertical = 1.dp)
            ) {
              Text(
                text = "Ad",
                fontSize = 9.sp,
                fontWeight = FontWeight.Bold,
                color = Color(0xFFFFD54F)
              )
            }
            Text(
              text = "ZOX Fleet VIP Pass",
              fontSize = 12.sp,
              fontWeight = FontWeight.Bold,
              color = Color.White,
              maxLines = 1,
              overflow = TextOverflow.Ellipsis
            )
          }
          Text(
            text = "Get 20% cashback on all Lunglei & Aizawl rides",
            fontSize = 10.sp,
            color = Color(0xFFA5A5BA),
            maxLines = 1,
            overflow = TextOverflow.Ellipsis
          )
        }
      }

      Button(
        onClick = { /* Simulated Ad Click */ },
        colors = ButtonDefaults.buttonColors(
          containerColor = ZoxOrangeAccent,
          contentColor = Color.Black
        ),
        shape = RoundedCornerShape(8.dp),
        contentPadding = androidx.compose.foundation.layout.PaddingValues(horizontal = 10.dp, vertical = 4.dp),
        modifier = Modifier.height(28.dp)
      ) {
        Text(text = "CLAIM", fontSize = 10.sp, fontWeight = FontWeight.Black)
      }
    }
  }
}

/**
 * Native Feed Ad Card integrated inside carousels and lists
 */
@Composable
fun AdMobNativeFeedCard(
  adMobConfig: AdMobConfig,
  modifier: Modifier = Modifier
) {
  if (!adMobConfig.isEnabled) return

  Card(
    modifier = modifier
      .width(260.dp)
      .height(180.dp)
      .testTag("admob_native_feed_card"),
    colors = CardDefaults.cardColors(containerColor = ZoxDarkCard),
    shape = RoundedCornerShape(16.dp),
    border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFF383652))
  ) {
    Column(
      modifier = Modifier
        .fillMaxSize()
        .padding(12.dp),
      verticalArrangement = Arrangement.SpaceBetween
    ) {
      Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
      ) {
        Row(
          verticalAlignment = Alignment.CenterVertically,
          horizontalArrangement = Arrangement.spacedBy(6.dp)
        ) {
          Box(
            modifier = Modifier
              .clip(RoundedCornerShape(4.dp))
              .background(ZoxOrangeAccent.copy(alpha = 0.2f))
              .border(0.8.dp, ZoxOrangeAccent, RoundedCornerShape(4.dp))
              .padding(horizontal = 5.dp, vertical = 1.dp)
          ) {
            Text(
              text = "SPONSORED",
              fontSize = 8.sp,
              fontWeight = FontWeight.Bold,
              color = ZoxOrangeAccent
            )
          }
          Text(text = "ZOX Store Partner", fontSize = 11.sp, color = Color(0xFFA0A0BA))
        }
        Icon(
          imageVector = Icons.Default.Star,
          contentDescription = null,
          tint = Color(0xFFFFD54F),
          modifier = Modifier.size(16.dp)
        )
      }

      Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
        Text(
          text = "Mizo Traditional Handicrafts & Organic Teas",
          fontSize = 13.sp,
          fontWeight = FontWeight.Bold,
          color = Color.White,
          maxLines = 2,
          overflow = TextOverflow.Ellipsis
        )
        Text(
          text = "Fast 30-min Tirhkah delivery across Aizawl town.",
          fontSize = 11.sp,
          color = Color(0xFFA8A8C0),
          maxLines = 2
        )
      }

      Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
      ) {
        Text(
          text = "Unit: ${adMobConfig.nativeUnitId.takeLast(8)}",
          fontSize = 9.sp,
          color = Color(0xFF6B6A82)
        )
        Box(
          modifier = Modifier
            .clip(RoundedCornerShape(6.dp))
            .background(ZoxPurplePrimary)
            .padding(horizontal = 8.dp, vertical = 4.dp)
        ) {
          Text(
            text = "EXPLORE",
            fontSize = 10.sp,
            fontWeight = FontWeight.Bold,
            color = Color.White
          )
        }
      }
    }
  }
}

/**
 * Rewarded Video Ad Modal Experience that awards ZOX Loyalty Coins
 */
@Composable
fun AdMobRewardedVideoModal(
  adMobConfig: AdMobConfig,
  onDismiss: () -> Unit,
  onRewardEarned: (coins: Int) -> Unit
) {
  var secondsLeft by remember { mutableIntStateOf(5) }
  var progress by remember { mutableFloatStateOf(0f) }
  var isCompleted by remember { mutableStateOf(false) }

  LaunchedEffect(Unit) {
    val totalSeconds = 5
    for (i in totalSeconds downTo 1) {
      secondsLeft = i
      progress = (totalSeconds - i + 1) / totalSeconds.toFloat()
      delay(1000)
    }
    isCompleted = true
  }

  Dialog(
    onDismissRequest = {
      if (isCompleted) onDismiss()
    },
    properties = DialogProperties(usePlatformDefaultWidth = false, dismissOnBackPress = isCompleted)
  ) {
    Box(
      modifier = Modifier
        .fillMaxSize()
        .background(Color.Black.copy(alpha = 0.95f))
        .padding(16.dp),
      contentAlignment = Alignment.Center
    ) {
      Card(
        modifier = Modifier
          .fillMaxWidth()
          .testTag("rewarded_video_ad_card"),
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(containerColor = ZoxDarkSurface),
        border = androidx.compose.foundation.BorderStroke(1.dp, ZoxOrangeAccent.copy(alpha = 0.5f))
      ) {
        Column(
          modifier = Modifier
            .fillMaxWidth()
            .padding(20.dp),
          horizontalAlignment = Alignment.CenterHorizontally,
          verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
          // Top Bar
          Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
          ) {
            Box(
              modifier = Modifier
                .clip(RoundedCornerShape(6.dp))
                .background(Color(0xFF38254C))
                .padding(horizontal = 8.dp, vertical = 4.dp)
            ) {
              Text(
                text = "Google AdMob Rewarded Video",
                fontSize = 11.sp,
                fontWeight = FontWeight.Bold,
                color = ZoxOrangeAccent
              )
            }

            if (isCompleted) {
              IconButton(onClick = {
                onRewardEarned(adMobConfig.rewardedCoinRate)
                onDismiss()
              }) {
                Icon(
                  imageVector = Icons.Default.Close,
                  contentDescription = "Close Ad",
                  tint = Color.White
                )
              }
            } else {
              Box(
                modifier = Modifier
                  .size(32.dp)
                  .clip(CircleShape)
                  .background(Color(0xFF2C2B3E)),
                contentAlignment = Alignment.Center
              ) {
                Text(
                  text = "${secondsLeft}s",
                  fontSize = 12.sp,
                  fontWeight = FontWeight.Bold,
                  color = Color.White
                )
              }
            }
          }

          // Video Simulation Box
          Box(
            modifier = Modifier
              .fillMaxWidth()
              .height(200.dp)
              .clip(RoundedCornerShape(14.dp))
              .background(
                Brush.verticalGradient(
                  listOf(Color(0xFF1E1333), Color(0xFF2E1C4E), Color(0xFF0F0B18))
                )
              ),
            contentAlignment = Alignment.Center
          ) {
            Column(
              horizontalAlignment = Alignment.CenterHorizontally,
              verticalArrangement = Arrangement.spacedBy(8.dp),
              modifier = Modifier.padding(16.dp)
            ) {
              Box(
                modifier = Modifier
                  .size(54.dp)
                  .clip(CircleShape)
                  .background(ZoxOrangeAccent),
                contentAlignment = Alignment.Center
              ) {
                Icon(
                  imageVector = if (isCompleted) Icons.Default.CheckCircle else Icons.Default.PlayArrow,
                  contentDescription = null,
                  tint = Color.Black,
                  modifier = Modifier.size(32.dp)
                )
              }
              Text(
                text = if (isCompleted) "Video Complete!" else "ZOX Super App Premium Partners",
                fontSize = 16.sp,
                fontWeight = FontWeight.Bold,
                color = Color.White
              )
              Text(
                text = "Instant grocery, taxi & bike hire delivered with high speed.",
                fontSize = 12.sp,
                color = Color(0xFFAFAFC7),
                textAlign = androidx.compose.ui.text.style.TextAlign.Center
              )
            }

            LinearProgressIndicator(
              progress = { progress },
              modifier = Modifier
                .fillMaxWidth()
                .align(Alignment.BottomCenter)
                .height(4.dp),
              color = ZoxOrangeAccent,
              trackColor = Color(0xFF333045)
            )
          }

          // Reward announcement
          Row(
            modifier = Modifier
              .fillMaxWidth()
              .clip(RoundedCornerShape(12.dp))
              .background(ZoxPurpleContainer)
              .padding(12.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(10.dp)
          ) {
            Icon(
              imageVector = Icons.Default.CardGiftcard,
              contentDescription = null,
              tint = ZoxOrangeAccent,
              modifier = Modifier.size(28.dp)
            )
            Column {
              Text(
                text = "Reward: +${adMobConfig.rewardedCoinRate} ZOX Loyalty Coins",
                fontSize = 13.sp,
                fontWeight = FontWeight.Bold,
                color = Color.White
              )
              Text(
                text = if (isCompleted) "Claim your coins now!" else "Watch full 5-sec video to receive reward",
                fontSize = 11.sp,
                color = Color(0xFFA5A5BC)
              )
            }
          }

          if (isCompleted) {
            Button(
              onClick = {
                onRewardEarned(adMobConfig.rewardedCoinRate)
                onDismiss()
              },
              modifier = Modifier
                .fillMaxWidth()
                .height(48.dp)
                .testTag("claim_reward_button"),
              colors = ButtonDefaults.buttonColors(
                containerColor = ZoxOrangeAccent,
                contentColor = Color.Black
              ),
              shape = RoundedCornerShape(12.dp)
            ) {
              Icon(imageVector = Icons.Default.Verified, contentDescription = null)
              Spacer(modifier = Modifier.width(8.dp))
              Text(
                text = "CLAIM +${adMobConfig.rewardedCoinRate} REWARD COINS",
                fontSize = 13.sp,
                fontWeight = FontWeight.Black
              )
            }
          }
        }
      }
    }
  }
}

/**
 * Interstitial Full-Screen Ad popped after bookings or orders
 */
@Composable
fun AdMobInterstitialAdModal(
  adMobConfig: AdMobConfig,
  onDismiss: () -> Unit
) {
  var canClose by remember { mutableStateOf(false) }
  var countdown by remember { mutableIntStateOf(3) }

  LaunchedEffect(Unit) {
    for (i in 3 downTo 1) {
      countdown = i
      delay(1000)
    }
    canClose = true
  }

  Dialog(
    onDismissRequest = {
      if (canClose) onDismiss()
    },
    properties = DialogProperties(usePlatformDefaultWidth = false)
  ) {
    Box(
      modifier = Modifier
        .fillMaxSize()
        .background(Color(0xFF0F0E17))
        .padding(20.dp)
        .testTag("interstitial_ad_screen")
    ) {
      Column(
        modifier = Modifier.fillMaxSize(),
        verticalArrangement = Arrangement.SpaceBetween
      ) {
        // Top row
        Row(
          modifier = Modifier.fillMaxWidth(),
          horizontalArrangement = Arrangement.SpaceBetween,
          verticalAlignment = Alignment.CenterVertically
        ) {
          Box(
            modifier = Modifier
              .clip(RoundedCornerShape(4.dp))
              .background(Color(0xFF2C2B3E))
              .padding(horizontal = 8.dp, vertical = 3.dp)
          ) {
            Text(
              text = "AdMob Interstitial (${adMobConfig.interstitialUnitId.takeLast(8)})",
              fontSize = 11.sp,
              color = Color(0xFFAAAAAA)
            )
          }

          if (canClose) {
            IconButton(
              onClick = onDismiss,
              modifier = Modifier
                .size(36.dp)
                .clip(CircleShape)
                .background(Color(0xFF2C2B3E))
            ) {
              Icon(imageVector = Icons.Default.Close, contentDescription = "Close", tint = Color.White)
            }
          } else {
            Text(
              text = "Skip in ${countdown}s",
              fontSize = 12.sp,
              fontWeight = FontWeight.Bold,
              color = ZoxOrangeAccent
            )
          }
        }

        // Ad Content
        Column(
          horizontalAlignment = Alignment.CenterHorizontally,
          verticalArrangement = Arrangement.spacedBy(16.dp),
          modifier = Modifier.fillMaxWidth()
        ) {
          Box(
            modifier = Modifier
              .size(100.dp)
              .clip(RoundedCornerShape(24.dp))
              .background(
                Brush.linearGradient(listOf(ZoxPurplePrimary, ZoxOrangeAccent))
              ),
            contentAlignment = Alignment.Center
          ) {
            ZoxLogoEmblem(size = 72.dp)
          }

          Text(
            text = "ZOX Pay UPI & Wallet",
            fontSize = 24.sp,
            fontWeight = FontWeight.Black,
            color = Color.White
          )

          Text(
            text = "Instant 0% transaction fee across all supermarkets and fuel stations in Mizoram.",
            fontSize = 14.sp,
            color = Color(0xFFA5A5C0),
            textAlign = androidx.compose.ui.text.style.TextAlign.Center,
            modifier = Modifier.padding(horizontal = 24.dp)
          )
        }

        Button(
          onClick = onDismiss,
          modifier = Modifier
            .fillMaxWidth()
            .height(52.dp),
          colors = ButtonDefaults.buttonColors(
            containerColor = ZoxPurplePrimary,
            contentColor = Color.White
          ),
          shape = RoundedCornerShape(14.dp)
        ) {
          Text(
            text = if (canClose) "CONTINUE TO SUPER APP" else "ADVERTISEMENT (${countdown}s)",
            fontSize = 14.sp,
            fontWeight = FontWeight.Bold
          )
        }
      }
    }
  }
}
