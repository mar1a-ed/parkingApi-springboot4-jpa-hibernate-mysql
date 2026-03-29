package com.dev.park_api.web.controller;

import com.dev.park_api.jwt.JwtToken;
import com.dev.park_api.jwt.JwtUserDetailsService;
import com.dev.park_api.web.dto.UsuarioLoginDto;
import com.dev.park_api.web.exception.ErrorMessage;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.AuthenticationException;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@RequiredArgsConstructor
@RestController
@RequestMapping("/api/v1")
public class AutenticacaoController {

    private final JwtUserDetailsService userDetailsService;

    private final AuthenticationManager authenticationManager;

    @PostMapping("/auth")
    public ResponseEntity<?> autentitcar(@RequestBody @Valid UsuarioLoginDto loginDto, HttpServletRequest request){
        log.info("Processo de autenticação pelo login {}", loginDto.getUsername());

        try{
            UsernamePasswordAuthenticationToken authenticationToken =
                    new UsernamePasswordAuthenticationToken(loginDto.getUsername(), loginDto.getPassword());

            authenticationManager.authenticate(authenticationToken);
            JwtToken token = userDetailsService.getTokenAuthenticator(loginDto.getUsername());
            return ResponseEntity.ok(token);

        }catch (AuthenticationException e){
            log.warn("Bad Credentials from username '{}'",loginDto.getUsername());
        }

        return ResponseEntity
                .badRequest()
                .body(new ErrorMessage(request, HttpStatus.BAD_REQUEST, "Credenciais Inválidos"));
    }
}
