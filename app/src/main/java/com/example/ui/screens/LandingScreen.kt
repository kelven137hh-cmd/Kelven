package com.example.ui.screens

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
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
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowForward
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.ChevronRight
import androidx.compose.material.icons.filled.ExpandLess
import androidx.compose.material.icons.filled.ExpandMore
import androidx.compose.material.icons.filled.FlashOn
import androidx.compose.material.icons.filled.Link
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Phone
import androidx.compose.material.icons.filled.ShoppingBag
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.filled.Storefront
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ElevatedCard
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Surface
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
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import coil.request.ImageRequest
import com.example.R
import com.example.model.PlanType
import com.example.ui.theme.ZapEmerald
import com.example.ui.theme.ZapEmeraldDark
import com.example.ui.theme.ZapEmeraldLight
import com.example.ui.theme.ZapSlateDark
import com.example.ui.theme.ZapSlateMuted
import com.example.ui.theme.ZapViolet
import com.example.ui.theme.ZapWhatsappGreen
import com.example.ui.viewmodel.AppScreen
import com.example.ui.viewmodel.ZapViewModel

@Composable
fun LandingScreen(
  viewModel: ZapViewModel,
  onNavigateToAuth: () -> Unit,
  onNavigateToWizard: () -> Unit,
  onOpenStoreCatalog: (slug: String) -> Unit
) {
  val allStores by viewModel.allStores.collectAsState()
  val currentProfile by viewModel.currentProfile.collectAsState()

  LazyColumn(
    modifier = Modifier
      .fillMaxSize()
      .background(MaterialTheme.colorScheme.background)
      .testTag("landing_screen_list"),
    contentPadding = PaddingValues(bottom = 40.dp)
  ) {
    // Top Bar
    item {
      LandingTopBar(
        isLoggedIn = currentProfile != null,
        userRole = currentProfile?.role,
        onLoginClick = onNavigateToAuth,
        onDashboardClick = {
          if (currentProfile?.role == "ADMIN") {
            viewModel.navigateTo(AppScreen.AdminPanel)
          } else {
            viewModel.navigateTo(AppScreen.Dashboard)
          }
        },
        onWizardClick = onNavigateToWizard
      )
    }

    // Hero Section
    item {
      HeroSection(
        onStartFreeClick = {
          if (currentProfile != null) onNavigateToWizard() else onNavigateToAuth()
        },
        onExploreDemos = {
          if (allStores.isNotEmpty()) {
            onOpenStoreCatalog(allStores.first().slug)
          }
        }
      )
    }

    // Benefits Section
    item {
      BenefitsSection()
    }

    // How it works
    item {
      HowItWorksSection()
    }

    // Interactive Demo Stores Showcase
    item {
      DemoStoresSection(
        stores = allStores,
        onSelectStore = onOpenStoreCatalog
      )
    }

    // Pricing Section (Free vs Pro 299 MT)
    item {
      PricingSection(
        onSelectPlan = {
          if (currentProfile != null) onNavigateToWizard() else onNavigateToAuth()
        }
      )
    }

    // FAQ Section
    item {
      FaqSection()
    }

    // Footer
    item {
      LandingFooter()
    }
  }
}

