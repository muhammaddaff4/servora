package com.example.ui.screens

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.*
import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.outlined.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.R
import com.example.model.*
import com.example.ui.components.*
import com.example.ui.theme.*
import com.example.viewmodel.ServoraViewModel

@Composable
fun HomeScreen(
    viewModel: ServoraViewModel,
    onNavigateExplore: (String?) -> Unit,
    onNavigateEmergency: () -> Unit,
    onNavigateSmartSearch: () -> Unit,
    onOpenLiveTracking: (Booking) -> Unit,
    onOpenProDetail: (Professional) -> Unit,
    onOpenTrustSafety: () -> Unit,
    modifier: Modifier = Modifier
) {
    val activeBooking by viewModel.activeTrackingBooking.collectAsState()
    val categories = viewModel.categories
    val professionals by viewModel.filteredProfessionals.collectAsState()
    val savedProIds by viewModel.savedProIds.collectAsState()
    val currency by viewModel.selectedCurrency.collectAsState()

    // Pulsing emergency animation
    val infiniteTransition = rememberInfiniteTransition(label = "pulse")
    val pulseScale by infiniteTransition.animateFloat(
        initialValue = 1f,
        targetValue = 1.08f,
        animationSpec = infiniteRepeatable(
            animation = tween(900, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ), label = "pulseScale"
    )

    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .background(SurfaceCanvas),
        contentPadding = PaddingValues(bottom = 90.dp)
    ) {
        // Hero Section with Dark Navy Background
        item {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(bottomStart = 28.dp, bottomEnd = 28.dp))
                    .background(
                        Brush.verticalGradient(
                            listOf(DeepNavy, CharcoalDark)
                        )
                    )
                    .padding(horizontal = 20.dp, vertical = 20.dp)
            ) {
                Column {
                    // Greeting & Subtitle
                    Text(
                        text = "Good morning, Gabriel",
                        color = TextSubtle,
                        fontSize = 13.sp,
                        fontWeight = FontWeight.Medium
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = "Need a professional?\nFind trusted help near you.",
                        color = Color.White,
                        fontSize = 24.sp,
                        fontWeight = FontWeight.Bold,
                        lineHeight = 30.sp
                    )

                    Spacer(modifier = Modifier.height(18.dp))

                    // Big Search Bar (Tap opens Smart Discovery)
                    Surface(
                        onClick = onNavigateSmartSearch,
                        shape = RoundedCornerShape(16.dp),
                        color = SlateDark,
                        border = BorderStroke(1.dp, SlateBorderDark),
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(54.dp)
                            .testTag("search_bar")
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier.padding(horizontal = 16.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Default.Search,
                                contentDescription = null,
                                tint = CyanAccent,
                                modifier = Modifier.size(20.dp)
                            )
                            Spacer(modifier = Modifier.width(12.dp))
                            Text(
                                text = "Search for a service or describe a problem...",
                                color = TextSubtle,
                                fontSize = 13.sp,
                                maxLines = 1,
                                overflow = TextOverflow.Ellipsis,
                                modifier = Modifier.weight(1f)
                            )
                            Box(
                                modifier = Modifier
                                    .clip(RoundedCornerShape(8.dp))
                                    .background(ElectricBlue.copy(alpha = 0.3f))
                                    .padding(horizontal = 8.dp, vertical = 4.dp)
                            ) {
                                Text(
                                    text = "AI Match",
                                    color = CyanAccent,
                                    fontSize = 10.sp,
                                    fontWeight = FontWeight.Bold
                                )
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(16.dp))

                    // Quick Action Badges (Grid-like Row)
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        QuickActionPill(
                            title = "Emergency",
                            icon = Icons.Default.FlashOn,
                            color = EmergencyRed,
                            onClick = onNavigateEmergency,
                            modifier = Modifier.scale(pulseScale)
                        )
                        QuickActionPill(
                            title = "Electrical",
                            icon = Icons.Default.Bolt,
                            color = AmberWarning,
                            onClick = { onNavigateExplore("electrical") }
                        )
                        QuickActionPill(
                            title = "AC & HVAC",
                            icon = Icons.Default.AcUnit,
                            color = CyanAccent,
                            onClick = { onNavigateExplore("hvac") }
                        )
                        QuickActionPill(
                            title = "Plumbing",
                            icon = Icons.Default.WaterDrop,
                            color = ElectricBlueLight,
                            onClick = { onNavigateExplore("plumbing") }
                        )
                    }

                    Spacer(modifier = Modifier.height(10.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        QuickActionPill(
                            title = "Cleaning",
                            icon = Icons.Default.CleaningServices,
                            color = EmeraldTrust,
                            onClick = { onNavigateExplore("cleaning") }
                        )
                        QuickActionPill(
                            title = "CCTV",
                            icon = Icons.Default.Videocam,
                            color = Color(0xFFA855F7),
                            onClick = { onNavigateExplore("cctv") }
                        )
                        QuickActionPill(
                            title = "Carpenter",
                            icon = Icons.Default.Build,
                            color = Color(0xFFF97316),
                            onClick = { onNavigateExplore("carpenter") }
                        )
                        QuickActionPill(
                            title = "Handyman",
                            icon = Icons.Default.HomeRepairService,
                            color = CyanGlow,
                            onClick = { onNavigateExplore("handyman") }
                        )
                    }
                }
            }
        }

        // Active Booking Live Tracking Card (If active)
        activeBooking?.let { booking ->
            if (booking.status != BookingStatus.COMPLETED && booking.status != BookingStatus.CANCELLED) {
                item {
                    Spacer(modifier = Modifier.height(16.dp))
                    Box(modifier = Modifier.padding(horizontal = 16.dp)) {
                        Card(
                            onClick = { onOpenLiveTracking(booking) },
                            shape = RoundedCornerShape(20.dp),
                            colors = CardDefaults.cardColors(containerColor = DeepNavy),
                            border = BorderStroke(1.dp, ElectricBlue.copy(alpha = 0.5f)),
                            modifier = Modifier
                                .fillMaxWidth()
                                .testTag("active_booking_card")
                        ) {
                            Column(modifier = Modifier.padding(16.dp)) {
                                Row(
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    modifier = Modifier.fillMaxWidth()
                                ) {
                                    Row(verticalAlignment = Alignment.CenterVertically) {
                                        Box(
                                            modifier = Modifier
                                                .size(10.dp)
                                                .clip(CircleShape)
                                                .background(EmeraldTrust)
                                        )
                                        Spacer(modifier = Modifier.width(8.dp))
                                        Text(
                                            text = "LIVE ACTIVE JOB",
                                            color = EmeraldTrust,
                                            fontSize = 11.sp,
                                            fontWeight = FontWeight.Bold,
                                            letterSpacing = 1.sp
                                        )
                                    }

                                    Surface(
                                        shape = RoundedCornerShape(12.dp),
                                        color = ElectricBlue.copy(alpha = 0.25f)
                                    ) {
                                        Text(
                                            text = "ETA: ${booking.liveEtaMinutes} mins",
                                            color = CyanAccent,
                                            fontSize = 11.sp,
                                            fontWeight = FontWeight.Bold,
                                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                                        )
                                    }
                                }

                                Spacer(modifier = Modifier.height(12.dp))

                                Row(
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Box(
                                        modifier = Modifier
                                            .size(46.dp)
                                            .clip(CircleShape)
                                            .background(SlateDark),
                                        contentAlignment = Alignment.Center
                                    ) {
                                        if (booking.professional.avatarRes != null) {
                                            Image(
                                                painter = painterResource(id = booking.professional.avatarRes),
                                                contentDescription = null,
                                                contentScale = ContentScale.Crop,
                                                modifier = Modifier.fillMaxSize()
                                            )
                                        } else {
                                            Text(
                                                text = booking.professional.name.take(2),
                                                color = Color.White,
                                                fontWeight = FontWeight.Bold
                                            )
                                        }
                                    }

                                    Spacer(modifier = Modifier.width(12.dp))

                                    Column(modifier = Modifier.weight(1f)) {
                                        Text(
                                            text = booking.professional.name,
                                            color = Color.White,
                                            fontSize = 15.sp,
                                            fontWeight = FontWeight.Bold
                                        )
                                        Text(
                                            text = "${booking.serviceCategoryName} • Status: ${booking.status.name.replace("_", " ")}",
                                            color = TextSubtle,
                                            fontSize = 12.sp
                                        )
                                    }

                                    Icon(
                                        imageVector = Icons.Default.DirectionsCar,
                                        contentDescription = null,
                                        tint = CyanAccent,
                                        modifier = Modifier.size(24.dp)
                                    )
                                }

                                Spacer(modifier = Modifier.height(14.dp))

                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Text(
                                        text = "Payment in Escrow: ${viewModel.formatPrice(booking.baseCostUsd)}",
                                        color = TextSubtle,
                                        fontSize = 11.sp
                                    )

                                    Row(verticalAlignment = Alignment.CenterVertically) {
                                        Text(
                                            text = "Open Live Map",
                                            color = CyanAccent,
                                            fontSize = 12.sp,
                                            fontWeight = FontWeight.Bold
                                        )
                                        Spacer(modifier = Modifier.width(4.dp))
                                        Icon(
                                            imageVector = Icons.Default.ArrowForward,
                                            contentDescription = null,
                                            tint = CyanAccent,
                                            modifier = Modifier.size(14.dp)
                                        )
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }

        // Horizontal Category Carousel
        item {
            Spacer(modifier = Modifier.height(24.dp))
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 20.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Text(
                        text = "Global Service Categories",
                        color = TextPrimary,
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold
                    )
                    Text(
                        text = "Over 17 verified service specialties available",
                        color = TextMuted,
                        fontSize = 12.sp
                    )
                }

                TextButton(onClick = { onNavigateExplore(null) }) {
                    Text("See All", color = ElectricBlue, fontWeight = FontWeight.Bold, fontSize = 13.sp)
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            LazyRow(
                contentPadding = PaddingValues(horizontal = 16.dp),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                items(categories) { category ->
                    CategoryCarouselCard(
                        category = category,
                        formattedPrice = viewModel.formatPrice(category.startingPriceUsd),
                        onClick = { onNavigateExplore(category.id) }
                    )
                }
            }
        }

        // Servora Protection Trust Card
        item {
            Spacer(modifier = Modifier.height(24.dp))
            Box(modifier = Modifier.padding(horizontal = 16.dp)) {
                ServoraProtectionCard(onLearnMoreClick = onOpenTrustSafety)
            }
        }

        // Featured Verified Professionals Showcase
        item {
            Spacer(modifier = Modifier.height(24.dp))
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 20.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Text(
                        text = "Top Verified Specialists",
                        color = TextPrimary,
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold
                    )
                    Text(
                        text = "Screened with background & skill verification",
                        color = TextMuted,
                        fontSize = 12.sp
                    )
                }

                TextButton(onClick = { onNavigateExplore(null) }) {
                    Text("Explore All", color = ElectricBlue, fontWeight = FontWeight.Bold, fontSize = 13.sp)
                }
            }
            Spacer(modifier = Modifier.height(12.dp))
        }

        items(professionals.take(4)) { pro ->
            Box(modifier = Modifier.padding(horizontal = 16.dp, vertical = 6.dp)) {
                ProCardItem(
                    pro = pro,
                    formattedStartingPrice = viewModel.formatPrice(pro.startingPriceUsd),
                    isSaved = savedProIds.contains(pro.id),
                    onSaveClick = { viewModel.toggleSavePro(pro.id) },
                    onViewProfile = { onOpenProDetail(pro) },
                    onBookClick = { viewModel.startBookingFlow(pro) }
                )
            }
        }
    }
}

@Composable
private fun QuickActionPill(
    title: String,
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    color: Color,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = modifier
            .clip(RoundedCornerShape(12.dp))
            .clickable { onClick() }
            .padding(horizontal = 4.dp, vertical = 6.dp)
    ) {
        Box(
            modifier = Modifier
                .size(44.dp)
                .clip(CircleShape)
                .background(color.copy(alpha = 0.15f))
                .border(1.dp, color.copy(alpha = 0.3f), CircleShape),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector = icon,
                contentDescription = title,
                tint = color,
                modifier = Modifier.size(20.dp)
            )
        }
        Spacer(modifier = Modifier.height(6.dp))
        Text(
            text = title,
            color = Color.White,
            fontSize = 11.sp,
            fontWeight = FontWeight.Medium
        )
    }
}

@Composable
fun CategoryCarouselCard(
    category: ServiceCategory,
    formattedPrice: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Card(
        onClick = onClick,
        shape = RoundedCornerShape(18.dp),
        colors = CardDefaults.cardColors(containerColor = SurfaceCard),
        border = BorderStroke(1.dp, SlateBorder),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
        modifier = modifier
            .width(150.dp)
            .height(170.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(14.dp),
            verticalArrangement = Arrangement.SpaceBetween
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.Top
            ) {
                Box(
                    modifier = Modifier
                        .size(42.dp)
                        .clip(RoundedCornerShape(12.dp))
                        .background(
                            if (category.isEmergency) EmergencyRed.copy(alpha = 0.12f)
                            else ElectricBlue.copy(alpha = 0.1f)
                        ),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = getCategoryIconVector(category.iconName),
                        contentDescription = null,
                        tint = if (category.isEmergency) EmergencyRed else ElectricBlue,
                        modifier = Modifier.size(22.dp)
                    )
                }

                if (category.isPopular) {
                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(6.dp))
                            .background(EmeraldTrust.copy(alpha = 0.15f))
                            .padding(horizontal = 6.dp, vertical = 2.dp)
                    ) {
                        Text(
                            text = "HOT",
                            color = EmeraldTrust,
                            fontSize = 9.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }
            }

            Column {
                Text(
                    text = category.name,
                    color = TextPrimary,
                    fontWeight = FontWeight.Bold,
                    fontSize = 13.sp,
                    maxLines = 2,
                    overflow = TextOverflow.Ellipsis
                )
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = "From $formattedPrice",
                    color = ElectricBlue,
                    fontWeight = FontWeight.SemiBold,
                    fontSize = 12.sp
                )
                Text(
                    text = "${category.activeProsCount} pros online",
                    color = TextMuted,
                    fontSize = 10.sp
                )
            }
        }
    }
}

