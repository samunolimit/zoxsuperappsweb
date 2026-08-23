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
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountBalanceWallet
import androidx.compose.material.icons.filled.Build
import androidx.compose.material.icons.filled.DirectionsCar
import androidx.compose.material.icons.filled.Inventory2
import androidx.compose.material.icons.filled.Emergency
import androidx.compose.material.icons.filled.Handyman
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material.icons.filled.Language
import androidx.compose.material.icons.filled.LocalShipping
import androidx.compose.material.icons.filled.LocalTaxi
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.MonetizationOn
import androidx.compose.material.icons.filled.MedicalServices
import androidx.compose.material.icons.filled.ReceiptLong
import androidx.compose.material.icons.filled.Restaurant
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.ShoppingBag
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.filled.SupportAgent
import androidx.compose.material.icons.filled.Storefront
import androidx.compose.material.icons.filled.TwoWheeler
import androidx.compose.material.icons.filled.Videocam
import androidx.compose.material.icons.filled.VideoCall
import androidx.compose.material.icons.filled.WifiTethering
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
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
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.model.AppPlugin
import com.example.model.FleetVehicle
import com.example.model.MasterVaultConfig
import com.example.model.UserProfile
import com.example.model.WalletData
import com.example.ui.components.AdMobBannerView
import com.example.ui.components.AdMobNativeFeedCard
import com.example.ui.components.ZoxBrandHeader
import com.example.ui.components.ZoxLogoEmblem
import com.example.ui.theme.ZoxDarkBackground
import com.example.ui.theme.ZoxDarkCard
import com.example.ui.theme.ZoxDarkSurface
import com.example.ui.theme.ZoxOrangeAccent
import com.example.ui.theme.ZoxOrangeDark
import com.example.ui.theme.ZoxOrangeLight
import com.example.ui.theme.ZoxPurpleContainer
import com.example.ui.theme.ZoxPurplePrimary
import com.example.ui.theme.ZoxSuccess

