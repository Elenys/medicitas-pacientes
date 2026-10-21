package co.edu.sena.medicitas.pacientes.modelo;

/**
 * Tipos de documento de identidad aceptados por MediCitas (tipo {@code document_type} de PostgreSQL).
 *
 * @author Carlas Elenys Montalvo Mendoza
 * @author Freyder López
 */
public enum TipoDocumento {
    CC("Cédula de ciudadanía"),
    TI("Tarjeta de identidad"),
    CE("Cédula de extranjería"),
    PA("Pasaporte");

    private final String etiqueta;

    TipoDocumento(String etiqueta) {
        this.etiqueta = etiqueta;
    }

    /** Texto que se muestra en la interfaz. */
    public String getEtiqueta() {
        return etiqueta;
    }
}
