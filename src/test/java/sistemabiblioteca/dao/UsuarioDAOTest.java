package sistemabiblioteca.dao;

import sistemabiblioteca.modelo.Usuario;
import sistemabiblioteca.test.ConexionTestDB;
import org.junit.jupiter.api.*;
import java.sql.Connection;
import java.util.List;
import static org.junit.jupiter.api.Assertions.*;

@TestInstance(TestInstance.Lifecycle.PER_METHOD)
class UsuarioDAOTest {
    private UsuarioDAO usuarioDAO;
    private Connection testConnection;
    
    @BeforeEach
    void setUp() throws Exception {
        testConnection = ConexionTestDB.getTestConnection();
        usuarioDAO = new UsuarioDAO(testConnection);
        
        // Limpiar datos antes de cada test
        try (var stmt = testConnection.createStatement()) {
            stmt.execute("DELETE FROM prestamos");
            stmt.execute("DELETE FROM libros");
            stmt.execute("DELETE FROM usuarios");
            
            // Insertar empleados básicos para las foreign keys
            stmt.execute("INSERT OR IGNORE INTO empleados (id, nombre, apellido_paterno, usuario, password, rol) VALUES " +
                        "(1, 'Admin', 'Sistema', 'admin', 'admin123', 'ADMIN'), " +
                        "(2, 'Empleado', 'Test', 'empleado', 'empleado123', 'EMPLEADO')");
        }
    }
    
    @Test
    void testInsertarUsuarioConEmpleadoId() {
        // Given
        Usuario usuario = new Usuario("Juan", "Pérez", "Gómez", 
                                    "Calle 123", "555-1234", 1);
        
        // When
        boolean resultado = usuarioDAO.insertarUsuario(usuario);
        
        // Then
        assertTrue(resultado, "El usuario debería insertarse correctamente");
        
        List<Usuario> usuarios = usuarioDAO.obtenerTodosLosUsuarios();
        assertEquals(1, usuarios.size(), "Debería haber exactamente 1 usuario");
        assertTrue(usuarios.get(0).getId() > 0, "El usuario debería tener un ID asignado");
        assertEquals(1, usuarios.get(0).getEmpleadoId(), "Debería tener el empleado_id correcto");
    }
    
    @Test
    void testInsertarUsuarioConSancionesYEmpleadoId() {
        // Given - Usuario con sanciones, monto y empleado_id
        Usuario usuario = new Usuario("María", "López", "Santos", 
                                    "Av. Principal 456", "555-5678", 2);
        usuario.setSanciones(2);
        usuario.setMontoSancion(50);
        
        // When
        boolean resultado = usuarioDAO.insertarUsuario(usuario);
        
        // Then
        assertTrue(resultado, "El usuario con sanciones debería insertarse correctamente");
        
        List<Usuario> usuarios = usuarioDAO.obtenerTodosLosUsuarios();
        Usuario usuarioInsertado = usuarios.get(0);
        assertEquals(2, usuarioInsertado.getSanciones());
        assertEquals(50, usuarioInsertado.getMontoSancion());
        assertEquals(2, usuarioInsertado.getEmpleadoId());
    }
    
    
    @Test
    void testObtenerUsuarioPorIdNoExistente() {
        // When
        Usuario usuarioObtenido = usuarioDAO.obtenerUsuarioPorId(999);
        
        // Then
        assertNull(usuarioObtenido, "Debería retornar null para ID no existente");
    }
    
    
    
    @Test
    void testBuscarUsuariosPorNombre() {
        // Given
        Usuario usuario1 = new Usuario("Carlos", "Pérez", "Gómez", "Dir 1", "111-1111", 1);
        Usuario usuario2 = new Usuario("Ana", "García", "López", "Dir 2", "222-2222", 2);
        Usuario usuario3 = new Usuario("Carlos", "Martínez", "Sánchez", "Dir 3", "333-3333", 1);
        
        usuarioDAO.insertarUsuario(usuario1);
        usuarioDAO.insertarUsuario(usuario2);
        usuarioDAO.insertarUsuario(usuario3);
        
        // When
        List<Usuario> resultados = usuarioDAO.buscarUsuarios("Nombre", "Carlos");
        
        // Then
        assertEquals(2, resultados.size(), "Debería encontrar 2 usuarios llamados Carlos");
        assertTrue(resultados.stream().allMatch(u -> u.getNombre().contains("Carlos")));
        assertTrue(resultados.stream().allMatch(u -> u.getEmpleadoId() > 0));
    }
    
