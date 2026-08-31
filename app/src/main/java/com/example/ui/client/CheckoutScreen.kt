package com.example.ui.client

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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.model.PaymentMethod
import com.example.ui.theme.AngolaGold
import com.example.ui.theme.AngolaRed
import com.example.ui.theme.VerifiedGreen
import com.example.viewmodel.MarketplaceViewModel
import com.example.viewmodel.ScreenDestination

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CheckoutScreen(
    requestId: String,
    quoteId: String,
    viewModel: MarketplaceViewModel,
    modifier: Modifier = Modifier
) {
    val serviceRequests by viewModel.serviceRequests.collectAsState()
    val quotes by viewModel.quotes.collectAsState()
    val currentUser by viewModel.currentUser.collectAsState()

    val request = serviceRequests.find { it.id == requestId } ?: serviceRequests.firstOrNull()
    val quote = quotes.find { it.id == quoteId } ?: quotes.firstOrNull()

    var selectedPaymentMethod by remember { mutableStateOf(PaymentMethod.MULTICAIXA_EXPRESS) }
    var multicaixaPhone by remember { mutableStateOf(currentUser.phone) }
    var isProcessing by remember { mutableStateOf(false) }

    val serviceValue = quote?.priceKz ?: 55000L
    val platformFee = (serviceValue * 0.10).toLong()
    val totalAmount = serviceValue + platformFee

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Confirmar Contratação", fontSize = 16.sp, fontWeight = FontWeight.Bold) },
                navigationIcon = {
                    IconButton(onClick = { viewModel.navigateTo(ScreenDestination.ClientHome) }) {
                        Icon(imageVector = Icons.Filled.ArrowBack, contentDescription = "Voltar")
                    }
                }
            )
        },
        bottomBar = {
            Surface(
                modifier = Modifier.fillMaxWidth(),
                tonalElevation = 8.dp,
                shadowElevation = 8.dp
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text("Total a pagar:", fontSize = 14.sp, color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f))
                        Text(
                            text = "${String.format("%,d", totalAmount).replace(",", ".")} Kz",
                            fontSize = 20.sp,
                            fontWeight = FontWeight.Black,
                            color = AngolaRed
                        )
                    }
                    Spacer(modifier = Modifier.height(10.dp))
                    Button(
                        onClick = {
                            isProcessing = true
                            viewModel.completePayment(request?.id ?: requestId, selectedPaymentMethod)
                        },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(52.dp)
                            .testTag("btn_confirm_pay"),
                        colors = ButtonDefaults.buttonColors(containerColor = AngolaRed),
                        shape = RoundedCornerShape(12.dp),
                        enabled = !isProcessing
                    ) {
                        if (isProcessing) {
                            CircularProgressIndicator(color = Color.White, modifier = Modifier.size(20.dp), strokeWidth = 2.dp)
                        } else {
                            Text("CONFIRMAR E PAGAR", fontWeight = FontWeight.Bold, fontSize = 15.sp)
                        }
                    }
                }
            }
        }
    ) { innerPadding ->
        LazyColumn(
            modifier = modifier
                .fillMaxSize()
                .padding(innerPadding)
                .background(MaterialTheme.colorScheme.background)
                .testTag("checkout_screen"),
            contentPadding = PaddingValues(16.dp)
        ) {
            // Contract Details Card matching Section 14 of Spec
            item {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Text("RESUMO DA CONTRATAÇÃO", fontSize = 12.sp, fontWeight = FontWeight.Bold, color = AngolaRed)
                        Spacer(modifier = Modifier.height(12.dp))

                        DetailRow(label = "Profissional:", value = quote?.providerName ?: "João Manuel")
                        DetailRow(label = "Serviço:", value = request?.subcategory ?: "Instalação elétrica")
                        DetailRow(label = "Data e Hora:", value = "${request?.scheduledDate ?: "02/09/2026"} às ${request?.scheduledTime ?: "15:00"}")
                        DetailRow(label = "Local:", value = "${currentUser.neighborhood}, ${currentUser.municipality}")

                        Spacer(modifier = Modifier.height(12.dp))
                        HorizontalDivider()
                        Spacer(modifier = Modifier.height(12.dp))

                        DetailRow(label = "Valor do Serviço:", value = "${String.format("%,d", serviceValue).replace(",", ".")} Kz")
                        DetailRow(label = "Taxa da plataforma (10%):", value = "${String.format("%,d", platformFee).replace(",", ".")} Kz")

                        Spacer(modifier = Modifier.height(8.dp))
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Text("Total:", fontWeight = FontWeight.Black, fontSize = 16.sp)
                            Text(
                                "${String.format("%,d", totalAmount).replace(",", ".")} Kz",
                                fontWeight = FontWeight.Black,
                                fontSize = 16.sp,
                                color = AngolaRed
                            )
                        }
                    }
                }
            }

            // Payment Methods Card matching Section 14 & 15 of Spec
            item {
                Spacer(modifier = Modifier.height(16.dp))
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Text("MÉTODO DE PAGAMENTO (ANGOLA)", fontSize = 12.sp, fontWeight = FontWeight.Bold, color = AngolaRed)
                        Text("Pagamento seguro retido pela plataforma até à conclusão do serviço.", fontSize = 11.sp, color = Color.Gray)
                        Spacer(modifier = Modifier.height(12.dp))

                        PaymentMethodItem(
                            title = "Multicaixa Express (MCX)",
                            subtitle = "Receba notificação direta no telemóvel",
                            badge = "🇦🇴 Recomendado",
                            isSelected = selectedPaymentMethod == PaymentMethod.MULTICAIXA_EXPRESS,
                            onClick = { selectedPaymentMethod = PaymentMethod.MULTICAIXA_EXPRESS }
                        )

                        if (selectedPaymentMethod == PaymentMethod.MULTICAIXA_EXPRESS) {
                            Spacer(modifier = Modifier.height(8.dp))
                            OutlinedTextField(
                                value = multicaixaPhone,
                                onValueChange = { multicaixaPhone = it },
                                label = { Text("Número de Telemóvel MCX (+244)") },
                                modifier = Modifier.fillMaxWidth(),
                                shape = RoundedCornerShape(10.dp),
                                singleLine = true
                            )
                            Spacer(modifier = Modifier.height(8.dp))
                        }

                        PaymentMethodItem(
                            title = "Cartão Multicaixa / Visa / Mastercard",
                            subtitle = "Débito ou crédito nacional e internacional",
                            badge = null,
                            isSelected = selectedPaymentMethod == PaymentMethod.CARD,
                            onClick = { selectedPaymentMethod = PaymentMethod.CARD }
                        )

                        PaymentMethodItem(
                            title = "Transferência Bancária (IBAN)",
                            subtitle = "BFA, BAI, BIC, BMA, Sol ou Millennium",
                            badge = null,
                            isSelected = selectedPaymentMethod == PaymentMethod.TRANSFER,
                            onClick = { selectedPaymentMethod = PaymentMethod.TRANSFER }
                        )
                    }
                }
            }

            // Trust & Escrow Guarantee note matching Section 15
            item {
                Spacer(modifier = Modifier.height(14.dp))
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(12.dp),
                    colors = CardDefaults.cardColors(containerColor = VerifiedGreen.copy(alpha = 0.08f))
                ) {
                    Row(
                        modifier = Modifier.padding(12.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(Icons.Filled.Security, contentDescription = null, tint = VerifiedGreen, modifier = Modifier.size(24.dp))
                        Spacer(modifier = Modifier.width(10.dp))
                        Text(
                            text = "Garantia Tudo em Um: O valor só é transferido ao profissional após a confirmação e aprovação do serviço.",
                            fontSize = 11.sp,
                            color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.85f)
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun DetailRow(label: String, value: String) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 3.dp),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Text(label, fontSize = 13.sp, color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f))
        Text(value, fontSize = 13.sp, fontWeight = FontWeight.SemiBold)
    }
}

@Composable
private fun PaymentMethodItem(
    title: String,
    subtitle: String,
    badge: String?,
    isSelected: Boolean,
    onClick: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp)
            .clip(RoundedCornerShape(10.dp))
            .background(if (isSelected) AngolaRed.copy(alpha = 0.08f) else Color.Transparent)
            .clickable(onClick = onClick)
            .padding(8.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        RadioButton(
            selected = isSelected,
            onClick = onClick
        )
        Spacer(modifier = Modifier.width(8.dp))
        Column(modifier = Modifier.weight(1f)) {
            Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                Text(title, fontWeight = FontWeight.Bold, fontSize = 13.sp)
                if (badge != null) {
                    Text(
                        text = badge,
                        fontSize = 9.sp,
                        fontWeight = FontWeight.Bold,
                        color = AngolaGold,
                        modifier = Modifier
                            .clip(RoundedCornerShape(4.dp))
                            .background(Color(0xFF0F172A))
                            .padding(horizontal = 4.dp, vertical = 2.dp)
                    )
                }
            }
            Text(subtitle, fontSize = 11.sp, color = Color.Gray)
        }
    }
}
