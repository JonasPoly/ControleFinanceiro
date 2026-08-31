package com.polystitch.controlefinanceiro.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.polystitch.controlefinanceiro.data.AppDatabase
import com.polystitch.controlefinanceiro.data.entity.TransacaoEntity
import com.polystitch.controlefinanceiro.data.entity.CartaoEntity
import com.polystitch.controlefinanceiro.data.entity.DespesaFixaEntity
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import java.util.Calendar

class FinanceViewModel(application: Application) : AndroidViewModel(application) {

    private val database = AppDatabase.obterBanco(application)
    private val transacaoDao = database.transacaoDao()
    private val cartaoDao = database.cartaoDao()
    private val despesaFixaDao = database.despesaFixaDao()

    val transacoes: StateFlow<List<TransacaoEntity>> = transacaoDao.obterTodas()
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = emptyList()
        )

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

    fun adicionarDespesaFixa(descricao: String, valor: Double, diaVencimento: Int, mesesAtivos: List<Int>) {
        viewModelScope.launch {
            val mesesString = mesesAtivos.joinToString(",")
            val entity = DespesaFixaEntity(
                id = 0L,
                descricao = descricao,
                valor = valor,
                diaVencimento = diaVencimento,
                mesesAtivos = mesesString
            )
            despesaFixaDao.inserir(entity)
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
        cartaoId: Long? = null
    ) {
        viewModelScope.launch {
            val transacao = TransacaoEntity(
                descricao = descricao,
                valor = valor,
                tipo = tipo,
                data = data,
                formaPagamento = formaPagamento,
                cartaoId = cartaoId
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
        quantidadeParcelas: Int
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