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
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Email
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.ShoppingBag
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRow
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
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
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.theme.ZapEmerald
import com.example.ui.theme.ZapEmeraldLight
import com.example.ui.theme.ZapSlateDark
import com.example.ui.theme.ZapSlateMuted
import com.example.ui.theme.ZapViolet
import com.example.ui.viewmodel.ZapViewModel

@Composable
fun AuthScreen(
  viewModel: ZapViewModel,
  onBack: () -> Unit
) {
  var selectedTab by remember { mutableIntStateOf(0) } // 0: Login, 1: Register
  var name by remember { mutableStateOf("") }
  var email by remember { mutableStateOf("") }
  var password by remember { mutableStateOf("") }
  var role by remember { mutableStateOf("MERCHANT") }

  Column(
    modifier = Modifier
      .fillMaxSize()
      .background(MaterialTheme.colorScheme.background)
      .verticalScroll(rememberScrollState())
      .padding(20.dp)
  ) {
    // Top Bar Back
    Row(
      modifier = Modifier.fillMaxWidth(),
      verticalAlignment = Alignment.CenterVertically
    ) {
      IconButton(onClick = onBack, modifier = Modifier.testTag("auth_back_button")) {
        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Voltar", tint = ZapSlateDark)
      }
      Spacer(modifier = Modifier.width(8.dp))
      Text(
        text = if (selectedTab == 0) "Aceder à Plataforma" else "Criar Conta de Comerciante",
        fontWeight = FontWeight.Bold,
        fontSize = 18.sp,
        color = ZapSlateDark
      )
    }

    Spacer(modifier = Modifier.height(16.dp))

    // Header Branding
    Column(
      modifier = Modifier.fillMaxWidth(),
      horizontalAlignment = Alignment.CenterHorizontally
    ) {
      Box(
        modifier = Modifier
          .size(54.dp)
          .clip(RoundedCornerShape(16.dp))
          .background(
            Brush.linearGradient(listOf(ZapEmerald, ZapViolet))
          ),
        contentAlignment = Alignment.Center
      ) {
        Icon(Icons.Default.ShoppingBag, contentDescription = null, tint = Color.White, modifier = Modifier.size(30.dp))
      }
      Spacer(modifier = Modifier.height(10.dp))
      Text(
        text = "ZapCatálogo Moçambique",
        fontWeight = FontWeight.Bold,
        fontSize = 20.sp,
        color = ZapSlateDark
      )
      Text(
        text = "Cria a tua loja e recebe pedidos no WhatsApp",
        fontSize = 12.sp,
        color = ZapSlateMuted
      )
    }

    Spacer(modifier = Modifier.height(24.dp))

    // Tab Switcher
    TabRow(
      selectedTabIndex = selectedTab,
      containerColor = Color.White,
      contentColor = ZapEmerald,
      modifier = Modifier.clip(RoundedCornerShape(10.dp))
    ) {
      Tab(
        selected = selectedTab == 0,
        onClick = { selectedTab = 0 },
        text = { Text("Iniciar Sessão", fontWeight = FontWeight.Bold) },
        modifier = Modifier.testTag("tab_login")
      )
      Tab(
        selected = selectedTab == 1,
        onClick = { selectedTab = 1 },
        text = { Text("Registar-se", fontWeight = FontWeight.Bold) },
        modifier = Modifier.testTag("tab_register")
      )
    }

    Spacer(modifier = Modifier.height(20.dp))

    Card(
      shape = RoundedCornerShape(16.dp),
      colors = CardDefaults.cardColors(containerColor = Color.White),
      elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
      Column(modifier = Modifier.padding(20.dp)) {
        if (selectedTab == 1) {
          OutlinedTextField(
            value = name,
            onValueChange = { name = it },
            label = { Text("Nome Completo ou Nome do Negócio") },
            leadingIcon = { Icon(Icons.Default.Person, contentDescription = null) },
            singleLine = true,
            modifier = Modifier
              .fillMaxWidth()
              .testTag("auth_name_input")
          )
          Spacer(modifier = Modifier.height(14.dp))
        }

        OutlinedTextField(
          value = email,
          onValueChange = { email = it },
          label = { Text("Email de Contacto") },
          leadingIcon = { Icon(Icons.Default.Email, contentDescription = null) },
          keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email),
          singleLine = true,
          modifier = Modifier
            .fillMaxWidth()
            .testTag("auth_email_input")
        )

        Spacer(modifier = Modifier.height(14.dp))

        OutlinedTextField(
          value = password,
          onValueChange = { password = it },
          label = { Text("Palavra-passe") },
          leadingIcon = { Icon(Icons.Default.Lock, contentDescription = null) },
          visualTransformation = PasswordVisualTransformation(),
          keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
          singleLine = true,
          modifier = Modifier
            .fillMaxWidth()
            .testTag("auth_password_input")
        )

        Spacer(modifier = Modifier.height(20.dp))

        Button(
          onClick = {
            if (selectedTab == 0) {
              if (email.isNotBlank()) {
                viewModel.login(email)
              } else {
                viewModel.showMessage("Por favor introduza o seu email.")
              }
            } else {
              if (email.isNotBlank()) {
                viewModel.register(name.ifBlank { "Comerciante" }, email, role)
              } else {
                viewModel.showMessage("Por favor preencha os campos obrigatórios.")
              }
            }
          },
          modifier = Modifier
            .fillMaxWidth()
            .height(48.dp)
            .testTag("auth_submit_button"),
          colors = ButtonDefaults.buttonColors(containerColor = ZapEmerald),
          shape = RoundedCornerShape(10.dp)
        ) {
          Text(
            text = if (selectedTab == 0) "Entrar no Painel" else "Criar a Minha Loja Grátis",
            fontWeight = FontWeight.Bold,
            fontSize = 14.sp
          )
        }
      }
    }

    Spacer(modifier = Modifier.height(24.dp))

    // Quick Test Demo Accounts Section
    Text(
      text = "Acessos Rápidos de Demonstração:",
      fontWeight = FontWeight.Bold,
      fontSize = 13.sp,
      color = ZapSlateMuted
    )
    Spacer(modifier = Modifier.height(8.dp))

    OutlinedButton(
      onClick = { viewModel.login("marta@modaelegante.mz") },
      modifier = Modifier
        .fillMaxWidth()
        .testTag("quick_login_merchant"),
      shape = RoundedCornerShape(10.dp)
    ) {
      Column(horizontalAlignment = Alignment.Start, modifier = Modifier.fillMaxWidth()) {
        Text("Entrar como Comerciante: Marta Cossa", fontWeight = FontWeight.Bold, fontSize = 13.sp, color = ZapSlateDark)
        Text("Gere 'Moda Elegante' e 'Urban Shoes MZ' (Maputo)", fontSize = 11.sp, color = ZapEmerald)
      }
    }

    Spacer(modifier = Modifier.height(8.dp))

    OutlinedButton(
      onClick = { viewModel.login("admin@zapcatalogo.mz") },
      modifier = Modifier
        .fillMaxWidth()
        .testTag("quick_login_admin"),
      shape = RoundedCornerShape(10.dp)
    ) {
      Column(horizontalAlignment = Alignment.Start, modifier = Modifier.fillMaxWidth()) {
        Text("Entrar como Administrador Geral", fontWeight = FontWeight.Bold, fontSize = 13.sp, color = ZapSlateDark)
        Text("Gerir lojas, estatísticas globais e ativação de planos", fontSize = 11.sp, color = ZapViolet)
      }
    }
  }
}
