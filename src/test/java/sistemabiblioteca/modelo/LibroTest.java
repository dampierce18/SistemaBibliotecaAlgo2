package sistemabiblioteca.modelo;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.BeforeEach;
import static org.junit.jupiter.api.Assertions.*;

class LibroTest {
    private Libro libro;
    
    @BeforeEach
    void setUp() {
        // Crear un libro de prueba antes de cada test con empleadoId
        libro = new Libro(1, "Cien Años de Soledad", "1967", 
                         "Gabriel García Márquez", "Realismo Mágico", 
                         "Sudamericana", 5, 3, 1);
    }
    
    @Test
    void testConstructorConParametros() {
        // Verificar que el constructor asigna correctamente todos los valores incluyendo empleadoId
        assertEquals(1, libro.getId());
        assertEquals("Cien Años de Soledad", libro.getTitulo());
        assertEquals("1967", libro.getAnio());
        assertEquals("Gabriel García Márquez", libro.getAutor());
        assertEquals("Realismo Mágico", libro.getCategoria());
        assertEquals("Sudamericana", libro.getEditorial());
        assertEquals(5, libro.getTotal());
        assertEquals(3, libro.getDisponibles());
        assertEquals(1, libro.getEmpleadoId());
    }
    
    @Test
    void testConstructorConFechaComoAnio() {
        // Verificar que el parámetro "fecha" se asigna correctamente a "anio"
        Libro libroConFecha = new Libro(2, "1984", "1949", "George Orwell", 
                                       "Ciencia Ficción", "Secker & Warburg", 3, 2, 1);
        
        assertEquals("1949", libroConFecha.getAnio());
        assertEquals("1984", libroConFecha.getTitulo());
        assertEquals(1, libroConFecha.getEmpleadoId());
    }
    
    @Test
    void testSettersYTodosLosGetters() {
        // Probar todos los setters y verificar con getters incluyendo empleadoId
        libro.setTitulo("Nuevo Título");
        libro.setAnio("2023");
        libro.setAutor("Nuevo Autor");
        libro.setCategoria("Nueva Categoría");
        libro.setEditorial("Nueva Editorial");
        libro.setTotal(10);
        libro.setDisponibles(8);
        libro.setEmpleadoId(2);
        
        assertEquals("Nuevo Título", libro.getTitulo());
        assertEquals("2023", libro.getAnio());
        assertEquals("Nuevo Autor", libro.getAutor());
        assertEquals("Nueva Categoría", libro.getCategoria());
        assertEquals("Nueva Editorial", libro.getEditorial());
        assertEquals(10, libro.getTotal());
        assertEquals(8, libro.getDisponibles());
        assertEquals(2, libro.getEmpleadoId());
    }
    
    @Test
    void testSetterTitulo() {
        libro.setTitulo("El Principito");
        assertEquals("El Principito", libro.getTitulo());
    }
    
    @Test
    void testSetterTituloVacio() {
        libro.setTitulo("");
        assertEquals("", libro.getTitulo());
    }
    
    @Test
    void testSetterTituloNull() {
        libro.setTitulo(null);
        assertNull(libro.getTitulo());
    }
    
    @Test
    void testSetterAnio() {
        libro.setAnio("2024");
        assertEquals("2024", libro.getAnio());
    }
    
    @Test
    void testSetterAutor() {
        libro.setAutor("J.K. Rowling");
        assertEquals("J.K. Rowling", libro.getAutor());
    }
    
    @Test
    void testSetterCategoria() {
        libro.setCategoria("Fantasía");
        assertEquals("Fantasía", libro.getCategoria());
    }
    
    @Test
    void testSetterEditorial() {
        libro.setEditorial("Penguin Random House");
        assertEquals("Penguin Random House", libro.getEditorial());
    }
    
    @Test
    void testSetterTotal() {
        libro.setTotal(15);
        assertEquals(15, libro.getTotal());
    }
    
    @Test
    void testSetterTotalCero() {
        libro.setTotal(0);
        assertEquals(0, libro.getTotal());
    }
    
    @Test
    void testSetterTotalNegativo() {
        libro.setTotal(-5);
        assertEquals(-5, libro.getTotal());
    }
    
    @Test
    void testSetterDisponibles() {
        libro.setDisponibles(7);
        assertEquals(7, libro.getDisponibles());
    }
    
    @Test
    void testSetterDisponiblesCero() {
        libro.setDisponibles(0);
        assertEquals(0, libro.getDisponibles());
    }
    
