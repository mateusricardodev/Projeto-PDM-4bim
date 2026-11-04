package com.pdm.calculadora

import java.math.BigDecimal
import java.math.RoundingMode
import kotlin.math.sqrt

/**
 * Regras da calculadora.
 *
 * Este arquivo nao importa nada do Android de proposito: toda a logica fica
 * separada da interface e pode ser testada na JVM (ver app/src/test).
 *
 * O [Estado] e imutavel: cada tecla pressionada produz um estado novo, o que
 * facilita acompanhar o que acontece e testar cada passo isoladamente.
 */

/** As quatro operacoes do visor. */
enum class Operacao(val simbolo: String) {
    SOMA("+"),
    SUBTRACAO("−"),
    MULTIPLICACAO("×"),
    DIVISAO("÷")
}

/** Situacoes que a calculadora recusa. */
enum class ErroCalculo { DIVISAO_POR_ZERO, RAIZ_DE_NEGATIVO }

/**
 * Estado completo da calculadora.
 *
 * @param visor o que esta escrito na tela.
 * @param acumulado primeiro operando, guardado enquanto o segundo e digitado.
 * @param operacao operacao pendente.
 * @param reiniciarVisor quando true, o proximo digito comeca um numero novo
 *        em vez de ser acrescentado ao que ja esta no visor.
 * @param erro preenchido quando a ultima operacao foi recusada.
 */
data class Estado(
    val visor: String = "0",
    val acumulado: Double? = null,
    val operacao: Operacao? = null,
    val reiniciarVisor: Boolean = false,
    val erro: ErroCalculo? = null
)

val ESTADO_INICIAL = Estado()

/** Maximo de caracteres digitaveis, para o numero nao estourar a tela. */
private const val MAX_CARACTERES = 15

/** Le o visor como numero. A virgula e o separador decimal exibido. */
fun paraNumero(visor: String): Double =
    visor.replace(',', '.').toDoubleOrNull() ?: 0.0

/**
 * Escreve um numero no formato do visor.
 *
 * Arredonda para no maximo 8 casas decimais e remove zeros a direita, de modo
 * que 2,0 aparece como "2" e 0,1 + 0,2 aparece como "0,3" em vez do
 * 0,30000000000000004 que o ponto flutuante produz.
 */
fun formatarNumero(valor: Double): String {
    if (!valor.isFinite()) return "0"
    return BigDecimal(valor)
        .setScale(8, RoundingMode.HALF_UP)
        .stripTrailingZeros()
        .toPlainString()
        .replace('.', ',')
}

/** Aplica uma das quatro operacoes. */
private fun aplicar(a: Double, b: Double, operacao: Operacao): Double = when (operacao) {
    Operacao.SOMA -> a + b
    Operacao.SUBTRACAO -> a - b
    Operacao.MULTIPLICACAO -> a * b
    Operacao.DIVISAO -> a / b
}

/** Digito de 0 a 9. Depois de um erro, a calculadora recomeca do zero. */
fun aoDigitar(estado: Estado, digito: Char): Estado {
    val base = if (estado.erro != null) ESTADO_INICIAL else estado

    val visor = when {
        base.reiniciarVisor -> digito.toString()
        base.visor == "0" -> digito.toString()
        base.visor == "-0" -> "-$digito"
        base.visor.length >= MAX_CARACTERES -> return base
        else -> base.visor + digito
    }
    return base.copy(visor = visor, reiniciarVisor = false)
}

/** Separador decimal. Um numero nunca recebe duas virgulas. */
fun aoDigitarVirgula(estado: Estado): Estado {
    val base = if (estado.erro != null) ESTADO_INICIAL else estado

    return when {
        base.reiniciarVisor -> base.copy(visor = "0,", reiniciarVisor = false)
        base.visor.contains(',') -> base
        base.visor.length >= MAX_CARACTERES -> base
        else -> base.copy(visor = base.visor + ",")
    }
}

