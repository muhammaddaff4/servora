package com.example.ui.screens

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.model.*
import com.example.ui.components.RatingStarsBar
import com.example.ui.components.VerifiedProBadge
import com.example.ui.theme.*
import com.example.viewmodel.BookingWizardState
import com.example.viewmodel.ServoraViewModel

@Composable
fun BookingFlowScreen(
    viewModel: ServoraViewModel,
    onBack: () -> Unit,
    onOpenLiveTracking: (Booking) -> Unit,
    modifier: Modifier = Modifier
) {
    val state by viewModel.bookingWizard.collectAsState()

    val stepTitles = listOf(
        "Problem Details",
        "Select Professional",
        "Schedule Service",
        "Service Address",
        "Price Estimate",
        "Secure Escrow Payment",
        "Confirmed"
    )

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(SurfaceCanvas)
            .statusBarsPadding()
    ) {
        // Top Step Progress Indicator
        Surface(
            color = SurfaceCard,
            tonalElevation = 2.dp,
            border = BorderStroke(1.dp, SlateBorder)
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    if (state.step > 1 && state.step < 7) {
                        IconButton(
                            onClick = { viewModel.updateBookingWizard { copy(step = step - 1) } },
                            modifier = Modifier.size(32.dp)
                        ) {
                            Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Previous", tint = TextPrimary)
                        }
                    } else if (state.step == 1) {
                        IconButton(onClick = onBack, modifier = Modifier.size(32.dp)) {
                            Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back", tint = TextPrimary)
                        }
                    }

                    Spacer(modifier = Modifier.width(8.dp))

                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            text = "Step ${state.step} of 7: ${stepTitles.getOrElse(state.step - 1) { "" }}",
                            color = TextPrimary,
                            fontSize = 15.sp,
                            fontWeight = FontWeight.Bold
                        )
                        LinearProgressIndicator(
                            progress = { state.step / 7f },
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(top = 6.dp)
                                .height(4.dp)
                                .clip(RoundedCornerShape(2.dp)),
                            color = ElectricBlue,
                            trackColor = SurfaceMuted,
                        )
                    }
                }
            }
        }

        // Body per Step
        Box(
            modifier = Modifier
                .weight(1f)
                .fillMaxWidth()
        ) {
            when (state.step) {
                1 -> StepOneProblem(state, viewModel)
                2 -> StepTwoProfessional(state, viewModel)
                3 -> StepThreeSchedule(state, viewModel)
                4 -> StepFourLocation(state, viewModel)
                5 -> StepFiveEstimate(state, viewModel)
                6 -> StepSixPayment(state, viewModel)
                7 -> StepSevenConfirmed(state, onOpenLiveTracking)
            }
        }

        // Bottom CTA button (for steps 1-6)
        if (state.step < 7) {
            Surface(
                modifier = Modifier
                    .fillMaxWidth()
                    .navigationBarsPadding(),
                color = SurfaceCard,
                border = BorderStroke(1.dp, SlateBorder)
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    if (state.step == 6) {
                        Button(
                            onClick = { viewModel.confirmBooking() },
                            enabled = !state.isSubmitting,
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(52.dp)
                                .testTag("authorize_payment_button"),
                            shape = RoundedCornerShape(12.dp),
                            colors = ButtonDefaults.buttonColors(containerColor = ElectricBlue)
                        ) {
                            if (state.isSubmitting) {
                                CircularProgressIndicator(color = Color.White, modifier = Modifier.size(20.dp))
                            } else {
                                Icon(Icons.Default.Lock, contentDescription = null, tint = Color.White, modifier = Modifier.size(16.dp))
                                Spacer(modifier = Modifier.width(8.dp))
                                Text("Authorize Escrow Payment", color = Color.White, fontWeight = FontWeight.Bold, fontSize = 15.sp)
                            }
                        }
                    } else {
                        Button(
                            onClick = { viewModel.updateBookingWizard { copy(step = step + 1) } },
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(50.dp)
                                .testTag("continue_step_button"),
                            shape = RoundedCornerShape(12.dp),
                            colors = ButtonDefaults.buttonColors(containerColor = ElectricBlue)
                        ) {
                            Text("Continue", color = Color.White, fontWeight = FontWeight.Bold, fontSize = 15.sp)
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun StepOneProblem(state: BookingWizardState, viewModel: ServoraViewModel) {
    val urgencies = listOf("Flexible", "Standard", "Urgent (2 hours)", "Emergency (Immediate)")

    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        contentPadding = PaddingValues(20.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        item {
            Text(
                text = "What do you need help with?",
                color = TextPrimary,
                fontSize = 20.sp,
                fontWeight = FontWeight.Bold
            )
            Text(
                text = "Explain the issue in detail so the technician brings the exact parts.",
                color = TextMuted,
                fontSize = 13.sp
            )
        }

        item {
            Text(text = "Problem Description", color = TextPrimary, fontSize = 14.sp, fontWeight = FontWeight.SemiBold)
            Spacer(modifier = Modifier.height(6.dp))
            OutlinedTextField(
                value = state.problemDescription,
                onValueChange = { desc -> viewModel.updateBookingWizard { copy(problemDescription = desc) } },
                placeholder = { Text("E.g. Unit makes a buzzing sound when turned on, no cool air...", color = TextSubtle, fontSize = 13.sp) },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(110.dp)
                    .testTag("problem_description_input"),
                shape = RoundedCornerShape(12.dp),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = ElectricBlue,
                    unfocusedBorderColor = SlateBorder,
                    focusedContainerColor = SurfaceCard,
                    unfocusedContainerColor = SurfaceCard
                )
            )
        }

        item {
            Text(text = "Upload Diagnostic Media", color = TextPrimary, fontSize = 14.sp, fontWeight = FontWeight.SemiBold)
            Spacer(modifier = Modifier.height(6.dp))
            Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                Surface(
                    shape = RoundedCornerShape(12.dp),
                    color = SurfaceCard,
                    border = BorderStroke(1.dp, SlateBorder),
                    modifier = Modifier
                        .weight(1f)
                        .height(80.dp)
                        .clickable { /* Attach photo simulation */ }
                ) {
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.Center
                    ) {
                        Icon(Icons.Default.AddPhotoAlternate, contentDescription = null, tint = ElectricBlue)
                        Spacer(modifier = Modifier.height(4.dp))
                        Text("Add Photos", fontSize = 12.sp, color = TextSecondary)
                    }
                }

                Surface(
                    shape = RoundedCornerShape(12.dp),
                    color = SurfaceCard,
                    border = BorderStroke(1.dp, SlateBorder),
                    modifier = Modifier
                        .weight(1f)
                        .height(80.dp)
                        .clickable { /* Attach video simulation */ }
                ) {
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.Center
                    ) {
                        Icon(Icons.Default.Videocam, contentDescription = null, tint = CyanGlow)
                        Spacer(modifier = Modifier.height(4.dp))
                        Text("Add Video", fontSize = 12.sp, color = TextSecondary)
                    }
                }
            }
        }

        item {
            Text(text = "Service Urgency", color = TextPrimary, fontSize = 14.sp, fontWeight = FontWeight.SemiBold)
            Spacer(modifier = Modifier.height(6.dp))
            urgencies.forEach { urg ->
                Card(
                    onClick = { viewModel.updateBookingWizard { copy(urgency = urg) } },
                    shape = RoundedCornerShape(12.dp),
                    colors = CardDefaults.cardColors(containerColor = SurfaceCard),
                    border = BorderStroke(1.dp, if (state.urgency == urg) ElectricBlue else SlateBorder),
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 4.dp)
                ) {
                    Row(
                        modifier = Modifier.padding(14.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        RadioButton(
                            selected = state.urgency == urg,
                            onClick = { viewModel.updateBookingWizard { copy(urgency = urg) } },
                            colors = RadioButtonDefaults.colors(selectedColor = ElectricBlue)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(text = urg, color = TextPrimary, fontSize = 14.sp, fontWeight = FontWeight.Medium)
                    }
                }
            }
        }
    }
}

@Composable
private fun StepTwoProfessional(state: BookingWizardState, viewModel: ServoraViewModel) {
    val pros = viewModel.professionals

    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        contentPadding = PaddingValues(20.dp),
        verticalArrangement = Arrangement.spacedBy(14.dp)
    ) {
        item {
            Text(
                text = "Select Specialist",
                color = TextPrimary,
                fontSize = 20.sp,
                fontWeight = FontWeight.Bold
            )
            Text(
                text = "Choose your preferred verified professional for this trade.",
                color = TextMuted,
                fontSize = 13.sp
            )
        }

        items(pros.take(4)) { pro ->
            val isSelected = state.selectedPro?.id == pro.id
            Card(
                onClick = { viewModel.updateBookingWizard { copy(selectedPro = pro) } },
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = SurfaceCard),
                border = BorderStroke(2.dp, if (isSelected) ElectricBlue else SlateBorder),
                modifier = Modifier.fillMaxWidth()
            ) {
                Row(
                    modifier = Modifier.padding(16.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    RadioButton(
                        selected = isSelected,
                        onClick = { viewModel.updateBookingWizard { copy(selectedPro = pro) } },
                        colors = RadioButtonDefaults.colors(selectedColor = ElectricBlue)
                    )

                    Spacer(modifier = Modifier.width(8.dp))

                    Column(modifier = Modifier.weight(1f)) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Text(text = pro.name, color = TextPrimary, fontSize = 15.sp, fontWeight = FontWeight.Bold)
                            Spacer(modifier = Modifier.width(6.dp))
                            VerifiedProBadge()
                        }
                        Text(text = pro.title, color = TextSecondary, fontSize = 12.sp)
                        Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.padding(top = 2.dp)) {
                            RatingStarsBar(rating = pro.rating, size = 12)
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(text = "• ${pro.distanceKm} km • ${pro.availability}", color = TextMuted, fontSize = 11.sp)
                        }
                    }

                    Text(
                        text = viewModel.formatPrice(pro.startingPriceUsd),
                        color = TextPrimary,
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Bold
                    )
                }
            }
        }
    }
}

