package co.edu.sena.medicitas.pacientes.modelo;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;

import java.time.OffsetDateTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;

/**
 * Cuenta de acceso al sistema (tabla {@code users}). Un paciente tiene exactamente un usuario.
 *
 * <p>Los tipos enumerados se guardan como los tipos {@code ENUM} nativos de PostgreSQL
 * ({@link SqlTypes#NAMED_ENUM}). Las marcas de tiempo las asigna la base de datos.</p>
 *
 * @author Carlas Elenys Montalvo Mendoza
 * @author Freyder López
 */
@Entity
@Table(name = "users")
public class Usuario {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Enumerated(EnumType.STRING)
    @JdbcTypeCode(SqlTypes.NAMED_ENUM)
    @Column(name = "role", nullable = false, columnDefinition = "user_role")
    private Rol rol;

    @Enumerated(EnumType.STRING)
    @JdbcTypeCode(SqlTypes.NAMED_ENUM)
    @Column(name = "document_type", nullable = false, columnDefinition = "document_type")
    private TipoDocumento tipoDocumento;

    @Column(name = "document_number", nullable = false, length = 20)
    private String numeroDocumento;

    @Column(name = "first_name", nullable = false, length = 60)
    private String nombres;

    @Column(name = "last_name", nullable = false, length = 60)
    private String apellidos;

    @Column(name = "email", nullable = false, length = 150)
    private String correo;

    @Column(name = "phone", length = 15)
    private String telefono;

    @Column(name = "password_hash", nullable = false)
    private String claveCifrada;

    @Column(name = "is_active", nullable = false)
    private boolean activo = true;

    @Column(name = "created_at", insertable = false, updatable = false)
    private OffsetDateTime creadoEn;

    @Column(name = "updated_at", insertable = false, updatable = false)
    private OffsetDateTime actualizadoEn;

    protected Usuario() {
        // Requerido por JPA
    }

    /** Crea la cuenta de un paciente nuevo, activa, con la contraseña ya cifrada. */
    public static Usuario nuevoPaciente(String claveCifrada) {
        Usuario usuario = new Usuario();
        usuario.rol = Rol.PATIENT;
        usuario.claveCifrada = claveCifrada;
        return usuario;
    }

    /** Nombre completo para mostrar. */
    public String getNombreCompleto() {
        return nombres + " " + apellidos;
    }

    public Integer getId() { return id; }
    public Rol getRol() { return rol; }
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
    /** Hash BCrypt de la contraseña (nunca la contraseña en texto plano). */
    public String getClaveCifrada() { return claveCifrada; }
    public boolean isActivo() { return activo; }
    public void setActivo(boolean activo) { this.activo = activo; }
    public OffsetDateTime getCreadoEn() { return creadoEn; }
    /** Fecha de registro convertida a la zona horaria del servidor, para mostrarla. */
    public ZonedDateTime getCreadoEnLocal() { return creadoEn == null ? null : creadoEn.atZoneSameInstant(ZoneId.systemDefault()); }
    public OffsetDateTime getActualizadoEn() { return actualizadoEn; }
}
