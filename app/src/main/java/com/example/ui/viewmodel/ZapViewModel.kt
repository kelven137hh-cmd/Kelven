package com.example.ui.viewmodel

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.widget.Toast
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.example.data.local.model.CategoryEntity
import com.example.data.local.model.OrderHistoryEntity
import com.example.data.local.model.ProductEntity
import com.example.data.local.model.ProfileEntity
import com.example.data.local.model.StoreEntity
import com.example.data.repository.ZapRepository
import com.example.model.CartItem
import com.example.model.MozFormatters
import com.example.model.PlanType
import com.example.model.StoreAnalytics
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import java.net.URLEncoder
import java.util.UUID

sealed class AppScreen {
  object Landing : AppScreen()
  object Auth : AppScreen()
  object Wizard : AppScreen()
  object Dashboard : AppScreen()
  data class PublicCatalog(val slug: String) : AppScreen()
  object AdminPanel : AppScreen()
  object PlanUpgrade : AppScreen()
}

class ZapViewModel(private val repository: ZapRepository) : ViewModel() {

  private val _currentScreen = MutableStateFlow<AppScreen>(AppScreen.Landing)
  val currentScreen: StateFlow<AppScreen> = _currentScreen.asStateFlow()

  private val _currentProfile = MutableStateFlow<ProfileEntity?>(null)
  val currentProfile: StateFlow<ProfileEntity?> = _currentProfile.asStateFlow()

  private val _activeStore = MutableStateFlow<StoreEntity?>(null)
  val activeStore: StateFlow<StoreEntity?> = _activeStore.asStateFlow()

  private val _myStores = MutableStateFlow<List<StoreEntity>>(emptyList())
  val myStores: StateFlow<List<StoreEntity>> = _myStores.asStateFlow()

  private val _allStores = MutableStateFlow<List<StoreEntity>>(emptyList())
  val allStores: StateFlow<List<StoreEntity>> = _allStores.asStateFlow()

  private val _categories = MutableStateFlow<List<CategoryEntity>>(emptyList())
  val categories: StateFlow<List<CategoryEntity>> = _categories.asStateFlow()

  private val _products = MutableStateFlow<List<ProductEntity>>(emptyList())
  val products: StateFlow<List<ProductEntity>> = _products.asStateFlow()

  private val _cartItems = MutableStateFlow<List<CartItem>>(emptyList())
  val cartItems: StateFlow<List<CartItem>> = _cartItems.asStateFlow()

  private val _storeAnalytics = MutableStateFlow(StoreAnalytics())
  val storeAnalytics: StateFlow<StoreAnalytics> = _storeAnalytics.asStateFlow()

  private val _orders = MutableStateFlow<List<OrderHistoryEntity>>(emptyList())
  val orders: StateFlow<List<OrderHistoryEntity>> = _orders.asStateFlow()

  private val _totalPlatformWhatsappClicks = MutableStateFlow(0)
  val totalPlatformWhatsappClicks: StateFlow<Int> = _totalPlatformWhatsappClicks.asStateFlow()

  // Selected product for detail modal in public catalog
  private val _selectedProductForDetail = MutableStateFlow<ProductEntity?>(null)
  val selectedProductForDetail: StateFlow<ProductEntity?> = _selectedProductForDetail.asStateFlow()

  // Global UI message (Toast or Snackbar alert)
  private val _userMessage = MutableStateFlow<String?>(null)
  val userMessage: StateFlow<String?> = _userMessage.asStateFlow()

  init {
    viewModelScope.launch {
      repository.ensureSampleData()
      _currentProfile.value = repository.currentProfile

      // Collect all stores
      repository.getAllStores().collectLatest { storeList ->
        _allStores.value = storeList
        val prof = _currentProfile.value
        if (prof != null) {
          _myStores.value = storeList.filter { it.ownerId == prof.id }
          if (_activeStore.value == null && _myStores.value.isNotEmpty()) {
            selectStore(_myStores.value.first().id)
          }
        }
        _totalPlatformWhatsappClicks.value = repository.getTotalPlatformWhatsappClicks()
      }
    }
  }

  fun navigateTo(screen: AppScreen) {
    _currentScreen.value = screen
    if (screen is AppScreen.PublicCatalog) {
      loadPublicStore(screen.slug)
    }
  }

  fun showMessage(msg: String) {
    _userMessage.value = msg
  }

  fun clearMessage() {
    _userMessage.value = null
  }

  fun selectStore(storeId: String) {
    viewModelScope.launch {
      val store = repository.getStoreById(storeId)
      if (store != null) {
        _activeStore.value = store
        loadStoreData(store.id)
      }
    }
  }

