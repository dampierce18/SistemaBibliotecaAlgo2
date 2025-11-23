package sistemabiblioteca.dao;

import sistemabiblioteca.modelo.Empleado;
import sistemabiblioteca.test.ConexionTestDB;
import org.junit.jupiter.api.*;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.List;
import static org.junit.jupiter.api.Assertions.*;

@TestInstance(TestInstance.Lifecycle.PER_METHOD)
class EmpleadoDAOTest {
    private EmpleadoDAO empleadoDAO;
    private Connection testConnection;
    
    @BeforeEach
    void setUp() throws Exception {
        testConnection = ConexionTestDB.getTestConnection();
        empleadoDAO = new EmpleadoDAO(testConnection);
        
        // Limpiar datos antes de cada test
        try (var stmt = testConnection.createStatement()) {
            stmt.execute("DELETE FROM empleados WHERE id > 2"); // Mantener admin y empleado básico
        }
    }
    
    @AfterEach
    void tearDown() throws Exception {
        if (testConnection != null && !testConnection.isClosed()) {
            testConnection.close();
        }
    }
    
    @Test
    void testInsertarEmpleado() {
        // Given
        Empleado empleado = new Empleado("Carlos", "Martínez", "Rodríguez", 
                                        "carlos.martinez", "password123", "EMPLEADO",
                                        "555-0101", "carlos@biblioteca.com");
        
        // When
        boolean resultado = empleadoDAO.insertarEmpleado(empleado);
        
        // Then
        assertTrue(resultado, "El empleado debería insertarse correctamente");
        
        List<Empleado> empleados = empleadoDAO.obtenerTodosLosEmpleados();
        assertEquals(3, empleados.size(), "Debería haber 3 empleados (admin + empleado test + nuevo)");
    }
    
    @Test
    void testObtenerEmpleadoPorId() {
        // Given - Insertar un empleado primero
        Empleado empleado = new Empleado("Ana", "García", "López", 
                                        "ana.garcia", "password456", "EMPLEADO",
                                        "555-0102", "ana@biblioteca.com");
        empleadoDAO.insertarEmpleado(empleado);
        
        List<Empleado> empleados = empleadoDAO.obtenerTodosLosEmpleados();
        Empleado empleadoInsertado = empleados.stream()
            .filter(e -> e.getUsuario().equals("ana.garcia"))
            .findFirst()
            .orElse(null);
        assertNotNull(empleadoInsertado, "Debería encontrar el empleado insertado");
        int idInsertado = empleadoInsertado.getId();
        
        // When
        Empleado empleadoObtenido = empleadoDAO.obtenerEmpleadoPorId(idInsertado);
        
        // Then
        assertNotNull(empleadoObtenido, "Debería encontrar el empleado por ID");
        assertEquals("Ana", empleadoObtenido.getNombre());
        assertEquals("García", empleadoObtenido.getApellidoPaterno());
        assertEquals("López", empleadoObtenido.getApellidoMaterno());
        assertEquals("ana.garcia", empleadoObtenido.getUsuario());
        assertEquals("EMPLEADO", empleadoObtenido.getRol());
        assertEquals("555-0102", empleadoObtenido.getTelefono());
        assertEquals("ana@biblioteca.com", empleadoObtenido.getEmail());
    }
    
    @Test
    void testObtenerEmpleadoPorIdNoExistente() {
        // When
        Empleado empleadoObtenido = empleadoDAO.obtenerEmpleadoPorId(999);
        
        // Then
        assertNull(empleadoObtenido, "Debería retornar null para ID no existente");
    }
    
    @Test
    void testObtenerTodosLosEmpleados() {
        // Given
        Empleado empleado1 = new Empleado("Empleado1", "Apellido1", "Materno1", 
                                         "user1", "pass1", "EMPLEADO", "111-1111", "user1@test.com");
        Empleado empleado2 = new Empleado("Empleado2", "Apellido2", "Materno2", 
                                         "user2", "pass2", "EMPLEADO", "222-2222", "user2@test.com");
        
        empleadoDAO.insertarEmpleado(empleado1);
        empleadoDAO.insertarEmpleado(empleado2);
        
        // When
        List<Empleado> empleados = empleadoDAO.obtenerTodosLosEmpleados();
        
        // Then
        assertTrue(empleados.size() >= 2, "Debería haber al menos 2 empleados");
        // Verificar orden por ID
        for (int i = 1; i < empleados.size(); i++) {
            assertTrue(empleados.get(i).getId() > empleados.get(i-1).getId(), 
                      "Deberían estar ordenados por ID ascendente");
        }
    }
    
    
    @Test
    void testEliminarEmpleado() {
        // Given
        Empleado empleado = new Empleado("Empleado a Eliminar", "Apellido", "Materno", 
                                        "user.delete", "pass123", "EMPLEADO", 
                                        "111-1111", "delete@test.com");
        empleadoDAO.insertarEmpleado(empleado);
        
        List<Empleado> empleadosAntes = empleadoDAO.obtenerTodosLosEmpleados();
        Empleado empleadoAEliminar = empleadosAntes.stream()
            .filter(e -> e.getUsuario().equals("user.delete"))
            .findFirst()
            .orElse(null);
        assertNotNull(empleadoAEliminar, "Debería encontrar el empleado a eliminar");
        
        // When
        boolean resultado = empleadoDAO.eliminarEmpleado(empleadoAEliminar.getId());
        
        // Then
        assertTrue(resultado, "La eliminación debería ser exitosa");
        
        Empleado empleadoEliminado = empleadoDAO.obtenerEmpleadoPorId(empleadoAEliminar.getId());
        assertNull(empleadoEliminado, "El empleado debería estar eliminado");
    }
    
