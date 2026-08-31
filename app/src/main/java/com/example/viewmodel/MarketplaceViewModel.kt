package com.example.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.data.MarketplaceRepository
import com.example.model.*
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch

sealed class ScreenDestination {
    object Splash : ScreenDestination()
    object AuthRegister : ScreenDestination()
    object AuthOtp : ScreenDestination()
    object LocationSetup : ScreenDestination()

    // Client screens
    object ClientHome : ScreenDestination()
    data class ProviderList(val categoryId: String? = null, val query: String = "") : ScreenDestination()
    data class ProviderDetail(val providerId: String) : ScreenDestination()
    data class CreateRequest(val categoryId: String, val subcategory: String = "") : ScreenDestination()
    data class RequestQuotes(val requestId: String) : ScreenDestination()
    data class QuotesComparison(val requestId: String) : ScreenDestination()
    data class Chat(val requestId: String, val providerId: String) : ScreenDestination()
    data class Checkout(val requestId: String, val quoteId: String) : ScreenDestination()
    object OrdersList : ScreenDestination()
    data class OrderTracking(val requestId: String) : ScreenDestination()
    data class ReviewService(val requestId: String, val providerId: String) : ScreenDestination()
    object UrgentSOS : ScreenDestination()

    // Provider screens
    object ProviderDashboard : ScreenDestination()
    object ProviderRequests : ScreenDestination()
    object ProviderAgenda : ScreenDestination()
    object ProviderWallet : ScreenDestination()
    object ProviderProfileScreen : ScreenDestination()

    // Admin screens
    object AdminDashboard : ScreenDestination()
    object AdminProfessionals : ScreenDestination()
    object AdminCategories : ScreenDestination()
    object AdminLocations : ScreenDestination()
    object AdminDisputes : ScreenDestination()
    object AdminReports : ScreenDestination()
}

data class DiagnosticResult(
    val detectedCategory: Category,
    val suggestedSubcategory: String,
    val identifiedProblem: String,
    val confidencePercent: Int = 96
)