@Composable
fun HomeScreen(
  userProfile: UserProfile,
  walletData: WalletData,
  selectedCity: String,
  plugins: List<AppPlugin>,
  vaultConfig: MasterVaultConfig,
  fleet: List<FleetVehicle>,
  onCityClick: () -> Unit,
  onWalletClick: () -> Unit,
  onMotorHireClick: () -> Unit,
  onTirhkahClick: () -> Unit,
  onVideoCallClick: () -> Unit,
  onWatchAdClick: () -> Unit,
  onPluginClick: (pluginId: String) -> Unit,
  onWebPortalClick: () -> Unit = {}
) {
  var searchQuery by remember { mutableStateOf("") }
  val activePlugins = plugins.filter { it.isInstalled && it.isEnabled }

  LazyColumn(
    modifier = Modifier
      .fillMaxSize()
      .background(ZoxDarkBackground)
      .testTag("home_screen"),
    contentPadding = PaddingValues(bottom = 90.dp),
    verticalArrangement = Arrangement.spacedBy(18.dp)
  ) {
    // 1. Dynamic Top Brand Header & Location Selector
    item {
      Column(
        modifier = Modifier
          .fillMaxWidth()
          .background(
            Brush.verticalGradient(
              listOf(Color(0xFF26193E), Color(0xFF1B172C), ZoxDarkBackground)
            )
          )
          .padding(horizontal = 16.dp, vertical = 14.dp),
        verticalArrangement = Arrangement.spacedBy(14.dp)
      ) {
        Row(
          modifier = Modifier.fillMaxWidth(),
          horizontalArrangement = Arrangement.SpaceBetween,
          verticalAlignment = Alignment.CenterVertically
        ) {
          // Brand Header with custom Vector Z Monogram
          ZoxBrandHeader(logoSize = 38.dp, showTagline = true)

          Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(8.dp)
          ) {
            // Web Portal Pill
            Row(
              modifier = Modifier
                .clip(RoundedCornerShape(20.dp))
                .background(Color(0xFF281C3E))
                .border(0.8.dp, ZoxPurplePrimary, RoundedCornerShape(20.dp))
                .clickable { onWebPortalClick() }
                .padding(horizontal = 9.dp, vertical = 6.dp)
                .testTag("home_web_portal_pill"),
              verticalAlignment = Alignment.CenterVertically,
              horizontalArrangement = Arrangement.spacedBy(4.dp)
            ) {
              Icon(
                imageVector = Icons.Default.WifiTethering,
                contentDescription = "Web Portal",
                tint = ZoxOrangeAccent,
                modifier = Modifier.size(13.dp)
              )
              Text(
                text = "Web Portal",
                fontSize = 11.sp,
                fontWeight = FontWeight.Bold,
                color = Color.White
              )
            }

            // Location Selector Pill
            Row(
              modifier = Modifier
                .clip(RoundedCornerShape(20.dp))
                .background(ZoxDarkSurface)
                .border(0.8.dp, Color(0xFF484568), RoundedCornerShape(20.dp))
                .clickable { onCityClick() }
                .padding(horizontal = 10.dp, vertical = 6.dp)
                .testTag("location_selector_pill"),
              verticalAlignment = Alignment.CenterVertically,
              horizontalArrangement = Arrangement.spacedBy(4.dp)
            ) {
              Icon(
                imageVector = Icons.Default.LocationOn,
                contentDescription = null,
                tint = ZoxOrangeAccent,
                modifier = Modifier.size(14.dp)
              )
              Text(
                text = selectedCity.substringBefore(","),
                fontSize = 11.sp,
                fontWeight = FontWeight.Bold,
                color = Color.White
              )
              Icon(
                imageVector = Icons.Default.KeyboardArrowDown,
                contentDescription = null,
                tint = Color(0xFFA5A5C0),
                modifier = Modifier.size(16.dp)
              )
            }
          }
        }

        // Search Bar
        OutlinedTextField(
          value = searchQuery,
          onValueChange = { searchQuery = it },
          placeholder = { Text("Search rides, tirhkah errands, food, bills...", fontSize = 12.sp, color = Color(0xFF787794)) },
          leadingIcon = {
            Icon(imageVector = Icons.Default.Search, contentDescription = null, tint = ZoxOrangeAccent, modifier = Modifier.size(18.dp))
          },
          singleLine = true,
          modifier = Modifier
            .fillMaxWidth()
            .height(48.dp),
          colors = OutlinedTextFieldDefaults.colors(
            focusedBorderColor = ZoxOrangeAccent,
            unfocusedBorderColor = Color(0xFF383550),
            focusedTextColor = Color.White,
            unfocusedTextColor = Color.White,
            focusedContainerColor = Color(0xFF161524),
            unfocusedContainerColor = Color(0xFF161524)
          ),
          shape = RoundedCornerShape(14.dp)
        )

        // Wallet & Rewards Quick Bar
        Row(
          modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(16.dp))
            .background(
              Brush.horizontalGradient(
                listOf(Color(0xFF2E1C4E), Color(0xFF1B1B2C))
              )
            )
            .border(1.dp, Color(0xFF453664), RoundedCornerShape(16.dp))
            .clickable { onWalletClick() }
            .padding(horizontal = 14.dp, vertical = 10.dp),
          horizontalArrangement = Arrangement.SpaceBetween,
          verticalAlignment = Alignment.CenterVertically
        ) {
          Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(10.dp)
          ) {
            Box(
              modifier = Modifier
                .size(36.dp)
                .clip(CircleShape)
                .background(ZoxOrangeAccent),
              contentAlignment = Alignment.Center
            ) {
              Icon(Icons.Default.AccountBalanceWallet, contentDescription = null, tint = Color.Black, modifier = Modifier.size(20.dp))
            }
            Column {
              Text(text = "ZOX Pay Wallet", fontSize = 11.sp, color = Color(0xFFA8A8C0))
              Text(
                text = "₹${String.format("%.2f", walletData.balanceInRupees)}",
                fontSize = 16.sp,
                fontWeight = FontWeight.Black,
                color = Color.White
              )
            }
          }

          Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(6.dp)
          ) {
            Box(
              modifier = Modifier
                .clip(RoundedCornerShape(8.dp))
                .background(Color(0xFF442D15))
                .border(0.8.dp, ZoxOrangeAccent, RoundedCornerShape(8.dp))
                .padding(horizontal = 8.dp, vertical = 4.dp)
            ) {
              Text(
                text = "🪙 ${walletData.rewardCoins} Coins",
                fontSize = 11.sp,
                fontWeight = FontWeight.Bold,
                color = ZoxOrangeLight
              )
            }
          }
        }
      }
    }

    // 2. Dynamic Plugin Grid (Instant Reflection of Super Admin Toggles)
    item {
      Column(
        modifier = Modifier
          .fillMaxWidth()
          .padding(horizontal = 16.dp),
        verticalArrangement = Arrangement.spacedBy(10.dp)
      ) {
        Row(
          modifier = Modifier.fillMaxWidth(),
          horizontalArrangement = Arrangement.SpaceBetween,
          verticalAlignment = Alignment.CenterVertically
        ) {
          Text(
            text = "Super App Services",
            fontSize = 16.sp,
            fontWeight = FontWeight.Bold,
            color = Color.White
          )
          Text(
            text = "${activePlugins.size} Active Plugins",
            fontSize = 11.sp,
            fontWeight = FontWeight.Medium,
            color = ZoxOrangeAccent
          )
        }

        // Render Dynamic Grid of Active Plugins
        val chunked = activePlugins.chunked(4)
        Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
          chunked.forEach { rowPlugins ->
            Row(
              modifier = Modifier.fillMaxWidth(),
              horizontalArrangement = Arrangement.SpaceBetween
            ) {
              rowPlugins.forEach { plugin ->
                PluginGridItem(
                  plugin = plugin,
                  onClick = {
                    when (plugin.id) {
                      "plugin_motor_hire" -> onMotorHireClick()
                      "plugin_tirhkah" -> onTirhkahClick()
                      "plugin_video_call" -> onVideoCallClick()
                      "plugin_admob" -> onWatchAdClick()
                      else -> onPluginClick(plugin.id)
                    }
                  },
                  modifier = Modifier.weight(1f)
                )
              }
              // Fill remaining space if row has less than 4 items
              if (rowPlugins.size < 4) {
                repeat(4 - rowPlugins.size) {
                  Spacer(modifier = Modifier.weight(1f))
                }
              }
            }
          }
        }
      }
    }

    // 3. Featured Hero: Motor Hire Fleet Carousel
    item {
      Column(
        modifier = Modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(10.dp)
      ) {
        Row(
          modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp),
          horizontalArrangement = Arrangement.SpaceBetween,
          verticalAlignment = Alignment.CenterVertically
        ) {
          Column {
            Text(
              text = "Motor Hire Fleet",
              fontSize = 16.sp,
              fontWeight = FontWeight.Bold,
              color = Color.White
            )
            Text(
              text = "Self-drive bikes, scooters & 4x4 SUVs in Mizoram",
              fontSize = 11.sp,
              color = Color(0xFFA0A0B8)
            )
          }

          Text(
            text = "Book Now",
            fontSize = 12.sp,
            fontWeight = FontWeight.Bold,
            color = ZoxOrangeAccent,
            modifier = Modifier.clickable { onMotorHireClick() }
          )
        }

        LazyRow(
          contentPadding = PaddingValues(horizontal = 16.dp),
          horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
          items(fleet) { vehicle ->
            FleetVehicleCard(
              vehicle = vehicle,
              onClick = { onMotorHireClick() }
            )
          }

          // Blended Native Feed Ad inside carousel
          if (vaultConfig.admob.isEnabled) {
            item {
              AdMobNativeFeedCard(adMobConfig = vaultConfig.admob)
            }
          }
        }
      }
    }

    // 4. Quick Tirhkah Runner Dispatch Card
    item {
      Card(
        modifier = Modifier
          .fillMaxWidth()
          .padding(horizontal = 16.dp)
          .testTag("tirhkah_quick_card"),
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(containerColor = ZoxDarkCard),
        border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFF3B3356))
      ) {
        Row(
          modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp),
          verticalAlignment = Alignment.CenterVertically,
          horizontalArrangement = Arrangement.SpaceBetween
        ) {
          Column(
            modifier = Modifier.weight(1f),
            verticalArrangement = Arrangement.spacedBy(4.dp)
          ) {
            Row(
              verticalAlignment = Alignment.CenterVertically,
              horizontalArrangement = Arrangement.spacedBy(6.dp)
            ) {
              Box(
                modifier = Modifier
                  .clip(RoundedCornerShape(4.dp))
                  .background(ZoxOrangeAccent)
                  .padding(horizontal = 6.dp, vertical = 2.dp)
              ) {
                Text(
                  text = "TIRHKAH",
                  fontSize = 9.sp,
                  fontWeight = FontWeight.Black,
                  color = Color.Black
                )
              }
              Text(
                text = "On-Demand Errand Runner",
                fontSize = 14.sp,
                fontWeight = FontWeight.Bold,
                color = Color.White
              )
            }
            Text(
              text = "Need someone to pick up groceries, food, or documents across town?",
              fontSize = 11.sp,
              color = Color(0xFFA0A0B8)
            )
          }

          Button(
            onClick = onTirhkahClick,
            colors = ButtonDefaults.buttonColors(
              containerColor = ZoxPurplePrimary,
              contentColor = Color.White
            ),
            shape = RoundedCornerShape(12.dp)
          ) {
            Text(text = "DISPATCH", fontSize = 11.sp, fontWeight = FontWeight.Bold)
          }
        }
      }
    }

    // 5. Live RTC Video Support Card
    item {
      Card(
        modifier = Modifier
          .fillMaxWidth()
          .padding(horizontal = 16.dp)
          .testTag("video_support_card"),
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(containerColor = Color(0xFF1B152A)),
        border = androidx.compose.foundation.BorderStroke(1.dp, ZoxPurplePrimary.copy(alpha = 0.6f))
      ) {
        Row(
          modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp),
          verticalAlignment = Alignment.CenterVertically,
          horizontalArrangement = Arrangement.SpaceBetween
        ) {
          Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(12.dp),
            modifier = Modifier.weight(1f)
          ) {
            Box(
              modifier = Modifier
                .size(44.dp)
                .clip(CircleShape)
                .background(Brush.linearGradient(listOf(ZoxPurplePrimary, ZoxOrangeAccent))),
              contentAlignment = Alignment.Center
            ) {
              Icon(Icons.Default.Videocam, contentDescription = null, tint = Color.White, modifier = Modifier.size(24.dp))
            }
            Column {
              Text(
                text = "Live RTC Video Consultation",
                fontSize = 13.sp,
                fontWeight = FontWeight.Bold,
                color = Color.White
              )
              Text(
                text = "Agora RTC Powered • Instant 1-on-1 Help",
                fontSize = 11.sp,
                color = ZoxOrangeLight
              )
            }
          }

          Button(
            onClick = onVideoCallClick,
            colors = ButtonDefaults.buttonColors(containerColor = ZoxOrangeAccent, contentColor = Color.Black),
            shape = RoundedCornerShape(10.dp)
          ) {
            Text(text = "CALL", fontSize = 11.sp, fontWeight = FontWeight.Black)
          }
        }
      }
    }

    // 6. Dynamic AdMob Banner Slot (Bottom)
    item {
      Box(modifier = Modifier.padding(horizontal = 16.dp)) {
        AdMobBannerView(adMobConfig = vaultConfig.admob)
      }
    }
  }
}