/** Apaga o ultimo caractere: serve para corrigir um numero digitado errado. */
fun aoApagar(estado: Estado): Estado {
    if (estado.erro != null) return ESTADO_INICIAL
    // Um resultado nao e apagado caractere a caractere.
    if (estado.reiniciarVisor) return estado

    val restante = estado.visor.dropLast(1)
    val visor = if (restante.isEmpty() || restante == "-") "0" else restante
    return estado.copy(visor = visor)
}

/** Tecla C: zera tudo. */
fun aoLimpar(): Estado = ESTADO_INICIAL

/** Inverte o sinal do numero que esta no visor. */
fun aoTrocarSinal(estado: Estado): Estado {
    if (estado.erro != null) return ESTADO_INICIAL
    if (estado.visor == "0") return estado

    val visor = if (estado.visor.startsWith("-")) {
        estado.visor.removePrefix("-")
    } else {
        "-${estado.visor}"
    }
    return estado.copy(visor = visor)
}

/**
 * Tecla de igual.
 *
 * Sem operacao pendente nao ha o que calcular e o estado volta inalterado.
 * Divisao por zero nao e executada: o estado passa a carregar o erro.
 */
fun aoIgual(estado: Estado): Estado {
    if (estado.erro != null) return estado
    val operacao = estado.operacao ?: return estado
    val acumulado = estado.acumulado ?: return estado

    val operando = paraNumero(estado.visor)
    if (operacao == Operacao.DIVISAO && operando == 0.0) {
        return estado.copy(erro = ErroCalculo.DIVISAO_POR_ZERO)
    }

    return Estado(
        visor = formatarNumero(aplicar(acumulado, operando, operacao)),
        reiniciarVisor = true
    )
}

/**
 * Tecla de operacao (+, -, x, /).
 *
 * Com uma operacao ja pendente e um numero recem digitado, o resultado
 * parcial e calculado antes: assim "2 + 3 + 4" funciona sem apertar igual.
 */
fun aoOperacao(estado: Estado, operacao: Operacao): Estado {
    if (estado.erro != null) return ESTADO_INICIAL

    val base = if (estado.operacao != null && estado.acumulado != null && !estado.reiniciarVisor) {
        val parcial = aoIgual(estado)
        if (parcial.erro != null) return parcial
        parcial
    } else {
        estado
    }

    return base.copy(
        acumulado = paraNumero(base.visor),
        operacao = operacao,
        reiniciarVisor = true
    )
}

/**
 * Porcentagem.
 *
 * Em soma e subtracao o percentual e calculado sobre o primeiro operando,
 * como nas calculadoras comuns: "200 + 10 %" resulta em 20, que somado da 220.
 * Nos demais casos o valor do visor e simplesmente dividido por 100.
 */
fun aoPorcentagem(estado: Estado): Estado {
    if (estado.erro != null) return ESTADO_INICIAL

    val valor = paraNumero(estado.visor)
    val acumulado = estado.acumulado
    val resultado = if (
        acumulado != null &&
        (estado.operacao == Operacao.SOMA || estado.operacao == Operacao.SUBTRACAO)
    ) {
        acumulado * valor / 100
    } else {
        valor / 100
    }

    return estado.copy(visor = formatarNumero(resultado), reiniciarVisor = false)
}

/** Raiz quadrada. Numero negativo nao tem raiz real e gera erro. */
fun aoRaizQuadrada(estado: Estado): Estado {
    if (estado.erro != null) return ESTADO_INICIAL

    val valor = paraNumero(estado.visor)
    if (valor < 0) return estado.copy(erro = ErroCalculo.RAIZ_DE_NEGATIVO)

    return estado.copy(visor = formatarNumero(sqrt(valor)), reiniciarVisor = true)
}

/** Linha do historico, por exemplo "2 + 3 = 5". */
fun descreverCalculo(acumulado: Double, operacao: Operacao, operando: String, resultado: String): String =
    "${formatarNumero(acumulado)} ${operacao.simbolo} $operando = $resultado"
