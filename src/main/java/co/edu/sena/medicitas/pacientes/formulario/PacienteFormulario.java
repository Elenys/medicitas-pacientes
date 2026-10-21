package co.edu.sena.medicitas.pacientes.formulario;

import co.edu.sena.medicitas.pacientes.modelo.Genero;
import co.edu.sena.medicitas.pacientes.modelo.Paciente;
import co.edu.sena.medicitas.pacientes.modelo.TipoDocumento;
import co.edu.sena.medicitas.pacientes.modelo.Usuario;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import org.springframework.format.annotation.DateTimeFormat;

import java.time.LocalDate;

/**
 * Datos del formulario de registro y edición de pacientes, con sus reglas de validación
 * (Bean Validation). Las longitudes máximas coinciden con las columnas de la base de datos.
 *
 * <p>Reglas por tipo de dato:</p>
 * <ul>
 *   <li><b>Fechas:</b> nacimiento obligatorio, entre 1900 y hoy ({@link FechaNacimientoValida}).</li>
 *   <li><b>Números:</b> documento de 6 a 10 dígitos; teléfono celular de 10 dígitos que empieza por 3.</li>
 *   <li><b>Textos:</b> nombres y apellidos solo con letras (incluye tildes, ñ, apóstrofo y espacios).</li>
 *   <li><b>Caracteres especiales:</b> dirección y EPS no admiten {@code < > { } [ ] ; $ \}.</li>
 *   <li><b>Longitudes:</b> nombres y apellidos 60, correo 150, dirección 120, EPS 80.</li>
 * </ul>
 *
 * @author Carlas Elenys Montalvo Mendoza
 * @author Freyder López
 */
public class PacienteFormulario {

    /** Letras en español, espacios y apóstrofo (por ejemplo «D'Alessandro»). El vacío lo informa @NotBlank. */
    static final String SOLO_LETRAS = "^[A-Za-zÁÉÍÓÚÜÑáéíóúüñ' ]*$";
    /** Texto libre sin caracteres usados en inyección de código. */
    static final String SIN_CARACTERES_PELIGROSOS = "^[^<>{}\\[\\];$\\\\]*$";

    @NotNull(message = "Seleccione el tipo de documento")
    private TipoDocumento tipoDocumento;

    @NotBlank(message = "El número de documento es obligatorio")
    @Pattern(regexp = "^$|^\\d{6,10}$", message = "El documento debe tener solo números, entre 6 y 10 dígitos")
    private String numeroDocumento;

    @NotBlank(message = "Los nombres son obligatorios")
    @Size(max = 60, message = "Los nombres no pueden superar 60 caracteres")
    @Pattern(regexp = SOLO_LETRAS, message = "Los nombres solo pueden tener letras y espacios")
    private String nombres;

    @NotBlank(message = "Los apellidos son obligatorios")
    @Size(max = 60, message = "Los apellidos no pueden superar 60 caracteres")
    @Pattern(regexp = SOLO_LETRAS, message = "Los apellidos solo pueden tener letras y espacios")
    private String apellidos;

    @NotBlank(message = "El correo es obligatorio")
    @Size(max = 150, message = "El correo no puede superar 150 caracteres")
    @Email(regexp = "^[^@\\s]+@[^@\\s]+\\.[^@\\s]+$", message = "Escriba un correo válido, por ejemplo nombre@correo.com")
    private String correo;

    @Pattern(regexp = "^$|^3\\d{9}$", message = "El celular debe tener 10 dígitos y empezar por 3")
    private String telefono;

    @NotNull(message = "La fecha de nacimiento es obligatoria")
    @FechaNacimientoValida
    @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
    private LocalDate fechaNacimiento;

    @NotNull(message = "Seleccione el género")
    private Genero genero;

    @Size(max = 120, message = "La dirección no puede superar 120 caracteres")
    @Pattern(regexp = SIN_CARACTERES_PELIGROSOS, message = "La dirección no puede tener los caracteres < > { } [ ] ; $ \\")
    private String direccion;

    @Size(max = 80, message = "La EPS no puede superar 80 caracteres")
    @Pattern(regexp = SIN_CARACTERES_PELIGROSOS, message = "La EPS no puede tener los caracteres < > { } [ ] ; $ \\")
    private String eps;

    /** Crea el formulario con los datos de un paciente existente (para editarlo). */
    public static PacienteFormulario desde(Paciente paciente) {
        Usuario usuario = paciente.getUsuario();
        PacienteFormulario formulario = new PacienteFormulario();
        formulario.tipoDocumento = usuario.getTipoDocumento();
        formulario.numeroDocumento = usuario.getNumeroDocumento();
        formulario.nombres = usuario.getNombres();
        formulario.apellidos = usuario.getApellidos();
        formulario.correo = usuario.getCorreo();
        formulario.telefono = usuario.getTelefono();
        formulario.fechaNacimiento = paciente.getFechaNacimiento();
        formulario.genero = paciente.getGenero();
        formulario.direccion = paciente.getDireccion();
        formulario.eps = paciente.getEps();
        return formulario;
    }

    /** Copia los datos (ya validados y normalizados) al paciente y a su usuario. */
    public void aplicarA(Paciente paciente) {
        Usuario usuario = paciente.getUsuario();
        usuario.setTipoDocumento(tipoDocumento);
        usuario.setNumeroDocumento(numeroDocumento.trim());
        usuario.setNombres(normalizarEspacios(nombres));
        usuario.setApellidos(normalizarEspacios(apellidos));
        usuario.setCorreo(correo.trim().toLowerCase());
        usuario.setTelefono(vacioANulo(telefono));
        paciente.setFechaNacimiento(fechaNacimiento);
        paciente.setGenero(genero);
        paciente.setDireccion(vacioANulo(direccion));
        paciente.setEps(vacioANulo(eps));
    }

    private static String normalizarEspacios(String texto) {
        return texto.trim().replaceAll("\\s+", " ");
    }

    private static String vacioANulo(String texto) {
        return texto == null || texto.isBlank() ? null : texto.trim();
    }

    public TipoDocumento getTipoDocumento() { return tipoDocumento; }
    public void setTipoDocumento(TipoDocumento tipoDocumento) { this.tipoDocumento = tipoDocumento; }
    public String getNumeroDocumento() { return numeroDocumento; }
    public void setNumeroDocumento(String numeroDocumento) { this.numeroDocumento = numeroDocumento; }
    public String getNombres() { return nombres; }
    public void setNombres(String nombres) { this.nombres = nombres; }
    public String getApellidos() { return apellidos; }
    public void setApellidos(String apellidos) { this.apellidos = apellidos; }
    public String getCorreo() { return correo; }
    public void setCorreo(String correo) { this.correo = correo; }
    public String getTelefono() { return telefono; }
    public void setTelefono(String telefono) { this.telefono = telefono; }
    public LocalDate getFechaNacimiento() { return fechaNacimiento; }
    public void setFechaNacimiento(LocalDate fechaNacimiento) { this.fechaNacimiento = fechaNacimiento; }
    public Genero getGenero() { return genero; }
    public void setGenero(Genero genero) { this.genero = genero; }
    public String getDireccion() { return direccion; }
    public void setDireccion(String direccion) { this.direccion = direccion; }
    public String getEps() { return eps; }
    public void setEps(String eps) { this.eps = eps; }
}
