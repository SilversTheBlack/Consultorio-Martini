package com.michele.martins.repository;

import com.michele.martins.model.Consulta;

import java.time.LocalDate;
import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface ConsultaRepository extends JpaRepository<Consulta, Long> {
    @Query("SELECT c FROM Consulta c WHERE LOWER(c.cliente.nome) LIKE LOWER(CONCAT('%', :keyword, '%'))")
    List<Consulta> findByClienteNomeContainingIgnoreCase(@Param("keyword") String keyword);

    // New method: Find by client name AND date after current date
    @Query("SELECT c FROM Consulta c WHERE LOWER(c.cliente.nome) LIKE LOWER(CONCAT('%', :keyword, '%')) AND c.data > :currentDate")
    List<Consulta> findByClienteNomeContainingIgnoreCaseAndDataAfter(@Param("keyword") String keyword,
            @Param("currentDate") LocalDate currentDate);

    // New method: Find all consultations after current date
    // Spring Data JPA can derive this query from the method name:
    List<Consulta> findByDataAfter(LocalDate currentDate);
}
