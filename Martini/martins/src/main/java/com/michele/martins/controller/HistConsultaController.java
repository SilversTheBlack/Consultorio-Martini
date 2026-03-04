package com.michele.martins.controller;

import com.michele.martins.model.Cliente;
import com.michele.martins.model.Consulta;
import com.michele.martins.repository.ClienteRepository;
import com.michele.martins.repository.ConsultaRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.List;
import java.util.Optional;

@Controller
@RequestMapping("/historico") // Define o caminho base para este controller
public class HistConsultaController {

    private final ConsultaRepository consultaRepository;
    private ClienteRepository clienteRepository;

    @Autowired
    public HistConsultaController(ConsultaRepository consultaRepository, ClienteRepository clienteRepository) {
        this.consultaRepository = consultaRepository;
        this.clienteRepository = clienteRepository;
    }

    @GetMapping("/Consultas")
    public String mostrarPaginaHistoricoConsultas(
            @RequestParam(value = "keyword", required = false) String keyword,
            Model model) {

        List<Consulta> listaConsultasHistorico;

        if (keyword != null && !keyword.trim().isEmpty()) {

            listaConsultasHistorico = consultaRepository.findByClienteNomeContainingIgnoreCase(keyword.trim());
            model.addAttribute("message", "Resultados da busca no histórico por cliente: '" + keyword + "'");
        } else {

            listaConsultasHistorico = consultaRepository.findAll();
        }

        model.addAttribute("consultas", listaConsultasHistorico);
        model.addAttribute("keyword", keyword);

        return "HistConsultas";
    }

    @GetMapping("/editar/{id}")
    public String mostrarFormularioEditarConsulta(@PathVariable("id") Long IdConsulta, Model model,
            RedirectAttributes redirectAttributes) {
        Optional<Consulta> consultaOptional = consultaRepository.findById(IdConsulta); // Use o parâmetro 'id'
        if (!consultaOptional.isPresent()) {
            redirectAttributes.addFlashAttribute("errorMessage", "Consulta não encontrada com ID: " + IdConsulta);
            return "redirect:/agendamentos/consultas";
        }
        // Importante: Adicionar a consulta encontrada ao model para preencher o
        // formulário de edição

        // Se o seu formulário CadConsultas.html precisa da lista de clientes,
        // adicione-a aqui também.
        List<Cliente> todosClientes = clienteRepository.findAll();
        model.addAttribute("todosClientes", todosClientes);
        Consulta consultaParaEditar = consultaOptional.get();
        model.addAttribute("consulta", consultaParaEditar);

        return "CadConsultas"; // Renderiza o formulário de edição
    }

    @GetMapping("/excluir/{id}")
    public String excluirConsulta(@PathVariable("id") Long IdConsulta, RedirectAttributes redirectAttributes) {
        try {
            if (!consultaRepository.existsById(IdConsulta)) {
                redirectAttributes.addFlashAttribute("errorMessage", "Consulta não encontrada com ID: " + IdConsulta);
                return "redirect:/agendamentos/consultas"; // Ou sua página de listagem
            }
            consultaRepository.deleteById(IdConsulta);
            redirectAttributes.addFlashAttribute("successMessage", "Consulta excluída com sucesso!");
        } catch (Exception e) {
            // Adicionar log do erro
            System.err.println("Erro ao excluir consulta: " + e.getMessage());
            e.printStackTrace();
            redirectAttributes.addFlashAttribute("errorMessage",
                    "Erro ao excluir consulta. Verifique se há dependências.");
        }
        return "redirect:/agendamentos/consultas"; // Redireciona para a página de listagem de consultas
    }
}