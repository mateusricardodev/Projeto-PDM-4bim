package com.pdm.cartaovisita

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Email
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.Phone
import androidx.compose.material.icons.filled.Share
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp

/**
 * Tela do cartao de visita.
 *
 * O layout se adapta ao espaco disponivel: em telas estreitas (celular em pe)
 * o avatar fica acima das informacoes; em telas largas (tablet ou celular
 * deitado) os dois ficam lado a lado.
 */
@Composable
fun CartaoScreen(
    perfil: Perfil,
    temaEscuro: Boolean,
    onAlternarTema: (Boolean) -> Unit,
    modifier: Modifier = Modifier
) {
    BoxWithConstraints(modifier = modifier.fillMaxSize()) {
        val telaLarga = maxWidth >= 600.dp

        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .padding(20.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            SeletorDeTema(temaEscuro = temaEscuro, onAlternarTema = onAlternarTema)

            Spacer(Modifier.height(12.dp))

            Card(
                modifier = Modifier
                    .widthIn(max = 640.dp)
                    .fillMaxWidth(),
                colors = CardDefaults.cardColors(
                    containerColor = MaterialTheme.colorScheme.surface
                ),
                elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
            ) {
                if (telaLarga) {
                    // Tela larga: avatar a esquerda, informacoes a direita.
                    Row(
                        modifier = Modifier.padding(24.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Avatar(nome = perfil.nome, tamanho = 140.dp)
                        Spacer(Modifier.size(24.dp))
                        Column { Identificacao(perfil, centralizado = false) }
                    }
                } else {
                    // Tela estreita: tudo empilhado e centralizado.
                    Column(
                        modifier = Modifier.padding(24.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Avatar(nome = perfil.nome, tamanho = 120.dp)
                        Spacer(Modifier.height(16.dp))
                        Identificacao(perfil, centralizado = true)
                    }
                }

                HorizontalDivider(
                    modifier = Modifier.padding(horizontal = 24.dp),
                    color = MaterialTheme.colorScheme.surfaceVariant
                )

                Column(modifier = Modifier.padding(24.dp)) {
                    Contatos(perfil)
                    Spacer(Modifier.height(20.dp))
                    RedesSociais(perfil)
                    Spacer(Modifier.height(20.dp))
                    BotaoCompartilhar(perfil)
                }
            }

            Spacer(Modifier.height(16.dp))
        }
    }
}

/** Nome, profissao e descricao. */
@Composable
private fun Identificacao(perfil: Perfil, centralizado: Boolean) {
    val alinhamento = if (centralizado) Alignment.CenterHorizontally else Alignment.Start
    val alinhamentoTexto = if (centralizado) TextAlign.Center else TextAlign.Start

    Column(horizontalAlignment = alinhamento) {
        Text(
            text = perfil.nome,
            style = MaterialTheme.typography.displaySmall,
            textAlign = alinhamentoTexto,
            color = MaterialTheme.colorScheme.onSurface
        )
        Spacer(Modifier.height(4.dp))
        Text(
            text = perfil.profissao.uppercase(),
            style = MaterialTheme.typography.titleMedium,
            textAlign = alinhamentoTexto,
            color = MaterialTheme.colorScheme.primary
        )
        Spacer(Modifier.height(12.dp))
        Text(
            text = perfil.descricao,
            style = MaterialTheme.typography.bodyMedium,
            textAlign = alinhamentoTexto,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
    }
}

/** E-mail, telefone e cidade. Os dois primeiros sao clicaveis. */
@Composable
private fun Contatos(perfil: Perfil) {
    val context = LocalContext.current

    Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
        // Um contato invalido simplesmente nao e exibido.
        if (emailValido(perfil.email)) {
            LinhaContato(Icons.Filled.Email, perfil.email) {
                enviarEmail(context, perfil.email)
            }
        }
        if (telefoneValido(perfil.telefone)) {
            LinhaContato(Icons.Filled.Phone, formatarTelefone(perfil.telefone)) {
                discarTelefone(context, perfil.telefone)
            }
        }
        if (perfil.cidade.isNotBlank()) {
            LinhaContato(Icons.Filled.LocationOn, perfil.cidade, aoClicar = null)
        }
    }
}

/** Uma linha de contato. Quando [aoClicar] e nulo, a linha e apenas informativa. */
@Composable
private fun LinhaContato(
    icone: ImageVector,
    texto: String,
    aoClicar: (() -> Unit)?
) {
    // A linha so vira area clicavel quando ha acao associada.
    val base = Modifier
        .fillMaxWidth()
        .clip(RoundedCornerShape(8.dp))
    val linha = if (aoClicar != null) base.clickable(onClick = aoClicar) else base

    Row(
        modifier = linha.padding(vertical = 10.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(
            imageVector = icone,
            contentDescription = null,
            tint = MaterialTheme.colorScheme.primary,
            modifier = Modifier.size(20.dp)
        )
        Spacer(Modifier.size(12.dp))
        Text(
            text = texto,
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurface
        )
    }
}

/** Botoes que abrem os perfis em redes sociais. */
@Composable
private fun RedesSociais(perfil: Perfil) {
    val context = LocalContext.current

    Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
        Text(
            text = stringResource(R.string.encontre_me),
            style = MaterialTheme.typography.labelLarge,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            perfil.redes.forEach { rede ->
                OutlinedButton(
                    onClick = { abrirLink(context, rede.url) },
                    modifier = Modifier.weight(1f)
                ) {
                    Text(text = rede.nome, maxLines = 1)
                }
            }
        }
    }
}

/** Botao que envia o cartao para outro aplicativo. */
@Composable
private fun BotaoCompartilhar(perfil: Perfil) {
    val context = LocalContext.current

    Button(
        onClick = { compartilharCartao(context, perfil) },
        modifier = Modifier.fillMaxWidth()
    ) {
        Icon(
            imageVector = Icons.Filled.Share,
            contentDescription = null,
            modifier = Modifier.size(18.dp)
        )
        Spacer(Modifier.size(8.dp))
        Text(text = stringResource(R.string.compartilhar))
    }
}

/** Interruptor que alterna entre tema claro e escuro. */
@Composable
private fun SeletorDeTema(temaEscuro: Boolean, onAlternarTema: (Boolean) -> Unit) {
    Row(
        modifier = Modifier
            .widthIn(max = 640.dp)
            .fillMaxWidth(),
        horizontalArrangement = Arrangement.End,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = stringResource(
                if (temaEscuro) R.string.tema_escuro else R.string.tema_claro
            ),
            style = MaterialTheme.typography.labelLarge,
            color = MaterialTheme.colorScheme.onBackground
        )
        Spacer(Modifier.size(8.dp))
        Switch(checked = temaEscuro, onCheckedChange = onAlternarTema)
    }
}

// ---------- Pre-visualizacoes do Android Studio ----------

@Preview(showBackground = true, name = "Tema claro")
@Composable
private fun PreviewClaro() {
    CartaoVisitaTheme(escuro = false) {
        CartaoScreen(perfil = PERFIL_PADRAO, temaEscuro = false, onAlternarTema = {})
    }
}

@Preview(showBackground = true, name = "Tema escuro")
@Composable
private fun PreviewEscuro() {
    CartaoVisitaTheme(escuro = true) {
        CartaoScreen(perfil = PERFIL_PADRAO, temaEscuro = true, onAlternarTema = {})
    }
}

@Preview(showBackground = true, name = "Tela larga", widthDp = 800, heightDp = 480)
@Composable
private fun PreviewTelaLarga() {
    CartaoVisitaTheme(escuro = false) {
        CartaoScreen(perfil = PERFIL_PADRAO, temaEscuro = false, onAlternarTema = {})
    }
}
