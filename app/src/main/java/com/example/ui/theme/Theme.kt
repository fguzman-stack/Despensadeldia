package com.example.ui.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.ui.graphics.Color
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable

private val LightColorScheme = lightColorScheme(
  primary = EmeraldDark,
  onPrimary = Color.White,
  primaryContainer = GreenContainer,
  onPrimaryContainer = Color(0xFF0A3D2A),
  secondary = AmberDark,
  onSecondary = Color.White,
  secondaryContainer = AmberContainer,
  onSecondaryContainer = Color(0xFF5C3D00),
  tertiary = Color(0xFF7A5DC0),
  onTertiary = Color.White,
  tertiaryContainer = SkyContainer,
  onTertiaryContainer = Color(0xFF3D2E6E),
  error = CoralDark,
  onError = Color.White,
  errorContainer = CoralContainer,
  onErrorContainer = Color(0xFF8B2D1C),
  background = Cream,
  onBackground = TextPrimary,
  surface = Color.White,
  onSurface = TextPrimary,
  surfaceVariant = Stone,
  onSurfaceVariant = TextSecondary,
  outline = BorderLight,
  outlineVariant = Color(0xFFE8E5DE),
  inverseSurface = DarkSurface,
  inverseOnSurface = TextOnDark,
  inversePrimary = EmeraldLight,
  surfaceTint = EmeraldDark
)

private val DarkColorScheme = darkColorScheme(
  primary = EmeraldLight,
  onPrimary = Color(0xFF052E16),
  primaryContainer = EmeraldDark,
  onPrimaryContainer = GreenContainer,
  secondary = Amber,
  onSecondary = Color(0xFF3B2200),
  secondaryContainer = AmberDark,
  onSecondaryContainer = AmberContainer,
  tertiary = Sky,
  onTertiary = Color(0xFF3D2E6E),
  tertiaryContainer = Color(0xFF5B4A9E),
  onTertiaryContainer = SkyContainer,
  error = Coral,
  onError = Color(0xFF410002),
  errorContainer = CoralDark,
  onErrorContainer = CoralContainer,
  background = DarkBackground,
  onBackground = TextOnDark,
  surface = DarkSurface,
  onSurface = TextOnDark,
  surfaceVariant = DarkSurfaceVariant,
  onSurfaceVariant = TextOnDarkSecondary,
  outline = BorderDark,
  outlineVariant = BorderMedium,
  inverseSurface = Stone,
  inverseOnSurface = TextPrimary,
  inversePrimary = Emerald,
  surfaceTint = EmeraldLight
)

private val AstralColorScheme = darkColorScheme(
  primary = AstralAction,
  onPrimary = Color(0xFF00382A),
  primaryContainer = Color(0xFF005139),
  onPrimaryContainer = Color(0xFFB2F2DA),
  secondary = AstralAttention,
  onSecondary = Color(0xFF3D2E00),
  secondaryContainer = Color(0xFF5C4000),
  onSecondaryContainer = Color(0xFFFFE1A0),
  tertiary = AstralDonation,
  onTertiary = Color(0xFF311A5E),
  tertiaryContainer = Color(0xFF4A3077),
  onTertiaryContainer = Color(0xFFE8D6FF),
  error = AstralUrgency,
  onError = Color(0xFF410002),
  errorContainer = Color(0xFF6E1818),
  onErrorContainer = Color(0xFFFFDAD6),
  background = AstralBackground,
  onBackground = AstralTextPrimary,
  surface = AstralSurface,
  onSurface = AstralTextPrimary,
  surfaceVariant = AstralSurfaceVariant,
  onSurfaceVariant = AstralTextSecondary,
  outline = AstralBorder,
  outlineVariant = Color(0xFF414B7A),
  inverseSurface = Color(0xFFF1F3FF),
  inverseOnSurface = Color(0xFF080A18),
  inversePrimary = Color(0xFF005139),
  surfaceTint = AstralAccent
)

@Composable
fun MyApplicationTheme(
  darkTheme: Boolean = isSystemInDarkTheme(),
  userTheme: String = "SYSTEM",
  content: @Composable () -> Unit,
) {
  val colorScheme = when (userTheme) {
    "ASTRAL" -> AstralColorScheme
    else -> if (darkTheme) DarkColorScheme else LightColorScheme
  }
  MaterialTheme(colorScheme = colorScheme, typography = Typography, content = content)
}
