package br.com.fiap.service;

import br.com.fiap.model.entity.Analise;
import org.springframework.stereotype.Service;

import java.text.Normalizer;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

// Analisa o texto da transcricao: Transcricao -> Processamento -> Indicadores -> Recomendacao
@Service
public class AnaliseService {

    // {nome exibido, palavra procurada no texto}
    private static final String[][] PRODUTOS_TOTVS = {
            {"Protheus", "protheus"}, {"RM", "rm"}, {"Fluig", "fluig"}, {"Datasul", "datasul"},
            {"Logix", "logix"}, {"Winthor", "winthor"}, {"Consinco", "consinco"},
            {"TOTVS Techfin", "techfin"}, {"RD Station", "rd station"}
    };

    private static final String[][] CONCORRENTES = {
            {"SAP", "sap"}, {"Oracle NetSuite", "netsuite"}, {"Senior Sistemas", "senior sistemas"},
            {"Sankhya", "sankhya"}, {"Omie", "omie"}, {"Microsoft Dynamics", "dynamics"}, {"Bling", "bling"}
    };

    private static final String[] PALAVRAS_POSITIVAS = {
            "satisfeito", "satisfeita", "satisfeitos", "gostou", "gostaram", "excelente", "otimo", "otima", "bom", "boa",
            "elogiou", "elogio", "positivo", "positiva", "aprovou", "aprovado", "interessado", "interessada", "resolvido",
            "rapido", "facil", "parabens", "sucesso", "confiante", "recomendaria", "feliz", "melhorou", "eficiente"
    };

    private static final String[] PALAVRAS_NEGATIVAS = {
            "insatisfeito", "insatisfeita", "insatisfacao", "problema", "problemas", "lento", "lenta", "lentidao", "erro",
            "erros", "falha", "falhas", "ruim", "pessimo", "pessima", "reclamacao", "reclamacoes", "reclamou", "atraso",
            "atrasado", "caro", "dificil", "travando", "trava", "frustrado", "frustracao", "demora", "bug", "instavel"
    };

    private static final String[] SINAIS_CHURN = {
            "cancelar", "cancelamento", "rescindir", "rescisao", "encerrar o contrato", "trocar de sistema",
            "migrar para", "outro fornecedor", "outro sistema", "nao vamos renovar", "avaliando alternativas", "desistir"
    };

    private static final String[] SINAIS_OPORTUNIDADE = {
            "proposta", "orcamento", "expandir", "expansao", "ampliar", "licencas", "modulo", "contratar", "interesse",
            "demonstracao", "upgrade", "implantacao", "adquirir", "comprar", "investimento", "cotacao", "renovar"
    };

    private static final String[] NEGACOES = {"nao", "nunca", "sem", "nem", "jamais"};

    private static final String[] PALAVRAS_IGNORADAS = {
            "para", "como", "mais", "muito", "esta", "isso", "essa", "esse", "este", "aqui", "tambem", "quando", "entao",
            "porque", "pois", "sobre", "pelo", "pela", "voce", "nossa", "nosso", "seus", "suas", "temos", "foram", "ainda",
            "disse", "falou", "quer", "querem", "precisa", "fazer", "estamos", "mesmo", "todo", "toda", "todos", "outro"
    };

    public Analise analisar(String texto) {
        String[] palavras = normalizar(texto).trim().split("[^a-z0-9]+");
        String textoNormalizado = " " + String.join(" ", palavras) + " ";

        int positivas = 0;
        int negativas = 0;
        int oportunidades = 0;
        for (int i = 0; i < palavras.length; i++) {
            boolean negada = estaNegada(palavras, i);
            if (contem(PALAVRAS_POSITIVAS, palavras[i])) {
                if (negada) negativas++; else positivas++;
            } else if (contem(PALAVRAS_NEGATIVAS, palavras[i])) {
                if (negada) positivas++; else negativas++;
            }
            if (contem(SINAIS_OPORTUNIDADE, palavras[i]) && !negada) {
                oportunidades++;
            }
        }

        int sinaisChurn = 0;
        for (String sinal : SINAIS_CHURN) {
            if (textoNormalizado.contains(" " + sinal + " ")) {
                sinaisChurn++;
            }
        }
        List<String> produtos = encontrar(PRODUTOS_TOTVS, textoNormalizado);
        List<String> concorrentes = encontrar(CONCORRENTES, textoNormalizado);

        // o "+ 2" evita que uma unica palavra gere um score extremo
        int scoreSentimento = (int) Math.round(100.0 * (positivas - negativas) / (positivas + negativas + 2));
        int riscoChurn = limitar(sinaisChurn * 35 + negativas * 10 + concorrentes.size() * 15 - positivas * 5);
        int scoreOportunidade = limitar(oportunidades * 15 + positivas * 5 + produtos.size() * 5 - sinaisChurn * 15);

        Analise analise = new Analise();
        analise.setScoreSentimento(scoreSentimento);
        analise.setSentimento(classificarSentimento(scoreSentimento));
        analise.setRiscoChurn(riscoChurn);
        analise.setScoreOportunidade(scoreOportunidade);
        analise.setProdutos(produtos.isEmpty() ? null : String.join(", ", produtos));
        analise.setConcorrentes(concorrentes.isEmpty() ? null : String.join(", ", concorrentes));
        analise.setTermos(String.join(", ", principaisTermos(palavras)));
        analise.setRecomendacao(recomendar(riscoChurn, scoreOportunidade, analise.getSentimento(), produtos, concorrentes));
        analise.setDataAnalise(LocalDateTime.now());
        return analise;
    }

