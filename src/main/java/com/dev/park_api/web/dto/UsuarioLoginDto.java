package com.dev.park_api.web.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.*;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@ToString
public class UsuarioLoginDto {

    @NotBlank
    @Email(message = "Formato do e-mail inválido")
    private String username;

    @NotBlank
    @Size(message = "Tamanho mínimo e máximo = 6", min = 6, max = 6)
    private String password;
}
