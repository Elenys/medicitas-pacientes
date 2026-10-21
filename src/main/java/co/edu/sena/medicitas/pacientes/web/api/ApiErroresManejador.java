package co.edu.sena.medicitas.pacientes.web.api;

import co.edu.sena.medicitas.pacientes.servicio.DatoDuplicadoException;
import co.edu.sena.medicitas.pacientes.servicio.PacienteNoEncontradoException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.LinkedHashMap;
import java.util.Map;

/**
 * Convierte las excepciones de la API en respuestas JSON con el código HTTP adecuado:
 * 400 (datos inválidos), 404 (paciente inexistente) y 409 (documento o correo duplicado).
 *
 * <p>Solo aplica al controlador de la API; las páginas Thymeleaf manejan sus errores aparte.</p>
 *
 * @author Carlas Elenys Montalvo Mendoza
 * @author Freyder López
 */
@RestControllerAdvice(assignableTypes = PacienteApiControlador.class)
public class ApiErroresManejador {

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ErrorRespuesta> datosInvalidos(MethodArgumentNotValidException error) {
        Map<String, String> errores = new LinkedHashMap<>();
        for (FieldError campo : error.getBindingResult().getFieldErrors()) {
            errores.putIfAbsent(campo.getField(), campo.getDefaultMessage());
        }
        return responder(HttpStatus.BAD_REQUEST, "Revise los campos marcados: hay datos que no cumplen las reglas.", errores);
    }

    /** JSON mal formado, fecha imposible o un valor que no está en la lista (tipo de documento, género). */
    @ExceptionHandler(HttpMessageNotReadableException.class)
    public ResponseEntity<ErrorRespuesta> jsonIlegible(HttpMessageNotReadableException error) {
        return responder(HttpStatus.BAD_REQUEST, "Los datos enviados no tienen un formato válido.", Map.of());
    }

    @ExceptionHandler(DatoDuplicadoException.class)
    public ResponseEntity<ErrorRespuesta> duplicado(DatoDuplicadoException error) {
        return responder(HttpStatus.CONFLICT, error.getMessage(), Map.of(error.getCampo(), error.getMessage()));
    }

    @ExceptionHandler(PacienteNoEncontradoException.class)
    public ResponseEntity<ErrorRespuesta> noEncontrado(PacienteNoEncontradoException error) {
        return responder(HttpStatus.NOT_FOUND, error.getMessage(), Map.of());
    }

    private static ResponseEntity<ErrorRespuesta> responder(HttpStatus estado, String mensaje, Map<String, String> errores) {
        return ResponseEntity.status(estado).body(new ErrorRespuesta(estado.value(), mensaje, errores));
    }
}
