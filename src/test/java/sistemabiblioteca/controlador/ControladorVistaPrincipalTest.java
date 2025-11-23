package sistemabiblioteca.controlador;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.junit.jupiter.MockitoSettings;
import org.mockito.quality.Strictness;
import sistemabiblioteca.modelo.Empleado;
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
    @Mock PanelEmpleados panelEmpleados;
    @Mock ControladorLibros controladorLibros;
    @Mock ControladorPrestamos controladorPrestamos;
    @Mock ControladorUsuarios controladorUsuarios;
    @Mock ControladorReportes controladorReportes;
    @Mock ControladorEmpleados controladorEmpleados;
    @Mock JButton btnReportes;
    @Mock JLabel lblTotalLibros;
    @Mock JLabel lblPrestamosActivos;
    @Mock JLabel lblAtrasados;
    @Mock JLabel lblTotalUsuarios;
    @Mock Empleado empleadoAdmin;
    @Mock Empleado empleadoNormal;

    private ControladorVistaPrincipal controlador;

    private void configurarMocksCompletos() {
        when(vista.getBtnReportes()).thenReturn(btnReportes);
        when(vista.getPanelLibros()).thenReturn(panelLibros);
        when(vista.getPanelUsuarios()).thenReturn(panelUsuarios);
        when(vista.getPanelPrestamos()).thenReturn(panelPrestamos);
        when(vista.getPanelReportes()).thenReturn(panelReportes);
        when(vista.getPanelEmpleados()).thenReturn(panelEmpleados);
        
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
        doNothing().when(vista).agregarListenerEmpleados(any());
        doNothing().when(vista).agregarListenerSalir(any());
        
        doNothing().when(vista).setVisible(true);
        doNothing().when(vista).mostrarMensaje(anyString());
        doNothing().when(vista).ocultarPestanaReportes();
        doNothing().when(vista).ocultarPestanaEmpleados();
        
        when(vista.confirmarSalida()).thenReturn(true);
        
        // Configurar empleados mock
        when(empleadoAdmin.getUsuario()).thenReturn("admin");
        when(empleadoAdmin.getRol()).thenReturn("ADMIN");
        when(empleadoAdmin.getNombreCompleto()).thenReturn("Administrador Sistema");
        
        when(empleadoNormal.getUsuario()).thenReturn("empleado");
        when(empleadoNormal.getRol()).thenReturn("EMPLEADO");
        when(empleadoNormal.getNombreCompleto()).thenReturn("Juan Pérez");
    }

    private ControladorVistaPrincipal crearControladorConEmpleado(Empleado empleado) {
        return new ControladorVistaPrincipal(
            vista, empleado, 
            controladorLibros, controladorPrestamos, 
            controladorUsuarios, controladorReportes,
            controladorEmpleados
        );
    }

    // Tests que funcionan con el nuevo constructor
    @Test
    void testMostrarPanelPrestamos() {
        configurarMocksCompletos();
        doNothing().when(vista).mostrarPanelPrestamos();

        controlador = crearControladorConEmpleado(empleadoAdmin);
        controlador.mostrarPanelPrestamos();

        verify(vista).mostrarPanelPrestamos();
    }

    @Test
    void testMostrarPanelUsuarios() {
        configurarMocksCompletos();
        doNothing().when(vista).mostrarPanelUsuarios();

        controlador = crearControladorConEmpleado(empleadoAdmin);
        controlador.mostrarPanelUsuarios();

        verify(vista).mostrarPanelUsuarios();
    }

    @Test
    void testMostrarPanelLibros() {
        configurarMocksCompletos();
        doNothing().when(vista).mostrarPanelLibros();

        controlador = crearControladorConEmpleado(empleadoAdmin);
        controlador.mostrarPanelLibros();

        verify(vista).mostrarPanelLibros();
    }

    @Test
    void testMostrarPanelReportes() {
        configurarMocksCompletos();
        doNothing().when(vista).mostrarPanelReportes();

        controlador = crearControladorConEmpleado(empleadoAdmin);
        controlador.mostrarPanelReportes();

        verify(vista).mostrarPanelReportes();
    }

    @Test
    void testMostrarPanelEmpleados() {
        configurarMocksCompletos();
        doNothing().when(vista).mostrarPanelEmpleados();

        controlador = crearControladorConEmpleado(empleadoAdmin);
        controlador.mostrarPanelEmpleados();

        verify(vista).mostrarPanelEmpleados();
    }

    @Test
    void testSalirSistema_conConfirmacionFalse() {
        configurarMocksCompletos();
        when(vista.confirmarSalida()).thenReturn(false);

        controlador = crearControladorConEmpleado(empleadoAdmin);
        controlador.salirSistema();

        verify(vista).confirmarSalida();
        // No debería llamar a System.exit si confirmación es false
    }

    @Test
    void testConfigurarEventos_llamaTodosLosListeners() {
        configurarMocksCompletos();

        controlador = crearControladorConEmpleado(empleadoAdmin);

        verify(vista).agregarListenerPrincipal(any());
        verify(vista).agregarListenerPrestamos(any());
        verify(vista).agregarListenerUsuarios(any());
        verify(vista).agregarListenerLibros(any());
        verify(vista).agregarListenerReportes(any());
        verify(vista).agregarListenerEmpleados(any());
        verify(vista).agregarListenerSalir(any());
    }


    @Test
    void testRefrescarReportes_conControladorNull_noLanzaExcepcion() {
        configurarMocksCompletos();

        controlador = new ControladorVistaPrincipal(
            vista, empleadoAdmin, 
            controladorLibros, controladorPrestamos, 
            controladorUsuarios, null,
            controladorEmpleados
        );

        assertDoesNotThrow(() -> controlador.refrescarReportes());
    }
    
    @Test
    void testRefrescarReportes_conControladorNoNull_llamaRefrescarReportes() {
        configurarMocksCompletos();
        doNothing().when(controladorReportes).refrescarReportes();

        controlador = crearControladorConEmpleado(empleadoAdmin);
        controlador.refrescarReportes();

        verify(controladorReportes).refrescarReportes();
    }

    @Test
    void testMostrarPanelPrincipal() {
        configurarMocksCompletos();
        doNothing().when(vista).mostrarPanelPrincipal();

        controlador = crearControladorConEmpleado(empleadoAdmin);
        controlador.mostrarPanelPrincipal();

        verify(vista).mostrarPanelPrincipal();
    }

    @Test
    void testCargarDatos_conExcepcion_configuraValoresPorDefecto() {
        configurarMocksCompletos();
        
        when(controladorLibros.obtenerTotalLibros()).thenThrow(new RuntimeException("Error de base de datos"));
        
        assertDoesNotThrow(() -> {
            controlador = crearControladorConEmpleado(empleadoAdmin);
        });

        verify(lblTotalLibros).setText("0");
        verify(lblPrestamosActivos).setText("0");
        verify(lblAtrasados).setText("0");
        verify(lblTotalUsuarios).setText("0");
    }

    @Test
    void testCargarDatos_exitoso_configuraValoresCorrectos() {
        configurarMocksCompletos();
        
        when(controladorLibros.obtenerTotalLibros()).thenReturn(150);
        when(controladorPrestamos.obtenerPrestamosActivos()).thenReturn(25);
        when(controladorPrestamos.obtenerPrestamosAtrasados()).thenReturn(3);
        when(controladorUsuarios.obtenerTotalUsuarios()).thenReturn(75);

        controlador = crearControladorConEmpleado(empleadoAdmin);

        verify(lblTotalLibros).setText("150");
        verify(lblPrestamosActivos).setText("25");
        verify(lblAtrasados).setText("3");
        verify(lblTotalUsuarios).setText("75");
    }

    @Test
    void testEsAdmin_conEmpleadoAdmin_retornaTrue() {
        configurarMocksCompletos();

        controlador = crearControladorConEmpleado(empleadoAdmin);

        assertTrue(controlador.esAdmin());
    }

    @Test
    void testEsAdmin_conEmpleadoNormal_retornaFalse() {
        configurarMocksCompletos();

        controlador = crearControladorConEmpleado(empleadoNormal);

        assertFalse(controlador.esAdmin());
    }

    @Test
    void testConfigurarPermisos_empleadoNormal_ocultaFuncionalidades() {
        configurarMocksCompletos();

        controlador = crearControladorConEmpleado(empleadoNormal);

        verify(vista).ocultarPestanaReportes();
        verify(vista).ocultarPestanaEmpleados();
        verify(panelLibros).setModoEmpleado();
        verify(panelUsuarios).setModoEmpleado();
    }

    @Test
    void testConfigurarPermisos_empleadoAdmin_noOcultaFuncionalidades() {
        configurarMocksCompletos();

        controlador = crearControladorConEmpleado(empleadoAdmin);

        verify(vista, never()).ocultarPestanaReportes();
        verify(vista, never()).ocultarPestanaEmpleados();
        verify(panelLibros, never()).setModoEmpleado();
        verify(panelUsuarios, never()).setModoEmpleado();
    }

    @Test
    void testMostrarMensajeBienvenida_empleadoAdmin() {
        configurarMocksCompletos();

        controlador = crearControladorConEmpleado(empleadoAdmin);

        verify(vista).mostrarMensaje("Bienvenido: Administrador Sistema (Administrador)");
    }

    @Test
    void testMostrarMensajeBienvenida_empleadoNormal() {
        configurarMocksCompletos();

        controlador = crearControladorConEmpleado(empleadoNormal);

        verify(vista).mostrarMensaje("Bienvenido: Juan Pérez (Empleado)");
    }

    @Test
    void testGetEmpleadoLogueado() {
        configurarMocksCompletos();

        controlador = crearControladorConEmpleado(empleadoAdmin);

        assertEquals(empleadoAdmin, controlador.getEmpleadoLogueado());
    }

    @Test
    void testGetUsuarioLogueado() {
        configurarMocksCompletos();

        controlador = crearControladorConEmpleado(empleadoAdmin);

        assertEquals("admin", controlador.getUsuarioLogueado());
    }

}