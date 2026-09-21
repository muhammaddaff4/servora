package com.example.ui.screens

import androidx.compose.foundation.*
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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.model.PortfolioItem
import com.example.model.Professional
import com.example.model.Review
import com.example.model.VerificationItem
import com.example.ui.components.RatingStarsBar
import com.example.ui.components.VerifiedProBadge
import com.example.ui.theme.*
import com.example.viewmodel.ServoraViewModel

@Composable
fun ProfessionalDetailScreen(
    pro: Professional,
    viewModel: ServoraViewModel,
    onBack: () -> Unit,
    onBookNow: () -> Unit,
    modifier: Modifier = Modifier
) {
    var selectedTab by remember { mutableStateOf(0) }
    val tabs = listOf("About", "Verification", "Portfolio", "Reviews")
    val savedProIds by viewModel.savedProIds.collectAsState()
    val isSaved = savedProIds.contains(pro.id)

    Box(
        modifier = modifier
            .fillMaxSize()
            .background(SurfaceCanvas)
    ) {
        LazyColumn(
            modifier = Modifier.fillMaxSize(),
            contentPadding = PaddingValues(bottom = 90.dp)
        ) {
            // Header Top App Bar with back & favorite
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
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            IconButton(onClick = onBack, modifier = Modifier.size(36.dp)) {
                                Icon(
                                    imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                                    contentDescription = "Back",
                                    tint = Color.White
                                )
                            }

                            Row {
                                IconButton(onClick = { viewModel.toggleSavePro(pro.id) }) {
                                    Icon(
                                        imageVector = if (isSaved) Icons.Default.Favorite else Icons.Default.FavoriteBorder,
                                        contentDescription = "Favorite",
                                        tint = if (isSaved) EmergencyRed else Color.White
                                    )
                                }
                                IconButton(onClick = { /* Share pro profile */ }) {
                                    Icon(
                                        imageVector = Icons.Default.Share,
                                        contentDescription = "Share",
                                        tint = Color.White
                                    )
                                }
                            }
                        }

                        Spacer(modifier = Modifier.height(10.dp))

                        // Profile Portrait & Basic Info
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Box(
                                modifier = Modifier
                                    .size(76.dp)
                                    .clip(RoundedCornerShape(20.dp))
                                    .background(SlateDark)
                                    .border(2.dp, CyanAccent, RoundedCornerShape(20.dp)),
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
                                        fontSize = 24.sp,
                                        fontWeight = FontWeight.Bold
                                    )
                                }
                            }

                            Spacer(modifier = Modifier.width(16.dp))

                            Column {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Text(
                                        text = pro.name,
                                        color = Color.White,
                                        fontSize = 19.sp,
                                        fontWeight = FontWeight.Bold
                                    )
                                    Spacer(modifier = Modifier.width(6.dp))
                                    Icon(
                                        imageVector = Icons.Default.CheckCircle,
                                        contentDescription = "Verified",
                                        tint = EmeraldTrust,
                                        modifier = Modifier.size(17.dp)
                                    )
                                }

                                Text(
                                    text = pro.title,
                                    color = TextSubtle,
                                    fontSize = 13.sp
                                )

                                Spacer(modifier = Modifier.height(4.dp))

                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    RatingStarsBar(rating = pro.rating, size = 13)
                                    Spacer(modifier = Modifier.width(6.dp))
                                    Text(
                                        text = "${pro.rating} (${pro.reviewCount} reviews)",
                                        color = CyanAccent,
                                        fontSize = 12.sp,
                                        fontWeight = FontWeight.SemiBold
                                    )
                                }
                            }
                        }

                        Spacer(modifier = Modifier.height(18.dp))

                        // Metric Stat Highlights (Jobs, Exp, Response, Languages)
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clip(RoundedCornerShape(14.dp))
                                .background(SlateDark)
                                .padding(12.dp),
                            horizontalArrangement = Arrangement.SpaceAround
                        ) {
                            ProStatMetric(value = "${pro.completedJobs}", label = "Jobs Done")
                            ProStatMetric(value = "${pro.experienceYears}+ yrs", label = "Experience")
                            ProStatMetric(value = "${pro.responseTimeMin}m", label = "Avg Response")
                            ProStatMetric(value = "${pro.languages.size}", label = "Languages")
                        }
                    }
                }
            }

            // Tabs Selector: About, Verification, Portfolio, Reviews
            item {
                Surface(
                    color = SurfaceCard,
                    border = BorderStroke(1.dp, SlateBorder)
                ) {
                    TabRow(
                        selectedTabIndex = selectedTab,
                        containerColor = SurfaceCard,
                        contentColor = ElectricBlue
                    ) {
                        tabs.forEachIndexed { index, title ->
                            Tab(
                                selected = selectedTab == index,
                                onClick = { selectedTab = index },
                                text = {
                                    Text(
                                        text = title,
                                        fontSize = 13.sp,
                                        fontWeight = if (selectedTab == index) FontWeight.Bold else FontWeight.Medium,
                                        color = if (selectedTab == index) ElectricBlue else TextSecondary
                                    )
                                }
                            )
                        }
                    }
                }
            }

            // Tab Content
            when (selectedTab) {
                0 -> {
                    // ABOUT TAB
                    item {
                        Column(modifier = Modifier.padding(16.dp)) {
                            Text(
                                text = "About Professional",
                                color = TextPrimary,
                                fontSize = 16.sp,
                                fontWeight = FontWeight.Bold
                            )
                            Spacer(modifier = Modifier.height(8.dp))
                            Text(
                                text = pro.bio,
                                color = TextSecondary,
                                fontSize = 13.sp,
                                lineHeight = 20.sp
                            )

                            Spacer(modifier = Modifier.height(20.dp))

                            Text(
                                text = "Transparent Pricing Schedule",
                                color = TextPrimary,
                                fontSize = 16.sp,
                                fontWeight = FontWeight.Bold
                            )
                            Spacer(modifier = Modifier.height(8.dp))

                            Card(
                                shape = RoundedCornerShape(14.dp),
                                colors = CardDefaults.cardColors(containerColor = SurfaceCard),
                                border = BorderStroke(1.dp, SlateBorder)
                            ) {
                                Column(modifier = Modifier.padding(14.dp)) {
                                    PriceRow(
                                        title = "Standard Diagnostic & Callout",
                                        amount = viewModel.formatPrice(pro.startingPriceUsd),
                                        subtitle = "Includes arrival and multi-point technical inspection"
                                    )
                                    Spacer(modifier = Modifier.height(10.dp))
                                    HorizontalDivider(color = SlateBorder)
                                    Spacer(modifier = Modifier.height(10.dp))
                                    PriceRow(
                                        title = "Hourly Rate",
                                        amount = "${viewModel.formatPrice(pro.hourlyRateUsd)} / hour",
                                        subtitle = "Billed only after written diagnostic quotation is approved"
                                    )
                                }
                            }

                            Spacer(modifier = Modifier.height(20.dp))

                            Text(
                                text = "Spoken Languages",
                                color = TextPrimary,
                                fontSize = 16.sp,
                                fontWeight = FontWeight.Bold
                            )
                            Spacer(modifier = Modifier.height(8.dp))
                            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                                pro.languages.forEach { lang ->
                                    Surface(
                                        shape = RoundedCornerShape(8.dp),
                                        color = SurfaceMuted
                                    ) {
                                        Text(
                                            text = lang,
                                            color = TextPrimary,
                                            fontSize = 12.sp,
                                            fontWeight = FontWeight.Medium,
                                            modifier = Modifier.padding(horizontal = 10.dp, vertical = 5.dp)
                                        )
                                    }
                                }
                            }
                        }
                    }
                }
                1 -> {
                    // VERIFICATION CENTER TAB
                    item {
                        Column(modifier = Modifier.padding(16.dp)) {
                            Text(
                                text = "Trust & Verification Center",
                                color = TextPrimary,
                                fontSize = 16.sp,
                                fontWeight = FontWeight.Bold
                            )
                            Text(
                                text = "All badges authenticated by Servora Trust Protocol",
                                color = TextMuted,
                                fontSize = 12.sp
                            )

                            Spacer(modifier = Modifier.height(14.dp))

                            pro.verifications.forEach { ver ->
                                VerificationBadgeCard(ver)
                                Spacer(modifier = Modifier.height(10.dp))
                            }
                        }
                    }
                }
                2 -> {
                    // PORTFOLIO TAB (Before / After)
                    item {
                        Column(modifier = Modifier.padding(16.dp)) {
                            Text(
                                text = "Recent Job Portfolios",
                                color = TextPrimary,
                                fontSize = 16.sp,
                                fontWeight = FontWeight.Bold
                            )
                            Text(
                                text = "Verified completed projects with customer work orders",
                                color = TextMuted,
                                fontSize = 12.sp
                            )
                            Spacer(modifier = Modifier.height(14.dp))
                        }
                    }

                    if (pro.beforeAfterPortfolios.isEmpty()) {
                        item {
                            Box(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(32.dp),
                                contentAlignment = Alignment.Center
                            ) {
                                Text("No showcase photos uploaded yet.", color = TextMuted, fontSize = 13.sp)
                            }
                        }
                    } else {
                        items(pro.beforeAfterPortfolios) { item ->
                            Box(modifier = Modifier.padding(horizontal = 16.dp, vertical = 6.dp)) {
                                PortfolioCardItem(item)
                            }
                        }
                    }
                }
                3 -> {
                    // REVIEWS TAB
                    item {
                        Column(modifier = Modifier.padding(16.dp)) {
                            Text(
                                text = "Authentic Verified Reviews",
                                color = TextPrimary,
                                fontSize = 16.sp,
                                fontWeight = FontWeight.Bold
                            )
                            Text(
                                text = "Only customers who completed paid services can leave reviews",
                                color = TextMuted,
                                fontSize = 12.sp
                            )
                            Spacer(modifier = Modifier.height(14.dp))
                        }
                    }

                    items(pro.reviews) { rev ->
                        Box(modifier = Modifier.padding(horizontal = 16.dp, vertical = 6.dp)) {
                            ReviewCardItem(rev)
                        }
                    }
                }
            }
        }

        // Sticky Bottom Booking Bar
        Surface(
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .fillMaxWidth()
                .navigationBarsPadding(),
            color = SurfaceCard,
            tonalElevation = 8.dp,
            border = BorderStroke(1.dp, SlateBorder)
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 20.dp, vertical = 14.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Text(text = "Starting service estimate", color = TextMuted, fontSize = 10.sp)
                    Text(
                        text = viewModel.formatPrice(pro.startingPriceUsd),
                        color = TextPrimary,
                        fontSize = 20.sp,
                        fontWeight = FontWeight.ExtraBold
                    )
                }

                Button(
                    onClick = onBookNow,
                    modifier = Modifier
                        .height(48.dp)
                        .testTag("book_service_button"),
                    shape = RoundedCornerShape(12.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = ElectricBlue),
                    contentPadding = PaddingValues(horizontal = 24.dp)
                ) {
                    Text(
                        text = "Book Service",
                        color = Color.White,
                        fontSize = 15.sp,
                        fontWeight = FontWeight.Bold
                    )
                }
            }
        }
    }
}

