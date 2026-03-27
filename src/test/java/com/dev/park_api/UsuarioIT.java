package com.dev.park_api;

import com.dev.park_api.web.dto.UsuarioCreateDto;
import com.dev.park_api.web.dto.UsuarioResponseDto;
import com.dev.park_api.web.dto.UsuarioSenhaDto;
import com.dev.park_api.web.exception.ErrorMessage;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.test.web.reactive.server.WebTestClient;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@Sql(scripts = "/sql/usuarios/usuarios-insert.sql", executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
@Sql(scripts = "/sql/usuarios/usuarios-delete.sql", executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD)
public class UsuarioIT {

    @Autowired
    WebTestClient testClient;

    @Test
    public void createUsuario_usernameEPasswordValidos_RetornarStatus201(){
        UsuarioResponseDto responseDto = testClient
                .post()
                .uri("/api/v1/usuarios")
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(new UsuarioCreateDto("tody@email.com", "123456"))
                .exchange()
                .expectStatus().isCreated()
                .expectBody(UsuarioResponseDto.class)
                .returnResult().getResponseBody();

        Assertions.assertThat(responseDto).isNotNull();
        Assertions.assertThat(responseDto.getId()).isNotNull();
        Assertions.assertThat(responseDto.getUsername()).isEqualTo("tody@email.com");
        Assertions.assertThat(responseDto.getRole()).isEqualTo("CLIENTE");
    }

    @Test
    public void createUsuario_usernameInvalido_RetornarStatus422(){
        ErrorMessage responseDto = testClient
                .post()
                .uri("/api/v1/usuarios")
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(new UsuarioCreateDto("","123456"))
                .exchange()
                .expectStatus().isEqualTo(422)
                .expectBody(ErrorMessage.class)
                .returnResult().getResponseBody();

        Assertions.assertThat(responseDto).isNotNull();
        Assertions.assertThat(responseDto.getStatus()).isEqualTo(422);

        responseDto = testClient
                .post()
                .uri("/api/v1/usuarios")
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(new UsuarioCreateDto("tody@","123456"))
                .exchange()
                .expectStatus().isEqualTo(422)
                .expectBody(ErrorMessage.class)
                .returnResult().getResponseBody();

        Assertions.assertThat(responseDto).isNotNull();
        Assertions.assertThat(responseDto.getStatus()).isEqualTo(422);

        responseDto = testClient
                .post()
                .uri("/api/v1/usuarios")
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(new UsuarioCreateDto("tody@email","123456"))
                .exchange()
                .expectStatus().isEqualTo(422)
                .expectBody(ErrorMessage.class)
                .returnResult().getResponseBody();

        Assertions.assertThat(responseDto).isNotNull();
        Assertions.assertThat(responseDto.getStatus()).isEqualTo(422);
    }

    @Test
    public void createUsuario_passwordInvalido_RetornarStatus422(){
        ErrorMessage responseDto = testClient
                .post()
                .uri("/api/v1/usuarios")
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(new UsuarioCreateDto("tody@email.com",""))
                .exchange()
                .expectStatus().isEqualTo(422)
                .expectBody(ErrorMessage.class)
                .returnResult().getResponseBody();

        Assertions.assertThat(responseDto).isNotNull();
        Assertions.assertThat(responseDto.getStatus()).isEqualTo(422);

        responseDto = testClient
                .post()
                .uri("/api/v1/usuarios")
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(new UsuarioCreateDto("tody@email.com","123"))
                .exchange()
                .expectStatus().isEqualTo(422)
                .expectBody(ErrorMessage.class)
                .returnResult().getResponseBody();

        Assertions.assertThat(responseDto).isNotNull();
        Assertions.assertThat(responseDto.getStatus()).isEqualTo(422);

        responseDto = testClient
                .post()
                .uri("/api/v1/usuarios")
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(new UsuarioCreateDto("tody@email.com","123456789"))
                .exchange()
                .expectStatus().isEqualTo(422)
                .expectBody(ErrorMessage.class)
                .returnResult().getResponseBody();

        Assertions.assertThat(responseDto).isNotNull();
        Assertions.assertThat(responseDto.getStatus()).isEqualTo(422);
    }

    @Test
    public void createUsuario_usernameRepetido_RetornarStatus409(){
        ErrorMessage responseDto = testClient
                .post()
                .uri("/api/v1/usuarios")
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(new UsuarioCreateDto("bia@email.com", "123456"))
                .exchange()
                .expectStatus().isEqualTo(409)
                .expectBody(ErrorMessage.class)
                .returnResult().getResponseBody();

        Assertions.assertThat(responseDto).isNotNull();
        Assertions.assertThat(responseDto.getStatus()).isEqualTo(409);
    }

    @Test
    public void buscarUsuario_ComIdExistente_RetornarStatus200(){
        UsuarioResponseDto responseDto = testClient
                .get()
                .uri("/api/v1/usuarios/100")
                .exchange()
                .expectStatus().isOk()
                .expectBody(UsuarioResponseDto.class)
                .returnResult().getResponseBody();

        Assertions.assertThat(responseDto).isNotNull();
        Assertions.assertThat(responseDto.getId()).isEqualTo(100);
        Assertions.assertThat(responseDto.getUsername()).isEqualTo("ana@email.com");
        Assertions.assertThat(responseDto.getRole()).isEqualTo("ADMIN");

    }

    @Test
    public void buscarUsuario_ComIdInexistente_RetornarStatus404(){
        ErrorMessage responseDto = testClient
                .get()
                .uri("/api/v1/usuarios/99")
                .exchange()
                .expectStatus().isOk()
                .expectBody(ErrorMessage.class)
                .returnResult().getResponseBody();

        Assertions.assertThat(responseDto).isNotNull();
        Assertions.assertThat(responseDto.getStatus()).isEqualTo(404);

    }

    @Test
    public void editarSenha_dadosValidos_RetornarStatus204(){
        testClient
                .patch()
                .uri("/api/v1/usuarios/100")
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(new UsuarioSenhaDto("131313", "123456", "123456"))
                .exchange()
                .expectStatus().isNoContent();

    }

    @Test
    public void editarSenha_comIdInexistente_RetornarStatus404(){
        ErrorMessage responseDto = testClient
                .patch()
                .uri("/api/v1/usuarios/0")
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(new UsuarioSenhaDto("131313", "123456", "123456"))
                .exchange()
                .expectStatus().isNotFound()
                .expectBody(ErrorMessage.class)
                .returnResult().getResponseBody();

        Assertions.assertThat(responseDto).isNotNull();
        Assertions.assertThat(responseDto.getStatus()).isEqualTo(404);

    }

    @Test
    public void editarSenha_comCamposInvalidos_RetornarStatus422(){
        ErrorMessage responseDto = testClient
                .patch()
                .uri("/api/v1/usuarios/100")
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(new UsuarioSenhaDto("", "", ""))
                .exchange()
                .expectStatus().isEqualTo(422)
                .expectBody(ErrorMessage.class)
                .returnResult().getResponseBody();

        Assertions.assertThat(responseDto).isNotNull();
        Assertions.assertThat(responseDto.getStatus()).isEqualTo(422);

        responseDto = testClient
                .patch()
                .uri("/api/v1/usuarios/100")
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(new UsuarioSenhaDto("12345", "12345", "12345"))
                .exchange()
                .expectStatus().isEqualTo(422)
                .expectBody(ErrorMessage.class)
                .returnResult().getResponseBody();

        Assertions.assertThat(responseDto).isNotNull();
        Assertions.assertThat(responseDto.getStatus()).isEqualTo(422);

        responseDto = testClient
                .patch()
                .uri("/api/v1/usuarios/100")
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(new UsuarioSenhaDto("1234567", "1234567", "1234567"))
                .exchange()
                .expectStatus().isEqualTo(422)
                .expectBody(ErrorMessage.class)
                .returnResult().getResponseBody();

        Assertions.assertThat(responseDto).isNotNull();
        Assertions.assertThat(responseDto.getStatus()).isEqualTo(422);
    }

    @Test
    public void editarSenha_comSenhaInvalida_RetornarStatus400(){
        ErrorMessage responseDto = testClient
                .patch()
                .uri("/api/v1/usuarios/100")
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(new UsuarioSenhaDto("131313", "123456", "123455"))
                .exchange()
                .expectStatus().isEqualTo(400)
                .expectBody(ErrorMessage.class)
                .returnResult().getResponseBody();

        Assertions.assertThat(responseDto).isNotNull();
        Assertions.assertThat(responseDto.getStatus()).isEqualTo(400);

        responseDto = testClient
                .patch()
                .uri("/api/v1/usuarios/100")
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(new UsuarioSenhaDto("111313", "123456", "123456"))
                .exchange()
                .expectStatus().isEqualTo(400)
                .expectBody(ErrorMessage.class)
                .returnResult().getResponseBody();

        Assertions.assertThat(responseDto).isNotNull();
        Assertions.assertThat(responseDto.getStatus()).isEqualTo(400);

    }

    @Test
    public void listarUsuarios_comUsuariosExistentes_RetornarStatus200() {
        UsuarioResponseDto responseDto = testClient
                .get()
                .uri("/api/v1/usuarios")
                .exchange()
                .expectStatus().isEqualTo(200)
                .expectBody(UsuarioResponseDto.class)
                .returnResult().getResponseBody();

        Assertions.assertThat(responseDto).isNotNull();

    }

    @Test
    public void listarUsuarios_comUsuariosInexistentes_RetornarStatus404() {
        ErrorMessage responseDto = testClient
                .get()
                .uri("/api/v1/usuarios")
                .exchange()
                .expectStatus().isEqualTo(404)
                .expectBody(ErrorMessage.class)
                .returnResult().getResponseBody();

        Assertions.assertThat(responseDto).isNotNull();
        Assertions.assertThat(responseDto.getStatus()).isEqualTo(404);

    }
}
