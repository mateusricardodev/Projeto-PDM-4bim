package com.pdm.calculadora

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.Saver
import androidx.compose.runtime.saveable.listSaver
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

/**
 * Guarda o estado da calculadora ao girar a tela.
 *
 * O Bundle do Android so aceita tipos simples, por isso o estado e salvo como
 * uma lista de valores primitivos e remontado na volta.
 */
private val EstadoSaver: Saver<Estado, Any> = listSaver(
    save = {
        listOf(it.visor, it.acumulado, it.operacao?.name, it.reiniciarVisor, it.erro?.name)
    },
    restore = {
        Estado(
            visor = it[0] as String,
            acumulado = it[1] as Double?,
            operacao = (it[2] as String?)?.let(Operacao::valueOf),
            reiniciarVisor = it[3] as Boolean,
            erro = (it[4] as String?)?.let(ErroCalculo::valueOf)
        )
    }
)

/** Tela da calculadora: visor em cima, teclado embaixo, historico no fim. */
@Composable
fun CalculadoraScreen(modifier: Modifier = Modifier) {
    var estado by rememberSaveable(stateSaver = EstadoSaver) { mutableStateOf(ESTADO_INICIAL) }
    val historico = remember { mutableStateListOf<String>() }

    // Registra o calculo antes de o estado ser trocado pelo resultado.
    fun confirmarIgual() {
        val anterior = estado
        val novo = aoIgual(anterior)
        if (novo.erro == null && anterior.operacao != null && anterior.acumulado != null) {
            historico.add(
                0,
                descreverCalculo(anterior.acumulado, anterior.operacao, anterior.visor, novo.visor)
            )
            while (historico.size > 10) historico.removeAt(historico.lastIndex)
        }
        estado = novo
    }

    Column(
        modifier = modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Column(modifier = Modifier.widthIn(max = 480.dp).fillMaxWidth()) {

            Visor(estado = estado)

            Spacer(Modifier.height(16.dp))

            Teclado(
                onDigito = { estado = aoDigitar(estado, it) },
                onVirgula = { estado = aoDigitarVirgula(estado) },
                onOperacao = { estado = aoOperacao(estado, it) },
                onIgual = { confirmarIgual() },
                onLimpar = { estado = aoLimpar() },
                onApagar = { estado = aoApagar(estado) },
                onTrocarSinal = { estado = aoTrocarSinal(estado) },
                onPorcentagem = { estado = aoPorcentagem(estado) },
                onRaiz = { estado = aoRaizQuadrada(estado) }
            )

            Spacer(Modifier.height(20.dp))

            Historico(historico = historico, onLimpar = { historico.clear() })
        }
    }
}

/** Visor: operacao pendente em cima, numero (ou mensagem de erro) embaixo. */
@Composable
private fun Visor(estado: Estado) {
    val mensagemErro = when (estado.erro) {
        ErroCalculo.DIVISAO_POR_ZERO -> stringResource(R.string.erro_divisao_zero)
        ErroCalculo.RAIZ_DE_NEGATIVO -> stringResource(R.string.erro_raiz_negativa)
        null -> null
    }

    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.primaryContainer
        )
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .heightIn(min = 112.dp)
                .padding(20.dp),
            horizontalAlignment = Alignment.End,
            verticalArrangement = Arrangement.Center
        ) {
            // Linha de apoio: mostra o calculo em andamento.
            val pendente = estado.acumulado?.let { acumulado ->
                estado.operacao?.let { "${formatarNumero(acumulado)} ${it.simbolo}" }
            }
            Text(
                text = pendente ?: " ",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onPrimaryContainer
            )

            Spacer(Modifier.height(4.dp))

            Text(
                text = mensagemErro ?: estado.visor,
                fontSize = if (mensagemErro != null) 22.sp else 44.sp,
                maxLines = 2,
                overflow = TextOverflow.Ellipsis,
                textAlign = TextAlign.End,
                color = if (mensagemErro != null) {
                    MaterialTheme.colorScheme.error
                } else {
                    MaterialTheme.colorScheme.onPrimaryContainer
                }
            )
        }
    }
}

