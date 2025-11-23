package sistemabiblioteca.dao;

import sistemabiblioteca.modelo.Libro;
import sistemabiblioteca.test.ConexionTestDB;
import org.junit.jupiter.api.*;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.List;
import static org.junit.jupiter.api.Assertions.*;

@TestInstance(TestInstance.Lifecycle.PER_METHOD)
class LibroDAOTest {
    private LibroDAO libroDAO;
    private Connection testConnection;
    
    @BeforeEach
    void setUp() throws Exception {
        testConnection = ConexionTestDB.getTestConnection();
        libroDAO = new LibroDAO(testConnection);
        
        // Limpiar datos específicos antes de cada test (manteniendo estructura básica)
        try (var stmt = testConnection.createStatement()) {
            stmt.execute("DELETE FROM prestamos");
            stmt.execute("DELETE FROM libros");
            stmt.execute("DELETE FROM usuarios WHERE id > 2"); // Mantener usuarios básicos
            // Los empleados básicos (id 1 y 2) se mantienen
        }
    }
    
    @AfterEach
    void tearDown() throws Exception {
        if (testConnection != null && !testConnection.isClosed()) {
            testConnection.close();
        }
    }
    
    // ✅ TESTS BÁSICOS ACTUALIZADOS
    
    @Test
    void testInsertarLibroConEmpleadoId() {
        // Given
        Libro libro = new Libro(0, "Libro de Prueba", "2023", 
                               "Autor Prueba", "Categoria", "Editorial", 1, 1, 1);
        
        // When
        boolean resultado = libroDAO.insertarLibro(libro);
        
        // Then
        assertTrue(resultado, "El libro debería insertarse correctamente");
        
        List<Libro> libros = libroDAO.obtenerTodosLosLibros();
        assertEquals(1, libros.size(), "Debería haber exactamente 1 libro");
        assertEquals("Libro de Prueba", libros.get(0).getTitulo());
        assertEquals(1, libros.get(0).getEmpleadoId());
    }
    
    @Test
    void testObtenerLibroPorIdNoExistente() {
        // When
        Libro libroObtenido = libroDAO.obtenerLibroPorId(999);
        
        // Then
        assertNull(libroObtenido, "Debería retornar null para ID no existente");
    }
    
    
    
    @Test
    void testBuscarLibrosPorTitulo() {
        // Given
        Libro libro1 = new Libro(0, "El principito", "1943", "Antoine de Saint-Exupéry", 
                                "Infantil", "Editorial", 2, 2, 1);
        Libro libro2 = new Libro(0, "El señor de los anillos", "1954", "J.R.R. Tolkien", 
                                "Fantasía", "Editorial", 3, 3, 2);
        
        libroDAO.insertarLibro(libro1);
        libroDAO.insertarLibro(libro2);
        
        // When
        List<Libro> resultados = libroDAO.buscarLibros("Título", "principito");
        
        // Then
        assertEquals(1, resultados.size(), "Debería encontrar 1 libro");
        assertEquals("El principito", resultados.get(0).getTitulo());
        assertEquals(1, resultados.get(0).getEmpleadoId());
    }
    
    @Test
    void testBuscarLibrosPorAutor() {
        // Given
        Libro libro1 = new Libro(0, "Cien años de soledad", "1967", "Gabriel García Márquez", 
                                "Realismo Mágico", "Editorial", 2, 2, 1);
        Libro libro2 = new Libro(0, "El amor en los tiempos del cólera", "1985", "Gabriel García Márquez", 
                                "Novela", "Editorial", 1, 1, 2);
        
        libroDAO.insertarLibro(libro1);
        libroDAO.insertarLibro(libro2);
        
        // When
        List<Libro> resultados = libroDAO.buscarLibros("Autor", "García");
        
        // Then
        assertEquals(2, resultados.size(), "Debería encontrar 2 libros del mismo autor");
        assertTrue(resultados.stream().allMatch(l -> l.getAutor().contains("García")));
    }
    
