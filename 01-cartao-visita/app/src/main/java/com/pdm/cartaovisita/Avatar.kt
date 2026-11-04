package com.pdm.cartaovisita

import androidx.compose.foundation.Image
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
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

/**
 * Avatar circular do cartao.
 *
 * Quando [foto] e informada, exibe a fotografia recortada em circulo. Sem
 * foto, desenha as iniciais do nome sobre um degrade — assim o cartao
 * continua apresentavel mesmo sem nenhuma imagem.
 */
@Composable
fun Avatar(
    nome: String,
    modifier: Modifier = Modifier,
    tamanho: Dp = 120.dp,
    foto: Int? = null
) {
    val forma = modifier
        .size(tamanho)
        .clip(CircleShape)
        .border(3.dp, MaterialTheme.colorScheme.surface, CircleShape)

    if (foto != null) {
        Image(
            painter = painterResource(foto),
            contentDescription = stringResource(R.string.foto_de, nome),
            // Crop preenche o circulo inteiro sem distorcer a fotografia.
            contentScale = ContentScale.Crop,
            modifier = forma
        )
    } else {
        IniciaisDoNome(nome = nome, tamanho = tamanho, modifier = forma)
    }
}

/** Avatar alternativo: as iniciais sobre um degrade das cores do tema. */
@Composable
private fun IniciaisDoNome(nome: String, tamanho: Dp, modifier: Modifier) {
    val degrade = Brush.linearGradient(
        listOf(
            MaterialTheme.colorScheme.primary,
            MaterialTheme.colorScheme.secondary
        )
    )

    Box(
        modifier = modifier.background(degrade),
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
