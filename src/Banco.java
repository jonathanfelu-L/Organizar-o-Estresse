// ──────────────────────────────────────────────
//  Banco.java
//  Banco de dados simples usando arquivo de texto.
//  Cada linha guarda uma atividade no formato:
//    nome|horaInicio|duracao|nivelEstresse
// ──────────────────────────────────────────────

import java.io.*;

class Banco {

    private static final String ARQUIVO = "data/rotinas.txt";

    // Le o arquivo e devolve uma Lista com todas as atividades
    static Lista carregar() {
        Lista lista = new Lista();
        File arquivo = new File(ARQUIVO);

        if (!arquivo.exists()) return lista; // banco vazio na primeira vez

        try {
            BufferedReader leitor = new BufferedReader(new FileReader(arquivo));
            String linha;
            while ((linha = leitor.readLine()) != null) {
                linha = linha.trim();
                if (linha.isEmpty() || linha.startsWith("#")) continue;
                try {
                    lista.inserirNoFim(Atividade.doArquivo(linha));
                } catch (Exception e) {
                    // ignora linha corrompida
                }
            }
            leitor.close();
        } catch (IOException e) {
            System.out.println("Aviso: nao foi possivel ler o banco de dados.");
        }

        lista.ordenarPorHorario();
        return lista;
    }

    // Salva toda a lista no arquivo, sobrescrevendo o conteudo anterior
    static void salvar(Lista lista) {
        // Garante que a pasta data/ existe
        new File("data").mkdirs();

        try {
            BufferedWriter escritor = new BufferedWriter(new FileWriter(ARQUIVO));
            escritor.write("# Banco de dados - RotinaCerta");
            escritor.newLine();
            escritor.write("# formato: nome|horaInicio|duracao|nivelEstresse");
            escritor.newLine();

            for (int i = 0; i < lista.tamanho; i++) {
                escritor.write(lista.obter(i).paraArquivo());
                escritor.newLine();
            }

            escritor.close();
        } catch (IOException e) {
            System.out.println("Aviso: nao foi possivel salvar o banco de dados.");
        }
    }
}
