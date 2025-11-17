# 2. Modelo de Datos (E-R)

El sistema utiliza una base de datos SQLite que se crea automáticamente (archivo `biblioteca.db`) en la raíz del proyecto si no existe. El modelo se define en la clase `ConexionSQLite.java` y consta de tres tablas principales.

## Descripción de las Tablas

### 1. Tabla `libros`
Almacena la información del catálogo de libros y el inventario.
* `id` (INTEGER, PK, AUTOINCREMENT): Identificador único del libro.
* `titulo` (TEXT, NOT NULL): Título del libro.
* `anio` (TEXT): Año de publicación.
* `autor` (TEXT, NOT NULL): Autor del libro.
* `categoria` (TEXT): Género o categoría.
* `editorial` (TEXT): Editorial del libro.
* `total` (INTEGER, DEFAULT 1): Número total de copias de este libro que posee la biblioteca.
* `disponibles` (INTEGER, DEFAULT 1): Número de copias actualmente en stock y listas para prestar.

### 2. Tabla `usuarios`
Almacena la información de los miembros registrados en la biblioteca.
* `id` (INTEGER, PK, AUTOINCREMENT): Identificador único del usuario.
* `nombre` (TEXT, NOT NULL): Nombre del usuario.
* `apellido_paterno` (TEXT, NOT NULL): Apellido paterno.
* `apellido_materno` (TEXT): Apellido materno.
* `domicilio` (TEXT): Dirección del usuario.
* `telefono` (TEXT): Número de contacto.
* `sanciones` (INTEGER, DEFAULT 0): Contador de sanciones (ej. por devoluciones tardías).
* `monto_sancion` (INTEGER, DEFAULT 0): Monto acumulado de multas.

### 3. Tabla `prestamos`
Tabla relacional que registra las transacciones de préstamos. Actúa como vínculo entre `usuarios` y `libros`.
* `id` (INTEGER, PK, AUTOINCREMENT): Identificador único del préstamo.
* `libro_id` (INTEGER, NOT NULL, FK): Referencia a `libros.id`.
* `usuario_id` (INTEGER, NOT NULL, FK): Referencia a `usuarios.id`.
* `fecha_prestamo` (DATE, NOT NULL): Fecha en que se realizó el préstamo.
* `fecha_devolucion` (DATE, NOT NULL): Fecha límite para devolver el libro.
* `fecha_devolucion_real` (DATE): Fecha real en que el usuario devolvió el libro.
* `estado` (TEXT, NOT NULL, DEFAULT 'ACTIVO'): Estado actual del préstamo (ej. "ACTIVO", "DEVUELTO").

## Diagrama Entidad-Relación (ERD)

```mermaid
erDiagram
    USUARIOS {
        INT id PK "ID (PK)"
        STRING nombre
        STRING apellido_paterno
        STRING telefono
        INT sanciones
    }

    LIBROS {
        INT id PK "ID (PK)"
        STRING titulo
        STRING autor
        STRING categoria
        INT total
        INT disponibles
    }

    PRESTAMOS {
        INT id PK "ID (PK)"
        INT libro_id FK "ID Libro (FK)"
        INT usuario_id FK "ID Usuario (FK)"
        DATE fecha_prestamo
        DATE fecha_devolucion
        STRING estado
    }

    USUARIOS ||--o{ PRESTAMOS : "realiza"
    LIBROS   ||--o{ PRESTAMOS : "contiene"