    @Test
    void testAutenticarCredencialesCorrectas() {
        // Given - Insertar un empleado para autenticar
        Empleado empleado = new Empleado("Usuario Test", "Apellido", "Materno", 
                                        "test.user", "test.password", "EMPLEADO",
                                        "555-1234", "test@biblioteca.com");
        empleadoDAO.insertarEmpleado(empleado);
        
        // When
        Empleado empleadoAutenticado = empleadoDAO.autenticar("test.user", "test.password");
        
        // Then
        assertNotNull(empleadoAutenticado, "Debería autenticar con credenciales correctas");
        assertEquals("test.user", empleadoAutenticado.getUsuario());
        assertEquals("EMPLEADO", empleadoAutenticado.getRol());
    }
    
    @Test
    void testAutenticarUsuarioIncorrecto() {
        // When
        Empleado empleadoAutenticado = empleadoDAO.autenticar("usuario.inexistente", "password");
        
        // Then
        assertNull(empleadoAutenticado, "Debería retornar null para usuario incorrecto");
    }
    
    @Test
    void testAutenticarPasswordIncorrecto() {
        // Given - Insertar un empleado
        Empleado empleado = new Empleado("Usuario", "Apellido", "Materno", 
                                        "usuario.test", "password.correcta", "EMPLEADO",
                                        "555-1234", "test@biblioteca.com");
        empleadoDAO.insertarEmpleado(empleado);
        
        // When
        Empleado empleadoAutenticado = empleadoDAO.autenticar("usuario.test", "password.incorrecta");
        
        // Then
        assertNull(empleadoAutenticado, "Debería retornar null para contraseña incorrecta");
    }
    
    @Test
    void testExisteUsuario() {
        // Given - Insertar un empleado
        Empleado empleado = new Empleado("Usuario", "Apellido", "Materno", 
                                        "usuario.existente", "password", "EMPLEADO",
                                        "555-1234", "test@biblioteca.com");
        empleadoDAO.insertarEmpleado(empleado);
        
        // When & Then
        assertTrue(empleadoDAO.existeUsuario("usuario.existente"), 
                  "Debería encontrar usuario existente");
        assertFalse(empleadoDAO.existeUsuario("usuario.inexistente"), 
                   "No debería encontrar usuario inexistente");
    }
    
    @Test
    void testContarTotalEmpleados() {
        // Given - Contar empleados iniciales
        int totalInicial = empleadoDAO.contarTotalEmpleados();
        
        // Insertar nuevos empleados
        Empleado empleado1 = new Empleado("Emp1", "Apellido1", "Materno1", 
                                         "user1", "pass1", "EMPLEADO", "111-1111", "user1@test.com");
        Empleado empleado2 = new Empleado("Emp2", "Apellido2", "Materno2", 
                                         "user2", "pass2", "EMPLEADO", "222-2222", "user2@test.com");
        
        empleadoDAO.insertarEmpleado(empleado1);
        empleadoDAO.insertarEmpleado(empleado2);
        
        // When
        int totalFinal = empleadoDAO.contarTotalEmpleados();
        
        // Then
        assertEquals(totalInicial + 2, totalFinal, "Debería haber 2 empleados más");
    }
    
    @Test
    void testEmpleadoConCamposNulos() {
        // Given - Empleado con algunos campos nulos
        Empleado empleado = new Empleado("Solo", "Nombre", null, 
                                        "user.nulo", "pass123", "EMPLEADO",
                                        null, null);
        
        // When
        boolean resultado = empleadoDAO.insertarEmpleado(empleado);
        
        // Then
        assertTrue(resultado, "Debería insertar empleado con campos nulos");
        
        Empleado empleadoInsertado = empleadoDAO.autenticar("user.nulo", "pass123");
        assertNotNull(empleadoInsertado);
        assertNull(empleadoInsertado.getApellidoMaterno());
        assertNull(empleadoInsertado.getTelefono());
        assertNull(empleadoInsertado.getEmail());
    }
    
