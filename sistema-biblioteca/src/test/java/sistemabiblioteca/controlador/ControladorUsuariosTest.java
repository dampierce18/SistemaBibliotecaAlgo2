package sistemabiblioteca.controlador;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import sistemabiblioteca.dao.UsuarioDAO;
import sistemabiblioteca.modelo.Usuario;
import sistemabiblioteca.vista.PanelUsuarios;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ControladorUsuariosTest {

    @Mock
    PanelUsuarios vista;

    @Mock
    UsuarioDAO usuarioDAO;

    private ControladorUsuarios controlador;

    @BeforeEach
    void configurar() {
        controlador = new ControladorUsuarios(vista, usuarioDAO);
    }

    @Test
    void testConstructor_configuraEventosYCargaUsuarios() {
        // Verificar que se configuran los eventos
        verify(vista).agregarGuardarUsuarioListener(any());
        verify(vista).agregarLimpiarUsuarioListener(any());
        verify(vista).agregarEditarUsuarioListener(any());
        verify(vista).agregarEliminarUsuarioListener(any());
        verify(vista).agregarActualizarUsuariosListener(any());
        verify(vista).agregarBuscarUsuarioListener(any());

        // Verificar que se cargan los usuarios iniciales
        verify(usuarioDAO).obtenerTodosLosUsuarios();
    }

    @Test
    void testGuardarUsuario_exitoso() {
        clearInvocations(vista, usuarioDAO);
        
        when(vista.validarCamposUsuario()).thenReturn(true);
        when(vista.getNombre()).thenReturn("Juan");
        when(vista.getApellidoPaterno()).thenReturn("Pérez");
        when(vista.getApellidoMaterno()).thenReturn("Gómez");
        when(vista.getDomicilio()).thenReturn("Calle 123");
        when(vista.getTelefono()).thenReturn("555-1234");
        when(usuarioDAO.insertarUsuario(any(Usuario.class))).thenReturn(true);

        controlador.guardarUsuario();

        verify(usuarioDAO).insertarUsuario(argThat(usuario -> 
            usuario.getNombre().equals("Juan") && 
            usuario.getApellidoPaterno().equals("Pérez") &&
            usuario.getApellidoMaterno().equals("Gómez") &&
            usuario.getDomicilio().equals("Calle 123") &&
            usuario.getTelefono().equals("555-1234")
        ));
        verify(vista).limpiarFormulario();
        verify(vista).mostrarMensaje("Usuario guardado exitosamente", javax.swing.JOptionPane.INFORMATION_MESSAGE);
        verify(usuarioDAO, times(1)).obtenerTodosLosUsuarios(); // Solo después de guardar
    }

    @Test
    void testGuardarUsuario_insercionFallida_muestraError() {
        clearInvocations(vista, usuarioDAO);
        
        when(vista.validarCamposUsuario()).thenReturn(true);
        when(vista.getNombre()).thenReturn("Juan");
        when(vista.getApellidoPaterno()).thenReturn("Pérez");
        when(vista.getApellidoMaterno()).thenReturn("Gómez");
        when(vista.getDomicilio()).thenReturn("Calle 123");
        when(vista.getTelefono()).thenReturn("555-1234");
        when(usuarioDAO.insertarUsuario(any(Usuario.class))).thenReturn(false);

        controlador.guardarUsuario();

        verify(vista).mostrarMensaje("Error al guardar el usuario", javax.swing.JOptionPane.ERROR_MESSAGE);
        verify(vista, never()).limpiarFormulario();
    }

    @Test
    void testGuardarUsuario_validacionFallida_noGuarda() {
        clearInvocations(vista, usuarioDAO);
        
        when(vista.validarCamposUsuario()).thenReturn(false);

        controlador.guardarUsuario();

        verify(usuarioDAO, never()).insertarUsuario(any());
    }

    @Test
    void testGuardarUsuario_excepcion_muestraError() {
        clearInvocations(vista, usuarioDAO);
        
        when(vista.validarCamposUsuario()).thenReturn(true);
        when(vista.getNombre()).thenReturn("Juan");
        when(vista.getApellidoPaterno()).thenReturn("Pérez");
        when(vista.getApellidoMaterno()).thenReturn("Gómez");
        when(vista.getDomicilio()).thenReturn("Calle 123");
        when(vista.getTelefono()).thenReturn("555-1234");
        when(usuarioDAO.insertarUsuario(any(Usuario.class))).thenThrow(new RuntimeException("Error de BD"));

        controlador.guardarUsuario();

        verify(vista).mostrarMensaje("Error: Error de BD", javax.swing.JOptionPane.ERROR_MESSAGE);
    }

    @Test
    void testEditarUsuario_sinSeleccion_muestraAdvertencia() {
        clearInvocations(vista);
        
        when(vista.obtenerUsuarioIdSeleccionado()).thenReturn(null);

        controlador.editarUsuario();

        verify(vista).mostrarMensaje("Seleccione un usuario para editar", javax.swing.JOptionPane.WARNING_MESSAGE);
    }

    @Test
    void testEditarUsuario_conSeleccion_muestraInfo() {
        clearInvocations(vista);
        
        when(vista.obtenerUsuarioIdSeleccionado()).thenReturn(1);

        controlador.editarUsuario();

        verify(vista).mostrarMensaje("Funcionalidad de edición en desarrollo", javax.swing.JOptionPane.INFORMATION_MESSAGE);
    }

    @Test
    void testEliminarUsuario_sinSeleccion_muestraAdvertencia() {
        clearInvocations(vista, usuarioDAO);
        
        when(vista.obtenerUsuarioIdSeleccionado()).thenReturn(null);

        controlador.eliminarUsuario();

        verify(vista).mostrarMensaje("Seleccione un usuario para eliminar", javax.swing.JOptionPane.WARNING_MESSAGE);
        verify(usuarioDAO, never()).eliminarUsuario(anyInt());
    }

    @Test
    void testEliminarUsuario_confirmacionFalse_noElimina() {
        clearInvocations(vista, usuarioDAO);
        
        when(vista.obtenerUsuarioIdSeleccionado()).thenReturn(1);
        when(vista.obtenerNombreUsuarioSeleccionado()).thenReturn("Juan Pérez");
        when(vista.mostrarConfirmacion(anyString(), anyString())).thenReturn(false);

        controlador.eliminarUsuario();

        verify(usuarioDAO, never()).eliminarUsuario(anyInt());
    }

    @Test
    void testEliminarUsuario_confirmacionTrue_eliminacionExitosa() {
        clearInvocations(vista, usuarioDAO);
        
        when(vista.obtenerUsuarioIdSeleccionado()).thenReturn(1);
        when(vista.obtenerNombreUsuarioSeleccionado()).thenReturn("Juan Pérez");
        when(vista.mostrarConfirmacion(anyString(), anyString())).thenReturn(true);
        when(usuarioDAO.eliminarUsuario(1)).thenReturn(true);

        controlador.eliminarUsuario();

        verify(usuarioDAO).eliminarUsuario(1);
        verify(vista).mostrarMensaje("Usuario eliminado exitosamente", javax.swing.JOptionPane.INFORMATION_MESSAGE);
        verify(usuarioDAO, times(1)).obtenerTodosLosUsuarios(); // Solo después de eliminar
    }

    @Test
    void testEliminarUsuario_confirmacionTrue_eliminacionFallida() {
        clearInvocations(vista, usuarioDAO);
        
        when(vista.obtenerUsuarioIdSeleccionado()).thenReturn(1);
        when(vista.obtenerNombreUsuarioSeleccionado()).thenReturn("Juan Pérez");
        when(vista.mostrarConfirmacion(anyString(), anyString())).thenReturn(true);
        when(usuarioDAO.eliminarUsuario(1)).thenReturn(false);

        controlador.eliminarUsuario();

        verify(usuarioDAO).eliminarUsuario(1);
        verify(vista).mostrarMensaje("Error al eliminar el usuario", javax.swing.JOptionPane.ERROR_MESSAGE);
        verify(usuarioDAO, never()).obtenerTodosLosUsuarios(); // No recarga si falla
    }

    @Test
    void testCargarUsuarios_actualizaVista() {
        clearInvocations(vista, usuarioDAO);
        
        Usuario usuario1 = new Usuario();
        usuario1.setId(1);
        usuario1.setNombre("Juan");
        usuario1.setApellidoPaterno("Pérez");
        
        Usuario usuario2 = new Usuario();
        usuario2.setId(2);
        usuario2.setNombre("María");
        usuario2.setApellidoPaterno("Gómez");
        
        when(usuarioDAO.obtenerTodosLosUsuarios()).thenReturn(List.of(usuario1, usuario2));

        controlador.cargarUsuarios();

        verify(vista).mostrarUsuarios(List.of(usuario1, usuario2));
    }

    @Test
    void testCargarUsuarios_listaVacia_actualizaVista() {
        clearInvocations(vista, usuarioDAO);
        
        when(usuarioDAO.obtenerTodosLosUsuarios()).thenReturn(List.of());

        controlador.cargarUsuarios();

        verify(vista).mostrarUsuarios(List.of());
    }

    @Test
    void testBuscarUsuarios_busquedaVacia_muestraAdvertencia() {
        clearInvocations(vista, usuarioDAO);
        
        when(vista.getTextoBusquedaUsuario()).thenReturn("");

        controlador.buscarUsuarios();

        verify(vista).mostrarMensaje("Ingrese un valor para buscar", javax.swing.JOptionPane.WARNING_MESSAGE);
        verify(usuarioDAO, never()).buscarUsuarios(anyString(), anyString());
    }

    @Test
    void testBuscarUsuarios_sinResultados_muestraInfo() {
        clearInvocations(vista, usuarioDAO);
        
        when(vista.getCriterioBusquedaUsuario()).thenReturn("nombre");
        when(vista.getTextoBusquedaUsuario()).thenReturn("Usuario Inexistente");
        when(usuarioDAO.buscarUsuarios("nombre", "Usuario Inexistente")).thenReturn(List.of());

        controlador.buscarUsuarios();

        verify(vista).mostrarResultadosBusqueda(List.of());
        verify(vista).mostrarMensaje("No se encontraron usuarios", javax.swing.JOptionPane.INFORMATION_MESSAGE);
    }

    @Test
    void testBuscarUsuarios_conResultados_actualizaVista() {
        clearInvocations(vista, usuarioDAO);
        
        when(vista.getCriterioBusquedaUsuario()).thenReturn("nombre");
        when(vista.getTextoBusquedaUsuario()).thenReturn("Juan");
        
        Usuario usuario = new Usuario();
        usuario.setId(1);
        usuario.setNombre("Juan Pérez");
        
        when(usuarioDAO.buscarUsuarios("nombre", "Juan")).thenReturn(List.of(usuario));

        controlador.buscarUsuarios();

        verify(vista).mostrarResultadosBusqueda(List.of(usuario));
        verify(vista, never()).mostrarMensaje(contains("No se encontraron usuarios"), anyInt());
    }

    @Test
    void testObtenerTotalUsuarios_retornaValorDao() {
        clearInvocations(usuarioDAO);
        
        when(usuarioDAO.contarTotalUsuarios()).thenReturn(25);

        int resultado = controlador.obtenerTotalUsuarios();

        assertEquals(25, resultado);
        verify(usuarioDAO).contarTotalUsuarios();
    }

    @Test
    void testObtenerTotalUsuarios_propagaExcepcion() {
        clearInvocations(usuarioDAO);
        
        when(usuarioDAO.contarTotalUsuarios()).thenThrow(new RuntimeException("Error de BD"));

        assertThrows(RuntimeException.class, () -> controlador.obtenerTotalUsuarios());
        verify(usuarioDAO).contarTotalUsuarios();
    }
}