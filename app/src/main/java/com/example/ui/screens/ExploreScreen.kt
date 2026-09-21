package com.example.ui.screens

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.model.Professional
import com.example.ui.components.getCategoryIconVector
import com.example.ui.theme.*
import com.example.viewmodel.ServoraViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ExploreScreen(
    viewModel: ServoraViewModel,
    onOpenProDetail: (Professional) -> Unit,
    modifier: Modifier = Modifier
) {
    val professionals by viewModel.filteredProfessionals.collectAsState()
    val filters by viewModel.filters.collectAsState()
    val savedProIds by viewModel.savedProIds.collectAsState()
    val categories = viewModel.categories

    var showFilterSheet by remember { mutableStateOf(false) }

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(SurfaceCanvas)
    ) {
        // Search & Filter Header
        Surface(
            color = SurfaceCard,
            tonalElevation = 2.dp,
            border = androidx.compose.foundation.BorderStroke(1.dp, SlateBorder)
        ) {
            Column(modifier = Modifier.padding(horizontal = 16.dp, vertical = 12.dp)) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    OutlinedTextField(
                        value = filters.searchQuery,
                        onValueChange = { q -> viewModel.updateFilters { copy(searchQuery = q) } },
                        placeholder = { Text("Filter professionals, trades...", fontSize = 13.sp, color = TextSubtle) },
                        leadingIcon = {
                            Icon(imageVector = Icons.Default.Search, contentDescription = null, tint = TextSubtle)
                        },
                        trailingIcon = {
                            if (filters.searchQuery.isNotEmpty()) {
                                IconButton(onClick = { viewModel.updateFilters { copy(searchQuery = "") } }) {
                                    Icon(Icons.Default.Clear, contentDescription = "Clear", tint = TextSubtle)
                                }
                            }
                        },
                        singleLine = true,
                        shape = RoundedCornerShape(12.dp),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = ElectricBlue,
                            unfocusedBorderColor = SlateBorder,
                            focusedContainerColor = SurfaceCanvas,
                            unfocusedContainerColor = SurfaceCanvas
                        ),
                        modifier = Modifier
                            .weight(1f)
                            .height(50.dp)
                    )

                    Spacer(modifier = Modifier.width(10.dp))

                    Surface(
                        onClick = { showFilterSheet = true },
                        shape = RoundedCornerShape(12.dp),
                        color = if (filters.verifiedOnly || filters.emergencyOnly || filters.hasInsurance || filters.minRating > 0) ElectricBlue else SlateDark,
                        modifier = Modifier
                            .size(50.dp)
                            .testTag("filter_button")
                    ) {
                        Box(contentAlignment = Alignment.Center) {
                            Icon(
                                imageVector = Icons.Default.Tune,
                                contentDescription = "Filters",
                                tint = Color.White,
                                modifier = Modifier.size(20.dp)
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(10.dp))

                // Horizontal Category filter chips
                LazyRow(
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    item {
                        FilterChip(
                            selected = filters.categoryId == null,
                            onClick = { viewModel.setFilterCategory(null) },
                            label = { Text("All Trades", fontSize = 11.sp, fontWeight = FontWeight.SemiBold) },
                            shape = RoundedCornerShape(16.dp),
                            colors = FilterChipDefaults.filterChipColors(
                                selectedContainerColor = ElectricBlue,
                                selectedLabelColor = Color.White
                            )
                        )
                    }

                    items(categories) { cat ->
                        FilterChip(
                            selected = filters.categoryId == cat.id,
                            onClick = { viewModel.setFilterCategory(cat.id) },
                            label = { Text(cat.name, fontSize = 11.sp, fontWeight = FontWeight.SemiBold) },
                            leadingIcon = {
                                Icon(
                                    imageVector = getCategoryIconVector(cat.iconName),
                                    contentDescription = null,
                                    modifier = Modifier.size(14.dp)
                                )
                            },
                            shape = RoundedCornerShape(16.dp),
                            colors = FilterChipDefaults.filterChipColors(
                                selectedContainerColor = ElectricBlue,
                                selectedLabelColor = Color.White
                            )
                        )
                    }
                }
            }
        }

        // Active filter pills row
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 8.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "${professionals.size} Verified Professionals Available",
                color = TextSecondary,
                fontSize = 12.sp,
                fontWeight = FontWeight.SemiBold
            )

            if (filters.verifiedOnly || filters.emergencyOnly || filters.hasInsurance || filters.minRating > 0 || filters.categoryId != null) {
                TextButton(
                    onClick = {
                        viewModel.updateFilters {
                            copy(
                                categoryId = null,
                                minRating = 0.0,
                                verifiedOnly = false,
                                emergencyOnly = false,
                                hasInsurance = false,
                                searchQuery = ""
                            )
                        }
                    },
                    contentPadding = PaddingValues(0.dp)
                ) {
                    Text("Reset Filters", color = ElectricBlue, fontSize = 11.sp, fontWeight = FontWeight.Bold)
                }
            }
        }

        // Professional List
        if (professionals.isEmpty()) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(32.dp),
                contentAlignment = Alignment.Center
            ) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Icon(
                        imageVector = Icons.Default.SearchOff,
                        contentDescription = null,
                        tint = TextSubtle,
                        modifier = Modifier.size(48.dp)
                    )
                    Spacer(modifier = Modifier.height(12.dp))
                    Text(
                        text = "No specialists match current filters",
                        color = TextPrimary,
                        fontSize = 15.sp,
                        fontWeight = FontWeight.Bold
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = "Try clearing or broadening your search distance or rating criteria.",
                        color = TextMuted,
                        fontSize = 12.sp,
                        textAlign = androidx.compose.ui.text.style.TextAlign.Center
                    )
                }
            }
        } else {
            LazyColumn(
                modifier = Modifier.fillMaxSize(),
                contentPadding = PaddingValues(start = 16.dp, end = 16.dp, top = 4.dp, bottom = 90.dp),
                verticalArrangement = Arrangement.spacedBy(14.dp)
            ) {
                items(professionals) { pro ->
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

    // Filter Bottom Sheet Modal
    if (showFilterSheet) {
        ModalBottomSheet(
            onDismissRequest = { showFilterSheet = false },
            containerColor = SurfaceCard,
            shape = RoundedCornerShape(topStart = 24.dp, topEnd = 24.dp)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(20.dp)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "Marketplace Filters",
                        color = TextPrimary,
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold
                    )

                    IconButton(onClick = { showFilterSheet = false }) {
                        Icon(Icons.Default.Close, contentDescription = "Close", tint = TextPrimary)
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                // Verified Only Toggle
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column {
                        Text("Verified Professionals Only", color = TextPrimary, fontSize = 14.sp, fontWeight = FontWeight.SemiBold)
                        Text("ID, license, & background check cleared", color = TextMuted, fontSize = 11.sp)
                    }
                    Switch(
                        checked = filters.verifiedOnly,
                        onCheckedChange = { ch -> viewModel.updateFilters { copy(verifiedOnly = ch) } },
                        colors = SwitchDefaults.colors(checkedThumbColor = Color.White, checkedTrackColor = EmeraldTrust)
                    )
                }

                Spacer(modifier = Modifier.height(14.dp))

                // Emergency Ready Toggle
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column {
                        Text("24/7 Emergency Available", color = TextPrimary, fontSize = 14.sp, fontWeight = FontWeight.SemiBold)
                        Text("Immediate dispatch equipped", color = TextMuted, fontSize = 11.sp)
                    }
                    Switch(
                        checked = filters.emergencyOnly,
                        onCheckedChange = { ch -> viewModel.updateFilters { copy(emergencyOnly = ch) } },
                        colors = SwitchDefaults.colors(checkedThumbColor = Color.White, checkedTrackColor = EmergencyRed)
                    )
                }

                Spacer(modifier = Modifier.height(14.dp))

                // Commercial Insurance Coverage Toggle
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column {
                        Text("Underwritten Insurance", color = TextPrimary, fontSize = 14.sp, fontWeight = FontWeight.SemiBold)
                        Text("Commercial property damage liability", color = TextMuted, fontSize = 11.sp)
                    }
                    Switch(
                        checked = filters.hasInsurance,
                        onCheckedChange = { ch -> viewModel.updateFilters { copy(hasInsurance = ch) } },
                        colors = SwitchDefaults.colors(checkedThumbColor = Color.White, checkedTrackColor = ElectricBlue)
                    )
                }

                Spacer(modifier = Modifier.height(18.dp))

                // Minimum Rating selector
                Text("Minimum Rating", color = TextPrimary, fontSize = 14.sp, fontWeight = FontWeight.SemiBold)
                Spacer(modifier = Modifier.height(8.dp))
                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    listOf(0.0 to "Any", 4.5 to "4.5+", 4.8 to "4.8+", 4.9 to "4.9+").forEach { (r, label) ->
                        FilterChip(
                            selected = filters.minRating == r,
                            onClick = { viewModel.updateFilters { copy(minRating = r) } },
                            label = { Text(label, fontSize = 12.sp) },
                            colors = FilterChipDefaults.filterChipColors(
                                selectedContainerColor = ElectricBlue,
                                selectedLabelColor = Color.White
                            )
                        )
                    }
                }

                Spacer(modifier = Modifier.height(24.dp))

                Button(
                    onClick = { showFilterSheet = false },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(50.dp),
                    shape = RoundedCornerShape(12.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = ElectricBlue)
                ) {
                    Text("Apply Filters (${professionals.size} results)", color = Color.White, fontWeight = FontWeight.Bold)
                }
            }
        }
    }
}
