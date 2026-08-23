package com.example.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountBalanceWallet
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.DirectionsCar
import androidx.compose.material.icons.filled.FileUpload
import androidx.compose.material.icons.filled.LocationCity
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.TwoWheeler
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableDoubleStateOf
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import com.example.model.FleetVehicle
import com.example.model.AppPlugin
import com.example.ui.theme.ZoxDarkCard
import com.example.ui.theme.ZoxDarkSurface
import com.example.ui.theme.ZoxOrangeAccent
import com.example.ui.theme.ZoxOrangeDark
import com.example.ui.theme.ZoxPurpleContainer
import com.example.ui.theme.ZoxPurplePrimary
import com.example.ui.theme.ZoxSuccess

/**
 * City & Location Selector Modal (Lunglei / Aizawl, Mizoram)
 */
@Composable
fun CityPickerModal(
  currentCity: String,
  onCitySelected: (String) -> Unit,
  onDismiss: () -> Unit
) {
  val cities = listOf(
    "Aizawl, Mizoram",
    "Lunglei, Mizoram",
    "Champhai, Mizoram",
    "Serchhip, Mizoram",
    "Kolasib, Mizoram",
    "Siaha, Mizoram"
  )

  Dialog(onDismissRequest = onDismiss) {
    Card(
      modifier = Modifier.fillMaxWidth(),
      shape = RoundedCornerShape(20.dp),
      colors = CardDefaults.cardColors(containerColor = ZoxDarkSurface),
      border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFF3E3B5C))
    ) {
      Column(
        modifier = Modifier
          .fillMaxWidth()
          .padding(20.dp),
        verticalArrangement = Arrangement.spacedBy(14.dp)
      ) {
        Row(
          modifier = Modifier.fillMaxWidth(),
          horizontalArrangement = Arrangement.SpaceBetween,
          verticalAlignment = Alignment.CenterVertically
        ) {
          Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(8.dp)
          ) {
            Icon(Icons.Default.LocationCity, contentDescription = null, tint = ZoxOrangeAccent)
            Text(
              text = "Select ZOX Region",
              fontSize = 17.sp,
              fontWeight = FontWeight.Bold,
              color = Color.White
            )
          }
          IconButton(onClick = onDismiss) {
            Icon(Icons.Default.Close, contentDescription = "Close", tint = Color.White)
          }
        }

        Text(
          text = "Select your operational town in Mizoram for live fleet and errand availability.",
          fontSize = 12.sp,
          color = Color(0xFFA5A5BA)
        )

        LazyColumn(verticalArrangement = Arrangement.spacedBy(8.dp)) {
          items(cities) { city ->
            val isSelected = city == currentCity
            Row(
              modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(12.dp))
                .background(if (isSelected) ZoxPurpleContainer else ZoxDarkCard)
                .border(
                  1.dp,
                  if (isSelected) ZoxOrangeAccent else Color(0xFF333148),
                  RoundedCornerShape(12.dp)
                )
                .clickable {
                  onCitySelected(city)
                  onDismiss()
                }
                .padding(horizontal = 14.dp, vertical = 12.dp),
              horizontalArrangement = Arrangement.SpaceBetween,
              verticalAlignment = Alignment.CenterVertically
            ) {
              Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(10.dp)
              ) {
                Icon(
                  imageVector = Icons.Default.LocationOn,
                  contentDescription = null,
                  tint = if (isSelected) ZoxOrangeAccent else Color(0xFF8888A0),
                  modifier = Modifier.size(18.dp)
                )
                Text(
                  text = city,
                  fontSize = 14.sp,
                  fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal,
                  color = Color.White
                )
              }
              if (isSelected) {
                Icon(
                  imageVector = Icons.Default.CheckCircle,
                  contentDescription = null,
                  tint = ZoxOrangeAccent,
                  modifier = Modifier.size(18.dp)
                )
              }
            }
          }
        }
      }
    }
  }
}

/**
 * Motor Hire Self-Drive Instant Booking Modal
 */