@Composable
private fun ProStatMetric(value: String, label: String) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Text(text = value, color = Color.White, fontSize = 15.sp, fontWeight = FontWeight.Bold)
        Text(text = label, color = TextSubtle, fontSize = 10.sp)
    }
}

@Composable
private fun PriceRow(title: String, amount: String, subtitle: String) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.Top
    ) {
        Column(modifier = Modifier.weight(1f)) {
            Text(text = title, color = TextPrimary, fontSize = 14.sp, fontWeight = FontWeight.SemiBold)
            Text(text = subtitle, color = TextMuted, fontSize = 11.sp)
        }
        Text(text = amount, color = ElectricBlue, fontSize = 15.sp, fontWeight = FontWeight.Bold)
    }
}

@Composable
private fun VerificationBadgeCard(ver: VerificationItem) {
    Card(
        shape = RoundedCornerShape(14.dp),
        colors = CardDefaults.cardColors(containerColor = SurfaceCard),
        border = BorderStroke(1.dp, SlateBorder)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(14.dp),
            verticalAlignment = Alignment.Top
        ) {
            Box(
                modifier = Modifier
                    .size(36.dp)
                    .clip(CircleShape)
                    .background(EmeraldTrust.copy(alpha = 0.12f)),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = Icons.Default.Verified,
                    contentDescription = null,
                    tint = EmeraldTrust,
                    modifier = Modifier.size(20.dp)
                )
            }

            Spacer(modifier = Modifier.width(12.dp))

            Column(modifier = Modifier.weight(1f)) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(text = ver.title, color = TextPrimary, fontSize = 14.sp, fontWeight = FontWeight.Bold)
                    Text(text = ver.verifiedDate, color = TextMuted, fontSize = 11.sp)
                }

                Text(
                    text = ver.badgeLabel,
                    color = EmeraldTrust,
                    fontSize = 12.sp,
                    fontWeight = FontWeight.SemiBold,
                    modifier = Modifier.padding(vertical = 2.dp)
                )

                Text(
                    text = ver.description,
                    color = TextSecondary,
                    fontSize = 11.sp,
                    lineHeight = 16.sp
                )
            }
        }
    }
}

