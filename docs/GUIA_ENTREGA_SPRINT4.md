# Guia de entrega — Sprint 4 · DDD Java (prazo 14/10/2026)

## O que já está pronto (80 pontos do projeto Java)
- [x] Camada **Model**, com todas as classes batendo com as tabelas do banco (10 pts)
- [x] Camadas **DAO + Service**, com CRUD completo e validações (20 pts)
- [x] **API RESTful** com 27 endpoints para o Front-End (30 pts)
- [x] **Boas práticas**: nomenclatura, exceções tratadas e JSON de erro padronizado, padrões DAO/Factory/DTO/Service, transações e testes (20 pts)
- [x] Script Oracle (`database/reunia_oracle.sql`), testado em Oracle 23
- [x] Diagrama de classes, modelo do banco e arquitetura em `docs/*.png`
- [x] Tabela de endpoints no `README.md`, seção 3

## O que VOCÊ precisa fazer

### 1. Configurar e testar (≈ 30 min)
1. No SQL Developer, conectado com seu RM, rode o arquivo `database/reunia_oracle.sql` inteiro com F5.
   - **Atenção:** o script apaga e recria as tabelas `T_REUNIA_*`. Se a tabela `T_REUNIA_USUARIO` da Sprint 3 tiver dados importantes, faça backup antes.
2. Em `src/main/java/br/com/fiap/reunia/factory/ConnectionFactory.java`, troque `SEU_RM` e `SUA_SENHA`.
3. Rode `mvnw.cmd quarkus:dev` (Windows) e abra http://localhost:8080/q/swagger-ui.
4. Teste o `POST /auth/login` com `erick@totvs.com.br` / `Totvs@2026`.

### 2. Ligar o site do grupo à API
- URL base: `http://localhost:8080`. O CORS já está liberado para qualquer origem.
- Use o `fetch` ou o `axios` apontando para os endpoints da tabela do README.
- Telas sugeridas e o endpoint de cada uma:

| Tela | Endpoints |
|---|---|
| Login / Cadastro | `POST /auth/login`, `POST /auth/cadastro` |
| Dashboard | `GET /dashboard/resumo`, `GET /dashboard/clientes-risco`, `GET /reunioes/prioritarias` |
| Clientes (lista + formulário) | `GET/POST/PUT/DELETE /clientes` |
| Reuniões (lista + formulário) | `GET/POST/PUT/DELETE /reunioes`, `PATCH /reunioes/{id}/status` |
| Detalhe da reunião | `GET /reunioes/{id}`, `POST /reunioes/{id}/transcricao`, `GET /reunioes/{id}/analise` |

### 3. PDF da documentação (20 pts). Monte nesta ordem:
1. **Capa**: nome da equipe, nome da solução (Reun.IA) e nome + RM de todos os integrantes
2. **Sumário**
3. **Objetivo e escopo**: use o texto do topo do README
4. **Funcionalidades**: use as seções 3 e 4 do README
5. **Tabela de endpoints**: copie a tabela da seção 3 do README (URI, verbo, status)
6. **Protótipo**: **prints das telas implementadas do site** funcionando com a API. Isso é obrigatório. Se o site não ficar pronto, coloque prints do Swagger UI executando os endpoints, mas o ideal são as telas do site.
7. **Modelo do banco**: `docs/modelo-banco.png`, ou o diagrama do Data Modeler da disciplina de Database Design
8. **Diagrama de classes**: `docs/diagrama-classes.png`

**NÃO coloque código-fonte nem script SQL no PDF.** Isso tirou 15 pontos na Sprint 3.

### 4. Entrega no Teams
- `.zip` do projeto **sem** a pasta `target/`, contendo `pom.xml`, `mvnw`, `mvnw.cmd`, `.mvn/`, `src/`, `database/` e `docs/`
- PDF da documentação
- Link do GitHub

## Pontos de atenção
- O modelo de banco foi criado por mim porque o script da Sprint 3 não foi enviado. Se a disciplina de **Database Design** usa outras tabelas, alinhe com quem fez essa parte: ou vocês adotam este script nas duas disciplinas, ou me mandam o script de vocês para eu adaptar o Model e os DAOs.
- Antes de entregar, teste o projeto em outro computador (ou peça para um colega rodar). Foi justamente "não executa" que tirou 20 pontos na Sprint 3.
