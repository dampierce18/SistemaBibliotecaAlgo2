package sistemabiblioteca.controlador;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.*;
import org.mockito.junit.jupiter.MockitoExtension;
import sistemabiblioteca.dao.LibroDAO;
import sistemabiblioteca.dao.PrestamoDAO;
import sistemabiblioteca.dao.UsuarioDAO;
import sistemabiblioteca.modelo.Libro;
import sistemabiblioteca.modelo.Prestamo;
import sistemabiblioteca.modelo.Usuario;
import sistemabiblioteca.vista.PanelPrestamos;

import java.time.LocalDate;
import java.util.List;

import javax.swing.JOptionPane;

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

    private ControladorPrestamos controlador;

    @BeforeEach
    void configurar() {
        controlador = new ControladorPrestamos(vista, prestamoDAO, libroDAO, usuarioDAO);
        // No usar clearInvocations aquí para no borrar las llamadas del constructor
    }

    @Test
    void testConstructor_configuraEventosYCargaDatos() {
        // Verificar que se configuran los eventos
        verify(vista).agregarRealizarPrestamoListener(any());
        verify(vista).agregarRegistrarDevolucionListener(any());
        verify(vista).agregarActualizarPrestamosListener(any());
        verify(vista).agregarLimpiarFormularioListener(any());

        // Verificar que se cargan los datos iniciales
        verify(prestamoDAO).obtenerPrestamosActivos();
        verify(prestamoDAO).obtenerTodosLosPrestamos();
    }

    @Test
    void testRealizarPrestamo_exitoso() {
        // Preparar test
        clearInvocations(vista, prestamoDAO, libroDAO, usuarioDAO);
        
        // Configurar mocks
        when(vista.getLibroId()).thenReturn("1");
        when(vista.getUsuarioId()).thenReturn("1");
        when(vista.getDiasPrestamo()).thenReturn("7");

        Libro libroMock = new Libro(1, "Libro Test", "2023", "Autor Test", "Categoría", "Editorial", 5, 3);
        
        // Crear usuario mock con el constructor correcto
        Usuario usuarioMock = new Usuario();
        usuarioMock.setId(1);
        usuarioMock.setNombre("Usuario Test");
        usuarioMock.setApellidoPaterno("Paterno");
        usuarioMock.setApellidoMaterno("Materno");
        usuarioMock.setDomicilio("Dirección Test");
        usuarioMock.setTelefono("123456789");

        when(libroDAO.obtenerLibroPorId(1)).thenReturn(libroMock);
        when(usuarioDAO.obtenerUsuarioPorId(1)).thenReturn(usuarioMock);
        when(prestamoDAO.realizarPrestamo(any(Prestamo.class))).thenReturn(true);
        when(libroDAO.actualizarLibro(any(Libro.class))).thenReturn(true);

        // Ejecutar
        controlador.realizarPrestamo();

        // Verificar
        verify(prestamoDAO).realizarPrestamo(any(Prestamo.class));
        verify(libroDAO).actualizarLibro(argThat(libro -> libro.getDisponibles() == 2));
        verify(vista).limpiarFormulario();
        verify(vista).mostrarMensaje(contains("Préstamo realizado exitosamente"), eq(JOptionPane.INFORMATION_MESSAGE));
        verify(prestamoDAO, times(1)).obtenerPrestamosActivos();
        verify(prestamoDAO, times(1)).obtenerTodosLosPrestamos();
    }

    @Test
    void testRealizarPrestamo_camposVacios_muestraError() {
        clearInvocations(vista);
        
        when(vista.getLibroId()).thenReturn("");

        controlador.realizarPrestamo();

        verify(vista).mostrarMensaje("ID Libro e ID Usuario son obligatorios", JOptionPane.ERROR_MESSAGE);
        verify(prestamoDAO, never()).realizarPrestamo(any());
    }

    @Test
    void testRealizarPrestamo_libroNoExiste_muestraError() {
        clearInvocations(vista, libroDAO);
        
        when(vista.getLibroId()).thenReturn("999");
        when(vista.getUsuarioId()).thenReturn("1");
        when(vista.getDiasPrestamo()).thenReturn("7");

        when(libroDAO.obtenerLibroPorId(999)).thenReturn(null);

        controlador.realizarPrestamo();

        verify(vista).mostrarMensaje(contains("El libro con ID 999 no existe"), eq(JOptionPane.ERROR_MESSAGE));
        verify(prestamoDAO, never()).realizarPrestamo(any());
    }

    @Test
    void testRealizarPrestamo_libroSinDisponibles_muestraError() {
        clearInvocations(vista, libroDAO);
        
        when(vista.getLibroId()).thenReturn("1");
        when(vista.getUsuarioId()).thenReturn("1");
        when(vista.getDiasPrestamo()).thenReturn("7");

        Libro libroSinDisponibles = new Libro(1, "Libro Test", "2023", "Autor Test", "Categoría", "Editorial", 5, 0);
        when(libroDAO.obtenerLibroPorId(1)).thenReturn(libroSinDisponibles);

        controlador.realizarPrestamo();

        verify(vista).mostrarMensaje("No hay ejemplares disponibles de este libro", JOptionPane.ERROR_MESSAGE);
        verify(prestamoDAO, never()).realizarPrestamo(any());
    }

    @Test
    void testRealizarPrestamo_usuarioNoExiste_muestraError() {
        clearInvocations(vista, usuarioDAO);
        
        when(vista.getLibroId()).thenReturn("1");
        when(vista.getUsuarioId()).thenReturn("999");
        when(vista.getDiasPrestamo()).thenReturn("7");

        Libro libroMock = new Libro(1, "Libro Test", "2023", "Autor Test", "Categoría", "Editorial", 5, 3);
        when(libroDAO.obtenerLibroPorId(1)).thenReturn(libroMock);
        when(usuarioDAO.obtenerUsuarioPorId(999)).thenReturn(null);

        controlador.realizarPrestamo();

        verify(vista).mostrarMensaje(contains("El usuario con ID 999 no existe"), eq(JOptionPane.ERROR_MESSAGE));
        verify(prestamoDAO, never()).realizarPrestamo(any());
    }

    @Test
    void testRealizarPrestamo_numeroInvalido_muestraError() {
        clearInvocations(vista);
        
        when(vista.getLibroId()).thenReturn("no-es-numero");
        when(vista.getUsuarioId()).thenReturn("1");

        controlador.realizarPrestamo();

        verify(vista).mostrarMensaje("Los IDs y días deben ser números válidos", JOptionPane.ERROR_MESSAGE);
        verify(prestamoDAO, never()).realizarPrestamo(any());
    }

    @Test
    void testRealizarPrestamo_prestamoFallido_muestraError() {
        clearInvocations(vista, prestamoDAO, libroDAO);
        
        when(vista.getLibroId()).thenReturn("1");
        when(vista.getUsuarioId()).thenReturn("1");
        when(vista.getDiasPrestamo()).thenReturn("7");

        Libro libroMock = new Libro(1, "Libro Test", "2023", "Autor Test", "Categoría", "Editorial", 5, 3);
        
        // Crear usuario mock con el constructor correcto
        Usuario usuarioMock = new Usuario();
        usuarioMock.setId(1);
        usuarioMock.setNombre("Usuario Test");
        usuarioMock.setApellidoPaterno("Paterno");
        usuarioMock.setApellidoMaterno("Materno");
        usuarioMock.setDomicilio("Dirección Test");
        usuarioMock.setTelefono("123456789");

        when(libroDAO.obtenerLibroPorId(1)).thenReturn(libroMock);
        when(usuarioDAO.obtenerUsuarioPorId(1)).thenReturn(usuarioMock);
        when(prestamoDAO.realizarPrestamo(any(Prestamo.class))).thenReturn(false);

        controlador.realizarPrestamo();

        verify(vista).mostrarMensaje("Error al realizar el préstamo", JOptionPane.ERROR_MESSAGE);
        verify(vista, never()).limpiarFormulario();
        verify(libroDAO, never()).actualizarLibro(any());
    }

    @Test
    void testRealizarPrestamo_excepcion_muestraError() {
        clearInvocations(vista, libroDAO);
        
        when(vista.getLibroId()).thenReturn("1");
        when(vista.getUsuarioId()).thenReturn("1");
        when(vista.getDiasPrestamo()).thenReturn("7");

        when(libroDAO.obtenerLibroPorId(1)).thenThrow(new RuntimeException("Error de base de datos"));

        controlador.realizarPrestamo();

        verify(vista).mostrarMensaje(contains("Error: Error de base de datos"), eq(JOptionPane.ERROR_MESSAGE));
        verify(prestamoDAO, never()).realizarPrestamo(any());
    }

    @Test
    void testRegistrarDevolucion_exitoso() {
        clearInvocations(vista, prestamoDAO, libroDAO);
        
        when(vista.obtenerPrestamoIdSeleccionado()).thenReturn(1);
        when(vista.obtenerLibroIdSeleccionado()).thenReturn(1);

        Libro libroMock = new Libro(1, "Libro Test", "2023", "Autor Test", "Categoría", "Editorial", 5, 2);
        when(libroDAO.obtenerLibroPorId(1)).thenReturn(libroMock);
        when(prestamoDAO.registrarDevolucion(1)).thenReturn(true);
        when(libroDAO.actualizarLibro(any(Libro.class))).thenReturn(true);

        controlador.registrarDevolucion();

        verify(prestamoDAO).registrarDevolucion(1);
        verify(libroDAO).actualizarLibro(argThat(libro -> libro.getDisponibles() == 3));
        verify(vista).mostrarMensaje("Devolución registrada exitosamente", JOptionPane.INFORMATION_MESSAGE);
        verify(prestamoDAO, times(1)).obtenerPrestamosActivos(); // Solo después de devolución
        verify(prestamoDAO, times(1)).obtenerTodosLosPrestamos(); // Solo después de devolución
    }

    @Test
    void testRegistrarDevolucion_sinSeleccion_muestraAdvertencia() {
        clearInvocations(vista);
        
        when(vista.obtenerPrestamoIdSeleccionado()).thenReturn(null);

        controlador.registrarDevolucion();

        verify(vista).mostrarMensaje("Seleccione un préstamo para registrar devolución", JOptionPane.WARNING_MESSAGE);
        verify(prestamoDAO, never()).registrarDevolucion(anyInt());
    }

    @Test
    void testRegistrarDevolucion_fallida_muestraError() {
        clearInvocations(vista, prestamoDAO);
        
        when(vista.obtenerPrestamoIdSeleccionado()).thenReturn(1);
        when(vista.obtenerLibroIdSeleccionado()).thenReturn(1);

        when(prestamoDAO.registrarDevolucion(1)).thenReturn(false);

        controlador.registrarDevolucion();

        verify(vista).mostrarMensaje("Error al registrar la devolución", JOptionPane.ERROR_MESSAGE);
        verify(libroDAO, never()).actualizarLibro(any());
    }

    @Test
    void testRegistrarDevolucion_excepcion_muestraError() {
        clearInvocations(vista, prestamoDAO);
        
        when(vista.obtenerPrestamoIdSeleccionado()).thenReturn(1);
        when(vista.obtenerLibroIdSeleccionado()).thenReturn(1);

        when(prestamoDAO.registrarDevolucion(1)).thenThrow(new RuntimeException("Error de BD"));

        controlador.registrarDevolucion();

        verify(vista).mostrarMensaje(contains("Error: Error de BD"), eq(JOptionPane.ERROR_MESSAGE));
    }

    @Test
    void testCargarPrestamosActivos_actualizaTabla() {
        clearInvocations(vista, prestamoDAO);
        
        Prestamo prestamo = new Prestamo(1, 1, LocalDate.now(), LocalDate.now().plusDays(7));
        prestamo.setId(1);
        when(prestamoDAO.obtenerPrestamosActivos()).thenReturn(List.of(prestamo));

        controlador.cargarPrestamosActivos();

        ArgumentCaptor<Object[][]> capturador = ArgumentCaptor.forClass(Object[][].class);
        verify(vista).actualizarTablaPrestamosActivos(capturador.capture());
        Object[][] datos = capturador.getValue();
        assertEquals(1, datos.length);
        assertEquals(1, datos[0][0]);
        assertEquals(1, datos[0][1]);
        assertEquals(1, datos[0][2]);
    }

    @Test
    void testCargarPrestamosActivos_listaVacia_actualizaTabla() {
        clearInvocations(vista, prestamoDAO);
        
        when(prestamoDAO.obtenerPrestamosActivos()).thenReturn(List.of());

        controlador.cargarPrestamosActivos();

        ArgumentCaptor<Object[][]> capturador = ArgumentCaptor.forClass(Object[][].class);
        verify(vista).actualizarTablaPrestamosActivos(capturador.capture());
        Object[][] datos = capturador.getValue();
        assertEquals(0, datos.length);
    }

    @Test
    void testCargarHistorialPrestamos_actualizaTabla() {
        clearInvocations(vista, prestamoDAO);
        
        Prestamo prestamo = new Prestamo(1, 1, LocalDate.now(), LocalDate.now().plusDays(7));
        prestamo.setId(1);
        prestamo.setFechaDevolucionReal(LocalDate.now());
        when(prestamoDAO.obtenerTodosLosPrestamos()).thenReturn(List.of(prestamo));

        controlador.cargarHistorialPrestamos();

        ArgumentCaptor<Object[][]> capturador = ArgumentCaptor.forClass(Object[][].class);
        verify(vista).actualizarTablaHistorial(capturador.capture());
        Object[][] datos = capturador.getValue();
        assertEquals(1, datos.length);
        assertEquals(1, datos[0][0]);
        assertEquals(1, datos[0][1]);
        assertEquals(1, datos[0][2]);
    }

    @Test
    void testCargarHistorialPrestamos_listaVacia_actualizaTabla() {
        clearInvocations(vista, prestamoDAO);
        
        when(prestamoDAO.obtenerTodosLosPrestamos()).thenReturn(List.of());

        controlador.cargarHistorialPrestamos();

        ArgumentCaptor<Object[][]> capturador = ArgumentCaptor.forClass(Object[][].class);
        verify(vista).actualizarTablaHistorial(capturador.capture());
        Object[][] datos = capturador.getValue();
        assertEquals(0, datos.length);
    }

    @Test
    void testActualizarListas_llamaMetodosCarga() {
        clearInvocations(vista, prestamoDAO);
        
        controlador.actualizarListas();

        verify(prestamoDAO).obtenerPrestamosActivos();
        verify(prestamoDAO).obtenerTodosLosPrestamos();
        verify(vista).actualizarTablaPrestamosActivos(any());
        verify(vista).actualizarTablaHistorial(any());
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
    void testRegistrarDevolucion_libroNull_noActualizaDisponibles() {
        clearInvocations(vista, prestamoDAO, libroDAO);
        
        when(vista.obtenerPrestamoIdSeleccionado()).thenReturn(1);
        when(vista.obtenerLibroIdSeleccionado()).thenReturn(1);

        when(libroDAO.obtenerLibroPorId(1)).thenReturn(null); // Libro no encontrado
        when(prestamoDAO.registrarDevolucion(1)).thenReturn(true);

        controlador.registrarDevolucion();

        verify(prestamoDAO).registrarDevolucion(1);
        verify(libroDAO, never()).actualizarLibro(any()); // No actualiza porque libro es null
        verify(vista).mostrarMensaje("Devolución registrada exitosamente", JOptionPane.INFORMATION_MESSAGE);
    }
}