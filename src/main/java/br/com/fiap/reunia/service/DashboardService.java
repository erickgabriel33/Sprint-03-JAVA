package br.com.fiap.reunia.service;

import br.com.fiap.reunia.dao.AnaliseDAO;
import br.com.fiap.reunia.dao.ClienteDAO;
import br.com.fiap.reunia.dao.ReuniaoDAO;
import br.com.fiap.reunia.dto.ClienteRiscoResponse;
import br.com.fiap.reunia.dto.DashboardResponse;
import br.com.fiap.reunia.exception.NegocioException;
import br.com.fiap.reunia.model.AnaliseReuniao;
import br.com.fiap.reunia.model.Cliente;
import br.com.fiap.reunia.model.Reuniao;
import br.com.fiap.reunia.model.ReuniaoCS;
import br.com.fiap.reunia.model.ReuniaoVendas;
import br.com.fiap.reunia.model.StatusReuniao;
import jakarta.enterprise.context.ApplicationScoped;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;
import java.util.function.Function;
import java.util.stream.Collectors;

import static br.com.fiap.reunia.factory.GerenciadorTransacao.consultar;

/**
 * Indicadores gerenciais calculados com Stream API sobre os dados do banco.
 */
@ApplicationScoped
public class DashboardService {

    private static final int RISCO_CHURN_ALERTA = 60;

    private record Dados(List<Cliente> clientes, List<Reuniao> reunioes, List<AnaliseReuniao> analises) {
    }

    public DashboardResponse resumo() {
        Dados dados = carregar();
        List<Reuniao> ativas = dados.reunioes().stream()
                .filter(r -> r.getStatus() != StatusReuniao.CANCELADA)
                .toList();
        List<ReuniaoVendas> vendas = ativas.stream()
                .filter(ReuniaoVendas.class::isInstance).map(ReuniaoVendas.class::cast).toList();

        BigDecimal pipeline = vendas.stream().map(ReuniaoVendas::getValorOportunidade).reduce(BigDecimal.ZERO, BigDecimal::add);
        BigDecimal pipelinePonderado = vendas.stream().map(ReuniaoVendas::calcularValorPonderado).reduce(BigDecimal.ZERO, BigDecimal::add);
        double npsMedio = dados.reunioes().stream()
                .filter(ReuniaoCS.class::isInstance).map(ReuniaoCS.class::cast)
                .mapToInt(ReuniaoCS::getNps).average().orElse(0);

        return new DashboardResponse(
                dados.clientes().size(),
                dados.reunioes().size(),
                dados.reunioes().stream().filter(Reuniao::isProcessada).count(),
                contar(dados.reunioes(), r -> r.getStatus().name()),
                contar(dados.reunioes(), r -> r.getTipo().name()),
                contar(dados.analises(), a -> a.getSentimento().name()),
                pipeline,
                pipelinePonderado,
                Math.round(npsMedio * 10) / 10.0,
                ativas.stream().filter(r -> r.calcularPrioridade() >= 70).count(),
                filtrarRisco(dados, RISCO_CHURN_ALERTA).stream().map(ClienteRiscoResponse::clienteId).distinct().count());
    }

    /** Reunioes analisadas com risco de churn >= minimo, do maior risco para o menor. */
    public List<ClienteRiscoResponse> clientesEmRisco(int riscoMinimo) {
        if (riscoMinimo < 0 || riscoMinimo > 100) {
            throw new NegocioException("O risco mínimo deve estar entre 0 e 100.");
        }
        return filtrarRisco(carregar(), riscoMinimo);
    }

    private List<ClienteRiscoResponse> filtrarRisco(Dados dados, int riscoMinimo) {
        Map<Long, Reuniao> reunioes = dados.reunioes().stream().collect(Collectors.toMap(Reuniao::getId, Function.identity()));
        Map<Long, String> nomesClientes = dados.clientes().stream().collect(Collectors.toMap(Cliente::getId, Cliente::getNome));

        return dados.analises().stream()
                .filter(a -> a.getRiscoChurn() >= riscoMinimo)
                .filter(a -> reunioes.containsKey(a.getReuniaoId()))
                .map(a -> {
                    Reuniao r = reunioes.get(a.getReuniaoId());
                    return new ClienteRiscoResponse(r.getClienteId(), nomesClientes.get(r.getClienteId()), r.getId(),
                            r.getTitulo(), a.getRiscoChurn(), a.getSentimento(), a.getRecomendacao());
                })
                .toList();
    }

    private Dados carregar() {
        return consultar(con -> new Dados(
                new ClienteDAO(con).listar(null),
                new ReuniaoDAO(con).listar(null, null, null, null),
                new AnaliseDAO(con).listar()));
    }

    private static <T> Map<String, Long> contar(List<T> itens, Function<T, String> chave) {
        return itens.stream().collect(Collectors.groupingBy(chave, TreeMap::new, Collectors.counting()));
    }
}
