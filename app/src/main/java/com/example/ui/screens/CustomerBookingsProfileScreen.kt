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
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.model.AppRole
import com.example.model.Booking
import com.example.model.BookingStatus
import com.example.model.GlobalCurrency
import com.example.model.GlobalLanguage
import com.example.ui.theme.*
import com.example.viewmodel.ServoraViewModel

@Composable
fun CustomerBookingsScreen(
    viewModel: ServoraViewModel,
    onOpenLiveTracking: (Booking) -> Unit,
    modifier: Modifier = Modifier
) {
    val bookings by viewModel.bookings.collectAsState()

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(SurfaceCanvas)
    ) {
        Surface(
            color = SurfaceCard,
            tonalElevation = 2.dp,
            border = BorderStroke(1.dp, SlateBorder)
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 20.dp, vertical = 16.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(
                    text = "My Service Bookings",
                    color = TextPrimary,
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Bold
                )

                Text(
                    text = "${bookings.size} Total Orders",
                    color = TextMuted,
                    fontSize = 12.sp
                )
            }
        }

        if (bookings.isEmpty()) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(32.dp),
                contentAlignment = Alignment.Center
            ) {
                Text("No service bookings yet.", color = TextMuted)
            }
        } else {
            LazyColumn(
                modifier = Modifier.fillMaxSize(),
                contentPadding = PaddingValues(16.dp),
                verticalArrangement = Arrangement.spacedBy(14.dp)
            ) {
                items(bookings) { booking ->
                    BookingListItemCard(
                        booking = booking,
                        formattedPrice = viewModel.formatPrice(booking.baseCostUsd),
                        onTrackClick = { onOpenLiveTracking(booking) },
                        onReviewClick = { viewModel.showReviewDialog.value = true }
                    )
                }
            }
        }
    }
}

