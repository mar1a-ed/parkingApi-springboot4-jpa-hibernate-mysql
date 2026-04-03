package com.dev.park_api.web.controller;

import com.dev.park_api.entity.Vaga;
import com.dev.park_api.service.VagaService;
import com.dev.park_api.web.dto.VagaCreateDto;
import com.dev.park_api.web.dto.VagaResponseDto;
import com.dev.park_api.web.dto.mapper.VagaMapper;
import com.dev.park_api.web.exception.ErrorMessage;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.headers.Header;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.net.URI;

@Tag(name = "Vagas", description = "Contém as operações referentes ao recurso 'Vaga'")
@RequiredArgsConstructor
@RestController
@RequestMapping("api/v1/vagas")
public class VagaController {

    private final VagaService vagaService;

    @Operation(summary = "Criar uma nova vaga", description = "Recurso para criar uma nova vaga de estacionamento" +
            "Requisição necessita um bearer token. Acesso permitido somente a Role='ADMIN'",
            responses = {
                @ApiResponse(responseCode = "201", description = "Sucesso",
                    headers = @Header(name = HttpHeaders.LOCATION, description = "URL do recurso criado")
                ),
                @ApiResponse(responseCode = "409", description = "Vaga já cadastrada no sistema",
                    content = @Content(mediaType = "application/json;charset=UTF-8",
                        schema = @Schema(implementation = ErrorMessage.class)
                    )
                ),
                @ApiResponse(responseCode = "422", description = "Recurso não processado. Dados inválidos ou falta de dados",
                    content = @Content(mediaType = "application/json;charset=UTF-8",
                        schema = @Schema(implementation = ErrorMessage.class)
                    )
                )
            }
    )
    @PostMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Void> create(@RequestBody @Valid VagaCreateDto dto){
        Vaga vaga = VagaMapper.toVaga(dto);
        vagaService.salvar(vaga);
        URI location = ServletUriComponentsBuilder
                .fromCurrentRequestUri().path("/{codigo}")
                .buildAndExpand(vaga.getCodigo())
                .toUri();

        return ResponseEntity.created(location).build();
    }

    @Operation(summary = "Localizar uma vaga pelo seu código", description = "Recurso para localizar uma nova vaga de estacionamento" +
            "Requisição necessita de um código referente à vaga e um bearer token para autenticação. Acesso permitido somente a Role='ADMIN'",
            responses = {
                    @ApiResponse(responseCode = "200", description = "Sucesso",
                            content = @Content(mediaType = "application/json;charset=UTF-8",
                                    schema = @Schema(implementation = VagaResponseDto.class)
                            )
                    ),
                    @ApiResponse(responseCode = "404", description = "Vaga não encontrada no sistema",
                            content = @Content(mediaType = "application/json;charset=UTF-8",
                                    schema = @Schema(implementation = ErrorMessage.class)
                            )
                    )
            }
    )
    @GetMapping("/{codigo}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<VagaResponseDto> getByCodigo(@PathVariable String codigo){
        Vaga vaga = vagaService.buscarPorCodigo(codigo);
        return ResponseEntity.ok(VagaMapper.toDto(vaga));
    }
}
