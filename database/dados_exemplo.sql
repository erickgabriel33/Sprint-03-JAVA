/* =====================================================================
   REUN.IA - Dados de exemplo (Oracle)
   ---------------------------------------------------------------------
   As tabelas sao criadas automaticamente pelo Spring
   (spring.jpa.hibernate.ddl-auto=update). Por isso:
   1. Rode a aplicacao (classe ReuniaApplication) UMA vez para criar as tabelas.
   2. Execute este script inteiro (F5) no SQL Developer, uma unica vez.
   Senha de todos os usuarios de exemplo: Totvs@2026
   ===================================================================== */

-- USUARIOS
INSERT INTO reunia_usuarios (nome, email, senha, cargo, empresa) VALUES ('Erick Gabriel Ferreira dos Santos', 'erick@totvs.com.br', 'Totvs@2026', 'Tech Lead', 'TOTVS');
INSERT INTO reunia_usuarios (nome, email, senha, cargo, empresa) VALUES ('Mariana Souza Lima', 'mariana.lima@totvs.com.br', 'Totvs@2026', 'Executiva de Vendas', 'TOTVS');
INSERT INTO reunia_usuarios (nome, email, senha, cargo, empresa) VALUES ('Rafael Costa Pereira', 'rafael.pereira@totvs.com.br', 'Totvs@2026', 'Analista de Customer Success', 'TOTVS');
INSERT INTO reunia_usuarios (nome, email, senha, cargo, empresa) VALUES ('Juliana Alves Rocha', 'juliana.rocha@totvs.com.br', 'Totvs@2026', 'Gerente Comercial', 'TOTVS');
INSERT INTO reunia_usuarios (nome, email, senha, cargo, empresa) VALUES ('Bruno Martins Teixeira', 'bruno.teixeira@totvs.com.br', 'Totvs@2026', 'Executivo de Contas', 'TOTVS');

-- CLIENTES (CNPJs ficticios, com digitos verificadores validos)
INSERT INTO reunia_clientes (nome, cnpj, segmento, porte, email) VALUES ('Transportadora TransBrasil', '11222333000181', 'Logistica', 'GRANDE', 'ti@transbrasil.com.br');
INSERT INTO reunia_clientes (nome, cnpj, segmento, porte, email) VALUES ('Rede Varejo Mais', '48291567000190', 'Varejo', 'MEDIO', 'compras@varejomais.com.br');
INSERT INTO reunia_clientes (nome, cnpj, segmento, porte, email) VALUES ('Industria Metalurgica Sul', '73140628000166', 'Industria', 'GRANDE', 'diretoria@metalsul.com.br');
INSERT INTO reunia_clientes (nome, cnpj, segmento, porte, email) VALUES ('FinTech Alfa Digital', '26503814000132', 'Servicos Financeiros', 'PEQUENO', 'ops@alfadigital.com.br');
INSERT INTO reunia_clientes (nome, cnpj, segmento, porte, email) VALUES ('Agro Campo Verde', '59017463000109', 'Agronegocio', 'MEDIO', 'gestao@campoverde.agr.br');

-- REUNIOES DE VENDAS (concorrente: 1 = sim, 0 = nao)
INSERT INTO reunia_reunioes (titulo, data_hora, duracao_minutos, tipo, status, processada, codigo_usuario, codigo_cliente, valor_oportunidade, score_risco, concorrente)
VALUES ('Apresentacao Modulo Logistica', TIMESTAMP '2026-10-14 10:00:00', 45, 'VENDAS', 'AGENDADA', 0, 2, 1, 85000, 80, 1);
INSERT INTO reunia_reunioes (titulo, data_hora, duracao_minutos, tipo, status, processada, codigo_usuario, codigo_cliente, valor_oportunidade, score_risco, concorrente)
VALUES ('Alinhamento Renovacao CRM', TIMESTAMP '2026-10-14 15:00:00', 30, 'VENDAS', 'AGENDADA', 0, 2, 2, 32000, 25, 0);
INSERT INTO reunia_reunioes (titulo, data_hora, duracao_minutos, tipo, status, processada, codigo_usuario, codigo_cliente, valor_oportunidade, score_risco, concorrente)
VALUES ('Demonstracao Protheus Agro', TIMESTAMP '2026-11-03 09:30:00', 60, 'VENDAS', 'AGENDADA', 0, 5, 5, 120000, 30, 0);

-- REUNIOES DE CS (sinal_churn: 1 = sim, 0 = nao)
INSERT INTO reunia_reunioes (titulo, data_hora, duracao_minutos, tipo, status, processada, codigo_usuario, codigo_cliente, nps, reclamacoes, sinal_churn)
VALUES ('QBR - Diagnostico de Operacao', TIMESTAMP '2026-10-15 14:00:00', 60, 'CS', 'AGENDADA', 0, 3, 3, 4, 5, 1);
INSERT INTO reunia_reunioes (titulo, data_hora, duracao_minutos, tipo, status, processada, codigo_usuario, codigo_cliente, nps, reclamacoes, sinal_churn)
VALUES ('Check-in Trimestral de Sucesso', TIMESTAMP '2026-10-15 16:30:00', 40, 'CS', 'AGENDADA', 0, 3, 4, 10, 0, 0);
INSERT INTO reunia_reunioes (titulo, data_hora, duracao_minutos, tipo, status, processada, codigo_usuario, codigo_cliente, nps, reclamacoes, sinal_churn)
VALUES ('Follow-up Suporte Fluig', TIMESTAMP '2026-11-05 11:00:00', 30, 'CS', 'AGENDADA', 0, 4, 2, 7, 2, 0);

COMMIT;

/* As transcricoes e analises sao geradas pela propria API.
   Use as requisicoes "Enviar transcricao" da colecao do Insomnia (pasta docs)
   ou a tela de detalhes da reuniao (http://localhost:8080/telas/reunioes). */
