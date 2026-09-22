package com.example.data

import android.util.Log
import com.example.model.*
import com.google.android.gms.tasks.Task
import com.google.firebase.firestore.DocumentSnapshot
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.QuerySnapshot
import com.google.firebase.firestore.SetOptions
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.suspendCancellableCoroutine
import kotlin.coroutines.resume
import kotlin.coroutines.resumeWithException

/**
 * Repository layer for Firebase Cloud Firestore integration.
 * Handles storage, real-time synchronization, and retrieval of
 * Professional profiles, Service categories, and Emergency service data.
 */
class FirestoreRepository(
    private val firestore: FirebaseFirestore? = tryGetFirestore()
) {

    companion object {
        private const val TAG = "FirestoreRepository"
        const val COLLECTION_PROFESSIONALS = "professionals"
        const val COLLECTION_SERVICES = "service_categories"
        const val COLLECTION_EMERGENCY_SERVICES = "emergency_services"
        const val COLLECTION_SYSTEM_LOGS = "system_audit_logs"

        fun tryGetFirestore(): FirebaseFirestore? {
            return try {
                FirebaseFirestore.getInstance()
            } catch (e: Exception) {
                Log.w(TAG, "Firebase not yet initialized or google-services.json not found: ${e.message}")
                null
            }
        }
    }

    /**
     * Checks if Firestore is currently initialized and connected.
     */
    fun isAvailable(): Boolean = firestore != null

    // ==========================================
    // PROFESSIONALS REPOSITORY OPERATIONS
    // ==========================================

    /**
     * Real-time Flow of all professional profiles from Firestore.
     * Falls back gracefully to [fallbackList] if Firestore is unavailable.
     */
    fun getProfessionalsFlow(fallbackList: List<Professional> = emptyList()): Flow<List<Professional>> = callbackFlow {
        val db = firestore
        if (db == null) {
            trySend(fallbackList)
            close()
            return@callbackFlow
        }

        val registration = db.collection(COLLECTION_PROFESSIONALS)
            .addSnapshotListener { snapshot, error ->
                if (error != null) {
                    Log.w(TAG, "Listen to professionals failed: ${error.message}")
                    trySend(fallbackList)
                    return@addSnapshotListener
                }

                if (snapshot != null && !snapshot.isEmpty) {
                    val pros = snapshot.documents.mapNotNull { it.toProfessional() }
                    trySend(pros)
                } else {
                    // Empty collection: emit fallback so UI displays initial marketplace
                    trySend(fallbackList)
                }
            }

        awaitClose { registration.remove() }
    }

    /**
     * Retrieves a single professional by ID from Firestore.
     */
    suspend fun getProfessionalById(id: String): Professional? {
        val db = firestore ?: return null
        return try {
            val doc = db.collection(COLLECTION_PROFESSIONALS).document(id).get().awaitTask()
            doc.toProfessional()
        } catch (e: Exception) {
            Log.e(TAG, "Error fetching professional $id: ${e.message}")
            null
        }
    }

    /**
     * Stores or updates a professional profile in Firestore.
     */
    suspend fun saveProfessional(professional: Professional): Result<Unit> {
        val db = firestore ?: return Result.failure(IllegalStateException("Firestore is not initialized"))
        return try {
            val map = professional.toFirestoreMap()
            db.collection(COLLECTION_PROFESSIONALS)
                .document(professional.id)
                .set(map, SetOptions.merge())
                .awaitTask()
            Log.d(TAG, "Successfully saved professional: ${professional.id} - ${professional.name}")
            Result.success(Unit)
        } catch (e: Exception) {
            Log.e(TAG, "Failed to save professional ${professional.id}: ${e.message}", e)
            Result.failure(e)
        }
    }

    /**
     * Deletes a professional profile from Firestore.
     */
    suspend fun deleteProfessional(id: String): Result<Unit> {
        val db = firestore ?: return Result.failure(IllegalStateException("Firestore is not initialized"))
        return try {
            db.collection(COLLECTION_PROFESSIONALS).document(id).delete().awaitTask()
            Result.success(Unit)
        } catch (e: Exception) {
            Log.e(TAG, "Failed to delete professional $id: ${e.message}", e)
            Result.failure(e)
        }
    }

    // ==========================================
    // SERVICE CATEGORIES REPOSITORY OPERATIONS
    // ==========================================

    /**
     * Real-time Flow of service categories from Firestore.
     */
    fun getCategoriesFlow(fallbackList: List<ServiceCategory> = emptyList()): Flow<List<ServiceCategory>> = callbackFlow {
        val db = firestore
        if (db == null) {
            trySend(fallbackList)
            close()
            return@callbackFlow
        }

        val registration = db.collection(COLLECTION_SERVICES)
            .addSnapshotListener { snapshot, error ->
                if (error != null) {
                    Log.w(TAG, "Listen to service categories failed: ${error.message}")
                    trySend(fallbackList)
                    return@addSnapshotListener
                }

                if (snapshot != null && !snapshot.isEmpty) {
                    val categories = snapshot.documents.mapNotNull { it.toServiceCategory() }
                    trySend(categories)
                } else {
                    trySend(fallbackList)
                }
            }

        awaitClose { registration.remove() }
    }

    /**
     * Stores or updates a service category in Firestore.
     */
    suspend fun saveCategory(category: ServiceCategory): Result<Unit> {
        val db = firestore ?: return Result.failure(IllegalStateException("Firestore is not initialized"))
        return try {
            val map = category.toFirestoreMap()
            db.collection(COLLECTION_SERVICES)
                .document(category.id)
                .set(map, SetOptions.merge())
                .awaitTask()
            Result.success(Unit)
        } catch (e: Exception) {
            Log.e(TAG, "Failed to save category ${category.id}: ${e.message}", e)
            Result.failure(e)
        }
    }

    // ==========================================
    // EMERGENCY SERVICES REPOSITORY OPERATIONS
    // ==========================================

    /**
     * Real-time Flow of emergency service types from Firestore.
     */
    fun getEmergencyServicesFlow(fallbackList: List<EmergencyServiceType> = emptyList()): Flow<List<EmergencyServiceType>> = callbackFlow {
        val db = firestore
        if (db == null) {
            trySend(fallbackList)
            close()
            return@callbackFlow
        }

        val registration = db.collection(COLLECTION_EMERGENCY_SERVICES)
            .addSnapshotListener { snapshot, error ->
                if (error != null) {
                    Log.w(TAG, "Listen to emergency services failed: ${error.message}")
                    trySend(fallbackList)
                    return@addSnapshotListener
                }

                if (snapshot != null && !snapshot.isEmpty) {
                    val services = snapshot.documents.mapNotNull { it.toEmergencyServiceType() }
                    trySend(services)
                } else {
                    trySend(fallbackList)
                }
            }

        awaitClose { registration.remove() }
    }

    /**
     * Stores or updates an emergency service type in Firestore.
     */
    suspend fun saveEmergencyService(service: EmergencyServiceType): Result<Unit> {
        val db = firestore ?: return Result.failure(IllegalStateException("Firestore is not initialized"))
        return try {
            val map = service.toFirestoreMap()
            db.collection(COLLECTION_EMERGENCY_SERVICES)
                .document(service.id)
                .set(map, SetOptions.merge())
                .awaitTask()
            Result.success(Unit)
        } catch (e: Exception) {
            Log.e(TAG, "Failed to save emergency service ${service.id}: ${e.message}", e)
            Result.failure(e)
        }
    }

    // ==========================================
    // SEEDING & SYNCHRONIZATION
    // ==========================================

    /**
     * Seeds initial professional profiles, service categories, and emergency services
     * to Firestore so the cloud database is fully initialized with production data.
     */
    suspend fun seedInitialData(
        professionals: List<Professional>,
        categories: List<ServiceCategory>,
        emergencies: List<EmergencyServiceType>
    ): Result<String> {
        val db = firestore ?: return Result.failure(IllegalStateException("Firestore is not available. Please verify Firebase configuration."))
        return try {
            val batch = db.batch()

            // 1. Batch seed professionals
            professionals.forEach { pro ->
                val ref = db.collection(COLLECTION_PROFESSIONALS).document(pro.id)
                batch.set(ref, pro.toFirestoreMap(), SetOptions.merge())
            }

            // 2. Batch seed categories
            categories.forEach { cat ->
                val ref = db.collection(COLLECTION_SERVICES).document(cat.id)
                batch.set(ref, cat.toFirestoreMap(), SetOptions.merge())
            }

            // 3. Batch seed emergency services
            emergencies.forEach { em ->
                val ref = db.collection(COLLECTION_EMERGENCY_SERVICES).document(em.id)
                batch.set(ref, em.toFirestoreMap(), SetOptions.merge())
            }

            batch.commit().awaitTask()
            val summary = "Successfully seeded ${professionals.size} pros, ${categories.size} categories, and ${emergencies.size} emergency services to Firestore."
            Log.i(TAG, summary)
            Result.success(summary)
        } catch (e: Exception) {
            Log.e(TAG, "Batch seed failed: ${e.message}", e)
            Result.failure(e)
        }
    }
}

