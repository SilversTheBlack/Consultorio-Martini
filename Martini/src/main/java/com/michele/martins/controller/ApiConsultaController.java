package com.michele.martins.controller;

import com.michele.martins.dto.ConsultaRequest;
import com.michele.martins.model.Consulta;
import com.michele.martins.repository.ConsultaRepository;
import com.michele.martins.service.ConsultaService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/consultas")
public class ApiConsultaController {

    @Autowired
    private ConsultaRepository consultaRepository;

    @Autowired
    private ConsultaService consultaService;

    // GET /api/consultas — formato para o popup Histórico de Consultas
    @GetMapping
    public List<Map<String, Object>> listar() {
        DateTimeFormatter fmtData = DateTimeFormatter.ofPattern("dd/MM/yyyy");
        DateTimeFormatter fmtHora = DateTimeFormatter.ofPattern("HH:mm");

        return consultaRepository.findAll(Sort.by(Sort.Direction.DESC, "data", "horario"))
                .stream()
                .map(c -> {
                    Map<String, Object> item = new LinkedHashMap<>();
                    item.put("id", c.getIdConsulta());
                    item.put("dataHora", c.getData().format(fmtData) + " " + c.getHorario().format(fmtHora));
                    item.put("paciente", c.getCliente().getNome());
                    item.put("cpf", c.getCliente().getCpf());
                    item.put("valor", String.format(Locale.ROOT, "R$ %.2f", c.getValor()).replace(".", ","));
                    return item;
                })
                .collect(Collectors.toList());
    }

    // GET /api/consultas/calendario — formato FullCalendar
    @GetMapping("/calendario")
    public List<Map<String, Object>> calendario() {
        return consultaService.findAll().stream()
                .map(this::toCalendarEvent)
                .collect(Collectors.toList());
    }

    // GET /api/consultas/{id}
    @GetMapping("/{id}")
    public ResponseEntity<Consulta> getById(@PathVariable Long id) {
        return consultaService.findById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    // POST /api/consultas
    @PostMapping
    public ResponseEntity<?> salvar(@RequestBody ConsultaRequest request) {
        try {
            Consulta saved = consultaService.create(request);
            return ResponseEntity.ok(Map.of("mensagem", "Consulta salva com sucesso", "id", saved.getIdConsulta()));
        } catch (IllegalArgumentException ex) {
            return ResponseEntity.badRequest().body(Map.of("erro", ex.getMessage()));
        }
    }

    // PUT /api/consultas/{id}
    @PutMapping("/{id}")
    public ResponseEntity<?> update(@PathVariable Long id, @RequestBody ConsultaRequest request) {
        try {
            consultaService.update(id, request);
            return ResponseEntity.ok(Map.of("mensagem", "Consulta atualizada com sucesso"));
        } catch (IllegalArgumentException ex) {
            return ResponseEntity.badRequest().body(Map.of("erro", ex.getMessage()));
        }
    }

    // DELETE /api/consultas/{id}
    @DeleteMapping("/{id}")
    public ResponseEntity<?> delete(@PathVariable Long id) {
        try {
            consultaService.delete(id);
            return ResponseEntity.ok(Map.of("mensagem", "Consulta excluída com sucesso"));
        } catch (IllegalArgumentException ex) {
            return ResponseEntity.notFound().build();
        }
    }

    // GET /api/consultas/horarios-disponiveis
    @GetMapping("/horarios-disponiveis")
    public ResponseEntity<?> horariosDisponiveis(
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate data) {
        List<LocalTime> slots = java.util.stream.IntStream.rangeClosed(9, 17)
                .mapToObj(h -> LocalTime.of(h, 0))
                .collect(Collectors.toList());
        List<Consulta> ocupadas = consultaService.findAll().stream()
                .filter(c -> c.getData().equals(data))
                .collect(Collectors.toList());
        List<Map<String, String>> disponiveis = slots.stream()
                .filter(s -> ocupadas.stream().noneMatch(c ->
                        c.getHorarioFim() != null &&
                        c.getHorario().isBefore(s.plusHours(1)) &&
                        c.getHorarioFim().isAfter(s)))
                .map(s -> {
                    Map<String, String> m = new HashMap<>();
                    m.put("inicio", s.toString());
                    m.put("fim", s.plusHours(1).toString());
                    m.put("label", s + " - " + s.plusHours(1));
                    return m;
                })
                .collect(Collectors.toList());
        return ResponseEntity.ok(Map.of("horarios", disponiveis));
    }

    private Map<String, Object> toCalendarEvent(Consulta c) {
        Map<String, Object> ev = new HashMap<>();
        ev.put("id", c.getIdConsulta());
        ev.put("title", c.getCliente().getNome());
        ev.put("start", c.getData().toString() + "T" + c.getHorario().toString());
        ev.put("end", c.getData().toString() + "T" +
                (c.getHorarioFim() != null ? c.getHorarioFim() : c.getHorario().plusHours(1)).toString());
        Map<String, Object> ext = new HashMap<>();
        ext.put("valor", c.getValor());
        ext.put("clienteId", c.getCliente().getId());
        ext.put("paciente", c.getCliente().getNome());
        ev.put("extendedProps", ext);
        return ev;
    }
}
