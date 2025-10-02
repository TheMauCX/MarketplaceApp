package com.marketplace.app.data.local.database

import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import android.content.Context
import androidx.room.TypeConverters
import com.marketplace.app.data.local.dao.VendedorDao
import com.marketplace.app.data.local.dao.ProductoDao
import com.marketplace.app.data.local.entities.VendedorEntity
import com.marketplace.app.data.local.entities.ProductoEntity
import com.marketplace.app.domain.util.BigDecimalConverter

@Database(
    entities = [VendedorEntity::class, ProductoEntity::class],
    version = 1,
    exportSchema = false
)
@TypeConverters(BigDecimalConverter::class)
abstract class AppDatabase : RoomDatabase() {

    abstract fun vendedorDao(): VendedorDao
    abstract fun productoDao(): ProductoDao

    companion object {
        @Volatile
        private var INSTANCE: AppDatabase? = null

        fun getInstance(context: Context): AppDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    AppDatabase::class.java,
                    "marketplace_db"
                ).build()
                INSTANCE = instance
                instance
            }
        }
    }
}