// ==========================================
// DATA MAPPING & SERIALIZATION HELPERS
// ==========================================

fun Professional.toFirestoreMap(): Map<String, Any?> {
    return mapOf(
        "id" to id,
        "name" to name,
        "title" to title,
        "rating" to rating,
        "reviewCount" to reviewCount,
        "completedJobs" to completedJobs,
        "experienceYears" to experienceYears,
        "distanceKm" to distanceKm,
        "startingPriceUsd" to startingPriceUsd,
        "hourlyRateUsd" to hourlyRateUsd,
        "responseTimeMin" to responseTimeMin,
        "availability" to availability,
        "isVerified" to isVerified,
        "hasInsurance" to hasInsurance,
        "hasWorkGuarantee" to hasWorkGuarantee,
        "languages" to languages,
        "categories" to categories,
        "bio" to bio,
        "avatarRes" to avatarRes,
        "serviceRadiusKm" to serviceRadiusKm,
        "locationName" to locationName,
        "beforeAfterPortfolios" to beforeAfterPortfolios.map { it.toFirestoreMap() },
        "reviews" to reviews.map { it.toFirestoreMap() },
        "verifications" to verifications.map { it.toFirestoreMap() },
        "updatedAt" to System.currentTimeMillis()
    )
}

fun DocumentSnapshot.toProfessional(): Professional? {
    val data = data ?: return null
    return try {
        val rawPortfolios = data["beforeAfterPortfolios"] as? List<*>
        val portfolios = rawPortfolios?.mapNotNull { item ->
            (item as? Map<*, *>)?.toPortfolioItem()
        } ?: emptyList()

        val rawReviews = data["reviews"] as? List<*>
        val reviews = rawReviews?.mapNotNull { item ->
            (item as? Map<*, *>)?.toReview()
        } ?: emptyList()

        val rawVerifications = data["verifications"] as? List<*>
        val verifications = rawVerifications?.mapNotNull { item ->
            (item as? Map<*, *>)?.toVerificationItem()
        } ?: emptyList()

        @Suppress("UNCHECKED_CAST")
        val languages = (data["languages"] as? List<String>) ?: listOf("English")
        @Suppress("UNCHECKED_CAST")
        val categories = (data["categories"] as? List<String>) ?: emptyList()

        Professional(
            id = getString("id") ?: id,
            name = getString("name") ?: "Professional",
            title = getString("title") ?: "Certified Technician",
            rating = (data["rating"] as? Number)?.toDouble() ?: 5.0,
            reviewCount = (data["reviewCount"] as? Number)?.toInt() ?: 0,
            completedJobs = (data["completedJobs"] as? Number)?.toInt() ?: 0,
            experienceYears = (data["experienceYears"] as? Number)?.toInt() ?: 1,
            distanceKm = (data["distanceKm"] as? Number)?.toDouble() ?: 3.0,
            startingPriceUsd = (data["startingPriceUsd"] as? Number)?.toDouble() ?: 35.0,
            hourlyRateUsd = (data["hourlyRateUsd"] as? Number)?.toDouble() ?: 50.0,
            responseTimeMin = (data["responseTimeMin"] as? Number)?.toInt() ?: 15,
            availability = getString("availability") ?: "Available today",
            isVerified = getBoolean("isVerified") ?: true,
            hasInsurance = getBoolean("hasInsurance") ?: true,
            hasWorkGuarantee = getBoolean("hasWorkGuarantee") ?: true,
            languages = languages,
            categories = categories,
            bio = getString("bio") ?: "",
            avatarRes = (data["avatarRes"] as? Number)?.toInt(),
            beforeAfterPortfolios = portfolios,
            reviews = reviews,
            verifications = verifications,
            serviceRadiusKm = (data["serviceRadiusKm"] as? Number)?.toInt() ?: 25,
            locationName = getString("locationName") ?: "Metro Area"
        )
    } catch (e: Exception) {
        Log.e("FirestoreRepository", "Failed to parse Professional document ${this.id}: ${e.message}")
        null
    }
}

