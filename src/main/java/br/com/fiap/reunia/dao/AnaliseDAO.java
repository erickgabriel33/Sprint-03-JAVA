package br.com.fiap.reunia.dao;

import br.com.fiap.reunia.model.AnaliseReuniao;
import br.com.fiap.reunia.model.Sentimento;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

/**
 * Persistencia da tabela T_REUNIA_ANALISE. Listas (produtos, concorrentes, termos) sao gravadas separadas por virgula.
 */
public class AnaliseDAO extends BaseDAO {

    private static final String COLUNAS = "id_analise, id_reuniao, ds_sentimento, nr_score_sentimento, nr_risco_churn, "
            + "nr_score_oportunidade, ds_produtos, ds_concorrentes, ds_termos, ds_recomendacao, dt_analise";

    public AnaliseDAO(Connection conexao) {
        super(conexao);
    }

    /** Insere a analise ou substitui a anterior da mesma reuniao (reprocessamento). */
    public AnaliseReuniao salvar(AnaliseReuniao analise) throws SQLException {
        boolean existe = buscarPorReuniao(analise.getReuniaoId()).isPresent();
        String sql = existe
                ? "UPDATE T_REUNIA_ANALISE SET ds_sentimento = ?, nr_score_sentimento = ?, nr_risco_churn = ?, "
                  + "nr_score_oportunidade = ?, ds_produtos = ?, ds_concorrentes = ?, ds_termos = ?, ds_recomendacao = ?, "
                  + "dt_analise = SYSTIMESTAMP WHERE id_reuniao = ?"
                : "INSERT INTO T_REUNIA_ANALISE (ds_sentimento, nr_score_sentimento, nr_risco_churn, nr_score_oportunidade, "
                  + "ds_produtos, ds_concorrentes, ds_termos, ds_recomendacao, id_reuniao) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?)";
        try (PreparedStatement ps = conexao.prepareStatement(sql)) {
            ps.setString(1, analise.getSentimento().name());
            ps.setInt(2, analise.getScoreSentimento());
            ps.setInt(3, analise.getRiscoChurn());
            ps.setInt(4, analise.getScoreOportunidade());
            ps.setString(5, juntar(analise.getProdutos()));
            ps.setString(6, juntar(analise.getConcorrentes()));
            ps.setString(7, juntar(analise.getTermos()));
            ps.setString(8, analise.getRecomendacao());
            ps.setLong(9, analise.getReuniaoId());
            ps.executeUpdate();
        }
        return buscarPorReuniao(analise.getReuniaoId()).orElseThrow();
    }

    public Optional<AnaliseReuniao> buscarPorReuniao(long reuniaoId) throws SQLException {
        String sql = "SELECT " + COLUNAS + " FROM T_REUNIA_ANALISE WHERE id_reuniao = ?";
        try (PreparedStatement ps = conexao.prepareStatement(sql)) {
            ps.setLong(1, reuniaoId);
            try (ResultSet rs = ps.executeQuery()) {
                return rs.next() ? Optional.of(mapear(rs)) : Optional.empty();
            }
        }
    }

    public List<AnaliseReuniao> listar() throws SQLException {
        String sql = "SELECT " + COLUNAS + " FROM T_REUNIA_ANALISE ORDER BY nr_risco_churn DESC";
        List<AnaliseReuniao> analises = new ArrayList<>();
        try (PreparedStatement ps = conexao.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                analises.add(mapear(rs));
            }
        }
        return analises;
    }

    private AnaliseReuniao mapear(ResultSet rs) throws SQLException {
        AnaliseReuniao analise = new AnaliseReuniao();
        analise.setId(rs.getLong("id_analise"));
        analise.setReuniaoId(rs.getLong("id_reuniao"));
        analise.setSentimento(Sentimento.valueOf(rs.getString("ds_sentimento")));
        analise.setScoreSentimento(rs.getInt("nr_score_sentimento"));
        analise.setRiscoChurn(rs.getInt("nr_risco_churn"));
        analise.setScoreOportunidade(rs.getInt("nr_score_oportunidade"));
        analise.setProdutos(separar(rs.getString("ds_produtos")));
        analise.setConcorrentes(separar(rs.getString("ds_concorrentes")));
        analise.setTermos(separar(rs.getString("ds_termos")));
        analise.setRecomendacao(rs.getString("ds_recomendacao"));
        analise.setDataAnalise(deTimestamp(rs.getTimestamp("dt_analise")));
        return analise;
    }

    private static String juntar(List<String> valores) {
        return valores == null || valores.isEmpty() ? null : String.join(", ", valores);
    }

    private static List<String> separar(String valor) {
        if (valor == null || valor.isBlank()) {
            return new ArrayList<>();
        }
        return new ArrayList<>(Arrays.stream(valor.split(",")).map(String::trim).toList());
    }
}
