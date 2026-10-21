package co.edu.sena.medicitas.pacientes.modelo;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;

import java.time.LocalDate;
import java.time.Period;

/**
 * Datos de salud y contacto de un paciente (tabla {@code patients}), unidos 1 a 1 con su {@link Usuario}.
 *
 * @author Carlas Elenys Montalvo Mendoza
 * @author Freyder López
 */
@Entity
@Table(name = "patients")
public class Paciente {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    /** Al guardar el paciente se guarda también su usuario, en la misma transacción. */
    @OneToOne(cascade = CascadeType.ALL, fetch = FetchType.EAGER, optional = false)
    @JoinColumn(name = "user_id", nullable = false, unique = true)
    private Usuario usuario;

    @Column(name = "birth_date", nullable = false)
    private LocalDate fechaNacimiento;

    @Enumerated(EnumType.STRING)
    @JdbcTypeCode(SqlTypes.NAMED_ENUM)
    @Column(name = "gender", nullable = false, columnDefinition = "gender")
    private Genero genero;

    @Column(name = "address", length = 120)
    private String direccion;

    @Column(name = "health_provider", length = 80)
    private String eps;

    protected Paciente() {
        // Requerido por JPA
    }

    public Paciente(Usuario usuario) {
        this.usuario = usuario;
    }

    /** Edad cumplida a la fecha de hoy. */
    public int getEdad() {
        return Period.between(fechaNacimiento, LocalDate.now()).getYears();
    }

    public Integer getId() { return id; }
    public Usuario getUsuario() { return usuario; }
    public LocalDate getFechaNacimiento() { return fechaNacimiento; }
    public void setFechaNacimiento(LocalDate fechaNacimiento) { this.fechaNacimiento = fechaNacimiento; }
    public Genero getGenero() { return genero; }
    public void setGenero(Genero genero) { this.genero = genero; }
    public String getDireccion() { return direccion; }
    public void setDireccion(String direccion) { this.direccion = direccion; }
    public String getEps() { return eps; }
    public void setEps(String eps) { this.eps = eps; }
}
