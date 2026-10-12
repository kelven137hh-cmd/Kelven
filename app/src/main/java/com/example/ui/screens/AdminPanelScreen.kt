package com.example.ui.screens

import androidx.compose.foundation.background
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
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.AdminPanelSettings
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.ExitToApp
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Phone
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.ShoppingBag
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.filled.Storefront
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ElevatedCard
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Surface
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.model.PlanType
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
fun AdminPanelScreen(
  viewModel: ZapViewModel,
  onBack: () -> Unit
) {
  val allStores by viewModel.allStores.collectAsState()
  val currentProfile by viewModel.currentProfile.collectAsState()
  val totalWhatsappClicks by viewModel.totalPlatformWhatsappClicks.collectAsState()

  var searchQuery by remember { mutableStateOf("") }

  // Check admin authorization
  if (currentProfile?.role != "ADMIN") {
    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
      Column(horizontalAlignment = Alignment.CenterHorizontally, modifier = Modifier.padding(24.dp)) {
        Icon(Icons.Default.Lock, contentDescription = null, tint = ZapRed, modifier = Modifier.size(48.dp))
        Spacer(modifier = Modifier.height(12.dp))
        Text("Acesso Restrito", fontWeight = FontWeight.Bold, fontSize = 18.sp, color = ZapSlateDark)
        Text(
          "Apenas administradores da plataforma têm autorização para aceder a esta área.",
          fontSize = 12.sp,
          color = ZapSlateMuted,
          modifier = Modifier.padding(vertical = 8.dp),
          textAlign = androidx.compose.ui.text.style.TextAlign.Center
        )
        Button(onClick = onBack, colors = ButtonDefaults.buttonColors(containerColor = ZapEmerald)) {
          Text("Voltar ao Início")
        }
      }
    }
    return
  }

  val filteredStores = allStores.filter {
    searchQuery.isBlank() || it.name.contains(searchQuery, ignoreCase = true) ||
      it.slug.contains(searchQuery, ignoreCase = true) ||
      it.city.contains(searchQuery, ignoreCase = true)
  }

  Column(
    modifier = Modifier
      .fillMaxSize()
      .background(MaterialTheme.colorScheme.background)
  ) {
    // Top Bar
    Surface(
      modifier = Modifier.fillMaxWidth(),
      color = ZapSlateDark,
      tonalElevation = 4.dp
    ) {
      Row(
        modifier = Modifier
          .fillMaxWidth()
          .padding(horizontal = 16.dp, vertical = 12.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
      ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
          IconButton(onClick = onBack, modifier = Modifier.testTag("admin_back_btn")) {
            Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Voltar", tint = Color.White)
          }
          Spacer(modifier = Modifier.width(6.dp))
          Column {
            Row(verticalAlignment = Alignment.CenterVertically) {
              Icon(Icons.Default.AdminPanelSettings, contentDescription = null, tint = ZapEmerald, modifier = Modifier.size(18.dp))
              Spacer(modifier = Modifier.width(6.dp))
              Text("Painel Administrativo SaaS", fontWeight = FontWeight.Bold, fontSize = 16.sp, color = Color.White)
            }
            Text("Gestão Global de Lojas & Subscrições", fontSize = 11.sp, color = Color(0xFF94A3B8))
          }
        }

        IconButton(onClick = { viewModel.logout() }) {
          Icon(Icons.Default.ExitToApp, contentDescription = "Sair", tint = Color.White)
        }
      }
    }

    LazyColumn(
      modifier = Modifier
        .fillMaxSize()
        .padding(16.dp),
      verticalArrangement = Arrangement.spacedBy(14.dp),
      contentPadding = PaddingValues(bottom = 60.dp)
    ) {
      // Platform Global Metrics
      item {
        Text("Métricas Globais da Plataforma", fontWeight = FontWeight.Bold, fontSize = 15.sp, color = ZapSlateDark)
        Spacer(modifier = Modifier.height(6.dp))
        Row(
          modifier = Modifier.fillMaxWidth(),
          horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
          MetricBox(
            modifier = Modifier.weight(1f),
            label = "Lojas Criadas",
            value = allStores.size.toString(),
            icon = Icons.Default.Storefront,
            color = ZapEmerald
          )
          MetricBox(
            modifier = Modifier.weight(1f),
            label = "Lojas Publicadas",
            value = allStores.count { it.isPublished }.toString(),
            icon = Icons.Default.Check,
            color = ZapViolet
          )
          MetricBox(
            modifier = Modifier.weight(1f),
            label = "Cliques WhatsApp",
            value = totalWhatsappClicks.toString(),
            icon = Icons.Default.Phone,
            color = ZapWhatsappGreen
          )
        }
      }

      // Search field
      item {
        OutlinedTextField(
          value = searchQuery,
          onValueChange = { searchQuery = it },
          placeholder = { Text("Pesquisar lojas por nome, slug ou cidade...") },
          leadingIcon = { Icon(Icons.Default.Search, contentDescription = null) },
          singleLine = true,
          modifier = Modifier.fillMaxWidth().testTag("admin_search_field"),
          shape = RoundedCornerShape(10.dp)
        )
      }

      // Stores List with Admin Actions
      item {
        Text("Lojas Registadas (${filteredStores.size}):", fontWeight = FontWeight.Bold, fontSize = 14.sp, color = ZapSlateDark)
      }

      items(filteredStores) { store ->
        val plan = PlanType.fromId(store.planId)
        Card(
          shape = RoundedCornerShape(12.dp),
          colors = CardDefaults.cardColors(containerColor = Color.White),
          elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
        ) {
          Column(modifier = Modifier.padding(14.dp)) {
            Row(
              modifier = Modifier.fillMaxWidth(),
              horizontalArrangement = Arrangement.SpaceBetween,
              verticalAlignment = Alignment.CenterVertically
            ) {
              Column {
                Text(store.name, fontWeight = FontWeight.Bold, fontSize = 15.sp, color = ZapSlateDark)
                Text(
                  "zapcatalogo.mz/loja/${store.slug} • ${store.city}",
                  fontSize = 11.sp,
                  color = ZapSlateMuted
                )
              }

              Surface(
                shape = RoundedCornerShape(6.dp),
                color = if (store.isPublished) ZapEmeraldLight else Color(0xFFF1F5F9)
              ) {
                Text(
                  text = if (store.isPublished) "Publicada" else "Suspensa",
                  color = if (store.isPublished) ZapEmeraldDark else Color.Gray,
                  fontSize = 10.sp,
                  fontWeight = FontWeight.Bold,
                  modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                )
              }
            }

            HorizontalDivider(modifier = Modifier.padding(vertical = 10.dp))

            Row(
              modifier = Modifier.fillMaxWidth(),
              horizontalArrangement = Arrangement.SpaceBetween,
              verticalAlignment = Alignment.CenterVertically
            ) {
              Row(verticalAlignment = Alignment.CenterVertically) {
                Text("Plano Atual: ", fontSize = 12.sp, color = ZapSlateDark)
                Surface(
                  shape = RoundedCornerShape(4.dp),
                  color = if (plan == PlanType.PRO) ZapViolet.copy(alpha = 0.15f) else Color(0xFFF1F5F9)
                ) {
                  Text(
                    text = plan.badgeLabel,
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Bold,
                    color = if (plan == PlanType.PRO) ZapViolet else ZapSlateDark,
                    modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                  )
                }
              }

              // 1-Click Plan Switcher
              Button(
                onClick = {
                  val newPlan = if (store.planId == "PRO") "FREE" else "PRO"
                  viewModel.updatePlan(store.id, newPlan)
                },
                colors = ButtonDefaults.buttonColors(
                  containerColor = if (store.planId == "PRO") Color.Gray else ZapEmerald
                ),
                shape = RoundedCornerShape(6.dp),
                contentPadding = PaddingValues(horizontal = 10.dp, vertical = 4.dp),
                modifier = Modifier.testTag("admin_toggle_plan_${store.id}")
              ) {
                Text(
                  text = if (store.planId == "PRO") "Descer p/ Gratuito" else "Ativar Plano Pro",
                  fontSize = 11.sp,
                  fontWeight = FontWeight.Bold
                )
              }
            }

            Spacer(modifier = Modifier.height(8.dp))

            Row(
              modifier = Modifier.fillMaxWidth(),
              horizontalArrangement = Arrangement.SpaceBetween,
              verticalAlignment = Alignment.CenterVertically
            ) {
              Text("WhatsApp: ${store.whatsappNumber}", fontSize = 11.sp, color = ZapSlateMuted)

              Row(verticalAlignment = Alignment.CenterVertically) {
                Text("Publicação:", fontSize = 11.sp, color = ZapSlateDark)
                Spacer(modifier = Modifier.width(6.dp))
                Switch(
                  checked = store.isPublished,
                  onCheckedChange = { viewModel.toggleStorePublish(store.id, it) },
                  colors = SwitchDefaults.colors(checkedThumbColor = Color.White, checkedTrackColor = ZapEmerald)
                )
              }
            }
          }
        }
      }
    }
  }
}
