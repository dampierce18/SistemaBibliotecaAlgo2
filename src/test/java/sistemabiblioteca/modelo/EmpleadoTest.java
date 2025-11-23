package sistemabiblioteca.modelo;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import static org.junit.jupiter.api.Assertions.*;

class EmpleadoTest {

    private Empleado empleado;

    @BeforeEach
    void setUp() {
        empleado = new Empleado("Juan", "Pérez", "Gómez", 
                               "juan.perez", "password123", "ADMIN", 
                               "555-1234", "juan@biblioteca.com");
    }

    @Test
    @DisplayName("Debería crear un empleado con constructor parametrizado")
    void testConstructorParametrizado() {
        // Verificar todos los atributos
        assertEquals("Juan", empleado.getNombre());
        assertEquals("Pérez", empleado.getApellidoPaterno());
        assertEquals("Gómez", empleado.getApellidoMaterno());
        assertEquals("juan.perez", empleado.getUsuario());
        assertEquals("password123", empleado.getPassword());
        assertEquals("ADMIN", empleado.getRol());
        assertEquals("555-1234", empleado.getTelefono());
        assertEquals("juan@biblioteca.com", empleado.getEmail());
    }

    @Test
    @DisplayName("Debería crear un empleado con constructor vacío")
    void testConstructorVacio() {
        Empleado empleadoVacio = new Empleado();
        
        // Verificar valores por defecto
        assertEquals(0, empleadoVacio.getId());
        assertNull(empleadoVacio.getNombre());
        assertNull(empleadoVacio.getApellidoPaterno());
        assertNull(empleadoVacio.getApellidoMaterno());
        assertNull(empleadoVacio.getUsuario());
        assertNull(empleadoVacio.getPassword());
        assertNull(empleadoVacio.getRol());
        assertNull(empleadoVacio.getTelefono());
        assertNull(empleadoVacio.getEmail());
    }

    @Test
    @DisplayName("Debería actualizar correctamente todos los atributos con setters")
    void testSettersYGetters() {
        // Configurar todos los atributos
        empleado.setId(1);
        empleado.setNombre("María");
        empleado.setApellidoPaterno("López");
        empleado.setApellidoMaterno("Hernández");
        empleado.setUsuario("maria.lopez");
        empleado.setPassword("nuevaPassword");
        empleado.setRol("EMPLEADO");
        empleado.setTelefono("555-5678");
        empleado.setEmail("maria@biblioteca.com");

        // Verificar todos los atributos
        assertEquals(1, empleado.getId());
        assertEquals("María", empleado.getNombre());
        assertEquals("López", empleado.getApellidoPaterno());
        assertEquals("Hernández", empleado.getApellidoMaterno());
        assertEquals("maria.lopez", empleado.getUsuario());
        assertEquals("nuevaPassword", empleado.getPassword());
        assertEquals("EMPLEADO", empleado.getRol());
        assertEquals("555-5678", empleado.getTelefono());
        assertEquals("maria@biblioteca.com", empleado.getEmail());
    }

    @Test
    @DisplayName("Debería manejar correctamente el nombre completo")
    void testGetNombreCompleto() {
        // Caso normal con los tres nombres
        assertEquals("Juan Pérez Gómez", empleado.getNombreCompleto());

        // Caso con apellido materno null
        empleado.setApellidoMaterno(null);
        assertEquals("Juan Pérez null", empleado.getNombreCompleto());

        // Caso con apellido materno vacío
        empleado.setApellidoMaterno("");
        assertEquals("Juan Pérez ", empleado.getNombreCompleto());

        // Caso con solo nombre y apellido paterno
        empleado.setNombre("Ana");
        empleado.setApellidoPaterno("López");
        empleado.setApellidoMaterno(null);
        assertEquals("Ana López null", empleado.getNombreCompleto());
    }

    @Test
    @DisplayName("Debería manejar valores nulos y vacíos en campos de texto")
    void testManejoValoresNulosYVacios() {
        Empleado empleadoNuevo = new Empleado();
        
        // Probar setters con valores nulos y vacíos
        empleadoNuevo.setNombre(null);
        empleadoNuevo.setApellidoPaterno("");
        empleadoNuevo.setApellidoMaterno("   ");
        empleadoNuevo.setUsuario(null);
        empleadoNuevo.setPassword("");
        empleadoNuevo.setRol("   ");
        empleadoNuevo.setTelefono(null);
        empleadoNuevo.setEmail("");
        
        // Verificar que los valores se asignan correctamente
        assertNull(empleadoNuevo.getNombre());
        assertEquals("", empleadoNuevo.getApellidoPaterno());
        assertEquals("   ", empleadoNuevo.getApellidoMaterno());
        assertNull(empleadoNuevo.getUsuario());
        assertEquals("", empleadoNuevo.getPassword());
        assertEquals("   ", empleadoNuevo.getRol());
        assertNull(empleadoNuevo.getTelefono());
        assertEquals("", empleadoNuevo.getEmail());
    }

