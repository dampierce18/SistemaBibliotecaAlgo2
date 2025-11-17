# 4. Diagramas de Proceso

Esta sección detalla los flujos de trabajo (procesos de negocio) más importantes implementados en el sistema, mostrando la lógica del controlador y las interacciones con la capa DAO.

## 1. Proceso: Realizar Préstamo

Este es el proceso central del sistema. Involucra la validación de inventario y la actualización de stock. Se basa en la lógica de `ControladorPrestamos.java`.

**Flujo de pasos:**
1.  El Empleado/Admin navega a la pestaña "Nuevo Préstamo".
2.  Ingresa el ID del Libro, ID del Usuario y Días de Préstamo.
3.  El usuario hace clic en el botón "Realizar Préstamo".
4.  El sistema (`ControladorPrestamos`) recibe la solicitud.
5.  **Validación 1:** ¿Los campos están vacíos o no son numéricos?
    * Si SÍ: Muestra error "Los IDs y días deben ser números válidos". Fin del proceso.
6.  **Validación 2:** ¿Existe el Usuario? (Consulta a `UsuarioDAO.obtenerUsuarioPorId()`).
    * Si NO: Muestra error "El usuario con ID X no existe". Fin del proceso.
7.  **Validación 3:** ¿Existe el Libro? (Consulta a `LibroDAO.obtenerLibroPorId()`).
    * Si NO: Muestra error "El libro con ID X no existe". Fin del proceso.
8.  **Validación 4:** ¿El libro tiene stock? (Comprueba `libro.getDisponibles() > 0`).
    * Si NO: Muestra error "No hay ejemplares disponibles". Fin del proceso.
9.  **Ejecución:**
    * Se crea un nuevo objeto `Prestamo`.
    * Se llama a `prestamoDAO.realizarPrestamo()` para guardarlo en la BD.
    * Se actualiza el libro: `libro.setDisponibles(libro.getDisponibles() - 1)`.
    * Se llama a `libroDAO.actualizarLibro()` para guardar el nuevo stock.
10. **Confirmación:** Muestra mensaje "Préstamo realizado exitosamente".
11. Se limpian los formularios y se actualizan las listas.

### Diagrama de Flujo (Realizar Préstamo)

```mermaid
graph TD
    A["Inicio: Panel Préstamos"] --> B{"Ingresa ID Libro, ID Usuario"};
    B --> C["Clic en 'Realizar Préstamo'"];
    C --> D{"¿Datos válidos (no vacíos, numéricos)?"};
    D -- No --> E["Error: IDs y días deben ser números válidos"];
    D -- Sí --> F["Buscar Usuario por ID (DAO)"];
    F --> G{"¿Usuario existe?"};
    G -- No --> H["Error: El usuario no existe"];
    G -- Sí --> I["Buscar Libro por ID (DAO)"];
    I --> J{"¿Libro existe?"};
    J -- No --> K["Error: El libro no existe"];
    J -- Sí --> L{"¿Libro.disponibles > 0?"};
    L -- No --> M["Error: No hay ejemplares disponibles"];
    L -- Sí --> N["1. Guardar Préstamo en BD (DAO)"];
    N --> O["2. Actualizar Libro: disponibles = disponibles - 1 (DAO)"];
    O --> P["Éxito: Préstamo realizado"];
    P --> Q["Fin"];