@Composable
fun MotorHireBookingModal(
  fleet: List<FleetVehicle>,
  onConfirmBooking: (vehicleName: String, hours: Int, fare: Double, pickup: String) -> Unit,
  onDismiss: () -> Unit
) {
  var selectedVehicle by remember { mutableStateOf(fleet.firstOrNull() ?: FleetVehicle("v1", "Hunter 350", "Cruiser", 120.0, 950.0, "Manual", "Petrol", "2 Seats", 4.9, 100)) }
  var rentalHours by remember { mutableIntStateOf(4) }
  var pickupLocation by remember { mutableStateOf("Chanmari Fleet Hub, Aizawl") }

  val totalFare = selectedVehicle.hourlyRate * rentalHours

  Dialog(onDismissRequest = onDismiss) {
    Card(
      modifier = Modifier
        .fillMaxWidth()
        .testTag("motor_hire_modal"),
      shape = RoundedCornerShape(22.dp),
      colors = CardDefaults.cardColors(containerColor = ZoxDarkSurface),
      border = androidx.compose.foundation.BorderStroke(1.dp, ZoxPurplePrimary)
    ) {
      Column(
        modifier = Modifier
          .fillMaxWidth()
          .padding(20.dp),
        verticalArrangement = Arrangement.spacedBy(14.dp)
      ) {
        Row(
          modifier = Modifier.fillMaxWidth(),
          horizontalArrangement = Arrangement.SpaceBetween,
          verticalAlignment = Alignment.CenterVertically
        ) {
          Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(8.dp)
          ) {
            Icon(Icons.Default.TwoWheeler, contentDescription = null, tint = ZoxOrangeAccent)
            Text(
              text = "Book Self-Drive Motor Hire",
              fontSize = 17.sp,
              fontWeight = FontWeight.Bold,
              color = Color.White
            )
          }
          IconButton(onClick = onDismiss) {
            Icon(Icons.Default.Close, contentDescription = "Close", tint = Color.White)
          }
        }

        // Vehicle selector row
        Text(text = "Select Vehicle:", fontSize = 12.sp, color = Color(0xFFA0A0BA))
        LazyColumn(modifier = Modifier.height(130.dp), verticalArrangement = Arrangement.spacedBy(6.dp)) {
          items(fleet) { vehicle ->
            val isSelected = vehicle.id == selectedVehicle.id
            Row(
              modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(10.dp))
                .background(if (isSelected) ZoxPurpleContainer else ZoxDarkCard)
                .border(
                  1.dp,
                  if (isSelected) ZoxOrangeAccent else Color(0xFF333148),
                  RoundedCornerShape(10.dp)
                )
                .clickable { selectedVehicle = vehicle }
                .padding(horizontal = 12.dp, vertical = 8.dp),
              horizontalArrangement = Arrangement.SpaceBetween,
              verticalAlignment = Alignment.CenterVertically
            ) {
              Column {
                Text(text = vehicle.name, fontSize = 13.sp, fontWeight = FontWeight.Bold, color = Color.White)
                Text(text = "${vehicle.category} • ${vehicle.transmission}", fontSize = 10.sp, color = Color(0xFFA5A5BA))
              }
              Text(text = "₹${vehicle.hourlyRate.toInt()}/hr", fontSize = 13.sp, fontWeight = FontWeight.Black, color = ZoxOrangeAccent)
            }
          }
        }

        // Duration selector
        Row(
          modifier = Modifier.fillMaxWidth(),
          horizontalArrangement = Arrangement.SpaceBetween,
          verticalAlignment = Alignment.CenterVertically
        ) {
          Text(text = "Rental Duration:", fontSize = 12.sp, color = Color(0xFFA0A0BA))
          Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
            listOf(2, 4, 8, 24).forEach { hours ->
              val isSel = rentalHours == hours
              Box(
                modifier = Modifier
                  .clip(RoundedCornerShape(8.dp))
                  .background(if (isSel) ZoxOrangeAccent else Color(0xFF262438))
                  .clickable { rentalHours = hours }
                  .padding(horizontal = 10.dp, vertical = 6.dp)
              ) {
                Text(
                  text = "${hours}h",
                  fontSize = 11.sp,
                  fontWeight = FontWeight.Bold,
                  color = if (isSel) Color.Black else Color.White
                )
              }
            }
          }
        }

        // Pickup Location input
        OutlinedTextField(
          value = pickupLocation,
          onValueChange = { pickupLocation = it },
          label = { Text("Pickup Hub / Delivery Point", color = Color(0xFFA0A0BA), fontSize = 11.sp) },
          singleLine = true,
          modifier = Modifier.fillMaxWidth(),
          colors = OutlinedTextFieldDefaults.colors(
            focusedBorderColor = ZoxOrangeAccent,
            unfocusedBorderColor = Color(0xFF454360),
            focusedTextColor = Color.White,
            unfocusedTextColor = Color.White
          ),
          shape = RoundedCornerShape(12.dp)
        )

        // Fare Breakdown Card
        Row(
          modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(12.dp))
            .background(Color(0xFF161522))
            .padding(12.dp),
          horizontalArrangement = Arrangement.SpaceBetween,
          verticalAlignment = Alignment.CenterVertically
        ) {
          Column {
            Text(text = "Total Payable Fare", fontSize = 11.sp, color = Color(0xFFA5A5BA))
            Text(text = "₹${totalFare.toInt()}", fontSize = 20.sp, fontWeight = FontWeight.Black, color = ZoxSuccess)
          }
          Text(text = "Zero Fuel Surcharge", fontSize = 10.sp, color = Color(0xFF7E7D9A))
        }

        Button(
          onClick = {
            onConfirmBooking(selectedVehicle.name, rentalHours, totalFare, pickupLocation)
          },
          modifier = Modifier
            .fillMaxWidth()
            .height(48.dp)
            .testTag("confirm_motor_hire_button"),
          colors = ButtonDefaults.buttonColors(containerColor = ZoxOrangeAccent, contentColor = Color.Black),
          shape = RoundedCornerShape(12.dp)
        ) {
          Text(text = "CONFIRM & DISPATCH MOTOR", fontSize = 13.sp, fontWeight = FontWeight.Black)
        }
      }
    }
  }
}

/**
 * Tirhkah / Runner On-Demand Delivery Errand Form Modal
 */
