package com.polystitch.controlefinanceiro.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.ChevronLeft
import androidx.compose.material.icons.filled.ChevronRight
import androidx.compose.material.icons.filled.CreditCard
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
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
import com.polystitch.controlefinanceiro.data.entity.CartaoEntity
import com.polystitch.controlefinanceiro.viewmodel.FinanceViewModel
import java.text.NumberFormat
import java.util.Calendar
import java.util.Locale

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CartoesScreen(
    viewModel: FinanceViewModel = viewModel(),
    onNavigateBack: () -> Unit = {}
) {
    val cartoes by viewModel.cartoes.collectAsState(initial = emptyList())
    val transacoes by viewModel.transacoes.collectAsState(initial = emptyList())

    var showDialog by remember { mutableStateOf(false) }
    var cartaoEmEdicao by remember { mutableStateOf<CartaoEntity?>(null) }
    var nomeInput by remember { mutableStateOf("") }
    var fechamentoInput by remember { mutableStateOf("") }
    var pagamentoInput by remember { mutableStateOf("") }

    var cartaoSelecionadoDetalhes by remember { mutableStateOf<CartaoEntity?>(null) }

    val calendarAtual = Calendar.getInstance()
    var mesSelecionado by remember { mutableStateOf(calendarAtual.get(Calendar.MONTH)) }
    var anoSelecionado by remember { mutableStateOf(calendarAtual.get(Calendar.YEAR)) }

    val mesesNomes = listOf(
        "Janeiro", "Fevereiro", "Março", "Abril", "Maio", "Junho",
        "Julho", "Agosto", "Setembro", "Outubro", "Novembro", "Dezembro"
    )

    val screenBackgroundBrush = Brush.verticalGradient(
        colors = listOf(
            Color(0xFFFFFFFF),
            Color(0xFFF1F5F9),
            Color(0xFF93C5FD),
            Color(0xFF3B82F6)
        )
    )

    val cardBackgroundBrush = Brush.horizontalGradient(
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
                                text = if (cartaoSelecionadoDetalhes == null) "Central de Cartões" else "Fatura: ${cartaoSelecionadoDetalhes?.nome}",
                                style = MaterialTheme.typography.titleMedium,
                                fontWeight = FontWeight.Bold,
                                color = Color(0xFF1E293B)
                            )
                            Text(
                                text = "Clique no Cartão para abrir a Fatura",
                                style = MaterialTheme.typography.bodySmall,
                                color = Color(0xFF475569)
                            )
                        }
                    },
                    navigationIcon = {
                        IconButton(onClick = {
                            if (cartaoSelecionadoDetalhes != null) {
                                cartaoSelecionadoDetalhes = null
                            } else {
                                onNavigateBack()
                            }
                        }) {
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

                if (cartaoSelecionadoDetalhes != null) {
                    val cartaoAtual = cartaoSelecionadoDetalhes!!

                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .shadow(elevation = 6.dp, shape = RoundedCornerShape(16.dp)),
                        shape = RoundedCornerShape(16.dp),
                        colors = CardDefaults.cardColors(containerColor = Color(0xFF1E293B))
                    ) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(12.dp),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            IconButton(onClick = {
                                if (mesSelecionado == 0) {
                                    mesSelecionado = 11
                                    anoSelecionado--
                                } else {
                                    mesSelecionado--
                                }
                            }) {
                                Icon(Icons.Default.ChevronLeft, contentDescription = "Mês Anterior", tint = Color.White)
                            }

                            Text(
                                text = "${mesesNomes[mesSelecionado]} de $anoSelecionado",
                                color = Color.White,
                                fontWeight = FontWeight.Bold,
                                fontSize = 16.sp
                            )

                            IconButton(onClick = {
                                if (mesSelecionado == 11) {
                                    mesSelecionado = 0
                                    anoSelecionado++
                                } else {
                                    mesSelecionado++
                                }
                            }) {
                                Icon(Icons.Default.ChevronRight, contentDescription = "Próximo Mês", tint = Color.White)
                            }
                        }
                    }

                    val gastosDoMes = transacoes.filter { transacao ->
                        val transCal = Calendar.getInstance().apply { timeInMillis = transacao.data }
                        transacao.cartaoId == cartaoAtual.id &&
                                transCal.get(Calendar.MONTH) == mesSelecionado &&
                                transCal.get(Calendar.YEAR) == anoSelecionado
                    }

                    val valorTotalFatura = gastosDoMes.sumOf { it.valor }
                    val formatoBRL = NumberFormat.getCurrencyInstance(Locale("pt", "BR"))

                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .shadow(elevation = 8.dp, shape = RoundedCornerShape(20.dp)),
                        shape = RoundedCornerShape(20.dp),
                        colors = CardDefaults.cardColors(containerColor = Color.Transparent)
                    ) {
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .background(cardBackgroundBrush)
                                .padding(20.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                Text(text = "Total da Fatura", fontSize = 13.sp, color = Color.White.copy(alpha = 0.7f))
                                Spacer(modifier = Modifier.height(4.dp))
                                Text(text = formatoBRL.format(valorTotalFatura), fontSize = 24.sp, fontWeight = FontWeight.Bold, color = Color.White)
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(4.dp))

                    if (gastosDoMes.isEmpty()) {
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(top = 20.dp, bottom = 20.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = "Nenhum gasto registrado neste mês.",
                                color = Color(0xFF1E293B).copy(alpha = 0.7f),
                                fontSize = 14.sp,
                                fontWeight = FontWeight.Medium
                            )
                        }
                    } else {
                        Column(
                            verticalArrangement = Arrangement.spacedBy(10.dp),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            gastosDoMes.forEach { gasto ->
                                Card(
                                    modifier = Modifier.fillMaxWidth(),
                                    shape = RoundedCornerShape(16.dp),
                                    colors = CardDefaults.cardColors(containerColor = Color.White)
                                ) {
                                    Row(
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .padding(16.dp),
                                        horizontalArrangement = Arrangement.SpaceBetween,
                                        verticalAlignment = Alignment.CenterVertically
                                    ) {
                                        Column(modifier = Modifier.weight(1f)) {
                                            Text(text = gasto.descricao, fontWeight = FontWeight.Bold, fontSize = 15.sp, color = Color(0xFF1E293B))
                                            Spacer(modifier = Modifier.height(2.dp))
                                            Text(text = "Forma: ${gasto.formaPagamento}", fontSize = 12.sp, color = Color(0xFF64748B))
                                        }
                                        Text(
                                            text = formatoBRL.format(gasto.valor),
                                            fontWeight = FontWeight.Bold,
                                            fontSize = 15.sp,
                                            color = Color(0xFFEF4444)
                                        )
                                    }
                                }
                            }
                        }
                    }

                } else {
                    Card(
                        onClick = {
                            cartaoEmEdicao = null
                            nomeInput = ""
                            fechamentoInput = ""
                            pagamentoInput = ""
                            showDialog = true
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
                                .padding(18.dp)
                        ) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Row(
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.spacedBy(14.dp)
                                ) {
                                    Box(
                                        modifier = Modifier
                                            .size(42.dp)
                                            .clip(CircleShape)
                                            .background(Color.White.copy(alpha = 0.15f)),
                                        contentAlignment = Alignment.Center
                                    ) {
                                        Icon(
                                            imageVector = Icons.Default.Add,
                                            contentDescription = null,
                                            tint = Color.White,
                                            modifier = Modifier.size(22.dp)
                                        )
                                    }
                                    Column {
                                        Text(
                                            text = "Cadastrar Novo Cartão",
                                            fontSize = 15.sp,
                                            fontWeight = FontWeight.Bold,
                                            color = Color.White
                                        )
                                        Text(
                                            text = "Adicione fechamento e vencimento",
                                            fontSize = 11.sp,
                                            color = Color.White.copy(alpha = 0.75f)
                                        )
                                    }
                                }
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(4.dp))

                    if (cartoes.isEmpty()) {
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(top = 20.dp, bottom = 20.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = "Nenhum cartão cadastrado ainda.",
                                color = Color(0xFF1E293B).copy(alpha = 0.7f),
                                fontSize = 14.sp,
                                fontWeight = FontWeight.Medium
                            )
                        }
                    } else {
                        Column(
                            verticalArrangement = Arrangement.spacedBy(12.dp),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            cartoes.forEach { cartao ->
                                Card(
                                    onClick = { cartaoSelecionadoDetalhes = cartao },
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
                                            .background(
                                                brush = Brush.horizontalGradient(
                                                    colors = listOf(
                                                        Color(0xFFE9D5FF),
                                                        Color(0xFFF3E8FF)
                                                    )
                                                )
                                            )
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
                                                        imageVector = Icons.Default.CreditCard,
                                                        contentDescription = null,
                                                        tint = Color(0xFF7C3AED),
                                                        modifier = Modifier.size(22.dp)
                                                    )
                                                }
                                                Column {
                                                    Text(
                                                        text = cartao.nome,
                                                        fontSize = 16.sp,
                                                        fontWeight = FontWeight.Bold,
                                                        color = Color(0xFF3B0764)
                                                    )
                                                    Spacer(modifier = Modifier.height(3.dp))
                                                    Text(
                                                        text = "Fechamento: Dia ${cartao.diaFechamento}  •  Pagamento: Dia ${cartao.diaPagamento}",
                                                        fontSize = 12.sp,
                                                        color = Color(0xFF6B21A8)
                                                    )
                                                }
                                            }

                                            Row(
                                                horizontalArrangement = Arrangement.spacedBy(4.dp),
                                                verticalAlignment = Alignment.CenterVertically
                                            ) {
                                                IconButton(
                                                    onClick = {
                                                        cartaoEmEdicao = cartao
                                                        nomeInput = cartao.nome
                                                        fechamentoInput = cartao.diaFechamento.toString()
                                                        pagamentoInput = cartao.diaPagamento.toString()
                                                        showDialog = true
                                                    },
                                                    modifier = Modifier.size(36.dp)
                                                ) {
                                                    Icon(
                                                        imageVector = Icons.Default.Edit,
                                                        contentDescription = "Editar",
                                                        tint = Color(0xFF7C3AED),
                                                        modifier = Modifier.size(18.dp)
                                                    )
                                                }

                                                IconButton(
                                                    onClick = { viewModel.deletarCartao(cartao) },
                                                    modifier = Modifier.size(36.dp)
                                                ) {
                                                    Icon(
                                                        imageVector = Icons.Default.Delete,
                                                        contentDescription = "Deletar",
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

        if (showDialog) {
            val isEditando = cartaoEmEdicao != null
            AlertDialog(
                onDismissRequest = { showDialog = false },
                title = { Text(if (isEditando) "Editar Cartão" else "Cadastrar Cartão") },
                text = {
                    Column(
                        verticalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        OutlinedTextField(
                            value = nomeInput,
                            onValueChange = { nomeInput = it },
                            label = { Text("Nome do Cartão (ex: Nubank, XP)") },
                            singleLine = true
                        )
                        OutlinedTextField(
                            value = fechamentoInput,
                            onValueChange = { fechamentoInput = it },
                            label = { Text("Dia de Fechamento (ex: 5)") },
                            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                            singleLine = true
                        )
                        OutlinedTextField(
                            value = pagamentoInput,
                            onValueChange = { pagamentoInput = it },
                            label = { Text("Dia de Pagamento (ex: 12)") },
                            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                            singleLine = true
                        )
                    }
                },
                confirmButton = {
                    TextButton(
                        onClick = {
                            val fechamentoInt = fechamentoInput.toIntOrNull() ?: 0
                            val pagamentoInt = pagamentoInput.toIntOrNull() ?: 0
                            if (nomeInput.isNotBlank() && fechamentoInt > 0 && pagamentoInt > 0) {
                                viewModel.salvarCartao(
                                    id = cartaoEmEdicao?.id ?: 0L,
                                    nome = nomeInput,
                                    diaFechamento = fechamentoInt,
                                    diaPagamento = pagamentoInt
                                )
                                nomeInput = ""
                                fechamentoInput = ""
                                pagamentoInput = ""
                                cartaoEmEdicao = null
                                showDialog = false
                            }
                        }
                    ) {
                        Text(if (isEditando) "Salvar Alterações" else "Adicionar")
                    }
                },
                dismissButton = {
                    TextButton(
                        onClick = {
                            cartaoEmEdicao = null
                            showDialog = false
                        }
                    ) {
                        Text("Cancelar")
                    }
                }
            )
        }
    }
}