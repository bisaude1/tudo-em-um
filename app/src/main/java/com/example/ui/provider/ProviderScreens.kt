package com.example.ui.provider

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.outlined.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.model.ServiceStatus
import com.example.model.VerificationStatus
import com.example.ui.components.StarRatingDisplay
import com.example.ui.components.VerifiedBadge
import com.example.ui.theme.*
import com.example.viewmodel.MarketplaceViewModel
import com.example.viewmodel.ScreenDestination

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ProviderDashboardScreen(
    viewModel: MarketplaceViewModel,
    modifier: Modifier = Modifier
) {
    val currentUser by viewModel.currentUser.collectAsState()
    val serviceRequests by viewModel.serviceRequests.collectAsState()
    val availableBalance by viewModel.providerAvailableBalanceKz.collectAsState()
    val pendingBalance by viewModel.providerPendingBalanceKz.collectAsState()
    val totalReceived by viewModel.providerTotalReceivedKz.collectAsState()

    var activeTab by remember { mutableStateOf(0) } // 0: Início, 1: Pedidos, 2: Agenda, 3: Carteira, 4: Perfil

    Scaffold(
        bottomBar = {
            NavigationBar(
                containerColor = MaterialTheme.colorScheme.surface,
                tonalElevation = 6.dp
            ) {
                NavigationBarItem(
                    selected = activeTab == 0,
                    onClick = { activeTab = 0 },
                    icon = { Icon(Icons.Filled.Dashboard, contentDescription = "Início") },
                    label = { Text("Início", fontSize = 10.sp) }
                )
                NavigationBarItem(
                    selected = activeTab == 1,
                    onClick = { activeTab = 1 },
                    icon = {
                        BadgedBox(badge = { Badge(containerColor = AngolaRed) { Text("8") } }) {
                            Icon(Icons.Filled.Inbox, contentDescription = "Pedidos")
                        }
                    },
                    label = { Text("Pedidos", fontSize = 10.sp) }
                )
                NavigationBarItem(
                    selected = activeTab == 2,
                    onClick = { activeTab = 2 },
                    icon = { Icon(Icons.Filled.CalendarMonth, contentDescription = "Agenda") },
                    label = { Text("Agenda", fontSize = 10.sp) }
                )
                NavigationBarItem(
                    selected = activeTab == 3,
                    onClick = { activeTab = 3 },
                    icon = { Icon(Icons.Filled.AccountBalanceWallet, contentDescription = "Carteira") },
                    label = { Text("Carteira", fontSize = 10.sp) }
                )
                NavigationBarItem(
                    selected = activeTab == 4,
                    onClick = { activeTab = 4 },
                    icon = { Icon(Icons.Filled.Person, contentDescription = "Perfil") },
                    label = { Text("Perfil", fontSize = 10.sp) }
                )
            }
        }
    ) { innerPadding ->
        when (activeTab) {
            0 -> ProviderHomeContent(
                availableBalance = availableBalance,
                onNavigateTab = { activeTab = it },
                modifier = modifier.padding(innerPadding)
            )
            1 -> ProviderRequestsTab(
                viewModel = viewModel,
                modifier = modifier.padding(innerPadding)
            )
            2 -> ProviderAgendaTab(
                modifier = modifier.padding(innerPadding)
            )
            3 -> ProviderWalletTab(
                availableBalance = availableBalance,
                pendingBalance = pendingBalance,
                totalReceived = totalReceived,
                onWithdraw = { amount -> viewModel.withdrawProviderFunds(amount) },
                modifier = modifier.padding(innerPadding)
            )
            4 -> ProviderProfileTab(
                viewModel = viewModel,
                modifier = modifier.padding(innerPadding)
            )
        }
    }
}

