package com.example.ui.components

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.outlined.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.model.AppRole
import com.example.model.CustomerScreen
import com.example.model.ProScreen
import com.example.ui.theme.*

@Composable
fun ServoraTopBar(
    role: AppRole,
    locationText: String,
    currencyCode: String,
    onLocationClick: () -> Unit,
    onNotificationClick: () -> Unit,
    onCurrencyClick: () -> Unit,
    onRoleToggle: () -> Unit,
    modifier: Modifier = Modifier
) {
    Surface(
        modifier = modifier
            .fillMaxWidth()
            .statusBarsPadding(),
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
            // Brand Logo & Location
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier
                    .weight(1f)
                    .clickable { onLocationClick() }
            ) {
                // Servora Icon Hexagon / Badge
                Box(
                    modifier = Modifier
                        .size(36.dp)
                        .clip(RoundedCornerShape(10.dp))
                        .background(
                            Brush.linearGradient(
                                listOf(ElectricBlue, CyanAccent)
                            )
                        ),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = "S",
                        color = Color.White,
                        fontWeight = FontWeight.Black,
                        fontSize = 20.sp
                    )
                }

                Spacer(modifier = Modifier.width(10.dp))

                Column {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text(
                            text = "SERVORA",
                            color = Color.White,
                            fontWeight = FontWeight.ExtraBold,
                            fontSize = 15.sp,
                            letterSpacing = 1.5.sp
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        Box(
                            modifier = Modifier
                                .clip(RoundedCornerShape(4.dp))
                                .background(CyanAccent.copy(alpha = 0.2f))
                                .padding(horizontal = 5.dp, vertical = 1.dp)
                        ) {
                            Text(
                                text = "GLOBAL",
                                color = CyanAccent,
                                fontSize = 9.sp,
                                fontWeight = FontWeight.Bold
                            )
                        }
                    }
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            imageVector = Icons.Default.LocationOn,
                            contentDescription = null,
                            tint = CyanAccent,
                            modifier = Modifier.size(12.dp)
                        )
                        Spacer(modifier = Modifier.width(3.dp))
                        Text(
                            text = locationText,
                            color = TextSubtle,
                            fontSize = 11.sp,
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis
                        )
                        Icon(
                            imageVector = Icons.Default.KeyboardArrowDown,
                            contentDescription = null,
                            tint = TextSubtle,
                            modifier = Modifier.size(14.dp)
                        )
                    }
                }
            }

            // Right Actions: Currency Pill, Mode Switcher, Notifications
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                // Currency selector pill
                Surface(
                    onClick = onCurrencyClick,
                    shape = RoundedCornerShape(16.dp),
                    color = SlateDark,
                    modifier = Modifier.height(30.dp)
                ) {
                    Row(
                        modifier = Modifier.padding(horizontal = 8.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = currencyCode,
                            color = Color.White,
                            fontSize = 11.sp,
                            fontWeight = FontWeight.SemiBold
                        )
                    }
                }

                // Role / Security Gateway pill (User, Pro, Admin, Dev)
                Surface(
                    onClick = onRoleToggle,
                    shape = RoundedCornerShape(16.dp),
                    color = when (role) {
                        AppRole.PROFESSIONAL -> EmeraldTrust.copy(alpha = 0.2f)
                        AppRole.ADMIN -> AmberWarning.copy(alpha = 0.2f)
                        AppRole.DEVELOPER -> CoralRed.copy(alpha = 0.2f)
                        AppRole.CUSTOMER -> CyanAccent.copy(alpha = 0.15f)
                    },
                    border = BorderStroke(
                        1.dp,
                        when (role) {
                            AppRole.PROFESSIONAL -> EmeraldTrust
                            AppRole.ADMIN -> AmberWarning
                            AppRole.DEVELOPER -> CoralRed
                            AppRole.CUSTOMER -> SlateBorderDark
                        }
                    ),
                    modifier = Modifier.height(30.dp)
                ) {
                    Row(
                        modifier = Modifier.padding(horizontal = 8.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            imageVector = when (role) {
                                AppRole.PROFESSIONAL -> Icons.Default.Engineering
                                AppRole.ADMIN -> Icons.Default.AdminPanelSettings
                                AppRole.DEVELOPER -> Icons.Default.Terminal
                                AppRole.CUSTOMER -> Icons.Default.Person
                            },
                            contentDescription = null,
                            tint = when (role) {
                                AppRole.PROFESSIONAL -> EmeraldTrust
                                AppRole.ADMIN -> AmberWarning
                                AppRole.DEVELOPER -> CoralRed
                                AppRole.CUSTOMER -> CyanAccent
                            },
                            modifier = Modifier.size(14.dp)
                        )
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(
                            text = when (role) {
                                AppRole.PROFESSIONAL -> "TUKANG"
                                AppRole.ADMIN -> "ADMIN"
                                AppRole.DEVELOPER -> "DEV"
                                AppRole.CUSTOMER -> "USER"
                            },
                            color = when (role) {
                                AppRole.PROFESSIONAL -> EmeraldTrust
                                AppRole.ADMIN -> AmberWarning
                                AppRole.DEVELOPER -> CoralRed
                                AppRole.CUSTOMER -> Color.White
                            },
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }

                // Notification Bell
                Box {
                    IconButton(
                        onClick = onNotificationClick,
                        modifier = Modifier
                            .size(32.dp)
                            .testTag("notification_button")
                    ) {
                        Icon(
                            imageVector = Icons.Outlined.Notifications,
                            contentDescription = "Notifications",
                            tint = Color.White,
                            modifier = Modifier.size(20.dp)
                        )
                    }
                    Box(
                        modifier = Modifier
                            .size(7.dp)
                            .clip(CircleShape)
                            .background(EmergencyRed)
                            .align(Alignment.TopEnd)
                    )
                }
            }
        }
    }
}