fun PortfolioItem.toFirestoreMap(): Map<String, Any?> = mapOf(
    "id" to id,
    "title" to title,
    "beforeDesc" to beforeDesc,
    "afterDesc" to afterDesc,
    "completedDate" to completedDate
)

fun Map<*, *>.toPortfolioItem(): PortfolioItem? {
    return try {
        PortfolioItem(
            id = this["id"]?.toString() ?: "port_${System.currentTimeMillis()}",
            title = this["title"]?.toString() ?: "",
            beforeDesc = this["beforeDesc"]?.toString() ?: "",
            afterDesc = this["afterDesc"]?.toString() ?: "",
            completedDate = this["completedDate"]?.toString() ?: ""
        )
    } catch (e: Exception) {
        null
    }
}

fun Review.toFirestoreMap(): Map<String, Any?> = mapOf(
    "id" to id,
    "authorName" to authorName,
    "authorAvatar" to authorAvatar,
    "date" to date,
    "rating" to rating,
    "comment" to comment,
    "quality" to quality,
    "professionalism" to professionalism,
    "punctuality" to punctuality,
    "value" to value,
    "serviceName" to serviceName,
    "verifiedBooking" to verifiedBooking
)

fun Map<*, *>.toReview(): Review? {
    return try {
        Review(
            id = this["id"]?.toString() ?: "rev_${System.currentTimeMillis()}",
            authorName = this["authorName"]?.toString() ?: "Client",
            authorAvatar = this["authorAvatar"]?.toString() ?: "C",
            date = this["date"]?.toString() ?: "Recently",
            rating = (this["rating"] as? Number)?.toDouble() ?: 5.0,
            comment = this["comment"]?.toString() ?: "",
            quality = (this["quality"] as? Number)?.toDouble() ?: 5.0,
            professionalism = (this["professionalism"] as? Number)?.toDouble() ?: 5.0,
            punctuality = (this["punctuality"] as? Number)?.toDouble() ?: 5.0,
            value = (this["value"] as? Number)?.toDouble() ?: 5.0,
            serviceName = this["serviceName"]?.toString() ?: "Service",
            verifiedBooking = this["verifiedBooking"] as? Boolean ?: true
        )
    } catch (e: Exception) {
        null
    }
}