@Composable
fun TirhkahBookingModal(
  onConfirmBooking: (item: String, pickup: String, dropoff: String, fare: Double) -> Unit,
  onDismiss: () -> Unit
) {
  var itemDescription by remember { mutableStateOf("Urgent Medical Supplies & Lab Reports") }
  var pickupLocation by remember { mutableStateOf("Civil Hospital, Dawrpui, Aizawl") }
  var dropoffLocation by remember { mutableStateOf("Upper Khatla, Near Secretariat") }
  var isUrgent by remember { mutableStateOf(true) }

  val baseFare = 49.0
  val distanceEstimatedKm = 4.5
  val kmRate = 12.0
  val calculatedFare = baseFare + (distanceEstimatedKm * kmRate) + (if (isUrgent) 25.0 else 0.0)

  Dialog(onDismissRequest = onDismiss) {
    Card(
      modifier = Modifier
        .fillMaxWidth()
        .testTag("tirhkah_booking_modal"),
      shape = RoundedCornerShape(22.dp),
      colors = CardDefaults.cardColors(containerColor = ZoxDarkSurface),
      border = androidx.compose.foundation.BorderStroke(1.dp, ZoxOrangeAccent)
    ) {
      Column(
        modifier = Modifier
          .fillMaxWidth()
          .padding(20.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
      ) {
        Row(
          modifier = Modifier.fillMaxWidth(),
          horizontalArrangement = Arrangement.SpaceBetween,
          verticalAlignment = Alignment.CenterVertically
        ) {
          Column {
            Text(
              text = "Tirhkah / Runner Request",
              fontSize = 17.sp,
              fontWeight = FontWeight.Bold,
              color = Color.White
            )
            Text(
              text = "Fast on-demand errand runner across Mizoram",
              fontSize = 11.sp,
              color = ZoxOrangeAccent
            )
          }
          IconButton(onClick = onDismiss) {
            Icon(Icons.Default.Close, contentDescription = "Close", tint = Color.White)
          }
        }

        OutlinedTextField(
          value = itemDescription,
          onValueChange = { itemDescription = it },
          label = { Text("What do you want us to pick up / deliver?", fontSize = 11.sp, color = Color(0xFFA0A0BA)) },
          singleLine = true,
          modifier = Modifier.fillMaxWidth(),
          colors = OutlinedTextFieldDefaults.colors(
            focusedBorderColor = ZoxOrangeAccent,
            unfocusedBorderColor = Color(0xFF454360),
            focusedTextColor = Color.White,
            unfocusedTextColor = Color.White
          ),
          shape = RoundedCornerShape(12.dp)
        )

        OutlinedTextField(
          value = pickupLocation,
          onValueChange = { pickupLocation = it },
          label = { Text("Pickup Location & Contact", fontSize = 11.sp, color = Color(0xFFA0A0BA)) },
          singleLine = true,
          modifier = Modifier.fillMaxWidth(),
          colors = OutlinedTextFieldDefaults.colors(
            focusedBorderColor = ZoxOrangeAccent,
            unfocusedBorderColor = Color(0xFF454360),
            focusedTextColor = Color.White,
            unfocusedTextColor = Color.White
          ),
          shape = RoundedCornerShape(12.dp)
        )

        OutlinedTextField(
          value = dropoffLocation,
          onValueChange = { dropoffLocation = it },
          label = { Text("Drop-off Location & Contact", fontSize = 11.sp, color = Color(0xFFA0A0BA)) },
          singleLine = true,
          modifier = Modifier.fillMaxWidth(),
          colors = OutlinedTextFieldDefaults.colors(
            focusedBorderColor = ZoxOrangeAccent,
            unfocusedBorderColor = Color(0xFF454360),
            focusedTextColor = Color.White,
            unfocusedTextColor = Color.White
          ),
          shape = RoundedCornerShape(12.dp)
        )

        // Fare Estimate
        Row(
          modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(12.dp))
            .background(Color(0xFF171624))
            .padding(12.dp),
          horizontalArrangement = Arrangement.SpaceBetween,
          verticalAlignment = Alignment.CenterVertically
        ) {
          Column {
            Text(text = "Estimated Tirhkah Fare", fontSize = 11.sp, color = Color(0xFFA5A5BA))
            Text(text = "₹${calculatedFare.toInt()}", fontSize = 20.sp, fontWeight = FontWeight.Black, color = ZoxSuccess)
          }
          Text(text = "Avg Dispatch: 6 mins", fontSize = 11.sp, color = ZoxOrangeAccent)
        }

        Button(
          onClick = {
            onConfirmBooking(itemDescription, pickupLocation, dropoffLocation, calculatedFare)
          },
          modifier = Modifier
            .fillMaxWidth()
            .height(48.dp)
            .testTag("confirm_tirhkah_button"),
          colors = ButtonDefaults.buttonColors(containerColor = ZoxPurplePrimary, contentColor = Color.White),
          shape = RoundedCornerShape(12.dp)
        ) {
          Text(text = "DISPATCH NEAREST TIRHKAH RUNNER", fontSize = 12.sp, fontWeight = FontWeight.Black)
        }
      }
    }
  }
}

