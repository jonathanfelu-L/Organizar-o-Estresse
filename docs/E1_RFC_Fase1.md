# Proposta RFC — Fase 1
**Estruturas de Dados I**
**Instituição:** [Nome da Instituição]
**Aluno:** Jonathan Ferreira Luna
**Professor:** [Nome do Professor]
**Disciplina:** Estruturas de Dados I

---

## Seção 1 — Nome do Sistema

**RotinaCerta — Sistema de Organização de Rotina Diária com Controle de Estresse**

---

## Seção 2 — Descrição do Problema

### 2.1 — Qual dor existe no mundo real?

Muitas pessoas terminam o dia exaustas sem entender o motivo. O problema não é a quantidade de horas trabalhadas — é a **composição** dessas horas. Uma agenda com reuniões seguidas por mais reuniões, sem pausas entre elas, gera um acúmulo de estresse que a pessoa só percebe quando já está esgotada. Não existe hoje uma ferramenta simples que mostre, antes do dia começar, o impacto acumulado de cada atividade no bem-estar do usuário.

### 2.2 — Quem enfrenta o problema?

- **Estudantes universitários** que precisam equilibrar aulas, estágios, trabalhos e vida pessoal
- **Trabalhadores** que acumulam reuniões e tarefas sem perceber o impacto no estresse
- **Qualquer pessoa** que sente que o dia foi pesado mas não consegue identificar o porquê

### 2.3 — Frequência e Impacto

O problema ocorre **diariamente**. Sem uma ferramenta de controle, o usuário não consegue identificar quais atividades estão causando mais estresse, não planeja pausas estratégicas e acaba o dia mais cansado do que precisaria. A longo prazo isso contribui para esgotamento, queda de produtividade e problemas de saúde.

### 2.4 — Por que vale a pena construir esse sistema?

Um sistema que permita ao usuário mapear suas 24 horas e visualizar o estresse acumulado antes do dia começar permite que ele reorganize suas atividades de forma consciente — colocando pausas nos momentos certos e identificando quais compromissos são os maiores vilões do seu bem-estar.

---

## Seção 3 — Descrição da Solução

### 3.1 — Visão Geral

O RotinaCerta é um sistema que permite ao usuário cadastrar todas as atividades das suas 24 horas, informando o nome, horário de início, duração e um nível de estresse de -10 (muito relaxante) a +10 (muito estressante). O sistema calcula automaticamente o estresse acumulado do dia e exibe uma avaliação geral. As atividades são ordenadas cronologicamente pelo Insertion Sort e organizadas em uma fila para processamento sequencial. Cada ação do usuário é registrada em um histórico que permite desfazer operações.

### 3.2 — Fluxo Principal

1. Usuário abre o sistema e é identificado pelo nome
2. Usuário cadastra uma atividade informando nome, hora de início, duração e nível de estresse
3. O sistema verifica se há conflito de horário com atividades já cadastradas
4. Se não houver conflito, a atividade é inserida na Lista encadeada
5. O Insertion Sort ordena automaticamente a lista por horário de início
6. A ação é registrada no topo da Pilha (histórico)
7. O usuário pode gerar a Fila do dia, que organiza todas as atividades em ordem FIFO com slots de descanso preenchidos automaticamente
8. O sistema exibe o estresse total e uma avaliação do dia
9. O usuário pode desfazer a última ação usando o Pop da Pilha

### 3.3 — Módulos Principais

| Módulo | Responsabilidade |
|--------|-----------------|
| Módulo de Agenda (Lista) | Armazena e ordena as atividades do dia |
| Módulo de Fila do Dia (Fila) | Organiza as atividades em ordem cronológica para processamento |
| Módulo de Histórico (Pilha) | Registra ações do usuário e permite desfazê-las |
| Módulo de Análise | Calcula o estresse total e gera o ranking de impacto |
| Módulo de Banco de Dados | Salva e carrega as atividades em arquivo de texto |

