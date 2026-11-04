package com.pdm.cartaovisita

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Typography
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp

/**
 * Tema do aplicativo: cores e tipografia proprias, em Material 3.
 *
 * O tema nao segue o sistema: quem decide e o botao da tela, cujo estado e
 * passado em [escuro].
 */

private val EsquemaClaro = lightColorScheme(
    primary = Color(0xFF1B5E5A),
    onPrimary = Color.White,
    primaryContainer = Color(0xFFB8E6E1),
    onPrimaryContainer = Color(0xFF00201E),
    secondary = Color(0xFF9A6A2F),
    onSecondary = Color.White,
    background = Color(0xFFF2F5F4),
    onBackground = Color(0xFF171D1C),
    surface = Color.White,
    onSurface = Color(0xFF171D1C),
    surfaceVariant = Color(0xFFE4EAE8),
    onSurfaceVariant = Color(0xFF4A5250),
    outline = Color(0xFF7B8381)
)

private val EsquemaEscuro = darkColorScheme(
    primary = Color(0xFF7FD4CC),
    onPrimary = Color(0xFF003734),
    primaryContainer = Color(0xFF17504C),
    onPrimaryContainer = Color(0xFFB8E6E1),
    secondary = Color(0xFFE5BC84),
    onSecondary = Color(0xFF442B00),
    background = Color(0xFF0F1413),
    onBackground = Color(0xFFDEE4E2),
    surface = Color(0xFF1A2120),
    onSurface = Color(0xFFDEE4E2),
    surfaceVariant = Color(0xFF3C4442),
    onSurfaceVariant = Color(0xFFBCC5C3),
    outline = Color(0xFF879190)
)

/** Tipografia personalizada: titulos marcantes e corpo de texto legivel. */
private val TipografiaCartao = Typography(
    displaySmall = TextStyle(
        fontSize = 30.sp,
        lineHeight = 36.sp,
        fontWeight = FontWeight.Bold,
        letterSpacing = (-0.5).sp
    ),
    titleMedium = TextStyle(
        fontSize = 16.sp,
        lineHeight = 22.sp,
        fontWeight = FontWeight.Medium,
        letterSpacing = 1.sp
    ),
    bodyMedium = TextStyle(
        fontSize = 15.sp,
        lineHeight = 22.sp,
        fontWeight = FontWeight.Normal
    ),
    labelLarge = TextStyle(
        fontSize = 14.sp,
        lineHeight = 20.sp,
        fontWeight = FontWeight.Medium
    )
)

@Composable
fun CartaoVisitaTheme(escuro: Boolean, content: @Composable () -> Unit) {
    MaterialTheme(
        colorScheme = if (escuro) EsquemaEscuro else EsquemaClaro,
        typography = TipografiaCartao,
        content = content
    )
}
