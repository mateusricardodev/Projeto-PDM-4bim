package com.pdm.cartaovisita

import android.content.ActivityNotFoundException
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.widget.Toast

/**
 * Acoes que o cartao dispara para fora do aplicativo (e-mail, telefone,
 * navegador e compartilhamento).
 *
 * Todas passam por [abrir], que avisa o usuario quando o aparelho nao tem
 * nenhum aplicativo capaz de atender ao pedido, em vez de fechar sozinho.
 */

private fun abrir(context: Context, intent: Intent, erro: String) {
    try {
        context.startActivity(intent)
    } catch (e: ActivityNotFoundException) {
        Toast.makeText(context, erro, Toast.LENGTH_SHORT).show()
    }
}

/** Abre o aplicativo de e-mail com o destinatario ja preenchido. */
fun enviarEmail(context: Context, email: String) {
    val intent = Intent(Intent.ACTION_SENDTO, Uri.parse("mailto:$email"))
    abrir(context, intent, context.getString(R.string.erro_email))
}

/** Abre o discador com o numero preenchido (nao liga sozinho). */
fun discarTelefone(context: Context, telefone: String) {
    val intent = Intent(Intent.ACTION_DIAL, Uri.parse("tel:${somenteDigitos(telefone)}"))
    abrir(context, intent, context.getString(R.string.erro_telefone))
}

/** Abre um endereco no navegador ou no aplicativo correspondente. */
fun abrirLink(context: Context, url: String) {
    val intent = Intent(Intent.ACTION_VIEW, Uri.parse(url))
    abrir(context, intent, context.getString(R.string.erro_link))
}

/** Compartilha os dados do cartao com outro aplicativo. */
fun compartilharCartao(context: Context, perfil: Perfil) {
    val envio = Intent(Intent.ACTION_SEND).apply {
        type = "text/plain"
        putExtra(Intent.EXTRA_SUBJECT, perfil.nome)
        putExtra(Intent.EXTRA_TEXT, textoCompartilhamento(perfil))
    }
    val seletor = Intent.createChooser(envio, context.getString(R.string.compartilhar))
    abrir(context, seletor, context.getString(R.string.erro_compartilhar))
}
