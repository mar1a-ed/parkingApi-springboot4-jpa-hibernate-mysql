package com.dev.park_api.web.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.*;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@ToString
public class UsuarioSenhaDto {

    @NotBlank
    @Size(message = "Tamanho mínimo e máximo = 6", min = 6, max = 6)
    private String senhaAtual;

    @NotBlank
    @Size(message = "Tamanho mínimo e máximo = 6", min = 6, max = 6)
    private String novaSenha;

    @NotBlank
    @Size(message = "Tamanho mínimo e máximo = 6", min = 6, max = 6)
    private String confirmaSenha;
}
