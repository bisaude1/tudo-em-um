package com.example.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
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
import androidx.compose.ui.window.Dialog
import com.example.model.LocationItem
import com.example.ui.theme.AngolaRed
import com.example.ui.theme.VerifiedGreen

@Composable
fun LocationSelectorDialog(
    locations: List<LocationItem>,
    currentProvince: String,
    currentMunicipality: String,
    currentDistrict: String,
    currentNeighborhood: String,
    onLocationConfirmed: (province: String, municipality: String, district: String, neighborhood: String) -> Unit,
    onDismiss: () -> Unit
) {
    var selectedProvince by remember { mutableStateOf(currentProvince) }
    var selectedMunicipality by remember { mutableStateOf(currentMunicipality) }
    var selectedDistrict by remember { mutableStateOf(currentDistrict) }
    var selectedNeighborhood by remember { mutableStateOf(currentNeighborhood) }

    val provinces = remember(locations) {
        (locations.map { it.province } + listOf("Luanda", "Benguela", "Huíla", "Huambo", "Cabinda")).distinct()
    }

    val municipalities = remember(selectedProvince, locations) {
        val list = locations.filter { it.province == selectedProvince }.map { it.municipality }.distinct()
        if (list.isEmpty()) listOf("Talatona", "Belas", "Luanda", "Kilamba Kiaxi", "Viana", "Cazenga", "Cacuaco") else list
    }

    val neighborhoods = remember(selectedMunicipality, locations) {
        val list = locations.filter { it.municipality == selectedMunicipality }.map { it.neighborhood }.distinct()
        if (list.isEmpty()) {
            when (selectedMunicipality) {
                "Talatona" -> listOf("Camama", "Morro Bento", "Talatona Centro", "Benfica", "Patriota")
                "Belas" -> listOf("Centralidade do Kilamba", "Ramiros", "Vila Verde")
                "Luanda" -> listOf("Maianga", "Alvalade", "Mutamba", "Ilha de Luanda", "Maculusso")
                "Viana" -> listOf("Zango 1", "Zango 2", "Zango 3", "Vila de Viana", "Estalagem")
                "Cazenga" -> listOf("Hoji-ya-Henda", "Tala Hady", "Cazenga Popular")
                "Cacuaco" -> listOf("Vila de Cacuaco", "Centralidade do Sequele", "Kicolo")
                else -> listOf("Centro", "Bairro 1", "Bairro 2")
            }
        } else list
    }

    Dialog(onDismissRequest = onDismiss) {
        Surface(
            modifier = Modifier
                .fillMaxWidth()
                .wrapContentHeight()
                .clip(RoundedCornerShape(20.dp))
                .testTag("location_selector_dialog"),
            color = MaterialTheme.colorScheme.surface,
            tonalElevation = 8.dp
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(20.dp)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column {
                        Text(
                            text = "Onde você está?",
                            fontSize = 18.sp,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.onSurface
                        )
                        Text(
                            text = "Hierarquia de localização em Angola",
                            fontSize = 11.sp,
                            color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f)
                        )
                    }
                    IconButton(onClick = onDismiss) {
                        Icon(imageVector = Icons.Filled.Close, contentDescription = "Fechar")
                    }
                }

                Spacer(modifier = Modifier.height(12.dp))

                // GPS Auto-detect Button
                OutlinedButton(
                    onClick = {
                        selectedProvince = "Luanda"
                        selectedMunicipality = "Talatona"
                        selectedDistrict = "Talatona"
                        selectedNeighborhood = "Camama"
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("btn_gps_location"),
                    colors = ButtonDefaults.outlinedButtonColors(contentColor = VerifiedGreen)
                ) {
                    Icon(
                        imageVector = Icons.Filled.MyLocation,
                        contentDescription = null,
                        modifier = Modifier.size(16.dp),
                        tint = VerifiedGreen
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text("📍 Usar minha localização (GPS)", fontWeight = FontWeight.SemiBold)
                }

                Spacer(modifier = Modifier.height(14.dp))

                // Província Dropdown
                Text("Província", fontSize = 12.sp, fontWeight = FontWeight.Bold, color = AngolaRed)
                DropdownField(
                    selectedValue = selectedProvince,
                    options = provinces,
                    onSelected = {
                        selectedProvince = it
                        selectedMunicipality = if (it == "Luanda") "Talatona" else "Sede"
                    }
                )

                Spacer(modifier = Modifier.height(10.dp))

                // Município Dropdown
                Text("Município", fontSize = 12.sp, fontWeight = FontWeight.Bold, color = AngolaRed)
                DropdownField(
                    selectedValue = selectedMunicipality,
                    options = municipalities,
                    onSelected = {
                        selectedMunicipality = it
                        selectedDistrict = it
                    }
                )

                Spacer(modifier = Modifier.height(10.dp))

                // Bairro Dropdown
                Text("Bairro", fontSize = 12.sp, fontWeight = FontWeight.Bold, color = AngolaRed)
                DropdownField(
                    selectedValue = selectedNeighborhood,
                    options = neighborhoods,
                    onSelected = { selectedNeighborhood = it }
                )

                Spacer(modifier = Modifier.height(20.dp))

                Button(
                    onClick = {
                        onLocationConfirmed(
                            selectedProvince,
                            selectedMunicipality,
                            selectedDistrict,
                            selectedNeighborhood
                        )
                        onDismiss()
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(48.dp)
                        .testTag("btn_confirm_location"),
                    colors = ButtonDefaults.buttonColors(containerColor = AngolaRed)
                ) {
                    Text("CONTINUAR", fontWeight = FontWeight.Bold, fontSize = 14.sp)
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun DropdownField(
    selectedValue: String,
    options: List<String>,
    onSelected: (String) -> Unit
) {
    var expanded by remember { mutableStateOf(false) }

    ExposedDropdownMenuBox(
        expanded = expanded,
        onExpandedChange = { expanded = !expanded },
        modifier = Modifier.fillMaxWidth()
    ) {
        OutlinedTextField(
            value = selectedValue,
            onValueChange = {},
            readOnly = true,
            trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = expanded) },
            modifier = Modifier
                .menuAnchor()
                .fillMaxWidth(),
            shape = RoundedCornerShape(10.dp)
        )

        ExposedDropdownMenu(
            expanded = expanded,
            onDismissRequest = { expanded = false }
        ) {
            options.forEach { option ->
                DropdownMenuItem(
                    text = { Text(option) },
                    onClick = {
                        onSelected(option)
                        expanded = false
                    }
                )
            }
        }
    }
}
