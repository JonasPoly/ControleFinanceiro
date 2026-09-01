package com.polystitch.controlefinanceiro.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ListAlt
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.ArrowDownward
import androidx.compose.material.icons.filled.ArrowUpward
import androidx.compose.material.icons.filled.Category
import androidx.compose.material.icons.filled.ChevronLeft
import androidx.compose.material.icons.filled.ChevronRight
import androidx.compose.material.icons.filled.CreditCard
import androidx.compose.material.icons.filled.EventRepeat
import androidx.compose.material.icons.filled.Palette
import androidx.compose.material.icons.filled.Payments
import androidx.compose.material.icons.filled.PictureAsPdf
import androidx.compose.material.icons.filled.PieChart
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.polystitch.controlefinanceiro.ui.theme.AppTheme
import com.polystitch.controlefinanceiro.utils.PdfGenerator
import com.polystitch.controlefinanceiro.viewmodel.FinanceViewModel
import java.util.Calendar
import java.util.Locale

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(
    viewModel: FinanceViewModel = viewModel(),
    interstitialAdManager: InterstitialAdManager? = null,
    onNavigateToCartoes: () -> Unit = {},
    onNavigateToAdicionarDespesaCartao: () -> Unit = {},
    onNavigateToAdicionarDespesaVista: () -> Unit = {},
    onNavigateToConsultarDespesas: () -> Unit = {},
    onNavigateToGraficos: () -> Unit = {},
    onNavigateToDespesasFixas: () -> Unit = {},
    onNavigateToCategorias: () -> Unit = {}
) {
    val context = LocalContext.current
    val activity = context as? android.app.Activity
    val transacoes by viewModel.transacoes.collectAsState(initial = emptyList())
    val currentTheme by viewModel.currentTheme.collectAsState()

    // Coleta de cartões e categorias do ViewModel para validação
    val cartoes by viewModel.cartoes.collectAsState(initial = emptyList())
    val categorias by viewModel.categorias.collectAsState(initial = emptyList())

    // Estados para controlar os diálogos de alerta de itens faltantes
    var mostrarAlertaFaltaCartao by remember { mutableStateOf(false) }
    var mostrarAlertaFaltaCategoria by remember { mutableStateOf(false) }

    var selectedCalendar by remember {
        mutableStateOf(Calendar.getInstance().apply {
            set(Calendar.DAY_OF_MONTH, 1)
        })
    }

    val selectedYear = selectedCalendar.get(Calendar.YEAR)
    val selectedMonth = selectedCalendar.get(Calendar.MONTH)

    val transacoesFiltradas = remember(transacoes, selectedYear, selectedMonth) {
        transacoes.filter { tx ->
            try {
                val calTx = Calendar.getInstance().apply { timeInMillis = tx.data }
                calTx.get(Calendar.YEAR) == selectedYear && calTx.get(Calendar.MONTH) == selectedMonth
            } catch (_: Exception) {
                false
            }
        }
    }

    val receitaTotal = transacoesFiltradas.filter { it.tipo == "RECEITA" }.sumOf { it.valor } -
            transacoesFiltradas.filter { it.tipo == "RETIRADA_RECEITA" }.sumOf { it.valor }
    val despesaTotal = transacoesFiltradas.filter { it.tipo == "DESPESA" }.sumOf { it.valor }
    val saldoTotal = receitaTotal - despesaTotal

    var showDialog by remember { mutableStateOf(false) }
    var tipoMovimentacao by remember { mutableStateOf("RECEITA") }
    var valorInput by remember { mutableStateOf("") }
    var showThemeDialog by remember { mutableStateOf(false) }

    val successColor = Color(0xFF34D399)
    val expenseColor = Color(0xFFF87171)

    // Cores dinâmicas baseadas no tema atual do MaterialTheme
    val primaryColor = MaterialTheme.colorScheme.primary
    val secondaryColor = MaterialTheme.colorScheme.secondary
    val backgroundSurface = MaterialTheme.colorScheme.background

    val screenBackgroundBrush = Brush.verticalGradient(
        colors = listOf(
            backgroundSurface,
            MaterialTheme.colorScheme.surface,
            primaryColor.copy(alpha = 0.2f),
            primaryColor.copy(alpha = 0.5f)
        )
    )

    val dynamicCardBrush = Brush.horizontalGradient(
        colors = listOf(primaryColor, secondaryColor)
    )

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(screenBackgroundBrush)
    ) {
        Scaffold(
            topBar = {
                TopAppBar(
                    title = {
                        Column {
                            Text(
                                text = "Visão Geral",
                                style = MaterialTheme.typography.titleSmall,
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.onBackground
                            )
                            Text(
                                text = "Controle do Sistema",
                                style = MaterialTheme.typography.bodySmall,
                                fontSize = 10.sp,
                                color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.7f)
                            )
                        }
                    },
                    actions = {
                        TextButton(
                            onClick = { showThemeDialog = true },
                            contentPadding = PaddingValues(horizontal = 4.dp, vertical = 2.dp)
                        ) {
                            Column(
                                horizontalAlignment = Alignment.CenterHorizontally,
                                verticalArrangement = Arrangement.Center
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Palette,
                                    contentDescription = "Alterar Tema",
                                    tint = MaterialTheme.colorScheme.onBackground,
                                    modifier = Modifier.size(18.dp)
                                )
                                Spacer(modifier = Modifier.height(1.dp))
                                Text(
                                    text = "altere o tema",
                                    fontSize = 9.sp,
                                    fontWeight = FontWeight.Medium,
                                    color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.8f)
                                )
                            }
                        }
                    },
                    colors = TopAppBarDefaults.topAppBarColors(
                        containerColor = Color.Transparent
                    )
                )
            },
            containerColor = Color.Transparent
        ) { paddingValues ->
            val scrollState = rememberScrollState()

            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues)
                    .verticalScroll(scrollState)
                    .padding(horizontal = 14.dp, vertical = 2.dp),
                verticalArrangement = Arrangement.spacedBy(6.dp)
            ) {
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .shadow(elevation = 4.dp, shape = RoundedCornerShape(10.dp)),
                    shape = RoundedCornerShape(10.dp),
                    colors = CardDefaults.cardColors(containerColor = Color.Transparent)
                ) {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .background(dynamicCardBrush)
                            .padding(horizontal = 6.dp, vertical = 2.dp)
                    ) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            IconButton(
                                onClick = {
                                    selectedCalendar = (selectedCalendar.clone() as Calendar).apply {
                                        add(Calendar.MONTH, -1)
                                    }
                                },
                                modifier = Modifier.size(28.dp)
                            ) {
                                Icon(Icons.Default.ChevronLeft, contentDescription = null, tint = Color.White)
                            }

                            val mesNome = selectedCalendar.getDisplayName(Calendar.MONTH, Calendar.LONG, Locale("pt", "BR"))
                                ?.replaceFirstChar { it.uppercase() } ?: ""
                            Text(
                                text = "$mesNome $selectedYear",
                                style = MaterialTheme.typography.bodyMedium,
                                fontWeight = FontWeight.Bold,
                                color = Color.White
                            )

                            IconButton(
                                onClick = {
                                    selectedCalendar = (selectedCalendar.clone() as Calendar).apply {
                                        add(Calendar.MONTH, 1)
                                    }
                                },
                                modifier = Modifier.size(28.dp)
                            ) {
                                Icon(Icons.Default.ChevronRight, contentDescription = null, tint = Color.White)
                            }
                        }
                    }
                }

                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .shadow(elevation = 8.dp, shape = RoundedCornerShape(18.dp))
                        .clip(RoundedCornerShape(18.dp))
                        .background(brush = dynamicCardBrush)
                        .padding(12.dp)
                ) {
                    Column(
                        verticalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Column(verticalArrangement = Arrangement.spacedBy(1.dp)) {
                                Text(
                                    text = "Saldo Total do Mês",
                                    color = Color.White.copy(alpha = 0.75f),
                                    fontSize = 11.sp,
                                    fontWeight = FontWeight.Medium
                                )
                                Text(
                                    text = "R$ %.2f".format(saldoTotal),
                                    color = Color.White,
                                    fontSize = 24.sp,
                                    fontWeight = FontWeight.ExtraBold
                                )
                            }
                        }

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            Button(
                                onClick = { tipoMovimentacao = "RECEITA"; showDialog = true },
                                modifier = Modifier
                                    .weight(1f)
                                    .height(30.dp),
                                colors = ButtonDefaults.buttonColors(containerColor = Color.White.copy(alpha = 0.2f)),
                                shape = RoundedCornerShape(8.dp),
                                contentPadding = PaddingValues(0.dp)
                            ) {
                                Icon(Icons.Default.Add, contentDescription = null, tint = Color.White, modifier = Modifier.size(14.dp))
                                Spacer(modifier = Modifier.width(3.dp))
                                Text(text = "Adicionar", color = Color.White, fontSize = 11.sp)
                            }

                            Button(
                                onClick = { tipoMovimentacao = "RETIRADA_RECEITA"; showDialog = true },
                                modifier = Modifier
                                    .weight(1f)
                                    .height(30.dp),
                                colors = ButtonDefaults.buttonColors(containerColor = Color.White.copy(alpha = 0.2f)),
                                shape = RoundedCornerShape(8.dp),
                                contentPadding = PaddingValues(0.dp)
                            ) {
                                Icon(Icons.Default.ArrowDownward, contentDescription = null, tint = Color.White, modifier = Modifier.size(14.dp))
                                Spacer(modifier = Modifier.width(3.dp))
                                Text(text = "Retirar", color = Color.White, fontSize = 11.sp)
                            }
                        }

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            FinanceIndicatorItem(
                                title = "Entradas",
                                value = "R$ %.2f".format(receitaTotal),
                                icon = Icons.Default.ArrowUpward,
                                color = successColor
                            )
                            FinanceIndicatorItem(
                                title = "Saídas",
                                value = "R$ %.2f".format(despesaTotal),
                                icon = Icons.Default.ArrowDownward,
                                color = expenseColor
                            )
                        }
                    }
                }

                Column(
                    verticalArrangement = Arrangement.spacedBy(5.dp),
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(bottom = 6.dp)
                ) {
                    HomeMenuButton(
                        title = "Incluir Despesa à Vista",
                        subtitle = "Pix, Débito ou Dinheiro",
                        icon = Icons.Default.Payments,
                        onClick = {
                            if (categorias.isEmpty()) {
                                mostrarAlertaFaltaCategoria = true
                            } else {
                                onNavigateToAdicionarDespesaVista()
                            }
                        },
                        cardBrush = dynamicCardBrush
                    )

                    HomeMenuButton(
                        title = "Incluir Despesa no Cartão",
                        subtitle = "Insira compras na fatura",
                        icon = Icons.Default.CreditCard,
                        onClick = {
                            if (cartoes.isEmpty()) {
                                mostrarAlertaFaltaCartao = true
                            } else if (categorias.isEmpty()) {
                                mostrarAlertaFaltaCategoria = true
                            } else {
                                onNavigateToAdicionarDespesaCartao()
                            }
                        },
                        cardBrush = dynamicCardBrush
                    )

                    HomeMenuButton(
                        title = "Despesas Fixas / Recorrentes",
                        subtitle = "Gerencie contas mensais automáticas",
                        icon = Icons.Default.EventRepeat,
                        onClick = {
                            if (categorias.isEmpty()) {
                                mostrarAlertaFaltaCategoria = true
                            } else {
                                onNavigateToDespesasFixas()
                            }
                        },
                        cardBrush = dynamicCardBrush
                    )

                    HomeMenuButton(
                        title = "Consultar Despesas",
                        subtitle = "Visualize todas as despesas",
                        icon = Icons.AutoMirrored.Filled.ListAlt,
                        onClick = onNavigateToConsultarDespesas,
                        cardBrush = dynamicCardBrush
                    )

                    HomeMenuButton(
                        title = "Análise Gráfica",
                        subtitle = "Gráficos de gastos e despesas",
                        icon = Icons.Default.PieChart,
                        onClick = onNavigateToGraficos,
                        cardBrush = dynamicCardBrush
                    )

                    HomeMenuButton(
                        title = "Central de Cartões",
                        subtitle = "Gerencie cartões e faturas",
                        icon = Icons.Default.CreditCard,
                        onClick = onNavigateToCartoes,
                        cardBrush = dynamicCardBrush
                    )

                    HomeMenuButton(
                        title = "Gerenciar Categorias",
                        subtitle = "Cadastre e organize categorias",
                        icon = Icons.Default.Category,
                        onClick = onNavigateToCategorias,
                        cardBrush = dynamicCardBrush
                    )

                    HomeMenuButton(
                        title = "Exportar Relatório PDF",
                        subtitle = "Salvar resumo do mês em PDF",
                        icon = Icons.Default.PictureAsPdf,
                        onClick = {
                            val acaoGerarPdf = {
                                val transacoesParaPdf = transacoesFiltradas.map {
                                    PdfGenerator.TransacaoItem(it.descricao, it.valor, it.tipo, it.data)
                                }
                                val categoriasGastosList = transacoesFiltradas
                                    .filter { it.tipo == "DESPESA" }
                                    .groupBy { it.descricao }
                                    .map { (categoria, lista) ->
                                        PdfGenerator.CategoriaGastoItem(
                                            nomeCategoria = if (categoria.isBlank()) "Geral" else categoria,
                                            valorTotal = lista.sumOf { it.valor }
                                        )
                                    }
                                val mesNome = selectedCalendar.getDisplayName(Calendar.MONTH, Calendar.LONG, Locale("pt", "BR")) ?: ""
                                PdfGenerator.gerarRelatorioFinanceiro(
                                    context = context,
                                    mesAnoNome = "$mesNome $selectedYear",
                                    receitaTotal = receitaTotal,
                                    despesaTotal = despesaTotal,
                                    saldoTotal = saldoTotal,
                                    transacoes = transacoesParaPdf,
                                    categoriasGastos = categoriasGastosList
                                )
                            }

                            if (activity != null && interstitialAdManager != null) {
                                interstitialAdManager.showAd(activity) { acaoGerarPdf() }
                            } else {
                                acaoGerarPdf()
                            }
                        },
                        cardBrush = dynamicCardBrush
                    )

                    com.polystitch.controlefinanceiro.ui.AdMobBanner(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(top = 2.dp)
                    )
                }
            }
        }

        // Alerta de Falta de Cartão
        if (mostrarAlertaFaltaCartao) {
            AlertDialog(
                onDismissRequest = { mostrarAlertaFaltaCartao = false },
                title = { Text("Nenhum cartão cadastrado") },
                text = { Text("Nenhum cartão cadastrado cadastre em central de cartões") },
                confirmButton = {
                    TextButton(
                        onClick = {
                            mostrarAlertaFaltaCartao = false
                            onNavigateToCartoes()
                        }
                    ) {
                        Text("Ir para Central de Cartões")
                    }
                },
                dismissButton = {
                    TextButton(onClick = { mostrarAlertaFaltaCartao = false }) {
                        Text("Cancelar")
                    }
                }
            )
        }

        // Alerta de Falta de Categoria
        if (mostrarAlertaFaltaCategoria) {
            AlertDialog(
                onDismissRequest = { mostrarAlertaFaltaCategoria = false },
                title = { Text("Nenhuma categoria cadastrada") },
                text = { Text("Nenhuma categoria cadastrada cadastre em gerenciar categorias") },
                confirmButton = {
                    TextButton(
                        onClick = {
                            mostrarAlertaFaltaCategoria = false
                            onNavigateToCategorias()
                        }
                    ) {
                        Text("Ir para Categorias")
                    }
                },
                dismissButton = {
                    TextButton(onClick = { mostrarAlertaFaltaCategoria = false }) {
                        Text("Cancelar")
                    }
                }
            )
        }

        if (showThemeDialog) {
            AlertDialog(
                onDismissRequest = { showThemeDialog = false },
                title = { Text(text = "Escolher Tema do App") },
                text = {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .verticalScroll(rememberScrollState()),
                        verticalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Text(
                            text = "Selecione a identidade visual que melhor combina com o seu estilo:",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.75f),
                            modifier = Modifier.padding(bottom = 4.dp)
                        )

                        val themes = listOf(
                            Triple(AppTheme.AZUL, "Azul Corporativo", "Visual profissional e sóbrio em tons de azul e marinho"),
                            Triple(AppTheme.ROXO, "Roxo / Lavanda", "Estilo moderno e elegante com nuances arroxeadas"),
                            Triple(AppTheme.VERDE, "Verde Menta", "Tema focado em finanças, frescor e equilíbrio visual"),
                            Triple(AppTheme.ESCURSO, "Escuro / Midnight", "Modo escuro profissional para menor fadiga visual"),
                            Triple(AppTheme.AMBAR, "Âmbar & Ouro", "Tons quentes inspirados em luxo e sofisticação"),
                            Triple(AppTheme.RUBI, "Rubi Vibrante", "Cores marcantes e dinâmicas com destaque em magenta"),
                            Triple(AppTheme.OCEANO, "Oceano Turquesa", "Inspiração nas profundezas com verde-água e teal"),
                            Triple(AppTheme.FLORES, "Flores & Rosé", "Tons rosados e delicados com alta legibilidade")
                        )

                        themes.forEach { (themeEnum, nomeTema, legenda) ->
                            val isSelected = currentTheme == themeEnum
                            Button(
                                onClick = { viewModel.setTheme(themeEnum); showThemeDialog = false },
                                modifier = Modifier.fillMaxWidth(),
                                shape = RoundedCornerShape(10.dp),
                                colors = ButtonDefaults.buttonColors(
                                    containerColor = if (isSelected) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.surfaceVariant
                                )
                            ) {
                                Column(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(vertical = 4.dp),
                                    horizontalAlignment = Alignment.Start
                                ) {
                                    Text(
                                        text = nomeTema,
                                        fontWeight = FontWeight.Bold,
                                        fontSize = 13.sp,
                                        color = if (isSelected) MaterialTheme.colorScheme.onPrimary else MaterialTheme.colorScheme.onSurfaceVariant
                                    )
                                    Spacer(modifier = Modifier.height(2.dp))
                                    Text(
                                        text = legenda,
                                        fontSize = 11.sp,
                                        color = if (isSelected) MaterialTheme.colorScheme.onPrimary.copy(alpha = 0.8f) else MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.7f)
                                    )
                                }
                            }
                        }
                    }
                },
                confirmButton = {
                    TextButton(onClick = { showThemeDialog = false }) {
                        Text("Fechar")
                    }
                }
            )
        }

        if (showDialog) {
            AlertDialog(
                onDismissRequest = { showDialog = false },
                title = { Text(text = if (tipoMovimentacao == "RECEITA") "Adicionar Receita" else "Retirar Entrada (Correção)") },
                text = {
                    Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                        OutlinedTextField(
                            value = valorInput,
                            onValueChange = { valorInput = it },
                            label = { Text("Valor (R$)") },
                            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                            singleLine = true
                        )
                    }
                },
                confirmButton = {
                    TextButton(
                        onClick = {
                            val valorParsed = valorInput.replace(",", ".").toDoubleOrNull()
                            if (valorParsed != null && valorParsed > 0) {
                                val targetDate = (selectedCalendar.clone() as Calendar).apply {
                                    set(Calendar.DAY_OF_MONTH, 1)
                                    set(Calendar.HOUR_OF_DAY, 0)
                                    set(Calendar.MINUTE, 0)
                                    set(Calendar.SECOND, 0)
                                    set(Calendar.MILLISECOND, 0)
                                }.timeInMillis

                                val descricaoTx = if (tipoMovimentacao == "RECEITA") "Receita Manual" else "Estorno / Retirada de Entrada"
                                viewModel.adicionarTransacao(descricaoTx, valorParsed, tipoMovimentacao, targetDate)
                                valorInput = ""
                                showDialog = false
                            }
                        }
                    ) { Text("Confirmar") }
                },
                dismissButton = {
                    TextButton(onClick = { valorInput = ""; showDialog = false }) { Text("Cancelar") }
                }
            )
        }
    }
}