  fun loadPublicStore(slug: String) {
    viewModelScope.launch {
      val store = repository.getStoreBySlug(slug)
      if (store != null) {
        _activeStore.value = store
        loadStoreData(store.id)
        repository.recordStoreView(store.id)
        refreshAnalytics(store.id)
      }
    }
  }

  private fun loadStoreData(storeId: String) {
    viewModelScope.launch {
      repository.getCategoriesByStore(storeId).collectLatest { catList ->
        _categories.value = catList
      }
    }
    viewModelScope.launch {
      repository.getProductsByStore(storeId).collectLatest { prodList ->
        _products.value = prodList
      }
    }
    viewModelScope.launch {
      repository.getOrdersByStore(storeId).collectLatest { ordList ->
        _orders.value = ordList
      }
    }
    refreshAnalytics(storeId)
  }

  fun refreshAnalytics(storeId: String) {
    viewModelScope.launch {
      _storeAnalytics.value = repository.getStoreAnalytics(storeId)
      _totalPlatformWhatsappClicks.value = repository.getTotalPlatformWhatsappClicks()
    }
  }

  fun login(email: String) {
    viewModelScope.launch {
      val profile = repository.login(email)
      _currentProfile.value = profile
      val userStores = _allStores.value.filter { it.ownerId == profile.id }
      _myStores.value = userStores
      if (userStores.isNotEmpty()) {
        selectStore(userStores.first().id)
        _currentScreen.value = AppScreen.Dashboard
      } else {
        _currentScreen.value = AppScreen.Wizard
      }
      showMessage("Bem-vindo(a), ${profile.name}!")
    }
  }

  fun register(name: String, email: String, role: String = "MERCHANT") {
    viewModelScope.launch {
      val profile = repository.register(name, email, role)
      _currentProfile.value = profile
      _currentScreen.value = if (role == "ADMIN") AppScreen.AdminPanel else AppScreen.Wizard
      showMessage("Conta criada com sucesso!")
    }
  }

  fun logout() {
    _currentProfile.value = null
    repository.setCurrentUser(null)
    _currentScreen.value = AppScreen.Landing
    showMessage("Sessão terminada.")
  }

  // Wizard store creation
  fun finishWizard(
    name: String,
    slug: String,
    businessCategory: String,
    whatsappNumber: String,
    city: String,
    visualTemplate: String,
    primaryColor: Long,
    secondaryColor: Long,
    initialProducts: List<Pair<String, Double>> = emptyList()
  ) {
    viewModelScope.launch {
      val profile = _currentProfile.value ?: repository.login("comerciante@zapcatalogo.mz")
      val cleanSlug = slug.trim().lowercase().replace("\\s+".toRegex(), "-").replace("[^a-z0-9-]".toRegex(), "")
      val store = StoreEntity(
        id = UUID.randomUUID().toString(),
        ownerId = profile.id,
        name = name.trim(),
        slug = if (cleanSlug.isBlank()) "loja-${UUID.randomUUID().toString().take(6)}" else cleanSlug,
        description = "Catálogo oficial no WhatsApp de $name em $city.",
        businessCategory = businessCategory,
        whatsappNumber = MozFormatters.normalizeMozPhone(whatsappNumber),
        city = city.ifBlank { "Maputo" },
        coverUrl = when (businessCategory) {
          "Calçado" -> "banner_shoes.jpg"
          "Perfumaria" -> "banner_perfume.jpg"
          else -> "banner_fashion.jpg"
        },
        visualTemplate = visualTemplate,
        primaryColor = primaryColor,
        secondaryColor = secondaryColor,
        isPublished = true,
        planId = "FREE"
      )
      repository.createStore(store)
      _activeStore.value = store

      // Create default category
      val defaultCat = CategoryEntity(
        id = UUID.randomUUID().toString(),
        storeId = store.id,
        name = "Destaques",
        sortOrder = 1
      )
      repository.insertCategory(defaultCat)

      // Add initial products if any
      initialProducts.forEach { (pName, pPrice) ->
        val prod = ProductEntity(
          id = UUID.randomUUID().toString(),
          storeId = store.id,
          categoryId = defaultCat.id,
          name = pName,
          price = pPrice,
          imageUrl = store.coverUrl,
          isAvailable = true,
          isFeatured = true
        )
        repository.insertProduct(prod)
      }

      loadStoreData(store.id)
      _currentScreen.value = AppScreen.Dashboard
      showMessage("Loja '${store.name}' criada e publicada com sucesso!")
    }
  }

  fun updateStoreDetails(store: StoreEntity) {
    viewModelScope.launch {
      repository.updateStore(store)
      _activeStore.value = store
      showMessage("Loja atualizada com sucesso.")
    }
  }

  fun toggleStorePublish(storeId: String, isPublished: Boolean) {
    viewModelScope.launch {
      repository.togglePublishStore(storeId, isPublished)
      val current = _activeStore.value
      if (current?.id == storeId) {
        _activeStore.value = current.copy(isPublished = isPublished)
      }
      showMessage(if (isPublished) "Catálogo publicado e online!" else "Catálogo pausado.")
    }
  }

