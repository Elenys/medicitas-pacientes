package co.edu.sena.medicitas.pacientes.formulario;

import co.edu.sena.medicitas.pacientes.PacientePrueba;

import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validation;
import jakarta.validation.Validator;
import jakarta.validation.ValidatorFactory;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

import java.time.LocalDate;
import java.util.Set;
import java.util.function.Consumer;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Pruebas unitarias de las reglas de validación del formulario de pacientes (sin base de datos).
 *
 * @author Carlas Elenys Montalvo Mendoza
 * @author Freyder López
 */
class PacienteFormularioTest {

    private static ValidatorFactory fabrica;
    private static Validator validador;

    @BeforeAll
    static void crearValidador() {
        fabrica = Validation.buildDefaultValidatorFactory();
        validador = fabrica.getValidator();
    }

    @AfterAll
    static void cerrarValidador() {
        fabrica.close();
    }

    @Test
    @DisplayName("Un formulario con datos correctos no tiene errores")
    void formularioValidoSinErrores() {
        assertThat(validador.validate(PacientePrueba.formularioValido())).isEmpty();
    }

    @Test
    @DisplayName("Los campos obligatorios vacíos se rechazan")
    void camposObligatorios() {
        Set<String> campos = camposConError(new PacienteFormulario());
        assertThat(campos).containsExactlyInAnyOrder("tipoDocumento", "numeroDocumento", "nombres",
                "apellidos", "correo", "fechaNacimiento", "genero");
    }

    @Test
    @DisplayName("Un campo obligatorio vacío muestra un solo mensaje")
    void vacioUnSoloMensaje() {
        PacienteFormulario formulario = PacientePrueba.formularioValido();
        formulario.setApellidos("");
        formulario.setNumeroDocumento("");
        assertThat(validador.validate(formulario)).hasSize(2)
                .extracting(ConstraintViolation::getMessage)
                .containsExactlyInAnyOrder("Los apellidos son obligatorios", "El número de documento es obligatorio");
    }

    @ParameterizedTest(name = "documento «{0}» se rechaza")
    @ValueSource(strings = {"12345", "12345678901", "10.234.567", "ABC123456"})
    void documentoInvalido(String documento) {
        assertThat(errorEn(f -> f.setNumeroDocumento(documento))).isEqualTo("numeroDocumento");
    }

    @ParameterizedTest(name = "nombre «{0}» se rechaza")
    @ValueSource(strings = {"Ana2", "<script>", "Juan_Carlos", "María@"})
    void nombreConCaracteresNoPermitidos(String nombre) {
        assertThat(errorEn(f -> f.setNombres(nombre))).isEqualTo("nombres");
    }

    @Test
    @DisplayName("Nombres con tildes, ñ y apóstrofo son válidos")
    void nombresEnEspanol() {
        PacienteFormulario formulario = PacientePrueba.formularioValido();
        formulario.setNombres("José Ñúñez");
        formulario.setApellidos("D'Alessandro Muñoz");
        assertThat(validador.validate(formulario)).isEmpty();
    }

    @Test
    @DisplayName("Los apellidos de más de 60 caracteres se rechazan")
    void apellidosMuyLargos() {
        assertThat(errorEn(f -> f.setApellidos("A".repeat(61)))).isEqualTo("apellidos");
    }

    @ParameterizedTest(name = "correo «{0}» se rechaza")
    @ValueSource(strings = {"lucia", "lucia@", "lucia@correo", "lucia correo@x.com"})
    void correoInvalido(String correo) {
        assertThat(errorEn(f -> f.setCorreo(correo))).isEqualTo("correo");
    }

    @ParameterizedTest(name = "celular «{0}» se rechaza")
    @ValueSource(strings = {"300123456", "2001234567", "30012345678", "300-123-4567"})
    void celularInvalido(String telefono) {
        assertThat(errorEn(f -> f.setTelefono(telefono))).isEqualTo("telefono");
    }

    @Test
    @DisplayName("El celular es opcional")
    void celularOpcional() {
        PacienteFormulario formulario = PacientePrueba.formularioValido();
        formulario.setTelefono("");
        assertThat(validador.validate(formulario)).isEmpty();
    }

    @Test
    @DisplayName("Una fecha de nacimiento futura se rechaza")
    void fechaFutura() {
        assertThat(errorEn(f -> f.setFechaNacimiento(LocalDate.now().plusDays(1)))).isEqualTo("fechaNacimiento");
    }

    @Test
    @DisplayName("Una fecha de nacimiento anterior a 1900 se rechaza")
    void fechaMuyAntigua() {
        assertThat(errorEn(f -> f.setFechaNacimiento(LocalDate.of(1899, 12, 31)))).isEqualTo("fechaNacimiento");
    }

    @Test
    @DisplayName("La fecha de hoy es válida (recién nacido)")
    void fechaDeHoy() {
        PacienteFormulario formulario = PacientePrueba.formularioValido();
        formulario.setFechaNacimiento(LocalDate.now());
        assertThat(validador.validate(formulario)).isEmpty();
    }

    @ParameterizedTest(name = "dirección «{0}» se rechaza")
    @ValueSource(strings = {"<b>Calle 1</b>", "Calle 1; DROP TABLE users", "${jndi:x}"})
    void direccionPeligrosa(String direccion) {
        assertThat(errorEn(f -> f.setDireccion(direccion))).isEqualTo("direccion");
    }

    @Test
    @DisplayName("La EPS de más de 80 caracteres se rechaza")
    void epsMuyLarga() {
        assertThat(errorEn(f -> f.setEps("E".repeat(81)))).isEqualTo("eps");
    }

    /** Aplica un cambio al formulario válido y devuelve el único campo con error. */
    private static String errorEn(Consumer<PacienteFormulario> cambio) {
        PacienteFormulario formulario = PacientePrueba.formularioValido();
        cambio.accept(formulario);
        Set<String> campos = camposConError(formulario);
        assertThat(campos).hasSize(1);
        return campos.iterator().next();
    }

    private static Set<String> camposConError(PacienteFormulario formulario) {
        Set<ConstraintViolation<PacienteFormulario>> errores = validador.validate(formulario);
        return errores.stream().map(e -> e.getPropertyPath().toString())
                .collect(java.util.stream.Collectors.toSet());
    }
}
