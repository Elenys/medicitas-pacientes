package co.edu.sena.medicitas.pacientes.web.api;

import co.edu.sena.medicitas.pacientes.modelo.Paciente;
import co.edu.sena.medicitas.pacientes.modelo.Usuario;

import java.time.LocalDate;
import java.time.ZonedDateTime;

/**
 * Datos de un paciente que devuelve la API en formato JSON.
 *
 * <p>Se usa un objeto de respuesta propio (y no la entidad JPA) para controlar exactamente qué se
 * expone: nunca se envía el hash de la contraseña ni otros datos internos del usuario.</p>
 *
 * @author Carlas Elenys Montalvo Mendoza
 * @author Freyder López
 */
public record PacienteRespuesta(
        Integer id,
        String tipoDocumento,
        String tipoDocumentoEtiqueta,
        String numeroDocumento,
        String nombres,
        String apellidos,
        String nombreCompleto,
        String correo,
        String telefono,
        LocalDate fechaNacimiento,
        int edad,
        String genero,
        String generoEtiqueta,
        String direccion,
        String eps,
        boolean activo,
        ZonedDateTime registradoEn) {

    /** Convierte la entidad en la respuesta de la API. */
    public static PacienteRespuesta desde(Paciente paciente) {
        Usuario usuario = paciente.getUsuario();
        return new PacienteRespuesta(
                paciente.getId(),
                usuario.getTipoDocumento().name(),
                usuario.getTipoDocumento().getEtiqueta(),
                usuario.getNumeroDocumento(),
                usuario.getNombres(),
                usuario.getApellidos(),
                usuario.getNombreCompleto(),
                usuario.getCorreo(),
                usuario.getTelefono(),
                paciente.getFechaNacimiento(),
                paciente.getEdad(),
                paciente.getGenero().name(),
                paciente.getGenero().getEtiqueta(),
                paciente.getDireccion(),
                paciente.getEps(),
                usuario.isActivo(),
                usuario.getCreadoEnLocal());
    }
}
