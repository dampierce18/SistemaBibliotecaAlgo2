package sistemabiblioteca.controlador;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.*;
import org.mockito.junit.jupiter.MockitoExtension;
import sistemabiblioteca.dao.LibroDAO;
import sistemabiblioteca.modelo.Libro;
import sistemabiblioteca.vista.PanelLibros;

import javax.swing.*;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ControladorLibrosTest {

    @Mock
    PanelLibros vista;

    @Mock
    LibroDAO libroDAO;

    ControladorLibros controlador;

    @BeforeEach
    void configurar() {
        controlador = new ControladorLibros(vista, libroDAO);
        // No usar clearInvocations aquí porque borra las llamadas del constructor
    }

    @Test
    void testConfigurarEventos_configuraTodosLosListeners() {
        // El constructor ya llamó a configurarEventos, así que verificamos las llamadas
        verify(vista).agregarGuardarListener(any());
        verify(vista).agregarLimpiarListener(any());
        verify(vista).agregarEditarListener(any());
        verify(vista).agregarEliminarListener(any());
        verify(vista).agregarActualizarListener(any());
        verify(vista).agregarBuscarListener(any());
    }

    @Test
    void testCargarLibros_actualizaVista() {
        // Limpiar invocaciones solo para este test
        clearInvocations(vista, libroDAO);
        
        Libro libro = new Libro(1, "Título", "2020", "Autor", "Categoría", "Editorial", 5, 5);
        when(libroDAO.obtenerTodosLosLibros()).thenReturn(List.of(libro));

        controlador.cargarLibros();

        verify(vista).mostrarLibros(List.of(libro));
    }

    @Test
    void testCargarLibros_listaVacia_actualizaVista() {
        clearInvocations(vista, libroDAO);
        
        when(libroDAO.obtenerTodosLosLibros()).thenReturn(List.of());

        controlador.cargarLibros();

        verify(vista).mostrarLibros(List.of());
    }

    @Test
    void testGuardarLibro_exitoso() {
        clearInvocations(vista, libroDAO);
        
        when(vista.validarCamposLibro()).thenReturn(true);
        when(vista.getTitulo()).thenReturn("Título Libro");
        when(vista.getAutor()).thenReturn("Autor Libro");
        when(vista.getAnioTexto()).thenReturn("2021");
        when(vista.getCategoria()).thenReturn("Categoría");
        when(vista.getEditorial()).thenReturn("Editorial");
        when(vista.getEjemplaresTexto()).thenReturn("3");
        when(libroDAO.insertarLibro(any())).thenReturn(true);

        controlador.guardarLibro();

        verify(libroDAO).insertarLibro(argThat(libro -> 
            libro.getTitulo().equals("Título Libro") && libro.getAutor().equals("Autor Libro")));
        verify(vista).limpiarFormulario();
        verify(vista).mostrarMensaje("Libro guardado exitosamente", JOptionPane.INFORMATION_MESSAGE);
        verify(libroDAO, times(1)).obtenerTodosLosLibros(); // Solo después de guardar (no cuenta constructor)
    }

    @Test
    void testGuardarLibro_insercionFallida_muestraError() {
        clearInvocations(vista, libroDAO);
        
        when(vista.validarCamposLibro()).thenReturn(true);
        when(vista.getTitulo()).thenReturn("Título");
        when(vista.getAutor()).thenReturn("Autor");
        when(vista.getAnioTexto()).thenReturn("2021");
        when(vista.getCategoria()).thenReturn("Categoría");
        when(vista.getEditorial()).thenReturn("Editorial");
        when(vista.getEjemplaresTexto()).thenReturn("2");
        when(libroDAO.insertarLibro(any())).thenReturn(false);

        controlador.guardarLibro();

        verify(vista).mostrarMensaje("Error al guardar el libro", JOptionPane.ERROR_MESSAGE);
        verify(vista, never()).limpiarFormulario();
    }

    @Test
    void testGuardarLibro_validacionFallida_noGuarda() {
        clearInvocations(vista, libroDAO);
        
        when(vista.validarCamposLibro()).thenReturn(false);

        controlador.guardarLibro();

        verify(libroDAO, never()).insertarLibro(any());
    }

    @Test
    void testGuardarLibro_ejemplaresInvalidos_muestraError() {
        clearInvocations(vista, libroDAO);
        
        when(vista.validarCamposLibro()).thenReturn(true);
        when(vista.getTitulo()).thenReturn("Título");
        when(vista.getAutor()).thenReturn("Autor");
        when(vista.getEjemplaresTexto()).thenReturn("no-es-un-numero");

        controlador.guardarLibro();

        verify(vista).mostrarMensaje("Ejemplares debe ser un número válido", JOptionPane.ERROR_MESSAGE);
        verify(libroDAO, never()).insertarLibro(any());
    }

    @Test
    void testGuardarLibro_daoLanzaExcepcion_muestraError() {
        clearInvocations(vista, libroDAO);
        
        when(vista.validarCamposLibro()).thenReturn(true);
        when(vista.getTitulo()).thenReturn("Título");
        when(vista.getAutor()).thenReturn("Autor");
        when(vista.getAnioTexto()).thenReturn("2021");
        when(vista.getCategoria()).thenReturn("Categoría");
        when(vista.getEditorial()).thenReturn("Editorial");
        when(vista.getEjemplaresTexto()).thenReturn("1");
        when(libroDAO.insertarLibro(any())).thenThrow(new RuntimeException("Error en BD"));

        controlador.guardarLibro();

        verify(vista).mostrarMensaje("Error: Error en BD", JOptionPane.ERROR_MESSAGE);
    }

    @Test
    void testEditarLibro_sinSeleccion_muestraAdvertencia() {
        clearInvocations(vista);
        
        when(vista.obtenerLibroIdSeleccionado()).thenReturn(null);

        controlador.editarLibro();

        verify(vista).mostrarMensaje("Seleccione un libro para editar", JOptionPane.WARNING_MESSAGE);
    }

    @Test
    void testEditarLibro_conSeleccion_muestraInfo() {
        clearInvocations(vista);
        
        when(vista.obtenerLibroIdSeleccionado()).thenReturn(1);

        controlador.editarLibro();

        verify(vista).mostrarMensaje("Funcionalidad de edición en desarrollo", JOptionPane.INFORMATION_MESSAGE);
    }

    @Test
    void testEliminarLibro_sinSeleccion_muestraAdvertencia() {
        clearInvocations(vista, libroDAO);
        
        when(vista.obtenerLibroIdSeleccionado()).thenReturn(null);

        controlador.eliminarLibro();

        verify(vista).mostrarMensaje("Seleccione un libro para eliminar", JOptionPane.WARNING_MESSAGE);
        verify(libroDAO, never()).eliminarLibro(anyInt());
    }

    @Test
    void testEliminarLibro_confirmacionFalse_noElimina() {
        clearInvocations(vista, libroDAO);
        
        when(vista.obtenerLibroIdSeleccionado()).thenReturn(1);
        when(vista.obtenerTituloLibroSeleccionado()).thenReturn("Título Libro");
        when(vista.mostrarConfirmacion(anyString(), anyString())).thenReturn(false);

        controlador.eliminarLibro();

        verify(libroDAO, never()).eliminarLibro(anyInt());
    }

    @Test
    void testEliminarLibro_confirmacionTrue_eliminacionExitosa() {
        clearInvocations(vista, libroDAO);
        
        when(vista.obtenerLibroIdSeleccionado()).thenReturn(1);
        when(vista.obtenerTituloLibroSeleccionado()).thenReturn("Título Libro");
        when(vista.mostrarConfirmacion(anyString(), anyString())).thenReturn(true);
        when(libroDAO.eliminarLibro(1)).thenReturn(true);

        controlador.eliminarLibro();

        verify(libroDAO).eliminarLibro(1);
        verify(vista).mostrarMensaje("Libro eliminado exitosamente", JOptionPane.INFORMATION_MESSAGE);
        verify(libroDAO, times(1)).obtenerTodosLosLibros(); // Solo después de eliminar
    }

    @Test
    void testEliminarLibro_confirmacionTrue_eliminacionFallida() {
        clearInvocations(vista, libroDAO);
        
        when(vista.obtenerLibroIdSeleccionado()).thenReturn(1);
        when(vista.obtenerTituloLibroSeleccionado()).thenReturn("Título Libro");
        when(vista.mostrarConfirmacion(anyString(), anyString())).thenReturn(true);
        when(libroDAO.eliminarLibro(1)).thenReturn(false);

        controlador.eliminarLibro();

        verify(libroDAO).eliminarLibro(1);
        verify(vista).mostrarMensaje("Error al eliminar el libro", JOptionPane.ERROR_MESSAGE);
        verify(libroDAO, never()).obtenerTodosLosLibros(); // No recarga si falla
    }

    @Test
    void testBuscarLibros_busquedaVacia_muestraAdvertencia() {
        clearInvocations(vista, libroDAO);
        
        when(vista.getTextoBusqueda()).thenReturn("");

        controlador.buscarLibros();

        verify(vista).mostrarMensaje("Ingrese un valor para buscar", JOptionPane.WARNING_MESSAGE);
        verify(libroDAO, never()).buscarLibros(anyString(), anyString());
    }

    @Test
    void testBuscarLibros_sinResultados_muestraInfo() {
        clearInvocations(vista, libroDAO);
        
        when(vista.getCriterioBusqueda()).thenReturn("autor");
        when(vista.getTextoBusqueda()).thenReturn("Autor Inexistente");
        when(libroDAO.buscarLibros("autor", "Autor Inexistente")).thenReturn(List.of());

        controlador.buscarLibros();

        verify(vista).mostrarResultadosBusqueda(List.of());
        verify(vista).mostrarMensaje("No se encontraron libros", JOptionPane.INFORMATION_MESSAGE);
    }

    @Test
    void testBuscarLibros_conResultados_actualizaVista() {
        clearInvocations(vista, libroDAO);
        
        when(vista.getCriterioBusqueda()).thenReturn("titulo");
        when(vista.getTextoBusqueda()).thenReturn("Java");
        Libro libro = new Libro(2, "Java Programming", "2020", "Autor", "Categoría", "Editorial", 1, 1);
        when(libroDAO.buscarLibros("titulo", "Java")).thenReturn(List.of(libro));

        controlador.buscarLibros();

        verify(vista).mostrarResultadosBusqueda(List.of(libro));
        verify(vista, never()).mostrarMensaje(contains("No se encontraron libros"), anyInt());
    }

    @Test
    void testObtenerTotalLibros_retornaValorDao() {
        clearInvocations(libroDAO);
        
        when(libroDAO.contarTotalLibros()).thenReturn(42);
        int total = controlador.obtenerTotalLibros();
        assertEquals(42, total);
        verify(libroDAO).contarTotalLibros();
    }

    @Test
    void testObtenerTotalLibros_propagaExcepcion() {
        clearInvocations(libroDAO);
        
        when(libroDAO.contarTotalLibros()).thenThrow(new RuntimeException("Error en BD"));
        assertThrows(RuntimeException.class, () -> controlador.obtenerTotalLibros());
        verify(libroDAO).contarTotalLibros();
    }
}