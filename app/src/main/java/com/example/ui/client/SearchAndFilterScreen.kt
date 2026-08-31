package com.example.ui.client

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.outlined.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.components.LocationSelectorDialog
import com.example.ui.theme.AngolaRed
import com.example.ui.theme.VerifiedGreen
import com.example.viewmodel.MarketplaceViewModel
import com.example.viewmodel.ScreenDestination

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SearchAndFilterScreen(
    categoryId: String?,
    initialQuery: String,
    viewModel: MarketplaceViewModel,
    modifier: Modifier = Modifier
) {
    val currentUser by viewModel.currentUser.collectAsState()
    val categories by viewModel.categories.collectAsState()
    val locations by viewModel.locations.collectAsState()
    val filterSortBy by viewModel.filterSortBy.collectAsState()
    val filterOnlyAvailableNow by viewModel.filterOnlyAvailableNow.collectAsState()
    val filterMinRating by viewModel.filterMinRating.collectAsState()

    var searchQuery by remember { mutableStateOf(initialQuery) }
    var showLocationDialog by remember { mutableStateOf(false) }

    val currentCategory = categories.find { it.id == categoryId }
    val filteredProviders = remember(categoryId, searchQuery, filterSortBy, filterOnlyAvailableNow, filterMinRating) {
        viewModel.getFilteredProviders(categoryId, searchQuery)
    }

    if (showLocationDialog) {
        LocationSelectorDialog(
            locations = locations,
            currentProvince = currentUser.province,
            currentMunicipality = currentUser.municipality,
            currentDistrict = currentUser.district,
            currentNeighborhood = currentUser.neighborhood,
            onLocationConfirmed = { prov, muni, dist, neigh ->
                viewModel.updateLocation(prov, muni, dist, neigh)
            },
            onDismiss = { showLocationDialog = false }
        )
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Column {
                        Text(
                            text = if (searchQuery.isNotEmpty()) searchQuery.uppercase() else currentCategory?.name?.uppercase() ?: "TODOS OS SERVIÇOS",
                            fontSize = 16.sp,
                            fontWeight = FontWeight.Bold
                        )
                        Text(
                            text = "EM ${currentUser.neighborhood.uppercase()}, ${currentUser.municipality.uppercase()}",
                            fontSize = 11.sp,
                            color = AngolaRed,
                            fontWeight = FontWeight.SemiBold
                        )
                    }
                },
                navigationIcon = {
                    IconButton(onClick = { viewModel.navigateTo(ScreenDestination.ClientHome) }) {
                        Icon(imageVector = Icons.Filled.ArrowBack, contentDescription = "Voltar")
                    }
                },
                actions = {
                    IconButton(onClick = { showLocationDialog = true }) {
                        Icon(imageVector = Icons.Filled.LocationOn, contentDescription = "Mudar Localização", tint = AngolaRed)
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
                .testTag("search_filter_screen"),
            contentPadding = PaddingValues(bottom = 80.dp)
        ) {
            // Search Input inside Screen
            item {
                OutlinedTextField(
                    value = searchQuery,
                    onValueChange = {
                        searchQuery = it
                        viewModel.onSearchQueryChanged(it)
                    },
                    placeholder = { Text("Filtrar por serviço, nome ou bairro...") },
                    leadingIcon = { Icon(Icons.Filled.Search, contentDescription = null, tint = AngolaRed) },
                    trailingIcon = {
                        if (searchQuery.isNotEmpty()) {
                            IconButton(onClick = { searchQuery = "" }) {
                                Icon(Icons.Filled.Clear, contentDescription = "Limpar")
                            }
                        }
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp, vertical = 8.dp)
                        .testTag("search_filter_input"),
                    shape = RoundedCornerShape(12.dp),
                    singleLine = true
                )
            }

            // Filter Chips Bar matching Section 7: Filtros (Distância, Avaliação, Preço, Disponível agora)
            item {
                LazyRow(
                    contentPadding = PaddingValues(horizontal = 16.dp),
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    modifier = Modifier.padding(bottom = 8.dp)
                ) {
                    item {
                        FilterChip(
                            selected = filterSortBy == "score",
                            onClick = { viewModel.setFilterSort("score") },
                            label = { Text("🎯 Melhor Score", fontSize = 12.sp) }
                        )
                    }
                    item {
                        FilterChip(
                            selected = filterOnlyAvailableNow,
                            onClick = { viewModel.toggleFilterOnlyAvailable(!filterOnlyAvailableNow) },
                            label = { Text("🟢 Disponível agora", fontSize = 12.sp) }
                        )
                    }
                    item {
                        FilterChip(
                            selected = filterSortBy == "distance",
                            onClick = { viewModel.setFilterSort("distance") },
                            label = { Text("📍 Distância", fontSize = 12.sp) }
                        )
                    }
                    item {
                        FilterChip(
                            selected = filterMinRating >= 4.8,
                            onClick = {
                                viewModel.setFilterMinRating(if (filterMinRating >= 4.8) 0.0 else 4.8)
                            },
                            label = { Text("⭐ Avaliação 4.8+", fontSize = 12.sp) }
                        )
                    }
                    item {
                        FilterChip(
                            selected = filterSortBy == "price",
                            onClick = { viewModel.setFilterSort("price") },
                            label = { Text("💰 Menor Preço", fontSize = 12.sp) }
                        )
                    }
                }
            }

            // Results Counter
            item {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp, vertical = 6.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "${filteredProviders.size} profissionais encontrados",
                        fontSize = 12.sp,
                        fontWeight = FontWeight.SemiBold,
                        color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f)
                    )
                    Text(
                        text = "Filtro ativo: ${when(filterSortBy) { "price" -> "Preço Kz"; "distance" -> "Mais Próximo"; "rating" -> "Avaliação"; else -> "Score Tudo em Um" }}",
                        fontSize = 11.sp,
                        color = AngolaRed
                    )
                }
            }

            if (filteredProviders.isEmpty()) {
                item {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(40.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Icon(
                                imageVector = Icons.Outlined.SearchOff,
                                contentDescription = null,
                                modifier = Modifier.size(54.dp),
                                tint = MaterialTheme.colorScheme.outline
                            )
                            Spacer(modifier = Modifier.height(12.dp))
                            Text(
                                text = "Nenhum profissional encontrado",
                                fontWeight = FontWeight.Bold,
                                fontSize = 16.sp
                            )
                            Text(
                                text = "Tente alterar os filtros ou pesquisar outro bairro de Luanda.",
                                fontSize = 12.sp,
                                color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f)
                            )
                        }
                    }
                }
            } else {
                items(filteredProviders) { provider ->
                    ProviderCardItem(
                        provider = provider,
                        onViewProfile = {
                            viewModel.navigateTo(ScreenDestination.ProviderDetail(provider.id))
                        },
                        onRequestQuote = {
                            viewModel.navigateTo(
                                ScreenDestination.CreateRequest(
                                    categoryId = provider.categoryId,
                                    subcategory = provider.profession
                                )
                            )
                        },
                        modifier = Modifier.padding(horizontal = 16.dp, vertical = 6.dp)
                    )
                }
            }
        }
    }
}
