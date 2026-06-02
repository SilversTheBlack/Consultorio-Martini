package com.michele.martins.controller;

import com.michele.martins.dto.PerfilClienteResponse;
import com.michele.martins.model.Cliente;
import com.michele.martins.model.Consulta;
import com.michele.martins.repository.ClienteRepository;
import com.michele.martins.repository.ConsultaRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@RestController
@RequestMapping("/api/clientes")
public class ApiClienteController {

    @Autowired
    private ClienteRepository clienteRepository;

    @Autowired
    private ConsultaRepository consultaRepository;

    @Autowired
    private RestTemplate restTemplate;

    @Value("${ms.cpf.url}")
    private String msCpfUrl;

    @Value("${ms.email.url}")
    private String msEmailUrl;

    private static final DateTimeFormatter FORMATO_DATA = DateTimeFormatter.ofPattern("dd/MM/yyyy");

    @GetMapping("/buscar")
    public List<Cliente> buscar(@RequestParam String nome) {
        if (nome == null || nome.isBlank()) return List.of();
        return clienteRepository.findByNomeContainingIgnoreCase(nome);
    }

    @GetMapping("/{id}/perfil")
    public ResponseEntity<PerfilClienteResponse> perfil(@PathVariable Long id) {
        Cliente cliente = clienteRepository.findById(id).orElse(null);
        if (cliente == null) return ResponseEntity.notFound().build();

        LocalDate hoje = LocalDate.now();

        Optional<Consulta> proxima = consultaRepository
                .findFirstByClienteIdAndDataGreaterThanEqualOrderByDataAsc(id, hoje);
        Optional<Consulta> anterior = consultaRepository
                .findFirstByClienteIdAndDataLessThanOrderByDataDesc(id, hoje);

        PerfilClienteResponse response = new PerfilClienteResponse();
        response.setId(cliente.getId());
        response.setNome(cliente.getNome() != null ? cliente.getNome() : "—");
        response.setCpf(cliente.getCpf() != null ? cliente.getCpf() : "—");
        response.setTelefone(cliente.getTelefone() != null ? cliente.getTelefone() : "—");
        response.setEmail(cliente.getEmail() != null ? cliente.getEmail() : "—");
        response.setGenero(cliente.getGenero() != null ? cliente.getGenero() : "—");
        response.setStatus(cliente.getStatus() != null ? cliente.getStatus() : "—");
        response.setProximaConsulta(proxima.map(c -> c.getData().format(FORMATO_DATA)).orElse("—"));
        response.setConsultaAnterior(anterior.map(c -> c.getData().format(FORMATO_DATA)).orElse("—"));

        return ResponseEntity.ok(response);
    }

    @PostMapping
    public ResponseEntity<Map<String, String>> salvar(@RequestBody Cliente cliente) {
        // valida CPF
        try {
            String urlCpf = msCpfUrl + "/validar?cpf=" + cliente.getCpf();
            Map respostaCpf = restTemplate.getForObject(urlCpf, Map.class);
            if (respostaCpf == null || !Boolean.TRUE.equals(respostaCpf.get("valido"))) {
                return ResponseEntity.badRequest().body(Map.of("erro", "CPF inválido"));
            }
        } catch (RestClientException e) {
            return ResponseEntity.internalServerError()
                    .body(Map.of("erro", "Serviço de validação de CPF indisponível"));
        }

        // valida e-mail
        try {
            String urlEmail = msEmailUrl + "/validar?email=" + cliente.getEmail();
            Map respostaEmail = restTemplate.getForObject(urlEmail, Map.class);
            if (respostaEmail == null || !Boolean.TRUE.equals(respostaEmail.get("valido"))) {
                return ResponseEntity.badRequest().body(Map.of("erro", "E-mail inválido"));
            }
        } catch (RestClientException e) {
            return ResponseEntity.internalServerError()
                    .body(Map.of("erro", "Serviço de validação de e-mail indisponível"));
        }

        try {
            clienteRepository.save(cliente);
            return ResponseEntity.ok(Map.of("mensagem", "Paciente salvo com sucesso"));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of("erro", "Erro ao salvar paciente"));
        }
    }
}
