package com.michele.martins.controller;

import com.michele.martins.dto.ConsultaRequest;
import com.michele.martins.model.Cliente;
import com.michele.martins.model.Consulta;
import com.michele.martins.repository.ClienteRepository;
import com.michele.martins.repository.ConsultaRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/consultas")
public class ApiConsultaController {

    @Autowired
    private ConsultaRepository consultaRepository;

    @Autowired
    private ClienteRepository clienteRepository;

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
