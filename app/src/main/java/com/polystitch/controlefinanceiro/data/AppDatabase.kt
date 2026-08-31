package com.polystitch.controlefinanceiro.data

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.polystitch.controlefinanceiro.data.dao.CartaoDao
import com.polystitch.controlefinanceiro.data.dao.DespesaFixaDao
import com.polystitch.controlefinanceiro.data.dao.TransacaoDao
import com.polystitch.controlefinanceiro.data.entity.CartaoEntity
import com.polystitch.controlefinanceiro.data.entity.DespesaFixaEntity
import com.polystitch.controlefinanceiro.data.entity.TransacaoEntity

@Database(
    entities = [
        CartaoEntity::class,
        DespesaFixaEntity::class,
        TransacaoEntity::class
    ],
    version = 4,
    exportSchema = false
)
abstract class AppDatabase : RoomDatabase() {

    abstract fun cartaoDao(): CartaoDao
    abstract fun despesaFixaDao(): DespesaFixaDao
    abstract fun transacaoDao(): TransacaoDao

    companion object {
        @Volatile
        private var INSTANCE: AppDatabase? = null

        fun obterBanco(context: Context): AppDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    AppDatabase::class.java,
                    "polystitch_finance_db"
                )
                    .fallbackToDestructiveMigration()
                    .build()
                INSTANCE = instance
                instance
            }
        }
    }
}