/**
 * Single dynamic plugin item in home grid
 */
@Composable
fun PluginGridItem(
  plugin: AppPlugin,
  onClick: () -> Unit,
  modifier: Modifier = Modifier
) {
  val iconVector: ImageVector = when (plugin.iconName) {
    "two_wheeler" -> Icons.Default.TwoWheeler
    "local_shipping" -> Icons.Default.LocalShipping
    "videocam" -> Icons.Default.Videocam
    "local_taxi" -> Icons.Default.LocalTaxi
    "restaurant" -> Icons.Default.Restaurant
    "shopping_bag" -> Icons.Default.ShoppingBag
    "storefront" -> Icons.Default.Storefront
    "inventory_2" -> Icons.Default.Inventory2
    "receipt_long" -> Icons.Default.ReceiptLong
    "monetization_on" -> Icons.Default.MonetizationOn
    "build" -> Icons.Default.Build
    "handyman" -> Icons.Default.Handyman
    "medical_services" -> Icons.Default.MedicalServices
    "emergency" -> Icons.Default.Emergency
    "support_agent" -> Icons.Default.SupportAgent
    else -> Icons.Default.DirectionsCar
  }

  Column(
    modifier = modifier
      .clickable { onClick() }
      .padding(4.dp),
    horizontalAlignment = Alignment.CenterHorizontally,
    verticalArrangement = Arrangement.spacedBy(6.dp)
  ) {
    Box(
      modifier = Modifier
        .size(64.dp)
        .clip(RoundedCornerShape(16.dp))
        .background(
          Brush.linearGradient(
            if (plugin.id == "plugin_motor_hire" || plugin.id == "plugin_tirhkah") {
              listOf(Color(0xFF381F54), Color(0xFF26193E))
            } else if (plugin.id == "plugin_video_call") {
              listOf(Color(0xFF421C34), Color(0xFF261424))
            } else {
              listOf(Color(0xFF252336), Color(0xFF1B1A2A))
            }
          )
        )
        .border(
          1.dp,
          if (plugin.badge != null) ZoxOrangeAccent.copy(alpha = 0.6f) else Color(0xFF383652),
          RoundedCornerShape(16.dp)
        ),
      contentAlignment = Alignment.Center
    ) {
      Icon(
        imageVector = iconVector,
        contentDescription = plugin.title,
        tint = if (plugin.badge != null) ZoxOrangeAccent else Color.White,
        modifier = Modifier.size(32.dp)
      )

      if (plugin.badge != null) {
        Box(
          modifier = Modifier
            .align(Alignment.TopEnd)
            .clip(RoundedCornerShape(bottomStart = 6.dp, topEnd = 16.dp))
            .background(ZoxOrangeAccent)
            .padding(horizontal = 4.dp, vertical = 1.dp)
        ) {
          Text(
            text = plugin.badge,
            fontSize = 7.sp,
            fontWeight = FontWeight.Black,
            color = Color.Black
          )
        }
      }
    }

    Text(
      text = plugin.title,
      fontSize = 11.sp,
      fontWeight = FontWeight.SemiBold,
      color = Color.White,
      maxLines = 1,
      overflow = TextOverflow.Ellipsis
    )
  }
}

