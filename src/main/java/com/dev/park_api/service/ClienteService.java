package com.dev.park_api.service;

import com.dev.park_api.entity.Cliente;
import com.dev.park_api.exception.CpfUniqueViolationException;
import com.dev.park_api.exception.EntityNotFoundException;
import com.dev.park_api.repository.ClienteRepository;
import com.dev.park_api.repository.projection.ClienteProjection;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.List;

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

    @Transactional
    public Cliente findById(Long id){
        return clienteRepository.findById(id).orElseThrow(
                () -> new EntityNotFoundException(String.format("Cliente id = %s não encontrado no sistema", id))
        );
    }

    @Transactional
    public Page<ClienteProjection> getAll(Pageable pageable){
        return clienteRepository.getAllPageable(pageable);
    }

    @Transactional
    public Cliente buscarPorUsuarioId(Long id){
        return clienteRepository.findByUsuarioId(id);
    }
}
