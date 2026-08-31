package com.example.ui.client

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
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
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.model.Quote
import com.example.ui.components.StarRatingDisplay
import com.example.ui.theme.AngolaGold
import com.example.ui.theme.AngolaRed
import com.example.ui.theme.VerifiedGreen
import com.example.viewmodel.MarketplaceViewModel
import com.example.viewmodel.ScreenDestination

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun QuotesComparisonScreen(
    requestId: String,
    viewModel: MarketplaceViewModel,
    modifier: Modifier = Modifier
) {
    val serviceRequests by viewModel.serviceRequests.collectAsState()
    val quotes by viewModel.quotes.collectAsState()

    val request = serviceRequests.find { it.id == requestId } ?: serviceRequests.firstOrNull()
    val requestQuotes = quotes.filter { it.requestId == requestId }

    var isComparisonMatrixView by remember { mutableStateOf(false) }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        if (isComparisonMatrixView) "Comparar Profissionais" else "Propostas Recebidas",
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Bold
                    )
                },
                navigationIcon = {
                    IconButton(onClick = {
                        if (isComparisonMatrixView) isComparisonMatrixView = false
                        else viewModel.navigateTo(ScreenDestination.ClientHome)
                    }) {
                        Icon(imageVector = Icons.Filled.ArrowBack, contentDescription = "Voltar")
                    }
                },
                actions = {
                    if (!isComparisonMatrixView && requestQuotes.isNotEmpty()) {
                        FilledTonalButton(
                            onClick = { isComparisonMatrixView = true },
                            modifier = Modifier.padding(end = 8.dp),
                            shape = RoundedCornerShape(8.dp)
                        ) {
                            Text("COMPARAR", fontSize = 11.sp, fontWeight = FontWeight.Bold)
                        }
                    }
                }
            )
        }
    ) { innerPadding ->
        if (isComparisonMatrixView) {
            // Section 12: Comparação Lado a Lado Matrix Table
            ComparisonMatrixView(
                quotes = requestQuotes,
                onSelectQuote = { quote ->
                    viewModel.selectQuoteAndCheckout(quote)
                },
                modifier = modifier.padding(innerPadding)
            )
        } else {
            // Section 11: Lista de Propostas Recebidas
            LazyColumn(
                modifier = modifier
                    .fillMaxSize()
                    .padding(innerPadding)
                    .background(MaterialTheme.colorScheme.background)
                    .testTag("quotes_screen"),
                contentPadding = PaddingValues(16.dp)
            ) {
                // Success Dispatch Header
                item {
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(16.dp),
                        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
                    ) {
                        Column(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(16.dp),
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            Box(
                                modifier = Modifier
                                    .size(48.dp)
                                    .clip(CircleShape)
                                    .background(VerifiedGreen.copy(alpha = 0.15f)),
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(Icons.Filled.Check, contentDescription = null, tint = VerifiedGreen, modifier = Modifier.size(24.dp))
                            }
                            Spacer(modifier = Modifier.height(8.dp))
                            Text("Seu pedido foi enviado!", fontSize = 17.sp, fontWeight = FontWeight.Bold)
                            Text(
                                text = "Estamos procurando profissionais próximos em ${request?.neighborhood ?: "Camama"}.",
                                fontSize = 12.sp,
                                textAlign = TextAlign.Center,
                                color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f)
                            )
                            Spacer(modifier = Modifier.height(8.dp))
                            Text(
                                text = "🟢 ${requestQuotes.size} profissionais responderam com propostas.",
                                fontSize = 12.sp,
                                fontWeight = FontWeight.Bold,
                                color = VerifiedGreen
                            )
                        }
                    }
                }

                item {
                    Spacer(modifier = Modifier.height(16.dp))
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text("PROPOSTAS RECEBIDAS", fontSize = 14.sp, fontWeight = FontWeight.Bold, color = AngolaRed)
                        TextButton(onClick = { isComparisonMatrixView = true }) {
                            Text("🔍 Comparar lado a lado", fontSize = 12.sp, fontWeight = FontWeight.Bold, color = AngolaRed)
                        }
                    }
                    Spacer(modifier = Modifier.height(8.dp))
                }

                items(requestQuotes) { quote ->
                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 6.dp)
                            .testTag("quote_card_${quote.id}"),
                        shape = RoundedCornerShape(14.dp),
                        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
                    ) {
                        Column(modifier = Modifier.padding(14.dp)) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.Top
                            ) {
                                Column {
                                    Text(quote.providerName, fontSize = 16.sp, fontWeight = FontWeight.Bold)
                                    Text(quote.providerProfession, fontSize = 12.sp, color = AngolaRed)
                                    Spacer(modifier = Modifier.height(2.dp))
                                    StarRatingDisplay(rating = quote.providerRating, starSize = 13)
                                }
                                Column(horizontalAlignment = Alignment.End) {
                                    Text(
                                        text = "${String.format("%,d", quote.priceKz).replace(",", ".")} Kz",
                                        fontSize = 17.sp,
                                        fontWeight = FontWeight.Black,
                                        color = MaterialTheme.colorScheme.onSurface
                                    )
                                    Text(
                                        text = quote.availabilityText,
                                        fontSize = 11.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = VerifiedGreen
                                    )
                                }
                            }

                            Spacer(modifier = Modifier.height(8.dp))
                            Text(
                                text = "📍 ${quote.providerDistanceKm} km de si • ${quote.providerExperienceYears} anos de experiência",
                                fontSize = 11.sp,
                                color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f)
                            )
                            if (quote.note.isNotEmpty()) {
                                Spacer(modifier = Modifier.height(4.dp))
                                Text(
                                    text = "💬 \"${quote.note}\"",
                                    fontSize = 12.sp,
                                    fontStyle = androidx.compose.ui.text.font.FontStyle.Italic,
                                    color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.8f)
                                )
                            }

                            Spacer(modifier = Modifier.height(12.dp))
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.spacedBy(8.dp)
                            ) {
                                OutlinedButton(
                                    onClick = {
                                        viewModel.navigateTo(
                                            ScreenDestination.Chat(
                                                requestId = quote.requestId,
                                                providerId = quote.providerId
                                            )
                                        )
                                    },
                                    modifier = Modifier.weight(1f),
                                    shape = RoundedCornerShape(8.dp)
                                ) {
                                    Text("Conversar / Chat", fontSize = 11.sp)
                                }

                                Button(
                                    onClick = {
                                        viewModel.selectQuoteAndCheckout(quote)
                                    },
                                    modifier = Modifier.weight(1f),
                                    colors = ButtonDefaults.buttonColors(containerColor = AngolaRed),
                                    shape = RoundedCornerShape(8.dp)
                                ) {
                                    Text("ESCOLHER", fontSize = 12.sp, fontWeight = FontWeight.Bold)
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
fun ComparisonMatrixView(
    quotes: List<Quote>,
    onSelectQuote: (Quote) -> Unit,
    modifier: Modifier = Modifier
) {
    val scrollState = rememberScrollState()

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .padding(16.dp)
            .testTag("comparison_matrix_view")
    ) {
        Text(
            text = "COMPARAR PROFISSIONAIS",
            fontSize = 16.sp,
            fontWeight = FontWeight.Black,
            color = AngolaRed
        )
        Text(
            text = "Compare os detalhes das propostas antes de contratar",
            fontSize = 11.sp,
            color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f)
        )
        Spacer(modifier = Modifier.height(14.dp))

        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(16.dp),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(12.dp)
                    .horizontalScroll(scrollState)
            ) {
                // Table Headers: Metric | Provider 1 | Provider 2 | Provider 3
                Row(
                    modifier = Modifier.padding(vertical = 8.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "Critério",
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier.width(90.dp)
                    )
                    quotes.forEach { q ->
                        Text(
                            text = q.providerName.split(" ").firstOrNull() ?: q.providerName,
                            fontSize = 13.sp,
                            fontWeight = FontWeight.Black,
                            textAlign = TextAlign.Center,
                            modifier = Modifier.width(90.dp)
                        )
                    }
                }
                HorizontalDivider()

                // Row: Avaliação
                MatrixRow(
                    label = "Avaliação",
                    values = quotes.map { "⭐ ${it.providerRating}" }
                )
                HorizontalDivider()

                // Row: Preço
                MatrixRow(
                    label = "Preço",
                    values = quotes.map { "${it.priceKz / 1000}K Kz" },
                    isBold = true
                )
                HorizontalDivider()

                // Row: Distância
                MatrixRow(
                    label = "Distância",
                    values = quotes.map { "${it.providerDistanceKm} km" }
                )
                HorizontalDivider()

                // Row: Experiência
                MatrixRow(
                    label = "Experiência",
                    values = quotes.map { "${it.providerExperienceYears} anos" }
                )
                HorizontalDivider()

                // Row: Disponível
                MatrixRow(
                    label = "Disponível",
                    values = quotes.map { it.availabilityText }
                )
                HorizontalDivider()

                // Action Row
                Row(
                    modifier = Modifier.padding(top = 12.dp, bottom = 4.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "Ação",
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier.width(90.dp)
                    )
                    quotes.forEach { q ->
                        Box(
                            modifier = Modifier.width(90.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            Button(
                                onClick = { onSelectQuote(q) },
                                colors = ButtonDefaults.buttonColors(containerColor = AngolaRed),
                                contentPadding = PaddingValues(horizontal = 6.dp, vertical = 2.dp),
                                shape = RoundedCornerShape(6.dp)
                            ) {
                                Text("ESCOLHER", fontSize = 10.sp, fontWeight = FontWeight.Bold)
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun MatrixRow(
    label: String,
    values: List<String>,
    isBold: Boolean = false
) {
    Row(
        modifier = Modifier.padding(vertical = 8.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = label,
            fontSize = 11.sp,
            color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f),
            modifier = Modifier.width(90.dp)
        )
        values.forEach { v ->
            Text(
                text = v,
                fontSize = 11.sp,
                fontWeight = if (isBold) FontWeight.Bold else FontWeight.Normal,
                textAlign = TextAlign.Center,
                color = MaterialTheme.colorScheme.onSurface,
                modifier = Modifier.width(90.dp)
            )
        }
    }
}