class MarketplaceViewModel(
    private val repository: MarketplaceRepository = MarketplaceRepository()
) : ViewModel() {

    val currentUser: StateFlow<User> = repository.currentUser
    val currentRole: StateFlow<UserRole> = repository.currentRole
    val categories: StateFlow<List<Category>> = repository.categories
    val locations: StateFlow<List<LocationItem>> = repository.locations
    val providers: StateFlow<List<ProviderProfile>> = repository.providers
    val serviceRequests: StateFlow<List<ServiceRequest>> = repository.serviceRequests
    val quotes: StateFlow<List<Quote>> = repository.quotes
    val chatMessages: StateFlow<List<ChatMessage>> = repository.chatMessages
    val reviews: StateFlow<List<Review>> = repository.reviews
    val disputes: StateFlow<List<Dispute>> = repository.disputes
    val adminMetrics: StateFlow<AdminPlatformMetrics> = repository.adminMetrics

    val providerAvailableBalanceKz: StateFlow<Long> = repository.providerAvailableBalanceKz
    val providerPendingBalanceKz: StateFlow<Long> = repository.providerPendingBalanceKz
    val providerTotalReceivedKz: StateFlow<Long> = repository.providerTotalReceivedKz

    private val _currentScreen = MutableStateFlow<ScreenDestination>(ScreenDestination.Splash)
    val currentScreen: StateFlow<ScreenDestination> = _currentScreen.asStateFlow()

    private val _searchQuery = MutableStateFlow("")
    val searchQuery: StateFlow<String> = _searchQuery.asStateFlow()

    private val _filterDistanceMaxKm = MutableStateFlow(25.0)
    val filterDistanceMaxKm: StateFlow<Double> = _filterDistanceMaxKm.asStateFlow()

    private val _filterMinRating = MutableStateFlow(0.0)
    val filterMinRating: StateFlow<Double> = _filterMinRating.asStateFlow()

    private val _filterOnlyAvailableNow = MutableStateFlow(false)
    val filterOnlyAvailableNow: StateFlow<Boolean> = _filterOnlyAvailableNow.asStateFlow()

    private val _filterSortBy = MutableStateFlow("score") // score, price, distance, rating
    val filterSortBy: StateFlow<String> = _filterSortBy.asStateFlow()

    private val _diagnosticResult = MutableStateFlow<DiagnosticResult?>(null)
    val diagnosticResult: StateFlow<DiagnosticResult?> = _diagnosticResult.asStateFlow()

    // Auth OTP state
    val registrationPhone = MutableStateFlow("+244 923 456 789")
    val registrationName = MutableStateFlow("Carlos Bento")
    val otpCode = MutableStateFlow("")

    fun navigateTo(screen: ScreenDestination) {
        _currentScreen.value = screen
    }

    fun switchRole(role: UserRole) {
        repository.switchRole(role)
        when (role) {
            UserRole.CLIENT -> navigateTo(ScreenDestination.ClientHome)
            UserRole.PROVIDER -> navigateTo(ScreenDestination.ProviderDashboard)
            UserRole.ADMIN -> navigateTo(ScreenDestination.AdminDashboard)
        }
    }

    fun onSearchQueryChanged(q: String) {
        _searchQuery.value = q
        analyzeNaturalLanguageQuery(q)
    }

    fun analyzeNaturalLanguageQuery(input: String) {
        val lower = input.lowercase().trim()
        if (lower.length < 4) {
            _diagnosticResult.value = null
            return
        }

        val allCats = categories.value
        val constrCat = allCats.find { it.id == "cat_construcao" } ?: allCats.firstOrNull()
        val autoCat = allCats.find { it.id == "cat_auto" } ?: allCats.firstOrNull()
        val reparCat = allCats.find { it.id == "cat_reparacao" } ?: allCats.firstOrNull()
        val limpCat = allCats.find { it.id == "cat_limpeza" } ?: allCats.firstOrNull()

        if (constrCat != null && (lower.contains("energia") || lower.contains("luz") || lower.contains("disjuntor") || lower.contains("tomada") || lower.contains("curto") || lower.contains("apagão") || lower.contains("sem energia") || lower.contains("quadro"))) {
            _diagnosticResult.value = DiagnosticResult(
                detectedCategory = constrCat,
                suggestedSubcategory = "Eletricista",
                identifiedProblem = "Falha de energia / Curto-circuito elétrico"
            )
        } else if (constrCat != null && (lower.contains("água") || lower.contains("fuga") || lower.contains("cano") || lower.contains("torneira") || lower.contains("esgoto") || lower.contains("tanque") || lower.contains("inund"))) {
            _diagnosticResult.value = DiagnosticResult(
                detectedCategory = constrCat,
                suggestedSubcategory = "Canalizador",
                identifiedProblem = "Fuga de água ou tubagem danificada"
            )
        } else if (autoCat != null && (lower.contains("carro") || lower.contains("motor") || lower.contains("arranca") || lower.contains("bateria") || lower.contains("pneu") || lower.contains("travão"))) {
            _diagnosticResult.value = DiagnosticResult(
                detectedCategory = autoCat,
                suggestedSubcategory = "Mecânico Auto",
                identifiedProblem = "Avaria mecânica automóvel"
            )
        } else if (reparCat != null && (lower.contains("ar condicionado") || lower.contains("ar frio") || lower.contains("split") || lower.contains("não gela") || lower.contains("gerador"))) {
            _diagnosticResult.value = DiagnosticResult(
                detectedCategory = reparCat,
                suggestedSubcategory = if (lower.contains("gerador")) "Geradores" else "Ar Condicionado",
                identifiedProblem = "Manutenção ou falha de equipamento"
            )
        } else if (limpCat != null && (lower.contains("limpar") || lower.contains("faxina") || lower.contains("sofá") || lower.contains("obra") || lower.contains("tapete"))) {
            _diagnosticResult.value = DiagnosticResult(
                detectedCategory = limpCat,
                suggestedSubcategory = "Limpeza Profunda",
                identifiedProblem = "Higienização e higienização residencial"
            )
        } else {
            _diagnosticResult.value = null
        }
    }

    fun setFilterSort(sortBy: String) {
        _filterSortBy.value = sortBy
    }

    fun toggleFilterOnlyAvailable(onlyAvail: Boolean) {
        _filterOnlyAvailableNow.value = onlyAvail
    }

    fun setFilterMinRating(rating: Double) {
        _filterMinRating.value = rating
    }

    fun updateLocation(province: String, municipality: String, district: String, neighborhood: String) {
        repository.updateLocation(province, municipality, district, neighborhood)
    }

    fun submitNewRequest(
        categoryId: String,
        subcategory: String,
        description: String,
        date: String,
        time: String,
        urgency: ServiceUrgency
    ) {
        val req = repository.createServiceRequest(
            categoryId = categoryId,
            subcategory = subcategory,
            description = description,
            date = date,
            time = time,
            urgency = urgency
        )
        navigateTo(ScreenDestination.RequestQuotes(req.id))
    }

    fun selectQuoteAndCheckout(quote: Quote) {
        repository.selectQuote(quote)
        navigateTo(ScreenDestination.Checkout(quote.requestId, quote.id))
    }

    fun completePayment(requestId: String, method: PaymentMethod) {
        repository.payServiceRequest(requestId, method)
        navigateTo(ScreenDestination.OrderTracking(requestId))
    }

    fun advanceOrderStage(requestId: String, nextStatus: ServiceStatus) {
        repository.advanceServiceStatus(requestId, nextStatus)
    }

    fun sendChatMessage(requestId: String, text: String, isCustomer: Boolean, type: MessageType = MessageType.TEXT, quotePriceKz: Long? = null) {
        repository.sendChatMessage(requestId, text, isCustomer, type, quotePriceKz)
    }

    fun submitServiceReview(
        requestId: String,
        providerId: String,
        quality: Int,
        punctuality: Int,
        service: Int,
        price: Int,
        comment: String
    ) {
        repository.submitReview(requestId, providerId, quality, punctuality, service, price, comment)
        navigateTo(ScreenDestination.OrderTracking(requestId))
    }

    fun reportDispute(requestId: String, providerName: String, serviceName: String, reason: String) {
        repository.addDispute(requestId, providerName, serviceName, reason)
        navigateTo(ScreenDestination.OrderTracking(requestId))
    }

    fun resolveDispute(disputeId: String) {
        repository.resolveDispute(disputeId)
    }

    fun withdrawProviderFunds(amountKz: Long): Boolean {
        return repository.withdrawFunds(amountKz)
    }

    fun toggleProviderVerification(providerId: String) {
        repository.toggleProviderStatus(providerId)
    }

    fun addNewCategory(name: String, emoji: String, desc: String, subs: List<String>) {
        repository.addCategory(name, emoji, desc, subs)
    }

    fun addNewLocation(province: String, municipality: String, district: String, neighborhood: String) {
        repository.addLocation(province, municipality, district, neighborhood)
    }

    fun getFilteredProviders(categoryId: String? = null, query: String = ""): List<ProviderProfile> {
        val q = query.lowercase().trim()
        var list = providers.value.filter { p ->
            val matchesCategory = categoryId.isNullOrEmpty() || p.categoryId == categoryId
            val matchesQuery = q.isEmpty() ||
                    p.name.lowercase().contains(q) ||
                    p.profession.lowercase().contains(q) ||
                    p.services.any { it.lowercase().contains(q) } ||
                    p.servedNeighborhoods.any { it.lowercase().contains(q) }

            val matchesAvailable = !_filterOnlyAvailableNow.value || p.isAvailableNow
            val matchesRating = p.rating >= _filterMinRating.value
            val matchesDistance = p.distanceKm <= _filterDistanceMaxKm.value

            matchesCategory && matchesQuery && matchesAvailable && matchesRating && matchesDistance
        }

        return when (_filterSortBy.value) {
            "price" -> list.sortedBy { it.startingPriceKz }
            "distance" -> list.sortedBy { it.distanceKm }
            "rating" -> list.sortedByDescending { it.rating }
            else -> list.sortedByDescending { it.calculateScore() } // Default smart Angola algorithm
        }
    }
}
