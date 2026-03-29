package com.dev.park_api.repository;

import com.dev.park_api.entity.Usuario;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface UsuarioRepository extends JpaRepository <Usuario, Long>{

    Optional<Usuario> findByUsername(String username);

    @Query(value = "select u.role from Usuario u where u.username like :username")
    Usuario.Role findRoleByUsername(String username);
}
