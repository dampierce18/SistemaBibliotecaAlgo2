package sistemabiblioteca.dao;

import sistemabiblioteca.modelo.Prestamo;
import sistemabiblioteca.test.ConexionTestDB;
import org.junit.jupiter.api.*;
import java.sql.Connection;
import java.time.LocalDate;
import java.util.List;
import static org.junit.jupiter.api.Assertions.*;

@TestInstance(TestInstance.Lifecycle.PER_METHOD)
class PrestamoDAOTest {
    private PrestamoDAO prestamoDAO;
    private Connection testConnection;
    
    @BeforeEach
    void setUp() throws Exception {
        testConnection = ConexionTestDB.getTestConnection();
        prestamoDAO = new PrestamoDAO(testConnection);
        
        // Limpiar datos antes de cada test
        try (var stmt = testConnection.createStatement()) {
            // ⚠️ IMPORTANTE: Eliminar en el orden correcto por las foreign keys
            stmt.execute("DELETE FROM prestamos");
            stmt.execute("DELETE FROM libros");
            stmt.execute("DELETE FROM usuarios");
            
            // ✅ Insertar datos de prueba NECESARIOS con IDs específicos
            stmt.execute("INSERT INTO usuarios (id, nombre, apellido_paterno, apellido_materno, domicilio, telefono, sanciones, monto_sancion, empleado_id) " +
                        "VALUES (1, 'Usuario Test', 'Apellido', 'Materno', 'Dirección', '123456', 0, 0, 1)");
            
            stmt.execute("INSERT INTO libros (id, titulo, anio, autor, categoria, editorial, total, disponibles, empleado_id) " +
                        "VALUES (1, 'Libro Test', '2023', 'Autor', 'Categoria', 'Editorial', 5, 5, 1)");
            
            stmt.execute("INSERT INTO libros (id, titulo, anio, autor, categoria, editorial, total, disponibles, empleado_id) " +
                        "VALUES (2, 'Libro Test 2', '2023', 'Autor', 'Categoria', 'Editorial', 3, 3, 2)");
        }
    }
    
    @Test
    void testRealizarPrestamoConEmpleadoId() {
        // Given
        LocalDate fechaPrestamo = LocalDate.now();
        LocalDate fechaDevolucion = fechaPrestamo.plusDays(14);
        Prestamo prestamo = new Prestamo(1, 1, 1, fechaPrestamo, fechaDevolucion);
        
        // When
        boolean resultado = prestamoDAO.realizarPrestamo(prestamo);
        
        // Then
        assertTrue(resultado, "El préstamo debería realizarse correctamente");
        
        List<Prestamo> prestamos = prestamoDAO.obtenerTodosLosPrestamos();
        assertEquals(1, prestamos.size(), "Debería haber exactamente 1 préstamo");
        assertTrue(prestamos.get(0).getId() > 0, "El préstamo debería tener un ID asignado");
        assertEquals(1, prestamos.get(0).getEmpleadoId(), "Debería tener el empleado_id correcto");
    }
    
    @Test
    void testRegistrarDevolucionPrestamoNoExistente() {
        // When - Intentar devolver un préstamo que no existe
        boolean resultado = prestamoDAO.registrarDevolucion(999);
        
        // Then
        assertFalse(resultado, "Debería retornar false para préstamo no existente");
    }
    
    
    
    
    @Test
    void testContarPrestamosAtrasadosSinAtrasados() {
        // Given - Solo préstamos con fechas futuras
        LocalDate hoy = LocalDate.now();
        
        Prestamo prestamo1 = new Prestamo(1, 1, 1, hoy.minusDays(5), hoy.plusDays(9));
        Prestamo prestamo2 = new Prestamo(2, 1, 2, hoy.minusDays(2), hoy.plusDays(12));
        
        prestamoDAO.realizarPrestamo(prestamo1);
        prestamoDAO.realizarPrestamo(prestamo2);
        
        // When
        int totalAtrasados = prestamoDAO.contarPrestamosAtrasados();
        
        // Then
        assertEquals(0, totalAtrasados, "No debería haber préstamos atrasados");
    }
    
