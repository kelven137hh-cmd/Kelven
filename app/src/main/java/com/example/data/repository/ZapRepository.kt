package com.example.data.repository

import com.example.data.local.AppDatabase
import com.example.data.local.model.AnalyticsEventEntity
import com.example.data.local.model.CategoryEntity
import com.example.data.local.model.OrderHistoryEntity
import com.example.data.local.model.ProductEntity
import com.example.data.local.model.ProfileEntity
import com.example.data.local.model.StoreEntity
import com.example.model.PlanType
import com.example.model.StoreAnalytics
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import java.util.UUID

class ZapRepository(private val db: AppDatabase) {

  // Current session in-memory profile
  var currentProfile: ProfileEntity? = null
    private set

  fun setCurrentUser(profile: ProfileEntity?) {
    currentProfile = profile
  }

  suspend fun login(email: String): ProfileEntity {
    var profile = db.profileDao().getProfileByEmail(email.trim().lowercase())
    if (profile == null) {
      val isAdmin = email.contains("admin", ignoreCase = true)
      profile = ProfileEntity(
        id = UUID.randomUUID().toString(),
        name = if (isAdmin) "Administrador Geral" else email.substringBefore("@").replaceFirstChar { it.uppercase() },
        email = email.trim().lowercase(),
        role = if (isAdmin) "ADMIN" else "MERCHANT"
      )
      db.profileDao().insertProfile(profile)
    }
    currentProfile = profile
    return profile
  }

  suspend fun register(name: String, email: String, role: String = "MERCHANT"): ProfileEntity {
    val existing = db.profileDao().getProfileByEmail(email.trim().lowercase())
    if (existing != null) {
      currentProfile = existing
      return existing
    }
    val profile = ProfileEntity(
      id = UUID.randomUUID().toString(),
      name = name.trim(),
      email = email.trim().lowercase(),
      role = role
    )
    db.profileDao().insertProfile(profile)
    currentProfile = profile
    return profile
  }

  fun getAllProfiles(): Flow<List<ProfileEntity>> = db.profileDao().getAllProfiles()

  // Stores
  fun getStoresByOwner(ownerId: String): Flow<List<StoreEntity>> = db.storeDao().getStoresByOwner(ownerId)
  fun getAllPublishedStores(): Flow<List<StoreEntity>> = db.storeDao().getAllPublishedStores()
  fun getAllStores(): Flow<List<StoreEntity>> = db.storeDao().getAllStores()
  suspend fun getStoreById(storeId: String): StoreEntity? = db.storeDao().getStoreById(storeId)
  suspend fun getStoreBySlug(slug: String): StoreEntity? = db.storeDao().getStoreBySlug(slug)

  suspend fun createStore(store: StoreEntity) {
    db.storeDao().insertStore(store)
  }

  suspend fun updateStore(store: StoreEntity) {
    db.storeDao().updateStore(store)
  }

  suspend fun deleteStore(store: StoreEntity) {
    db.storeDao().deleteStore(store)
    db.productDao().deleteAllProductsForStore(store.id)
    db.categoryDao().deleteAllCategoriesForStore(store.id)
  }

  suspend fun togglePublishStore(storeId: String, publish: Boolean) {
    db.storeDao().updatePublishStatus(storeId, publish)
  }

  suspend fun updateStorePlan(storeId: String, planId: String) {
    db.storeDao().updatePlan(storeId, planId)
  }

  // Categories
  fun getCategoriesByStore(storeId: String): Flow<List<CategoryEntity>> = db.categoryDao().getCategoriesByStore(storeId)
  suspend fun insertCategory(category: CategoryEntity) = db.categoryDao().insertCategory(category)
  suspend fun updateCategory(category: CategoryEntity) = db.categoryDao().updateCategory(category)
  suspend fun deleteCategory(category: CategoryEntity) = db.categoryDao().deleteCategory(category)

