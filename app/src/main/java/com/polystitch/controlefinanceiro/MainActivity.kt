package com.polystitch.controlefinanceiro

import android.Manifest
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Surface
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.core.content.ContextCompat
import androidx.lifecycle.viewmodel.compose.viewModel
import com.google.android.gms.ads.MobileAds
import com.polystitch.controlefinanceiro.ui.AdicionarDespesaCardScreen
import com.polystitch.controlefinanceiro.ui.AdicionarDespesaVistaScreen
import com.polystitch.controlefinanceiro.ui.CartoesScreen
import com.polystitch.controlefinanceiro.ui.CategoriasScreen
import com.polystitch.controlefinanceiro.ui.ConsultarDespesasScreen
import com.polystitch.controlefinanceiro.ui.DespesasFixasScreen
import com.polystitch.controlefinanceiro.ui.GraficosScreen
import com.polystitch.controlefinanceiro.ui.HomeScreen
import com.polystitch.controlefinanceiro.ui.InterstitialAdManager
import com.polystitch.controlefinanceiro.ui.theme.ControleFinanceiroTheme
import com.polystitch.controlefinanceiro.viewmodel.FinanceViewModel

class MainActivity : ComponentActivity() {
    lateinit var interstitialAdManager: InterstitialAdManager

    private val requestPermissionLauncher = registerForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) { _ ->
        // O usuário respondeu à permissão; mantemos o fluxo normal do app
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        MobileAds.initialize(this) {}

        interstitialAdManager = InterstitialAdManager(this)
        interstitialAdManager.loadAd()

        enableEdgeToEdge()
        setContent {
            val viewModel: FinanceViewModel = viewModel()
            val currentTheme by viewModel.currentTheme.collectAsState()

            // Solicita a permissão após a UI principal já estar desenhada na tela
            LaunchedEffect(Unit) {
                verificarESolicitarPermissaoNotificacao()
            }

            ControleFinanceiroTheme(appTheme = currentTheme) {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = Color(0xFFF8FAFC)
                ) {
                    var currentScreen by remember { mutableStateOf("home") }

                    when (currentScreen) {
                        "home" -> {
                            HomeScreen(
                                viewModel = viewModel,
                                interstitialAdManager = interstitialAdManager,
                                onNavigateToCartoes = { currentScreen = "cartoes" },
                                onNavigateToAdicionarDespesaCartao = { currentScreen = "adicionar_despesa_cartao" },
                                onNavigateToAdicionarDespesaVista = { currentScreen = "adicionar_despesa_vista" },
                                onNavigateToConsultarDespesas = { currentScreen = "consultar_despesas" },
                                onNavigateToGraficos = { currentScreen = "graficos" },
                                onNavigateToDespesasFixas = { currentScreen = "despesas_fixas" },
                                onNavigateToCategorias = { currentScreen = "categorias" }
                            )
                        }
                        "cartoes" -> {
                            CartoesScreen(viewModel = viewModel, onNavigateBack = { currentScreen = "home" })
                        }
                        "adicionar_despesa_cartao" -> {
                            AdicionarDespesaCardScreen(viewModel = viewModel, onNavigateBack = { currentScreen = "home" })
                        }
                        "adicionar_despesa_vista" -> {
                            AdicionarDespesaVistaScreen(viewModel = viewModel, onNavigateBack = { currentScreen = "home" })
                        }
                        "consultar_despesas" -> {
                            ConsultarDespesasScreen(viewModel = viewModel, onNavigateBack = { currentScreen = "home" })
                        }
                        "graficos" -> {
                            GraficosScreen(viewModel = viewModel, onNavigateBack = { currentScreen = "home" })
                        }
                        "despesas_fixas" -> {
                            DespesasFixasScreen(viewModel = viewModel, onNavigateBack = { currentScreen = "home" })
                        }
                        "categorias" -> {
                            CategoriasScreen(viewModel = viewModel, onNavigateBack = { currentScreen = "home" })
                        }
                    }
                }
            }
        }
    }

    private fun verificarESolicitarPermissaoNotificacao() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            if (ContextCompat.checkSelfPermission(
                    this,
                    Manifest.permission.POST_NOTIFICATIONS
                ) != PackageManager.PERMISSION_GRANTED
            ) {
                requestPermissionLauncher.launch(Manifest.permission.POST_NOTIFICATIONS)
            }
        }
    }
}