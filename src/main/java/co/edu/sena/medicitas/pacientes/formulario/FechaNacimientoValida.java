package co.edu.sena.medicitas.pacientes.formulario;

import jakarta.validation.Constraint;
import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;
import jakarta.validation.Payload;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import java.time.LocalDate;

/**
 * Validación propia: la fecha de nacimiento debe estar entre el 1 de enero de 1900 y hoy,
 * la misma regla que la restricción {@code patients_birth_date_range} de la base de datos.
 * Un valor vacío lo rechaza {@code @NotNull}, no esta validación.
 *
 * @author Carlas Elenys Montalvo Mendoza
 * @author Freyder López
 */
@Documented
@Constraint(validatedBy = FechaNacimientoValida.Validador.class)
@Target(ElementType.FIELD)
@Retention(RetentionPolicy.RUNTIME)
public @interface FechaNacimientoValida {

    String message() default "La fecha de nacimiento debe estar entre el 01/01/1900 y hoy";

    Class<?>[] groups() default {};

    Class<? extends Payload>[] payload() default {};

    /** Implementación de la regla. */
    class Validador implements ConstraintValidator<FechaNacimientoValida, LocalDate> {

        private static final LocalDate FECHA_MINIMA = LocalDate.of(1900, 1, 1);

        @Override
        public boolean isValid(LocalDate fecha, ConstraintValidatorContext contexto) {
            return fecha == null || (!fecha.isBefore(FECHA_MINIMA) && !fecha.isAfter(LocalDate.now()));
        }
    }
}
