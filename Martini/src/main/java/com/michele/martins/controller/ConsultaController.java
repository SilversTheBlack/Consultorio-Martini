package com.michele.martins.controller;
import com.michele.martins.model.Consulta;
import com.michele.martins.repository.ConsultaRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

@Controller
@RequestMapping("/consultas")
public class ConsultaController {

    @Autowired
    private ConsultaRepository consultaRepository;

    // Lista todas as consultas
    @GetMapping
    public String listarConsultas(Model model) {
        model.addAttribute("consultas", consultaRepository.findAll());
        return "consultas/index"; // View para listar consultas
    }
    // Exibe formulário para criar nova consulta
    // Exibe formulário para editar consulta existente
    @GetMapping("/editar/{id}")
    public String mostrarFormEditar(@PathVariable Long id, Model model) {
        Consulta consulta = consultaRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Consulta inválida Id:" + id));
        model.addAttribute("consulta", consulta);
        return "consultas/form"; // Mesma view do formulário (edição)
    }
    // Salva nova consulta ou atualiza existente
    @PostMapping("/salvar")
    public String salvar(@ModelAttribute Consulta consulta) {
        consultaRepository.save(consulta);
        return "redirect:/consultas"; // Redireciona para lista
    }

    // Deleta consulta
    @GetMapping("/deletar/{id}")
    public String deletar(@PathVariable Long id) {
        consultaRepository.deleteById(id);
        return "redirect:/consultas"; // Redireciona para lista
    }
}
