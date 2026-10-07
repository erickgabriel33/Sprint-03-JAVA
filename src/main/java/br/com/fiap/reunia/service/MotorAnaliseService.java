package br.com.fiap.reunia.service;

import br.com.fiap.reunia.model.AnaliseReuniao;
import br.com.fiap.reunia.model.Sentimento;
import br.com.fiap.reunia.util.TextoUtil;
import jakarta.enterprise.context.ApplicationScoped;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * Motor de analise da transcricao (Transcricao -> Processamento -> Indicadores -> Recomendacao).
 * Usa dicionarios de palavras-chave com tratamento de negacao ("nao gostou" conta como negativo),
 * sem depender de servicos externos.
 */
@ApplicationScoped
public class MotorAnaliseService {

    private static final Map<String, List<String>> PRODUTOS_TOTVS = new LinkedHashMap<>();
    private static final Map<String, List<String>> CONCORRENTES = new LinkedHashMap<>();

    static {
        PRODUTOS_TOTVS.put("Protheus", List.of("protheus"));
        PRODUTOS_TOTVS.put("RM", List.of("rm", "totvs rm"));
        PRODUTOS_TOTVS.put("Fluig", List.of("fluig"));
        PRODUTOS_TOTVS.put("Datasul", List.of("datasul"));
        PRODUTOS_TOTVS.put("Logix", List.of("logix"));
        PRODUTOS_TOTVS.put("Winthor", List.of("winthor"));
        PRODUTOS_TOTVS.put("Consinco", List.of("consinco"));
        PRODUTOS_TOTVS.put("TOTVS Techfin", List.of("techfin"));
        PRODUTOS_TOTVS.put("RD Station", List.of("rd station", "rdstation"));

        CONCORRENTES.put("SAP", List.of("sap"));
        CONCORRENTES.put("Oracle NetSuite", List.of("netsuite"));
        CONCORRENTES.put("Senior Sistemas", List.of("senior sistemas"));
        CONCORRENTES.put("Sankhya", List.of("sankhya"));
        CONCORRENTES.put("Omie", List.of("omie"));
        CONCORRENTES.put("Microsoft Dynamics", List.of("dynamics"));
        CONCORRENTES.put("Bling", List.of("bling"));
    }

    private static final Set<String> POSITIVAS = Set.of(
            "satisfeito", "satisfeita", "satisfeitos", "gostou", "gostaram", "gostamos", "excelente", "otimo", "otima",
            "bom", "boa", "elogiou", "elogio", "elogios", "positivo", "positiva", "aprovou", "aprovado", "interessado",
            "interessada", "resolvido", "resolveu", "rapido", "facil", "parabens", "sucesso", "confiante",
            "recomendaria", "feliz", "melhorou", "eficiente", "agil", "tranquilo");

    private static final Set<String> NEGATIVAS = Set.of(
            "insatisfeito", "insatisfeita", "insatisfacao", "problema", "problemas", "lento", "lenta", "lentidao",
            "erro", "erros", "falha", "falhas", "ruim", "pessimo", "pessima", "reclamacao", "reclamacoes", "reclamou",
            "atraso", "atrasado", "atrasada", "caro", "dificil", "travando", "trava", "frustrado", "frustrada",
            "frustracao", "demora", "demorado", "bug", "bugs", "instavel", "indisponivel", "preocupado", "preocupada");

    private static final List<String> SINAIS_CHURN = List.of(
            "cancelar", "cancelamento", "rescindir", "rescisao", "encerrar o contrato", "trocar de sistema",
            "migrar para", "outro fornecedor", "outro sistema", "nao vamos renovar", "nao renovar",
            "avaliando alternativas", "avaliando outras", "sair da totvs", "desistir", "multa contratual");

    private static final Set<String> SINAIS_OPORTUNIDADE = Set.of(
            "proposta", "orcamento", "expandir", "expansao", "ampliar", "licencas", "modulo", "contratar",
            "contratacao", "interesse", "demonstracao", "upgrade", "implantacao", "adquirir", "comprar",
            "investimento", "cotacao", "aditivo", "piloto", "renovar", "renovacao", "upsell");

    private static final Set<String> NEGACOES = Set.of("nao", "nunca", "sem", "nem", "jamais");

    private static final Set<String> STOPWORDS = Set.of(
            "para", "como", "mais", "muito", "muita", "esta", "estao", "isso", "essa", "esse", "este", "aqui", "tambem",
            "quando", "entao", "porque", "pois", "sobre", "pelo", "pela", "pelos", "pelas", "numa", "num", "dele",
            "dela", "eles", "elas", "voce", "voces", "nossa", "nosso", "seus", "suas", "tem", "temos", "foram", "seria",
            "ainda", "disse", "falou", "quer", "querem", "precisa", "fazer", "ser", "estamos", "tinha", "mesmo", "cada",
            "todo", "toda", "todos", "todas", "outro", "outra", "qual", "onde", "ate", "apos", "com", "uma", "uns",
            "umas", "dos", "das", "nos", "nas", "que", "ele", "ela", "sim", "bem", "ter", "vai", "vamos", "pode");

