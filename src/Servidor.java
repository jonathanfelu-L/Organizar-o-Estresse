// ──────────────────────────────────────────────
//  Servidor.java
//  Servidor HTTP na porta 8080.
//  Serve o arquivo web/index.html e responde
//  chamadas da API feitas pelo navegador.
// ──────────────────────────────────────────────

import com.sun.net.httpserver.HttpServer;
import com.sun.net.httpserver.HttpExchange;
import java.io.*;
import java.net.InetSocketAddress;
import java.nio.file.Files;

public class Servidor {

    // Estruturas em memoria (carregadas do banco ao iniciar)
    static Lista  agenda    = new Lista();
    static Pilha  historico = new Pilha();

    public static void main(String[] args) throws IOException {

        // Carrega as atividades salvas no arquivo
        agenda = Banco.carregar();
        System.out.println("Banco carregado: " + agenda.tamanho + " atividade(s).");

        // Cria o servidor HTTP na porta 8080
        HttpServer servidor = HttpServer.create(new InetSocketAddress(8080), 0);

        // Rotas
        servidor.createContext("/",                  Servidor::servirHTML);
        servidor.createContext("/api/atividades",    Servidor::rotaAtividades);
        servidor.createContext("/api/fila",          Servidor::rotaFila);
        servidor.createContext("/api/estresse",      Servidor::rotaEstresse);
        servidor.createContext("/api/historico",     Servidor::rotaHistorico);

        servidor.start();
        System.out.println("Servidor rodando em: http://localhost:8080");
        System.out.println("Pressione Ctrl+C para parar.");
    }


    // ── Serve o arquivo index.html ───────────────────────────

    static void servirHTML(HttpExchange troca) throws IOException {
        File html = new File("web/index.html");
        if (!html.exists()) {
            responder(troca, 404, "text/plain", "index.html nao encontrado.");
            return;
        }
        byte[] conteudo = Files.readAllBytes(html.toPath());
        troca.getResponseHeaders().set("Content-Type", "text/html; charset=UTF-8");
        troca.sendResponseHeaders(200, conteudo.length);
        troca.getResponseBody().write(conteudo);
        troca.getResponseBody().close();
    }


    // ── /api/atividades ──────────────────────────────────────

    static void rotaAtividades(HttpExchange troca) throws IOException {
        String metodo = troca.getRequestMethod();
        adicionarCORS(troca);

        if (metodo.equals("GET")) {
            // Retorna todas as atividades
            responder(troca, 200, "application/json", agenda.paraJSON());

        } else if (metodo.equals("POST")) {
            // Adiciona uma atividade
            String corpo = lerCorpo(troca);
            try {
                String nome  = extrairString(corpo, "nome");
                int hora     = extrairInt(corpo, "horaInicio");
                int duracao  = extrairInt(corpo, "duracao");
                int estresse = extrairInt(corpo, "nivelEstresse");

                if (agenda.temConflito(hora, duracao)) {
                    responder(troca, 409, "application/json",
                        "{\"erro\":\"Conflito de horario!\"}");
                    return;
                }

                Atividade nova = new Atividade(nome, hora, duracao, estresse);
                agenda.inserirNoFim(nova);
                agenda.ordenarPorHorario();
                historico.push("ADICIONOU: " + nome + " as " + hora + ":00");
                Banco.salvar(agenda);

                responder(troca, 201, "application/json", nova.paraJSON());

            } catch (Exception e) {
                responder(troca, 400, "application/json",
                    "{\"erro\":\"Dados invalidos: " + e.getMessage() + "\"}");
            }

        } else if (metodo.equals("DELETE")) {
            // Remove por posicao (?pos=N na URL)
            String query = troca.getRequestURI().getQuery();
            try {
                int pos = Integer.parseInt(query.replace("pos=", ""));
                Atividade removida = agenda.remover(pos);
                historico.push("REMOVEU: " + removida.nome);
                Banco.salvar(agenda);
                responder(troca, 200, "application/json", removida.paraJSON());
            } catch (Exception e) {
                responder(troca, 400, "application/json",
                    "{\"erro\":\"Posicao invalida\"}");
            }

        } else if (metodo.equals("PUT")) {
            // Modifica estresse (?pos=N na URL)
            String query = troca.getRequestURI().getQuery();
            String corpo = lerCorpo(troca);
            try {
                int pos          = Integer.parseInt(query.replace("pos=", ""));
                int novoEstresse = extrairInt(corpo, "nivelEstresse");
                agenda.obter(pos).nivelEstresse = novoEstresse;
                historico.push("MODIFICOU estresse: posicao " + pos + " -> " + novoEstresse);
                Banco.salvar(agenda);
                responder(troca, 200, "application/json", agenda.obter(pos).paraJSON());
            } catch (Exception e) {
                responder(troca, 400, "application/json",
                    "{\"erro\":\"Dados invalidos\"}");
            }

        } else {
            responder(troca, 405, "text/plain", "Metodo nao suportado");
        }
    }


