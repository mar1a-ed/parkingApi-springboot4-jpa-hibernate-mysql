package com.dev.park_api.service;

import com.dev.park_api.entity.Usuario;
import com.dev.park_api.repository.UsuarioRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@RequiredArgsConstructor
@Service
public class UsuarioService {

    private final UsuarioRepository usuarioRepository;

    @Transactional
    public Usuario salvar(Usuario usuario){
        return usuarioRepository.save(usuario);
    }

    @Transactional
    public Usuario findById(Long id){
        return usuarioRepository.findById(id).orElseThrow(
                () -> new RuntimeException("Usuário não encontrado.")
        );
    }

    @Transactional
    public Usuario updateSenha(Long id, String password){
        Usuario user = findById(id);
        user.setPassword(password);
        return user;
    }

    @Transactional
    public List<Usuario> findAll(){
        return usuarioRepository.findAll();
    }
}
