package com.polystitch.controlefinanceiro.data.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "categorias")
data class CategoriaEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0L,
    val nome: String,
    val tipo: String // "RECEITA" ou "DESPESA" para filtrar se necessário
)