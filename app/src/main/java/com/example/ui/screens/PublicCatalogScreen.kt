package com.example.ui.screens

import android.content.Context
import android.content.Intent
import android.net.Uri
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.Phone
import androidx.compose.material.icons.filled.Remove
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Share
import androidx.compose.material.icons.filled.ShoppingBag
import androidx.compose.material.icons.filled.ShoppingCart
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.filled.Storefront
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ElevatedCard
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.RadioButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import coil.request.ImageRequest
import com.example.R
import com.example.data.local.model.CategoryEntity
import com.example.data.local.model.ProductEntity
import com.example.data.local.model.StoreEntity
import com.example.model.MozFormatters
import com.example.ui.theme.ZapAmber
import com.example.ui.theme.ZapEmerald
import com.example.ui.theme.ZapEmeraldDark
import com.example.ui.theme.ZapEmeraldLight
import com.example.ui.theme.ZapRed
import com.example.ui.theme.ZapSlateDark
import com.example.ui.theme.ZapSlateMuted
import com.example.ui.theme.ZapWhatsappGreen
import com.example.ui.viewmodel.ZapViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PublicCatalogScreen(
  viewModel: ZapViewModel,
  onBack: () -> Unit
) {
  val context = LocalContext.current
  val activeStore by viewModel.activeStore.collectAsState()
  val categories by viewModel.categories.collectAsState()
  val products by viewModel.products.collectAsState()
  val cartItems by viewModel.cartItems.collectAsState()
  val selectedDetailProduct by viewModel.selectedProductForDetail.collectAsState()

  var selectedCategoryId by remember { mutableStateOf<String?>(null) }
  var searchQuery by remember { mutableStateOf("") }
  var showCartSheet by remember { mutableStateOf(false) }

  val store = activeStore

  if (store == null) {
    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
      Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Text("Loja não encontrada ou link indisponível.", color = ZapSlateMuted)
        Spacer(modifier = Modifier.height(12.dp))
        Button(onClick = onBack) { Text("Voltar") }
      }
    }
    return
  }

  // Filter products
  val filteredProducts = products.filter { prod ->
    val matchesCategory = selectedCategoryId == null || prod.categoryId == selectedCategoryId
    val matchesSearch = searchQuery.isBlank() || prod.name.contains(searchQuery, ignoreCase = true) ||
      prod.description.contains(searchQuery, ignoreCase = true)
    matchesCategory && matchesSearch
  }

  val totalCartCount = cartItems.sumOf { it.quantity }
  val totalCartAmount = cartItems.sumOf { it.subtotal }

  Scaffold(
    topBar = {
      // Store Top Bar
      Surface(
        modifier = Modifier.fillMaxWidth(),
        color = Color.White,
        tonalElevation = 2.dp
      ) {
        Row(
          modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 12.dp, vertical = 8.dp),
          verticalAlignment = Alignment.CenterVertically,
          horizontalArrangement = Arrangement.SpaceBetween
        ) {
          Row(verticalAlignment = Alignment.CenterVertically) {
            IconButton(onClick = onBack, modifier = Modifier.testTag("catalog_back_btn")) {
              Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Voltar", tint = ZapSlateDark)
            }
            Spacer(modifier = Modifier.width(4.dp))
            Column {
              Text(store.name, fontWeight = FontWeight.Bold, fontSize = 15.sp, color = ZapSlateDark)
              Text("zapcatalogo.mz/loja/${store.slug}", fontSize = 10.sp, color = ZapEmerald)
            }
          }

          Row(verticalAlignment = Alignment.CenterVertically) {
            IconButton(
              onClick = {
                viewModel.recordShare(store.id)
                val shareIntent = Intent(Intent.ACTION_SEND).apply {
                  type = "text/plain"
                  putExtra(Intent.EXTRA_TEXT, "Conheça a loja ${store.name} no WhatsApp: https://zapcatalogo.mz/loja/${store.slug}")
                }
                context.startActivity(Intent.createChooser(shareIntent, "Partilhar Catálogo"))
              },
              modifier = Modifier.testTag("catalog_share_btn")
            ) {
              Icon(Icons.Default.Share, contentDescription = "Partilhar", tint = ZapSlateDark)
            }

            // WhatsApp Direct Contact Button
            IconButton(
              onClick = {
                val cleanPhone = MozFormatters.normalizeMozPhone(store.whatsappNumber)
                val intent = Intent(Intent.ACTION_VIEW, Uri.parse("https://wa.me/$cleanPhone"))
                try {
                  context.startActivity(intent)
                } catch (e: Exception) {
                  // Fallback
                }
              },
              modifier = Modifier.testTag("catalog_direct_whatsapp_btn")
            ) {
              Icon(Icons.Default.Phone, contentDescription = "Falar no WhatsApp", tint = ZapWhatsappGreen)
            }
          }
        }
      }
    },
    floatingActionButton = {
      if (totalCartCount > 0) {
        FloatingActionButton(
          onClick = { showCartSheet = true },
          containerColor = ZapWhatsappGreen,
          contentColor = Color.White,
          shape = RoundedCornerShape(24.dp),
          modifier = Modifier.testTag("floating_cart_button")
        ) {
          Row(
            modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp),
            verticalAlignment = Alignment.CenterVertically
          ) {
            Icon(Icons.Default.ShoppingCart, contentDescription = "Carrinho")
            Spacer(modifier = Modifier.width(8.dp))
            Column {
              Text("$totalCartCount itens", fontWeight = FontWeight.Bold, fontSize = 12.sp)
              Text(MozFormatters.formatMt(totalCartAmount), fontSize = 11.sp)
            }
          }
        }
      }
    }
  ) { innerPadding ->
    LazyColumn(
      modifier = Modifier
        .fillMaxSize()
        .background(MaterialTheme.colorScheme.background)
        .padding(innerPadding),
      contentPadding = PaddingValues(bottom = 80.dp)
    ) {
      // Store Header Banner & Info
      item {
        StoreHeaderCard(store = store)
      }

      // Search & Filters
      item {
        Column(modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp)) {
          OutlinedTextField(
            value = searchQuery,
            onValueChange = { searchQuery = it },
            placeholder = { Text("Pesquisar produtos...") },
            leadingIcon = { Icon(Icons.Default.Search, contentDescription = null, tint = ZapSlateMuted) },
            trailingIcon = {
              if (searchQuery.isNotBlank()) {
                IconButton(onClick = { searchQuery = "" }) {
                  Icon(Icons.Default.Clear, contentDescription = "Limpar")
                }
              }
            },
            singleLine = true,
            modifier = Modifier
              .fillMaxWidth()
              .testTag("catalog_search_field"),
            shape = RoundedCornerShape(12.dp)
          )

          Spacer(modifier = Modifier.height(10.dp))

          // Categories Chips
          LazyRow(
            horizontalArrangement = Arrangement.spacedBy(8.dp)
          ) {
            item {
              FilterChip(
                selected = selectedCategoryId == null,
                onClick = { selectedCategoryId = null },
                label = { Text("Todos (${products.size})") },
                colors = FilterChipDefaults.filterChipColors(
                  selectedContainerColor = Color(store.primaryColor),
                  selectedLabelColor = Color.White
                )
              )
            }
            items(categories) { cat ->
              FilterChip(
                selected = selectedCategoryId == cat.id,
                onClick = { selectedCategoryId = cat.id },
                label = { Text(cat.name) },
                colors = FilterChipDefaults.filterChipColors(
                  selectedContainerColor = Color(store.primaryColor),
                  selectedLabelColor = Color.White
                )
              )
            }
          }
        }
      }

      // Product Grid List
      if (filteredProducts.isEmpty()) {
        item {
          Box(
            modifier = Modifier
              .fillMaxWidth()
              .height(200.dp),
            contentAlignment = Alignment.Center
          ) {
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
              Icon(Icons.Default.ShoppingBag, contentDescription = null, tint = Color.LightGray, modifier = Modifier.size(48.dp))
              Spacer(modifier = Modifier.height(8.dp))
              Text("Nenhum produto encontrado nesta categoria.", fontSize = 13.sp, color = ZapSlateMuted)
            }
          }
        }
      } else {
        items(filteredProducts.chunked(2)) { pair ->
          Row(
            modifier = Modifier
              .fillMaxWidth()
              .padding(horizontal = 16.dp, vertical = 6.dp),
            horizontalArrangement = Arrangement.spacedBy(10.dp)
          ) {
            pair.forEach { prod ->
              Box(modifier = Modifier.weight(1f)) {
                ProductItemCard(
                  product = prod,
                  store = store,
                  onDetailClick = { viewModel.openProductDetail(prod) },
                  onAddToCartDirect = {
                    val defaultSize = prod.sizesCsv.split(",").firstOrNull()?.trim() ?: ""
                    val defaultColor = prod.colorsCsv.split(",").firstOrNull()?.trim() ?: ""
                    viewModel.addToCart(prod, size = defaultSize, color = defaultColor, quantity = 1)
                  }
                )
              }
            }
            if (pair.size == 1) {
              Spacer(modifier = Modifier.weight(1f))
            }
          }
        }
      }
    }
  }

  // Product Detail Modal Dialog
  if (selectedDetailProduct != null) {
    ProductDetailDialog(
      product = selectedDetailProduct!!,
      store = store,
      onDismiss = { viewModel.closeProductDetail() },
      onAddToCart = { size, color, qty ->
        viewModel.addToCart(selectedDetailProduct!!, size, color, qty)
        viewModel.closeProductDetail()
      }
    )
  }

  // Cart Bottom Sheet
  if (showCartSheet) {
    ModalBottomSheet(
      onDismissRequest = { showCartSheet = false },
      sheetState = rememberModalBottomSheetState()
    ) {
      CartBottomSheetContent(
        store = store,
        cartItems = cartItems,
        onUpdateQuantity = { idx, qty -> viewModel.updateCartQuantity(idx, qty) },
        onRemoveItem = { idx -> viewModel.removeCartItem(idx) },
        onOrderSubmit = { name, phone, loc, delivType, notes ->
          viewModel.submitWhatsappOrder(
            context = context,
            customerName = name,
            customerPhone = phone,
            deliveryLocation = loc,
            deliveryType = delivType,
            notes = notes
          )
          showCartSheet = false
        }
      )
    }
  }
}

