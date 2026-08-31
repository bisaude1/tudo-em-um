package com.example

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.model.UserRole
import com.example.ui.admin.AdminDashboardScreen
import com.example.ui.client.*
import com.example.ui.components.SuperappRoleHeader
import com.example.ui.provider.ProviderDashboardScreen
import com.example.ui.theme.TudoEmUmTheme
import com.example.viewmodel.MarketplaceViewModel
import com.example.viewmodel.ScreenDestination

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            TudoEmUmTheme {
                val vm: MarketplaceViewModel = viewModel()
                MainAppContent(viewModel = vm)
            }
        }
    }
}

@Composable
fun MainAppContent(
    viewModel: MarketplaceViewModel,
    modifier: Modifier = Modifier
) {
    val currentRole by viewModel.currentRole.collectAsState()
    val currentScreen by viewModel.currentScreen.collectAsState()

    Scaffold(
        modifier = modifier.fillMaxSize(),
        topBar = {
            // Show role switcher bar for easy review among the 3 systems (Cliente / Profissional / Admin)
            if (currentScreen !is ScreenDestination.Splash) {
                SuperappRoleHeader(
                    currentRole = currentRole,
                    onRoleSelected = { role ->
                        viewModel.switchRole(role)
                    }
                )
            }
        }
    ) { innerPadding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
        ) {
            when (currentRole) {
                UserRole.CLIENT -> {
                    when (val screen = currentScreen) {
                        is ScreenDestination.Splash -> {
                            SplashScreen(viewModel = viewModel)
                        }
                        is ScreenDestination.AuthRegister -> {
                            AuthRegisterScreen(viewModel = viewModel)
                        }
                        is ScreenDestination.AuthOtp -> {
                            AuthOtpScreen(viewModel = viewModel)
                        }
                        is ScreenDestination.ClientHome -> {
                            ClientHomeScreen(viewModel = viewModel)
                        }
                        is ScreenDestination.ProviderList -> {
                            SearchAndFilterScreen(
                                categoryId = screen.categoryId,
                                initialQuery = screen.query,
                                viewModel = viewModel
                            )
                        }
                        is ScreenDestination.ProviderDetail -> {
                            ProviderDetailScreen(
                                providerId = screen.providerId,
                                viewModel = viewModel
                            )
                        }
                        is ScreenDestination.CreateRequest -> {
                            CreateRequestScreen(
                                categoryId = screen.categoryId,
                                initialSubcategory = screen.subcategory,
                                viewModel = viewModel
                            )
                        }
                        is ScreenDestination.RequestQuotes -> {
                            QuotesComparisonScreen(
                                requestId = screen.requestId,
                                viewModel = viewModel
                            )
                        }
                        is ScreenDestination.QuotesComparison -> {
                            QuotesComparisonScreen(
                                requestId = screen.requestId,
                                viewModel = viewModel
                            )
                        }
                        is ScreenDestination.Chat -> {
                            ChatScreen(
                                requestId = screen.requestId,
                                providerId = screen.providerId,
                                viewModel = viewModel
                            )
                        }
                        is ScreenDestination.Checkout -> {
                            CheckoutScreen(
                                requestId = screen.requestId,
                                quoteId = screen.quoteId,
                                viewModel = viewModel
                            )
                        }
                        is ScreenDestination.OrdersList -> {
                            OrdersTrackingScreen(
                                requestId = null,
                                viewModel = viewModel
                            )
                        }
                        is ScreenDestination.OrderTracking -> {
                            OrdersTrackingScreen(
                                requestId = screen.requestId,
                                viewModel = viewModel
                            )
                        }
                        is ScreenDestination.UrgentSOS -> {
                            UrgentSOSScreen(viewModel = viewModel)
                        }
                        else -> {
                            ClientHomeScreen(viewModel = viewModel)
                        }
                    }
                }
                UserRole.PROVIDER -> {
                    ProviderDashboardScreen(viewModel = viewModel)
                }
                UserRole.ADMIN -> {
                    AdminDashboardScreen(viewModel = viewModel)
                }
            }
        }
    }
}
