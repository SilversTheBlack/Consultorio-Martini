package com.michele.martins.service;

import com.michele.martins.dto.ConsultaRequest;
import com.michele.martins.model.Consulta;
import com.michele.martins.model.Cliente;
import com.michele.martins.repository.ConsultaRepository;
import com.michele.martins.repository.ClienteRepository;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;
import java.util.Optional;

@Service
public class ConsultaService {

    private final ConsultaRepository consultaRepository;
    private final ClienteRepository clienteRepository;
    private final EmailService emailService;

    @Autowired
    public ConsultaService(ConsultaRepository consultaRepository,
                           ClienteRepository clienteRepository,
                           EmailService emailService) {
        this.consultaRepository = consultaRepository;
        this.clienteRepository = clienteRepository;
        this.emailService = emailService;
    }

    public List<Consulta> findAll() {
        return consultaRepository.findAll();
    }

    public Optional<Consulta> findById(Long id) {
        return consultaRepository.findById(id);
    }

    @Transactional
    public Consulta create(@Valid ConsultaRequest request) {
        validateRequest(request, null);
        Cliente cliente = clienteRepository.findById(request.getClienteId())
                .orElseThrow(() -> new IllegalArgumentException("Cliente não encontrado"));
        Consulta consulta = new Consulta();
        consulta.setCliente(cliente);
        consulta.setData(request.getData());
        consulta.setHorario(request.getHorario());
        consulta.setHorarioFim(request.getHorarioFim());
        consulta.setValor(request.getValor());
        Consulta saved = consultaRepository.save(consulta);
        // Try to send email, ignore failure for persistence
        try {
            emailService.enviarConfirmacaoConsulta(saved);
        } catch (Exception e) {
            // log could be added; for now we just swallow to keep the transaction
        }
        return saved;
    }

    @Transactional
    public Consulta update(Long id, @Valid ConsultaRequest request) {
        validateRequest(request, id);
        Consulta consulta = consultaRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Consulta não encontrada"));
        Cliente cliente = clienteRepository.findById(request.getClienteId())
                .orElseThrow(() -> new IllegalArgumentException("Cliente não encontrado"));
        consulta.setCliente(cliente);
        consulta.setData(request.getData());
        consulta.setHorario(request.getHorario());
        consulta.setHorarioFim(request.getHorarioFim());
        consulta.setValor(request.getValor());
        Consulta saved = consultaRepository.save(consulta);
        try {
            emailService.enviarConfirmacaoConsulta(saved);
        } catch (Exception e) {
            // ignore email failure
        }
        return saved;
    }

    @Transactional
    public void delete(Long id) {
        Consulta consulta = consultaRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Consulta não encontrada"));
        consultaRepository.delete(consulta);
    }

    private void validateRequest(ConsultaRequest request, Long existingId) {
        if (request.getClienteId() == null) {
            throw new IllegalArgumentException("Selecione um paciente.");
        }
        if (request.getData() == null) {
            throw new IllegalArgumentException("Informe a data da consulta.");
        }
        if (request.getHorario() == null || request.getHorarioFim() == null) {
            throw new IllegalArgumentException("Informe o horário inicial e final.");
        }
        if (!request.getHorarioFim().isAfter(request.getHorario())) {
            throw new IllegalArgumentException("O horário final deve ser maior que o horário inicial.");
        }
        // check overlapping
        boolean conflict = existingId == null ?
                consultaRepository.existsOverlappingConsulta(request.getData(), request.getHorario(), request.getHorarioFim()) :
                consultaRepository.existsOverlappingConsultaExcludingId(request.getData(), request.getHorario(), request.getHorarioFim(), existingId);
        if (conflict) {
            throw new IllegalArgumentException("Já existe uma consulta marcada nesse horário.");
        }
    }
}