    @Test
    void testSetterDisponiblesNegativo() {
        libro.setDisponibles(-2);
        assertEquals(-2, libro.getDisponibles());
    }
    
    @Test
    void testSetterEmpleadoId() {
        libro.setEmpleadoId(5);
        assertEquals(5, libro.getEmpleadoId());
    }
    
    @Test
    void testSetterEmpleadoIdCero() {
        libro.setEmpleadoId(0);
        assertEquals(0, libro.getEmpleadoId());
    }
    
    @Test
    void testSetterEmpleadoIdNegativo() {
        libro.setEmpleadoId(-1);
        assertEquals(-1, libro.getEmpleadoId());
    }
    
    @Test
    void testDisponiblesNoMayorQueTotal() {
        // Este test verifica el comportamiento cuando disponibles > total
        libro.setTotal(5);
        libro.setDisponibles(10); // Más que el total
        assertEquals(10, libro.getDisponibles());
        assertEquals(5, libro.getTotal());
    }
    
    @Test
    void testValoresInicialesConsistentes() {
        // Verificar que los valores iniciales son consistentes
        assertEquals(1, libro.getId());
        assertEquals("Cien Años de Soledad", libro.getTitulo());
        assertEquals("1967", libro.getAnio());
        assertEquals(1, libro.getEmpleadoId());
    }
    
    @Test
    void testLibroConTodosLosCamposVacios() {
        Libro libroVacio = new Libro(0, "", "", "", "", "", 0, 0, 0);
        
        assertEquals(0, libroVacio.getId());
        assertEquals("", libroVacio.getTitulo());
        assertEquals("", libroVacio.getAnio());
        assertEquals("", libroVacio.getAutor());
        assertEquals("", libroVacio.getCategoria());
        assertEquals("", libroVacio.getEditorial());
        assertEquals(0, libroVacio.getTotal());
        assertEquals(0, libroVacio.getDisponibles());
        assertEquals(0, libroVacio.getEmpleadoId());
    }
    
    @Test
    void testLibroConValoresExtremos() {
        Libro libroExtremo = new Libro(Integer.MAX_VALUE, "T".repeat(100), "9999", 
                                      "A".repeat(50), "C".repeat(30), "E".repeat(40), 
                                      Integer.MAX_VALUE, Integer.MAX_VALUE, Integer.MAX_VALUE);
        
        assertEquals(Integer.MAX_VALUE, libroExtremo.getId());
        assertEquals("T".repeat(100), libroExtremo.getTitulo());
        assertEquals("9999", libroExtremo.getAnio());
        assertEquals("A".repeat(50), libroExtremo.getAutor());
        assertEquals("C".repeat(30), libroExtremo.getCategoria());
        assertEquals("E".repeat(40), libroExtremo.getEditorial());
        assertEquals(Integer.MAX_VALUE, libroExtremo.getTotal());
        assertEquals(Integer.MAX_VALUE, libroExtremo.getDisponibles());
        assertEquals(Integer.MAX_VALUE, libroExtremo.getEmpleadoId());
    }
    
    @Test
    void testLibroConCaracteresEspeciales() {
        libro.setTitulo("¿Quién se ha llevado mi queso?");
        libro.setAutor("María José");
        libro.setCategoria("Auto-ayuda");
        libro.setEditorial("Editorial S.A.");
        libro.setEmpleadoId(3);
        
        assertEquals("¿Quién se ha llevado mi queso?", libro.getTitulo());
        assertEquals("María José", libro.getAutor());
        assertEquals("Auto-ayuda", libro.getCategoria());
        assertEquals("Editorial S.A.", libro.getEditorial());
        assertEquals(3, libro.getEmpleadoId());
    }
    
    @Test
    void testMultipleModificaciones() {
        // Probar múltiples modificaciones secuenciales
        libro.setTitulo("Título 1");
        assertEquals("Título 1", libro.getTitulo());
        
        libro.setTitulo("Título 2");
        assertEquals("Título 2", libro.getTitulo());
        
        libro.setTitulo("Título 3");
        assertEquals("Título 3", libro.getTitulo());
        
        libro.setTotal(1);
        libro.setTotal(2);
        libro.setTotal(3);
        assertEquals(3, libro.getTotal());
        
        libro.setEmpleadoId(1);
        libro.setEmpleadoId(2);
        libro.setEmpleadoId(3);
        assertEquals(3, libro.getEmpleadoId());
    }
    
