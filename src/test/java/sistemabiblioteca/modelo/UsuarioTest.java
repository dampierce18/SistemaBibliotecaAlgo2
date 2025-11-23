package sistemabiblioteca.modelo;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import static org.junit.jupiter.api.Assertions.*;

class UsuarioTest {

    private Usuario usuario;

    @BeforeEach
    void setUp() {
        usuario = new Usuario("Juan", "Pérez", "Gómez", 
                            "Calle Principal 123", "555-1234", 1);
    }

    @Test
    @DisplayName("Debería crear un usuario con constructor parametrizado y valores por defecto incluyendo empleadoId")
    void testConstructorParametrizado() {
        // Verificar datos básicos
        assertEquals("Juan", usuario.getNombre());
        assertEquals("Pérez", usuario.getApellidoPaterno());
        assertEquals("Gómez", usuario.getApellidoMaterno());
        assertEquals("Calle Principal 123", usuario.getDomicilio());
        assertEquals("555-1234", usuario.getTelefono());
        assertEquals(1, usuario.getEmpleadoId());
        
        // Verificar valores por defecto
        assertEquals(0, usuario.getSanciones());
        assertEquals(0, usuario.getMontoSancion());
    }

    @Test
    @DisplayName("Debería crear un usuario con constructor vacío y valores por defecto")
    void testConstructorVacio() {
        Usuario usuarioVacio = new Usuario();
        
        // Verificar valores por defecto
        assertEquals(0, usuarioVacio.getId());
        assertNull(usuarioVacio.getNombre());
        assertNull(usuarioVacio.getApellidoPaterno());
        assertNull(usuarioVacio.getApellidoMaterno());
        assertNull(usuarioVacio.getDomicilio());
        assertNull(usuarioVacio.getTelefono());
        assertEquals(0, usuarioVacio.getSanciones());
        assertEquals(0, usuarioVacio.getMontoSancion());
        assertEquals(0, usuarioVacio.getEmpleadoId());
    }

    @Test
    @DisplayName("Debería actualizar correctamente todos los atributos con setters incluyendo empleadoId")
    void testSettersYGetters() {
        // Configurar todos los atributos
        usuario.setId(1);
        usuario.setNombre("María");
        usuario.setApellidoPaterno("López");
        usuario.setApellidoMaterno("Hernández");
        usuario.setDomicilio("Avenida Central 456");
        usuario.setTelefono("555-5678");
        usuario.setSanciones(2);
        usuario.setMontoSancion(100);
        usuario.setEmpleadoId(3);

        // Verificar todos los atributos
        assertEquals(1, usuario.getId());
        assertEquals("María", usuario.getNombre());
        assertEquals("López", usuario.getApellidoPaterno());
        assertEquals("Hernández", usuario.getApellidoMaterno());
        assertEquals("Avenida Central 456", usuario.getDomicilio());
        assertEquals("555-5678", usuario.getTelefono());
        assertEquals(2, usuario.getSanciones());
        assertEquals(100, usuario.getMontoSancion());
        assertEquals(3, usuario.getEmpleadoId());
    }

    @Test
    @DisplayName("Debería manejar correctamente las sanciones y montos")
    void testManejoSanciones() {
        // Probar diferentes valores de sanciones
        usuario.setSanciones(0);
        assertEquals(0, usuario.getSanciones());

        usuario.setSanciones(1);
        assertEquals(1, usuario.getSanciones());

        usuario.setSanciones(5);
        assertEquals(5, usuario.getSanciones());

        // Probar diferentes montos de sanción
        usuario.setMontoSancion(0);
        assertEquals(0, usuario.getMontoSancion());

        usuario.setMontoSancion(50);
        assertEquals(50, usuario.getMontoSancion());

        usuario.setMontoSancion(200);
        assertEquals(200, usuario.getMontoSancion());
    }