    @Test
    void testContarTotalLibros() {
        // Given
        assertEquals(0, libroDAO.contarTotalLibros(), "Debería haber 0 libros inicialmente");
        
        Libro libro1 = new Libro(0, "Libro 1", "2000", "Autor 1", 
                                "Categoria", "Editorial", 1, 1, 1);
        Libro libro2 = new Libro(0, "Libro 2", "2010", "Autor 2", 
                                "Categoria", "Editorial", 1, 1, 2);
        
        libroDAO.insertarLibro(libro1);
        libroDAO.insertarLibro(libro2);
        
        // When
        int total = libroDAO.contarTotalLibros();
        
        // Then
        assertEquals(2, total, "Debería haber 2 libros en total");
    }
    
    // 🔥 NUEVOS TESTS PARA FUNCIONALIDADES ACTUALIZADAS
    
    
    @Test
    void testContarPrestamosActivosSinPrestamos() {
        // Given
        Libro libro = new Libro(0, "Libro Sin Préstamos", "2023", 
                               "Autor", "Categoria", "Editorial", 3, 3, 1);
        libroDAO.insertarLibro(libro);
        
        List<Libro> libros = libroDAO.obtenerTodosLosLibros();
        int libroId = libros.get(0).getId();
        
        // When
        int prestamosActivos = libroDAO.contarPrestamosActivos(libroId);
        
        // Then
        assertEquals(0, prestamosActivos, "Debería retornar 0 cuando no hay préstamos activos");
    }
    
    @Test
    void testContarPrestamosActivosLibroNoExistente() {
        // When
        int prestamosActivos = libroDAO.contarPrestamosActivos(999);
        
        // Then
        assertEquals(0, prestamosActivos, "Debería retornar 0 para libro no existente");
    }
    
    @Test
    void testObtenerTodosLosLibrosConOrdenamiento() {
        // Given
        Libro libro1 = new Libro(0, "C", "2020", "Autor C", "Categoria", "Editorial", 1, 1, 2);
        Libro libro2 = new Libro(0, "A", "2019", "Autor A", "Categoria", "Editorial", 1, 1, 1);
        Libro libro3 = new Libro(0, "B", "2021", "Autor B", "Categoria", "Editorial", 1, 1, 2);
        
        libroDAO.insertarLibro(libro1);
        libroDAO.insertarLibro(libro2);
        libroDAO.insertarLibro(libro3);
        
        // When - Ordenar por título
        List<Libro> librosOrdenados = libroDAO.obtenerTodosLosLibros("titulo");
        
        // Then
        assertEquals(3, librosOrdenados.size());
        assertEquals("A", librosOrdenados.get(0).getTitulo());
        assertEquals("B", librosOrdenados.get(1).getTitulo());
        assertEquals("C", librosOrdenados.get(2).getTitulo());
    }
    
    @Test
    void testObtenerTodosLosLibrosConOrdenamientoAutor() {
        // Given
        Libro libro1 = new Libro(0, "Libro 1", "2020", "Zorro", "Categoria", "Editorial", 1, 1, 1);
        Libro libro2 = new Libro(0, "Libro 2", "2019", "Alfa", "Categoria", "Editorial", 1, 1, 2);
        Libro libro3 = new Libro(0, "Libro 3", "2021", "Beta", "Categoria", "Editorial", 1, 1, 1);
        
        libroDAO.insertarLibro(libro1);
        libroDAO.insertarLibro(libro2);
        libroDAO.insertarLibro(libro3);
        
        // When - Ordenar por autor
        List<Libro> librosOrdenados = libroDAO.obtenerTodosLosLibros("autor");
        
        // Then
        assertEquals(3, librosOrdenados.size());
        assertEquals("Alfa", librosOrdenados.get(0).getAutor());
        assertEquals("Beta", librosOrdenados.get(1).getAutor());
        assertEquals("Zorro", librosOrdenados.get(2).getAutor());
    }
    
