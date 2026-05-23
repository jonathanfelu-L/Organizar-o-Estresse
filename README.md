# 📅 RotinaCerta

> **Organize suas 24 horas. Controle seu estresse. Viva melhor.**

Sistema de organização de rotina diária com controle de estresse, desenvolvido em Java com interface web. O usuário cadastra suas atividades, informa o quanto cada uma causa ou alivia estresse, e o sistema calcula o impacto acumulado do dia.

---

## 👤 Identificação

| Campo | Informação |
|-------|-----------|
| **Aluno** | Jonathan Ferreira Luna |
| **Grupo** | 02 |
| **Disciplina** | Estruturas de Dados I |
| **Repositório** | https://github.com/jonathanfelu-L/Organizar-o-Estresse |
| **Site** | https://jonathanfelu-l.github.io/Organizar-o-Estresse/web/index.html |

---

## 🧩 Estruturas de Dados Utilizadas

Este projeto implementa **três estruturas de dados do zero**, sem usar bibliotecas prontas do Java.

### 📋 Lista Encadeada — Agenda do Dia
Cada atividade é um **nó** que aponta para o próximo. A lista não tem tamanho fixo — cresce conforme o usuário adiciona atividades. O **Insertion Sort** percorre a lista e mantém as atividades em ordem cronológica automaticamente.

```
[Dormir 00:00] → [Academia 07:00] → [Trabalho 09:00] → null
```

| Operação | Complexidade |
|----------|-------------|
| Inserção | O(n) |
| Remoção | O(n) |
| Busca | O(n) |
| Ordenação (Insertion Sort) | O(n²) |

---

### ↪ Fila (FIFO) — Fila do Dia
**Primeiro que entra, primeiro que sai** — igual fila de banco. As atividades são enfileiradas da manhã até a noite. Slots vazios são preenchidos automaticamente com "Descanso". O usuário processa as atividades na ordem em que elas acontecem no dia.

```
FRENTE → [Dormir] → [Academia] → [Trabalho] → [Descanso] → FUNDO
           ↑ próxima a ser processada
```

| Operação | Complexidade |
|----------|-------------|
| Enqueue (adicionar) | O(1) |
| Dequeue (processar) | O(1) |
| Ver próxima (peek) | O(1) |

---

### 🗂 Pilha (LIFO) — Histórico de Ações
**Último que entra, primeiro que sai** — igual Ctrl+Z. Cada ação do usuário (adicionar, remover, modificar atividade) é **empilhada**. Ao desfazer, a ação mais recente (topo da pilha) é retirada primeiro.

```
TOPO → [ADICIONOU: Trabalho às 09:00]
       [REMOVEU: Reunião às 14:00]
       [ADICIONOU: Academia às 07:00]  ← mais antiga
```

| Operação | Complexidade |
|----------|-------------|
| Push (empilhar) | O(1) |
| Pop (desempilhar / desfazer) | O(1) |
| Peek (ver topo) | O(1) |

---

## 📊 Critério de Estresse

O nível de estresse vai de **-10** (muito relaxante) a **+10** (muito estressante). O estresse total do dia é calculado como a soma de `nível × duração` de cada atividade.

| Nível | Classificação | Exemplos |
|-------|--------------|---------|
| -10 | Muito relaxante | Meditação, spa |
| -4 | Relaxante | Dormir (7-8h), ioga |
| -2 | Levemente relaxante | Academia, caminhada |
| -1 | Descanso | Refeição, pausa |
| 0 | Neutro | Higiene, trajeto |
| +2 | Leve | Estudo, trabalho leve |
| +5 | Moderado | Trabalho intenso, prova |
| +7 | Estressante | Apresentação, reunião |
| +9 | Muito estressante | Deadline urgente |
| +10 | Extremo | Crise, emergência |

---

## 🗂 Estrutura do Projeto

```
Organizar-o-Estresse/
├── src/
│   ├── Estruturas.java   ← Lista, Fila, Pilha e Insertion Sort
│   ├── Banco.java        ← banco de dados em arquivo .txt
│   └── Servidor.java     ← servidor HTTP + API REST (porta 8080)
├── web/
│   └── index.html        ← interface completa no navegador
├── data/
│   └── rotinas.txt       ← banco de dados (criado automaticamente)
├── docs/
│   ├── E1_RFC_Fase1.md   ← proposta RFC — Fase 1
│   └── CRONOLOGIA.md     ← cronologia do desenvolvimento
├── doc/
│   └── E2_Grupo2_Design_Tecnico.md  ← design técnico e MVP
├── .gitignore
└── README.md
```

---

## ⚙️ Como Executar

### Opção 1 — Navegador direto (sem instalar nada)

Baixe o repositório e abra o arquivo:
```
web/index.html
```
Clique duplo no arquivo ou arraste para qualquer navegador moderno. Os dados ficam salvos no navegador.

**Ou acesse direto pelo link:**
```
https://jonathanfelu-l.github.io/Organizar-o-Estresse/web/index.html
```

---

### Opção 2 — Com servidor Java (salva os dados permanentemente)

Requer **Java 11** ou superior instalado.

**1. Clone o repositório:**
```bash
git clone https://github.com/jonathanfelu-L/Organizar-o-Estresse.git
cd Organizar-o-Estresse
```

**2. Compile os arquivos Java:**
```bash
javac -d out src/Estruturas.java src/Banco.java src/Servidor.java
```

**3. Execute o servidor:**
```bash
java -cp out Servidor
```

**4. Acesse no navegador:**
```
http://localhost:8080
```

Os dados são salvos automaticamente em `data/rotinas.txt`.

---

## 🔧 Algoritmo de Ordenação

O projeto usa **Insertion Sort** para ordenar as atividades por horário de início.

**Como funciona:** funciona como organizar cartas na mão. Você pega uma carta, compara com as já ordenadas à esquerda, e a encaixa no lugar certo deslocando as outras.

```java
void ordenarPorHorario() {
    No externo = cabeca.proximo;
    while (externo != null) {
        Atividade chave = (Atividade) externo.dado;
        No interno = cabeca;
        while (interno != externo
                && ((Atividade) interno.dado).horaInicio <= chave.horaInicio) {
            interno = interno.proximo;
        }
        // desloca dados para encaixar a chave na posição correta
        ...
        externo = externo.proximo;
    }
}
```

| Caso | Complexidade |
|------|-------------|
| Melhor caso (já ordenado) | O(n) |
| Caso médio | O(n²) |
| Pior caso (invertido) | O(n²) |
| Espaço extra | O(1) |

Para uma agenda diária (máximo 24 slots), O(n²) com n≤24 é eficiente o suficiente.

---

## 📁 Documentação

- [Proposta RFC — Fase 1](docs/E1_RFC_Fase1.md)
- [Design Técnico e MVP — E2](doc/E2_Grupo2_Design_Tecnico.md)
- [Cronologia do Desenvolvimento](docs/CRONOLOGIA.md)
