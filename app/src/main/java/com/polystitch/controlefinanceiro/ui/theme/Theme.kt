package com.polystitch.controlefinanceiro.ui.theme

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Typography
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

enum class AppTheme {
    AZUL,
    ROXO,
    VERDE,
    ESCURSO,     // Dark / Midnight Professional
    AMBAR,       // Warm Amber & Gold
    RUBI,        // Vibrant Ruby Red
    OCEANO,      // Deep Ocean Turquoise
    FLORES       // Rose & Magenta
}

private val AzulColorScheme = lightColorScheme(
    primary = Color(0xFF0F172A),
    onPrimary = Color.White,
    primaryContainer = Color(0xFFDBEAFE),
    onPrimaryContainer = Color(0xFF1E3A8A),
    secondary = Color(0xFF3B82F6),
    background = Color(0xFFFFFFFF),
    surface = Color(0xFFF1F5F9),
    onBackground = Color(0xFF1E293B),
    onSurface = Color(0xFF1E293B),
)

private val RoxoColorScheme = lightColorScheme(
    primary = Color(0xFF4C1D95),
    onPrimary = Color.White,
    primaryContainer = Color(0xFFEDE9FE),
    onPrimaryContainer = Color(0xFF2E1065),
    secondary = Color(0xFF7C3AED),
    background = Color(0xFFFFFFFF),
    surface = Color(0xFFF5F3FF),
    onBackground = Color(0xFF2E1065),
    onSurface = Color(0xFF2E1065),
)

private val VerdeColorScheme = lightColorScheme(
    primary = Color(0xFF065F46),
    onPrimary = Color.White,
    primaryContainer = Color(0xFFD1FAE5),
    onPrimaryContainer = Color(0xFF064E3B),
    secondary = Color(0xFF059669),
    background = Color(0xFFFFFFFF),
    surface = Color(0xFFECFDF5),
    onBackground = Color(0xFF064E3B),
    onSurface = Color(0xFF064E3B),
)

private val EscuroColorScheme = darkColorScheme(
    primary = Color(0xFF1E1B4B),      // Mais escuro na esquerda do gradiente
    onPrimary = Color.White,
    primaryContainer = Color(0xFF1E1B4B),
    onPrimaryContainer = Color(0xFFE0E7FF),
    secondary = Color(0xFF818CF8),    // Mais claro e vibrante na direita do gradiente
    background = Color(0xFF090A0F),
    surface = Color(0xFF141721),
    onBackground = Color(0xFFF1F5F9),
    onSurface = Color(0xFFF1F5F9),
    onSurfaceVariant = Color(0xFF94A3B8)
)

private val AmbarColorScheme = lightColorScheme(
    primary = Color(0xFF78350F),
    onPrimary = Color.White,
    primaryContainer = Color(0xFFFEF3C7),
    onPrimaryContainer = Color(0xFF451A03),
    secondary = Color(0xFFD97706),
    background = Color(0xFFFFFFFF),
    surface = Color(0xFFFFFBEB),
    onBackground = Color(0xFF451A03),
    onSurface = Color(0xFF451A03),
)

private val RubiColorScheme = lightColorScheme(
    primary = Color(0xFF831843),
    onPrimary = Color.White,
    primaryContainer = Color(0xFFFCE7F3),
    onPrimaryContainer = Color(0xFF500724),
    secondary = Color(0xFFDB2777),
    background = Color(0xFFFFFFFF),
    surface = Color(0xFFFFF1F2),
    onBackground = Color(0xFF500724),
    onSurface = Color(0xFF500724),
)

private val OceanoColorScheme = lightColorScheme(
    primary = Color(0xFF134E4A),
    onPrimary = Color.White,
    primaryContainer = Color(0xFFCCFBF1),
    onPrimaryContainer = Color(0xFF042F2E),
    secondary = Color(0xFF0D9488),
    background = Color(0xFFFFFFFF),
    surface = Color(0xFFF0FDFA),
    onBackground = Color(0xFF042F2E),
    onSurface = Color(0xFF042F2E),
)

private val FloresColorScheme = lightColorScheme(
    primary = Color(0xFF881337),
    onPrimary = Color.White,
    primaryContainer = Color(0xFFFFE4E6),
    onPrimaryContainer = Color(0xFF4C0519),
    secondary = Color(0xFFF43F5E),
    background = Color(0xFFFFFFFF),
    surface = Color(0xFFFFF1F2),
    onBackground = Color(0xFF4C0519),
    onSurface = Color(0xFF4C0519),
)

@Composable
fun ControleFinanceiroTheme(
    appTheme: AppTheme = AppTheme.AZUL,
    content: @Composable () -> Unit
) {
    val colorScheme = when (appTheme) {
        AppTheme.AZUL -> AzulColorScheme
        AppTheme.ROXO -> RoxoColorScheme
        AppTheme.VERDE -> VerdeColorScheme
        AppTheme.ESCURSO -> EscuroColorScheme
        AppTheme.AMBAR -> AmbarColorScheme
        AppTheme.RUBI -> RubiColorScheme
        AppTheme.OCEANO -> OceanoColorScheme
        AppTheme.FLORES -> FloresColorScheme
    }

    MaterialTheme(
        colorScheme = colorScheme,
        typography = Typography(),
        content = content
    )
}