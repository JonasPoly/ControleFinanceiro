package com.polystitch.controlefinanceiro.data.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.polystitch.controlefinanceiro.data.entity.TransacaoEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface TransacaoDao {

    @Query("SELECT * FROM transacoes ORDER BY data DESC")
    fun obterTodas(): Flow<List<TransacaoEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun inserir(transacao: TransacaoEntity)

    @Query("DELETE FROM transacoes WHERE id = :id")
    suspend fun deletarPorId(id: Long)
}