package com.polystitch.controlefinanceiro.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.ArrowDownward
import androidx.compose.material.icons.filled.ChevronLeft
import androidx.compose.material.icons.filled.ChevronRight
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.EventRepeat
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
import com.polystitch.controlefinanceiro.viewmodel.FinanceViewModel
import java.time.Instant
import java.time.ZoneId
import java.time.YearMonth
import java.time.format.DateTimeFormatter
import java.time.format.TextStyle
import java.util.Locale

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ConsultarDespesasScreen(
    viewModel: FinanceViewModel = viewModel(),
    onNavigateBack: () -> Unit = {}
) {
    val transacoes by viewModel.transacoes.collectAsState()
    val despesasFixas by viewModel.despesasFixas.collectAsState()

    var selectedYearMonth by remember { mutableStateOf(YearMonth.now()) }

    // Filtra transações (à vista e cartão) do mês selecionado
    val despesasTransacoesFiltradas = remember(transacoes, selectedYearMonth) {
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
        }.sortedByDescending { tx -> tx.data }
    }

    // Filtra despesas fixas ativas no mês selecionado (convertendo string de meses "1,2,3" em lista de Int)
    val mesSelecionadoInt = selectedYearMonth.monthValue
    val despesasFixasDoMes = remember(despesasFixas, mesSelecionadoInt) {
        despesasFixas.filter { fixa: DespesaFixaEntity ->
            val mesesList = fixa.mesesAtivos
                .split(",")
                .mapNotNull { it.trim().toIntOrNull() }
            mesesList.contains(mesSelecionadoInt)
        }
    }

    val dateFormatter = remember { DateTimeFormatter.ofPattern("dd/MM/yyyy", Locale("pt", "BR")) }

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

    val semNenhumaDespesa = despesasTransacoesFiltradas.isEmpty() && despesasFixasDoMes.isEmpty()

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
                                text = "Consultar Despesas",
                                style = MaterialTheme.typography.titleMedium,
                                fontWeight = FontWeight.Bold,
                                color = Color(0xFF1E293B)
                            )
                            Text(
                                text = "À vista, cartão e fixas filtradas por mês",
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

                if (semNenhumaDespesa) {
                    Box(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(bottom = 60.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = "Nenhuma despesa para este mês.",
                            color = Color(0xFF475569),
                            fontSize = 15.sp,
                            fontWeight = FontWeight.Medium
                        )
                    }
                } else {
                    LazyColumn(
                        modifier = Modifier.fillMaxSize(),
                        verticalArrangement = Arrangement.spacedBy(12.dp),
                        contentPadding = PaddingValues(bottom = 24.dp)
                    ) {
                        if (despesasFixasDoMes.isNotEmpty()) {
                            item {
                                Text(
                                    text = "Despesas Fixas / Recorrentes",
                                    fontSize = 14.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = Color(0xFF1E293B),
                                    modifier = Modifier.padding(top = 4.dp, bottom = 2.dp)
                                )
                            }

                            items(despesasFixasDoMes, key = { "fixa_${it.id}" }) { fixa ->
                                Card(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .shadow(
                                            elevation = 8.dp,
                                            shape = RoundedCornerShape(20.dp),
                                            ambientColor = Color(0xFF7C3AED).copy(alpha = 0.15f),
                                            spotColor = Color(0xFF6D28D9).copy(alpha = 0.2f)
                                        ),
                                    shape = RoundedCornerShape(20.dp),
                                    colors = CardDefaults.cardColors(containerColor = Color.Transparent)
                                ) {
                                    Box(
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .background(cardBackgroundBrush)
                                            .padding(18.dp)
                                    ) {
                                        Row(
                                            modifier = Modifier.fillMaxWidth(),
                                            horizontalArrangement = Arrangement.SpaceBetween,
                                            verticalAlignment = Alignment.CenterVertically
                                        ) {
                                            Row(
                                                verticalAlignment = Alignment.CenterVertically,
                                                horizontalArrangement = Arrangement.spacedBy(14.dp),
                                                modifier = Modifier.weight(1f)
                                            ) {
                                                Box(
                                                    modifier = Modifier
                                                        .size(42.dp)
                                                        .clip(CircleShape)
                                                        .background(Color(0xFF9333EA).copy(alpha = 0.15f)),
                                                    contentAlignment = Alignment.Center
                                                ) {
                                                    Icon(
                                                        imageVector = Icons.Default.EventRepeat,
                                                        contentDescription = null,
                                                        tint = Color(0xFF7C3AED),
                                                        modifier = Modifier.size(22.dp)
                                                    )
                                                }

                                                Column(
                                                    verticalArrangement = Arrangement.spacedBy(3.dp)
                                                ) {
                                                    Text(
                                                        text = fixa.descricao,
                                                        fontWeight = FontWeight.Bold,
                                                        fontSize = 16.sp,
                                                        color = Color(0xFF3B0764)
                                                    )
                                                    Text(
                                                        text = "•  Despesa Fixa / Recorrente",
                                                        fontSize = 12.sp,
                                                        color = Color(0xFF6B21A8),
                                                        fontWeight = FontWeight.Medium
                                                    )
                                                }
                                            }

                                            Row(
                                                verticalAlignment = Alignment.CenterVertically,
                                                horizontalArrangement = Arrangement.spacedBy(12.dp)
                                            ) {
                                                Text(
                                                    text = "R$ %.2f".format(fixa.valor),
                                                    fontWeight = FontWeight.Bold,
                                                    fontSize = 15.sp,
                                                    color = Color(0xFFEF4444)
                                                )
                                            }
                                        }
                                    }
                                }
                            }
                        }

                        if (despesasTransacoesFiltradas.isNotEmpty()) {
                            item {
                                Text(
                                    text = "Despesas Avulsas e de Cartão",
                                    fontSize = 14.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = Color(0xFF1E293B),
                                    modifier = Modifier.padding(top = 8.dp, bottom = 2.dp)
                                )
                            }

                            items(despesasTransacoesFiltradas, key = { it.id }) { despesa ->
                                val dataFormatada = try {
                                    Instant.ofEpochMilli(despesa.data)
                                        .atZone(ZoneId.systemDefault())
                                        .format(dateFormatter)
                                } catch (_: Exception) {
                                    ""
                                }

                                Card(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .shadow(
                                            elevation = 8.dp,
                                            shape = RoundedCornerShape(20.dp),
                                            ambientColor = Color(0xFF7C3AED).copy(alpha = 0.15f),
                                            spotColor = Color(0xFF6D28D9).copy(alpha = 0.2f)
                                        ),
                                    shape = RoundedCornerShape(20.dp),
                                    colors = CardDefaults.cardColors(containerColor = Color.Transparent)
                                ) {
                                    Box(
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .background(cardBackgroundBrush)
                                            .padding(18.dp)
                                    ) {
                                        Row(
                                            modifier = Modifier.fillMaxWidth(),
                                            horizontalArrangement = Arrangement.SpaceBetween,
                                            verticalAlignment = Alignment.CenterVertically
                                        ) {
                                            Row(
                                                verticalAlignment = Alignment.CenterVertically,
                                                horizontalArrangement = Arrangement.spacedBy(14.dp),
                                                modifier = Modifier.weight(1f)
                                            ) {
                                                Box(
                                                    modifier = Modifier
                                                        .size(42.dp)
                                                        .clip(CircleShape)
                                                        .background(Color(0xFF9333EA).copy(alpha = 0.15f)),
                                                    contentAlignment = Alignment.Center
                                                ) {
                                                    Icon(
                                                        imageVector = Icons.Default.ArrowDownward,
                                                        contentDescription = null,
                                                        tint = Color(0xFF7C3AED),
                                                        modifier = Modifier.size(22.dp)
                                                    )
                                                }

                                                Column(
                                                    verticalArrangement = Arrangement.spacedBy(3.dp)
                                                ) {
                                                    Text(
                                                        text = despesa.descricao,
                                                        fontWeight = FontWeight.Bold,
                                                        fontSize = 16.sp,
                                                        color = Color(0xFF3B0764)
                                                    )
                                                    Row(
                                                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                                                    ) {
                                                        if (dataFormatada.isNotBlank()) {
                                                            Text(
                                                                text = dataFormatada,
                                                                fontSize = 12.sp,
                                                                color = Color(0xFF6B21A8)
                                                            )
                                                        }
                                                        val forma = despesa.formaPagamento
                                                        if (!forma.isNullOrBlank()) {
                                                            Text(
                                                                text = "•  $forma",
                                                                fontSize = 12.sp,
                                                                color = Color(0xFF6B21A8),
                                                                fontWeight = FontWeight.Medium
                                                            )
                                                        }
                                                    }
                                                }
                                            }

                                            Row(
                                                verticalAlignment = Alignment.CenterVertically,
                                                horizontalArrangement = Arrangement.spacedBy(12.dp)
                                            ) {
                                                Text(
                                                    text = "R$ %.2f".format(despesa.valor),
                                                    fontWeight = FontWeight.Bold,
                                                    fontSize = 15.sp,
                                                    color = Color(0xFFEF4444)
                                                )

                                                IconButton(
                                                    onClick = { viewModel.deletarTransacao(despesa.id) },
                                                    modifier = Modifier.size(36.dp)
                                                ) {
                                                    Icon(
                                                        imageVector = Icons.Default.Delete,
                                                        contentDescription = "Excluir",
                                                        tint = Color(0xFFDC2626),
                                                        modifier = Modifier.size(18.dp)
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
        }
    }
}