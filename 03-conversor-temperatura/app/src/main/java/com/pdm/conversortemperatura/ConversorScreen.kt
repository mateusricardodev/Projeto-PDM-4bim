package com.pdm.conversortemperatura

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
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
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.foundation.text.KeyboardOptions

/**
 * Tela do conversor.
 *
 * O resultado e recalculado a cada tecla digitada: nao existe botao
 * "converter". A validacao vem toda de [avaliar], em Conversao.kt.
 */
@Composable
fun ConversorScreen(modifier: Modifier = Modifier) {
    var texto by rememberSaveable { mutableStateOf("") }
    var unidadeOrigem by rememberSaveable { mutableStateOf(Unidade.CELSIUS) }
    var unidadeDestino by rememberSaveable { mutableStateOf(Unidade.FAHRENHEIT) }

    // Historico das conversoes confirmadas pelo usuario.
    val historico = remember { mutableStateListOf<String>() }

    val resultado = avaliar(texto, unidadeOrigem, unidadeDestino)

    Column(
        modifier = modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(20.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Column(modifier = Modifier.widthIn(max = 520.dp).fillMaxWidth()) {

            Text(
                text = stringResource(R.string.titulo),
                style = MaterialTheme.typography.displaySmall,
                color = MaterialTheme.colorScheme.onBackground
            )

            Spacer(Modifier.height(20.dp))

            CampoTemperatura(
                texto = texto,
                erro = (resultado as? Resultado.Invalido)?.erro,
                unidadeOrigem = unidadeOrigem,
                onTextoMudou = { texto = it }
            )

            Spacer(Modifier.height(16.dp))

            // Seletores de unidade, lado a lado.
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                SeletorUnidade(
                    rotulo = stringResource(R.string.de),
                    selecionada = unidadeOrigem,
                    onSelecionar = { unidadeOrigem = it },
                    modifier = Modifier.weight(1f)
                )
                SeletorUnidade(
                    rotulo = stringResource(R.string.para),
                    selecionada = unidadeDestino,
                    onSelecionar = { unidadeDestino = it },
                    modifier = Modifier.weight(1f)
                )
            }

            Spacer(Modifier.height(12.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                OutlinedButton(
                    onClick = {
                        // Inverte origem e destino sem perder o valor digitado.
                        val anterior = unidadeOrigem
                        unidadeOrigem = unidadeDestino
                        unidadeDestino = anterior
                    },
                    modifier = Modifier.weight(1f)
                ) {
                    Text(stringResource(R.string.inverter))
                }
                OutlinedButton(
                    onClick = { texto = "" },
                    enabled = texto.isNotEmpty(),
                    modifier = Modifier.weight(1f)
                ) {
                    Icon(Icons.Filled.Clear, contentDescription = null, modifier = Modifier.size(18.dp))
                    Spacer(Modifier.size(6.dp))
                    Text(stringResource(R.string.limpar))
                }
            }

            Spacer(Modifier.height(20.dp))

            PainelResultado(
                resultado = resultado,
                unidadeOrigem = unidadeOrigem,
                unidadeDestino = unidadeDestino,
                onGuardar = { linha ->
                    // Evita repetir a mesma linha no topo do historico.
                    if (historico.firstOrNull() != linha) historico.add(0, linha)
                    // Guarda apenas as dez conversoes mais recentes.
                    while (historico.size > 10) historico.removeAt(historico.lastIndex)
                }
            )

            Spacer(Modifier.height(24.dp))

            Historico(historico = historico, onLimpar = { historico.clear() })
        }
    }
}

/** Campo de entrada, com mensagem de erro e botao de trocar o sinal. */
@Composable
private fun CampoTemperatura(
    texto: String,
    erro: Erro?,
    unidadeOrigem: Unidade,
    onTextoMudou: (String) -> Unit
) {
    val mensagemErro = when (erro) {
        Erro.NAO_E_NUMERO -> stringResource(R.string.erro_numero)
        Erro.ABAIXO_DO_ZERO_ABSOLUTO -> stringResource(
            R.string.erro_zero_absoluto,
            formatar(zeroAbsoluto(unidadeOrigem)),
            unidadeOrigem.simbolo
        )
        null -> null
    }

    OutlinedTextField(
        value = texto,
        onValueChange = onTextoMudou,
        label = { Text(stringResource(R.string.temperatura)) },
        suffix = { Text(unidadeOrigem.simbolo) },
        singleLine = true,
        isError = erro != null,
        supportingText = mensagemErro?.let { { Text(it) } },
        // O teclado decimal nao traz o sinal de menos; dai o botao ao lado.
        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
        trailingIcon = {
            TextButton(
                onClick = {
                    onTextoMudou(
                        if (texto.startsWith("-")) texto.removePrefix("-") else "-$texto"
                    )
                }
            ) {
                Text("+/-", fontWeight = FontWeight.Bold)
            }
        },
        modifier = Modifier.fillMaxWidth()
    )
}

/** Lista suspensa com as tres escalas. */
@Composable
private fun SeletorUnidade(
    rotulo: String,
    selecionada: Unidade,
    onSelecionar: (Unidade) -> Unit,
    modifier: Modifier = Modifier
) {
    var aberto by remember { mutableStateOf(false) }

    Column(modifier = modifier) {
        Text(
            text = rotulo,
            style = MaterialTheme.typography.labelLarge,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
        Spacer(Modifier.height(4.dp))

        Box {
            OutlinedButton(
                onClick = { aberto = true },
                modifier = Modifier.fillMaxWidth()
            ) {
                Text(text = selecionada.rotulo, maxLines = 1)
                Spacer(Modifier.size(4.dp))
                Icon(Icons.Filled.ArrowDropDown, contentDescription = null)
            }

            DropdownMenu(expanded = aberto, onDismissRequest = { aberto = false }) {
                Unidade.entries.forEach { unidade ->
                    DropdownMenuItem(
                        text = { Text("${unidade.rotulo} (${unidade.simbolo})") },
                        onClick = {
                            onSelecionar(unidade)
                            aberto = false
                        }
                    )
                }
            }
        }
    }
}

/** Mostra o valor convertido, ou uma dica quando ainda nao ha o que mostrar. */
@Composable
private fun PainelResultado(
    resultado: Resultado,
    unidadeOrigem: Unidade,
    unidadeDestino: Unidade,
    onGuardar: (String) -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.primaryContainer
        )
    ) {
        Column(
            modifier = Modifier.fillMaxWidth().padding(20.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            when (resultado) {
                is Resultado.Valido -> {
                    Text(
                        text = "${formatar(resultado.entrada)} ${unidadeOrigem.simbolo}",
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onPrimaryContainer
                    )
                    Spacer(Modifier.height(6.dp))
                    Text(
                        text = "${formatar(resultado.saida)} ${unidadeDestino.simbolo}",
                        style = MaterialTheme.typography.displaySmall,
                        color = MaterialTheme.colorScheme.onPrimaryContainer
                    )
                    Spacer(Modifier.height(8.dp))
                    TextButton(
                        onClick = {
                            onGuardar(
                                descreverConversao(
                                    resultado.entrada, unidadeOrigem,
                                    resultado.saida, unidadeDestino
                                )
                            )
                        }
                    ) {
                        Text(stringResource(R.string.historico))
                    }
                }

                else -> Text(
                    text = stringResource(R.string.dica_inicial),
                    style = MaterialTheme.typography.bodyMedium,
                    textAlign = TextAlign.Center,
                    color = MaterialTheme.colorScheme.onPrimaryContainer
                )
            }
        }
    }
}

/** Lista das ultimas conversoes guardadas. */
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
            IconButton(onClick = onLimpar) {
                Icon(
                    Icons.Filled.Delete,
                    contentDescription = stringResource(R.string.limpar_historico)
                )
            }
        }
    }

    if (historico.isEmpty()) {
        Text(
            text = stringResource(R.string.sem_historico),
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
    } else {
        // A lista e curta (no maximo dez itens) e a tela ja rola, entao uma
        // Column simples evita rolagem dentro de rolagem.
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

@Preview(showBackground = true, name = "Conversor")
@Composable
private fun PreviewConversor() {
    ConversorTheme(escuro = false) { ConversorScreen() }
}

@Preview(showBackground = true, name = "Conversor escuro")
@Composable
private fun PreviewConversorEscuro() {
    ConversorTheme(escuro = true) { ConversorScreen() }
}
