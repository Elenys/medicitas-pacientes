package co.edu.sena.medicitas.pacientes.servicio;

import co.edu.sena.medicitas.pacientes.PacientePrueba;
import co.edu.sena.medicitas.pacientes.formulario.PacienteFormulario;
import co.edu.sena.medicitas.pacientes.modelo.Paciente;
import co.edu.sena.medicitas.pacientes.modelo.Rol;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.transaction.annotation.Transactional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

/**
 * Pruebas de integración del servicio contra la base PostgreSQL real de MediCitas.
 * Cada prueba corre en una transacción que se revierte al final: la base queda como estaba.
 *
 * @author Carlas Elenys Montalvo Mendoza
 * @author Freyder López
 */
@SpringBootTest
@Transactional
class PacienteServicioTest {

    @Autowired
    private PacienteServicio servicio;

    @Test
    @DisplayName("Registrar crea el usuario con rol PATIENT y la contraseña cifrada con BCrypt")
    void registrarPaciente() {
        PacienteServicio.RegistroResultado resultado = servicio.registrar(PacientePrueba.formularioValido());
        Paciente paciente = resultado.paciente();

        assertThat(paciente.getId()).isNotNull();
        assertThat(paciente.getUsuario().getRol()).isEqualTo(Rol.PATIENT);
        assertThat(paciente.getUsuario().isActivo()).isTrue();
        assertThat(resultado.claveTemporal()).hasSize(10);
        assertThat(paciente.getUsuario().getClaveCifrada()).startsWith("$2").isNotEqualTo(resultado.claveTemporal());
        assertThat(new BCryptPasswordEncoder().matches(resultado.claveTemporal(), paciente.getUsuario().getClaveCifrada())).isTrue();
    }

    @Test
    @DisplayName("Registrar normaliza el correo a minúsculas y quita espacios sobrantes")
    void normalizaDatos() {
        PacienteFormulario formulario = PacientePrueba.formularioValido();
        formulario.setCorreo("  Lucia.Prueba@Correo.COM ");
        formulario.setNombres("  Lucía   Andrea ");
        Paciente paciente = servicio.registrar(formulario).paciente();
        assertThat(paciente.getUsuario().getCorreo()).isEqualTo("lucia.prueba@correo.com");
        assertThat(paciente.getUsuario().getNombres()).isEqualTo("Lucía Andrea");
    }

    @Test
    @DisplayName("No se registra un documento repetido")
    void documentoRepetido() {
        servicio.registrar(PacientePrueba.formularioValido());
        PacienteFormulario otro = PacientePrueba.formularioValido();
        otro.setCorreo("otra.persona@correo.com");
        assertThatThrownBy(() -> servicio.registrar(otro))
                .isInstanceOf(DatoDuplicadoException.class)
                .extracting("campo").isEqualTo("numeroDocumento");
    }

    @Test
    @DisplayName("No se registra un correo repetido (sin importar mayúsculas)")
    void correoRepetido() {
        servicio.registrar(PacientePrueba.formularioValido());
        PacienteFormulario otro = PacientePrueba.formularioValido();
        otro.setNumeroDocumento("1122334455");
        otro.setCorreo("LUCIA.PRUEBA@correo.com");
        assertThatThrownBy(() -> servicio.registrar(otro))
                .isInstanceOf(DatoDuplicadoException.class)
                .extracting("campo").isEqualTo("correo");
    }

    @Test
    @DisplayName("Buscar encuentra por nombre, documento y correo")
    void buscar() {
        servicio.registrar(PacientePrueba.formularioValido());
        assertThat(servicio.listar("lucía")).extracting(p -> p.getUsuario().getNumeroDocumento()).contains("9988776655");
        assertThat(servicio.listar("99887766")).hasSize(1);
        assertThat(servicio.listar("lucia.prueba@")).hasSize(1);
        assertThat(servicio.listar("zzzz-no-existe")).isEmpty();
    }

    @Test
    @DisplayName("Actualizar cambia los datos y permite conservar el mismo documento y correo")
    void actualizar() {
        int id = servicio.registrar(PacientePrueba.formularioValido()).paciente().getId();
        PacienteFormulario cambios = PacienteFormulario.desde(servicio.obtener(id));
        cambios.setEps("Sura EPS");
        cambios.setTelefono("3109876543");
        servicio.actualizar(id, cambios);

        Paciente actualizado = servicio.obtener(id);
        assertThat(actualizado.getEps()).isEqualTo("Sura EPS");
        assertThat(actualizado.getUsuario().getTelefono()).isEqualTo("3109876543");
    }

    @Test
    @DisplayName("Cambiar estado desactiva y vuelve a activar la cuenta")
    void cambiarEstado() {
        int id = servicio.registrar(PacientePrueba.formularioValido()).paciente().getId();
        assertThat(servicio.cambiarEstado(id)).isFalse();
        assertThat(servicio.cambiarEstado(id)).isTrue();
    }

    @Test
    @DisplayName("Consultar un paciente inexistente lanza PacienteNoEncontradoException")
    void pacienteInexistente() {
        assertThatThrownBy(() -> servicio.obtener(-1)).isInstanceOf(PacienteNoEncontradoException.class);
    }
}
