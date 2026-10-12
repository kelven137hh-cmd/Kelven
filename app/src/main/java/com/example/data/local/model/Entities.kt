package com.example.data.local.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "profiles")
data class ProfileEntity(
  @PrimaryKey val id: String,
  val name: String,
  val email: String,
  val role: String = "MERCHANT", // MERCHANT or ADMIN
  val createdAt: Long = System.currentTimeMillis()
)

@Entity(tableName = "stores")
data class StoreEntity(
  @PrimaryKey val id: String,
  val ownerId: String,
  val name: String,
  val slug: String,
  val description: String,
  val businessCategory: String, // Moda Feminina, Moda Masculina, Calçado, Perfumaria, Acessórios
  val whatsappNumber: String, // e.g. "+258841234567"
  val city: String = "Maputo",
  val logoUrl: String = "",
  val coverUrl: String = "",
  val primaryColor: Long = 0xFF059669, // Emerald
  val secondaryColor: Long = 0xFF7C3AED, // Violet
  val visualTemplate: String = "FEMININE", // FEMININE, MASCULINE, SHOES, PERFUME, ACCESSORIES
  val isPublished: Boolean = true,
  val planId: String = "FREE", // FREE or PRO
  val createdAt: Long = System.currentTimeMillis(),
  val updatedAt: Long = System.currentTimeMillis()
)

@Entity(tableName = "categories")
data class CategoryEntity(
  @PrimaryKey val id: String,
  val storeId: String,
  val name: String,
  val sortOrder: Int = 0,
  val createdAt: Long = System.currentTimeMillis()
)

@Entity(tableName = "products")
data class ProductEntity(
  @PrimaryKey val id: String,
  val storeId: String,
  val categoryId: String = "",
  val name: String,
  val description: String = "",
  val price: Double, // In Meticais (MT)
  val promotionalPrice: Double? = null,
  val imageUrl: String = "",
  val isAvailable: Boolean = true,
  val isFeatured: Boolean = false,
  val sizesCsv: String = "", // e.g. "S,M,L,XL" or "39,40,41,42"
  val colorsCsv: String = "", // e.g. "Preto,Branco,Azul"
  val volume: String = "", // e.g. "50ml,100ml" for perfume
  val brand: String = "",
  val internalSku: String = "",
  val createdAt: Long = System.currentTimeMillis(),
  val updatedAt: Long = System.currentTimeMillis()
)

@Entity(tableName = "analytics_events")
data class AnalyticsEventEntity(
  @PrimaryKey val id: String,
  val storeId: String,
  val productId: String? = null,
  val eventType: String, // VIEW_STORE, VIEW_PRODUCT, WHATSAPP_CLICK, SHARE
  val timestamp: Long = System.currentTimeMillis()
)

@Entity(tableName = "order_history")
data class OrderHistoryEntity(
  @PrimaryKey val id: String,
  val storeId: String,
  val customerName: String,
  val customerPhone: String,
  val deliveryLocation: String,
  val deliveryType: String, // ENTREGA or LEVANTAMENTO
  val itemsSummary: String,
  val totalAmount: Double,
  val formattedMessage: String,
  val createdAt: Long = System.currentTimeMillis()
)
