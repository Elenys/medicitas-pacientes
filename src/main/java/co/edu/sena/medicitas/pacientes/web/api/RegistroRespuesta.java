package co.edu.sena.medicitas.pacientes.web.api;

/**
 * Respuesta del registro: el paciente creado y su contraseña temporal, que solo se envía esta vez.
 *
 * @author Carlas Elenys Montalvo Mendoza
 * @author Freyder López
 */
public record RegistroRespuesta(PacienteRespuesta paciente, String claveTemporal) {
}