    @Test
    @DisplayName("Debería manejar valores nulos y vacíos en campos de texto")
    void testManejoValoresNulosYVacios() {
        Usuario usuarioNuevo = new Usuario();
        
        // Probar setters con valores nulos
        usuarioNuevo.setNombre(null);
        usuarioNuevo.setApellidoPaterno("");
        usuarioNuevo.setApellidoMaterno("   ");
        usuarioNuevo.setDomicilio(null);
        usuarioNuevo.setTelefono("");
        usuarioNuevo.setEmpleadoId(2);
        
        // Verificar que los valores se asignan correctamente
        assertNull(usuarioNuevo.getNombre());
        assertEquals("", usuarioNuevo.getApellidoPaterno());
        assertEquals("   ", usuarioNuevo.getApellidoMaterno());
        assertNull(usuarioNuevo.getDomicilio());
        assertEquals("", usuarioNuevo.getTelefono());
        assertEquals(2, usuarioNuevo.getEmpleadoId());
    }

    @Test
    @DisplayName("Debería mantener la independencia entre sanciones y monto de sanción")
    void testIndependenciaSancionesYMonto() {
        // Configurar sanciones sin afectar monto
        usuario.setSanciones(3);
        assertEquals(3, usuario.getSanciones());
        assertEquals(0, usuario.getMontoSancion()); // Debe mantenerse en 0

        // Configurar monto sin afectar sanciones
        usuario.setMontoSancion(150);
        assertEquals(3, usuario.getSanciones()); // Debe mantenerse en 3
        assertEquals(150, usuario.getMontoSancion());

        // Cambiar sanciones sin afectar monto
        usuario.setSanciones(1);
        assertEquals(1, usuario.getSanciones());
        assertEquals(150, usuario.getMontoSancion()); // Debe mantenerse en 150
    }

    @Test
    @DisplayName("Debería manejar correctamente el empleadoId en diferentes escenarios")
    void testManejoEmpleadoId() {
        // Verificar empleadoId inicial
        assertEquals(1, usuario.getEmpleadoId());

        // Cambiar empleadoId
        usuario.setEmpleadoId(5);
        assertEquals(5, usuario.getEmpleadoId());

        // Cambiar a cero
        usuario.setEmpleadoId(0);
        assertEquals(0, usuario.getEmpleadoId());

        // Cambiar a negativo
        usuario.setEmpleadoId(-1);
        assertEquals(-1, usuario.getEmpleadoId());
    }

    @Test
    @DisplayName("Debería generar correctamente el nombre completo")
    void testGetNombreCompleto() {
        // Caso normal con los tres nombres
        assertEquals("Juan Pérez Gómez", usuario.getNombreCompleto());

        // Caso sin apellido materno
        usuario.setApellidoMaterno(null);
        assertEquals("Juan Pérez", usuario.getNombreCompleto());

        // Caso con apellido materno vacío
        usuario.setApellidoMaterno("");
        assertEquals("Juan Pérez", usuario.getNombreCompleto());

        // Caso con apellido materno con espacios
        usuario.setApellidoMaterno("   ");
        assertEquals("Juan Pérez", usuario.getNombreCompleto());

        // Caso con solo nombre y apellido paterno
        usuario.setNombre("Ana");
        usuario.setApellidoPaterno("López");
        usuario.setApellidoMaterno(null);
        assertEquals("Ana López", usuario.getNombreCompleto());
    }

    @Test
    @DisplayName("Debería crear usuarios con diferentes empleados")
    void testUsuariosConDiferentesEmpleados() {
        Usuario usuarioEmpleado1 = new Usuario("Usuario1", "Apellido1", "Materno1", 
                                              "Dir1", "111-1111", 1);
        Usuario usuarioEmpleado2 = new Usuario("Usuario2", "Apellido2", "Materno2", 
                                              "Dir2", "222-2222", 2);
        Usuario usuarioEmpleado3 = new Usuario("Usuario3", "Apellido3", "Materno3", 
                                              "Dir3", "333-3333", 3);

        assertEquals(1, usuarioEmpleado1.getEmpleadoId());
        assertEquals(2, usuarioEmpleado2.getEmpleadoId());
        assertEquals(3, usuarioEmpleado3.getEmpleadoId());
    }

