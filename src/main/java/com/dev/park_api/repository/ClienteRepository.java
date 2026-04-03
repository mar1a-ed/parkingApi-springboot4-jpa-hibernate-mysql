package com.dev.park_api.repository;

import com.dev.park_api.entity.Cliente;
import com.dev.park_api.repository.projection.ClienteProjection;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface ClienteRepository extends JpaRepository<Cliente, Long> {

    @Query("select c from Cliente c")
    Page<ClienteProjection> getAllPageable(Pageable pageable);

    Cliente findByUsuarioId(Long id);

    Optional<Cliente> findByCpf(String cpf);
}
