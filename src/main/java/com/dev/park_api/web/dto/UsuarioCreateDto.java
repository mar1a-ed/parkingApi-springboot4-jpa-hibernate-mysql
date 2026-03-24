package com.dev.park_api.web.dto;

import lombok.*;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@ToString
public class UsuarioCreateDto {

    private String username;

    private String password;

}
