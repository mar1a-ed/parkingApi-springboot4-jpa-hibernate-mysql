package com.dev.park_api.web.controller;

import com.dev.park_api.entity.ClienteVaga;
import com.dev.park_api.service.EstacionamentoService;
import com.dev.park_api.web.dto.EstacionamentoCreateDto;
import com.dev.park_api.web.dto.EstacionamentoResponseDto;
import com.dev.park_api.web.dto.mapper.ClienteVagaMapper;
import com.dev.park_api.web.exception.ErrorMessage;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.headers.Header;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.net.URI;

@Tag(name = "Estacionamentos", description = "Contém operações de registro de entrada e saída no sistema de veículos do estacionamento")
@RequiredArgsConstructor
@RestController
@RequestMapping("api/v1/estacionamentos")
public class EstacionamentoController {

    private final EstacionamentoService estacionamentoService;

    @Operation(summary = "Check-In no Estacionamento", description = "Recurso para estacionar um veículo no estacionamento." +
            "A requisição necessita de um bearer token para validar a autenticação de um 'ADMIN'",
            security = @SecurityRequirement(name = "security"),
            responses = {
                @ApiResponse(responseCode = "201", description = "Sucesso",
                    headers = @Header(name = HttpHeaders.LOCATION, description = "URL de acesso ao recurso criado"),
                    content = @Content(mediaType = "application/json;charset=UTF-8",
                        schema = @Schema(implementation = EstacionamentoResponseDto.class))
                ),
                @ApiResponse(responseCode = "404", description = "CPF não cadastrado no sistema ou nenhuma vaga foi localizada",
                    content = @Content(mediaType = "application/json;charset=UTF-8",
                        schema = @Schema(implementation = ErrorMessage.class))
                ),
                @ApiResponse(responseCode = "422", description = "Dados inválidos ou faltantes",
                    content = @Content(mediaType = "application/json;charset=UTF-8",
                        schema = @Schema(implementation = ErrorMessage.class))
                ),
                @ApiResponse(responseCode = "403", description = "Recurso não permitido a Role='CLIENTE'",
                    content = @Content(mediaType = "application/json;charset=UTF-8",
                        schema = @Schema(implementation = ErrorMessage.class))
                )
            }
    )
    @PostMapping("/check-in")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<EstacionamentoResponseDto> checkIn(@RequestBody @Valid EstacionamentoCreateDto dto){
        ClienteVaga clienteVaga = ClienteVagaMapper.toClienteVaga(dto);
        estacionamentoService.checkIn(clienteVaga);
        EstacionamentoResponseDto responseDto = ClienteVagaMapper.toDto(clienteVaga);
        URI location = ServletUriComponentsBuilder
                .fromCurrentRequestUri().path("/{recibo}")
                .buildAndExpand(clienteVaga.getRecibo())
                .toUri();

        return ResponseEntity.created(location).body(responseDto);
    }
}
