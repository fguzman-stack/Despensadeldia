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

private val PremiumColorScheme = lightColorScheme(
  primary = PremiumAction,
  onPrimary = Color.White,
  primaryContainer = Color(0xFFFFC2D8),
  onPrimaryContainer = PremiumTextPrimary,
  secondary = PremiumAttention,
  onSecondary = Color(0xFF3B2B00),
  secondaryContainer = Color(0xFFFFECB0),
  onSecondaryContainer = Color(0xFF3B2B00),
  tertiary = PremiumDonation,
  onTertiary = Color.White,
  tertiaryContainer = Color(0xFFC8F7DC),
  onTertiaryContainer = Color(0xFF073B2E),
  error = PremiumUrgency,
  onError = Color.White,
  errorContainer = Color(0xFFFFD1D1),
  onErrorContainer = Color(0xFF5C1010),
  background = PremiumBackground,
  onBackground = PremiumTextPrimary,
  surface = PremiumSurface,
  onSurface = PremiumTextPrimary,
  surfaceVariant = PremiumSurfaceVariant,
  onSurfaceVariant = PremiumTextSecondary,
  outline = PremiumBorder,
  outlineVariant = Color(0xFFFFD7E5),
  inverseSurface = PremiumTextPrimary,
  inverseOnSurface = Color.White,
  inversePrimary = Color(0xFFFFA6C4),
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
