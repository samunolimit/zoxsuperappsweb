package com.example.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.border
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
import androidx.compose.material.icons.filled.Call
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.DirectionsCar
import androidx.compose.material.icons.filled.LocalShipping
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.TwoWheeler
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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.model.BookingItem
import com.example.model.BookingStatus
import com.example.model.BookingType
import com.example.ui.theme.ZoxDarkBackground
import com.example.ui.theme.ZoxDarkCard
import com.example.ui.theme.ZoxError
import com.example.ui.theme.ZoxOrangeAccent
import com.example.ui.theme.ZoxOrangeLight
import com.example.ui.theme.ZoxPurpleContainer
import com.example.ui.theme.ZoxPurplePrimary
import com.example.ui.theme.ZoxSuccess
import com.example.ui.theme.ZoxWarning

@Composable
fun BookingsScreen(
  bookings: List<BookingItem>,
  onNewBookingClick: () -> Unit
) {
  LazyColumn(
    modifier = Modifier
      .fillMaxSize()
      .background(ZoxDarkBackground)
      .testTag("bookings_screen"),
    contentPadding = PaddingValues(top = 16.dp, bottom = 90.dp, start = 16.dp, end = 16.dp),
    verticalArrangement = Arrangement.spacedBy(14.dp)
  ) {
    item {
      Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
      ) {
        Column {
          Text(
            text = "My Activity & Bookings",
            fontSize = 20.sp,
            fontWeight = FontWeight.Bold,
            color = Color.White
          )
          Text(
            text = "Real-time dispatch, active rides & completed errands",
            fontSize = 12.sp,
            color = Color(0xFFA0A0BA)
          )
        }
      }
    }

    if (bookings.isEmpty()) {
      item {
        Card(
          modifier = Modifier.fillMaxWidth(),
          shape = RoundedCornerShape(16.dp),
          colors = CardDefaults.cardColors(containerColor = ZoxDarkCard)
        ) {
          Column(
            modifier = Modifier
              .fillMaxWidth()
              .padding(32.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(10.dp)
          ) {
            Icon(Icons.Default.DirectionsCar, contentDescription = null, tint = Color(0xFF6A6882), modifier = Modifier.size(48.dp))
            Text(text = "No Active Bookings", fontSize = 16.sp, fontWeight = FontWeight.Bold, color = Color.White)
            Text(text = "Book a self-drive motor hire or dispatch a runner.", fontSize = 12.sp, color = Color(0xFFA0A0BA))
            Button(
              onClick = onNewBookingClick,
              colors = ButtonDefaults.buttonColors(containerColor = ZoxOrangeAccent, contentColor = Color.Black)
            ) {
              Text("EXPLORE SERVICES", fontWeight = FontWeight.Bold)
            }
          }
        }
      }
    } else {
      items(bookings) { booking ->
        BookingCard(booking = booking)
      }
    }
  }
}

