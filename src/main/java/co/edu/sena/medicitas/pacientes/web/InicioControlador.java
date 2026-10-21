package co.edu.sena.medicitas.pacientes.web;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

/**
 * Lleva la raíz de la aplicación al listado de pacientes.
 *
 * @author Carlas Elenys Montalvo Mendoza
 * @author Freyder López
 */
@Controller
public class InicioControlador {

    @GetMapping("/")
    public String inicio() {
        return "redirect:/pacientes";
    }
}
