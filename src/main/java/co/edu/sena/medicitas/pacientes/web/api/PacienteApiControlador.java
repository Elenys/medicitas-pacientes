package co.edu.sena.medicitas.pacientes.web.api;

import co.edu.sena.medicitas.pacientes.formulario.PacienteFormulario;
import co.edu.sena.medicitas.pacientes.modelo.Genero;
import co.edu.sena.medicitas.pacientes.modelo.TipoDocumento;
import co.edu.sena.medicitas.pacientes.servicio.PacienteServicio;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.net.URI;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

/**
 * API REST (JSON) del módulo de pacientes. La usa el front-end en React {@code medicitas-pacientes-web}.
 *
 * <table>
 *   <caption>Rutas</caption>
 *   <tr><td>GET</td><td>/api/pacientes?buscar=</td><td>Listado o búsqueda</td></tr>
 *   <tr><td>GET</td><td>/api/pacientes/{id}</td><td>Un paciente</td></tr>
 *   <tr><td>POST</td><td>/api/pacientes</td><td>Registrar (201 + contraseña temporal)</td></tr>
 *   <tr><td>PUT</td><td>/api/pacientes/{id}</td><td>Actualizar</td></tr>
 *   <tr><td>PATCH</td><td>/api/pacientes/{id}/estado</td><td>Activar o desactivar</td></tr>
 *   <tr><td>GET</td><td>/api/catalogos</td><td>Tipos de documento y géneros</td></tr>
 * </table>
 *
 * <p>Las validaciones son las mismas del formulario web ({@link PacienteFormulario}); los errores se
 * responden con {@link ErrorRespuesta} desde {@link ApiErroresManejador}.</p>
 *
 * @author Carlas Elenys Montalvo Mendoza
 * @author Freyder López
 */
@RestController
@RequestMapping("/api")
public class PacienteApiControlador {

    private final PacienteServicio pacienteServicio;

    public PacienteApiControlador(PacienteServicio pacienteServicio) {
        this.pacienteServicio = pacienteServicio;
    }

    @GetMapping("/pacientes")
    public List<PacienteRespuesta> listar(@RequestParam(name = "buscar", required = false) String buscar) {
        return pacienteServicio.listar(buscar).stream().map(PacienteRespuesta::desde).toList();
    }

    @GetMapping("/pacientes/{id}")
    public PacienteRespuesta obtener(@PathVariable int id) {
        return PacienteRespuesta.desde(pacienteServicio.obtener(id));
    }

    @PostMapping("/pacientes")
    public ResponseEntity<RegistroRespuesta> registrar(@Valid @RequestBody PacienteFormulario formulario) {
        PacienteServicio.RegistroResultado resultado = pacienteServicio.registrar(formulario);
        PacienteRespuesta paciente = PacienteRespuesta.desde(resultado.paciente());
        return ResponseEntity.created(URI.create("/api/pacientes/" + paciente.id()))
                .body(new RegistroRespuesta(paciente, resultado.claveTemporal()));
    }

    @PutMapping("/pacientes/{id}")
    public PacienteRespuesta actualizar(@PathVariable int id, @Valid @RequestBody PacienteFormulario formulario) {
        return PacienteRespuesta.desde(pacienteServicio.actualizar(id, formulario));
    }

    @PatchMapping("/pacientes/{id}/estado")
    public PacienteRespuesta cambiarEstado(@PathVariable int id) {
        pacienteServicio.cambiarEstado(id);
        return PacienteRespuesta.desde(pacienteServicio.obtener(id));
    }

    /** Opciones de las listas desplegables del formulario, con su texto en español. */
    @GetMapping("/catalogos")
    public Map<String, List<Opcion>> catalogos() {
        return Map.of(
                "tiposDocumento", Arrays.stream(TipoDocumento.values()).map(t -> new Opcion(t.name(), t.getEtiqueta())).toList(),
                "generos", Arrays.stream(Genero.values()).map(g -> new Opcion(g.name(), g.getEtiqueta())).toList());
    }
}
