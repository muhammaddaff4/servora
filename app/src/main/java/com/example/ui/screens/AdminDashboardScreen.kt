package com.example.ui.screens

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.model.AppRole
import com.example.ui.theme.*
import com.example.viewmodel.ServoraViewModel

@Composable
fun AdminDashboardScreen(
    viewModel: ServoraViewModel,
    onBack: () -> Unit,
    modifier: Modifier = Modifier
) {
    val totalUsers by viewModel.adminTotalUsers.collectAsState()
    val activePros by viewModel.adminActivePros.collectAsState()
    val totalBookings by viewModel.adminTotalBookings.collectAsState()
    val grossRevenue by viewModel.adminGrossRevenue.collectAsState()
    val escrowHeld by viewModel.adminEscrowHeld.collectAsState()
    val disputes by viewModel.adminPendingDisputes.collectAsState()
    val verificationQueue by viewModel.adminVerificationQueue.collectAsState()

    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .background(SurfaceCanvas),
        contentPadding = PaddingValues(bottom = 80.dp)
    ) {
        // Header
        item {
            Surface(
                color = DeepNavy,
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .statusBarsPadding()
                        .padding(16.dp)
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        IconButton(onClick = onBack, modifier = Modifier.size(36.dp)) {
                            Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back", tint = Color.White)
                        }
                        Spacer(modifier = Modifier.width(8.dp))
                        Column {
                            Text(
                                text = "Servora Global HQ Admin",
                                color = Color.White,
                                fontSize = 18.sp,
                                fontWeight = FontWeight.Bold
                            )
                            Text(
                                text = "Platform Oversight, Escrow Ledger & Compliance",
                                color = CyanAccent,
                                fontSize = 11.sp
                            )
                        }
                    }
                }
            }
        }

        // Key KPI Cards Grid
        item {
            Column(modifier = Modifier.padding(16.dp)) {
                Text(
                    text = "Global Marketplace Health",
                    color = TextPrimary,
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold
                )
                Spacer(modifier = Modifier.height(10.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    AdminMetricCard(
                        title = "Gross Merchandise",
                        value = "$2.18M",
                        subtitle = "+18% this month",
                        modifier = Modifier.weight(1f)
                    )
                    AdminMetricCard(
                        title = "Escrow Ledger",
                        value = "$348,900",
                        subtitle = "Protected in bank",
                        modifier = Modifier.weight(1f)
                    )
                }

                Spacer(modifier = Modifier.height(10.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    AdminMetricCard(
                        title = "Active Pros",
                        value = "8,640",
                        subtitle = "100% Background Checked",
                        modifier = Modifier.weight(1f)
                    )
                    AdminMetricCard(
                        title = "Total Users",
                        value = "142,850",
                        subtitle = "Across 24 countries",
                        modifier = Modifier.weight(1f)
                    )
                }
            }
        }

        // Trust & Compliance Queue
        item {
            Column(modifier = Modifier.padding(horizontal = 16.dp)) {
                Text(
                    text = "Verification & Compliance Queue ($verificationQueue)",
                    color = TextPrimary,
                    fontSize = 15.sp,
                    fontWeight = FontWeight.Bold
                )
                Spacer(modifier = Modifier.height(8.dp))

                Card(
                    shape = RoundedCornerShape(14.dp),
                    colors = CardDefaults.cardColors(containerColor = SurfaceCard),
                    border = BorderStroke(1.dp, SlateBorder),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(modifier = Modifier.padding(14.dp)) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Column {
                                Text("Marco Rossi • Electrician Master", color = TextPrimary, fontSize = 14.sp, fontWeight = FontWeight.SemiBold)
                                Text("Rome, Italy • Trade Licence #IT-49102", color = TextMuted, fontSize = 11.sp)
                            }
                            Button(
                                onClick = { /* Approve */ },
                                shape = RoundedCornerShape(8.dp),
                                colors = ButtonDefaults.buttonColors(containerColor = EmeraldTrust),
                                contentPadding = PaddingValues(horizontal = 12.dp, vertical = 4.dp)
                            ) {
                                Text("Approve", fontSize = 11.sp)
                            }
                        }
                    }
                }
            }
        }

        // Active Disputes & Mediation
        item {
            Spacer(modifier = Modifier.height(16.dp))
            Column(modifier = Modifier.padding(horizontal = 16.dp)) {
                Text(
                    text = "Mediation & Dispute Center ($disputes)",
                    color = TextPrimary,
                    fontSize = 15.sp,
                    fontWeight = FontWeight.Bold
                )
                Spacer(modifier = Modifier.height(8.dp))

                Card(
                    shape = RoundedCornerShape(14.dp),
                    colors = CardDefaults.cardColors(containerColor = SurfaceCard),
                    border = BorderStroke(1.dp, SlateBorder),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(modifier = Modifier.padding(14.dp)) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Text("Dispute #DSP-3091", color = EmergencyRed, fontSize = 12.sp, fontWeight = FontWeight.Bold)
                            Text("Escrow Frozen: $120.00", color = TextPrimary, fontSize = 12.sp, fontWeight = FontWeight.SemiBold)
                        }
                        Text(
                            text = "Customer claims plumbing leak reoccurred after 24 hours. Servora Guarantee investigation pending.",
                            color = TextSecondary,
                            fontSize = 12.sp,
                            modifier = Modifier.padding(vertical = 4.dp)
                        )
                        Row(
                            horizontalArrangement = Arrangement.spacedBy(8.dp),
                            modifier = Modifier.padding(top = 6.dp)
                        ) {
                            OutlinedButton(
                                onClick = { /* Release to customer */ },
                                shape = RoundedCornerShape(8.dp),
                                contentPadding = PaddingValues(horizontal = 10.dp, vertical = 4.dp)
                            ) {
                                Text("Refund Customer", fontSize = 11.sp)
                            }
                            Button(
                                onClick = { /* Send inspector */ },
                                shape = RoundedCornerShape(8.dp),
                                colors = ButtonDefaults.buttonColors(containerColor = ElectricBlue),
                                contentPadding = PaddingValues(horizontal = 10.dp, vertical = 4.dp)
                            ) {
                                Text("Dispatch Senior Inspector", fontSize = 11.sp)
                            }
                        }
                    }
                }
            }
        }

        // Back to customer mode
        item {
            Spacer(modifier = Modifier.height(24.dp))
            Box(modifier = Modifier.padding(horizontal = 16.dp)) {
                Button(
                    onClick = { viewModel.switchRole(AppRole.CUSTOMER) },
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(12.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = DeepNavy)
                ) {
                    Text("Exit Admin Mode (Return to User Mode)")
                }
            }
        }
    }
}

@Composable
private fun AdminMetricCard(
    title: String,
    value: String,
    subtitle: String,
    modifier: Modifier = Modifier
) {
    Card(
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = SurfaceCard),
        border = BorderStroke(1.dp, SlateBorder),
        modifier = modifier
    ) {
        Column(modifier = Modifier.padding(14.dp)) {
            Text(text = title, color = TextMuted, fontSize = 11.sp)
            Spacer(modifier = Modifier.height(4.dp))
            Text(text = value, color = TextPrimary, fontSize = 19.sp, fontWeight = FontWeight.Bold)
            Spacer(modifier = Modifier.height(2.dp))
            Text(text = subtitle, color = EmeraldTrust, fontSize = 10.sp, fontWeight = FontWeight.SemiBold)
        }
    }
}
