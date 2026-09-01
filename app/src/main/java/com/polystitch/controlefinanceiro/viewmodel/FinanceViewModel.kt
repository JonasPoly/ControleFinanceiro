package com.polystitch.controlefinanceiro.viewmodel

import android.app.Application
import android.content.Context
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.polystitch.controlefinanceiro.data.AppDatabase
import com.polystitch.controlefinanceiro.data.entity.TransacaoEntity
import com.polystitch.controlefinanceiro.data.entity.CartaoEntity
import com.polystitch.controlefinanceiro.data.entity.DespesaFixaEntity
import com.polystitch.controlefinanceiro.data.entity.CategoriaEntity
import com.polystitch.controlefinanceiro.ui.theme.AppTheme
import com.polystitch.controlefinanceiro.utils.NotificationHelper
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import java.util.Calendar

class FinanceViewModel(application: Application) : AndroidViewModel(application) {

    private val sharedPreferences = application.getSharedPreferences("prefs_app", Context.MODE_PRIVATE)

    private val database = AppDatabase.obterBanco(application)
    private val transacaoDao = database.transacaoDao()
    private val cartaoDao = database.cartaoDao()
    private val despesaFixaDao = database.despesaFixaDao()
    private val categoriaDao = database.categoriaDao()

    private val _currentTheme = MutableStateFlow(loadSavedTheme())
    val currentTheme: StateFlow<AppTheme> = _currentTheme.asStateFlow()

    private fun loadSavedTheme(): AppTheme {
        val savedName = sharedPreferences.getString("app_theme", AppTheme.AZUL.name) ?: AppTheme.AZUL.name
        return try {
            AppTheme.valueOf(savedName)
        } catch (_: Exception) {
            AppTheme.AZUL
        }
    }

    fun setTheme(theme: AppTheme) {
        _currentTheme.value = theme
        sharedPreferences.edit().putString("app_theme", theme.name).apply()
    }

