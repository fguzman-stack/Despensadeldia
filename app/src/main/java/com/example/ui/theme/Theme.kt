package com.example.ui.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.ui.graphics.Color
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable

private val LightColorScheme = lightColorScheme(
  primary = Emerald,
  onPrimary = TextOnDark,
  primaryContainer = GreenContainer,
  onPrimaryContainer = EmeraldDark,
  secondary = Amber,
  onSecondary = TextOnDark,
  secondaryContainer = AmberContainer,
  onSecondaryContainer = AmberDark,
  tertiary = Sky,
  onTertiary = TextOnDark,
  tertiaryContainer = SkyContainer,
  onTertiaryContainer = Color(0xFF075985),
  error = Coral,
  onError = TextOnDark,
  errorContainer = CoralContainer,
  onErrorContainer = CoralDark,
  background = Cream,
  onBackground = TextPrimary,
  surface = Color(0xFFFFFFFF),
  onSurface = TextPrimary,
  surfaceVariant = Stone,
  onSurfaceVariant = TextSecondary,
  outline = BorderLight,
  outlineVariant = BorderMedium,
  inverseSurface = DarkSurface,
  inverseOnSurface = TextOnDark,
  inversePrimary = EmeraldLight,
  surfaceTint = Emerald
)

private val DarkColorScheme = darkColorScheme(
  primary = EmeraldLight,
  onPrimary = Color(0xFF052E16),
  primaryContainer = EmeraldDark,
  onPrimaryContainer = GreenContainer,
  secondary = AmberLight,
  onSecondary = Color(0xFF3B2200),
  secondaryContainer = AmberDark,
  onSecondaryContainer = AmberContainer,
  tertiary = SkyLight,
  onTertiary = Color(0xFF00324E),
  tertiaryContainer = Color(0xFF064E6F),
  onTertiaryContainer = SkyContainer,
  error = CoralLight,
  onError = Color(0xFF410002),
  errorContainer = CoralDark,
  onErrorContainer = CoralContainer,
  background = DarkBackground,
  onBackground = TextOnDark,
  surface = DarkSurface,
  onSurface = TextOnDark,
  surfaceVariant = DarkSurfaceVariant,
  onSurfaceVariant = Color(0xFFA8A29E),
  outline = BorderDark,
  outlineVariant = Color(0xFF57534E),
  inverseSurface = Stone,
  inverseOnSurface = TextPrimary,
  inversePrimary = Emerald,
  surfaceTint = EmeraldLight
)

@Composable
fun MyApplicationTheme(
  darkTheme: Boolean = isSystemInDarkTheme(),
  content: @Composable () -> Unit,
) {
  val colorScheme = if (darkTheme) DarkColorScheme else LightColorScheme
  MaterialTheme(colorScheme = colorScheme, typography = Typography, content = content)
}