    @Test
    void testBuscarUsuariosPorApellidoPaterno() {
        // Given
        Usuario usuario1 = new Usuario("Juan", "García", "Pérez", "Dir 1", "111-1111", 1);
        Usuario usuario2 = new Usuario("María", "López", "Gómez", "Dir 2", "222-2222", 2);
        Usuario usuario3 = new Usuario("Pedro", "García", "Martínez", "Dir 3", "333-3333", 1);
        
        usuarioDAO.insertarUsuario(usuario1);
        usuarioDAO.insertarUsuario(usuario2);
        usuarioDAO.insertarUsuario(usuario3);
        
        // When
        List<Usuario> resultados = usuarioDAO.buscarUsuarios("Apellido Paterno", "García");
        
        // Then
        assertEquals(2, resultados.size(), "Debería encontrar 2 usuarios con apellido García");
        assertTrue(resultados.stream().allMatch(u -> u.getApellidoPaterno().contains("García")));
        assertTrue(resultados.stream().allMatch(u -> u.getEmpleadoId() > 0));
    }
    
    @Test
    void testBuscarUsuariosPorTelefono() {
        // Given
        Usuario usuario1 = new Usuario("Usuario1", "Apellido1", "Materno1", "Dir 1", "555-1234", 1);
        Usuario usuario2 = new Usuario("Usuario2", "Apellido2", "Materno2", "Dir 2", "555-5678", 2);
        Usuario usuario3 = new Usuario("Usuario3", "Apellido3", "Materno3", "Dir 3", "555-1234", 1);
        
        usuarioDAO.insertarUsuario(usuario1);
        usuarioDAO.insertarUsuario(usuario2);
        usuarioDAO.insertarUsuario(usuario3);
        
        // When
        List<Usuario> resultados = usuarioDAO.buscarUsuarios("Teléfono", "555-1234");
        
        // Then
        assertEquals(2, resultados.size(), "Debería encontrar 2 usuarios con el mismo teléfono");
        assertTrue(resultados.stream().allMatch(u -> u.getTelefono().contains("555-1234")));
        assertTrue(resultados.stream().allMatch(u -> u.getEmpleadoId() > 0));
    }
    
    
    @Test
    void testBuscarUsuariosPorIDNoValido() {
        // Given - No insertar usuarios
        
        // When - Buscar por ID no numérico
        List<Usuario> resultados = usuarioDAO.buscarUsuarios("ID", "no-es-un-numero");
        
        // Then
        assertTrue(resultados.isEmpty(), "Debería retornar lista vacía para ID no válido");
    }
    
    @Test
    void testBuscarUsuariosConCriterioDefault() {
        // Given
        Usuario usuario = new Usuario("Usuario Default", "Apellido", "Materno", "Dir", "111-1111", 1);
        usuarioDAO.insertarUsuario(usuario);
        
        // When - Usar un criterio no reconocido (debería usar el default: Nombre)
        List<Usuario> resultados = usuarioDAO.buscarUsuarios("CriterioDesconocido", "Default");
        
        // Then
        assertEquals(1, resultados.size(), "Debería encontrar el usuario por nombre (default)");
        assertEquals("Usuario Default", resultados.get(0).getNombre());
        assertEquals(1, resultados.get(0).getEmpleadoId());
    }
    
    @Test
    void testContarTotalUsuarios() {
        // Given
        assertEquals(0, usuarioDAO.contarTotalUsuarios(), "Debería haber 0 usuarios inicialmente");
        
        Usuario usuario1 = new Usuario("Usuario 1", "Apellido1", "Materno1", "Dir 1", "111-1111", 1);
        Usuario usuario2 = new Usuario("Usuario 2", "Apellido2", "Materno2", "Dir 2", "222-2222", 2);
        
        usuarioDAO.insertarUsuario(usuario1);
        usuarioDAO.insertarUsuario(usuario2);
        
        // When
        int total = usuarioDAO.contarTotalUsuarios();
        
        // Then
        assertEquals(2, total, "Debería haber 2 usuarios en total");
    }
    
