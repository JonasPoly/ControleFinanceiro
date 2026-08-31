package com.polystitch.controlefinanceiro.data.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.polystitch.controlefinanceiro.data.entity.DespesaFixaEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface DespesaFixaDao {
    @Query("SELECT * FROM despesas_fixas")
    fun observarDespesasFixas(): Flow<List<DespesaFixaEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun inserir(despesa: DespesaFixaEntity)

    @Query("DELETE FROM despesas_fixas WHERE id = :id")
    suspend fun deletar(id: Long)
}
