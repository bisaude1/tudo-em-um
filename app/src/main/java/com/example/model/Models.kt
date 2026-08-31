package com.example.model

data class User(
    val id: String,
    val name: String,
    val phone: String,
    val email: String,
    val role: UserRole,
    val province: String = "Luanda",
    val municipality: String = "Talatona",
    val district: String = "Talatona",
    val neighborhood: String = "Camama",
    val avatarUrl: String = ""
)

data class LocationItem(
    val id: String,
    val province: String,
    val municipality: String,
    val district: String,
    val neighborhood: String
)

data class Category(
    val id: String,
    val name: String,
    val iconEmoji: String,
    val description: String,
    val subcategories: List<String>
)

data class ProviderProfile(
    val id: String,
    val name: String,
    val phone: String,
    val profession: String,
    val categoryId: String,
    val rating: Double,
    val reviewsCount: Int,
    val isVerified: Boolean,
    val distanceKm: Double,
    val startingPriceKz: Long,
    val experienceYears: Int,
    val availabilityText: String, // e.g. "Hoje", "Disponível agora", "Amanhã"
    val isAvailableNow: Boolean,
    val bio: String,
    val services: List<String>,
    val servedNeighborhoods: List<String>,
    val completionRatePercent: Int = 98,
    val responseTimeMinutes: Int = 5,
    val verificationStatus: VerificationStatus = VerificationStatus.VERIFIED
) {
    // Recommendation algorithm according to technical spec:
    // Score = 30% rating + 25% distance + 15% availability + 15% experience + 10% completion + 5% response time
    fun calculateScore(): Int {
        val ratingFactor = (rating / 5.0) * 30.0
        val distFactor = (1.0 - (distanceKm.coerceAtMost(20.0) / 20.0)) * 25.0
        val availFactor = if (isAvailableNow) 15.0 else 8.0
        val expFactor = ((experienceYears.coerceAtMost(15)) / 15.0) * 15.0
        val compFactor = (completionRatePercent / 100.0) * 10.0
        val respFactor = (1.0 - (responseTimeMinutes.coerceAtMost(60) / 60.0)) * 5.0
        return (ratingFactor + distFactor + availFactor + expFactor + compFactor + respFactor).toInt().coerceIn(10, 99)
    }
}

data class ServiceRequest(
    val id: String,
    val customerId: String,
    val customerName: String,
    val customerPhone: String,
    val categoryId: String,
    val subcategory: String,
    val description: String,
    val province: String = "Luanda",
    val municipality: String = "Talatona",
    val neighborhood: String = "Camama",
    val scheduledDate: String,
    val scheduledTime: String,
    val urgency: ServiceUrgency = ServiceUrgency.NORMAL,
    val status: ServiceStatus = ServiceStatus.PENDENTE,
    val selectedProviderId: String? = null,
    val agreedPriceKz: Long? = null,
    val platformFeeKz: Long? = null,
    val createdAtMillis: Long = System.currentTimeMillis()
)

data class Quote(
    val id: String,
    val requestId: String,
    val providerId: String,
    val providerName: String,
    val providerProfession: String,
    val providerRating: Double,
    val providerDistanceKm: Double,
    val providerExperienceYears: Int,
    val availabilityText: String,
    val priceKz: Long,
    val etaText: String,
    val note: String,
    val isAccepted: Boolean = false
)

data class ChatMessage(
    val id: String,
    val requestId: String,
    val senderId: String,
    val senderName: String,
    val isFromCustomer: Boolean,
    val text: String,
    val type: MessageType = MessageType.TEXT,
    val quotePriceKz: Long? = null,
    val timestamp: String = "14:32"
)

data class Review(
    val id: String,
    val requestId: String,
    val providerId: String,
    val customerName: String,
    val overallRating: Int,
    val qualityRating: Int,
    val punctualityRating: Int,
    val serviceRating: Int,
    val priceRating: Int,
    val comment: String,
    val date: String
)

data class AdminPlatformMetrics(
    val totalCustomers: Int = 8420,
    val totalProviders: Int = 2315,
    val totalRequests: Int = 12840,
    val completedServices: Int = 9630,
    val platformRevenueKz: Long = 14250000L
)

data class Dispute(
    val id: String,
    val requestId: String,
    val customerName: String,
    val providerName: String,
    val serviceName: String,
    val reason: String,
    val date: String,
    val status: String = "Em Análise"
)