@Composable
fun LandingTopBar(
  isLoggedIn: Boolean,
  userRole: String?,
  onLoginClick: () -> Unit,
  onDashboardClick: () -> Unit,
  onWizardClick: () -> Unit
) {
  Surface(
    modifier = Modifier.fillMaxWidth(),
    color = MaterialTheme.colorScheme.surface,
    tonalElevation = 2.dp
  ) {
    Row(
      modifier = Modifier
        .fillMaxWidth()
        .padding(horizontal = 16.dp, vertical = 12.dp),
      verticalAlignment = Alignment.CenterVertically,
      horizontalArrangement = Arrangement.SpaceBetween
    ) {
      Row(verticalAlignment = Alignment.CenterVertically) {
        Box(
          modifier = Modifier
            .size(36.dp)
            .clip(RoundedCornerShape(10.dp))
            .background(
              Brush.linearGradient(
                colors = listOf(ZapEmerald, ZapViolet)
              )
            ),
          contentAlignment = Alignment.Center
        ) {
          Icon(
            imageVector = Icons.Default.ShoppingBag,
            contentDescription = "ZapCatálogo Logo",
            tint = Color.White,
            modifier = Modifier.size(20.dp)
          )
        }
        Spacer(modifier = Modifier.width(8.dp))
        Column {
          Text(
            text = "ZapCatálogo",
            fontWeight = FontWeight.Bold,
            fontSize = 18.sp,
            color = ZapSlateDark
          )
          Text(
            text = "Moçambique 🇲🇿",
            fontSize = 11.sp,
            color = ZapEmerald,
            fontWeight = FontWeight.Medium
          )
        }
      }

      Row(verticalAlignment = Alignment.CenterVertically) {
        if (isLoggedIn) {
          Button(
            onClick = onDashboardClick,
            modifier = Modifier.testTag("top_bar_dashboard_button"),
            colors = ButtonDefaults.buttonColors(containerColor = ZapEmerald),
            shape = RoundedCornerShape(8.dp),
            contentPadding = PaddingValues(horizontal = 12.dp, vertical = 6.dp)
          ) {
            Icon(
              imageVector = if (userRole == "ADMIN") Icons.Default.Lock else Icons.Default.Storefront,
              contentDescription = null,
              modifier = Modifier.size(16.dp)
            )
            Spacer(modifier = Modifier.width(4.dp))
            Text(if (userRole == "ADMIN") "Painel Admin" else "Meu Painel", fontSize = 12.sp)
          }
        } else {
          OutlinedButton(
            onClick = onLoginClick,
            modifier = Modifier.testTag("top_bar_login_button"),
            shape = RoundedCornerShape(8.dp),
            contentPadding = PaddingValues(horizontal = 12.dp, vertical = 6.dp)
          ) {
            Text("Entrar", fontSize = 12.sp, color = ZapSlateDark)
          }
          Spacer(modifier = Modifier.width(8.dp))
          Button(
            onClick = onWizardClick,
            modifier = Modifier.testTag("top_bar_signup_button"),
            colors = ButtonDefaults.buttonColors(containerColor = ZapEmerald),
            shape = RoundedCornerShape(8.dp),
            contentPadding = PaddingValues(horizontal = 12.dp, vertical = 6.dp)
          ) {
            Text("Criar Loja", fontSize = 12.sp, fontWeight = FontWeight.SemiBold)
          }
        }
      }
    }
  }
}