    @Test
    @DisplayName("Debería manejar diferentes roles válidos")
    void testManejoRoles() {
        // Probar rol ADMIN
        empleado.setRol("ADMIN");
        assertEquals("ADMIN", empleado.getRol());

        // Probar rol EMPLEADO
        empleado.setRol("EMPLEADO");
        assertEquals("EMPLEADO", empleado.getRol());

        // Probar rol con espacios
        empleado.setRol("  ADMIN  ");
        assertEquals("  ADMIN  ", empleado.getRol());

        // Probar rol null
        empleado.setRol(null);
        assertNull(empleado.getRol());
    }

    @Test
    @DisplayName("Debería mantener la independencia entre campos")
    void testIndependenciaEntreCampos() {
        // Cambiar nombre sin afectar otros campos
        String usuarioOriginal = empleado.getUsuario();
        String rolOriginal = empleado.getRol();
        
        empleado.setNombre("Nuevo Nombre");
        
        assertEquals("Nuevo Nombre", empleado.getNombre());
        assertEquals(usuarioOriginal, empleado.getUsuario());
        assertEquals(rolOriginal, empleado.getRol());

        // Cambiar rol sin afectar otros campos
        String nombreOriginal = empleado.getNombre();
        String emailOriginal = empleado.getEmail();
        
        empleado.setRol("EMPLEADO");
        
        assertEquals("EMPLEADO", empleado.getRol());
        assertEquals(nombreOriginal, empleado.getNombre());
        assertEquals(emailOriginal, empleado.getEmail());
    }

    @Test
    @DisplayName("Debería manejar valores extremos en campos de texto")
    void testValoresExtremos() {
        // Nombres muy largos
        String nombreLargo = "A".repeat(100);
        String apellidoLargo = "B".repeat(100);
        
        empleado.setNombre(nombreLargo);
        empleado.setApellidoPaterno(apellidoLargo);
        empleado.setUsuario("usuario".repeat(10));
        empleado.setPassword("password".repeat(10));
        
        assertEquals(nombreLargo, empleado.getNombre());
        assertEquals(apellidoLargo, empleado.getApellidoPaterno());
        assertEquals("usuario".repeat(10), empleado.getUsuario());
        assertEquals("password".repeat(10), empleado.getPassword());
    }

    @Test
    @DisplayName("Debería manejar caracteres especiales en todos los campos")
    void testCaracteresEspeciales() {
        empleado.setNombre("María José");
        empleado.setApellidoPaterno("Ñañez");
        empleado.setApellidoMaterno("D'Artagnan");
        empleado.setUsuario("user.name@domain");
        empleado.setPassword("p@ssw0rd!#$%");
        empleado.setRol("ADMIN-EMPLEADO");
        empleado.setTelefono("+1 (555) 123-4567");
        empleado.setEmail("user.name+tag@domain.co.uk");

        assertEquals("María José", empleado.getNombre());
        assertEquals("Ñañez", empleado.getApellidoPaterno());
        assertEquals("D'Artagnan", empleado.getApellidoMaterno());
        assertEquals("user.name@domain", empleado.getUsuario());
        assertEquals("p@ssw0rd!#$%", empleado.getPassword());
        assertEquals("ADMIN-EMPLEADO", empleado.getRol());
        assertEquals("+1 (555) 123-4567", empleado.getTelefono());
        assertEquals("user.name+tag@domain.co.uk", empleado.getEmail());
    }

    @Test
    @DisplayName("Debería permitir múltiples cambios secuenciales")
    void testMultiplesCambiosSecuenciales() {
        // Múltiples cambios de nombre
        empleado.setNombre("Nombre1");
        assertEquals("Nombre1", empleado.getNombre());
        
        empleado.setNombre("Nombre2");
        assertEquals("Nombre2", empleado.getNombre());
        
        empleado.setNombre("Nombre3");
        assertEquals("Nombre3", empleado.getNombre());

        // Múltiples cambios de rol
        empleado.setRol("ADMIN");
        assertEquals("ADMIN", empleado.getRol());
        
        empleado.setRol("EMPLEADO");
        assertEquals("EMPLEADO", empleado.getRol());
        
        empleado.setRol("ADMIN");
        assertEquals("ADMIN", empleado.getRol());
    }