  // Products
  fun getProductsByStore(storeId: String): Flow<List<ProductEntity>> = db.productDao().getProductsByStore(storeId)
  suspend fun getProductCountByStore(storeId: String): Int = db.productDao().getProductCountByStore(storeId)
  suspend fun getProductById(productId: String): ProductEntity? = db.productDao().getProductById(productId)
  suspend fun insertProduct(product: ProductEntity) = db.productDao().insertProduct(product)
  suspend fun updateProduct(product: ProductEntity) = db.productDao().updateProduct(product)
  suspend fun deleteProduct(product: ProductEntity) = db.productDao().deleteProduct(product)

  suspend fun canAddMoreProducts(storeId: String): Boolean {
    val store = getStoreById(storeId) ?: return false
    val plan = PlanType.fromId(store.planId)
    val currentCount = getProductCountByStore(storeId)
    return currentCount < plan.maxProducts
  }

  // Analytics
  suspend fun recordStoreView(storeId: String) {
    db.analyticsDao().insertEvent(
      AnalyticsEventEntity(
        id = UUID.randomUUID().toString(),
        storeId = storeId,
        eventType = "VIEW_STORE"
      )
    )
  }

  suspend fun recordProductView(storeId: String, productId: String) {
    db.analyticsDao().insertEvent(
      AnalyticsEventEntity(
        id = UUID.randomUUID().toString(),
        storeId = storeId,
        productId = productId,
        eventType = "VIEW_PRODUCT"
      )
    )
  }

  suspend fun recordWhatsappClick(storeId: String) {
    db.analyticsDao().insertEvent(
      AnalyticsEventEntity(
        id = UUID.randomUUID().toString(),
        storeId = storeId,
        eventType = "WHATSAPP_CLICK"
      )
    )
  }

  suspend fun recordShare(storeId: String) {
    db.analyticsDao().insertEvent(
      AnalyticsEventEntity(
        id = UUID.randomUUID().toString(),
        storeId = storeId,
        eventType = "SHARE"
      )
    )
  }

  suspend fun getStoreAnalytics(storeId: String): StoreAnalytics {
    return StoreAnalytics(
      views = db.analyticsDao().getStoreViewsCount(storeId),
      whatsappClicks = db.analyticsDao().getWhatsappClicksCount(storeId),
      shares = db.analyticsDao().getSharesCount(storeId),
      productViews = db.analyticsDao().getProductViewsCount(storeId)
    )
  }

  suspend fun getTotalPlatformWhatsappClicks(): Int {
    return db.analyticsDao().getTotalPlatformWhatsappClicks()
  }

  // Orders
  fun getOrdersByStore(storeId: String): Flow<List<OrderHistoryEntity>> = db.orderHistoryDao().getOrdersByStore(storeId)

  suspend fun recordOrder(order: OrderHistoryEntity) {
    db.orderHistoryDao().insertOrder(order)
    recordWhatsappClick(order.storeId)
  }

