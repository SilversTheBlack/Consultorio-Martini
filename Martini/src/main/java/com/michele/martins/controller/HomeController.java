package com.michele.martins.controller;

import com.michele.martins.dto.PerfilClienteResponse;
import com.michele.martins.model.Consulta;
import com.michele.martins.repository.ClienteRepository;
import com.michele.martins.repository.ConsultaRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import java.time.LocalDate;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Controller
public class HomeController {

    @Autowired
    private ConsultaRepository consultaRepository;

    @Autowired
    private ClienteRepository clienteRepository;

    @GetMapping("/")
    public String home(Model model) {
        LocalDate hoje = LocalDate.now();
        LocalTime agora = LocalTime.now();

        long consultasHoje = consultaRepository.countByData(hoje);
        long consultasFaltam = consultaRepository.countByDataAndHorarioAfter(hoje, agora);
        long pacientesAtivos = clienteRepository.countByStatus("ativo");

        DateTimeFormatter fmt = DateTimeFormatter.ofPattern("dd/MM/yyyy");

        List<PerfilClienteResponse> clientes = clienteRepository.findAll().stream().map(c -> {
            Optional<Consulta> proxima = consultaRepository
                    .findFirstByClienteIdAndDataGreaterThanEqualOrderByDataAsc(c.getId(), hoje);
            Optional<Consulta> anterior = consultaRepository
                    .findFirstByClienteIdAndDataLessThanOrderByDataDesc(c.getId(), hoje);

            PerfilClienteResponse p = new PerfilClienteResponse();
            p.setId(c.getId());
            p.setNome(c.getNome());
            p.setCpf(c.getCpf());
            p.setStatus(c.getStatus());
            p.setProximaConsulta(proxima.map(con -> con.getData().format(fmt)).orElse("—"));
            p.setConsultaAnterior(anterior.map(con -> con.getData().format(fmt)).orElse("—"));
            return p;
        }).collect(Collectors.toList());

        model.addAttribute("consultasHoje", consultasHoje);
        model.addAttribute("consultasFaltam", consultasFaltam);
        model.addAttribute("pacientesAtivos", pacientesAtivos);
        model.addAttribute("clientes", clientes);

        return "index";
    }
}