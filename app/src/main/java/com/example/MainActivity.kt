package com.example

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.animation.Crossfade
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.model.*
import com.example.ui.components.*
import com.example.ui.screens.*
import com.example.ui.theme.*
import com.example.viewmodel.ServoraViewModel

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            ServoraTheme {
                val viewModel: ServoraViewModel = viewModel()
                ServoraApp(viewModel)
            }
        }
    }
}

@Composable
fun ServoraApp(viewModel: ServoraViewModel) {
    val hasCompletedOnboarding by viewModel.hasCompletedOnboarding.collectAsState()
    val currentRole by viewModel.currentRole.collectAsState()
    val customerScreen by viewModel.customerScreen.collectAsState()
    val proScreen by viewModel.proScreen.collectAsState()
    val userLocation by viewModel.userLocation.collectAsState()
    val selectedCurrency by viewModel.selectedCurrency.collectAsState()
    val selectedLanguage by viewModel.selectedLanguage.collectAsState()

    // Dialog states
    val showCurrencyDialog by viewModel.showCurrencyDialog.collectAsState()
    val showLanguageDialog by viewModel.showLanguageDialog.collectAsState()
    val showTrustSafetySheet by viewModel.showTrustSafetySheet.collectAsState()
    val showReviewDialog by viewModel.showReviewDialog.collectAsState()
    val showWithdrawSuccessDialog by viewModel.showWithdrawSuccessDialog.collectAsState()
    val showExtraWorkApprovalDialog by viewModel.showExtraWorkApprovalDialog.collectAsState()
    val showRoleSwitcherDialog by viewModel.showRoleSwitcherDialog.collectAsState()

    var showLocationDialog by remember { mutableStateOf(false) }

    // Onboarding Gate
    if (!hasCompletedOnboarding) {
        OnboardingScreen(
            onFinish = { viewModel.hasCompletedOnboarding.value = true }
        )
        return
    }

    // Role-based Layout
    if (currentRole == AppRole.ADMIN) {
        AdminDashboardScreen(
            viewModel = viewModel,
            onBack = { viewModel.switchRole(AppRole.CUSTOMER) }
        )
    } else if (currentRole == AppRole.DEVELOPER) {
        DeveloperConsoleScreen(
            viewModel = viewModel,
            onBack = { viewModel.switchRole(AppRole.CUSTOMER) }
        )
    } else {
        Scaffold(
            topBar = {
                // Show standard TopBar on main screens (not on deep checkout/tracking screens)
                if (customerScreen == CustomerScreen.HOME ||
                    customerScreen == CustomerScreen.EXPLORE ||
                    customerScreen == CustomerScreen.BOOKINGS ||
                    customerScreen == CustomerScreen.PROFILE ||
                    currentRole == AppRole.PROFESSIONAL
                ) {
                    ServoraTopBar(
                        role = currentRole,
                        locationText = userLocation,
                        currencyCode = selectedCurrency.code,
                        onLocationClick = { showLocationDialog = true },
                        onNotificationClick = { /* Notifications */ },
                        onCurrencyClick = { viewModel.showCurrencyDialog.value = true },
                        onRoleToggle = {
                            viewModel.showRoleSwitcherDialog.value = true
                        }
                    )
                }
            },
            bottomBar = {
                // Hide bottom nav on specific sub-flows (booking flow, live tracking, emergency, smart search)
                val shouldShowBottomBar = when (currentRole) {
                    AppRole.CUSTOMER -> customerScreen in listOf(
                        CustomerScreen.HOME,
                        CustomerScreen.EXPLORE,
                        CustomerScreen.BOOKINGS,
                        CustomerScreen.MESSAGES,
                        CustomerScreen.PROFILE
                    )
                    AppRole.PROFESSIONAL -> proScreen != ProScreen.MESSAGES
                    AppRole.ADMIN -> false
                    AppRole.DEVELOPER -> false
                }

                if (shouldShowBottomBar) {
                    ServoraBottomNavigation(
                        role = currentRole,
                        customerScreen = customerScreen,
                        proScreen = proScreen,
                        onNavigateCustomer = { viewModel.navigateCustomer(it) },
                        onNavigatePro = { viewModel.navigatePro(it) }
                    )
                }
            },
            containerColor = SurfaceCanvas,
            contentWindowInsets = WindowInsets(0, 0, 0, 0)
        ) { innerPadding ->
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(innerPadding)
            ) {
                if (currentRole == AppRole.CUSTOMER) {
                    Crossfade(targetState = customerScreen, label = "customer_nav") { screen ->
                        when (screen) {
                            CustomerScreen.HOME -> HomeScreen(
                                viewModel = viewModel,
                                onNavigateExplore = { categoryId ->
                                    viewModel.setFilterCategory(categoryId)
                                    viewModel.navigateCustomer(CustomerScreen.EXPLORE)
                                },
                                onNavigateEmergency = { viewModel.navigateCustomer(CustomerScreen.EMERGENCY) },
                                onNavigateSmartSearch = { viewModel.navigateCustomer(CustomerScreen.SMART_SEARCH) },
                                onOpenLiveTracking = { booking -> viewModel.openLiveTracking(booking) },
                                onOpenProDetail = { pro -> viewModel.viewProDetail(pro) },
                                onOpenTrustSafety = { viewModel.showTrustSafetySheet.value = true }
                            )

                            CustomerScreen.EXPLORE -> ExploreScreen(
                                viewModel = viewModel,
                                onOpenProDetail = { pro -> viewModel.viewProDetail(pro) }
                            )

                            CustomerScreen.SMART_SEARCH -> SmartSearchScreen(
                                viewModel = viewModel,
                                onBack = { viewModel.navigateCustomer(CustomerScreen.HOME) },
                                onSelectPro = { pro ->
                                    viewModel.viewProDetail(pro)
                                }
                            )

                            CustomerScreen.PRO_DETAIL -> {
                                val pro = viewModel.selectedProForDetail.collectAsState().value ?: viewModel.professionals.first()
                                ProfessionalDetailScreen(
                                    pro = pro,
                                    viewModel = viewModel,
                                    onBack = { viewModel.navigateCustomer(CustomerScreen.EXPLORE) },
                                    onBookNow = { viewModel.startBookingFlow(pro) }
                                )
                            }

                            CustomerScreen.BOOKING_FLOW -> BookingFlowScreen(
                                viewModel = viewModel,
                                onBack = { viewModel.navigateCustomer(CustomerScreen.EXPLORE) },
                                onOpenLiveTracking = { booking -> viewModel.openLiveTracking(booking) }
                            )

                            CustomerScreen.LIVE_TRACKING -> {
                                val booking = viewModel.activeTrackingBooking.collectAsState().value ?: viewModel.bookings.value.first()
                                LiveTrackingScreen(
                                    booking = booking,
                                    viewModel = viewModel,
                                    onBack = { viewModel.navigateCustomer(CustomerScreen.HOME) },
                                    onOpenChat = { viewModel.navigateCustomer(CustomerScreen.MESSAGES) }
                                )
                            }

                            CustomerScreen.BOOKINGS -> CustomerBookingsScreen(
                                viewModel = viewModel,
                                onOpenLiveTracking = { booking -> viewModel.openLiveTracking(booking) }
                            )

                            CustomerScreen.MESSAGES -> ChatScreen(
                                viewModel = viewModel,
                                onBack = { viewModel.navigateCustomer(CustomerScreen.HOME) }
                            )

                            CustomerScreen.EMERGENCY -> EmergencyScreen(
                                viewModel = viewModel,
                                onBack = { viewModel.navigateCustomer(CustomerScreen.HOME) },
                                onDispatchEmergency = { emType ->
                                    val cat = viewModel.categories.find { it.id == "emergency" } ?: viewModel.categories.first()
                                    viewModel.startBookingFlow(category = cat)
                                }
                            )

                            CustomerScreen.PROFILE, CustomerScreen.SAFETY_CENTER -> CustomerProfileScreen(
                                viewModel = viewModel,
                                onOpenTrustSafety = { viewModel.showTrustSafetySheet.value = true }
                            )
                        }
                    }
                } else {
                    // PROFESSIONAL MODE
                    Crossfade(targetState = proScreen, label = "pro_nav") { screen ->
                        when (screen) {
                            ProScreen.DASHBOARD, ProScreen.JOBS, ProScreen.EARNINGS -> ProfessionalDashboardScreen(
                                viewModel = viewModel,
                                onOpenJobTracking = { booking -> viewModel.openLiveTracking(booking) }
                            )
                            ProScreen.MESSAGES -> ChatScreen(
                                viewModel = viewModel,
                                onBack = { viewModel.navigatePro(ProScreen.DASHBOARD) }
                            )
                            ProScreen.PROFILE, ProScreen.VERIFICATION_CENTER -> ProfessionalDashboardScreen(
                                viewModel = viewModel,
                                onOpenJobTracking = { booking -> viewModel.openLiveTracking(booking) }
                            )
                        }
                    }
                }
            }
        }
    }

    // Global Modal Dialogs
    if (showCurrencyDialog) {
        CurrencySelectionDialog(
            currentCurrency = selectedCurrency,
            onSelect = { viewModel.setCurrency(it) },
            onDismiss = { viewModel.showCurrencyDialog.value = false }
        )
    }

    if (showLanguageDialog) {
        LanguageSelectionDialog(
            currentLanguage = selectedLanguage,
            onSelect = { viewModel.setLanguage(it) },
            onDismiss = { viewModel.showLanguageDialog.value = false }
        )
    }

    val showAddAddressDialog by viewModel.showAddAddressDialog.collectAsState()
    val vmShowLocationDialog by viewModel.showLocationDialog.collectAsState()
    val isLocationDialogVisible = showLocationDialog || vmShowLocationDialog

    if (isLocationDialogVisible) {
        val addresses by viewModel.userAddresses.collectAsState()
        LocationSelectionDialog(
            currentLocation = userLocation,
            savedAddresses = addresses,
            onSelectAddress = { addr ->
                viewModel.setDefaultAddress(addr)
                showLocationDialog = false
                viewModel.showLocationDialog.value = false
            },
            onAddNewAddressClick = {
                showLocationDialog = false
                viewModel.showLocationDialog.value = false
                viewModel.showAddAddressDialog.value = true
            },
            onSelectCity = { city ->
                viewModel.setLocation(city)
                showLocationDialog = false
                viewModel.showLocationDialog.value = false
            },
            onDismiss = {
                showLocationDialog = false
                viewModel.showLocationDialog.value = false
            }
        )
    }

    if (showAddAddressDialog) {
        AddNewAddressDialog(
            onSave = { title, street, city, state, country, postal, notes ->
                viewModel.addNewAddress(title, street, city, state, country, postal, notes)
            },
            onDismiss = { viewModel.showAddAddressDialog.value = false }
        )
    }

    if (showTrustSafetySheet) {
        TrustSafetyBottomSheet(
            onDismiss = { viewModel.showTrustSafetySheet.value = false }
        )
    }

    if (showReviewDialog) {
        val pro = viewModel.activeTrackingBooking.value?.professional ?: viewModel.professionals.first()
        ReviewSubmissionDialog(
            onSubmit = { rating, comment, q, p, pu, v ->
                viewModel.submitReview(pro.id, rating, comment, q, p, pu, v)
            },
            onDismiss = { viewModel.showReviewDialog.value = false }
        )
    }

    if (showWithdrawSuccessDialog) {
        AlertDialog(
            onDismissRequest = { viewModel.showWithdrawSuccessDialog.value = false },
            title = { Text("Payout Initiated", fontWeight = FontWeight.Bold) },
            text = { Text("Your payout request of ${viewModel.formatPrice(viewModel.proTodayEarnings.value)} has been successfully routed to your verified bank account via Stripe Instant Payout.") },
            confirmButton = {
                Button(
                    onClick = { viewModel.showWithdrawSuccessDialog.value = false },
                    colors = ButtonDefaults.buttonColors(containerColor = EmeraldTrust)
                ) {
                    Text("OK", color = Color.White)
                }
            }
        )
    }

    if (showRoleSwitcherDialog) {
        RoleAccessGatewayDialog(
            currentRole = currentRole,
            onRoleSelected = { targetRole, pin ->
                viewModel.attemptRoleSwitch(targetRole, pin)
            },
            onDismiss = { viewModel.showRoleSwitcherDialog.value = false }
        )
    }
}

