package com.pdm.conversortemperatura

import java.util.Locale

/**
 * Regras de conversao de temperatura.
 *
 * Este arquivo nao importa nada do Android de proposito: a matematica e a
 * validacao ficam separadas da interface e podem ser testadas na JVM
 * (ver app/src/test).
 */

/** As tres escalas aceitas pelo aplicativo. */
enum class Unidade(val simbolo: String, val rotulo: String) {
    CELSIUS("°C", "Celsius"),
    FAHRENHEIT("°F", "Fahrenheit"),
    KELVIN("K", "Kelvin")
}

/** Margem para comparacoes com ponto flutuante (evita recusar -273,15 exato). */
private const val TOLERANCIA = 1e-9

/** Menor temperatura fisicamente possivel em cada escala. */
fun zeroAbsoluto(unidade: Unidade): Double = when (unidade) {
    Unidade.CELSIUS -> -273.15
    Unidade.FAHRENHEIT -> -459.67
    Unidade.KELVIN -> 0.0
}

/** Converte qualquer escala para Celsius, usada como escala intermediaria. */
fun paraCelsius(valor: Double, de: Unidade): Double = when (de) {
    Unidade.CELSIUS -> valor
    Unidade.FAHRENHEIT -> (valor - 32) * 5 / 9
    Unidade.KELVIN -> valor - 273.15
}

/** Converte de Celsius para a escala desejada. */
fun deCelsius(celsius: Double, para: Unidade): Double = when (para) {
    Unidade.CELSIUS -> celsius
    Unidade.FAHRENHEIT -> celsius * 9 / 5 + 32
    Unidade.KELVIN -> celsius + 273.15
}

/**
 * Converte um valor entre duas escalas.
 *
 * Passar sempre por Celsius evita escrever as seis formulas possiveis: basta
 * uma entrada e uma saida por escala.
 */
fun converter(valor: Double, de: Unidade, para: Unidade): Double =
    deCelsius(paraCelsius(valor, de), para)

/**
 * Le o numero digitado, aceitando virgula ou ponto como separador decimal.
 *
 * Devolve null quando o texto nao e um numero utilizavel. Valores infinitos e
 * NaN sao recusados, porque a conversao deles nao faria sentido.
 */
fun interpretarNumero(texto: String): Double? {
    val numero = texto.trim().replace(',', '.').toDoubleOrNull()
    return if (numero != null && numero.isFinite()) numero else null
}

/** Motivo pelo qual uma entrada foi recusada. */
enum class Erro { NAO_E_NUMERO, ABAIXO_DO_ZERO_ABSOLUTO }

/** Situacao atual do formulario. */
sealed interface Resultado {
    /** Nada digitado ainda: a tela nao mostra resultado nem erro. */
    data object Vazio : Resultado

    /** Entrada recusada, com o motivo. */
    data class Invalido(val erro: Erro) : Resultado

    /** Conversao concluida. */
    data class Valido(val entrada: Double, val saida: Double) : Resultado
}

/**
 * Valida o texto digitado e, se estiver correto, ja devolve a conversao.
 *
 * A validacao respeita o limite fisico de cada escala: nao existe temperatura
 * abaixo do zero absoluto.
 */
fun avaliar(texto: String, de: Unidade, para: Unidade): Resultado {
    if (texto.isBlank()) return Resultado.Vazio

    val numero = interpretarNumero(texto)
        ?: return Resultado.Invalido(Erro.NAO_E_NUMERO)

    if (numero < zeroAbsoluto(de) - TOLERANCIA) {
        return Resultado.Invalido(Erro.ABAIXO_DO_ZERO_ABSOLUTO)
    }

    return Resultado.Valido(entrada = numero, saida = converter(numero, de, para))
}

/**
 * Formata um valor com duas casas decimais.
 *
 * Valores muito proximos de zero sao normalizados para nao exibir "-0,00".
 */
fun formatar(valor: Double): String {
    val normalizado = if (kotlin.math.abs(valor) < 0.005) 0.0 else valor
    return String.format(Locale.getDefault(), "%.2f", normalizado)
}

/** Linha do historico, por exemplo "25,00 °C = 77,00 °F". */
fun descreverConversao(entrada: Double, de: Unidade, saida: Double, para: Unidade): String =
    "${formatar(entrada)} ${de.simbolo} = ${formatar(saida)} ${para.simbolo}"
