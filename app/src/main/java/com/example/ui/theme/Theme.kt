package com.example.ui.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.ui.graphics.Color
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Shapes
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.ui.unit.dp

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
  surface = StoneLight,
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

private val PremiumColorScheme = darkColorScheme(
  primary = PremiumAction,
  onPrimary = Color(0xFF3D0020),
  primaryContainer = Color(0xFFFFB3C1),
  onPrimaryContainer = Color(0xFF3D0020),
  secondary = PremiumAttention,
  onSecondary = Color(0xFF3D2E00),
  secondaryContainer = Color(0xFFFFF3C4),
  onSecondaryContainer = Color(0xFF3D2E00),
  tertiary = PremiumDonation,
  onTertiary = Color(0xFF003020),
  tertiaryContainer = Color(0xFFD4F5D4),
  onTertiaryContainer = Color(0xFF003020),
  error = PremiumUrgency,
  onError = Color(0xFF4A0010),
  errorContainer = Color(0xFFFFD4D4),
  onErrorContainer = Color(0xFF4A0010),
  background = PremiumBackground,
  onBackground = PremiumTextPrimary,
  surface = PremiumSurface,
  onSurface = PremiumTextPrimary,
  surfaceVariant = PremiumSurfaceVariant,
  onSurfaceVariant = PremiumTextSecondary,
  outline = PremiumBorder,
  outlineVariant = Color(0xFF5A2A3A),
  inverseSurface = Color(0xFFFFF0F0),
  inverseOnSurface = Color(0xFF1A0F14),
  inversePrimary = Color(0xFFFF6B81),
  surfaceTint = PremiumAction
)

private val AppShapes = Shapes(
  extraSmall = RoundedCornerShape(10.dp),
  small = RoundedCornerShape(14.dp),
  medium = RoundedCornerShape(20.dp),
  large = RoundedCornerShape(28.dp),
  extraLarge = RoundedCornerShape(34.dp)
)

@Composable
fun MyApplicationTheme(
  darkTheme: Boolean = isSystemInDarkTheme(),
  userTheme: String = "SYSTEM",
  content: @Composable () -> Unit,
) {
  val colorScheme = when (userTheme) {
    "ASTRAL" -> PremiumColorScheme
    else -> if (darkTheme) DarkColorScheme else LightColorScheme
  }
  MaterialTheme(colorScheme = colorScheme, typography = Typography, shapes = AppShapes, content = content)
}
