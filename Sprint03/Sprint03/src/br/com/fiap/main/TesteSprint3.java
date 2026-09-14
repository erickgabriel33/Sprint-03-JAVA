package br.com.fiap.main;

import br.com.fiap.dao.UsuarioDAO;
import br.com.fiap.dao.UsuarioDAOImpl;
import br.com.fiap.exception.NegocioException;
import br.com.fiap.model.Reuniao;
import br.com.fiap.model.ReuniaoCS;
import br.com.fiap.model.ReuniaoVendas;
import br.com.fiap.model.Usuario;

import java.util.ArrayList;
import java.util.List;

public class TesteSprint3 {

    static void main() {

        System.out.println("  REUN.IA - SPRINT 3 (TESTE DE REGRAS, STREAMS E JDBC DAO)");
    

        try {
            Usuario resp = new Usuario(1, "Erick Gabriel Ferreira dos Santos", "erick@totvs.com.br", "Tech Lead", "TOTVS");

            // 1. REGRAS DE NEGÓCIO EM MEMÓRIA
            System.out.println("--- 1. REGRAS DE NEGÓCIO (MÉTODOS DE RELEVÂNCIA) ---");
            List<Reuniao> reunioes = new ArrayList<>();

            reunioes.add(new ReuniaoVendas(101, "Apresentação Módulo Logística", "14/09/2026", 45, true,
                    resp, "Transportadora TransBrasil", 85000.0, 80, true));
            reunioes.add(new ReuniaoVendas(102, "Alinhamento Renovação CRM", "14/09/2026", 30, true,
                    resp, "Rede Varejo Mais", 32000.0, 25, false));
            reunioes.add(new ReuniaoCS(201, "QBR - Diagnóstico de Operação", "15/09/2026", 60, true,
                    resp, "Indústria Metalúrgica Sul", 4, 5, true));
            reunioes.add(new ReuniaoCS(202, "Check-in Trimestral de Sucesso", "15/09/2026", 40, true,
                    resp, "FinTech Alfa Digital", 10, 0, false));

            for (Reuniao r : reunioes) {
                System.out.println("\n[Tipo]: " + r.tipoReuniao() + " | [Título]: " + r.getTitulo());
                System.out.println(" -> Prioridade Ponderada: " + String.format("%.2f", r.calcularPrioridade()));

                if (r instanceof ReuniaoVendas rv) {
                    System.out.println(" -> Classificação de Risco: " + rv.classificarRisco());
                    System.out.println(" -> Alerta Concorrência: " + rv.alertaConcorrencia());
                } else if (r instanceof ReuniaoCS rcs) {
                    System.out.println(" -> Classificação NPS: " + rcs.classificarNPS());
                    System.out.println(" -> Relatório Retenção: " + rcs.relatorioRetencao());
                }
            }

            // 2. STREAM API (AULA 30)
            System.out.println("\n--- 2. PROCESSAMENTO DECLARATIVO (STREAM API) ---");
            List<Reuniao> criticas = reunioes.stream()
                    .filter(r -> r.calcularPrioridade() >= 50.0)
                    .toList();

            System.out.println("Reuniões de Alta Prioridade (Score >= 50):");
            criticas.forEach(c -> System.out.println(" - " + c.getTitulo() + " (Score: " + String.format("%.2f", c.calcularPrioridade()) + ")"));

            // 3. PERSISTÊNCIA DAO (CRUD COMPLETO COM TRANSAÇÃO)
            System.out.println("\n--- 3. PERSISTÊNCIA COM DAO & JDBC ---");
            UsuarioDAO dao = new UsuarioDAOImpl();

            String emailTeste = "erick." + System.currentTimeMillis() + "@totvs.com.br";
            Usuario novoUsuario = new Usuario("Erick Gabriel", emailTeste, "Engenheiro de Software", "TOTVS");

            // Create
            dao.salvar(novoUsuario);
            System.out.println("✓ [CREATE] Usuário persistido com commit.");

            // Read
            List<Usuario> lista = dao.listarTodos();
            System.out.println("✓ [READ] Registros encontrados no banco: " + lista.size());

            if (!lista.isEmpty()) {
                Usuario ultimo = lista.getLast();
                System.out.println("   Registro consultado: " + ultimo);

                // Update
                ultimo.setCargo("Arquiteto de Software");
                boolean atualizado = dao.atualizar(ultimo);
                System.out.println("✓ [UPDATE] Atualização realizada? " + atualizado);

                // Delete
                boolean excluido = dao.excluir(ultimo.getId());
                System.out.println("✓ [DELETE] Exclusão realizada? " + excluido);
            }

        } catch (NegocioException e) {
            System.err.println("Exceção capturada: " + e.getMessage());
        }


        System.out.println("  TESTE CONCLUÍDO COM SUCESSO!");
    }
}