/**
 * Fleet vehicle item card
 */
@Composable
fun FleetVehicleCard(
  vehicle: FleetVehicle,
  onClick: () -> Unit
) {
  Card(
    modifier = Modifier
      .width(220.dp)
      .height(180.dp)
      .clickable { onClick() }
      .testTag("fleet_card_${vehicle.id}"),
    shape = RoundedCornerShape(18.dp),
    colors = CardDefaults.cardColors(containerColor = ZoxDarkCard),
    border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFF36344E))
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
        if (vehicle.tag != null) {
          Box(
            modifier = Modifier
              .clip(RoundedCornerShape(4.dp))
              .background(ZoxPurplePrimary)
              .padding(horizontal = 6.dp, vertical = 2.dp)
          ) {
            Text(text = vehicle.tag, fontSize = 8.sp, fontWeight = FontWeight.Bold, color = Color.White)
          }
        } else {
          Text(text = vehicle.category, fontSize = 10.sp, color = Color(0xFFA0A0BA))
        }

        Row(
          verticalAlignment = Alignment.CenterVertically,
          horizontalArrangement = Arrangement.spacedBy(2.dp)
        ) {
          Icon(Icons.Default.Star, contentDescription = null, tint = Color(0xFFFFD54F), modifier = Modifier.size(14.dp))
          Text(text = "${vehicle.rating}", fontSize = 11.sp, fontWeight = FontWeight.Bold, color = Color.White)
        }
      }

      // Visual Icon Placeholder
      Box(
        modifier = Modifier
          .fillMaxWidth()
          .height(55.dp)
          .clip(RoundedCornerShape(10.dp))
          .background(Color(0xFF1C1A2B)),
        contentAlignment = Alignment.Center
      ) {
        Icon(
          imageVector = if (vehicle.category.contains("Bike") || vehicle.category.contains("Scooter")) Icons.Default.TwoWheeler else Icons.Default.DirectionsCar,
          contentDescription = null,
          tint = ZoxOrangeAccent,
          modifier = Modifier.size(36.dp)
        )
      }

      Column(verticalArrangement = Arrangement.spacedBy(2.dp)) {
        Text(
          text = vehicle.name,
          fontSize = 12.sp,
          fontWeight = FontWeight.Bold,
          color = Color.White,
          maxLines = 1,
          overflow = TextOverflow.Ellipsis
        )
        Text(
          text = "${vehicle.seats} • ${vehicle.fuelType}",
          fontSize = 10.sp,
          color = Color(0xFFA0A0BA)
        )
      }

      Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
      ) {
        Column {
          Text(text = "From", fontSize = 9.sp, color = Color(0xFF7C7B96))
          Text(
            text = "₹${vehicle.hourlyRate.toInt()}/hr",
            fontSize = 14.sp,
            fontWeight = FontWeight.Black,
            color = ZoxOrangeAccent
          )
        }

        Box(
          modifier = Modifier
            .clip(RoundedCornerShape(8.dp))
            .background(ZoxPurpleContainer)
            .padding(horizontal = 8.dp, vertical = 4.dp)
        ) {
          Text(text = "HIRE", fontSize = 10.sp, fontWeight = FontWeight.Bold, color = Color.White)
        }
      }
    }
  }
}