    val transacoes: StateFlow<List<TransacaoEntity>> = transacaoDao.obterTodas()
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = emptyList()
        )

    // --- ESTADOS DE FILTRO ---
    private val _termoBusca = MutableStateFlow("")
    val termoBusca: StateFlow<String> = _termoBusca.asStateFlow()

    private val _dataInicioFiltro = MutableStateFlow(obterInicioDoMesAtual())
    val dataInicioFiltro: StateFlow<Long> = _dataInicioFiltro.asStateFlow()

    private val _dataFimFiltro = MutableStateFlow(obterFimDoMesAtual())
    val dataFimFiltro: StateFlow<Long> = _dataFimFiltro.asStateFlow()

    private val _filtroFormaPagamento = MutableStateFlow("")
    val filtroFormaPagamento: StateFlow<String> = _filtroFormaPagamento.asStateFlow()

    private val _filtroCategoriaId = MutableStateFlow<Long?>(null)
    val filtroCategoriaId: StateFlow<Long?> = _filtroCategoriaId.asStateFlow()

    // Classe auxiliar para agrupar os parâmetros
    private data class FilterParams(
        val termo: String,
        val inicio: Long,
        val fim: Long,
        val forma: String,
        val categoria: Long?
    )

    // --- LISTAGEM FILTRADA REATIVA ---
    @OptIn(ExperimentalCoroutinesApi::class)
    val transacoesFiltradas: StateFlow<List<TransacaoEntity>> =
        combine(
            _termoBusca,
            _dataInicioFiltro,
            _dataFimFiltro,
            _filtroFormaPagamento,
            _filtroCategoriaId
        ) { termo, inicio, fim, forma, categoria ->
            FilterParams(termo, inicio, fim, forma, categoria)
        }.flatMapLatest { params ->
            transacaoDao.filtrarTransacoesAvancado(
                termoBusca = params.termo,
                dataInicio = params.inicio,
                dataFim = params.fim,
                formaPagamento = params.forma,
                categoriaId = params.categoria
            )
        }.stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = emptyList()
        )

    // --- FUNÇÕES DE ATUALIZAÇÃO DE FILTROS ---
    fun atualizarTermoBusca(novoTermo: String) {
        _termoBusca.value = novoTermo
    }

    fun atualizarPeriodoFiltro(inicio: Long, fim: Long) {
        _dataInicioFiltro.value = inicio
        _dataFimFiltro.value = fim
    }

    fun atualizarFiltroFormaPagamento(forma: String) {
        _filtroFormaPagamento.value = forma
    }

    fun atualizarFiltroCategoria(categoriaId: Long?) {
        _filtroCategoriaId.value = categoriaId
    }

    private fun obterInicioDoMesAtual(): Long {
        return Calendar.getInstance().apply {
            set(Calendar.DAY_OF_MONTH, 1)
            set(Calendar.HOUR_OF_DAY, 0)
            set(Calendar.MINUTE, 0)
            set(Calendar.SECOND, 0)
            set(Calendar.MILLISECOND, 0)
        }.timeInMillis
    }

    private fun obterFimDoMesAtual(): Long {
        return Calendar.getInstance().apply {
            set(Calendar.DAY_OF_MONTH, getActualMaximum(Calendar.DAY_OF_MONTH))
            set(Calendar.HOUR_OF_DAY, 23)
            set(Calendar.MINUTE, 59)
            set(Calendar.SECOND, 59)
            set(Calendar.MILLISECOND, 999)
        }.timeInMillis
    }

    // --- CARTÕES, DESPESAS FIXAS E CATEGORIAS ---
    val cartoes: StateFlow<List<CartaoEntity>> = cartaoDao.listarCartoes()
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = emptyList()
        )

    val despesasFixas: StateFlow<List<DespesaFixaEntity>> = despesaFixaDao.observarDespesasFixas()
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = emptyList()
        )

    val categorias: StateFlow<List<CategoriaEntity>> = categoriaDao.listarCategorias()
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = emptyList()
        )

    private fun calcularDelayAteDia(diaAlvo: Int): Long {
        val agora = Calendar.getInstance()
        val alvo = Calendar.getInstance().apply {
            set(Calendar.DAY_OF_MONTH, diaAlvo)
            set(Calendar.HOUR_OF_DAY, 9)
            set(Calendar.MINUTE, 0)
            set(Calendar.SECOND, 0)
            set(Calendar.MILLISECOND, 0)
        }
        if (alvo.before(agora)) {
            alvo.add(Calendar.MONTH, 1)
        }
        return alvo.timeInMillis - agora.timeInMillis
    }

    fun salvarCategoria(nome: String, tipo: String) {
        viewModelScope.launch {
            categoriaDao.inserirCategoria(CategoriaEntity(nome = nome, tipo = tipo))
        }
    }

    fun deletarCategoria(id: Long) {
        viewModelScope.launch {
            categoriaDao.deletarCategoria(id)
        }
    }

    fun adicionarDespesaFixa(
        descricao: String,
        valor: Double,
        diaVencimento: Int,
        mesesAtivos: List<Int>,
        categoriaId: Long? = null
    ) {
        viewModelScope.launch {
            val mesesString = mesesAtivos.joinToString(",")
            val entity = DespesaFixaEntity(
                id = 0L,
                descricao = descricao,
                valor = valor,
                diaVencimento = diaVencimento,
                mesesAtivos = mesesString,
                categoriaId = categoriaId
            )
            despesaFixaDao.inserir(entity)

            val delay = calcularDelayAteDia(diaVencimento)
            val idNotificacao = System.currentTimeMillis().toInt()

            NotificationHelper.agendarNotificacao(
                context = getApplication(),
                tag = "despesa_fixa_$idNotificacao",
                titulo = "Despesa Fixa Vencendo",
                mensagem = "A conta '$descricao' no valor de R$ %.2f vence hoje.".format(valor),
                delayEmMillis = delay,
                idNotificacao = idNotificacao
            )
        }
    }

    fun removerDespesaFixa(id: Long) {
        viewModelScope.launch {
            despesaFixaDao.deletar(id)
        }
    }

    fun adicionarTransacao(
        descricao: String,
        valor: Double,
        tipo: String,
        data: Long = System.currentTimeMillis(),
        formaPagamento: String = "DINHEIRO",
        cartaoId: Long? = null,
        categoriaId: Long? = null
    ) {
        viewModelScope.launch {
            val transacao = TransacaoEntity(
                descricao = descricao,
                valor = valor,
                tipo = tipo,
                data = data,
                formaPagamento = formaPagamento,
                cartaoId = cartaoId,
                categoriaId = categoriaId
            )
            transacaoDao.inserir(transacao)
        }
    }

    fun adicionarTransacaoComParcelas(
        descricao: String,
        valorTotal: Double,
        tipo: String,
        dataCompraMillis: Long,
        formaPagamento: String,
        cartaoId: Long?,
        diaFechamentoCartao: Int?,
        quantidadeParcelas: Int,
        categoriaId: Long? = null
    ) {
        viewModelScope.launch {
            val parcelas = if (quantidadeParcelas < 1) 1 else quantidadeParcelas
            val valorParcela = valorTotal / parcelas

            val calendar = Calendar.getInstance().apply {
                timeInMillis = dataCompraMillis
            }

            if (formaPagamento == "CREDITO" && diaFechamentoCartao != null) {
                val diaCompra = calendar.get(Calendar.DAY_OF_MONTH)
                if (diaCompra >= diaFechamentoCartao) {
                    calendar.add(Calendar.MONTH, 1)
                }
            }

            for (i in 1..parcelas) {
                val transacaoParcela = TransacaoEntity(
                    descricao = if (parcelas > 1) "$descricao ($i/$parcelas)" else descricao,
                    valor = valorParcela,
                    tipo = tipo,
                    data = calendar.timeInMillis,
                    formaPagamento = formaPagamento,
                    cartaoId = cartaoId,
                    categoriaId = categoriaId,
                    numeroParcela = if (parcelas > 1) i else null,
                    totalParcelas = if (parcelas > 1) parcelas else null,
                    transacaoPaiId = null
                )

                transacaoDao.inserir(transacaoParcela)
                calendar.add(Calendar.MONTH, 1)
            }
        }
    }

    fun salvarCartao(id: Long = 0L, nome: String, diaFechamento: Int, diaPagamento: Int) {
        viewModelScope.launch {
            val cartao = CartaoEntity(
                id = id,
                nome = nome,
                diaFechamento = diaFechamento,
                diaPagamento = diaPagamento
            )
            cartaoDao.inserirCartao(cartao)

            val delayFechamento = calcularDelayAteDia(diaFechamento)
            val delayPagamento = calcularDelayAteDia(diaPagamento)
            val baseId = System.currentTimeMillis()

            NotificationHelper.agendarNotificacao(
                context = getApplication(),
                tag = "cartao_fechamento_$baseId",
                titulo = "Fechamento de Fatura",
                mensagem = "A fatura do cartão '$nome' fecha hoje.",
                delayEmMillis = delayFechamento,
                idNotificacao = (baseId % Int.MAX_VALUE).toInt()
            )

            NotificationHelper.agendarNotificacao(
                context = getApplication(),
                tag = "cartao_pagamento_$baseId",
                titulo = "Vencimento de Fatura",
                mensagem = "O pagamento da fatura do cartão '$nome' vence hoje.",
                delayEmMillis = delayPagamento,
                idNotificacao = ((baseId + 1) % Int.MAX_VALUE).toInt()
            )
        }
    }

    fun deletarCartao(cartao: CartaoEntity) {
        viewModelScope.launch {
            cartaoDao.deletarCartao(cartao)
        }
    }

    fun deletarTransacao(id: Long) {
        viewModelScope.launch {
            transacaoDao.deletarPorId(id)
        }
    }
}