    @Test
    @DisplayName("Debería mantener la consistencia después de múltiples modificaciones")
    void testConsistenciaDespuesDeModificaciones() {
        // Valores iniciales
        String nombreOriginal = usuario.getNombre();

        // Realizar múltiples modificaciones
        usuario.setSanciones(2);
        usuario.setMontoSancion(75);
        usuario.setDomicilio("Nueva Dirección");
        usuario.setEmpleadoId(10);

        // Verificar que algunos campos cambiaron y otros no
        assertEquals(nombreOriginal, usuario.getNombre()); // No cambió
        assertEquals(2, usuario.getSanciones()); // Cambió
        assertEquals(75, usuario.getMontoSancion()); // Cambió
        assertEquals("Nueva Dirección", usuario.getDomicilio()); // Cambió
        assertEquals(10, usuario.getEmpleadoId()); // Cambió
    }

    @Test
    @DisplayName("Debería funcionar correctamente todos los getters después del constructor")
    void testTodosLosGettersDespuesDelConstructor() {
        Usuario usuarioCompleto = new Usuario("Carlos", "García", "Martínez",
                                             "Avenida Siempre Viva 742", "555-9999", 5);

        assertEquals("Carlos", usuarioCompleto.getNombre());
        assertEquals("García", usuarioCompleto.getApellidoPaterno());
        assertEquals("Martínez", usuarioCompleto.getApellidoMaterno());
        assertEquals("Avenida Siempre Viva 742", usuarioCompleto.getDomicilio());
        assertEquals("555-9999", usuarioCompleto.getTelefono());
        assertEquals(5, usuarioCompleto.getEmpleadoId());
        assertEquals(0, usuarioCompleto.getSanciones());
        assertEquals(0, usuarioCompleto.getMontoSancion());
    }

    @Test
    @DisplayName("Debería permitir múltiples cambios de empleadoId")
    void testMultiplesCambiosEmpleadoId() {
        assertEquals(1, usuario.getEmpleadoId());

        usuario.setEmpleadoId(3);
        assertEquals(3, usuario.getEmpleadoId());

        usuario.setEmpleadoId(7);
        assertEquals(7, usuario.getEmpleadoId());

        usuario.setEmpleadoId(15);
        assertEquals(15, usuario.getEmpleadoId());
    }

    @Test
    @DisplayName("Debería crear usuario con empleadoId cero")
    void testUsuarioConEmpleadoIdCero() {
        Usuario usuarioCero = new Usuario("Test", "Apellido", "Materno", 
                                         "Dirección", "Teléfono", 0);
        assertEquals(0, usuarioCero.getEmpleadoId());
        assertEquals("Test", usuarioCero.getNombre());
    }

    @Test
    @DisplayName("Debería crear usuario con empleadoId negativo")
    void testUsuarioConEmpleadoIdNegativo() {
        Usuario usuarioNegativo = new Usuario("Test", "Apellido", "Materno", 
                                             "Dirección", "Teléfono", -5);
        assertEquals(-5, usuarioNegativo.getEmpleadoId());
        assertEquals("Test", usuarioNegativo.getNombre());
    }

    @Test
    @DisplayName("Debería mantener la independencia entre empleadoId y otros campos")
    void testIndependenciaEmpleadoId() {
        // Cambiar empleadoId sin afectar otros campos
        int sancionesOriginal = usuario.getSanciones();
        int montoOriginal = usuario.getMontoSancion();
        String domicilioOriginal = usuario.getDomicilio();

        usuario.setEmpleadoId(8);

        assertEquals(8, usuario.getEmpleadoId());
        assertEquals(sancionesOriginal, usuario.getSanciones());
        assertEquals(montoOriginal, usuario.getMontoSancion());
        assertEquals(domicilioOriginal, usuario.getDomicilio());

        // Cambiar otros campos sin afectar empleadoId
        usuario.setSanciones(4);
        usuario.setMontoSancion(200);
        usuario.setDomicilio("Nuevo Domicilio");

        assertEquals(8, usuario.getEmpleadoId()); // Debe mantenerse igual
        assertEquals(4, usuario.getSanciones());
        assertEquals(200, usuario.getMontoSancion());
        assertEquals("Nuevo Domicilio", usuario.getDomicilio());
    }
}