@Composable
fun ProCardItem(
    pro: Professional,
    formattedStartingPrice: String,
    isSaved: Boolean,
    onSaveClick: () -> Unit,
    onViewProfile: () -> Unit,
    onBookClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Card(
        shape = RoundedCornerShape(18.dp),
        colors = CardDefaults.cardColors(containerColor = SurfaceCard),
        border = BorderStroke(1.dp, SlateBorder),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
        modifier = modifier.fillMaxWidth()
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            // Header: Avatar, Name, Title, Favorite Heart
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
                    if (pro.avatarRes != null) {
                        Image(
                            painter = painterResource(id = pro.avatarRes),
                            contentDescription = pro.name,
                            contentScale = ContentScale.Crop,
                            modifier = Modifier.fillMaxSize()
                        )
                    } else {
                        Text(
                            text = pro.name.split(" ").mapNotNull { it.firstOrNull()?.toString() }.take(2).joinToString(""),
                            color = Color.White,
                            fontWeight = FontWeight.Bold,
                            fontSize = 18.sp
                        )
                    }
                }

                Spacer(modifier = Modifier.width(12.dp))

                Column(modifier = Modifier.weight(1f)) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text(
                            text = pro.name,
                            color = TextPrimary,
                            fontSize = 16.sp,
                            fontWeight = FontWeight.Bold
                        )
                        if (pro.isVerified) {
                            Spacer(modifier = Modifier.width(6.dp))
                            Icon(
                                imageVector = Icons.Default.CheckCircle,
                                contentDescription = "Verified",
                                tint = EmeraldTrust,
                                modifier = Modifier.size(15.dp)
                            )
                        }
                    }
                    Text(
                        text = pro.title,
                        color = TextSecondary,
                        fontSize = 12.sp,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier.padding(top = 2.dp)
                    ) {
                        RatingStarsBar(rating = pro.rating, size = 13)
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(
                            text = "(${pro.reviewCount}) • ${pro.completedJobs} jobs",
                            color = TextMuted,
                            fontSize = 11.sp
                        )
                    }
                }

                IconButton(onClick = onSaveClick, modifier = Modifier.size(36.dp)) {
                    Icon(
                        imageVector = if (isSaved) Icons.Default.Favorite else Icons.Default.FavoriteBorder,
                        contentDescription = "Save Pro",
                        tint = if (isSaved) EmergencyRed else TextSubtle
                    )
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            // Badges & Metrics Row
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                MetricChip(label = "${pro.distanceKm} km away")
                MetricChip(label = pro.availability, isHighlight = true)
                MetricChip(label = "${pro.responseTimeMin}m response")
                if (pro.hasInsurance) {
                    MetricChip(label = "Insured", isTrust = true)
                }
            }

            Spacer(modifier = Modifier.height(14.dp))
            HorizontalDivider(color = SlateBorder.copy(alpha = 0.7f))
            Spacer(modifier = Modifier.height(12.dp))

            // Footer: Starting Price & CTAs
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Text(text = "Starting from", color = TextMuted, fontSize = 10.sp)
                    Text(
                        text = formattedStartingPrice,
                        color = TextPrimary,
                        fontSize = 17.sp,
                        fontWeight = FontWeight.ExtraBold
                    )
                }

                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    OutlinedButton(
                        onClick = onViewProfile,
                        shape = RoundedCornerShape(12.dp),
                        border = BorderStroke(1.dp, SlateBorder),
                        contentPadding = PaddingValues(horizontal = 14.dp, vertical = 6.dp)
                    ) {
                        Text(
                            text = "View Profile",
                            color = TextPrimary,
                            fontSize = 12.sp,
                            fontWeight = FontWeight.SemiBold
                        )
                    }

                    Button(
                        onClick = onBookClick,
                        shape = RoundedCornerShape(12.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = ElectricBlue),
                        contentPadding = PaddingValues(horizontal = 16.dp, vertical = 6.dp)
                    ) {
                        Text(
                            text = "Book",
                            color = Color.White,
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun MetricChip(
    label: String,
    isHighlight: Boolean = false,
    isTrust: Boolean = false
) {
    Surface(
        shape = RoundedCornerShape(8.dp),
        color = when {
            isTrust -> EmeraldTrust.copy(alpha = 0.1f)
            isHighlight -> ElectricBlue.copy(alpha = 0.08f)
            else -> SurfaceMuted
        }
    ) {
        Text(
            text = label,
            color = when {
                isTrust -> EmeraldTrust
                isHighlight -> ElectricBlue
                else -> TextSecondary
            },
            fontSize = 10.sp,
            fontWeight = FontWeight.SemiBold,
            modifier = Modifier.padding(horizontal = 7.dp, vertical = 3.dp)
        )
    }
}
