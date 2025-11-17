package sistemabiblioteca.controlador;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.junit.jupiter.MockitoSettings;
import org.mockito.quality.Strictness;
import sistemabiblioteca.vista.*;

import javax.swing.*;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;
import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(MockitoExtension.class)
@MockitoSettings(strictness = Strictness.LENIENT)
class ControladorVistaPrincipalTest {

    @Mock VistaPrincipal vista;
    @Mock PanelLibros panelLibros;
    @Mock PanelPrestamos panelPrestamos;
    @Mock PanelUsuarios panelUsuarios;
    @Mock PanelReportes panelReportes;
    @Mock ControladorLibros controladorLibros;
    @Mock ControladorPrestamos controladorPrestamos;
    @Mock ControladorUsuarios controladorUsuarios;
    @Mock ControladorReportes controladorReportes;
    @Mock JButton btnReportes;
    @Mock JLabel lblTotalLibros;
    @Mock JLabel lblPrestamosActivos;
    @Mock JLabel lblAtrasados;
    @Mock JLabel lblTotalUsuarios;

    private ControladorVistaPrincipal controlador;

    private void configurarMocksCompletos() {
        // Configurar TODOS los mocks que el constructor necesita
        when(vista.getBtnReportes()).thenReturn(btnReportes);
        when(vista.getPanelLibros()).thenReturn(panelLibros);
        when(vista.getPanelUsuarios()).thenReturn(panelUsuarios);
        when(vista.getPanelPrestamos()).thenReturn(panelPrestamos);
        when(vista.getPanelReportes()).thenReturn(panelReportes);
        
        // Configurar los labels que cargarDatos() necesita - CRÍTICO
        when(vista.getLblTotalLibros()).thenReturn(lblTotalLibros);
        when(vista.getLblPrestamosActivos()).thenReturn(lblPrestamosActivos);
        when(vista.getLblAtrasados()).thenReturn(lblAtrasados);
        when(vista.getLblTotalUsuarios()).thenReturn(lblTotalUsuarios);
        
        // Configurar datos por defecto para cargarDatos()
        when(controladorLibros.obtenerTotalLibros()).thenReturn(0);
        when(controladorPrestamos.obtenerPrestamosActivos()).thenReturn(0);
        when(controladorPrestamos.obtenerPrestamosAtrasados()).thenReturn(0);
        when(controladorUsuarios.obtenerTotalUsuarios()).thenReturn(0);
        
        // Configurar todos los listeners
        doNothing().when(vista).agregarListenerPrincipal(any());
        doNothing().when(vista).agregarListenerPrestamos(any());
        doNothing().when(vista).agregarListenerUsuarios(any());
        doNothing().when(vista).agregarListenerLibros(any());
        doNothing().when(vista).agregarListenerReportes(any());
        doNothing().when(vista).agregarListenerSalir(any());
        
        // Configurar métodos void básicos
        doNothing().when(vista).setVisible(true);
        doNothing().when(vista).mostrarMensaje(anyString());
        
        // Configurar confirmarSalida por defecto
        when(vista.confirmarSalida()).thenReturn(true);
    }

    private ControladorVistaPrincipal crearControlador(String usuario) {
        return new ControladorVistaPrincipal(
            vista, usuario, 
            controladorLibros, controladorPrestamos, 
            controladorUsuarios, controladorReportes
        );
    }

    // Tests existentes...

    @Test
    void testMostrarPanelPrestamos() {
        // Configurar mocks
        configurarMocksCompletos();
        doNothing().when(vista).mostrarPanelPrestamos();

        // Ejecutar
        controlador = crearControlador("admin");
        controlador.mostrarPanelPrestamos();

        // Verificar
        verify(vista).mostrarPanelPrestamos();
    }

    @Test
    void testMostrarPanelUsuarios() {
        // Configurar mocks
        configurarMocksCompletos();
        doNothing().when(vista).mostrarPanelUsuarios();

        // Ejecutar
        controlador = crearControlador("admin");
        controlador.mostrarPanelUsuarios();

        // Verificar
        verify(vista).mostrarPanelUsuarios();
    }

    @Test
    void testMostrarPanelLibros() {
        // Configurar mocks
        configurarMocksCompletos();
        doNothing().when(vista).mostrarPanelLibros();

        // Ejecutar
        controlador = crearControlador("admin");
        controlador.mostrarPanelLibros();

        // Verificar
        verify(vista).mostrarPanelLibros();
    }

    @Test
    void testMostrarPanelReportes() {
        // Configurar mocks
        configurarMocksCompletos();
        doNothing().when(vista).mostrarPanelReportes();

        // Ejecutar
        controlador = crearControlador("admin");
        controlador.mostrarPanelReportes();

        // Verificar
        verify(vista).mostrarPanelReportes();
    }


    @Test
    void testSalirSistema_conConfirmacionFalse() {
        // Configurar mocks
        configurarMocksCompletos();
        when(vista.confirmarSalida()).thenReturn(false);

        // Ejecutar
        controlador = crearControlador("admin");
        controlador.salirSistema();

        // Verificar que NO se llama a System.exit
        verify(vista).confirmarSalida();
        // No debería salir del sistema cuando confirmación es false
    }

    @Test
    void testConfigurarEventos_llamaTodosLosListeners() {
        // Configurar mocks
        configurarMocksCompletos();

        // Ejecutar
        controlador = crearControlador("admin");

        // Verificar que se configuran todos los listeners
        verify(vista).agregarListenerPrincipal(any());
        verify(vista).agregarListenerPrestamos(any());
        verify(vista).agregarListenerUsuarios(any());
        verify(vista).agregarListenerLibros(any());
        verify(vista).agregarListenerReportes(any());
        verify(vista).agregarListenerSalir(any());
    }

    @Test
    void testConstructor_conUsuarioNull_noLanzaExcepcion() {
        // Configurar mocks
        configurarMocksCompletos();

        // Ejecutar con usuario null
        assertDoesNotThrow(() -> {
            controlador = new ControladorVistaPrincipal(
                vista, null, 
                controladorLibros, controladorPrestamos, 
                controladorUsuarios, controladorReportes
            );
        });

        // Verificar que esAdmin funciona con null
        assertFalse(controlador.esAdmin());
    }

    @Test
    void testRefrescarReportes_conControladorNull_noLanzaExcepcion() {
        // Configurar mocks
        configurarMocksCompletos();

        // Crear controlador con controladorReportes null
        controlador = new ControladorVistaPrincipal(
            vista, "admin", 
            controladorLibros, controladorPrestamos, 
            controladorUsuarios, null  // controladorReportes null
        );

        // Ejecutar - no debería lanzar excepción
        assertDoesNotThrow(() -> controlador.refrescarReportes());
    }
}