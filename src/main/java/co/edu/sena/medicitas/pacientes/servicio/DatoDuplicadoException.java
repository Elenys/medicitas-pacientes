package co.edu.sena.medicitas.pacientes.servicio;

/**
 * Se lanza cuando el correo o el documento ya pertenecen a otro usuario.
 * Indica el campo del formulario para mostrar el mensaje junto a él.
 *
 * @author Carlas Elenys Montalvo Mendoza
 * @author Freyder López
 */
public class DatoDuplicadoException extends RuntimeException {

    private final String campo;

    public DatoDuplicadoException(String campo, String mensaje) {
        super(mensaje);
        this.campo = campo;
    }

    public String getCampo() {
        return campo;
    }
}
