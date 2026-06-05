package com.michele.martins.controller;

import com.michele.martins.dto.ConsultaRequest;
import com.michele.martins.model.Cliente;
import com.michele.martins.model.Consulta;
import com.michele.martins.repository.ClienteRepository;
import com.michele.martins.repository.ConsultaRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.format.DateTimeFormatter;
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
    private ClienteRepository clienteRepository;

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

    @PostMapping
    public ResponseEntity<Map<String, String>> salvar(@RequestBody ConsultaRequest request) {
        Cliente cliente = clienteRepository.findById(request.getClienteId()).orElse(null);
        if (cliente == null) {
            return ResponseEntity.badRequest().body(Map.of("erro", "Paciente não encontrado"));
        }

        Consulta consulta = new Consulta();
        consulta.setCliente(cliente);
        consulta.setData(request.getData());
        consulta.setHorario(request.getHorario());
        consulta.setValor(request.getValor());

        try {
            consultaRepository.save(consulta);
            return ResponseEntity.ok(Map.of("mensagem", "Consulta salva com sucesso"));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of("erro", "Erro ao salvar consulta"));
        }
    }
}
