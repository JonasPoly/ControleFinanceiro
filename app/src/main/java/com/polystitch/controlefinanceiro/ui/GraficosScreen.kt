package com.polystitch.controlefinanceiro.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.ChevronLeft
import androidx.compose.material.icons.filled.ChevronRight
import androidx.compose.material.icons.filled.DonutLarge
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.polystitch.controlefinanceiro.data.entity.DespesaFixaEntity
import com.polystitch.controlefinanceiro.data.entity.TransacaoEntity
import com.polystitch.controlefinanceiro.viewmodel.FinanceViewModel
import java.util.Calendar
import java.util.Locale

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun GraficosScreen(
    viewModel: FinanceViewModel = viewModel(),
    onNavigateBack: () -> Unit = {}
) {
    // Cores dinâmicas retiradas do tema atual do Material 3
    val primaryColor = MaterialTheme.colorScheme.primary
    val secondaryColor = MaterialTheme.colorScheme.secondary

    val transacoes: List<TransacaoEntity> by viewModel.transacoes.collectAsState(initial = emptyList())
    val despesasFixas: List<DespesaFixaEntity> by viewModel.despesasFixas.collectAsState(initial = emptyList())
    val categorias by viewModel.categorias.collectAsState(initial = emptyList())

    // Usando Calendar para gerenciar mês e ano compatível com qualquer minSdk
    var selectedCalendar by remember {
        mutableStateOf(Calendar.getInstance().apply {
            set(Calendar.DAY_OF_MONTH, 1)
        })
    }

    val selectedYear = selectedCalendar.get(Calendar.YEAR)
    val selectedMonth = selectedCalendar.get(Calendar.MONTH) // 0 a 11

    val despesasFiltradas = remember(transacoes, selectedYear, selectedMonth) {
        transacoes.filter { tx ->
            if (tx.tipo != "DESPESA") return@filter false
            try {
                val calTx = Calendar.getInstance().apply { timeInMillis = tx.data }
                calTx.get(Calendar.YEAR) == selectedYear && calTx.get(Calendar.MONTH) == selectedMonth
            } catch (_: Exception) {
                false
            }
        }
    }

    val mesSelecionadoInt = selectedMonth + 1 // 1 a 12
    val despesasFixasDoMes = despesasFixas.filter { despesa ->
        val mesesList = despesa.mesesAtivos
            .split(",")
            .mapNotNull { it.trim().toIntOrNull() }
        mesesList.contains(mesSelecionadoInt)
    }

    val totalDespesasTransacoes = despesasFiltradas.sumOf { it.valor }
    val totalDespesasFixas = despesasFixasDoMes.sumOf { it.valor }
    val totalDespesasMes = totalDespesasTransacoes + totalDespesasFixas

    val gastosPorForma = remember(despesasFiltradas, despesasFixasDoMes) {
        val mapa = mutableMapOf<String, Double>()

        despesasFiltradas.forEach { tx ->
            val forma = if (tx.formaPagamento.isBlank()) "Outros" else tx.formaPagamento
            mapa[forma] = (mapa[forma] ?: 0.0) + tx.valor
        }

        if (totalDespesasFixas > 0.0) {
            val chaveFixa = "Despesas Fixas / Recorrentes"
            mapa[chaveFixa] = (mapa[chaveFixa] ?: 0.0) + totalDespesasFixas
        }

        mapa.entries.sortedByDescending { it.value }
    }

    val gastosPorCategoria = remember(despesasFiltradas, despesasFixasDoMes, categorias) {
        val mapa = mutableMapOf<String, Double>()

        despesasFiltradas.forEach { tx ->
            val nomeCategoria = categorias.find { it.id == tx.categoriaId }?.nome ?: "Outros"
            mapa[nomeCategoria] = (mapa[nomeCategoria] ?: 0.0) + tx.valor
        }

        if (totalDespesasFixas > 0.0) {
            val chaveFixa = "Despesas Fixas"
            mapa[chaveFixa] = (mapa[chaveFixa] ?: 0.0) + totalDespesasFixas
        }

        mapa.entries.sortedByDescending { it.value }
    }

    val screenBackgroundBrush = Brush.verticalGradient(
        colors = listOf(
            MaterialTheme.colorScheme.background,
            MaterialTheme.colorScheme.surface,
            primaryColor.copy(alpha = 0.2f),
            primaryColor.copy(alpha = 0.5f)
        )
    )

    val blueGradientBrush = Brush.horizontalGradient(
        colors = listOf(
            primaryColor,
            secondaryColor
        )
    )

    val cardBackgroundBrush = Brush.horizontalGradient(
        colors = listOf(
            primaryColor.copy(alpha = 0.15f),
            secondaryColor.copy(alpha = 0.1f)
        )
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
                                text = "Análise Gráfica",
                                style = MaterialTheme.typography.titleMedium,
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.onBackground
                            )
                            Text(
                                text = "Distribuição de despesas",
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.7f)
                            )
                        }
                    },
                    navigationIcon = {
                        IconButton(onClick = onNavigateBack) {
                            Icon(
                                imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                                contentDescription = "Voltar",
                                tint = MaterialTheme.colorScheme.onBackground
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
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues)
                    .padding(horizontal = 20.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                Spacer(modifier = Modifier.height(4.dp))

                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .shadow(
                            elevation = 8.dp,
                            shape = RoundedCornerShape(16.dp),
                            ambientColor = primaryColor.copy(alpha = 0.2f),
                            spotColor = secondaryColor.copy(alpha = 0.3f)
                        ),
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(containerColor = Color.Transparent)
                ) {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .background(blueGradientBrush)
                            .padding(horizontal = 12.dp, vertical = 4.dp)
                    ) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            IconButton(onClick = {
                                selectedCalendar = (selectedCalendar.clone() as Calendar).apply {
                                    add(Calendar.MONTH, -1)
                                }
                            }) {
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

                            IconButton(onClick = {
                                selectedCalendar = (selectedCalendar.clone() as Calendar).apply {
                                    add(Calendar.MONTH, 1)
                                }
                            }) {
                                Icon(
                                    imageVector = Icons.Default.ChevronRight,
                                    contentDescription = "Próximo Mês",
                                    tint = Color.White
                                )
                            }
                        }
                    }
                }

                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .shadow(
                            elevation = 12.dp,
                            shape = RoundedCornerShape(24.dp),
                            ambientColor = primaryColor.copy(alpha = 0.2f),
                            spotColor = secondaryColor.copy(alpha = 0.3f)
                        ),
                    shape = RoundedCornerShape(24.dp),
                    colors = CardDefaults.cardColors(containerColor = Color.Transparent)
                ) {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .background(blueGradientBrush)
                            .padding(20.dp)
                    ) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                                Text(
                                    text = "Total de Saídas no Mês",
                                    color = Color.White.copy(alpha = 0.75f),
                                    fontSize = 12.sp,
                                    fontWeight = FontWeight.Medium
                                )
                                Text(
                                    text = "R$ %.2f".format(totalDespesasMes),
                                    color = Color.White,
                                    fontSize = 28.sp,
                                    fontWeight = FontWeight.ExtraBold
                                )
                            }

                            Box(
                                modifier = Modifier
                                    .size(48.dp)
                                    .clip(CircleShape)
                                    .background(Color.White.copy(alpha = 0.15f)),
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(
                                    imageVector = Icons.Default.DonutLarge,
                                    contentDescription = null,
                                    tint = Color.White,
                                    modifier = Modifier.size(26.dp)
                                )
                            }
                        }
                    }
                }

                Spacer(modifier = Modifier.height(4.dp))

                if (gastosPorForma.isEmpty() && gastosPorCategoria.isEmpty()) {
                    Box(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(bottom = 60.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = "Nenhum gasto registrado para este mês.",
                            color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.7f),
                            fontSize = 15.sp,
                            fontWeight = FontWeight.Medium
                        )
                    }
                } else {
                    LazyColumn(
                        modifier = Modifier.fillMaxSize(),
                        verticalArrangement = Arrangement.spacedBy(16.dp),
                        contentPadding = PaddingValues(bottom = 24.dp)
                    ) {
                        if (gastosPorCategoria.isNotEmpty()) {
                            item {
                                Text(
                                    text = "Gastos por Categoria",
                                    fontSize = 14.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = MaterialTheme.colorScheme.onBackground
                                )
                            }

                            items(gastosPorCategoria, key = { it.key }) { entry ->
                                val categoria = entry.key
                                val valor = entry.value
                                val porcentagem = if (totalDespesasMes > 0) (valor / totalDespesasMes).toFloat() else 0f

                                Card(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .shadow(
                                            elevation = 8.dp,
                                            shape = RoundedCornerShape(16.dp),
                                            ambientColor = primaryColor.copy(alpha = 0.15f),
                                            spotColor = secondaryColor.copy(alpha = 0.2f)
                                        ),
                                    shape = RoundedCornerShape(16.dp),
                                    colors = CardDefaults.cardColors(containerColor = Color.Transparent)
                                ) {
                                    Box(
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .background(cardBackgroundBrush)
                                            .padding(16.dp)
                                    ) {
                                        Column(
                                            verticalArrangement = Arrangement.spacedBy(10.dp)
                                        ) {
                                            Row(
                                                modifier = Modifier.fillMaxWidth(),
                                                horizontalArrangement = Arrangement.SpaceBetween,
                                                verticalAlignment = Alignment.CenterVertically
                                            ) {
                                                Text(
                                                    text = categoria,
                                                    fontWeight = FontWeight.Bold,
                                                    fontSize = 15.sp,
                                                    color = primaryColor,
                                                    modifier = Modifier.weight(1f)
                                                )
                                                Text(
                                                    text = "R$ %.2f  (%.1f%%)".format(valor, porcentagem * 100),
                                                    fontWeight = FontWeight.Bold,
                                                    fontSize = 14.sp,
                                                    color = Color(0xFFEF4444)
                                                )
                                            }

                                            LinearProgressIndicator(
                                                progress = { porcentagem },
                                                modifier = Modifier
                                                    .fillMaxWidth()
                                                    .height(8.dp)
                                                    .clip(RoundedCornerShape(4.dp)),
                                                color = primaryColor,
                                                trackColor = primaryColor.copy(alpha = 0.2f)
                                            )
                                        }
                                    }
                                }
                            }
                        }

                        if (gastosPorForma.isNotEmpty()) {
                            item {
                                Spacer(modifier = Modifier.height(4.dp))
                                Text(
                                    text = "Gastos por Forma de Pagamento",
                                    fontSize = 14.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = MaterialTheme.colorScheme.onBackground
                                )
                            }

                            items(gastosPorForma, key = { it.key }) { entry ->
                                val forma = entry.key
                                val valor = entry.value
                                val porcentagem = if (totalDespesasMes > 0) (valor / totalDespesasMes).toFloat() else 0f

                                Card(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .shadow(
                                            elevation = 8.dp,
                                            shape = RoundedCornerShape(16.dp),
                                            ambientColor = primaryColor.copy(alpha = 0.15f),
                                            spotColor = secondaryColor.copy(alpha = 0.2f)
                                        ),
                                    shape = RoundedCornerShape(16.dp),
                                    colors = CardDefaults.cardColors(containerColor = Color.Transparent)
                                ) {
                                    Box(
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .background(cardBackgroundBrush)
                                            .padding(16.dp)
                                    ) {
                                        Column(
                                            verticalArrangement = Arrangement.spacedBy(10.dp)
                                        ) {
                                            Row(
                                                modifier = Modifier.fillMaxWidth(),
                                                horizontalArrangement = Arrangement.SpaceBetween,
                                                verticalAlignment = Alignment.CenterVertically
                                            ) {
                                                Text(
                                                    text = forma,
                                                    fontWeight = FontWeight.Bold,
                                                    fontSize = 15.sp,
                                                    color = primaryColor,
                                                    modifier = Modifier.weight(1f)
                                                )
                                                Text(
                                                    text = "R$ %.2f  (%.1f%%)".format(valor, porcentagem * 100),
                                                    fontWeight = FontWeight.Bold,
                                                    fontSize = 14.sp,
                                                    color = Color(0xFFEF4444)
                                                )
                                            }

                                            LinearProgressIndicator(
                                                progress = { porcentagem },
                                                modifier = Modifier
                                                    .fillMaxWidth()
                                                    .height(8.dp)
                                                    .clip(RoundedCornerShape(4.dp)),
                                                color = primaryColor,
                                                trackColor = primaryColor.copy(alpha = 0.2f)
                                            )
                                        }
                                    }
                                }
                            }
                        }

                        // Banner do AdMob integrado na rolagem da LazyColumn
                        item {
                            com.polystitch.controlefinanceiro.ui.AdMobBanner(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(top = 8.dp, bottom = 12.dp)
                            )
                        }
                    }
                }
            }
        }
    }
}