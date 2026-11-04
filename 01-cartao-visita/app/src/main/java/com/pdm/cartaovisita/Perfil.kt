package com.pdm.cartaovisita

/**
 * Dados do cartao e as funcoes que trabalham sobre eles.
 *
 * Este arquivo nao importa nenhuma classe do Android de proposito: assim as
 * regras ficam separadas da interface e podem ser testadas na JVM
 * (ver app/src/test). Para personalizar o cartao, altere [PERFIL_PADRAO].
 */

/** Um link de rede social exibido como botao no cartao. */
data class RedeSocial(
    val nome: String,
    val usuario: String,
    val url: String
)

/** Todas as informacoes exibidas no cartao de visita. */
data class Perfil(
    val nome: String,
    val profissao: String,
    val descricao: String,
    val email: String,
    /** Somente digitos, com DDD. Ex.: "43999998888". */
    val telefone: String,
    val cidade: String,
    val redes: List<RedeSocial>
)

/** Perfil exibido pelo aplicativo. */
val PERFIL_PADRAO = Perfil(
    nome = "Mateus Ricardo",
    profissao = "Desenvolvedor Android",
    descricao = "Estudante de Programacao para Dispositivos Moveis. " +
        "Trabalho com Kotlin e Jetpack Compose na construcao de aplicativos nativos.",
    email = "mateus.ricardo761919@gmail.com",
    // Sem telefone cadastrado: a linha simplesmente nao aparece no cartao.
    // Para exibi-la, basta preencher com DDD mais o numero, so digitos.
    telefone = "",
    cidade = "Parana, Brasil",
    redes = listOf(
        RedeSocial("GitHub", "@mateusricardodev", "https://github.com/mateusricardodev"),
        RedeSocial("LinkedIn", "mateus-ricardo", "https://www.linkedin.com/in/mateus-ricardo"),
        RedeSocial("Instagram", "@mateusricardo23", "https://www.instagram.com/mateusricardo23/")
    )
)

/**
 * Iniciais usadas no avatar: primeira letra do primeiro e do ultimo nome.
 *
 * Preposicoes ("de", "da", "dos"...) sao ignoradas, porque nao ajudam a
 * identificar a pessoa. Nome vazio devolve string vazia.
 */
fun iniciais(nome: String): String {
    val preposicoes = setOf("de", "da", "do", "das", "dos", "e")
    val partes = nome.trim()
        .split(Regex("\\s+"))
        .filter { it.isNotBlank() && it.lowercase() !in preposicoes }

    return when {
        partes.isEmpty() -> ""
        partes.size == 1 -> partes[0].first().uppercase()
        else -> "${partes.first().first()}${partes.last().first()}".uppercase()
    }
}

/** Mantem apenas os digitos de um texto (usado para montar o link "tel:"). */
fun somenteDigitos(texto: String): String = texto.filter { it.isDigit() }

/**
 * Formata o telefone para leitura: "43999998888" vira "(43) 99999-8888".
 *
 * Numeros que nao tenham 10 ou 11 digitos sao devolvidos sem alteracao, para
 * que um valor inesperado nunca quebre a tela.
 */
fun formatarTelefone(telefone: String): String {
    val d = somenteDigitos(telefone)
    return when (d.length) {
        11 -> "(${d.substring(0, 2)}) ${d.substring(2, 7)}-${d.substring(7)}"
        10 -> "(${d.substring(0, 2)}) ${d.substring(2, 6)}-${d.substring(6)}"
        else -> telefone
    }
}

/** Validacao simples de e-mail, usada para nao exibir um contato invalido. */
fun emailValido(email: String): Boolean =
    Regex("^[^\\s@]+@[^\\s@]+\\.[^\\s@]{2,}$").matches(email.trim())

/** Considera o telefone utilizavel quando tem DDD mais 8 ou 9 digitos. */
fun telefoneValido(telefone: String): Boolean =
    somenteDigitos(telefone).length in 10..11

/** Texto enviado pelo botao de compartilhar. */
fun textoCompartilhamento(perfil: Perfil): String = buildString {
    appendLine(perfil.nome)
    appendLine(perfil.profissao)
    if (emailValido(perfil.email)) appendLine("E-mail: ${perfil.email}")
    if (telefoneValido(perfil.telefone)) appendLine("Telefone: ${formatarTelefone(perfil.telefone)}")
    perfil.redes.forEach { appendLine("${it.nome}: ${it.url}") }
}.trim()
