package com.dev.park_api.service;

import com.dev.park_api.entity.Usuario;
import com.dev.park_api.exception.EntityNotFoundException;
import com.dev.park_api.exception.UsernameUniqueViolationException;
import com.dev.park_api.repository.UsuarioRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;

import java.util.List;

@RequiredArgsConstructor
@Service
public class UsuarioService {

    private final UsuarioRepository usuarioRepository;

    @Transactional
    public Usuario salvar(Usuario usuario) {
        try {
            return usuarioRepository.save(usuario);
        }catch(DataIntegrityViolationException e){
            throw new UsernameUniqueViolationException(String.format("Username {%s} já cadastrado", usuario.getUsername()));
        }
    }

    @Transactional
    public Usuario findById(Long id){
        return usuarioRepository.findById(id).orElseThrow(
                () -> new EntityNotFoundException(String.format("Usuário {id=%s} não encontrado.", id))
        );
    }

    @Transactional
    public Usuario updateSenha(Long id, String senhaAtual, String novaSenha, String confirmaSenha){
        if(!novaSenha.equals(confirmaSenha)){
            throw new RuntimeException("Nova Senha não confere com confirmação de senha");
        }

        Usuario user = findById(id);

        if(!user.getPassword().equals(senhaAtual)){
            throw new RuntimeException("Sua senha não confere");
        }

        user.setPassword(novaSenha);
        return user;
    }

    @Transactional
    public List<Usuario> findAll(){
        return usuarioRepository.findAll();
    }
}