@Composable
private fun StepThreeSchedule(state: BookingWizardState, viewModel: ServoraViewModel) {
    val dates = listOf("Today", "Tomorrow", "In 2 Days", "Select Date")
    val timeSlots = listOf(
        "08:00 AM - 10:00 AM",
        "10:00 AM - 12:00 PM",
        "01:00 PM - 03:00 PM",
        "03:00 PM - 05:00 PM",
        "05:00 PM - 07:00 PM"
    )

    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        contentPadding = PaddingValues(20.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        item {
            Text(text = "Preferred Date", color = TextPrimary, fontSize = 18.sp, fontWeight = FontWeight.Bold)
            Spacer(modifier = Modifier.height(8.dp))
            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                dates.forEach { d ->
                    FilterChip(
                        selected = state.preferredDate == d,
                        onClick = { viewModel.updateBookingWizard { copy(preferredDate = d) } },
                        label = { Text(d, fontSize = 12.sp) },
                        colors = FilterChipDefaults.filterChipColors(
                            selectedContainerColor = ElectricBlue,
                            selectedLabelColor = Color.White
                        )
                    )
                }
            }
        }

        item {
            Text(text = "Preferred Arrival Window", color = TextPrimary, fontSize = 18.sp, fontWeight = FontWeight.Bold)
            Spacer(modifier = Modifier.height(8.dp))
            timeSlots.forEach { slot ->
                Card(
                    onClick = { viewModel.updateBookingWizard { copy(preferredTime = slot) } },
                    shape = RoundedCornerShape(12.dp),
                    colors = CardDefaults.cardColors(containerColor = SurfaceCard),
                    border = BorderStroke(1.dp, if (state.preferredTime == slot) ElectricBlue else SlateBorder),
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 4.dp)
                ) {
                    Row(
                        modifier = Modifier.padding(14.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        RadioButton(
                            selected = state.preferredTime == slot,
                            onClick = { viewModel.updateBookingWizard { copy(preferredTime = slot) } },
                            colors = RadioButtonDefaults.colors(selectedColor = ElectricBlue)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(text = slot, color = TextPrimary, fontSize = 14.sp)
                    }
                }
            }
        }
    }
}

