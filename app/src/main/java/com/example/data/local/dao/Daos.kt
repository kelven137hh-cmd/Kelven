package com.example.data.local.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.example.data.local.model.AnalyticsEventEntity
import com.example.data.local.model.CategoryEntity
import com.example.data.local.model.OrderHistoryEntity
import com.example.data.local.model.ProductEntity
import com.example.data.local.model.ProfileEntity
import com.example.data.local.model.StoreEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface ProfileDao {
  @Query("SELECT * FROM profiles WHERE id = :id")
  suspend fun getProfileById(id: String): ProfileEntity?

  @Query("SELECT * FROM profiles WHERE email = :email LIMIT 1")
  suspend fun getProfileByEmail(email: String): ProfileEntity?

  @Query("SELECT * FROM profiles")
  fun getAllProfiles(): Flow<List<ProfileEntity>>

  @Insert(onConflict = OnConflictStrategy.REPLACE)
  suspend fun insertProfile(profile: ProfileEntity)
}

@Dao
interface StoreDao {
  @Query("SELECT * FROM stores WHERE id = :id")
  suspend fun getStoreById(id: String): StoreEntity?

  @Query("SELECT * FROM stores WHERE slug = :slug LIMIT 1")
  suspend fun getStoreBySlug(slug: String): StoreEntity?

  @Query("SELECT * FROM stores WHERE ownerId = :ownerId")
  fun getStoresByOwner(ownerId: String): Flow<List<StoreEntity>>

  @Query("SELECT * FROM stores WHERE isPublished = 1")
  fun getAllPublishedStores(): Flow<List<StoreEntity>>

  @Query("SELECT * FROM stores")
  fun getAllStores(): Flow<List<StoreEntity>>

  @Insert(onConflict = OnConflictStrategy.REPLACE)
  suspend fun insertStore(store: StoreEntity)

  @Update
  suspend fun updateStore(store: StoreEntity)

  @Delete
  suspend fun deleteStore(store: StoreEntity)

  @Query("UPDATE stores SET isPublished = :isPublished WHERE id = :storeId")
  suspend fun updatePublishStatus(storeId: String, isPublished: Boolean)

  @Query("UPDATE stores SET planId = :planId WHERE id = :storeId")
  suspend fun updatePlan(storeId: String, planId: String)
}

@Dao
interface CategoryDao {
  @Query("SELECT * FROM categories WHERE storeId = :storeId ORDER BY sortOrder ASC, name ASC")
  fun getCategoriesByStore(storeId: String): Flow<List<CategoryEntity>>

  @Insert(onConflict = OnConflictStrategy.REPLACE)
  suspend fun insertCategory(category: CategoryEntity)

  @Update
  suspend fun updateCategory(category: CategoryEntity)

  @Delete
  suspend fun deleteCategory(category: CategoryEntity)

  @Query("DELETE FROM categories WHERE storeId = :storeId")
  suspend fun deleteAllCategoriesForStore(storeId: String)
}

@Dao
interface ProductDao {
  @Query("SELECT * FROM products WHERE storeId = :storeId ORDER BY isFeatured DESC, createdAt DESC")
  fun getProductsByStore(storeId: String): Flow<List<ProductEntity>>

  @Query("SELECT * FROM products WHERE id = :id")
  suspend fun getProductById(id: String): ProductEntity?

  @Query("SELECT COUNT(*) FROM products WHERE storeId = :storeId")
  suspend fun getProductCountByStore(storeId: String): Int

  @Insert(onConflict = OnConflictStrategy.REPLACE)
  suspend fun insertProduct(product: ProductEntity)

  @Update
  suspend fun updateProduct(product: ProductEntity)

  @Delete
  suspend fun deleteProduct(product: ProductEntity)

  @Query("DELETE FROM products WHERE storeId = :storeId")
  suspend fun deleteAllProductsForStore(storeId: String)
}

@Dao
interface AnalyticsDao {
  @Insert(onConflict = OnConflictStrategy.REPLACE)
  suspend fun insertEvent(event: AnalyticsEventEntity)

  @Query("SELECT COUNT(*) FROM analytics_events WHERE storeId = :storeId AND eventType = 'VIEW_STORE'")
  suspend fun getStoreViewsCount(storeId: String): Int

  @Query("SELECT COUNT(*) FROM analytics_events WHERE storeId = :storeId AND eventType = 'WHATSAPP_CLICK'")
  suspend fun getWhatsappClicksCount(storeId: String): Int

  @Query("SELECT COUNT(*) FROM analytics_events WHERE storeId = :storeId AND eventType = 'SHARE'")
  suspend fun getSharesCount(storeId: String): Int

  @Query("SELECT COUNT(*) FROM analytics_events WHERE storeId = :storeId AND eventType = 'VIEW_PRODUCT'")
  suspend fun getProductViewsCount(storeId: String): Int

  @Query("SELECT COUNT(*) FROM analytics_events WHERE eventType = 'WHATSAPP_CLICK'")
  suspend fun getTotalPlatformWhatsappClicks(): Int
}

@Dao
interface OrderHistoryDao {
  @Query("SELECT * FROM order_history WHERE storeId = :storeId ORDER BY createdAt DESC")
  fun getOrdersByStore(storeId: String): Flow<List<OrderHistoryEntity>>

  @Insert(onConflict = OnConflictStrategy.REPLACE)
  suspend fun insertOrder(order: OrderHistoryEntity)
}
