package com.dev.park_api.web.dto;

import lombok.*;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@ToString
public class UsuarioSenhaDto {

    private String senhaAtual;

    private String novaSenha;

    private String confirmaSenha;
}