@Composable
private fun StepFourLocation(state: BookingWizardState, viewModel: ServoraViewModel) {
    val addresses by viewModel.userAddresses.collectAsState()

    val quickCitiesIndonesia = listOf("Jakarta Selatan", "Jakarta Pusat", "Surabaya", "Bandung", "Bali (Canggu)")
    val quickCitiesUS = listOf("New York (Manhattan)", "San Francisco (CA)", "Los Angeles (CA)")

    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        contentPadding = PaddingValues(20.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        item {
            Text(text = "Pilih / Tambah Alamat Servis", color = TextPrimary, fontSize = 20.sp, fontWeight = FontWeight.Bold)
            Text(text = "Tukang/teknisi akan segera dikirim ke titik lokasi ini.", color = TextMuted, fontSize = 13.sp)
        }

        // Action button to Add New Address
        item {
            Button(
                onClick = { viewModel.showAddAddressDialog.value = true },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(48.dp),
                shape = RoundedCornerShape(12.dp),
                colors = ButtonDefaults.buttonColors(containerColor = ElectricBlue)
            ) {
                Icon(Icons.Default.AddLocationAlt, contentDescription = null, modifier = Modifier.size(20.dp))
                Spacer(modifier = Modifier.width(8.dp))
                Text("Tambah Lokasi / Alamat Baru", fontWeight = FontWeight.Bold, fontSize = 14.sp)
            }
        }

        item {
            Text(text = "Alamat Tersimpan Anda (${addresses.size})", color = TextSecondary, fontSize = 13.sp, fontWeight = FontWeight.Bold)
        }

        items(addresses) { addr ->
            val formatted = "${addr.streetAddress}, ${addr.city}"
            val isSelected = state.locationAddress.contains(addr.streetAddress.take(15)) || state.locationAddress == formatted

            Card(
                onClick = { viewModel.updateBookingWizard { copy(locationAddress = formatted) } },
                shape = RoundedCornerShape(14.dp),
                colors = CardDefaults.cardColors(containerColor = if (isSelected) SurfaceElevated else SurfaceCard),
                border = BorderStroke(1.dp, if (isSelected) ElectricBlue else SlateBorder),
                modifier = Modifier.fillMaxWidth()
            ) {
                Row(
                    modifier = Modifier.padding(16.dp),
                    verticalAlignment = Alignment.Top
                ) {
                    Icon(
                        imageVector = if (addr.title.contains("Kantor", true)) Icons.Default.Business else Icons.Default.Home,
                        contentDescription = null,
                        tint = if (isSelected) ElectricBlue else TextSubtle,
                        modifier = Modifier.padding(top = 2.dp)
                    )
                    Spacer(modifier = Modifier.width(12.dp))
                    Column(modifier = Modifier.weight(1f)) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Text(text = addr.title, color = TextPrimary, fontSize = 14.sp, fontWeight = FontWeight.Bold)
                            Spacer(modifier = Modifier.width(8.dp))
                            Surface(
                                shape = RoundedCornerShape(4.dp),
                                color = if (addr.country == "Indonesia") CoralRed.copy(alpha = 0.12f) else ElectricBlue.copy(alpha = 0.12f)
                            ) {
                                Text(
                                    text = if (addr.country == "Indonesia") "🇮🇩 Indonesia" else "🇺🇸 ${addr.country}",
                                    color = if (addr.country == "Indonesia") CoralRed else ElectricBlue,
                                    fontSize = 10.sp,
                                    fontWeight = FontWeight.SemiBold,
                                    modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                                )
                            }
                            if (addr.isDefault) {
                                Spacer(modifier = Modifier.width(4.dp))
                                Surface(
                                    shape = RoundedCornerShape(4.dp),
                                    color = EmeraldTrust.copy(alpha = 0.12f)
                                ) {
                                    Text("Default", color = EmeraldTrust, fontSize = 10.sp, fontWeight = FontWeight.SemiBold, modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp))
                                }
                            }
                        }
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(text = addr.streetAddress, color = TextPrimary, fontSize = 13.sp)
                        Text(text = "${addr.city}, ${addr.stateProvince} ${addr.postalCode}", color = TextSecondary, fontSize = 12.sp)

                        if (addr.technicianNotes.isNotBlank()) {
                            Spacer(modifier = Modifier.height(6.dp))
                            Surface(
                                shape = RoundedCornerShape(6.dp),
                                color = SlateDark.copy(alpha = 0.5f),
                                border = BorderStroke(1.dp, SlateBorderDark)
                            ) {
                                Row(
                                    modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Icon(Icons.Default.Info, contentDescription = null, tint = AmberWarning, modifier = Modifier.size(12.dp))
                                    Spacer(modifier = Modifier.width(6.dp))
                                    Text("Petunjuk: ${addr.technicianNotes}", color = TextSubtle, fontSize = 11.sp)
                                }
                            }
                        }
                    }

                    RadioButton(
                        selected = isSelected,
                        onClick = { viewModel.updateBookingWizard { copy(locationAddress = formatted) } },
                        colors = RadioButtonDefaults.colors(selectedColor = ElectricBlue)
                    )
                }
            }
        }

        // Quick City Selector
        item {
            Spacer(modifier = Modifier.height(4.dp))
            Text("Atau Pilih Cepat Wilayah / Kota", color = TextSecondary, fontSize = 13.sp, fontWeight = FontWeight.Bold)
            Spacer(modifier = Modifier.height(8.dp))
            Text("Kota Layanan Indonesia 🇮🇩", color = TextMuted, fontSize = 11.sp, fontWeight = FontWeight.SemiBold)
            Spacer(modifier = Modifier.height(4.dp))
            LazyRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                items(quickCitiesIndonesia) { city ->
                    FilterChip(
                        selected = state.locationAddress.contains(city),
                        onClick = { viewModel.updateBookingWizard { copy(locationAddress = "$city Area") } },
                        label = { Text(city, fontSize = 12.sp) }
                    )
                }
            }
            Spacer(modifier = Modifier.height(8.dp))
            Text("United States Cities 🇺🇸", color = TextMuted, fontSize = 11.sp, fontWeight = FontWeight.SemiBold)
            Spacer(modifier = Modifier.height(4.dp))
            LazyRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                items(quickCitiesUS) { city ->
                    FilterChip(
                        selected = state.locationAddress.contains(city),
                        onClick = { viewModel.updateBookingWizard { copy(locationAddress = "$city Area") } },
                        label = { Text(city, fontSize = 12.sp) }
                    )
                }
            }
        }
    }
}