@Composable
fun ServoraBottomNavigation(
    role: AppRole,
    customerScreen: CustomerScreen,
    proScreen: ProScreen,
    onNavigateCustomer: (CustomerScreen) -> Unit,
    onNavigatePro: (ProScreen) -> Unit,
    modifier: Modifier = Modifier
) {
    Surface(
        modifier = modifier
            .fillMaxWidth()
            .navigationBarsPadding(),
        color = DeepNavy,
        tonalElevation = 8.dp,
        border = BorderStroke(1.dp, SlateBorderDark)
    ) {
        if (role == AppRole.CUSTOMER) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 6.dp),
                horizontalArrangement = Arrangement.SpaceAround,
                verticalAlignment = Alignment.CenterVertically
            ) {
                NavItem(
                    label = "Home",
                    icon = Icons.Default.Home,
                    isSelected = customerScreen == CustomerScreen.HOME,
                    onClick = { onNavigateCustomer(CustomerScreen.HOME) }
                )
                NavItem(
                    label = "Explore",
                    icon = Icons.Default.Search,
                    isSelected = customerScreen == CustomerScreen.EXPLORE,
                    onClick = { onNavigateCustomer(CustomerScreen.EXPLORE) }
                )
                NavItem(
                    label = "Bookings",
                    icon = Icons.Default.Assignment,
                    isSelected = customerScreen == CustomerScreen.BOOKINGS || customerScreen == CustomerScreen.LIVE_TRACKING,
                    onClick = { onNavigateCustomer(CustomerScreen.BOOKINGS) }
                )
                NavItem(
                    label = "Messages",
                    icon = Icons.Default.ChatBubbleOutline,
                    isSelected = customerScreen == CustomerScreen.MESSAGES,
                    onClick = { onNavigateCustomer(CustomerScreen.MESSAGES) }
                )
                NavItem(
                    label = "Profile",
                    icon = Icons.Default.PersonOutline,
                    isSelected = customerScreen == CustomerScreen.PROFILE,
                    onClick = { onNavigateCustomer(CustomerScreen.PROFILE) }
                )
            }
        } else {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 6.dp),
                horizontalArrangement = Arrangement.SpaceAround,
                verticalAlignment = Alignment.CenterVertically
            ) {
                NavItem(
                    label = "Dashboard",
                    icon = Icons.Default.Dashboard,
                    isSelected = proScreen == ProScreen.DASHBOARD,
                    onClick = { onNavigatePro(ProScreen.DASHBOARD) }
                )
                NavItem(
                    label = "Jobs",
                    icon = Icons.Default.WorkOutline,
                    isSelected = proScreen == ProScreen.JOBS,
                    onClick = { onNavigatePro(ProScreen.JOBS) }
                )
                NavItem(
                    label = "Messages",
                    icon = Icons.Default.ChatBubbleOutline,
                    isSelected = proScreen == ProScreen.MESSAGES,
                    onClick = { onNavigatePro(ProScreen.MESSAGES) }
                )
                NavItem(
                    label = "Earnings",
                    icon = Icons.Default.AccountBalanceWallet,
                    isSelected = proScreen == ProScreen.EARNINGS,
                    onClick = { onNavigatePro(ProScreen.EARNINGS) }
                )
                NavItem(
                    label = "Profile",
                    icon = Icons.Default.VerifiedUser,
                    isSelected = proScreen == ProScreen.PROFILE || proScreen == ProScreen.VERIFICATION_CENTER,
                    onClick = { onNavigatePro(ProScreen.PROFILE) }
                )
            }
        }
    }
}