/**
 * Add Money to ZOX Wallet Modal via UPI / Razorpay Gateway
 */
@Composable
fun AddMoneyModal(
  onTopUpConfirmed: (amount: Double) -> Unit,
  onDismiss: () -> Unit
) {
  var amountString by remember { mutableStateOf("500") }

  Dialog(onDismissRequest = onDismiss) {
    Card(
      modifier = Modifier.fillMaxWidth(),
      shape = RoundedCornerShape(22.dp),
      colors = CardDefaults.cardColors(containerColor = ZoxDarkSurface),
      border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFF383556))
    ) {
      Column(
        modifier = Modifier
          .fillMaxWidth()
          .padding(20.dp),
        verticalArrangement = Arrangement.spacedBy(14.dp)
      ) {
        Row(
          modifier = Modifier.fillMaxWidth(),
          horizontalArrangement = Arrangement.SpaceBetween,
          verticalAlignment = Alignment.CenterVertically
        ) {
          Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(8.dp)
          ) {
            Icon(Icons.Default.AccountBalanceWallet, contentDescription = null, tint = ZoxOrangeAccent)
            Text(text = "Top-Up ZOX Wallet", fontSize = 17.sp, fontWeight = FontWeight.Bold, color = Color.White)
          }
          IconButton(onClick = onDismiss) {
            Icon(Icons.Default.Close, contentDescription = "Close", tint = Color.White)
          }
        }

        OutlinedTextField(
          value = amountString,
          onValueChange = { amountString = it.filter { ch -> ch.isDigit() } },
          label = { Text("Enter Amount (₹)", color = Color(0xFFA0A0BA)) },
          keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
          singleLine = true,
          modifier = Modifier.fillMaxWidth(),
          colors = OutlinedTextFieldDefaults.colors(
            focusedBorderColor = ZoxOrangeAccent,
            unfocusedBorderColor = Color(0xFF454360),
            focusedTextColor = Color.White,
            unfocusedTextColor = Color.White
          ),
          shape = RoundedCornerShape(12.dp)
        )

        // Quick amount chips
        Row(
          modifier = Modifier.fillMaxWidth(),
          horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
          listOf("200", "500", "1000", "2000").forEach { chip ->
            Box(
              modifier = Modifier
                .weight(1f)
                .clip(RoundedCornerShape(8.dp))
                .background(if (amountString == chip) ZoxPurpleContainer else Color(0xFF262438))
                .border(
                  1.dp,
                  if (amountString == chip) ZoxOrangeAccent else Color.Transparent,
                  RoundedCornerShape(8.dp)
                )
                .clickable { amountString = chip }
                .padding(vertical = 8.dp),
              contentAlignment = Alignment.Center
            ) {
              Text(
                text = "+₹$chip",
                fontSize = 12.sp,
                fontWeight = FontWeight.Bold,
                color = if (amountString == chip) ZoxOrangeAccent else Color.White
              )
            }
          }
        }

        Button(
          onClick = {
            val amount = amountString.toDoubleOrNull() ?: 500.0
            onTopUpConfirmed(amount)
          },
          modifier = Modifier
            .fillMaxWidth()
            .height(48.dp),
          colors = ButtonDefaults.buttonColors(containerColor = ZoxOrangeAccent, contentColor = Color.Black),
          shape = RoundedCornerShape(12.dp)
        ) {
          Text(text = "PROCEED VIA RAZORPAY UPI", fontSize = 13.sp, fontWeight = FontWeight.Black)
        }
      }
    }
  }
}

/**
 * Driving License KYC Document Upload Modal
 */
