package co.edu.sena.medicitas.pacientes.servicio;

/**
 * Se lanza al consultar un paciente que no existe.
 *
 * @author Carlas Elenys Montalvo Mendoza
 * @author Freyder López
 */
public class PacienteNoEncontradoException extends RuntimeException {

    public PacienteNoEncontradoException(int id) {
        super("No existe un paciente con el id " + id);
    }
}