@Composable
fun ProviderHomeContent(
    availableBalance: Long,
    onNavigateTab: (Int) -> Unit,
    modifier: Modifier = Modifier
) {
    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .testTag("provider_home_content"),
        contentPadding = PaddingValues(16.dp)
    ) {
        // Welcome Card matching Section 19: Bom dia, João 👋
        item {
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(18.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column {
                            Text("TUDO EM UM PROFISSIONAL", fontSize = 11.sp, fontWeight = FontWeight.Black, color = AngolaRed)
                            Text("Bom dia, João 👋", fontSize = 20.sp, fontWeight = FontWeight.Bold)
                            Text("Eletricista Certificado • Camama, Luanda", fontSize = 12.sp, color = Color.Gray)
                        }
                        VerifiedBadge()
                    }

                    Spacer(modifier = Modifier.height(14.dp))
                    HorizontalDivider()
                    Spacer(modifier = Modifier.height(14.dp))

                    // 4 Stat Metric Cards (Section 19: Saldo, Novos pedidos, Hoje, Avaliação)
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        MetricBox(
                            title = "💰 Saldo",
                            value = "${String.format("%,d", availableBalance).replace(",", ".")} Kz",
                            modifier = Modifier.weight(1.3f)
                        )
                        MetricBox(
                            title = "📥 Novos Pedidos",
                            value = "8",
                            modifier = Modifier.weight(1f)
                        )
                    }

                    Spacer(modifier = Modifier.height(8.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        MetricBox(
                            title = "📅 Hoje",
                            value = "3 serviços",
                            modifier = Modifier.weight(1f)
                        )
                        MetricBox(
                            title = "⭐ Avaliação",
                            value = "4.9 (126)",
                            modifier = Modifier.weight(1f)
                        )
                    }
                }
            }
        }

        // Quick Navigation Buttons matching Section 19
        item {
            Spacer(modifier = Modifier.height(18.dp))
            Text("Gestão Operacional", fontSize = 14.sp, fontWeight = FontWeight.Bold)
            Spacer(modifier = Modifier.height(8.dp))

            val buttons = listOf(
                Triple("📥 Novos Pedidos Próximos", "8 clientes aguardam orçamento no Camama/Talatona", 1),
                Triple("📅 Minha Agenda", "3 serviços agendados para hoje", 2),
                Triple("💰 Carteira & Levantamentos", "Saldo disponível para levantamento Multicaixa", 3),
                Triple("👤 Perfil & Documentos INEFOP", "Estado de verificação e catálogo de serviços", 4)
            )

            buttons.forEach { (title, subtitle, tabIndex) ->
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 4.dp)
                        .clickable { onNavigateTab(tabIndex) },
                    shape = RoundedCornerShape(12.dp),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(14.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column {
                            Text(title, fontWeight = FontWeight.Bold, fontSize = 14.sp)
                            Text(subtitle, fontSize = 11.sp, color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f))
                        }
                        Icon(Icons.Filled.ChevronRight, contentDescription = null, tint = AngolaRed)
                    }
                }
            }
        }
    }
}

@Composable
private fun MetricBox(title: String, value: String, modifier: Modifier = Modifier) {
    Card(
        modifier = modifier,
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant)
    ) {
        Column(modifier = Modifier.padding(10.dp)) {
            Text(title, fontSize = 11.sp, color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f))
            Spacer(modifier = Modifier.height(2.dp))
            Text(value, fontSize = 14.sp, fontWeight = FontWeight.Black, color = MaterialTheme.colorScheme.onSurface)
        }
    }
}

