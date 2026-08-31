package com.example.ui.client

import androidx.compose.animation.core.*
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
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
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.model.ServiceUrgency
import com.example.ui.theme.AngolaGold
import com.example.ui.theme.EmergencyRed
import com.example.ui.theme.VerifiedGreen
import com.example.viewmodel.MarketplaceViewModel
import com.example.viewmodel.ScreenDestination

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun UrgentSOSScreen(
    viewModel: MarketplaceViewModel,
    modifier: Modifier = Modifier
) {
    val currentUser by viewModel.currentUser.collectAsState()
    var selectedEmergencyService by remember { mutableStateOf<String?>(null) }
    var isSearchingRadar by remember { mutableStateOf(false) }

    val infiniteTransition = rememberInfiniteTransition(label = "radar")
    val radarScale by infiniteTransition.animateFloat(
        initialValue = 0.8f,
        targetValue = 1.4f,
        animationSpec = infiniteRepeatable(
            animation = tween(1200, easing = LinearEasing),
            repeatMode = RepeatMode.Restart
        ),
        label = "scale"
    )
    val radarAlpha by infiniteTransition.animateFloat(
        initialValue = 0.8f,
        targetValue = 0.0f,
        animationSpec = infiniteRepeatable(
            animation = tween(1200, easing = LinearEasing),
            repeatMode = RepeatMode.Restart
        ),
        label = "alpha"
    )

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("🚨 SERVIÇO URGENTE 24/7", fontSize = 16.sp, fontWeight = FontWeight.Black, color = EmergencyRed) },
                navigationIcon = {
                    IconButton(onClick = { viewModel.navigateTo(ScreenDestination.ClientHome) }) {
                        Icon(imageVector = Icons.Filled.ArrowBack, contentDescription = "Voltar")
                    }
                }
            )
        }
    ) { innerPadding ->
        LazyColumn(
            modifier = modifier
                .fillMaxSize()
                .padding(innerPadding)
                .background(MaterialTheme.colorScheme.background)
                .testTag("urgent_sos_screen"),
            contentPadding = PaddingValues(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // Header Banner
            item {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(18.dp),
                    colors = CardDefaults.cardColors(containerColor = Color(0xFF1E1B24))
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(20.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Text("🚨 SOCORRO IMEDIATO", fontSize = 13.sp, fontWeight = FontWeight.Black, color = EmergencyRed)
                        Spacer(modifier = Modifier.height(6.dp))
                        Text(
                            text = "Precisa de alguém agora em ${currentUser.neighborhood}?",
                            fontSize = 18.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color.White,
                            textAlign = TextAlign.Center
                        )
                        Spacer(modifier = Modifier.height(6.dp))
                        Text(
                            text = "O sistema procura profissionais disponíveis + mais próximos + verificados para atendimento prioritário.",
                            fontSize = 12.sp,
                            color = Color.LightGray,
                            textAlign = TextAlign.Center
                        )
                    }
                }
            }

            // Radar Simulation when searching
            if (isSearchingRadar) {
                item {
                    Spacer(modifier = Modifier.height(24.dp))
                    Box(
                        modifier = Modifier
                            .size(160.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        // Animated pulsing wave
                        Box(
                            modifier = Modifier
                                .size((140 * radarScale).dp)
                                .clip(CircleShape)
                                .background(EmergencyRed.copy(alpha = radarAlpha))
                        )
                        Box(
                            modifier = Modifier
                                .size(90.dp)
                                .clip(CircleShape)
                                .background(EmergencyRed),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = Icons.Filled.LocationSearching,
                                contentDescription = null,
                                tint = Color.White,
                                modifier = Modifier.size(42.dp)
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(14.dp))
                    Text(
                        text = "Localizando ${selectedEmergencyService?.uppercase() ?: "PROFISSIONAL"}...",
                        fontSize = 15.sp,
                        fontWeight = FontWeight.Bold,
                        color = EmergencyRed
                    )
                    Text(
                        text = "Rastreando profissionais num raio de 5 km em Luanda",
                        fontSize = 11.sp,
                        color = Color.Gray
                    )

                    Spacer(modifier = Modifier.height(20.dp))
                    Button(
                        onClick = {
                            viewModel.submitNewRequest(
                                categoryId = "cat_construcao",
                                subcategory = selectedEmergencyService ?: "Eletricista Urgente",
                                description = "Chamado de Emergência SOS: ${selectedEmergencyService ?: "Serviço Urgente"} necessário imediatamente!",
                                date = "Hoje",
                                time = "Imediato",
                                urgency = ServiceUrgency.URGENT
                            )
                        },
                        colors = ButtonDefaults.buttonColors(containerColor = VerifiedGreen),
                        shape = RoundedCornerShape(10.dp)
                    ) {
                        Text("3 PROFISSIONAIS ENCONTRADOS - VER PROPOSTAS", fontWeight = FontWeight.Bold)
                    }
                }
            } else {
                // Section 17 Emergency Service Types Buttons
                item {
                    Spacer(modifier = Modifier.height(20.dp))
                    Text(
                        text = "Selecione o tipo de emergência:",
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier.fillMaxWidth()
                    )
                    Spacer(modifier = Modifier.height(12.dp))

                    val emergencies = listOf(
                        Triple("⚡ ELETRICISTA", "Curto-circuito, apagão, cheiro a queimado", "cat_construcao"),
                        Triple("💧 CANALIZADOR", "Inundação, rebentamento de canos, fuga grave", "cat_construcao"),
                        Triple("🔑 CHAVEIRO", "Trancado para fora de casa ou carro", "cat_construcao"),
                        Triple("🚗 MECÂNICO AUTO", "Viatura não liga, bateria morta, travões", "cat_auto"),
                        Triple("🚚 REBOQUE 24/7", "Socorro na estrada, reboque de veículos", "cat_auto")
                    )

                    emergencies.forEach { (name, desc, catId) ->
                        Card(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 6.dp)
                                .clickable {
                                    selectedEmergencyService = name.substringAfter(" ")
                                    isSearchingRadar = true
                                }
                                .testTag("btn_sos_${name.take(4)}"),
                            shape = RoundedCornerShape(14.dp),
                            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                            elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
                        ) {
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(16.dp),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Column(modifier = Modifier.weight(1f)) {
                                    Text(name, fontWeight = FontWeight.Black, fontSize = 15.sp, color = EmergencyRed)
                                    Text(desc, fontSize = 11.sp, color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f))
                                }
                                Button(
                                    onClick = {
                                        selectedEmergencyService = name.substringAfter(" ")
                                        isSearchingRadar = true
                                    },
                                    colors = ButtonDefaults.buttonColors(containerColor = EmergencyRed),
                                    shape = RoundedCornerShape(8.dp),
                                    contentPadding = PaddingValues(horizontal = 10.dp, vertical = 4.dp)
                                ) {
                                    Text("CHAMAR", fontSize = 11.sp, fontWeight = FontWeight.Bold)
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}
