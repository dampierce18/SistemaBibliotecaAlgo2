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
        prestamo = new Prestamo(1, 100, 5, fechaPrestamo, fechaDevolucion);
    }

    @Test
    @DisplayName("Debería crear un préstamo con estado ACTIVO por defecto y empleadoId")
    void testCrearPrestamoConEstadoActivoYEmpleadoId() {
        // Verificar que el préstamo se crea con estado ACTIVO
        assertEquals("ACTIVO", prestamo.getEstado());
        
        // Verificar los demás atributos incluyendo empleadoId
        assertEquals(1, prestamo.getLibroId());
        assertEquals(100, prestamo.getUsuarioId());
        assertEquals(5, prestamo.getEmpleadoId());
        assertEquals(fechaPrestamo, prestamo.getFechaPrestamo());
        assertEquals(fechaDevolucion, prestamo.getFechaDevolucion());
    }

    @Test
    @DisplayName("Debería actualizar correctamente todos los atributos del préstamo incluyendo empleadoId")
    void testSettersYGetters() {
        // Configurar valores
        prestamo.setId(50);
        prestamo.setLibroId(2);
        prestamo.setUsuarioId(200);
        prestamo.setEmpleadoId(10);
        prestamo.setFechaPrestamo(LocalDate.of(2024, 2, 1));
        prestamo.setFechaDevolucion(LocalDate.of(2024, 2, 15));
        prestamo.setFechaDevolucionReal(LocalDate.of(2024, 2, 10));
        prestamo.setEstado("DEVUELTO");

        // Verificar valores
        assertEquals(50, prestamo.getId());
        assertEquals(2, prestamo.getLibroId());
        assertEquals(200, prestamo.getUsuarioId());
        assertEquals(10, prestamo.getEmpleadoId());
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
        assertEquals(0, prestamoVacio.getEmpleadoId());
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

    @Test
    @DisplayName("Debería manejar correctamente el empleadoId en diferentes escenarios")
    void testEmpleadoId() {
        // Verificar empleadoId inicial
        assertEquals(5, prestamo.getEmpleadoId());

        // Cambiar empleadoId
        prestamo.setEmpleadoId(15);
        assertEquals(15, prestamo.getEmpleadoId());

        // Cambiar a cero
        prestamo.setEmpleadoId(0);
        assertEquals(0, prestamo.getEmpleadoId());

        // Cambiar a negativo
        prestamo.setEmpleadoId(-1);
        assertEquals(-1, prestamo.getEmpleadoId());
    }

    @Test
    @DisplayName("Debería crear préstamos con diferentes empleados")
    void testPrestamosConDiferentesEmpleados() {
        Prestamo prestamoEmpleado1 = new Prestamo(1, 100, 1, fechaPrestamo, fechaDevolucion);
        Prestamo prestamoEmpleado2 = new Prestamo(2, 101, 2, fechaPrestamo, fechaDevolucion);
        Prestamo prestamoEmpleado3 = new Prestamo(3, 102, 3, fechaPrestamo, fechaDevolucion);

        assertEquals(1, prestamoEmpleado1.getEmpleadoId());
        assertEquals(2, prestamoEmpleado2.getEmpleadoId());
        assertEquals(3, prestamoEmpleado3.getEmpleadoId());
    }

    @Test
    @DisplayName("Debería mantener la consistencia después de múltiples modificaciones")
    void testConsistenciaDespuesDeModificaciones() {
        // Valores iniciales
        int libroIdOriginal = prestamo.getLibroId();
        int usuarioIdOriginal = prestamo.getUsuarioId();

        // Realizar múltiples modificaciones
        prestamo.setEstado("DEVUELTO");
        prestamo.setFechaDevolucionReal(LocalDate.now());
        prestamo.setEmpleadoId(20);

        // Verificar que algunos campos cambiaron y otros no
        assertEquals(libroIdOriginal, prestamo.getLibroId());
        assertEquals(usuarioIdOriginal, prestamo.getUsuarioId());
        assertEquals(20, prestamo.getEmpleadoId()); // Cambió
        assertEquals("DEVUELTO", prestamo.getEstado()); // Cambió
        assertNotNull(prestamo.getFechaDevolucionReal()); // Cambió
    }

    @Test
    @DisplayName("Debería manejar correctamente fechas límite")
    void testFechasLimite() {
        // Fecha mínima
        LocalDate fechaMinima = LocalDate.of(1900, 1, 1);
        prestamo.setFechaPrestamo(fechaMinima);
        assertEquals(fechaMinima, prestamo.getFechaPrestamo());

        // Fecha futura
        LocalDate fechaFutura = LocalDate.of(2100, 12, 31);
        prestamo.setFechaDevolucion(fechaFutura);
        assertEquals(fechaFutura, prestamo.getFechaDevolucion());

        // Fecha null
        prestamo.setFechaDevolucionReal(null);
        assertNull(prestamo.getFechaDevolucionReal());
    }

    @Test
    @DisplayName("Debería funcionar correctamente todos los getters después del constructor")
    void testTodosLosGettersDespuesDelConstructor() {
        Prestamo prestamoCompleto = new Prestamo(10, 50, 8, 
                                                LocalDate.of(2024, 3, 1), 
                                                LocalDate.of(2024, 3, 15));

        assertEquals(10, prestamoCompleto.getLibroId());
        assertEquals(50, prestamoCompleto.getUsuarioId());
        assertEquals(8, prestamoCompleto.getEmpleadoId());
        assertEquals(LocalDate.of(2024, 3, 1), prestamoCompleto.getFechaPrestamo());
        assertEquals(LocalDate.of(2024, 3, 15), prestamoCompleto.getFechaDevolucion());
        assertEquals("ACTIVO", prestamoCompleto.getEstado());
        assertNull(prestamoCompleto.getFechaDevolucionReal());
    }

    @Test
    @DisplayName("Debería permitir múltiples cambios de empleadoId")
    void testMultiplesCambiosEmpleadoId() {
        assertEquals(5, prestamo.getEmpleadoId());

        prestamo.setEmpleadoId(10);
        assertEquals(10, prestamo.getEmpleadoId());

        prestamo.setEmpleadoId(15);
        assertEquals(15, prestamo.getEmpleadoId());

        prestamo.setEmpleadoId(20);
        assertEquals(20, prestamo.getEmpleadoId());
    }

    @Test
    @DisplayName("Debería manejar correctamente el estado null")
    void testEstadoNull() {
        prestamo.setEstado(null);
        assertNull(prestamo.getEstado());
    }

    @Test
    @DisplayName("Debería crear préstamo con empleadoId cero")
    void testPrestamoConEmpleadoIdCero() {
        Prestamo prestamoCero = new Prestamo(1, 100, 0, fechaPrestamo, fechaDevolucion);
        assertEquals(0, prestamoCero.getEmpleadoId());
        assertEquals("ACTIVO", prestamoCero.getEstado());
    }

    @Test
    @DisplayName("Debería crear préstamo con empleadoId negativo")
    void testPrestamoConEmpleadoIdNegativo() {
        Prestamo prestamoNegativo = new Prestamo(1, 100, -5, fechaPrestamo, fechaDevolucion);
        assertEquals(-5, prestamoNegativo.getEmpleadoId());
        assertEquals("ACTIVO", prestamoNegativo.getEstado());
    }

    @Test
    @DisplayName("Debería mantener la fecha de préstamo después de cambios en otros campos")
    void testInmutabilidadFechaPrestamo() {
        LocalDate fechaOriginal = prestamo.getFechaPrestamo();

        // Modificar otros campos
        prestamo.setEstado("DEVUELTO");
        prestamo.setFechaDevolucionReal(LocalDate.now());
        prestamo.setEmpleadoId(25);

        // La fecha de préstamo debería permanecer igual
        assertEquals(fechaOriginal, prestamo.getFechaPrestamo());
    }

    @Test
    @DisplayName("Debería permitir la transición completa de estados con empleadoId")
    void testTransicionCompletaEstados() {
        // Préstamo recién creado
        assertEquals("ACTIVO", prestamo.getEstado());
        assertEquals(5, prestamo.getEmpleadoId());

        // Cambiar a ATRASADO
        prestamo.setEstado("ATRASADO");
        assertEquals("ATRASADO", prestamo.getEstado());
        assertEquals(5, prestamo.getEmpleadoId());

        // Cambiar a DEVUELTO con fecha de devolución real
        prestamo.setEstado("DEVUELTO");
        prestamo.setFechaDevolucionReal(LocalDate.now());
        assertEquals("DEVUELTO", prestamo.getEstado());
        assertNotNull(prestamo.getFechaDevolucionReal());
        assertEquals(5, prestamo.getEmpleadoId());

        // Cambiar empleado durante el proceso
        prestamo.setEmpleadoId(10);
        assertEquals(10, prestamo.getEmpleadoId());
    }
}