    @Test
    void testConstructorUsuarioVacio() {
        // Given
        Usuario usuario = new Usuario();
        
        // When - Establecer valores después de crear
        usuario.setId(1);
        usuario.setNombre("Test");
        usuario.setApellidoPaterno("Apellido");
        usuario.setApellidoMaterno("Materno");
        usuario.setDomicilio("Domicilio");
        usuario.setTelefono("Telefono");
        usuario.setSanciones(1);
        usuario.setMontoSancion(25);
        usuario.setEmpleadoId(2);
        
        // Then - Verificar valores por defecto y setters
        assertEquals(1, usuario.getId());
        assertEquals("Test", usuario.getNombre());
        assertEquals("Apellido", usuario.getApellidoPaterno());
        assertEquals("Materno", usuario.getApellidoMaterno());
        assertEquals("Domicilio", usuario.getDomicilio());
        assertEquals("Telefono", usuario.getTelefono());
        assertEquals(1, usuario.getSanciones());
        assertEquals(25, usuario.getMontoSancion());
        assertEquals(2, usuario.getEmpleadoId());
    }
    
    @Test
    void testConstructorUsuarioConParametrosYEmpleadoId() {
        // Given
        Usuario usuario = new Usuario("Nombre", "Paterno", "Materno", "Domicilio", "Telefono", 2);
        
        // Then - Verificar valores iniciales
        assertEquals("Nombre", usuario.getNombre());
        assertEquals("Paterno", usuario.getApellidoPaterno());
        assertEquals("Materno", usuario.getApellidoMaterno());
        assertEquals("Domicilio", usuario.getDomicilio());
        assertEquals("Telefono", usuario.getTelefono());
        assertEquals(0, usuario.getSanciones()); // Valor por defecto
        assertEquals(0, usuario.getMontoSancion()); // Valor por defecto
        assertEquals(2, usuario.getEmpleadoId()); // Nuevo parámetro
    }
    
    @Test
    void testTodosLosSettersDeUsuario() {
        // Given
        Usuario usuario = new Usuario();
        
        // When - Usar TODOS los setters incluyendo empleadoId
        usuario.setId(100);
        usuario.setNombre("Nuevo Nombre");
        usuario.setApellidoPaterno("Nuevo Paterno");
        usuario.setApellidoMaterno("Nuevo Materno");
        usuario.setDomicilio("Nuevo Domicilio");
        usuario.setTelefono("Nuevo Teléfono");
        usuario.setSanciones(5);
        usuario.setMontoSancion(100);
        usuario.setEmpleadoId(3);
        
        // Then - Verificar que TODOS los setters funcionan
        assertEquals(100, usuario.getId());
        assertEquals("Nuevo Nombre", usuario.getNombre());
        assertEquals("Nuevo Paterno", usuario.getApellidoPaterno());
        assertEquals("Nuevo Materno", usuario.getApellidoMaterno());
        assertEquals("Nuevo Domicilio", usuario.getDomicilio());
        assertEquals("Nuevo Teléfono", usuario.getTelefono());
        assertEquals(5, usuario.getSanciones());
        assertEquals(100, usuario.getMontoSancion());
        assertEquals(3, usuario.getEmpleadoId());
    }
    
    @Test
    void testGetNombreCompleto() {
        // Given
        Usuario usuario = new Usuario("Juan", "Pérez", "Gómez", "Dir", "Tel", 1);
        
        // When
        String nombreCompleto = usuario.getNombreCompleto();
        
        // Then
        assertEquals("Juan Pérez Gómez", nombreCompleto);
    }
    
    @Test
    void testGetNombreCompletoSinApellidoMaterno() {
        // Given
        Usuario usuario = new Usuario();
        usuario.setNombre("Ana");
        usuario.setApellidoPaterno("López");
        usuario.setApellidoMaterno(""); // Apellido materno vacío
        
        // When
        String nombreCompleto = usuario.getNombreCompleto();
        
        // Then
        assertEquals("Ana López", nombreCompleto);
    }
    
    @Test
    void testGetNombreCompletoSoloNombreYApellidoPaterno() {
        // Given
        Usuario usuario = new Usuario();
        usuario.setNombre("Pedro");
        usuario.setApellidoPaterno("García");
        // Sin apellido materno
        
        // When
        String nombreCompleto = usuario.getNombreCompleto();
        
        // Then
        assertEquals("Pedro García", nombreCompleto);
    }
    