fun VerificationItem.toFirestoreMap(): Map<String, Any?> = mapOf(
    "title" to title,
    "isVerified" to isVerified,
    "badgeLabel" to badgeLabel,
    "verifiedDate" to verifiedDate,
    "description" to description
)

fun Map<*, *>.toVerificationItem(): VerificationItem? {
    return try {
        VerificationItem(
            title = this["title"]?.toString() ?: "",
            isVerified = this["isVerified"] as? Boolean ?: true,
            badgeLabel = this["badgeLabel"]?.toString() ?: "Verified",
            verifiedDate = this["verifiedDate"]?.toString() ?: "",
            description = this["description"]?.toString() ?: ""
        )
    } catch (e: Exception) {
        null
    }
}

fun ServiceCategory.toFirestoreMap(): Map<String, Any?> = mapOf(
    "id" to id,
    "name" to name,
    "iconName" to iconName,
    "description" to description,
    "startingPriceUsd" to startingPriceUsd,
    "isEmergency" to isEmergency,
    "isPopular" to isPopular,
    "activeProsCount" to activeProsCount
)

fun DocumentSnapshot.toServiceCategory(): ServiceCategory? {
    val data = data ?: return null
    return try {
        ServiceCategory(
            id = getString("id") ?: id,
            name = getString("name") ?: "Service",
            iconName = getString("iconName") ?: "build",
            description = getString("description") ?: "",
            startingPriceUsd = (data["startingPriceUsd"] as? Number)?.toDouble() ?: 30.0,
            isEmergency = getBoolean("isEmergency") ?: false,
            isPopular = getBoolean("isPopular") ?: false,
            activeProsCount = (data["activeProsCount"] as? Number)?.toInt() ?: 100
        )
    } catch (e: Exception) {
        Log.e("FirestoreRepository", "Failed to parse ServiceCategory document ${this.id}: ${e.message}")
        null
    }
}

fun EmergencyServiceType.toFirestoreMap(): Map<String, Any?> = mapOf(
    "id" to id,
    "title" to title,
    "iconName" to iconName,
    "description" to description,
    "avgEtaMin" to avgEtaMin,
    "baseFeeUsd" to baseFeeUsd
)

fun DocumentSnapshot.toEmergencyServiceType(): EmergencyServiceType? {
    val data = data ?: return null
    return try {
        EmergencyServiceType(
            id = getString("id") ?: id,
            title = getString("title") ?: "Emergency Service",
            iconName = getString("iconName") ?: "warning",
            description = getString("description") ?: "",
            avgEtaMin = (data["avgEtaMin"] as? Number)?.toInt() ?: 15,
            baseFeeUsd = (data["baseFeeUsd"] as? Number)?.toDouble() ?: 50.0
        )
    } catch (e: Exception) {
        Log.e("FirestoreRepository", "Failed to parse EmergencyServiceType document ${this.id}: ${e.message}")
        null
    }
}

// Coroutine Task adapter
private suspend fun <T> Task<T>.awaitTask(): T =
    suspendCancellableCoroutine { continuation ->
        addOnSuccessListener { result ->
            continuation.resume(result)
        }
        addOnFailureListener { exception ->
            continuation.resumeWithException(exception)
        }
        addOnCanceledListener {
            continuation.cancel()
        }
    }
