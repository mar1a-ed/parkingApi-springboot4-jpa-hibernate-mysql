package com.dev.park_api;

import com.dev.park_api.web.dto.EstacionamentoCreateDto;
import com.dev.park_api.web.dto.PageableDto;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.test.web.reactive.server.WebTestClient;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@Sql(scripts = "/sql/estacionamentos/estacionamentos-insert.sql", executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
@Sql(scripts = "/sql/estacionamentos/estacionamentos-delete.sql", executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD)
public class EstacionamentoIT {

    @Autowired
    WebTestClient testClient;

    @Test
    public void createCheckIn_ComDadosValidos_RetornarStatus201(){
        EstacionamentoCreateDto createDto = EstacionamentoCreateDto.builder()
                .placa("WER-1111")
                .marca("Fiat")
                .modelo("Palio")
                .cor("Azul")
                .cpfCliente("09191773016")
                .build();

        testClient.post().uri("/api/v1/estacionamentos/check-in")
                .contentType(MediaType.APPLICATION_JSON)
                .headers(JwtAuthentication.getHeaderAuthorization(testClient,"ana@email.com", "123456"))
                .bodyValue(createDto)
                .exchange()
                .expectStatus().isCreated()
                .expectHeader().exists(HttpHeaders.LOCATION)
                .expectBody()
                .jsonPath("placa").isEqualTo("WER-1111")
                .jsonPath("marca").isEqualTo("Fiat")
                .jsonPath("modelo").isEqualTo("Palio")
                .jsonPath("cor").isEqualTo("Azul")
                .jsonPath("cpfCliente").isEqualTo("09191773016")
                .jsonPath("recibo").exists()
                .jsonPath("dataEntrada").exists()
                .jsonPath("vagaCodigo").exists();
    }

    @Test
    public void createCheckIn_ComRoleCliente_RetornarStatus403(){
        EstacionamentoCreateDto createDto = EstacionamentoCreateDto.builder()
                .placa("WER-1111")
                .marca("Fiat")
                .modelo("Palio")
                .cor("Azul")
                .cpfCliente("09191773016")
                .build();

        testClient.post().uri("/api/v1/estacionamentos/check-in")
                .contentType(MediaType.APPLICATION_JSON)
                .headers(JwtAuthentication.getHeaderAuthorization(testClient,"bia@email.com", "123456"))
                .bodyValue(createDto)
                .exchange()
                .expectStatus().isForbidden()
                .expectBody()
                .jsonPath("status").isEqualTo(403)
                .jsonPath("path").isEqualTo("api/v1/estacionamentos/check-in")
                .jsonPath("method").isEqualTo("POST");
    }

    @Test
    public void createCheckIn_ComDadosInvalidos_RetornarStatus422(){
        EstacionamentoCreateDto createDto = EstacionamentoCreateDto.builder()
                .placa("")
                .marca("")
                .modelo("")
                .cor("")
                .cpfCliente("")
                .build();

        testClient.post().uri("/api/v1/estacionamentos/check-in")
                .contentType(MediaType.APPLICATION_JSON)
                .headers(JwtAuthentication.getHeaderAuthorization(testClient,"bia@email.com", "123456"))
                .bodyValue(createDto)
                .exchange()
                .expectStatus().isEqualTo(422)
                .expectBody()
                .jsonPath("status").isEqualTo(422)
                .jsonPath("path").isEqualTo("api/v1/estacionamentos/check-in")
                .jsonPath("method").isEqualTo("POST");
    }

    @Test
    public void createCheckIn_ComCpfInexistente_RetornarStatus404(){
        EstacionamentoCreateDto createDto = EstacionamentoCreateDto.builder()
                .placa("WER-1111")
                .marca("Fiat")
                .modelo("Palio")
                .cor("Azul")
                .cpfCliente("66416725061")
                .build();

        testClient.post().uri("/api/v1/estacionamentos/check-in")
                .contentType(MediaType.APPLICATION_JSON)
                .headers(JwtAuthentication.getHeaderAuthorization(testClient,"ana@email.com", "123456"))
                .bodyValue(createDto)
                .exchange()
                .expectStatus().isNotFound()
                .expectBody()
                .jsonPath("status").isEqualTo(404)
                .jsonPath("path").isEqualTo("api/v1/estacionamentos/check-in")
                .jsonPath("method").isEqualTo("POST");
    }

    @Sql(scripts = "/sql/estacionamentos/estacionamentos-insert-vagasocupadas.sql", executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
    @Sql(scripts = "/sql/estacionamentos/estacionamentos-delete-vagasocupadas.sql", executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD)
    @Test
    public void createCheckIn_ComNenhumaVagaDisponivel_RetornarStatus404(){
        EstacionamentoCreateDto createDto = EstacionamentoCreateDto.builder()
                .placa("WER-1111")
                .marca("Fiat")
                .modelo("Palio")
                .cor("Azul")
                .cpfCliente("98481203015")
                .build();

        testClient.post().uri("/api/v1/estacionamentos/check-in")
                .contentType(MediaType.APPLICATION_JSON)
                .headers(JwtAuthentication.getHeaderAuthorization(testClient,"ana@email.com", "123456"))
                .bodyValue(createDto)
                .exchange()
                .expectStatus().isNotFound()
                .expectBody()
                .jsonPath("status").isEqualTo(404)
                .jsonPath("path").isEqualTo("api/v1/estacionamentos/check-in")
                .jsonPath("method").isEqualTo("POST");
    }

    @Test
    public void findCheckIn_ComAdmin_RetornarStatus200(){
        testClient.get().uri("/api/v1/estacionamentos/check-in/{recibo}", "20260403-102900")
                .headers(JwtAuthentication.getHeaderAuthorization(testClient,"ana@email.com", "123456"))
                .exchange()
                .expectStatus().isOk()
                .expectBody()
                .jsonPath("placa").isEqualTo("FIT-1020")
                .jsonPath("marca").isEqualTo("Fiat")
                .jsonPath("modelo").isEqualTo("Palio")
                .jsonPath("cor").isEqualTo("Verde")
                .jsonPath("cpfCliente").isEqualTo("98481203015")
                .jsonPath("recibo").isEqualTo("20260403-102900")
                .jsonPath("dataEntrada").isEqualTo("2026-04-03 10:29:00")
                .jsonPath("vagaCodigo").isEqualTo("A-01");
    }

    @Test
    public void findCheckIn_ComCliente_RetornarStatus200(){
        testClient.get().uri("/api/v1/estacionamentos/check-in/{recibo}", "20260403-102900")
                .headers(JwtAuthentication.getHeaderAuthorization(testClient,"bob@email.com", "123456"))
                .exchange()
                .expectStatus().isOk()
                .expectBody()
                .jsonPath("placa").isEqualTo("FIT-1020")
                .jsonPath("marca").isEqualTo("Fiat")
                .jsonPath("modelo").isEqualTo("Palio")
                .jsonPath("cor").isEqualTo("Verde")
                .jsonPath("cpfCliente").isEqualTo("98481203015")
                .jsonPath("recibo").isEqualTo("20260403-102900")
                .jsonPath("dataEntrada").isEqualTo("2026-04-03 10:29:00")
                .jsonPath("vagaCodigo").isEqualTo("A-01");
    }

    @Test
    public void findCheckIn_ComReciboInexistente_RetornarStatus404(){
        testClient.get().uri("/api/v1/estacionamentos/check-in/{recibo}", "20250403-102900")
                .headers(JwtAuthentication.getHeaderAuthorization(testClient,"bob@email.com", "123456"))
                .exchange()
                .expectStatus().isOk()
                .expectBody()
                .jsonPath("status").isEqualTo("404")
                .jsonPath("path").isEqualTo("/api/v1/estacionamentos/check-in/20250403-102900")
                .jsonPath("method").isEqualTo("GET");
    }

    @Test
    public void createCheckOut_ComReciboExistente_RetornarStatus200(){
        testClient.put().uri("/api/v1/estacionamentos/check-out/{recibo}", "20260403-102900")
                .headers(JwtAuthentication.getHeaderAuthorization(testClient,"ana@email.com", "123456"))
                .exchange()
                .expectStatus().isOk()
                .expectBody()
                .jsonPath("placa").isEqualTo("FIT-1020")
                .jsonPath("marca").isEqualTo("Fiat")
                .jsonPath("modelo").isEqualTo("Palio")
                .jsonPath("cor").isEqualTo("Verde")
                .jsonPath("cpfCliente").isEqualTo("98481203015")
                .jsonPath("recibo").isEqualTo("20260403-102900")
                .jsonPath("dataEntrada").isEqualTo("2026-04-03 10:29:00")
                .jsonPath("vagaCodigo").isEqualTo("A-01")
                .jsonPath("dataSaida").exists()
                .jsonPath("valor").exists()
                .jsonPath("desconto").exists();
    }

    @Test
    public void createCheckOut_ComReciboInexistente_RetornarStatus404() {
        testClient.put().uri("/api/v1/estacionamentos/check-out/{recibo}", "20230403-102900")
                .headers(JwtAuthentication.getHeaderAuthorization(testClient, "ana@email.com", "123456"))
                .exchange()
                .expectStatus().isNotFound()
                .expectBody()
                .jsonPath("status").isEqualTo("404")
                .jsonPath("path").isEqualTo("/api/v1/estacionamentos/check-out/20230403-102900")
                .jsonPath("method").isEqualTo("PUT");
    }

    @Test
    public void createCheckOut_ComRoleCliente_RetornarStatus403() {
        testClient.put().uri("/api/v1/estacionamentos/check-out/{recibo}", "20260403-102900")
                .headers(JwtAuthentication.getHeaderAuthorization(testClient, "bob@email.com", "123456"))
                .exchange()
                .expectStatus().isForbidden()
                .expectBody()
                .jsonPath("status").isEqualTo("403")
                .jsonPath("path").isEqualTo("/api/v1/estacionamentos/check-out/20260403-102900")
                .jsonPath("method").isEqualTo("PUT");
    }

    @Test
    public void buscarEstacionamento_PorCpfCliente_RetornarStatus200() {
        PageableDto responseBody = testClient.get().uri("/api/v1/estacionamentos/{cpf}?size=1&page=0", "98481203015")
                .headers(JwtAuthentication.getHeaderAuthorization(testClient, "ana@email.com", "123456"))
                .exchange()
                .expectStatus().isOk()
                .expectBody(PageableDto.class)
                .returnResult().getResponseBody();

        Assertions.assertThat(responseBody).isNotNull();
        Assertions.assertThat(responseBody.getContent().size()).isEqualTo(1);
    }

    @Test
    public void buscarEstacionamento_ComRoleCliente_RetornarStatus403() {
        testClient.get().uri("/api/v1/estacionamentos/{cpf}?size=1&page=0", "98481203015")
                .headers(JwtAuthentication.getHeaderAuthorization(testClient, "bia@email.com", "123456"))
                .exchange()
                .expectStatus().isForbidden()
                .expectBody()
                .jsonPath("status").isEqualTo("403")
                .jsonPath("path").isEqualTo("/api/v1/estacionamentos/cpf/98481203015")
                .jsonPath("method").isEqualTo("GET");
    }

    @Test
    public void buscarEstacionamento_ComRoleCliente_RetornarStatus200() {
        PageableDto responseBody = testClient.get().uri("/api/v1/estacionamentos/size=1&page=0")
                .headers(JwtAuthentication.getHeaderAuthorization(testClient, "bob@email.com", "123456"))
                .exchange()
                .expectStatus().isOk()
                .expectBody(PageableDto.class)
                .returnResult().getResponseBody();

        Assertions.assertThat(responseBody).isNotNull();
        Assertions.assertThat(responseBody.getContent().size()).isEqualTo(1);

    }

    @Test
    public void buscarEstacionamento_ComRoleAdmin_RetornarStatus403() {
        testClient.get().uri("/api/v1/estacionamentos")
                .headers(JwtAuthentication.getHeaderAuthorization(testClient, "ana@email.com", "123456"))
                .exchange()
                .expectStatus().isForbidden()
                .expectBody()
                .jsonPath("status").isEqualTo("403")
                .jsonPath("path").isEqualTo("/api/v1/estacionamentos")
                .jsonPath("method").isEqualTo("GET");
    }
}