@Composable
private fun StepFiveEstimate(state: BookingWizardState, viewModel: ServoraViewModel) {
    val pro = state.selectedPro ?: viewModel.professionals.first()
    val starting = pro.startingPriceUsd
    val maxEst = starting * 1.55

    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        contentPadding = PaddingValues(20.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        item {
            Text(text = "Transparent Price Estimate", color = TextPrimary, fontSize = 20.sp, fontWeight = FontWeight.Bold)
            Text(text = "Servora guarantees no surprise hidden fees.", color = TextMuted, fontSize = 13.sp)
        }

        item {
            Card(
                shape = RoundedCornerShape(18.dp),
                colors = CardDefaults.cardColors(containerColor = DeepNavy),
                border = BorderStroke(1.dp, SlateBorderDark),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.padding(20.dp)) {
                    Text(text = "ESTIMATED SERVICE COST", color = TextSubtle, fontSize = 11.sp, fontWeight = FontWeight.Bold)
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = "${viewModel.formatPrice(starting)} – ${viewModel.formatPrice(maxEst)}",
                        color = CyanAccent,
                        fontSize = 28.sp,
                        fontWeight = FontWeight.ExtraBold
                    )
                    Spacer(modifier = Modifier.height(10.dp))
                    Text(
                        text = "Final price confirmed after on-site diagnostic inspection. If additional parts are needed, technician will submit a digital quote inside the app for your approval before proceeding.",
                        color = TextSubtle,
                        fontSize = 12.sp,
                        lineHeight = 18.sp
                    )
                }
            }
        }

        item {
            Card(
                shape = RoundedCornerShape(14.dp),
                colors = CardDefaults.cardColors(containerColor = SurfaceCard),
                border = BorderStroke(1.dp, SlateBorder)
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text("Diagnostic & Callout Fee", color = TextPrimary, fontSize = 14.sp)
                        Text(viewModel.formatPrice(starting), color = TextPrimary, fontWeight = FontWeight.SemiBold)
                    }
                    Spacer(modifier = Modifier.height(8.dp))
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text("Servora Protection Guarantee", color = TextPrimary, fontSize = 14.sp)
                        Text("Included ($0)", color = EmeraldTrust, fontWeight = FontWeight.Bold)
                    }
                    Spacer(modifier = Modifier.height(8.dp))
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text("Commercial Insurance Underwriting", color = TextPrimary, fontSize = 14.sp)
                        Text("Included ($0)", color = EmeraldTrust, fontWeight = FontWeight.Bold)
                    }
                }
            }
        }
    }
}

