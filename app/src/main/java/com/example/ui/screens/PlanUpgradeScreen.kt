package com.example.ui.screens

import androidx.compose.foundation.background
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
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.model.PlanType
import com.example.ui.theme.ZapEmerald
import com.example.ui.theme.ZapEmeraldLight
import com.example.ui.theme.ZapSlateDark
import com.example.ui.theme.ZapSlateMuted
import com.example.ui.theme.ZapViolet
import com.example.ui.viewmodel.AppScreen
import com.example.ui.viewmodel.ZapViewModel

@Composable
fun PlanUpgradeScreen(
  viewModel: ZapViewModel,
  onBack: () -> Unit
) {
  val activeStore by viewModel.activeStore.collectAsState()

  Column(
    modifier = Modifier
      .fillMaxSize()
      .background(MaterialTheme.colorScheme.background)
      .verticalScroll(rememberScrollState())
      .padding(20.dp)
  ) {
    // Top Bar
    Row(verticalAlignment = Alignment.CenterVertically) {
      IconButton(onClick = onBack, modifier = Modifier.testTag("upgrade_back_btn")) {
        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Voltar", tint = ZapSlateDark)
      }
      Spacer(modifier = Modifier.width(6.dp))
      Text("Subscrição do Plano Pro", fontWeight = FontWeight.Bold, fontSize = 18.sp, color = ZapSlateDark)
    }

    Spacer(modifier = Modifier.height(16.dp))

    // Header Card
    Card(
      shape = RoundedCornerShape(16.dp),
      colors = CardDefaults.cardColors(containerColor = Color.White),
      border = androidx.compose.foundation.BorderStroke(2.dp, ZapEmerald),
      elevation = CardDefaults.cardElevation(defaultElevation = 3.dp)
    ) {
      Column(modifier = Modifier.padding(20.dp)) {
        Row(
          modifier = Modifier.fillMaxWidth(),
          horizontalArrangement = Arrangement.SpaceBetween,
          verticalAlignment = Alignment.CenterVertically
        ) {
          Column {
            Row(verticalAlignment = Alignment.CenterVertically) {
              Icon(Icons.Default.Star, contentDescription = null, tint = ZapEmerald, modifier = Modifier.size(22.dp))
              Spacer(modifier = Modifier.width(6.dp))
              Text("Plano Pro", fontWeight = FontWeight.ExtraBold, fontSize = 20.sp, color = ZapSlateDark)
            }
            Text("Potencializa o teu catálogo em Moçambique", fontSize = 12.sp, color = ZapSlateMuted)
          }

          Column(horizontalAlignment = Alignment.End) {
            Text("299 MT", fontWeight = FontWeight.ExtraBold, fontSize = 24.sp, color = ZapEmerald)
            Text("por mês", fontSize = 11.sp, color = ZapSlateMuted)
          }
        }

        HorizontalDivider(modifier = Modifier.padding(vertical = 14.dp))

        PlanType.PRO.features.forEach { feat ->
          Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.padding(vertical = 4.dp)
          ) {
            Icon(Icons.Default.Check, contentDescription = null, tint = ZapEmerald, modifier = Modifier.size(16.dp))
            Spacer(modifier = Modifier.width(8.dp))
            Text(feat, fontSize = 13.sp, color = ZapSlateDark)
          }
        }

        Spacer(modifier = Modifier.height(20.dp))

        // Activation details for Mozambique
        Surface(
          shape = RoundedCornerShape(10.dp),
          color = Color(0xFFF1F5F9),
          modifier = Modifier.fillMaxWidth()
        ) {
          Column(modifier = Modifier.padding(14.dp)) {
            Text("Formas de Pagamento em Moçambique:", fontWeight = FontWeight.Bold, fontSize = 12.sp, color = ZapSlateDark)
            Spacer(modifier = Modifier.height(4.dp))
            Text("• M-Pesa: 84 123 4567 (ZapCatálogo Lda)", fontSize = 11.sp, color = ZapSlateMuted)
            Text("• E-Mola: 86 123 4567", fontSize = 11.sp, color = ZapSlateMuted)
            Text("• Ativação manual e instantânea disponível no painel", fontSize = 11.sp, color = ZapSlateMuted)
          }
        }

        Spacer(modifier = Modifier.height(18.dp))

        Button(
          onClick = {
            activeStore?.let {
              viewModel.updatePlan(it.id, "PRO")
              viewModel.navigateTo(AppScreen.Dashboard)
            }
          },
          modifier = Modifier
            .fillMaxWidth()
            .height(48.dp)
            .testTag("confirm_upgrade_btn"),
          colors = ButtonDefaults.buttonColors(containerColor = ZapEmerald),
          shape = RoundedCornerShape(10.dp)
        ) {
          Text("Ativar Plano Pro Agora", fontWeight = FontWeight.Bold, fontSize = 14.sp)
        }
      }
    }

    Spacer(modifier = Modifier.height(14.dp))

    OutlinedButton(
      onClick = onBack,
      modifier = Modifier.fillMaxWidth(),
      shape = RoundedCornerShape(10.dp)
    ) {
      Text("Continuar no Plano Gratuito", color = ZapSlateDark)
    }
  }
}
