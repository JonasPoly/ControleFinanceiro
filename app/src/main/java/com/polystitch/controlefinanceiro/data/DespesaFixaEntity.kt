package com.polystitch.controlefinanceiro.data.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "despesas_fixas")
data class DespesaFixaEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0L,
    val descricao: String,
    val valor: Double,
    val diaVencimento: Int,
    val mesesAtivos: String, // Armazenado como texto (ex: "1,2,3,4")
    val categoriaId: Long? = null // Adicionado para suportar o vínculo com a categoria
)