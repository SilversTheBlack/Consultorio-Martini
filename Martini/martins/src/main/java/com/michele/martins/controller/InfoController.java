package com.michele.martins.controller;

import com.michele.martins.model.Info;
import com.michele.martins.repository.InfoRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

@Controller
@RequestMapping("/infos")
public class InfoController {

    @Autowired
    private InfoRepository infoRepository;

    // Lista todas as infos
    @GetMapping
    public String listarInfos(Model model) {
        model.addAttribute("infos", infoRepository.findAll());
        return "infos/listar";  // View para listar
    }

    // Formulário para nova info
    @GetMapping("/novo")
    public String mostrarFormNovo(Model model) {
        model.addAttribute("info", new Info());
        return "infos/form";  // View para formulário
    }

    // Formulário para editar info existente
    @GetMapping("/editar/{id}")
    public String mostrarFormEditar(@PathVariable Long id, Model model) {
        Info info = infoRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Info inválida Id:" + id));
        model.addAttribute("info", info);
        return "infos/form";
    }

    // Salvar nova ou atualizar info existente
    @PostMapping("/salvar")
    public String salvar(@ModelAttribute Info info) {
        infoRepository.save(info);
        return "redirect:/infos";
    }

    // Deletar info
    @GetMapping("/deletar/{id}")
    public String deletar(@PathVariable Long id) {
        infoRepository.deleteById(id);
        return "redirect:/infos";
    }
}
