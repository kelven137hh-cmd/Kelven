package com.example.ui.screens

import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.content.Intent
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
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Category
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.ContentCopy
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.ExitToApp
import androidx.compose.material.icons.filled.FiberManualRecord
import androidx.compose.material.icons.filled.Inventory
import androidx.compose.material.icons.filled.Link
import androidx.compose.material.icons.filled.OpenInNew
import androidx.compose.material.icons.filled.Palette
import androidx.compose.material.icons.filled.Phone
import androidx.compose.material.icons.filled.ReceiptLong
import androidx.compose.material.icons.filled.Share
import androidx.compose.material.icons.filled.ShoppingBag
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.filled.Storefront
import androidx.compose.material.icons.filled.TrendingUp
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Checkbox
import androidx.compose.material3.ElevatedCard
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.ScrollableTabRow
import androidx.compose.material3.Surface
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.material3.Tab
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.local.model.CategoryEntity
import com.example.data.local.model.ProductEntity
import com.example.model.MozFormatters
import com.example.model.PlanType
import com.example.ui.theme.ZapAmber
import com.example.ui.theme.ZapEmerald
import com.example.ui.theme.ZapEmeraldDark
import com.example.ui.theme.ZapEmeraldLight
import com.example.ui.theme.ZapRed
import com.example.ui.theme.ZapSlateDark
import com.example.ui.theme.ZapSlateMuted
import com.example.ui.theme.ZapViolet
import com.example.ui.theme.ZapWhatsappGreen
import com.example.ui.viewmodel.AppScreen
import com.example.ui.viewmodel.ZapViewModel