    @Test
    void testConstructorPrestamoVacio() {
        // Given
        Prestamo prestamo = new Prestamo();
        
        // When - Establecer todos los valores
        prestamo.setId(1);
        prestamo.setLibroId(1);
        prestamo.setUsuarioId(1);
        prestamo.setEmpleadoId(2); // NUEVO: establecer empleado_id
        prestamo.setFechaPrestamo(LocalDate.now());
        prestamo.setFechaDevolucion(LocalDate.now().plusDays(14));
        prestamo.setFechaDevolucionReal(LocalDate.now());
        prestamo.setEstado("DEVUELTO");
        
        // Then - Verificar todos los getters
        assertEquals(1, prestamo.getId());
        assertEquals(1, prestamo.getLibroId());
        assertEquals(1, prestamo.getUsuarioId());
        assertEquals(2, prestamo.getEmpleadoId()); // NUEVO: verificar empleado_id
        assertNotNull(prestamo.getFechaPrestamo());
        assertNotNull(prestamo.getFechaDevolucion());
        assertNotNull(prestamo.getFechaDevolucionReal());
        assertEquals("DEVUELTO", prestamo.getEstado());
    }
    
    @Test
    void testConstructorPrestamoConParametros() {
        // Given
        LocalDate fechaPrestamo = LocalDate.now();
        LocalDate fechaDevolucion = fechaPrestamo.plusDays(14);
        
        // When
        Prestamo prestamo = new Prestamo(1, 1, 2, fechaPrestamo, fechaDevolucion);
        
        // Then
        assertEquals(1, prestamo.getLibroId());
        assertEquals(1, prestamo.getUsuarioId());
        assertEquals(2, prestamo.getEmpleadoId()); // NUEVO: verificar empleado_id
        assertEquals(fechaPrestamo, prestamo.getFechaPrestamo());
        assertEquals(fechaDevolucion, prestamo.getFechaDevolucion());
        assertEquals("ACTIVO", prestamo.getEstado());
        assertNull(prestamo.getFechaDevolucionReal()); // No establecido en constructor
    }
    
    @Test
    void testTodosLosSettersDePrestamo() {
        // Given
        Prestamo prestamo = new Prestamo();
        
        // When - Usar TODOS los setters
        prestamo.setId(100);
        prestamo.setLibroId(200);
        prestamo.setUsuarioId(300);
        prestamo.setEmpleadoId(400); // NUEVO: establecer empleado_id
        prestamo.setFechaPrestamo(LocalDate.of(2024, 1, 1));
        prestamo.setFechaDevolucion(LocalDate.of(2024, 1, 15));
        prestamo.setFechaDevolucionReal(LocalDate.of(2024, 1, 10));
        prestamo.setEstado("ATRASADO");
        
        // Then - Verificar TODOS los getters
        assertEquals(100, prestamo.getId());
        assertEquals(200, prestamo.getLibroId());
        assertEquals(300, prestamo.getUsuarioId());
        assertEquals(400, prestamo.getEmpleadoId()); // NUEVO: verificar empleado_id
        assertEquals(LocalDate.of(2024, 1, 1), prestamo.getFechaPrestamo());
        assertEquals(LocalDate.of(2024, 1, 15), prestamo.getFechaDevolucion());
        assertEquals(LocalDate.of(2024, 1, 10), prestamo.getFechaDevolucionReal());
        assertEquals("ATRASADO", prestamo.getEstado());
    }
    
    // 🔥 TESTS PARA CASOS DE ERROR
    @Test
    void testRealizarPrestamoConError() throws Exception {
        // Given - Cerrar conexión para forzar error
        testConnection.close();
        
        Prestamo prestamo = new Prestamo(1, 1, 1, LocalDate.now(), LocalDate.now().plusDays(14));
        
        // When
        boolean resultado = prestamoDAO.realizarPrestamo(prestamo);
        
        // Then
        assertFalse(resultado, "Debería retornar false cuando hay error SQL");
    }
    
    @Test
    void testObtenerPrestamosActivosConError() throws Exception {
        // Given - Cerrar conexión para forzar error
        testConnection.close();
        
        // When
        List<Prestamo> prestamos = prestamoDAO.obtenerPrestamosActivos();
        
        // Then
        assertNotNull(prestamos, "Debería retornar lista vacía (no null)");
        assertTrue(prestamos.isEmpty(), "La lista debería estar vacía cuando hay error");
    }
    
