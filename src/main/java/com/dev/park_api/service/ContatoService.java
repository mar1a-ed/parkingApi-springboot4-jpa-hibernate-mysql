package com.dev.park_api.service;

import com.dev.park_api.entity.Contato;
import com.dev.park_api.repository.ContatoRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@RequiredArgsConstructor
@Service
public class ContatoService {

    private final ContatoRepository contatoRepository;

    @Transactional
    public Contato salvar(Contato contato){
        return contatoRepository.save(contato);
    }


}