@Composable
fun MerchantDashboardScreen(
  viewModel: ZapViewModel,
  onOpenStoreCatalog: (slug: String) -> Unit,
  onCreateNewStore: () -> Unit
) {
  val context = LocalContext.current
  val activeStore by viewModel.activeStore.collectAsState()
  val myStores by viewModel.myStores.collectAsState()
  val products by viewModel.products.collectAsState()
  val categories by viewModel.categories.collectAsState()
  val analytics by viewModel.storeAnalytics.collectAsState()
  val orders by viewModel.orders.collectAsState()
  val profile by viewModel.currentProfile.collectAsState()

  var selectedTab by remember { mutableIntStateOf(0) }
  // 0: Visão Geral, 1: Produtos, 2: Categorias, 3: Encomendas, 4: Personalizar, 5: Plano

  // Dialogs
  var showAddProductDialog by remember { mutableStateOf(false) }
  var productToEdit by remember { mutableStateOf<ProductEntity?>(null) }
  var productToDelete by remember { mutableStateOf<ProductEntity?>(null) }
  var showAddCategoryDialog by remember { mutableStateOf(false) }
  var newCategoryName by remember { mutableStateOf("") }

  val store = activeStore

  if (store == null) {
    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
      Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Text("Nenhuma loja ativa selecionada.", fontSize = 16.sp, color = ZapSlateMuted)
        Spacer(modifier = Modifier.height(12.dp))
        Button(onClick = onCreateNewStore, colors = ButtonDefaults.buttonColors(containerColor = ZapEmerald)) {
          Text("Criar Nova Loja")
        }
      }
    }
    return
  }

  val plan = PlanType.fromId(store.planId)
  val isOverLimit = products.size >= plan.maxProducts

  Column(
    modifier = Modifier
      .fillMaxSize()
      .background(MaterialTheme.colorScheme.background)
  ) {
    // Top Bar
    Surface(
      modifier = Modifier.fillMaxWidth(),
      color = Color.White,
      tonalElevation = 2.dp
    ) {
      Row(
        modifier = Modifier
          .fillMaxWidth()
          .padding(horizontal = 16.dp, vertical = 10.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
      ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
          Box(
            modifier = Modifier
              .size(34.dp)
              .clip(RoundedCornerShape(8.dp))
              .background(Color(store.primaryColor)),
            contentAlignment = Alignment.Center
          ) {
            Icon(Icons.Default.Storefront, contentDescription = null, tint = Color.White, modifier = Modifier.size(18.dp))
          }
          Spacer(modifier = Modifier.width(10.dp))
          Column {
            Text(store.name, fontWeight = FontWeight.Bold, fontSize = 15.sp, color = ZapSlateDark)
            Text("${store.city} • ${store.businessCategory}", fontSize = 11.sp, color = ZapSlateMuted)
          }
        }

        Row(verticalAlignment = Alignment.CenterVertically) {
          IconButton(
            onClick = { onOpenStoreCatalog(store.slug) },
            modifier = Modifier.testTag("dashboard_preview_store_btn")
          ) {
            Icon(Icons.Default.OpenInNew, contentDescription = "Ver Loja como Cliente", tint = ZapEmerald)
          }
          IconButton(
            onClick = { viewModel.logout() },
            modifier = Modifier.testTag("dashboard_logout_btn")
          ) {
            Icon(Icons.Default.ExitToApp, contentDescription = "Terminar Sessão", tint = ZapSlateMuted)
          }
        }
      }
    }

    // Scrollable Tabs
    ScrollableTabRow(
      selectedTabIndex = selectedTab,
      containerColor = Color.White,
      contentColor = ZapEmerald,
      edgePadding = 12.dp
    ) {
      val tabs = listOf(
        "Visão Geral" to Icons.Default.TrendingUp,
        "Produtos (${products.size})" to Icons.Default.Inventory,
        "Categorias" to Icons.Default.Category,
        "Encomendas (${orders.size})" to Icons.Default.ReceiptLong,
        "Personalizar" to Icons.Default.Palette,
        "Plano ${plan.badgeLabel}" to Icons.Default.Star
      )
      tabs.forEachIndexed { index, (label, icon) ->
        Tab(
          selected = selectedTab == index,
          onClick = { selectedTab = index },
          text = {
            Row(verticalAlignment = Alignment.CenterVertically) {
              Icon(icon, contentDescription = null, modifier = Modifier.size(14.dp))
              Spacer(modifier = Modifier.width(4.dp))
              Text(label, fontSize = 12.sp, fontWeight = if (selectedTab == index) FontWeight.Bold else FontWeight.Normal)
            }
          },
          modifier = Modifier.testTag("dashboard_tab_$index")
        )
      }
    }

    // Tab Content
    Box(modifier = Modifier.fillMaxSize()) {
      when (selectedTab) {
        // Tab 0: Visão Geral
        0 -> {
          LazyColumn(
            modifier = Modifier
              .fillMaxSize()
              .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(14.dp)
          ) {
            // Status & Link Card
            item {
              ElevatedCard(
                shape = RoundedCornerShape(14.dp),
                colors = CardDefaults.elevatedCardColors(containerColor = Color.White),
                elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
              ) {
                Column(modifier = Modifier.padding(16.dp)) {
                  Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                  ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                      Box(
                        modifier = Modifier
                          .size(10.dp)
                          .clip(CircleShape)
                          .background(if (store.isPublished) ZapEmerald else Color.Gray)
                      )
                      Spacer(modifier = Modifier.width(8.dp))
                      Text(
                        text = if (store.isPublished) "Catálogo Publicado & Online" else "Catálogo Pausado",
                        fontWeight = FontWeight.Bold,
                        fontSize = 14.sp,
                        color = if (store.isPublished) ZapEmeraldDark else Color.Gray
                      )
                    }

                    Switch(
                      checked = store.isPublished,
                      onCheckedChange = { viewModel.toggleStorePublish(store.id, it) },
                      colors = SwitchDefaults.colors(checkedThumbColor = Color.White, checkedTrackColor = ZapEmerald),
                      modifier = Modifier.testTag("dashboard_publish_switch")
                    )
                  }

                  HorizontalDivider(modifier = Modifier.padding(vertical = 12.dp))

                  // Public Link
                  Text("Link Público Exclusivo:", fontSize = 11.sp, color = ZapSlateMuted, fontWeight = FontWeight.SemiBold)
                  Spacer(modifier = Modifier.height(4.dp))
                  Surface(
                    shape = RoundedCornerShape(8.dp),
                    color = Color(0xFFF1F5F9),
                    modifier = Modifier.fillMaxWidth()
                  ) {
                    Row(
                      modifier = Modifier.padding(horizontal = 12.dp, vertical = 8.dp),
                      verticalAlignment = Alignment.CenterVertically,
                      horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                      Text(
                        "zapcatalogo.mz/loja/${store.slug}",
                        fontWeight = FontWeight.Bold,
                        fontSize = 12.sp,
                        color = ZapEmeraldDark,
                        modifier = Modifier.weight(1f)
                      )
                      Row {
                        IconButton(
                          onClick = {
                            val clipboard = context.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
                            val clip = ClipData.newPlainText("Link do Catálogo", "https://zapcatalogo.mz/loja/${store.slug}")
                            clipboard.setPrimaryClip(clip)
                            viewModel.showMessage("Link copiado para a área de transferência!")
                          },
                          modifier = Modifier.size(32.dp).testTag("copy_link_btn")
                        ) {
                          Icon(Icons.Default.ContentCopy, contentDescription = "Copiar", tint = ZapSlateDark, modifier = Modifier.size(16.dp))
                        }
                        IconButton(
                          onClick = {
                            viewModel.recordShare(store.id)
                            val shareIntent = Intent(Intent.ACTION_SEND).apply {
                              type = "text/plain"
                              putExtra(Intent.EXTRA_TEXT, "Conheça o nosso catálogo oficial no WhatsApp: https://zapcatalogo.mz/loja/${store.slug}")
                            }
                            context.startActivity(Intent.createChooser(shareIntent, "Partilhar Catálogo"))
                          },
                          modifier = Modifier.size(32.dp).testTag("share_link_btn")
                        ) {
                          Icon(Icons.Default.Share, contentDescription = "Partilhar", tint = ZapEmerald, modifier = Modifier.size(16.dp))
                        }
                      }
                    }
                  }

                  Spacer(modifier = Modifier.height(10.dp))

                  Button(
                    onClick = { onOpenStoreCatalog(store.slug) },
                    modifier = Modifier.fillMaxWidth().testTag("view_store_as_client_btn"),
                    colors = ButtonDefaults.buttonColors(containerColor = ZapEmerald),
                    shape = RoundedCornerShape(8.dp)
                  ) {
                    Icon(Icons.Default.Visibility, contentDescription = null, modifier = Modifier.size(16.dp))
                    Spacer(modifier = Modifier.width(6.dp))
                    Text("Abrir Loja como Cliente (Simulação Real)", fontSize = 12.sp, fontWeight = FontWeight.Bold)
                  }
                }
              }
            }

            // Real Metrics Cards
            item {
              Text("Desempenho da Loja", fontWeight = FontWeight.Bold, fontSize = 15.sp, color = ZapSlateDark)
              Spacer(modifier = Modifier.height(6.dp))
              Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
              ) {
                MetricBox(
                  modifier = Modifier.weight(1f),
                  label = "Visualizações",
                  value = analytics.views.toString(),
                  icon = Icons.Default.Visibility,
                  color = ZapEmerald
                )
                MetricBox(
                  modifier = Modifier.weight(1f),
                  label = "Cliques WhatsApp",
                  value = analytics.whatsappClicks.toString(),
                  icon = Icons.Default.Phone,
                  color = ZapWhatsappGreen
                )
                MetricBox(
                  modifier = Modifier.weight(1f),
                  label = "Partilhas",
                  value = analytics.shares.toString(),
                  icon = Icons.Default.Share,
                  color = ZapViolet
                )
              }
            }

            // Products Limit & Plan Progress
            item {
              Card(
                shape = RoundedCornerShape(12.dp),
                colors = CardDefaults.cardColors(containerColor = Color.White),
                border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFFE2E8F0))
              ) {
                Column(modifier = Modifier.padding(14.dp)) {
                  Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                  ) {
                    Text("Produtos Cadastrados", fontWeight = FontWeight.Bold, fontSize = 13.sp, color = ZapSlateDark)
                    Text("${products.size} / ${plan.maxProducts}", fontWeight = FontWeight.Bold, fontSize = 13.sp, color = if (isOverLimit) ZapRed else ZapEmerald)
                  }

                  Spacer(modifier = Modifier.height(6.dp))

                  LinearProgressIndicator(
                    progress = { (products.size.toFloat() / plan.maxProducts.toFloat()).coerceIn(0f, 1f) },
                    modifier = Modifier.fillMaxWidth().height(6.dp).clip(RoundedCornerShape(3.dp)),
                    color = if (isOverLimit) ZapRed else ZapEmerald,
                    trackColor = Color(0xFFE2E8F0)
                  )

                  if (isOverLimit) {
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                      "Atingiste o limite do ${plan.title}. Faz upgrade para o Plano Pro para teres até 100 produtos!",
                      fontSize = 11.sp,
                      color = ZapRed
                    )
                  }
                }
              }
            }

            // Recommended Tasks Checklist
            item {
              Card(
                shape = RoundedCornerShape(12.dp),
                colors = CardDefaults.cardColors(containerColor = Color.White)
              ) {
                Column(modifier = Modifier.padding(14.dp)) {
                  Text("Tarefas para Potencializar as Vendas:", fontWeight = FontWeight.Bold, fontSize = 13.sp, color = ZapSlateDark)
                  Spacer(modifier = Modifier.height(8.dp))
                  val checklist = listOf(
                    Pair("Adicionar pelo menos 3 produtos com fotos e preços", products.size >= 3),
                    Pair("Criar categorias para organizar o catálogo", categories.isNotEmpty()),
                    Pair("Verificar número de WhatsApp (${store.whatsappNumber})", store.whatsappNumber.isNotBlank()),
                    Pair("Partilhar o link no WhatsApp e status das redes", analytics.shares > 0)
                  )
                  checklist.forEach { (task, done) ->
                    Row(
                      modifier = Modifier.padding(vertical = 4.dp),
                      verticalAlignment = Alignment.CenterVertically
                    ) {
                      Icon(
                        imageVector = if (done) Icons.Default.CheckCircle else Icons.Default.FiberManualRecord,
                        contentDescription = null,
                        tint = if (done) ZapEmerald else Color.LightGray,
                        modifier = Modifier.size(16.dp)
                      )
                      Spacer(modifier = Modifier.width(8.dp))
                      Text(task, fontSize = 12.sp, color = if (done) ZapSlateDark else ZapSlateMuted)
                    }
                  }
                }
              }
            }
          }
        }

        // Tab 1: Produtos
        1 -> {
          LazyColumn(
            modifier = Modifier.fillMaxSize().padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(10.dp),
            contentPadding = PaddingValues(bottom = 80.dp)
          ) {
            item {
              Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
              ) {
                Column {
                  Text("Gestão de Produtos", fontWeight = FontWeight.Bold, fontSize = 16.sp, color = ZapSlateDark)
                  Text("${products.size} de ${plan.maxProducts} produtos", fontSize = 12.sp, color = ZapSlateMuted)
                }

                Button(
                  onClick = {
                    if (isOverLimit) {
                      viewModel.showMessage("Limite atingido (${plan.maxProducts} produtos). Faça upgrade para o Plano Pro!")
                      selectedTab = 5
                    } else {
                      productToEdit = null
                      showAddProductDialog = true
                    }
                  },
                  colors = ButtonDefaults.buttonColors(containerColor = ZapEmerald),
                  shape = RoundedCornerShape(8.dp),
                  contentPadding = PaddingValues(horizontal = 10.dp, vertical = 6.dp),
                  modifier = Modifier.testTag("add_product_btn")
                ) {
                  Icon(Icons.Default.Add, contentDescription = null, modifier = Modifier.size(16.dp))
                  Spacer(modifier = Modifier.width(4.dp))
                  Text("Adicionar Produto", fontSize = 12.sp)
                }
              }
            }

            if (products.isEmpty()) {
              item {
                Box(
                  modifier = Modifier.fillMaxWidth().height(160.dp),
                  contentAlignment = Alignment.Center
                ) {
                  Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Icon(Icons.Default.Inventory, contentDescription = null, tint = Color.LightGray, modifier = Modifier.size(40.dp))
                    Spacer(modifier = Modifier.height(8.dp))
                    Text("Nenhum produto cadastrado ainda.", fontSize = 13.sp, color = ZapSlateMuted)
                  }
                }
              }
            } else {
              items(products) { prod ->
                Card(
                  shape = RoundedCornerShape(12.dp),
                  colors = CardDefaults.cardColors(containerColor = Color.White),
                  elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
                ) {
                  Row(
                    modifier = Modifier.fillMaxWidth().padding(12.dp),
                    verticalAlignment = Alignment.CenterVertically
                  ) {
                    Box(
                      modifier = Modifier
                        .size(48.dp)
                        .clip(RoundedCornerShape(8.dp))
                        .background(Color(0xFFF1F5F9)),
                      contentAlignment = Alignment.Center
                    ) {
                      Icon(Icons.Default.ShoppingBag, contentDescription = null, tint = ZapEmerald, modifier = Modifier.size(24.dp))
                    }

                    Spacer(modifier = Modifier.width(12.dp))

                    Column(modifier = Modifier.weight(1f)) {
                      Row(verticalAlignment = Alignment.CenterVertically) {
                        Text(prod.name, fontWeight = FontWeight.Bold, fontSize = 14.sp, color = ZapSlateDark)
                        if (prod.isFeatured) {
                          Spacer(modifier = Modifier.width(4.dp))
                          Icon(Icons.Default.Star, contentDescription = "Destaque", tint = ZapAmber, modifier = Modifier.size(14.dp))
                        }
                      }
                      Row(verticalAlignment = Alignment.CenterVertically) {
                        Text(MozFormatters.formatMt(prod.price), fontSize = 12.sp, fontWeight = FontWeight.SemiBold, color = ZapEmerald)
                        if (prod.promotionalPrice != null) {
                          Spacer(modifier = Modifier.width(6.dp))
                          Text("Promo: ${MozFormatters.formatMt(prod.promotionalPrice)}", fontSize = 11.sp, color = ZapRed)
                        }
                      }
                      if (prod.sizesCsv.isNotBlank()) {
                        Text("Tamanhos: ${prod.sizesCsv}", fontSize = 10.sp, color = ZapSlateMuted)
                      }
                    }

                    Row {
                      IconButton(
                        onClick = {
                          productToEdit = prod
                          showAddProductDialog = true
                        },
                        modifier = Modifier.size(32.dp)
                      ) {
                        Icon(Icons.Default.Edit, contentDescription = "Editar", tint = ZapSlateDark, modifier = Modifier.size(16.dp))
                      }
                      IconButton(
                        onClick = { viewModel.duplicateProduct(prod) },
                        modifier = Modifier.size(32.dp)
                      ) {
                        Icon(Icons.Default.ContentCopy, contentDescription = "Duplicar", tint = ZapEmerald, modifier = Modifier.size(16.dp))
                      }
                      IconButton(
                        onClick = { productToDelete = prod },
                        modifier = Modifier.size(32.dp)
                      ) {
                        Icon(Icons.Default.Delete, contentDescription = "Eliminar", tint = ZapRed, modifier = Modifier.size(16.dp))
                      }
                    }
                  }
                }
              }
            }
          }
        }

        // Tab 2: Categorias
        2 -> {
          LazyColumn(
            modifier = Modifier.fillMaxSize().padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(10.dp)
          ) {
            item {
              Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
              ) {
                Text("Categorias da Loja", fontWeight = FontWeight.Bold, fontSize = 16.sp, color = ZapSlateDark)
                Button(
                  onClick = { showAddCategoryDialog = true },
                  colors = ButtonDefaults.buttonColors(containerColor = ZapEmerald),
                  shape = RoundedCornerShape(8.dp),
                  contentPadding = PaddingValues(horizontal = 10.dp, vertical = 6.dp)
                ) {
                  Icon(Icons.Default.Add, contentDescription = null, modifier = Modifier.size(16.dp))
                  Spacer(modifier = Modifier.width(4.dp))
                  Text("Nova Categoria", fontSize = 12.sp)
                }
              }
            }

            if (categories.isEmpty()) {
              item {
                Text("Sem categorias. Adiciona categorias para organizar os teus produtos.", fontSize = 12.sp, color = ZapSlateMuted)
              }
            } else {
              items(categories) { cat ->
                Card(
                  shape = RoundedCornerShape(10.dp),
                  colors = CardDefaults.cardColors(containerColor = Color.White)
                ) {
                  Row(
                    modifier = Modifier.fillMaxWidth().padding(14.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                  ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                      Icon(Icons.Default.Category, contentDescription = null, tint = ZapEmerald, modifier = Modifier.size(18.dp))
                      Spacer(modifier = Modifier.width(10.dp))
                      Text(cat.name, fontWeight = FontWeight.SemiBold, fontSize = 14.sp, color = ZapSlateDark)
                    }
                    IconButton(onClick = { viewModel.deleteCategory(cat) }) {
                      Icon(Icons.Default.Delete, contentDescription = "Remover", tint = ZapRed, modifier = Modifier.size(18.dp))
                    }
                  }
                }
              }
            }
          }
        }

        // Tab 3: Encomendas
        3 -> {
          LazyColumn(
            modifier = Modifier.fillMaxSize().padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(10.dp)
          ) {
            item {
              Text("Histórico de Pedidos WhatsApp", fontWeight = FontWeight.Bold, fontSize = 16.sp, color = ZapSlateDark)
              Text("Pedidos gerados e enviados para o teu WhatsApp pelos clientes:", fontSize = 12.sp, color = ZapSlateMuted)
            }

            if (orders.isEmpty()) {
              item {
                Card(
                  shape = RoundedCornerShape(12.dp),
                  colors = CardDefaults.cardColors(containerColor = Color.White),
                  modifier = Modifier.fillMaxWidth().padding(top = 10.dp)
                ) {
                  Column(
                    modifier = Modifier.padding(24.dp).fillMaxWidth(),
                    horizontalAlignment = Alignment.CenterHorizontally
                  ) {
                    Icon(Icons.Default.ReceiptLong, contentDescription = null, tint = Color.LightGray, modifier = Modifier.size(36.dp))
                    Spacer(modifier = Modifier.height(8.dp))
                    Text("Ainda não recebeste pedidos.", fontWeight = FontWeight.Bold, fontSize = 13.sp, color = ZapSlateDark)
                    Text("Partilha o link do teu catálogo para começar a receber pedidos!", fontSize = 11.sp, color = ZapSlateMuted)
                  }
                }
              }
            } else {
              items(orders) { ord ->
                Card(
                  shape = RoundedCornerShape(12.dp),
                  colors = CardDefaults.cardColors(containerColor = Color.White)
                ) {
                  Column(modifier = Modifier.padding(14.dp)) {
                    Row(
                      modifier = Modifier.fillMaxWidth(),
                      horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                      Text(ord.customerName, fontWeight = FontWeight.Bold, fontSize = 14.sp, color = ZapSlateDark)
                      Text(MozFormatters.formatMt(ord.totalAmount), fontWeight = FontWeight.Bold, fontSize = 14.sp, color = ZapEmerald)
                    }
                    if (ord.deliveryLocation.isNotBlank()) {
                      Text("📍 ${ord.deliveryLocation} (${ord.deliveryType})", fontSize = 11.sp, color = ZapSlateMuted)
                    }
                    Spacer(modifier = Modifier.height(4.dp))
                    Text("Itens: ${ord.itemsSummary}", fontSize = 12.sp, color = ZapSlateDark)
                  }
                }
              }
            }
          }
        }

        // Tab 4: Personalizar
        4 -> {
          StoreCustomizeContent(store = store, onSave = { updated -> viewModel.updateStoreDetails(updated) })
        }

        // Tab 5: Plano & Utilização
        5 -> {
          PlanUsageContent(
            store = store,
            productCount = products.size,
            onUpgradeToPro = { viewModel.updatePlan(store.id, "PRO") }
          )
        }
      }
    }
  }

  // Add / Edit Product Dialog
  if (showAddProductDialog) {
    ProductEditDialog(
      initialProduct = productToEdit,
      categories = categories,
      onDismiss = { showAddProductDialog = false },
      onSave = { name, desc, price, promoPrice, catId, sizes, colors, volume, isAvail, isFeat ->
        val success = viewModel.saveProduct(
          id = productToEdit?.id,
          name = name,
          description = desc,
          price = price,
          promoPrice = promoPrice,
          categoryId = catId,
          sizes = sizes,
          colors = colors,
          volume = volume,
          isAvailable = isAvail,
          isFeatured = isFeat
        )
        if (success) {
          showAddProductDialog = false
        }
      }
    )
  }

  // Delete Product Confirmation Dialog
  if (productToDelete != null) {
    AlertDialog(
      onDismissRequest = { productToDelete = null },
      title = { Text("Eliminar Produto?") },
      text = { Text("Tens a certeza que desejas eliminar '${productToDelete?.name}'? Esta ação não pode ser desfeita.") },
      confirmButton = {
        Button(
          onClick = {
            productToDelete?.let { viewModel.deleteProduct(it) }
            productToDelete = null
          },
          colors = ButtonDefaults.buttonColors(containerColor = ZapRed)
        ) {
          Text("Eliminar")
        }
      },
      dismissButton = {
        TextButton(onClick = { productToDelete = null }) {
          Text("Cancelar")
        }
      }
    )
  }

  // Add Category Dialog
  if (showAddCategoryDialog) {
    AlertDialog(
      onDismissRequest = { showAddCategoryDialog = false },
      title = { Text("Nova Categoria") },
      text = {
        OutlinedTextField(
          value = newCategoryName,
          onValueChange = { newCategoryName = it },
          label = { Text("Nome da Categoria") },
          placeholder = { Text("ex: Vestidos, Camisas ou Sapatilhas") },
          singleLine = true,
          modifier = Modifier.fillMaxWidth().testTag("new_category_input")
        )
      },
      confirmButton = {
        Button(
          onClick = {
            if (newCategoryName.isNotBlank()) {
              viewModel.addCategory(newCategoryName)
              newCategoryName = ""
              showAddCategoryDialog = false
            }
          },
          colors = ButtonDefaults.buttonColors(containerColor = ZapEmerald)
        ) {
          Text("Adicionar")
        }
      },
      dismissButton = {
        TextButton(onClick = { showAddCategoryDialog = false }) {
          Text("Cancelar")
        }
      }
    )
  }
}

