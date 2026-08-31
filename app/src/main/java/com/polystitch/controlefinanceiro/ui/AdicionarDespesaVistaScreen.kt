package com.polystitch.controlefinanceiro.ui

import android.app.DatePickerDialog
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.CalendarMonth
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
import com.polystitch.controlefinanceiro.viewmodel.FinanceViewModel
import java.text.SimpleDateFormat
import java.util.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AdicionarDespesaVistaScreen(
    viewModel: FinanceViewModel = viewModel(),
    onNavigateBack: () -> Unit = {}
) {
    var descricaoInput by remember { mutableStateOf("") }
    var valorInput by remember { mutableStateOf("") }
    var formaPagamentoInput by remember { mutableStateOf("Dinheiro") }

    // Estados para o seletor de Categoria
    val categorias by viewModel.categorias.collectAsState(initial = emptyList())
    var selectedCategoriaNome by remember { mutableStateOf("") }
    var expandedCategoriaDropdown by remember { mutableStateOf(false) }

    val context = LocalContext.current
    var dataMillis by remember { mutableStateOf(System.currentTimeMillis()) }
    val dateFormatter = remember { SimpleDateFormat("dd/MM/yyyy", Locale("pt", "BR")) }

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

    // Gradiente alinhado do tom mais escuro para o vibrante (da esquerda para a direita)
    val inputBackgroundBrush = Brush.horizontalGradient(
        colors = listOf(
            primaryColor,
            secondaryColor
        )
    )

    // Função para abrir o DatePickerDialog
    val abrirCalendario = {
        val calendar = Calendar.getInstance().apply { timeInMillis = dataMillis }
        val year = calendar.get(Calendar.YEAR)
        val month = calendar.get(Calendar.MONTH)
        val day = calendar.get(Calendar.DAY_OF_MONTH)

        DatePickerDialog(
            context,
            { _, selectedYear, selectedMonth, selectedDay ->
                val newCalendar = Calendar.getInstance().apply {
                    set(selectedYear, selectedMonth, selectedDay)
                }
                dataMillis = newCalendar.timeInMillis
            },
            year,
            month,
            day
        ).show()
    }

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
                                text = "Nova Despesa à Vista",
                                style = MaterialTheme.typography.titleMedium,
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.onBackground
                            )
                            Text(
                                text = "Adicione a Despesa",
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
                            shape = RoundedCornerShape(20.dp),
                            ambientColor = primaryColor.copy(alpha = 0.2f),
                            spotColor = secondaryColor.copy(alpha = 0.3f)
                        ),
                    shape = RoundedCornerShape(20.dp),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(16.dp),
                        verticalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .shadow(
                                    elevation = 6.dp,
                                    shape = RoundedCornerShape(14.dp),
                                    ambientColor = primaryColor.copy(alpha = 0.15f),
                                    spotColor = secondaryColor.copy(alpha = 0.25f)
                                )
                                .clip(RoundedCornerShape(14.dp))
                                .background(inputBackgroundBrush)
                        ) {
                            OutlinedTextField(
                                value = descricaoInput,
                                onValueChange = { descricaoInput = it },
                                label = { Text("Descrição (ex: Supermercado, Aluguel)", color = Color.White.copy(alpha = 0.8f)) },
                                singleLine = true,
                                shape = RoundedCornerShape(14.dp),
                                colors = OutlinedTextFieldDefaults.colors(
                                    focusedBorderColor = primaryColor.copy(alpha = 0.6f),
                                    unfocusedBorderColor = Color.Transparent,
                                    focusedLabelColor = Color.White,
                                    unfocusedLabelColor = Color.White.copy(alpha = 0.8f),
                                    focusedTextColor = Color.White,
                                    unfocusedTextColor = Color.White,
                                    cursorColor = Color.White,
                                    focusedContainerColor = Color.Transparent,
                                    unfocusedContainerColor = Color.Transparent
                                ),
                                modifier = Modifier.fillMaxWidth()
                            )
                        }

                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .shadow(
                                    elevation = 6.dp,
                                    shape = RoundedCornerShape(14.dp),
                                    ambientColor = primaryColor.copy(alpha = 0.15f),
                                    spotColor = secondaryColor.copy(alpha = 0.25f)
                                )
                                .clip(RoundedCornerShape(14.dp))
                                .background(inputBackgroundBrush)
                        ) {
                            OutlinedTextField(
                                value = valorInput,
                                onValueChange = { valorInput = it },
                                label = { Text("Valor (R$)", color = Color.White.copy(alpha = 0.8f)) },
                                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                                singleLine = true,
                                shape = RoundedCornerShape(14.dp),
                                colors = OutlinedTextFieldDefaults.colors(
                                    focusedBorderColor = primaryColor.copy(alpha = 0.6f),
                                    unfocusedBorderColor = Color.Transparent,
                                    focusedLabelColor = Color.White,
                                    unfocusedLabelColor = Color.White.copy(alpha = 0.8f),
                                    focusedTextColor = Color.White,
                                    unfocusedTextColor = Color.White,
                                    cursorColor = Color.White,
                                    focusedContainerColor = Color.Transparent,
                                    unfocusedContainerColor = Color.Transparent
                                ),
                                modifier = Modifier.fillMaxWidth()
                            )
                        }

                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .shadow(
                                    elevation = 6.dp,
                                    shape = RoundedCornerShape(14.dp),
                                    ambientColor = primaryColor.copy(alpha = 0.15f),
                                    spotColor = secondaryColor.copy(alpha = 0.25f)
                                )
                                .clip(RoundedCornerShape(14.dp))
                                .background(inputBackgroundBrush)
                        ) {
                            OutlinedTextField(
                                value = formaPagamentoInput,
                                onValueChange = { formaPagamentoInput = it },
                                label = { Text("Forma de Pagamento (ex: Pix, Débito, Dinheiro)", color = Color.White.copy(alpha = 0.8f)) },
                                singleLine = true,
                                shape = RoundedCornerShape(14.dp),
                                colors = OutlinedTextFieldDefaults.colors(
                                    focusedBorderColor = primaryColor.copy(alpha = 0.6f),
                                    unfocusedBorderColor = Color.Transparent,
                                    focusedLabelColor = Color.White,
                                    unfocusedLabelColor = Color.White.copy(alpha = 0.8f),
                                    focusedTextColor = Color.White,
                                    unfocusedTextColor = Color.White,
                                    cursorColor = Color.White,
                                    focusedContainerColor = Color.Transparent,
                                    unfocusedContainerColor = Color.Transparent
                                ),
                                modifier = Modifier.fillMaxWidth()
                            )
                        }

                        // Dropdown para Categoria
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .shadow(
                                    elevation = 6.dp,
                                    shape = RoundedCornerShape(14.dp),
                                    ambientColor = primaryColor.copy(alpha = 0.15f),
                                    spotColor = secondaryColor.copy(alpha = 0.25f)
                                )
                                .clip(RoundedCornerShape(14.dp))
                                .background(inputBackgroundBrush)
                        ) {
                            OutlinedButton(
                                onClick = { expandedCategoriaDropdown = true },
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .height(56.dp),
                                shape = RoundedCornerShape(14.dp),
                                colors = ButtonDefaults.outlinedButtonColors(
                                    containerColor = Color.Transparent,
                                    contentColor = Color.White
                                ),
                                border = null
                            ) {
                                Box(
                                    modifier = Modifier.fillMaxWidth(),
                                    contentAlignment = Alignment.CenterStart
                                ) {
                                    Text(
                                        text = selectedCategoriaNome.ifBlank { "Selecione a Categoria" },
                                        color = if (selectedCategoriaNome.isBlank()) Color.White.copy(alpha = 0.8f) else Color.White,
                                        fontSize = 15.sp,
                                        fontWeight = FontWeight.Medium
                                    )
                                }
                            }

                            DropdownMenu(
                                expanded = expandedCategoriaDropdown,
                                onDismissRequest = { expandedCategoriaDropdown = false },
                                modifier = Modifier
                                    .fillMaxWidth(0.85f)
                                    .background(primaryColor)
                            ) {
                                if (categorias.isEmpty()) {
                                    DropdownMenuItem(
                                        text = { Text("Nenhuma categoria cadastrada", color = Color.White) },
                                        onClick = { expandedCategoriaDropdown = false }
                                    )
                                } else {
                                    categorias.forEach { categoria ->
                                        DropdownMenuItem(
                                            text = { Text(categoria.nome, color = Color.White) },
                                            onClick = {
                                                selectedCategoriaNome = categoria.nome
                                                expandedCategoriaDropdown = false
                                            }
                                        )
                                    }
                                }
                            }
                        }

                        val calendar = Calendar.getInstance().apply { timeInMillis = dataMillis }
                        val dataFormatada = dateFormatter.format(calendar.time)

                        // Campo de Data com Box interativo por cima para garantir o clique
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .shadow(
                                    elevation = 6.dp,
                                    shape = RoundedCornerShape(14.dp),
                                    ambientColor = primaryColor.copy(alpha = 0.15f),
                                    spotColor = secondaryColor.copy(alpha = 0.25f)
                                )
                                .clip(RoundedCornerShape(14.dp))
                                .background(inputBackgroundBrush)
                        ) {
                            OutlinedTextField(
                                value = dataFormatada,
                                onValueChange = {},
                                readOnly = true,
                                enabled = false, // Desativa o foco do campo para o clique ir direto pro Box pai
                                label = { Text("Data do Pagamento", color = Color.White.copy(alpha = 0.8f)) },
                                trailingIcon = {
                                    Icon(
                                        imageVector = Icons.Default.CalendarMonth,
                                        contentDescription = null,
                                        tint = Color.White
                                    )
                                },
                                shape = RoundedCornerShape(14.dp),
                                colors = OutlinedTextFieldDefaults.colors(
                                    disabledBorderColor = Color.Transparent,
                                    disabledLabelColor = Color.White.copy(alpha = 0.8f),
                                    disabledTextColor = Color.White,
                                    disabledContainerColor = Color.Transparent,
                                    disabledTrailingIconColor = Color.White
                                ),
                                modifier = Modifier.fillMaxWidth()
                            )

                            // Camada invisível interativa que intercepta o toque e abre o calendário
                            Box(
                                modifier = Modifier
                                    .matchParentSize()
                                    .clickable { abrirCalendario() }
                            )
                        }
                    }
                }

                Card(
                    onClick = {
                        val valorParsed = valorInput.replace(",", ".").toDoubleOrNull()
                        val categoriaEscolhida = categorias.find { it.nome == selectedCategoriaNome }

                        if (descricaoInput.isNotBlank() && valorParsed != null && valorParsed > 0) {
                            viewModel.adicionarTransacao(
                                descricao = descricaoInput,
                                valor = valorParsed,
                                tipo = "DESPESA",
                                data = dataMillis,
                                formaPagamento = formaPagamentoInput,
                                categoriaId = categoriaEscolhida?.id
                            )
                            onNavigateBack()
                        }
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .shadow(
                            elevation = 10.dp,
                            shape = RoundedCornerShape(20.dp),
                            ambientColor = primaryColor.copy(alpha = 0.2f),
                            spotColor = secondaryColor.copy(alpha = 0.3f)
                        ),
                    shape = RoundedCornerShape(20.dp),
                    colors = CardDefaults.cardColors(
                        containerColor = Color.Transparent
                    )
                ) {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .background(inputBackgroundBrush)
                            .padding(18.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = "Salvar Despesa",
                            fontSize = 15.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color.White
                        )
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