@Composable
private fun PortfolioCardItem(item: PortfolioItem) {
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
                Text(text = item.title, color = TextPrimary, fontSize = 15.sp, fontWeight = FontWeight.Bold)
                Text(text = item.completedDate, color = TextMuted, fontSize = 11.sp)
            }

            Spacer(modifier = Modifier.height(10.dp))

            // Before / After technical callout boxes
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Column(
                    modifier = Modifier
                        .weight(1f)
                        .clip(RoundedCornerShape(10.dp))
                        .background(EmergencyRed.copy(alpha = 0.08f))
                        .padding(10.dp)
                ) {
                    Text(text = "Initial State (Issue)", color = EmergencyRed, fontSize = 10.sp, fontWeight = FontWeight.Bold)
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(text = item.beforeDesc, color = TextSecondary, fontSize = 11.sp, lineHeight = 15.sp)
                }

                Column(
                    modifier = Modifier
                        .weight(1f)
                        .clip(RoundedCornerShape(10.dp))
                        .background(EmeraldTrust.copy(alpha = 0.08f))
                        .padding(10.dp)
                ) {
                    Text(text = "Resolution", color = EmeraldTrust, fontSize = 10.sp, fontWeight = FontWeight.Bold)
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(text = item.afterDesc, color = TextSecondary, fontSize = 11.sp, lineHeight = 15.sp)
                }
            }
        }
    }
}

