package com.example.data.local

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.example.data.local.dao.AnalyticsDao
import com.example.data.local.dao.CategoryDao
import com.example.data.local.dao.OrderHistoryDao
import com.example.data.local.dao.ProductDao
import com.example.data.local.dao.ProfileDao
import com.example.data.local.dao.StoreDao
import com.example.data.local.model.AnalyticsEventEntity
import com.example.data.local.model.CategoryEntity
import com.example.data.local.model.OrderHistoryEntity
import com.example.data.local.model.ProductEntity
import com.example.data.local.model.ProfileEntity
import com.example.data.local.model.StoreEntity

@Database(
  entities = [
    ProfileEntity::class,
    StoreEntity::class,
    CategoryEntity::class,
    ProductEntity::class,
    AnalyticsEventEntity::class,
    OrderHistoryEntity::class
  ],
  version = 1,
  exportSchema = false
)
abstract class AppDatabase : RoomDatabase() {
  abstract fun profileDao(): ProfileDao
  abstract fun storeDao(): StoreDao
  abstract fun categoryDao(): CategoryDao
  abstract fun productDao(): ProductDao
  abstract fun analyticsDao(): AnalyticsDao
  abstract fun orderHistoryDao(): OrderHistoryDao

  companion object {
    @Volatile
    private var INSTANCE: AppDatabase? = null

    fun getDatabase(context: Context): AppDatabase {
      return INSTANCE ?: synchronized(this) {
        val instance = Room.databaseBuilder(
          context.applicationContext,
          AppDatabase::class.java,
          "zap_catalogo_db"
        ).fallbackToDestructiveMigration().build()
        INSTANCE = instance
        instance
      }
    }
  }
}
