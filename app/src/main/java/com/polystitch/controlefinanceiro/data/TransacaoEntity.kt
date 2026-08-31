package com.polystitch.controlefinanceiro.data.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "transacoes")
data class TransacaoEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0L,
    val descricao: String,
    val valor: Double,
    val tipo: String, // "RECEITA" ou "DESPESA"
    val data: Long,
    val formaPagamento: String,
    val cartaoId: Long? = null,
    val numeroParcela: Int? = null,
    val totalParcelas: Int? = null,
    val transacaoPaiId: Long? = null
)