  // Seed sample data
  suspend fun ensureSampleData() {
    val existingStores = db.storeDao().getAllStores().first()
    if (existingStores.isNotEmpty()) return

    // 1. Admin Profile
    val admin = ProfileEntity(
      id = "user_admin",
      name = "Administrador Geral ZapCatálogo",
      email = "admin@zapcatalogo.mz",
      role = "ADMIN"
    )
    db.profileDao().insertProfile(admin)

    // 2. Demo Merchant Marta
    val marta = ProfileEntity(
      id = "user_marta",
      name = "Marta Cossa",
      email = "marta@modaelegante.mz",
      role = "MERCHANT"
    )
    db.profileDao().insertProfile(marta)

    // Set active default user
    if (currentProfile == null) {
      currentProfile = marta
    }

    // 3. Demo Stores
    // Store 1: Moda Elegante
    val store1 = StoreEntity(
      id = "store_moda_elegante",
      ownerId = marta.id,
      name = "Moda Elegante",
      slug = "moda-elegante",
      description = "Vestidos de festa, conjuntos sofisticados e moda feminina contemporânea em Maputo.",
      businessCategory = "Moda Feminina",
      whatsappNumber = "+258841234567",
      city = "Maputo, Polana Cimento",
      coverUrl = "banner_fashion.jpg",
      primaryColor = 0xFF059669,
      secondaryColor = 0xFFDB2777,
      visualTemplate = "FEMININE",
      isPublished = true,
      planId = "PRO"
    )
    db.storeDao().insertStore(store1)

    val cat1Festa = CategoryEntity(id = "cat_1_1", storeId = store1.id, name = "Vestidos de Festa", sortOrder = 1)
    val cat1Casual = CategoryEntity(id = "cat_1_2", storeId = store1.id, name = "Blazers & Conjuntos", sortOrder = 2)
    val cat1Blusas = CategoryEntity(id = "cat_1_3", storeId = store1.id, name = "Blusas & Tops", sortOrder = 3)
    db.categoryDao().insertCategory(cat1Festa)
    db.categoryDao().insertCategory(cat1Casual)
    db.categoryDao().insertCategory(cat1Blusas)

    db.productDao().insertProduct(
      ProductEntity(
        id = "prod_1_1",
        storeId = store1.id,
        categoryId = cat1Festa.id,
        name = "Vestido Longo Gala Esmeralda",
        description = "Vestido longo em cetim com decote nas costas e fenda lateral sutil. Tecido respirável e caimento impecável.",
        price = 3400.0,
        promotionalPrice = 2950.0,
        imageUrl = "banner_fashion.jpg",
        isAvailable = true,
        isFeatured = true,
        sizesCsv = "S,M,L,XL",
        colorsCsv = "Verde Esmeralda,Azul Royal,Preto",
        brand = "Aurélia Couture"
      )
    )
    db.productDao().insertProduct(
      ProductEntity(
        id = "prod_1_2",
        storeId = store1.id,
        categoryId = cat1Casual.id,
        name = "Blazer Alfaiataria Linho Cru",
        description = "Blazer estruturado em linho natural premium com botões em madrepérola. Ideal para reuniões e jantares.",
        price = 2800.0,
        promotionalPrice = null,
        imageUrl = "banner_fashion.jpg",
        isAvailable = true,
        isFeatured = true,
        sizesCsv = "M,L,XL",
        colorsCsv = "Cru Natural,Branco Neve,Terracota",
        brand = "Aurélia Couture"
      )
    )
    db.productDao().insertProduct(
      ProductEntity(
        id = "prod_1_3",
        storeId = store1.id,
        categoryId = cat1Blusas.id,
        name = "Camisa Seda com Laço Gola",
        description = "Camisa sofisticada em seda suave, acabamento nobre nos punhos e gola com laço removível.",
        price = 1600.0,
        promotionalPrice = 1350.0,
        imageUrl = "banner_fashion.jpg",
        isAvailable = true,
        isFeatured = false,
        sizesCsv = "S,M,L",
        colorsCsv = "Champagne,Branco,Preto",
        brand = "Elegance Chic"
      )
    )
    db.productDao().insertProduct(
      ProductEntity(
        id = "prod_1_4",
        storeId = store1.id,
        categoryId = cat1Casual.id,
        name = "Calças Alfaiataria Wide Leg",
        description = "Calças de cintura alta com corte amplo, pregas frontais e bolsos laterais funcionais.",
        price = 1800.0,
        promotionalPrice = null,
        imageUrl = "banner_fashion.jpg",
        isAvailable = true,
        isFeatured = false,
        sizesCsv = "38,40,42,44",
        colorsCsv = "Areia,Preto,Azul Noite",
        brand = "Aurélia Couture"
      )
    )

    // Store 2: Urban Shoes
    val store2 = StoreEntity(
      id = "store_urban_shoes",
      ownerId = marta.id,
      name = "Urban Shoes MZ",
      slug = "urban-shoes",
      description = "Os melhores ténis urbanos, sapatilhas e botas de qualidade na Matola e Maputo.",
      businessCategory = "Calçado",
      whatsappNumber = "+258859876543",
      city = "Matola Rio",
      coverUrl = "banner_shoes.jpg",
      primaryColor = 0xFFD97706,
      secondaryColor = 0xFF2563EB,
      visualTemplate = "SHOES",
      isPublished = true,
      planId = "PRO"
    )
    db.storeDao().insertStore(store2)

    val cat2Sneakers = CategoryEntity(id = "cat_2_1", storeId = store2.id, name = "Sneakers & Ténis", sortOrder = 1)
    val cat2Botas = CategoryEntity(id = "cat_2_2", storeId = store2.id, name = "Botas & Botins", sortOrder = 2)
    db.categoryDao().insertCategory(cat2Sneakers)
    db.categoryDao().insertCategory(cat2Botas)

    db.productDao().insertProduct(
      ProductEntity(
        id = "prod_2_1",
        storeId = store2.id,
        categoryId = cat2Sneakers.id,
        name = "Ténis Retro Runner 2026",
        description = "Sneaker esportivo e casual com amortecimento em gel, solado antiderrapante e detalhes em camurça.",
        price = 3900.0,
        promotionalPrice = 3450.0,
        imageUrl = "banner_shoes.jpg",
        isAvailable = true,
        isFeatured = true,
        sizesCsv = "39,40,41,42,43,44",
        colorsCsv = "Branco/Cinza,Preto/Vermelho,Azul Marinho",
        brand = "Urban Sport"
      )
    )
    db.productDao().insertProduct(
      ProductEntity(
        id = "prod_2_2",
        storeId = store2.id,
        categoryId = cat2Botas.id,
        name = "Botim Chelsea Couro Legítimo",
        description = "Bota estilo chelsea artesanal com elásticos reforçados nas laterais e puxador traseiro.",
        price = 5200.0,
        promotionalPrice = 4600.0,
        imageUrl = "banner_shoes.jpg",
        isAvailable = true,
        isFeatured = true,
        sizesCsv = "40,41,42,43",
        colorsCsv = "Castanho Havana,Preto Nobre",
        brand = "Crafted Footwear"
      )
    )
    db.productDao().insertProduct(
      ProductEntity(
        id = "prod_2_3",
        storeId = store2.id,
        categoryId = cat2Sneakers.id,
        name = "Sapatilhas Classic Leather White",
        description = "Sapatilhas minimalistas em couro ecológico macio. Um clássico indispensável para o dia a dia.",
        price = 2800.0,
        promotionalPrice = null,
        imageUrl = "banner_shoes.jpg",
        isAvailable = true,
        isFeatured = false,
        sizesCsv = "38,39,40,41,42",
        colorsCsv = "Branco Total,Branco com Verde",
        brand = "Urban Sport"
      )
    )

    // Store 3: Essência Perfumes
    val store3 = StoreEntity(
      id = "store_essencia_perfumes",
      ownerId = marta.id,
      name = "Essência Perfumes",
      slug = "essencia-perfumes",
      description = "Fragrâncias originais, perfumaria de nicho e águas de colónia de prestígio.",
      businessCategory = "Perfumaria",
      whatsappNumber = "+258863334455",
      city = "Maputo, Sommerschield",
      coverUrl = "banner_perfume.jpg",
      primaryColor = 0xFF7C3AED,
      secondaryColor = 0xFFD97706,
      visualTemplate = "PERFUME",
      isPublished = true,
      planId = "PRO"
    )
    db.storeDao().insertStore(store3)

    val cat3Nicho = CategoryEntity(id = "cat_3_1", storeId = store3.id, name = "Fragrâncias de Nicho", sortOrder = 1)
    val cat3Fem = CategoryEntity(id = "cat_3_2", storeId = store3.id, name = "Perfumes Femininos", sortOrder = 2)
    db.categoryDao().insertCategory(cat3Nicho)
    db.categoryDao().insertCategory(cat3Fem)

    db.productDao().insertProduct(
      ProductEntity(
        id = "prod_3_1",
        storeId = store3.id,
        categoryId = cat3Nicho.id,
        name = "Maison Elegance Royale 100ml",
        description = "Fragrância amadeirada especiada com notas de açafrão, âmbar cinzento e sândalo nobre. Fixação extrema de até 18 horas.",
        price = 6800.0,
        promotionalPrice = 6100.0,
        imageUrl = "banner_perfume.jpg",
        isAvailable = true,
        isFeatured = true,
        volume = "100ml",
        brand = "Maison Royale"
      )
    )
    db.productDao().insertProduct(
      ProductEntity(
        id = "prod_3_2",
        storeId = store3.id,
        categoryId = cat3Fem.id,
        name = "Aurora D'Or et de Soie 75ml",
        description = "Floral luminoso com jasmim sambac, baunilha de Madagáscar e mandarina fresca.",
        price = 4500.0,
        promotionalPrice = null,
        imageUrl = "banner_perfume.jpg",
        isAvailable = true,
        isFeatured = true,
        volume = "75ml",
        brand = "Maison Aurora"
      )
    )
    db.productDao().insertProduct(
      ProductEntity(
        id = "prod_3_3",
        storeId = store3.id,
        categoryId = cat3Nicho.id,
        name = "Velvet Oud Nocturne 100ml",
        description = "Oud sofisticado com toques de couro macio, rosa damascena e cardamomo.",
        price = 5400.0,
        promotionalPrice = 4800.0,
        imageUrl = "banner_perfume.jpg",
        isAvailable = true,
        isFeatured = false,
        volume = "100ml",
        brand = "Velvet Fragrances"
      )
    )

    // Store 4: Acessórios Premium
    val store4 = StoreEntity(
      id = "store_acessorios_premium",
      ownerId = marta.id,
      name = "Acessórios Premium MZ",
      slug = "acessorios-premium",
      description = "Relógios elegantes, carteiras em pele e joalharia contemporânea.",
      businessCategory = "Acessórios",
      whatsappNumber = "+258872221199",
      city = "Maputo Central",
      coverUrl = "banner_fashion.jpg",
      primaryColor = 0xFF0D9488,
      secondaryColor = 0xFF475569,
      visualTemplate = "ACCESSORIES",
      isPublished = true,
      planId = "FREE"
    )
    db.storeDao().insertStore(store4)

    val cat4Relogios = CategoryEntity(id = "cat_4_1", storeId = store4.id, name = "Relógios", sortOrder = 1)
    val cat4Couro = CategoryEntity(id = "cat_4_2", storeId = store4.id, name = "Carteiras & Cintos", sortOrder = 2)
    db.categoryDao().insertCategory(cat4Relogios)
    db.categoryDao().insertCategory(cat4Couro)

    db.productDao().insertProduct(
      ProductEntity(
        id = "prod_4_1",
        storeId = store4.id,
        categoryId = cat4Relogios.id,
        name = "Relógio Cronógrafo Steel Minimalist",
        description = "Mostrador em safira resistente a riscos, caixa em aço inoxidável 316L e resistência 5 ATM.",
        price = 3800.0,
        promotionalPrice = 3300.0,
        imageUrl = "banner_fashion.jpg",
        isAvailable = true,
        isFeatured = true,
        colorsCsv = "Prata Escovada,Preto Fosco,Ouro Rosê",
        brand = "Cronos Elite"
      )
    )
    db.productDao().insertProduct(
      ProductEntity(
        id = "prod_4_2",
        storeId = store4.id,
        categoryId = cat4Couro.id,
        name = "Carteira Slim Couro Genuíno com RFID",
        description = "Carteira compacta com bloqueio RFID, espaço para 8 cartões e compartimento para notas dobradas.",
        price = 1200.0,
        promotionalPrice = null,
        imageUrl = "banner_fashion.jpg",
        isAvailable = true,
        isFeatured = false,
        colorsCsv = "Castanho Chocolate,Preto Clássico",
        brand = "Artisan Leather"
      )
    )

    // Seed initial demo analytics events
    repeat(18) { db.analyticsDao().insertEvent(AnalyticsEventEntity(UUID.randomUUID().toString(), store1.id, null, "VIEW_STORE")) }
    repeat(7) { db.analyticsDao().insertEvent(AnalyticsEventEntity(UUID.randomUUID().toString(), store1.id, null, "WHATSAPP_CLICK")) }
    repeat(3) { db.analyticsDao().insertEvent(AnalyticsEventEntity(UUID.randomUUID().toString(), store1.id, null, "SHARE")) }

    repeat(12) { db.analyticsDao().insertEvent(AnalyticsEventEntity(UUID.randomUUID().toString(), store2.id, null, "VIEW_STORE")) }
    repeat(5) { db.analyticsDao().insertEvent(AnalyticsEventEntity(UUID.randomUUID().toString(), store2.id, null, "WHATSAPP_CLICK")) }
  }
}
