package com.example.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
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
import com.example.model.UserRole
import com.example.ui.theme.*

@Composable
fun SuperappRoleHeader(
    currentRole: UserRole,
    onRoleSelected: (UserRole) -> Unit,
    modifier: Modifier = Modifier
) {
    Surface(
        modifier = modifier
            .fillMaxWidth()
            .testTag("superapp_role_header"),
        color = MaterialTheme.colorScheme.surface,
        tonalElevation = 2.dp,
        shadowElevation = 2.dp
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 12.dp, vertical = 6.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                TudoEmUmLogo(
                    size = 32.dp,
                    showGlow = false
                )
                Spacer(modifier = Modifier.width(8.dp))
                Column {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text(
                            text = "TUDO EM UM",
                            fontWeight = FontWeight.Black,
                            fontSize = 12.sp,
                            color = MaterialTheme.colorScheme.onSurface,
                            letterSpacing = 0.5.sp
                        )
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(
                            text = "AO",
                            fontWeight = FontWeight.Bold,
                            fontSize = 8.sp,
                            color = AngolaGold,
                            modifier = Modifier
                                .clip(RoundedCornerShape(3.dp))
                                .background(Color(0xFF0F172A))
                                .padding(horizontal = 3.dp, vertical = 1.dp)
                        )
                    }
                    Text(
                        text = "Luanda • Serviços",
                        fontSize = 9.sp,
                        color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f)
                    )
                }
            }

            // Role Pills
            Row(
                modifier = Modifier
                    .clip(RoundedCornerShape(20.dp))
                    .background(MaterialTheme.colorScheme.surfaceVariant)
                    .padding(2.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                RolePillItem(
                    label = "Cliente",
                    isSelected = currentRole == UserRole.CLIENT,
                    onClick = { onRoleSelected(UserRole.CLIENT) },
                    testTag = "role_pill_client"
                )
                RolePillItem(
                    label = "Profissional",
                    isSelected = currentRole == UserRole.PROVIDER,
                    onClick = { onRoleSelected(UserRole.PROVIDER) },
                    testTag = "role_pill_provider"
                )
                RolePillItem(
                    label = "Admin",
                    isSelected = currentRole == UserRole.ADMIN,
                    onClick = { onRoleSelected(UserRole.ADMIN) },
                    testTag = "role_pill_admin"
                )
            }
        }
    }
}

@Composable
private fun RolePillItem(
    label: String,
    isSelected: Boolean,
    onClick: () -> Unit,
    testTag: String
) {
    Box(
        modifier = Modifier
            .testTag(testTag)
            .clip(RoundedCornerShape(16.dp))
            .background(
                if (isSelected) AngolaRed else Color.Transparent
            )
            .clickable(onClick = onClick)
            .padding(horizontal = 10.dp, vertical = 4.dp),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = label,
            fontSize = 11.sp,
            fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium,
            color = if (isSelected) Color.White else MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f)
        )
    }
}

@Composable
fun StarRatingDisplay(
    rating: Double,
    modifier: Modifier = Modifier,
    starSize: Int = 14,
    showCount: Int? = null
) {
    Row(
        modifier = modifier,
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(2.dp)
    ) {
        Icon(
            imageVector = Icons.Filled.Star,
            contentDescription = "Avaliação",
            tint = AngolaGold,
            modifier = Modifier.size(starSize.dp)
        )
        Text(
            text = String.format("%.1f", rating),
            fontSize = (starSize - 1).sp,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.onSurface
        )
        if (showCount != null) {
            Text(
                text = "($showCount)",
                fontSize = (starSize - 2).sp,
                color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f)
            )
        }
    }
}

@Composable
fun VerifiedBadge(modifier: Modifier = Modifier) {
    Row(
        modifier = modifier
            .clip(RoundedCornerShape(4.dp))
            .background(VerifiedGreen.copy(alpha = 0.12f))
            .padding(horizontal = 6.dp, vertical = 2.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(2.dp)
    ) {
        Icon(
            imageVector = Icons.Filled.CheckCircle,
            contentDescription = "Verificado",
            tint = VerifiedGreen,
            modifier = Modifier.size(12.dp)
        )
        Text(
            text = "Verificado",
            fontSize = 10.sp,
            fontWeight = FontWeight.SemiBold,
            color = VerifiedGreen
        )
    }
}

@Composable
fun SmartScoreBadge(
    score: Int,
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier
            .clip(RoundedCornerShape(6.dp))
            .background(
                Brush.linearGradient(
                    listOf(
                        Color(0xFF0F172A),
                        Color(0xFF1E293B)
                    )
                )
            )
            .border(0.5.dp, AngolaGold.copy(alpha = 0.5f), RoundedCornerShape(6.dp))
            .padding(horizontal = 6.dp, vertical = 3.dp),
        contentAlignment = Alignment.Center
    ) {
        Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(3.dp)) {
            Icon(
                imageVector = Icons.Filled.AutoAwesome,
                contentDescription = null,
                tint = AngolaGold,
                modifier = Modifier.size(11.dp)
            )
            Text(
                text = "SCORE $score/100",
                fontSize = 9.sp,
                fontWeight = FontWeight.Black,
                color = AngolaGold,
                letterSpacing = 0.5.sp
            )
        }
    }
}

@Composable
fun ServiceStatusTimeline(
    currentStatus: ServiceStatus,
    modifier: Modifier = Modifier
) {
    val steps = listOf(
        ServiceStatus.PENDENTE to "Pendente",
        ServiceStatus.PROPOSTAS_RECEBIDAS to "Propostas",
        ServiceStatus.PAGO to "Pago",
        ServiceStatus.EM_ANDAMENTO to "Em Andamento",
        ServiceStatus.CONCLUIDO to "Concluído",
        ServiceStatus.AVALIADO to "Avaliado"
    )

    Card(
        modifier = modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant)
    ) {
        Column(modifier = Modifier.padding(12.dp)) {
            Text(
                text = "ESTADO DO SERVIÇO: ${currentStatus.label.uppercase()}",
                fontSize = 11.sp,
                fontWeight = FontWeight.Bold,
                color = if (currentStatus == ServiceStatus.DISPUTA) EmergencyRed else AngolaRed
            )
            Spacer(modifier = Modifier.height(10.dp))
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                steps.forEachIndexed { index, pair ->
                    val isDone = currentStatus.stepIndex >= pair.first.stepIndex
                    val isCurrent = currentStatus.stepIndex == pair.first.stepIndex

                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        modifier = Modifier.weight(1f)
                    ) {
                        Box(
                            modifier = Modifier
                                .size(22.dp)
                                .clip(CircleShape)
                                .background(
                                    when {
                                        isCurrent -> AngolaRed
                                        isDone -> VerifiedGreen
                                        else -> Color.LightGray.copy(alpha = 0.5f)
                                    }
                                ),
                            contentAlignment = Alignment.Center
                        ) {
                            if (isDone && !isCurrent) {
                                Icon(
                                    imageVector = Icons.Filled.Check,
                                    contentDescription = null,
                                    tint = Color.White,
                                    modifier = Modifier.size(12.dp)
                                )
                            } else {
                                Text(
                                    text = "${index + 1}",
                                    fontSize = 10.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = Color.White
                                )
                            }
                        }
                        Spacer(modifier = Modifier.height(3.dp))
                        Text(
                            text = pair.second,
                            fontSize = 8.sp,
                            maxLines = 1,
                            fontWeight = if (isCurrent) FontWeight.Bold else FontWeight.Normal,
                            color = if (isCurrent) AngolaRed else MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f)
                        )
                    }
                }
            }
        }
    }
}
