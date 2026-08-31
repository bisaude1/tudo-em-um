package com.example.ui.components

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.*
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.drawscope.Fill
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.R
import com.example.ui.theme.AngolaGold
import com.example.ui.theme.AngolaRed

/**
 * Logotipo oficial do "TUDO EM UM" (Angola).
 * Emblema em formato de escudo/monograma 'U' dinâmico com laços entrelaçados
 * nas cores Verde Floresta (#0E5A35), Dourado Ouro (#F59E0B) e miolo de ferramentas.
 */
@Composable
fun TudoEmUmLogo(
    modifier: Modifier = Modifier,
    size: Dp = 80.dp,
    showGlow: Boolean = true
) {
    Box(
        modifier = modifier
            .size(size)
            .testTag("tudo_em_um_logo"),
        contentAlignment = Alignment.Center
    ) {
        if (showGlow) {
            Box(
                modifier = Modifier
                    .size(size * 0.9f)
                    .clip(CircleShape)
                    .background(
                        Brush.radialGradient(
                            colors = listOf(
                                AngolaGold.copy(alpha = 0.25f),
                                Color.Transparent
                            )
                        )
                    )
            )
        }

        // Renderiza o vetor de alta fidelidade
        Icon(
            painter = painterResource(id = R.drawable.ic_logo_tudoemum),
            contentDescription = "Logotipo Tudo em Um Angola",
            tint = Color.Unspecified,
            modifier = Modifier.size(size)
        )
    }
}

/**
 * Cabeçalho de marca completo com o logotipo e tipografia institucional.
 */
@Composable
fun TudoEmUmBrandHeader(
    modifier: Modifier = Modifier,
    logoSize: Dp = 64.dp,
    showTagline: Boolean = true,
    isDarkTheme: Boolean = true
) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = modifier.testTag("tudo_em_um_brand_header")
    ) {
        TudoEmUmLogo(size = logoSize)

        Spacer(modifier = Modifier.height(10.dp))

        Row(verticalAlignment = Alignment.CenterVertically) {
            Text(
                text = "TUDO",
                fontSize = (logoSize.value * 0.32f).sp,
                fontWeight = FontWeight.Black,
                color = if (isDarkTheme) Color.White else MaterialTheme.colorScheme.onBackground,
                letterSpacing = 1.sp
            )
            Spacer(modifier = Modifier.width(6.dp))
            Box(
                modifier = Modifier
                    .clip(RoundedCornerShape(4.dp))
                    .background(AngolaGold)
                    .padding(horizontal = 6.dp, vertical = 2.dp)
            ) {
                Text(
                    text = "EM UM",
                    fontSize = (logoSize.value * 0.26f).sp,
                    fontWeight = FontWeight.Black,
                    color = Color(0xFF0F172A),
                    letterSpacing = 0.5.sp
                )
            }
        }

        if (showTagline) {
            Spacer(modifier = Modifier.height(6.dp))
            Text(
                text = "“Tudo o que precisa, num só lugar.”",
                fontSize = 13.sp,
                fontWeight = FontWeight.SemiBold,
                color = AngolaGold
            )
            Text(
                text = "SERVIÇOS & PROFISSIONAIS EM ANGOLA",
                fontSize = 9.sp,
                fontWeight = FontWeight.Bold,
                color = if (isDarkTheme) Color.White.copy(alpha = 0.6f) else Color.Gray,
                letterSpacing = 1.sp
            )
        }
    }
}