@Composable
fun HeroSection(
  onStartFreeClick: () -> Unit,
  onExploreDemos: () -> Unit
) {
  val context = LocalContext.current

  Column(
    modifier = Modifier
      .fillMaxWidth()
      .padding(horizontal = 20.dp, vertical = 24.dp),
    horizontalAlignment = Alignment.CenterHorizontally
  ) {
    // Mozambique Badge
    Surface(
      shape = RoundedCornerShape(20.dp),
      color = ZapEmeraldLight,
      modifier = Modifier.padding(bottom = 12.dp)
    ) {
      Row(
        modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp),
        verticalAlignment = Alignment.CenterVertically
      ) {
        Icon(
          imageVector = Icons.Default.FlashOn,
          contentDescription = null,
          tint = ZapEmeraldDark,
          modifier = Modifier.size(14.dp)
        )
        Spacer(modifier = Modifier.width(6.dp))
        Text(
          text = "Catálogos Digitais para WhatsApp em Moçambique",
          color = ZapEmeraldDark,
          fontSize = 11.sp,
          fontWeight = FontWeight.SemiBold
        )
      }
    }

    Text(
      text = "Transforma os teus produtos numa loja digital e recebe encomendas pelo WhatsApp.",
      fontSize = 26.sp,
      lineHeight = 32.sp,
      fontWeight = FontWeight.ExtraBold,
      color = ZapSlateDark,
      textAlign = TextAlign.Center
    )

    Spacer(modifier = Modifier.height(12.dp))

    Text(
      text = "Cria o teu catálogo online, apresenta os teus produtos de forma profissional e partilha um único link com os teus clientes em Maputo, Matola, Beira e Nampula.",
      fontSize = 14.sp,
      lineHeight = 20.sp,
      color = ZapSlateMuted,
      textAlign = TextAlign.Center
    )

    Spacer(modifier = Modifier.height(20.dp))

    Row(
      modifier = Modifier.fillMaxWidth(),
      horizontalArrangement = Arrangement.spacedBy(10.dp)
    ) {
      Button(
        onClick = onStartFreeClick,
        modifier = Modifier
          .weight(1f)
          .height(48.dp)
          .testTag("hero_create_catalog_button"),
        colors = ButtonDefaults.buttonColors(containerColor = ZapEmerald),
        shape = RoundedCornerShape(12.dp)
      ) {
        Text("Criar Catálogo Grátis", fontWeight = FontWeight.Bold, fontSize = 13.sp)
        Spacer(modifier = Modifier.width(4.dp))
        Icon(Icons.Default.ArrowForward, contentDescription = null, modifier = Modifier.size(16.dp))
      }

      OutlinedButton(
        onClick = onExploreDemos,
        modifier = Modifier
          .weight(1f)
          .height(48.dp)
          .testTag("hero_see_demo_button"),
        shape = RoundedCornerShape(12.dp)
      ) {
        Icon(Icons.Default.Visibility, contentDescription = null, tint = ZapSlateDark, modifier = Modifier.size(16.dp))
        Spacer(modifier = Modifier.width(6.dp))
        Text("Ver Lojas Demo", color = ZapSlateDark, fontWeight = FontWeight.SemiBold, fontSize = 13.sp)
      }
    }

    Spacer(modifier = Modifier.height(24.dp))

    // Smartphone Showcase Mockup
    Card(
      modifier = Modifier
        .fillMaxWidth()
        .clip(RoundedCornerShape(18.dp)),
      elevation = CardDefaults.cardElevation(defaultElevation = 6.dp),
      colors = CardDefaults.cardColors(containerColor = Color.White)
    ) {
      Column {
        Box(
          modifier = Modifier
            .fillMaxWidth()
            .height(220.dp)
        ) {
          AsyncImage(
            model = ImageRequest.Builder(context)
              .data("file:///android_asset/hero_showcase.jpg")
              .error(R.drawable.hero_showcase)
              .placeholder(R.drawable.hero_showcase)
              .build(),
            contentDescription = "Demonstração de Catálogo ZapCatálogo no Smartphone",
            modifier = Modifier.fillMaxSize(),
            contentScale = ContentScale.Crop
          )

          // WhatsApp Pill on corner
          Surface(
            shape = RoundedCornerShape(12.dp),
            color = ZapWhatsappGreen,
            modifier = Modifier
              .align(Alignment.BottomEnd)
              .padding(12.dp),
            shadowElevation = 4.dp
          ) {
            Row(
              modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp),
              verticalAlignment = Alignment.CenterVertically
            ) {
              Icon(
                imageVector = Icons.Default.Phone,
                contentDescription = null,
                tint = Color.White,
                modifier = Modifier.size(14.dp)
              )
              Spacer(modifier = Modifier.width(6.dp))
              Text(
                text = "Encomendas no WhatsApp",
                color = Color.White,
                fontSize = 11.sp,
                fontWeight = FontWeight.Bold
              )
            }
          }
        }

        Row(
          modifier = Modifier
            .fillMaxWidth()
            .background(Color(0xFFF8FAFC))
            .padding(12.dp),
          horizontalArrangement = Arrangement.SpaceBetween,
          verticalAlignment = Alignment.CenterVertically
        ) {
          Column {
            Text(
              text = "Exemplo: Moda Elegante (Maputo)",
              fontWeight = FontWeight.Bold,
              fontSize = 13.sp,
              color = ZapSlateDark
            )
            Text(
              text = "Preços em Meticais (MT) • Checkout direto no WhatsApp",
              fontSize = 11.sp,
              color = ZapSlateMuted
            )
          }

          Surface(
            color = ZapEmeraldLight,
            shape = RoundedCornerShape(6.dp)
          ) {
            Text(
              text = "100% Funcional",
              color = ZapEmeraldDark,
              fontSize = 10.sp,
              fontWeight = FontWeight.Bold,
              modifier = Modifier.padding(horizontal = 6.dp, vertical = 3.dp)
            )
          }
        }
      }
    }
  }
}

