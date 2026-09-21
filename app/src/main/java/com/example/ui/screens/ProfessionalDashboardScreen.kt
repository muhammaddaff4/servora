package com.example.ui.screens

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.model.AppRole
import com.example.model.Booking
import com.example.model.BookingStatus
import com.example.ui.theme.*
import com.example.viewmodel.ServoraViewModel

@Composable
fun ProfessionalDashboardScreen(
    viewModel: ServoraViewModel,
    onOpenJobTracking: (Booking) -> Unit,
    modifier: Modifier = Modifier
) {
    var isOnline by remember { mutableStateOf(true) }
    val bookings by viewModel.bookings.collectAsState()
    val todayEarnings by viewModel.proTodayEarnings.collectAsState()
    val monthEarnings by viewModel.proMonthEarnings.collectAsState()
    val acceptanceRate by viewModel.proAcceptanceRate.collectAsState()
    val responseRate by viewModel.proResponseRate.collectAsState()

    var showWithdrawDialog by remember { mutableStateOf(false) }

    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .background(SurfaceCanvas),
        contentPadding = PaddingValues(bottom = 90.dp)
    ) {
        // Top Pro Status Header
        item {
            Surface(
                color = DeepNavy,
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .statusBarsPadding()
                        .padding(20.dp)
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Text(
                                    text = "Alex Morgan",
                                    color = Color.White,
                                    fontSize = 20.sp,
                                    fontWeight = FontWeight.Bold
                                )
                                Spacer(modifier = Modifier.width(6.dp))
                                Icon(Icons.Default.CheckCircle, contentDescription = null, tint = EmeraldTrust, modifier = Modifier.size(16.dp))
                            }
                            Text(
                                text = "HVAC Master Specialist • Servora Elite",
                                color = TextSubtle,
                                fontSize = 12.sp
                            )
                        }

                        // Online / Offline Switch
                        Surface(
                            shape = RoundedCornerShape(20.dp),
                            color = if (isOnline) EmeraldTrust.copy(alpha = 0.2f) else SlateDark,
                            border = BorderStroke(1.dp, if (isOnline) EmeraldTrust else SlateBorderDark)
                        ) {
                            Row(
                                modifier = Modifier
                                    .clickable { isOnline = !isOnline }
                                    .padding(horizontal = 10.dp, vertical = 6.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Box(
                                    modifier = Modifier
                                        .size(8.dp)
                                        .clip(CircleShape)
                                        .background(if (isOnline) EmeraldTrust else TextSubtle)
                                )
                                Spacer(modifier = Modifier.width(6.dp))
                                Text(
                                    text = if (isOnline) "ONLINE" else "OFFLINE",
                                    color = if (isOnline) EmeraldTrust else TextSubtle,
                                    fontSize = 11.sp,
                                    fontWeight = FontWeight.Bold
                                )
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(18.dp))

                    // Earnings Banner
                    Card(
                        shape = RoundedCornerShape(16.dp),
                        colors = CardDefaults.cardColors(containerColor = SlateDark),
                        border = BorderStroke(1.dp, SlateBorderDark),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Column(modifier = Modifier.padding(16.dp)) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Column {
                                    Text("Available Escrow Balance", color = TextSubtle, fontSize = 11.sp)
                                    Text(
                                        text = viewModel.formatPrice(todayEarnings),
                                        color = Color.White,
                                        fontSize = 24.sp,
                                        fontWeight = FontWeight.ExtraBold
                                    )
                                    Text(
                                        text = "Month total: ${viewModel.formatPrice(monthEarnings)}",
                                        color = CyanAccent,
                                        fontSize = 11.sp,
                                        fontWeight = FontWeight.SemiBold
                                    )
                                }

                                Button(
                                    onClick = { showWithdrawDialog = true },
                                    shape = RoundedCornerShape(10.dp),
                                    colors = ButtonDefaults.buttonColors(containerColor = EmeraldTrust)
                                ) {
                                    Text("Payout", fontWeight = FontWeight.Bold, fontSize = 12.sp)
                                }
                            }
                        }
                    }
                }
            }
        }

        // Performance KPI Metrics Row
        item {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                ProKpiCard(
                    title = "Acceptance",
                    value = "${acceptanceRate.toInt()}%",
                    icon = Icons.Default.Check,
                    modifier = Modifier.weight(1f)
                )
                ProKpiCard(
                    title = "Response",
                    value = "${responseRate.toInt()}%",
                    icon = Icons.Default.FlashOn,
                    modifier = Modifier.weight(1f)
                )
                ProKpiCard(
                    title = "Rating",
                    value = "4.95 ★",
                    icon = Icons.Default.Star,
                    modifier = Modifier.weight(1f)
                )
            }
        }

        // Incoming / Active Jobs Section
        item {
            Column(modifier = Modifier.padding(horizontal = 16.dp)) {
                Text(
                    text = "Active & Incoming Job Dispatches",
                    color = TextPrimary,
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold
                )
                Text(
                    text = "Manage dispatch status and customer parts approval",
                    color = TextMuted,
                    fontSize = 12.sp
                )
                Spacer(modifier = Modifier.height(10.dp))
            }
        }

        items(bookings) { b ->
            Box(modifier = Modifier.padding(horizontal = 16.dp, vertical = 6.dp)) {
                ProJobCard(
                    booking = b,
                    formattedPrice = viewModel.formatPrice(b.baseCostUsd),
                    onManageClick = { onOpenJobTracking(b) },
                    onAdvanceStatus = { viewModel.advanceTrackingStatus() },
                    onRequestExtra = { viewModel.simulateProAdditionalWorkRequest() }
                )
            }
        }

        // Switch to Customer Mode CTA
        item {
            Spacer(modifier = Modifier.height(20.dp))
            Box(modifier = Modifier.padding(horizontal = 16.dp)) {
                OutlinedButton(
                    onClick = { viewModel.switchRole(AppRole.CUSTOMER) },
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(12.dp),
                    border = BorderStroke(1.dp, ElectricBlue)
                ) {
                    Icon(Icons.Default.SwapHoriz, contentDescription = null, tint = ElectricBlue)
                    Spacer(modifier = Modifier.width(8.dp))
                    Text("Switch Back to Customer View", color = ElectricBlue, fontWeight = FontWeight.Bold)
                }
            }
        }
    }

    if (showWithdrawDialog) {
        AlertDialog(
            onDismissRequest = { showWithdrawDialog = false },
            title = { Text("Request Instant Payout", fontWeight = FontWeight.Bold) },
            text = {
                Text(
                    "Transfer available balance of ${viewModel.formatPrice(todayEarnings)} directly to your linked verified bank account via Stripe Instant Payout."
                )
            },
            confirmButton = {
                Button(
                    onClick = {
                        showWithdrawDialog = false
                        viewModel.showWithdrawSuccessDialog.value = true
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = EmeraldTrust)
                ) {
                    Text("Confirm Transfer", color = Color.White)
                }
            },
            dismissButton = {
                TextButton(onClick = { showWithdrawDialog = false }) {
                    Text("Cancel", color = TextSecondary)
                }
            }
        )
    }
}

