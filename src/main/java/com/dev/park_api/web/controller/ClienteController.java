package com.dev.park_api.web.controller;

import com.dev.park_api.entity.Cliente;
import com.dev.park_api.jwt.JwtUserDetails;
import com.dev.park_api.repository.projection.ClienteProjection;
import com.dev.park_api.service.ClienteService;
import com.dev.park_api.service.UsuarioService;
import com.dev.park_api.web.dto.ClienteCreateDto;
import com.dev.park_api.web.dto.ClienteResponseDto;
import com.dev.park_api.web.dto.PageableDto;
import com.dev.park_api.web.dto.UsuarioResponseDto;
import com.dev.park_api.web.dto.mapper.ClienteMapper;
import com.dev.park_api.web.dto.mapper.PageableMapper;
import com.dev.park_api.web.exception.ErrorMessage;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.enums.ParameterIn;
import io.swagger.v3.oas.annotations.media.ArraySchema;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@Tag(name = "Clientes", description = "Contém todas as operações referentes ao recurso 'Cliente'")
@RequiredArgsConstructor
@RestController
@RequestMapping("api/v1/clientes")
public class ClienteController {

    private final ClienteService clienteService;

    private final UsuarioService usuarioService;

    @Operation(summary = "Criar um novo cliente", description = "Recurso para criar um novo cliente. Requisição necessita de um bearer token. Acesso permitido somente a Role='Cliente'", responses = {
            @ApiResponse(responseCode = "201", description = "Sucesso",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = UsuarioResponseDto.class))),
            @ApiResponse(responseCode = "409", description = "Cliente CPF já cadastrado no sistema",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = ErrorMessage.class))),
            @ApiResponse(responseCode = "422", description = "Campos inválidos",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = ErrorMessage.class))),
            @ApiResponse(responseCode = "403", description = "Recurso não permitido ao perfil de ADMIN",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = ErrorMessage.class)))
    })
    @PostMapping
    @PreAuthorize("hasRole('CLIENTE')")
    public ResponseEntity<ClienteResponseDto> create(@RequestBody @Valid ClienteCreateDto dto, @AuthenticationPrincipal JwtUserDetails userDetails){

        Cliente cliente = ClienteMapper.toCliente(dto);
        cliente.setUsuario(usuarioService.findById(userDetails.getId()));
        clienteService.salvar(cliente);
        return ResponseEntity.status(201).body(ClienteMapper.toDto(cliente));
    }

    @Operation(summary = "Localizar um cliente", description = "Recurso para localizar um cliente pelo ID. Requisição necessita de um bearer token. Acesso permitido somente a Role='ADMIN'", responses = {
            @ApiResponse(responseCode = "200", description = "Sucesso",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = UsuarioResponseDto.class))),
            @ApiResponse(responseCode = "404", description = "Cliente não encontrado no sistema",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = ErrorMessage.class))),
            @ApiResponse(responseCode = "403", description = "Recurso não permitido ao perfil de CLIENTE",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = ErrorMessage.class)))
    })
    @GetMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ClienteResponseDto> getById(@PathVariable Long id){
        Cliente cliente = clienteService.findById(id);
        return ResponseEntity.ok(ClienteMapper.toDto(cliente));
    }

    @Operation(summary = "Localizar todos os clientes", description = "Requisição exige uso de um bearer token. Acesso permitido somente a Role='ADMIN'",
            parameters = {
                @Parameter(in = ParameterIn.QUERY, name = "page",
                        content = @Content(schema = @Schema(type = "integer", defaultValue = "0")),
                        description = "Representa a página retornada"
                ),
                @Parameter(in = ParameterIn.QUERY, name = "size",
                        content = @Content(schema = @Schema(type = "integer", defaultValue = "20")),
                        description = "Representa o total de elementos por página"
                ),
                @Parameter(in = ParameterIn.QUERY, name = "sort", hidden = true,
                        array = @ArraySchema(schema = @Schema(type = "string", defaultValue = "id,asc")),
                        description = "Representa a ordenação dos resultados"
                )
            },
            responses = {
                @ApiResponse(responseCode = "200", description = "Sucesso",
                    content = @Content(mediaType = " application/json;charset=UTF-8",
                        schema = @Schema(implementation = ErrorMessage.class)
                    )
                ),
                @ApiResponse(responseCode = "403", description = "Recurso não permitido para Role='CLIENTE'",
                    content = @Content(mediaType = " application/json;charset=UTF-8",
                        schema = @Schema(implementation = ErrorMessage.class)
                    )
                )

            }
    )
    @GetMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<PageableDto> getAll(@Parameter(hidden = true) @PageableDefault(size = 5, sort = {"nome"}) Pageable pageable){
        Page<ClienteProjection> clientes = clienteService.getAll(pageable);
        return ResponseEntity.ok(PageableMapper.toDto(clientes));
    }

    @Operation(summary = "Recuperar dados detalhados do cliente autenticado",
            description = "Requisição exige uso de um bearer token. Acesso restrito a Role='CLIENTE'",
            security = @SecurityRequirement(name = "security"),
            responses = {
                @ApiResponse(responseCode = "200", description = "Sucesso",
                    content = @Content(mediaType = "application/json;charset=UTF-8",
                        schema = @Schema(implementation = ClienteResponseDto.class))
                ),
                @ApiResponse(responseCode = "403", description = "Recurso não permitido a Role='ADMIN'",
                    content = @Content(mediaType = "application/json;charset=UTF-8",
                        schema = @Schema(implementation = ErrorMessage.class))
                )

            }
    )
    @GetMapping("/detalhes")
    @PreAuthorize("hasRole('CLIENTE')")
    public ResponseEntity<ClienteResponseDto> getDetalhes(@AuthenticationPrincipal JwtUserDetails userDetails){
        Cliente cliente = clienteService.buscarPorUsuarioId(userDetails.getId());
        return ResponseEntity.ok(ClienteMapper.toDto(cliente));
    }
}
