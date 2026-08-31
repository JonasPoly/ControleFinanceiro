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
import androidx.compose.material.icons.filled.Category
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
import java.util.Calendar
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
    onNavigateToDespesasFixas: () -> Unit = {},
    onNavigateToCategorias: () -> Unit = {}
) {
    val transacoes by viewModel.transacoes.collectAsState(initial = emptyList())

    var selectedCalendar by remember {
        mutableStateOf(Calendar.getInstance().apply {
            set(Calendar.DAY_OF_MONTH, 1)
        })
    }

    val selectedYear = selectedCalendar.get(Calendar.YEAR)
    val selectedMonth = selectedCalendar.get(Calendar.MONTH) // 0 a 11

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

    // Receitas somam entradas normais (RECEITA) e subtraem correções/retiradas manuais de entrada
    val receitaTotal = transacoesFiltradas.filter { it.tipo == "RECEITA" }.sumOf { it.valor } -
            transacoesFiltradas.filter { it.tipo == "RETIRADA_RECEITA" }.sumOf { it.valor }

    // Saídas contabilizam apenas as despesas reais do usuário
    val despesaTotal = transacoesFiltradas.filter { it.tipo == "DESPESA" }.sumOf { it.valor }

    val saldoTotal = receitaTotal - despesaTotal

    var showDialog by remember { mutableStateOf(false) }
    var tipoMovimentacao by remember { mutableStateOf("RECEITA") } // "RECEITA" ou "RETIRADA_RECEITA"
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
                                text = "Controle do Sistema",
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
                                onClick = {
                                    selectedCalendar = (selectedCalendar.clone() as Calendar).apply {
                                        add(Calendar.MONTH, -1)
                                    }
                                },
                                modifier = Modifier.size(34.dp)
                            ) {
                                Icon(
                                    imageVector = Icons.Default.ChevronLeft,
                                    contentDescription = "Mês Anterior",
                                    tint = Color.White
                                )
                            }

                            val mesNome = selectedCalendar.getDisplayName(Calendar.MONTH, Calendar.LONG, Locale("pt", "BR"))
                                ?.replaceFirstChar { it.uppercase() } ?: ""
                            Text(
                                text = "$mesNome $selectedYear",
                                style = MaterialTheme.typography.bodyLarge,
                                fontWeight = FontWeight.Bold,
                                color = Color.White
                            )

                            IconButton(
                                onClick = {
                                    selectedCalendar = (selectedCalendar.clone() as Calendar).apply {
                                        add(Calendar.MONTH, 1)
                                    }
                                },
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
                    Column(
                        verticalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Column(verticalArrangement = Arrangement.spacedBy(2.dp)) {
                                Text(
                                    text = "Saldo Total do Mês",
                                    color = Color.White.copy(alpha = 0.75f),
                                    fontSize = 12.sp,
                                    fontWeight = FontWeight.Medium
                                )
                                Text(
                                    text = "R$ %.2f".format(saldoTotal),
                                    color = Color.White,
                                    fontSize = 30.sp,
                                    fontWeight = FontWeight.ExtraBold
                                )
                            }
                        }

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(10.dp)
                        ) {
                            Button(
                                onClick = {
                                    tipoMovimentacao = "RECEITA"
                                    showDialog = true
                                },
                                modifier = Modifier.weight(1f),
                                colors = ButtonDefaults.buttonColors(
                                    containerColor = Color.White.copy(alpha = 0.2f)
                                ),
                                shape = RoundedCornerShape(12.dp),
                                contentPadding = PaddingValues(vertical = 8.dp)
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Add,
                                    contentDescription = null,
                                    tint = Color.White,
                                    modifier = Modifier.size(16.dp)
                                )
                                Spacer(modifier = Modifier.width(4.dp))
                                Text(text = "Adicionar", color = Color.White, fontSize = 13.sp)
                            }

                            Button(
                                onClick = {
                                    tipoMovimentacao = "RETIRADA_RECEITA"
                                    showDialog = true
                                },
                                modifier = Modifier.weight(1f),
                                colors = ButtonDefaults.buttonColors(
                                    containerColor = Color.White.copy(alpha = 0.2f)
                                ),
                                shape = RoundedCornerShape(12.dp),
                                contentPadding = PaddingValues(vertical = 8.dp)
                            ) {
                                Icon(
                                    imageVector = Icons.Default.ArrowDownward,
                                    contentDescription = null,
                                    tint = Color.White,
                                    modifier = Modifier.size(16.dp)
                                )
                                Spacer(modifier = Modifier.width(4.dp))
                                Text(text = "Retirar", color = Color.White, fontSize = 13.sp)
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

                    HomeMenuButton(
                        title = "Gerenciar Categorias",
                        subtitle = "Cadastre e organize categorias",
                        icon = Icons.Default.Category,
                        onClick = onNavigateToCategorias,
                        blueGradientBrush = blueGradientBrush
                    )
                }
            }
        }

        if (showDialog) {
            AlertDialog(
                onDismissRequest = { showDialog = false },
                title = {
                    Text(text = if (tipoMovimentacao == "RECEITA") "Adicionar Receita" else "Retirar Entrada (Correção)")
                },
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
                                val targetDate = (selectedCalendar.clone() as Calendar).apply {
                                    set(Calendar.DAY_OF_MONTH, 1)
                                    set(Calendar.HOUR_OF_DAY, 0)
                                    set(Calendar.MINUTE, 0)
                                    set(Calendar.SECOND, 0)
                                    set(Calendar.MILLISECOND, 0)
                                }.timeInMillis

                                val descricaoTx = if (tipoMovimentacao == "RECEITA") "Receita Manual" else "Estorno / Retirada de Entrada"

                                viewModel.adicionarTransacao(
                                    descricao = descricaoTx,
                                    valor = valorParsed,
                                    tipo = tipoMovimentacao,
                                    data = targetDate
                                )
                                valorInput = ""
                                showDialog = false
                            }
                        }
                    ) {
                        Text("Confirmar")
                    }
                },
                dismissButton = {
                    TextButton(onClick = {
                        valorInput = ""
                        showDialog = false
                    }) {
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