@Composable
private fun StepSixPayment(state: BookingWizardState, viewModel: ServoraViewModel) {
    val currentCurrency by viewModel.selectedCurrency.collectAsState()
    val allMethods = viewModel.paymentMethods
    var selectedCategory by remember { mutableStateOf<PaymentCategoryType?>(null) }
    var qrisSimulatedPaid by remember { mutableStateOf(false) }

    val pro = state.selectedPro ?: viewModel.professionals.first()
    val startingPrice = pro.startingPriceUsd
    val displayPrice = viewModel.formatPrice(startingPrice)

    val filteredMethods = remember(selectedCategory) {
        if (selectedCategory == null) allMethods else allMethods.filter { it.category == selectedCategory }
    }

    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        contentPadding = PaddingValues(20.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // Header
        item {
            Text(text = "Metode Pembayaran & Escrow", color = TextPrimary, fontSize = 20.sp, fontWeight = FontWeight.Bold)
            Text(text = "Pilih pembayaran lokal Indonesia, Bank Amerika, QRIS, atau Bayar di Tempat.", color = TextMuted, fontSize = 13.sp)
        }

        // Currency Selector / Toggle Banner (Mata uang Rupiah & Dollar)
        item {
            Card(
                shape = RoundedCornerShape(14.dp),
                colors = CardDefaults.cardColors(containerColor = DeepNavy),
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
                            Text("MATA UANG AKTIF", color = TextSubtle, fontSize = 10.sp, fontWeight = FontWeight.Bold)
                            Text(
                                text = "${currentCurrency.code} (${currentCurrency.symbol}) - ${currentCurrency.name}",
                                color = Color.White,
                                fontSize = 14.sp,
                                fontWeight = FontWeight.Bold
                            )
                        }

                        // One-tap quick toggle between IDR and USD
                        Surface(
                            onClick = { viewModel.quickToggleCurrency() },
                            shape = RoundedCornerShape(20.dp),
                            color = SlateDark,
                            border = BorderStroke(1.dp, SlateBorderDark)
                        ) {
                            Row(
                                modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Icon(Icons.Default.SwapHoriz, contentDescription = null, tint = CyanAccent, modifier = Modifier.size(16.dp))
                                Spacer(modifier = Modifier.width(6.dp))
                                Text(
                                    text = if (currentCurrency.code == "IDR") "Ganti USD ($)" else "Ganti IDR (Rp)",
                                    color = CyanAccent,
                                    fontSize = 11.sp,
                                    fontWeight = FontWeight.Bold
                                )
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(10.dp))
                    HorizontalDivider(color = SlateBorderDark)
                    Spacer(modifier = Modifier.height(10.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text("Total Estimasi Jasa Teknisi:", color = TextSecondary, fontSize = 13.sp)
                        Text(
                            text = displayPrice,
                            color = CyanAccent,
                            fontSize = 20.sp,
                            fontWeight = FontWeight.ExtraBold
                        )
                    }
                }
            }
        }

        // Trust badge banner
        item {
            Card(
                shape = RoundedCornerShape(14.dp),
                colors = CardDefaults.cardColors(containerColor = EmeraldTrustLight.copy(alpha = 0.5f)),
                border = BorderStroke(1.dp, EmeraldTrust.copy(alpha = 0.3f))
            ) {
                Row(modifier = Modifier.padding(14.dp), verticalAlignment = Alignment.CenterVertically) {
                    Icon(Icons.Default.Shield, contentDescription = null, tint = EmeraldTrust)
                    Spacer(modifier = Modifier.width(10.dp))
                    Column {
                        Text("Garansi Pembayaran 100% Aman (Escrow & Proteksi)", color = TextPrimary, fontSize = 13.sp, fontWeight = FontWeight.Bold)
                        Text("Untuk transfer/online, dana ditahan di Servora Escrow hingga servis tuntas. Untuk bayar tunai di tempat (COD), garansi servis 30 hari tetap aktif penuh.", color = TextSecondary, fontSize = 11.sp)
                    }
                }
            }
        }

        // Category Filter Chips
        item {
            Column {
                Text("Kategori Pembayaran:", color = TextSecondary, fontSize = 13.sp, fontWeight = FontWeight.Bold)
                Spacer(modifier = Modifier.height(8.dp))
                LazyRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    item {
                        FilterChip(
                            selected = selectedCategory == null,
                            onClick = { selectedCategory = null },
                            label = { Text("Semua (${allMethods.size})", fontSize = 12.sp) }
                        )
                    }
                    val categories = listOf(
                        PaymentCategoryType.QRIS_INDONESIA,
                        PaymentCategoryType.VIRTUAL_ACCOUNT,
                        PaymentCategoryType.BANK_CARD,
                        PaymentCategoryType.E_WALLET,
                        PaymentCategoryType.CASH_ON_DELIVERY
                    )
                    items(categories) { cat ->
                        val count = allMethods.count { it.category == cat }
                        val label = when (cat) {
                            PaymentCategoryType.QRIS_INDONESIA -> "🇮🇩 QRIS ($count)"
                            PaymentCategoryType.VIRTUAL_ACCOUNT -> "Virtual Account ($count)"
                            PaymentCategoryType.BANK_CARD -> "Kartu Bank ($count)"
                            PaymentCategoryType.E_WALLET -> "E-Wallet ($count)"
                            PaymentCategoryType.CASH_ON_DELIVERY -> "💵 Bayar di Tempat ($count)"
                            else -> cat.displayName
                        }
                        FilterChip(
                            selected = selectedCategory == cat,
                            onClick = { selectedCategory = cat },
                            label = { Text(label, fontSize = 12.sp) }
                        )
                    }
                }
            }
        }

        // List of filtered methods
        items(filteredMethods) { method ->
            val isSelected = state.selectedPaymentMethod == method.name

            Card(
                onClick = {
                    viewModel.updateBookingWizard { copy(selectedPaymentMethod = method.name) }
                    qrisSimulatedPaid = false
                },
                shape = RoundedCornerShape(14.dp),
                colors = CardDefaults.cardColors(containerColor = if (isSelected) SurfaceElevated else SurfaceCard),
                border = BorderStroke(1.dp, if (isSelected) ElectricBlue else SlateBorder),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.padding(14.dp)) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        // Flag badge
                        Surface(
                            shape = RoundedCornerShape(6.dp),
                            color = when (method.countryOrigin) {
                                "ID" -> CoralRed.copy(alpha = 0.15f)
                                "US" -> ElectricBlue.copy(alpha = 0.15f)
                                else -> EmeraldTrust.copy(alpha = 0.15f)
                            }
                        ) {
                            Text(
                                text = when (method.countryOrigin) {
                                    "ID" -> "🇮🇩 ID"
                                    "US" -> "🇺🇸 US"
                                    else -> "💵 COD"
                                },
                                color = when (method.countryOrigin) {
                                    "ID" -> CoralRed
                                    "US" -> ElectricBlue
                                    else -> EmeraldTrust
                                },
                                fontSize = 10.sp,
                                fontWeight = FontWeight.Bold,
                                modifier = Modifier.padding(horizontal = 6.dp, vertical = 3.dp)
                            )
                        }

                        Spacer(modifier = Modifier.width(10.dp))

                        Column(modifier = Modifier.weight(1f)) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Text(
                                    text = method.name,
                                    color = TextPrimary,
                                    fontSize = 14.sp,
                                    fontWeight = FontWeight.Bold
                                )
                                if (method.badge != null) {
                                    Spacer(modifier = Modifier.width(6.dp))
                                    Surface(
                                        shape = RoundedCornerShape(4.dp),
                                        color = EmeraldTrust.copy(alpha = 0.12f)
                                    ) {
                                        Text(method.badge, color = EmeraldTrust, fontSize = 9.sp, fontWeight = FontWeight.Bold, modifier = Modifier.padding(horizontal = 4.dp, vertical = 2.dp))
                                    }
                                }
                            }
                            Text(text = method.subtext, color = TextMuted, fontSize = 11.sp)
                        }

                        RadioButton(
                            selected = isSelected,
                            onClick = {
                                viewModel.updateBookingWizard { copy(selectedPaymentMethod = method.name) }
                                qrisSimulatedPaid = false
                            },
                            colors = RadioButtonDefaults.colors(selectedColor = ElectricBlue)
                        )
                    }

                    // Interactive details if this method is selected
                    if (isSelected) {
                        Spacer(modifier = Modifier.height(12.dp))
                        HorizontalDivider(color = SlateBorder)
                        Spacer(modifier = Modifier.height(12.dp))

                        when (method.category) {
                            PaymentCategoryType.QRIS_INDONESIA -> {
                                QrisVisualCard(
                                    amountText = displayPrice,
                                    isPaid = qrisSimulatedPaid,
                                    onSimulatePaid = { qrisSimulatedPaid = true }
                                )
                            }
                            PaymentCategoryType.VIRTUAL_ACCOUNT -> {
                                VirtualAccountDetailCard(
                                    bankName = method.name,
                                    vaNumber = if (method.countryOrigin == "ID") "8809 1029 4821 9912" else "8920 4401 2289",
                                    amountText = displayPrice
                                )
                            }
                            PaymentCategoryType.CASH_ON_DELIVERY -> {
                                CashOnDeliveryDetailCard(
                                    amountText = displayPrice
                                )
                            }
                            PaymentCategoryType.BANK_CARD -> {
                                BankCardDetailCard(
                                    cardTitle = method.name,
                                    country = method.countryOrigin
                                )
                            }
                            PaymentCategoryType.E_WALLET -> {
                                EWalletDetailCard(
                                    walletName = method.name,
                                    country = method.countryOrigin
                                )
                            }
                            else -> {}
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun QrisVisualCard(
    amountText: String,
    isPaid: Boolean,
    onSimulatePaid: () -> Unit
) {
    Card(
        shape = RoundedCornerShape(14.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        border = BorderStroke(1.dp, SlateBorder),
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // QRIS Header
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Surface(
                    shape = RoundedCornerShape(4.dp),
                    color = Color(0xFFD32F2F)
                ) {
                    Text(
                        "QRIS",
                        color = Color.White,
                        fontWeight = FontWeight.ExtraBold,
                        fontSize = 14.sp,
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 2.dp)
                    )
                }

                Text(
                    "GPN • Bank Indonesia",
                    color = Color.DarkGray,
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Bold
                )
            }

            Spacer(modifier = Modifier.height(10.dp))
            Text("SERVORA INDONESIA ESCROW", color = Color.Black, fontSize = 13.sp, fontWeight = FontWeight.Bold)
            Text("NMID: ID102026884192", color = Color.Gray, fontSize = 10.sp)

            Spacer(modifier = Modifier.height(12.dp))

            // Realistic QR Code Matrix pattern drawn on Canvas
            Box(
                modifier = Modifier
                    .size(160.dp)
                    .background(Color.White, RoundedCornerShape(8.dp))
                    .border(2.dp, Color.Black, RoundedCornerShape(8.dp))
                    .padding(8.dp),
                contentAlignment = Alignment.Center
            ) {
                Canvas(modifier = Modifier.fillMaxSize()) {
                    val w = size.width
                    val h = size.height
                    val step = w / 16f

                    // Draw QR finder corners
                    // Top-left
                    drawRect(Color.Black, Offset(0f, 0f), Size(step * 4, step * 4))
                    drawRect(Color.White, Offset(step, step), Size(step * 2, step * 2))
                    drawRect(Color.Black, Offset(step * 1.3f, step * 1.3f), Size(step * 1.4f, step * 1.4f))

                    // Top-right
                    drawRect(Color.Black, Offset(w - step * 4, 0f), Size(step * 4, step * 4))
                    drawRect(Color.White, Offset(w - step * 3, step), Size(step * 2, step * 2))
                    drawRect(Color.Black, Offset(w - step * 2.7f, step * 1.3f), Size(step * 1.4f, step * 1.4f))

                    // Bottom-left
                    drawRect(Color.Black, Offset(0f, h - step * 4), Size(step * 4, step * 4))
                    drawRect(Color.White, Offset(step, h - step * 3), Size(step * 2, step * 2))
                    drawRect(Color.Black, Offset(step * 1.3f, h - step * 2.7f), Size(step * 1.4f, step * 1.4f))

                    // Scatter pseudo-QR modules
                    for (i in 4..11) {
                        for (j in 4..11) {
                            if ((i * 7 + j * 13) % 3 != 0) {
                                drawRect(Color.Black, Offset(i * step, j * step), Size(step * 0.9f, step * 0.9f))
                            }
                        }
                    }

                    // Alignment dots
                    for (i in 0..15) {
                        if (i % 2 == 0) {
                            drawRect(Color.Black, Offset(i * step, step * 5), Size(step * 0.9f, step * 0.9f))
                            drawRect(Color.Black, Offset(step * 5, i * step), Size(step * 0.9f, step * 0.9f))
                        }
                    }
                }

                // Center logo
                Surface(
                    shape = RoundedCornerShape(4.dp),
                    color = ElectricBlue,
                    modifier = Modifier.size(28.dp)
                ) {
                    Box(contentAlignment = Alignment.Center) {
                        Text("S", color = Color.White, fontWeight = FontWeight.Bold, fontSize = 14.sp)
                    }
                }
            }

            Spacer(modifier = Modifier.height(10.dp))
            Text("Jumlah Tagihan:", color = Color.Gray, fontSize = 11.sp)
            Text(amountText, color = Color(0xFFD32F2F), fontSize = 18.sp, fontWeight = FontWeight.ExtraBold)

            Spacer(modifier = Modifier.height(8.dp))
            Text(
                "Mendukung: BCA Mobile, Livin\' Mandiri, BRImo, BNI, GoPay, OVO, DANA, ShopeePay, LinkAja",
                color = Color.DarkGray,
                fontSize = 10.sp,
                textAlign = TextAlign.Center
            )

            Spacer(modifier = Modifier.height(12.dp))

            if (isPaid) {
                Surface(
                    shape = RoundedCornerShape(8.dp),
                    color = EmeraldTrust.copy(alpha = 0.15f),
                    border = BorderStroke(1.dp, EmeraldTrust)
                ) {
                    Row(
                        modifier = Modifier.padding(horizontal = 14.dp, vertical = 8.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(Icons.Default.CheckCircle, contentDescription = null, tint = EmeraldTrust, modifier = Modifier.size(16.dp))
                        Spacer(modifier = Modifier.width(6.dp))
                        Text("Pembayaran QRIS Terverifikasi Berhasil!", color = EmeraldTrust, fontSize = 12.sp, fontWeight = FontWeight.Bold)
                    }
                }
            } else {
                Button(
                    onClick = onSimulatePaid,
                    shape = RoundedCornerShape(8.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFD32F2F)),
                    contentPadding = PaddingValues(horizontal = 14.dp, vertical = 8.dp)
                ) {
                    Icon(Icons.Default.QrCodeScanner, contentDescription = null, modifier = Modifier.size(16.dp))
                    Spacer(modifier = Modifier.width(6.dp))
                    Text("Simulasi Scan & Konfirmasi QRIS", fontSize = 12.sp, fontWeight = FontWeight.Bold)
                }
            }
        }
    }
}

