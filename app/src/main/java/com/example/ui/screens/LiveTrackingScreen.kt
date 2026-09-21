package com.example.ui.screens

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.*
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
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
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.PathEffect
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.model.Booking
import com.example.model.BookingStatus
import com.example.model.JobProgressStep
import com.example.model.PaymentStatus
import com.example.ui.theme.*
import com.example.viewmodel.ServoraViewModel

@Composable
fun LiveTrackingScreen(
    booking: Booking,
    viewModel: ServoraViewModel,
    onBack: () -> Unit,
    onOpenChat: () -> Unit,
    modifier: Modifier = Modifier
) {
    var isMapExpanded by remember { mutableStateOf(false) }

    // Pulsing circle animation for live GPS beacon
    val infiniteTransition = rememberInfiniteTransition(label = "pulse")
    val beaconRadius by infiniteTransition.animateFloat(
        initialValue = 18f,
        targetValue = 42f,
        animationSpec = infiniteRepeatable(
            animation = tween(1200, easing = LinearEasing),
            repeatMode = RepeatMode.Restart
        ), label = "beacon"
    )

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(SurfaceCanvas)
            .statusBarsPadding()
    ) {
        // Top Bar
        Surface(
            color = DeepNavy,
            tonalElevation = 4.dp
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 12.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    IconButton(onClick = onBack, modifier = Modifier.size(36.dp)) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Back",
                            tint = Color.White
                        )
                    }
                    Spacer(modifier = Modifier.width(8.dp))
                    Column {
                        Text(
                            text = "Live Dispatch & Job Tracker",
                            color = Color.White,
                            fontSize = 15.sp,
                            fontWeight = FontWeight.Bold
                        )
                        Text(
                            text = "Booking #${booking.id}",
                            color = CyanAccent,
                            fontSize = 11.sp
                        )
                    }
                }

                // Simulation Advance Button (Helpful for demonstration)
                OutlinedButton(
                    onClick = { viewModel.advanceTrackingStatus() },
                    shape = RoundedCornerShape(12.dp),
                    border = BorderStroke(1.dp, CyanAccent),
                    contentPadding = PaddingValues(horizontal = 10.dp, vertical = 4.dp)
                ) {
                    Icon(Icons.Default.FastForward, contentDescription = null, tint = CyanAccent, modifier = Modifier.size(14.dp))
                    Spacer(modifier = Modifier.width(4.dp))
                    Text("Next State", color = CyanAccent, fontSize = 10.sp, fontWeight = FontWeight.Bold)
                }
            }
        }

        // Live Vector Map Canvas
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(if (isMapExpanded) 340.dp else 220.dp)
                .background(Color(0xFF0F172A))
        ) {
            Canvas(modifier = Modifier.fillMaxSize()) {
                val w = size.width
                val h = size.height

                // Stylized dark city grid roads
                val roadColor = Color(0xFF1E293B)
                val riverColor = Color(0xFF132238)

                // Background River/Greenbelt
                drawRect(color = Color(0xFF0B1220))

                // Roads (horizontal & vertical lines)
                for (y in 0..h.toInt() step 60) {
                    drawLine(
                        color = roadColor,
                        start = Offset(0f, y.toFloat()),
                        end = Offset(w, y.toFloat()),
                        strokeWidth = 14f
                    )
                }
                for (x in 0..w.toInt() step 70) {
                    drawLine(
                        color = roadColor,
                        start = Offset(x.toFloat(), 0f),
                        end = Offset(x.toFloat(), h),
                        strokeWidth = 14f
                    )
                }

                // Route Polyline from Technician to Destination
                val proLoc = Offset(w * 0.28f, h * 0.65f)
                val destLoc = Offset(w * 0.72f, h * 0.32f)

                val routePath = Path().apply {
                    moveTo(proLoc.x, proLoc.y)
                    lineTo(w * 0.45f, proLoc.y)
                    lineTo(w * 0.45f, destLoc.y)
                    lineTo(destLoc.x, destLoc.y)
                }

                // Outer route glow
                drawPath(
                    path = routePath,
                    color = CyanAccent.copy(alpha = 0.35f),
                    style = Stroke(width = 10f)
                )

                // Dashed Route
                drawPath(
                    path = routePath,
                    color = CyanAccent,
                    style = Stroke(
                        width = 4f,
                        pathEffect = PathEffect.dashPathEffect(floatArrayOf(15f, 10f), 0f)
                    )
                )

                // Pulsing beacon around technician
                drawCircle(
                    color = CyanAccent.copy(alpha = 0.35f),
                    radius = beaconRadius,
                    center = proLoc
                )

                // Technician Point
                drawCircle(
                    color = Color.White,
                    radius = 12f,
                    center = proLoc
                )
                drawCircle(
                    color = ElectricBlue,
                    radius = 8f,
                    center = proLoc
                )

                // Destination Home Point
                drawCircle(
                    color = EmergencyRed.copy(alpha = 0.3f),
                    radius = 24f,
                    center = destLoc
                )
                drawCircle(
                    color = EmergencyRed,
                    radius = 10f,
                    center = destLoc
                )
            }

            // Map Overlay Badge: ETA & Distance
            Surface(
                shape = RoundedCornerShape(12.dp),
                color = DeepNavy.copy(alpha = 0.85f),
                border = BorderStroke(1.dp, CyanAccent.copy(alpha = 0.5f)),
                modifier = Modifier
                    .align(Alignment.TopStart)
                    .padding(14.dp)
            ) {
                Row(
                    modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        imageVector = Icons.Default.DirectionsCar,
                        contentDescription = null,
                        tint = CyanAccent,
                        modifier = Modifier.size(16.dp)
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Column {
                        Text(
                            text = "${booking.liveEtaMinutes} min ETA",
                            color = Color.White,
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Bold
                        )
                        Text(
                            text = "${booking.technicianDistanceKm} km away",
                            color = TextSubtle,
                            fontSize = 10.sp
                        )
                    }
                }
            }

            // Expand Map toggle
            IconButton(
                onClick = { isMapExpanded = !isMapExpanded },
                modifier = Modifier
                    .align(Alignment.BottomEnd)
                    .padding(12.dp)
                    .background(DeepNavy.copy(alpha = 0.8f), CircleShape)
                    .size(36.dp)
            ) {
                Icon(
                    imageVector = if (isMapExpanded) Icons.Default.FullscreenExit else Icons.Default.Fullscreen,
                    contentDescription = "Expand Map",
                    tint = Color.White
                )
            }
        }

        // Body: Status Pipeline & In-Job Progress
        LazyColumn(
            modifier = Modifier
                .weight(1f)
                .fillMaxWidth(),
            contentPadding = PaddingValues(16.dp),
            verticalArrangement = Arrangement.spacedBy(14.dp)
        ) {
            // Pro Card with Quick Call & Chat
            item {
                Card(
                    shape = RoundedCornerShape(18.dp),
                    colors = CardDefaults.cardColors(containerColor = SurfaceCard),
                    border = BorderStroke(1.dp, SlateBorder)
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Box(
                                modifier = Modifier
                                    .size(54.dp)
                                    .clip(RoundedCornerShape(14.dp))
                                    .background(SlateDark),
                                contentAlignment = Alignment.Center
                            ) {
                                if (booking.professional.avatarRes != null) {
                                    Image(
                                        painter = painterResource(id = booking.professional.avatarRes),
                                        contentDescription = booking.professional.name,
                                        contentScale = ContentScale.Crop,
                                        modifier = Modifier.fillMaxSize()
                                    )
                                } else {
                                    Text(
                                        text = booking.professional.name.take(2),
                                        color = Color.White,
                                        fontWeight = FontWeight.Bold,
                                        fontSize = 18.sp
                                    )
                                }
                            }

                            Spacer(modifier = Modifier.width(12.dp))

                            Column(modifier = Modifier.weight(1f)) {
                                Text(
                                    text = booking.professional.name,
                                    color = TextPrimary,
                                    fontSize = 16.sp,
                                    fontWeight = FontWeight.Bold
                                )
                                Text(
                                    text = booking.professional.title,
                                    color = TextSecondary,
                                    fontSize = 12.sp
                                )
                                Text(
                                    text = "Licence #HV-884920 • Verified Master",
                                    color = EmeraldTrust,
                                    fontSize = 11.sp,
                                    fontWeight = FontWeight.Medium
                                )
                            }
                        }

                        Spacer(modifier = Modifier.height(14.dp))

                        // Actions: Call, Chat, Share Location
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(10.dp)
                        ) {
                            OutlinedButton(
                                onClick = { /* Phone dialer simulation */ },
                                modifier = Modifier.weight(1f),
                                shape = RoundedCornerShape(10.dp)
                            ) {
                                Icon(Icons.Default.Phone, contentDescription = null, modifier = Modifier.size(16.dp))
                                Spacer(modifier = Modifier.width(6.dp))
                                Text("Call", fontSize = 12.sp)
                            }

                            Button(
                                onClick = onOpenChat,
                                modifier = Modifier.weight(1f),
                                shape = RoundedCornerShape(10.dp),
                                colors = ButtonDefaults.buttonColors(containerColor = ElectricBlue)
                            ) {
                                Icon(Icons.Default.ChatBubble, contentDescription = null, modifier = Modifier.size(16.dp))
                                Spacer(modifier = Modifier.width(6.dp))
                                Text("Chat", fontSize = 12.sp)
                            }

                            OutlinedButton(
                                onClick = { /* Share link */ },
                                modifier = Modifier.weight(1f),
                                shape = RoundedCornerShape(10.dp)
                            ) {
                                Icon(Icons.Default.ShareLocation, contentDescription = null, modifier = Modifier.size(16.dp))
                                Spacer(modifier = Modifier.width(6.dp))
                                Text("Share", fontSize = 12.sp)
                            }
                        }
                    }
                }
            }

            // Status Progress Timeline
            item {
                Card(
                    shape = RoundedCornerShape(18.dp),
                    colors = CardDefaults.cardColors(containerColor = SurfaceCard),
                    border = BorderStroke(1.dp, SlateBorder)
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Text(
                            text = "Job Lifecycle Timeline",
                            color = TextPrimary,
                            fontSize = 15.sp,
                            fontWeight = FontWeight.Bold
                        )
                        Spacer(modifier = Modifier.height(12.dp))

                        TimelineStatusItem(
                            title = "Booking Confirmed",
                            desc = "Payment secured in Servora Escrow",
                            isCompleted = true,
                            isCurrent = booking.status == BookingStatus.CONFIRMED
                        )
                        TimelineStatusItem(
                            title = "Specialist Accepted",
                            desc = "Tools & replacement parts prepped",
                            isCompleted = booking.status.ordinal >= BookingStatus.ACCEPTED.ordinal,
                            isCurrent = booking.status == BookingStatus.ACCEPTED
                        )
                        TimelineStatusItem(
                            title = "On the Way",
                            desc = "GPS tracking active (${booking.liveEtaMinutes} min)",
                            isCompleted = booking.status.ordinal >= BookingStatus.ON_THE_WAY.ordinal,
                            isCurrent = booking.status == BookingStatus.ON_THE_WAY
                        )
                        TimelineStatusItem(
                            title = "Arrived & Inspection",
                            desc = "On-site multi-point diagnostics",
                            isCompleted = booking.status.ordinal >= BookingStatus.ARRIVED.ordinal,
                            isCurrent = booking.status == BookingStatus.ARRIVED
                        )
                        TimelineStatusItem(
                            title = "Work in Progress",
                            desc = "Repair and servicing underway",
                            isCompleted = booking.status.ordinal >= BookingStatus.WORKING.ordinal,
                            isCurrent = booking.status == BookingStatus.WORKING
                        )
                        TimelineStatusItem(
                            title = "Completed & Inspected",
                            desc = "Release escrow payment upon satisfaction",
                            isCompleted = booking.status == BookingStatus.COMPLETED,
                            isCurrent = booking.status == BookingStatus.COMPLETED,
                            isLast = true
                        )
                    }
                }
            }

            // Additional Work Approval Card (if technician proposes extra parts)
            booking.extraWork?.let { extra ->
                item {
                    Card(
                        shape = RoundedCornerShape(16.dp),
                        colors = CardDefaults.cardColors(
                            containerColor = if (extra.isApproved) EmeraldTrustLight.copy(alpha = 0.3f) else AmberWarning.copy(alpha = 0.1f)
                        ),
                        border = BorderStroke(1.dp, if (extra.isApproved) EmeraldTrust else AmberWarning)
                    ) {
                        Column(modifier = Modifier.padding(16.dp)) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Icon(
                                    imageVector = if (extra.isApproved) Icons.Default.CheckCircle else Icons.Default.Warning,
                                    contentDescription = null,
                                    tint = if (extra.isApproved) EmeraldTrust else AmberWarning
                                )
                                Spacer(modifier = Modifier.width(8.dp))
                                Text(
                                    text = if (extra.isApproved) "Additional Order Approved" else "Additional Part Quotation Requested",
                                    color = TextPrimary,
                                    fontSize = 14.sp,
                                    fontWeight = FontWeight.Bold
                                )
                            }

                            Spacer(modifier = Modifier.height(8.dp))

                            Text(text = "Item: ${extra.description}", color = TextPrimary, fontSize = 13.sp, fontWeight = FontWeight.SemiBold)
                            Text(text = "Reason: ${extra.reason}", color = TextSecondary, fontSize = 12.sp)
                            Text(
                                text = "Additional Cost: +${viewModel.formatPrice(extra.additionalCostUsd)}",
                                color = ElectricBlue,
                                fontSize = 14.sp,
                                fontWeight = FontWeight.Bold,
                                modifier = Modifier.padding(vertical = 4.dp)
                            )

                            if (!extra.isApproved) {
                                Text(
                                    text = "Servora Rule: The specialist cannot bill this without your explicit consent.",
                                    color = TextMuted,
                                    fontSize = 11.sp
                                )
                                Spacer(modifier = Modifier.height(10.dp))
                                Button(
                                    onClick = { viewModel.approveExtraWork() },
                                    shape = RoundedCornerShape(10.dp),
                                    colors = ButtonDefaults.buttonColors(containerColor = EmeraldTrust)
                                ) {
                                    Text("Approve Work Order (+${viewModel.formatPrice(extra.additionalCostUsd)})", color = Color.White, fontWeight = FontWeight.Bold)
                                }
                            }
                        }
                    }
                }
            }

            // Escrow Payment Card & Release Action
            item {
                Card(
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(containerColor = DeepNavy),
                    border = BorderStroke(1.dp, SlateBorderDark)
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text("Servora Escrow Protection", color = Color.White, fontSize = 14.sp, fontWeight = FontWeight.Bold)
                            Surface(
                                shape = RoundedCornerShape(8.dp),
                                color = if (booking.paymentStatus == PaymentStatus.RELEASED) EmeraldTrust.copy(alpha = 0.2f) else CyanAccent.copy(alpha = 0.2f)
                            ) {
                                Text(
                                    text = if (booking.paymentStatus == PaymentStatus.RELEASED) "Payment Released" else "Funds Held in Escrow",
                                    color = if (booking.paymentStatus == PaymentStatus.RELEASED) EmeraldTrust else CyanAccent,
                                    fontSize = 10.sp,
                                    fontWeight = FontWeight.Bold,
                                    modifier = Modifier.padding(horizontal = 8.dp, vertical = 3.dp)
                                )
                            }
                        }

                        Spacer(modifier = Modifier.height(8.dp))

                        Text(
                            text = "Amount: ${viewModel.formatPrice(booking.baseCostUsd)}",
                            color = Color.White,
                            fontSize = 18.sp,
                            fontWeight = FontWeight.ExtraBold
                        )

                        Text(
                            text = "Funds are safely held until the professional completes the service and you give final sign-off.",
                            color = TextSubtle,
                            fontSize = 11.sp,
                            modifier = Modifier.padding(top = 4.dp)
                        )

                        if (booking.paymentStatus != PaymentStatus.RELEASED) {
                            Spacer(modifier = Modifier.height(14.dp))
                            Button(
                                onClick = { viewModel.releaseEscrowPayment() },
                                shape = RoundedCornerShape(10.dp),
                                colors = ButtonDefaults.buttonColors(containerColor = EmeraldTrust),
                                modifier = Modifier.fillMaxWidth()
                            ) {
                                Icon(Icons.Default.Verified, contentDescription = null, tint = Color.White)
                                Spacer(modifier = Modifier.width(6.dp))
                                Text("Release Payment & Complete", color = Color.White, fontWeight = FontWeight.Bold)
                            }
                        }
                    }
                }
            }

            // Simulate Extra Work Request Trigger button
            if (booking.extraWork == null && booking.status == BookingStatus.WORKING) {
                item {
                    OutlinedButton(
                        onClick = { viewModel.simulateProAdditionalWorkRequest() },
                        shape = RoundedCornerShape(12.dp),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text("Simulate Specialist Requesting Extra Part Quote")
                    }
                }
            }
        }
    }
}

