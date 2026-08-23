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
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountBalanceWallet
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.CardGiftcard
import androidx.compose.material.icons.filled.Download
import androidx.compose.material.icons.filled.MonetizationOn
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.filled.SwapHoriz
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.model.TransactionItem
import com.example.model.TransactionType
import com.example.model.WalletData
import com.example.ui.theme.ZoxDarkBackground
import com.example.ui.theme.ZoxDarkCard
import com.example.ui.theme.ZoxError
import com.example.ui.theme.ZoxOrangeAccent
import com.example.ui.theme.ZoxOrangeLight
import com.example.ui.theme.ZoxPurpleContainer
import com.example.ui.theme.ZoxPurplePrimary
import com.example.ui.theme.ZoxSuccess

@Composable
fun WalletScreen(
  wallet: WalletData,
  transactions: List<TransactionItem>,
  isAdMobEnabled: Boolean,
  onTopUpClick: () -> Unit,
  onWatchRewardedAdClick: () -> Unit
) {
  LazyColumn(
    modifier = Modifier
      .fillMaxSize()
      .background(ZoxDarkBackground)
      .testTag("wallet_screen"),
    contentPadding = PaddingValues(top = 16.dp, bottom = 90.dp, start = 16.dp, end = 16.dp),
    verticalArrangement = Arrangement.spacedBy(16.dp)
  ) {
    item {
      Text(
        text = "ZOX Pay & Wallet Hub",
        fontSize = 20.sp,
        fontWeight = FontWeight.Bold,
        color = Color.White
      )
    }

    // 1. Hero Balance Card with Gradient
    item {
      Card(
        modifier = Modifier
          .fillMaxWidth()
          .testTag("wallet_balance_card"),
        shape = RoundedCornerShape(24.dp),
        colors = CardDefaults.cardColors(containerColor = Color.Transparent),
        border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFF4A386D))
      ) {
        Box(
          modifier = Modifier
            .fillMaxWidth()
            .background(
              Brush.linearGradient(
                listOf(Color(0xFF371E5E), Color(0xFF22163A), Color(0xFF161528))
              )
            )
            .padding(20.dp)
        ) {
          Column(verticalArrangement = Arrangement.spacedBy(16.dp)) {
            Row(
              modifier = Modifier.fillMaxWidth(),
              horizontalArrangement = Arrangement.SpaceBetween,
              verticalAlignment = Alignment.CenterVertically
            ) {
              Column {
                Text(text = "Total Available Balance", fontSize = 12.sp, color = Color(0xFFAFAFC7))
                Text(
                  text = "₹${String.format("%.2f", wallet.balanceInRupees)}",
                  fontSize = 30.sp,
                  fontWeight = FontWeight.Black,
                  color = Color.White
                )
              }

              Box(
                modifier = Modifier
                  .size(48.dp)
                  .clip(CircleShape)
                  .background(ZoxOrangeAccent),
                contentAlignment = Alignment.Center
              ) {
                Icon(
                  imageVector = Icons.Default.AccountBalanceWallet,
                  contentDescription = null,
                  tint = Color.Black,
                  modifier = Modifier.size(28.dp)
                )
              }
            }

            // Reward Coins Pill & Loyalty Tier
            Row(
              modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(12.dp))
                .background(Color(0xFF1D1B2D))
                .padding(horizontal = 14.dp, vertical = 10.dp),
              horizontalArrangement = Arrangement.SpaceBetween,
              verticalAlignment = Alignment.CenterVertically
            ) {
              Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(8.dp)
              ) {
                Text(text = "🪙", fontSize = 16.sp)
                Column {
                  Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                    Text(text = "ZOX Loyalty Coins", fontSize = 11.sp, color = Color(0xFFA0A0BA))
                    Box(
                      modifier = Modifier
                        .clip(RoundedCornerShape(4.dp))
                        .background(
                          when(wallet.loyaltyTier.name) {
                            "BRONZE" -> Color(0xFFCD7F32)
                            "SILVER" -> Color(0xFFC0C0C0)
                            "GOLD" -> Color(0xFFFFD700)
                            "DIAMOND" -> Color(0xFFb9f2ff)
                            else -> ZoxPurplePrimary
                          }
                        )
                        .padding(horizontal = 4.dp, vertical = 2.dp)
                    ) {
                      Text(
                        text = wallet.loyaltyTier.badgeName,
                        fontSize = 8.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color.Black
                      )
                    }
                  }
                  Text(text = "${wallet.rewardCoins} Coins", fontSize = 14.sp, fontWeight = FontWeight.Bold, color = ZoxOrangeLight)
                }
              }

              Text(
                text = "100 Coins = ₹10 Cashback",
                fontSize = 10.sp,
                color = Color(0xFF7A7995)
              )
            }

            // Action Buttons
            Row(
              modifier = Modifier.fillMaxWidth(),
              horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
              Button(
                onClick = onTopUpClick,
                modifier = Modifier
                  .weight(1f)
                  .height(44.dp)
                  .testTag("top_up_button"),
                colors = ButtonDefaults.buttonColors(containerColor = ZoxOrangeAccent, contentColor = Color.Black),
                shape = RoundedCornerShape(12.dp)
              ) {
                Icon(Icons.Default.Add, contentDescription = null, modifier = Modifier.size(18.dp))
                Spacer(modifier = Modifier.size(4.dp))
                Text("ADD MONEY", fontSize = 11.sp, fontWeight = FontWeight.Black)
              }

              Button(
                onClick = { /* Simulated Send UPI */ },
                modifier = Modifier
                  .weight(1f)
                  .height(44.dp),
                colors = ButtonDefaults.buttonColors(containerColor = ZoxPurplePrimary, contentColor = Color.White),
                shape = RoundedCornerShape(12.dp)
              ) {
                Icon(Icons.Default.SwapHoriz, contentDescription = null, modifier = Modifier.size(18.dp))
                Spacer(modifier = Modifier.size(4.dp))
                Text("SEND UPI", fontSize = 11.sp, fontWeight = FontWeight.Bold)
              }
            }
          }
        }
      }
    }

    // 2. AdMob Rewarded Video Monetization Card
    if (isAdMobEnabled) {
      item {
        Card(
          modifier = Modifier
            .fillMaxWidth()
            .testTag("watch_rewarded_ad_card"),
          shape = RoundedCornerShape(18.dp),
          colors = CardDefaults.cardColors(containerColor = Color(0xFF221735)),
          border = androidx.compose.foundation.BorderStroke(1.dp, ZoxOrangeAccent.copy(alpha = 0.5f))
        ) {
          Row(
            modifier = Modifier
              .fillMaxWidth()
              .padding(16.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
          ) {
            Row(
              verticalAlignment = Alignment.CenterVertically,
              horizontalArrangement = Arrangement.spacedBy(12.dp),
              modifier = Modifier.weight(1f)
            ) {
              Box(
                modifier = Modifier
                  .size(42.dp)
                  .clip(CircleShape)
                  .background(ZoxOrangeAccent),
                contentAlignment = Alignment.Center
              ) {
                Icon(Icons.Default.PlayArrow, contentDescription = null, tint = Color.Black)
              }
              Column {
                Text(
                  text = "Watch Ad & Earn +50 Coins",
                  fontSize = 13.sp,
                  fontWeight = FontWeight.Bold,
                  color = Color.White
                )
                Text(
                  text = "AdMob Rewarded Video Engine (Instant Credit)",
                  fontSize = 10.sp,
                  color = ZoxOrangeLight
                )
              }
            }

            Button(
              onClick = onWatchRewardedAdClick,
              colors = ButtonDefaults.buttonColors(containerColor = ZoxOrangeAccent, contentColor = Color.Black),
              shape = RoundedCornerShape(10.dp)
            ) {
              Text("WATCH", fontSize = 11.sp, fontWeight = FontWeight.Black)
            }
          }
        }
      }
    }

    // 3. Transactions Ledger
    item {
      Text(
        text = "Recent Transactions",
        fontSize = 16.sp,
        fontWeight = FontWeight.Bold,
        color = Color.White
      )
    }

    items(transactions) { tx ->
      Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(14.dp),
        colors = CardDefaults.cardColors(containerColor = ZoxDarkCard),
        border = androidx.compose.foundation.BorderStroke(0.8.dp, Color(0xFF33314A))
      ) {
        Row(
          modifier = Modifier
            .fillMaxWidth()
            .padding(14.dp),
          horizontalArrangement = Arrangement.SpaceBetween,
          verticalAlignment = Alignment.CenterVertically
        ) {
          Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(10.dp),
            modifier = Modifier.weight(1f)
          ) {
            Box(
              modifier = Modifier
                .size(38.dp)
                .clip(CircleShape)
                .background(
                  when (tx.type) {
                    TransactionType.CREDIT -> Color(0xFF183B28)
                    TransactionType.DEBIT -> Color(0xFF3B1824)
                    TransactionType.REWARD -> Color(0xFF3B2D18)
                  }
                ),
              contentAlignment = Alignment.Center
            ) {
              Icon(
                imageVector = when (tx.type) {
                  TransactionType.CREDIT -> Icons.Default.Download
                  TransactionType.DEBIT -> Icons.Default.SwapHoriz
                  TransactionType.REWARD -> Icons.Default.CardGiftcard
                },
                contentDescription = null,
                tint = when (tx.type) {
                  TransactionType.CREDIT -> ZoxSuccess
                  TransactionType.DEBIT -> ZoxError
                  TransactionType.REWARD -> ZoxOrangeAccent
                },
                modifier = Modifier.size(20.dp)
              )
            }
            Column {
              Text(text = tx.title, fontSize = 13.sp, fontWeight = FontWeight.Bold, color = Color.White)
              Text(text = "${tx.date} • ${tx.paymentMethod}", fontSize = 10.sp, color = Color(0xFFA0A0BA))
            }
          }

          Column(horizontalAlignment = Alignment.End) {
            val prefix = when (tx.type) {
              TransactionType.CREDIT -> "+₹"
              TransactionType.DEBIT -> "-₹"
              TransactionType.REWARD -> "+🪙"
            }
            val color = when (tx.type) {
              TransactionType.CREDIT -> ZoxSuccess
              TransactionType.DEBIT -> Color.White
              TransactionType.REWARD -> ZoxOrangeAccent
            }
            Text(
              text = "$prefix${tx.amount.toInt()}",
              fontSize = 14.sp,
              fontWeight = FontWeight.Black,
              color = color
            )
            Text(text = tx.status, fontSize = 9.sp, color = Color(0xFF7A7996))
          }
        }
      }
    }
  }
}