    // 🔥 NUEVOS TESTS ESPECÍFICOS PARA EMPLEADO_ID
    
    @Test
    void testUsuarioConDiferentesEmpleados() {
        // Given - Crear usuarios con diferentes empleados
        Usuario usuario1 = new Usuario("Usuario1", "Apellido1", "Materno1", "Dir1", "111-1111", 1);
        Usuario usuario2 = new Usuario("Usuario2", "Apellido2", "Materno2", "Dir2", "222-2222", 2);
        Usuario usuario3 = new Usuario("Usuario3", "Apellido3", "Materno3", "Dir3", "333-3333", 1);
        
        usuarioDAO.insertarUsuario(usuario1);
        usuarioDAO.insertarUsuario(usuario2);
        usuarioDAO.insertarUsuario(usuario3);
        
        // When
        List<Usuario> usuarios = usuarioDAO.obtenerTodosLosUsuarios();
        
        // Then
        assertEquals(3, usuarios.size());
        assertEquals(1, usuarios.get(0).getEmpleadoId());
        assertEquals(2, usuarios.get(1).getEmpleadoId());
        assertEquals(1, usuarios.get(2).getEmpleadoId());
    }
    
    @Test
    void testUsuarioConEmpleadoIdCero() {
        // Given - Empleado_id = 0 (caso límite)
        Usuario usuario = new Usuario("Usuario Test", "Apellido", "Materno", "Dir", "Tel", 0);
        
        // When
        boolean resultado = usuarioDAO.insertarUsuario(usuario);
        
        // Then
        assertTrue(resultado, "Debería insertarse incluso con empleado_id = 0");
        
        List<Usuario> usuarios = usuarioDAO.obtenerTodosLosUsuarios();
        assertEquals(0, usuarios.get(0).getEmpleadoId());
    }
    
    
    // 🔥 TESTS PARA CASOS DE ERROR
    @Test
    void testActualizarUsuarioNoExistente() {
        // Given - Crear un usuario con ID que no existe en la BD
        Usuario usuarioNoExistente = new Usuario("No Existe", "Apellido", "Materno", "Dir", "Tel", 1);
        usuarioNoExistente.setId(9999);
        
        // When
        boolean resultado = usuarioDAO.actualizarUsuario(usuarioNoExistente);
        
        // Then
        assertFalse(resultado, "Debería retornar false al actualizar usuario no existente");
    }
    
    @Test
    void testEliminarUsuarioNoExistente() {
        // When
        boolean resultado = usuarioDAO.eliminarUsuario(9999);
        
        // Then
        assertFalse(resultado, "Debería retornar false al eliminar usuario no existente");
    }
    
    @Test
    void testBuscarUsuariosSinResultados() {
        // Given - No insertar ningún usuario
        
        // When
        List<Usuario> resultados = usuarioDAO.buscarUsuarios("Nombre", "NoExiste");
        
        // Then
        assertNotNull(resultados, "Debería retornar una lista (no null)");
        assertTrue(resultados.isEmpty(), "La lista de resultados debería estar vacía");
    }
    
    @Test
    void testConstructorPorDefecto() {
        // When - Crear UsuarioDAO con constructor por defecto
        UsuarioDAO usuarioDAODefault = new UsuarioDAO();
        
        // Then - Verificar que se crea correctamente
        assertNotNull(usuarioDAODefault, "El constructor por defecto debería funcionar");
        
        // También puedes verificar que puede realizar operaciones básicas
        List<Usuario> usuarios = usuarioDAODefault.obtenerTodosLosUsuarios();
        assertNotNull(usuarios, "Debería retornar una lista (aunque esté vacía)");
    }
    
    @Test
    void testUsuarioConValoresPorDefecto() {
        // Given - Usuario con constructor vacío
        Usuario usuario = new Usuario();
        
        // Then - Verificar valores por defecto
        assertEquals(0, usuario.getId());
        assertNull(usuario.getNombre());
        assertNull(usuario.getApellidoPaterno());
        assertNull(usuario.getApellidoMaterno());
        assertNull(usuario.getDomicilio());
        assertNull(usuario.getTelefono());
        assertEquals(0, usuario.getSanciones());
        assertEquals(0, usuario.getMontoSancion());
        assertEquals(0, usuario.getEmpleadoId());
    }
}