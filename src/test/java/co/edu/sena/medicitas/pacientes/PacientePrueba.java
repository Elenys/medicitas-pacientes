package co.edu.sena.medicitas.pacientes;

import co.edu.sena.medicitas.pacientes.formulario.PacienteFormulario;
import co.edu.sena.medicitas.pacientes.modelo.Genero;
import co.edu.sena.medicitas.pacientes.modelo.TipoDocumento;

import java.time.LocalDate;

/**
 * Datos de prueba reutilizables: un formulario válido que cada prueba modifica.
 *
 * @author Carlas Elenys Montalvo Mendoza
 * @author Freyder López
 */
public final class PacientePrueba {

    private PacientePrueba() {
    }

    /** Formulario con todos los datos correctos; el documento y el correo no existen en la base. */
    public static PacienteFormulario formularioValido() {
        PacienteFormulario formulario = new PacienteFormulario();
        formulario.setTipoDocumento(TipoDocumento.CC);
        formulario.setNumeroDocumento("9988776655");
        formulario.setNombres("Lucía Andrea");
        formulario.setApellidos("Pérez Gómez");
        formulario.setCorreo("lucia.prueba@correo.com");
        formulario.setTelefono("3001234567");
        formulario.setFechaNacimiento(LocalDate.of(1995, 4, 12));
        formulario.setGenero(Genero.FEMALE);
        formulario.setDireccion("Calle 10 # 5-20, Montería");
        formulario.setEps("Nueva EPS");
        return formulario;
    }
}
