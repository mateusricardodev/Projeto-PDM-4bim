# 🧮 Projeto 02 — Calculadora Inteligente

Calculadora com as quatro operações, feita em Kotlin com Jetpack Compose.

## ✅ Funcionalidades obrigatórias

| Requisito | Como foi atendido |
|---|---|
| Adição, subtração, multiplicação e divisão | Teclado com as quatro operações; encadeia cálculos sem apertar `=` |
| Exibir resultados em um visor | Visor com o número em destaque e o cálculo pendente acima |
| Limpar os valores digitados | Tecla **C** |
| Corrigir números digitados incorretamente | Tecla **⌫**, que apaga um caractere por vez |
| Tratar divisões por zero | A divisão não é executada; o visor mostra "Não é possível dividir por zero" |

Funciona com inteiros e decimais. O resultado é arredondado para 8 casas e os
zeros à direita são removidos, então `0,1 + 0,2` mostra **0,3** em vez do
`0,30000000000000004` que o ponto flutuante produziria.

## 🏆 Desafios extras

- **Porcentagem** — isolada divide por 100; em soma e subtração calcula sobre o
  primeiro operando, como nas calculadoras comuns (`200 + 10 %` = 220).
- **Raiz quadrada** — recusa número negativo, com mensagem no visor.
- **Histórico** dos últimos 10 cálculos, com botão para apagar.

## 🧠 Conceitos trabalhados

`Button` • `Text` • `mutableStateOf` • `rememberSaveable` (com `Saver`
personalizado) • `mutableStateListOf` • funções Kotlin • operadores matemáticos •
`data class` imutável

## 📂 Estrutura

```text
02-calculadora/
├── app/src/main/java/com/pdm/calculadora/
│   ├── MainActivity.kt         # Activity
│   ├── Calculadora.kt          # estado e operações — sem Android, testável
│   ├── CalculadoraScreen.kt    # visor, teclado e histórico
│   └── Theme.kt                # cores e tipografia (Material 3)
└── app/src/test/java/com/pdm/calculadora/
    └── CalculadoraTest.kt      # 28 testes
```

O `Estado` é **imutável**: cada tecla produz um estado novo em vez de alterar o
anterior. Isso torna cada passo testável isoladamente e evita que o visor e os
operandos saiam de sincronia.

## 🧪 Testes

```bash
./gradlew test
```

| Grupo | O que é verificado |
|---|---|
| Operações | As quatro operações, com decimais e com negativos |
| Divisão por zero | Gera erro; qualquer dígito depois recomeça; `0 ÷ 5` continua válido |
| Digitação | Zeros à esquerda não se acumulam; uma única vírgula; vírgula no início vira `0,` |
| Correção | `⌫` apaga um caractere; apagar tudo deixa `0`; `C` zera a operação pendente |
| Encadeamento | `2 + 3 +` já mostra 5; trocar de operação antes de digitar não calcula |
| Extras | Porcentagem isolada e na soma; raiz quadrada; raiz de negativo gera erro |
| Formatação | Inteiro sem casas decimais; fração arredondada |

**Resultado: 28/28 aprovados.** Build: `./gradlew assembleDebug` → **BUILD SUCCESSFUL**.
