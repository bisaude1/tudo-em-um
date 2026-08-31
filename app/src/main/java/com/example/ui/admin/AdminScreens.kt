package com.example.ui.admin

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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.theme.*
import com.example.viewmodel.MarketplaceViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AdminDashboardScreen(
    viewModel: MarketplaceViewModel,
    modifier: Modifier = Modifier
) {
    var adminTab by remember { mutableStateOf(0) } // 0: Visão Geral, 1: Profissionais, 2: Categorias, 3: Localizações, 4: Disputas

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Column {
                        Text("PAINEL ADMINISTRATIVO", fontSize = 14.sp, fontWeight = FontWeight.Black, color = AngolaGold)
                        Text("Tudo em Um — Controlo da Plataforma Angola", fontSize = 11.sp, color = Color.White.copy(alpha = 0.8f))
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = Color(0xFF0F172A),
                    titleContentColor = Color.White
                )
            )
        },
        bottomBar = {
            NavigationBar(
                containerColor = Color(0xFF0F172A),
                tonalElevation = 6.dp
            ) {
                NavigationBarItem(
                    selected = adminTab == 0,
                    onClick = { adminTab = 0 },
                    icon = { Icon(Icons.Filled.Analytics, contentDescription = "Geral", tint = if (adminTab == 0) AngolaGold else Color.Gray) },
                    label = { Text("Métricas", fontSize = 10.sp, color = if (adminTab == 0) AngolaGold else Color.Gray) }
                )
                NavigationBarItem(
                    selected = adminTab == 1,
                    onClick = { adminTab = 1 },
                    icon = { Icon(Icons.Filled.Badge, contentDescription = "Profissionais", tint = if (adminTab == 1) AngolaGold else Color.Gray) },
                    label = { Text("Profissionais", fontSize = 10.sp, color = if (adminTab == 1) AngolaGold else Color.Gray) }
                )
                NavigationBarItem(
                    selected = adminTab == 2,
                    onClick = { adminTab = 2 },
                    icon = { Icon(Icons.Filled.Category, contentDescription = "Categorias", tint = if (adminTab == 2) AngolaGold else Color.Gray) },
                    label = { Text("Categorias", fontSize = 10.sp, color = if (adminTab == 2) AngolaGold else Color.Gray) }
                )
                NavigationBarItem(
                    selected = adminTab == 3,
                    onClick = { adminTab = 3 },
                    icon = { Icon(Icons.Filled.LocationCity, contentDescription = "Luanda", tint = if (adminTab == 3) AngolaGold else Color.Gray) },
                    label = { Text("Locais", fontSize = 10.sp, color = if (adminTab == 3) AngolaGold else Color.Gray) }
                )
                NavigationBarItem(
                    selected = adminTab == 4,
                    onClick = { adminTab = 4 },
                    icon = {
                        BadgedBox(badge = { Badge(containerColor = EmergencyRed) { Text("2") } }) {
                            Icon(Icons.Filled.Gavel, contentDescription = "Disputas", tint = if (adminTab == 4) AngolaGold else Color.Gray)
                        }
                    },
                    label = { Text("Disputas", fontSize = 10.sp, color = if (adminTab == 4) AngolaGold else Color.Gray) }
                )
            }
        }
    ) { innerPadding ->
        when (adminTab) {
            0 -> AdminMetricsOverview(modifier = modifier.padding(innerPadding))
            1 -> AdminProfessionalsTab(viewModel = viewModel, modifier = modifier.padding(innerPadding))
            2 -> AdminCategoriesTab(viewModel = viewModel, modifier = modifier.padding(innerPadding))
            3 -> AdminLocationsTab(viewModel = viewModel, modifier = modifier.padding(innerPadding))
            4 -> AdminDisputesTab(viewModel = viewModel, modifier = modifier.padding(innerPadding))
            else -> AdminMetricsOverview(modifier = modifier.padding(innerPadding))
        }
    }
}

