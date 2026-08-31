package com.example.model

enum class UserRole {
    CLIENT,
    PROVIDER,
    ADMIN
}

enum class VerificationStatus {
    PENDING_ANALYSIS,
    VERIFIED,
    REJECTED
}

enum class ServiceUrgency {
    NORMAL,
    URGENT
}

enum class ServiceStatus(val label: String, val stepIndex: Int) {
    PENDENTE("Pendente", 0),
    PROPOSTAS_RECEBIDAS("Propostas Recebidas", 1),
    PROFISSIONAL_ESCOLHIDO("Profissional Escolhido", 2),
    PAGAMENTO_PENDENTE("Pagamento Pendente", 3),
    PAGO("Pago", 4),
    AGENDADO("Agendado", 5),
    EM_DESLOCAMENTO("Em Deslocamento", 6),
    EM_ANDAMENTO("Em Andamento", 7),
    CONCLUIDO("Concluído", 8),
    CONFIRMADO("Confirmado", 9),
    AVALIADO("Avaliado", 10),
    DISPUTA("Em Disputa", -1)
}

enum class PaymentMethod(val title: String) {
    MULTICAIXA_EXPRESS("Multicaixa Express"),
    CARD("Cartão Bancário"),
    TRANSFER("Transferência Bancária (IBAN)"),
    CASH_ON_DELIVERY("Pagamento Direto Autorizado")
}

enum class MessageType {
    TEXT,
    QUOTE,
    IMAGE,
    AUDIO,
    LOCATION
}
