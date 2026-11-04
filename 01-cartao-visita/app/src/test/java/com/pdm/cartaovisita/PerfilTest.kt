package com.pdm.cartaovisita

import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test

/**
 * Testes das funcoes de Perfil.kt.
 *
 * Rodam na JVM (./gradlew test), sem emulador nem aparelho.
 */
class PerfilTest {

    // ---------- Iniciais do avatar ----------

    @Test
    fun iniciaisUsamPrimeiroEUltimoNome() {
        assertEquals("MR", iniciais("Mateus Ricardo"))
        assertEquals("MS", iniciais("Mateus Ricardo Silva"))
    }

    @Test
    fun iniciaisIgnoramPreposicoes() {
        assertEquals("MS", iniciais("Mateus de Souza"))
        assertEquals("AO", iniciais("Ana da Costa e Oliveira"))
    }

    @Test
    fun iniciaisFuncionamComNomeUnico() {
        assertEquals("M", iniciais("Mateus"))
    }

    @Test
    fun iniciaisToleramEspacosExtrasEMinusculas() {
        assertEquals("MR", iniciais("   mateus    ricardo   "))
    }

    @Test
    fun iniciaisDeNomeVazioSaoVazias() {
        assertEquals("", iniciais(""))
        assertEquals("", iniciais("    "))
    }

    // ---------- Telefone ----------

    @Test
    fun formataCelularComNoveDigitos() {
        assertEquals("(43) 99999-8888", formatarTelefone("43999998888"))
    }

    @Test
    fun formataTelefoneFixoComOitoDigitos() {
        assertEquals("(43) 3333-4444", formatarTelefone("4333334444"))
    }

    @Test
    fun telefoneForaDoPadraoNaoEAlterado() {
        assertEquals("123", formatarTelefone("123"))
    }

    @Test
    fun extraiSomenteOsDigitos() {
        assertEquals("43999998888", somenteDigitos("(43) 99999-8888"))
        assertEquals("", somenteDigitos("sem numero"))
    }

    @Test
    fun validaTelefoneComDddMaisOitoOuNoveDigitos() {
        assertTrue(telefoneValido("43999998888"))
        assertTrue(telefoneValido("(43) 3333-4444"))
        assertFalse(telefoneValido("999998888"))
        assertFalse(telefoneValido(""))
    }

    // ---------- E-mail ----------

    @Test
    fun aceitaEmailBemFormado() {
        assertTrue(emailValido("mateus@exemplo.com"))
        assertTrue(emailValido("  mateus.ricardo@dominio.com.br  "))
    }

    @Test
    fun recusaEmailMalFormado() {
        assertFalse(emailValido("mateus"))
        assertFalse(emailValido("mateus@"))
        assertFalse(emailValido("@exemplo.com"))
        assertFalse(emailValido("mateus @exemplo.com"))
        assertFalse(emailValido(""))
    }

    // ---------- Texto de compartilhamento ----------

    @Test
    fun textoDeCompartilhamentoTrazNomeProfissaoERedes() {
        val texto = textoCompartilhamento(PERFIL_PADRAO)

        assertTrue(texto.contains(PERFIL_PADRAO.nome))
        assertTrue(texto.contains(PERFIL_PADRAO.profissao))
        assertTrue(texto.contains("github.com/mateusricardodev"))
    }

    @Test
    fun textoDeCompartilhamentoOmiteContatoInvalido() {
        val perfil = PERFIL_PADRAO.copy(email = "nao-e-um-email", telefone = "123")
        val texto = textoCompartilhamento(perfil)

        assertFalse(texto.contains("E-mail:"))
        assertFalse(texto.contains("Telefone:"))
        assertTrue(texto.contains(perfil.nome))
    }

    @Test
    fun textoDeCompartilhamentoNaoComecaNemTerminaComLinhaVazia() {
        val texto = textoCompartilhamento(PERFIL_PADRAO)
        assertEquals(texto, texto.trim())
    }
}
