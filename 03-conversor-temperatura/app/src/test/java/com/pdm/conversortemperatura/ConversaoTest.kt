package com.pdm.conversortemperatura

import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Test

/** Testes das regras de conversao. Rodam na JVM (./gradlew test). */
class ConversaoTest {

    private val delta = 0.0001

    // ---------- Formulas do enunciado ----------

    @Test
    fun celsiusParaFahrenheit() {
        assertEquals(32.0, converter(0.0, Unidade.CELSIUS, Unidade.FAHRENHEIT), delta)
        assertEquals(212.0, converter(100.0, Unidade.CELSIUS, Unidade.FAHRENHEIT), delta)
        assertEquals(77.0, converter(25.0, Unidade.CELSIUS, Unidade.FAHRENHEIT), delta)
    }

    @Test
    fun fahrenheitParaCelsius() {
        assertEquals(0.0, converter(32.0, Unidade.FAHRENHEIT, Unidade.CELSIUS), delta)
        assertEquals(100.0, converter(212.0, Unidade.FAHRENHEIT, Unidade.CELSIUS), delta)
        assertEquals(37.0, converter(98.6, Unidade.FAHRENHEIT, Unidade.CELSIUS), delta)
    }

    @Test
    fun celsiusParaKelvin() {
        assertEquals(273.15, converter(0.0, Unidade.CELSIUS, Unidade.KELVIN), delta)
        assertEquals(373.15, converter(100.0, Unidade.CELSIUS, Unidade.KELVIN), delta)
    }

    @Test
    fun kelvinParaFahrenheit() {
        // 0 K e o zero absoluto, que vale -459,67 °F.
        assertEquals(-459.67, converter(0.0, Unidade.KELVIN, Unidade.FAHRENHEIT), delta)
    }

    @Test
    fun converterParaMesmaUnidadeNaoAlteraOValor() {
        Unidade.entries.forEach { u ->
            assertEquals(42.0, converter(42.0, u, u), delta)
        }
    }

    @Test
    fun converterEVoltarDevolveOValorOriginal() {
        for (de in Unidade.entries) {
            for (para in Unidade.entries) {
                val ida = converter(30.0, de, para)
                val volta = converter(ida, para, de)
                assertEquals("$de -> $para", 30.0, volta, delta)
            }
        }
    }

    @Test
    fun quarentaGrausNegativosSaoIguaisNasDuasEscalas() {
        assertEquals(-40.0, converter(-40.0, Unidade.CELSIUS, Unidade.FAHRENHEIT), delta)
    }

    // ---------- Leitura do numero digitado ----------

    @Test
    fun aceitaVirgulaEPontoComoSeparadorDecimal() {
        assertEquals(25.5, interpretarNumero("25,5")!!, delta)
        assertEquals(25.5, interpretarNumero("25.5")!!, delta)
        assertEquals(-3.2, interpretarNumero("  -3,2  ")!!, delta)
    }

    @Test
    fun recusaTextoQueNaoEnumero() {
        assertNull(interpretarNumero("abc"))
        assertNull(interpretarNumero("12,3,4"))
        assertNull(interpretarNumero(""))
        assertNull(interpretarNumero("-"))
    }

    @Test
    fun recusaValoresInfinitosEnaN() {
        assertNull(interpretarNumero("Infinity"))
        assertNull(interpretarNumero("NaN"))
    }

    // ---------- Validacao ----------

    @Test
    fun campoVazioNaoGeraErro() {
        assertEquals(Resultado.Vazio, avaliar("", Unidade.CELSIUS, Unidade.FAHRENHEIT))
        assertEquals(Resultado.Vazio, avaliar("   ", Unidade.CELSIUS, Unidade.FAHRENHEIT))
    }

    @Test
    fun textoInvalidoEIdentificado() {
        val r = avaliar("abc", Unidade.CELSIUS, Unidade.FAHRENHEIT)
        assertEquals(Resultado.Invalido(Erro.NAO_E_NUMERO), r)
    }

    @Test
    fun recusaTemperaturaAbaixoDoZeroAbsoluto() {
        assertEquals(
            Resultado.Invalido(Erro.ABAIXO_DO_ZERO_ABSOLUTO),
            avaliar("-300", Unidade.CELSIUS, Unidade.KELVIN)
        )
        assertEquals(
            Resultado.Invalido(Erro.ABAIXO_DO_ZERO_ABSOLUTO),
            avaliar("-500", Unidade.FAHRENHEIT, Unidade.CELSIUS)
        )
        assertEquals(
            Resultado.Invalido(Erro.ABAIXO_DO_ZERO_ABSOLUTO),
            avaliar("-1", Unidade.KELVIN, Unidade.CELSIUS)
        )
    }

    @Test
    fun aceitaExatamenteOZeroAbsoluto() {
        Unidade.entries.forEach { u ->
            val r = avaliar(zeroAbsoluto(u).toString(), u, Unidade.KELVIN)
            assertTrue("$u deveria ser aceito", r is Resultado.Valido)
        }
    }

    @Test
    fun entradaValidaDevolveEntradaESaida() {
        val r = avaliar("25", Unidade.CELSIUS, Unidade.FAHRENHEIT)
        assertTrue(r is Resultado.Valido)
        r as Resultado.Valido
        assertEquals(25.0, r.entrada, delta)
        assertEquals(77.0, r.saida, delta)
    }

    // ---------- Formatacao ----------

    @Test
    fun formataComDuasCasasDecimais() {
        assertTrue(formatar(77.0).matches(Regex("77[.,]00")))
        assertTrue(formatar(25.456).matches(Regex("25[.,]46")))
    }

    @Test
    fun naoExibeMenosZero() {
        // -0,001 arredondado daria "-0,00"; o valor e normalizado para zero.
        assertTrue(formatar(-0.001).matches(Regex("0[.,]00")))
    }

    @Test
    fun descricaoDoHistoricoTrazOsDoisSimbolos() {
        val texto = descreverConversao(25.0, Unidade.CELSIUS, 77.0, Unidade.FAHRENHEIT)
        assertTrue(texto.contains("°C"))
        assertTrue(texto.contains("°F"))
        assertTrue(texto.contains("="))
    }
}
