# MediCitas · Módulo de gestión de pacientes (Spring Boot)

Evidencia **GA7-220501096-AA3-EV01** — Programa Análisis y Desarrollo de Software, SENA (ficha 3186702).

Módulo web del proyecto formativo **MediCitas** (sistema de gestión de citas médicas) construido con
**Spring Boot**. Permite al personal administrativo **registrar, consultar, buscar, editar y activar/desactivar**
pacientes sobre la base de datos PostgreSQL real del proyecto.

**Autores:** Carlas Elenys Montalvo Mendoza · Freyder López

## Tecnologías

| Capa | Tecnología |
|---|---|
| Lenguaje | Java 21 |
| Framework | Spring Boot 4.1 (Spring MVC, Spring Data JPA / Hibernate, Bean Validation) |
| Vistas | Thymeleaf + HTML5 + CSS3 |
| Base de datos | PostgreSQL 16 (tablas `users` y `patients` de MediCitas) |
| Seguridad | BCrypt (spring-security-crypto) para la contraseña temporal |
| Pruebas | JUnit 5, AssertJ, MockMvc |
| Construcción | Maven (wrapper `mvnw`) |

## Arquitectura (capas)

```
src/main/java/co/edu/sena/medicitas/pacientes
├── modelo/        Entidades JPA (Usuario, Paciente) y enumeraciones (TipoDocumento, Genero, Rol)
├── repositorio/   PacienteRepositorio (Spring Data JPA, consultas JPQL)
├── formulario/    PacienteFormulario (DTO con las reglas de validación) y @FechaNacimientoValida
├── servicio/      PacienteServicio (reglas de negocio y transacciones) y excepciones
└── web/           PacienteControlador e InicioControlador (Spring MVC)
src/main/resources
├── templates/     Vistas Thymeleaf: pacientes/lista, formulario, detalle + fragmentos
└── static/css/    estilos.css
```

Flujo: **Vista (Thymeleaf) → Controlador → Servicio → Repositorio → PostgreSQL**. Las entidades nunca se
exponen al formulario: el controlador recibe un `PacienteFormulario` validado y el servicio lo aplica a la entidad.

## Funcionalidades

| Ruta | Método | Acción |
|---|---|---|
| `/pacientes` | GET | Listado; `?buscar=` filtra por nombre, apellido, documento o correo |
| `/pacientes/nuevo` | GET | Formulario de registro |
| `/pacientes` | POST | Registra el paciente y su usuario (rol PATIENT) con contraseña temporal cifrada |
| `/pacientes/{id}` | GET | Detalle del paciente |
| `/pacientes/{id}/editar` | GET | Formulario de edición |
| `/pacientes/{id}` | POST | Guarda los cambios |
| `/pacientes/{id}/estado` | POST | Activa o desactiva la cuenta |

## Validaciones

| Campo | Regla |
|---|---|
| Tipo de documento | Obligatorio (CC, TI, CE, PA) |
| Número de documento | Obligatorio, solo números, 6 a 10 dígitos; único junto con el tipo |
| Nombres / Apellidos | Obligatorios, máx. 60, solo letras (con tildes y ñ), espacios y apóstrofo |
| Correo | Obligatorio, formato válido, máx. 150, único (se guarda en minúsculas) |
| Celular | Opcional; 10 dígitos que empiezan por 3 |
| Fecha de nacimiento | Obligatoria, entre 01/01/1900 y hoy |
| Género | Obligatorio |
| Dirección / EPS | Opcionales, máx. 120 / 80, sin los caracteres `< > { } [ ] ; $ \` |

Las reglas se aplican en el servidor (Bean Validation); además la base de datos tiene sus propias
restricciones (`UNIQUE`, `CHECK`, llaves foráneas). Las consultas usan parámetros (JPA), nunca SQL concatenado.

## Estándares de codificación

- Nombres en español y descriptivos: clases en `PascalCase`, métodos y variables en `camelCase`, constantes en `MAYUSCULAS`.
- Paquete base `co.edu.sena.medicitas.pacientes`, un paquete por capa.
- Javadoc en clases y métodos públicos con `@author`.
- Inyección de dependencias por constructor; transacciones declaradas en el servicio (`@Transactional`).
- Patrón Post/Redirect/Get en todos los formularios.
- La contraseña nunca se guarda en texto plano: se cifra con BCrypt y la temporal se muestra una sola vez.
- La configuración de la base se puede sobrescribir con variables de entorno (no se escriben claves reales en el código).

## Cómo ejecutar

Requisitos: JDK 21 o superior y la base de datos de MediCitas en marcha (`docker compose up -d db` en el proyecto MediCitas).

```bash
./mvnw spring-boot:run
```

Abrir <http://localhost:8081>. Para otra base de datos:

```bash
MEDICITAS_DB_URL=jdbc:postgresql://servidor:5432/medicitas MEDICITAS_DB_USUARIO=usuario MEDICITAS_DB_CLAVE=clave ./mvnw spring-boot:run
```

## Pruebas

```bash
./mvnw test
```

44 pruebas automáticas:

- `PacienteFormularioTest` (29): reglas de validación de cada campo, sin base de datos.
- `PacienteServicioTest` (8): registro, cifrado BCrypt, normalización, duplicados, búsqueda, edición y estado contra PostgreSQL.
- `PacienteControladorTest` (6): rutas, vistas, mensajes de error y redirecciones con MockMvc.
- `MedicitasPacientesApplicationTests` (1): la aplicación arranca.

Las pruebas que usan la base corren dentro de una transacción que se revierte: la base queda igual.
