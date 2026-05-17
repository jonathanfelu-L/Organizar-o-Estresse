# RotinaCerta 📅

**Gerencie suas 24 horas e controle seu estresse diário.**

Organize suas atividades, veja o impacto de cada uma no seu estresse e deixe o sistema montar a fila do dia automaticamente.

> Projeto da disciplina **Estruturas de Dados I** — Grupo 2  
> Integrante: Jonathan Ferreira Luna

---

## Como Executar

### Opção 1 — Com servidor Java (salva os dados)

Requer **Java 11** ou superior.

```bash
# Clone o projeto
git clone https://github.com/JonathanFLuna/rotinacerta.git
cd rotinacerta

# Compile
javac -d out src/Estruturas.java src/Banco.java src/Servidor.java

# Execute
java -cp out Servidor

# Acesse no navegador
http://localhost:8080
```

### Opção 2 — Direto no navegador

Abra o arquivo `web/index.html` em qualquer navegador moderno. Nenhuma instalação necessária.

---

## Estruturas de Dados Utilizadas

| Estrutura | Uso |
|-----------|-----|
| Lista Encadeada | Agenda do dia — ordenada pelo Insertion Sort |
| Fila (FIFO) | Fila do dia — atividades em ordem cronológica |
| Pilha (LIFO) | Histórico de ações — desfazer (Ctrl+Z) |

## Algoritmo de Ordenação

**Insertion Sort** — O(n²) — organiza as atividades por horário de início.

---

## Estrutura do Projeto

```
rotinacerta/
├── src/
│   ├── Estruturas.java   ← estruturas de dados + insertion sort
│   ├── Banco.java        ← banco de dados em arquivo .txt
│   └── Servidor.java     ← servidor HTTP + API REST
├── web/
│   └── index.html        ← interface principal
├── data/                 ← banco de dados (criado automaticamente)
├── doc/
│   └── E2_Grupo2_Design_Tecnico.md
└── README.md
```
