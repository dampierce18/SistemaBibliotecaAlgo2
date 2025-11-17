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
        // ✅ Necesitamos modificar UsuarioDAO para aceptar conexión de test
        // Por ahora, usaremos el mismo patrón que LibroDAO
        usuarioDAO = new UsuarioDAO(testConnection);
        
        // Limpiar datos antes de cada test
        try (var stmt = testConnection.createStatement()) {
            stmt.execute("DELETE FROM prestamos");
            stmt.execute("DELETE FROM libros");
            stmt.execute("DELETE FROM usuarios");
        }
    }
    
    @Test
    void testInsertarUsuario() {
        // Given
        Usuario usuario = new Usuario("Juan", "Pérez", "Gómez", 
                                    "Calle 123", "555-1234");
        
        // When
        boolean resultado = usuarioDAO.insertarUsuario(usuario);
        
        // Then
        assertTrue(resultado, "El usuario debería insertarse correctamente");
        
        List<Usuario> usuarios = usuarioDAO.obtenerTodosLosUsuarios();
        assertEquals(1, usuarios.size(), "Debería haber exactamente 1 usuario");
        assertTrue(usuarios.get(0).getId() > 0, "El usuario debería tener un ID asignado");
    }
    
    @Test
    void testInsertarUsuarioConSanciones() {
        // Given - Usuario con sanciones y monto
        Usuario usuario = new Usuario("María", "López", "Santos", 
                                    "Av. Principal 456", "555-5678");
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
    }
    
    @Test
    void testObtenerUsuarioPorId() {
        // Given
        Usuario usuario = new Usuario("Carlos", "García", "Martínez", 
                                    "Calle Secundaria 789", "555-9012");
        usuarioDAO.insertarUsuario(usuario);
        
        List<Usuario> usuarios = usuarioDAO.obtenerTodosLosUsuarios();
        int idInsertado = usuarios.get(0).getId();
        
        // When
        Usuario usuarioObtenido = usuarioDAO.obtenerUsuarioPorId(idInsertado);
        
        // Then
        assertNotNull(usuarioObtenido, "Debería encontrar el usuario por ID");
        assertEquals("Carlos", usuarioObtenido.getNombre());
        assertEquals("García", usuarioObtenido.getApellidoPaterno());
        assertEquals("Martínez", usuarioObtenido.getApellidoMaterno());
        assertEquals("Calle Secundaria 789", usuarioObtenido.getDomicilio());
        assertEquals("555-9012", usuarioObtenido.getTelefono());
    }
    
    @Test
    void testObtenerUsuarioPorIdNoExistente() {
        // When
        Usuario usuarioObtenido = usuarioDAO.obtenerUsuarioPorId(999);
        
        // Then
        assertNull(usuarioObtenido, "Debería retornar null para ID no existente");
    }
    
    @Test
    void testObtenerTodosLosUsuarios() {
        // Given
        Usuario usuario1 = new Usuario("Ana", "Rodríguez", "Fernández", "Dir 1", "111-1111");
        Usuario usuario2 = new Usuario("Pedro", "Sánchez", "Díaz", "Dir 2", "222-2222");
        Usuario usuario3 = new Usuario("Laura", "Hernández", "Jiménez", "Dir 3", "333-3333");
        
        usuarioDAO.insertarUsuario(usuario1);
        usuarioDAO.insertarUsuario(usuario2);
        usuarioDAO.insertarUsuario(usuario3);
        
        // When
        List<Usuario> usuarios = usuarioDAO.obtenerTodosLosUsuarios();
        
        // Then
        assertEquals(3, usuarios.size(), "Debería haber 3 usuarios");
        // Verificar orden alfabético
        assertEquals("Ana", usuarios.get(0).getNombre());
        assertEquals("Laura", usuarios.get(1).getNombre());
        assertEquals("Pedro", usuarios.get(2).getNombre());
    }
    
    @Test
    void testActualizarUsuario() {
        // Given
        Usuario usuario = new Usuario("Nombre Original", "Apellido Orig", "Materno Orig", 
                                    "Domicilio Orig", "000-0000");
        usuarioDAO.insertarUsuario(usuario);
        
        List<Usuario> usuarios = usuarioDAO.obtenerTodosLosUsuarios();
        Usuario usuarioInsertado = usuarios.get(0);
        
        // When - Actualizar el usuario
        usuarioInsertado.setNombre("Nombre Actualizado");
        usuarioInsertado.setApellidoPaterno("Apellido Actualizado");
        usuarioInsertado.setApellidoMaterno("Materno Actualizado");
        usuarioInsertado.setDomicilio("Domicilio Actualizado");
        usuarioInsertado.setTelefono("999-9999");
        usuarioInsertado.setSanciones(3);
        usuarioInsertado.setMontoSancion(75);
        
        boolean resultado = usuarioDAO.actualizarUsuario(usuarioInsertado);
        
        // Then
        assertTrue(resultado, "La actualización debería ser exitosa");
        
        Usuario usuarioActualizado = usuarioDAO.obtenerUsuarioPorId(usuarioInsertado.getId());
        assertNotNull(usuarioActualizado);
        assertEquals("Nombre Actualizado", usuarioActualizado.getNombre());
        assertEquals("Apellido Actualizado", usuarioActualizado.getApellidoPaterno());
        assertEquals("Materno Actualizado", usuarioActualizado.getApellidoMaterno());
        assertEquals("Domicilio Actualizado", usuarioActualizado.getDomicilio());
        assertEquals("999-9999", usuarioActualizado.getTelefono());
        assertEquals(3, usuarioActualizado.getSanciones());
        assertEquals(75, usuarioActualizado.getMontoSancion());
    }
    
    @Test
    void testEliminarUsuario() {
        // Given
        Usuario usuario = new Usuario("Usuario a Eliminar", "Apellido", "Materno", 
                                    "Domicilio", "111-1111");
        usuarioDAO.insertarUsuario(usuario);
        
        List<Usuario> usuarios = usuarioDAO.obtenerTodosLosUsuarios();
        int idAEliminar = usuarios.get(0).getId();
        assertEquals(1, usuarios.size(), "Debería haber 1 usuario antes de eliminar");
        
        // When
        boolean resultado = usuarioDAO.eliminarUsuario(idAEliminar);
        
        // Then
        assertTrue(resultado, "La eliminación debería ser exitosa");
        
        List<Usuario> usuariosDespues = usuarioDAO.obtenerTodosLosUsuarios();
        assertEquals(0, usuariosDespues.size(), "No debería haber usuarios después de eliminar");
    }
    
    @Test
    void testBuscarUsuariosPorNombre() {
        // Given
        Usuario usuario1 = new Usuario("Carlos", "Pérez", "Gómez", "Dir 1", "111-1111");
        Usuario usuario2 = new Usuario("Ana", "García", "López", "Dir 2", "222-2222");
        Usuario usuario3 = new Usuario("Carlos", "Martínez", "Sánchez", "Dir 3", "333-3333");
        
        usuarioDAO.insertarUsuario(usuario1);
        usuarioDAO.insertarUsuario(usuario2);
        usuarioDAO.insertarUsuario(usuario3);
        
        // When
        List<Usuario> resultados = usuarioDAO.buscarUsuarios("Nombre", "Carlos");
        
        // Then
        assertEquals(2, resultados.size(), "Debería encontrar 2 usuarios llamados Carlos");
        assertTrue(resultados.stream().allMatch(u -> u.getNombre().contains("Carlos")));
    }
    
    @Test
    void testBuscarUsuariosPorApellidoPaterno() {
        // Given
        Usuario usuario1 = new Usuario("Juan", "García", "Pérez", "Dir 1", "111-1111");
        Usuario usuario2 = new Usuario("María", "López", "Gómez", "Dir 2", "222-2222");
        Usuario usuario3 = new Usuario("Pedro", "García", "Martínez", "Dir 3", "333-3333");
        
        usuarioDAO.insertarUsuario(usuario1);
        usuarioDAO.insertarUsuario(usuario2);
        usuarioDAO.insertarUsuario(usuario3);
        
        // When
        List<Usuario> resultados = usuarioDAO.buscarUsuarios("Apellido Paterno", "García");
        
        // Then
        assertEquals(2, resultados.size(), "Debería encontrar 2 usuarios con apellido García");
        assertTrue(resultados.stream().allMatch(u -> u.getApellidoPaterno().contains("García")));
    }
    
    @Test
    void testBuscarUsuariosPorTelefono() {
        // Given
        Usuario usuario1 = new Usuario("Usuario1", "Apellido1", "Materno1", "Dir 1", "555-1234");
        Usuario usuario2 = new Usuario("Usuario2", "Apellido2", "Materno2", "Dir 2", "555-5678");
        Usuario usuario3 = new Usuario("Usuario3", "Apellido3", "Materno3", "Dir 3", "555-1234");
        
        usuarioDAO.insertarUsuario(usuario1);
        usuarioDAO.insertarUsuario(usuario2);
        usuarioDAO.insertarUsuario(usuario3);
        
        // When
        List<Usuario> resultados = usuarioDAO.buscarUsuarios("Teléfono", "555-1234");
        
        // Then
        assertEquals(2, resultados.size(), "Debería encontrar 2 usuarios con el mismo teléfono");
        assertTrue(resultados.stream().allMatch(u -> u.getTelefono().contains("555-1234")));
    }
    
    @Test
    void testBuscarUsuariosPorID() {
        // Given
        Usuario usuario1 = new Usuario("Usuario1", "Apellido1", "Materno1", "Dir 1", "111-1111");
        Usuario usuario2 = new Usuario("Usuario2", "Apellido2", "Materno2", "Dir 2", "222-2222");
        
        usuarioDAO.insertarUsuario(usuario1);
        usuarioDAO.insertarUsuario(usuario2);
        
        List<Usuario> usuarios = usuarioDAO.obtenerTodosLosUsuarios();
        int idBuscado = usuarios.get(0).getId();
        
        // When - Buscar por ID específico
        List<Usuario> resultados = usuarioDAO.buscarUsuarios("ID", String.valueOf(idBuscado));
        
        // Then
        assertEquals(1, resultados.size(), "Debería encontrar exactamente 1 usuario por ID");
        assertEquals(idBuscado, resultados.get(0).getId());
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
        Usuario usuario = new Usuario("Usuario Default", "Apellido", "Materno", "Dir", "111-1111");
        usuarioDAO.insertarUsuario(usuario);
        
        // When - Usar un criterio no reconocido (debería usar el default: Nombre)
        List<Usuario> resultados = usuarioDAO.buscarUsuarios("CriterioDesconocido", "Default");
        
        // Then
        assertEquals(1, resultados.size(), "Debería encontrar el usuario por nombre (default)");
        assertEquals("Usuario Default", resultados.get(0).getNombre());
    }
    
    @Test
    void testContarTotalUsuarios() {
        // Given
        assertEquals(0, usuarioDAO.contarTotalUsuarios(), "Debería haber 0 usuarios inicialmente");
        
        Usuario usuario1 = new Usuario("Usuario 1", "Apellido1", "Materno1", "Dir 1", "111-1111");
        Usuario usuario2 = new Usuario("Usuario 2", "Apellido2", "Materno2", "Dir 2", "222-2222");
        
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
        
        // Then - Verificar valores por defecto y setters
        assertEquals(1, usuario.getId());
        assertEquals("Test", usuario.getNombre());
        assertEquals("Apellido", usuario.getApellidoPaterno());
        assertEquals("Materno", usuario.getApellidoMaterno());
        assertEquals("Domicilio", usuario.getDomicilio());
        assertEquals("Telefono", usuario.getTelefono());
        assertEquals(1, usuario.getSanciones());
        assertEquals(25, usuario.getMontoSancion());
    }
    
    @Test
    void testConstructorUsuarioConParametros() {
        // Given
        Usuario usuario = new Usuario("Nombre", "Paterno", "Materno", "Domicilio", "Telefono");
        
        // Then - Verificar valores iniciales
        assertEquals("Nombre", usuario.getNombre());
        assertEquals("Paterno", usuario.getApellidoPaterno());
        assertEquals("Materno", usuario.getApellidoMaterno());
        assertEquals("Domicilio", usuario.getDomicilio());
        assertEquals("Telefono", usuario.getTelefono());
        assertEquals(0, usuario.getSanciones()); // Valor por defecto
        assertEquals(0, usuario.getMontoSancion()); // Valor por defecto
    }
    
    @Test
    void testTodosLosSettersDeUsuario() {
        // Given
        Usuario usuario = new Usuario();
        
        // When - Usar TODOS los setters
        usuario.setId(100);
        usuario.setNombre("Nuevo Nombre");
        usuario.setApellidoPaterno("Nuevo Paterno");
        usuario.setApellidoMaterno("Nuevo Materno");
        usuario.setDomicilio("Nuevo Domicilio");
        usuario.setTelefono("Nuevo Teléfono");
        usuario.setSanciones(5);
        usuario.setMontoSancion(100);
        
        // Then - Verificar que TODOS los setters funcionan
        assertEquals(100, usuario.getId());
        assertEquals("Nuevo Nombre", usuario.getNombre());
        assertEquals("Nuevo Paterno", usuario.getApellidoPaterno());
        assertEquals("Nuevo Materno", usuario.getApellidoMaterno());
        assertEquals("Nuevo Domicilio", usuario.getDomicilio());
        assertEquals("Nuevo Teléfono", usuario.getTelefono());
        assertEquals(5, usuario.getSanciones());
        assertEquals(100, usuario.getMontoSancion());
    }
    
    // 🔥 TESTS PARA CASOS DE ERROR (necesitan UsuarioDAO modificado)
    @Test
    void testActualizarUsuarioNoExistente() {
        // Given - Crear un usuario con ID que no existe en la BD
        Usuario usuarioNoExistente = new Usuario("No Existe", "Apellido", "Materno", "Dir", "Tel");
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
        // (aunque fallen por la conexión real, el constructor está cubierto)
        List<Usuario> usuarios = usuarioDAODefault.obtenerTodosLosUsuarios();
        assertNotNull(usuarios, "Debería retornar una lista (aunque esté vacía)");
    }
}