package co.edu.sena.medicitas.pacientes.modelo;

/**
 * Perfiles de usuario de MediCitas (tipo {@code user_role} de PostgreSQL).
 * Este módulo solo crea usuarios con el rol {@link #PATIENT}.
 *
 * @author Carlas Elenys Montalvo Mendoza
 * @author Freyder López
 */
public enum Rol {
    PATIENT,
    DOCTOR,
    ADMIN
}
