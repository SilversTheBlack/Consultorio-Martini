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

    @GetMapping("/validar")
    public ResponseEntity<Map<String, Object>> validar(@RequestParam String email) {
        boolean valido = validarEmail(email);
        return ResponseEntity.ok(Map.of("valido", valido, "email", email));
    }

    private boolean validarEmail(String email) {
        if (email == null || email.isBlank()) return false;
        String regex = "^[a-zA-Z0-9._%+\\-]+@[a-zA-Z0-9.\\-]+\\.[a-zA-Z]{2,}$";
        return email.matches(regex);
    }

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