@Composable
fun OnboardingModal(
  onSave: (name: String, email: String, address: String, emergencyContact: String) -> Unit
) {
  var name by remember { mutableStateOf("") }
  var email by remember { mutableStateOf("") }
  var address by remember { mutableStateOf("") }
  var emergencyContact by remember { mutableStateOf("") }

  Dialog(onDismissRequest = { /* Cannot dismiss, mandatory */ }) {
    Card(
      modifier = Modifier.fillMaxWidth(),
      shape = RoundedCornerShape(22.dp),
      colors = CardDefaults.cardColors(containerColor = ZoxDarkSurface),
      border = androidx.compose.foundation.BorderStroke(1.dp, ZoxPurplePrimary)
    ) {
      LazyColumn(
        modifier = Modifier
          .fillMaxWidth()
          .padding(20.dp),
        verticalArrangement = Arrangement.spacedBy(14.dp)
      ) {
        item {
          Text(text = "Welcome to ZOX Super App", fontSize = 18.sp, fontWeight = FontWeight.Bold, color = Color.White)
          Text(
            text = "Please complete your profile to unlock all features. You will be automatically granted 'APPROVED CUSTOMER' status.",
            fontSize = 12.sp,
            color = Color(0xFFA0A0BA)
          )
        }

        item {
          OutlinedTextField(
            value = name,
            onValueChange = { name = it },
            label = { Text("Full Name", color = Color(0xFFA0A0BA)) },
            singleLine = true,
            modifier = Modifier.fillMaxWidth(),
            colors = OutlinedTextFieldDefaults.colors(
              focusedBorderColor = ZoxOrangeAccent,
              unfocusedBorderColor = Color(0xFF454360),
              focusedTextColor = Color.White,
              unfocusedTextColor = Color.White
            ),
            shape = RoundedCornerShape(12.dp)
          )
        }
        item {
          OutlinedTextField(
            value = email,
            onValueChange = { email = it },
            label = { Text("Email Address", color = Color(0xFFA0A0BA)) },
            singleLine = true,
            modifier = Modifier.fillMaxWidth(),
            colors = OutlinedTextFieldDefaults.colors(
              focusedBorderColor = ZoxOrangeAccent,
              unfocusedBorderColor = Color(0xFF454360),
              focusedTextColor = Color.White,
              unfocusedTextColor = Color.White
            ),
            shape = RoundedCornerShape(12.dp)
          )
        }
        item {
          OutlinedTextField(
            value = address,
            onValueChange = { address = it },
            label = { Text("Home Address (For Deliveries)", color = Color(0xFFA0A0BA)) },
            singleLine = true,
            modifier = Modifier.fillMaxWidth(),
            colors = OutlinedTextFieldDefaults.colors(
              focusedBorderColor = ZoxOrangeAccent,
              unfocusedBorderColor = Color(0xFF454360),
              focusedTextColor = Color.White,
              unfocusedTextColor = Color.White
            ),
            shape = RoundedCornerShape(12.dp)
          )
        }
        item {
          OutlinedTextField(
            value = emergencyContact,
            onValueChange = { emergencyContact = it },
            label = { Text("Emergency Contact Number", color = Color(0xFFA0A0BA)) },
            singleLine = true,
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Phone),
            modifier = Modifier.fillMaxWidth(),
            colors = OutlinedTextFieldDefaults.colors(
              focusedBorderColor = ZoxOrangeAccent,
              unfocusedBorderColor = Color(0xFF454360),
              focusedTextColor = Color.White,
              unfocusedTextColor = Color.White
            ),
            shape = RoundedCornerShape(12.dp)
          )
        }

        item {
          Button(
            onClick = { onSave(name, email, address, emergencyContact) },
            modifier = Modifier
              .fillMaxWidth()
              .height(48.dp),
            colors = ButtonDefaults.buttonColors(containerColor = ZoxOrangeAccent, contentColor = Color.Black),
            shape = RoundedCornerShape(12.dp),
            enabled = name.isNotBlank() && address.isNotBlank()
          ) {
            Text(text = "SAVE PROFILE & CONTINUE", fontSize = 12.sp, fontWeight = FontWeight.Bold)
          }
        }
      }
    }
  }
}