@Composable
private fun NavItem(
    label: String,
    icon: ImageVector,
    isSelected: Boolean,
    onClick: () -> Unit
) {
    val tint by animateColorAsState(
        targetValue = if (isSelected) CyanAccent else TextSubtle,
        animationSpec = tween(200), label = "nav_color"
    )

    Column(
        modifier = Modifier
            .clip(RoundedCornerShape(12.dp))
            .clickable { onClick() }
            .padding(horizontal = 14.dp, vertical = 6.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Icon(
            imageVector = icon,
            contentDescription = label,
            tint = tint,
            modifier = Modifier.size(22.dp)
        )
        Spacer(modifier = Modifier.height(2.dp))
        Text(
            text = label,
            color = tint,
            fontSize = 10.sp,
            fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal
        )
    }
}

@Composable
fun VerifiedProBadge(
    modifier: Modifier = Modifier,
    text: String = "Verified Pro"
) {
    Surface(
        shape = RoundedCornerShape(20.dp),
        color = EmeraldTrust.copy(alpha = 0.12f),
        border = BorderStroke(1.dp, EmeraldTrust.copy(alpha = 0.3f)),
        modifier = modifier
    ) {
        Row(
            modifier = Modifier.padding(horizontal = 8.dp, vertical = 3.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                imageVector = Icons.Default.CheckCircle,
                contentDescription = null,
                tint = EmeraldTrust,
                modifier = Modifier.size(13.dp)
            )
            Spacer(modifier = Modifier.width(4.dp))
            Text(
                text = text,
                color = EmeraldTrust,
                fontSize = 10.sp,
                fontWeight = FontWeight.Bold
            )
        }
    }
}

@Composable
fun ServoraProtectionCard(
    onLearnMoreClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(
            containerColor = DeepNavy
        ),
        border = BorderStroke(1.dp, SlateBorderDark)
    ) {
        Column(
            modifier = Modifier.padding(16.dp)
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween,
                modifier = Modifier.fillMaxWidth()
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Box(
                        modifier = Modifier
                            .size(28.dp)
                            .clip(CircleShape)
                            .background(ElectricBlue.copy(alpha = 0.2f)),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.Shield,
                            contentDescription = null,
                            tint = CyanAccent,
                            modifier = Modifier.size(16.dp)
                        )
                    }
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = "Servora Protection",
                        color = Color.White,
                        fontWeight = FontWeight.Bold,
                        fontSize = 15.sp
                    )
                }

                TextButton(onClick = onLearnMoreClick) {
                    Text(
                        text = "Details",
                        color = CyanAccent,
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Bold
                    )
                }
            }

            Spacer(modifier = Modifier.height(10.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                ProtectionCheckItem(label = "Verified Pros")
                ProtectionCheckItem(label = "Escrow Payment")
                ProtectionCheckItem(label = "Insurance Backed")
                ProtectionCheckItem(label = "24/7 Support")
            }
        }
    }
}

@Composable
private fun ProtectionCheckItem(label: String) {
    Row(verticalAlignment = Alignment.CenterVertically) {
        Icon(
            imageVector = Icons.Default.Check,
            contentDescription = null,
            tint = EmeraldTrust,
            modifier = Modifier.size(13.dp)
        )
        Spacer(modifier = Modifier.width(3.dp))
        Text(
            text = label,
            color = TextSubtle,
            fontSize = 10.sp,
            fontWeight = FontWeight.Medium
        )
    }
}

@Composable
fun RatingStarsBar(
    rating: Double,
    modifier: Modifier = Modifier,
    size: Int = 14
) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = modifier
    ) {
        Icon(
            imageVector = Icons.Default.Star,
            contentDescription = null,
            tint = AmberWarning,
            modifier = Modifier.size(size.dp)
        )
        Spacer(modifier = Modifier.width(3.dp))
        Text(
            text = String.format(java.util.Locale.US, "%.1f", rating),
            color = TextPrimary,
            fontWeight = FontWeight.Bold,
            fontSize = (size - 1).sp
        )
    }
}

fun getCategoryIconVector(iconName: String): ImageVector {
    return when (iconName) {
        "ac_unit" -> Icons.Default.AcUnit
        "bolt" -> Icons.Default.Bolt
        "water_drop" -> Icons.Default.WaterDrop
        "videocam" -> Icons.Default.Videocam
        "cleaning_services" -> Icons.Default.CleaningServices
        "carpenter" -> Icons.Default.Build
        "foundation" -> Icons.Default.HomeRepairService
        "format_paint" -> Icons.Default.FormatPaint
        "kitchen" -> Icons.Default.Kitchen
        "lock", "lock_clock" -> Icons.Default.Lock
        "wifi" -> Icons.Default.Wifi
        "yard" -> Icons.Default.Yard
        "local_shipping" -> Icons.Default.LocalShipping
        "pest_control" -> Icons.Default.BugReport
        "build" -> Icons.Default.Handyman
        "crisis_alert" -> Icons.Default.CrisisAlert
        "hub" -> Icons.Default.Sensors
        "local_fire_department" -> Icons.Default.LocalFireDepartment
        "security" -> Icons.Default.Security
        "severe_cold" -> Icons.Default.SevereCold
        else -> Icons.Default.Construction
    }
}
