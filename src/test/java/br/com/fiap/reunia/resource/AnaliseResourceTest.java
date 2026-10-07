package br.com.fiap.reunia.resource;

import io.quarkus.test.junit.QuarkusTest;
import org.junit.jupiter.api.Test;

import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.hasItem;

/**
 * Testa a API de ponta a ponta nos endpoints que nao dependem do Oracle.
 */
@QuarkusTest
class AnaliseResourceTest {

    @Test
    void simularAnaliseRetornaIndicadores() {
        given()
                .contentType("application/json")
                .body("{\"texto\": \"Cliente insatisfeito com o Protheus, avaliando cancelar o contrato.\"}")
                .when().post("/analises/simular")
                .then()
                .statusCode(200)
                .body("sentimento", equalTo("NEGATIVO"))
                .body("produtos", hasItem("Protheus"))
                .body("nivelRiscoChurn", equalTo("MEDIO"));
    }

    @Test
    void textoCurtoRetorna400ComErroPadronizado() {
        given()
                .contentType("application/json")
                .body("{\"texto\": \"curto\"}")
                .when().post("/analises/simular")
                .then()
                .statusCode(400)
                .body("status", equalTo(400))
                .body("mensagem", equalTo("A transcrição deve ter entre 20 e 100000 caracteres."));
    }

    @Test
    void rotaInexistenteRetorna404() {
        given().when().get("/nao-existe").then().statusCode(404);
    }
}
