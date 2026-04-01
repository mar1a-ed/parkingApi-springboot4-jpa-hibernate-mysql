package com.dev.park_api.service;

import com.dev.park_api.entity.Cliente;
import com.dev.park_api.exception.CpfUniqueViolationException;
import com.dev.park_api.repository.ClienteRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;

@RequiredArgsConstructor
@Service
public class ClienteService {

    private final ClienteRepository clienteRepository;

    @Transactional
    public Cliente salvar(Cliente cliente){
        try{
            return clienteRepository.save(cliente);
        }catch (DataIntegrityViolationException e){
            throw new CpfUniqueViolationException(String.format("CPF '%s' não pode ser cadastrado pois já foi cadastrado anteriormente", cliente.getCpf()));
        }
    }
}
