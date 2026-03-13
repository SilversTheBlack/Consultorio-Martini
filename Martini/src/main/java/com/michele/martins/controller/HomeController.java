package com.michele.martins.controller;

import com.michele.martins.model.Consulta; // Assuming this is your Consulta entity
import com.michele.martins.repository.ClienteRepository;
import com.michele.martins.repository.ConsultaRepository; // You will need this repository
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import java.util.List; // Import List

@Controller
public class HomeController {

    // Inject ConsultaRepository to fetch consultation data
    @Autowired
    private ConsultaRepository consultaRepository;

    // Optional: If you still need ClienteRepository for other purposes, you can
    // keep it.
    @Autowired
    private ClienteRepository clienteRepository;

    @GetMapping("/") // Maps to the root path
    public String home(Model model) {
        // Fetch all Consulta objects
        try {
            List<Consulta> todasConsultas = consultaRepository.findAll(); // Temporarily
            // comment this out
            // Test with an empty list
            model.addAttribute("consultas", todasConsultas);

            System.out.println("Found " + todasConsultas.size() + " consultations");
        } catch (Exception e) {
            System.err.println("Error in HomeController before returning view: " + e.getMessage());
            e.printStackTrace(); // Print stack trace to console
            // Optionally, you could add the error to the model to display on an error page
            // model.addAttribute("errorMessage", "Error fetching data: " + e.getMessage());
            // return "error_page"; // Or re-throw to let Spring Boot handle it
        }
        // Return the name of the HTML file (without .html extension)
        // This maps to src/main/resources/templates/index.html
        return "index";
    }

    // The previous ambiguous @GetMapping for listar(clientes) has been removed
    // to avoid conflict and because index.html now expects "consultas".
    // If you need a separate page for listing "clientes",
    // it should have a different mapping (e.g., @GetMapping("/clientes"))
    // and return a different view or the same view if it's designed to handle both.

}