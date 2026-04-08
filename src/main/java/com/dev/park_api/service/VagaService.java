package com.dev.park_api.service;

import com.dev.park_api.entity.Vaga;
import com.dev.park_api.exception.CodigoUniqueViolationException;
import com.dev.park_api.exception.EntityNotFoundException;
import com.dev.park_api.exception.VagaDisponivelException;
import com.dev.park_api.repository.VagaRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@RequiredArgsConstructor
@Service
public class VagaService {

    private final VagaRepository vagaRepository;

    @Transactional
    public Vaga salvar(Vaga vaga){
        try{
            return vagaRepository.save(vaga);
        }catch (DataIntegrityViolationException e){
            throw new CodigoUniqueViolationException("Vaga", vaga.getCodigo());
        }
    }

    @Transactional
    public Vaga buscarPorCodigo(String codigo){
        return vagaRepository.findByCodigo(codigo).orElseThrow(
                () -> new EntityNotFoundException("Vaga", codigo)
        );
    }

    @Transactional(readOnly = true)
    public Vaga buscarPorVagaLivre() {
        return vagaRepository.findFirstByStatusVaga(Vaga.StatusVaga.LIVRE).orElseThrow(
                () -> new VagaDisponivelException("Não há vagas livres encontradas")
        );
    }
}