@Composable
private fun VirtualAccountDetailCard(bankName: String, vaNumber: String, amountText: String) {
    var copied by remember { mutableStateOf(false) }

    Surface(
        shape = RoundedCornerShape(10.dp),
        color = DeepNavy,
        border = BorderStroke(1.dp, SlateBorderDark),
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(modifier = Modifier.padding(14.dp)) {
            Text("NOMOR VIRTUAL ACCOUNT ($bankName)", color = TextSubtle, fontSize = 10.sp, fontWeight = FontWeight.Bold)
            Spacer(modifier = Modifier.height(4.dp))
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = vaNumber,
                    color = CyanAccent,
                    fontSize = 17.sp,
                    fontWeight = FontWeight.Bold,
                    letterSpacing = 1.sp
                )

                Button(
                    onClick = { copied = true },
                    shape = RoundedCornerShape(6.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = SlateDark),
                    contentPadding = PaddingValues(horizontal = 10.dp, vertical = 4.dp)
                ) {
                    Text(if (copied) "Tersalin!" else "Salin VA", fontSize = 11.sp, color = if (copied) EmeraldTrust else Color.White)
                }
            }

            Spacer(modifier = Modifier.height(8.dp))
            Text("Total Transfer: $amountText (Otomatis verifikasi 24/7 tanpa bukti transfer)", color = TextSubtle, fontSize = 11.sp)
        }
    }
}