    public AnaliseReuniao analisar(String texto) {
        List<String> tokens = TextoUtil.tokenizar(texto);
        String textoNormalizado = " " + String.join(" ", tokens) + " ";

        int positivos = 0;
        int negativos = 0;
        int oportunidades = 0;
        for (int i = 0; i < tokens.size(); i++) {
            String token = tokens.get(i);
            boolean negado = negado(tokens, i);
            if (POSITIVAS.contains(token)) {
                if (negado) negativos++; else positivos++;
            } else if (NEGATIVAS.contains(token)) {
                if (negado) positivos++; else negativos++;
            }
            if (SINAIS_OPORTUNIDADE.contains(token) && !negado) {
                oportunidades++;
            }
        }
        int sinaisChurn = (int) SINAIS_CHURN.stream().filter(s -> textoNormalizado.contains(" " + s + " ")).count();
        List<String> produtos = encontrar(PRODUTOS_TOTVS, textoNormalizado);
        List<String> concorrentes = encontrar(CONCORRENTES, textoNormalizado);

        // Suavizacao (+2 no denominador) evita que uma unica palavra gere score extremo.
        int scoreSentimento = (int) Math.round(100.0 * (positivos - negativos) / (positivos + negativos + 2));
        int riscoChurn = limitar(sinaisChurn * 35 + negativos * 10 + concorrentes.size() * 15 - positivos * 5);
        int scoreOportunidade = limitar(oportunidades * 15 + positivos * 5 + produtos.size() * 5 - sinaisChurn * 15);

        AnaliseReuniao analise = new AnaliseReuniao();
        analise.setScoreSentimento(scoreSentimento);
        analise.setSentimento(Sentimento.porScore(scoreSentimento));
        analise.setRiscoChurn(riscoChurn);
        analise.setScoreOportunidade(scoreOportunidade);
        analise.setProdutos(produtos);
        analise.setConcorrentes(concorrentes);
        analise.setTermos(principaisTermos(tokens));
        analise.setRecomendacao(recomendar(analise));
        analise.setDataAnalise(LocalDateTime.now());
        return analise;
    }

    /** Considera negada a palavra precedida por "nao", "nunca"... em ate duas posicoes ("nao ficou satisfeito"). */
    private boolean negado(List<String> tokens, int indice) {
        for (int i = Math.max(0, indice - 2); i < indice; i++) {
            if (NEGACOES.contains(tokens.get(i))) {
                return true;
            }
        }
        return false;
    }

    private List<String> encontrar(Map<String, List<String>> dicionario, String textoNormalizado) {
        return dicionario.entrySet().stream()
                .filter(e -> e.getValue().stream().anyMatch(alias -> textoNormalizado.contains(" " + alias + " ")))
                .map(Map.Entry::getKey)
                .toList();
    }

    /** As 5 palavras mais frequentes (4+ letras, sem stopwords); empate mantem a ordem de aparicao. */
    private List<String> principaisTermos(List<String> tokens) {
        Map<String, Integer> frequencia = new LinkedHashMap<>();
        tokens.stream()
                .filter(t -> t.length() >= 4 && !STOPWORDS.contains(t) && !NEGACOES.contains(t) && !t.matches("\\d+"))
                .forEach(t -> frequencia.merge(t, 1, Integer::sum));
        return frequencia.entrySet().stream()
                .sorted(Map.Entry.<String, Integer>comparingByValue().reversed())
                .limit(5)
                .map(Map.Entry::getKey)
                .toList();
    }

    private String recomendar(AnaliseReuniao analise) {
        List<String> acoes = new ArrayList<>();
        if (analise.getRiscoChurn() >= 70) {
            acoes.add("Risco crítico de churn: acionar squad de CS e gerente da conta em até 24h com plano de resgate.");
        } else if (analise.getRiscoChurn() >= 40) {
            acoes.add("Risco moderado: agendar follow-up em até 7 dias para tratar os pontos de insatisfação.");
        }
        if (!analise.getConcorrentes().isEmpty()) {
            acoes.add("Concorrente citado (" + String.join(", ", analise.getConcorrentes())
                    + "): reforçar diferenciais da TOTVS e revisar condições comerciais.");
        }
        if (analise.getScoreOportunidade() >= 60) {
            String produtos = analise.getProdutos().isEmpty() ? "" : " envolvendo " + String.join(", ", analise.getProdutos());
            acoes.add("Alta oportunidade comercial: enviar proposta em até 48h" + produtos + ".");
        } else if (analise.getScoreOportunidade() >= 30) {
            acoes.add("Oportunidade em desenvolvimento: nutrir o cliente com demonstração e conteúdo do produto.");
        }
        if (analise.getSentimento() == Sentimento.POSITIVO && analise.getRiscoChurn() < 40) {
            acoes.add("Cliente satisfeito: solicitar indicação ou case de sucesso.");
        }
        if (acoes.isEmpty()) {
            acoes.add("Sem sinais relevantes: manter acompanhamento periódico.");
        }
        return String.join(" ", acoes);
    }

    private static int limitar(int valor) {
        return Math.max(0, Math.min(100, valor));
    }
}
