package com.polystitch.controlefinanceiro.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.AddCircle
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
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.polystitch.controlefinanceiro.data.entity.CategoriaEntity
import com.polystitch.controlefinanceiro.data.entity.DespesaFixaEntity
import com.polystitch.controlefinanceiro.viewmodel.FinanceViewModel
import java.util.Calendar

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DespesasFixasScreen(
    viewModel: FinanceViewModel = viewModel(),
    onNavigateBack: () -> Unit = {}
) {
    val gradientStart = Color(0xFF0F172A)
    val gradientEnd = Color(0xFF3B82F6)
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

    val despesasFixas by viewModel.despesasFixas.collectAsState()

    val categorias by viewModel.categorias.collectAsState(initial = emptyList())
    var selectedCategoriaNome by remember { mutableStateOf("") }
    var expandedCategoriaDropdown by remember { mutableStateOf(false) }

    var showDialog by remember { mutableStateOf(false) }
    var descricaoInput by remember { mutableStateOf("") }
    var valorInput by remember { mutableStateOf("") }
    var diaVencimentoInput by remember { mutableStateOf("") }

    val mesesNomes = listOf("Jan", "Fev", "Mar", "Abr", "Mai", "Jun", "Jul", "Ago", "Set", "Out", "Nov", "Dez")
    val mesesSelecionados = remember { mutableStateMapOf<Int, Boolean>() }

    val mesAtual = Calendar.getInstance().get(Calendar.MONTH) + 1

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
                                text = "Despesas Fixas e Sazonais",
                                style = MaterialTheme.typography.titleMedium,
                                fontWeight = FontWeight.Bold,
                                color = Color(0xFF1E293B)
                            )
                            Text(
                                text = "Controle contas recorrentes por mes",
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
                    .padding(horizontal = 18.dp, vertical = 6.dp),
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                val totalMesAtual = despesasFixas
                    .filter { despesa ->
                        val mesesList = despesa.mesesAtivos
                            .split(",")
                            .mapNotNull { it.trim().toIntOrNull() }
                        mesesList.contains(mesAtual)
                    }
                    .sumOf { it.valor }

                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .shadow(
                            elevation = 8.dp,
                            shape = RoundedCornerShape(20.dp),
                            ambientColor = Color(0xFF0F172A).copy(alpha = 0.2f),
                            spotColor = Color(0xFF1E3A8A).copy(alpha = 0.25f)
                        )
                        .clip(RoundedCornerShape(20.dp))
                        .background(
                            brush = Brush.horizontalGradient(
                                colors = listOf(gradientStart, gradientEnd)
                            )
                        )
                        .padding(18.dp)
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column {
                            Text(
                                text = "Compromisso do Mes (${mesesNomes[mesAtual - 1]})",
                                color = Color.White.copy(alpha = 0.75f),
                                fontSize = 12.sp,
                                fontWeight = FontWeight.Medium
                            )
                            Spacer(modifier = Modifier.height(4.dp))
                            Text(
                                text = "R$ %.2f".format(totalMesAtual),
                                color = Color.White,
                                fontSize = 26.sp,
                                fontWeight = FontWeight.ExtraBold
                            )
                        }
                        Box(
                            modifier = Modifier
                                .size(44.dp)
                                .clip(CircleShape)
                                .background(Color.White.copy(alpha = 0.15f)),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = Icons.Default.EventRepeat,
                                contentDescription = null,
                                tint = Color.White,
                                modifier = Modifier.size(22.dp)
                            )
                        }
                    }
                }

                Button(
                    onClick = {
                        mesesSelecionados.clear()
                        selectedCategoriaNome = ""
                        showDialog = true
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(48.dp)
                        .shadow(
                            elevation = 4.dp,
                            shape = RoundedCornerShape(14.dp),
                            ambientColor = Color(0xFF1E3A8A).copy(alpha = 0.2f)
                        ),
                    shape = RoundedCornerShape(14.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = Color(0xFF2563EB)
                    )
                ) {
                    Icon(
                        imageVector = Icons.Default.AddCircle,
                        contentDescription = null,
                        tint = Color.White,
                        modifier = Modifier.size(20.dp)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = "Nova Despesa / Sazonal",
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color.White
                    )
                }

                Spacer(modifier = Modifier.height(2.dp))

                if (despesasFixas.isEmpty()) {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .weight(1f),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = "Nenhuma despesa fixa ou sazonal cadastrada.",
                            color = Color(0xFF475569),
                            fontSize = 14.sp,
                            fontWeight = FontWeight.Medium
                        )
                    }
                } else {
                    LazyColumn(
                        modifier = Modifier.weight(1f),
                        verticalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        items(despesasFixas, key = { despesa -> despesa.id }) { despesa: DespesaFixaEntity ->
                            val mesesList = despesa.mesesAtivos
                                .split(",")
                                .mapNotNull { it.trim().toIntOrNull() }

                            val categoriaNome = categorias.find { categoria -> categoria.id == despesa.categoriaId }?.nome

                            Card(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .shadow(
                                        elevation = 4.dp,
                                        shape = RoundedCornerShape(16.dp),
                                        ambientColor = Color(0xFF0F172A).copy(alpha = 0.15f)
                                    ),
                                shape = RoundedCornerShape(16.dp),
                                colors = CardDefaults.cardColors(containerColor = Color.Transparent)
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
                                                    imageVector = Icons.Default.EventRepeat,
                                                    contentDescription = null,
                                                    tint = expenseColor,
                                                    modifier = Modifier.size(19.dp)
                                                )
                                            }
                                            Column {
                                                Text(
                                                    text = despesa.descricao,
                                                    fontSize = 14.sp,
                                                    fontWeight = FontWeight.Bold,
                                                    color = Color.White
                                                )
                                                val qtdMeses = mesesList.size
                                                val textoMeses = if (qtdMeses == 1) "1 mes ativo" else "$qtdMeses meses ativos"
                                                val infoExtra = if (!categoriaNome.isNullOrBlank()) {
                                                    "Venc.: dia ${despesa.diaVencimento} • $categoriaNome • $textoMeses"
                                                } else {
                                                    "Vencimento: dia ${despesa.diaVencimento} • $textoMeses"
                                                }
                                                Text(
                                                    text = infoExtra,
                                                    fontSize = 11.sp,
                                                    color = Color.White.copy(alpha = 0.75f)
                                                )
                                            }
                                        }

                                        Row(
                                            verticalAlignment = Alignment.CenterVertically,
                                            horizontalArrangement = Arrangement.spacedBy(12.dp)
                                        ) {
                                            Text(
                                                text = "R$ %.2f".format(despesa.valor),
                                                fontSize = 14.sp,
                                                fontWeight = FontWeight.ExtraBold,
                                                color = Color.White
                                            )

                                            IconButton(
                                                onClick = {
                                                    viewModel.removerDespesaFixa(despesa.id)
                                                },
                                                modifier = Modifier.size(32.dp)
                                            ) {
                                                Icon(
                                                    imageVector = Icons.Default.Delete,
                                                    contentDescription = "Excluir",
                                                    tint = expenseColor.copy(alpha = 0.9f),
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

        if (showDialog) {
            AlertDialog(
                onDismissRequest = { showDialog = false },
                title = { Text("Despesa Fixa / Sazonal") },
                text = {
                    Column(
                        verticalArrangement = Arrangement.spacedBy(10.dp),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        OutlinedTextField(
                            value = descricaoInput,
                            onValueChange = { descricaoInput = it },
                            label = { Text("Descricao (ex: IPVA, IPTU, Aluguel)") },
                            singleLine = true,
                            modifier = Modifier.fillMaxWidth()
                        )
                        OutlinedTextField(
                            value = valorInput,
                            onValueChange = { valorInput = it },
                            label = { Text("Valor Mensal (R$)") },
                            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                            singleLine = true,
                            modifier = Modifier.fillMaxWidth()
                        )
                        OutlinedTextField(
                            value = diaVencimentoInput,
                            onValueChange = { diaVencimentoInput = it },
                            label = { Text("Dia do Vencimento (ex: 10)") },
                            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                            singleLine = true,
                            modifier = Modifier.fillMaxWidth()
                        )

                        Box(
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            OutlinedButton(
                                onClick = { expandedCategoriaDropdown = true },
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .height(56.dp),
                                shape = RoundedCornerShape(4.dp),
                                colors = ButtonDefaults.outlinedButtonColors(
                                    containerColor = Color.Transparent,
                                    contentColor = MaterialTheme.colorScheme.onSurface
                                )
                            ) {
                                Box(
                                    modifier = Modifier.fillMaxWidth(),
                                    contentAlignment = Alignment.CenterStart
                                ) {
                                    Text(
                                        text = selectedCategoriaNome.ifBlank { "Selecione a Categoria" },
                                        color = if (selectedCategoriaNome.isBlank()) MaterialTheme.colorScheme.onSurfaceVariant else MaterialTheme.colorScheme.onSurface,
                                        fontSize = 14.sp
                                    )
                                }
                            }

                            DropdownMenu(
                                expanded = expandedCategoriaDropdown,
                                onDismissRequest = { expandedCategoriaDropdown = false },
                                modifier = Modifier.fillMaxWidth(0.75f)
                            ) {
                                if (categorias.isEmpty()) {
                                    DropdownMenuItem(
                                        text = { Text("Nenhuma categoria cadastrada") },
                                        onClick = { expandedCategoriaDropdown = false }
                                    )
                                } else {
                                    categorias.forEach { categoria: CategoriaEntity ->
                                        DropdownMenuItem(
                                            text = { Text(categoria.nome) },
                                            onClick = {
                                                selectedCategoriaNome = categoria.nome
                                                expandedCategoriaDropdown = false
                                            }
                                        )
                                    }
                                }
                            }
                        }

                        Spacer(modifier = Modifier.height(2.dp))
                        Text(
                            text = "Selecione os meses em que esta despesa ocorre:",
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )

                        val chunkedMeses = mesesNomes.withIndex().chunked(4)
                        chunkedMeses.forEach { rowItems ->
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.spacedBy(6.dp)
                            ) {
                                rowItems.forEach { (index, nomeMes) ->
                                    val mesReal = index + 1
                                    val isSelected = mesesSelecionados[mesReal] == true

                                    Box(
                                        modifier = Modifier
                                            .weight(1f)
                                            .clip(RoundedCornerShape(8.dp))
                                            .background(if (isSelected) Color(0xFF2563EB) else Color(0xFFE2E8F0))
                                            .clickable {
                                                mesesSelecionados[mesReal] = !isSelected
                                            }
                                            .padding(vertical = 8.dp),
                                        contentAlignment = Alignment.Center
                                    ) {
                                        Text(
                                            text = nomeMes,
                                            fontSize = 11.sp,
                                            fontWeight = FontWeight.Bold,
                                            color = if (isSelected) Color.White else Color(0xFF334155),
                                            textAlign = TextAlign.Center
                                        )
                                    }
                                }
                            }
                        }
                    }
                },
                confirmButton = {
                    TextButton(
                        onClick = {
                            val valorParsed = valorInput.replace(",", ".").toDoubleOrNull()
                            val diaParsed = diaVencimentoInput.toIntOrNull()
                            val mesesAtivosList = mesesSelecionados.filter { entry -> entry.value }.keys.sorted()
                            val categoriaEscolhida = categorias.find { categoria -> categoria.nome == selectedCategoriaNome }

                            if (descricaoInput.isNotBlank() && valorParsed != null && valorParsed > 0 && diaParsed != null && mesesAtivosList.isNotEmpty()) {
                                viewModel.adicionarDespesaFixa(
                                    descricao = descricaoInput,
                                    valor = valorParsed,
                                    diaVencimento = diaParsed,
                                    mesesAtivos = mesesAtivosList,
                                    categoriaId = categoriaEscolhida?.id
                                )
                                descricaoInput = ""
                                valorInput = ""
                                diaVencimentoInput = ""
                                selectedCategoriaNome = ""
                                mesesSelecionados.clear()
                                showDialog = false
                            }
                        }
                    ) {
                        Text("Salvar")
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