@Composable
fun KycUploadModal(
  onSubmitKyc: (docType: String, docNumber: String, requestedRole: com.example.model.UserRole, selfieUrl: String, vehicleDetails: String?) -> Unit,
  onDismiss: () -> Unit
) {
  var docType by remember { mutableStateOf("Driving License (Motor Hire Eligible)") }
  var docNumber by remember { mutableStateOf("MZ-01-2026-") }
  var requestedRole by remember { mutableStateOf(com.example.model.UserRole.DRIVER) }
  var isSelfieCaptured by remember { mutableStateOf(false) }
  var vehicleDetails by remember { mutableStateOf("Tata Nexon EV - MZ01 AB 1234") }

  Dialog(onDismissRequest = onDismiss) {
    Card(
      modifier = Modifier.fillMaxWidth(),
      shape = RoundedCornerShape(22.dp),
      colors = CardDefaults.cardColors(containerColor = ZoxDarkSurface),
      border = androidx.compose.foundation.BorderStroke(1.dp, ZoxPurplePrimary)
    ) {
      LazyColumn(
        modifier = Modifier
          .fillMaxWidth()
          .padding(20.dp),
        verticalArrangement = Arrangement.spacedBy(14.dp)
      ) {
        item {
          Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
          ) {
            Row(
              verticalAlignment = Alignment.CenterVertically,
              horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
              Icon(Icons.Default.FileUpload, contentDescription = null, tint = ZoxOrangeAccent)
              Text(text = "Role Upgrade & KYC", fontSize = 17.sp, fontWeight = FontWeight.Bold, color = Color.White)
            }
            IconButton(onClick = onDismiss) {
              Icon(Icons.Default.Close, contentDescription = "Close", tint = Color.White)
            }
          }
        }

        item {
          Text(
            text = "Request a Role Upgrade to become a Driver, Runner, or Partner. Identity verification is mandatory.",
            fontSize = 12.sp,
            color = Color(0xFFA0A0BA)
          )
        }

        item {
          // Role Selection (Mock Dropdown logic using buttons for simplicity)
          Text("Select Requested Role:", color = Color.White, fontSize = 12.sp)
          Row(horizontalArrangement = Arrangement.spacedBy(8.dp), modifier = Modifier.fillMaxWidth()) {
            val roles = listOf(com.example.model.UserRole.DRIVER, com.example.model.UserRole.TIRHKAH_RUNNER, com.example.model.UserRole.VEHICLE_OWNER, com.example.model.UserRole.COUNTER_STAFF)
            roles.forEach { role ->
              Button(
                onClick = { requestedRole = role },
                colors = ButtonDefaults.buttonColors(
                  containerColor = if (requestedRole == role) ZoxPurplePrimary else Color(0xFF2C2A44)
                ),
                shape = RoundedCornerShape(8.dp)
              ) {
                Text(role.name.replace("_", " "), fontSize = 10.sp)
              }
            }
          }
        }

        item {
          OutlinedTextField(
            value = docNumber,
            onValueChange = { docNumber = it },
            label = { Text("Govt ID / Driving License Number", color = Color(0xFFA0A0BA)) },
            singleLine = true,
            modifier = Modifier.fillMaxWidth(),
            colors = OutlinedTextFieldDefaults.colors(
              focusedBorderColor = ZoxOrangeAccent,
              unfocusedBorderColor = Color(0xFF454360),
              focusedTextColor = Color.White,
              unfocusedTextColor = Color.White
            ),
            shape = RoundedCornerShape(12.dp)
          )
        }
        
        item {
          if (requestedRole == com.example.model.UserRole.DRIVER) {
            OutlinedTextField(
              value = vehicleDetails,
              onValueChange = { vehicleDetails = it },
              label = { Text("Vehicle & Registration", color = Color(0xFFA0A0BA)) },
              singleLine = true,
              modifier = Modifier.fillMaxWidth(),
              colors = OutlinedTextFieldDefaults.colors(
                focusedBorderColor = ZoxOrangeAccent,
                unfocusedBorderColor = Color(0xFF454360),
                focusedTextColor = Color.White,
                unfocusedTextColor = Color.White
              ),
              shape = RoundedCornerShape(12.dp)
            )
          }
        }

        item {
          // Selfie Capture Mock
          Box(
            modifier = Modifier
              .fillMaxWidth()
              .height(100.dp)
              .clip(RoundedCornerShape(12.dp))
              .background(Color(0xFF161522))
              .border(1.dp, if (isSelfieCaptured) ZoxSuccess else Color(0xFF383556), RoundedCornerShape(12.dp))
              .clickable { isSelfieCaptured = true },
            contentAlignment = Alignment.Center
          ) {
            Column(horizontalAlignment = Alignment.CenterHorizontally, verticalArrangement = Arrangement.spacedBy(4.dp)) {
              Icon(
                if (isSelfieCaptured) Icons.Default.CheckCircle else Icons.Default.TwoWheeler, // Using TwoWheeler as placeholder for camera icon
                contentDescription = null,
                tint = if (isSelfieCaptured) ZoxSuccess else ZoxOrangeAccent,
                modifier = Modifier.size(24.dp)
              )
              Text(
                text = if (isSelfieCaptured) "Live Selfie Captured & Verified" else "Tap to Capture Live Selfie",
                fontSize = 11.sp,
                color = if (isSelfieCaptured) ZoxSuccess else Color(0xFFC0C0D4)
              )
            }
          }
        }

        item {
          Button(
            onClick = {
              onSubmitKyc(docType, docNumber, requestedRole, "url_to_selfie", vehicleDetails.takeIf { requestedRole == com.example.model.UserRole.DRIVER })
            },
            modifier = Modifier
              .fillMaxWidth()
              .height(48.dp),
            colors = ButtonDefaults.buttonColors(containerColor = ZoxPurplePrimary, contentColor = Color.White),
            shape = RoundedCornerShape(12.dp),
            enabled = docNumber.isNotBlank() && isSelfieCaptured
          ) {
            Text(text = "SUBMIT TO STAFF APPROVAL DESK", fontSize = 12.sp, fontWeight = FontWeight.Bold)
          }
        }
      }
    }
  }
}

