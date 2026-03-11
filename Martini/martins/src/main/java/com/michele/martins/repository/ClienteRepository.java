package com.michele.martins.repository;

import com.michele.martins.model.Cliente;


import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface ClienteRepository extends JpaRepository<Cliente, Long> {
     @Query("SELECT cl FROM Cliente cl WHERE LOWER(cl.nome) LIKE LOWER(CONCAT('%', :keyword, '%'))")
    List<Cliente> findByNomeContainingIgnoreCase(@Param("keyword") String keyword);

}