@Composable
private fun CashOnDeliveryDetailCard(amountText: String) {
    Surface(
        shape = RoundedCornerShape(10.dp),
        color = EmeraldTrustLight.copy(alpha = 0.5f),
        border = BorderStroke(1.dp, EmeraldTrust.copy(alpha = 0.4f)),
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(modifier = Modifier.padding(14.dp)) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(Icons.Default.Payments, contentDescription = null, tint = EmeraldTrust)
                Spacer(modifier = Modifier.width(8.dp))
                Text("Bayar Tunai di Tempat (Cash on Delivery)", color = TextPrimary, fontWeight = FontWeight.Bold, fontSize = 13.sp)
            }
            Spacer(modifier = Modifier.height(6.dp))
            Text(
                "• Bayar langsung uang tunai senilai $amountText kepada tukang setelah servis selesai dan diperiksa.",
                color = TextSecondary,
                fontSize = 11.sp
            )
            Text(
                "• Garansi Servora 30 hari & Asuransi $1.000.000 tetap berlaku penuh.",
                color = TextSecondary,
                fontSize = 11.sp
            )
            Text(
                "• Tukang akan menerbitkan e-receipt digital resmi di aplikasi Servora saat uang diterima.",
                color = TextSecondary,
                fontSize = 11.sp
            )
        }
    }
}

