package com.michele.martins.controller;

import com.michele.martins.model.Endereco;
import com.michele.martins.repository.EnderecoRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

@Controller
@RequestMapping("/enderecos")
public class EnderecoController {

    @Autowired
    private EnderecoRepository enderecoRepository;

    // Lista todos os endereços
    @GetMapping
    public String listarEnderecos(Model model) {
        model.addAttribute("enderecos", enderecoRepository.findAll());
        return "enderecos/listar";  // View para listar
    }

    // Formulário para novo endereço
    @GetMapping("/novo")
    public String mostrarFormNovo(Model model) {
        model.addAttribute("endereco", new Endereco());
        return "enderecos/form";  // View para formulário
    }

    // Formulário para editar endereço existente
    @GetMapping("/editar/{id}")
    public String mostrarFormEditar(@PathVariable Long id, Model model) {
        Endereco endereco = enderecoRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Endereço inválido Id:" + id));
        model.addAttribute("endereco", endereco);
        return "enderecos/form";  // Mesma view do formulário
    }

    // Salvar novo ou atualizar existente
    @PostMapping("/salvar")
    public String salvar(@ModelAttribute Endereco endereco) {
        enderecoRepository.save(endereco);
        return "redirect:/enderecos";  // Redireciona para lista
    }

    // Deletar endereço
    @GetMapping("/deletar/{id}")
    public String deletar(@PathVariable Long id) {
        enderecoRepository.deleteById(id);
        return "redirect:/enderecos";  // Redireciona para lista
    }
}
