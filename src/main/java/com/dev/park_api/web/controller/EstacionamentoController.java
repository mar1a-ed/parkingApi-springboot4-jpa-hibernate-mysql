package com.dev.park_api.web.controller;

import com.dev.park_api.entity.ClienteVaga;
import com.dev.park_api.jwt.JwtUserDetails;
import com.dev.park_api.repository.projection.ClienteVagaProjection;
import com.dev.park_api.service.ClienteService;
import com.dev.park_api.service.ClienteVagaService;
import com.dev.park_api.service.EstacionamentoService;
import com.dev.park_api.service.JasperService;
import com.dev.park_api.web.dto.EstacionamentoCreateDto;
import com.dev.park_api.web.dto.EstacionamentoResponseDto;
import com.dev.park_api.web.dto.PageableDto;
import com.dev.park_api.web.dto.mapper.ClienteVagaMapper;
import com.dev.park_api.web.dto.mapper.PageableMapper;
import com.dev.park_api.web.exception.ErrorMessage;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.enums.ParameterIn;
import io.swagger.v3.oas.annotations.headers.Header;
import io.swagger.v3.oas.annotations.media.ArraySchema;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.io.IOException;
import java.net.URI;

@Tag(name = "Estacionamentos", description = "Contém operações de registro de entrada e saída no sistema de veículos do estacionamento")
@RequiredArgsConstructor
@RestController
@RequestMapping("api/v1/estacionamentos")
public class EstacionamentoController {

    private final EstacionamentoService estacionamentoService;

    private final ClienteVagaService clienteVagaService;

    private final ClienteService clienteService;

    private final JasperService jasperService;

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

    @Operation(summary = "Localizar um veículo estacionado no Estacionamento", description = "Recurso para localizar um veículo estacionado no estacionamento." +
            "A requisição necessita de um bearer token para validar a autenticação de um 'ADMIN' ou 'CLIENTE'",
            security = @SecurityRequirement(name = "security"),
            parameters = {
                @Parameter(in = ParameterIn.PATH, name = "recibo", description = "Número do recibo gerado pelo processo de check-in")
            },
            responses = {
                    @ApiResponse(responseCode = "200", description = "Sucesso",
                            content = @Content(mediaType = "application/json;charset=UTF-8",
                                    schema = @Schema(implementation = EstacionamentoResponseDto.class))
                    ),
                    @ApiResponse(responseCode = "404", description = "Número do recibo não encontrado no sistema",
                            content = @Content(mediaType = "application/json;charset=UTF-8",
                                    schema = @Schema(implementation = ErrorMessage.class))
                    )
            }
    )
    @GetMapping("/check-in/{recibo}")
    @PreAuthorize("hasAnyRole('ADMIN', 'CLIENTE')")
    public ResponseEntity<EstacionamentoResponseDto> findByRecibo(@PathVariable String recibo){
        ClienteVaga clienteVaga = clienteVagaService.buscarPorRecibo(recibo);
        EstacionamentoResponseDto dto = ClienteVagaMapper.toDto(clienteVaga);

        return ResponseEntity.ok(dto);
    }

    @Operation(summary = "Check-Out no Estacionamento", description = "Recurso para realizar a operação de check-out no estacionamento." +
            "A requisição necessita de um bearer token para validar a autenticação de um 'ADMIN'",
            security = @SecurityRequirement(name = "security"),
            parameters = {
                    @Parameter(in = ParameterIn.PATH, name = "recibo", description = "Número do recibo gerado pelo processo de check-in")
            },
            responses = {
                    @ApiResponse(responseCode = "200", description = "Sucesso",
                            content = @Content(mediaType = "application/json;charset=UTF-8",
                                    schema = @Schema(implementation = EstacionamentoResponseDto.class))
                    ),
                    @ApiResponse(responseCode = "404", description = "Número do recibo não encontrado no sistema",
                            content = @Content(mediaType = "application/json;charset=UTF-8",
                                    schema = @Schema(implementation = ErrorMessage.class))
                    ),
                    @ApiResponse(responseCode = "403", description = "Recurso não permitido a Role='CLIENTE'",
                            content = @Content(mediaType = "application/json;charset=UTF-8",
                                    schema = @Schema(implementation = ErrorMessage.class))
                    )
            }
    )
    @PutMapping("/check-out/{recibo}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<EstacionamentoResponseDto> checkOut(@PathVariable String recibo){
        ClienteVaga clienteVaga = estacionamentoService.checkOut(recibo);
        EstacionamentoResponseDto dto = ClienteVagaMapper.toDto(clienteVaga);

        return ResponseEntity.ok(dto);
    }