@Composable
fun BenefitsSection() {
  Column(
    modifier = Modifier
      .fillMaxWidth()
      .padding(horizontal = 20.dp, vertical = 16.dp)
  ) {
    Text(
      text = "Vantagens para o Teu Negócio",
      fontSize = 20.sp,
      fontWeight = FontWeight.Bold,
      color = ZapSlateDark
    )
    Text(
      text = "Criado a pensar nos lojistas moçambicanos que querem vender mais sem complicação técnica.",
      fontSize = 13.sp,
      color = ZapSlateMuted,
      modifier = Modifier.padding(top = 4.dp, bottom = 16.dp)
    )

    val benefits = listOf(
      Triple(
        Icons.Default.Storefront,
        "Criação Simples & Rápida",
        "Regista a tua loja em 2 minutos. Escolhe o teu modelo de Moda, Calçado ou Perfumaria e publica de imediato."
      ),
      Triple(
        Icons.Default.ShoppingBag,
        "Organização de Produtos & MT",
        "Apresenta os teus produtos com fotografias, preços claros em Meticais, tamanhos, cores e categorias."
      ),
      Triple(
        Icons.Default.Link,
        "Um Único Link para Partilhar",
        "Coloca o teu link na biografia do Instagram, partilha no WhatsApp e status do Facebook."
      ),
      Triple(
        Icons.Default.Phone,
        "Encomendas no WhatsApp",
        "O cliente escolhe produtos, monta o carrinho e envia a encomenda formatada direto para o teu WhatsApp."
      )
    )

    Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
      benefits.forEach { (icon, title, desc) ->
        ElevatedCard(
          shape = RoundedCornerShape(12.dp),
          colors = CardDefaults.elevatedCardColors(containerColor = Color.White),
          elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
        ) {
          Row(
            modifier = Modifier
              .fillMaxWidth()
              .padding(14.dp),
            verticalAlignment = Alignment.Top
          ) {
            Box(
              modifier = Modifier
                .size(40.dp)
                .clip(RoundedCornerShape(10.dp))
                .background(ZapEmeraldLight),
              contentAlignment = Alignment.Center
            ) {
              Icon(icon, contentDescription = null, tint = ZapEmeraldDark, modifier = Modifier.size(22.dp))
            }
            Spacer(modifier = Modifier.width(12.dp))
            Column {
              Text(text = title, fontWeight = FontWeight.Bold, fontSize = 14.sp, color = ZapSlateDark)
              Text(
                text = desc,
                fontSize = 12.sp,
                color = ZapSlateMuted,
                lineHeight = 16.sp,
                modifier = Modifier.padding(top = 2.dp)
              )
            }
          }
        }
      }
    }
  }
}

@Composable
fun HowItWorksSection() {
  Column(
    modifier = Modifier
      .fillMaxWidth()
      .background(Color(0xFFF1F5F9))
      .padding(horizontal = 20.dp, vertical = 24.dp)
  ) {
    Text(
      text = "Como Funciona em 3 Passos",
      fontSize = 20.sp,
      fontWeight = FontWeight.Bold,
      color = ZapSlateDark
    )
    Text(
      text = "Do cadastro à primeira encomenda em menos de 10 minutos.",
      fontSize = 13.sp,
      color = ZapSlateMuted,
      modifier = Modifier.padding(top = 4.dp, bottom = 16.dp)
    )

    val steps = listOf(
      Pair("1", "Cria a tua Loja\nRegista os dados da tua loja, número de WhatsApp e escolhe o teu modelo visual."),
      Pair("2", "Adiciona os teus Produtos\nCarrega fotografias, define preços em Meticais (MT), tamanhos e categorias."),
      Pair("3", "Partilha e Recebe Encomendas\nPartilha o link e recebe encomendas completas com cálculo de total no WhatsApp.")
    )

    steps.forEach { (num, text) ->
      Row(
        modifier = Modifier
          .fillMaxWidth()
          .padding(vertical = 8.dp),
        verticalAlignment = Alignment.Top
      ) {
        Box(
          modifier = Modifier
            .size(32.dp)
            .clip(CircleShape)
            .background(ZapEmerald),
          contentAlignment = Alignment.Center
        ) {
          Text(text = num, color = Color.White, fontWeight = FontWeight.Bold, fontSize = 14.sp)
        }
        Spacer(modifier = Modifier.width(12.dp))
        val parts = text.split("\n")
        Column {
          Text(text = parts[0], fontWeight = FontWeight.Bold, fontSize = 14.sp, color = ZapSlateDark)
          Text(text = parts[1], fontSize = 12.sp, color = ZapSlateMuted, lineHeight = 16.sp)
        }
      }
    }
  }
}

