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
                            "Calle Principal 123", "555-1234");
    }

    @Test
    @DisplayName("Debería crear un usuario con constructor parametrizado y valores por defecto")
    void testConstructorParametrizado() {
        // Verificar datos básicos
        assertEquals("Juan", usuario.getNombre());
        assertEquals("Pérez", usuario.getApellidoPaterno());
        assertEquals("Gómez", usuario.getApellidoMaterno());
        assertEquals("Calle Principal 123", usuario.getDomicilio());
        assertEquals("555-1234", usuario.getTelefono());
        
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
    }

    @Test
    @DisplayName("Debería actualizar correctamente todos los atributos con setters")
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

        // Verificar todos los atributos
        assertEquals(1, usuario.getId());
        assertEquals("María", usuario.getNombre());
        assertEquals("López", usuario.getApellidoPaterno());
        assertEquals("Hernández", usuario.getApellidoMaterno());
        assertEquals("Avenida Central 456", usuario.getDomicilio());
        assertEquals("555-5678", usuario.getTelefono());
        assertEquals(2, usuario.getSanciones());
        assertEquals(100, usuario.getMontoSancion());
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
        
        // Verificar que los valores se asignan correctamente
        assertNull(usuarioNuevo.getNombre());
        assertEquals("", usuarioNuevo.getApellidoPaterno());
        assertEquals("   ", usuarioNuevo.getApellidoMaterno());
        assertNull(usuarioNuevo.getDomicilio());
        assertEquals("", usuarioNuevo.getTelefono());
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
}