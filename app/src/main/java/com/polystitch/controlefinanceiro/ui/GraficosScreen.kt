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
import java.time.Instant
import java.time.ZoneId
import java.time.YearMonth
import java.time.format.TextStyle
import java.util.Locale

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun GraficosScreen(
    viewModel: FinanceViewModel = viewModel(),
    onNavigateBack: () -> Unit = {}
) {
    val transacoes: List<TransacaoEntity> by viewModel.transacoes.collectAsState(initial = emptyList())
    val despesasFixas: List<DespesaFixaEntity> by viewModel.despesasFixas.collectAsState(initial = emptyList())
    var selectedYearMonth by remember { mutableStateOf(YearMonth.now()) }

    val despesasFiltradas = remember(transacoes, selectedYearMonth) {
        transacoes.filter { tx ->
            if (tx.tipo != "DESPESA") return@filter false
            try {
                val txYearMonth = YearMonth.from(
                    Instant.ofEpochMilli(tx.data)
                        .atZone(ZoneId.systemDefault())
                        .toLocalDate()
                )
                txYearMonth == selectedYearMonth
            } catch (_: Exception) {
                false
            }
        }
    }

    val mesSelecionadoInt = selectedYearMonth.monthValue
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
            val forma = if (tx.formaPagamento.isNullOrBlank()) "Outros" else tx.formaPagamento
            mapa[forma] = (mapa[forma] ?: 0.0) + tx.valor
        }

        if (totalDespesasFixas > 0.0) {
            val chaveFixa = "Despesas Fixas / Recorrentes"
            mapa[chaveFixa] = (mapa[chaveFixa] ?: 0.0) + totalDespesasFixas
        }

        mapa.entries
            .sortedByDescending { it.value }
    }

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

    val cardBackgroundBrush = Brush.horizontalGradient(
        colors = listOf(
            Color(0xFFE9D5FF),
            Color(0xFFF3E8FF)
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
                                color = Color(0xFF1E293B)
                            )
                            Text(
                                text = "Distribuição de despesas por categoria",
                                style = MaterialTheme.typography.bodySmall,
                                color = Color(0xFF475569)
                            )
                        }
                    },
                    navigationIcon = {
                        IconButton(onClick = onNavigateBack) {
                            Icon(
                                imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                                contentDescription = "Voltar",
                                tint = Color(0xFF1E293B)
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
                            ambientColor = Color(0xFF0F172A).copy(alpha = 0.2f),
                            spotColor = Color(0xFF1E3A8A).copy(alpha = 0.3f)
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
                            IconButton(onClick = { selectedYearMonth = selectedYearMonth.minusMonths(1) }) {
                                Icon(
                                    imageVector = Icons.Default.ChevronLeft,
                                    contentDescription = "Mês Anterior",
                                    tint = Color.White
                                )
                            }

                            val mesNome = selectedYearMonth.month.getDisplayName(TextStyle.FULL, Locale.forLanguageTag("pt-BR"))
                                .replaceFirstChar { it.uppercase() }
                            Text(
                                text = "$mesNome ${selectedYearMonth.year}",
                                style = MaterialTheme.typography.bodyLarge,
                                fontWeight = FontWeight.Bold,
                                color = Color.White
                            )

                            IconButton(onClick = { selectedYearMonth = selectedYearMonth.plusMonths(1) }) {
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
                            ambientColor = Color(0xFF0F172A).copy(alpha = 0.2f),
                            spotColor = Color(0xFF1E3A8A).copy(alpha = 0.3f)
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

                if (gastosPorForma.isEmpty()) {
                    Box(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(bottom = 60.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = "Nenhum gasto registrado para este mês.",
                            color = Color(0xFF475569),
                            fontSize = 15.sp,
                            fontWeight = FontWeight.Medium
                        )
                    }
                } else {
                    Text(
                        text = "Gastos por Forma de Pagamento",
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color(0xFF1E293B)
                    )

                    LazyColumn(
                        modifier = Modifier.fillMaxSize(),
                        verticalArrangement = Arrangement.spacedBy(12.dp),
                        contentPadding = PaddingValues(bottom = 24.dp)
                    ) {
                        items(gastosPorForma) { (forma, valor) ->
                            val porcentagem = if (totalDespesasMes > 0) (valor / totalDespesasMes).toFloat() else 0f

                            Card(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .shadow(
                                        elevation = 8.dp,
                                        shape = RoundedCornerShape(16.dp),
                                        ambientColor = Color(0xFF7C3AED).copy(alpha = 0.15f),
                                        spotColor = Color(0xFF6D28D9).copy(alpha = 0.2f)
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
                                                color = Color(0xFF3B0764)
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
                                            color = Color(0xFF7C3AED),
                                            trackColor = Color(0xFFE9D5FF)
                                        )
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}