package com.pdm.calculadora

import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Test

/** Testes da logica da calculadora. Rodam na JVM (./gradlew test). */
class CalculadoraTest {

    /** Atalho: digita uma sequencia de teclas numericas sobre um estado. */
    private fun digitar(estado: Estado, teclas: String): Estado =
        teclas.fold(estado) { e, c -> if (c == ',') aoDigitarVirgula(e) else aoDigitar(e, c) }

    // ---------- As quatro operacoes ----------

    @Test
    fun realizaAdicao() {
        var e = digitar(ESTADO_INICIAL, "2")
        e = aoOperacao(e, Operacao.SOMA)
        e = digitar(e, "3")
        assertEquals("5", aoIgual(e).visor)
    }

    @Test
    fun realizaSubtracao() {
        var e = digitar(ESTADO_INICIAL, "10")
        e = aoOperacao(e, Operacao.SUBTRACAO)
        e = digitar(e, "4")
        assertEquals("6", aoIgual(e).visor)
    }

    @Test
    fun realizaMultiplicacao() {
        var e = digitar(ESTADO_INICIAL, "7")
        e = aoOperacao(e, Operacao.MULTIPLICACAO)
        e = digitar(e, "6")
        assertEquals("42", aoIgual(e).visor)
    }

    @Test
    fun realizaDivisao() {
        var e = digitar(ESTADO_INICIAL, "20")
        e = aoOperacao(e, Operacao.DIVISAO)
        e = digitar(e, "8")
        assertEquals("2,5", aoIgual(e).visor)
    }

    @Test
    fun operaComNumerosDecimais() {
        var e = digitar(ESTADO_INICIAL, "0,1")
        e = aoOperacao(e, Operacao.SOMA)
        e = digitar(e, "0,2")
        // Sem arredondar, o ponto flutuante daria 0,30000000000000004.
        assertEquals("0,3", aoIgual(e).visor)
    }

    @Test
    fun operaComNumerosNegativos() {
        var e = aoTrocarSinal(digitar(ESTADO_INICIAL, "5"))
        assertEquals("-5", e.visor)
        e = aoOperacao(e, Operacao.MULTIPLICACAO)
        e = digitar(e, "3")
        assertEquals("-15", aoIgual(e).visor)
    }

    // ---------- Divisao por zero ----------

    @Test
    fun divisaoPorZeroGeraErro() {
        var e = digitar(ESTADO_INICIAL, "5")
        e = aoOperacao(e, Operacao.DIVISAO)
        e = digitar(e, "0")
        assertEquals(ErroCalculo.DIVISAO_POR_ZERO, aoIgual(e).erro)
    }

    @Test
    fun depoisDoErroQualquerDigitoRecomecaDoZero() {
        var e = digitar(ESTADO_INICIAL, "5")
        e = aoOperacao(e, Operacao.DIVISAO)
        e = aoIgual(digitar(e, "0"))
        assertEquals(ErroCalculo.DIVISAO_POR_ZERO, e.erro)

        e = aoDigitar(e, '7')
        assertNull(e.erro)
        assertEquals("7", e.visor)
    }

    @Test
    fun zeroDivididoPorNumeroEValido() {
        var e = digitar(ESTADO_INICIAL, "0")
        e = aoOperacao(e, Operacao.DIVISAO)
        e = digitar(e, "5")
        val r = aoIgual(e)
        assertNull(r.erro)
        assertEquals("0", r.visor)
    }

    // ---------- Digitacao ----------

    @Test
    fun zerosAEsquerdaNaoSeAcumulam() {
        assertEquals("5", digitar(ESTADO_INICIAL, "005").visor)
    }

    @Test
    fun aceitaUmaUnicaVirgula() {
        val e = digitar(ESTADO_INICIAL, "1,5")
        assertEquals("1,5", aoDigitarVirgula(e).visor)
    }

    @Test
    fun virgulaNoInicioViraZeroVirgula() {
        assertEquals("0,", aoDigitarVirgula(ESTADO_INICIAL).visor)
    }

    // ---------- Correcao e limpeza ----------

    @Test
    fun apagarRemoveOUltimoCaractere() {
        val e = digitar(ESTADO_INICIAL, "123")
        assertEquals("12", aoApagar(e).visor)
    }

