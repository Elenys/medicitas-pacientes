package co.edu.sena.medicitas.pacientes.web;

import co.edu.sena.medicitas.pacientes.formulario.PacienteFormulario;
import co.edu.sena.medicitas.pacientes.modelo.Genero;
import co.edu.sena.medicitas.pacientes.modelo.Paciente;
import co.edu.sena.medicitas.pacientes.modelo.TipoDocumento;
import co.edu.sena.medicitas.pacientes.servicio.DatoDuplicadoException;
import co.edu.sena.medicitas.pacientes.servicio.PacienteNoEncontradoException;
import co.edu.sena.medicitas.pacientes.servicio.PacienteServicio;
import jakarta.validation.Valid;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

/**
 * Controlador web (Spring MVC) del módulo de pacientes: listado, registro, consulta, edición y
 * activación/desactivación. Las vistas son plantillas Thymeleaf en {@code templates/pacientes}.
 *
 * <p>Si el formulario tiene errores de validación se vuelve a mostrar con los mensajes junto a cada
 * campo; si todo es correcto se redirige (patrón Post/Redirect/Get) con un mensaje de confirmación.</p>
 *
 * @author Carlas Elenys Montalvo Mendoza
 * @author Freyder López
 */
@Controller
@RequestMapping("/pacientes")
public class PacienteControlador {

    private static final String VISTA_LISTA = "pacientes/lista";
    private static final String VISTA_FORMULARIO = "pacientes/formulario";
    private static final String VISTA_DETALLE = "pacientes/detalle";
    private static final String REDIRECCION_LISTA = "redirect:/pacientes";

    private final PacienteServicio pacienteServicio;

    public PacienteControlador(PacienteServicio pacienteServicio) {
        this.pacienteServicio = pacienteServicio;
    }

    /** Opciones de las listas desplegables, disponibles en todas las vistas. */
    @ModelAttribute("tiposDocumento")
    public TipoDocumento[] tiposDocumento() {
        return TipoDocumento.values();
    }

    @ModelAttribute("generos")
    public Genero[] generos() {
        return Genero.values();
    }

    @GetMapping
    public String listar(@RequestParam(name = "buscar", required = false) String buscar, Model modelo) {
        modelo.addAttribute("pacientes", pacienteServicio.listar(buscar));
        modelo.addAttribute("buscar", buscar == null ? "" : buscar.trim());
        return VISTA_LISTA;
    }

    @GetMapping("/nuevo")
    public String nuevo(Model modelo) {
        modelo.addAttribute("formulario", new PacienteFormulario());
        modelo.addAttribute("edicion", false);
        return VISTA_FORMULARIO;
    }

    @PostMapping
    public String registrar(@Valid @ModelAttribute("formulario") PacienteFormulario formulario, BindingResult errores,
                            Model modelo, RedirectAttributes redireccion) {
        modelo.addAttribute("edicion", false);
        if (errores.hasErrors()) {
            return VISTA_FORMULARIO;
        }
        try {
            PacienteServicio.RegistroResultado resultado = pacienteServicio.registrar(formulario);
            redireccion.addFlashAttribute("mensaje", "Paciente " + resultado.paciente().getUsuario().getNombreCompleto() + " registrado.");
            redireccion.addFlashAttribute("claveTemporal", resultado.claveTemporal());
            return "redirect:/pacientes/" + resultado.paciente().getId();
        } catch (DatoDuplicadoException duplicado) {
            errores.rejectValue(duplicado.getCampo(), "duplicado", duplicado.getMessage());
            return VISTA_FORMULARIO;
        }
    }

    @GetMapping("/{id}")
    public String detalle(@PathVariable int id, Model modelo) {
        modelo.addAttribute("paciente", pacienteServicio.obtener(id));
        return VISTA_DETALLE;
    }

    @GetMapping("/{id}/editar")
    public String editar(@PathVariable int id, Model modelo) {
        Paciente paciente = pacienteServicio.obtener(id);
        modelo.addAttribute("formulario", PacienteFormulario.desde(paciente));
        modelo.addAttribute("idPaciente", id);
        modelo.addAttribute("edicion", true);
        return VISTA_FORMULARIO;
    }

    @PostMapping("/{id}")
    public String actualizar(@PathVariable int id, @Valid @ModelAttribute("formulario") PacienteFormulario formulario,
                             BindingResult errores, Model modelo, RedirectAttributes redireccion) {
        modelo.addAttribute("idPaciente", id);
        modelo.addAttribute("edicion", true);
        if (errores.hasErrors()) {
            return VISTA_FORMULARIO;
        }
        try {
            Paciente paciente = pacienteServicio.actualizar(id, formulario);
            redireccion.addFlashAttribute("mensaje", "Datos de " + paciente.getUsuario().getNombreCompleto() + " actualizados.");
            return "redirect:/pacientes/" + id;
        } catch (DatoDuplicadoException duplicado) {
            errores.rejectValue(duplicado.getCampo(), "duplicado", duplicado.getMessage());
            return VISTA_FORMULARIO;
        }
    }

    @PostMapping("/{id}/estado")
    public String cambiarEstado(@PathVariable int id, RedirectAttributes redireccion) {
        boolean activo = pacienteServicio.cambiarEstado(id);
        redireccion.addFlashAttribute("mensaje", activo ? "La cuenta del paciente quedó activa." : "La cuenta del paciente quedó desactivada.");
        return "redirect:/pacientes/" + id;
    }

    @ExceptionHandler(PacienteNoEncontradoException.class)
    public String noEncontrado(PacienteNoEncontradoException error, RedirectAttributes redireccion) {
        redireccion.addFlashAttribute("error", error.getMessage());
        return REDIRECCION_LISTA;
    }
}