@Composable
fun MetricBox(
  modifier: Modifier = Modifier,
  label: String,
  value: String,
  icon: androidx.compose.ui.graphics.vector.ImageVector,
  color: Color
) {
  ElevatedCard(
    modifier = modifier,
    shape = RoundedCornerShape(12.dp),
    colors = CardDefaults.elevatedCardColors(containerColor = Color.White),
    elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
  ) {
    Column(modifier = Modifier.padding(12.dp)) {
      Icon(icon, contentDescription = null, tint = color, modifier = Modifier.size(18.dp))
      Spacer(modifier = Modifier.height(6.dp))
      Text(value, fontWeight = FontWeight.ExtraBold, fontSize = 18.sp, color = ZapSlateDark)
      Text(label, fontSize = 10.sp, color = ZapSlateMuted, fontWeight = FontWeight.Medium)
    }
  }
}

@Composable
fun ProductEditDialog(
  initialProduct: ProductEntity?,
  categories: List<CategoryEntity>,
  onDismiss: () -> Unit,
  onSave: (
    name: String,
    desc: String,
    price: Double,
    promoPrice: Double?,
    catId: String,
    sizes: String,
    colors: String,
    volume: String,
    isAvailable: Boolean,
    isFeatured: Boolean
  ) -> Unit
) {
  var name by remember { mutableStateOf(initialProduct?.name ?: "") }
  var desc by remember { mutableStateOf(initialProduct?.description ?: "") }
  var priceStr by remember { mutableStateOf(initialProduct?.price?.toInt()?.toString() ?: "") }
  var promoPriceStr by remember { mutableStateOf(initialProduct?.promotionalPrice?.toInt()?.toString() ?: "") }
  var selectedCatId by remember { mutableStateOf(initialProduct?.categoryId ?: (categories.firstOrNull()?.id ?: "")) }
  var sizes by remember { mutableStateOf(initialProduct?.sizesCsv ?: "") }
  var colors by remember { mutableStateOf(initialProduct?.colorsCsv ?: "") }
  var volume by remember { mutableStateOf(initialProduct?.volume ?: "") }
  var isAvailable by remember { mutableStateOf(initialProduct?.isAvailable ?: true) }
  var isFeatured by remember { mutableStateOf(initialProduct?.isFeatured ?: false) }

  AlertDialog(
    onDismissRequest = onDismiss,
    title = { Text(if (initialProduct == null) "Adicionar Produto" else "Editar Produto", fontWeight = FontWeight.Bold) },
    text = {
      Column(
        modifier = Modifier
          .fillMaxWidth()
          .verticalScroll(androidx.compose.foundation.rememberScrollState()),
        verticalArrangement = Arrangement.spacedBy(10.dp)
      ) {
        OutlinedTextField(
          value = name,
          onValueChange = { name = it },
          label = { Text("Nome do Produto *") },
          singleLine = true,
          modifier = Modifier.fillMaxWidth().testTag("product_name_field")
        )

        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
          OutlinedTextField(
            value = priceStr,
            onValueChange = { priceStr = it },
            label = { Text("Preço (MT) *") },
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
            modifier = Modifier.weight(1f).testTag("product_price_field")
          )
          OutlinedTextField(
            value = promoPriceStr,
            onValueChange = { promoPriceStr = it },
            label = { Text("Preço Promo (MT)") },
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
            modifier = Modifier.weight(1f).testTag("product_promo_price_field")
          )
        }

        OutlinedTextField(
          value = desc,
          onValueChange = { desc = it },
          label = { Text("Descrição detalhada") },
          maxLines = 3,
          modifier = Modifier.fillMaxWidth().testTag("product_desc_field")
        )

        OutlinedTextField(
          value = sizes,
          onValueChange = { sizes = it },
          label = { Text("Tamanhos ou Números") },
          placeholder = { Text("ex: S,M,L,XL ou 39,40,41,42") },
          singleLine = true,
          modifier = Modifier.fillMaxWidth().testTag("product_sizes_field")
        )

        OutlinedTextField(
          value = colors,
          onValueChange = { colors = it },
          label = { Text("Cores disponíveis") },
          placeholder = { Text("ex: Preto,Branco,Azul") },
          singleLine = true,
          modifier = Modifier.fillMaxWidth().testTag("product_colors_field")
        )

        OutlinedTextField(
          value = volume,
          onValueChange = { volume = it },
          label = { Text("Volume (para Perfumaria)") },
          placeholder = { Text("ex: 50ml, 100ml") },
          singleLine = true,
          modifier = Modifier.fillMaxWidth().testTag("product_volume_field")
        )

        Row(verticalAlignment = Alignment.CenterVertically) {
          Checkbox(checked = isFeatured, onCheckedChange = { isFeatured = it })
          Text("Destacar produto no topo da loja", fontSize = 12.sp)
        }

        Row(verticalAlignment = Alignment.CenterVertically) {
          Checkbox(checked = isAvailable, onCheckedChange = { isAvailable = it })
          Text("Produto disponível em estoque", fontSize = 12.sp)
        }
      }
    },
    confirmButton = {
      Button(
        onClick = {
          val p = priceStr.toDoubleOrNull() ?: 0.0
          val promo = promoPriceStr.toDoubleOrNull()
          if (name.isNotBlank() && p > 0) {
            onSave(name, desc, p, promo, selectedCatId, sizes, colors, volume, isAvailable, isFeatured)
          }
        },
        colors = ButtonDefaults.buttonColors(containerColor = ZapEmerald),
        modifier = Modifier.testTag("save_product_confirm_btn")
      ) {
        Text("Guardar")
      }
    },
    dismissButton = {
      TextButton(onClick = onDismiss) {
        Text("Cancelar")
      }
    }
  )
}

