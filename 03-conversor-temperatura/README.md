# 🌡️ Projeto 03 — Conversor de Temperatura

Converte temperaturas entre **Celsius**, **Fahrenheit** e **Kelvin**, em Kotlin
com Jetpack Compose.

## ✅ Funcionalidades obrigatórias

| Requisito | Como foi atendido |
|---|---|
| Receber a temperatura informada | `OutlinedTextField` com teclado decimal e botão `+/-` para o sinal |
| Selecionar a unidade de origem | `DropdownMenu` com as três escalas |
| Selecionar a unidade de destino | `DropdownMenu` com as três escalas |
| Calcular automaticamente | O resultado é recalculado a cada tecla — não há botão "converter" |
| Exibir o valor convertido | Cartão em destaque com o valor de entrada e o convertido |
| Validar os dados digitados | Aceita vírgula ou ponto; recusa texto, vazio, infinito e `NaN` |
| Permitir limpar os campos | Botão **Limpar** |

**Limite físico:** valores abaixo do zero absoluto são recusados, com a mensagem
indicando o limite da escala escolhida (−273,15 °C, −459,67 °F ou 0 K).

## 🏆 Desafios extras

- **Histórico** das últimas 10 conversões, com botão para apagar.
- **Inverter unidades** — troca origem e destino sem perder o valor digitado.

## 📐 Fórmulas

Todas as conversões passam por Celsius como escala intermediária, o que evita
escrever as seis fórmulas possíveis:

```kotlin
F = C × 9/5 + 32          C = (F − 32) × 5/9
K = C + 273,15            C = K − 273,15
```

## 🧠 Conceitos trabalhados

`OutlinedTextField` • `DropdownMenu` • `remember` • `rememberSaveable` •
`mutableStateListOf` • condicionais • validação • `sealed interface`

## 📂 Estrutura

```text
03-conversor-temperatura/
├── app/src/main/java/com/pdm/conversortemperatura/
│   ├── MainActivity.kt       # Activity
│   ├── Conversao.kt          # fórmulas e validação — sem Android, testável
│   ├── ConversorScreen.kt    # interface
│   └── Theme.kt              # cores e tipografia (Material 3)
└── app/src/test/java/com/pdm/conversortemperatura/
    └── ConversaoTest.kt      # 18 testes
```

## 🧪 Testes

```bash
./gradlew test
```

| Grupo | O que é verificado |
|---|---|
| Fórmulas | 0 °C = 32 °F, 100 °C = 212 °F, 0 °C = 273,15 K, 0 K = −459,67 °F, −40 igual nas duas escalas |
| Ida e volta | Converter e voltar devolve o valor original, nas 9 combinações |
| Leitura do número | Vírgula e ponto; recusa texto, vazio, `Infinity` e `NaN` |
| Validação | Campo vazio não é erro; recusa abaixo do zero absoluto nas 3 escalas; aceita o zero absoluto exato |
| Formatação | Duas casas decimais; nunca exibe "−0,00" |

**Resultado: 18/18 aprovados.** Build: `./gradlew assembleDebug` → **BUILD SUCCESSFUL**.