    @Operation(summary = "Localizar registros do cliente por CPF", description = "Recurso para realizar a localização dos registros do cliente no estacionamento." +
            "A requisição necessita de um bearer token para validar a autenticação de um 'ADMIN'",
            security = @SecurityRequirement(name = "security"),
            parameters = {
                    @Parameter(in = ParameterIn.PATH, name = "cpf", description = "Número do CPF do cliente a ser consultado",
                        required = true
                    ),
                    @Parameter(in = ParameterIn.QUERY, name = "page", description = "Representa a página retornada",
                        content = @Content(schema = @Schema(type = "integer", defaultValue = "0"))
                    ),
                    @Parameter(in = ParameterIn.QUERY, name = "size", description = "Representa o total de elementos por página",
                        content = @Content(schema = @Schema(type = "integer", defaultValue = "5"))
                    ),
                    @Parameter(in = ParameterIn.QUERY, name = "sort", description = "Campo de ordenação 'dataEntrada,asc'",
                        array = @ArraySchema(schema = @Schema(type = "string", defaultValue = "dataEntrada,asc")),
                        hidden = true
                    )

            },
            responses = {
                    @ApiResponse(responseCode = "200", description = "Sucesso",
                            content = @Content(mediaType = "application/json;charset=UTF-8",
                                    schema = @Schema(implementation = PageableDto.class))
                    ),
                    @ApiResponse(responseCode = "403", description = "Recurso não permitido a Role='CLIENTE'",
                            content = @Content(mediaType = "application/json;charset=UTF-8",
                                    schema = @Schema(implementation = ErrorMessage.class))
                    )
            }
    )
    @GetMapping("/cpf/{cpf}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<PageableDto> findAllEstacionamentosPorCpf(@PathVariable String cpf, @PageableDefault(size = 5, sort = "dataEntrada", direction = Sort.Direction.ASC) Pageable pageable){
        Page<ClienteVagaProjection> projection = clienteVagaService.buscarTodosPorClienteCpf(cpf, pageable);
        PageableDto dto = PageableMapper.toDto(projection);
        return ResponseEntity.ok(dto);
    }

    @Operation(summary = "Localizar registros do cliente logado", description = "Recurso para realizar a localização dos registros do cliente logado, no estacionamento." +
            "A requisição necessita de um bearer token para validar a autenticação do próprio cliente",
            security = @SecurityRequirement(name = "security"),
            parameters = {
                    @Parameter(in = ParameterIn.QUERY, name = "page", description = "Representa a página retornada",
                            content = @Content(schema = @Schema(type = "integer", defaultValue = "0"))
                    ),
                    @Parameter(in = ParameterIn.QUERY, name = "size", description = "Representa o total de elementos por página",
                            content = @Content(schema = @Schema(type = "integer", defaultValue = "5"))
                    ),
                    @Parameter(in = ParameterIn.QUERY, name = "sort", description = "Campo de ordenação 'dataEntrada,asc'",
                            array = @ArraySchema(schema = @Schema(type = "string", defaultValue = "dataEntrada,asc")),
                            hidden = true
                    )
            },
            responses = {
                    @ApiResponse(responseCode = "200", description = "Sucesso",
                            content = @Content(mediaType = "application/json;charset=UTF-8",
                                    schema = @Schema(implementation = PageableDto.class))
                    ),
                    @ApiResponse(responseCode = "403", description = "Recurso não permitido a Role='ADMIN'",
                            content = @Content(mediaType = "application/json;charset=UTF-8",
                                    schema = @Schema(implementation = ErrorMessage.class))
                    )
            }
    )
    @GetMapping
    @PreAuthorize("hasRole('CLIENTE')")
    public ResponseEntity<PageableDto> findAllEstacionamentosDoCpf(@AuthenticationPrincipal JwtUserDetails user, @PageableDefault(size = 5, sort = "dataEntrada", direction = Sort.Direction.ASC) Pageable pageable){
        Page<ClienteVagaProjection> projection = clienteVagaService.buscarTodosPorUsuarioId(user.getId(), pageable);
        PageableDto dto = PageableMapper.toDto(projection);
        return ResponseEntity.ok(dto);
    }

    @GetMapping("/relatorio")
    @PreAuthorize("hasRole('CLIENTE')")
    public ResponseEntity<Void> gerarRelatorio(HttpServletResponse response, @AuthenticationPrincipal JwtUserDetails userDetails) throws IOException {
        String cpfCliente = clienteService.buscarPorUsuarioId(userDetails.getId()).getCpf();

        jasperService.addParams("CPF", cpfCliente);

        byte[] bytes = jasperService.gerarPdf();

        response.setContentType(MediaType.APPLICATION_PDF_VALUE);
        response.setHeader("Content-Disposition", "inline; filename=" + System.currentTimeMillis() + ".pdf");
        response.getOutputStream().write(bytes);

        return ResponseEntity.ok().build();
    }
}
