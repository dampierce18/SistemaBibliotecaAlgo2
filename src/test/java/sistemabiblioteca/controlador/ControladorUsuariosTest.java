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
    void testGuardarUsuario_validacionFallida_noGuarda() {
        clearInvocations(vista, usuarioDAO);
        
        when(vista.validarCamposUsuario()).thenReturn(false);

        controlador.guardarUsuario();

        verify(usuarioDAO, never()).insertarUsuario(any());
    }


    @Test
    void testEditarUsuario_sinSeleccion_muestraAdvertencia() {
        clearInvocations(vista);
        
        when(vista.obtenerUsuarioIdSeleccionado()).thenReturn(null);

        controlador.editarUsuario();

        verify(vista).mostrarMensaje("Seleccione un usuario para editar", javax.swing.JOptionPane.WARNING_MESSAGE);
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