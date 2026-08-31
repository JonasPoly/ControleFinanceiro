package com.polystitch.controlefinanceiro.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.ArrowDownward
import androidx.compose.material.icons.filled.ArrowUpward
import androidx.compose.material.icons.filled.ChevronLeft
import androidx.compose.material.icons.filled.ChevronRight
import androidx.compose.material.icons.filled.CreditCard
import androidx.compose.material.icons.filled.EventRepeat
import androidx.compose.material.icons.filled.ListAlt
import androidx.compose.material.icons.filled.Payments
import androidx.compose.material.icons.filled.PieChart
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.polystitch.controlefinanceiro.viewmodel.FinanceViewModel
import java.time.Instant
import java.time.ZoneId
import java.time.YearMonth
import java.time.format.TextStyle
import java.util.Locale

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(
    viewModel: FinanceViewModel = viewModel(),
    onNavigateToCartoes: () -> Unit = {},
    onNavigateToAdicionarDespesaCartao: () -> Unit = {},
    onNavigateToAdicionarDespesaVista: () -> Unit = {},
    onNavigateToConsultarDespesas: () -> Unit = {},
    onNavigateToGraficos: () -> Unit = {},
    onNavigateToDespesasFixas: () -> Unit = {}
) {
    val transacoes by viewModel.transacoes.collectAsState(initial = emptyList())

    var selectedYearMonth by remember { mutableStateOf(YearMonth.now()) }

    val transacoesFiltradas = remember(transacoes, selectedYearMonth) {
        transacoes.filter { tx ->
            try {
                val txYearMonth = YearMonth.from(
                    Instant.ofEpochMilli(tx.data)
                        .atZone(ZoneId.systemDefault())
                        .toLocalDate()
                )
                txYearMonth == selectedYearMonth
            } catch (_: Exception) {
                true
            }
        }
    }

    val receitaTotal = transacoesFiltradas.filter { it.tipo == "RECEITA" }.sumOf { it.valor }
    val despesaTotal = transacoesFiltradas.filter { it.tipo == "DESPESA" }.sumOf { it.valor }
    val saldoTotal = receitaTotal - despesaTotal

    var showDialog by remember { mutableStateOf(false) }
    var valorInput by remember { mutableStateOf("") }

    val gradientStart = Color(0xFF0F172A)
    val gradientEnd = Color(0xFF3B82F6)
    val successColor = Color(0xFF34D399)
    val expenseColor = Color(0xFFF87171)

    val screenBackgroundBrush = Brush.verticalGradient(
        colors = listOf(
            Color(0xFFFFFFFF),
            Color(0xFFF1F5F9),
            Color(0xFF93C5FD),
            Color(0xFF3B82F6)
        )
    )

    val blueGradientBrush = Brush.horizontalGradient(
        colors = listOf(Color(0xFF1E293B), Color(0xFF2563EB))
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
                                style = MaterialTheme.typography.titleMedium,
                                fontWeight = FontWeight.Bold,
                                color = Color(0xFF1E293B)
                            )
                            Text(
                                text = "Polystitch Finance",
                                style = MaterialTheme.typography.bodySmall,
                                color = Color(0xFF475569)
                            )
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
                    .padding(horizontal = 18.dp, vertical = 6.dp),
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                // Seletor de Mês intermediário
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .shadow(
                            elevation = 6.dp,
                            shape = RoundedCornerShape(14.dp),
                            ambientColor = Color(0xFF0F172A).copy(alpha = 0.18f),
                            spotColor = Color(0xFF1E3A8A).copy(alpha = 0.25f)
                        ),
                    shape = RoundedCornerShape(14.dp),
                    colors = CardDefaults.cardColors(containerColor = Color.Transparent)
                ) {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .background(blueGradientBrush)
                            .padding(horizontal = 8.dp, vertical = 3.dp)
                    ) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            IconButton(
                                onClick = { selectedYearMonth = selectedYearMonth.minusMonths(1) },
                                modifier = Modifier.size(34.dp)
                            ) {
                                Icon(
                                    imageVector = Icons.Default.ChevronLeft,
                                    contentDescription = "Mês Anterior",
                                    tint = Color.White
                                )
                            }

                            val mesNome = selectedYearMonth.month.getDisplayName(TextStyle.FULL, Locale("pt", "BR"))
                                .replaceFirstChar { it.uppercase() }
                            Text(
                                text = "$mesNome ${selectedYearMonth.year}",
                                style = MaterialTheme.typography.bodyLarge,
                                fontWeight = FontWeight.Bold,
                                color = Color.White
                            )

                            IconButton(
                                onClick = { selectedYearMonth = selectedYearMonth.plusMonths(1) },
                                modifier = Modifier.size(34.dp)
                            ) {
                                Icon(
                                    imageVector = Icons.Default.ChevronRight,
                                    contentDescription = "Próximo Mês",
                                    tint = Color.White
                                )
                            }
                        }
                    }
                }

                // Card Principal de Saldo intermediário
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .shadow(
                            elevation = 12.dp,
                            shape = RoundedCornerShape(24.dp),
                            ambientColor = Color(0xFF0F172A).copy(alpha = 0.22f),
                            spotColor = Color(0xFF1E3A8A).copy(alpha = 0.3f)
                        )
                        .clip(RoundedCornerShape(24.dp))
                        .background(
                            brush = Brush.horizontalGradient(
                                colors = listOf(gradientStart, gradientEnd)
                            )
                        )
                        .padding(18.dp)
                ) {
                    Column {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = "Saldo Total do Mês",
                                color = Color.White.copy(alpha = 0.75f),
                                fontSize = 12.sp,
                                fontWeight = FontWeight.Medium
                            )

                            IconButton(
                                onClick = { showDialog = true },
                                modifier = Modifier
                                    .size(32.dp)
                                    .clip(CircleShape)
                                    .background(Color.White.copy(alpha = 0.15f))
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Add,
                                    contentDescription = "Adicionar Receita",
                                    tint = Color.White,
                                    modifier = Modifier.size(18.dp)
                                )
                            }
                        }

                        Spacer(modifier = Modifier.height(4.dp))

                        Text(
                            text = "R$ %.2f".format(saldoTotal),
                            color = Color.White,
                            fontSize = 30.sp,
                            fontWeight = FontWeight.ExtraBold
                        )

                        Spacer(modifier = Modifier.height(14.dp))

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

                // Botões de Ação com tamanho equilibrado
                Column(
                    verticalArrangement = Arrangement.spacedBy(8.dp),
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(bottom = 10.dp)
                ) {
                    HomeMenuButton(
                        title = "Incluir Despesa à Vista",
                        subtitle = "Pix, Débito ou Dinheiro",
                        icon = Icons.Default.Payments,
                        onClick = onNavigateToAdicionarDespesaVista,
                        blueGradientBrush = blueGradientBrush
                    )

                    HomeMenuButton(
                        title = "Incluir Despesa no Cartão",
                        subtitle = "Insira compras na fatura",
                        icon = Icons.Default.CreditCard,
                        onClick = onNavigateToAdicionarDespesaCartao,
                        blueGradientBrush = blueGradientBrush
                    )

                    HomeMenuButton(
                        title = "Despesas Fixas / Recorrentes",
                        subtitle = "Gerencie contas mensais automáticas",
                        icon = Icons.Default.EventRepeat,
                        onClick = onNavigateToDespesasFixas,
                        blueGradientBrush = blueGradientBrush
                    )

                    HomeMenuButton(
                        title = "Consultar Despesas",
                        subtitle = "Visualize todas as despesas",
                        icon = Icons.Default.ListAlt,
                        onClick = onNavigateToConsultarDespesas,
                        blueGradientBrush = blueGradientBrush
                    )

                    HomeMenuButton(
                        title = "Análise Gráfica",
                        subtitle = "Gráficos de gastos e despesas",
                        icon = Icons.Default.PieChart,
                        onClick = onNavigateToGraficos,
                        blueGradientBrush = blueGradientBrush
                    )

                    HomeMenuButton(
                        title = "Central de Cartões",
                        subtitle = "Gerencie cartões e faturas",
                        icon = Icons.Default.CreditCard,
                        onClick = onNavigateToCartoes,
                        blueGradientBrush = blueGradientBrush
                    )
                }
            }
        }

        if (showDialog) {
            AlertDialog(
                onDismissRequest = { showDialog = false },
                title = { Text("Adicionar Receita") },
                text = {
                    Column(
                        verticalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
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
                                val targetDate = selectedYearMonth.atDay(1)
                                    .atStartOfDay(ZoneId.systemDefault())
                                    .toInstant()
                                    .toEpochMilli()

                                viewModel.adicionarTransacao(
                                    descricao = "Receita",
                                    valor = valorParsed,
                                    tipo = "RECEITA",
                                    data = targetDate
                                )
                                valorInput = ""
                                showDialog = false
                            }
                        }
                    ) {
                        Text("Adicionar")
                    }
                },
                dismissButton = {
                    TextButton(onClick = { showDialog = false }) {
                        Text("Cancelar")
                    }
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
    blueGradientBrush: Brush
) {
    Card(
        onClick = onClick,
        modifier = Modifier
            .fillMaxWidth()
            .shadow(
                elevation = 6.dp,
                shape = RoundedCornerShape(16.dp),
                ambientColor = Color(0xFF0F172A).copy(alpha = 0.18f),
                spotColor = Color(0xFF1E3A8A).copy(alpha = 0.25f)
            ),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(
            containerColor = Color.Transparent
        )
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .background(blueGradientBrush)
                .padding(horizontal = 14.dp, vertical = 12.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    Box(
                        modifier = Modifier
                            .size(38.dp)
                            .clip(CircleShape)
                            .background(Color.White.copy(alpha = 0.15f)),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = icon,
                            contentDescription = null,
                            tint = Color.White,
                            modifier = Modifier.size(19.dp)
                        )
                    }
                    Column {
                        Text(
                            text = title,
                            fontSize = 14.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color.White
                        )
                        Text(
                            text = subtitle,
                            fontSize = 11.sp,
                            color = Color.White.copy(alpha = 0.75f)
                        )
                    }
                }
                Icon(
                    imageVector = Icons.Default.ChevronRight,
                    contentDescription = null,
                    tint = Color.White.copy(alpha = 0.8f),
                    modifier = Modifier.size(18.dp)
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
                .size(32.dp)
                .clip(CircleShape)
                .background(Color.White.copy(alpha = 0.12f)),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector = icon,
                contentDescription = null,
                tint = color,
                modifier = Modifier.size(15.dp)
            )
        }
        Spacer(modifier = Modifier.width(8.dp))
        Column {
            Text(
                text = title,
                color = Color.White.copy(alpha = 0.7f),
                fontSize = 11.sp,
                fontWeight = FontWeight.Medium
            )
            Text(
                text = value,
                color = Color.White,
                fontSize = 13.sp,
                fontWeight = FontWeight.Bold
            )
        }
    }
}