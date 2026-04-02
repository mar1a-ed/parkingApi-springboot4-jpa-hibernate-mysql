package com.dev.park_api;

import com.dev.park_api.entity.Cliente;
import com.dev.park_api.web.dto.ClienteCreateDto;
import com.dev.park_api.web.dto.ClienteResponseDto;
import com.dev.park_api.web.dto.PageableDto;
import com.dev.park_api.web.exception.ErrorMessage;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.test.web.reactive.server.WebTestClient;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@Sql(scripts = "/sql/clientes/insert-clientes.sql", executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
@Sql(scripts = "/sql/clientes/delete-clientes.sql", executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD)
public class ClienteIT {

    @Autowired
    WebTestClient testClient;

    @Test
    public void criarCliente_ComDadosValidos_RetornarStatus201(){
        ClienteResponseDto responseBody = testClient
                .post()
                .uri("/api/v1/clientes")
                .contentType(MediaType.APPLICATION_JSON)
                .headers(JwtAuthentication.getHeaderAuthorization(testClient, "tobby@email.com", "151515"))
                .bodyValue(new ClienteCreateDto("Tobias Ferreira", "61739776054"))
                .exchange()
                .expectStatus().isCreated()
                .expectBody(ClienteResponseDto.class)
                .returnResult().getResponseBody();

        Assertions.assertThat(responseBody).isNotNull();
        Assertions.assertThat(responseBody.getId()).isNotNull();
        Assertions.assertThat(responseBody.getNome()).isEqualTo("Tobias Ferreira");
        Assertions.assertThat(responseBody.getCpf()).isEqualTo("61739776054");
    }

    @Test
    public void criarCliente_ComCpfJaCadastrado_RetornarStatus409(){
        ErrorMessage responseBody = testClient
                .post()
                .uri("/api/v1/clientes")
                .contentType(MediaType.APPLICATION_JSON)
                .headers(JwtAuthentication.getHeaderAuthorization(testClient, "tobby@email.com", "151515"))
                .bodyValue(new ClienteCreateDto("Tobias Ferreira", "44797978074"))
                .exchange()
                .expectStatus().isEqualTo(409)
                .expectBody(ErrorMessage.class)
                .returnResult().getResponseBody();

        Assertions.assertThat(responseBody).isNotNull();
        Assertions.assertThat(responseBody.getStatus()).isEqualTo(409);
    }

    @Test
    public void criarCliente_ComDadosInvalidos_RetornarStatus422(){
        ErrorMessage responseBody = testClient
                .post()
                .uri("/api/v1/clientes")
                .contentType(MediaType.APPLICATION_JSON)
                .headers(JwtAuthentication.getHeaderAuthorization(testClient, "tobby@email.com", "151515"))
                .bodyValue(new ClienteCreateDto("", ""))
                .exchange()
                .expectStatus().isEqualTo(422)
                .expectBody(ErrorMessage.class)
                .returnResult().getResponseBody();

        Assertions.assertThat(responseBody).isNotNull();
        Assertions.assertThat(responseBody.getStatus()).isEqualTo(422);

        responseBody = testClient
                .post()
                .uri("/api/v1/clientes")
                .contentType(MediaType.APPLICATION_JSON)
                .headers(JwtAuthentication.getHeaderAuthorization(testClient, "tobby@email.com", "151515"))
                .bodyValue(new ClienteCreateDto("Tobi", "00000000000"))
                .exchange()
                .expectStatus().isEqualTo(422)
                .expectBody(ErrorMessage.class)
                .returnResult().getResponseBody();

        Assertions.assertThat(responseBody).isNotNull();
        Assertions.assertThat(responseBody.getStatus()).isEqualTo(422);

        responseBody = testClient
                .post()
                .uri("/api/v1/clientes")
                .contentType(MediaType.APPLICATION_JSON)
                .headers(JwtAuthentication.getHeaderAuthorization(testClient, "tobby@email.com", "151515"))
                .bodyValue(new ClienteCreateDto("Tobi", "447.979.780-74"))
                .exchange()
                .expectStatus().isEqualTo(422)
                .expectBody(ErrorMessage.class)
                .returnResult().getResponseBody();

        Assertions.assertThat(responseBody).isNotNull();
        Assertions.assertThat(responseBody.getStatus()).isEqualTo(422);
    }

    @Test
    public void criarCliente_ComUsuarioNaoPermitido_RetornarStatus403(){
        ErrorMessage responseBody = testClient
                .post()
                .uri("/api/v1/clientes")
                .contentType(MediaType.APPLICATION_JSON)
                .headers(JwtAuthentication.getHeaderAuthorization(testClient, "ana@email.com", "131313"))
                .bodyValue(new ClienteCreateDto("Ana Silva", "44797978074"))
                .exchange()
                .expectStatus().isForbidden()
                .expectBody(ErrorMessage.class)
                .returnResult().getResponseBody();

        Assertions.assertThat(responseBody).isNotNull();
        Assertions.assertThat(responseBody.getStatus()).isEqualTo(403);
    }

    @Test
    public void buscarCliente_ComIdExistente_RetornarStatus200(){
        ClienteResponseDto responseBody = testClient
                .post()
                .uri("/api/v1/clientes/10")
                .headers(JwtAuthentication.getHeaderAuthorization(testClient, "ana@email.com", "131313"))
                .exchange()
                .expectStatus().isOk()
                .expectBody(ClienteResponseDto.class)
                .returnResult().getResponseBody();

        Assertions.assertThat(responseBody).isNotNull();
        Assertions.assertThat(responseBody.getId()).isEqualTo(10);
    }

    @Test
    public void buscarCliente_ComIdInexistente_RetornarStatus404(){
        ErrorMessage responseBody = testClient
                .post()
                .uri("/api/v1/clientes/99")
                .headers(JwtAuthentication.getHeaderAuthorization(testClient, "ana@email.com", "131313"))
                .exchange()
                .expectStatus().isNotFound()
                .expectBody(ErrorMessage.class)
                .returnResult().getResponseBody();

        Assertions.assertThat(responseBody).isNotNull();
        Assertions.assertThat(responseBody.getStatus()).isEqualTo(404);
    }

    @Test
    public void buscarCliente_ComRoleNaoPermitido_RetornarStatus403(){
        ErrorMessage responseBody = testClient
                .post()
                .uri("/api/v1/clientes/10")
                .headers(JwtAuthentication.getHeaderAuthorization(testClient, "bia@email.com", "141414"))
                .exchange()
                .expectStatus().isForbidden()
                .expectBody(ErrorMessage.class)
                .returnResult().getResponseBody();

        Assertions.assertThat(responseBody).isNotNull();
        Assertions.assertThat(responseBody.getStatus()).isEqualTo(403);
    }

    @Test
    public void buscarCliente_ComPaginacaoPeloAdmin_RetornarStatus200(){
        PageableDto responseBody = testClient
                .post()
                .uri("/api/v1/clientes")
                .headers(JwtAuthentication.getHeaderAuthorization(testClient, "ana@email.com", "131313"))
                .exchange()
                .expectStatus().isOk()
                .expectBody(PageableDto.class)
                .returnResult().getResponseBody();

        Assertions.assertThat(responseBody).isNotNull();
        Assertions.assertThat(responseBody.getContent().size()).isEqualTo(2);
        Assertions.assertThat(responseBody.getNumber()).isEqualTo(0);
        Assertions.assertThat(responseBody.getTotalPages()).isEqualTo(1);

        testClient
                .post()
                .uri("/api/v1/clientes?size=1&page=1")
                .headers(JwtAuthentication.getHeaderAuthorization(testClient, "ana@email.com", "131313"))
                .exchange()
                .expectStatus().isOk()
                .expectBody(PageableDto.class)
                .returnResult().getResponseBody();

        Assertions.assertThat(responseBody).isNotNull();
        Assertions.assertThat(responseBody.getContent().size()).isEqualTo(1);
        Assertions.assertThat(responseBody.getNumber()).isEqualTo(1);
        Assertions.assertThat(responseBody.getTotalPages()).isEqualTo(2);
    }

    @Test
    public void buscarCliente_ComPaginacaoPeloCliente_RetornarStatus403(){
        ErrorMessage responseBody = testClient
                .post()
                .uri("/api/v1/clientes")
                .headers(JwtAuthentication.getHeaderAuthorization(testClient, "bia@email.com", "141414"))
                .exchange()
                .expectStatus().isForbidden()
                .expectBody(ErrorMessage.class)
                .returnResult().getResponseBody();

        Assertions.assertThat(responseBody).isNotNull();
        Assertions.assertThat(responseBody.getStatus()).isEqualTo(403);

        testClient
                .post()
                .uri("/api/v1/clientes?size=1&page=1")
                .headers(JwtAuthentication.getHeaderAuthorization(testClient, "bia@email.com", "141414"))
                .exchange()
                .expectStatus().isForbidden()
                .expectBody(ErrorMessage.class)
                .returnResult().getResponseBody();

        Assertions.assertThat(responseBody).isNotNull();
        Assertions.assertThat(responseBody.getStatus()).isEqualTo(403);

    }

    @Test
    public void buscarCliente_ComDadosDoTokenCliente_RetornarStatus200() {
        ClienteResponseDto responseBody = testClient
                .post()
                .uri("/api/v1/clientes/detalhes")
                .headers(JwtAuthentication.getHeaderAuthorization(testClient, "bia@email.com", "141414"))
                .exchange()
                .expectStatus().isOk()
                .expectBody(ClienteResponseDto.class)
                .returnResult().getResponseBody();

        Assertions.assertThat(responseBody).isNotNull();
        Assertions.assertThat(responseBody.getCpf()).isEqualTo("48601342060");
        Assertions.assertThat(responseBody.getNome()).isEqualTo("Bianca Silva");

    }

    @Test
    public void buscarCliente_ComDadosDoTokenAdmin_RetornarStatus403() {
        ErrorMessage responseBody = testClient
                .post()
                .uri("/api/v1/clientes/detalhes")
                .headers(JwtAuthentication.getHeaderAuthorization(testClient, "admin@email.com", "131313"))
                .exchange()
                .expectStatus().isForbidden()
                .expectBody(ErrorMessage.class)
                .returnResult().getResponseBody();

        Assertions.assertThat(responseBody).isNotNull();
        Assertions.assertThat(responseBody.getStatus()).isEqualTo(403);

    }
}
