package com.michele.martins.controller;

import com.michele.martins.repository.ClienteRepository;
import com.michele.martins.repository.ConsultaRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import java.time.LocalDate;
import java.time.LocalTime;

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

        model.addAttribute("consultasHoje", consultasHoje);
        model.addAttribute("consultasFaltam", consultasFaltam);
        model.addAttribute("pacientesAtivos", pacientesAtivos);
        model.addAttribute("clientes", clienteRepository.findAll());

        return "index";
    }
}