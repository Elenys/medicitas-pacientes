package co.edu.sena.medicitas.pacientes.web;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

import static org.hamcrest.Matchers.containsString;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.flash;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.model;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.redirectedUrl;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.redirectedUrlPattern;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.view;

/**
 * Pruebas del controlador web: rutas, vistas, mensajes de validación y redirecciones.
 * Usa la base real dentro de una transacción que se revierte.
 *
 * @author Carlas Elenys Montalvo Mendoza
 * @author Freyder López
 */
@SpringBootTest
@AutoConfigureMockMvc
@Transactional
class PacienteControladorTest {

    @Autowired
    private MockMvc mvc;

    @Test
    @DisplayName("La raíz redirige al listado de pacientes")
    void raizRedirige() throws Exception {
        mvc.perform(get("/")).andExpect(redirectedUrl("/pacientes"));
    }

    @Test
    @DisplayName("El listado muestra la vista con los pacientes")
    void listado() throws Exception {
        mvc.perform(get("/pacientes"))
                .andExpect(status().isOk())
                .andExpect(view().name("pacientes/lista"))
                .andExpect(model().attributeExists("pacientes"))
                .andExpect(content().string(containsString("Registrar paciente")));
    }

    @Test
    @DisplayName("Un registro válido redirige al detalle con la contraseña temporal")
    void registroValido() throws Exception {
        mvc.perform(post("/pacientes").params(datosValidos()))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrlPattern("/pacientes/*"))
                .andExpect(flash().attributeExists("mensaje", "claveTemporal"));
    }

    @Test
    @DisplayName("Un registro con errores vuelve al formulario con el mensaje de cada campo")
    void registroInvalido() throws Exception {
        var datos = datosValidos();
        datos.set("numeroDocumento", "12ab");
        datos.set("correo", "sin-arroba");
        datos.set("telefono", "12345");
        mvc.perform(post("/pacientes").params(datos))
                .andExpect(status().isOk())
                .andExpect(view().name("pacientes/formulario"))
                .andExpect(model().attributeHasFieldErrors("formulario", "numeroDocumento", "correo", "telefono"))
                .andExpect(content().string(containsString("El documento debe tener solo números")))
                .andExpect(content().string(containsString("El celular debe tener 10 dígitos")));
    }

    @Test
    @DisplayName("Un documento repetido se informa en el campo del documento")
    void registroDuplicado() throws Exception {
        mvc.perform(post("/pacientes").params(datosValidos()));
        var otro = datosValidos();
        otro.set("correo", "otro.correo@correo.com");
        mvc.perform(post("/pacientes").params(otro))
                .andExpect(view().name("pacientes/formulario"))
                .andExpect(model().attributeHasFieldErrors("formulario", "numeroDocumento"))
                .andExpect(content().string(containsString("Ya existe un usuario con ese tipo y número de documento")));
    }

    @Test
    @DisplayName("Un paciente inexistente redirige al listado con un error")
    void pacienteInexistente() throws Exception {
        mvc.perform(get("/pacientes/999999"))
                .andExpect(redirectedUrl("/pacientes"))
                .andExpect(flash().attributeExists("error"));
    }

    private static org.springframework.util.LinkedMultiValueMap<String, String> datosValidos() {
        var datos = new org.springframework.util.LinkedMultiValueMap<String, String>();
        datos.add("tipoDocumento", "CC");
        datos.add("numeroDocumento", "9988776655");
        datos.add("nombres", "Lucía Andrea");
        datos.add("apellidos", "Pérez Gómez");
        datos.add("correo", "lucia.prueba@correo.com");
        datos.add("telefono", "3001234567");
        datos.add("fechaNacimiento", "1995-04-12");
        datos.add("genero", "FEMALE");
        datos.add("direccion", "Calle 10 # 5-20");
        datos.add("eps", "Nueva EPS");
        return datos;
    }
}
