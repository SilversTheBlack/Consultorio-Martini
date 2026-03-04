package com.michele.martins.controller;

import com.michele.martins.model.Cliente;
import com.michele.martins.model.Consulta;
import com.michele.martins.repository.ClienteRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.List;
import java.util.Optional;

@Controller
@RequestMapping("/clientes")
public class ClienteController {

    @Autowired
    private ClienteRepository clienteRepository;

    @GetMapping("/listar")
    public String listar(
            @RequestParam(value = "keyword", required = false) String keyword,
            Model model) {

        List<Cliente> listaClientes;

        if (keyword != null && !keyword.trim().isEmpty()) {

            listaClientes = clienteRepository.findByNomeContainingIgnoreCase(keyword.trim());
            model.addAttribute("message", "Resultados da busca por cliente: '" + keyword + "'");
        } else {

            listaClientes = clienteRepository.findAll();

        }

        model.addAttribute("clientes", listaClientes);
        model.addAttribute("keyword", keyword);

        return "fichaPacientes";
    }

    @GetMapping("/novo")
    public String novo(Model model) {
        model.addAttribute("cliente", new Cliente());

        return "CadPaciente";
    }

    @PostMapping("/salvar")
    public String salvar(@ModelAttribute Cliente cliente) {
        clienteRepository.save(cliente);
        return "redirect:/clientes";
    }

    @GetMapping("/editar/{id}")
    public String editar(@PathVariable("id") Long id, Model model, RedirectAttributes redirectAttributes) {
        Optional<Cliente> clienteOptional = clienteRepository.findById(id);
        if (!clienteOptional.isPresent()) {
            redirectAttributes.addFlashAttribute("errorMessage", "Cliente não encontrado com ID: " + id);
            return "redirect:/clientes/listar";
        }
        Cliente clienteParaEditar = clienteOptional.get();
        System.out.println("Editing Cliente ID: " + clienteParaEditar.getId()); 
                                                                                
        model.addAttribute("cliente", clienteParaEditar);

        return "CadPaciente";   
    }

    @GetMapping("/deletar/{id}")
    public String deletar(@PathVariable("id") Long id) {
        clienteRepository.deleteById(id);
        return "redirect:/clientes/listar";
    }
}