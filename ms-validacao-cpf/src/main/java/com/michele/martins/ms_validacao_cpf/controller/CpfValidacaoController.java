package com.michele.martins.ms_validacao_cpf.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@RestController
@RequestMapping("/validar")
public class CpfValidacaoController {

    @GetMapping
    public ResponseEntity<Map<String, Object>> validar(@RequestParam String cpf) {
        boolean valido = validarCpf(cpf);
        return ResponseEntity.ok(Map.of("valido", valido, "cpf", cpf));
    }

    private boolean validarCpf(String cpf) {
        cpf = cpf.replaceAll("[^0-9]", "");

        if (cpf.length() != 11) return false;
        if (cpf.matches("(\\d)\\1{10}")) return false;

        int soma = 0;
        for (int i = 0; i < 9; i++) soma += (cpf.charAt(i) - '0') * (10 - i);
        int primeiroDigito = (soma * 10 % 11) % 10;

        soma = 0;
        for (int i = 0; i < 10; i++) soma += (cpf.charAt(i) - '0') * (11 - i);
        int segundoDigito = (soma * 10 % 11) % 10;

        return primeiroDigito == (cpf.charAt(9) - '0')
                && segundoDigito == (cpf.charAt(10) - '0');
    }
}
