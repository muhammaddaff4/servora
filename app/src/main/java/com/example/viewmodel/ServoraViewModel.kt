package com.example.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.data.ServoraRepository
import com.example.model.*
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch

data class MarketplaceFilters(
    val categoryId: String? = null,
    val maxDistanceKm: Double = 50.0,
    val minRating: Double = 0.0,
    val verifiedOnly: Boolean = false,
    val emergencyOnly: Boolean = false,
    val hasInsurance: Boolean = false,
    val searchQuery: String = ""
)

data class BookingWizardState(
    val step: Int = 1, // 1: Service/Problem, 2: Professional, 3: Schedule, 4: Location, 5: Estimate, 6: Payment, 7: Confirmation
    val selectedCategory: ServiceCategory? = null,
    val selectedPro: Professional? = null,
    val problemDescription: String = "",
    val urgency: String = "Standard",
    val preferredDate: String = "Today",
    val preferredTime: String = "10:00 AM - 12:00 PM",
    val locationAddress: String = "450 Lexington Ave, Suite 18B, New York",
    val selectedPaymentMethod: String = "Apple Pay •••• 4921",
    val isSubmitting: Boolean = false,
    val confirmedBooking: Booking? = null
)

class ServoraViewModel(
    val repository: ServoraRepository = ServoraRepository()
) : ViewModel() {

    // Onboarding State
    val hasCompletedOnboarding = MutableStateFlow(false)
    val onboardingPageIndex = MutableStateFlow(0)

    // Current App Role & Screens
    val currentRole = repository.currentRole
    val customerScreen = repository.customerScreen
    val proScreen = repository.proScreen

    val selectedCurrency = repository.selectedCurrency
    val selectedLanguage = repository.selectedLanguage
    val userLocation = repository.userLocation
    val categories = repository.categories
    val emergencyTypes = repository.emergencyTypes
    val professionals = repository.professionals
    val professionalsFlow = repository.professionalsFlow
    val categoriesFlow = repository.categoriesFlow
    val emergencyTypesFlow = repository.emergencyTypesFlow
    val bookings = repository.bookings
    val messages = repository.messages
    val savedProIds = repository.savedProIds
    val selectedProForDetail = repository.selectedProForDetail

    // Pro Dashboard state
    val proTodayEarnings = repository.proTodayEarnings
    val proMonthEarnings = repository.proMonthEarnings
    val proAcceptanceRate = repository.proAcceptanceRate
    val proResponseRate = repository.proResponseRate

    // Admin state
    val adminTotalUsers = repository.adminTotalUsers
    val adminActivePros = repository.adminActivePros
    val adminTotalBookings = repository.adminTotalBookings
    val adminGrossRevenue = repository.adminGrossRevenue
    val adminEscrowHeld = repository.adminEscrowHeld
    val adminPendingDisputes = repository.adminPendingDisputes
    val adminVerificationQueue = repository.adminVerificationQueue

    // Firestore Integration State
    val isFirestoreAvailable = repository.firestoreRepository.isAvailable()
    val firestoreSyncStatus = MutableStateFlow<String>(
        if (repository.firestoreRepository.isAvailable()) "Connected • Real-time Sync Active"
        else "Standby (Local Fallback Mode)"
    )
    val isSyncingFirestore = MutableStateFlow(false)

    // Marketplace Filters
    private val _filters = MutableStateFlow(MarketplaceFilters())
    val filters: StateFlow<MarketplaceFilters> = _filters.asStateFlow()

    // Filtered Pros (Updated in real-time as Firestore documents change)
    val filteredProfessionals: StateFlow<List<Professional>> = combine(
        repository.professionalsFlow,
        _filters
    ) { pros, filter ->
        pros.filter { pro ->
            val matchCategory = filter.categoryId == null || pro.categories.contains(filter.categoryId)
            val matchDistance = pro.distanceKm <= filter.maxDistanceKm
            val matchRating = pro.rating >= filter.minRating
            val matchVerified = !filter.verifiedOnly || pro.isVerified
            val matchEmergency = !filter.emergencyOnly || pro.categories.contains("emergency")
            val matchInsurance = !filter.hasInsurance || pro.hasInsurance
            val matchSearch = filter.searchQuery.isBlank() ||
                    pro.name.contains(filter.searchQuery, ignoreCase = true) ||
                    pro.title.contains(filter.searchQuery, ignoreCase = true) ||
                    pro.categories.any { it.contains(filter.searchQuery, ignoreCase = true) }

            matchCategory && matchDistance && matchRating && matchVerified && matchEmergency && matchInsurance && matchSearch
        }
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), repository.professionals)

    // Smart Search State
    val smartSearchQuery = MutableStateFlow("")
    private val _smartSearchResult = MutableStateFlow<ServoraRepository.SmartMatchResult?>(null)
    val smartSearchResult: StateFlow<ServoraRepository.SmartMatchResult?> = _smartSearchResult.asStateFlow()
    val isAiMatching = MutableStateFlow(false)

    // Booking Wizard State
    private val _bookingWizard = MutableStateFlow(BookingWizardState())
    val bookingWizard: StateFlow<BookingWizardState> = _bookingWizard.asStateFlow()

    // Dialogs & Modals
    val showCurrencyDialog = MutableStateFlow(false)
    val showLanguageDialog = MutableStateFlow(false)
    val showLocationDialog = MutableStateFlow(false)
    val showAddAddressDialog = MutableStateFlow(false)
    val showEmergencyDialog = MutableStateFlow(false)
    val showTrustSafetySheet = MutableStateFlow(false)
    val showReviewDialog = MutableStateFlow(false)
    val showWithdrawSuccessDialog = MutableStateFlow(false)
    val showExtraWorkApprovalDialog = MutableStateFlow(false)
    val showRoleSwitcherDialog = MutableStateFlow(false)

    // User Addresses & Payments
    val userAddresses = repository.userAddresses
    val paymentMethods = repository.paymentMethods

    // Security & Roles (RBAC)
    val roleDefinitions = repository.roleDefinitions
    val securityFeatureFlags = repository.securityFeatureFlags
    val systemAuditLogs = repository.systemAuditLogs

    // Selected booking for live tracking
    private val _activeTrackingBooking = MutableStateFlow<Booking?>(null)
    val activeTrackingBooking: StateFlow<Booking?> = _activeTrackingBooking.asStateFlow()

    init {
        // Start Firestore real-time synchronization
        repository.syncWithFirestore(viewModelScope)

        // Set first booking as default tracked booking if available
        if (repository.bookings.value.isNotEmpty()) {
            _activeTrackingBooking.value = repository.bookings.value.first()
        }
    }

    // Firestore Synchronization & Management
    fun syncFirestoreData() {
        viewModelScope.launch {
            isSyncingFirestore.value = true
            firestoreSyncStatus.value = "Syncing profiles & services to Firestore..."
            val result = repository.seedInitialDataToFirestore()
            isSyncingFirestore.value = false
            firestoreSyncStatus.value = if (result.isSuccess) {
                result.getOrNull() ?: "Successfully synced to Firestore"
            } else {
                "Offline Mode: ${result.exceptionOrNull()?.message ?: "Check configuration"}"
            }
        }
    }

    fun saveProfessionalProfile(pro: Professional) {
        viewModelScope.launch {
            repository.saveProfessional(pro)
        }
    }

    fun saveServiceCategory(category: ServiceCategory) {
        viewModelScope.launch {
            repository.saveCategory(category)
        }
    }

    fun saveEmergencyService(service: EmergencyServiceType) {
        viewModelScope.launch {
            repository.saveEmergencyService(service)
        }
    }

    // Role switching with Security PIN Validation
    fun switchRole(role: AppRole) {
        repository.setRole(role)
        repository.addAuditLog(
            actor = role.name,
            action = "ROLE_ACTIVATED",
            details = "Active user context shifted to $role.",
            severity = "INFO"
        )
    }

    fun attemptRoleSwitch(targetRole: AppRole, pin: String): Boolean {
        val isValid = repository.verifyRolePin(targetRole, pin)
        if (isValid) {
            switchRole(targetRole)
            return true
        } else {
            repository.addAuditLog(
                actor = currentRole.value.name,
                action = "AUTH_FAILURE",
                details = "Failed PIN verification attempt to escalate to $targetRole.",
                severity = "CRITICAL"
            )
            return false
        }
    }

    fun toggleSecurityFlag(flagId: String): Boolean {
        return repository.toggleSecurityFeatureFlag(flagId, currentRole.value)
    }

    fun resetSandboxData() {
        if (currentRole.value == AppRole.DEVELOPER) {
            repository.resetAndSeedSandboxData()
        }
    }

    fun clearAuditLogs() {
        if (currentRole.value == AppRole.DEVELOPER) {
            repository.clearAuditLogs()
        }
    }

    // Navigation
    fun navigateCustomer(screen: CustomerScreen) {
        repository.navigateCustomer(screen)
    }

    fun navigatePro(screen: ProScreen) {
        repository.navigatePro(screen)
    }

    fun viewProDetail(pro: Professional) {
        repository.selectProForDetail(pro)
    }

    fun setLocation(loc: String) {
        repository.setLocation(loc)
    }

    fun toggleSavePro(proId: String) {
        repository.toggleSavePro(proId)
    }

    fun setFilterCategory(catId: String?) {
        _filters.value = _filters.value.copy(categoryId = if (_filters.value.categoryId == catId) null else catId)
    }

    fun updateFilters(update: MarketplaceFilters.() -> MarketplaceFilters) {
        _filters.value = _filters.value.update()
    }

    // Smart Discovery trigger
    fun executeSmartSearch(query: String) {
        smartSearchQuery.value = query
        if (query.isBlank()) {
            _smartSearchResult.value = null
            return
        }
        viewModelScope.launch {
            isAiMatching.value = true
            delay(400) // Realistic matching feedback
            _smartSearchResult.value = repository.matchServiceQuery(query)
            isAiMatching.value = false
        }
    }

    // Start Booking from Pro or Category
    fun startBookingFlow(pro: Professional? = null, category: ServiceCategory? = null) {
        val cat = category ?: (pro?.categories?.firstOrNull()?.let { id -> categories.find { it.id == id } } ?: categories.first())
        _bookingWizard.value = BookingWizardState(
            step = 1,
            selectedCategory = cat,
            selectedPro = pro ?: professionals.firstOrNull { it.categories.contains(cat.id) } ?: professionals.first(),
            problemDescription = if (cat.id == "hvac") "Air conditioner is not cooling properly and blowing warm air." else "General diagnostic and repair needed.",
            preferredDate = "Today",
            preferredTime = "10:30 AM - 12:30 PM",
            locationAddress = repository.userLocation.value
        )
        navigateCustomer(CustomerScreen.BOOKING_FLOW)
    }

    fun updateBookingWizard(update: BookingWizardState.() -> BookingWizardState) {
        _bookingWizard.value = _bookingWizard.value.update()
    }

    fun confirmBooking() {
        val st = _bookingWizard.value
        val cat = st.selectedCategory ?: categories.first()
        val pro = st.selectedPro ?: professionals.first()

        viewModelScope.launch {
            _bookingWizard.value = _bookingWizard.value.copy(isSubmitting = true)
            delay(600) // Network & payment tokenization simulation
            val newBooking = repository.createBooking(
                category = cat,
                pro = pro,
                date = st.preferredDate,
                time = st.preferredTime,
                address = st.locationAddress,
                problem = st.problemDescription,
                urgency = st.urgency,
                paymentMethod = st.selectedPaymentMethod
            )
            _activeTrackingBooking.value = newBooking
            _bookingWizard.value = _bookingWizard.value.copy(
                isSubmitting = false,
                step = 7, // Confirmation step
                confirmedBooking = newBooking
            )
        }
    }

    fun openLiveTracking(booking: Booking) {
        _activeTrackingBooking.value = booking
        navigateCustomer(CustomerScreen.LIVE_TRACKING)
    }

    // Advance live tracking demo status
    fun advanceTrackingStatus() {
        val cur = _activeTrackingBooking.value ?: return
        val nextStatus = when (cur.status) {
            BookingStatus.CONFIRMED -> BookingStatus.ACCEPTED
            BookingStatus.ACCEPTED -> BookingStatus.ON_THE_WAY
            BookingStatus.ON_THE_WAY -> BookingStatus.ARRIVED
            BookingStatus.ARRIVED -> BookingStatus.WORKING
            BookingStatus.WORKING -> BookingStatus.COMPLETED
            BookingStatus.COMPLETED -> BookingStatus.COMPLETED
            BookingStatus.CANCELLED -> BookingStatus.CANCELLED
        }
        repository.updateBookingStatus(cur.id, nextStatus)
        _activeTrackingBooking.value = repository.bookings.value.find { it.id == cur.id }
    }

    fun simulateProAdditionalWorkRequest() {
        val cur = _activeTrackingBooking.value ?: return
        repository.requestAdditionalWork(
            bookingId = cur.id,
            desc = "Heavy-Duty Dual Run Capacitor (45/5 uF 440V)",
            reason = "Original run capacitor failed under load, preventing compressor rotation.",
            costUsd = 28.50
        )
        _activeTrackingBooking.value = repository.bookings.value.find { it.id == cur.id }
        showExtraWorkApprovalDialog.value = true
    }

    fun approveExtraWork() {
        val cur = _activeTrackingBooking.value ?: return
        repository.approveAdditionalWork(cur.id)
        _activeTrackingBooking.value = repository.bookings.value.find { it.id == cur.id }
        showExtraWorkApprovalDialog.value = false
    }

    fun releaseEscrowPayment() {
        val cur = _activeTrackingBooking.value ?: return
        repository.releaseEscrowPayment(cur.id)
        _activeTrackingBooking.value = repository.bookings.value.find { it.id == cur.id }
        showReviewDialog.value = true
    }

    fun sendChatMessage(bookingId: String, text: String) {
        val role = if (currentRole.value == AppRole.PROFESSIONAL) "PROFESSIONAL" else "CUSTOMER"
        val name = if (currentRole.value == AppRole.PROFESSIONAL) "Alex Morgan" else "Gabriel"
        repository.sendMessage(bookingId, role, name, text)
    }

    fun submitReview(proId: String, rating: Double, comment: String, quality: Double, prof: Double, punct: Double, value: Double) {
        val review = Review(
            id = "rev_" + System.currentTimeMillis(),
            authorName = "Gabriel Sterling",
            authorAvatar = "GS",
            date = "Just now",
            rating = rating,
            comment = comment,
            quality = quality,
            professionalism = prof,
            punctuality = punct,
            value = value,
            serviceName = "AC / HVAC Diagnostics",
            verifiedBooking = true
        )
        repository.addReview(proId, review)
        showReviewDialog.value = false
    }

    fun setCurrency(currency: GlobalCurrency) {
        repository.setCurrency(currency)
        showCurrencyDialog.value = false
    }

    fun quickToggleCurrency() {
        val current = selectedCurrency.value
        val target = if (current.code == "IDR") {
            repository.currencies.first { it.code == "USD" }
        } else {
            repository.currencies.first { it.code == "IDR" }
        }
        setCurrency(target)
    }

    fun setLanguage(language: GlobalLanguage) {
        repository.setLanguage(language)
        showLanguageDialog.value = false
    }

    fun addNewAddress(
        title: String,
        streetAddress: String,
        city: String,
        stateProvince: String,
        country: String,
        postalCode: String,
        technicianNotes: String = ""
    ) {
        val newAddr = repository.addNewAddress(title, streetAddress, city, stateProvince, country, postalCode, technicianNotes)
        updateBookingWizard { copy(locationAddress = "${newAddr.streetAddress}, ${newAddr.city}") }
        showAddAddressDialog.value = false
    }

    fun deleteAddress(addressId: String) {
        repository.deleteAddress(addressId)
    }

    fun setDefaultAddress(address: UserAddress) {
        repository.setDefaultAddress(address)
        updateBookingWizard { copy(locationAddress = "${address.streetAddress}, ${address.city}") }
    }

    fun formatPrice(amountUsd: Double): String {
        return repository.formatPrice(amountUsd)
    }
}
