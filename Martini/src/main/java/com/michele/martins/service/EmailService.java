package com.michele.martins.service;

import com.michele.martins.model.Consulta;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.HashMap;
import java.util.Map;

@Service
public class EmailService {

    private final RestTemplate restTemplate;
    private final String emailServiceUrl;

    public EmailService(RestTemplate restTemplate,
                        @Value("${ms.email.url}") String emailServiceUrl) {
        this.restTemplate = restTemplate;
        this.emailServiceUrl = emailServiceUrl;
    }

    public void enviarConfirmacaoConsulta(Consulta consulta) {
        String url = emailServiceUrl + "/email/confirmacao-consulta";
        Map<String, Object> payload = new HashMap<>();
        payload.put("destinatario", consulta.getCliente().getEmail());
        payload.put("nomePaciente", consulta.getCliente().getNome());
        payload.put("data", consulta.getData().toString());
        payload.put("horarioInicio", consulta.getHorario().toString());
        payload.put("horarioFim", consulta.getHorarioFim().toString());
        payload.put("valor", consulta.getValor());

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        HttpEntity<Map<String, Object>> request = new HttpEntity<>(payload, headers);
        restTemplate.postForObject(url, request, Void.class);
    }
}