    @Test
    void testObtenerTodosLosLibrosConOrdenamientoEmpleadoId() {
        // Given
        Libro libro1 = new Libro(0, "Libro 1", "2020", "Autor 1", "Categoria", "Editorial", 1, 1, 2);
        Libro libro2 = new Libro(0, "Libro 2", "2019", "Autor 2", "Categoria", "Editorial", 1, 1, 1);
        Libro libro3 = new Libro(0, "Libro 3", "2021", "Autor 3", "Categoria", "Editorial", 1, 1, 2);
        
        libroDAO.insertarLibro(libro1);
        libroDAO.insertarLibro(libro2);
        libroDAO.insertarLibro(libro3);
        
        // When - Ordenar por empleado_id
        List<Libro> librosOrdenados = libroDAO.obtenerTodosLosLibros("empleado_id");
        
        // Then
        assertEquals(3, librosOrdenados.size());
        // Los libros deberían estar ordenados por empleado_id (1, 2, 2)
        assertEquals(1, librosOrdenados.get(0).getEmpleadoId());
        assertEquals(2, librosOrdenados.get(1).getEmpleadoId());
        assertEquals(2, librosOrdenados.get(2).getEmpleadoId());
    }
    @Test
    void testBuscarLibrosPorIDConStringInvalido() {
        // Given
        Libro libro = new Libro(0, "Libro Test", "2020", "Autor", "Categoria", "Editorial", 1, 1, 1);
        libroDAO.insertarLibro(libro);
        
        // When - Buscar por ID con string no numérico
        List<Libro> resultados = libroDAO.buscarLibros("ID", "no_es_un_numero");
        
        // Then
        assertNotNull(resultados);
        assertTrue(resultados.isEmpty(), "Debería retornar lista vacía para ID no numérico");
    }
    
    @Test
    void testBuscarLibrosPorCategoria() {
        // Given
        Libro libro1 = new Libro(0, "Libro Ciencia", "2020", "Autor 1", 
                                "Ciencia", "Editorial", 2, 2, 1);
        Libro libro2 = new Libro(0, "Libro Historia", "2019", "Autor 2", 
                                "Historia", "Editorial", 1, 1, 2);
        Libro libro3 = new Libro(0, "Otro de Ciencia", "2021", "Autor 3", 
                                "Ciencia", "Editorial", 3, 3, 1);
        
        libroDAO.insertarLibro(libro1);
        libroDAO.insertarLibro(libro2);
        libroDAO.insertarLibro(libro3);
        
        // When
        List<Libro> resultados = libroDAO.buscarLibros("Categoría", "Ciencia");
        
        // Then
        assertEquals(2, resultados.size(), "Debería encontrar 2 libros de ciencia");
        assertTrue(resultados.stream().allMatch(l -> l.getCategoria().contains("Ciencia")));
    }
    
    @Test
    void testBuscarLibrosPorEditorial() {
        // Given
        Libro libro1 = new Libro(0, "Libro 1", "2020", "Autor 1", 
                                "Categoria", "Penguin", 2, 2, 1);
        Libro libro2 = new Libro(0, "Libro 2", "2019", "Autor 2", 
                                "Categoria", "Random House", 1, 1, 2);
        Libro libro3 = new Libro(0, "Libro 3", "2021", "Autor 3", 
                                "Categoria", "Penguin", 3, 3, 1);
        
        libroDAO.insertarLibro(libro1);
        libroDAO.insertarLibro(libro2);
        libroDAO.insertarLibro(libro3);
        
        // When
        List<Libro> resultados = libroDAO.buscarLibros("Editorial", "Penguin");
        
        // Then
        assertEquals(2, resultados.size(), "Debería encontrar 2 libros de Penguin");
        assertTrue(resultados.stream().allMatch(l -> l.getEditorial().contains("Penguin")));
    }
    
    // 🔧 TESTS DE VALIDACIÓN Y SEGURIDAD
    
    @Test
    void testValidarOrdenConCriteriosPermitidos() {
        // Test indirecto de la validación de orden
        String[] criteriosValidos = {
            "id", "titulo", "autor", "categoria", "editorial", "anio", 
            "total", "disponibles", "disponibles DESC", "anio DESC", "empleado_id"
        };
        
        for (String criterio : criteriosValidos) {
            assertDoesNotThrow(() -> {
                List<Libro> libros = libroDAO.obtenerTodosLosLibros(criterio);
                assertNotNull(libros);
            }, "No debería lanzar excepción con criterio: " + criterio);
        }
    }
    
