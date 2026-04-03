package com.dev.park_api.repository;

import com.dev.park_api.entity.ClienteVaga;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ClienteVagaRepository extends JpaRepository<ClienteVaga, Long> {
}
