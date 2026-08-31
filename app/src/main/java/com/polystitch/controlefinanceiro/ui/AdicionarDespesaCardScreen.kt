package com.polystitch.controlefinanceiro.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
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

    val screenBackgroundBrush = Brush.verticalGradient(
        colors = listOf(
            Color(0xFFFFFFFF),
            Color(0xFFF1F5F9),
            Color(0xFF93C5FD),
            Color(0xFF3B82F6)
        )
    )

    val cardBackgroundBrush = Brush.horizontalGradient(
        colors = listOf(
            Color(0xFF1E293B),
            Color(0xFF2563EB)
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
                                color = Color(0xFF1E293B)
                            )
                            Text(
                                text = "Polystitch Finance",
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

                OutlinedTextField(
                    value = descricao,
                    onValueChange = { descricao = it },
                    label = { Text("Descrição da Compra", color = Color.White.copy(alpha = 0.7f)) },
                    modifier = Modifier
                        .fillMaxWidth()
                        .shadow(
                            elevation = 8.dp,
                            shape = RoundedCornerShape(20.dp),
                            ambientColor = Color(0xFF0F172A).copy(alpha = 0.2f),
                            spotColor = Color(0xFF1E3A8A).copy(alpha = 0.3f)
                        )
                        .clip(RoundedCornerShape(20.dp))
                        .background(cardBackgroundBrush),
                    shape = RoundedCornerShape(20.dp),
                    singleLine = true,
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedContainerColor = Color.Transparent,
                        unfocusedContainerColor = Color.Transparent,
                        disabledContainerColor = Color.Transparent,
                        focusedBorderColor = Color(0xFF93C5FD),
                        unfocusedBorderColor = Color.Transparent,
                        focusedTextColor = Color.White,
                        unfocusedTextColor = Color.White,
                        focusedLabelColor = Color(0xFF93C5FD),
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
                            ambientColor = Color(0xFF0F172A).copy(alpha = 0.2f),
                            spotColor = Color(0xFF1E3A8A).copy(alpha = 0.3f)
                        )
                        .clip(RoundedCornerShape(20.dp))
                        .background(cardBackgroundBrush),
                    shape = RoundedCornerShape(20.dp),
                    singleLine = true,
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedContainerColor = Color.Transparent,
                        unfocusedContainerColor = Color.Transparent,
                        disabledContainerColor = Color.Transparent,
                        focusedBorderColor = Color(0xFF93C5FD),
                        unfocusedBorderColor = Color.Transparent,
                        focusedTextColor = Color.White,
                        unfocusedTextColor = Color.White,
                        focusedLabelColor = Color(0xFF93C5FD),
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
                            ambientColor = Color(0xFF0F172A).copy(alpha = 0.2f),
                            spotColor = Color(0xFF1E3A8A).copy(alpha = 0.3f)
                        )
                        .clip(RoundedCornerShape(20.dp))
                        .background(cardBackgroundBrush),
                    shape = RoundedCornerShape(20.dp),
                    singleLine = true,
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedContainerColor = Color.Transparent,
                        unfocusedContainerColor = Color.Transparent,
                        disabledContainerColor = Color.Transparent,
                        focusedBorderColor = Color(0xFF93C5FD),
                        unfocusedBorderColor = Color.Transparent,
                        focusedTextColor = Color.White,
                        unfocusedTextColor = Color.White,
                        focusedLabelColor = Color(0xFF93C5FD),
                        unfocusedLabelColor = Color.White.copy(alpha = 0.7f)
                    )
                )

                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .shadow(
                            elevation = 8.dp,
                            shape = RoundedCornerShape(20.dp),
                            ambientColor = Color(0xFF0F172A).copy(alpha = 0.2f),
                            spotColor = Color(0xFF1E3A8A).copy(alpha = 0.3f)
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
                            .background(Color(0xFF1E293B))
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

                Spacer(modifier = Modifier.height(8.dp))

                Card(
                    onClick = {
                        val valorParsed = valor.replace(",", ".").toDoubleOrNull()
                        val qtdParcelas = parcelas.toIntOrNull() ?: 1
                        val cartaoEscolhido = cartoes.find { it.nome == selectedCartaoNome }

                        if (descricao.isNotBlank() && valorParsed != null && valorParsed > 0 && cartaoEscolhido != null) {
                            viewModel.adicionarTransacaoComParcelas(
                                descricao = descricao,
                                valorTotal = valorParsed,
                                tipo = "DESPESA",
                                dataCompraMillis = System.currentTimeMillis(),
                                formaPagamento = "CREDITO",
                                cartaoId = cartaoEscolhido.id,
                                diaFechamentoCartao = cartaoEscolhido.diaFechamento,
                                quantidadeParcelas = qtdParcelas
                            )
                            onNavigateBack()
                        }
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .shadow(
                            elevation = 10.dp,
                            shape = RoundedCornerShape(20.dp),
                            ambientColor = Color(0xFF0F172A).copy(alpha = 0.2f),
                            spotColor = Color(0xFF1E3A8A).copy(alpha = 0.3f)
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
            }
        }
    }
}