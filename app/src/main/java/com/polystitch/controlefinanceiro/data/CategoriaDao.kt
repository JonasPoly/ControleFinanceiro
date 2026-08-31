package com.polystitch.controlefinanceiro.data.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.polystitch.controlefinanceiro.data.entity.CategoriaEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface CategoriaDao {
    @Query("SELECT * FROM categorias")
    fun listarCategorias(): Flow<List<CategoriaEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun inserirCategoria(categoria: CategoriaEntity)

    @Query("DELETE FROM categorias WHERE id = :id")
    suspend fun deletarCategoria(id: Long)
}