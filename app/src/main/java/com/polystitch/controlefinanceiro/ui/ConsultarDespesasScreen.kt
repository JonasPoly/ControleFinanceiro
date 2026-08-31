package com.polystitch.controlefinanceiro.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
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

    // Cores dinâmicas retiradas do tema atual do Material 3
    val primaryColor = MaterialTheme.colorScheme.primary
    val secondaryColor = MaterialTheme.colorScheme.secondary

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
                                color = MaterialTheme.colorScheme.onBackground
                            )
                            Text(
                                text = "À vista, cartão e fixas filtradas por mês",
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
            val scrollState = rememberScrollState()

            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues)
                    .verticalScroll(scrollState)
                    .padding(horizontal = 20.dp, vertical = 8.dp),
                verticalArrangement = Arrangement.spacedBy(14.dp)
            ) {
                Spacer(modifier = Modifier.height(2.dp))

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

                Spacer(modifier = Modifier.height(2.dp))

                if (semNenhumaDespesa) {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(top = 20.dp, bottom = 20.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = "Nenhuma despesa para este mês.",
                            color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.7f),
                            fontSize = 15.sp,
                            fontWeight = FontWeight.Medium
                        )
                    }
                } else {
                    Column(
                        modifier = Modifier.fillMaxWidth(),
                        verticalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        if (despesasFixasDoMes.isNotEmpty()) {
                            Text(
                                text = "Despesas Fixas / Recorrentes",
                                fontSize = 14.sp,
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.onBackground,
                                modifier = Modifier.padding(top = 4.dp, bottom = 2.dp)
                            )

                            despesasFixasDoMes.forEach { fixa ->
                                Card(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .shadow(
                                            elevation = 8.dp,
                                            shape = RoundedCornerShape(20.dp),
                                            ambientColor = primaryColor.copy(alpha = 0.15f),
                                            spotColor = secondaryColor.copy(alpha = 0.2f)
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
                                                        .background(primaryColor.copy(alpha = 0.15f)),
                                                    contentAlignment = Alignment.Center
                                                ) {
                                                    Icon(
                                                        imageVector = Icons.Default.EventRepeat,
                                                        contentDescription = null,
                                                        tint = primaryColor,
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
                                                        color = primaryColor
                                                    )
                                                    Text(
                                                        text = "•  Despesa Fixa / Recorrente",
                                                        fontSize = 12.sp,
                                                        color = secondaryColor,
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
                            Text(
                                text = "Despesas Avulsas e de Cartão",
                                fontSize = 14.sp,
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.onBackground,
                                modifier = Modifier.padding(top = 8.dp, bottom = 2.dp)
                            )

                            despesasTransacoesFiltradas.forEach { despesa ->
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
                                            ambientColor = primaryColor.copy(alpha = 0.15f),
                                            spotColor = secondaryColor.copy(alpha = 0.2f)
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
                                                        .background(primaryColor.copy(alpha = 0.15f)),
                                                    contentAlignment = Alignment.Center
                                                ) {
                                                    Icon(
                                                        imageVector = Icons.Default.ArrowDownward,
                                                        contentDescription = null,
                                                        tint = primaryColor,
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
                                                        color = primaryColor
                                                    )
                                                    Row(
                                                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                                                    ) {
                                                        if (dataFormatada.isNotBlank()) {
                                                            Text(
                                                                text = dataFormatada,
                                                                fontSize = 12.sp,
                                                                color = secondaryColor
                                                            )
                                                        }
                                                        val forma = despesa.formaPagamento
                                                        if (!forma.isNullOrBlank()) {
                                                            Text(
                                                                text = "•  $forma",
                                                                fontSize = 12.sp,
                                                                color = secondaryColor,
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

                // Banner do AdMob integrado com rolagem na parte inferior
                com.polystitch.controlefinanceiro.ui.AdMobBanner(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 4.dp, bottom = 8.dp)
                )
            }
        }
    }
}