package sistemabiblioteca.dao;

import sistemabiblioteca.test.ConexionTestDB;
import org.junit.jupiter.api.*;
import java.sql.Connection;
import java.util.List;
import java.util.Map;
import static org.junit.jupiter.api.Assertions.*;

@TestInstance(TestInstance.Lifecycle.PER_METHOD)
class ReporteDAOTest {
    private ReporteDAO reporteDAO;
    private Connection testConnection;
    
    @BeforeEach
    void setUp() throws Exception {
        testConnection = ConexionTestDB.getTestConnection();
        reporteDAO = new ReporteDAO(testConnection);
        
        // Limpiar y preparar datos de prueba
        try (var stmt = testConnection.createStatement()) {
            // Limpiar tablas
            stmt.execute("DELETE FROM prestamos");
            stmt.execute("DELETE FROM libros");
            stmt.execute("DELETE FROM usuarios");
            
            // Insertar datos de prueba para reportes
            // Usuarios
            stmt.execute("INSERT INTO usuarios (id, nombre, apellido_paterno, apellido_materno, domicilio, telefono, sanciones, monto_sancion) " +
                        "VALUES (1, 'Juan', 'Pérez', 'Gómez', 'Calle 123', '111-1111', 0, 0)");
            stmt.execute("INSERT INTO usuarios (id, nombre, apellido_paterno, apellido_materno, domicilio, telefono, sanciones, monto_sancion) " +
                        "VALUES (2, 'María', 'López', 'Santos', 'Av 456', '222-2222', 1, 50)");
            stmt.execute("INSERT INTO usuarios (id, nombre, apellido_paterno, apellido_materno, domicilio, telefono, sanciones, monto_sancion) " +
                        "VALUES (3, 'Carlos', 'García', 'Martínez', 'Calle 789', '333-3333', 0, 0)");
            
            // Libros
            stmt.execute("INSERT INTO libros (id, titulo, anio, autor, categoria, editorial, total, disponibles) " +
                        "VALUES (1, 'Cien Años de Soledad', '1967', 'Gabriel García Márquez', 'Ficción', 'Sudamericana', 5, 3)");
            stmt.execute("INSERT INTO libros (id, titulo, anio, autor, categoria, editorial, total, disponibles) " +
                        "VALUES (2, '1984', '1949', 'George Orwell', 'Ciencia Ficción', 'Secker & Warburg', 3, 0)");
            stmt.execute("INSERT INTO libros (id, titulo, anio, autor, categoria, editorial, total, disponibles) " +
                        "VALUES (3, 'El Principito', '1943', 'Antoine de Saint-Exupéry', 'Infantil', 'Gallimard', 2, 2)");
            
            // Préstamos (algunos del mes actual, otros de meses anteriores)
            stmt.execute("INSERT INTO prestamos (id, libro_id, usuario_id, fecha_prestamo, fecha_devolucion, fecha_devolucion_real, estado) " +
                        "VALUES (1, 1, 1, date('now'), date('now', '+14 days'), NULL, 'ACTIVO')");
            stmt.execute("INSERT INTO prestamos (id, libro_id, usuario_id, fecha_prestamo, fecha_devolucion, fecha_devolucion_real, estado) " +
                        "VALUES (2, 2, 2, date('now', '-5 days'), date('now', '-1 day'), NULL, 'ACTIVO')"); // Atrasado
            stmt.execute("INSERT INTO prestamos (id, libro_id, usuario_id, fecha_prestamo, fecha_devolucion, fecha_devolucion_real, estado) " +
                        "VALUES (3, 3, 3, date('now', '-1 month'), date('now', '-1 month', '+14 days'), date('now', '-1 month', '+10 days'), 'DEVUELTO')");
            stmt.execute("INSERT INTO prestamos (id, libro_id, usuario_id, fecha_prestamo, fecha_devolucion, fecha_devolucion_real, estado) " +
                        "VALUES (4, 1, 2, date('now'), date('now', '+10 days'), NULL, 'ACTIVO')");
        }
    }
    
    @Test
    void testObtenerResumenMes() {
        // When
        Map<String, Integer> resumen = reporteDAO.obtenerResumenMes();
        
        // Then
        assertNotNull(resumen, "El resumen no debería ser null");
        assertFalse(resumen.isEmpty(), "El resumen no debería estar vacío");
        
        // Verificar que contiene todas las claves esperadas
        assertTrue(resumen.containsKey("prestamos_mes"));
        assertTrue(resumen.containsKey("prestamos_activos"));
        assertTrue(resumen.containsKey("prestamos_atrasados"));
        assertTrue(resumen.containsKey("usuarios_sancionados"));
        assertTrue(resumen.containsKey("multas_pendientes"));
        
        // Verificar valores (dependen de los datos de prueba)
        assertTrue(resumen.get("prestamos_mes") >= 0);
        assertTrue(resumen.get("prestamos_activos") >= 0);
        assertTrue(resumen.get("prestamos_atrasados") >= 0);
        assertTrue(resumen.get("usuarios_sancionados") >= 0);
        assertTrue(resumen.get("multas_pendientes") >= 0);
    }
    