@Composable
fun DemoStoresSection(
  stores: List<com.example.data.local.model.StoreEntity>,
  onSelectStore: (slug: String) -> Unit
) {
  val context = LocalContext.current

  Column(
    modifier = Modifier
      .fillMaxWidth()
      .padding(horizontal = 20.dp, vertical = 24.dp)
  ) {
    Row(
      modifier = Modifier.fillMaxWidth(),
      horizontalArrangement = Arrangement.SpaceBetween,
      verticalAlignment = Alignment.CenterVertically
    ) {
      Column {
        Text(
          text = "Exemplos de Lojas no Ar",
          fontSize = 20.sp,
          fontWeight = FontWeight.Bold,
          color = ZapSlateDark
        )
        Text(
          text = "Experimenta o catálogo como se fosses um cliente real:",
          fontSize = 12.sp,
          color = ZapSlateMuted
        )
      }
    }

    Spacer(modifier = Modifier.height(14.dp))

    LazyRow(
      horizontalArrangement = Arrangement.spacedBy(14.dp),
      contentPadding = PaddingValues(end = 12.dp)
    ) {
      items(stores) { store ->
        Card(
          modifier = Modifier
            .width(240.dp)
            .clickable { onSelectStore(store.slug) }
            .testTag("demo_store_card_${store.slug}"),
          shape = RoundedCornerShape(14.dp),
          colors = CardDefaults.cardColors(containerColor = Color.White),
          elevation = CardDefaults.cardElevation(defaultElevation = 3.dp)
        ) {
          Column {
            Box(
              modifier = Modifier
                .fillMaxWidth()
                .height(110.dp)
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
                contentDescription = store.name,
                modifier = Modifier.fillMaxSize(),
                contentScale = ContentScale.Crop
              )

              Surface(
                modifier = Modifier
                  .align(Alignment.TopEnd)
                  .padding(8.dp),
                shape = RoundedCornerShape(6.dp),
                color = Color.Black.copy(alpha = 0.65f)
              ) {
                Text(
                  text = store.businessCategory,
                  color = Color.White,
                  fontSize = 10.sp,
                  fontWeight = FontWeight.SemiBold,
                  modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                )
              }
            }

            Column(modifier = Modifier.padding(12.dp)) {
              Text(
                text = store.name,
                fontWeight = FontWeight.Bold,
                fontSize = 15.sp,
                color = ZapSlateDark
              )
              Text(
                text = store.city,
                fontSize = 11.sp,
                color = ZapSlateMuted,
                modifier = Modifier.padding(top = 2.dp)
              )

              Spacer(modifier = Modifier.height(8.dp))

              Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
              ) {
                Text(
                  text = "Abrir Catálogo",
                  fontSize = 12.sp,
                  fontWeight = FontWeight.Bold,
                  color = ZapEmerald
                )
                Icon(
                  Icons.Default.ChevronRight,
                  contentDescription = null,
                  tint = ZapEmerald,
                  modifier = Modifier.size(16.dp)
                )
              }
            }
          }
        }
      }
    }
  }
}

