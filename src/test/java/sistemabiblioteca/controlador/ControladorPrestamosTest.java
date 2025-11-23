package sistemabiblioteca.controlador;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import sistemabiblioteca.dao.LibroDAO;
import sistemabiblioteca.dao.PrestamoDAO;
import sistemabiblioteca.dao.UsuarioDAO;
import sistemabiblioteca.modelo.Empleado;
import sistemabiblioteca.modelo.Libro;
import sistemabiblioteca.modelo.Prestamo;
import sistemabiblioteca.modelo.Usuario;
import sistemabiblioteca.vista.PanelPrestamos;

import javax.swing.*;
import java.time.LocalDate;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ControladorPrestamosTest {

    @Mock
    PanelPrestamos vista;

    @Mock
    PrestamoDAO prestamoDAO;

    @Mock
    LibroDAO libroDAO;

    @Mock
    UsuarioDAO usuarioDAO;

    @Mock
    Empleado empleado;

    private ControladorPrestamos controlador;

    @BeforeEach
    void configurar() {
        // Configurar stubs necesarios para la inicialización del controlador
        when(prestamoDAO.obtenerPrestamosActivos("id")).thenReturn(List.of());
        when(prestamoDAO.obtenerTodosLosPrestamos("fecha_prestamo DESC")).thenReturn(List.of());
        
        controlador = new ControladorPrestamos(vista, prestamoDAO, libroDAO, usuarioDAO, empleado);
    }

    @Test
    void testConstructor_configuraEventosYCargaPrestamos() {
        // Verificar que se configuran los eventos
        verify(vista).agregarRealizarPrestamoListener(any());
        verify(vista).agregarRegistrarDevolucionListener(any());
        verify(vista).agregarActualizarPrestamosListener(any());
        verify(vista).agregarLimpiarFormularioListener(any());
        verify(vista).setOrdenarActivosListener(any());
        verify(vista).setOrdenarHistorialListener(any());

        // Verificar que se cargan los préstamos iniciales
        verify(prestamoDAO).obtenerPrestamosActivos("id");
        verify(prestamoDAO).obtenerTodosLosPrestamos("fecha_prestamo DESC");
    }

    @Test
    void testRealizarPrestamo_exitoso() {
        clearInvocations(vista, prestamoDAO, libroDAO, usuarioDAO);
        
        when(vista.getLibroId()).thenReturn("1");
        when(vista.getUsuarioId()).thenReturn("1");
        when(vista.getDiasPrestamo()).thenReturn("14");
        
        Libro libro = new Libro(1, "Libro Test", "2020", "Autor", "Categoría", "Editorial", 5, 3, 1);
        when(libroDAO.obtenerLibroPorId(1)).thenReturn(libro);
        
        Usuario usuario = new Usuario();
        usuario.setId(1);
        usuario.setNombre("Usuario Test");
        when(usuarioDAO.obtenerUsuarioPorId(1)).thenReturn(usuario);
        
        when(empleado.getId()).thenReturn(1);
        when(empleado.getNombre()).thenReturn("Empleado Test");
        
        when(prestamoDAO.realizarPrestamo(any(Prestamo.class))).thenReturn(true);
        when(libroDAO.actualizarLibro(libro)).thenReturn(true);

        controlador.realizarPrestamo();

        verify(prestamoDAO).realizarPrestamo(argThat(prestamo -> 
            prestamo.getLibroId() == 1 &&
            prestamo.getUsuarioId() == 1 &&
            prestamo.getEmpleadoId() == 1 &&
            prestamo.getFechaDevolucion().equals(prestamo.getFechaPrestamo().plusDays(14))
        ));
        
        verify(libroDAO).actualizarLibro(argThat(l -> l.getDisponibles() == 2));
        verify(vista).limpiarFormulario();
        verify(vista).mostrarMensaje(contains("Préstamo realizado exitosamente"), eq(JOptionPane.INFORMATION_MESSAGE));
        verify(prestamoDAO).obtenerPrestamosActivos(anyString());
        verify(prestamoDAO).obtenerTodosLosPrestamos(anyString());
    }

    @Test
    void testRealizarPrestamo_libroNoExiste_muestraError() {
        clearInvocations(vista, prestamoDAO);
        
        when(vista.getLibroId()).thenReturn("999");
        when(vista.getUsuarioId()).thenReturn("1");
        when(vista.getDiasPrestamo()).thenReturn("14");
        
        when(libroDAO.obtenerLibroPorId(999)).thenReturn(null);

        controlador.realizarPrestamo();

        verify(vista).mostrarMensaje("El libro con ID 999 no existe", JOptionPane.ERROR_MESSAGE);
        verify(prestamoDAO, never()).realizarPrestamo(any());
    }

    @Test
    void testRealizarPrestamo_libroNoDisponible_muestraError() {
        clearInvocations(vista, prestamoDAO);
        
        when(vista.getLibroId()).thenReturn("1");
        when(vista.getUsuarioId()).thenReturn("1");
        when(vista.getDiasPrestamo()).thenReturn("14");
        
        Libro libro = new Libro(1, "Libro Test", "2020", "Autor", "Categoría", "Editorial", 5, 0, 1);
        when(libroDAO.obtenerLibroPorId(1)).thenReturn(libro);

        controlador.realizarPrestamo();

        verify(vista).mostrarMensaje("No hay ejemplares disponibles de este libro", JOptionPane.ERROR_MESSAGE);
        verify(prestamoDAO, never()).realizarPrestamo(any());
    }

    @Test
    void testRealizarPrestamo_usuarioNoExiste_muestraError() {
        clearInvocations(vista, prestamoDAO);
        
        when(vista.getLibroId()).thenReturn("1");
        when(vista.getUsuarioId()).thenReturn("999");
        when(vista.getDiasPrestamo()).thenReturn("14");
        
        Libro libro = new Libro(1, "Libro Test", "2020", "Autor", "Categoría", "Editorial", 5, 3, 1);
        when(libroDAO.obtenerLibroPorId(1)).thenReturn(libro);
        when(usuarioDAO.obtenerUsuarioPorId(999)).thenReturn(null);

        controlador.realizarPrestamo();

        verify(vista).mostrarMensaje("El usuario con ID 999 no existe", JOptionPane.ERROR_MESSAGE);
        verify(prestamoDAO, never()).realizarPrestamo(any());
    }

    @Test
    void testRealizarPrestamo_numeroInvalido_muestraError() {
        clearInvocations(vista, prestamoDAO);
        
        when(vista.getLibroId()).thenReturn("no-es-numero");
        when(vista.getUsuarioId()).thenReturn("1");

        controlador.realizarPrestamo();

        verify(vista).mostrarMensaje("Los IDs y días deben ser números válidos", JOptionPane.ERROR_MESSAGE);
        verify(prestamoDAO, never()).realizarPrestamo(any());
    }

    @Test
    void testRealizarPrestamo_insercionFallida_muestraError() {
        clearInvocations(vista, prestamoDAO, libroDAO);
        
        when(vista.getLibroId()).thenReturn("1");
        when(vista.getUsuarioId()).thenReturn("1");
        when(vista.getDiasPrestamo()).thenReturn("14");
        
        Libro libro = new Libro(1, "Libro Test", "2020", "Autor", "Categoría", "Editorial", 5, 3, 1);
        when(libroDAO.obtenerLibroPorId(1)).thenReturn(libro);
        
        Usuario usuario = new Usuario();
        usuario.setId(1);
        usuario.setNombre("Usuario Test");
        when(usuarioDAO.obtenerUsuarioPorId(1)).thenReturn(usuario);
        
        when(prestamoDAO.realizarPrestamo(any())).thenReturn(false);

        controlador.realizarPrestamo();

        verify(vista).mostrarMensaje("Error al realizar el préstamo", JOptionPane.ERROR_MESSAGE);
        verify(libroDAO, never()).actualizarLibro(any());
        verify(vista, never()).limpiarFormulario();
    }

    @Test
    void testRegistrarDevolucion_exitoso() {
        clearInvocations(vista, prestamoDAO, libroDAO);
        
        when(vista.obtenerPrestamoIdSeleccionado()).thenReturn(1);
        when(vista.obtenerLibroIdSeleccionado()).thenReturn(1);
        
        when(prestamoDAO.registrarDevolucion(1)).thenReturn(true);
        
        Libro libro = new Libro(1, "Libro Test", "2020", "Autor", "Categoría", "Editorial", 5, 2, 1);
        when(libroDAO.obtenerLibroPorId(1)).thenReturn(libro);
        when(libroDAO.actualizarLibro(libro)).thenReturn(true);

        controlador.registrarDevolucion();

        verify(prestamoDAO).verificarEstadoPrestamo(1);
        verify(prestamoDAO).registrarDevolucion(1);
        verify(libroDAO).actualizarLibro(argThat(l -> l.getDisponibles() == 3));
        verify(vista).mostrarMensaje("Devolución registrada exitosamente", JOptionPane.INFORMATION_MESSAGE);
        verify(prestamoDAO).obtenerPrestamosActivos(anyString());
        verify(prestamoDAO).obtenerTodosLosPrestamos(anyString());
    }

    @Test
    void testRegistrarDevolucion_sinSeleccion_muestraAdvertencia() {
        clearInvocations(vista, prestamoDAO);
        
        when(vista.obtenerPrestamoIdSeleccionado()).thenReturn(null);

        controlador.registrarDevolucion();

        verify(vista).mostrarMensaje("Seleccione un préstamo para registrar devolución", JOptionPane.WARNING_MESSAGE);
        verify(prestamoDAO, never()).registrarDevolucion(anyInt());
    }

    @Test
    void testRegistrarDevolucion_fallida_muestraError() {
        clearInvocations(vista, prestamoDAO, libroDAO);
        
        when(vista.obtenerPrestamoIdSeleccionado()).thenReturn(1);
        when(vista.obtenerLibroIdSeleccionado()).thenReturn(1);
        
        when(prestamoDAO.registrarDevolucion(1)).thenReturn(false);

        controlador.registrarDevolucion();

        verify(vista).mostrarMensaje("Error al registrar la devolución", JOptionPane.ERROR_MESSAGE);
        verify(libroDAO, never()).actualizarLibro(any());
    }

    @Test
    void testOrdenarPrestamosActivos_diferentesCriterios() {
        clearInvocations(vista, prestamoDAO);
        
        String[] criterios = {"id", "libro_id", "usuario_id", "empleado_id", "fecha_prestamo DESC", "fecha_devolucion"};
        String[] mensajes = {
            "Préstamos activos ordenados por ID",
            "Préstamos activos ordenados por ID Libro", 
            "Préstamos activos ordenados por ID Usuario",
            "Préstamos activos ordenados por ID Empleado",
            "Préstamos activos ordenados por Fecha Préstamo (más recientes primero)",
            "Préstamos activos ordenados por Fecha Devolución"
        };

        for (int i = 0; i < criterios.length; i++) {
            clearInvocations(vista, prestamoDAO);
            controlador.ordenarPrestamosActivos(criterios[i]);
            assertEquals(criterios[i], controlador.getOrdenActivos());
            verify(vista).mostrarMensaje(mensajes[i], JOptionPane.INFORMATION_MESSAGE);
            verify(prestamoDAO).obtenerPrestamosActivos(criterios[i]);
        }
    }

    @Test
    void testOrdenarHistorial_diferentesCriterios() {
        clearInvocations(vista, prestamoDAO);
        
        String[] criterios = {"id", "libro_id", "usuario_id", "empleado_id", "fecha_prestamo DESC", "fecha_devolucion", "fecha_devolucion_real DESC"};
        String[] mensajes = {
            "Historial ordenado por ID",
            "Historial ordenado por ID Libro",
            "Historial ordenado por ID Usuario", 
            "Historial ordenado por ID Empleado",
            "Historial ordenado por Fecha Préstamo (más recientes primero)",
            "Historial ordenado por Fecha Devolución",
            "Historial ordenado por Fecha Devolución Real (más recientes primero)"
        };

        for (int i = 0; i < criterios.length; i++) {
            clearInvocations(vista, prestamoDAO);
            controlador.ordenarHistorial(criterios[i]);
            assertEquals(criterios[i], controlador.getOrdenHistorial());
            verify(vista).mostrarMensaje(mensajes[i], JOptionPane.INFORMATION_MESSAGE);
            verify(prestamoDAO).obtenerTodosLosPrestamos(criterios[i]);
        }
    }

    @Test
    void testActualizarListas_recargaAmbasListas() {
        clearInvocations(prestamoDAO);
        
        controlador.actualizarListas();
        
        verify(prestamoDAO).obtenerPrestamosActivos("id");
        verify(prestamoDAO).obtenerTodosLosPrestamos("fecha_prestamo DESC");
    }

    @Test
    void testObtenerPrestamosActivos_retornaValorDao() {
        clearInvocations(prestamoDAO);
        
        when(prestamoDAO.contarPrestamosActivos()).thenReturn(5);
        int resultado = controlador.obtenerPrestamosActivos();
        assertEquals(5, resultado);
        verify(prestamoDAO).contarPrestamosActivos();
    }

    @Test
    void testObtenerPrestamosAtrasados_retornaValorDao() {
        clearInvocations(prestamoDAO);
        
        when(prestamoDAO.contarPrestamosAtrasados()).thenReturn(2);
        int resultado = controlador.obtenerPrestamosAtrasados();
        assertEquals(2, resultado);
        verify(prestamoDAO).contarPrestamosAtrasados();
    }

    @Test
    void testConstructorSinEmpleado_creaInstanciaCorrectamente() {
        clearInvocations(prestamoDAO);
        
        when(prestamoDAO.obtenerPrestamosActivos("id")).thenReturn(List.of());
        when(prestamoDAO.obtenerTodosLosPrestamos("fecha_prestamo DESC")).thenReturn(List.of());
        
        ControladorPrestamos controladorSinEmpleado = new ControladorPrestamos(vista, prestamoDAO, libroDAO, usuarioDAO, null);
        assertNotNull(controladorSinEmpleado);
        assertNull(controladorSinEmpleado.getEmpleadoLogueado());
    }
    
    @Test
    void testCargarPrestamosActivos_conPrestamosAtrasados() {
        clearInvocations(vista, prestamoDAO);
        
        // Crear préstamos de prueba
        Prestamo prestamoActivo = new Prestamo(1, 1, 1, LocalDate.now(), LocalDate.now().plusDays(14));
        prestamoActivo.setId(1);
        prestamoActivo.setEstado("ACTIVO");
        
        Prestamo prestamoAtrasado = new Prestamo(2, 2, 1, LocalDate.now().minusDays(20), LocalDate.now().minusDays(5));
        prestamoAtrasado.setId(2);
        prestamoAtrasado.setEstado("ATRASADO");
        
        when(prestamoDAO.obtenerPrestamosActivos("id")).thenReturn(List.of(prestamoActivo, prestamoAtrasado));

        controlador.cargarPrestamosActivos();

        verify(vista).actualizarTablaPrestamosActivos(argThat(datos -> {
            // Verificar que se crea el array correcto
            if (datos.length != 2) return false;
            
            // Verificar datos del préstamo activo
            if (!datos[0][0].equals(1) || !datos[0][6].equals("ACTIVO")) return false;
            
            // Verificar que el préstamo atrasado tiene el indicador ⚠
            return "ATRASADO ⚠".equals(datos[1][6]);
        }));
    }

    @Test
    void testCargarPrestamosActivos_listaVacia() {
        clearInvocations(vista, prestamoDAO);
        
        when(prestamoDAO.obtenerPrestamosActivos("id")).thenReturn(List.of());

        controlador.cargarPrestamosActivos();

        verify(vista).actualizarTablaPrestamosActivos(argThat(datos -> 
            datos.length == 0
        ));
    }

    @Test
    void testCargarPrestamosActivos_conDatosCompletos() {
        clearInvocations(vista, prestamoDAO);
        
        Prestamo prestamo = new Prestamo(1, 2, 3, LocalDate.of(2024, 1, 15), LocalDate.of(2024, 1, 29));
        prestamo.setId(5);
        prestamo.setEstado("ACTIVO");
        
        when(prestamoDAO.obtenerPrestamosActivos("id")).thenReturn(List.of(prestamo));

        controlador.cargarPrestamosActivos();

        verify(vista).actualizarTablaPrestamosActivos(argThat(datos -> {
            if (datos.length != 1) return false;
            
            Object[] fila = datos[0];
            return fila[0].equals(5) &&        // id
                   fila[1].equals(1) &&        // libroId
                   fila[2].equals(2) &&        // usuarioId
                   fila[3].equals(3) &&        // empleadoId
                   fila[4].equals(LocalDate.of(2024, 1, 15)) &&  // fechaPrestamo
                   fila[5].equals(LocalDate.of(2024, 1, 29)) &&  // fechaDevolucion
                   fila[6].equals("ACTIVO");   // estado
        }));
    }

    @Test
    void testCargarHistorialPrestamos_conDatosCompletos() {
        clearInvocations(vista, prestamoDAO);
        
        Prestamo prestamo = new Prestamo(1, 2, 3, LocalDate.of(2024, 1, 10), LocalDate.of(2024, 1, 24));
        prestamo.setId(5);
        prestamo.setEstado("DEVUELTO");
        prestamo.setFechaDevolucionReal(LocalDate.of(2024, 1, 22));
        
        when(prestamoDAO.obtenerTodosLosPrestamos("fecha_prestamo DESC")).thenReturn(List.of(prestamo));

        controlador.cargarHistorialPrestamos();

        verify(vista).actualizarTablaHistorial(argThat(datos -> {
            if (datos.length != 1 || datos[0].length != 8) return false;
            
            Object[] fila = datos[0];
            return fila[0].equals(5) &&                            // id
                   fila[1].equals(1) &&                            // libroId
                   fila[2].equals(2) &&                            // usuarioId
                   fila[3].equals(3) &&                            // empleadoId
                   fila[4].equals(LocalDate.of(2024, 1, 10)) &&    // fechaPrestamo
                   fila[5].equals(LocalDate.of(2024, 1, 24)) &&    // fechaDevolucion
                   fila[6].equals(LocalDate.of(2024, 1, 22)) &&    // fechaDevolucionReal
                   fila[7].equals("DEVUELTO");                     // estado
        }));
    }

    @Test
    void testCargarHistorialPrestamos_conFechaDevolucionRealNula() {
        clearInvocations(vista, prestamoDAO);
        
        Prestamo prestamo = new Prestamo(1, 2, 3, LocalDate.now(), LocalDate.now().plusDays(14));
        prestamo.setId(1);
        prestamo.setEstado("ACTIVO");
        // fechaDevolucionReal permanece null
        
        when(prestamoDAO.obtenerTodosLosPrestamos("fecha_prestamo DESC")).thenReturn(List.of(prestamo));

        controlador.cargarHistorialPrestamos();

        verify(vista).actualizarTablaHistorial(argThat(datos -> 
            datos.length == 1 && datos[0][6] == null  // fechaDevolucionReal debe ser null
        ));
    }

    @Test
    void testCargarHistorialPrestamos_listaVacia() {
        clearInvocations(vista, prestamoDAO);
        
        when(prestamoDAO.obtenerTodosLosPrestamos("fecha_prestamo DESC")).thenReturn(List.of());

        controlador.cargarHistorialPrestamos();

        verify(vista).actualizarTablaHistorial(argThat(datos -> 
            datos.length == 0
        ));
    }

    @Test
    void testCargarHistorialPrestamos_conMultiplesPrestamos() {
        clearInvocations(vista, prestamoDAO);
        
        Prestamo prestamo1 = new Prestamo(1, 1, 1, LocalDate.now().minusDays(10), LocalDate.now().minusDays(3));
        prestamo1.setId(1);
        prestamo1.setEstado("DEVUELTO");
        prestamo1.setFechaDevolucionReal(LocalDate.now().minusDays(2));
        
        Prestamo prestamo2 = new Prestamo(2, 3, 2, LocalDate.now().minusDays(5), LocalDate.now().plusDays(2));
        prestamo2.setId(2);
        prestamo2.setEstado("ACTIVO");
        
        when(prestamoDAO.obtenerTodosLosPrestamos("fecha_prestamo DESC")).thenReturn(List.of(prestamo1, prestamo2));

        controlador.cargarHistorialPrestamos();

        verify(vista).actualizarTablaHistorial(argThat(datos -> 
            datos.length == 2 && 
            datos[0].length == 8 && 
            datos[1].length == 8
        ));
    }

    @Test 
    void testCargarPrestamosActivos_conDiferentesEstados() {
        clearInvocations(vista, prestamoDAO);
        
        Prestamo prestamoActivo = new Prestamo(1, 1, 1, LocalDate.now(), LocalDate.now().plusDays(14));
        prestamoActivo.setId(1);
        prestamoActivo.setEstado("ACTIVO");
        
        Prestamo prestamoAtrasado = new Prestamo(2, 2, 1, LocalDate.now().minusDays(20), LocalDate.now().minusDays(5));
        prestamoAtrasado.setId(2);
        prestamoAtrasado.setEstado("ATRASADO");
        
        Prestamo prestamoDevuelto = new Prestamo(3, 3, 1, LocalDate.now().minusDays(15), LocalDate.now().minusDays(1));
        prestamoDevuelto.setId(3);
        prestamoDevuelto.setEstado("DEVUELTO");
        
        when(prestamoDAO.obtenerPrestamosActivos("id")).thenReturn(List.of(prestamoActivo, prestamoAtrasado, prestamoDevuelto));

        controlador.cargarPrestamosActivos();

        verify(vista).actualizarTablaPrestamosActivos(argThat(datos -> {
            if (datos.length != 3) return false;
            
            // Verificar que solo los ATRASADO tienen el indicador ⚠
            return "ACTIVO".equals(datos[0][6]) &&
                   "ATRASADO ⚠".equals(datos[1][6]) &&
                   "DEVUELTO".equals(datos[2][6]);
        }));
    }
}