package com.polystitch.controlefinanceiro.data.dao

import androidx.room.*
import com.polystitch.controlefinanceiro.data.entity.CartaoEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface CartaoDao {
    @Query("SELECT * FROM cartoes")
    fun listarCartoes(): Flow<List<CartaoEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun inserirCartao(cartao: CartaoEntity)

    @Delete
    suspend fun deletarCartao(cartao: CartaoEntity)
}