@Composable
private fun ReviewCardItem(review: Review) {
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
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Box(
                        modifier = Modifier
                            .size(32.dp)
                            .clip(CircleShape)
                            .background(ElectricBlue.copy(alpha = 0.15f)),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(text = review.authorAvatar, color = ElectricBlue, fontWeight = FontWeight.Bold, fontSize = 12.sp)
                    }
                    Spacer(modifier = Modifier.width(8.dp))
                    Column {
                        Text(text = review.authorName, color = TextPrimary, fontSize = 13.sp, fontWeight = FontWeight.Bold)
                        Text(text = review.serviceName, color = TextMuted, fontSize = 10.sp)
                    }
                }

                Column(horizontalAlignment = Alignment.End) {
                    RatingStarsBar(rating = review.rating, size = 12)
                    Text(text = review.date, color = TextMuted, fontSize = 10.sp)
                }
            }

            Spacer(modifier = Modifier.height(8.dp))

            Text(
                text = review.comment,
                color = TextSecondary,
                fontSize = 12.sp,
                lineHeight = 17.sp
            )

            Spacer(modifier = Modifier.height(8.dp))

            // Sub-metrics pills: Quality 5.0, Prof 5.0, Punctuality 5.0
            Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                ReviewSubmetricPill(label = "Quality: ${review.quality}")
                ReviewSubmetricPill(label = "Prof: ${review.professionalism}")
                ReviewSubmetricPill(label = "Punctual: ${review.punctuality}")
            }
        }
    }
}

@Composable
private fun ReviewSubmetricPill(label: String) {
    Surface(
        shape = RoundedCornerShape(6.dp),
        color = SurfaceMuted
    ) {
        Text(
            text = label,
            color = TextMuted,
            fontSize = 9.sp,
            fontWeight = FontWeight.SemiBold,
            modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
        )
    }
}
