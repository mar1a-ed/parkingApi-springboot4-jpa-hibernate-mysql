package com.dev.park_api.service;

import com.dev.park_api.entity.ClienteVaga;
import com.dev.park_api.repository.ClienteVagaRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@RequiredArgsConstructor
@Service
public class ClienteVagaService {

    private final ClienteVagaRepository clienteVagaRepository;

    @Transactional
    public ClienteVaga salvar(ClienteVaga clienteVaga){
        return clienteVagaRepository.save(clienteVaga);
    }
}
