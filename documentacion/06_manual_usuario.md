# 6. Manual de Usuario

Bienvenido al Sistema de Gestión de Biblioteca. Esta guía le mostrará cómo utilizar las funciones principales del software.

**Nota:** Para esta guía, se recomienda **agregar capturas de pantalla** del software en funcionamiento en las secciones marcadas. Puede guardar las capturas en una carpeta `docs/images/` y enlazarlas en este documento.

## 1. Acceso al Sistema

Al iniciar la aplicación, se presentará la ventana de "Acceso al Sistema".

Debe ingresar sus credenciales. Existen dos niveles de acceso:

* **Rol Administrador:** (Acceso completo)
    * **Usuario:** `admin`
    * **Contraseña:** `123`
* **Rol Empleado:** (Acceso para operaciones diarias)
    * **Usuario:** `empleado`
    * **Contraseña:** `123`

Haga clic en "Entrar" para continuar o "Salir" para cerrar la aplicación.


## 2. Pantalla Principal (Dashboard)

Después de iniciar sesión, verá la pantalla principal.

* **Menú Lateral (Izquierda):** Contiene los botones para navegar a las diferentes secciones:
    * `Principal`: Vuelve al Dashboard.
    * `Préstamos`: Gestiona préstamos y devoluciones.
    * `Usuarios`: Administra a los miembros.
    * `Libros`: Administra el catálogo de libros.
    * `Reportes`: (Solo visible para Administradores).
    * `Salir`: Cierra la aplicación.
* **Panel de Contenido (Centro):** Muestra la sección seleccionada.
* **Dashboard:** La vista por defecto (`Principal`) muestra un resumen estadístico de la biblioteca:
    * Total Libros
    * Total Usuarios
    * Préstamos activos
    * Atrasados

## 3. Gestión de Libros

Haga clic en el botón "Libros" del menú. Esta sección tiene 3 pestañas.

* **Pestaña "Lista de Libros":**
    * Muestra una tabla con todos los libros registrados (ID, Título, Autor, Disponibles, etc.).
    * `Actualizar Lista`: Refresca los datos de la tabla.
    * `(Solo Admin)` `Editar`: (Funcionalidad en desarrollo).
    * `(Solo Admin)` `Eliminar`: Seleccione un libro de la tabla y haga clic para eliminarlo.

* **Pestaña "Agregar Libro":**
    * Rellene los campos del formulario (Título, Autor, Editorial, Año, Categoría, Ejemplares).
    * `Guardar Libro`: Añade el libro al sistema.
    * `Limpiar Campos`: Borra el formulario.

* **Pestaña "Buscar Libro":**
    * Seleccione un criterio de búsqueda (Título, Autor, ID, Categoría).
    * Escriba el valor a buscar y haga clic en "Buscar".
    * Los resultados aparecerán en la tabla inferior.

## 4. Gestión de Préstamos

Haga clic en el botón "Préstamos". Esta sección tiene 3 pestañas:

* **Pestaña "Nuevo Préstamo":**
    * **ID Libro:** Ingrese el ID del libro (puede buscarlo en la sección Libros).
    * **ID Usuario:** Ingrese el ID del usuario (puede buscarlo en la sección Usuarios).
    * **Días de Préstamo:** Ingrese la cantidad de días (ej. 15).
    * Haga clic en "Realizar Préstamo". El sistema validará que el libro tenga ejemplares disponibles.

* **Pestaña "Préstamos Activos":**
    * Muestra una tabla de todos los libros que están actualmente prestados.
    * **Para registrar una devolución:** Seleccione una fila de la tabla y haga clic en el botón "Registrar Devolución". El libro volverá a estar disponible en el inventario.
    * `Actualizar Lista`: Refresca la tabla.

* **Pestaña "Historial":**
    * Muestra un registro de *todos* los préstamos realizados, incluyendo los que ya han sido devueltos.

## 5. Gestión de Usuarios

Similar a la gestión de libros, esta sección (botón "Usuarios") permite:

1.  **Ver la lista** de usuarios registrados.
2.  **Agregar** un nuevo usuario (Nombre, Apellidos, Domicilio, Teléfono).
3.  **Buscar** un usuario por Nombre, Apellido Paterno, Teléfono o ID.
4.  **(Solo Admin):** `Editar` (en desarrollo) o `Eliminar` usuarios existentes.