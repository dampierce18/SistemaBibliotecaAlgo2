package sistemabiblioteca.controlador;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.*;
import org.mockito.junit.jupiter.MockitoExtension;
import sistemabiblioteca.dao.LibroDAO;
import sistemabiblioteca.modelo.Empleado;
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

    @Mock
    Empleado empleado;

    ControladorLibros controlador;

    @BeforeEach
    void configurar() {
        // Usar lenient para evitar el UnnecessaryStubbingException
        lenient().when(empleado.getId()).thenReturn(1);
        controlador = new ControladorLibros(vista, libroDAO, empleado);
    }

    @Test
    void testConfigurarEventos_configuraTodosLosListeners() {
        verify(vista).agregarGuardarListener(any());
        verify(vista).agregarLimpiarListener(any());
        verify(vista).agregarEditarListener(any());
        verify(vista).agregarEliminarListener(any());
        verify(vista).agregarActualizarListener(any());
        verify(vista).agregarBuscarListener(any());
        verify(vista).setOrdenarListener(any());
    }

    @Test
    void testCargarLibros_actualizaVista() {
        clearInvocations(vista, libroDAO);
        
        Libro libro = new Libro(1, "Título", "2020", "Autor", "Categoría", "Editorial", 5, 5, 1);
        when(libroDAO.obtenerTodosLosLibros("id")).thenReturn(List.of(libro));

        controlador.cargarLibros();

        verify(vista).mostrarLibros(List.of(libro));
    }

    @Test
    void testOrdenarLibros_cambiaOrdenYRecarga() {
        clearInvocations(vista, libroDAO);
        
        controlador.ordenarLibros("titulo");
        
        assertEquals("titulo", controlador.getOrdenActual());
        verify(libroDAO).obtenerTodosLosLibros("titulo");
        verify(vista).mostrarMensaje("Ordenado por Título", JOptionPane.INFORMATION_MESSAGE);
    }

    @Test
    void testGuardarLibro_modoInsercionExitoso() {
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
            libro.getTitulo().equals("Título Libro") && 
            libro.getAutor().equals("Autor Libro") &&
            libro.getEmpleadoId() == 1));
        verify(vista).limpiarFormulario();
        verify(vista).mostrarMensaje("Libro guardado exitosamente", JOptionPane.INFORMATION_MESSAGE);
        verify(libroDAO).obtenerTodosLosLibros("id");
    }

    @Test
    void testGuardarLibro_modoEdicionExitoso() {
        clearInvocations(vista, libroDAO);
        
        // Configurar modo edición
        controlador = new ControladorLibros(vista, libroDAO, empleado) {
            {
                this.modoEdicion = true;
                this.libroEditandoId = 1;
            }
        };

        when(vista.validarCamposLibro()).thenReturn(true);
        when(vista.getTitulo()).thenReturn("Título Editado");
        when(vista.getAutor()).thenReturn("Autor Editado");
        when(vista.getAnioTexto()).thenReturn("2022");
        when(vista.getCategoria()).thenReturn("Categoría Editada");
        when(vista.getEditorial()).thenReturn("Editorial Editada");
        when(vista.getEjemplaresTexto()).thenReturn("10");
        
        Libro libroOriginal = new Libro(1, "Título Original", "2020", "Autor", "Categoría", "Editorial", 5, 3, 1);
        when(libroDAO.obtenerLibroPorId(1)).thenReturn(libroOriginal);
        when(libroDAO.actualizarLibro(any())).thenReturn(true);

        controlador.guardarLibro();

        verify(libroDAO).actualizarLibro(argThat(libro -> 
            libro.getId() == 1 &&
            libro.getTitulo().equals("Título Editado") &&
            libro.getTotal() == 10 &&
            libro.getDisponibles() == 8 && // 10 total - 2 prestados (5-3=2)
            libro.getEmpleadoId() == 1));
        
        verify(vista).mostrarMensaje(contains("Libro actualizado exitosamente"), eq(JOptionPane.INFORMATION_MESSAGE));
        verify(vista).limpiarFormulario();
    }

    @Test
    void testGuardarLibro_modoEdicionNuevoTotalMenorQuePrestados_muestraError() {
        clearInvocations(vista, libroDAO);
        
        // Configurar modo edición
        controlador = new ControladorLibros(vista, libroDAO, empleado) {
            {
                this.modoEdicion = true;
                this.libroEditandoId = 1;
            }
        };

        when(vista.validarCamposLibro()).thenReturn(true);
        when(vista.getEjemplaresTexto()).thenReturn("2"); // Intenta reducir a 2
        
        Libro libroOriginal = new Libro(1, "Título", "2020", "Autor", "Categoría", "Editorial", 5, 1, 1);
        when(libroDAO.obtenerLibroPorId(1)).thenReturn(libroOriginal);

        controlador.guardarLibro();

        verify(vista).mostrarMensaje(
            contains("No puede reducir el total a 2 porque hay 4 libros prestados"),
            eq(JOptionPane.ERROR_MESSAGE)
        );
        verify(libroDAO, never()).actualizarLibro(any());
    }

    @Test
    void testEditarLibro_sinSeleccion_muestraAdvertencia() {
        clearInvocations(vista);
        
        when(vista.obtenerLibroIdSeleccionado()).thenReturn(null);

        controlador.editarLibro();

        verify(vista).mostrarMensaje("Seleccione un libro para editar", JOptionPane.WARNING_MESSAGE);
    }

    @Test
    void testEditarLibro_conSeleccion_cargaDatosYActivaModoEdicion() {
        clearInvocations(vista);
        
        when(vista.obtenerLibroIdSeleccionado()).thenReturn(1);
        Libro libro = new Libro(1, "Título", "2020", "Autor", "Categoría", "Editorial", 10, 7, 1);
        when(libroDAO.obtenerLibroPorId(1)).thenReturn(libro);

        controlador.editarLibro();

        verify(vista).cargarDatosEnFormulario(libro);
        verify(vista).cambiarAPestanaFormulario();
        verify(vista).mostrarMensaje(
            "Editando libro: Título. Total actual: 10, Disponibles: 7", 
            JOptionPane.INFORMATION_MESSAGE
        );
        
        assertTrue(controlador.isModoEdicion());
        assertEquals(1, controlador.getLibroEditandoId());
    }

    @Test
    void testEditarLibro_libroNoEncontrado_muestraError() {
        clearInvocations(vista);
        
        when(vista.obtenerLibroIdSeleccionado()).thenReturn(1);
        when(libroDAO.obtenerLibroPorId(1)).thenReturn(null);

        controlador.editarLibro();

        verify(vista).mostrarMensaje("Error al cargar los datos del libro", JOptionPane.ERROR_MESSAGE);
        assertFalse(controlador.isModoEdicion());
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
        verify(libroDAO).obtenerTodosLosLibros("id");
    }

    @Test
    void testBuscarLibros_conResultados_actualizaVista() {
        clearInvocations(vista, libroDAO);
        
        when(vista.getCriterioBusqueda()).thenReturn("titulo");
        when(vista.getTextoBusqueda()).thenReturn("Java");
        Libro libro = new Libro(2, "Java Programming", "2020", "Autor", "Categoría", "Editorial", 1, 1, 1);
        when(libroDAO.buscarLibros("titulo", "Java")).thenReturn(List.of(libro));

        controlador.buscarLibros();

        verify(vista).mostrarResultadosBusqueda(List.of(libro));
        verify(vista, never()).mostrarMensaje(contains("No se encontraron libros"), anyInt());
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
    void testLimpiarFormulario_reseteaModoEdicion() {
        // Configurar en modo edición
        controlador = new ControladorLibros(vista, libroDAO, empleado) {
            {
                this.modoEdicion = true;
                this.libroEditandoId = 1;
            }
        };

        controlador.limpiarFormulario();

        verify(vista).limpiarFormulario();
        assertFalse(controlador.isModoEdicion());
        assertNull(controlador.getLibroEditandoId());
    }

    @Test
    void testConstructorSinEmpleado_empleadoLogueadoEsNull() {
        ControladorLibros controladorSinEmpleado = new ControladorLibros(vista, libroDAO);
        assertNull(controladorSinEmpleado.getEmpleadoLogueado());
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
    void testOrdenarLibros_diferentesCriterios_mensajesCorrectos() {
        String[] criterios = {"id", "titulo", "autor", "categoria", "disponibles DESC", "anio DESC"};
        String[] mensajes = {
            "Ordenado por ID",
            "Ordenado por Título", 
            "Ordenado por Autor",
            "Ordenado por Categoría",
            "Ordenado por Disponibles (mayor a menor)",
            "Ordenado por Año (más reciente primero)"
        };

        for (int i = 0; i < criterios.length; i++) {
            clearInvocations(vista);
            controlador.ordenarLibros(criterios[i]);
            verify(vista).mostrarMensaje(mensajes[i], JOptionPane.INFORMATION_MESSAGE);
        }
    }
    
    @Test
    void testCargarLibros_sinResultados_muestraListaVacia() {
        clearInvocations(vista, libroDAO);

        when(libroDAO.obtenerTodosLosLibros("id")).thenReturn(List.of());

        controlador.cargarLibros();

        verify(vista).mostrarLibros(List.of());
    }
    @Test
    void testOrdenarLibros_mismoCriterio_noMuestraMensajeRepetido() {
        clearInvocations(vista, libroDAO);

        // Primera: cambia el orden
        controlador.ordenarLibros("titulo");

        clearInvocations(vista, libroDAO);

        // Segunda: mismo criterio
        controlador.ordenarLibros("titulo");

        verify(libroDAO).obtenerTodosLosLibros("titulo");
    }
    
    @Test
    void testGuardarLibro_camposInvalidos_noGuarda() {
        clearInvocations(vista, libroDAO);

        when(vista.validarCamposLibro()).thenReturn(false);

        controlador.guardarLibro();

        verify(libroDAO, never()).insertarLibro(any());
    }
    @Test
    void testEliminarLibro_sinSeleccion_noHaceNada() {
        clearInvocations(vista);

        when(vista.obtenerLibroIdSeleccionado()).thenReturn(null);

        controlador.eliminarLibro();

        verify(vista).mostrarMensaje("Seleccione un libro para eliminar", JOptionPane.WARNING_MESSAGE);
        verify(libroDAO, never()).eliminarLibro(anyInt());
    }
    @Test
    void testEliminarLibro_usuarioCancela_noElimina() {
        clearInvocations(vista, libroDAO);

        when(vista.obtenerLibroIdSeleccionado()).thenReturn(1);
        when(vista.obtenerTituloLibroSeleccionado()).thenReturn("Título Libro");
        when(vista.mostrarConfirmacion(anyString(), anyString())).thenReturn(false);

        controlador.eliminarLibro();

        verify(libroDAO, never()).eliminarLibro(anyInt());
    }


    @Test
    void testLimpiarFormulario_fueraDeModoEdicion() {
        clearInvocations(vista);

        controlador.limpiarFormulario();

        verify(vista).limpiarFormulario();
        assertFalse(controlador.isModoEdicion());
    }
    @Test
    void testGuardarLibro_errorEnDao_muestraError() {
        clearInvocations(vista, libroDAO);

        when(vista.validarCamposLibro()).thenReturn(true);
        when(vista.getTitulo()).thenReturn("ABC");
        when(vista.getAutor()).thenReturn("Autor");
        when(vista.getAnioTexto()).thenReturn("2023");
        when(vista.getCategoria()).thenReturn("Cat");
        when(vista.getEditorial()).thenReturn("Edit");
        when(vista.getEjemplaresTexto()).thenReturn("2");

        when(libroDAO.insertarLibro(any())).thenReturn(false);

        controlador.guardarLibro();

        verify(vista).mostrarMensaje("Error al guardar el libro", JOptionPane.ERROR_MESSAGE);
    }
    @Test
    void testConstructor_registraListeners() {
        verify(vista, times(1)).agregarGuardarListener(any());
        verify(vista, times(1)).agregarLimpiarListener(any());
        verify(vista, times(1)).agregarEditarListener(any());
        verify(vista, times(1)).agregarEliminarListener(any());
        verify(vista, times(1)).agregarActualizarListener(any());
        verify(vista, times(1)).agregarBuscarListener(any());
        verify(vista, times(1)).setOrdenarListener(any());
    }


}