    // ── /api/fila ────────────────────────────────────────────

    static void rotaFila(HttpExchange troca) throws IOException {
        adicionarCORS(troca);

        // Gera a fila do dia preenchendo buracos com Descanso
        Fila fila = new Fila();
        int horaAtual = 0;

        for (int i = 0; i < agenda.tamanho; i++) {
            Atividade a = agenda.obter(i);
            if (a.horaInicio > horaAtual) {
                fila.enqueue(new Atividade("Descanso / Tempo livre",
                    horaAtual, a.horaInicio - horaAtual, -1));
            }
            fila.enqueue(a);
            horaAtual = a.getHoraFim();
        }
        if (horaAtual < 24) {
            fila.enqueue(new Atividade("Descanso / Tempo livre",
                horaAtual, 24 - horaAtual, -1));
        }

        responder(troca, 200, "application/json", fila.paraJSON());
    }


    // ── /api/estresse ────────────────────────────────────────

    static void rotaEstresse(HttpExchange troca) throws IOException {
        adicionarCORS(troca);
        int total    = agenda.calcularEstresseTotal();
        String nivel = avaliarEstresse(total);
        String json  = "{\"total\":" + total + ",\"avaliacao\":\"" + nivel + "\"}";
        responder(troca, 200, "application/json", json);
    }


    // ── /api/historico ───────────────────────────────────────

    static void rotaHistorico(HttpExchange troca) throws IOException {
        adicionarCORS(troca);
        responder(troca, 200, "application/json", historico.paraJSON());
    }


    // ── Utilitarios ──────────────────────────────────────────

    static String avaliarEstresse(int total) {
        if (total <= -10) return "Dia muito relaxante";
        if (total <    0) return "Dia tranquilo";
        if (total ==   0) return "Dia equilibrado";
        if (total <=  15) return "Dia moderado";
        if (total <=  30) return "Dia estressante";
        return                   "Dia muito estressante";
    }

    static void responder(HttpExchange t, int status, String tipo, String corpo)
            throws IOException {
        byte[] bytes = corpo.getBytes("UTF-8");
        t.getResponseHeaders().set("Content-Type", tipo + "; charset=UTF-8");
        t.sendResponseHeaders(status, bytes.length);
        t.getResponseBody().write(bytes);
        t.getResponseBody().close();
    }

    static void adicionarCORS(HttpExchange t) {
        t.getResponseHeaders().set("Access-Control-Allow-Origin",  "*");
        t.getResponseHeaders().set("Access-Control-Allow-Methods", "GET,POST,PUT,DELETE,OPTIONS");
        t.getResponseHeaders().set("Access-Control-Allow-Headers", "Content-Type");
    }

    static String lerCorpo(HttpExchange t) throws IOException {
        return new String(t.getRequestBody().readAllBytes(), "UTF-8");
    }

    // Extrai valor de string de um JSON simples: "chave":"valor"
    static String extrairString(String json, String chave) {
        String token = "\"" + chave + "\":\"";
        int ini = json.indexOf(token) + token.length();
        int fim = json.indexOf("\"", ini);
        return json.substring(ini, fim);
    }

    // Extrai valor numerico de um JSON simples: "chave":123
    static int extrairInt(String json, String chave) {
        String token = "\"" + chave + "\":";
        int ini = json.indexOf(token) + token.length();
        int fim = json.indexOf(",", ini);
        if (fim == -1) fim = json.indexOf("}", ini);
        return Integer.parseInt(json.substring(ini, fim).trim());
    }
}
