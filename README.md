# Projeto PDM — 4º Bimestre

Trilha de projetos práticos de desenvolvimento Android em **Kotlin** e **Jetpack Compose**,
seguindo as especificações do repositório de referência
[perabru/ProjetoPDM-4Bimestre](https://github.com/perabru/ProjetoPDM-4Bimestre).

Cada projeto fica em sua própria pasta e é um projeto Android independente,
com seu próprio Gradle. Abra a pasta do projeto no Android Studio, não a raiz.

## Projetos

| # | Projeto | Pasta | Status |
|:-:|---------|-------|:------:|
| 01 | 💼 Cartão de Visita Digital | [`01-cartao-visita/`](01-cartao-visita/) | ✅ Concluído |
| 02 | 🧮 Calculadora Inteligente | `02-calculadora/` | ⬜ |
| 03 | 🌡️ Conversor de Temperatura | `03-conversor/` | ⬜ |
| 04 | 🧠 Quiz Interativo | `04-quiz/` | ⬜ |
| 05 | ⏱️ Temporizador Pomodoro | `05-pomodoro/` | ⬜ |
| 06 | ✅ Gerenciador de Tarefas | `06-tarefas/` | ⬜ |
| 07 | 📦 Sistema de Estoque | `07-estoque/` | ⬜ |
| 08 | ☁️ Aplicativo de Clima | `08-clima/` | ⬜ |
| 09 | 💰 Controle Financeiro | `09-financas/` | ⬜ |
| 10 | 📝 Corretor de Provas | `10-corretor-provas/` | ⬜ |

## Como executar

1. Abra o **Android Studio**.
2. **Open** → selecione a pasta do projeto desejado (ex.: `01-cartao-visita`).
3. Aguarde o Gradle Sync.
4. Conecte um aparelho ou inicie um emulador.
5. **Run ▶**.

> ⚠️ Em Windows, use um caminho **sem acentos** (ex.: `C:\AndroidProjects\`).
> O plugin do Android recusa caminhos com caracteres não-ASCII.

Para rodar apenas os testes de lógica, sem emulador:

```bash
cd 01-cartao-visita
./gradlew test
```

## Autor

**Mateus Ricardo** — [@mateusricardodev](https://github.com/mateusricardodev)
