package com.example.data

import com.example.model.*
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import java.util.UUID

class MarketplaceRepository(
    val databaseClient: DatabaseClient = DatabaseClient.getInstance()
) {

    private val _currentUser = MutableStateFlow(
        User(
            id = "user_carlos",
            name = "Carlos Bento",
            phone = "+244 923 456 789",
            email = "carlos.bento@gmail.com",
            role = UserRole.CLIENT,
            province = "Luanda",
            municipality = "Talatona",
            district = "Talatona",
            neighborhood = "Camama"
        )
    )
    val currentUser: StateFlow<User> = _currentUser.asStateFlow()

    private val _currentRole = MutableStateFlow(UserRole.CLIENT)
    val currentRole: StateFlow<UserRole> = _currentRole.asStateFlow()

    private val _categories = MutableStateFlow<List<Category>>(emptyList())
    val categories: StateFlow<List<Category>> = _categories.asStateFlow()

    private val _locations = MutableStateFlow<List<LocationItem>>(emptyList())
    val locations: StateFlow<List<LocationItem>> = _locations.asStateFlow()

    private val _providers = MutableStateFlow<List<ProviderProfile>>(emptyList())
    val providers: StateFlow<List<ProviderProfile>> = _providers.asStateFlow()

    private val _serviceRequests = MutableStateFlow<List<ServiceRequest>>(emptyList())
    val serviceRequests: StateFlow<List<ServiceRequest>> = _serviceRequests.asStateFlow()

    private val _quotes = MutableStateFlow<List<Quote>>(emptyList())
    val quotes: StateFlow<List<Quote>> = _quotes.asStateFlow()

    private val _chatMessages = MutableStateFlow<List<ChatMessage>>(emptyList())
    val chatMessages: StateFlow<List<ChatMessage>> = _chatMessages.asStateFlow()

    private val _reviews = MutableStateFlow<List<Review>>(emptyList())
    val reviews: StateFlow<List<Review>> = _reviews.asStateFlow()

    private val _disputes = MutableStateFlow<List<Dispute>>(emptyList())
    val disputes: StateFlow<List<Dispute>> = _disputes.asStateFlow()

    private val _adminMetrics = MutableStateFlow(AdminPlatformMetrics())
    val adminMetrics: StateFlow<AdminPlatformMetrics> = _adminMetrics.asStateFlow()

    // Provider Wallet
    private val _providerAvailableBalanceKz = MutableStateFlow(350000L)
    val providerAvailableBalanceKz: StateFlow<Long> = _providerAvailableBalanceKz.asStateFlow()

    private val _providerPendingBalanceKz = MutableStateFlow(80000L)
    val providerPendingBalanceKz: StateFlow<Long> = _providerPendingBalanceKz.asStateFlow()

    private val _providerTotalReceivedKz = MutableStateFlow(1250000L)
    val providerTotalReceivedKz: StateFlow<Long> = _providerTotalReceivedKz.asStateFlow()

    init {
        seedInitialData()
    }

    fun switchRole(role: UserRole) {
        _currentRole.value = role
        if (role == UserRole.PROVIDER) {
            _currentUser.value = _currentUser.value.copy(
                id = "prov_joao",
                name = "João Manuel",
                phone = "+244 912 345 678",
                role = UserRole.PROVIDER,
                neighborhood = "Camama"
            )
        } else if (role == UserRole.CLIENT) {
            _currentUser.value = _currentUser.value.copy(
                id = "user_carlos",
                name = "Carlos Bento",
                phone = "+244 923 456 789",
                role = UserRole.CLIENT,
                neighborhood = "Camama"
            )
        } else {
            _currentUser.value = _currentUser.value.copy(
                id = "admin_master",
                name = "Super Admin Tudo em Um",
                phone = "+244 999 000 111",
                role = UserRole.ADMIN
            )
        }
    }

    fun updateLocation(province: String, municipality: String, district: String, neighborhood: String) {
        _currentUser.value = _currentUser.value.copy(
            province = province,
            municipality = municipality,
            district = district,
            neighborhood = neighborhood
        )
    }

    fun createServiceRequest(
        categoryId: String,
        subcategory: String,
        description: String,
        date: String,
        time: String,
        urgency: ServiceUrgency
    ): ServiceRequest {
        val user = _currentUser.value
        val newReq = ServiceRequest(
            id = "req_${System.currentTimeMillis()}",
            customerId = user.id,
            customerName = user.name,
            customerPhone = user.phone,
            categoryId = categoryId,
            subcategory = subcategory,
            description = description,
            province = user.province,
            municipality = user.municipality,
            neighborhood = user.neighborhood,
            scheduledDate = date,
            scheduledTime = time,
            urgency = urgency,
            status = ServiceStatus.PROPOSTAS_RECEBIDAS
        )

        // Seed 3 realistic competitive quotes for this request as specified in technical specification
        val mockQuotes = listOf(
            Quote(
                id = "quote_1_${newReq.id}",
                requestId = newReq.id,
                providerId = "prov_joao",
                providerName = "João Manuel",
                providerProfession = "Eletricista Certificado",
                providerRating = 4.9,
                providerDistanceKm = 2.1,
                providerExperienceYears = 8,
                availabilityText = if (urgency == ServiceUrgency.URGENT) "Chega em 30 min" else "Disponível Hoje",
                priceKz = 55000L,
                etaText = if (urgency == ServiceUrgency.URGENT) "30 minutos" else "Hoje às 15:00",
                note = "Material padrão e ferramentas inclusos. Diagnóstico completo."
            ),
            Quote(
                id = "quote_2_${newReq.id}",
                requestId = newReq.id,
                providerId = "prov_manuel",
                providerName = "Manuel António",
                providerProfession = "Eletricista e Reparações",
                providerRating = 4.7,
                providerDistanceKm = 4.2,
                providerExperienceYears = 5,
                availabilityText = "Disponível amanhã",
                priceKz = 48000L,
                etaText = "Amanhã de manhã",
                note = "Preço competitivo, garantia de 30 dias após o serviço."
            ),
            Quote(
                id = "quote_3_${newReq.id}",
                requestId = newReq.id,
                providerId = "prov_pedro",
                providerName = "Pedro Domingos",
                providerProfession = "Mestre Eletricista",
                providerRating = 4.8,
                providerDistanceKm = 3.0,
                providerExperienceYears = 10,
                availabilityText = "Disponível hoje",
                priceKz = 60000L,
                etaText = "Hoje às 16:30",
                note = "Técnico sénior com equipamentos de medição digital e alta precisão."
            )
        )

        _serviceRequests.value = listOf(newReq) + _serviceRequests.value
        _quotes.value = mockQuotes + _quotes.value

        // Seed initial chat
        val welcomeMsg = ChatMessage(
            id = UUID.randomUUID().toString(),
            requestId = newReq.id,
            senderId = "prov_joao",
            senderName = "João Manuel",
            isFromCustomer = false,
            text = "Boa tarde Sr. ${user.name.split(" ").firstOrNull() ?: "Carlos"}. Analisei seu pedido de $subcategory em ${user.neighborhood}. Posso comparecer com equipamento completo.",
            type = MessageType.TEXT,
            timestamp = "14:30"
        )
        _chatMessages.value = listOf(welcomeMsg) + _chatMessages.value

        return newReq
    }

    fun selectQuote(quote: Quote) {
        val req = _serviceRequests.value.find { it.id == quote.requestId } ?: return
        val platformFee = (quote.priceKz * 0.10).toLong()
        val updatedReq = req.copy(
            status = ServiceStatus.PAGAMENTO_PENDENTE,
            selectedProviderId = quote.providerId,
            agreedPriceKz = quote.priceKz,
            platformFeeKz = platformFee
        )
        _serviceRequests.value = _serviceRequests.value.map { if (it.id == req.id) updatedReq else it }
        _quotes.value = _quotes.value.map { if (it.id == quote.id) it.copy(isAccepted = true) else it }
    }

    fun payServiceRequest(requestId: String, method: PaymentMethod) {
        _serviceRequests.value = _serviceRequests.value.map {
            if (it.id == requestId) it.copy(status = ServiceStatus.PAGO) else it
        }
    }

    fun advanceServiceStatus(requestId: String, nextStatus: ServiceStatus) {
        _serviceRequests.value = _serviceRequests.value.map {
            if (it.id == requestId) it.copy(status = nextStatus) else it
        }
        if (nextStatus == ServiceStatus.CONFIRMADO) {
            // Add funds to provider balance
            _providerAvailableBalanceKz.value += 49500L // 55000 - 10% platform fee
            _providerTotalReceivedKz.value += 49500L
        }
    }

    fun sendChatMessage(requestId: String, text: String, isCustomer: Boolean, type: MessageType = MessageType.TEXT, quoteKz: Long? = null) {
        val sender = if (isCustomer) _currentUser.value.name else "João Manuel"
        val senderId = if (isCustomer) _currentUser.value.id else "prov_joao"
        val msg = ChatMessage(
            id = UUID.randomUUID().toString(),
            requestId = requestId,
            senderId = senderId,
            senderName = sender,
            isFromCustomer = isCustomer,
            text = text,
            type = type,
            quotePriceKz = quoteKz,
            timestamp = "14:35"
        )
        _chatMessages.value = _chatMessages.value + msg
    }

    fun submitReview(
        requestId: String,
        providerId: String,
        quality: Int,
        punctuality: Int,
        service: Int,
        price: Int,
        comment: String
    ) {
        val overall = ((quality + punctuality + service + price) / 4.0).toInt().coerceIn(1, 5)
        val review = Review(
            id = UUID.randomUUID().toString(),
            requestId = requestId,
            providerId = providerId,
            customerName = _currentUser.value.name,
            overallRating = overall,
            qualityRating = quality,
            punctualityRating = punctuality,
            serviceRating = service,
            priceRating = price,
            comment = comment,
            date = "Hoje"
        )
        _reviews.value = listOf(review) + _reviews.value
        _serviceRequests.value = _serviceRequests.value.map {
            if (it.id == requestId) it.copy(status = ServiceStatus.AVALIADO) else it
        }
    }

    fun addDispute(requestId: String, providerName: String, serviceName: String, reason: String) {
        val d = Dispute(
            id = "disp_${System.currentTimeMillis()}",
            requestId = requestId,
            customerName = _currentUser.value.name,
            providerName = providerName,
            serviceName = serviceName,
            reason = reason,
            date = "Hoje"
        )
        _disputes.value = listOf(d) + _disputes.value
        _serviceRequests.value = _serviceRequests.value.map {
            if (it.id == requestId) it.copy(status = ServiceStatus.DISPUTA) else it
        }
    }

    fun resolveDispute(disputeId: String) {
        _disputes.value = _disputes.value.map {
            if (it.id == disputeId) it.copy(status = "Resolvido por Mediação Tudo em Um") else it
        }
    }

    fun withdrawFunds(amountKz: Long): Boolean {
        if (_providerAvailableBalanceKz.value >= amountKz) {
            _providerAvailableBalanceKz.value -= amountKz
            return true
        }
        return false
    }

    fun toggleProviderStatus(providerId: String) {
        _providers.value = _providers.value.map {
            if (it.id == providerId) {
                val newStatus = if (it.verificationStatus == VerificationStatus.VERIFIED)
                    VerificationStatus.REJECTED
                else
                    VerificationStatus.VERIFIED
                it.copy(verificationStatus = newStatus, isVerified = (newStatus == VerificationStatus.VERIFIED))
            } else it
        }
    }

    fun addCategory(name: String, iconEmoji: String, description: String, subcategories: List<String>) {
        val newCat = Category(
            id = "cat_${System.currentTimeMillis()}",
            name = name,
            iconEmoji = iconEmoji,
            description = description,
            subcategories = subcategories
        )
        _categories.value = _categories.value + newCat
    }

    fun addLocation(province: String, municipality: String, district: String, neighborhood: String) {
        val newLoc = LocationItem(
            id = "loc_${System.currentTimeMillis()}",
            province = province,
            municipality = municipality,
            district = district,
            neighborhood = neighborhood
        )
        _locations.value = _locations.value + newLoc
    }

    private fun seedInitialData() {
        _categories.value = listOf(
            Category("cat_casa", "Casa", "🏠", "Serviços domésticos e manutenção de residência", listOf("Limpeza profunda", "Pintura residencial", "Montagem de móveis", "Decoração", "Jardinagem")),
            Category("cat_construcao", "Construção", "🔨", "Construção civil e alvenaria em Luanda", listOf("Pedreiro", "Pintor", "Eletricista", "Canalizador", "Carpinteiro", "Serralheiro", "Gesseiro")),
            Category("cat_reparacao", "Reparação", "🔧", "Conserto de aparelhos, refrigeração e geradores", listOf("Eletrodomésticos", "Ar Condicionado", "Geradores", "Bombas de Água", "Frio Comercial")),
            Category("cat_auto", "Automóvel", "🚗", "Mecânica, eletricidade e socorro auto 24/7", listOf("Mecânico Auto", "Eletricista Auto", "Bate-Chapa", "Lavagem ao Domicílio", "Reboque")),
            Category("cat_tecnologia", "Tecnologia", "💻", "Informática, redes, antenas e segurança eletrónica", listOf("Reparação de PC/Portátil", "Redes & Wi-Fi", "Câmaras CCTV", "Antenas TV/ZAP", "Telemóveis")),
            Category("cat_limpeza", "Limpeza", "🧹", "Higienização profissional residencial e corporativa", listOf("Limpeza Doméstica", "Limpeza Pós-Obra", "Sofás e Tapetes", "Limpeza de Escritório")),
            Category("cat_beleza", "Beleza", "💇", "Cuidados pessoais e estética ao domicílio", listOf("Barbeiro", "Cabeleireira", "Manicure e Pedicure", "Maquilhagem", "Massagem")),
            Category("cat_transporte", "Transporte", "🚚", "Mudanças, fretes e transporte de carga em Angola", listOf("Mudanças Residenciais", "Fretes e Carretos", "Estafeta Expresso", "Camião de Carga"))
        )

        _locations.value = listOf(
            LocationItem("1", "Luanda", "Talatona", "Talatona", "Camama"),
            LocationItem("2", "Luanda", "Talatona", "Talatona", "Morro Bento"),
            LocationItem("3", "Luanda", "Talatona", "Talatona", "Talatona Centro"),
            LocationItem("4", "Luanda", "Talatona", "Talatona", "Benfica"),
            LocationItem("5", "Luanda", "Talatona", "Talatona", "Patriota"),
            LocationItem("6", "Luanda", "Belas", "Kilamba", "Centralidade do Kilamba"),
            LocationItem("7", "Luanda", "Belas", "Ramiros", "Ramiros"),
            LocationItem("8", "Luanda", "Luanda", "Maianga", "Maianga"),
            LocationItem("9", "Luanda", "Luanda", "Maianga", "Alvalade"),
            LocationItem("10", "Luanda", "Luanda", "Ingombota", "Mutamba"),
            LocationItem("11", "Luanda", "Luanda", "Ingombota", "Ilha de Luanda"),
            LocationItem("12", "Luanda", "Viana", "Zango", "Zango 1"),
            LocationItem("13", "Luanda", "Viana", "Zango", "Zango 3"),
            LocationItem("14", "Luanda", "Viana", "Viana Sede", "Vila de Viana"),
            LocationItem("15", "Luanda", "Cazenga", "Hoji-ya-Henda", "Cazenga Popular"),
            LocationItem("16", "Luanda", "Cacuaco", "Cacuaco", "Centralidade do Sequele")
        )

        _providers.value = listOf(
            ProviderProfile(
                id = "prov_joao",
                name = "João Manuel",
                phone = "+244 912 345 678",
                profession = "Eletricista",
                categoryId = "cat_construcao",
                rating = 4.9,
                reviewsCount = 126,
                isVerified = true,
                distanceKm = 2.1,
                startingPriceKz = 15000L,
                experienceYears = 8,
                availabilityText = "Hoje",
                isAvailableNow = true,
                bio = "Técnico eletricista credenciado pelo INEFOP com 8 anos de experiência em Luanda. Especialista em instalações residenciais, quadros elétricos trifásicos, geradores e iluminação LED.",
                services = listOf("Instalação elétrica", "Tomadas e interruptores", "Disjuntores e quadros", "Iluminação", "Manutenção elétrica preventiva"),
                servedNeighborhoods = listOf("Camama", "Talatona", "Kilamba", "Morro Bento", "Patriota"),
                completionRatePercent = 98,
                responseTimeMinutes = 5
            ),
            ProviderProfile(
                id = "prov_manuel",
                name = "Manuel António",
                phone = "+244 923 111 222",
                profession = "Eletricista & Canalizador",
                categoryId = "cat_construcao",
                rating = 4.8,
                reviewsCount = 94,
                isVerified = true,
                distanceKm = 3.4,
                startingPriceKz = 12000L,
                experienceYears = 5,
                availabilityText = "Amanhã",
                isAvailableNow = false,
                bio = "Profissional polivalente em eletricidade residencial e canalização de água pressurizada. Atendimento rápido e garantido.",
                services = listOf("Instalação de tomadas", "Reparação de curto-circuito", "Bombas de água", "Troca de fiação"),
                servedNeighborhoods = listOf("Camama", "Talatona", "Benfica", "Viana"),
                completionRatePercent = 96,
                responseTimeMinutes = 12
            ),
            ProviderProfile(
                id = "prov_pedro",
                name = "Pedro Domingos",
                phone = "+244 934 888 999",
                profession = "Mestre Eletricista",
                categoryId = "cat_construcao",
                rating = 4.8,
                reviewsCount = 110,
                isVerified = true,
                distanceKm = 3.0,
                startingPriceKz = 18000L,
                experienceYears = 10,
                availabilityText = "Hoje",
                isAvailableNow = true,
                bio = "Mais de uma década atuando em grandes condomínios do Talatona e Maianga. Diagnóstico computorizado de falhas elétricas e no-breaks.",
                services = listOf("Instalação de quadros gerais", "Sistemas de terra e para-raios", "Automação residencial", "Geradores industriais"),
                servedNeighborhoods = listOf("Talatona", "Camama", "Kilamba", "Maianga", "Alvalade"),
                completionRatePercent = 99,
                responseTimeMinutes = 8
            ),
            ProviderProfile(
                id = "prov_ana",
                name = "Ana Paula Silva",
                phone = "+244 945 667 788",
                profession = "Especialista em Limpeza",
                categoryId = "cat_limpeza",
                rating = 5.0,
                reviewsCount = 88,
                isVerified = true,
                distanceKm = 1.8,
                startingPriceKz = 20000L,
                experienceYears = 6,
                availabilityText = "Hoje",
                isAvailableNow = true,
                bio = "Equipa equipada com aspiradores industriais e produtos ecológicos. Higienização de sofás, tapetes e limpeza pós-obra em Luanda.",
                services = listOf("Limpeza profunda", "Higienização de sofás", "Limpeza pós-obra", "Limpeza de vidros altos"),
                servedNeighborhoods = listOf("Camama", "Talatona", "Morro Bento", "Alvalade", "Maianga"),
                completionRatePercent = 100,
                responseTimeMinutes = 4
            ),
            ProviderProfile(
                id = "prov_mateus",
                name = "Mateus Francisco",
                phone = "+244 911 234 567",
                profession = "Mecânico Auto & Socorro 24h",
                categoryId = "cat_auto",
                rating = 4.7,
                reviewsCount = 76,
                isVerified = true,
                distanceKm = 4.5,
                startingPriceKz = 14000L,
                experienceYears = 12,
                availabilityText = "Hoje",
                isAvailableNow = true,
                bio = "Oficina móvel pronta para atender chamados na Via Expressa, Estrada de Catete e interior de Luanda. Diagnóstico OBD e troca de alternadores.",
                services = listOf("Diagnóstico eletrónico", "Arranque e alternador", "Troca de correias", "Socorro na estrada"),
                servedNeighborhoods = listOf("Camama", "Viana", "Talatona", "Kilamba", "Cazenga"),
                completionRatePercent = 95,
                responseTimeMinutes = 10
            ),
            ProviderProfile(
                id = "prov_domingos",
                name = "Domingos Sebastião",
                phone = "+244 928 900 112",
                profession = "Chaveiro Especialista",
                categoryId = "cat_construcao",
                rating = 4.9,
                reviewsCount = 142,
                isVerified = true,
                distanceKm = 1.2,
                startingPriceKz = 10000L,
                experienceYears = 7,
                availabilityText = "Hoje",
                isAvailableNow = true,
                bio = "Abertura de emergência de portas residenciais, cofres e viaturas sem danificar a fechadura. Atendimento imediato.",
                services = listOf("Abertura de portas blindadas", "Cópia de chaves codificadas", "Troca de canhões", "Fechaduras digitais"),
                servedNeighborhoods = listOf("Camama", "Talatona", "Morro Bento", "Patriota", "Kilamba"),
                completionRatePercent = 99,
                responseTimeMinutes = 3
            )
        )

        // Seed initial reviews for João Manuel
        _reviews.value = listOf(
            Review(
                id = "rev_1",
                requestId = "req_initial",
                providerId = "prov_joao",
                customerName = "António Dias",
                overallRating = 5,
                qualityRating = 5,
                punctualityRating = 5,
                serviceRating = 5,
                priceRating = 5,
                comment = "Excelente profissional! Chegou no Camama em 20 minutos e resolveu o curto-circuito no quadro elétrico com muita segurança.",
                date = "Ontem"
            ),
            Review(
                id = "rev_2",
                requestId = "req_initial_2",
                providerId = "prov_joao",
                customerName = "Maria Luísa Costa",
                overallRating = 5,
                qualityRating = 5,
                punctualityRating = 4,
                serviceRating = 5,
                priceRating = 5,
                comment = "Muito educado e transparente com o preço dos disjuntores. Recomendo vivamente a todos no Talatona.",
                date = "Há 3 dias"
            )
        )

        // Seed sample request for demo
        val demoReq = ServiceRequest(
            id = "req_demo_01",
            customerId = "user_carlos",
            customerName = "Carlos Bento",
            customerPhone = "+244 923 456 789",
            categoryId = "cat_construcao",
            subcategory = "Instalação Elétrica",
            description = "Troca de disjuntores principais e instalação de 4 tomadas na sala.",
            province = "Luanda",
            municipality = "Talatona",
            neighborhood = "Camama",
            scheduledDate = "02/09/2026",
            scheduledTime = "14:00",
            urgency = ServiceUrgency.NORMAL,
            status = ServiceStatus.PROPOSTAS_RECEBIDAS
        )
        _serviceRequests.value = listOf(demoReq)

        _quotes.value = listOf(
            Quote(
                id = "quote_demo_1",
                requestId = demoReq.id,
                providerId = "prov_joao",
                providerName = "João Manuel",
                providerProfession = "Eletricista",
                providerRating = 4.9,
                providerDistanceKm = 2.1,
                providerExperienceYears = 8,
                availabilityText = "Chega em 30 min",
                priceKz = 55000L,
                etaText = "Hoje às 15:00",
                note = "Inclui mão de obra especializada e certificação de carga."
            ),
            Quote(
                id = "quote_demo_2",
                requestId = demoReq.id,
                providerId = "prov_manuel",
                providerName = "Manuel António",
                providerProfession = "Eletricista",
                providerRating = 4.7,
                providerDistanceKm = 3.4,
                providerExperienceYears = 5,
                availabilityText = "Disponível amanhã",
                priceKz = 48000L,
                etaText = "Amanhã às 09:00",
                note = "Disponibilidade matinal com desconto no pacote."
            ),
            Quote(
                id = "quote_demo_3",
                requestId = demoReq.id,
                providerId = "prov_pedro",
                providerName = "Pedro Domingos",
                providerProfession = "Mestre Eletricista",
                providerRating = 4.8,
                providerDistanceKm = 3.0,
                providerExperienceYears = 10,
                availabilityText = "Disponível hoje",
                priceKz = 60000L,
                etaText = "Hoje às 16:30",
                note = "Garantia total de 60 dias e revisão do quadro geral."
            )
        )

        _chatMessages.value = listOf(
            ChatMessage(
                id = "chat_1",
                requestId = demoReq.id,
                senderId = "user_carlos",
                senderName = "Carlos Bento",
                isFromCustomer = true,
                text = "Boa tarde. O senhor consegue vir hoje ao Camama?",
                type = MessageType.TEXT,
                timestamp = "14:20"
            ),
            ChatMessage(
                id = "chat_2",
                requestId = demoReq.id,
                senderId = "prov_joao",
                senderName = "João Manuel",
                isFromCustomer = false,
                text = "Sim, com certeza! Posso estar aí às 15h.",
                type = MessageType.TEXT,
                timestamp = "14:22"
            ),
            ChatMessage(
                id = "chat_3",
                requestId = demoReq.id,
                senderId = "user_carlos",
                senderName = "Carlos Bento",
                isFromCustomer = true,
                text = "Quanto vai custar o serviço?",
                type = MessageType.TEXT,
                timestamp = "14:23"
            ),
            ChatMessage(
                id = "chat_4",
                requestId = demoReq.id,
                senderId = "prov_joao",
                senderName = "João Manuel",
                isFromCustomer = false,
                text = "Proposta formal de 55.000 Kz incluindo mão de obra e deslocação.",
                type = MessageType.QUOTE,
                quotePriceKz = 55000L,
                timestamp = "14:25"
            )
        )
    }
}