    @Test
    @DisplayName("Debería mantener la consistencia después de múltiples modificaciones")
    void testConsistenciaDespuesDeModificaciones() {
        // Valores originales
        String nombreOriginal = empleado.getNombre();
        String usuarioOriginal = empleado.getUsuario();

        // Realizar múltiples modificaciones
        empleado.setApellidoPaterno("Nuevo Apellido");
        empleado.setRol("EMPLEADO");
        empleado.setTelefono("999-9999");
        empleado.setEmail("nuevo@email.com");

        // Verificar que algunos campos cambiaron y otros no
        assertEquals(nombreOriginal, empleado.getNombre()); // No cambió
        assertEquals(usuarioOriginal, empleado.getUsuario()); // No cambió
        assertEquals("Nuevo Apellido", empleado.getApellidoPaterno()); // Cambió
        assertEquals("EMPLEADO", empleado.getRol()); // Cambió
        assertEquals("999-9999", empleado.getTelefono()); // Cambió
        assertEquals("nuevo@email.com", empleado.getEmail()); // Cambió
    }

    @Test
    @DisplayName("Debería funcionar correctamente todos los getters después del constructor")
    void testTodosLosGettersDespuesDelConstructor() {
        Empleado empleadoCompleto = new Empleado("Carlos", "García", "Martínez",
                                                "carlos.garcia", "pass456", "EMPLEADO",
                                                "555-8888", "carlos@biblioteca.com");

        assertEquals("Carlos", empleadoCompleto.getNombre());
        assertEquals("García", empleadoCompleto.getApellidoPaterno());
        assertEquals("Martínez", empleadoCompleto.getApellidoMaterno());
        assertEquals("carlos.garcia", empleadoCompleto.getUsuario());
        assertEquals("pass456", empleadoCompleto.getPassword());
        assertEquals("EMPLEADO", empleadoCompleto.getRol());
        assertEquals("555-8888", empleadoCompleto.getTelefono());
        assertEquals("carlos@biblioteca.com", empleadoCompleto.getEmail());
        assertEquals("Carlos García Martínez", empleadoCompleto.getNombreCompleto());
    }

    @Test
    @DisplayName("Debería manejar correctamente el ID")
    void testManejoId() {
        // Probar diferentes valores de ID
        empleado.setId(0);
        assertEquals(0, empleado.getId());

        empleado.setId(1);
        assertEquals(1, empleado.getId());

        empleado.setId(100);
        assertEquals(100, empleado.getId());

        empleado.setId(-1);
        assertEquals(-1, empleado.getId());

        empleado.setId(Integer.MAX_VALUE);
        assertEquals(Integer.MAX_VALUE, empleado.getId());
    }

    @Test
    @DisplayName("Debería crear empleados con diferentes configuraciones")
    void testEmpleadosConDiferentesConfiguraciones() {
        // Empleado sin apellido materno
        Empleado empleado1 = new Empleado("Ana", "López", null, 
                                         "ana.lopez", "pass1", "EMPLEADO", 
                                         "111-1111", "ana@biblioteca.com");

        // Empleado sin teléfono ni email
        Empleado empleado2 = new Empleado("Pedro", "Gómez", "Martínez", 
                                         "pedro.gomez", "pass2", "ADMIN", 
                                         null, null);

        // Empleado con espacios en blanco
        Empleado empleado3 = new Empleado("  María  ", "  Hernández  ", "  López  ", 
                                         "  maria  ", "  pass3  ", "  EMPLEADO  ", 
                                         "  333-3333  ", "  maria@biblioteca.com  ");

        assertEquals("Ana", empleado1.getNombre());
        assertNull(empleado1.getApellidoMaterno());
        
        assertEquals("Pedro", empleado2.getNombre());
        assertNull(empleado2.getTelefono());
        assertNull(empleado2.getEmail());
        
        assertEquals("  María  ", empleado3.getNombre());
        assertEquals("  Hernández  ", empleado3.getApellidoPaterno());
        assertEquals("  maria  ", empleado3.getUsuario());
    }

    @Test
    @DisplayName("Debería mantener la inmutabilidad relativa de los campos")
    void testInmutabilidadRelativa() {
        // El ID no debería cambiar una vez establecido (a menos que se llame setId nuevamente)
        empleado.setId(5);
        int idOriginal = empleado.getId();

        // Modificar otros campos
        empleado.setNombre("Nuevo Nombre");
        empleado.setRol("EMPLEADO");
        empleado.setTelefono("000-0000");

        // El ID debería permanecer igual
        assertEquals(idOriginal, empleado.getId());
    }

    @Test
    @DisplayName("Debería manejar contraseñas con diferentes formatos")
    void testManejoContraseñas() {
        // Contraseña vacía
        empleado.setPassword("");
        assertEquals("", empleado.getPassword());

        // Contraseña con espacios
        empleado.setPassword("  password  ");
        assertEquals("  password  ", empleado.getPassword());

        // Contraseña muy larga
        String passwordLarga = "P".repeat(1000);
        empleado.setPassword(passwordLarga);
        assertEquals(passwordLarga, empleado.getPassword());

        // Contraseña con caracteres especiales
        empleado.setPassword("p@$$w0rd!123#");
        assertEquals("p@$$w0rd!123#", empleado.getPassword());
    }
}