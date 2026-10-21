package co.edu.sena.medicitas.pacientes.web.api;

import java.util.Map;

/**
 * Cuerpo JSON de las respuestas de error de la API.
 *
 * @param estado  código HTTP (400, 404, 409)
 * @param mensaje descripción general del error
 * @param errores mensaje por campo del formulario (vacío si el error no es de un campo)
 * @author Carlas Elenys Montalvo Mendoza
 * @author Freyder López
 */
public record ErrorRespuesta(int estado, String mensaje, Map<String, String> errores) {
}