    private String classificarSentimento(int score) {
        if (score >= 25) return "POSITIVO";
        if (score <= -25) return "NEGATIVO";
        return "NEUTRO";
    }

    private String recomendar(int riscoChurn, int scoreOportunidade, String sentimento,
                              List<String> produtos, List<String> concorrentes) {
        String recomendacao = "";
        if (riscoChurn >= 70) {
            recomendacao += "Risco crítico de churn: acionar squad de CS e gerente da conta em até 24h com plano de resgate. ";
        } else if (riscoChurn >= 40) {
            recomendacao += "Risco moderado: agendar follow-up em até 7 dias para tratar os pontos de insatisfação. ";
        }
        if (!concorrentes.isEmpty()) {
            recomendacao += "Concorrente citado (" + String.join(", ", concorrentes)
                    + "): reforçar diferenciais da TOTVS e revisar condições comerciais. ";
        }
        if (scoreOportunidade >= 60) {
            recomendacao += "Alta oportunidade comercial: enviar proposta em até 48h";
            if (!produtos.isEmpty()) {
                recomendacao += " envolvendo " + String.join(", ", produtos);
            }
            recomendacao += ". ";
        } else if (scoreOportunidade >= 30) {
            recomendacao += "Oportunidade em desenvolvimento: nutrir o cliente com demonstração e conteúdo do produto. ";
        }
        if ("POSITIVO".equals(sentimento) && riscoChurn < 40) {
            recomendacao += "Cliente satisfeito: solicitar indicação ou case de sucesso. ";
        }
        if (recomendacao.isEmpty()) {
            recomendacao = "Sem sinais relevantes: manter acompanhamento periódico.";
        }
        return recomendacao.trim();
    }

    // As 5 palavras que mais aparecem (com 4 letras ou mais e fora da lista de palavras ignoradas)
    private List<String> principaisTermos(String[] palavras) {
        Map<String, Integer> frequencia = new LinkedHashMap<>();
        for (String palavra : palavras) {
            if (palavra.length() >= 4 && !contem(PALAVRAS_IGNORADAS, palavra) && !contem(NEGACOES, palavra)) {
                frequencia.put(palavra, frequencia.getOrDefault(palavra, 0) + 1);
            }
        }
        List<Map.Entry<String, Integer>> lista = new ArrayList<>(frequencia.entrySet());
        lista.sort((a, b) -> b.getValue() - a.getValue());
        List<String> termos = new ArrayList<>();
        for (int i = 0; i < lista.size() && i < 5; i++) {
            termos.add(lista.get(i).getKey());
        }
        return termos;
    }

    // "nao gostou" ou "nao ficou satisfeito": a palavra e negada se houver negacao ate 2 posicoes antes
    private boolean estaNegada(String[] palavras, int posicao) {
        for (int i = Math.max(0, posicao - 2); i < posicao; i++) {
            if (contem(NEGACOES, palavras[i])) {
                return true;
            }
        }
        return false;
    }

    private List<String> encontrar(String[][] dicionario, String textoNormalizado) {
        List<String> encontrados = new ArrayList<>();
        for (String[] item : dicionario) {
            if (textoNormalizado.contains(" " + item[1] + " ")) {
                encontrados.add(item[0]);
            }
        }
        return encontrados;
    }

    private boolean contem(String[] lista, String palavra) {
        for (String item : lista) {
            if (item.equals(palavra)) {
                return true;
            }
        }
        return false;
    }

    // Deixa o texto em minusculas e sem acentos: "Insatisfação" vira "insatisfacao"
    private String normalizar(String texto) {
        return Normalizer.normalize(texto, Normalizer.Form.NFD).replaceAll("\\p{M}", "").toLowerCase();
    }

    private int limitar(int valor) {
        return Math.max(0, Math.min(100, valor));
    }
}
