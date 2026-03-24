package com.dev.park_api.web.controller;

import com.dev.park_api.entity.Contato;
import com.dev.park_api.service.ContatoService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RequiredArgsConstructor
@RestController
@RequestMapping("/tarefas/contatos")
public class ContatoController {

    private final ContatoService contatoService;

    @PostMapping
    public ResponseEntity<Contato> create(@RequestBody Contato contato){
        Contato contact = contatoService.salvar(contato);
        return ResponseEntity.status(HttpStatus.CREATED).body(contact);
    }
}