    @Test
    void testObtenerTodosLosLibrosConOrdenamientoInvalido() {
        // Given
        Libro libro1 = new Libro(0, "B", "2020", "Autor", "Categoria", "Editorial", 1, 1, 1);
        Libro libro2 = new Libro(0, "A", "2019", "Autor", "Categoria", "Editorial", 1, 1, 2);
        
        libroDAO.insertarLibro(libro1);
        libroDAO.insertarLibro(libro2);
        
        // When - Usar ordenamiento inválido (debería usar orden por defecto: id)
        List<Libro> libros = libroDAO.obtenerTodosLosLibros("orden_invalido; DROP TABLE libros");
        
        // Then - Debería usar orden por defecto sin vulnerabilidad SQL
        assertEquals(2, libros.size());
        // Verificar que los datos siguen intactos
        assertDoesNotThrow(() -> libroDAO.obtenerTodosLosLibros());
    }
    
    // 🚨 TESTS DE ERROR Y CASOS ESPECIALES
    
    @Test
    void testInsertarLibroConErrorSQL() throws SQLException {
        // Given - Cerrar la conexión para forzar un error
        testConnection.close();
        
        Libro libro = new Libro(0, "Libro Test", "2023", "Autor", "Categoria", "Editorial", 1, 1, 1);
        
        // When
        boolean resultado = libroDAO.insertarLibro(libro);
        
        // Then
        assertFalse(resultado, "Debería retornar false cuando hay error SQL");
    }
    
    @Test
    void testContarPrestamosActivosConErrorSQL() throws SQLException {
        // Given - Cerrar la conexión para forzar un error
        testConnection.close();
        
        // When
        int resultado = libroDAO.contarPrestamosActivos(1);
        
        // Then
        assertEquals(0, resultado, "Debería retornar 0 cuando hay error SQL");
    }
    
    @Test
    void testActualizarLibroConErrorSQL() throws SQLException {
        // Given - Cerrar la conexión para forzar un error
        testConnection.close();
        
        Libro libro = new Libro(1, "Título", "2020", "Autor", "Categoria", "Editorial", 1, 1, 1);
        
        // When
        boolean resultado = libroDAO.actualizarLibro(libro);
        
        // Then
        assertFalse(resultado, "Debería retornar false cuando hay error SQL");
    }
    
    @Test
    void testEliminarLibroConErrorSQL() throws SQLException {
        // Given - Cerrar la conexión para forzar un error
        testConnection.close();
        
        // When
        boolean resultado = libroDAO.eliminarLibro(1);
        
        // Then
        assertFalse(resultado, "Debería retornar false cuando hay error SQL");
    }
    
    @Test
    void testBuscarLibrosConErrorSQL() throws SQLException {
        // Given - Cerrar la conexión para forzar un error
        testConnection.close();
        
        // When
        List<Libro> resultados = libroDAO.buscarLibros("Título", "test");
        
        // Then
        assertNotNull(resultados, "Debería retornar lista vacía (no null)");
        assertTrue(resultados.isEmpty(), "La lista debería estar vacía cuando hay error");
    }
    
    @Test
    void testContarLibrosConErrorSQL() throws SQLException {
        // Given - Cerrar la conexión para forzar un error
        testConnection.close();
        
        // When
        int total = libroDAO.contarTotalLibros();
        
        // Then
        assertEquals(0, total, "Debería retornar 0 cuando hay error SQL");
    }
    
    @Test
    void testObtenerLibroPorIdConErrorSQL() throws SQLException {
        // Given - Cerrar la conexión para forzar un error
        testConnection.close();
        
        // When
        Libro libro = libroDAO.obtenerLibroPorId(1);
        
        // Then
        assertNull(libro, "Debería retornar null cuando hay error SQL");
    }
    
    // 🧪 TESTS DEL MODELO LIBRO
    