    @Test
    void testObtenerLibrosMasPrestados() {
        // When
        List<Object[]> librosMasPrestados = reporteDAO.obtenerLibrosMasPrestados();
        
        // Then
        assertNotNull(librosMasPrestados, "La lista no debería ser null");
        
        // Verificar estructura de cada fila
        if (!librosMasPrestados.isEmpty()) {
            Object[] primerLibro = librosMasPrestados.get(0);
            assertEquals(5, primerLibro.length, "Cada fila debería tener 5 columnas");
            
            // Verificar tipos de datos
            assertTrue(primerLibro[0] instanceof Integer, "Posición debería ser Integer");
            assertTrue(primerLibro[1] instanceof String, "Título debería ser String");
            assertTrue(primerLibro[2] instanceof String, "Autor debería ser String");
            assertTrue(primerLibro[3] instanceof Integer, "Total préstamos debería ser Integer");
            assertTrue(primerLibro[4] instanceof Integer, "Disponibles debería ser Integer");
        }
    }
    
    @Test
    void testObtenerUsuariosMasActivos() {
        // When
        List<Object[]> usuariosMasActivos = reporteDAO.obtenerUsuariosMasActivos();
        
        // Then
        assertNotNull(usuariosMasActivos, "La lista no debería ser null");
        
        // Verificar estructura de cada fila
        if (!usuariosMasActivos.isEmpty()) {
            Object[] primerUsuario = usuariosMasActivos.get(0);
            assertEquals(5, primerUsuario.length, "Cada fila debería tener 5 columnas");
            
            // Verificar tipos de datos
            assertTrue(primerUsuario[0] instanceof Integer, "Posición debería ser Integer");
            assertTrue(primerUsuario[1] instanceof String, "Nombre completo debería ser String");
            assertTrue(primerUsuario[2] instanceof Integer, "Total préstamos debería ser Integer");
            assertTrue(primerUsuario[3] instanceof Integer, "Atrasos debería ser Integer");
            assertTrue(primerUsuario[4] instanceof Integer, "Sanciones debería ser Integer");
        }
    }
    
    @Test
    void testObtenerPrestamosPorMes() {
        // When
        List<Object[]> prestamosPorMes = reporteDAO.obtenerPrestamosPorMes();
        
        // Then
        assertNotNull(prestamosPorMes, "La lista no debería ser null");
        
        // Verificar estructura de cada fila
        if (!prestamosPorMes.isEmpty()) {
            Object[] primerMes = prestamosPorMes.get(0);
            assertEquals(5, primerMes.length, "Cada fila debería tener 5 columnas");
            
            // Verificar tipos de datos
            assertTrue(primerMes[0] instanceof String, "Mes debería ser String");
            assertTrue(primerMes[1] instanceof Integer, "Total préstamos debería ser Integer");
            assertTrue(primerMes[2] instanceof Integer, "Préstamos activos debería ser Integer");
            assertTrue(primerMes[3] instanceof Integer, "Atrasos debería ser Integer");
            assertTrue(primerMes[4] instanceof String, "Tasa devolución debería ser String");
            
            // Verificar formato de tasa de devolución
            String tasaDevolucion = (String) primerMes[4];
            assertTrue(tasaDevolucion.endsWith("%"), "La tasa de devolución debería terminar con %");
        }
    }
    
    @Test
    void testObtenerSituacionActual() {
        // When
        List<Object[]> situacionActual = reporteDAO.obtenerSituacionActual();
        
        // Then
        assertNotNull(situacionActual, "La lista no debería ser null");
        assertFalse(situacionActual.isEmpty(), "Debería haber al menos una alerta o mensaje positivo");
        
        // Verificar estructura de cada fila
        Object[] primeraAlerta = situacionActual.get(0);
        assertEquals(5, primeraAlerta.length, "Cada fila debería tener 5 columnas");
        
        // Verificar tipos de datos
        assertTrue(primeraAlerta[0] instanceof String, "Tipo debería ser String");
        assertTrue(primeraAlerta[1] instanceof String, "Descripción debería ser String");
        assertTrue(primeraAlerta[2] instanceof Integer || primeraAlerta[2] instanceof String, 
                  "Cantidad debería ser Integer o String");
        assertTrue(primeraAlerta[3] instanceof String, "Estado debería ser String");
        assertTrue(primeraAlerta[4] instanceof String, "Acción debería ser String");
    }
    
    @Test
    void testObtenerResumenMesConDatosVacios() throws Exception {
        // Given - Base de datos vacía
        try (var stmt = testConnection.createStatement()) {
            stmt.execute("DELETE FROM prestamos");
            stmt.execute("DELETE FROM libros");
            stmt.execute("DELETE FROM usuarios");
        }
        
        // When
        Map<String, Integer> resumen = reporteDAO.obtenerResumenMes();
        
        // Then
        assertNotNull(resumen, "El resumen no debería ser null incluso con datos vacíos");
        assertFalse(resumen.isEmpty(), "El resumen debería contener las claves incluso con datos vacíos");
        
        // Verificar que todos los valores son 0 o positivos
        for (Integer valor : resumen.values()) {
            assertTrue(valor >= 0, "Los valores deberían ser >= 0");
        }
    }
    