@Composable
fun PricingSection(onSelectPlan: () -> Unit) {
  Column(
    modifier = Modifier
      .fillMaxWidth()
      .padding(horizontal = 20.dp, vertical = 20.dp)
  ) {
    Text(
      text = "Planos Transparentes em Meticais",
      fontSize = 20.sp,
      fontWeight = FontWeight.Bold,
      color = ZapSlateDark
    )
    Text(
      text = "Começa sem custos e escala quando o teu negócio crescer.",
      fontSize = 13.sp,
      color = ZapSlateMuted,
      modifier = Modifier.padding(top = 4.dp, bottom = 16.dp)
    )

    Column(verticalArrangement = Arrangement.spacedBy(14.dp)) {
      // Plano Gratuito
      ElevatedCard(
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.elevatedCardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
      ) {
        Column(modifier = Modifier.padding(18.dp)) {
          Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
          ) {
            Column {
              Text("Plano Gratuito", fontWeight = FontWeight.Bold, fontSize = 17.sp, color = ZapSlateDark)
              Text("Para quem está a começar a divulgar", fontSize = 11.sp, color = ZapSlateMuted)
            }
            Text("0 MT", fontWeight = FontWeight.ExtraBold, fontSize = 22.sp, color = ZapSlateDark)
          }

          HorizontalDivider(modifier = Modifier.padding(vertical = 12.dp))

          PlanType.FREE.features.forEach { feat ->
            Row(
              verticalAlignment = Alignment.CenterVertically,
              modifier = Modifier.padding(vertical = 3.dp)
            ) {
              Icon(Icons.Default.Check, contentDescription = null, tint = ZapEmerald, modifier = Modifier.size(16.dp))
              Spacer(modifier = Modifier.width(8.dp))
              Text(text = feat, fontSize = 12.sp, color = ZapSlateDark)
            }
          }

          Spacer(modifier = Modifier.height(14.dp))

          OutlinedButton(
            onClick = onSelectPlan,
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(10.dp)
          ) {
            Text("Começar com Plano Gratuito", color = ZapSlateDark, fontWeight = FontWeight.SemiBold)
          }
        }
      }

      // Plano Pro (299 MT/mês)
      Card(
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        border = androidx.compose.foundation.BorderStroke(2.dp, ZapEmerald),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
      ) {
        Column(modifier = Modifier.padding(18.dp)) {
          Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
          ) {
            Column {
              Row(verticalAlignment = Alignment.CenterVertically) {
                Text("Plano Pro", fontWeight = FontWeight.Bold, fontSize = 18.sp, color = ZapSlateDark)
                Spacer(modifier = Modifier.width(6.dp))
                Surface(
                  color = ZapEmerald,
                  shape = RoundedCornerShape(6.dp)
                ) {
                  Text(
                    "Recomendado",
                    color = Color.White,
                    fontSize = 10.sp,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                  )
                }
              }
              Text("Para lojas ativas com dezenas de peças", fontSize = 11.sp, color = ZapSlateMuted)
            }
            Column(horizontalAlignment = Alignment.End) {
              Text("299 MT", fontWeight = FontWeight.ExtraBold, fontSize = 22.sp, color = ZapEmerald)
              Text("por mês", fontSize = 10.sp, color = ZapSlateMuted)
            }
          }

          HorizontalDivider(modifier = Modifier.padding(vertical = 12.dp))

          PlanType.PRO.features.forEach { feat ->
            Row(
              verticalAlignment = Alignment.CenterVertically,
              modifier = Modifier.padding(vertical = 3.dp)
            ) {
              Icon(Icons.Default.Check, contentDescription = null, tint = ZapEmerald, modifier = Modifier.size(16.dp))
              Spacer(modifier = Modifier.width(8.dp))
              Text(text = feat, fontSize = 12.sp, color = ZapSlateDark, fontWeight = FontWeight.Medium)
            }
          }

          Spacer(modifier = Modifier.height(14.dp))

          Button(
            onClick = onSelectPlan,
            modifier = Modifier.fillMaxWidth(),
            colors = ButtonDefaults.buttonColors(containerColor = ZapEmerald),
            shape = RoundedCornerShape(10.dp)
          ) {
            Text("Subscrever Plano Pro", fontWeight = FontWeight.Bold)
          }
        }
      }
    }
  }
}