@Composable
private fun TimelineStatusItem(
    title: String,
    desc: String,
    isCompleted: Boolean,
    isCurrent: Boolean,
    isLast: Boolean = false
) {
    Row(modifier = Modifier.fillMaxWidth()) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Box(
                modifier = Modifier
                    .size(24.dp)
                    .clip(CircleShape)
                    .background(
                        when {
                            isCompleted -> EmeraldTrust
                            isCurrent -> ElectricBlue
                            else -> SurfaceMuted
                        }
                    ),
                contentAlignment = Alignment.Center
            ) {
                if (isCompleted) {
                    Icon(Icons.Default.Check, contentDescription = null, tint = Color.White, modifier = Modifier.size(14.dp))
                } else if (isCurrent) {
                    Box(modifier = Modifier.size(8.dp).clip(CircleShape).background(Color.White))
                }
            }

            if (!isLast) {
                Box(
                    modifier = Modifier
                        .width(2.dp)
                        .height(34.dp)
                        .background(if (isCompleted) EmeraldTrust else SlateBorder)
                )
            }
        }

        Spacer(modifier = Modifier.width(12.dp))

        Column(modifier = Modifier.padding(bottom = 12.dp)) {
            Text(
                text = title,
                color = if (isCurrent) ElectricBlue else TextPrimary,
                fontSize = 13.sp,
                fontWeight = if (isCurrent || isCompleted) FontWeight.Bold else FontWeight.Medium
            )
            Text(text = desc, color = TextMuted, fontSize = 11.sp)
        }
    }
}
