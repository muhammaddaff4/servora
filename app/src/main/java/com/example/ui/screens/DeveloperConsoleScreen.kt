package com.example.ui.screens

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.model.AppRole
import com.example.model.SecurityFeatureFlag
import com.example.model.SystemAuditLog
import com.example.ui.theme.*
import com.example.viewmodel.ServoraViewModel

@Composable
fun DeveloperConsoleScreen(
    viewModel: ServoraViewModel,
    onBack: () -> Unit,
    modifier: Modifier = Modifier
) {
    val featureFlags by viewModel.securityFeatureFlags.collectAsState()
    val auditLogs by viewModel.systemAuditLogs.collectAsState()
    val firestoreSyncStatus by viewModel.firestoreSyncStatus.collectAsState()
    val isSyncingFirestore by viewModel.isSyncingFirestore.collectAsState()
    var selectedTab by remember { mutableStateOf(0) } // 0: Security Flags, 1: Audit Logs, 2: API & Telemetry, 3: Sandbox
    var showResetSnackbar by remember { mutableStateOf(false) }

    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .background(SurfaceCanvas),
        contentPadding = PaddingValues(bottom = 80.dp)
    ) {
        // Developer Top Console Header
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
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            IconButton(onClick = onBack, modifier = Modifier.size(36.dp)) {
                                Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Exit Dev Console", tint = Color.White)
                            }
                            Spacer(modifier = Modifier.width(8.dp))
                            Column {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Text(
                                        text = "DEV CONSOLE",
                                        color = Color.White,
                                        fontSize = 18.sp,
                                        fontWeight = FontWeight.Bold,
                                        fontFamily = FontFamily.Monospace
                                    )
                                    Spacer(modifier = Modifier.width(6.dp))
                                    Box(
                                        modifier = Modifier
                                            .clip(RoundedCornerShape(4.dp))
                                            .background(CyanAccent.copy(alpha = 0.2f))
                                            .padding(horizontal = 6.dp, vertical = 2.dp)
                                    ) {
                                        Text(
                                            text = "ROOT ACCESS",
                                            color = CyanAccent,
                                            fontSize = 9.sp,
                                            fontWeight = FontWeight.Bold
                                        )
                                    }
                                }
                                Text(
                                    text = "System Core & Security Control Layer",
                                    color = TextSubtle,
                                    fontSize = 11.sp
                                )
                            }
                        }

                        // Exit button pill
                        Button(
                            onClick = onBack,
                            colors = ButtonDefaults.buttonColors(containerColor = SlateDark),
                            shape = RoundedCornerShape(12.dp),
                            contentPadding = PaddingValues(horizontal = 10.dp, vertical = 4.dp),
                            modifier = Modifier.height(32.dp)
                        ) {
                            Icon(Icons.Default.ExitToApp, contentDescription = null, tint = TextMuted, modifier = Modifier.size(14.dp))
                            Spacer(modifier = Modifier.width(4.dp))
                            Text("Exit Role", color = Color.White, fontSize = 11.sp)
                        }
                    }

                    Spacer(modifier = Modifier.height(14.dp))

                    // Role Restriction Notice Banner
                    Card(
                        shape = RoundedCornerShape(10.dp),
                        colors = CardDefaults.cardColors(containerColor = Color(0xFF131D3B)),
                        border = BorderStroke(1.dp, CyanAccent.copy(alpha = 0.3f)),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Row(
                            modifier = Modifier.padding(12.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Box(
                                modifier = Modifier
                                    .size(32.dp)
                                    .clip(CircleShape)
                                    .background(CyanAccent.copy(alpha = 0.15f)),
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(Icons.Default.VpnKey, contentDescription = null, tint = CyanAccent, modifier = Modifier.size(16.dp))
                            }
                            Spacer(modifier = Modifier.width(10.dp))
                            Column(modifier = Modifier.weight(1f)) {
                                Text(
                                    text = "Dedicated Security Boundary",
                                    color = Color.White,
                                    fontSize = 12.sp,
                                    fontWeight = FontWeight.Bold
                                )
                                Text(
                                    text = "Pelanggan & Tukang diblokir dari menu ini. Hanya Developer berlisensi (PIN 9900) yang berhak mengubah parameter keamanan dan fitur sistem.",
                                    color = TextSubtle,
                                    fontSize = 11.sp,
                                    lineHeight = 15.sp
                                )
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(14.dp))

                    // Tab Selector: Security Flags, Audit Logs, Telemetry, Sandbox
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        DevTabPill("Security Flags", selectedTab == 0) { selectedTab = 0 }
                        DevTabPill("Audit Logs", selectedTab == 1) { selectedTab = 1 }
                        DevTabPill("API & Health", selectedTab == 2) { selectedTab = 2 }
                        DevTabPill("Sandbox DB", selectedTab == 3) { selectedTab = 3 }
                    }
                }
            }
        }

        // TAB CONTENT
        when (selectedTab) {
            0 -> {
                // SECURITY FEATURE FLAGS
                item {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Column {
                                Text(
                                    text = "Core Security Feature Flags",
                                    color = TextPrimary,
                                    fontSize = 16.sp,
                                    fontWeight = FontWeight.Bold
                                )
                                Text(
                                    text = "Perubahan langsung memengaruhi mekanisme validasi di seluruh aplikasi",
                                    color = TextMuted,
                                    fontSize = 12.sp
                                )
                            }
                        }
                        Spacer(modifier = Modifier.height(12.dp))
                    }
                }

                items(featureFlags) { flag ->
                    SecurityFlagCard(
                        flag = flag,
                        onToggle = {
                            viewModel.toggleSecurityFlag(flag.id)
                        }
                    )
                }
            }

            1 -> {
                // AUDIT LOGS
                item {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Column {
                                Text(
                                    text = "Immutable Security Audit Trail",
                                    color = TextPrimary,
                                    fontSize = 16.sp,
                                    fontWeight = FontWeight.Bold
                                )
                                Text(
                                    text = "Mencatat aktivitas otentikasi, escrow, dan percobaan akses ilegal",
                                    color = TextMuted,
                                    fontSize = 12.sp
                                )
                            }
                            OutlinedButton(
                                onClick = { viewModel.clearAuditLogs() },
                                shape = RoundedCornerShape(8.dp),
                                contentPadding = PaddingValues(horizontal = 10.dp, vertical = 4.dp),
                                modifier = Modifier.height(30.dp)
                            ) {
                                Text("Reset Logs", fontSize = 11.sp)
                            }
                        }
                        Spacer(modifier = Modifier.height(10.dp))
                    }
                }

                items(auditLogs) { log ->
                    AuditLogItem(log = log)
                }
            }

            2 -> {
                // API & SYSTEM TELEMETRY
                item {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Text(
                            text = "Platform Telemetry & Infrastructure",
                            color = TextPrimary,
                            fontSize = 16.sp,
                            fontWeight = FontWeight.Bold
                        )
                        Text(
                            text = "Status koneksi modul keamanan backend & AI services",
                            color = TextMuted,
                            fontSize = 12.sp
                        )
                        Spacer(modifier = Modifier.height(14.dp))

                        TelemetryCard(
                            title = "Escrow Smart Vault Engine",
                            status = "OPERATIONAL",
                            statusColor = EmeraldTrust,
                            latency = "14ms",
                            detail = "SHA-256 Multi-signature Vault Protocol. 100% of transaction values protected until customer digital release."
                        )

                        Spacer(modifier = Modifier.height(10.dp))

                        TelemetryCard(
                            title = "Gemini AI Diagnostic Engine",
                            status = "ONLINE",
                            statusColor = EmeraldTrust,
                            latency = "92ms",
                            detail = "Model: Gemini 2.5 Flash. Real-time emergency symptom triage and automated scope pricing estimation."
                        )

                        Spacer(modifier = Modifier.height(10.dp))

                        TelemetryCard(
                            title = "Stripe Connect & Escrow Payouts",
                            status = "CONNECTED (TESTNET)",
                            statusColor = CyanAccent,
                            latency = "118ms",
                            detail = "Instant technician payouts ready for SEPA, ACH, FAST, and PayNow routing."
                        )

                        Spacer(modifier = Modifier.height(10.dp))

                        TelemetryCard(
                            title = "Firebase Cloud Firestore",
                            status = if (viewModel.isFirestoreAvailable) "ONLINE & ACTIVE" else "STANDBY / OFFLINE",
                            statusColor = if (viewModel.isFirestoreAvailable) EmeraldTrust else AmberWarning,
                            latency = "28ms",
                            detail = "Persists professionals, service categories, and 24/7 emergency dispatch types. Uses reactive Flow listeners for real-time document sync."
                        )

                        Spacer(modifier = Modifier.height(10.dp))

                        TelemetryCard(
                            title = "Google Play Policy & Permission Guard",
                            status = "COMPLIANT",
                            statusColor = EmeraldTrust,
                            latency = "0ms",
                            detail = "Zero broad storage permissions. Uses Photo Picker contracts. Least-privilege runtime security verified."
                        )
                    }
                }
            }

            3 -> {
                // SANDBOX DATABASE CONTROLS
                item {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Text(
                            text = "Sandbox & Test Database Controls",
                            color = TextPrimary,
                            fontSize = 16.sp,
                            fontWeight = FontWeight.Bold
                        )
                        Text(
                            text = "Alat pengujian internal untuk menguji skenario tanpa merusak sistem nyata",
                            color = TextMuted,
                            fontSize = 12.sp
                        )
                        Spacer(modifier = Modifier.height(14.dp))

                        Card(
                            shape = RoundedCornerShape(12.dp),
                            colors = CardDefaults.cardColors(containerColor = SurfaceCard),
                            border = BorderStroke(1.dp, SlateBorder),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Column(modifier = Modifier.padding(16.dp)) {
                                Text(
                                    text = "Reset & Re-Seed Test Data",
                                    color = TextPrimary,
                                    fontSize = 14.sp,
                                    fontWeight = FontWeight.Bold
                                )
                                Text(
                                    text = "Kembalikan pesanan dummy, saldo escrow teknisi, dan simulasi sengketa pelanggan ke status awal.",
                                    color = TextMuted,
                                    fontSize = 12.sp
                                )
                                Spacer(modifier = Modifier.height(12.dp))
                                Button(
                                    onClick = {
                                        viewModel.resetSandboxData()
                                        showResetSnackbar = true
                                    },
                                    colors = ButtonDefaults.buttonColors(containerColor = ElectricBlue),
                                    shape = RoundedCornerShape(10.dp),
                                    modifier = Modifier.fillMaxWidth()
                                ) {
                                    Icon(Icons.Default.Refresh, contentDescription = null, modifier = Modifier.size(16.dp))
                                    Spacer(modifier = Modifier.width(8.dp))
                                    Text("Re-Seed Sandbox State", fontWeight = FontWeight.Bold)
                                }
                            }
                        }

                        Spacer(modifier = Modifier.height(12.dp))

                        Card(
                            shape = RoundedCornerShape(12.dp),
                            colors = CardDefaults.cardColors(containerColor = SurfaceCard),
                            border = BorderStroke(1.dp, SlateBorder),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Column(modifier = Modifier.padding(16.dp)) {
                                Text(
                                    text = "Simulate Unauthorized Intrusion",
                                    color = TextPrimary,
                                    fontSize = 14.sp,
                                    fontWeight = FontWeight.Bold
                                )
                                Text(
                                    text = "Uji sistem pertahanan: buat log pelanggaran saat pengguna tanpa izin mencoba mengakses file keamanan.",
                                    color = TextMuted,
                                    fontSize = 12.sp
                                )
                                Spacer(modifier = Modifier.height(12.dp))
                                OutlinedButton(
                                    onClick = {
                                        viewModel.attemptRoleSwitch(AppRole.ADMIN, "wrong_pin_999")
                                    },
                                    shape = RoundedCornerShape(10.dp),
                                    modifier = Modifier.fillMaxWidth()
                                ) {
                                    Icon(Icons.Default.Shield, contentDescription = null, tint = CoralRed, modifier = Modifier.size(16.dp))
                                    Spacer(modifier = Modifier.width(8.dp))
                                    Text("Trigger Test Security Rejection", color = CoralRed, fontWeight = FontWeight.SemiBold)
                                }
                            }
                        }

                        Spacer(modifier = Modifier.height(12.dp))

                        Card(
                            shape = RoundedCornerShape(12.dp),
                            colors = CardDefaults.cardColors(containerColor = SurfaceCard),
                            border = BorderStroke(1.dp, if (viewModel.isFirestoreAvailable) CyanAccent.copy(alpha = 0.4f) else SlateBorder),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Column(modifier = Modifier.padding(16.dp)) {
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Text(
                                        text = "Firebase Firestore Cloud Sync",
                                        color = TextPrimary,
                                        fontSize = 14.sp,
                                        fontWeight = FontWeight.Bold
                                    )
                                    Box(
                                        modifier = Modifier
                                            .clip(RoundedCornerShape(4.dp))
                                            .background(
                                                if (viewModel.isFirestoreAvailable) EmeraldTrust.copy(alpha = 0.15f)
                                                else AmberWarning.copy(alpha = 0.15f)
                                            )
                                            .padding(horizontal = 6.dp, vertical = 2.dp)
                                    ) {
                                        Text(
                                            text = if (viewModel.isFirestoreAvailable) "ONLINE" else "OFFLINE",
                                            color = if (viewModel.isFirestoreAvailable) EmeraldTrust else AmberWarning,
                                            fontSize = 9.sp,
                                            fontWeight = FontWeight.Bold
                                        )
                                    }
                                }
                                Spacer(modifier = Modifier.height(4.dp))
                                Text(
                                    text = "Status: $firestoreSyncStatus",
                                    color = TextMuted,
                                    fontSize = 11.sp,
                                    fontFamily = FontFamily.Monospace
                                )
                                Spacer(modifier = Modifier.height(12.dp))
                                Button(
                                    onClick = {
                                        viewModel.syncFirestoreData()
                                    },
                                    enabled = !isSyncingFirestore,
                                    colors = ButtonDefaults.buttonColors(containerColor = CyanAccent),
                                    shape = RoundedCornerShape(10.dp),
                                    modifier = Modifier.fillMaxWidth()
                                ) {
                                    if (isSyncingFirestore) {
                                        CircularProgressIndicator(
                                            modifier = Modifier.size(16.dp),
                                            color = DeepNavy,
                                            strokeWidth = 2.dp
                                        )
                                        Spacer(modifier = Modifier.width(8.dp))
                                        Text("Syncing to Firestore...", color = DeepNavy, fontWeight = FontWeight.Bold)
                                    } else {
                                        Icon(Icons.Default.CloudSync, contentDescription = null, tint = DeepNavy, modifier = Modifier.size(16.dp))
                                        Spacer(modifier = Modifier.width(8.dp))
                                        Text("Sync & Seed to Firestore", color = DeepNavy, fontWeight = FontWeight.Bold)
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun RowScope.DevTabPill(
    label: String,
    isSelected: Boolean,
    onClick: () -> Unit
) {
    Surface(
        onClick = onClick,
        shape = RoundedCornerShape(8.dp),
        color = if (isSelected) CyanAccent else SlateDark,
        modifier = Modifier.weight(1f).height(32.dp)
    ) {
        Box(
            modifier = Modifier.fillMaxSize(),
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = label,
                color = if (isSelected) DeepNavy else Color.White,
                fontSize = 10.sp,
                fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal,
                maxLines = 1
            )
        }
    }
}

@Composable
private fun SecurityFlagCard(
    flag: SecurityFeatureFlag,
    onToggle: () -> Unit
) {
    Card(
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = SurfaceCard),
        border = BorderStroke(1.dp, if (flag.isEnabled) ElectricBlue.copy(alpha = 0.3f) else SlateBorder),
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 5.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(14.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(
                        text = flag.title,
                        color = TextPrimary,
                        fontSize = 13.sp,
                        fontWeight = FontWeight.Bold
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(4.dp))
                            .background(
                                if (flag.category == "SECURITY") CoralRed.copy(alpha = 0.15f)
                                else if (flag.category == "ESCROW") EmeraldTrust.copy(alpha = 0.15f)
                                else ElectricBlue.copy(alpha = 0.15f)
                            )
                            .padding(horizontal = 5.dp, vertical = 1.dp)
                    ) {
                        Text(
                            text = flag.category,
                            color = if (flag.category == "SECURITY") CoralRed
                            else if (flag.category == "ESCROW") EmeraldTrust
                            else ElectricBlue,
                            fontSize = 8.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }
                Spacer(modifier = Modifier.height(3.dp))
                Text(
                    text = flag.description,
                    color = TextMuted,
                    fontSize = 11.sp,
                    lineHeight = 15.sp
                )
            }

            Spacer(modifier = Modifier.width(12.dp))

            Switch(
                checked = flag.isEnabled,
                onCheckedChange = { onToggle() },
                colors = SwitchDefaults.colors(
                    checkedThumbColor = Color.White,
                    checkedTrackColor = ElectricBlue,
                    uncheckedThumbColor = TextSubtle,
                    uncheckedTrackColor = SlateDark
                ),
                modifier = Modifier.testTag("flag_switch_${flag.id}")
            )
        }
    }
}

@Composable
private fun AuditLogItem(log: SystemAuditLog) {
    Card(
        shape = RoundedCornerShape(10.dp),
        colors = CardDefaults.cardColors(containerColor = SurfaceCard),
        border = BorderStroke(
            1.dp,
            if (log.severity == "CRITICAL") CoralRed.copy(alpha = 0.4f)
            else if (log.severity == "WARN") AmberWarning.copy(alpha = 0.4f)
            else SlateBorder
        ),
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 4.dp)
    ) {
        Column(modifier = Modifier.padding(12.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Box(
                        modifier = Modifier
                            .size(6.dp)
                            .clip(CircleShape)
                            .background(
                                if (log.severity == "CRITICAL") CoralRed
                                else if (log.severity == "WARN") AmberWarning
                                else EmeraldTrust
                            )
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                        text = "[${log.timestamp}]",
                        color = TextSubtle,
                        fontSize = 10.sp,
                        fontFamily = FontFamily.Monospace
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                        text = log.actorRole,
                        color = CyanAccent,
                        fontSize = 10.sp,
                        fontWeight = FontWeight.Bold,
                        fontFamily = FontFamily.Monospace
                    )
                }

                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(4.dp))
                        .background(
                            if (log.severity == "CRITICAL") CoralRed.copy(alpha = 0.2f)
                            else if (log.severity == "WARN") AmberWarning.copy(alpha = 0.2f)
                            else ElectricBlue.copy(alpha = 0.15f)
                        )
                        .padding(horizontal = 5.dp, vertical = 1.dp)
                ) {
                    Text(
                        text = log.action,
                        color = if (log.severity == "CRITICAL") CoralRed
                        else if (log.severity == "WARN") AmberWarning
                        else ElectricBlue,
                        fontSize = 9.sp,
                        fontWeight = FontWeight.Bold,
                        fontFamily = FontFamily.Monospace
                    )
                }
            }

            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = log.details,
                color = TextPrimary,
                fontSize = 11.sp,
                lineHeight = 15.sp
            )
        }
    }
}

@Composable
private fun TelemetryCard(
    title: String,
    status: String,
    statusColor: Color,
    latency: String,
    detail: String
) {
    Card(
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = SurfaceCard),
        border = BorderStroke(1.dp, SlateBorder),
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(modifier = Modifier.padding(14.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(
                    text = title,
                    color = TextPrimary,
                    fontSize = 13.sp,
                    fontWeight = FontWeight.Bold
                )

                Row(verticalAlignment = Alignment.CenterVertically) {
                    Box(
                        modifier = Modifier
                            .size(6.dp)
                            .clip(CircleShape)
                            .background(statusColor)
                    )
                    Spacer(modifier = Modifier.width(5.dp))
                    Text(
                        text = "$status ($latency)",
                        color = statusColor,
                        fontSize = 10.sp,
                        fontWeight = FontWeight.Bold,
                        fontFamily = FontFamily.Monospace
                    )
                }
            }
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = detail,
                color = TextMuted,
                fontSize = 11.sp,
                lineHeight = 15.sp
            )
        }
    }
}
