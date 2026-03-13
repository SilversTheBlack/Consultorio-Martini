package com.michele.martins.controller; // Or your actual package name

import com.michele.martins.model.Cliente; // Assuming your Cliente entity exists
import com.michele.martins.repository.ClienteRepository; // Assuming your ClienteRepository exists

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import jakarta.validation.Valid;

@Controller
@RequestMapping("/cliente") // Base path for patient-related actions
public class PacienteController {
    private final ClienteRepository clienteRepository;

    @Autowired
    public PacienteController(ClienteRepository clienteRepository) {
        this.clienteRepository = clienteRepository;
    }

    /**
     * Displays the form for creating a new patient.
     * 
     * @param model The Spring Model to add attributes to for the view.
     * @return The name of the Thymeleaf template to render (CadPaciente.html).
     */
    @GetMapping("/novo")
    public String mostrarFormularioCadastro(Model model) {
        if (!model.containsAttribute("cliente")) {
            model.addAttribute("cliente", new Cliente());
        }
        return "CadPaciente";
    }

    /**
     * 
     * Processes the submission of the new patient form.
     * 
     * @param cliente            The Cliente object populated with form data.
     * 
     * @param bindingResult      For form validation results (optional).
     * 
     * @param redirectAttributes Used to pass messages after a redirect.
     * 
     * @return A redirect instruction to another page (e.g., a patient list or back
     *         to the form).
     */
    @PostMapping("/salvar") // This path should match the th:action in your form
    public String salvarPaciente(@Valid @ModelAttribute("cliente") Cliente cliente,
            BindingResult bindingResult,
            RedirectAttributes redirectAttributes) {

        if (bindingResult.hasErrors()) {
            // If there are validation errors, add the cliente object (with errors)
            // back to the redirect attributes so they can be displayed on the form.
            // Also add the BindingResult itself.
            redirectAttributes.addFlashAttribute("org.springframework.validation.BindingResult.cliente", bindingResult);
            redirectAttributes.addFlashAttribute("cliente", cliente);
            return "redirect:/cliente/novo"; // Redirect back to the form to show errors
        }

        try {
            clienteRepository.save(cliente);
            redirectAttributes.addFlashAttribute("successMessage",
                    "Paciente '" + cliente.getNome() + "' salvo com sucesso!");
            // Redirect to a patient list page (e.g., /pacientes) or back to the form, or
            // index.
            // For this example, let's redirect to the new patient form page,
            // which could then display the success message.
            return "redirect:/cliente/novo"; // Or "redirect:/pacientes" if you have a list page
        } catch (Exception e) {
            // Log the exception (e.g., using SLF4J logger)
            // logger.error("Error saving paciente: {}", e.getMessage());
            redirectAttributes.addFlashAttribute("errorMessage", "Erro ao salvar o paciente: " + e.getMessage());
            redirectAttributes.addFlashAttribute("cliente", cliente); // Send back the object for re-editing
            return "redirect:/cliente/novo";
        }

    }
}
// You might also want a method to list patients:
// @GetMapping
// public String listarPacientes(Model model) {
// model.addAttribute("listaPacientes", clienteRepository.findAll());
// return "lista-pacientes"; // Assuming you have a lista-pacientes.