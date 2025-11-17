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
        when(vista.getBtnReportes()).thenReturn(btnReportes);
        when(vista.getPanelLibros()).thenReturn(panelLibros);
        when(vista.getPanelUsuarios()).thenReturn(panelUsuarios);
        when(vista.getPanelPrestamos()).thenReturn(panelPrestamos);
        when(vista.getPanelReportes()).thenReturn(panelReportes);
        
        when(vista.getLblTotalLibros()).thenReturn(lblTotalLibros);
        when(vista.getLblPrestamosActivos()).thenReturn(lblPrestamosActivos);
        when(vista.getLblAtrasados()).thenReturn(lblAtrasados);
        when(vista.getLblTotalUsuarios()).thenReturn(lblTotalUsuarios);
        
        when(controladorLibros.obtenerTotalLibros()).thenReturn(0);
        when(controladorPrestamos.obtenerPrestamosActivos()).thenReturn(0);
        when(controladorPrestamos.obtenerPrestamosAtrasados()).thenReturn(0);
        when(controladorUsuarios.obtenerTotalUsuarios()).thenReturn(0);
        
        doNothing().when(vista).agregarListenerPrincipal(any());
        doNothing().when(vista).agregarListenerPrestamos(any());
        doNothing().when(vista).agregarListenerUsuarios(any());
        doNothing().when(vista).agregarListenerLibros(any());
        doNothing().when(vista).agregarListenerReportes(any());
        doNothing().when(vista).agregarListenerSalir(any());
        
        doNothing().when(vista).setVisible(true);
        doNothing().when(vista).mostrarMensaje(anyString());
        
        when(vista.confirmarSalida()).thenReturn(true);
    }

    private ControladorVistaPrincipal crearControlador(String usuario) {
        return new ControladorVistaPrincipal(
            vista, usuario, 
            controladorLibros, controladorPrestamos, 
            controladorUsuarios, controladorReportes
        );
    }


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

        verify(vista).confirmarSalida();
    }

    @Test
    void testConfigurarEventos_llamaTodosLosListeners() {
        // Configurar mocks
        configurarMocksCompletos();

        // Ejecutar
        controlador = crearControlador("admin");

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

        assertDoesNotThrow(() -> {
            controlador = new ControladorVistaPrincipal(
                vista, null, 
                controladorLibros, controladorPrestamos, 
                controladorUsuarios, controladorReportes
            );
        });

        assertFalse(controlador.esAdmin());
    }

    @Test
    void testRefrescarReportes_conControladorNull_noLanzaExcepcion() {
        // Configurar mocks
        configurarMocksCompletos();

        controlador = new ControladorVistaPrincipal(
            vista, "admin", 
            controladorLibros, controladorPrestamos, 
            controladorUsuarios, null 
        );

        assertDoesNotThrow(() -> controlador.refrescarReportes());
    }
    
    
    
    @Test
    void testRefrescarReportes_conControladorNoNull_llamaRefrescarReportes() {
        // Configurar mocks
        configurarMocksCompletos();
        doNothing().when(controladorReportes).refrescarReportes();

        // Ejecutar
        controlador = crearControlador("admin");
        controlador.refrescarReportes();

        // Verificar que se llama al método refrescarReportes
        verify(controladorReportes).refrescarReportes();
    }

    @Test
    void testMostrarPanelPrincipal() {
        // Configurar mocks
        configurarMocksCompletos();
        doNothing().when(vista).mostrarPanelPrincipal();

        // Ejecutar
        controlador = crearControlador("admin");
        controlador.mostrarPanelPrincipal();

        // Verificar
        verify(vista).mostrarPanelPrincipal();
    }

    @Test
    void testCargarDatos_conExcepcion_configuraValoresPorDefecto() {
        // Configurar mocks
        configurarMocksCompletos();
        
        // Simular excepción en uno de los métodos
        when(controladorLibros.obtenerTotalLibros()).thenThrow(new RuntimeException("Error de base de datos"));
        
        // Ejecutar - no debería lanzar excepción
        assertDoesNotThrow(() -> {
            controlador = crearControlador("admin");
        });

        verify(lblTotalLibros).setText("0");
        verify(lblPrestamosActivos).setText("0");
        verify(lblAtrasados).setText("0");
        verify(lblTotalUsuarios).setText("0");
    }

    @Test
    void testCargarDatos_exitoso_configuraValoresCorrectos() {
        // Configurar mocks
        configurarMocksCompletos();
        
        // Configurar valores específicos
        when(controladorLibros.obtenerTotalLibros()).thenReturn(150);
        when(controladorPrestamos.obtenerPrestamosActivos()).thenReturn(25);
        when(controladorPrestamos.obtenerPrestamosAtrasados()).thenReturn(3);
        when(controladorUsuarios.obtenerTotalUsuarios()).thenReturn(75);

        // Ejecutar
        controlador = crearControlador("admin");

        // Verificar que los labels tienen los valores correctos
        verify(lblTotalLibros).setText("150");
        verify(lblPrestamosActivos).setText("25");
        verify(lblAtrasados).setText("3");
        verify(lblTotalUsuarios).setText("75");
    }

    @Test
    void testEsAdmin_conUsuarioAdmin_retornaTrue() {
        // Configurar mocks
        configurarMocksCompletos();

        // Ejecutar con usuario "admin"
        controlador = crearControlador("admin");

        // Verificar
        assertTrue(controlador.esAdmin());
    }

    @Test
    void testEsAdmin_conUsuarioNoAdmin_retornaFalse() {
        // Configurar mocks
        configurarMocksCompletos();

        // Ejecutar con usuario no admin
        controlador = crearControlador("empleado");

        // Verificar
        assertFalse(controlador.esAdmin());
    }

}