@Composable
fun BookingCard(booking: BookingItem) {
  Card(
    modifier = Modifier
      .fillMaxWidth()
      .testTag("booking_card_${booking.id}"),
    shape = RoundedCornerShape(18.dp),
    colors = CardDefaults.cardColors(containerColor = ZoxDarkCard),
    border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFF383652))
  ) {
    Column(
      modifier = Modifier
        .fillMaxWidth()
        .padding(16.dp),
      verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
      // Top header with status badge
      Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
      ) {
        Row(
          verticalAlignment = Alignment.CenterVertically,
          horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
          Box(
            modifier = Modifier
              .size(36.dp)
              .clip(CircleShape)
              .background(if (booking.type == BookingType.MOTOR_HIRE) ZoxPurpleContainer else Color(0xFF382315)),
            contentAlignment = Alignment.Center
          ) {
            Icon(
              imageVector = if (booking.type == BookingType.MOTOR_HIRE) Icons.Default.TwoWheeler else Icons.Default.LocalShipping,
              contentDescription = null,
              tint = if (booking.type == BookingType.MOTOR_HIRE) ZoxPurplePrimary else ZoxOrangeAccent,
              modifier = Modifier.size(20.dp)
            )
          }
          Column {
            Text(text = booking.id, fontSize = 11.sp, fontWeight = FontWeight.Bold, color = Color(0xFFA0A0BA))
            Text(text = booking.title, fontSize = 13.sp, fontWeight = FontWeight.Bold, color = Color.White)
          }
        }

        // Status pill
        val (badgeBg, badgeText, badgeColor) = when (booking.status) {
          BookingStatus.ASSIGNED, BookingStatus.ON_THE_WAY -> Triple(Color(0xFF1E3A2B), "LIVE ACTIVE", ZoxSuccess)
          BookingStatus.COMPLETED -> Triple(Color(0xFF222036), "COMPLETED", Color(0xFFA0A0BA))
          BookingStatus.PENDING_DISPATCH -> Triple(Color(0xFF3E311B), "DISPATCHING", ZoxWarning)
          BookingStatus.CANCELLED -> Triple(Color(0xFF3E1B1B), "CANCELLED", ZoxError)
        }

        Box(
          modifier = Modifier
            .clip(RoundedCornerShape(6.dp))
            .background(badgeBg)
            .padding(horizontal = 8.dp, vertical = 4.dp)
        ) {
          Text(text = badgeText, fontSize = 9.sp, fontWeight = FontWeight.Black, color = badgeColor)
        }
      }

      // Locations
      Column(
        modifier = Modifier
          .fillMaxWidth()
          .clip(RoundedCornerShape(12.dp))
          .background(Color(0xFF181726))
          .padding(10.dp),
        verticalArrangement = Arrangement.spacedBy(6.dp)
      ) {
        Row(
          verticalAlignment = Alignment.CenterVertically,
          horizontalArrangement = Arrangement.spacedBy(6.dp)
        ) {
          Icon(Icons.Default.LocationOn, contentDescription = null, tint = ZoxOrangeAccent, modifier = Modifier.size(14.dp))
          Text(text = "Pickup: ${booking.pickupLocation}", fontSize = 11.sp, color = Color(0xFFDCDCE8))
        }
        if (booking.dropoffLocation.isNotEmpty() && booking.dropoffLocation != booking.pickupLocation) {
          Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(6.dp)
          ) {
            Icon(Icons.Default.LocationOn, contentDescription = null, tint = ZoxPurplePrimary, modifier = Modifier.size(14.dp))
            Text(text = "Drop: ${booking.dropoffLocation}", fontSize = 11.sp, color = Color(0xFFDCDCE8))
          }
        }
      }

      // Live Agent details & OTP
      if (booking.status == BookingStatus.ASSIGNED || booking.status == BookingStatus.ON_THE_WAY) {
        Row(
          modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(10.dp))
            .background(ZoxPurpleContainer)
            .padding(10.dp),
          horizontalArrangement = Arrangement.SpaceBetween,
          verticalAlignment = Alignment.CenterVertically
        ) {
          Column {
            Text(text = "Partner: ${booking.driverOrAgentName}", fontSize = 11.sp, fontWeight = FontWeight.Bold, color = Color.White)
            Text(text = "Phone: ${booking.driverPhone}", fontSize = 10.sp, color = ZoxOrangeLight)
          }

          Box(
            modifier = Modifier
              .clip(RoundedCornerShape(6.dp))
              .background(ZoxOrangeAccent)
              .padding(horizontal = 8.dp, vertical = 4.dp)
          ) {
            Text(text = "START OTP: ${booking.otpCode}", fontSize = 10.sp, fontWeight = FontWeight.Black, color = Color.Black)
          }
        }
      }

      // Bottom Fare & Date
      Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
      ) {
        Text(text = booking.formattedDate, fontSize = 11.sp, color = Color(0xFF7A7996))
        Text(text = "₹${booking.fare.toInt()}", fontSize = 16.sp, fontWeight = FontWeight.Black, color = ZoxSuccess)
      }
    }
  }
}