@Composable
private fun BookingListItemCard(
    booking: Booking,
    formattedPrice: String,
    onTrackClick: () -> Unit,
    onReviewClick: () -> Unit
) {
    Card(
        shape = RoundedCornerShape(18.dp),
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
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Box(
                        modifier = Modifier
                            .size(8.dp)
                            .clip(CircleShape)
                            .background(
                                when (booking.status) {
                                    BookingStatus.COMPLETED -> EmeraldTrust
                                    BookingStatus.CANCELLED -> EmergencyRed
                                    else -> ElectricBlue
                                }
                            )
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                        text = booking.status.name.replace("_", " "),
                        color = when (booking.status) {
                            BookingStatus.COMPLETED -> EmeraldTrust
                            BookingStatus.CANCELLED -> EmergencyRed
                            else -> ElectricBlue
                        },
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold
                    )
                }

                Text(
                    text = booking.scheduledDate,
                    color = TextMuted,
                    fontSize = 11.sp
                )
            }

            Spacer(modifier = Modifier.height(10.dp))

            Text(
                text = booking.serviceCategoryName,
                color = TextPrimary,
                fontSize = 16.sp,
                fontWeight = FontWeight.Bold
            )

            Text(
                text = "Specialist: ${booking.professional.name} • ${booking.customerAddress}",
                color = TextSecondary,
                fontSize = 12.sp,
                modifier = Modifier.padding(top = 2.dp)
            )

            Spacer(modifier = Modifier.height(12.dp))
            HorizontalDivider(color = SlateBorder)
            Spacer(modifier = Modifier.height(10.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Text(text = "Escrow Total", color = TextMuted, fontSize = 10.sp)
                    Text(text = formattedPrice, color = TextPrimary, fontSize = 16.sp, fontWeight = FontWeight.Bold)
                }

                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    if (booking.status != BookingStatus.COMPLETED && booking.status != BookingStatus.CANCELLED) {
                        Button(
                            onClick = onTrackClick,
                            shape = RoundedCornerShape(10.dp),
                            colors = ButtonDefaults.buttonColors(containerColor = ElectricBlue),
                            contentPadding = PaddingValues(horizontal = 14.dp, vertical = 6.dp)
                        ) {
                            Icon(Icons.Default.Navigation, contentDescription = null, modifier = Modifier.size(14.dp))
                            Spacer(modifier = Modifier.width(4.dp))
                            Text("Track Live", fontSize = 12.sp, fontWeight = FontWeight.Bold)
                        }
                    } else if (booking.status == BookingStatus.COMPLETED) {
                        OutlinedButton(
                            onClick = onReviewClick,
                            shape = RoundedCornerShape(10.dp),
                            contentPadding = PaddingValues(horizontal = 14.dp, vertical = 6.dp)
                        ) {
                            Text("Rate & Review", fontSize = 12.sp)
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun CustomerProfileScreen(
    viewModel: ServoraViewModel,
    onOpenTrustSafety: () -> Unit,
    modifier: Modifier = Modifier
) {
    val currency by viewModel.selectedCurrency.collectAsState()
    val language by viewModel.selectedLanguage.collectAsState()
    val addresses by viewModel.userAddresses.collectAsState()

    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .background(SurfaceCanvas),
        contentPadding = PaddingValues(bottom = 90.dp)
    ) {
        // Top Profile Card
        item {
            Surface(
                color = DeepNavy,
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .statusBarsPadding()
                        .padding(20.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Box(
                        modifier = Modifier
                            .size(70.dp)
                            .clip(CircleShape)
                            .background(Brush.linearGradient(listOf(ElectricBlue, CyanAccent))),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = "GS",
                            color = Color.White,
                            fontSize = 24.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }

                    Spacer(modifier = Modifier.height(10.dp))

                    Text(
                        text = "Gabriel Sterling",
                        color = Color.White,
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold
                    )
                    Text(
                        text = "gabriel.sterling@example.com",
                        color = TextSubtle,
                        fontSize = 12.sp
                    )

                    Spacer(modifier = Modifier.height(12.dp))

                    Surface(
                        shape = RoundedCornerShape(16.dp),
                        color = SlateDark,
                        border = BorderStroke(1.dp, SlateBorderDark)
                    ) {
                        Row(
                            modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Icon(Icons.Default.Verified, contentDescription = null, tint = EmeraldTrust, modifier = Modifier.size(14.dp))
                            Spacer(modifier = Modifier.width(6.dp))
                            Text("Servora Verified Member", color = Color.White, fontSize = 11.sp, fontWeight = FontWeight.SemiBold)
                        }
                    }
                }
            }
        }

        // Wallet Card
        item {
            Box(modifier = Modifier.padding(16.dp)) {
                Card(
                    shape = RoundedCornerShape(18.dp),
                    colors = CardDefaults.cardColors(containerColor = SurfaceCard),
                    border = BorderStroke(1.dp, SlateBorder),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(16.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column {
                            Text("Servora Global Wallet", color = TextMuted, fontSize = 11.sp)
                            Text("$420.00", color = TextPrimary, fontSize = 22.sp, fontWeight = FontWeight.Bold)
                            Text("Protected balance for escrow bookings", color = EmeraldTrust, fontSize = 10.sp)
                        }

                        Button(
                            onClick = { /* Top up simulation */ },
                            shape = RoundedCornerShape(10.dp),
                            colors = ButtonDefaults.buttonColors(containerColor = ElectricBlue),
                            contentPadding = PaddingValues(horizontal = 14.dp, vertical = 6.dp)
                        ) {
                            Text("Top Up", fontSize = 12.sp)
                        }
                    }
                }
            }
        }

        // Global Preferences Section
        item {
            Column(modifier = Modifier.padding(horizontal = 16.dp)) {
                Text(
                    text = "Global App Preferences",
                    color = TextPrimary,
                    fontSize = 15.sp,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.padding(bottom = 8.dp)
                )

                ProfileSettingItem(
                    title = "Currency",
                    subtitle = "${currency.code} (${currency.symbol})",
                    icon = Icons.Default.AttachMoney,
                    onClick = { viewModel.showCurrencyDialog.value = true }
                )

                ProfileSettingItem(
                    title = "Language",
                    subtitle = "${language.name} (${language.localName})",
                    icon = Icons.Default.Language,
                    onClick = { viewModel.showLanguageDialog.value = true }
                )

                ProfileSettingItem(
                    title = "Service Addresses & Lokasi Servis",
                    subtitle = "${addresses.size} Alamat Tersimpan (Indo & US) • Tambah / Kelola",
                    icon = Icons.Default.LocationOn,
                    onClick = { viewModel.showLocationDialog.value = true }
                )
            }
        }

        // Trust, Security & Role Access Gateway
        item {
            Spacer(modifier = Modifier.height(16.dp))
            Column(modifier = Modifier.padding(horizontal = 16.dp)) {
                Text(
                    text = "Trust, Security & Access Control (RBAC)",
                    color = TextPrimary,
                    fontSize = 15.sp,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.padding(bottom = 8.dp)
                )

                ProfileSettingItem(
                    title = "Servora Trust & Safety Protocol",
                    subtitle = "Escrow protection, $1M insurance, criminal background check",
                    icon = Icons.Default.Shield,
                    onClick = onOpenTrustSafety
                )

                ProfileSettingItem(
                    title = "Role & Access Control Gateway",
                    subtitle = "Pemisahan hak akses: Customer, Tukang, Admin & Developer",
                    icon = Icons.Default.VpnKey,
                    onClick = { viewModel.showRoleSwitcherDialog.value = true }
                )

                ProfileSettingItem(
                    title = "Switch to Professional Mode",
                    subtitle = "Portal teknisi: terima order & ajukan penambahan biaya",
                    icon = Icons.Default.Engineering,
                    onClick = { viewModel.switchRole(AppRole.PROFESSIONAL) }
                )

                ProfileSettingItem(
                    title = "Platform Admin HQ (Restricted)",
                    subtitle = "Audit escrow, lisensi teknisi, sengketa (Perlu PIN 1122)",
                    icon = Icons.Default.AdminPanelSettings,
                    onClick = { viewModel.showRoleSwitcherDialog.value = true }
                )

                ProfileSettingItem(
                    title = "Developer System & Security Console",
                    subtitle = "Satu-satunya pengubah keamanan sistem & flags (Perlu PIN 9900)",
                    icon = Icons.Default.Terminal,
                    onClick = { viewModel.showRoleSwitcherDialog.value = true }
                )
            }
        }
    }
}

@Composable
private fun ProfileSettingItem(
    title: String,
    subtitle: String,
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    onClick: () -> Unit
) {
    Card(
        onClick = onClick,
        shape = RoundedCornerShape(14.dp),
        colors = CardDefaults.cardColors(containerColor = SurfaceCard),
        border = BorderStroke(1.dp, SlateBorder),
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp)
    ) {
        Row(
            modifier = Modifier.padding(14.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .size(36.dp)
                    .clip(RoundedCornerShape(10.dp))
                    .background(ElectricBlue.copy(alpha = 0.1f)),
                contentAlignment = Alignment.Center
            ) {
                Icon(imageVector = icon, contentDescription = null, tint = ElectricBlue, modifier = Modifier.size(18.dp))
            }
            Spacer(modifier = Modifier.width(12.dp))
            Column(modifier = Modifier.weight(1f)) {
                Text(text = title, color = TextPrimary, fontSize = 14.sp, fontWeight = FontWeight.SemiBold)
                Text(text = subtitle, color = TextMuted, fontSize = 11.sp)
            }
            Icon(Icons.Default.ChevronRight, contentDescription = null, tint = TextSubtle, modifier = Modifier.size(20.dp))
        }
    }
}
