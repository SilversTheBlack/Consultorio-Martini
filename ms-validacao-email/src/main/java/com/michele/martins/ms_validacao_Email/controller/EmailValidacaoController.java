package com.michele.martins.ms_validacao_Email.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@RestController
@RequestMapping("/validar")
public class EmailValidacaoController {

    @GetMapping
    public ResponseEntity<Map<String, Object>> validar(@RequestParam String email) {
        boolean valido = validarEmail(email);
        return ResponseEntity.ok(Map.of("valido", valido, "email", email));
    }

    private boolean validarEmail(String email) {
        if (email == null || email.isBlank()) return false;
        String regex = "^[a-zA-Z0-9._%+\\-]+@[a-zA-Z0-9.\\-]+\\.[a-zA-Z]{2,}$";
        return email.matches(regex);
    }
}
