package sistemabiblioteca.modelo;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import java.time.LocalDate;
import static org.junit.jupiter.api.Assertions.*;

class PrestamoTest {

    private Prestamo prestamo;
    private LocalDate fechaPrestamo;
    private LocalDate fechaDevolucion;

    @BeforeEach
    void setUp() {
        fechaPrestamo = LocalDate.of(2024, 1, 15);
        fechaDevolucion = LocalDate.of(2024, 1, 25);
        prestamo = new Prestamo(1, 100, fechaPrestamo, fechaDevolucion);
    }

    @Test
    @DisplayName("Debería crear un préstamo con estado ACTIVO por defecto")
    void testCrearPrestamoConEstadoActivo() {
        // Verificar que el préstamo se crea con estado ACTIVO
        assertEquals("ACTIVO", prestamo.getEstado());
        
        // Verificar los demás atributos
        assertEquals(1, prestamo.getLibroId());
        assertEquals(100, prestamo.getUsuarioId());
        assertEquals(fechaPrestamo, prestamo.getFechaPrestamo());
        assertEquals(fechaDevolucion, prestamo.getFechaDevolucion());
    }

    @Test
    @DisplayName("Debería actualizar correctamente todos los atributos del préstamo")
    void testSettersYGetters() {
        // Configurar valores
        prestamo.setId(50);
        prestamo.setLibroId(2);
        prestamo.setUsuarioId(200);
        prestamo.setFechaPrestamo(LocalDate.of(2024, 2, 1));
        prestamo.setFechaDevolucion(LocalDate.of(2024, 2, 15));
        prestamo.setFechaDevolucionReal(LocalDate.of(2024, 2, 10));
        prestamo.setEstado("DEVUELTO");

        // Verificar valores
        assertEquals(50, prestamo.getId());
        assertEquals(2, prestamo.getLibroId());
        assertEquals(200, prestamo.getUsuarioId());
        assertEquals(LocalDate.of(2024, 2, 1), prestamo.getFechaPrestamo());
        assertEquals(LocalDate.of(2024, 2, 15), prestamo.getFechaDevolucion());
        assertEquals(LocalDate.of(2024, 2, 10), prestamo.getFechaDevolucionReal());
        assertEquals("DEVUELTO", prestamo.getEstado());
    }

    @Test
    @DisplayName("Debería manejar correctamente el constructor vacío")
    void testConstructorVacio() {
        Prestamo prestamoVacio = new Prestamo();
        
        // Verificar que los valores por defecto son los esperados
        assertEquals(0, prestamoVacio.getId());
        assertEquals(0, prestamoVacio.getLibroId());
        assertEquals(0, prestamoVacio.getUsuarioId());
        assertNull(prestamoVacio.getFechaPrestamo());
        assertNull(prestamoVacio.getFechaDevolucion());
        assertNull(prestamoVacio.getFechaDevolucionReal());
        assertNull(prestamoVacio.getEstado());
    }

    @Test
    @DisplayName("Debería permitir cambiar el estado a diferentes valores válidos")
    void testCambiosDeEstado() {
        // Cambiar a DEVUELTO
        prestamo.setEstado("DEVUELTO");
        assertEquals("DEVUELTO", prestamo.getEstado());

        // Cambiar a ATRASADO
        prestamo.setEstado("ATRASADO");
        assertEquals("ATRASADO", prestamo.getEstado());

        // Cambiar de vuelta a ACTIVO
        prestamo.setEstado("ACTIVO");
        assertEquals("ACTIVO", prestamo.getEstado());
    }

    @Test
    @DisplayName("Debería manejar correctamente fechas de devolución real")
    void testFechasDevolucion() {
        LocalDate fechaDevolucionReal = LocalDate.of(2024, 1, 20);
        
        // Establecer fecha de devolución real
        prestamo.setFechaDevolucionReal(fechaDevolucionReal);
        
        // Verificar que se guardó correctamente
        assertEquals(fechaDevolucionReal, prestamo.getFechaDevolucionReal());
        
        // Verificar que no afecta a la fecha de devolución esperada
        assertEquals(fechaDevolucion, prestamo.getFechaDevolucion());
        
        // Cambiar fecha de devolución real a null
        prestamo.setFechaDevolucionReal(null);
        assertNull(prestamo.getFechaDevolucionReal());
    }
}