@Composable
fun StoreHeaderCard(store: StoreEntity) {
  val context = LocalContext.current
  Card(
    modifier = Modifier
      .fillMaxWidth()
      .padding(16.dp),
    shape = RoundedCornerShape(16.dp),
    colors = CardDefaults.cardColors(containerColor = Color.White),
    elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
  ) {
    Column {
      // Cover image
      Box(
        modifier = Modifier
          .fillMaxWidth()
          .height(130.dp)
      ) {
        val bannerRes = when (store.businessCategory) {
          "Calçado" -> R.drawable.banner_shoes
          "Perfumaria" -> R.drawable.banner_perfume
          else -> R.drawable.banner_fashion
        }
        AsyncImage(
          model = ImageRequest.Builder(context)
            .data(bannerRes)
            .placeholder(bannerRes)
            .error(bannerRes)
            .build(),
          contentDescription = store.name,
          modifier = Modifier.fillMaxSize(),
          contentScale = ContentScale.Crop
        )

        Surface(
          shape = RoundedCornerShape(6.dp),
          color = Color(store.primaryColor),
          modifier = Modifier
            .align(Alignment.BottomStart)
            .padding(10.dp)
        ) {
          Text(
            text = store.businessCategory,
            color = Color.White,
            fontWeight = FontWeight.Bold,
            fontSize = 11.sp,
            modifier = Modifier.padding(horizontal = 8.dp, vertical = 3.dp)
          )
        }
      }

      Column(modifier = Modifier.padding(14.dp)) {
        Text(
          text = store.name,
          fontWeight = FontWeight.ExtraBold,
          fontSize = 18.sp,
          color = ZapSlateDark
        )

        Row(
          modifier = Modifier.padding(top = 2.dp),
          verticalAlignment = Alignment.CenterVertically
        ) {
          Icon(Icons.Default.LocationOn, contentDescription = null, tint = ZapSlateMuted, modifier = Modifier.size(14.dp))
          Spacer(modifier = Modifier.width(4.dp))
          Text(store.city, fontSize = 12.sp, color = ZapSlateMuted)
        }

        if (store.description.isNotBlank()) {
          Text(
            text = store.description,
            fontSize = 12.sp,
            color = ZapSlateDark,
            lineHeight = 16.sp,
            modifier = Modifier.padding(top = 6.dp)
          )
        }

        Spacer(modifier = Modifier.height(10.dp))

        Surface(
          shape = RoundedCornerShape(8.dp),
          color = ZapEmeraldLight,
          modifier = Modifier.fillMaxWidth()
        ) {
          Row(
            modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp),
            verticalAlignment = Alignment.CenterVertically
          ) {
            Icon(Icons.Default.Phone, contentDescription = null, tint = ZapEmeraldDark, modifier = Modifier.size(14.dp))
            Spacer(modifier = Modifier.width(6.dp))
            Text(
              text = "WhatsApp: ${MozFormatters.formatPhoneDisplay(store.whatsappNumber)}",
              fontSize = 11.sp,
              color = ZapEmeraldDark,
              fontWeight = FontWeight.SemiBold
            )
          }
        }
      }
    }
  }
}