@Composable
private fun ProKpiCard(
    title: String,
    value: String,
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    modifier: Modifier = Modifier
) {
    Card(
        shape = RoundedCornerShape(14.dp),
        colors = CardDefaults.cardColors(containerColor = SurfaceCard),
        border = BorderStroke(1.dp, SlateBorder),
        modifier = modifier
    ) {
        Column(
            modifier = Modifier.padding(12.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Icon(imageVector = icon, contentDescription = null, tint = ElectricBlue, modifier = Modifier.size(18.dp))
            Spacer(modifier = Modifier.height(4.dp))
            Text(text = value, color = TextPrimary, fontSize = 16.sp, fontWeight = FontWeight.Bold)
            Text(text = title, color = TextMuted, fontSize = 10.sp)
        }
    }
}

@Composable
private fun ProJobCard(
    booking: Booking,
    formattedPrice: String,
    onManageClick: () -> Unit,
    onAdvanceStatus: () -> Unit,
    onRequestExtra: () -> Unit
) {
    Card(
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = SurfaceCard),
        border = BorderStroke(1.dp, SlateBorder),
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Surface(
                    shape = RoundedCornerShape(6.dp),
                    color = ElectricBlue.copy(alpha = 0.15f)
                ) {
                    Text(
                        text = booking.status.name.replace("_", " "),
                        color = ElectricBlue,
                        fontSize = 10.sp,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                    )
                }

                Text(
                    text = "Customer: Gabriel S.",
                    color = TextSecondary,
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Medium
                )
            }

            Spacer(modifier = Modifier.height(8.dp))

            Text(text = booking.serviceCategoryName, color = TextPrimary, fontSize = 15.sp, fontWeight = FontWeight.Bold)
            Text(text = "Location: ${booking.customerAddress}", color = TextMuted, fontSize = 12.sp)

            Spacer(modifier = Modifier.height(10.dp))
            HorizontalDivider(color = SlateBorder)
            Spacer(modifier = Modifier.height(10.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Text("Payout in Escrow", color = TextMuted, fontSize = 10.sp)
                    Text(formattedPrice, color = TextPrimary, fontSize = 16.sp, fontWeight = FontWeight.Bold)
                }

                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    OutlinedButton(
                        onClick = onManageClick,
                        shape = RoundedCornerShape(10.dp),
                        contentPadding = PaddingValues(horizontal = 12.dp, vertical = 4.dp)
                    ) {
                        Text("Live Map", fontSize = 11.sp)
                    }

                    Button(
                        onClick = onAdvanceStatus,
                        shape = RoundedCornerShape(10.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = ElectricBlue),
                        contentPadding = PaddingValues(horizontal = 12.dp, vertical = 4.dp)
                    ) {
                        Text("Advance", fontSize = 11.sp, fontWeight = FontWeight.Bold)
                    }
                }
            }
        }
    }
}