    @Test
    void testObtenerLibrosMasPrestadosConDatosVacios() throws Exception {
        // Given - Base de datos vacía
        try (var stmt = testConnection.createStatement()) {
            stmt.execute("DELETE FROM prestamos");
            stmt.execute("DELETE FROM libros");
        }
        
        // Insertar solo libros sin préstamos
        try (var stmt = testConnection.createStatement()) {
            stmt.execute("INSERT INTO libros (id, titulo, anio, autor, categoria, editorial, total, disponibles) " +
                        "VALUES (1, 'Libro Test', '2023', 'Autor', 'Categoria', 'Editorial', 5, 5)");
        }
        
        // When
        List<Object[]> librosMasPrestados = reporteDAO.obtenerLibrosMasPrestados();
        
        // Then
        assertNotNull(librosMasPrestados, "La lista no debería ser null");
        // Puede estar vacía o contener libros con 0 préstamos
    }
    
    @Test
    void testObtenerSituacionActualSinAlertas() throws Exception {
        // Given - Base de datos sin problemas
        try (var stmt = testConnection.createStatement()) {
            stmt.execute("DELETE FROM prestamos");
            stmt.execute("UPDATE usuarios SET sanciones = 0, monto_sancion = 0");
            stmt.execute("UPDATE libros SET disponibles = total");
            
            // Insertar un préstamo que no esté atrasado
            stmt.execute("INSERT INTO prestamos (id, libro_id, usuario_id, fecha_prestamo, fecha_devolucion, estado) " +
                        "VALUES (1, 1, 1, date('now'), date('now', '+14 days'), 'ACTIVO')");
        }
        
        // When
        List<Object[]> situacionActual = reporteDAO.obtenerSituacionActual();
        
        // Then
        assertNotNull(situacionActual, "La lista no debería ser null");
        assertFalse(situacionActual.isEmpty(), "Debería mostrar el mensaje 'Todo en Orden'");
        
        Object[] mensaje = situacionActual.get(0);
        assertEquals("Todo en Orden", mensaje[0]);
        assertEquals("No hay alertas pendientes", mensaje[1]);
    }
    
    @Test
    void testConstructorPorDefecto() {
        // When
        ReporteDAO reporteDAODefault = new ReporteDAO();
        
        // Then
        assertNotNull(reporteDAODefault, "El constructor por defecto debería funcionar");
    }
    
    // 🔥 TESTS PARA CASOS DE ERROR
    @Test
    void testObtenerResumenMesConError() throws Exception {
        // Given - Cerrar conexión para forzar error
        testConnection.close();
        
        // When
        Map<String, Integer> resumen = reporteDAO.obtenerResumenMes();
        
        // Then
        assertNotNull(resumen, "Debería retornar mapa vacío (no null) cuando hay error");
        assertTrue(resumen.isEmpty(), "El mapa debería estar vacío cuando hay error");
    }
    
    @Test
    void testObtenerLibrosMasPrestadosConError() throws Exception {
        // Given - Cerrar conexión para forzar error
        testConnection.close();
        
        // When
        List<Object[]> resultados = reporteDAO.obtenerLibrosMasPrestados();
        
        // Then
        assertNotNull(resultados, "Debería retornar lista vacía (no null) cuando hay error");
        assertTrue(resultados.isEmpty(), "La lista debería estar vacía cuando hay error");
    }
    
    @Test
    void testObtenerUsuariosMasActivosConError() throws Exception {
        // Given - Cerrar conexión para forzar error
        testConnection.close();
        
        // When
        List<Object[]> resultados = reporteDAO.obtenerUsuariosMasActivos();
        
        // Then
        assertNotNull(resultados, "Debería retornar lista vacía (no null) cuando hay error");
        assertTrue(resultados.isEmpty(), "La lista debería estar vacía cuando hay error");
    }
    
    @Test
    void testObtenerPrestamosPorMesConError() throws Exception {
        // Given - Cerrar conexión para forzar error
        testConnection.close();
        
        // When
        List<Object[]> resultados = reporteDAO.obtenerPrestamosPorMes();
        
        // Then
        assertNotNull(resultados, "Debería retornar lista vacía (no null) cuando hay error");
        assertTrue(resultados.isEmpty(), "La lista debería estar vacía cuando hay error");
    }
    
    @Test
    void testObtenerSituacionActualConError() throws Exception {
        // Given - Cerrar conexión para forzar error
        testConnection.close();
        
        // When
        List<Object[]> resultados = reporteDAO.obtenerSituacionActual();
        
        // Then
        assertNotNull(resultados, "Debería retornar lista vacía (no null) cuando hay error");
        assertTrue(resultados.isEmpty(), "La lista debería estar vacía cuando hay error");
    }
}