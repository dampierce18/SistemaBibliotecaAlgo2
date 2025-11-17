# 1. Requisitos del Sistema

Este documento define los requisitos funcionales y no funcionales para el Sistema de Gestión de Biblioteca.

## Requisitos Funcionales (RF)

Los requisitos funcionales describen las acciones que el sistema debe ser capaz de realizar.

* **RF-01: Autenticación de Usuarios**
    * El sistema debe poseer una pantalla de login.
    * El sistema debe validar las credenciales de usuario (usuario y contraseña).

* **RF-02: Gestión de Roles**
    * El sistema debe diferenciar entre dos roles: "Administrador" y "Empleado".
    * Las funciones de "Reportes", "Editar" y "Eliminar" (en Libros y Usuarios) deben estar ocultas para el rol "Empleado".

* **RF-03: Gestión de Libros (CRUD)**
    * El sistema debe permitir registrar (Crear) un nuevo libro.
    * El sistema debe permitir consultar (Leer) la lista de todos los libros.
    * El sistema debe permitir buscar libros por Título, Autor, ID o Categoría.
    * El sistema debe permitir Eliminar un libro (solo Administrador).
    * El sistema debe tener una función para Editar un libro (solo Administrador).
  
* **RF-04: Gestión de Usuarios**
    * El sistema debe permitir registrar (Crear) un nuevo usuario.
    * El sistema debe permitir consultar (Leer) la lista de todos los usuarios.
    * El sistema debe permitir buscar usuarios por Nombre, Apellido, Teléfono o ID.
    * El sistema debe permitir Eliminar un usuario (solo Administrador).

* **RF-05: Gestión de Préstamos**
    * El sistema debe permitir registrar un nuevo préstamo asociando un `libro_id` y un `usuario_id`.
    * El sistema debe validar que el libro tenga ejemplares `disponibles > 0` antes de realizar un préstamo.
    * Al realizar un préstamo, el sistema debe restar 1 a la cantidad de `disponibles` del libro.
    * El sistema debe permitir registrar una devolución.
    * Al registrar una devolución, el sistema debe sumar 1 a la cantidad de `disponibles` del libro.
    * El sistema debe mostrar un historial de todos los préstamos.

* **RF-06: Dashboard (Panel Principal)**
    * El sistema debe mostrar en la pantalla principal un resumen de: Total de Libros, Total de Usuarios, Préstamos Activos y Préstamos Atrasados.

## Requisitos No Funcionales (RNF)

* **RNF-01: Persistencia:** El sistema debe utilizar una base de datos local **SQLite** (archivo `biblioteca.db`).
* **RNF-02: Tecnología:** El sistema debe estar desarrollado en **Java**.
* **RNF-03: Interfaz de Usuario:** La interfaz debe ser una aplicación de escritorio nativa utilizando la biblioteca **Swing**.
* **RNF-04: Portabilidad:** El sistema debe poder ejecutarse en cualquier Sistema Operativo (Windows, macOS, Linux) que tenga una JVM compatible.
* **RNF-05: Dependencias:** El proyecto debe gestionar sus dependencias usando **Maven**. La dependencia principal de ejecución es `sqlite-jdbc`.
* **RNF-06: Configuración:** El sistema debe ser autoconfigurable, creando la base de datos y las tablas automáticamente si no existen.

## Diagrama de Casos de Uso

```mermaid
graph TD
    subgraph "Actores"
        Admin(Administrador)
        Emp(Empleado)
    end
    
    Admin -- "hereda" --> Emp

    subgraph "Casos de Uso (Comunes)"
        UC1(Realizar Préstamo)
        UC2(Registrar Devolución)
        UC3(Agregar Libro)
        UC4(Agregar Usuario)
        UC5(Buscar Libro/Usuario)
        UC6(Iniciar Sesión)
    end

    subgraph "Casos de Uso (Admin)"
        UC7(Eliminar Libro)
        UC8(Eliminar Usuario)
        UC9(Acceder a Reportes)
    end
    
    Emp --> UC1
    Emp --> UC2
    Emp --> UC3
    Emp --> UC4
    Emp --> UC5
    Emp --> UC6
    
    Admin --> UC7
    Admin --> UC8
    Admin --> UC9
