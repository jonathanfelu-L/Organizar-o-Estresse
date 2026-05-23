# Cronologia do Desenvolvimento — RotinaCerta

Este arquivo registra as etapas do desenvolvimento do projeto em ordem cronológica.
Serve como documentação do processo, não faz parte do código do sistema.

---

## Etapa 1 — Definição do Problema e Estruturas

**O que foi decidido:**
- Tema: organização de rotina diária com controle de estresse
- Problema central: pessoas não conseguem visualizar o impacto acumulado das atividades do dia no seu bem-estar
- Solução: sistema que permite cadastrar 24 horas de atividades com nível de estresse e calcular o saldo do dia

**Estruturas escolhidas e justificativa:**
- **Lista encadeada** → agenda do dia (precisa de inserção/remoção em qualquer posição)
- **Fila FIFO** → fila do dia (atividades processadas na ordem em que ocorrem)
- **Pilha LIFO** → histórico de ações (desfazer = último que entrou, primeiro que sai)

---

## Etapa 2 — Implementação das Estruturas em Java

**Arquivos criados:**
- `src/Estruturas.java` — contém as classes: `Atividade`, `No`, `Lista`, `Fila`, `Pilha`

**Decisões técnicas:**
- Classe `No` genérica com `Object dado` para ser compartilhada pelas três estruturas
- Cast explícito `(Atividade)` na Lista e Fila, `(String)` na Pilha
- Insertion Sort implementado diretamente na Lista por dois motivos:
  1. O tamanho máximo é 24 (slots de 1 hora), então O(n²) é aceitável
  2. É o algoritmo mais simples de entender e visualizar para fins didáticos
- Método `paraJSON()` adicionado em cada estrutura para comunicação com o navegador

---

## Etapa 3 — Banco de Dados em Arquivo de Texto

**Arquivo criado:**
- `src/Banco.java` — lê e salva atividades em `data/rotinas.txt`

**Formato do arquivo:**
```
nome|horaInicio|duracao|nivelEstresse
```

**Decisão:** usar arquivo de texto simples em vez de banco de dados relacional (SQLite, MySQL) porque:
- Não requer instalação de nenhuma dependência externa
- Compatível com o Java puro da disciplina
- Suficiente para o volume de dados (máximo 24 atividades)

---

## Etapa 4 — Servidor HTTP e API REST

**Arquivo criado:**
- `src/Servidor.java` — servidor HTTP na porta 8080 usando `com.sun.net.httpserver`

**Rotas da API:**
| Método | Rota | O que faz |
|--------|------|-----------|
| GET | /api/atividades | Retorna todas as atividades em JSON |
| POST | /api/atividades | Adiciona nova atividade |
| DELETE | /api/atividades?pos=N | Remove atividade na posição N |
| PUT | /api/atividades?pos=N | Modifica estresse da posição N |
| GET | /api/fila | Retorna a fila do dia com descansos |
| GET | /api/estresse | Retorna estresse total e avaliação |
| GET | /api/historico | Retorna o histórico de ações (pilha) |

**Decisão:** usar `com.sun.net.httpserver` porque já vem no Java — não precisa instalar nada extra como Spring Boot ou Tomcat.

---

## Etapa 5 — Interface HTML

**Arquivo criado:**
- `web/index.html` — página completa com todas as seções

**Seções da página:**
1. **Hero** — nome do projeto, integrante e grupo
2. **O Problema** — descrição do problema que o sistema resolve
3. **Estruturas** — explicação de como Lista, Fila e Pilha são usadas
4. **Ferramentas** — bibliotecas, arquivos e algoritmo de ordenação
5. **Critérios de Estresse** — tabela de referência dos níveis
6. **Como Instalar** — passo a passo para rodar o projeto
7. **O Sistema** — interface interativa com as três estruturas funcionando

**Modo duplo:** a página funciona com ou sem o servidor Java:
- Com Java rodando → dados salvos no arquivo `data/rotinas.txt`
- Sem Java → dados salvos no `localStorage` do navegador

---

## Etapa 6 — Demonstração das Três Estruturas na Interface

**Problema identificado:** as três estruturas precisavam estar visíveis e compreensíveis na interface.

**Solução implementada:**
- **Aba "Agenda (Lista)"** → mostra a lista encadeada ordenada pelo Insertion Sort, com botão de remover em cada item
- **Aba "Fila do Dia (Fila FIFO)"** → gera e exibe a fila com numeração da frente para o fundo, slots de descanso automáticos
- **Aba "Histórico (Pilha LIFO)"** → mostra a pilha do topo para a base com botão de Pop (desfazer)
- **Aba "Análise"** → estresse total com barra visual e ranking

---

## Etapa 7 — GitHub e Documentação

**Repositório:** https://github.com/jonathanfelu-L/Organizar-o-Estresse

**Estrutura de commits:**
```
docs: adiciona README e gitignore
feat: adiciona arquivos iniciais do projeto
docs: adiciona documento E2 design tecnico
docs: adiciona RFC fase 1 e cronologia do desenvolvimento
feat: torna pilha e fila mais explícitas na interface
```

**Arquivos de documentação:**
- `README.md` — apresentação do projeto com instruções de execução
- `docs/E1_RFC_Fase1.md` — proposta RFC da Fase 1
- `doc/E2_Grupo2_Design_Tecnico.md` — design técnico e MVP
- `docs/CRONOLOGIA.md` — este arquivo

---

## Observações Finais

O projeto usa conceitos que vão além do escopo da disciplina (servidor HTTP, API REST, interface web), mas o núcleo do sistema — as três estruturas de dados implementadas do zero em Java — está completamente dentro do conteúdo de Estruturas de Dados I. As estruturas extras servem apenas para tornar as estruturas de dados mais visíveis e interativas para o usuário.