    @Test
    void testInmutabilidadDelId() {
        // El ID no debería cambiar ya que no hay setId()
        int idOriginal = libro.getId();
        
        // Modificar otros campos
        libro.setTitulo("Nuevo Título");
        libro.setTotal(100);
        libro.setEmpleadoId(5);
        
        // El ID debería permanecer igual
        assertEquals(idOriginal, libro.getId());
    }
    
    @Test
    void testLibroConAnioNoNumerico() {
        libro.setAnio("MCMLXVII"); // Año en números romanos
        assertEquals("MCMLXVII", libro.getAnio());
        
        libro.setAnio("Año 2023");
        assertEquals("Año 2023", libro.getAnio());
    }
    
    @Test
    void testLibroConDiferentesEmpleados() {
        // Probar libros con diferentes empleadoId
        Libro libroEmpleado1 = new Libro(1, "Libro 1", "2020", "Autor 1", 
                                        "Categoria", "Editorial", 5, 3, 1);
        Libro libroEmpleado2 = new Libro(2, "Libro 2", "2021", "Autor 2", 
                                        "Categoria", "Editorial", 3, 2, 2);
        Libro libroEmpleado3 = new Libro(3, "Libro 3", "2022", "Autor 3", 
                                        "Categoria", "Editorial", 4, 1, 3);
        
        assertEquals(1, libroEmpleado1.getEmpleadoId());
        assertEquals(2, libroEmpleado2.getEmpleadoId());
        assertEquals(3, libroEmpleado3.getEmpleadoId());
    }
    
    @Test
    void testActualizacionEmpleadoId() {
        // Probar la actualización del empleadoId
        assertEquals(1, libro.getEmpleadoId());
        
        libro.setEmpleadoId(10);
        assertEquals(10, libro.getEmpleadoId());
        
        libro.setEmpleadoId(20);
        assertEquals(20, libro.getEmpleadoId());
    }
    
    @Test
    void testLibroConEmpleadoIdCero() {
        // Caso límite: empleadoId = 0
        Libro libroCero = new Libro(1, "Libro Test", "2023", "Autor", 
                                   "Categoria", "Editorial", 1, 1, 0);
        
        assertEquals(0, libroCero.getEmpleadoId());
    }
    
    @Test
    void testLibroConEmpleadoIdNegativo() {
        // Caso límite: empleadoId negativo
        Libro libroNegativo = new Libro(1, "Libro Test", "2023", "Autor", 
                                       "Categoria", "Editorial", 1, 1, -5);
        
        assertEquals(-5, libroNegativo.getEmpleadoId());
    }
    
    @Test
    void testConsistenciaDespuesDeModificaciones() {
        // Verificar que todos los campos mantienen su valor después de múltiples modificaciones
        int idOriginal = libro.getId();
        String tituloOriginal = libro.getTitulo();
        
        libro.setAnio("2025");
        libro.setAutor("Nuevo Autor");
        libro.setCategoria("Nueva Categoria");
        libro.setEditorial("Nueva Editorial");
        libro.setTotal(20);
        libro.setDisponibles(15);
        libro.setEmpleadoId(8);
        
        // Verificar que algunos campos cambiaron y otros no
        assertEquals(idOriginal, libro.getId());
        assertEquals(tituloOriginal, libro.getTitulo()); // No cambió
        assertEquals("2025", libro.getAnio()); // Cambió
        assertEquals("Nuevo Autor", libro.getAutor()); // Cambió
        assertEquals("Nueva Categoria", libro.getCategoria()); // Cambió
        assertEquals("Nueva Editorial", libro.getEditorial()); // Cambió
        assertEquals(20, libro.getTotal()); // Cambió
        assertEquals(15, libro.getDisponibles()); // Cambió
        assertEquals(8, libro.getEmpleadoId()); // Cambió
    }
    
    @Test
    void testTodosLosGettersFuncionan() {
        // Verificar que todos los getters devuelven valores correctos
        Libro libroCompleto = new Libro(100, "Título Completo", "1999", 
                                       "Autor Completo", "Categoría Completa", 
                                       "Editorial Completa", 10, 5, 7);
        
        assertEquals(100, libroCompleto.getId());
        assertEquals("Título Completo", libroCompleto.getTitulo());
        assertEquals("1999", libroCompleto.getAnio());
        assertEquals("Autor Completo", libroCompleto.getAutor());
        assertEquals("Categoría Completa", libroCompleto.getCategoria());
        assertEquals("Editorial Completa", libroCompleto.getEditorial());
        assertEquals(10, libroCompleto.getTotal());
        assertEquals(5, libroCompleto.getDisponibles());
        assertEquals(7, libroCompleto.getEmpleadoId());
    }
}