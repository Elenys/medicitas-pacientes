package co.edu.sena.medicitas.pacientes.servicio;

import co.edu.sena.medicitas.pacientes.formulario.PacienteFormulario;
import co.edu.sena.medicitas.pacientes.modelo.Paciente;
import co.edu.sena.medicitas.pacientes.modelo.Usuario;
import co.edu.sena.medicitas.pacientes.repositorio.PacienteRepositorio;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.security.SecureRandom;
import java.util.List;

/**
 * Reglas de negocio del módulo de pacientes.
 *
 * <ul>
 *   <li>El correo y el tipo + número de documento no se pueden repetir.</li>
 *   <li>Al registrar un paciente se crea su usuario con una contraseña temporal aleatoria, que se
 *       muestra una sola vez y se guarda cifrada con BCrypt (igual que en MediCitas).</li>
 *   <li>Los pacientes no se eliminan (conservan su historial de citas): se activan o desactivan.</li>
 * </ul>
 *
 * @author Carlas Elenys Montalvo Mendoza
 * @author Freyder López
 */
@Service
public class PacienteServicio {

    private static final String CARACTERES_CLAVE = "ABCDEFGHJKLMNPQRSTUVWXYZabcdefghijkmnpqrstuvwxyz23456789";
    private static final int LONGITUD_CLAVE = 10;

    private final PacienteRepositorio pacienteRepositorio;
    private final BCryptPasswordEncoder cifrador = new BCryptPasswordEncoder();
    private final SecureRandom aleatorio = new SecureRandom();

    public PacienteServicio(PacienteRepositorio pacienteRepositorio) {
        this.pacienteRepositorio = pacienteRepositorio;
    }

    /** Lista todos los pacientes o, si hay texto, los que coinciden con la búsqueda. */
    @Transactional(readOnly = true)
    public List<Paciente> listar(String busqueda) {
        return busqueda == null || busqueda.isBlank()
                ? pacienteRepositorio.listarOrdenados()
                : pacienteRepositorio.buscar(busqueda.trim());
    }

    @Transactional(readOnly = true)
    public Paciente obtener(int id) {
        return pacienteRepositorio.findById(id).orElseThrow(() -> new PacienteNoEncontradoException(id));
    }

    /**
     * Registra un paciente y su usuario en una sola transacción.
     *
     * @return el paciente creado y la contraseña temporal en texto plano (solo para mostrarla una vez)
     */
    @Transactional
    public RegistroResultado registrar(PacienteFormulario formulario) {
        validarDuplicados(formulario, null);
        String claveTemporal = generarClaveTemporal();
        Paciente paciente = new Paciente(Usuario.nuevoPaciente(cifrador.encode(claveTemporal)));
        formulario.aplicarA(paciente);
        return new RegistroResultado(pacienteRepositorio.save(paciente), claveTemporal);
    }

    /** Actualiza los datos personales y de contacto del paciente. */
    @Transactional
    public Paciente actualizar(int id, PacienteFormulario formulario) {
        Paciente paciente = obtener(id);
        validarDuplicados(formulario, paciente.getUsuario().getId());
        formulario.aplicarA(paciente);
        return pacienteRepositorio.save(paciente);
    }

    /** Activa o desactiva la cuenta del paciente. Devuelve el nuevo estado. */
    @Transactional
    public boolean cambiarEstado(int id) {
        Paciente paciente = obtener(id);
        paciente.getUsuario().setActivo(!paciente.getUsuario().isActivo());
        return paciente.getUsuario().isActivo();
    }

    private void validarDuplicados(PacienteFormulario formulario, Integer idUsuarioActual) {
        if (pacienteRepositorio.existeDocumento(formulario.getTipoDocumento(), formulario.getNumeroDocumento().trim(), idUsuarioActual)) {
            throw new DatoDuplicadoException("numeroDocumento", "Ya existe un usuario con ese tipo y número de documento");
        }
        if (pacienteRepositorio.existeCorreo(formulario.getCorreo().trim(), idUsuarioActual)) {
            throw new DatoDuplicadoException("correo", "Ya existe un usuario con ese correo");
        }
    }

    private String generarClaveTemporal() {
        StringBuilder clave = new StringBuilder(LONGITUD_CLAVE);
        for (int i = 0; i < LONGITUD_CLAVE; i++) {
            clave.append(CARACTERES_CLAVE.charAt(aleatorio.nextInt(CARACTERES_CLAVE.length())));
        }
        return clave.toString();
    }

    /** Resultado del registro: el paciente guardado y su contraseña temporal. */
    public record RegistroResultado(Paciente paciente, String claveTemporal) {
    }
}