    @Test
    void testEmpleadoConRolADMIN() {
        // Given
        Empleado empleadoAdmin = new Empleado("Admin", "Sistema", "Principal", 
                                             "admin.test", "admin123", "ADMIN",
                                             "555-0000", "admin@test.com");
        
        // When
        boolean resultado = empleadoDAO.insertarEmpleado(empleadoAdmin);
        
        // Then
        assertTrue(resultado, "Debería insertar empleado con rol ADMIN");
        
        Empleado empleadoAutenticado = empleadoDAO.autenticar("admin.test", "admin123");
        assertNotNull(empleadoAutenticado);
        assertEquals("ADMIN", empleadoAutenticado.getRol());
    }
    
    @Test
    void testActualizarEmpleadoNoExistente() {
        // Given - Crear un empleado con ID que no existe
        Empleado empleadoNoExistente = new Empleado();
        empleadoNoExistente.setId(9999);
        empleadoNoExistente.setNombre("No Existe");
        empleadoNoExistente.setUsuario("no.existe");
        
        // When
        boolean resultado = empleadoDAO.actualizarEmpleado(empleadoNoExistente);
        
        // Then
        assertFalse(resultado, "Debería retornar false al actualizar empleado no existente");
    }
    
    @Test
    void testEliminarEmpleadoNoExistente() {
        // When
        boolean resultado = empleadoDAO.eliminarEmpleado(9999);
        
        // Then
        assertFalse(resultado, "Debería retornar false al eliminar empleado no existente");
    }
    
    @Test
    void testAutenticarCaseSensitive() {
        // Given - Insertar empleado con usuario en minúsculas
        Empleado empleado = new Empleado("Usuario", "Test", "Case", 
                                        "usuario.case", "Password123", "EMPLEADO",
                                        "555-1234", "case@test.com");
        empleadoDAO.insertarEmpleado(empleado);
        
        // When & Then - Debería ser case sensitive
        assertNotNull(empleadoDAO.autenticar("usuario.case", "Password123"), 
                     "Debería autenticar con mismo case");
        assertNull(empleadoDAO.autenticar("USUARIO.CASE", "Password123"), 
                  "No debería autenticar con diferente case en usuario");
        assertNull(empleadoDAO.autenticar("usuario.case", "PASSWORD123"), 
                  "No debería autenticar con diferente case en contraseña");
    }
    
    @Test
    void testConstructorPorDefecto() {
        // When
        EmpleadoDAO empleadoDAODefault = new EmpleadoDAO();
        
        // Then
        assertNotNull(empleadoDAODefault, "El constructor por defecto debería funcionar");
    }
    
    @Test
    void testInsertarEmpleadoConErrorSQL() throws SQLException {
        // Given - Cerrar la conexión para forzar un error
        testConnection.close();
        
        Empleado empleado = new Empleado("Test", "Error", "SQL", 
                                        "test.error", "pass123", "EMPLEADO",
                                        "555-1234", "error@test.com");
        
        // When
        boolean resultado = empleadoDAO.insertarEmpleado(empleado);
        
        // Then
        assertFalse(resultado, "Debería retornar false cuando hay error SQL");
    }
    
    @Test
    void testObtenerEmpleadoPorIdConErrorSQL() throws SQLException {
        // Given - Cerrar la conexión para forzar un error
        testConnection.close();
        
        // When
        Empleado empleado = empleadoDAO.obtenerEmpleadoPorId(1);
        
        // Then
        assertNull(empleado, "Debería retornar null cuando hay error SQL");
    }
    
    @Test
    void testAutenticarConErrorSQL() throws SQLException {
        // Given - Cerrar la conexión para forzar un error
        testConnection.close();
        
        // When
        Empleado empleado = empleadoDAO.autenticar("usuario", "password");
        
        // Then
        assertNull(empleado, "Debería retornar null cuando hay error SQL");
    }
    
    @Test
    void testCrearEmpleadoDesdeResultSet() throws SQLException {
        // Este test verifica indirectamente el método privado crearEmpleadoDesdeResultSet
        // Insertamos un empleado y verificamos que se mapea correctamente
        
        // Given
        Empleado empleadoOriginal = new Empleado("Test", "Mapping", "ResultSet", 
                                                "test.mapping", "pass123", "EMPLEADO",
                                                "555-8888", "mapping@test.com");
        empleadoDAO.insertarEmpleado(empleadoOriginal);
        
        // When
        Empleado empleadoMapeado = empleadoDAO.autenticar("test.mapping", "pass123");
        
        // Then
        assertNotNull(empleadoMapeado);
        assertEquals("Test", empleadoMapeado.getNombre());
        assertEquals("Mapping", empleadoMapeado.getApellidoPaterno());
        assertEquals("ResultSet", empleadoMapeado.getApellidoMaterno());
        assertEquals("test.mapping", empleadoMapeado.getUsuario());
        assertEquals("EMPLEADO", empleadoMapeado.getRol());
        assertEquals("555-8888", empleadoMapeado.getTelefono());
        assertEquals("mapping@test.com", empleadoMapeado.getEmail());
        assertTrue(empleadoMapeado.getId() > 0, "Debería tener un ID asignado");
    }
}