@Composable
fun ProductItemCard(
  product: ProductEntity,
  store: StoreEntity,
  onDetailClick: () -> Unit,
  onAddToCartDirect: () -> Unit
) {
  val context = LocalContext.current
  Card(
    modifier = Modifier
      .fillMaxWidth()
      .clip(RoundedCornerShape(12.dp))
      .clickable { onDetailClick() }
      .testTag("catalog_product_card_${product.id}"),
    colors = CardDefaults.cardColors(containerColor = Color.White),
    elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
  ) {
    Column {
      // Photo with badge
      Box(
        modifier = Modifier
          .fillMaxWidth()
          .height(120.dp)
      ) {
        val imgRes = when (store.businessCategory) {
          "Calçado" -> R.drawable.banner_shoes
          "Perfumaria" -> R.drawable.banner_perfume
          else -> R.drawable.banner_fashion
        }
        AsyncImage(
          model = ImageRequest.Builder(context)
            .data(imgRes)
            .placeholder(imgRes)
            .error(imgRes)
            .build(),
          contentDescription = product.name,
          modifier = Modifier.fillMaxSize(),
          contentScale = ContentScale.Crop
        )

        if (product.isFeatured) {
          Surface(
            shape = RoundedCornerShape(4.dp),
            color = ZapAmber,
            modifier = Modifier
              .align(Alignment.TopStart)
              .padding(6.dp)
          ) {
            Text(
              "Destaque",
              color = Color.White,
              fontWeight = FontWeight.Bold,
              fontSize = 9.sp,
              modifier = Modifier.padding(horizontal = 4.dp, vertical = 2.dp)
            )
          }
        }

        if (product.promotionalPrice != null) {
          Surface(
            shape = RoundedCornerShape(4.dp),
            color = ZapRed,
            modifier = Modifier
              .align(Alignment.TopEnd)
              .padding(6.dp)
          ) {
            Text(
              "Promoção",
              color = Color.White,
              fontWeight = FontWeight.Bold,
              fontSize = 9.sp,
              modifier = Modifier.padding(horizontal = 4.dp, vertical = 2.dp)
            )
          }
        }
      }

      Column(modifier = Modifier.padding(10.dp)) {
        Text(
          text = product.name,
          fontWeight = FontWeight.Bold,
          fontSize = 13.sp,
          color = ZapSlateDark,
          maxLines = 1,
          overflow = TextOverflow.Ellipsis
        )

        val priceToShow = product.promotionalPrice ?: product.price
        Row(
          modifier = Modifier.padding(top = 2.dp),
          verticalAlignment = Alignment.CenterVertically
        ) {
          Text(
            text = MozFormatters.formatMt(priceToShow),
            fontWeight = FontWeight.ExtraBold,
            fontSize = 13.sp,
            color = Color(store.primaryColor)
          )
          if (product.promotionalPrice != null) {
            Spacer(modifier = Modifier.width(6.dp))
            Text(
              text = MozFormatters.formatMt(product.price),
              fontSize = 10.sp,
              color = ZapSlateMuted,
              textDecoration = TextDecoration.LineThrough
            )
          }
        }

        if (product.sizesCsv.isNotBlank()) {
          Text(
            text = "Tam: ${product.sizesCsv}",
            fontSize = 10.sp,
            color = ZapSlateMuted,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis,
            modifier = Modifier.padding(top = 2.dp)
          )
        }

        Spacer(modifier = Modifier.height(8.dp))

        Button(
          onClick = onAddToCartDirect,
          modifier = Modifier
            .fillMaxWidth()
            .height(34.dp)
            .testTag("add_to_cart_btn_${product.id}"),
          colors = ButtonDefaults.buttonColors(containerColor = Color(store.primaryColor)),
          shape = RoundedCornerShape(8.dp),
          contentPadding = PaddingValues(0.dp)
        ) {
          Icon(Icons.Default.Add, contentDescription = null, modifier = Modifier.size(14.dp))
          Spacer(modifier = Modifier.width(4.dp))
          Text("Adicionar", fontSize = 11.sp, fontWeight = FontWeight.Bold)
        }
      }
    }
  }
}

