# Guia de entrega — Sprint 4 · DDD Java

## 1. Rodar o projeto no seu computador
1. Abra a pasta do projeto no IntelliJ (ele reconhece o `pom.xml`) e espere o Maven baixar as dependências.
2. Em `src/main/resources/application.properties`, troque:
   - `spring.datasource.username=seu_usuario` → seu RM (ex.: `rm565144`)
   - `spring.datasource.password=sua_senha` → sua senha do Oracle
3. Rode a classe `ReuniaApplication`. No console devem aparecer os `create table` das 5 tabelas `REUNIA_*`.
4. No SQL Developer, rode o arquivo `database/dados_exemplo.sql` (F5) **uma única vez**.
5. Abra http://localhost:8080/telas e teste.

## 2. Testar no Insomnia
- Insomnia → **Import** → **From File** → `docs/insomnia-reunia.json`.
- As pastas seguem a ordem: Usuários, Clientes, Reuniões, Transcrição e Análise, Dashboard.
- Rode primeiro as 4 requisições "enviar transcricao" para o painel ficar com dados.

## 3. Montar o PDF (20 pontos)
1. **Capa**: nome da equipe, nome da solução (Reun.IA), nome e RM de todos os integrantes
2. **Sumário**
3. **Objetivo e escopo**: use o primeiro parágrafo do README
4. **Funcionalidades**: use a seção "Regras de negócio" do README
5. **Tabela dos endpoints**: copie a tabela do README (URI, verbo e códigos de status)
6. **Protótipo, com prints das telas implementadas**: imagens de `docs/prints` (ou tire os seus com o projeto rodando)
7. **Modelo do banco de dados**: `docs/modelo-banco.png`
8. **Diagrama de classes**: `docs/diagrama-classes.png`

**Não coloque código-fonte nem script SQL no PDF.** Na Sprint 3 isso tirou pontos.

## 4. Entregar no Teams
- `.zip` do projeto **sem a pasta `target`**, com `pom.xml`, `mvnw`, `mvnw.cmd`, `.mvn`, `src`, `database` e `docs`
- O PDF
- O link do GitHub

## O que foi usado além do que aparece nos slides (e por quê)
| Item | Motivo |
|---|---|
| Camada `service` com `@Service` | A rubrica da Sprint 4 exige "Camada DAO e Service (validações)". Os slides não mostram Service; ela usa o mesmo `@Autowired` da aula. |
| `@Column(length = 4000)` e `@Column(length = 1000)` | Sem isso a coluna de texto do Oracle teria só 255 caracteres, e uma transcrição não caberia. |
| `@CrossOrigin(origins = "*")` nos controllers da API | Libera o site do grupo (front-end separado) para chamar a API pelo navegador. Se vocês usarem só as telas Thymeleaf, pode remover. |
| `#temporals` e `#numbers` no Thymeleaf | Mostram a data como `14/10/2026 10:00` e o valor como `R$ 85.000,00` nas telas. |
| `Normalizer` no `AnaliseService` | Tira os acentos do texto para comparar as palavras ("insatisfação" = "insatisfacao"). |

## Pontos de atenção
- A senha dos usuários é salva como texto puro, como na aula. Se o professor cobrar segurança, dá para guardar um hash.
- Antes de subir para o GitHub, lembre que o `application.properties` vai conter sua senha do Oracle.
- Teste em outro computador antes de entregar. "Projeto não executa" tirou 20 pontos na Sprint 3.