@Composable
fun FaqSection() {
  Column(
    modifier = Modifier
      .fillMaxWidth()
      .padding(horizontal = 20.dp, vertical = 16.dp)
  ) {
    Text(
      text = "Perguntas Frequentes (FAQ)",
      fontSize = 20.sp,
      fontWeight = FontWeight.Bold,
      color = ZapSlateDark
    )
    Text(
      text = "Tudo o que precisas de saber sobre o ZapCatálogo em Moçambique.",
      fontSize = 13.sp,
      color = ZapSlateMuted,
      modifier = Modifier.padding(top = 4.dp, bottom = 14.dp)
    )

    val faqs = listOf(
      Pair(
        "Como funciona a encomenda pelo WhatsApp?",
        "Quando o teu cliente navega pelo teu catálogo, adiciona os produtos ao carrinho e clica em 'Encomendar pelo WhatsApp'. O aplicativo monta automaticamente uma mensagem elegante com a lista de peças, tamanhos, quantidades e total em Meticais (MT) e abre a conversa contigo no WhatsApp com o pedido já pronto!"
      ),
      Pair(
        "O cliente precisa de instalar alguma aplicação?",
        "Não! O comprador não precisa instalar nada nem criar conta. Acede ao link da tua loja a partir de qualquer telemóvel, escolhe os produtos e fala diretamente contigo."
      ),
      Pair(
        "Como funciona o Plano Gratuito?",
        "O Plano Gratuito é permanente e permite cadastrar até 10 produtos e ter a tua loja 100% online. Se precisares de cadastrar até 100 produtos, podes ativar o Plano Pro por apenas 299 MT/mês."
      ),
      Pair(
        "Como recebo o pagamento das encomendas?",
        "Os pagamentos continuam a ser acordados diretamente entre ti e o comprador via M-Pesa, E-Mola, POS ou numerário na entrega. O ZapCatálogo organiza todo o pedido para não perderes vendas."
      )
    )

    Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
      faqs.forEach { (question, answer) ->
        var expanded by remember { mutableStateOf(false) }
        Card(
          shape = RoundedCornerShape(10.dp),
          colors = CardDefaults.cardColors(containerColor = Color.White),
          modifier = Modifier
            .fillMaxWidth()
            .clickable { expanded = !expanded }
        ) {
          Column(modifier = Modifier.padding(14.dp)) {
            Row(
              modifier = Modifier.fillMaxWidth(),
              horizontalArrangement = Arrangement.SpaceBetween,
              verticalAlignment = Alignment.CenterVertically
            ) {
              Text(
                text = question,
                fontWeight = FontWeight.SemiBold,
                fontSize = 13.sp,
                color = ZapSlateDark,
                modifier = Modifier.weight(1f)
              )
              Icon(
                imageVector = if (expanded) Icons.Default.ExpandLess else Icons.Default.ExpandMore,
                contentDescription = null,
                tint = ZapSlateMuted,
                modifier = Modifier.size(18.dp)
              )
            }
            AnimatedVisibility(visible = expanded) {
              Text(
                text = answer,
                fontSize = 12.sp,
                color = ZapSlateMuted,
                lineHeight = 16.sp,
                modifier = Modifier.padding(top = 8.dp)
              )
            }
          }
        }
      }
    }
  }
}

@Composable
fun LandingFooter() {
  Column(
    modifier = Modifier
      .fillMaxWidth()
      .background(ZapSlateDark)
      .padding(24.dp),
    horizontalAlignment = Alignment.CenterHorizontally
  ) {
    Row(verticalAlignment = Alignment.CenterVertically) {
      Box(
        modifier = Modifier
          .size(28.dp)
          .clip(RoundedCornerShape(8.dp))
        .background(ZapEmerald),
        contentAlignment = Alignment.Center
      ) {
        Icon(Icons.Default.ShoppingBag, contentDescription = null, tint = Color.White, modifier = Modifier.size(16.dp))
      }
      Spacer(modifier = Modifier.width(8.dp))
      Text(
        text = "ZapCatálogo Moçambique",
        color = Color.White,
        fontWeight = FontWeight.Bold,
        fontSize = 16.sp
      )
    }

    Spacer(modifier = Modifier.height(12.dp))

    Text(
      text = "A plataforma líder de catálogos digitais para WhatsApp em Moçambique.\nMaputo • Matola • Beira • Nampula",
      color = Color(0xFF94A3B8),
      fontSize = 11.sp,
      textAlign = TextAlign.Center,
      lineHeight = 16.sp
    )

    Spacer(modifier = Modifier.height(16.dp))
    HorizontalDivider(color = Color(0xFF334155))
    Spacer(modifier = Modifier.height(12.dp))

    Text(
      text = "© 2026 ZapCatálogo SaaS. Todos os direitos reservados.",
      color = Color(0xFF64748B),
      fontSize = 10.sp
    )
  }
}
