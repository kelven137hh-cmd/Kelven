package com.example.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.ArrowForward
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Link
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.Phone
import androidx.compose.material.icons.filled.ShoppingBag
import androidx.compose.material.icons.filled.Storefront
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.model.MozFormatters
import com.example.model.VisualTemplate
import com.example.ui.theme.ZapEmerald
import com.example.ui.theme.ZapEmeraldDark
import com.example.ui.theme.ZapEmeraldLight
import com.example.ui.theme.ZapSlateDark
import com.example.ui.theme.ZapSlateMuted
import com.example.ui.theme.ZapViolet
import com.example.ui.viewmodel.ZapViewModel

@Composable
fun StoreWizardScreen(
  viewModel: ZapViewModel,
  onCancel: () -> Unit
) {
  var currentStep by remember { mutableIntStateOf(1) } // 1..5

  // Form states
  var storeName by remember { mutableStateOf("") }
  var storeSlug by remember { mutableStateOf("") }
  var businessCategory by remember { mutableStateOf("Moda Feminina") }
  var whatsappNumber by remember { mutableStateOf("+258 84 ") }
  var city by remember { mutableStateOf("Maputo") }

  var selectedTemplate by remember { mutableStateOf(VisualTemplate.FEMININE) }
  var primaryColorHex by remember { mutableStateOf(0xFF059669L) }
  var secondaryColorHex by remember { mutableStateOf(0xFF7C3AEDL) }

  // Initial products
  val initialProducts = remember { mutableStateListOf<Pair<String, Double>>() }
  var tempProdName by remember { mutableStateOf("") }
  var tempProdPrice by remember { mutableStateOf("") }

  Column(
    modifier = Modifier
      .fillMaxSize()
      .background(MaterialTheme.colorScheme.background)
      .verticalScroll(rememberScrollState())
      .padding(20.dp)
  ) {
    // Top Bar
    Row(
      modifier = Modifier.fillMaxWidth(),
      verticalAlignment = Alignment.CenterVertically
    ) {
      IconButton(
        onClick = {
          if (currentStep > 1) currentStep-- else onCancel()
        },
        modifier = Modifier.testTag("wizard_back_button")
      ) {
        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Voltar", tint = ZapSlateDark)
      }
      Spacer(modifier = Modifier.width(6.dp))
      Column {
        Text(
          text = "Assistente de Criação de Loja",
          fontWeight = FontWeight.Bold,
          fontSize = 17.sp,
          color = ZapSlateDark
        )
        Text(
          text = "Passo $currentStep de 5",
          fontSize = 12.sp,
          color = ZapEmerald,
          fontWeight = FontWeight.SemiBold
        )
      }
    }

    Spacer(modifier = Modifier.height(10.dp))

    LinearProgressIndicator(
      progress = { currentStep / 5f },
      modifier = Modifier
        .fillMaxWidth()
        .clip(RoundedCornerShape(4.dp)),
      color = ZapEmerald,
      trackColor = Color(0xFFE2E8F0)
    )

    Spacer(modifier = Modifier.height(20.dp))

    when (currentStep) {
      // Step 1: Store Information
      1 -> {
        Text("Etapa 1 — Informações da Loja", fontSize = 18.sp, fontWeight = FontWeight.Bold, color = ZapSlateDark)
        Text("Define a identidade básica da tua loja digital.", fontSize = 12.sp, color = ZapSlateMuted)
        Spacer(modifier = Modifier.height(16.dp))

        Card(
          shape = RoundedCornerShape(14.dp),
          colors = CardDefaults.cardColors(containerColor = Color.White),
          elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
        ) {
          Column(modifier = Modifier.padding(16.dp)) {
            OutlinedTextField(
              value = storeName,
              onValueChange = {
                storeName = it
                if (storeSlug.isBlank() || storeSlug == storeName.lowercase().replace(" ", "-").dropLast(1)) {
                  storeSlug = it.trim().lowercase().replace("\\s+".toRegex(), "-").replace("[^a-z0-9-]".toRegex(), "")
                }
              },
              label = { Text("Nome da Loja *") },
              placeholder = { Text("ex: Boutique Bella Maputo") },
              singleLine = true,
              modifier = Modifier.fillMaxWidth().testTag("wizard_name_input")
            )

            Spacer(modifier = Modifier.height(12.dp))

            OutlinedTextField(
              value = storeSlug,
              onValueChange = { storeSlug = it.lowercase().replace(" ", "-") },
              label = { Text("Link público (slug) *") },
              prefix = { Text("zapcatalogo.mz/loja/", color = ZapSlateMuted, fontSize = 12.sp) },
              singleLine = true,
              modifier = Modifier.fillMaxWidth().testTag("wizard_slug_input")
            )

            Spacer(modifier = Modifier.height(12.dp))

            Text("Tipo de Negócio / Nicho:", fontSize = 13.sp, fontWeight = FontWeight.SemiBold, color = ZapSlateDark)
            Spacer(modifier = Modifier.height(6.dp))
            val categories = listOf("Moda Feminina", "Moda Masculina", "Calçado", "Perfumaria", "Acessórios")
            Row(
              modifier = Modifier.fillMaxWidth(),
              horizontalArrangement = Arrangement.spacedBy(6.dp)
            ) {
              categories.take(3).forEach { cat ->
                val selected = businessCategory == cat
                Surface(
                  shape = RoundedCornerShape(8.dp),
                  color = if (selected) ZapEmeraldLight else Color(0xFFF1F5F9),
                  border = if (selected) androidx.compose.foundation.BorderStroke(1.dp, ZapEmerald) else null,
                  modifier = Modifier
                    .weight(1f)
                    .clickable {
                      businessCategory = cat
                      selectedTemplate = when (cat) {
                        "Moda Masculina" -> VisualTemplate.MASCULINE
                        "Calçado" -> VisualTemplate.SHOES
                        "Perfumaria" -> VisualTemplate.PERFUME
                        "Acessórios" -> VisualTemplate.ACCESSORIES
                        else -> VisualTemplate.FEMININE
                      }
                    }
                ) {
                  Text(
                    text = cat,
                    fontSize = 11.sp,
                    fontWeight = if (selected) FontWeight.Bold else FontWeight.Normal,
                    color = if (selected) ZapEmeraldDark else ZapSlateDark,
                    modifier = Modifier.padding(vertical = 8.dp),
                    textAlign = androidx.compose.ui.text.style.TextAlign.Center
                  )
                }
              }
            }
            Spacer(modifier = Modifier.height(6.dp))
            Row(
              modifier = Modifier.fillMaxWidth(),
              horizontalArrangement = Arrangement.spacedBy(6.dp)
            ) {
              categories.drop(3).forEach { cat ->
                val selected = businessCategory == cat
                Surface(
                  shape = RoundedCornerShape(8.dp),
                  color = if (selected) ZapEmeraldLight else Color(0xFFF1F5F9),
                  border = if (selected) androidx.compose.foundation.BorderStroke(1.dp, ZapEmerald) else null,
                  modifier = Modifier
                    .weight(1f)
                    .clickable {
                      businessCategory = cat
                      selectedTemplate = when (cat) {
                        "Moda Masculina" -> VisualTemplate.MASCULINE
                        "Calçado" -> VisualTemplate.SHOES
                        "Perfumaria" -> VisualTemplate.PERFUME
                        "Acessórios" -> VisualTemplate.ACCESSORIES
                        else -> VisualTemplate.FEMININE
                      }
                    }
                ) {
                  Text(
                    text = cat,
                    fontSize = 11.sp,
                    fontWeight = if (selected) FontWeight.Bold else FontWeight.Normal,
                    color = if (selected) ZapEmeraldDark else ZapSlateDark,
                    modifier = Modifier.padding(vertical = 8.dp),
                    textAlign = androidx.compose.ui.text.style.TextAlign.Center
                  )
                }
              }
            }

            Spacer(modifier = Modifier.height(14.dp))

            OutlinedTextField(
              value = whatsappNumber,
              onValueChange = { whatsappNumber = it },
              label = { Text("Número de WhatsApp para Encomendas *") },
              placeholder = { Text("+258 84 123 4567") },
              leadingIcon = { Icon(Icons.Default.Phone, contentDescription = null, tint = ZapEmerald) },
              keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Phone),
              singleLine = true,
              modifier = Modifier.fillMaxWidth().testTag("wizard_whatsapp_input")
            )

            Spacer(modifier = Modifier.height(12.dp))

            OutlinedTextField(
              value = city,
              onValueChange = { city = it },
              label = { Text("Cidade / Localização em Moçambique") },
              placeholder = { Text("ex: Maputo, Polana ou Matola") },
              leadingIcon = { Icon(Icons.Default.LocationOn, contentDescription = null) },
              singleLine = true,
              modifier = Modifier.fillMaxWidth().testTag("wizard_city_input")
            )
          }
        }
      }

      // Step 2: Visual Template
      2 -> {
        Text("Etapa 2 — Escolha do Modelo Visual", fontSize = 18.sp, fontWeight = FontWeight.Bold, color = ZapSlateDark)
        Text("Modelos concebidos especificamente para cada tipo de produto.", fontSize = 12.sp, color = ZapSlateMuted)
        Spacer(modifier = Modifier.height(16.dp))

        Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
          VisualTemplate.entries.forEach { tpl ->
            val isSelected = selectedTemplate == tpl
            Card(
              shape = RoundedCornerShape(12.dp),
              colors = CardDefaults.cardColors(containerColor = if (isSelected) ZapEmeraldLight else Color.White),
              border = if (isSelected) androidx.compose.foundation.BorderStroke(2.dp, ZapEmerald) else androidx.compose.foundation.BorderStroke(1.dp, Color(0xFFE2E8F0)),
              modifier = Modifier
                .fillMaxWidth()
                .clickable {
                  selectedTemplate = tpl
                  primaryColorHex = tpl.defaultPrimaryColor
                  secondaryColorHex = tpl.defaultSecondaryColor
                }
                .testTag("wizard_template_${tpl.id}")
            ) {
              Row(
                modifier = Modifier
                  .fillMaxWidth()
                  .padding(14.dp),
                verticalAlignment = Alignment.CenterVertically
              ) {
                Box(
                  modifier = Modifier
                    .size(40.dp)
                    .clip(CircleShape)
                    .background(Color(tpl.defaultPrimaryColor)),
                  contentAlignment = Alignment.Center
                ) {
                  Icon(Icons.Default.ShoppingBag, contentDescription = null, tint = Color.White, modifier = Modifier.size(20.dp))
                }
                Spacer(modifier = Modifier.width(12.dp))
                Column(modifier = Modifier.weight(1f)) {
                  Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(tpl.displayName, fontWeight = FontWeight.Bold, fontSize = 14.sp, color = ZapSlateDark)
                    Spacer(modifier = Modifier.width(6.dp))
                    Surface(
                      color = Color(tpl.defaultSecondaryColor).copy(alpha = 0.15f),
                      shape = RoundedCornerShape(4.dp)
                    ) {
                      Text(
                        tpl.badgeText,
                        fontSize = 9.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color(tpl.defaultSecondaryColor),
                        modifier = Modifier.padding(horizontal = 4.dp, vertical = 2.dp)
                      )
                    }
                  }
                  Text(tpl.subtitle, fontSize = 11.sp, color = ZapSlateMuted, modifier = Modifier.padding(top = 2.dp))
                }
                if (isSelected) {
                  Icon(Icons.Default.Check, contentDescription = null, tint = ZapEmerald, modifier = Modifier.size(20.dp))
                }
              }
            }
          }
        }
      }

      // Step 3: Customization & Live Preview
      3 -> {
        Text("Etapa 3 — Personalização das Cores", fontSize = 18.sp, fontWeight = FontWeight.Bold, color = ZapSlateDark)
        Text("Escolhe a cor de destaque principal para a tua marca.", fontSize = 12.sp, color = ZapSlateMuted)
        Spacer(modifier = Modifier.height(16.dp))

        Card(
          shape = RoundedCornerShape(14.dp),
          colors = CardDefaults.cardColors(containerColor = Color.White),
          elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
        ) {
          Column(modifier = Modifier.padding(16.dp)) {
            Text("Cor Principal de Destaque:", fontWeight = FontWeight.SemiBold, fontSize = 13.sp, color = ZapSlateDark)
            Spacer(modifier = Modifier.height(10.dp))

            val colorOptions = listOf(
              Pair("Verde Esmeralda", 0xFF059669L),
              Pair("Violeta Nobre", 0xFF7C3AEDL),
              Pair("Azul Petróleo", 0xFF0284C7L),
              Pair("Ouro & Âmbar", 0xFFD97706L),
              Pair("Preto Urbano", 0xFF1E293BL)
            )

            Row(
              modifier = Modifier.fillMaxWidth(),
              horizontalArrangement = Arrangement.SpaceBetween
            ) {
              colorOptions.forEach { (name, hex) ->
                val isSelected = primaryColorHex == hex
                Box(
                  modifier = Modifier
                    .size(46.dp)
                    .clip(CircleShape)
                    .background(Color(hex))
                    .clickable { primaryColorHex = hex }
                    .border(
                      width = if (isSelected) 3.dp else 1.dp,
                      color = if (isSelected) ZapSlateDark else Color.Transparent,
                      shape = CircleShape
                    ),
                  contentAlignment = Alignment.Center
                ) {
                  if (isSelected) {
                    Icon(Icons.Default.Check, contentDescription = null, tint = Color.White, modifier = Modifier.size(20.dp))
                  }
                }
              }
            }

            Spacer(modifier = Modifier.height(16.dp))
            HorizontalDivider()
            Spacer(modifier = Modifier.height(14.dp))

            Text("Pré-visualização do Cartão do Catálogo:", fontWeight = FontWeight.SemiBold, fontSize = 13.sp, color = ZapSlateDark)
            Spacer(modifier = Modifier.height(10.dp))

            // Mini Mockup Card
            Card(
              shape = RoundedCornerShape(12.dp),
              colors = CardDefaults.cardColors(containerColor = Color(0xFFF8FAFC)),
              border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFFE2E8F0)),
              modifier = Modifier.fillMaxWidth()
            ) {
              Column(modifier = Modifier.padding(12.dp)) {
                Row(
                  modifier = Modifier.fillMaxWidth(),
                  horizontalArrangement = Arrangement.SpaceBetween,
                  verticalAlignment = Alignment.CenterVertically
                ) {
                  Text(storeName.ifBlank { "Minha Loja Digital" }, fontWeight = FontWeight.Bold, fontSize = 14.sp, color = Color(primaryColorHex))
                  Surface(
                    color = Color(primaryColorHex).copy(alpha = 0.12f),
                    shape = RoundedCornerShape(4.dp)
                  ) {
                    Text(selectedTemplate.badgeText, fontSize = 9.sp, fontWeight = FontWeight.Bold, color = Color(primaryColorHex), modifier = Modifier.padding(horizontal = 4.dp, vertical = 2.dp))
                  }
                }
                Spacer(modifier = Modifier.height(6.dp))
                Text("Vestido / Calçado de Demonstração", fontSize = 12.sp, color = ZapSlateDark)
                Row(
                  modifier = Modifier.fillMaxWidth().padding(top = 4.dp),
                  horizontalArrangement = Arrangement.SpaceBetween,
                  verticalAlignment = Alignment.CenterVertically
                ) {
                  Text("2.500 MT", fontWeight = FontWeight.ExtraBold, fontSize = 14.sp, color = Color(primaryColorHex))
                  Button(
                    onClick = {},
                    colors = ButtonDefaults.buttonColors(containerColor = Color(primaryColorHex)),
                    shape = RoundedCornerShape(6.dp),
                    contentPadding = androidx.compose.foundation.layout.PaddingValues(horizontal = 8.dp, vertical = 4.dp)
                  ) {
                    Text("Adicionar", fontSize = 10.sp)
                  }
                }
              }
            }
          }
        }
      }

      // Step 4: Add Initial Products (optional)
      4 -> {
        Text("Etapa 4 — Adicionar Primeiros Produtos", fontSize = 18.sp, fontWeight = FontWeight.Bold, color = ZapSlateDark)
        Text("Podes adicionar agora ou fazer mais tarde no teu painel.", fontSize = 12.sp, color = ZapSlateMuted)
        Spacer(modifier = Modifier.height(16.dp))

        Card(
          shape = RoundedCornerShape(14.dp),
          colors = CardDefaults.cardColors(containerColor = Color.White),
          elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
        ) {
          Column(modifier = Modifier.padding(16.dp)) {
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
              OutlinedTextField(
                value = tempProdName,
                onValueChange = { tempProdName = it },
                label = { Text("Nome da Peça") },
                placeholder = { Text("ex: Camisola Preta") },
                modifier = Modifier.weight(1.4f).testTag("wizard_prod_name_input")
              )
              OutlinedTextField(
                value = tempProdPrice,
                onValueChange = { tempProdPrice = it },
                label = { Text("Preço (MT)") },
                placeholder = { Text("1200") },
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                modifier = Modifier.weight(1f).testTag("wizard_prod_price_input")
              )
            }

            Spacer(modifier = Modifier.height(10.dp))

            Button(
              onClick = {
                val priceNum = tempProdPrice.toDoubleOrNull()
                if (tempProdName.isNotBlank() && priceNum != null) {
                  initialProducts.add(Pair(tempProdName.trim(), priceNum))
                  tempProdName = ""
                  tempProdPrice = ""
                }
              },
              colors = ButtonDefaults.buttonColors(containerColor = ZapEmerald),
              shape = RoundedCornerShape(8.dp),
              modifier = Modifier.fillMaxWidth().testTag("wizard_add_prod_btn")
            ) {
              Icon(Icons.Default.Add, contentDescription = null, modifier = Modifier.size(16.dp))
              Spacer(modifier = Modifier.width(6.dp))
              Text("Adicionar à Lista")
            }

            if (initialProducts.isNotEmpty()) {
              Spacer(modifier = Modifier.height(14.dp))
              Text("Produtos Adicionados (${initialProducts.size}):", fontSize = 12.sp, fontWeight = FontWeight.Bold, color = ZapSlateDark)
              Spacer(modifier = Modifier.height(6.dp))
              initialProducts.forEachIndexed { idx, (pName, pPrice) ->
                Row(
                  modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 4.dp),
                  horizontalArrangement = Arrangement.SpaceBetween,
                  verticalAlignment = Alignment.CenterVertically
                ) {
                  Column {
                    Text(pName, fontSize = 13.sp, fontWeight = FontWeight.SemiBold, color = ZapSlateDark)
                    Text(MozFormatters.formatMt(pPrice), fontSize = 12.sp, color = ZapEmerald)
                  }
                  IconButton(onClick = { initialProducts.removeAt(idx) }) {
                    Icon(Icons.Default.Delete, contentDescription = "Remover", tint = Color.Gray, modifier = Modifier.size(18.dp))
                  }
                }
              }
            }
          }
        }
      }

      // Step 5: Summary & Publish
      5 -> {
        Text("Etapa 5 — Resumo e Publicação", fontSize = 18.sp, fontWeight = FontWeight.Bold, color = ZapSlateDark)
        Text("A tua loja está pronta para ser publicada no WhatsApp!", fontSize = 12.sp, color = ZapSlateMuted)
        Spacer(modifier = Modifier.height(16.dp))

        Card(
          shape = RoundedCornerShape(14.dp),
          colors = CardDefaults.cardColors(containerColor = Color.White),
          elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
        ) {
          Column(modifier = Modifier.padding(18.dp)) {
            Row(verticalAlignment = Alignment.CenterVertically) {
              Box(
                modifier = Modifier
                  .size(44.dp)
                  .clip(RoundedCornerShape(12.dp))
                  .background(Color(primaryColorHex)),
                contentAlignment = Alignment.Center
              ) {
                Icon(Icons.Default.Storefront, contentDescription = null, tint = Color.White, modifier = Modifier.size(24.dp))
              }
              Spacer(modifier = Modifier.width(12.dp))
              Column {
                Text(storeName.ifBlank { "Minha Loja Digital" }, fontWeight = FontWeight.Bold, fontSize = 16.sp, color = ZapSlateDark)
                Text(businessCategory, fontSize = 12.sp, color = ZapSlateMuted)
              }
            }

            Spacer(modifier = Modifier.height(14.dp))
            HorizontalDivider()
            Spacer(modifier = Modifier.height(14.dp))

            Row(verticalAlignment = Alignment.CenterVertically) {
              Icon(Icons.Default.Link, contentDescription = null, tint = ZapEmerald, modifier = Modifier.size(18.dp))
              Spacer(modifier = Modifier.width(8.dp))
              Text("zapcatalogo.mz/loja/${storeSlug.ifBlank { "minha-loja" }}", fontWeight = FontWeight.Bold, fontSize = 13.sp, color = ZapEmerald)
            }

            Spacer(modifier = Modifier.height(8.dp))

            Row(verticalAlignment = Alignment.CenterVertically) {
              Icon(Icons.Default.Phone, contentDescription = null, tint = ZapSlateMuted, modifier = Modifier.size(18.dp))
              Spacer(modifier = Modifier.width(8.dp))
              Text("WhatsApp: $whatsappNumber", fontSize = 13.sp, color = ZapSlateDark)
            }

            Spacer(modifier = Modifier.height(8.dp))

            Row(verticalAlignment = Alignment.CenterVertically) {
              Icon(Icons.Default.LocationOn, contentDescription = null, tint = ZapSlateMuted, modifier = Modifier.size(18.dp))
              Spacer(modifier = Modifier.width(8.dp))
              Text("Localização: $city", fontSize = 13.sp, color = ZapSlateDark)
            }

            Spacer(modifier = Modifier.height(16.dp))

            Surface(
              shape = RoundedCornerShape(8.dp),
              color = ZapEmeraldLight,
              modifier = Modifier.fillMaxWidth()
            ) {
              Text(
                text = "🎉 O teu catálogo ficará imediatamente disponível com link público e carrinho integrado no WhatsApp!",
                fontSize = 12.sp,
                color = ZapEmeraldDark,
                modifier = Modifier.padding(12.dp),
                lineHeight = 16.sp
              )
            }
          }
        }
      }
    }

    Spacer(modifier = Modifier.height(24.dp))

    // Next / Previous Buttons
    Row(
      modifier = Modifier.fillMaxWidth(),
      horizontalArrangement = Arrangement.spacedBy(10.dp)
    ) {
      if (currentStep > 1) {
        OutlinedButton(
          onClick = { currentStep-- },
          modifier = Modifier.weight(1f).height(48.dp)
        ) {
          Text("Anterior", color = ZapSlateDark)
        }
      }

      Button(
        onClick = {
          if (currentStep < 5) {
            if (currentStep == 1 && storeName.isBlank()) {
              viewModel.showMessage("Por favor insere o nome da tua loja.")
              return@Button
            }
            currentStep++
          } else {
            // Finish & Publish
            viewModel.finishWizard(
              name = storeName,
              slug = storeSlug,
              businessCategory = businessCategory,
              whatsappNumber = whatsappNumber,
              city = city,
              visualTemplate = selectedTemplate.id,
              primaryColor = primaryColorHex,
              secondaryColor = secondaryColorHex,
              initialProducts = initialProducts.toList()
            )
          }
        },
        modifier = Modifier
          .weight(1f)
          .height(48.dp)
          .testTag("wizard_next_button"),
        colors = ButtonDefaults.buttonColors(containerColor = ZapEmerald),
        shape = RoundedCornerShape(10.dp)
      ) {
        Text(
          text = if (currentStep == 5) "Publicar Catálogo" else "Continuar",
          fontWeight = FontWeight.Bold,
          fontSize = 14.sp
        )
        if (currentStep < 5) {
          Spacer(modifier = Modifier.width(4.dp))
          Icon(Icons.Default.ArrowForward, contentDescription = null, modifier = Modifier.size(16.dp))
        }
      }
    }
  }
}