### 3.4 — Interação do Usuário

- Adicionar atividade com nome, horário, duração e nível de estresse
- Remover atividade da agenda
- Modificar o nível de estresse de uma atividade existente
- Visualizar a agenda ordenada por horário
- Gerar e visualizar a fila do dia com slots de descanso automáticos
- Desfazer a última ação realizada
- Carregar atividades de arquivo CSV
- Visualizar análise de estresse e ranking de impacto

---

## Seção 4 — Justificativa das Estruturas de Dados

### 4.1 — Lista Encadeada

**O que armazena:** todas as atividades cadastradas pelo usuário na agenda do dia.

**Por que usar lista:** a agenda diária precisa de inserção e remoção em qualquer posição, pois o usuário pode adicionar uma atividade de manhã, depois uma à noite e depois uma ao meio-dia. A lista encadeada permite isso sem realocação de memória, ao contrário de um array fixo. O tamanho máximo natural é 24 slots (uma atividade por hora), então o custo O(n) das operações é sempre pequeno.

**Operações:**
- Inserção no fim: O(n) — percorre até o último nó
- Remoção por posição: O(n) — percorre até a posição
- Busca por horário: O(n) — busca linear
- Ordenação (Insertion Sort): O(n²) — adequado para n ≤ 24

**Exemplo concreto:** o usuário cadastra "Trabalho às 09:00" e depois "Academia às 07:00". A lista insere os dois e o Insertion Sort os reordena para que a Academia apareça antes do Trabalho na agenda.

### 4.2 — Fila (FIFO — First In, First Out)

**O que é enfileirado:** as atividades do dia em ordem cronológica, incluindo slots de descanso preenchidos automaticamente nos horários vazios.

**Por que FIFO:** a sequência do dia é naturalmente FIFO — a primeira atividade da manhã é a primeira a ser "vivida". Processar as atividades na ordem em que acontecem é exatamente o comportamento de uma fila: a que entrou primeiro (mais cedo no dia) é a primeira a sair (a ser processada).

**Operações:**
- Enqueue: O(1) — insere no fundo, ponteiro direto
- Dequeue: O(1) — remove da frente, ponteiro direto
- Ver frente (peek): O(1) — consulta sem remover

**Exemplo concreto:** o usuário tem Dormir (00:00), Academia (07:00) e Trabalho (09:00). A fila é gerada nessa ordem. Ao processar, Dormir sai primeiro (dequeue), depois Academia, depois Trabalho — respeitando a ordem cronológica do dia.

### 4.3 — Pilha (LIFO — Last In, First Out)

**O que é empilhado:** a descrição de cada ação realizada pelo usuário (adicionar, remover, modificar atividade).

**Por que LIFO:** o comportamento de "desfazer" (Ctrl+Z) é naturalmente LIFO — a última ação feita é a primeira a ser desfeita. Se o usuário adicionou Academia, depois Trabalho e quer desfazer, ele quer desfazer o Trabalho (o mais recente), não a Academia.

**Operações:**
- Push: O(1) — insere no topo
- Pop: O(1) — remove do topo
- Peek: O(1) — consulta o topo sem remover

**Exemplo concreto:** usuário faz 3 ações: (1) adiciona Dormir, (2) adiciona Trabalho, (3) remove Dormir. A pilha tem no topo "REMOVEU: Dormir". Ao desfazer, o Pop retira essa ação do topo — o sistema informa que a última ação foi remover Dormir.

---

## Seção 5 — Análise de Complexidade

### 5.1 — Operações Custosas

1. **Ordenação da agenda (Insertion Sort)** — executada toda vez que uma atividade é adicionada
2. **Verificação de conflito de horário** — executada antes de cada inserção
3. **Geração da fila do dia** — percorre toda a lista para enfileirar as atividades

### 5.2 — Análise de Escalabilidade