@Composable
fun StoreCustomizeContent(
  store: com.example.data.local.model.StoreEntity,
  onSave: (com.example.data.local.model.StoreEntity) -> Unit
) {
  var name by remember { mutableStateOf(store.name) }
  var slug by remember { mutableStateOf(store.slug) }
  var whatsapp by remember { mutableStateOf(store.whatsappNumber) }
  var city by remember { mutableStateOf(store.city) }
  var desc by remember { mutableStateOf(store.description) }

  LazyColumn(
    modifier = Modifier.fillMaxSize().padding(16.dp),
    verticalArrangement = Arrangement.spacedBy(12.dp)
  ) {
    item {
      Text("Personalizar Identidade da Loja", fontWeight = FontWeight.Bold, fontSize = 16.sp, color = ZapSlateDark)
    }

    item {
      Card(
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White)
      ) {
        Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(10.dp)) {
          OutlinedTextField(
            value = name,
            onValueChange = { name = it },
            label = { Text("Nome da Loja") },
            modifier = Modifier.fillMaxWidth().testTag("cust_name_field")
          )

          OutlinedTextField(
            value = slug,
            onValueChange = { slug = it.lowercase().replace(" ", "-") },
            label = { Text("Slug do Link Público") },
            prefix = { Text("zapcatalogo.mz/loja/", fontSize = 11.sp, color = ZapSlateMuted) },
            modifier = Modifier.fillMaxWidth().testTag("cust_slug_field")
          )

          OutlinedTextField(
            value = whatsapp,
            onValueChange = { whatsapp = it },
            label = { Text("Número de WhatsApp (+258...)") },
            modifier = Modifier.fillMaxWidth().testTag("cust_whatsapp_field")
          )

          OutlinedTextField(
            value = city,
            onValueChange = { city = it },
            label = { Text("Cidade / Província em Moçambique") },
            modifier = Modifier.fillMaxWidth().testTag("cust_city_field")
          )

          OutlinedTextField(
            value = desc,
            onValueChange = { desc = it },
            label = { Text("Descrição da Loja") },
            maxLines = 3,
            modifier = Modifier.fillMaxWidth().testTag("cust_desc_field")
          )

          Spacer(modifier = Modifier.height(6.dp))

          Button(
            onClick = {
              onSave(
                store.copy(
                  name = name.trim(),
                  slug = slug.trim(),
                  whatsappNumber = whatsapp.trim(),
                  city = city.trim(),
                  description = desc.trim(),
                  updatedAt = System.currentTimeMillis()
                )
              )
            },
            colors = ButtonDefaults.buttonColors(containerColor = ZapEmerald),
            modifier = Modifier.fillMaxWidth().testTag("save_customization_btn"),
            shape = RoundedCornerShape(8.dp)
          ) {
            Text("Guardar Alterações")
          }
        }
      }
    }
  }
}

