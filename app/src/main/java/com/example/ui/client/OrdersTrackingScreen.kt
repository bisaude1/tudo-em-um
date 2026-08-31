package com.example.ui.client

import androidx.compose.foundation.background
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
import com.example.model.ServiceRequest
import com.example.model.ServiceStatus
import com.example.ui.components.ServiceStatusTimeline
import com.example.ui.components.StarRatingDisplay
import com.example.ui.theme.AngolaGold
import com.example.ui.theme.AngolaRed
import com.example.ui.theme.EmergencyRed
import com.example.ui.theme.VerifiedGreen
import com.example.viewmodel.MarketplaceViewModel
import com.example.viewmodel.ScreenDestination

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun OrdersTrackingScreen(
    requestId: String?,
    viewModel: MarketplaceViewModel,
    modifier: Modifier = Modifier
) {
    val serviceRequests by viewModel.serviceRequests.collectAsState()
    val reviews by viewModel.reviews.collectAsState()

    var showDisputeDialog by remember { mutableStateOf(false) }
    var showReviewDialog by remember { mutableStateOf(false) }
    var disputeReason by remember { mutableStateOf("") }

    val activeRequest = if (requestId != null) {
        serviceRequests.find { it.id == requestId } ?: serviceRequests.firstOrNull()
    } else {
        serviceRequests.firstOrNull()
    }

    if (showDisputeDialog && activeRequest != null) {
        AlertDialog(
            onDismissRequest = { showDisputeDialog = false },
            title = { Text("Abrir Disputa / Denúncia", fontWeight = FontWeight.Bold) },
            text = {
                Column {
                    Text("Descreva o problema ocorrido com o serviço para intervenção da equipa Tudo em Um:", fontSize = 12.sp)
                    Spacer(modifier = Modifier.height(8.dp))
                    OutlinedTextField(
                        value = disputeReason,
                        onValueChange = { disputeReason = it },
                        placeholder = { Text("Ex: O profissional atrasou-se ou o serviço não ficou concluído...") },
                        modifier = Modifier.fillMaxWidth()
                    )
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        viewModel.reportDispute(activeRequest.id, "João Manuel", activeRequest.subcategory, disputeReason)
                        showDisputeDialog = false
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = EmergencyRed)
                ) {
                    Text("REGISTAR DISPUTA")
                }
            },
            dismissButton = {
                TextButton(onClick = { showDisputeDialog = false }) { Text("Cancelar") }
            }
        )
    }

    if (showReviewDialog && activeRequest != null) {
        ReviewServiceDialog(
            requestId = activeRequest.id,
            providerName = "João Manuel",
            onSubmit = { q, p, s, pr, c ->
                viewModel.submitServiceReview(activeRequest.id, "prov_joao", q, p, s, pr, c)
                showReviewDialog = false
            },
            onDismiss = { showReviewDialog = false }
        )
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Acompanhamento de Serviços", fontSize = 16.sp, fontWeight = FontWeight.Bold) },
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
                .testTag("orders_tracking_screen"),
            contentPadding = PaddingValues(16.dp)
        ) {
            if (activeRequest == null) {
                item {
                    Box(modifier = Modifier.fillMaxWidth().padding(40.dp), contentAlignment = Alignment.Center) {
                        Text("Nenhum pedido ativo no momento.")
                    }
                }
            } else {
                // Service Card Header
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
                                Text(
                                    text = "PEDIDO #${activeRequest.id.takeLast(6).uppercase()}",
                                    fontSize = 12.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = AngolaRed
                                )
                                Text(
                                    text = activeRequest.scheduledDate,
                                    fontSize = 12.sp,
                                    color = Color.Gray
                                )
                            }
                            Spacer(modifier = Modifier.height(6.dp))
                            Text(
                                text = activeRequest.subcategory,
                                fontSize = 18.sp,
                                fontWeight = FontWeight.Bold
                            )
                            Text(
                                text = "📍 ${activeRequest.neighborhood}, ${activeRequest.municipality}",
                                fontSize = 12.sp,
                                color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f)
                            )
                            Text(
                                text = "\"${activeRequest.description}\"",
                                fontSize = 12.sp,
                                color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.85f),
                                fontStyle = androidx.compose.ui.text.font.FontStyle.Italic
                            )
                        }
                    }
                }

                // Section 16: Complete State Pipeline Timeline
                item {
                    Spacer(modifier = Modifier.height(14.dp))
                    ServiceStatusTimeline(currentStatus = activeRequest.status)
                }

                // Interactive State Advancement Simulator (to demonstrate full lifecycle in demo)
                item {
                    Spacer(modifier = Modifier.height(14.dp))
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(16.dp),
                        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
                    ) {
                        Column(modifier = Modifier.padding(16.dp)) {
                            Text("SIMULADOR DE CICLO DE VIDA (DEMO)", fontSize = 11.sp, fontWeight = FontWeight.Black, color = AngolaGold)
                            Text("Avance o estado do serviço para testar todos os passos:", fontSize = 11.sp, color = Color.Gray)
                            Spacer(modifier = Modifier.height(10.dp))

                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.spacedBy(6.dp)
                            ) {
                                Button(
                                    onClick = { viewModel.advanceOrderStage(activeRequest.id, ServiceStatus.EM_DESLOCAMENTO) },
                                    modifier = Modifier.weight(1f),
                                    shape = RoundedCornerShape(8.dp),
                                    contentPadding = PaddingValues(4.dp)
                                ) {
                                    Text("Deslocamento", fontSize = 10.sp)
                                }
                                Button(
                                    onClick = { viewModel.advanceOrderStage(activeRequest.id, ServiceStatus.EM_ANDAMENTO) },
                                    modifier = Modifier.weight(1f),
                                    shape = RoundedCornerShape(8.dp),
                                    contentPadding = PaddingValues(4.dp)
                                ) {
                                    Text("Em Andamento", fontSize = 10.sp)
                                }
                                Button(
                                    onClick = { viewModel.advanceOrderStage(activeRequest.id, ServiceStatus.CONCLUIDO) },
                                    modifier = Modifier.weight(1f),
                                    shape = RoundedCornerShape(8.dp),
                                    colors = ButtonDefaults.buttonColors(containerColor = VerifiedGreen),
                                    contentPadding = PaddingValues(4.dp)
                                ) {
                                    Text("Concluir", fontSize = 10.sp, fontWeight = FontWeight.Bold)
                                }
                            }
                        }
                    }
                }

                // Actions Card (Confirm service, Review, Chat, Dispute)
                item {
                    Spacer(modifier = Modifier.height(14.dp))
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(16.dp),
                        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
                    ) {
                        Column(modifier = Modifier.padding(16.dp)) {
                            Text("AÇÕES DISPONÍVEIS", fontSize = 12.sp, fontWeight = FontWeight.Bold)
                            Spacer(modifier = Modifier.height(10.dp))

                            if (activeRequest.status == ServiceStatus.CONCLUIDO || activeRequest.status == ServiceStatus.CONFIRMADO) {
                                Button(
                                    onClick = {
                                        viewModel.advanceOrderStage(activeRequest.id, ServiceStatus.CONFIRMADO)
                                        showReviewDialog = true
                                    },
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .height(48.dp)
                                        .testTag("btn_avaliar_servico"),
                                    colors = ButtonDefaults.buttonColors(containerColor = AngolaRed),
                                    shape = RoundedCornerShape(10.dp)
                                ) {
                                    Icon(Icons.Filled.Star, contentDescription = null, tint = AngolaGold, modifier = Modifier.size(18.dp))
                                    Spacer(modifier = Modifier.width(8.dp))
                                    Text("CONFIRMAR E AVALIAR SERVIÇO", fontWeight = FontWeight.Bold)
                                }
                                Spacer(modifier = Modifier.height(10.dp))
                            }

                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.spacedBy(8.dp)
                            ) {
                                OutlinedButton(
                                    onClick = {
                                        viewModel.navigateTo(
                                            ScreenDestination.Chat(
                                                requestId = activeRequest.id,
                                                providerId = activeRequest.selectedProviderId ?: "prov_joao"
                                            )
                                        )
                                    },
                                    modifier = Modifier.weight(1f),
                                    shape = RoundedCornerShape(8.dp)
                                ) {
                                    Icon(Icons.Outlined.Chat, contentDescription = null, modifier = Modifier.size(16.dp))
                                    Spacer(modifier = Modifier.width(6.dp))
                                    Text("Abrir Chat", fontSize = 12.sp)
                                }

                                OutlinedButton(
                                    onClick = { showDisputeDialog = true },
                                    modifier = Modifier.weight(1f),
                                    colors = ButtonDefaults.outlinedButtonColors(contentColor = EmergencyRed),
                                    shape = RoundedCornerShape(8.dp)
                                ) {
                                    Icon(Icons.Outlined.ReportProblem, contentDescription = null, modifier = Modifier.size(16.dp))
                                    Spacer(modifier = Modifier.width(6.dp))
                                    Text("Disputa", fontSize = 12.sp)
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun ReviewServiceDialog(
    requestId: String,
    providerName: String,
    onSubmit: (quality: Int, punctuality: Int, service: Int, price: Int, comment: String) -> Unit,
    onDismiss: () -> Unit
) {
    var qual by remember { mutableStateOf(5) }
    var punc by remember { mutableStateOf(5) }
    var serv by remember { mutableStateOf(5) }
    var pric by remember { mutableStateOf(5) }
    var commentText by remember { mutableStateOf("Excelente profissional! Muito pontual e resolveu tudo com perfeição.") }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Column {
                Text("SERVIÇO CONCLUÍDO", fontSize = 14.sp, fontWeight = FontWeight.Black, color = AngolaRed)
                Text("Como foi a experiência com $providerName?", fontSize = 16.sp, fontWeight = FontWeight.Bold)
            }
        },
        text = {
            Column(modifier = Modifier.fillMaxWidth()) {
                RatingRow(title = "Qualidade do trabalho", value = qual, onSelect = { qual = it })
                RatingRow(title = "Pontualidade", value = punc, onSelect = { punc = it })
                RatingRow(title = "Atendimento", value = serv, onSelect = { serv = it })
                RatingRow(title = "Preço & Transparência", value = pric, onSelect = { pric = it })

                Spacer(modifier = Modifier.height(10.dp))
                Text("Comentário:", fontSize = 12.sp, fontWeight = FontWeight.Bold)
                Spacer(modifier = Modifier.height(4.dp))
                OutlinedTextField(
                    value = commentText,
                    onValueChange = { commentText = it },
                    placeholder = { Text("Escreva sua opinião...") },
                    modifier = Modifier.fillMaxWidth().height(80.dp),
                    shape = RoundedCornerShape(10.dp)
                )
            }
        },
        confirmButton = {
            Button(
                onClick = { onSubmit(qual, punc, serv, pric, commentText) },
                colors = ButtonDefaults.buttonColors(containerColor = AngolaRed)
            ) {
                Text("ENVIAR AVALIAÇÃO", fontWeight = FontWeight.Bold)
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) { Text("Depois") }
        }
    )
}

@Composable
private fun RatingRow(
    title: String,
    value: Int,
    onSelect: (Int) -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(title, fontSize = 12.sp)
        Row(horizontalArrangement = Arrangement.spacedBy(2.dp)) {
            (1..5).forEach { star ->
                Icon(
                    imageVector = Icons.Filled.Star,
                    contentDescription = null,
                    tint = if (star <= value) AngolaGold else Color.LightGray,
                    modifier = Modifier
                        .size(20.dp)
                        .clickable { onSelect(star) }
                )
            }
        }
    }
}
