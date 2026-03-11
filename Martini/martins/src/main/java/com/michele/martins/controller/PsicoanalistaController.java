package com.michele.martins.controller;

import com.michele.martins.model.Psicoanalista;
import com.michele.martins.repository.PsicoanalistaRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

@Controller
@RequestMapping("/psicoanalistas")
public class PsicoanalistaController {

    @Autowired
    private PsicoanalistaRepository psicoanalistaRepository;

    // Lista todos os psicoanalistas
    @GetMapping
    public String listarTodos(Model model) {
        model.addAttribute("psicoanalistas", psicoanalistaRepository.findAll());
        return "psicoanalistas/listar";
    }

    // Exibe formulário para criar novo psicoanalista
    @GetMapping("/novo")
    public String mostrarFormNovo(Model model) {
        model.addAttribute("psicoanalista", new Psicoanalista());
        return "psicoanalistas/form";
    }

    // Exibe formulário para editar psicoanalista existente
    @GetMapping("/editar/{id}")
    public String mostrarFormEditar(@PathVariable Long id, Model model) {
        Psicoanalista psicoanalista = psicoanalistaRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Psicoanalista inválido Id:" + id));
        model.addAttribute("psicoanalista", psicoanalista);
        return "psicoanalistas/form";
    }

    // Salvar ou atualizar psicoanalista
    @PostMapping("/salvar")
    public String salvar(@ModelAttribute Psicoanalista psicoanalista) {
        psicoanalistaRepository.save(psicoanalista);
        return "redirect:/psicoanalistas";
    }

    // Deletar psicoanalista
    @GetMapping("/deletar/{id}")
    public String deletar(@PathVariable Long id) {
        psicoanalistaRepository.deleteById(id);
        return "redirect:/psicoanalistas";
    }
}