@Composable
private fun BankCardDetailCard(cardTitle: String, country: String) {
    Surface(
        shape = RoundedCornerShape(10.dp),
        color = DeepNavy,
        border = BorderStroke(1.dp, SlateBorderDark),
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(modifier = Modifier.padding(14.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    if (country == "ID") "Kartu Debit / Kredit Indonesia (GPN & International)" else "US Bank Debit / Credit Card",
                    color = Color.White,
                    fontWeight = FontWeight.Bold,
                    fontSize = 12.sp
                )
                Text("3D Secure OTP", color = EmeraldTrust, fontSize = 10.sp, fontWeight = FontWeight.Bold)
            }
            Spacer(modifier = Modifier.height(6.dp))
            Text("•••• •••• •••• 8832 (Exp 12/28)", color = CyanAccent, fontSize = 14.sp, fontWeight = FontWeight.Medium)
            Spacer(modifier = Modifier.height(4.dp))
            Text("Mendukung BCA, Mandiri, BNI, BRI, Chase, BofA, Wells Fargo, Visa, Mastercard, AMEX.", color = TextSubtle, fontSize = 10.sp)
        }
    }
}

@Composable
private fun EWalletDetailCard(walletName: String, country: String) {
    Surface(
        shape = RoundedCornerShape(10.dp),
        color = SurfaceElevated,
        border = BorderStroke(1.dp, SlateBorder),
        modifier = Modifier.fillMaxWidth()
    ) {
        Row(
            modifier = Modifier.padding(12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(Icons.Default.AccountBalanceWallet, contentDescription = null, tint = ElectricBlue)
            Spacer(modifier = Modifier.width(10.dp))
            Column {
                Text("Terhubung ke akun $walletName", color = TextPrimary, fontWeight = FontWeight.Bold, fontSize = 12.sp)
                Text(
                    if (country == "ID") "Otorisasi instan 1-klik melalui GoPay/OVO/DANA PIN" else "Biometric 1-tap checkout via Apple Pay/Google Pay/Venmo",
                    color = TextSecondary,
                    fontSize = 10.sp
                )
            }
        }
    }
}

@Composable
private fun StepSevenConfirmed(
    state: BookingWizardState,
    onOpenLiveTracking: (Booking) -> Unit
) {
    val booking = state.confirmedBooking

    Box(
        modifier = Modifier
            .fillMaxSize()
            .padding(24.dp),
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier.fillMaxWidth()
        ) {
            // Animated Success Circle
            Box(
                modifier = Modifier
                    .size(80.dp)
                    .clip(CircleShape)
                    .background(EmeraldTrust),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = Icons.Default.Check,
                    contentDescription = "Success",
                    tint = Color.White,
                    modifier = Modifier.size(44.dp)
                )
            }

            Spacer(modifier = Modifier.height(20.dp))

            Text(
                text = "Booking Confirmed!",
                color = TextPrimary,
                fontSize = 24.sp,
                fontWeight = FontWeight.Bold
            )

            Text(
                text = "Professional has accepted and is preparing dispatch.",
                color = TextMuted,
                fontSize = 14.sp,
                modifier = Modifier.padding(top = 4.dp)
            )

            Spacer(modifier = Modifier.height(24.dp))

            Card(
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = SurfaceCard),
                border = BorderStroke(1.dp, SlateBorder),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text("Booking ID", color = TextMuted, fontSize = 12.sp)
                        Text(booking?.id ?: "SRV-90214", color = TextPrimary, fontWeight = FontWeight.Bold, fontSize = 13.sp)
                    }
                    Spacer(modifier = Modifier.height(8.dp))
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text("Assigned Specialist", color = TextMuted, fontSize = 12.sp)
                        Text(booking?.professional?.name ?: "Alex Morgan", color = TextPrimary, fontWeight = FontWeight.Bold, fontSize = 13.sp)
                    }
                    Spacer(modifier = Modifier.height(8.dp))
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text("Estimated Arrival", color = TextMuted, fontSize = 12.sp)
                        Text("15 Minutes", color = ElectricBlue, fontWeight = FontWeight.Bold, fontSize = 13.sp)
                    }
                }
            }

            Spacer(modifier = Modifier.height(30.dp))

            Button(
                onClick = {
                    booking?.let { onOpenLiveTracking(it) }
                },
                shape = RoundedCornerShape(14.dp),
                colors = ButtonDefaults.buttonColors(containerColor = ElectricBlue),
                modifier = Modifier
                    .fillMaxWidth()
                    .height(52.dp)
                    .testTag("track_live_button")
            ) {
                Icon(Icons.Default.Navigation, contentDescription = null, tint = Color.White)
                Spacer(modifier = Modifier.width(8.dp))
                Text("Track Live on Map", color = Color.White, fontWeight = FontWeight.Bold, fontSize = 15.sp)
            }
        }
    }
}