@Composable
fun HomeMenuButton(
    title: String,
    subtitle: String,
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    onClick: () -> Unit,
    cardBrush: Brush
) {
    Card(
        onClick = onClick,
        modifier = Modifier
            .fillMaxWidth()
            .shadow(elevation = 3.dp, shape = RoundedCornerShape(12.dp)),
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = Color.Transparent)
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .background(cardBrush)
                .padding(horizontal = 10.dp, vertical = 7.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    Box(
                        modifier = Modifier
                            .size(30.dp)
                            .clip(CircleShape)
                            .background(Color.White.copy(alpha = 0.15f)),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = icon,
                            contentDescription = null,
                            tint = Color.White,
                            modifier = Modifier.size(15.dp)
                        )
                    }
                    Column {
                        Text(
                            text = title,
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color.White
                        )
                        Text(
                            text = subtitle,
                            fontSize = 10.sp,
                            color = Color.White.copy(alpha = 0.75f)
                        )
                    }
                }
                Icon(
                    imageVector = Icons.Default.ChevronRight,
                    contentDescription = null,
                    tint = Color.White.copy(alpha = 0.8f),
                    modifier = Modifier.size(15.dp)
                )
            }
        }
    }
}

@Composable
fun FinanceIndicatorItem(
    title: String,
    value: String,
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    color: Color
) {
    Row(verticalAlignment = Alignment.CenterVertically) {
        Box(
            modifier = Modifier
                .size(26.dp)
                .clip(CircleShape)
                .background(Color.White.copy(alpha = 0.12f)),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector = icon,
                contentDescription = null,
                tint = color,
                modifier = Modifier.size(13.dp)
            )
        }
        Spacer(modifier = Modifier.width(6.dp))
        Column {
            Text(
                text = title,
                color = Color.White.copy(alpha = 0.7f),
                fontSize = 10.sp,
                fontWeight = FontWeight.Medium
            )
            Text(
                text = value,
                color = Color.White,
                fontSize = 11.sp,
                fontWeight = FontWeight.Bold
            )
        }
    }
}