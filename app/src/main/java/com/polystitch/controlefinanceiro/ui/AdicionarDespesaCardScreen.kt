package com.polystitch.controlefinanceiro.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
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

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AdicionarDespesaCardScreen(
    viewModel: FinanceViewModel = viewModel(),
    onNavigateBack: () -> Unit
) {
    var descricao by remember { mutableStateOf("") }
    var valor by remember { mutableStateOf("") }
    var parcelas by remember { mutableStateOf("1") }

    val cartoes by viewModel.cartoes.collectAsState(initial = emptyList())
    var selectedCartaoNome by remember { mutableStateOf("") }
    var expandedDropdown by remember { mutableStateOf(false) }

    // Estados para o seletor de Categoria
    val categorias by viewModel.categorias.collectAsState(initial = emptyList())
    var selectedCategoriaNome by remember { mutableStateOf("") }
    var expandedCategoriaDropdown by remember { mutableStateOf(false) }

    // Cores integradas diretamente ao Theme do MaterialTheme atual
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
    val cardBackgroundBrush = Brush.horizontalGradient(
        colors = listOf(
            primaryColor,
            secondaryColor
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
                                text = "Nova Despesa no Cartão",
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

                OutlinedTextField(
                    value = descricao,
                    onValueChange = { descricao = it },
                    label = { Text("Descrição da Compra", color = Color.White.copy(alpha = 0.7f)) },
                    modifier = Modifier
                        .fillMaxWidth()
                        .shadow(
                            elevation = 8.dp,
                            shape = RoundedCornerShape(20.dp),
                            ambientColor = primaryColor.copy(alpha = 0.2f),
                            spotColor = secondaryColor.copy(alpha = 0.3f)
                        )
                        .clip(RoundedCornerShape(20.dp))
                        .background(cardBackgroundBrush),
                    shape = RoundedCornerShape(20.dp),
                    singleLine = true,
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedContainerColor = Color.Transparent,
                        unfocusedContainerColor = Color.Transparent,
                        disabledContainerColor = Color.Transparent,
                        focusedBorderColor = primaryColor.copy(alpha = 0.6f),
                        unfocusedBorderColor = Color.Transparent,
                        focusedTextColor = Color.White,
                        unfocusedTextColor = Color.White,
                        focusedLabelColor = Color.White.copy(alpha = 0.9f),
                        unfocusedLabelColor = Color.White.copy(alpha = 0.7f)
                    )
                )

                OutlinedTextField(
                    value = valor,
                    onValueChange = { valor = it },
                    label = { Text("Valor Total (R$)", color = Color.White.copy(alpha = 0.7f)) },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                    modifier = Modifier
                        .fillMaxWidth()
                        .shadow(
                            elevation = 8.dp,
                            shape = RoundedCornerShape(20.dp),
                            ambientColor = primaryColor.copy(alpha = 0.2f),
                            spotColor = secondaryColor.copy(alpha = 0.3f)
                        )
                        .clip(RoundedCornerShape(20.dp))
                        .background(cardBackgroundBrush),
                    shape = RoundedCornerShape(20.dp),
                    singleLine = true,
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedContainerColor = Color.Transparent,
                        unfocusedContainerColor = Color.Transparent,
                        disabledContainerColor = Color.Transparent,
                        focusedBorderColor = primaryColor.copy(alpha = 0.6f),
                        unfocusedBorderColor = Color.Transparent,
                        focusedTextColor = Color.White,
                        unfocusedTextColor = Color.White,
                        focusedLabelColor = Color.White.copy(alpha = 0.9f),
                        unfocusedLabelColor = Color.White.copy(alpha = 0.7f)
                    )
                )

                OutlinedTextField(
                    value = parcelas,
                    onValueChange = { parcelas = it },
                    label = { Text("Número de Parcelas (Ex: 1 para à vista)", color = Color.White.copy(alpha = 0.7f)) },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                    modifier = Modifier
                        .fillMaxWidth()
                        .shadow(
                            elevation = 8.dp,
                            shape = RoundedCornerShape(20.dp),
                            ambientColor = primaryColor.copy(alpha = 0.2f),
                            spotColor = secondaryColor.copy(alpha = 0.3f)
                        )
                        .clip(RoundedCornerShape(20.dp))
                        .background(cardBackgroundBrush),
                    shape = RoundedCornerShape(20.dp),
                    singleLine = true,
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedContainerColor = Color.Transparent,
                        unfocusedContainerColor = Color.Transparent,
                        disabledContainerColor = Color.Transparent,
                        focusedBorderColor = primaryColor.copy(alpha = 0.6f),
                        unfocusedBorderColor = Color.Transparent,
                        focusedTextColor = Color.White,
                        unfocusedTextColor = Color.White,
                        focusedLabelColor = Color.White.copy(alpha = 0.9f),
                        unfocusedLabelColor = Color.White.copy(alpha = 0.7f)
                    )
                )

                // Dropdown para Cartão
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .shadow(
                            elevation = 8.dp,
                            shape = RoundedCornerShape(20.dp),
                            ambientColor = primaryColor.copy(alpha = 0.2f),
                            spotColor = secondaryColor.copy(alpha = 0.3f)
                        )
                        .clip(RoundedCornerShape(20.dp))
                        .background(cardBackgroundBrush)
                ) {
                    OutlinedButton(
                        onClick = { expandedDropdown = true },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(56.dp),
                        shape = RoundedCornerShape(20.dp),
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
                                text = selectedCartaoNome.ifBlank { "Selecione o Cartão" },
                                color = if (selectedCartaoNome.isBlank()) Color.White.copy(alpha = 0.7f) else Color.White,
                                fontSize = 15.sp,
                                fontWeight = FontWeight.Medium
                            )
                        }
                    }

                    DropdownMenu(
                        expanded = expandedDropdown,
                        onDismissRequest = { expandedDropdown = false },
                        modifier = Modifier
                            .fillMaxWidth(0.85f)
                            .background(primaryColor)
                    ) {
                        if (cartoes.isEmpty()) {
                            DropdownMenuItem(
                                text = { Text("Nenhum cartão cadastrado", color = Color.White) },
                                onClick = { expandedDropdown = false }
                            )
                        } else {
                            cartoes.forEach { cartao ->
                                DropdownMenuItem(
                                    text = { Text("${cartao.nome} (Fechamento dia ${cartao.diaFechamento})", color = Color.White) },
                                    onClick = {
                                        selectedCartaoNome = cartao.nome
                                        expandedDropdown = false
                                    }
                                )
                            }
                        }
                    }
                }

                // Dropdown para Categoria
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .shadow(
                            elevation = 8.dp,
                            shape = RoundedCornerShape(20.dp),
                            ambientColor = primaryColor.copy(alpha = 0.2f),
                            spotColor = secondaryColor.copy(alpha = 0.3f)
                        )
                        .clip(RoundedCornerShape(20.dp))
                        .background(cardBackgroundBrush)
                ) {
                    OutlinedButton(
                        onClick = { expandedCategoriaDropdown = true },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(56.dp),
                        shape = RoundedCornerShape(20.dp),
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
                                color = if (selectedCategoriaNome.isBlank()) Color.White.copy(alpha = 0.7f) else Color.White,
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

                Spacer(modifier = Modifier.height(4.dp))

                Card(
                    onClick = {
                        val valorParsed = valor.replace(",", ".").toDoubleOrNull()
                        val qtdParcelas = parcelas.toIntOrNull() ?: 1
                        val cartaoEscolhido = cartoes.find { it.nome == selectedCartaoNome }
                        val categoriaEscolhida = categorias.find { it.nome == selectedCategoriaNome }

                        if (descricao.isNotBlank() && valorParsed != null && valorParsed > 0 && cartaoEscolhido != null) {
                            viewModel.adicionarTransacaoComParcelas(
                                descricao = descricao,
                                valorTotal = valorParsed,
                                tipo = "DESPESA",
                                dataCompraMillis = System.currentTimeMillis(),
                                formaPagamento = "CREDITO",
                                cartaoId = cartaoEscolhido.id,
                                diaFechamentoCartao = cartaoEscolhido.diaFechamento,
                                quantidadeParcelas = qtdParcelas,
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
                    colors = CardDefaults.cardColors(containerColor = Color.Transparent)
                ) {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .background(cardBackgroundBrush)
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

                // Banner do AdMob inserido de forma segura com rolagem
                com.polystitch.controlefinanceiro.ui.AdMobBanner(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 4.dp, bottom = 8.dp)
                )
            }
        }
    }
}