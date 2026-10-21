package co.edu.sena.medicitas.pacientes.web;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.matchesPattern;
import static org.hamcrest.Matchers.not;
import static org.hamcrest.Matchers.containsString;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.header;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

/**
 * Pruebas de la API REST de pacientes: códigos HTTP, cuerpo JSON y mensajes de error.
 * Usa la base real dentro de una transacción que se revierte.
 *
 * @author Carlas Elenys Montalvo Mendoza
 * @author Freyder López
 */
@SpringBootTest
@AutoConfigureMockMvc
@Transactional
class PacienteApiControladorTest {

    private static final String VALIDO = """
            {"tipoDocumento":"CC","numeroDocumento":"9988776655","nombres":"Lucía Andrea","apellidos":"Pérez Gómez",
             "correo":"lucia.prueba@correo.com","telefono":"3001234567","fechaNacimiento":"1995-04-12",
             "genero":"FEMALE","direccion":"Calle 10 # 5-20","eps":"Nueva EPS"}""";

    @Autowired
    private MockMvc mvc;

    @Test
    @DisplayName("GET /api/pacientes devuelve la lista en JSON sin datos internos")
    void listar() throws Exception {
        mvc.perform(get("/api/pacientes"))
                .andExpect(status().isOk())
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$[0].nombreCompleto").exists())
                .andExpect(content().string(not(containsString("clave"))));
    }

    @Test
    @DisplayName("POST válido responde 201 con Location y la contraseña temporal")
    void registrar() throws Exception {
        mvc.perform(post("/api/pacientes").contentType(MediaType.APPLICATION_JSON).content(VALIDO))
                .andExpect(status().isCreated())
                .andExpect(header().string("Location", matchesPattern("/api/pacientes/\\d+")))
                .andExpect(jsonPath("$.paciente.correo").value("lucia.prueba@correo.com"))
                .andExpect(jsonPath("$.paciente.fechaNacimiento").value("1995-04-12"))
                .andExpect(jsonPath("$.claveTemporal", matchesPattern("[A-Za-z0-9]{10}")));
    }

    @Test
    @DisplayName("POST con datos inválidos responde 400 con un mensaje por campo")
    void registrarInvalido() throws Exception {
        String invalido = VALIDO.replace("9988776655", "12ab").replace("3001234567", "123");
        mvc.perform(post("/api/pacientes").contentType(MediaType.APPLICATION_JSON).content(invalido))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.errores.numeroDocumento").value("El documento debe tener solo números, entre 6 y 10 dígitos"))
                .andExpect(jsonPath("$.errores.telefono").value("El celular debe tener 10 dígitos y empezar por 3"));
    }

    @Test
    @DisplayName("POST con un género que no existe responde 400")
    void jsonIlegible() throws Exception {
        mvc.perform(post("/api/pacientes").contentType(MediaType.APPLICATION_JSON).content(VALIDO.replace("FEMALE", "X")))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.mensaje").value("Los datos enviados no tienen un formato válido."));
    }

    @Test
    @DisplayName("POST con documento repetido responde 409")
    void duplicado() throws Exception {
        mvc.perform(post("/api/pacientes").contentType(MediaType.APPLICATION_JSON).content(VALIDO));
        mvc.perform(post("/api/pacientes").contentType(MediaType.APPLICATION_JSON).content(VALIDO.replace("lucia.prueba", "otra")))
                .andExpect(status().isConflict())
                .andExpect(jsonPath("$.errores.numeroDocumento").exists());
    }

    @Test
    @DisplayName("GET de un paciente inexistente responde 404")
    void inexistente() throws Exception {
        mvc.perform(get("/api/pacientes/999999")).andExpect(status().isNotFound())
                .andExpect(jsonPath("$.estado").value(404));
    }

    @Test
    @DisplayName("PUT actualiza y PATCH cambia el estado")
    void actualizarYCambiarEstado() throws Exception {
        String ubicacion = mvc.perform(post("/api/pacientes").contentType(MediaType.APPLICATION_JSON).content(VALIDO))
                .andReturn().getResponse().getHeader("Location");
        mvc.perform(put(ubicacion).contentType(MediaType.APPLICATION_JSON).content(VALIDO.replace("Nueva EPS", "Sura EPS")))
                .andExpect(status().isOk()).andExpect(jsonPath("$.eps").value("Sura EPS"));
        mvc.perform(patch(ubicacion + "/estado")).andExpect(jsonPath("$.activo").value(false));
    }

    @Test
    @DisplayName("GET /api/catalogos devuelve las opciones con su etiqueta")
    void catalogos() throws Exception {
        mvc.perform(get("/api/catalogos"))
                .andExpect(jsonPath("$.tiposDocumento", hasSize(4)))
                .andExpect(jsonPath("$.generos[0].etiqueta").exists());
    }
}
