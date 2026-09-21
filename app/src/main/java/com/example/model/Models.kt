package com.example.model

enum class AppRole {
    CUSTOMER,
    PROFESSIONAL,
    ADMIN,
    DEVELOPER
}

enum class CustomerScreen {
    HOME,
    EXPLORE,
    BOOKINGS,
    MESSAGES,
    PROFILE,
    PRO_DETAIL,
    BOOKING_FLOW,
    LIVE_TRACKING,
    EMERGENCY,
    SMART_SEARCH,
    SAFETY_CENTER
}

enum class ProScreen {
    DASHBOARD,
    JOBS,
    MESSAGES,
    EARNINGS,
    PROFILE,
    VERIFICATION_CENTER
}

data class ServiceCategory(
    val id: String,
    val name: String,
    val iconName: String,
    val description: String,
    val startingPriceUsd: Double,
    val isEmergency: Boolean = false,
    val isPopular: Boolean = false,
    val activeProsCount: Int = 120
)

data class PortfolioItem(
    val id: String,
    val title: String,
    val beforeDesc: String,
    val afterDesc: String,
    val completedDate: String
)

data class Review(
    val id: String,
    val authorName: String,
    val authorAvatar: String,
    val date: String,
    val rating: Double,
    val comment: String,
    val quality: Double = 5.0,
    val professionalism: Double = 5.0,
    val punctuality: Double = 5.0,
    val value: Double = 4.8,
    val serviceName: String,
    val verifiedBooking: Boolean = true
)

data class VerificationItem(
    val title: String,
    val isVerified: Boolean,
    val badgeLabel: String,
    val verifiedDate: String,
    val description: String
)

data class Professional(
    val id: String,
    val name: String,
    val title: String,
    val rating: Double,
    val reviewCount: Int,
    val completedJobs: Int,
    val experienceYears: Int,
    val distanceKm: Double,
    val startingPriceUsd: Double,
    val hourlyRateUsd: Double,
    val responseTimeMin: Int,
    val availability: String,
    val isVerified: Boolean = true,
    val hasInsurance: Boolean = true,
    val hasWorkGuarantee: Boolean = true,
    val languages: List<String>,
    val categories: List<String>,
    val bio: String,
    val avatarRes: Int? = null,
    val beforeAfterPortfolios: List<PortfolioItem> = emptyList(),
    val reviews: List<Review> = emptyList(),
    val verifications: List<VerificationItem> = emptyList(),
    val serviceRadiusKm: Int = 25,
    val locationName: String = "Metro Area"
)

enum class BookingStatus {
    CONFIRMED,
    ACCEPTED,
    ON_THE_WAY,
    ARRIVED,
    WORKING,
    COMPLETED,
    CANCELLED
}

enum class JobProgressStep {
    JOB_STARTED,
    INSPECTION,
    WORK_IN_PROGRESS,
    EXTRA_WORK_REQUESTED,
    COMPLETED
}

enum class PaymentStatus {
    AUTHORIZED,
    HELD_IN_ESCROW,
    RELEASED,
    REFUNDED
}

data class ExtraWorkQuote(
    val description: String,
    val reason: String,
    val additionalCostUsd: Double,
    val isApproved: Boolean = false
)

data class Booking(
    val id: String,
    val serviceCategoryName: String,
    val professional: Professional,
    val status: BookingStatus,
    val progressStep: JobProgressStep,
    val scheduledDate: String,
    val scheduledTime: String,
    val customerName: String,
    val customerAddress: String,
    val problemDescription: String,
    val urgency: String,
    val estimatedMinUsd: Double,
    val estimatedMaxUsd: Double,
    val baseCostUsd: Double,
    val paymentMethod: String,
    val paymentStatus: PaymentStatus,
    val liveEtaMinutes: Int,
    val technicianDistanceKm: Double,
    val extraWork: ExtraWorkQuote? = null,
    val createdAt: Long = System.currentTimeMillis()
)

data class ChatMessage(
    val id: String,
    val bookingId: String,
    val senderRole: String, // "CUSTOMER", "PROFESSIONAL", "SYSTEM"
    val senderName: String,
    val text: String,
    val timestamp: String,
    val isAudio: Boolean = false,
    val isLocation: Boolean = false,
    val isImage: Boolean = false
)

data class GlobalCurrency(
    val code: String,
    val symbol: String,
    val name: String,
    val rateToUsd: Double
)

data class GlobalLanguage(
    val code: String,
    val name: String,
    val localName: String
)

data class EmergencyServiceType(
    val id: String,
    val title: String,
    val iconName: String,
    val description: String,
    val avgEtaMin: Int,
    val baseFeeUsd: Double
)

data class RoleDefinition(
    val role: AppRole,
    val title: String,
    val indonesianTitle: String,
    val description: String,
    val permissions: List<String>,
    val canAlterSecurity: Boolean,
    val requiresPin: Boolean,
    val defaultPin: String? = null
)

data class SecurityFeatureFlag(
    val id: String,
    val title: String,
    val description: String,
    val isEnabled: Boolean,
    val category: String = "SECURITY", // "SECURITY", "ESCROW", "SYSTEM", "AI"
    val requiresDevRole: Boolean = true
)

data class SystemAuditLog(
    val id: String,
    val timestamp: String,
    val actorRole: String,
    val action: String,
    val details: String,
    val severity: String = "INFO" // "INFO", "WARN", "CRITICAL"
)

data class UserAddress(
    val id: String,
    val title: String, // e.g. "Rumah Jakarta", "Apartemen", "Manhattan Office"
    val streetAddress: String,
    val city: String,
    val stateProvince: String,
    val country: String, // "Indonesia", "United States", "Global"
    val postalCode: String,
    val technicianNotes: String = "",
    val isDefault: Boolean = false
)

enum class PaymentCategoryType(val displayName: String, val badge: String) {
    ALL("Semua", "Global"),
    QRIS_INDONESIA("QRIS", "Indonesia"),
    VIRTUAL_ACCOUNT("Virtual Account", "Indo & US"),
    BANK_CARD("Kartu Debit/Kredit", "Indo & US"),
    E_WALLET("E-Wallet", "Indo & US"),
    CASH_ON_DELIVERY("Bayar di Tempat", "COD / Tunai")
}

data class PaymentMethodItem(
    val id: String,
    val name: String,
    val category: PaymentCategoryType,
    val countryOrigin: String, // "ID" (Indonesia), "US" (USA), "GLOBAL"
    val subtext: String,
    val iconType: String, // "qris", "card", "bank", "wallet", "cash"
    val badge: String? = null,
    val isEscrowProtected: Boolean = true
)


