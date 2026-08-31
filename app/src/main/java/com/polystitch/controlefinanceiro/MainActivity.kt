package com.polystitch.controlefinanceiro

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Surface
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
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

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        MobileAds.initialize(this) {}

        // Inicializa e carrega o anúncio de tela cheia
        interstitialAdManager = InterstitialAdManager(this)
        interstitialAdManager.loadAd()

        enableEdgeToEdge()
        setContent {
            ControleFinanceiroTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = Color(0xFFF8FAFC)
                ) {
                    val viewModel: FinanceViewModel = viewModel()
                    var currentScreen by remember { mutableStateOf("home") }

                    when (currentScreen) {
                        "home" -> {
                            HomeScreen(
                                viewModel = viewModel,
                                interstitialAdManager = interstitialAdManager, // Passando para a Home
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
}