    @Test
    void testContarPrestamosActivosConError() throws Exception {
        // Given - Cerrar conexión para forzar error
        testConnection.close();
        
        // When
        int total = prestamoDAO.contarPrestamosActivos();
        
        // Then
        assertEquals(0, total, "Debería retornar 0 cuando hay error SQL");
    }
    
    @Test
    void testConstructorPorDefecto() {
        // When
        PrestamoDAO prestamoDAODefault = new PrestamoDAO();
        
        // Then
        assertNotNull(prestamoDAODefault, "El constructor por defecto debería funcionar");
    }
    

    @Test
    void testObtenerPrestamoPorIdNoExistente() {
        // When - Buscar un préstamo que no existe
        Prestamo prestamoObtenido = prestamoDAO.obtenerPrestamoPorId(9999);
        
        // Then
        assertNull(prestamoObtenido, "Debería retornar null para ID no existente");
    }

    @Test
    void testObtenerPrestamosPorUsuario() {
        // Given - Crear préstamos para el mismo usuario
        LocalDate hoy = LocalDate.now();
        
        Prestamo prestamo1 = new Prestamo(1, 1, 1, hoy.minusDays(10), hoy.minusDays(3));
        Prestamo prestamo2 = new Prestamo(2, 1, 2, hoy.minusDays(5), hoy.plusDays(9));
        Prestamo prestamo3 = new Prestamo(1, 2, 1, hoy.minusDays(2), hoy.plusDays(12)); // Usuario diferente
        
        boolean p1Creado = prestamoDAO.realizarPrestamo(prestamo1);
        boolean p2Creado = prestamoDAO.realizarPrestamo(prestamo2);
        boolean p3Creado = prestamoDAO.realizarPrestamo(prestamo3);
        
        assertTrue(p1Creado, "Primer préstamo debería crearse");
        assertTrue(p2Creado, "Segundo préstamo debería crearse");
        assertTrue(p3Creado, "Tercer préstamo debería crearse");
        
        // When - Obtener préstamos del usuario 1
        List<Prestamo> prestamosUsuario = prestamoDAO.obtenerPrestamosPorUsuario(1);
        
        // Then
        assertEquals(2, prestamosUsuario.size(), "Debería encontrar 2 préstamos del usuario 1");
        assertTrue(prestamosUsuario.stream().allMatch(p -> p.getUsuarioId() == 1));
        
        // Verificar orden descendente por fecha de préstamo
        assertTrue(prestamosUsuario.get(0).getFechaPrestamo().isAfter(prestamosUsuario.get(1).getFechaPrestamo()) || 
                  prestamosUsuario.get(0).getFechaPrestamo().isEqual(prestamosUsuario.get(1).getFechaPrestamo()));
        
        // Verificar que tienen empleado_id
        assertTrue(prestamosUsuario.stream().allMatch(p -> p.getEmpleadoId() > 0));
    }

    @Test
    void testObtenerPrestamosPorUsuarioSinPrestamos() {
        // Given - No crear préstamos para el usuario 999
        
        // When
        List<Prestamo> prestamosUsuario = prestamoDAO.obtenerPrestamosPorUsuario(999);
        
        // Then
        assertNotNull(prestamosUsuario, "Debería retornar lista (no null)");
        assertTrue(prestamosUsuario.isEmpty(), "La lista debería estar vacía para usuario sin préstamos");
    }

