# 5. Manual de Instalación (para Desarrollo)

Esta guía detalla los pasos para configurar y ejecutar el proyecto del Sistema de Gestión de Biblioteca en un entorno de desarrollo.

## 1. Requisitos Previos (Software)
**Para desarrollo:**
Asegúrese de tener instalado el siguiente software en su sistema:

* **Java JDK:** El proyecto está configurado para **Java 21** https://download.oracle.com/java/21/archive/jdk-21.0.8_windows-x64_bin.exe
* **Apache Maven:** Versión 3.6 o superior, para la gestión de dependencias y construcción del proyecto.
* **Git:** Para clonar el repositorio.
* **Un IDE (Recomendado):** El proyecto incluye archivos de configuración para **Eclipse**, pero cualquier IDE compatible con Maven (como IntelliJ IDEA o VS Code) funcionará.

**Para producción:**
**Java Runtime Environment (JRE):** Versión 21 o superior.
**Opcional:** El repositorio incluye un instalador de Java 21 para sistemas Windows.

## 2. Instalación
**Para desarrollo:**
1.  **Clonar el Repositorio:**
    Abra una terminal o Git Bash y clone el repositorio en su máquina local:
    ```sh
    git clone https://github.com/dampierce18/SistemaBibliotecaAlgo2
    cd sistema-biblioteca
    ```

2.  **Importar en el IDE (Eclipse):**
    * En Eclipse, vaya a `File -> Import...`.
    * Seleccione `Maven -> Existing Maven Projects`.
    * Haga clic en `Next`.
    * En `Root Directory`, haga clic en `Browse...` y navegue hasta la carpeta `sistema-biblioteca` que acaba de clonar.
    * Eclipse detectará el archivo `pom.xml`.
    * Haga clic en `Finish`.

3.  **Instalar Dependencias:**
    El IDE debería iniciar la descarga de dependencias de Maven automáticamente. La única dependencia externa es `org.xerial:sqlite-jdbc`.
    
    Si esto no ocurre, puede forzarlo:
    * Haga clic derecho en el proyecto en el "Package Explorer".
    * Seleccione `Maven -> Update Project...`.
    * Asegúrese de que su proyecto esté seleccionado y haga clic en `OK`.

**Para producción:**
El repositorio incluye una carpeta distribucion con todo lo necesario para ejecutar el sistema sin necesidad de IDE:
1. **Descargar el Proyecto:**
    * Clone el repositorio o descargue el ZIP desde GitHub.

2. **Instalar Java (si no está instalado):**
    * Navegue a la carpeta Biblioteca/
    * Ejecute el archivo java-21-installer.exe para instalar Java 21 en Windows
    * Nota: Si ya tiene Java 21 instalado, puede omitir este paso

3. **Ejecutar la Aplicación:**
    * En la carpeta distribucion/, haga doble clic en SistemaBiblioteca.jar
## 3. Configuración

**No se requiere configuración adicional.**

El sistema está diseñado para funcionar "de caja" (out-of-the-box):

* **Base de Datos:** El sistema utiliza un archivo de base de datos SQLite llamado `biblioteca.db`. Si el archivo no existe en la raíz del proyecto, el programa lo **creará automáticamente** junto con las tablas (`libros`, `usuarios`, `prestamos`) la primera vez que se ejecute.
   
* **Credenciales de Usuario:** Las credenciales de acceso están "hardcodeadas" (fijas en el código) en `ControladorLogin.java`:
    * **Administrador:** `admin` / `123`
    * **Empleado:** `empleado` / `123`

## 4. Ejecución
**Para desarrollo:**
1.  En el "Package Explorer" de su IDE, navegue hasta el archivo:
    `src/main/java/sistemabiblioteca/Main.java`.
2.  Haga clic derecho en el archivo `Main.java`.
3.  Seleccione `Run As -> Java Application`.
4.  La aplicación se compilará y se iniciará, mostrando la ventana de Login.

**Para producción:**
1.  Asegúrese de tener Java 21 instalado (use el instalador incluido si es necesario)
2.  Navegue a la carpeta biblioteca/
3.  Haga doble clic en SistemaBiblioteca.jar

