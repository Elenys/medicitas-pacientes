package co.edu.sena.medicitas.pacientes.repositorio;

import co.edu.sena.medicitas.pacientes.modelo.Paciente;
import co.edu.sena.medicitas.pacientes.modelo.TipoDocumento;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

/**
 * Acceso a datos de pacientes con Spring Data JPA: las consultas básicas (guardar, buscar por id,
 * listar) las genera el framework; aquí solo se declaran las propias del módulo.
 *
 * @author Carlas Elenys Montalvo Mendoza
 * @author Freyder López
 */
public interface PacienteRepositorio extends JpaRepository<Paciente, Integer> {

    /** Busca por nombres, apellidos, documento o correo, sin distinguir mayúsculas. */
    @Query("""
            SELECT p FROM Paciente p
            WHERE LOWER(p.usuario.nombres) LIKE LOWER(CONCAT('%', :texto, '%'))
               OR LOWER(p.usuario.apellidos) LIKE LOWER(CONCAT('%', :texto, '%'))
               OR p.usuario.numeroDocumento LIKE CONCAT('%', :texto, '%')
               OR LOWER(p.usuario.correo) LIKE LOWER(CONCAT('%', :texto, '%'))
            ORDER BY p.usuario.apellidos, p.usuario.nombres
            """)
    List<Paciente> buscar(@Param("texto") String texto);

    /** Todos los pacientes ordenados por apellidos y nombres. */
    @Query("SELECT p FROM Paciente p ORDER BY p.usuario.apellidos, p.usuario.nombres")
    List<Paciente> listarOrdenados();

    /** ¿Otro usuario (distinto de {@code idUsuario}) ya usa este correo? */
    @Query("""
            SELECT COUNT(u) > 0 FROM Usuario u
            WHERE LOWER(u.correo) = LOWER(:correo) AND (:idUsuario IS NULL OR u.id <> :idUsuario)
            """)
    boolean existeCorreo(@Param("correo") String correo, @Param("idUsuario") Integer idUsuario);

    /** ¿Otro usuario ya tiene este tipo y número de documento? */
    @Query("""
            SELECT COUNT(u) > 0 FROM Usuario u
            WHERE u.tipoDocumento = :tipo AND u.numeroDocumento = :numero AND (:idUsuario IS NULL OR u.id <> :idUsuario)
            """)
    boolean existeDocumento(@Param("tipo") TipoDocumento tipo, @Param("numero") String numero,
                            @Param("idUsuario") Integer idUsuario);
}
