package com.michele.martins.repository;

import com.michele.martins.model.Consulta;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;
import java.util.Optional;

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
    List<Consulta> findByDataAfter(LocalDate currentDate);

    long countByData(LocalDate data);

    long countByDataAndHorarioAfter(LocalDate data, LocalTime horario);

    Optional<Consulta> findFirstByClienteIdAndDataGreaterThanEqualOrderByDataAsc(Long clienteId, LocalDate data);

    Optional<Consulta> findFirstByClienteIdAndDataLessThanOrderByDataDesc(Long clienteId, LocalDate data);

    @Query("SELECT COUNT(c) > 0 FROM Consulta c " +
           "WHERE c.data = :data " +
           "AND c.horarioFim IS NOT NULL " +
           "AND c.horario < :horarioFim " +
           "AND c.horarioFim > :horarioInicio")
    boolean existsOverlappingConsulta(@Param("data") LocalDate data,
                                      @Param("horarioInicio") LocalTime horarioInicio,
                                      @Param("horarioFim") LocalTime horarioFim);

    @Query("SELECT COUNT(c) > 0 FROM Consulta c " +
           "WHERE c.idConsulta <> :id " +
           "AND c.data = :data " +
           "AND c.horarioFim IS NOT NULL " +
           "AND c.horario < :horarioFim " +
           "AND c.horarioFim > :horarioInicio")
    boolean existsOverlappingConsultaExcludingId(@Param("data") LocalDate data,
                                                 @Param("horarioInicio") LocalTime horarioInicio,
                                                 @Param("horarioFim") LocalTime horarioFim,
                                                 @Param("id") Long id);
}
