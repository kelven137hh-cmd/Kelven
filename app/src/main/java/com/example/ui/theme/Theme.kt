package com.example.ui.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

private val LightColorScheme = lightColorScheme(
  primary = ZapEmerald,
  onPrimary = Color.White,
  primaryContainer = ZapEmeraldLight,
  onPrimaryContainer = ZapEmeraldDark,
  secondary = ZapViolet,
  onSecondary = Color.White,
  secondaryContainer = ZapVioletLight,
  onSecondaryContainer = ZapViolet,
  tertiary = ZapAmber,
  background = Color(0xFFF8FAFC),
  onBackground = ZapSlateDark,
  surface = Color.White,
  onSurface = ZapSlateDark,
  surfaceVariant = Color(0xFFF1F5F9),
  onSurfaceVariant = ZapSlateMuted,
  outline = ZapSlateBorder
)

private val DarkColorScheme = darkColorScheme(
  primary = ZapEmerald,
  onPrimary = Color.White,
  secondary = ZapViolet,
  background = Color(0xFF0F172A),
  onBackground = Color(0xFFF8FAFC),
  surface = Color(0xFF1E293B),
  onSurface = Color(0xFFF8FAFC),
  surfaceVariant = Color(0xFF334155),
  onSurfaceVariant = Color(0xFF94A3B8),
  outline = Color(0xFF475569)
)

@Composable
fun MyApplicationTheme(
  darkTheme: Boolean = isSystemInDarkTheme(),
  dynamicColor: Boolean = false, // Keep consistent branding colors
  content: @Composable () -> Unit
) {
  val colorScheme = if (darkTheme) DarkColorScheme else LightColorScheme

  MaterialTheme(
    colorScheme = colorScheme,
    typography = Typography,
    content = content
  )
}
