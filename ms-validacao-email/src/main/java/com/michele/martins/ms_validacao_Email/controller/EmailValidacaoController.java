package com.michele.martins.ms_validacao_Email.controller;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestTemplate;

import javax.naming.directory.Attributes;
import javax.naming.directory.DirContext;
import javax.naming.directory.InitialDirContext;
import java.util.Hashtable;
import java.util.Map;

@RestController
@RequestMapping("/validar")
public class EmailValidacaoController {

    private final RestTemplate restTemplate;

    @Value("${abstract.api.key}")
    private String apiKey;

    @Value("${abstract.api.url}")
    private String apiUrl;

    public EmailValidacaoController(RestTemplate restTemplate) {
        this.restTemplate = restTemplate;
    }

    @GetMapping
    public ResponseEntity<Map<String, Object>> validar(@RequestParam String email) {
        System.out.println("[MS-EMAIL] Recebido: " + email);

        // Etapa 1: formato básico
        if (!formatoValido(email)) {
            System.out.println("[MS-EMAIL] Etapa 1 falhou: formato inválido");
            return ResponseEntity.ok(Map.of("valido", false, "email", email, "motivo", "Formato de e-mail inválido"));
        }
        System.out.println("[MS-EMAIL] Etapa 1 OK: formato válido");

        // Etapa 2: MX lookup — domínio tem servidores de e-mail?
        // null = erro de rede/DNS → passa para AbstractAPI decidir
        String dominio = email.substring(email.indexOf('@') + 1);
        Boolean mxResult = dominioTemMx(dominio);
        System.out.println("[MS-EMAIL] Etapa 2 MX lookup resultado: " + mxResult);
        if (Boolean.FALSE.equals(mxResult)) {
            return ResponseEntity.ok(Map.of("valido", false, "email", email, "motivo", "Domínio não recebe e-mails"));
        }

        // Etapa 3: AbstractAPI Email Reputation — caixa postal existe e é entregável?
        try {
            String url = apiUrl + "?api_key=" + apiKey + "&email=" + email;
            System.out.println("[MS-EMAIL] Etapa 3 chamando AbstractAPI: " + url);
            Map<String, Object> resposta = restTemplate.getForObject(url, Map.class);
            System.out.println("[MS-EMAIL] Etapa 3 resposta: " + resposta);
            if (resposta == null) {
                return ResponseEntity.ok(Map.of("valido", false, "email", email, "motivo", "Serviço de verificação indisponível"));
            }
            // Email Reputation retorna: { email_deliverability: { status: "deliverable"|"undeliverable"|"unknown" } }
            @SuppressWarnings("unchecked")
            Map<String, Object> emailDeliverability = (Map<String, Object>) resposta.get("email_deliverability");
            String status = emailDeliverability != null ? (String) emailDeliverability.get("status") : null;
            System.out.println("[MS-EMAIL] Etapa 3 status: " + status);
            // undeliverable = certamente inválido | deliverable ou unknown = aceito
            boolean valido = "deliverable".equals(status) || "unknown".equals(status);
            return ResponseEntity.ok(Map.of("valido", valido, "email", email));
        } catch (Exception e) {
            System.out.println("[MS-EMAIL] Etapa 3 exceção: " + e.getMessage());
            return ResponseEntity.ok(Map.of("valido", false, "email", email, "motivo", "Erro ao verificar e-mail: " + e.getMessage()));
        }
    }

    private boolean formatoValido(String email) {
        if (email == null || email.isBlank()) return false;
        String regex = "^[a-zA-Z0-9._%+\\-]+@[a-zA-Z0-9.\\-]+\\.[a-zA-Z]{2,}$";
        return email.matches(regex);
    }

    // Retorna true (tem MX), false (não tem MX), ou null (erro de DNS → AbstractAPI decide)
    private Boolean dominioTemMx(String dominio) {
        try {
            Hashtable<String, String> env = new Hashtable<>();
            env.put("java.naming.factory.initial", "com.sun.jndi.dns.DnsContextFactory");
            env.put("java.naming.provider.url", "dns://8.8.8.8");
            DirContext ctx = new InitialDirContext(env);
            Attributes attrs = ctx.getAttributes(dominio, new String[]{"MX"});
            javax.naming.directory.Attribute mx = attrs.get("MX");
            return mx != null && mx.size() > 0;
        } catch (Exception e) {
            return null;
        }
    }
}
