package co.edu.sena.medicitas.pacientes.modelo;

/**
 * Género del paciente (tipo {@code gender} de PostgreSQL).
 *
 * @author Carlas Elenys Montalvo Mendoza
 * @author Freyder López
 */
public enum Genero {
    FEMALE("Femenino"),
    MALE("Masculino"),
    OTHER("Otro");

    private final String etiqueta;

    Genero(String etiqueta) {
        this.etiqueta = etiqueta;
    }

    /** Texto que se muestra en la interfaz. */
    public String getEtiqueta() {
        return etiqueta;
    }
}
