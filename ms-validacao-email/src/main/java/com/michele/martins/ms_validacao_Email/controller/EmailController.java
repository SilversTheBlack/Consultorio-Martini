package com.michele.martins.ms_validacao_Email.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.michele.martins.ms_validacao_Email.service.EmailService;

import java.util.Map;

@RestController
@RequestMapping("/")
public class EmailController {

    @Autowired
    private EmailService emailService;

    @PostMapping("/email/confirmacao-consulta")
    public ResponseEntity<Map<String, String>> enviarConfirmacao(@RequestBody Map<String, Object> payload) {
        try {
            String destinatario = (String) payload.get("destinatario");
            String nomePaciente = (String) payload.get("nomePaciente");
            String data = (String) payload.get("data");
            String horarioInicio = (String) payload.get("horarioInicio");
            String horarioFim = (String) payload.get("horarioFim");
            Double valor = payload.get("valor") instanceof Integer ?
                ((Integer) payload.get("valor")).doubleValue() :
                (Double) payload.get("valor");

            emailService.enviarConfirmacaoConsulta(destinatario, nomePaciente, data, horarioInicio, horarioFim, valor);
            return ResponseEntity.ok(Map.of("mensagem", "E-mail enviado com sucesso"));
        } catch (Exception e) {
            return ResponseEntity.internalServerError()
                    .body(Map.of("erro", "Erro ao enviar e-mail: " + e.getMessage()));
        }
    }
}