  // Plan Management
  fun updatePlan(storeId: String, planId: String) {
    viewModelScope.launch {
      repository.updateStorePlan(storeId, planId)
      val current = _activeStore.value
      if (current?.id == storeId) {
        _activeStore.value = current.copy(planId = planId)
      }
      showMessage("Plano atualizado para ${PlanType.fromId(planId).title}!")
    }
  }

  // Product CRUD
  fun saveProduct(
    id: String?,
    name: String,
    description: String,
    price: Double,
    promoPrice: Double?,
    categoryId: String,
    sizes: String,
    colors: String,
    volume: String,
    isAvailable: Boolean,
    isFeatured: Boolean
  ): Boolean {
    val store = _activeStore.value ?: return false

    if (id == null) {
      val currentCount = _products.value.size
      val plan = PlanType.fromId(store.planId)
      if (currentCount >= plan.maxProducts) {
        showMessage("Atingiu o limite de ${plan.maxProducts} produtos do ${plan.title}. Faça upgrade para o Plano Pro!")
        _currentScreen.value = AppScreen.PlanUpgrade
        return false
      }
    }

    viewModelScope.launch {
      val prod = ProductEntity(
        id = id ?: UUID.randomUUID().toString(),
        storeId = store.id,
        categoryId = categoryId,
        name = name.trim(),
        description = description.trim(),
        price = price,
        promotionalPrice = promoPrice,
        imageUrl = store.coverUrl.ifBlank { "banner_fashion.jpg" },
        isAvailable = isAvailable,
        isFeatured = isFeatured,
        sizesCsv = sizes.trim(),
        colorsCsv = colors.trim(),
        volume = volume.trim()
      )
      if (id == null) {
        repository.insertProduct(prod)
        showMessage("Produto '${prod.name}' adicionado com sucesso!")
      } else {
        repository.updateProduct(prod)
        showMessage("Produto atualizado!")
      }
    }
    return true
  }

  fun deleteProduct(product: ProductEntity) {
    viewModelScope.launch {
      repository.deleteProduct(product)
      showMessage("Produto '${product.name}' eliminado.")
    }
  }

  fun duplicateProduct(product: ProductEntity) {
    val store = _activeStore.value ?: return
    val plan = PlanType.fromId(store.planId)
    if (_products.value.size >= plan.maxProducts) {
      showMessage("Limite de produtos atingido (${plan.maxProducts}). Faça upgrade para o Plano Pro!")
      return
    }
    viewModelScope.launch {
      val dup = product.copy(
        id = UUID.randomUUID().toString(),
        name = "${product.name} (Cópia)",
        createdAt = System.currentTimeMillis()
      )
      repository.insertProduct(dup)
      showMessage("Produto duplicado com sucesso!")
    }
  }

  // Category CRUD
  fun addCategory(name: String) {
    val store = _activeStore.value ?: return
    if (name.isBlank()) return
    viewModelScope.launch {
      val cat = CategoryEntity(
        id = UUID.randomUUID().toString(),
        storeId = store.id,
        name = name.trim(),
        sortOrder = _categories.value.size + 1
      )
      repository.insertCategory(cat)
      showMessage("Categoria '${cat.name}' criada!")
    }
  }

  fun deleteCategory(category: CategoryEntity) {
    viewModelScope.launch {
      repository.deleteCategory(category)
      showMessage("Categoria removida.")
    }
  }

  // Public Catalog & Product Detail
  fun openProductDetail(product: ProductEntity) {
    _selectedProductForDetail.value = product
    val store = _activeStore.value
    if (store != null) {
      viewModelScope.launch {
        repository.recordProductView(store.id, product.id)
      }
    }
  }

  fun closeProductDetail() {
    _selectedProductForDetail.value = null
  }

  // Shopping Cart
  fun addToCart(product: ProductEntity, size: String = "", color: String = "", quantity: Int = 1) {
    val current = _cartItems.value.toMutableList()
    val existingIndex = current.indexOfFirst {
      it.product.id == product.id && it.selectedSize == size && it.selectedColor == color
    }
    if (existingIndex >= 0) {
      val existing = current[existingIndex]
      current[existingIndex] = existing.copy(quantity = existing.quantity + quantity)
    } else {
      current.add(CartItem(product, quantity, size, color))
    }
    _cartItems.value = current
    showMessage("${product.name} adicionado ao carrinho!")
  }

  fun updateCartQuantity(index: Int, newQuantity: Int) {
    if (index !in _cartItems.value.indices) return
    val current = _cartItems.value.toMutableList()
    if (newQuantity <= 0) {
      current.removeAt(index)
    } else {
      current[index] = current[index].copy(quantity = newQuantity)
    }
    _cartItems.value = current
  }

