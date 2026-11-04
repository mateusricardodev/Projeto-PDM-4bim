# 💼 Projeto 01 — Cartão de Visita Digital

Aplicativo de apresentação pessoal feito com **Kotlin** e **Jetpack Compose**,
primeiro projeto da trilha do 4º bimestre.

## ✅ Funcionalidades obrigatórias

| Requisito | Como foi atendido |
|---|---|
| Exibir fotografia ou avatar | Avatar circular com as iniciais do nome, desenhado pelo Compose com degradê — sem depender de arquivo de imagem |
| Mostrar nome completo | `Text` com estilo `displaySmall` da tipografia personalizada |
| Apresentar profissão e descrição | Profissão em destaque na cor primária e descrição abaixo |
| Exibir e-mail e telefone | Linhas com ícone, clicáveis: abrem o app de e-mail e o discador |
| Criar botões para redes sociais | `OutlinedButton` para GitHub, LinkedIn e Instagram, que abrem o link |
| Aplicar cores e tipografia personalizadas | `Theme.kt` define esquemas claro/escuro próprios e uma `Typography` customizada |
| Adaptar o layout a diferentes tamanhos de tela | `BoxWithConstraints`: abaixo de 600 dp o avatar fica acima do texto; acima disso, lado a lado |

## 🏆 Desafios extras

- **Alternância entre tema claro e escuro** — `Switch` no topo da tela. A escolha
  sobrevive à rotação (`rememberSaveable`) e, na primeira abertura, segue o tema do sistema.
- **Botão de compartilhamento** — monta um texto com os dados do cartão e envia
  por `Intent.ACTION_SEND`.

## 🧠 Conceitos trabalhados

`@Composable` • `Text` • `Column` • `Row` • `Box` • `Button` • `Modifier` •
`BoxWithConstraints` • `rememberSaveable` • Material Design 3 • Intents

## 📂 Estrutura

```text
01-cartao-visita/
├── app/src/main/java/com/pdm/cartaovisita/
│   ├── MainActivity.kt      # Activity e estado do tema
│   ├── Perfil.kt            # dados e regras — sem Android, testável na JVM
│   ├── Acoes.kt             # Intents de e-mail, telefone, link e compartilhar
│   ├── Avatar.kt            # avatar circular com iniciais
│   ├── CartaoScreen.kt      # interface do cartão
│   └── Theme.kt             # cores e tipografia (Material 3)
├── app/src/test/java/com/pdm/cartaovisita/
│   └── PerfilTest.kt        # 15 testes das funções puras
└── app/src/main/res/        # strings, temas e ícone (vetor)
```

A interface e as regras de negócio ficam separadas: `Perfil.kt` não importa
nenhuma classe do Android, o que permite testar a lógica sem emulador.

## ✏️ Personalizando o cartão

Todos os dados exibidos estão em um único lugar — `PERFIL_PADRAO`, em `Perfil.kt`:

```kotlin
val PERFIL_PADRAO = Perfil(
    nome = "Mateus Ricardo",
    profissao = "Desenvolvedor Android",
    descricao = "...",
    email = "seu.email@exemplo.com",   // <- troque
    telefone = "43999998888",          // <- troque (só dígitos)
    cidade = "Parana, Brasil",
    redes = listOf(/* ... */)
)
```

> O e-mail, o telefone e os perfis de LinkedIn e Instagram estão com **valores de
> exemplo**. Substitua pelos seus antes de apresentar o projeto.

## 🧪 Testes

### Testes de unidade

15 testes em `PerfilTest.kt`, executados na JVM:

```bash
./gradlew test
```

| Grupo | O que é verificado |
|---|---|
| Iniciais do avatar | Primeiro e último nome; preposições ignoradas; nome único; espaços extras; nome vazio |
| Telefone | Formatação de celular e fixo; número fora do padrão; extração de dígitos; validação |
| E-mail | Aceita endereço bem formado e recusa mal formado |
| Compartilhamento | Inclui nome, profissão e redes; omite contato inválido; sem linhas vazias nas pontas |

**Resultado: 15/15 aprovados.**

### Compilação

```bash
./gradlew assembleDebug
```

**BUILD SUCCESSFUL** — APK de debug gerado, sem warnings do compilador Kotlin.

## 🛠️ Configuração

| Item | Versão |
|---|---|
| Android Gradle Plugin | 8.7.3 |
| Kotlin | 2.0.21 |
| Compose BOM | 2024.12.01 |
| minSdk / targetSdk | 24 / 35 |