| Operação | Estrutura | Big-O | Justificativa | Impacto |
|----------|-----------|-------|---------------|---------|
| Insertion Sort | Lista | O(n²) | Dois loops aninhados sobre a lista | Baixo — n ≤ 24 sempre |
| Verificação de conflito | Lista | O(n) | Percorre todos os nós comparando horários | Baixo — n ≤ 24 sempre |
| Geração da fila do dia | Lista + Fila | O(n) | Percorre a lista e enfileira cada elemento | Baixo — n ≤ 24 sempre |
| Push/Pop do histórico | Pilha | O(1) | Acesso direto ao topo | Nenhum |
| Enqueue/Dequeue | Fila | O(1) | Ponteiros diretos para frente e fundo | Nenhum |

### 5.3 — Dashboard de Desempenho

Um painel de monitoramento exibiria:
- **Tamanho da lista:** número de atividades cadastradas (máximo: 24)
- **Tamanho da fila:** número de slots na fila do dia incluindo descansos
- **Tamanho da pilha:** número de ações no histórico
- **Estresse total do dia:** soma de (nível × duração) de todas as atividades
- **Atividade mais estressante:** topo do ranking de impacto
- **Horas de descanso:** total de horas não ocupadas por atividades

---

## Seção 6 — Conclusão

### 6.1 — Resumo

O RotinaCerta é um sistema de organização de rotina diária que usa três estruturas de dados — Lista Encadeada, Fila FIFO e Pilha LIFO — de forma integrada e coerente com o domínio do problema. A lista armazena e ordena as atividades via Insertion Sort, a fila representa a sequência cronológica do dia e a pilha registra o histórico de ações para desfazimento. O ponto central do sistema é o cálculo do estresse acumulado, que permite ao usuário visualizar o impacto de sua rotina antes que o dia comece.

### 6.2 — Três Principais Benefícios

1. **Consciência do estresse:** o usuário vê o impacto acumulado de toda a rotina, não só de atividades isoladas
2. **Organização automática:** o Insertion Sort elimina a necessidade de cadastrar as atividades em ordem — o sistema ordena sozinho
3. **Flexibilidade:** a lista encadeada permite adicionar, remover e modificar atividades a qualquer momento sem limitação de tamanho fixo

### 6.3 — Viabilidade Técnica

**Dificuldade: Média**

As estruturas de dados (Lista, Fila, Pilha) são implementadas do zero em Java sem bibliotecas externas, o que exige entendimento de ponteiros e nós encadeados. A interface web com HTML/JavaScript adiciona complexidade mas permite uma visualização muito mais clara das estruturas. O maior desafio foi conectar o servidor Java com o navegador via HTTP, que é um conceito além do escopo da disciplina mas que foi implementado para tornar o sistema mais acessível.

---

## Seção 7 — Checklist de Autoavaliação

- [x] Capa preenchida (nome, professor, disciplina)
- [x] Problema descrito nas seções 2.1 a 2.4
- [x] Solução descrita nas seções 3.1 a 3.4
- [x] Lista justificada com operações e exemplo concreto
- [x] Fila justificada com FIFO e exemplo concreto
- [x] Pilha justificada com LIFO e exemplo concreto
- [x] Tabela de complexidade preenchida (seção 5.2)
- [x] Dashboard de desempenho descrito (seção 5.3)
- [x] Conclusão com resumo, benefícios e viabilidade (seção 6)

---

## Seção 8 — Referências

- CORMEN, T. H. et al. *Introdução a Algoritmos*. 3. ed. Rio de Janeiro: Elsevier, 2012.
- GOODRICH, M. T.; TAMASSIA, R. *Estruturas de Dados e Algoritmos em Java*. 5. ed. Porto Alegre: Bookman, 2013.
- SEDGEWICK, R.; WAYNE, K. *Algorithms*. 4. ed. Addison-Wesley, 2011.