@Composable
fun RouteVehicleEnrollmentModal(
  onEnroll: (com.example.model.RouteVehicle) -> Unit,
  onDismiss: () -> Unit
) {
  var regNumber by remember { mutableStateOf("") }
  var vehicleType by remember { mutableStateOf("Maxi Cab") }
  var routeName by remember { mutableStateOf("") }
  var ownerName by remember { mutableStateOf("") }
  var ownerPhone by remember { mutableStateOf("") }
  var driverName by remember { mutableStateOf("") }
  var driverPhone by remember { mutableStateOf("") }
  
  var insuranceExpiry by remember { mutableStateOf("") }
  var fcExpiry by remember { mutableStateOf("") }
  var permitExpiry by remember { mutableStateOf("") }
  var licenseExpiry by remember { mutableStateOf("") }

  Dialog(onDismissRequest = onDismiss) {
    Card(
      modifier = Modifier.fillMaxWidth().height(600.dp),
      shape = RoundedCornerShape(22.dp),
      colors = CardDefaults.cardColors(containerColor = ZoxDarkSurface),
      border = androidx.compose.foundation.BorderStroke(1.dp, ZoxPurplePrimary)
    ) {
      LazyColumn(
        modifier = Modifier.fillMaxWidth().padding(20.dp),
        verticalArrangement = Arrangement.spacedBy(14.dp)
      ) {
        item {
          Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
          ) {
            Row(
              verticalAlignment = Alignment.CenterVertically,
              horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
              Icon(Icons.Default.DirectionsCar, contentDescription = null, tint = ZoxOrangeAccent)
              Text(text = "Enroll Route Vehicle", fontSize = 17.sp, fontWeight = FontWeight.Bold, color = Color.White)
            }
            IconButton(onClick = onDismiss) {
              Icon(Icons.Default.Close, contentDescription = "Close", tint = Color.White)
            }
          }
        }

        item {
          OutlinedTextField(value = regNumber, onValueChange = { regNumber = it }, label = { Text("Registration Number (e.g. MZ-01-A-1234)", color = Color(0xFFA0A0BA)) }, singleLine = true, modifier = Modifier.fillMaxWidth(), colors = OutlinedTextFieldDefaults.colors(focusedBorderColor = ZoxOrangeAccent, unfocusedBorderColor = Color(0xFF454360), focusedTextColor = Color.White, unfocusedTextColor = Color.White), shape = RoundedCornerShape(12.dp))
        }
        item {
          OutlinedTextField(value = routeName, onValueChange = { routeName = it }, label = { Text("Route Name (e.g. Aizawl - Lunglei)", color = Color(0xFFA0A0BA)) }, singleLine = true, modifier = Modifier.fillMaxWidth(), colors = OutlinedTextFieldDefaults.colors(focusedBorderColor = ZoxOrangeAccent, unfocusedBorderColor = Color(0xFF454360), focusedTextColor = Color.White, unfocusedTextColor = Color.White), shape = RoundedCornerShape(12.dp))
        }
        item {
          OutlinedTextField(value = ownerName, onValueChange = { ownerName = it }, label = { Text("Owner Full Name", color = Color(0xFFA0A0BA)) }, singleLine = true, modifier = Modifier.fillMaxWidth(), colors = OutlinedTextFieldDefaults.colors(focusedBorderColor = ZoxOrangeAccent, unfocusedBorderColor = Color(0xFF454360), focusedTextColor = Color.White, unfocusedTextColor = Color.White), shape = RoundedCornerShape(12.dp))
        }
        item {
          OutlinedTextField(value = ownerPhone, onValueChange = { ownerPhone = it }, label = { Text("Owner Contact Phone", color = Color(0xFFA0A0BA)) }, singleLine = true, modifier = Modifier.fillMaxWidth(), colors = OutlinedTextFieldDefaults.colors(focusedBorderColor = ZoxOrangeAccent, unfocusedBorderColor = Color(0xFF454360), focusedTextColor = Color.White, unfocusedTextColor = Color.White), shape = RoundedCornerShape(12.dp))
        }
        item {
          OutlinedTextField(value = driverName, onValueChange = { driverName = it }, label = { Text("Designated Driver Name", color = Color(0xFFA0A0BA)) }, singleLine = true, modifier = Modifier.fillMaxWidth(), colors = OutlinedTextFieldDefaults.colors(focusedBorderColor = ZoxOrangeAccent, unfocusedBorderColor = Color(0xFF454360), focusedTextColor = Color.White, unfocusedTextColor = Color.White), shape = RoundedCornerShape(12.dp))
        }
        item {
          OutlinedTextField(value = driverPhone, onValueChange = { driverPhone = it }, label = { Text("Designated Driver Phone", color = Color(0xFFA0A0BA)) }, singleLine = true, modifier = Modifier.fillMaxWidth(), colors = OutlinedTextFieldDefaults.colors(focusedBorderColor = ZoxOrangeAccent, unfocusedBorderColor = Color(0xFF454360), focusedTextColor = Color.White, unfocusedTextColor = Color.White), shape = RoundedCornerShape(12.dp))
        }

        item {
          Text("Mandatory Documents Expiry Dates (YYYY-MM-DD):", fontSize = 12.sp, color = Color.White, fontWeight = FontWeight.Bold)
        }
        
        item {
          OutlinedTextField(value = insuranceExpiry, onValueChange = { insuranceExpiry = it }, label = { Text("Insurance Policy Expiry", color = Color(0xFFA0A0BA)) }, singleLine = true, modifier = Modifier.fillMaxWidth(), colors = OutlinedTextFieldDefaults.colors(focusedBorderColor = ZoxOrangeAccent, unfocusedBorderColor = Color(0xFF454360), focusedTextColor = Color.White, unfocusedTextColor = Color.White), shape = RoundedCornerShape(12.dp))
        }
        item {
          OutlinedTextField(value = fcExpiry, onValueChange = { fcExpiry = it }, label = { Text("Fitness Certificate (FC) Expiry", color = Color(0xFFA0A0BA)) }, singleLine = true, modifier = Modifier.fillMaxWidth(), colors = OutlinedTextFieldDefaults.colors(focusedBorderColor = ZoxOrangeAccent, unfocusedBorderColor = Color(0xFF454360), focusedTextColor = Color.White, unfocusedTextColor = Color.White), shape = RoundedCornerShape(12.dp))
        }
        item {
          OutlinedTextField(value = permitExpiry, onValueChange = { permitExpiry = it }, label = { Text("Route Permit Expiry", color = Color(0xFFA0A0BA)) }, singleLine = true, modifier = Modifier.fillMaxWidth(), colors = OutlinedTextFieldDefaults.colors(focusedBorderColor = ZoxOrangeAccent, unfocusedBorderColor = Color(0xFF454360), focusedTextColor = Color.White, unfocusedTextColor = Color.White), shape = RoundedCornerShape(12.dp))
        }
        item {
          OutlinedTextField(value = licenseExpiry, onValueChange = { licenseExpiry = it }, label = { Text("Commercial Driver License Expiry", color = Color(0xFFA0A0BA)) }, singleLine = true, modifier = Modifier.fillMaxWidth(), colors = OutlinedTextFieldDefaults.colors(focusedBorderColor = ZoxOrangeAccent, unfocusedBorderColor = Color(0xFF454360), focusedTextColor = Color.White, unfocusedTextColor = Color.White), shape = RoundedCornerShape(12.dp))
        }
        
        item {
          // Photo Mock
          Box(
            modifier = Modifier.fillMaxWidth().height(80.dp).clip(RoundedCornerShape(12.dp)).background(Color(0xFF161522)).border(1.dp, ZoxSuccess, RoundedCornerShape(12.dp)),
            contentAlignment = Alignment.Center
          ) {
            Row(horizontalArrangement = Arrangement.spacedBy(4.dp), verticalAlignment = Alignment.CenterVertically) {
              Icon(Icons.Default.CheckCircle, contentDescription = null, tint = ZoxSuccess, modifier = Modifier.size(20.dp))
              Text(text = "Vehicle Photo & RC Scan Attached", fontSize = 11.sp, color = ZoxSuccess)
            }
          }
        }

        item {
          Button(
            onClick = {
              onEnroll(
                com.example.model.RouteVehicle(
                  id = "rv_${System.currentTimeMillis().toString().takeLast(4)}",
                  regNumber = regNumber,
                  type = vehicleType,
                  routeName = routeName,
                  ownerName = ownerName,
                  ownerPhone = ownerPhone,
                  driverName = driverName,
                  driverPhone = driverPhone,
                  insuranceExpiry = insuranceExpiry.ifBlank { "2027-12-31" },
                  fcExpiry = fcExpiry.ifBlank { "2027-12-31" },
                  permitExpiry = permitExpiry.ifBlank { "2027-12-31" },
                  licenseExpiry = licenseExpiry.ifBlank { "2027-12-31" }
                )
              )
            },
            modifier = Modifier.fillMaxWidth().height(48.dp),
            colors = ButtonDefaults.buttonColors(containerColor = ZoxPurplePrimary, contentColor = Color.White),
            shape = RoundedCornerShape(12.dp),
            enabled = regNumber.isNotBlank() && routeName.isNotBlank()
          ) {
            Text(text = "ENROLL VEHICLE TO LIVE DESK", fontSize = 12.sp, fontWeight = FontWeight.Bold)
          }
        }
      }
    }
  }
}