    @Test
    void testObtenerPrestamosPorLibro() {
        // Given - Crear préstamos para el mismo libro
        LocalDate hoy = LocalDate.now();
        
        Prestamo prestamo1 = new Prestamo(1, 1, 1, hoy.minusDays(10), hoy.minusDays(3));
        Prestamo prestamo2 = new Prestamo(1, 2, 2, hoy.minusDays(5), hoy.plusDays(9));
        Prestamo prestamo3 = new Prestamo(2, 1, 1, hoy.minusDays(2), hoy.plusDays(12)); // Libro diferente
        
        boolean p1Creado = prestamoDAO.realizarPrestamo(prestamo1);
        boolean p2Creado = prestamoDAO.realizarPrestamo(prestamo2);
        boolean p3Creado = prestamoDAO.realizarPrestamo(prestamo3);
        
        assertTrue(p1Creado, "Primer préstamo debería crearse");
        assertTrue(p2Creado, "Segundo préstamo debería crearse");
        assertTrue(p3Creado, "Tercer préstamo debería crearse");
        
        // When - Obtener préstamos del libro 1
        List<Prestamo> prestamosLibro = prestamoDAO.obtenerPrestamosPorLibro(1);
        
        // Then
        assertEquals(2, prestamosLibro.size(), "Debería encontrar 2 préstamos del libro 1");
        assertTrue(prestamosLibro.stream().allMatch(p -> p.getLibroId() == 1));
        
        // Verificar orden descendente por fecha de préstamo
        assertTrue(prestamosLibro.get(0).getFechaPrestamo().isAfter(prestamosLibro.get(1).getFechaPrestamo()) || 
                  prestamosLibro.get(0).getFechaPrestamo().isEqual(prestamosLibro.get(1).getFechaPrestamo()));
        
        // Verificar que tienen empleado_id
        assertTrue(prestamosLibro.stream().allMatch(p -> p.getEmpleadoId() > 0));
    }

    @Test
    void testObtenerPrestamosPorLibroSinPrestamos() {
        // Given - No crear préstamos para el libro 999
        
        // When
        List<Prestamo> prestamosLibro = prestamoDAO.obtenerPrestamosPorLibro(999);
        
        // Then
        assertNotNull(prestamosLibro, "Debería retornar lista (no null)");
        assertTrue(prestamosLibro.isEmpty(), "La lista debería estar vacía para libro sin préstamos");
    }

    @Test
    void testObtenerPrestamoPorIdConError() throws Exception {
        // Given - Cerrar conexión para forzar error
        testConnection.close();
        
        // When
        Prestamo prestamo = prestamoDAO.obtenerPrestamoPorId(1);
        
        // Then
        assertNull(prestamo, "Debería retornar null cuando hay error SQL");
    }

    @Test
    void testObtenerPrestamosPorUsuarioConError() throws Exception {
        // Given - Cerrar conexión para forzar error
        testConnection.close();
        
        // When
        List<Prestamo> prestamos = prestamoDAO.obtenerPrestamosPorUsuario(1);
        
        // Then
        assertNotNull(prestamos, "Debería retornar lista vacía (no null)");
        assertTrue(prestamos.isEmpty(), "La lista debería estar vacía cuando hay error");
    }

    @Test
    void testObtenerPrestamosPorLibroConError() throws Exception {
        // Given - Cerrar conexión para forzar error
        testConnection.close();
        
        // When
        List<Prestamo> prestamos = prestamoDAO.obtenerPrestamosPorLibro(1);
        
        // Then
        assertNotNull(prestamos, "Debería retornar lista vacía (no null)");
        assertTrue(prestamos.isEmpty(), "La lista debería estar vacía cuando hay error");
    }
    
    
    @Test
    void testPrestamoConEmpleadoIdCero() {
        // Given - Empleado_id = 0 (caso límite)
        LocalDate hoy = LocalDate.now();
        Prestamo prestamo = new Prestamo(1, 1, 0, hoy, hoy.plusDays(14));
        
        // When
        boolean resultado = prestamoDAO.realizarPrestamo(prestamo);
        
        // Then
        assertTrue(resultado, "Debería insertarse incluso con empleado_id = 0");
        
        List<Prestamo> prestamos = prestamoDAO.obtenerTodosLosPrestamos();
        assertEquals(0, prestamos.get(0).getEmpleadoId());
    }
    
    @Test
    void testActualizarEmpleadoIdEnPrestamo() {
        // Given - Crear un préstamo
        LocalDate hoy = LocalDate.now();
        Prestamo prestamo = new Prestamo(1, 1, 1, hoy, hoy.plusDays(14));
        prestamoDAO.realizarPrestamo(prestamo);
        
        // Obtener el préstamo
        List<Prestamo> prestamos = prestamoDAO.obtenerTodosLosPrestamos();
        Prestamo prestamoObtenido = prestamos.get(0);
        
        // When - Actualizar empleado_id
        prestamoObtenido.setEmpleadoId(2);
        // Nota: En una implementación real, necesitarías un método para actualizar el préstamo
        
        // Then - Verificar que el setter funciona
        assertEquals(2, prestamoObtenido.getEmpleadoId());
    }
}