@Composable
fun AdminMetricsOverview(modifier: Modifier = Modifier) {
    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .testTag("admin_metrics_tab"),
        contentPadding = PaddingValues(16.dp)
    ) {
        item {
            Text("INDICADORES GERAIS DA PLATAFORMA", fontSize = 15.sp, fontWeight = FontWeight.Black, color = AngolaRed)
            Text("Operações de marketplace em Angola (Luanda MVP)", fontSize = 11.sp, color = Color.Gray)
            Spacer(modifier = Modifier.height(14.dp))
        }

        item {
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                AdminStatCard("👥 Usuários Totais", "12.450", "+340 esta semana", VerifiedGreen, Modifier.weight(1f))
                AdminStatCard("⚡ Profissionais", "1.280", "148 pendentes", AngolaGold, Modifier.weight(1f))
            }
            Spacer(modifier = Modifier.height(8.dp))
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                AdminStatCard("🔨 Serviços Feitos", "8.320", "Taxa sucesso 98.2%", VerifiedGreen, Modifier.weight(1f))
                AdminStatCard("⚖️ Disputas Ativas", "2", "Aguardando mediação", EmergencyRed, Modifier.weight(1f))
            }
        }

        item {
            Spacer(modifier = Modifier.height(14.dp))
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = Color(0xFF0F172A))
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text("FATURAMENTO GLOBAL (MÊS)", fontSize = 12.sp, color = Color.LightGray)
                    Spacer(modifier = Modifier.height(4.dp))
                    Text("45.800.000 Kz", fontSize = 26.sp, fontWeight = FontWeight.Black, color = AngolaGold)
                    Spacer(modifier = Modifier.height(4.dp))
                    Text("Comissão Tudo em Um retida (10%): 4.580.000 Kz", fontSize = 12.sp, color = VerifiedGreen, fontWeight = FontWeight.SemiBold)
                }
            }
        }

        item {
            Spacer(modifier = Modifier.height(18.dp))
            Text("Top Bairros com Mais Demanda (Luanda)", fontSize = 14.sp, fontWeight = FontWeight.Bold)
            Spacer(modifier = Modifier.height(8.dp))

            val bairros = listOf(
                Pair("1. Talatona", "2.840 serviços • 16.4M Kz"),
                Pair("2. Camama (Talatona)", "2.120 serviços • 11.2M Kz"),
                Pair("3. Kilamba (Belas)", "1.650 serviços • 8.9M Kz"),
                Pair("4. Maianga (Luanda)", "940 serviços • 5.1M Kz"),
                Pair("5. Morro Bento", "770 serviços • 4.2M Kz")
            )

            bairros.forEach { (name, stats) ->
                Card(
                    modifier = Modifier.fillMaxWidth().padding(vertical = 3.dp),
                    shape = RoundedCornerShape(10.dp),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth().padding(12.dp),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text(name, fontWeight = FontWeight.Bold, fontSize = 13.sp)
                        Text(stats, fontSize = 11.sp, color = Color.Gray)
                    }
                }
            }
        }
    }
}

@Composable
private fun AdminStatCard(
    title: String,
    value: String,
    sub: String,
    accentColor: Color,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier,
        shape = RoundedCornerShape(14.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
    ) {
        Column(modifier = Modifier.padding(12.dp)) {
            Text(title, fontSize = 11.sp, color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f))
            Spacer(modifier = Modifier.height(4.dp))
            Text(value, fontSize = 18.sp, fontWeight = FontWeight.Black)
            Spacer(modifier = Modifier.height(2.dp))
            Text(sub, fontSize = 10.sp, color = accentColor, fontWeight = FontWeight.SemiBold)
        }
    }
}

