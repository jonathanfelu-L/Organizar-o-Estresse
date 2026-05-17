// ──────────────────────────────────────────────
//  Estruturas.java
//  Contém: Atividade, No, Lista, Fila, Pilha
// ──────────────────────────────────────────────

// Representa uma atividade do dia do usuario
class Atividade {
    String nome;
    int horaInicio;
    int duracao;
    int nivelEstresse;

    Atividade(String nome, int horaInicio, int duracao, int nivelEstresse) {
        this.nome          = nome;
        this.horaInicio    = horaInicio;
        this.duracao       = duracao;
        this.nivelEstresse = nivelEstresse;
    }

    int getHoraFim()        { return horaInicio + duracao; }
    int getEstresseTotal()  { return nivelEstresse * duracao; }

    // Converte para linha de arquivo (banco de dados)
    String paraArquivo() {
        return nome + "|" + horaInicio + "|" + duracao + "|" + nivelEstresse;
    }

    // Le uma linha do arquivo e cria uma Atividade
    static Atividade doArquivo(String linha) {
        String[] p = linha.split("\\|");
        return new Atividade(
            p[0],
            Integer.parseInt(p[1]),
            Integer.parseInt(p[2]),
            Integer.parseInt(p[3])
        );
    }

    // Converte para JSON para enviar ao navegador
    String paraJSON() {
        return "{\"nome\":\"" + nome + "\""
             + ",\"horaInicio\":" + horaInicio
             + ",\"duracao\":" + duracao
             + ",\"nivelEstresse\":" + nivelEstresse
             + ",\"horaFim\":" + getHoraFim()
             + ",\"estresseTotal\":" + getEstresseTotal() + "}";
    }
}


// No generico usado pelas tres estruturas
class No {
    Object dado;
    No     proximo;

    No(Object dado) {
        this.dado    = dado;
        this.proximo = null;
    }
}


// ── LISTA ENCADEADA ──────────────────────────────
// Armazena as atividades da agenda do dia.
// Cada no aponta para o proximo -- nao usa array.
class Lista {
    No  cabeca;
    int tamanho;

    Lista() { cabeca = null; tamanho = 0; }

    // Insere no fim -- O(n)
    void inserirNoFim(Atividade a) {
        No novo = new No(a);
        if (cabeca == null) {
            cabeca = novo;
        } else {
            No atual = cabeca;
            while (atual.proximo != null) atual = atual.proximo;
            atual.proximo = novo;
        }
        tamanho++;
    }

    // Acessa por posicao -- O(n)
    Atividade obter(int pos) {
        No atual = cabeca;
        for (int i = 0; i < pos; i++) atual = atual.proximo;
        return (Atividade) atual.dado;
    }

    // Remove por posicao -- O(n)
    Atividade remover(int pos) {
        if (pos < 0 || pos >= tamanho)
            throw new IndexOutOfBoundsException("Posicao invalida: " + pos);
        Atividade removida;
        if (pos == 0) {
            removida = (Atividade) cabeca.dado;
            cabeca   = cabeca.proximo;
        } else {
            No ant = cabeca;
            for (int i = 0; i < pos - 1; i++) ant = ant.proximo;
            removida = (Atividade) ant.proximo.dado;
            ant.proximo = ant.proximo.proximo;
        }
        tamanho--;
        return removida;
    }

    // Verifica se o horario ja esta ocupado
    boolean temConflito(int horaInicio, int duracao) {
        int fim = horaInicio + duracao;
        No atual = cabeca;
        while (atual != null) {
            Atividade a = (Atividade) atual.dado;
            if (horaInicio < a.getHoraFim() && fim > a.horaInicio) return true;
            atual = atual.proximo;
        }
        return false;
    }

    // Soma estresse total do dia
    int calcularEstresseTotal() {
        int total = 0;
        No atual = cabeca;
        while (atual != null) {
            total += ((Atividade) atual.dado).getEstresseTotal();
            atual = atual.proximo;
        }
        return total;
    }

    // ── INSERTION SORT por horario -- O(n²) ──────
    // Funciona como organizar cartas na mao:
    // pega uma, acha o lugar certo entre as ja
    // ordenadas e encaixa la deslocando as outras.
    void ordenarPorHorario() {
        if (tamanho <= 1) return;
        No externo = cabeca.proximo;
        while (externo != null) {
            Atividade chave = (Atividade) externo.dado;
            No interno = cabeca;
            while (interno != externo
                    && ((Atividade) interno.dado).horaInicio <= chave.horaInicio) {
                interno = interno.proximo;
            }
            if (interno != externo) {
                No d = interno;
                while (d != externo) {
                    Object temp   = d.dado;
                    d.dado        = d.proximo.dado;
                    d.proximo.dado = temp;
                    d = d.proximo;
                }
            }
            externo = externo.proximo;
        }
    }

    // Converte toda a lista para JSON array
    String paraJSON() {
        StringBuilder sb = new StringBuilder("[");
        No atual = cabeca;
        while (atual != null) {
            sb.append(((Atividade) atual.dado).paraJSON());
            if (atual.proximo != null) sb.append(",");
            atual = atual.proximo;
        }
        sb.append("]");
        return sb.toString();
    }

    boolean estaVazia() { return tamanho == 0; }
}


// ── FILA (FIFO) ──────────────────────────────────
// Ordem do dia: primeiro que entra, primeiro que sai.
class Fila {
    No  frente;
    No  fundo;
    int tamanho;

    Fila() { frente = null; fundo = null; tamanho = 0; }

    // Coloca no fundo -- O(1)
    void enqueue(Atividade a) {
        No novo = new No(a);
        if (fundo == null) { frente = novo; fundo = novo; }
        else { fundo.proximo = novo; fundo = novo; }
        tamanho++;
    }

    // Tira da frente -- O(1)
    Atividade dequeue() {
        if (estaVazia()) throw new IllegalStateException("Fila vazia!");
        Atividade a = (Atividade) frente.dado;
        frente = frente.proximo;
        if (frente == null) fundo = null;
        tamanho--;
        return a;
    }

    // Converte para JSON array
    String paraJSON() {
        StringBuilder sb = new StringBuilder("[");
        No atual = frente;
        while (atual != null) {
            sb.append(((Atividade) atual.dado).paraJSON());
            if (atual.proximo != null) sb.append(",");
            atual = atual.proximo;
        }
        sb.append("]");
        return sb.toString();
    }

    boolean estaVazia() { return tamanho == 0; }
}


// ── PILHA (LIFO) ─────────────────────────────────
// Historico de acoes: ultimo que entra, primeiro que sai.
class Pilha {
    No  topo;
    int tamanho;

    Pilha() { topo = null; tamanho = 0; }

    // Empilha -- O(1)
    void push(String acao) {
        No novo = new No(acao);
        novo.proximo = topo;
        topo = novo;
        tamanho++;
    }

    // Desempilha -- O(1)
    String pop() {
        if (estaVazia()) throw new IllegalStateException("Historico vazio!");
        String a = (String) topo.dado;
        topo = topo.proximo;
        tamanho--;
        return a;
    }

    String peek() {
        if (estaVazia()) return "(vazio)";
        return (String) topo.dado;
    }

    // Converte para JSON array
    String paraJSON() {
        StringBuilder sb = new StringBuilder("[");
        No atual = topo;
        while (atual != null) {
            sb.append("\"").append(atual.dado).append("\"");
            if (atual.proximo != null) sb.append(",");
            atual = atual.proximo;
        }
        sb.append("]");
        return sb.toString();
    }

    boolean estaVazia() { return tamanho == 0; }
}