    @Test
    fun apagarTudoDeixaZero() {
        var e = digitar(ESTADO_INICIAL, "7")
        e = aoApagar(e)
        assertEquals("0", e.visor)
        // Continuar apagando nao quebra nada.
        assertEquals("0", aoApagar(e).visor)
    }

    @Test
    fun limparZeraOperacaoPendente() {
        var e = digitar(ESTADO_INICIAL, "9")
        e = aoOperacao(e, Operacao.SOMA)
        assertEquals(ESTADO_INICIAL, aoLimpar())
        assertNull(aoLimpar().operacao)
    }

    @Test
    fun trocarSinalNaoAfetaOZero() {
        assertEquals("0", aoTrocarSinal(ESTADO_INICIAL).visor)
    }

    @Test
    fun trocarSinalDuasVezesVoltaAoOriginal() {
        val e = digitar(ESTADO_INICIAL, "42")
        assertEquals("42", aoTrocarSinal(aoTrocarSinal(e)).visor)
    }

    // ---------- Encadeamento ----------

    @Test
    fun encadeiaOperacoesSemApertarIgual() {
        var e = digitar(ESTADO_INICIAL, "2")
        e = aoOperacao(e, Operacao.SOMA)
        e = digitar(e, "3")
        e = aoOperacao(e, Operacao.SOMA)   // ja mostra o 5
        assertEquals("5", e.visor)
        e = digitar(e, "4")
        assertEquals("9", aoIgual(e).visor)
    }

    @Test
    fun trocarDeOperacaoAntesDeDigitarNaoCalcula() {
        var e = digitar(ESTADO_INICIAL, "8")
        e = aoOperacao(e, Operacao.SOMA)
        e = aoOperacao(e, Operacao.MULTIPLICACAO)  // arrependeu-se
        e = digitar(e, "2")
        assertEquals("16", aoIgual(e).visor)
    }

    @Test
    fun igualSemOperacaoPendenteNaoAlteraOVisor() {
        val e = digitar(ESTADO_INICIAL, "12")
        assertEquals("12", aoIgual(e).visor)
    }

    @Test
    fun digitarDepoisDoResultadoComecaNumeroNovo() {
        var e = digitar(ESTADO_INICIAL, "2")
        e = aoOperacao(e, Operacao.SOMA)
        e = aoIgual(digitar(e, "3"))
        assertEquals("5", e.visor)
        assertEquals("7", aoDigitar(e, '7').visor)
    }

    // ---------- Porcentagem e raiz (desafio extra) ----------

    @Test
    fun porcentagemIsoladaDividePorCem() {
        val e = digitar(ESTADO_INICIAL, "50")
        assertEquals("0,5", aoPorcentagem(e).visor)
    }

    @Test
    fun porcentagemNaSomaUsaOPrimeiroOperando() {
        var e = digitar(ESTADO_INICIAL, "200")
        e = aoOperacao(e, Operacao.SOMA)
        e = digitar(e, "10")
        e = aoPorcentagem(e)
        assertEquals("20", e.visor)
        assertEquals("220", aoIgual(e).visor)
    }

    @Test
    fun calculaRaizQuadrada() {
        assertEquals("9", aoRaizQuadrada(digitar(ESTADO_INICIAL, "81")).visor)
        assertEquals("1,5", aoRaizQuadrada(digitar(ESTADO_INICIAL, "2,25")).visor)
    }

    @Test
    fun raizDeNegativoGeraErro() {
        val e = aoTrocarSinal(digitar(ESTADO_INICIAL, "9"))
        assertEquals(ErroCalculo.RAIZ_DE_NEGATIVO, aoRaizQuadrada(e).erro)
    }

    // ---------- Formatacao ----------

    @Test
    fun resultadoInteiroNaoMostraCasasDecimais() {
        assertEquals("4", formatarNumero(4.0))
        assertEquals("-7", formatarNumero(-7.0))
    }

    @Test
    fun resultadoFracionarioEArredondado() {
        assertEquals("0,33333333", formatarNumero(1.0 / 3.0))
    }

    @Test
    fun descricaoDoHistoricoTemOsDoisOperandos() {
        assertEquals("2 + 3 = 5", descreverCalculo(2.0, Operacao.SOMA, "3", "5"))
    }
}
