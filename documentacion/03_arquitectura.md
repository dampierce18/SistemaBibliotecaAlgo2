# 3. Arquitectura del Software

El proyecto está desarrollado siguiendo un patrón de diseño **Modelo-Vista-Controlador (MVC)**, complementado con una capa **DAO (Data Access Object)** para garantizar una clara separación de responsabilidades.

Esta arquitectura desacopla la lógica de negocio, la interfaz de usuario y el acceso a datos, facilitando el mantenimiento y la escalabilidad del sistema.

## Descripción de las Capas

### 1. Vista (Paquete: `sistemabiblioteca.vista`)
* **Responsabilidad:** Esta capa es la única que el usuario ve. Se encarga de renderizar la interfaz gráfica y capturar las interacciones del usuario (clics, texto ingresado).
* **Tecnología:** Java Swing.
* **Herramienta:** WindowBuilder.
* **Componentes clave:**
    * `VistaPrincipal.java`: El `JFrame` principal que contiene el menú y los paneles.
    * `LoginFrame.java`: La ventana de inicio de sesión.
    * `PanelLibros.java`, `PanelUsuarios.java`, `PanelPrestamos.java`, `PanelReporte.java`: `JPanel` que definen cada módulo.
* **Importante:** Esta capa *no* contiene lógica de negocio (ej. no valida si un libro está disponible). Solo delega la acción al Controlador.

### 2. Controlador (Paquete: `sistemabiblioteca.controlador`)
* **Responsabilidad:** Es el "cerebro" de la aplicación. Escucha los eventos de la Vista (clics de botón) y decide qué hacer. Orquesta la lógica de negocio, valida los datos de entrada y pide o envía información al DAO.
* **Componentes clave:**
    * `ControladorLogin.java`: Valida las credenciales de usuario.
    * `ControladorVistaPrincipal.java`: Gestiona la navegación y los permisos de rol.
    * `ControladorLibros.java`, `ControladorPrestamos.java`, `ControladorReportes.java`: Contienen la lógica para cada módulo (ej. la validación de stock disponible está en `ControladorPrestamos`).

### 3. Modelo (Paquete: `sistemabiblioteca.modelo`)
* **Responsabilidad:** Representa las entidades de datos del sistema. Son clases "POJO" (Plain Old Java Objects) que solo contienen atributos (campos) y sus métodos `get` y `set`.
* **Componentes clave:**
    * `Libro.java`
    * `Usuario.java`
    * `Prestamo.java`
* **Importante:** Estas clases no se conectan a la BD ni ejecutan lógica compleja. Solo transportan datos.

### 4. DAO (Data Access Object) (Paquete: `sistemabiblioteca.dao`)
* **Responsabilidad:** Es la única capa que interactúa directamente con la base de datos. Abstrae toda la complejidad de SQL (INSERT, SELECT, UPDATE, DELETE) del resto de la aplicación.
* **Componentes clave:**
    * `LibroDAO.java`, `UsuarioDAO.java`, `PrestamoDAO.java`, `ReporteDAO.java`.
    * `ConexionSQLite.java` (en `sistemabiblioteca.bd`): Gestiona la conexión con el archivo `biblioteca.db`.

## Diagrama de Flujo de Arquitectura

Este diagrama muestra cómo fluye la información entre las capas. El flujo es unidireccional y ordenado:

```mermaid
graph TD
    subgraph " "
        direction LR
        
        subgraph "Capa de Vista (Swing)"
            V(VistaPrincipal, PanelLibros, etc.)
        end
    
        subgraph "Capa de Controlador"
            C(ControladorLibros, ControladorPrestamos, etc.)
        end
    
        subgraph "Capa de Modelo"
            M(Clase Libro, Clase Usuario, etc.)
        end
        
        subgraph "Capa de Acceso a Datos (DAO)"
            DAO(LibroDAO, UsuarioDAO, etc.)
        end
    
        subgraph "Base de Datos"
            DB[(biblioteca.db - SQLite)]
        end
    
        V -- "1. Notifica evento (clic)" --> C
        C -- "2. Valida y decide" --> DAO
        C -- "7. Envía datos a la Vista" --> V
        DAO -- "3. Solicita datos a BD" --> DB
        DB -- "4. Retorna datos crudos" --> DAO
        DAO -- "5. Mapea datos a Objetos (Modelo)" --> M
        DAO -- "6. Retorna Objetos" --> C
    end