@Composable
fun ProductDetailDialog(
  product: ProductEntity,
  store: StoreEntity,
  onDismiss: () -> Unit,
  onAddToCart: (size: String, color: String, qty: Int) -> Unit
) {
  val context = LocalContext.current
  val availableSizes = remember { product.sizesCsv.split(",").map { it.trim() }.filter { it.isNotBlank() } }
  val availableColors = remember { product.colorsCsv.split(",").map { it.trim() }.filter { it.isNotBlank() } }

  var selectedSize by remember { mutableStateOf(availableSizes.firstOrNull() ?: "") }
  var selectedColor by remember { mutableStateOf(availableColors.firstOrNull() ?: "") }
  var quantity by remember { mutableIntStateOf(1) }

  AlertDialog(
    onDismissRequest = onDismiss,
    title = null,
    text = {
      Column(
        modifier = Modifier
          .fillMaxWidth()
          .verticalScroll(androidx.compose.foundation.rememberScrollState())
      ) {
        // Image Header
        Box(
          modifier = Modifier
            .fillMaxWidth()
            .height(180.dp)
            .clip(RoundedCornerShape(12.dp))
        ) {
          val imgRes = when (store.businessCategory) {
            "Calçado" -> R.drawable.banner_shoes
            "Perfumaria" -> R.drawable.banner_perfume
            else -> R.drawable.banner_fashion
          }
          AsyncImage(
            model = ImageRequest.Builder(context)
              .data(imgRes)
              .placeholder(imgRes)
              .error(imgRes)
              .build(),
            contentDescription = product.name,
            modifier = Modifier.fillMaxSize(),
            contentScale = ContentScale.Crop
          )
        }

        Spacer(modifier = Modifier.height(12.dp))

        Text(product.name, fontWeight = FontWeight.Bold, fontSize = 16.sp, color = ZapSlateDark)

        val priceToShow = product.promotionalPrice ?: product.price
        Row(modifier = Modifier.padding(top = 4.dp), verticalAlignment = Alignment.CenterVertically) {
          Text(MozFormatters.formatMt(priceToShow), fontWeight = FontWeight.ExtraBold, fontSize = 18.sp, color = Color(store.primaryColor))
          if (product.promotionalPrice != null) {
            Spacer(modifier = Modifier.width(8.dp))
            Text(
              MozFormatters.formatMt(product.price),
              fontSize = 12.sp,
              color = ZapSlateMuted,
              textDecoration = TextDecoration.LineThrough
            )
          }
        }

        if (product.description.isNotBlank()) {
          Text(
            product.description,
            fontSize = 12.sp,
            color = ZapSlateDark,
            lineHeight = 16.sp,
            modifier = Modifier.padding(top = 8.dp)
          )
        }

        // Sizes selector
        if (availableSizes.isNotEmpty()) {
          Spacer(modifier = Modifier.height(12.dp))
          Text("Escolhe o Tamanho / Número:", fontSize = 12.sp, fontWeight = FontWeight.Bold, color = ZapSlateDark)
          Spacer(modifier = Modifier.height(4.dp))
          LazyRow(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
            items(availableSizes) { sz ->
              val isSel = selectedSize == sz
              Surface(
                shape = RoundedCornerShape(6.dp),
                color = if (isSel) Color(store.primaryColor) else Color(0xFFF1F5F9),
                modifier = Modifier.clickable { selectedSize = sz }
              ) {
                Text(
                  text = sz,
                  fontSize = 12.sp,
                  fontWeight = FontWeight.Bold,
                  color = if (isSel) Color.White else ZapSlateDark,
                  modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp)
                )
              }
            }
          }
        }

        // Colors selector
        if (availableColors.isNotEmpty()) {
          Spacer(modifier = Modifier.height(12.dp))
          Text("Escolhe a Cor:", fontSize = 12.sp, fontWeight = FontWeight.Bold, color = ZapSlateDark)
          Spacer(modifier = Modifier.height(4.dp))
          LazyRow(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
            items(availableColors) { col ->
              val isSel = selectedColor == col
              Surface(
                shape = RoundedCornerShape(6.dp),
                color = if (isSel) Color(store.primaryColor) else Color(0xFFF1F5F9),
                modifier = Modifier.clickable { selectedColor = col }
              ) {
                Text(
                  text = col,
                  fontSize = 12.sp,
                  fontWeight = FontWeight.Bold,
                  color = if (isSel) Color.White else ZapSlateDark,
                  modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp)
                )
              }
            }
          }
        }

        // Quantity selector
        Spacer(modifier = Modifier.height(14.dp))
        Row(
          modifier = Modifier.fillMaxWidth(),
          horizontalArrangement = Arrangement.SpaceBetween,
          verticalAlignment = Alignment.CenterVertically
        ) {
          Text("Quantidade:", fontSize = 13.sp, fontWeight = FontWeight.Bold, color = ZapSlateDark)
          Row(verticalAlignment = Alignment.CenterVertically) {
            IconButton(
              onClick = { if (quantity > 1) quantity-- },
              modifier = Modifier.size(32.dp)
            ) {
              Icon(Icons.Default.Remove, contentDescription = "Diminuir", tint = ZapSlateDark)
            }
            Text("$quantity", fontWeight = FontWeight.Bold, fontSize = 14.sp, modifier = Modifier.padding(horizontal = 8.dp))
            IconButton(
              onClick = { quantity++ },
              modifier = Modifier.size(32.dp)
            ) {
              Icon(Icons.Default.Add, contentDescription = "Aumentar", tint = ZapSlateDark)
            }
          }
        }
      }
    },
    confirmButton = {
      Button(
        onClick = { onAddToCart(selectedSize, selectedColor, quantity) },
        colors = ButtonDefaults.buttonColors(containerColor = Color(store.primaryColor)),
        shape = RoundedCornerShape(8.dp),
        modifier = Modifier.fillMaxWidth()
      ) {
        Icon(Icons.Default.ShoppingBag, contentDescription = null, modifier = Modifier.size(16.dp))
        Spacer(modifier = Modifier.width(6.dp))
        Text("Adicionar ao Carrinho")
      }
    },
    dismissButton = {
      TextButton(onClick = onDismiss, modifier = Modifier.fillMaxWidth()) {
        Text("Fechar")
      }
    }
  )
}

