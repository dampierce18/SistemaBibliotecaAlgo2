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
            stmt.execute("INSERT INTO usuarios (id, nombre, apellido_paterno, apellido_materno, domicilio, telefono, sanciones, monto_sancion) " +
                        "VALUES (1, 'Usuario Test', 'Apellido', 'Materno', 'Dirección', '123456', 0, 0)");
            
            stmt.execute("INSERT INTO libros (id, titulo, anio, autor, categoria, editorial, total, disponibles) " +
                        "VALUES (1, 'Libro Test', '2023', 'Autor', 'Categoria', 'Editorial', 5, 5)");
            
            stmt.execute("INSERT INTO libros (id, titulo, anio, autor, categoria, editorial, total, disponibles) " +
                        "VALUES (2, 'Libro Test 2', '2023', 'Autor', 'Categoria', 'Editorial', 3, 3)");
        }
    }
    
    @Test
    void testRealizarPrestamo() {
        // Given
        LocalDate fechaPrestamo = LocalDate.now();
        LocalDate fechaDevolucion = fechaPrestamo.plusDays(14);
        Prestamo prestamo = new Prestamo(1, 1, fechaPrestamo, fechaDevolucion);
        
        // When
        boolean resultado = prestamoDAO.realizarPrestamo(prestamo);
        
        // Then
        assertTrue(resultado, "El préstamo debería realizarse correctamente");
        
        List<Prestamo> prestamos = prestamoDAO.obtenerTodosLosPrestamos();
        assertEquals(1, prestamos.size(), "Debería haber exactamente 1 préstamo");
        assertTrue(prestamos.get(0).getId() > 0, "El préstamo debería tener un ID asignado");
    }
    
    @Test
    void testRegistrarDevolucion() {
        // Given - Primero crear un préstamo y VERIFICAR que se insertó
        LocalDate fechaPrestamo = LocalDate.now().minusDays(5);
        LocalDate fechaDevolucion = LocalDate.now().plusDays(9);
        Prestamo prestamo = new Prestamo(1, 1, fechaPrestamo, fechaDevolucion);
        
        // Insertar el préstamo
        boolean prestamoCreado = prestamoDAO.realizarPrestamo(prestamo);
        assertTrue(prestamoCreado, "El préstamo debería crearse primero");
        
        // Obtener el ID del préstamo recién creado
        List<Prestamo> prestamos = prestamoDAO.obtenerTodosLosPrestamos();
        assertFalse(prestamos.isEmpty(), "Debería haber al menos un préstamo");
        
        int prestamoId = prestamos.get(0).getId();
        assertTrue(prestamoId > 0, "El préstamo debería tener un ID válido");
        
        // When - Registrar devolución
        boolean resultado = prestamoDAO.registrarDevolucion(prestamoId);
        
        // Then
        assertTrue(resultado, "La devolución debería registrarse correctamente");
        
        List<Prestamo> prestamosActualizados = prestamoDAO.obtenerTodosLosPrestamos();
        Prestamo prestamoDevuelto = prestamosActualizados.get(0);
        assertEquals("DEVUELTO", prestamoDevuelto.getEstado());
        assertNotNull(prestamoDevuelto.getFechaDevolucionReal());
    }
    
    @Test
    void testRegistrarDevolucionPrestamoNoExistente() {
        // When - Intentar devolver un préstamo que no existe
        boolean resultado = prestamoDAO.registrarDevolucion(999);
        
        // Then
        assertFalse(resultado, "Debería retornar false para préstamo no existente");
    }
    
    @Test
    void testObtenerPrestamosActivos() {
        // Given - Crear préstamos activos y devueltos
        LocalDate hoy = LocalDate.now();
        
        // Préstamo activo 1
        Prestamo prestamo1 = new Prestamo(1, 1, hoy.minusDays(5), hoy.plusDays(9));
        boolean prestamo1Creado = prestamoDAO.realizarPrestamo(prestamo1);
        assertTrue(prestamo1Creado, "Primer préstamo debería crearse");
        
        // Préstamo activo 2
        Prestamo prestamo2 = new Prestamo(2, 1, hoy.minusDays(2), hoy.plusDays(12));
        boolean prestamo2Creado = prestamoDAO.realizarPrestamo(prestamo2);
        assertTrue(prestamo2Creado, "Segundo préstamo debería crearse");
        
        // Verificar que hay préstamos antes de la devolución
        List<Prestamo> todos = prestamoDAO.obtenerTodosLosPrestamos();
        assertFalse(todos.isEmpty(), "Debería haber préstamos antes de la devolución");
        assertEquals(2, todos.size(), "Debería haber 2 préstamos inicialmente");
        
        // Registrar devolución de uno (no debería aparecer en activos)
        int prestamoIdADevolver = todos.get(0).getId();
        boolean devolucionExitosa = prestamoDAO.registrarDevolucion(prestamoIdADevolver);
        assertTrue(devolucionExitosa, "La devolución debería ser exitosa");
        
        // When
        List<Prestamo> prestamosActivos = prestamoDAO.obtenerPrestamosActivos();
        
        // Then
        assertEquals(1, prestamosActivos.size(), "Debería haber 1 préstamo activo");
        assertEquals("ACTIVO", prestamosActivos.get(0).getEstado());
        assertEquals(todos.get(1).getId(), prestamosActivos.get(0).getId(), "Debería ser el segundo préstamo el que queda activo");
    }
    
    @Test
    void testObtenerTodosLosPrestamos() {
        // Given
        LocalDate hoy = LocalDate.now();
        
        Prestamo prestamo1 = new Prestamo(1, 1, hoy.minusDays(10), hoy.minusDays(3));
        Prestamo prestamo2 = new Prestamo(2, 1, hoy.minusDays(5), hoy.plusDays(9));
        
        prestamoDAO.realizarPrestamo(prestamo1);
        prestamoDAO.realizarPrestamo(prestamo2);
        
        // When
        List<Prestamo> prestamos = prestamoDAO.obtenerTodosLosPrestamos();
        
        // Then
        assertEquals(2, prestamos.size(), "Debería haber 2 préstamos en total");
        // Verificar orden descendente por fecha de préstamo
        assertTrue(prestamos.get(0).getFechaPrestamo().isAfter(prestamos.get(1).getFechaPrestamo()) || 
                  prestamos.get(0).getFechaPrestamo().isEqual(prestamos.get(1).getFechaPrestamo()));
    }
    
    @Test
    void testContarPrestamosActivos() {
        // Given
        assertEquals(0, prestamoDAO.contarPrestamosActivos(), "Debería haber 0 préstamos activos inicialmente");
        
        LocalDate hoy = LocalDate.now();
        
        // Crear y VERIFICAR que los préstamos se insertan
        Prestamo prestamo1 = new Prestamo(1, 1, hoy.minusDays(5), hoy.plusDays(9));
        Prestamo prestamo2 = new Prestamo(2, 1, hoy.minusDays(2), hoy.plusDays(12));
        
        boolean prestamo1Creado = prestamoDAO.realizarPrestamo(prestamo1);
        boolean prestamo2Creado = prestamoDAO.realizarPrestamo(prestamo2);
        
        assertTrue(prestamo1Creado, "Primer préstamo debería crearse");
        assertTrue(prestamo2Creado, "Segundo préstamo debería crearse");
        
        // Registrar devolución de uno
        List<Prestamo> todos = prestamoDAO.obtenerTodosLosPrestamos();
        assertFalse(todos.isEmpty(), "Debería haber préstamos antes de la devolución");
        
        boolean devolucionExitosa = prestamoDAO.registrarDevolucion(todos.get(0).getId());
        assertTrue(devolucionExitosa, "La devolución debería ser exitosa");
        
        // When
        int totalActivos = prestamoDAO.contarPrestamosActivos();
        
        // Then
        assertEquals(1, totalActivos, "Debería haber 1 préstamo activo");
    }
    
    @Test
    void testContarPrestamosAtrasados() {
        // Given
        LocalDate hoy = LocalDate.now();
        
        // Préstamo atrasado (fecha de devolución en el pasado)
        Prestamo prestamoAtrasado = new Prestamo(1, 1, hoy.minusDays(20), hoy.minusDays(6));
        prestamoDAO.realizarPrestamo(prestamoAtrasado);
        
        // Préstamo no atrasado (fecha de devolución en el futuro)
        Prestamo prestamoNoAtrasado = new Prestamo(2, 1, hoy.minusDays(5), hoy.plusDays(9));
        prestamoDAO.realizarPrestamo(prestamoNoAtrasado);
        
        // When
        int totalAtrasados = prestamoDAO.contarPrestamosAtrasados();
        
        // Then
        assertEquals(1, totalAtrasados, "Debería haber 1 préstamo atrasado");
    }
    
    @Test
    void testContarPrestamosAtrasadosSinAtrasados() {
        // Given - Solo préstamos con fechas futuras
        LocalDate hoy = LocalDate.now();
        
        Prestamo prestamo1 = new Prestamo(1, 1, hoy.minusDays(5), hoy.plusDays(9));
        Prestamo prestamo2 = new Prestamo(2, 1, hoy.minusDays(2), hoy.plusDays(12));
        
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
        prestamo.setFechaPrestamo(LocalDate.now());
        prestamo.setFechaDevolucion(LocalDate.now().plusDays(14));
        prestamo.setFechaDevolucionReal(LocalDate.now());
        prestamo.setEstado("DEVUELTO");
        
        // Then - Verificar todos los getters
        assertEquals(1, prestamo.getId());
        assertEquals(1, prestamo.getLibroId());
        assertEquals(1, prestamo.getUsuarioId());
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
        Prestamo prestamo = new Prestamo(1, 1, fechaPrestamo, fechaDevolucion);
        
        // Then
        assertEquals(1, prestamo.getLibroId());
        assertEquals(1, prestamo.getUsuarioId());
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
        prestamo.setFechaPrestamo(LocalDate.of(2024, 1, 1));
        prestamo.setFechaDevolucion(LocalDate.of(2024, 1, 15));
        prestamo.setFechaDevolucionReal(LocalDate.of(2024, 1, 10));
        prestamo.setEstado("ATRASADO");
        
        // Then - Verificar TODOS los getters
        assertEquals(100, prestamo.getId());
        assertEquals(200, prestamo.getLibroId());
        assertEquals(300, prestamo.getUsuarioId());
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
        
        Prestamo prestamo = new Prestamo(1, 1, LocalDate.now(), LocalDate.now().plusDays(14));
        
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
    void testObtenerPrestamoPorId() {
        // Given - Crear un préstamo primero
        LocalDate fechaPrestamo = LocalDate.now();
        LocalDate fechaDevolucion = fechaPrestamo.plusDays(14);
        Prestamo prestamo = new Prestamo(1, 1, fechaPrestamo, fechaDevolucion);
        
        boolean prestamoCreado = prestamoDAO.realizarPrestamo(prestamo);
        assertTrue(prestamoCreado, "El préstamo debería crearse primero");
        
        // Obtener el ID del préstamo recién creado
        List<Prestamo> todosLosPrestamos = prestamoDAO.obtenerTodosLosPrestamos();
        assertFalse(todosLosPrestamos.isEmpty(), "Debería haber préstamos");
        int prestamoId = todosLosPrestamos.get(0).getId();
        
        // When
        Prestamo prestamoObtenido = prestamoDAO.obtenerPrestamoPorId(prestamoId);
        
        // Then
        assertNotNull(prestamoObtenido, "Debería encontrar el préstamo por ID");
        assertEquals(prestamoId, prestamoObtenido.getId());
        assertEquals(1, prestamoObtenido.getLibroId());
        assertEquals(1, prestamoObtenido.getUsuarioId());
        assertEquals("ACTIVO", prestamoObtenido.getEstado());
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
        
        Prestamo prestamo1 = new Prestamo(1, 1, hoy.minusDays(10), hoy.minusDays(3));
        Prestamo prestamo2 = new Prestamo(2, 1, hoy.minusDays(5), hoy.plusDays(9));
        Prestamo prestamo3 = new Prestamo(1, 2, hoy.minusDays(2), hoy.plusDays(12)); // Usuario diferente
        
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
        
        Prestamo prestamo1 = new Prestamo(1, 1, hoy.minusDays(10), hoy.minusDays(3));
        Prestamo prestamo2 = new Prestamo(1, 2, hoy.minusDays(5), hoy.plusDays(9));
        Prestamo prestamo3 = new Prestamo(2, 1, hoy.minusDays(2), hoy.plusDays(12)); // Libro diferente
        
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
}