@Composable
fun AdminProfessionalsTab(
    viewModel: MarketplaceViewModel,
    modifier: Modifier = Modifier
) {
    val providers by viewModel.providers.collectAsState()

    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .testTag("admin_professionals_tab"),
        contentPadding = PaddingValues(16.dp)
    ) {
        item {
            Text("GESTÃO DE PROFISSIONAIS", fontSize = 16.sp, fontWeight = FontWeight.Black, color = AngolaRed)
            Text("Aprovação de BI, certificados INEFOP e auditoria", fontSize = 11.sp, color = Color.Gray)
            Spacer(modifier = Modifier.height(14.dp))
        }

        items(providers) { prov ->
            Card(
                modifier = Modifier.fillMaxWidth().padding(vertical = 6.dp),
                shape = RoundedCornerShape(14.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
            ) {
                Column(modifier = Modifier.padding(14.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column {
                            Text(prov.name, fontWeight = FontWeight.Bold, fontSize = 15.sp)
                            Text("⚡ ${prov.profession} • 📍 ${prov.servedNeighborhoods.firstOrNull() ?: "Camama"}", fontSize = 12.sp, color = Color.Gray)
                        }
                        Text(
                            text = if (prov.isVerified) "🟢 APROVADO" else "🟡 PENDENTE",
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold,
                            color = if (prov.isVerified) VerifiedGreen else AngolaGold
                        )
                    }

                    Spacer(modifier = Modifier.height(10.dp))
                    Text("Score Tudo em Um: ${prov.calculateScore()}/100 • ${prov.reviewsCount} avaliações", fontSize = 11.sp)

                    Spacer(modifier = Modifier.height(12.dp))
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Button(
                            onClick = {
                                viewModel.toggleProviderVerification(prov.id)
                            },
                            colors = ButtonDefaults.buttonColors(
                                containerColor = if (prov.isVerified) Color.DarkGray else VerifiedGreen
                            ),
                            shape = RoundedCornerShape(8.dp),
                            modifier = Modifier.weight(1f)
                        ) {
                            Text(if (prov.isVerified) "Desativar Verificação" else "Aprovar BI / INEFOP", fontSize = 11.sp)
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun AdminCategoriesTab(
    viewModel: MarketplaceViewModel,
    modifier: Modifier = Modifier
) {
    val categories by viewModel.categories.collectAsState()

    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .testTag("admin_categories_tab"),
        contentPadding = PaddingValues(16.dp)
    ) {
        item {
            Text("GESTÃO DE CATEGORIAS E COMISSÕES", fontSize = 16.sp, fontWeight = FontWeight.Black, color = AngolaRed)
            Text("Controlo de catálogo de serviços e percentual de comissão Tudo em Um (10%)", fontSize = 11.sp, color = Color.Gray)
            Spacer(modifier = Modifier.height(14.dp))
        }

        items(categories) { cat ->
            Card(
                modifier = Modifier.fillMaxWidth().padding(vertical = 4.dp),
                shape = RoundedCornerShape(12.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth().padding(14.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                        Text(cat.iconEmoji, fontSize = 24.sp)
                        Column {
                            Text(cat.name, fontWeight = FontWeight.Bold, fontSize = 14.sp)
                            Text("${cat.subcategories.size} subcategorias • Comissão: 10%", fontSize = 11.sp, color = Color.Gray)
                        }
                    }
                    Text("🟢 ATIVA", fontSize = 11.sp, fontWeight = FontWeight.Bold, color = VerifiedGreen)
                }
            }
        }
    }
}

@Composable
fun AdminLocationsTab(
    viewModel: MarketplaceViewModel,
    modifier: Modifier = Modifier
) {
    val locations by viewModel.locations.collectAsState()

    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .testTag("admin_locations_tab"),
        contentPadding = PaddingValues(16.dp)
    ) {
        item {
            Text("GEOGRAFIA & COBERTURA ANGOLA", fontSize = 16.sp, fontWeight = FontWeight.Black, color = AngolaRed)
            Text("Hierarquia geográfica: Província > Município > Distrito > Bairro", fontSize = 11.sp, color = Color.Gray)
            Spacer(modifier = Modifier.height(14.dp))
        }

        items(locations) { loc ->
            Card(
                modifier = Modifier.fillMaxWidth().padding(vertical = 4.dp),
                shape = RoundedCornerShape(14.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
            ) {
                Column(modifier = Modifier.padding(14.dp)) {
                    Text("🇦🇴 Província de ${loc.province}", fontWeight = FontWeight.Black, fontSize = 15.sp, color = AngolaRed)
                    Spacer(modifier = Modifier.height(4.dp))
                    Text("🏙️ Município: ${loc.municipality}", fontWeight = FontWeight.Bold, fontSize = 13.sp)
                    Text("Distrito: ${loc.district} • Bairro: ${loc.neighborhood}", fontSize = 11.sp, color = Color.Gray)
                }
            }
        }
    }
}

@Composable
fun AdminDisputesTab(
    viewModel: MarketplaceViewModel,
    modifier: Modifier = Modifier
) {
    val disputes by viewModel.disputes.collectAsState()

    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .testTag("admin_disputes_tab"),
        contentPadding = PaddingValues(16.dp)
    ) {
        item {
            Text("CENTRAL DE DISPUTAS & MEDIAÇÃO", fontSize = 16.sp, fontWeight = FontWeight.Black, color = EmergencyRed)
            Text("Análise e resolução de conflitos entre clientes e prestadores de serviço", fontSize = 11.sp, color = Color.Gray)
            Spacer(modifier = Modifier.height(14.dp))
        }

        items(disputes) { disp ->
            Card(
                modifier = Modifier.fillMaxWidth().padding(vertical = 6.dp),
                shape = RoundedCornerShape(14.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
            ) {
                Column(modifier = Modifier.padding(14.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text("CASO #${disp.id.takeLast(4).uppercase()}", fontWeight = FontWeight.Bold, fontSize = 13.sp, color = EmergencyRed)
                        Text(disp.status, fontSize = 11.sp, fontWeight = FontWeight.Bold, color = AngolaGold)
                    }

                    Spacer(modifier = Modifier.height(6.dp))
                    Text("Serviço: ${disp.serviceName}", fontWeight = FontWeight.Bold, fontSize = 14.sp)
                    Text("Profissional: ${disp.providerName} • Cliente: ${disp.customerName}", fontSize = 12.sp, color = Color.Gray)

                    Spacer(modifier = Modifier.height(8.dp))
                    Card(
                        shape = RoundedCornerShape(8.dp),
                        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant)
                    ) {
                        Text(
                            text = "Motivo da denúncia: \"${disp.reason}\"",
                            fontSize = 11.sp,
                            modifier = Modifier.padding(8.dp),
                            color = MaterialTheme.colorScheme.onSurface
                        )
                    }

                    if (disp.status == "Resolvido") {
                        Spacer(modifier = Modifier.height(8.dp))
                        Text("Decisão: Acordo finalizado", fontSize = 11.sp, fontWeight = FontWeight.Bold, color = VerifiedGreen)
                    } else {
                        Spacer(modifier = Modifier.height(12.dp))
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(6.dp)
                        ) {
                            Button(
                                onClick = { viewModel.resolveDispute(disp.id) },
                                colors = ButtonDefaults.buttonColors(containerColor = AngolaRed),
                                modifier = Modifier.weight(1f),
                                contentPadding = PaddingValues(4.dp),
                                shape = RoundedCornerShape(8.dp)
                            ) {
                                Text("Reembolsar", fontSize = 10.sp)
                            }
                            Button(
                                onClick = { viewModel.resolveDispute(disp.id) },
                                colors = ButtonDefaults.buttonColors(containerColor = VerifiedGreen),
                                modifier = Modifier.weight(1f),
                                contentPadding = PaddingValues(4.dp),
                                shape = RoundedCornerShape(8.dp)
                            ) {
                                Text("Pagar Profissional", fontSize = 10.sp)
                            }
                        }
                    }
                }
            }
        }
    }
}
