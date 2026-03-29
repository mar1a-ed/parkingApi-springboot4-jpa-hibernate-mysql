package com.dev.park_api.service;

import com.dev.park_api.entity.Usuario;
import com.dev.park_api.exception.EntityNotFoundException;
import com.dev.park_api.exception.UsernameUniqueViolationException;
import com.dev.park_api.repository.UsuarioRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;

@RequiredArgsConstructor
@Service
public class UsuarioService {

    private final UsuarioRepository usuarioRepository;
    private final PasswordEncoder passwordEncoder;

    @Transactional
    public Usuario salvar(Usuario usuario) {
        try {
            usuario.setPassword(passwordEncoder.encode(usuario.getPassword()));
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

        if(!passwordEncoder.matches(senhaAtual, user.getPassword())){
            throw new RuntimeException("Sua senha não confere");
        }

        user.setPassword(passwordEncoder.encode(novaSenha));
        return user;
    }

    @Transactional
    public List<Usuario> findAll(){
        return usuarioRepository.findAll();
    }

    @Transactional
    public Usuario buscarPorUsername(String username){
        return usuarioRepository.findByUsername(username).orElseThrow(
                () -> new EntityNotFoundException(String.format("Usuário com 'username' não encontrado",username))
        );
    }

    @Transactional
    public Usuario.Role buscarRolePorUsername(String username){
        return usuarioRepository.findRoleByUsername(username);
    }
}
