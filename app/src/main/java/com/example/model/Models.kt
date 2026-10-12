package com.example.model

import com.example.data.local.model.ProductEntity
import java.text.NumberFormat
import java.util.Locale

enum class VisualTemplate(
  val id: String,
  val displayName: String,
  val subtitle: String,
  val defaultPrimaryColor: Long,
  val defaultSecondaryColor: Long,
  val badgeText: String
) {
  FEMININE(
    id = "FEMININE",
    displayName = "Moda Feminina & Elegância",
    subtitle = "Design suave com tons rosê e esmeralda para vestidos e moda chic",
    defaultPrimaryColor = 0xFF059669,
    defaultSecondaryColor = 0xFFDB2777,
    badgeText = "Elegance & Chic"
  ),
  MASCULINE(
    id = "MASCULINE",
    displayName = "Moda Masculina Urbana",
    subtitle = "Linhas sóbrias, contrastes fortes para camisas, polos e alfaiataria",
    defaultPrimaryColor = 0xFF1E293B,
    defaultSecondaryColor = 0xFF0284C7,
    badgeText = "Urban Men"
  ),
  SHOES(
    id = "SHOES",
    displayName = "Calçado & Ténis",
    subtitle = "Cartões dinâmicos com ênfase em modelos e numerações de calçado",
    defaultPrimaryColor = 0xFFD97706,
    defaultSecondaryColor = 0xFF2563EB,
    badgeText = "Footwear Club"
  ),
  PERFUME(
    id = "PERFUME",
    displayName = "Perfumaria & Fragrâncias",
    subtitle = "Visual luxuoso com detalhes dourados e apresentação de volumes",
    defaultPrimaryColor = 0xFF7C3AED,
    defaultSecondaryColor = 0xFFD97706,
    badgeText = "Luxury Scent"
  ),
  ACCESSORIES(
    id = "ACCESSORIES",
    displayName = "Acessórios & Bijuteria",
    subtitle = "Minimalista e refinado para relógios, carteiras e joias finas",
    defaultPrimaryColor = 0xFF0D9488,
    defaultSecondaryColor = 0xFF475569,
    badgeText = "Premium Details"
  );

  companion object {
    fun fromId(id: String): VisualTemplate {
      return entries.find { it.id.equals(id, ignoreCase = true) } ?: FEMININE
    }
  }
}

enum class PlanType(
  val id: String,
  val title: String,
  val priceMonthlyMt: Int,
  val maxProducts: Int,
  val badgeLabel: String,
  val features: List<String>
) {
  FREE(
    id = "FREE",
    title = "Plano Gratuito",
    priceMonthlyMt = 0,
    maxProducts = 10,
    badgeLabel = "Gratuito",
    features = listOf(
      "1 Loja Digital exclusiva",
      "Até 10 Produtos no catálogo",
      "Link público personalizado",
      "Gestão de categorias",
      "Receção de encomendas no WhatsApp",
      "Personalização visual básica"
    )
  ),
  PRO(
    id = "PRO",
    title = "Plano Pro",
    priceMonthlyMt = 299,
    maxProducts = 100,
    badgeLabel = "Pro (299 MT/mês)",
    features = listOf(
      "1 Loja com suporte expandido",
      "Até 100 Produtos no catálogo",
      "Link exclusivo e sem limite de visitas",
      "Todos os modelos visuais avançados",
      "Estatísticas completas (Cliques e Vistas)",
      "Variações de tamanhos e cores ilimitadas",
      "Ativação de prioridade de apoio técnico"
    )
  );

  companion object {
    fun fromId(id: String): PlanType {
      return entries.find { it.id.equals(id, ignoreCase = true) } ?: FREE
    }
  }
}

data class CartItem(
  val product: ProductEntity,
  val quantity: Int = 1,
  val selectedSize: String = "",
  val selectedColor: String = ""
) {
  val subtotal: Double
    get() {
      val effectivePrice = product.promotionalPrice ?: product.price
      return effectivePrice * quantity
    }
}

data class StoreAnalytics(
  val views: Int = 0,
  val whatsappClicks: Int = 0,
  val shares: Int = 0,
  val productViews: Int = 0
)

object MozFormatters {
  fun formatMt(amount: Double): String {
    val integerPart = amount.toLong()
    val formatted = NumberFormat.getNumberInstance(Locale("pt", "MZ")).apply {
      isGroupingUsed = true
      maximumFractionDigits = 0
    }.format(integerPart)
    return "$formatted MT"
  }

  fun normalizeMozPhone(phone: String): String {
    val clean = phone.replace("[^0-9]".toRegex(), "")
    return if (clean.startsWith("258")) {
      clean
    } else if (clean.length == 9) {
      "258$clean"
    } else {
      clean
    }
  }

  fun formatPhoneDisplay(phone: String): String {
    val norm = normalizeMozPhone(phone)
    if (norm.length == 12 && norm.startsWith("258")) {
      val national = norm.substring(2)
      return "+258 ${national.substring(0, 2)} ${national.substring(2, 5)} ${national.substring(5)}"
    }
    return phone
  }
}
