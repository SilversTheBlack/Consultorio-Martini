
package com.michele.martins.controller;

import com.michele.martins.model.Cliente;
import com.michele.martins.model.Consulta;
import com.michele.martins.repository.ClienteRepository;
import com.michele.martins.repository.ConsultaRepository;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.List;

@Controller
@RequestMapping("/consultas") // Base path for consultation-related actions
public class CadConsultasController {

    private final ConsultaRepository consultaRepository;
    private final ClienteRepository clienteRepository;

    @Autowired
    public CadConsultasController(ConsultaRepository consultaRepository, ClienteRepository clienteRepository) {
        this.consultaRepository = consultaRepository;
        this.clienteRepository = clienteRepository;
    }

    /**
     * 
     * Displays the form to register a new consultation.
     * 
     * Accessed via GET /consultas/novo
     */
    @GetMapping("/novo")
    public String mostrarFormularioNovaConsulta(Model model) {
        // Add an empty Consulta object for form binding
        if (!model.containsAttribute("consulta")) {
            model.addAttribute("consulta", new Consulta());
        }
        // Add a list of all clients to populate the patient dropdown
        List<Cliente> todosClientes = clienteRepository.findAll();
        model.addAttribute("todosClientes", todosClientes);

        return "CadConsultas"; // Renders src/main/resources/templates/CadConsultas.html
    }

    /**
     * 
     * Processes the submission of the new consultation form.
     * 
     * Accessed via POST /consultas
     */
    @PostMapping // Handles POST requests to /consultas
    public String salvarConsulta(@Valid @ModelAttribute("consulta") Consulta consulta,
            BindingResult bindingResult,
            Model model, // Added model to repopulate dropdown on validation error
            RedirectAttributes redirectAttributes) {

        // If validation errors occur, return to the form and display them
        if (bindingResult.hasErrors()) {
            // Repopulate the list of clients for the dropdown if returning to the form
            List<Cliente> todosClientes = clienteRepository.findAll();
            model.addAttribute("todosClientes", todosClientes);
            // The 'consulta' object with errors is already added by @ModelAttribute
            return "CadConsultas"; // Return to the form view, not a redirect
        }

        try {
            // Ensure the Cliente object within Consulta is properly managed if only ID is
            // submitted
            // If th:field="*{cliente}" is used with a select dropdown that submits client
            // ID,
            // Spring might set only the ID on consulta.getCliente().
            // We need to fetch the full Cliente entity.
            if (consulta.getCliente() != null && consulta.getCliente().getId() != null) { // Assuming Cliente has
                                                                                          // getIdCliente()
                Cliente clienteSelecionado = clienteRepository.findById(consulta.getCliente().getId())
                        .orElseThrow(() -> new IllegalArgumentException(
                                "Cliente inválido Id: " + consulta.getCliente().getId()));
                consulta.setCliente(clienteSelecionado);
            } else {
                // Handle case where no client is selected or client ID is null, if necessary
                // For example, add a binding error
                bindingResult.rejectValue("cliente", "error.cliente", "Cliente é obrigatório.");
                List<Cliente> todosClientes = clienteRepository.findAll();
                model.addAttribute("todosClientes", todosClientes);
                return "CadConsultas";
            }

            consultaRepository.save(consulta);
            redirectAttributes.addFlashAttribute("successMessage", "Consulta agendada com sucesso!");
            return "redirect:/consultas/novo"; // Redirect to allow adding another or to a list page
        } catch (Exception e) {
            System.err.println("Erro ao salvar consulta: " + e.getMessage());
            e.printStackTrace();
            redirectAttributes.addFlashAttribute("errorMessage", "Erro ao salvar consulta: " + e.getMessage());
            // Repopulate necessary model attributes for the form if redirecting back
            redirectAttributes.addFlashAttribute("consulta", consulta);
            // Fetching clients again for the redirect might be needed if not using flash
            // attributes for everything
            // For simplicity, redirecting to novo which will fetch clients again.
            return "redirect:/consultas/novo";
        }
    }
}