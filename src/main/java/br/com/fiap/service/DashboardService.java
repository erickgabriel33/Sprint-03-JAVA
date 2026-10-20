package br.com.fiap.service;

import br.com.fiap.model.dto.ClienteRiscoDTO;
import br.com.fiap.model.dto.DashboardDTO;
import br.com.fiap.model.entity.Analise;
import br.com.fiap.model.entity.Cliente;
import br.com.fiap.model.entity.Reuniao;
import br.com.fiap.model.repository.AnaliseRepository;
import br.com.fiap.model.repository.ClienteRepository;
import br.com.fiap.model.repository.ReuniaoRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class DashboardService {

    private static final int RISCO_CHURN_ALERTA = 60;

    @Autowired
    private ClienteRepository clienteRepository;

    @Autowired
    private ReuniaoRepository reuniaoRepository;

    @Autowired
    private AnaliseRepository analiseRepository;

    public DashboardDTO gerarResumo() {
        List<Reuniao> reunioes = reuniaoRepository.findAll();
        long agendadas = 0;
        long realizadas = 0;
        long altaPrioridade = 0;
        double valorEmNegociacao = 0;
        double somaNps = 0;
        int quantidadeNps = 0;

        for (Reuniao reuniao : reunioes) {
            if ("AGENDADA".equals(reuniao.getStatus())) agendadas++;
            if ("REALIZADA".equals(reuniao.getStatus())) realizadas++;
            if ("CANCELADA".equals(reuniao.getStatus())) continue;

            if (reuniao.calcularPrioridade() >= 70) altaPrioridade++;
            if ("VENDAS".equals(reuniao.getTipo())) {
                valorEmNegociacao += reuniao.getValorOportunidade();
            } else {
                somaNps += reuniao.getNps();
                quantidadeNps++;
            }
        }
        double npsMedio = quantidadeNps == 0 ? 0 : Math.round(somaNps / quantidadeNps * 10) / 10.0;

        return new DashboardDTO(clienteRepository.count(), reunioes.size(), agendadas, realizadas,
                altaPrioridade, listarClientesEmRisco().size(), valorEmNegociacao, npsMedio);
    }

    // Reunioes analisadas com risco de churn >= 60, do maior risco para o menor
    public List<ClienteRiscoDTO> listarClientesEmRisco() {
        List<Analise> analises = analiseRepository.findByRiscoChurnGreaterThanEqual(RISCO_CHURN_ALERTA);
        analises.sort((a, b) -> b.getRiscoChurn() - a.getRiscoChurn());

        List<ClienteRiscoDTO> clientesEmRisco = new ArrayList<>();
        for (Analise analise : analises) {
            Reuniao reuniao = reuniaoRepository.findByCodigo(analise.getCodigoReuniao());
            Cliente cliente = clienteRepository.findByCodigo(reuniao.getCodigoCliente());
            clientesEmRisco.add(new ClienteRiscoDTO(cliente.getCodigo(), cliente.getNome(), reuniao.getCodigo(),
                    reuniao.getTitulo(), analise.getRiscoChurn(), analise.getSentimento(), analise.getRecomendacao()));
        }
        return clientesEmRisco;
    }
}
