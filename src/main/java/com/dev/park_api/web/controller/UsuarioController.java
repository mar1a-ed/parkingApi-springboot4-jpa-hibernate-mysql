package com.dev.park_api.web.controller;

import com.dev.park_api.entity.Usuario;
import com.dev.park_api.service.UsuarioService;
import com.dev.park_api.web.dto.UsuarioCreateDto;
import com.dev.park_api.web.dto.UsuarioResponseDto;
import com.dev.park_api.web.dto.UsuarioSenhaDto;
import com.dev.park_api.web.dto.mapper.UsuarioMapper;
import com.dev.park_api.web.exception.ErrorMessage;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Tag(name = "Usuarios", description = "Contém todas as operações para cadastro, update e leitura de determinado usuário")
@RequiredArgsConstructor
@RestController
@RequestMapping("api/v1/usuarios")
public class UsuarioController {

    private final UsuarioService usuarioService;

    @Operation(summary = "Cadastrar um usuário", responses = {
            @ApiResponse(responseCode = "201", description = "Sucesso",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = UsuarioResponseDto.class))),
            @ApiResponse(responseCode = "409", description = "Usuário e e-mail já cadastrado no sistema",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = ErrorMessage.class))),
            @ApiResponse(responseCode = "422", description = "Campos inválidos",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = ErrorMessage.class)))
    })
    @PostMapping
    public ResponseEntity<UsuarioResponseDto> create(@Valid @RequestBody UsuarioCreateDto createDto){
       Usuario user = usuarioService.salvar(UsuarioMapper.toUsuario(createDto));
       return ResponseEntity.status(HttpStatus.CREATED).body(UsuarioMapper.toDto(user));
    }

    @Operation(summary = "Localizar um usuário pelo ID", responses = {
            @ApiResponse(responseCode = "200", description = "Sucesso",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = UsuarioResponseDto.class))),
            @ApiResponse(responseCode = "404", description = "Usuário não encontrado",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = ErrorMessage.class)))
    })

    @GetMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<UsuarioResponseDto> findById(@PathVariable Long id){
        Usuario user = usuarioService.findById(id);
        return ResponseEntity.ok(UsuarioMapper.toDto(user));
    }

    @Operation(summary = "Editar senha", responses = {
            @ApiResponse(responseCode = "204", description = "Senha editada com sucesso",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = Void.class))),
            @ApiResponse(responseCode = "404", description = "Usuário não encontrado",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = ErrorMessage.class))),
            @ApiResponse(responseCode = "400", description = "Senha não confere",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = ErrorMessage.class)))
    })
    @PatchMapping("/{id}")
    public ResponseEntity<UsuarioResponseDto> updatePassword(@PathVariable Long id, @Valid @RequestBody UsuarioSenhaDto usuarioSenhaDto){
        Usuario user = usuarioService.updateSenha(id, usuarioSenhaDto.getSenhaAtual(), usuarioSenhaDto.getNovaSenha(), usuarioSenhaDto.getConfirmaSenha());
        return ResponseEntity.ok(UsuarioMapper.toDto(user));
    }

    @Operation(summary = "Listar todos os usuários", responses = {
            @ApiResponse(responseCode = "200", description = "Sucesso",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = UsuarioResponseDto.class))),
            @ApiResponse(responseCode = "404", description = "Usuários não encontrados",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = ErrorMessage.class))),
    })
    @GetMapping
    public ResponseEntity<List<UsuarioResponseDto>> findAll(){
        List<Usuario> users = usuarioService.findAll();
        return ResponseEntity.ok(UsuarioMapper.toListDto(users));
    }
}
