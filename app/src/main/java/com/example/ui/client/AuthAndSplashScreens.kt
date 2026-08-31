package com.example.ui.client

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
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
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.components.TudoEmUmBrandHeader
import com.example.ui.components.TudoEmUmLogo
import com.example.ui.theme.*
import com.example.viewmodel.MarketplaceViewModel
import com.example.viewmodel.ScreenDestination

@Composable
fun SplashScreen(
    viewModel: MarketplaceViewModel,
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier
            .fillMaxSize()
            .background(
                Brush.verticalGradient(
                    listOf(
                        Color(0xFF07120C),
                        Color(0xFF0F172A),
                        AngolaRedDark
                    )
                )
            )
            .testTag("splash_screen"),
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier.padding(24.dp)
        ) {
            // Emblema e Logotipo Oficial Tudo em Um Angola
            TudoEmUmBrandHeader(
                logoSize = 100.dp,
                showTagline = true,
                isDarkTheme = true
            )

            Spacer(modifier = Modifier.height(36.dp))

            Button(
                onClick = { viewModel.navigateTo(ScreenDestination.AuthRegister) },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(50.dp)
                    .testTag("btn_splash_entrar"),
                colors = ButtonDefaults.buttonColors(containerColor = AngolaRed),
                shape = RoundedCornerShape(12.dp)
            ) {
                Text("ENTRAR / CRIAR CONTA", fontWeight = FontWeight.Bold, fontSize = 14.sp)
            }

            Spacer(modifier = Modifier.height(10.dp))
            TextButton(
                onClick = { viewModel.navigateTo(ScreenDestination.ClientHome) }
            ) {
                Text("Explorar como Visitante →", color = Color.White.copy(alpha = 0.85f), fontSize = 13.sp)
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AuthRegisterScreen(
    viewModel: MarketplaceViewModel,
    modifier: Modifier = Modifier
) {
    var fullName by remember { mutableStateOf("Carlos Bento") }
    var phone by remember { mutableStateOf("923 456 789") }
    var email by remember { mutableStateOf("carlos.bento@gmail.com") }
    var password by remember { mutableStateOf("••••••••") }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Criar Conta", fontSize = 16.sp, fontWeight = FontWeight.Bold) },
                navigationIcon = {
                    IconButton(onClick = { viewModel.navigateTo(ScreenDestination.Splash) }) {
                        Icon(Icons.Filled.ArrowBack, contentDescription = "Voltar")
                    }
                }
            )
        }
    ) { innerPadding ->
        Column(
            modifier = modifier
                .fillMaxSize()
                .padding(innerPadding)
                .padding(20.dp)
                .background(MaterialTheme.colorScheme.background)
                .testTag("auth_register_screen")
        ) {
            Text("Nome completo", fontSize = 12.sp, fontWeight = FontWeight.Bold, color = AngolaRed)
            Spacer(modifier = Modifier.height(4.dp))
            OutlinedTextField(
                value = fullName,
                onValueChange = { fullName = it },
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(10.dp),
                singleLine = true
            )

            Spacer(modifier = Modifier.height(12.dp))
            Text("Telefone (Angola)", fontSize = 12.sp, fontWeight = FontWeight.Bold, color = AngolaRed)
            Spacer(modifier = Modifier.height(4.dp))
            OutlinedTextField(
                value = phone,
                onValueChange = { phone = it },
                prefix = { Text("+244 ", fontWeight = FontWeight.Bold) },
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Phone),
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(10.dp),
                singleLine = true
            )

            Spacer(modifier = Modifier.height(12.dp))
            Text("Email", fontSize = 12.sp, fontWeight = FontWeight.Bold, color = AngolaRed)
            Spacer(modifier = Modifier.height(4.dp))
            OutlinedTextField(
                value = email,
                onValueChange = { email = it },
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email),
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(10.dp),
                singleLine = true
            )

            Spacer(modifier = Modifier.height(12.dp))
            Text("Senha", fontSize = 12.sp, fontWeight = FontWeight.Bold, color = AngolaRed)
            Spacer(modifier = Modifier.height(4.dp))
            OutlinedTextField(
                value = password,
                onValueChange = { password = it },
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(10.dp),
                singleLine = true
            )

            Spacer(modifier = Modifier.height(24.dp))
            Button(
                onClick = {
                    viewModel.registrationPhone.value = "+244 $phone"
                    viewModel.registrationName.value = fullName
                    viewModel.navigateTo(ScreenDestination.AuthOtp)
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(50.dp)
                    .testTag("btn_criar_conta"),
                colors = ButtonDefaults.buttonColors(containerColor = AngolaRed),
                shape = RoundedCornerShape(12.dp)
            ) {
                Text("CRIAR CONTA", fontWeight = FontWeight.Bold, fontSize = 14.sp)
            }

            Spacer(modifier = Modifier.height(12.dp))
            Text(
                text = "🔒 Proteção de contas com autenticação por OTP e encriptação.",
                fontSize = 11.sp,
                color = Color.Gray,
                textAlign = TextAlign.Center,
                modifier = Modifier.fillMaxWidth()
            )
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AuthOtpScreen(
    viewModel: MarketplaceViewModel,
    modifier: Modifier = Modifier
) {
    val phone by viewModel.registrationPhone.collectAsState()
    var otpDigits by remember { mutableStateOf(listOf("2", "4", "4", "0", "9", "2")) }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Confirmação por SMS/OTP", fontSize = 16.sp, fontWeight = FontWeight.Bold) },
                navigationIcon = {
                    IconButton(onClick = { viewModel.navigateTo(ScreenDestination.AuthRegister) }) {
                        Icon(Icons.Filled.ArrowBack, contentDescription = "Voltar")
                    }
                }
            )
        }
    ) { innerPadding ->
        Column(
            modifier = modifier
                .fillMaxSize()
                .padding(innerPadding)
                .padding(24.dp)
                .background(MaterialTheme.colorScheme.background)
                .testTag("auth_otp_screen"),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Box(
                modifier = Modifier
                    .size(60.dp)
                    .clip(CircleShape)
                    .background(AngolaRed.copy(alpha = 0.12f)),
                contentAlignment = Alignment.Center
            ) {
                Icon(Icons.Filled.Sms, contentDescription = null, tint = AngolaRed, modifier = Modifier.size(30.dp))
            }
            Spacer(modifier = Modifier.height(14.dp))
            Text(
                text = "Digite o código enviado\npara $phone",
                fontSize = 16.sp,
                fontWeight = FontWeight.Bold,
                textAlign = TextAlign.Center
            )
            Spacer(modifier = Modifier.height(20.dp))

            // 6-digit OTP Box
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceEvenly
            ) {
                otpDigits.forEachIndexed { index, digit ->
                    Box(
                        modifier = Modifier
                            .size(46.dp)
                            .clip(RoundedCornerShape(10.dp))
                            .background(MaterialTheme.colorScheme.surfaceVariant)
                            .border(1.dp, AngolaRed.copy(alpha = 0.5f), RoundedCornerShape(10.dp)),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(digit, fontSize = 20.sp, fontWeight = FontWeight.Bold)
                    }
                }
            }

            Spacer(modifier = Modifier.height(20.dp))
            TextButton(onClick = { /* Resend */ }) {
                Text("Reenviar código por SMS", color = AngolaRed, fontWeight = FontWeight.SemiBold)
            }

            Spacer(modifier = Modifier.height(24.dp))
            Button(
                onClick = { viewModel.navigateTo(ScreenDestination.ClientHome) },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(50.dp)
                    .testTag("btn_confirm_otp"),
                colors = ButtonDefaults.buttonColors(containerColor = AngolaRed),
                shape = RoundedCornerShape(12.dp)
            ) {
                Text("CONFIRMAR E ENTRAR", fontWeight = FontWeight.Bold)
            }

            Spacer(modifier = Modifier.height(14.dp))
            Text(
                text = "⚠️ Nunca compartilhe sua senha ou código OTP com terceiros.",
                fontSize = 11.sp,
                color = Color.Gray,
                textAlign = TextAlign.Center
            )
        }
    }
}