@Composable
fun CartBottomSheetContent(
  store: StoreEntity,
  cartItems: List<com.example.model.CartItem>,
  onUpdateQuantity: (index: Int, newQuantity: Int) -> Unit,
  onRemoveItem: (index: Int) -> Unit,
  onOrderSubmit: (name: String, phone: String, loc: String, delivType: String, notes: String) -> Unit
) {
  var customerName by remember { mutableStateOf("") }
  var customerPhone by remember { mutableStateOf("") }
  var deliveryLocation by remember { mutableStateOf("") }
  var deliveryType by remember { mutableStateOf("Entrega ao Domicílio") }
  var notes by remember { mutableStateOf("") }

  val totalAmount = cartItems.sumOf { it.subtotal }

  Column(
    modifier = Modifier
      .fillMaxWidth()
      .padding(horizontal = 20.dp, vertical = 10.dp)
      .verticalScroll(androidx.compose.foundation.rememberScrollState())
  ) {
    Row(
      modifier = Modifier.fillMaxWidth(),
      horizontalArrangement = Arrangement.SpaceBetween,
      verticalAlignment = Alignment.CenterVertically
    ) {
      Column {
        Text("Carrinho de Encomenda", fontWeight = FontWeight.ExtraBold, fontSize = 18.sp, color = ZapSlateDark)
        Text("Loja: ${store.name}", fontSize = 12.sp, color = ZapEmerald)
      }
      Surface(
        color = ZapEmeraldLight,
        shape = RoundedCornerShape(8.dp)
      ) {
        Text(
          "${cartItems.sumOf { it.quantity }} itens",
          color = ZapEmeraldDark,
          fontWeight = FontWeight.Bold,
          fontSize = 11.sp,
          modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
        )
      }
    }

    Spacer(modifier = Modifier.height(14.dp))

    // Items list
    cartItems.forEachIndexed { index, item ->
      Card(
        shape = RoundedCornerShape(10.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        modifier = Modifier
          .fillMaxWidth()
          .padding(vertical = 4.dp)
      ) {
        Row(
          modifier = Modifier
            .fillMaxWidth()
            .padding(10.dp),
          verticalAlignment = Alignment.CenterVertically
        ) {
          Column(modifier = Modifier.weight(1f)) {
            Text(item.product.name, fontWeight = FontWeight.Bold, fontSize = 13.sp, color = ZapSlateDark)
            val details = mutableListOf<String>()
            if (item.selectedSize.isNotBlank()) details.add("Tam: ${item.selectedSize}")
            if (item.selectedColor.isNotBlank()) details.add("Cor: ${item.selectedColor}")
            if (details.isNotEmpty()) {
              Text(details.joinToString(" • "), fontSize = 10.sp, color = ZapSlateMuted)
            }
            Text(
              MozFormatters.formatMt(item.subtotal),
              fontWeight = FontWeight.ExtraBold,
              fontSize = 12.sp,
              color = Color(store.primaryColor)
            )
          }

          Row(verticalAlignment = Alignment.CenterVertically) {
            IconButton(onClick = { onUpdateQuantity(index, item.quantity - 1) }, modifier = Modifier.size(28.dp)) {
              Icon(Icons.Default.Remove, contentDescription = "Menos", modifier = Modifier.size(16.dp))
            }
            Text("${item.quantity}", fontWeight = FontWeight.Bold, fontSize = 13.sp, modifier = Modifier.padding(horizontal = 4.dp))
            IconButton(onClick = { onUpdateQuantity(index, item.quantity + 1) }, modifier = Modifier.size(28.dp)) {
              Icon(Icons.Default.Add, contentDescription = "Mais", modifier = Modifier.size(16.dp))
            }
            IconButton(onClick = { onRemoveItem(index) }, modifier = Modifier.size(28.dp)) {
              Icon(Icons.Default.Delete, contentDescription = "Remover", tint = ZapRed, modifier = Modifier.size(16.dp))
            }
          }
        }
      }
    }

    Spacer(modifier = Modifier.height(10.dp))

    // Total row
    Row(
      modifier = Modifier.fillMaxWidth(),
      horizontalArrangement = Arrangement.SpaceBetween,
      verticalAlignment = Alignment.CenterVertically
    ) {
      Text("Total Estimado dos Produtos:", fontWeight = FontWeight.Bold, fontSize = 14.sp, color = ZapSlateDark)
      Text(MozFormatters.formatMt(totalAmount), fontWeight = FontWeight.ExtraBold, fontSize = 18.sp, color = Color(store.primaryColor))
    }

    HorizontalDivider(modifier = Modifier.padding(vertical = 12.dp))

    // Customer details
    Text("Dados para Entrega (opcional):", fontWeight = FontWeight.Bold, fontSize = 13.sp, color = ZapSlateDark)
    Spacer(modifier = Modifier.height(8.dp))

    OutlinedTextField(
      value = customerName,
      onValueChange = { customerName = it },
      label = { Text("O teu Nome") },
      placeholder = { Text("ex: António ou Fátima") },
      singleLine = true,
      modifier = Modifier.fillMaxWidth().testTag("cart_customer_name_input")
    )

    Spacer(modifier = Modifier.height(8.dp))

    OutlinedTextField(
      value = deliveryLocation,
      onValueChange = { deliveryLocation = it },
      label = { Text("Bairro / Cidade para Entrega") },
      placeholder = { Text("ex: Polana, Maputo ou Matola") },
      singleLine = true,
      modifier = Modifier.fillMaxWidth().testTag("cart_delivery_location_input")
    )

    Spacer(modifier = Modifier.height(8.dp))

    Row(modifier = Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically) {
      Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.clickable { deliveryType = "Entrega ao Domicílio" }) {
        RadioButton(selected = deliveryType == "Entrega ao Domicílio", onClick = { deliveryType = "Entrega ao Domicílio" })
        Text("Entrega", fontSize = 12.sp)
      }
      Spacer(modifier = Modifier.width(12.dp))
      Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.clickable { deliveryType = "Levantamento na Loja" }) {
        RadioButton(selected = deliveryType == "Levantamento na Loja", onClick = { deliveryType = "Levantamento na Loja" })
        Text("Levantamento na Loja", fontSize = 12.sp)
      }
    }

    Spacer(modifier = Modifier.height(14.dp))

    // Main WhatsApp Order Action Button
    Button(
      onClick = {
        onOrderSubmit(customerName, customerPhone, deliveryLocation, deliveryType, notes)
      },
      modifier = Modifier
        .fillMaxWidth()
        .height(50.dp)
        .testTag("submit_whatsapp_order_btn"),
      colors = ButtonDefaults.buttonColors(containerColor = ZapWhatsappGreen),
      shape = RoundedCornerShape(12.dp)
    ) {
      Icon(Icons.Default.Phone, contentDescription = null, tint = Color.White, modifier = Modifier.size(20.dp))
      Spacer(modifier = Modifier.width(8.dp))
      Text("ENCOMENDAR PELO WHATSAPP", fontWeight = FontWeight.Bold, fontSize = 13.sp)
    }

    Spacer(modifier = Modifier.height(20.dp))
  }
}