@Composable
fun ProviderRequestsTab(
    viewModel: MarketplaceViewModel,
    modifier: Modifier = Modifier
) {
    val serviceRequests by viewModel.serviceRequests.collectAsState()
    var quoteSentSuccess by remember { mutableStateOf<String?>(null) }

    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .testTag("provider_requests_tab"),
        contentPadding = PaddingValues(16.dp)
    ) {
        item {
            Text("Oportunidades & Novos Pedidos em Luanda", fontSize = 16.sp, fontWeight = FontWeight.Bold)
            Text("Clientes próximos solicitando orçamentos para a sua especialidade", fontSize = 11.sp, color = Color.Gray)
            Spacer(modifier = Modifier.height(14.dp))
        }

        items(serviceRequests) { req ->
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 6.dp),
                shape = RoundedCornerShape(14.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
            ) {
                Column(modifier = Modifier.padding(14.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text(req.subcategory, fontWeight = FontWeight.Bold, fontSize = 15.sp)
                        Text(
                            text = if (req.urgency == com.example.model.ServiceUrgency.URGENT) "🚨 URGENTE" else "Normal",
                            fontSize = 10.sp,
                            fontWeight = FontWeight.Bold,
                            color = if (req.urgency == com.example.model.ServiceUrgency.URGENT) EmergencyRed else Color.Gray
                        )
                    }
                    Spacer(modifier = Modifier.height(4.dp))
                    Text("Cliente: ${req.customerName} • 📍 ${req.neighborhood}, ${req.municipality}", fontSize = 12.sp, color = AngolaRed)
                    Spacer(modifier = Modifier.height(4.dp))
                    Text("\"${req.description}\"", fontSize = 12.sp, color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.85f))
                    Spacer(modifier = Modifier.height(8.dp))
                    Text("📅 Agendado para: ${req.scheduledDate} às ${req.scheduledTime}", fontSize = 11.sp, color = Color.Gray)

                    Spacer(modifier = Modifier.height(12.dp))
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Button(
                            onClick = {
                                quoteSentSuccess = req.id
                            },
                            colors = ButtonDefaults.buttonColors(containerColor = AngolaRed),
                            shape = RoundedCornerShape(8.dp),
                            modifier = Modifier.weight(1f)
                        ) {
                            Text(if (quoteSentSuccess == req.id) "Proposta Enviada ✓" else "Enviar Orçamento (55.000 Kz)", fontSize = 11.sp, fontWeight = FontWeight.Bold)
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun ProviderAgendaTab(modifier: Modifier = Modifier) {
    // Section 22: Minha Agenda (SEG 31, TER 01, QUA 02)
    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .testTag("provider_agenda_tab"),
        contentPadding = PaddingValues(16.dp)
    ) {
        item {
            Text("MINHA AGENDA DE TRABALHO", fontSize = 16.sp, fontWeight = FontWeight.Black, color = AngolaRed)
            Text("Compromissos e horários agendados com clientes", fontSize = 11.sp, color = Color.Gray)
            Spacer(modifier = Modifier.height(14.dp))
        }

        val days = listOf(
            Triple("SEG 31", "2 serviços", listOf("08:00 — Revisão Elétrica Residencial (Camama)", "14:30 — Instalação de Tomadas (Talatona)")),
            Triple("TER 01", "3 serviços", listOf("08:00 — Troca de Disjuntores (Kilamba)", "10:30 — Eletricidade e Quadro (Morro Bento)", "15:00 — Instalação de Foco LED (Patriota)")),
            Triple("QUA 02", "4 serviços", listOf("08:00 — Limpeza e Manutenção de Quadro (Maianga)", "10:30 — Eletricidade Geral (Alvalade)", "15:00 — Instalação Elétrica Completa (Camama)", "18:00 — Diagnóstico de Iluminação (Benfica)"))
        )

        days.forEach { (day, count, items) ->
            item {
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 6.dp),
                    shape = RoundedCornerShape(14.dp),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
                ) {
                    Column(modifier = Modifier.padding(14.dp)) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Text(day, fontWeight = FontWeight.Black, fontSize = 15.sp, color = AngolaRed)
                            Text(count, fontSize = 12.sp, fontWeight = FontWeight.Bold, color = VerifiedGreen)
                        }
                        Spacer(modifier = Modifier.height(8.dp))
                        items.forEach { task ->
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(vertical = 3.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Box(modifier = Modifier.size(6.dp).clip(CircleShape).background(AngolaGold))
                                Spacer(modifier = Modifier.width(8.dp))
                                Text(task, fontSize = 12.sp)
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun ProviderWalletTab(
    availableBalance: Long,
    pendingBalance: Long,
    totalReceived: Long,
    onWithdraw: (Long) -> Boolean,
    modifier: Modifier = Modifier
) {
    var showWithdrawDialog by remember { mutableStateOf(false) }
    var withdrawSuccess by remember { mutableStateOf(false) }

    if (showWithdrawDialog) {
        AlertDialog(
            onDismissRequest = { showWithdrawDialog = false },
            title = { Text("Solicitar Levantamento Bancário", fontWeight = FontWeight.Bold) },
            text = {
                Column {
                    Text("Transferência para a sua conta bancária angolana (BFA / BAI / BIC / Express):", fontSize = 12.sp)
                    Spacer(modifier = Modifier.height(10.dp))
                    Text("Valor a transferir: ${String.format("%,d", availableBalance).replace(",", ".")} Kz", fontWeight = FontWeight.Bold)
                    Text("IBAN: AO06.0040.0000.1234.5678.9012.3", fontSize = 11.sp, color = Color.Gray)
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        onWithdraw(availableBalance)
                        withdrawSuccess = true
                        showWithdrawDialog = false
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = AngolaRed)
                ) {
                    Text("CONFIRMAR LEVANTAMENTO")
                }
            },
            dismissButton = {
                TextButton(onClick = { showWithdrawDialog = false }) { Text("Cancelar") }
            }
        )
    }

    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .testTag("provider_wallet_tab"),
        contentPadding = PaddingValues(16.dp)
    ) {
        // Section 23: Minha Carteira
        item {
            Text("MINHA CARTEIRA", fontSize = 16.sp, fontWeight = FontWeight.Black, color = AngolaRed)
            Text("Gestão de recebimentos, saldo e transferências", fontSize = 11.sp, color = Color.Gray)
            Spacer(modifier = Modifier.height(14.dp))
        }

        item {
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(18.dp),
                colors = CardDefaults.cardColors(containerColor = Color(0xFF0F172A))
            ) {
                Column(modifier = Modifier.padding(18.dp)) {
                    Text("Saldo Disponível", fontSize = 12.sp, color = Color.LightGray)
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = "${String.format("%,d", availableBalance).replace(",", ".")} Kz",
                        fontSize = 26.sp,
                        fontWeight = FontWeight.Black,
                        color = AngolaGold
                    )

                    Spacer(modifier = Modifier.height(14.dp))
                    HorizontalDivider(color = Color.DarkGray)
                    Spacer(modifier = Modifier.height(14.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Column {
                            Text("A receber (Garantia)", fontSize = 11.sp, color = Color.LightGray)
                            Text("${String.format("%,d", pendingBalance).replace(",", ".")} Kz", fontSize = 14.sp, fontWeight = FontWeight.Bold, color = Color.White)
                        }
                        Column(horizontalAlignment = Alignment.End) {
                            Text("Total recebido histórico", fontSize = 11.sp, color = Color.LightGray)
                            Text("${String.format("%,d", totalReceived).replace(",", ".")} Kz", fontSize = 14.sp, fontWeight = FontWeight.Bold, color = VerifiedGreen)
                        }
                    }

                    Spacer(modifier = Modifier.height(16.dp))
                    Button(
                        onClick = { showWithdrawDialog = true },
                        modifier = Modifier.fillMaxWidth().height(46.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = AngolaRed),
                        shape = RoundedCornerShape(10.dp)
                    ) {
                        Text("LEVANTAR FUNDOS", fontWeight = FontWeight.Bold)
                    }
                }
            }
        }

        item {
            Spacer(modifier = Modifier.height(18.dp))
            Text("Histórico de Transações", fontSize = 14.sp, fontWeight = FontWeight.Bold)
            Spacer(modifier = Modifier.height(8.dp))

            val transactions = listOf(
                Triple("Serviço #8420 — Instalação Elétrica (Carlos)", "+49.500 Kz", "Ontem (Comissão 10% descontada)"),
                Triple("Serviço #8391 — Troca de Disjuntores (António)", "+31.500 Kz", "28/08/2026"),
                Triple("Levantamento Bancário BFA", "-120.000 Kz", "25/08/2026"),
                Triple("Serviço #8310 — Quadro Geral (Maria)", "+67.500 Kz", "22/08/2026")
            )

            transactions.forEach { (title, amount, date) ->
                Card(
                    modifier = Modifier.fillMaxWidth().padding(vertical = 4.dp),
                    shape = RoundedCornerShape(10.dp),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth().padding(12.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column {
                            Text(title, fontWeight = FontWeight.SemiBold, fontSize = 12.sp)
                            Text(date, fontSize = 10.sp, color = Color.Gray)
                        }
                        Text(
                            amount,
                            fontWeight = FontWeight.Bold,
                            fontSize = 13.sp,
                            color = if (amount.startsWith("+")) VerifiedGreen else AngolaRed
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun ProviderProfileTab(
    viewModel: MarketplaceViewModel,
    modifier: Modifier = Modifier
) {
    // Section 20 & 21: Perfil Profissional e Verificação
    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .testTag("provider_profile_tab"),
        contentPadding = PaddingValues(16.dp)
    ) {
        item {
            Text("PERFIL PROFISSIONAL & VERIFICAÇÃO", fontSize = 16.sp, fontWeight = FontWeight.Black, color = AngolaRed)
            Text("Documentos oficiais e credenciação para Angola", fontSize = 11.sp, color = Color.Gray)
            Spacer(modifier = Modifier.height(14.dp))
        }

        item {
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column {
                            Text("João Manuel", fontWeight = FontWeight.Bold, fontSize = 17.sp)
                            Text("+244 912 345 678", fontSize = 12.sp, color = Color.Gray)
                        }
                        VerifiedBadge()
                    }

                    Spacer(modifier = Modifier.height(14.dp))
                    Text("Documentos Submetidos:", fontSize = 12.sp, fontWeight = FontWeight.Bold)
                    Spacer(modifier = Modifier.height(6.dp))

                    val docs = listOf(
                        "✓ Bilhete de Identidade (BI Nacional)",
                        "✓ Fotografia Selfie de Identificação",
                        "✓ Certificado Profissional INEFOP (Eletricidade)",
                        "✓ Comprovativo de Morada (Camama, Luanda)",
                        "✓ Comprovativo de IBAN Bancário"
                    )

                    docs.forEach { doc ->
                        Text(doc, fontSize = 12.sp, color = VerifiedGreen, modifier = Modifier.padding(vertical = 2.dp))
                    }

                    Spacer(modifier = Modifier.height(14.dp))
                    Text("Zonas Atendidas em Luanda:", fontSize = 12.sp, fontWeight = FontWeight.Bold)
                    Text("Camama, Talatona, Kilamba, Morro Bento, Patriota, Maianga", fontSize = 12.sp, color = Color.Gray)
                }
            }
        }
    }
}