@Composable
fun PlanUsageContent(
  store: com.example.data.local.model.StoreEntity,
  productCount: Int,
  onUpgradeToPro: () -> Unit
) {
  val plan = PlanType.fromId(store.planId)

  LazyColumn(
    modifier = Modifier.fillMaxSize().padding(16.dp),
    verticalArrangement = Arrangement.spacedBy(14.dp)
  ) {
    item {
      Text("Plano & Limites de Utilização", fontWeight = FontWeight.Bold, fontSize = 16.sp, color = ZapSlateDark)
    }

    item {
      Card(
        shape = RoundedCornerShape(14.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
      ) {
        Column(modifier = Modifier.padding(18.dp)) {
          Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
          ) {
            Column {
              Text(plan.title, fontWeight = FontWeight.ExtraBold, fontSize = 18.sp, color = ZapSlateDark)
              Text("Estado: Ativo e Operacional", fontSize = 12.sp, color = ZapEmeraldDark)
            }
            Text(
              if (plan == PlanType.FREE) "0 MT/mês" else "299 MT/mês",
              fontWeight = FontWeight.Bold,
              fontSize = 18.sp,
              color = ZapEmerald
            )
          }

          HorizontalDivider(modifier = Modifier.padding(vertical = 12.dp))

          Text("Capacidade de Produtos:", fontSize = 12.sp, fontWeight = FontWeight.SemiBold, color = ZapSlateDark)
          Spacer(modifier = Modifier.height(4.dp))
          Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween
          ) {
            Text("$productCount produtos utilizados", fontSize = 12.sp, color = ZapSlateMuted)
            Text("Limite: ${plan.maxProducts}", fontSize = 12.sp, fontWeight = FontWeight.Bold, color = ZapSlateDark)
          }

          Spacer(modifier = Modifier.height(6.dp))

          LinearProgressIndicator(
            progress = { (productCount.toFloat() / plan.maxProducts.toFloat()).coerceIn(0f, 1f) },
            modifier = Modifier.fillMaxWidth().height(8.dp).clip(RoundedCornerShape(4.dp)),
            color = ZapEmerald,
            trackColor = Color(0xFFE2E8F0)
          )

          if (plan == PlanType.FREE) {
            Spacer(modifier = Modifier.height(18.dp))
            Surface(
              shape = RoundedCornerShape(10.dp),
              color = ZapEmeraldLight,
              modifier = Modifier.fillMaxWidth()
            ) {
              Column(modifier = Modifier.padding(12.dp)) {
                Text("Vantagens do Plano Pro (299 MT/mês):", fontWeight = FontWeight.Bold, fontSize = 13.sp, color = ZapEmeraldDark)
                Spacer(modifier = Modifier.height(6.dp))
                Text("• Até 100 produtos no teu catálogo", fontSize = 12.sp, color = ZapEmeraldDark)
                Text("• Estatísticas avançadas de cliques e acessos", fontSize = 12.sp, color = ZapEmeraldDark)
                Text("• Suporte prioritário via WhatsApp", fontSize = 12.sp, color = ZapEmeraldDark)
                Spacer(modifier = Modifier.height(10.dp))
                Button(
                  onClick = onUpgradeToPro,
                  colors = ButtonDefaults.buttonColors(containerColor = ZapEmerald),
                  shape = RoundedCornerShape(8.dp),
                  modifier = Modifier.fillMaxWidth().testTag("upgrade_plan_btn")
                ) {
                  Text("Ativar Plano Pro (299 MT/mês)", fontWeight = FontWeight.Bold)
                }
              }
            }
          }
        }
      }
    }
  }
}