  fun removeCartItem(index: Int) {
    if (index !in _cartItems.value.indices) return
    val current = _cartItems.value.toMutableList()
    current.removeAt(index)
    _cartItems.value = current
  }

  fun clearCart() {
    _cartItems.value = emptyList()
  }

  fun recordShare(storeId: String) {
    viewModelScope.launch {
      repository.recordShare(storeId)
      refreshAnalytics(storeId)
    }
  }

  // WhatsApp Order Generation & Intent Dispatch
  fun submitWhatsappOrder(
    context: Context,
    customerName: String,
    customerPhone: String,
    deliveryLocation: String,
    deliveryType: String,
    notes: String
  ) {
    val store = _activeStore.value ?: return
    val items = _cartItems.value
    if (items.isEmpty()) {
      showMessage("O carrinho está vazio.")
      return
    }

    val totalAmount = items.sumOf { it.subtotal }
    val sb = StringBuilder()
    sb.append("Olá! Gostaria de fazer uma encomenda na loja ${store.name}.\n\n")

    if (customerName.isNotBlank()) {
      sb.append("👤 Cliente: ${customerName.trim()}\n")
    }
    if (customerPhone.isNotBlank()) {
      sb.append("📞 Contacto: ${customerPhone.trim()}\n")
    }
    if (deliveryLocation.isNotBlank()) {
      sb.append("📍 Localização: ${deliveryLocation.trim()} ($deliveryType)\n")
    }
    if (notes.isNotBlank()) {
      sb.append("📝 Obs: ${notes.trim()}\n")
    }

    sb.append("\n🛍️ PRODUTOS:\n")
    items.forEachIndexed { i, item ->
      val unitPrice = item.product.promotionalPrice ?: item.product.price
      sb.append("${i + 1}. ${item.product.name}")
      val details = mutableListOf<String>()
      if (item.selectedSize.isNotBlank()) details.add("Tam: ${item.selectedSize}")
      if (item.selectedColor.isNotBlank()) details.add("Cor: ${item.selectedColor}")
      if (item.product.volume.isNotBlank()) details.add("Vol: ${item.product.volume}")
      if (details.isNotEmpty()) {
        sb.append(" (${details.joinToString(", ")})")
      }
      sb.append("\n")
      sb.append("   Quantidade: ${item.quantity}\n")
      sb.append("   Preço unitário: ${MozFormatters.formatMt(unitPrice)}\n")
      sb.append("   Subtotal: ${MozFormatters.formatMt(item.subtotal)}\n\n")
    }

    sb.append("💰 TOTAL ESTIMADO DOS PRODUTOS: ${MozFormatters.formatMt(totalAmount)}\n\n")
    sb.append("Poderiam confirmar a disponibilidade e informar-me sobre a entrega?")

    val formattedMessage = sb.toString()

    // Save order in database
    viewModelScope.launch {
      val order = OrderHistoryEntity(
        id = UUID.randomUUID().toString(),
        storeId = store.id,
        customerName = customerName.ifBlank { "Comprador Anónimo" },
        customerPhone = customerPhone,
        deliveryLocation = deliveryLocation,
        deliveryType = deliveryType,
        itemsSummary = items.joinToString("; ") { "${it.quantity}x ${it.product.name}" },
        totalAmount = totalAmount,
        formattedMessage = formattedMessage
      )
      repository.recordOrder(order)
      refreshAnalytics(store.id)
    }

    // Launch WhatsApp
    val cleanPhone = MozFormatters.normalizeMozPhone(store.whatsappNumber)
    val encodedText = URLEncoder.encode(formattedMessage, "UTF-8")
    val url = "https://wa.me/$cleanPhone?text=$encodedText"

    val intent = Intent(Intent.ACTION_VIEW, Uri.parse(url)).apply {
      flags = Intent.FLAG_ACTIVITY_NEW_TASK
    }

    try {
      context.startActivity(intent)
      clearCart()
      showMessage("A abrir WhatsApp para enviar a sua encomenda...")
    } catch (e: Exception) {
      // Fallback: copy to clipboard or open in browser
      val browserIntent = Intent(Intent.ACTION_VIEW, Uri.parse(url)).apply {
        flags = Intent.FLAG_ACTIVITY_NEW_TASK
      }
      try {
        context.startActivity(browserIntent)
        clearCart()
      } catch (e2: Exception) {
        Toast.makeText(context, "Erro ao abrir WhatsApp: ${e2.message}", Toast.LENGTH_LONG).show()
      }
    }
  }

  class Factory(private val repository: ZapRepository) : ViewModelProvider.Factory {
    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
      return ZapViewModel(repository) as T
    }
  }
}
