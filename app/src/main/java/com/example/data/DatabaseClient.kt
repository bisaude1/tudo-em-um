package com.example.data

import android.util.Log
import com.example.model.*
import com.google.android.gms.tasks.Task
import com.google.firebase.firestore.DocumentSnapshot
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import com.google.firebase.firestore.SetOptions
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.suspendCancellableCoroutine
import kotlin.coroutines.resume
import kotlin.coroutines.resumeWithException

/**
 * Cliente central do Firebase Firestore para o superapp "Tudo em Um" (Angola).
 * Gerencia as coleções principais:
 *  - "service_requests": pedidos de clientes, cotações, status de execução e pagamentos em Kwanzas.
 *  - "users": perfis de utilizadores (clientes e administradores) com dados de província/município/bairro.
 *  - "providers": perfis de prestadores de serviço com certificações INEFOP, avaliações e raio de cobertura.
 */
class DatabaseClient(
    private val firestore: FirebaseFirestore? = try {
        FirebaseFirestore.getInstance()
    } catch (e: Exception) {
        Log.w("DatabaseClient", "FirebaseFirestore instance not available yet: ${e.message}")
        null
    }
) {

    companion object {
        const val TAG = "TudoEmUm_DatabaseClient"

        // Nomes das coleções principais no Firestore
        const val COLLECTION_SERVICE_REQUESTS = "service_requests"
        const val COLLECTION_USERS = "users"
        const val COLLECTION_PROVIDERS = "providers"
        const val COLLECTION_QUOTES = "quotes"
        const val COLLECTION_REVIEWS = "reviews"
        const val COLLECTION_DISPUTES = "disputes"
        const val COLLECTION_CATEGORIES = "categories"

        @Volatile
        private var INSTANCE: DatabaseClient? = null

        fun getInstance(): DatabaseClient {
            return INSTANCE ?: synchronized(this) {
                INSTANCE ?: DatabaseClient().also { INSTANCE = it }
            }
        }
    }

    // =========================================================================
    // COLEÇÃO: service_requests
    // =========================================================================

    /**
     * Cria ou atualiza uma solicitação de serviço na coleção `service_requests`.
     */
    suspend fun saveServiceRequest(request: ServiceRequest): Result<String> {
        val db = firestore ?: return Result.failure(IllegalStateException("Firestore não configurado"))
        return try {
            val docRef = if (request.id.isBlank()) {
                db.collection(COLLECTION_SERVICE_REQUESTS).document()
            } else {
                db.collection(COLLECTION_SERVICE_REQUESTS).document(request.id)
            }
            val requestWithId = request.copy(id = docRef.id)
            val data = serviceRequestToMap(requestWithId)

            docRef.set(data, SetOptions.merge()).awaitTask()
            Log.d(TAG, "Solicitação salva com sucesso: ${docRef.id}")
            Result.success(docRef.id)
        } catch (e: Exception) {
            Log.e(TAG, "Erro ao salvar service_request", e)
            Result.failure(e)
        }
    }

    /**
     * Obtém uma solicitação pelo ID.
     */
    suspend fun getServiceRequest(requestId: String): Result<ServiceRequest?> {
        val db = firestore ?: return Result.failure(IllegalStateException("Firestore não configurado"))
        return try {
            val doc = db.collection(COLLECTION_SERVICE_REQUESTS).document(requestId).get().awaitTask()
            val req = if (doc.exists()) mapToServiceRequest(doc) else null
            Result.success(req)
        } catch (e: Exception) {
            Log.e(TAG, "Erro ao obter service_request $requestId", e)
            Result.failure(e)
        }
    }

    /**
     * Observa em tempo real a lista de solicitações de um utilizador específico.
     */
    fun observeUserServiceRequests(customerId: String): Flow<List<ServiceRequest>> = callbackFlow {
        val db = firestore
        if (db == null) {
            trySend(emptyList())
            close()
            return@callbackFlow
        }

        val registration = db.collection(COLLECTION_SERVICE_REQUESTS)
            .whereEqualTo("customerId", customerId)
            .addSnapshotListener { snapshot, error ->
                if (error != null) {
                    Log.e(TAG, "Erro ao observar service_requests", error)
                    return@addSnapshotListener
                }
                if (snapshot != null) {
                    val requests = snapshot.documents.mapNotNull { mapToServiceRequest(it) }
                    trySend(requests)
                }
            }

        awaitClose { registration.remove() }
    }

    /**
     * Observa todas as solicitações disponíveis para profissionais na região (ex.: Luanda/Camama).
     */
    fun observeAvailableRequestsForProviders(category: String? = null): Flow<List<ServiceRequest>> = callbackFlow {
        val db = firestore
        if (db == null) {
            trySend(emptyList())
            close()
            return@callbackFlow
        }

        var query: Query = db.collection(COLLECTION_SERVICE_REQUESTS)
            .whereEqualTo("status", ServiceStatus.PROPOSTAS_RECEBIDAS.name)

        if (!category.isNullOrBlank()) {
            query = query.whereEqualTo("categoryId", category)
        }

        val registration = query.addSnapshotListener { snapshot, error ->
            if (error != null) {
                Log.e(TAG, "Erro ao escutar solicitações de profissionais", error)
                return@addSnapshotListener
            }
            if (snapshot != null) {
                val list = snapshot.documents.mapNotNull { mapToServiceRequest(it) }
                trySend(list)
            }
        }

        awaitClose { registration.remove() }
    }

    /**
     * Atualiza o estado de uma solicitação de serviço (ex.: PAGO, EM_ANDAMENTO, CONCLUIDO).
     */
    suspend fun updateServiceStatus(requestId: String, newStatus: ServiceStatus): Result<Unit> {
        val db = firestore ?: return Result.failure(IllegalStateException("Firestore não configurado"))
        return try {
            db.collection(COLLECTION_SERVICE_REQUESTS).document(requestId)
                .update(
                    mapOf(
                        "status" to newStatus.name,
                        "updatedAt" to System.currentTimeMillis()
                    )
                )
                .awaitTask()
            Result.success(Unit)
        } catch (e: Exception) {
            Log.e(TAG, "Erro ao atualizar status de $requestId", e)
            Result.failure(e)
        }
    }

    // =========================================================================
    // COLEÇÃO: users (Perfis de Clientes e Administradores)
    // =========================================================================

    /**
     * Salva ou atualiza os dados cadastrais e localização do perfil de utilizador.
     */
    suspend fun saveUserProfile(user: User): Result<Unit> {
        val db = firestore ?: return Result.failure(IllegalStateException("Firestore não configurado"))
        return try {
            val data = userToMap(user)
            db.collection(COLLECTION_USERS).document(user.id)
                .set(data, SetOptions.merge())
                .awaitTask()
            Log.d(TAG, "Perfil de utilizador salvo: ${user.id}")
            Result.success(Unit)
        } catch (e: Exception) {
            Log.e(TAG, "Erro ao salvar utilizador ${user.id}", e)
            Result.failure(e)
        }
    }

    /**
     * Obtém os dados de perfil de um utilizador pelo seu ID.
     */
    suspend fun getUserProfile(userId: String): Result<User?> {
        val db = firestore ?: return Result.failure(IllegalStateException("Firestore não configurado"))
        return try {
            val doc = db.collection(COLLECTION_USERS).document(userId).get().awaitTask()
            val user = if (doc.exists()) mapToUser(doc) else null
            Result.success(user)
        } catch (e: Exception) {
            Log.e(TAG, "Erro ao obter utilizador $userId", e)
            Result.failure(e)
        }
    }

    /**
     * Observa em tempo real as mudanças no perfil do utilizador logado.
     */
    fun observeUserProfile(userId: String): Flow<User?> = callbackFlow {
        val db = firestore
        if (db == null) {
            trySend(null)
            close()
            return@callbackFlow
        }

        val registration = db.collection(COLLECTION_USERS).document(userId)
            .addSnapshotListener { snapshot, error ->
                if (error != null) {
                    Log.e(TAG, "Erro ao escutar user $userId", error)
                    return@addSnapshotListener
                }
                val user = if (snapshot != null && snapshot.exists()) mapToUser(snapshot) else null
                trySend(user)
            }

        awaitClose { registration.remove() }
    }

    // =========================================================================
    // COLEÇÃO: providers (Perfis de Prestadores de Serviço)
    // =========================================================================

    /**
     * Salva ou atualiza os dados do perfil profissional.
     */
    suspend fun saveProviderProfile(provider: ProviderProfile): Result<Unit> {
        val db = firestore ?: return Result.failure(IllegalStateException("Firestore não configurado"))
        return try {
            val data = providerToMap(provider)
            db.collection(COLLECTION_PROVIDERS).document(provider.id)
                .set(data, SetOptions.merge())
                .awaitTask()
            Log.d(TAG, "Perfil de prestador salvo: ${provider.id}")
            Result.success(Unit)
        } catch (e: Exception) {
            Log.e(TAG, "Erro ao salvar prestador ${provider.id}", e)
            Result.failure(e)
        }
    }

    /**
     * Obtém a lista de prestadores cadastrados com suporte a filtro de categoria.
     */
    suspend fun getProviders(categoryId: String? = null): Result<List<ProviderProfile>> {
        val db = firestore ?: return Result.failure(IllegalStateException("Firestore não configurado"))
        return try {
            var query: Query = db.collection(COLLECTION_PROVIDERS)
            if (!categoryId.isNullOrBlank()) {
                query = query.whereEqualTo("categoryId", categoryId)
            }
            val snapshot = query.get().awaitTask()
            val list = snapshot.documents.mapNotNull { mapToProvider(it) }
            Result.success(list)
        } catch (e: Exception) {
            Log.e(TAG, "Erro ao obter prestadores", e)
            Result.failure(e)
        }
    }

    /**
     * Observa todos os prestadores em tempo real para o catálogo e mapas.
     */
    fun observeAllProviders(): Flow<List<ProviderProfile>> = callbackFlow {
        val db = firestore
        if (db == null) {
            trySend(emptyList())
            close()
            return@callbackFlow
        }

        val registration = db.collection(COLLECTION_PROVIDERS)
            .addSnapshotListener { snapshot, error ->
                if (error != null) {
                    Log.e(TAG, "Erro ao observar prestadores", error)
                    return@addSnapshotListener
                }
                if (snapshot != null) {
                    val list = snapshot.documents.mapNotNull { mapToProvider(it) }
                    trySend(list)
                }
            }

        awaitClose { registration.remove() }
    }

    /**
     * Atualiza o estado de verificação (BI / INEFOP) de um profissional (Ação Administrativa).
     */
    suspend fun setProviderVerification(providerId: String, isVerified: Boolean): Result<Unit> {
        val db = firestore ?: return Result.failure(IllegalStateException("Firestore não configurado"))
        return try {
            db.collection(COLLECTION_PROVIDERS).document(providerId)
                .update(
                    mapOf(
                        "isVerified" to isVerified,
                        "verificationStatus" to if (isVerified) VerificationStatus.VERIFIED.name else VerificationStatus.REJECTED.name
                    )
                )
                .awaitTask()
            Result.success(Unit)
        } catch (e: Exception) {
            Log.e(TAG, "Erro ao atualizar verificação do prestador $providerId", e)
            Result.failure(e)
        }
    }

    // =========================================================================
    // MAPPER HELPERS (Serialização e Deserialização)
    // =========================================================================

    private fun serviceRequestToMap(req: ServiceRequest): Map<String, Any?> = mapOf(
        "id" to req.id,
        "customerId" to req.customerId,
        "customerName" to req.customerName,
        "customerPhone" to req.customerPhone,
        "categoryId" to req.categoryId,
        "subcategory" to req.subcategory,
        "description" to req.description,
        "province" to req.province,
        "municipality" to req.municipality,
        "neighborhood" to req.neighborhood,
        "scheduledDate" to req.scheduledDate,
        "scheduledTime" to req.scheduledTime,
        "urgency" to req.urgency.name,
        "status" to req.status.name,
        "selectedProviderId" to req.selectedProviderId,
        "agreedPriceKz" to req.agreedPriceKz,
        "platformFeeKz" to req.platformFeeKz,
        "createdAtMillis" to req.createdAtMillis
    )

    private fun mapToServiceRequest(doc: DocumentSnapshot): ServiceRequest? {
        return try {
            ServiceRequest(
                id = doc.id,
                customerId = doc.getString("customerId") ?: "",
                customerName = doc.getString("customerName") ?: "",
                customerPhone = doc.getString("customerPhone") ?: "",
                categoryId = doc.getString("categoryId") ?: "",
                subcategory = doc.getString("subcategory") ?: "",
                description = doc.getString("description") ?: "",
                province = doc.getString("province") ?: "Luanda",
                municipality = doc.getString("municipality") ?: "Talatona",
                neighborhood = doc.getString("neighborhood") ?: "Camama",
                scheduledDate = doc.getString("scheduledDate") ?: "",
                scheduledTime = doc.getString("scheduledTime") ?: "",
                urgency = try {
                    ServiceUrgency.valueOf(doc.getString("urgency") ?: ServiceUrgency.NORMAL.name)
                } catch (_: Exception) {
                    ServiceUrgency.NORMAL
                },
                status = try {
                    ServiceStatus.valueOf(doc.getString("status") ?: ServiceStatus.PROPOSTAS_RECEBIDAS.name)
                } catch (_: Exception) {
                    ServiceStatus.PROPOSTAS_RECEBIDAS
                },
                selectedProviderId = doc.getString("selectedProviderId"),
                agreedPriceKz = doc.getLong("agreedPriceKz"),
                platformFeeKz = doc.getLong("platformFeeKz"),
                createdAtMillis = doc.getLong("createdAtMillis") ?: System.currentTimeMillis()
            )
        } catch (e: Exception) {
            Log.w(TAG, "Falha ao converter doc ${doc.id} para ServiceRequest", e)
            null
        }
    }

    private fun userToMap(user: User): Map<String, Any?> = mapOf(
        "id" to user.id,
        "name" to user.name,
        "phone" to user.phone,
        "email" to user.email,
        "role" to user.role.name,
        "province" to user.province,
        "municipality" to user.municipality,
        "district" to user.district,
        "neighborhood" to user.neighborhood,
        "avatarUrl" to user.avatarUrl
    )

    private fun mapToUser(doc: DocumentSnapshot): User? {
        return try {
            User(
                id = doc.id,
                name = doc.getString("name") ?: "",
                phone = doc.getString("phone") ?: "",
                email = doc.getString("email") ?: "",
                role = try {
                    UserRole.valueOf(doc.getString("role") ?: UserRole.CLIENT.name)
                } catch (_: Exception) {
                    UserRole.CLIENT
                },
                province = doc.getString("province") ?: "Luanda",
                municipality = doc.getString("municipality") ?: "Talatona",
                district = doc.getString("district") ?: "Talatona",
                neighborhood = doc.getString("neighborhood") ?: "Camama",
                avatarUrl = doc.getString("avatarUrl") ?: ""
            )
        } catch (e: Exception) {
            Log.w(TAG, "Falha ao converter doc ${doc.id} para User", e)
            null
        }
    }

    private fun providerToMap(prov: ProviderProfile): Map<String, Any?> = mapOf(
        "id" to prov.id,
        "name" to prov.name,
        "phone" to prov.phone,
        "profession" to prov.profession,
        "categoryId" to prov.categoryId,
        "rating" to prov.rating,
        "reviewsCount" to prov.reviewsCount,
        "isVerified" to prov.isVerified,
        "distanceKm" to prov.distanceKm,
        "startingPriceKz" to prov.startingPriceKz,
        "experienceYears" to prov.experienceYears,
        "availabilityText" to prov.availabilityText,
        "isAvailableNow" to prov.isAvailableNow,
        "bio" to prov.bio,
        "services" to prov.services,
        "servedNeighborhoods" to prov.servedNeighborhoods,
        "completionRatePercent" to prov.completionRatePercent,
        "responseTimeMinutes" to prov.responseTimeMinutes,
        "verificationStatus" to prov.verificationStatus.name
    )

    @Suppress("UNCHECKED_CAST")
    private fun mapToProvider(doc: DocumentSnapshot): ProviderProfile? {
        return try {
            ProviderProfile(
                id = doc.id,
                name = doc.getString("name") ?: "",
                phone = doc.getString("phone") ?: "",
                profession = doc.getString("profession") ?: "",
                categoryId = doc.getString("categoryId") ?: "",
                rating = doc.getDouble("rating") ?: 5.0,
                reviewsCount = doc.getLong("reviewsCount")?.toInt() ?: 0,
                isVerified = doc.getBoolean("isVerified") ?: false,
                distanceKm = doc.getDouble("distanceKm") ?: 0.0,
                startingPriceKz = doc.getLong("startingPriceKz") ?: 0L,
                experienceYears = doc.getLong("experienceYears")?.toInt() ?: 1,
                availabilityText = doc.getString("availabilityText") ?: "Hoje",
                isAvailableNow = doc.getBoolean("isAvailableNow") ?: true,
                bio = doc.getString("bio") ?: "",
                services = (doc.get("services") as? List<String>) ?: emptyList(),
                servedNeighborhoods = (doc.get("servedNeighborhoods") as? List<String>) ?: emptyList(),
                completionRatePercent = doc.getLong("completionRatePercent")?.toInt() ?: 95,
                responseTimeMinutes = doc.getLong("responseTimeMinutes")?.toInt() ?: 10,
                verificationStatus = try {
                    VerificationStatus.valueOf(doc.getString("verificationStatus") ?: VerificationStatus.VERIFIED.name)
                } catch (_: Exception) {
                    VerificationStatus.VERIFIED
                }
            )
        } catch (e: Exception) {
            Log.w(TAG, "Falha ao converter doc ${doc.id} para ProviderProfile", e)
            null
        }
    }
}

/**
 * Extensão utilitária para converter Tasks do Google Play Services em Coroutines suspensas
 * sem depender de bibliotecas externas adicionais.
 */
private suspend fun <T> Task<T>.awaitTask(): T = suspendCancellableCoroutine { cont ->
    addOnSuccessListener { result ->
        cont.resume(result)
    }
    addOnFailureListener { exception ->
        cont.resumeWithException(exception)
    }
    addOnCanceledListener {
        cont.cancel()
    }
}
