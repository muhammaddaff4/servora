package com.example.ui.screens

import androidx.compose.animation.core.*
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
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
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.model.EmergencyServiceType
import com.example.ui.components.getCategoryIconVector
import com.example.ui.theme.*
import com.example.viewmodel.ServoraViewModel

@Composable
fun EmergencyScreen(
    viewModel: ServoraViewModel,
    onBack: () -> Unit,
    onDispatchEmergency: (EmergencyServiceType) -> Unit,
    modifier: Modifier = Modifier
) {
    val emergencyTypes = viewModel.emergencyTypes
    var selectedType by remember { mutableStateOf(emergencyTypes.first()) }
    var showDispatchConfirmDialog by remember { mutableStateOf(false) }

    // Pulsing animation for emergency beacon
    val infiniteTransition = rememberInfiniteTransition(label = "pulse")
    val pulseScale by infiniteTransition.animateFloat(
        initialValue = 0.98f,
        targetValue = 1.04f,
        animationSpec = infiniteRepeatable(
            animation = tween(800, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ), label = "pulse"
    )

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(SurfaceCanvas)
            .statusBarsPadding()
    ) {
        // Red Top Header with 24/7 Emergency Badge
        Surface(
            color = DeepNavy,
            modifier = Modifier.fillMaxWidth()
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    IconButton(onClick = onBack, modifier = Modifier.size(36.dp)) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Back",
                            tint = Color.White
                        )
                    }

                    Spacer(modifier = Modifier.width(8.dp))

                    Row(
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Box(
                            modifier = Modifier
                                .size(12.dp)
                                .clip(CircleShape)
                                .background(EmergencyRed)
                                .scale(pulseScale)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = "24/7 EMERGENCY DISPATCH",
                            color = EmergencyRed,
                            fontSize = 15.sp,
                            fontWeight = FontWeight.ExtraBold,
                            letterSpacing = 1.sp
                        )
                    }
                }

                Spacer(modifier = Modifier.height(14.dp))

                Text(
                    text = "Critical Home & Property Emergencies",
                    color = Color.White,
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Bold
                )
                Text(
                    text = "Nearest certified on-call technician will be dispatched instantly. Average arrival under 15 minutes.",
                    color = TextSubtle,
                    fontSize = 12.sp,
                    lineHeight = 18.sp
                )

                Spacer(modifier = Modifier.height(14.dp))

                // Guarantee Banner
                Surface(
                    shape = RoundedCornerShape(12.dp),
                    color = SlateDark,
                    border = BorderStroke(1.dp, SlateBorderDark)
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(12.dp),
                        horizontalArrangement = Arrangement.SpaceAround
                    ) {
                        EmergencyMetric(value = "< 15 min", label = "Avg Response")
                        EmergencyMetric(value = "Priority", label = "Routing")
                        EmergencyMetric(value = "Guaranteed", label = "Arrival")
                    }
                }
            }
        }

        // Triage Problem Types List
        LazyColumn(
            modifier = Modifier
                .weight(1f)
                .fillMaxWidth(),
            contentPadding = PaddingValues(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            item {
                Text(
                    text = "Select Urgent Situation",
                    color = TextPrimary,
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold
                )
            }

            items(emergencyTypes) { emType ->
                val isSelected = selectedType.id == emType.id
                Card(
                    onClick = { selectedType = emType },
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(
                        containerColor = if (isSelected) SurfaceCard else SurfaceCard
                    ),
                    border = BorderStroke(
                        if (isSelected) 2.dp else 1.dp,
                        if (isSelected) EmergencyRed else SlateBorder
                    ),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Row(
                        modifier = Modifier.padding(16.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Box(
                            modifier = Modifier
                                .size(46.dp)
                                .clip(RoundedCornerShape(12.dp))
                                .background(EmergencyRed.copy(alpha = 0.12f)),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = getCategoryIconVector(emType.iconName),
                                contentDescription = null,
                                tint = EmergencyRed,
                                modifier = Modifier.size(24.dp)
                            )
                        }

                        Spacer(modifier = Modifier.width(14.dp))

                        Column(modifier = Modifier.weight(1f)) {
                            Text(
                                text = emType.title,
                                color = TextPrimary,
                                fontSize = 15.sp,
                                fontWeight = FontWeight.Bold
                            )
                            Text(
                                text = emType.description,
                                color = TextSecondary,
                                fontSize = 11.sp
                            )
                            Spacer(modifier = Modifier.height(4.dp))
                            Text(
                                text = "Emergency Callout: ${viewModel.formatPrice(emType.baseFeeUsd)} • ETA ${emType.avgEtaMin}m",
                                color = EmergencyRed,
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Bold
                            )
                        }

                        RadioButton(
                            selected = isSelected,
                            onClick = { selectedType = emType },
                            colors = RadioButtonDefaults.colors(selectedColor = EmergencyRed)
                        )
                    }
                }
            }

            // Safety instructions item
            item {
                Card(
                    shape = RoundedCornerShape(14.dp),
                    colors = CardDefaults.cardColors(containerColor = SurfaceMuted),
                    border = BorderStroke(1.dp, SlateBorder)
                ) {
                    Column(modifier = Modifier.padding(14.dp)) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(Icons.Default.HealthAndSafety, contentDescription = null, tint = AmberWarning)
                            Spacer(modifier = Modifier.width(8.dp))
                            Text("Safety First While Waiting", color = TextPrimary, fontSize = 13.sp, fontWeight = FontWeight.Bold)
                        }
                        Spacer(modifier = Modifier.height(6.dp))
                        Text(
                            text = "• For severe water leaks: shut off your main water valve immediately.\n• For electrical sparks or burning smell: turn off main electrical panel and keep away.\n• In life-threatening emergencies, dial local authorities (e.g., 911 / 112).",
                            color = TextSecondary,
                            fontSize = 11.sp,
                            lineHeight = 16.sp
                        )
                    }
                }
            }
        }

        // Bottom Dispatch Button
        Surface(
            color = SurfaceCard,
            border = BorderStroke(1.dp, SlateBorder),
            modifier = Modifier
                .fillMaxWidth()
                .navigationBarsPadding()
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                Button(
                    onClick = { showDispatchConfirmDialog = true },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(54.dp)
                        .testTag("emergency_dispatch_button"),
                    shape = RoundedCornerShape(14.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = EmergencyRed)
                ) {
                    Icon(Icons.Default.FlashOn, contentDescription = null, tint = Color.White)
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = "1-Tap Emergency Dispatch (${viewModel.formatPrice(selectedType.baseFeeUsd)})",
                        color = Color.White,
                        fontSize = 15.sp,
                        fontWeight = FontWeight.Bold
                    )
                }
            }
        }
    }

    if (showDispatchConfirmDialog) {
        AlertDialog(
            onDismissRequest = { showDispatchConfirmDialog = false },
            title = {
                Text("Confirm Emergency Dispatch", fontWeight = FontWeight.Bold)
            },
            text = {
                Text(
                    "You are requesting priority emergency dispatch for '${selectedType.title}'. An on-call certified professional will be routed to your address (${viewModel.userLocation.value}) with priority ETA within ${selectedType.avgEtaMin} minutes."
                )
            },
            confirmButton = {
                Button(
                    onClick = {
                        showDispatchConfirmDialog = false
                        onDispatchEmergency(selectedType)
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = EmergencyRed)
                ) {
                    Text("Confirm Priority Dispatch", color = Color.White, fontWeight = FontWeight.Bold)
                }
            },
            dismissButton = {
                TextButton(onClick = { showDispatchConfirmDialog = false }) {
                    Text("Cancel", color = TextSecondary)
                }
            }
        )
    }
}

@Composable
private fun EmergencyMetric(value: String, label: String) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Text(text = value, color = Color.White, fontSize = 14.sp, fontWeight = FontWeight.Bold)
        Text(text = label, color = TextSubtle, fontSize = 10.sp)
    }
}
