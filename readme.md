# Sistema de Gestión de Biblioteca

Este es un proyecto de software de escritorio para la administración de una biblioteca, desarrollado en **Java** con la biblioteca gráfica **Swing**.

El sistema permite la gestión de libros, usuarios y el registro de préstamos y devoluciones, utilizando una base de datos local **SQLite** para almacenar la información.

## Características Principales

* **Gestión de Libros:** Registrar, buscar y eliminar libros del inventario.
* **Gestión de Usuarios:** Registrar, buscar y eliminar usuarios (miembros de la biblioteca).
* **Control de Préstamos:**
    * Realizar nuevos préstamos, validando la disponibilidad de ejemplares.
    * Registrar devoluciones, actualizando el stock de libros disponibles.
* **Control de Roles:** El sistema maneja dos tipos de usuario con diferentes permisos:
    * **Administrador:** Acceso total (incluyendo eliminación de registros y reportes).
    * **Empleado:** Acceso limitado a operaciones diarias (préstamos, devoluciones, registros).
* **Dashboard:** Un panel principal que muestra estadísticas clave como el total de libros, total de usuarios y préstamos activos.

## Tecnologías Utilizadas

* **Java** (Configurado para JDK 21)
* **Java Swing** (Para la interfaz gráfica de usuario)
* **SQLite** (Para la base de datos local)
* **Maven** (Para la gestión de dependencias)

## Cómo Empezar

### Requisitos

* Java JDK (1.8 o superior)
* Apache Maven

### Ejecución Local

1.  Clona este repositorio:
    ```sh
    git clone https://github.com/dampierce18/SistemaBibliotecaAlgo2
    ```
2.  Importa el proyecto en tu IDE (Eclipse, IntelliJ IDEA, etc.) como un "Proyecto Maven existente".
3.  El IDE descargará automáticamente la dependencia `sqlite-jdbc`.
4.  Ejecuta el método `main` en la clase `src/main/java/sistemabiblioteca/Main.java`.

### Credenciales de Acceso

El sistema utiliza credenciales fijas para iniciar sesión:

| Rol | Usuario | Contraseña |
| :--- | :--- | :--- |
| Administrador | `admin` | `123` |
| Empleado | `empleado` | `123` |

## Arquitectura

El proyecto sigue un patrón de diseño **Modelo-Vista-Controlador (MVC)**, complementado con una capa **DAO (Data Access Object)** para separar la lógica de negocio del acceso a la base de datos.

* **`modelo`**: Clases de entidad (POJOs) como `Libro`, `Usuario`.
* **`vista`**: Clases de UI (`JFrame`, `JPanel`) como `VistaPrincipal`, `PanelLibros`.
* **`controlador`**: Lógica de negocio y manejo de eventos (`ControladorLogin`, `ControladorLibros`).

* **`dao`**: Clases para consultas SQL (`LibroDAO`, `UsuarioDAO`).
