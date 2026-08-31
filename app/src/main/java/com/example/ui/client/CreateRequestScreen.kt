package com.example.ui.client

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
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
import com.example.model.ServiceUrgency
import com.example.ui.components.LocationSelectorDialog
import com.example.ui.theme.AngolaRed
import com.example.ui.theme.EmergencyRed
import com.example.viewmodel.MarketplaceViewModel
import com.example.viewmodel.ScreenDestination

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CreateRequestScreen(
    categoryId: String,
    initialSubcategory: String,
    viewModel: MarketplaceViewModel,
    modifier: Modifier = Modifier
) {
    val currentUser by viewModel.currentUser.collectAsState()
    val categories by viewModel.categories.collectAsState()
    val locations by viewModel.locations.collectAsState()

    val currentCategory = categories.find { it.id == categoryId } ?: categories.first()
    var selectedSubcategory by remember { mutableStateOf(if (initialSubcategory.isNotEmpty()) initialSubcategory else currentCategory.subcategories.firstOrNull() ?: "Instalação Elétrica") }
    var descriptionText by remember { mutableStateOf("Troca de disjuntores e instalação de 4 tomadas na sala.") }
    var selectedDate by remember { mutableStateOf("02/09/2026") }
    var selectedTime by remember { mutableStateOf("14:00") }
    var urgency by remember { mutableStateOf(ServiceUrgency.NORMAL) }
    var attachedPhotosCount by remember { mutableStateOf(2) }
    var attachedVideo by remember { mutableStateOf(false) }

    var showLocationDialog by remember { mutableStateOf(false) }

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
                title = { Text("Solicitar Serviço", fontSize = 16.sp, fontWeight = FontWeight.Bold) },
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
                .testTag("create_request_screen"),
            contentPadding = PaddingValues(16.dp)
        ) {
            // Service Category & Subcategory matching Section 10
            item {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Text("Serviço:", fontSize = 13.sp, fontWeight = FontWeight.Bold, color = AngolaRed)
                        Spacer(modifier = Modifier.height(6.dp))
                        Text(
                            text = "${currentCategory.iconEmoji} ${currentCategory.name} • $selectedSubcategory",
                            fontSize = 16.sp,
                            fontWeight = FontWeight.Bold
                        )

                        Spacer(modifier = Modifier.height(14.dp))
                        Text("Descreva o que precisa:", fontSize = 13.sp, fontWeight = FontWeight.Bold)
                        Spacer(modifier = Modifier.height(6.dp))
                        OutlinedTextField(
                            value = descriptionText,
                            onValueChange = { descriptionText = it },
                            placeholder = { Text("Ex: Estou sem energia numa parte da casa ou preciso trocar tomada...") },
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(110.dp)
                                .testTag("input_request_description"),
                            shape = RoundedCornerShape(12.dp)
                        )
                    }
                }
            }

            // Photos and Video Attachments
            item {
                Spacer(modifier = Modifier.height(14.dp))
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Text("Anexos (Fotos & Vídeo)", fontSize = 14.sp, fontWeight = FontWeight.Bold)
                        Spacer(modifier = Modifier.height(10.dp))
                        Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                            // Photo button
                            OutlinedButton(
                                onClick = { attachedPhotosCount = (attachedPhotosCount + 1).coerceAtMost(5) },
                                shape = RoundedCornerShape(10.dp),
                                modifier = Modifier.weight(1f)
                            ) {
                                Icon(Icons.Outlined.PhotoCamera, contentDescription = null, modifier = Modifier.size(18.dp))
                                Spacer(modifier = Modifier.width(6.dp))
                                Text(if (attachedPhotosCount > 0) "$attachedPhotosCount Fotos ✓" else "+ Fotos", fontSize = 12.sp)
                            }

                            // Video button
                            OutlinedButton(
                                onClick = { attachedVideo = !attachedVideo },
                                shape = RoundedCornerShape(10.dp),
                                modifier = Modifier.weight(1f)
                            ) {
                                Icon(Icons.Outlined.Videocam, contentDescription = null, modifier = Modifier.size(18.dp))
                                Spacer(modifier = Modifier.width(6.dp))
                                Text(if (attachedVideo) "1 Vídeo ✓" else "+ Vídeo", fontSize = 12.sp)
                            }
                        }
                    }
                }
            }

            // Location
            item {
                Spacer(modifier = Modifier.height(14.dp))
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable { showLocationDialog = true },
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(16.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column {
                            Text("Local do Serviço:", fontSize = 12.sp, color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f))
                            Spacer(modifier = Modifier.height(2.dp))
                            Text(
                                text = "${currentUser.province} • ${currentUser.municipality} • ${currentUser.neighborhood}",
                                fontWeight = FontWeight.Bold,
                                fontSize = 14.sp
                            )
                        }
                        TextButton(onClick = { showLocationDialog = true }) {
                            Text("Alterar", color = AngolaRed, fontWeight = FontWeight.Bold)
                        }
                    }
                }
            }

            // Date & Time
            item {
                Spacer(modifier = Modifier.height(14.dp))
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(10.dp)
                        ) {
                            Column(modifier = Modifier.weight(1f)) {
                                Text("Data:", fontSize = 12.sp, fontWeight = FontWeight.Bold)
                                Spacer(modifier = Modifier.height(4.dp))
                                OutlinedTextField(
                                    value = selectedDate,
                                    onValueChange = { selectedDate = it },
                                    leadingIcon = { Icon(Icons.Outlined.CalendarToday, contentDescription = null, modifier = Modifier.size(16.dp)) },
                                    singleLine = true,
                                    shape = RoundedCornerShape(10.dp)
                                )
                            }
                            Column(modifier = Modifier.weight(1f)) {
                                Text("Horário:", fontSize = 12.sp, fontWeight = FontWeight.Bold)
                                Spacer(modifier = Modifier.height(4.dp))
                                OutlinedTextField(
                                    value = selectedTime,
                                    onValueChange = { selectedTime = it },
                                    leadingIcon = { Icon(Icons.Outlined.Schedule, contentDescription = null, modifier = Modifier.size(16.dp)) },
                                    singleLine = true,
                                    shape = RoundedCornerShape(10.dp)
                                )
                            }
                        }

                        Spacer(modifier = Modifier.height(14.dp))
                        Text("Urgência do Pedido:", fontSize = 13.sp, fontWeight = FontWeight.Bold)
                        Spacer(modifier = Modifier.height(6.dp))
                        Row(horizontalArrangement = Arrangement.spacedBy(16.dp)) {
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                modifier = Modifier.clickable { urgency = ServiceUrgency.NORMAL }
                            ) {
                                RadioButton(
                                    selected = urgency == ServiceUrgency.NORMAL,
                                    onClick = { urgency = ServiceUrgency.NORMAL }
                                )
                                Text("Normal", fontSize = 13.sp)
                            }
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                modifier = Modifier.clickable { urgency = ServiceUrgency.URGENT }
                            ) {
                                RadioButton(
                                    selected = urgency == ServiceUrgency.URGENT,
                                    onClick = { urgency = ServiceUrgency.URGENT }
                                )
                                Text("🚨 Urgente (Hoje)", fontSize = 13.sp, color = EmergencyRed, fontWeight = FontWeight.Bold)
                            }
                        }
                    }
                }
            }

            // Submit Button
            item {
                Spacer(modifier = Modifier.height(24.dp))
                Button(
                    onClick = {
                        viewModel.submitNewRequest(
                            categoryId = currentCategory.id,
                            subcategory = selectedSubcategory,
                            description = descriptionText,
                            date = selectedDate,
                            time = selectedTime,
                            urgency = urgency
                        )
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(52.dp)
                        .testTag("btn_enviar_pedido"),
                    colors = ButtonDefaults.buttonColors(containerColor = AngolaRed),
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Text("ENVIAR PEDIDO", fontWeight = FontWeight.Black, fontSize = 15.sp)
                }
            }
        }
    }
}