    @Test
    void testLibroConstructorCompleto() {
        // Given - Parámetros completos del constructor
        int id = 1;
        String titulo = "Título Test";
        String anio = "2023";
        String autor = "Autor Test";
        String categoria = "Categoría Test";
        String editorial = "Editorial Test";
        int total = 5;
        int disponibles = 3;
        int empleadoId = 2;
        
        // When
        Libro libro = new Libro(id, titulo, anio, autor, categoria, editorial, total, disponibles, empleadoId);
        
        // Then - Verificar que todos los campos se asignan correctamente
        assertEquals(id, libro.getId());
        assertEquals(titulo, libro.getTitulo());
        assertEquals(anio, libro.getAnio());
        assertEquals(autor, libro.getAutor());
        assertEquals(categoria, libro.getCategoria());
        assertEquals(editorial, libro.getEditorial());
        assertEquals(total, libro.getTotal());
        assertEquals(disponibles, libro.getDisponibles());
        assertEquals(empleadoId, libro.getEmpleadoId());
    }
    
    @Test
    void testLibroSettersCompletos() {
        // Given
        Libro libro = new Libro(1, "Título", "2020", "Autor", 
                               "Categoria", "Editorial", 5, 3, 1);
        
        // When - Usar todos los setters
        libro.setTitulo("Nuevo Título");
        libro.setAutor("Nuevo Autor");
        libro.setAnio("2023");
        libro.setCategoria("Nueva Categoria");
        libro.setEditorial("Nueva Editorial");
        libro.setTotal(10);
        libro.setDisponibles(7);
        libro.setEmpleadoId(2);
        
        // Then - Verificar que todos los setters funcionan
        assertEquals("Nuevo Título", libro.getTitulo());
        assertEquals("Nuevo Autor", libro.getAutor());
        assertEquals("2023", libro.getAnio());
        assertEquals("Nueva Categoria", libro.getCategoria());
        assertEquals("Nueva Editorial", libro.getEditorial());
        assertEquals(10, libro.getTotal());
        assertEquals(7, libro.getDisponibles());
        assertEquals(2, libro.getEmpleadoId());
    }
    
    @Test
    void testLibroConEmpleadoIdCero() {
        // Given - Empleado_id = 0 (caso límite)
        Libro libro = new Libro(0, "Libro Sin Empleado", "2023", 
                               "Autor", "Categoria", "Editorial", 1, 1, 0);
        
        // When
        boolean resultado = libroDAO.insertarLibro(libro);
        
        // Then
        assertTrue(resultado, "Debería insertarse incluso con empleado_id = 0");
        
        List<Libro> libros = libroDAO.obtenerTodosLosLibros();
        assertEquals(0, libros.get(0).getEmpleadoId());
    }
    
    @Test
    void testResultSetALibroConEmpleadoId() {
        // Given - Insertar libro con empleado_id específico
        Libro libroOriginal = new Libro(0, "Título Completo", "2023", 
                                       "Autor Completo", "Categoría Completa", 
                                       "Editorial Completa", 10, 5, 2);
        libroDAO.insertarLibro(libroOriginal);
        
        // When - Obtener el libro insertado
        List<Libro> libros = libroDAO.obtenerTodosLosLibros();
        Libro libroObtenido = libros.get(0);
        
        // Then - Verificar que empleado_id se mapea correctamente desde la BD
        assertNotNull(libroObtenido);
        assertEquals("Título Completo", libroObtenido.getTitulo());
        assertEquals("Autor Completo", libroObtenido.getAutor());
        assertEquals("2023", libroObtenido.getAnio());
        assertEquals("Categoría Completa", libroObtenido.getCategoria());
        assertEquals("Editorial Completa", libroObtenido.getEditorial());
        assertEquals(10, libroObtenido.getTotal());
        assertEquals(5, libroObtenido.getDisponibles());
        assertEquals(2, libroObtenido.getEmpleadoId());
    }
    
    @Test
    void testConstructorPorDefecto() {
        // When - Crear DAO con constructor por defecto
        LibroDAO daoConexionNormal = new LibroDAO();
        
        // Then - Verificar que no lance excepción
        assertNotNull(daoConexionNormal, "Debería crearse correctamente");
    }
    
}