/** Teclado: cinco linhas de quatro teclas, mais o igual em largura cheia. */
@Composable
private fun Teclado(
    onDigito: (Char) -> Unit,
    onVirgula: () -> Unit,
    onOperacao: (Operacao) -> Unit,
    onIgual: () -> Unit,
    onLimpar: () -> Unit,
    onApagar: () -> Unit,
    onTrocarSinal: () -> Unit,
    onPorcentagem: () -> Unit,
    onRaiz: () -> Unit
) {
    Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
        LinhaDeTeclas {
            Tecla("C", Modifier.weight(1f), Papel.ACAO, onLimpar)
            Tecla("⌫", Modifier.weight(1f), Papel.ACAO, onApagar)
            Tecla("%", Modifier.weight(1f), Papel.ACAO, onPorcentagem)
            Tecla("√", Modifier.weight(1f), Papel.ACAO, onRaiz)
        }
        LinhaDeTeclas {
            Tecla("7", Modifier.weight(1f), Papel.NUMERO) { onDigito('7') }
            Tecla("8", Modifier.weight(1f), Papel.NUMERO) { onDigito('8') }
            Tecla("9", Modifier.weight(1f), Papel.NUMERO) { onDigito('9') }
            Tecla(Operacao.DIVISAO.simbolo, Modifier.weight(1f), Papel.OPERACAO) {
                onOperacao(Operacao.DIVISAO)
            }
        }
        LinhaDeTeclas {
            Tecla("4", Modifier.weight(1f), Papel.NUMERO) { onDigito('4') }
            Tecla("5", Modifier.weight(1f), Papel.NUMERO) { onDigito('5') }
            Tecla("6", Modifier.weight(1f), Papel.NUMERO) { onDigito('6') }
            Tecla(Operacao.MULTIPLICACAO.simbolo, Modifier.weight(1f), Papel.OPERACAO) {
                onOperacao(Operacao.MULTIPLICACAO)
            }
        }
        LinhaDeTeclas {
            Tecla("1", Modifier.weight(1f), Papel.NUMERO) { onDigito('1') }
            Tecla("2", Modifier.weight(1f), Papel.NUMERO) { onDigito('2') }
            Tecla("3", Modifier.weight(1f), Papel.NUMERO) { onDigito('3') }
            Tecla(Operacao.SUBTRACAO.simbolo, Modifier.weight(1f), Papel.OPERACAO) {
                onOperacao(Operacao.SUBTRACAO)
            }
        }
        LinhaDeTeclas {
            Tecla("+/-", Modifier.weight(1f), Papel.ACAO, onTrocarSinal)
            Tecla("0", Modifier.weight(1f), Papel.NUMERO) { onDigito('0') }
            Tecla(",", Modifier.weight(1f), Papel.NUMERO, onVirgula)
            Tecla(Operacao.SOMA.simbolo, Modifier.weight(1f), Papel.OPERACAO) {
                onOperacao(Operacao.SOMA)
            }
        }
        Tecla("=", Modifier.fillMaxWidth(), Papel.IGUAL, onIgual)
    }
}

@Composable
private fun LinhaDeTeclas(conteudo: @Composable RowScope.() -> Unit) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(8.dp),
        content = conteudo
    )
}

/** Cada papel tem uma cor, para separar numeros, operacoes e acoes. */
private enum class Papel { NUMERO, OPERACAO, ACAO, IGUAL }

@Composable
private fun Tecla(
    texto: String,
    modifier: Modifier = Modifier,
    papel: Papel = Papel.NUMERO,
    aoClicar: () -> Unit
) {
    val fundo: Color = when (papel) {
        Papel.NUMERO -> MaterialTheme.colorScheme.surfaceVariant
        Papel.OPERACAO -> MaterialTheme.colorScheme.secondary
        Papel.ACAO -> MaterialTheme.colorScheme.primaryContainer
        Papel.IGUAL -> MaterialTheme.colorScheme.primary
    }
    val frente: Color = when (papel) {
        Papel.NUMERO -> MaterialTheme.colorScheme.onSurfaceVariant
        Papel.OPERACAO -> MaterialTheme.colorScheme.onSecondary
        Papel.ACAO -> MaterialTheme.colorScheme.onPrimaryContainer
        Papel.IGUAL -> MaterialTheme.colorScheme.onPrimary
    }

    Button(
        onClick = aoClicar,
        modifier = modifier.height(60.dp),
        colors = ButtonDefaults.buttonColors(containerColor = fundo, contentColor = frente),
        contentPadding = PaddingValues(4.dp)
    ) {
        Text(text = texto, fontSize = 20.sp, maxLines = 1)
    }
}

/** Ultimos calculos confirmados com a tecla de igual. */
@Composable
private fun Historico(historico: List<String>, onLimpar: () -> Unit) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = stringResource(R.string.historico),
            style = MaterialTheme.typography.titleMedium,
            color = MaterialTheme.colorScheme.onBackground,
            modifier = Modifier.weight(1f)
        )
        if (historico.isNotEmpty()) {
            TextButton(onClick = onLimpar) { Text(stringResource(R.string.limpar)) }
        }
    }

    if (historico.isEmpty()) {
        Text(
            text = stringResource(R.string.sem_historico),
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
    } else {
        // Sao no maximo dez linhas e a tela ja rola: uma Column simples evita
        // rolagem dentro de rolagem.
        Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
            historico.forEach { linha ->
                Text(
                    text = linha,
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurface
                )
            }
        }
    }
}

@Preview(showBackground = true, name = "Calculadora")
@Composable
private fun PreviewCalculadora() {
    CalculadoraTheme(escuro = false) { CalculadoraScreen() }
}

@Preview(showBackground = true, name = "Calculadora escura")
@Composable
private fun PreviewCalculadoraEscura() {
    CalculadoraTheme(escuro = true) { CalculadoraScreen() }
}