@Composable
fun PluginServiceBookingModal(
  plugin: AppPlugin?,
  onConfirm: (pickup: String, destination: String, fare: Double) -> Unit,
  onDismiss: () -> Unit
) {
  var pickup by remember { mutableStateOf("") }
  var destination by remember { mutableStateOf("") }
  var fare by remember { mutableStateOf("") }

  Dialog(onDismissRequest = onDismiss) {
    Card(
      modifier = Modifier.fillMaxWidth(),
      shape = RoundedCornerShape(22.dp),
      colors = CardDefaults.cardColors(containerColor = ZoxDarkCard)
    ) {
      Column(
        modifier = Modifier.padding(20.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
      ) {
        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
          Column(modifier = Modifier.weight(1f)) {
            Text(text = plugin?.title ?: "ZOX Service", color = Color.White, fontSize = 18.sp, fontWeight = FontWeight.Bold)
            Text(text = plugin?.subtitle ?: "Create a service request", color = Color(0xFFA0A0BA), fontSize = 11.sp)
          }
          IconButton(onClick = onDismiss) { Icon(Icons.Default.Close, contentDescription = "Close", tint = Color.White) }
        }
        OutlinedTextField(value = pickup, onValueChange = { pickup = it }, label = { Text("Pickup / service location") }, modifier = Modifier.fillMaxWidth(), singleLine = true)
        OutlinedTextField(value = destination, onValueChange = { destination = it }, label = { Text("Destination / delivery details") }, modifier = Modifier.fillMaxWidth(), singleLine = true)
        OutlinedTextField(value = fare, onValueChange = { fare = it.filter { char -> char.isDigit() || char == '.' } }, label = { Text("Estimated fare") }, modifier = Modifier.fillMaxWidth(), singleLine = true, keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal))
        Button(onClick = { onConfirm(pickup, destination, fare.toDoubleOrNull() ?: 0.0) }, modifier = Modifier.fillMaxWidth(), enabled = pickup.isNotBlank() && destination.isNotBlank() && (fare.toDoubleOrNull() ?: 0.0) > 0, colors = ButtonDefaults.buttonColors(containerColor = ZoxOrangeAccent, contentColor = Color.Black), shape = RoundedCornerShape(12.dp)) {
          Text("SUBMIT REQUEST", fontWeight = FontWeight.Bold)
        }
      }
    }
  }
}
