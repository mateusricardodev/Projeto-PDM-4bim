package com.pdm.cartaovisita

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

/**
 * Avatar circular com as iniciais do nome.
 *
 * E desenhado pelo proprio Compose, com um degrade de fundo, em vez de usar
 * uma imagem: o aplicativo nao depende de nenhum arquivo externo e o avatar
 * acompanha o nome configurado em [PERFIL_PADRAO].
 */
@Composable
fun Avatar(
    nome: String,
    modifier: Modifier = Modifier,
    tamanho: Dp = 120.dp
) {
    val degrade = Brush.linearGradient(
        listOf(
            MaterialTheme.colorScheme.primary,
            MaterialTheme.colorScheme.secondary
        )
    )

    Box(
        modifier = modifier
            .size(tamanho)
            .clip(CircleShape)
            .background(degrade)
            .border(3.dp, MaterialTheme.colorScheme.surface, CircleShape),
        contentAlignment = Alignment.Center
    ) {
        Text(
            // As iniciais ocupam pouco mais de um terco do diametro.
            text = iniciais(nome),
            fontSize = (tamanho.value * 0.34f).sp,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.onPrimary
        )
    }
}
