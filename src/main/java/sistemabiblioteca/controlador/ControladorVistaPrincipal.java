package sistemabiblioteca.controlador;

import sistemabiblioteca.vista.VistaPrincipal;
import javax.swing.*;

public class ControladorVistaPrincipal {
    private VistaPrincipal vista;
    private ControladorLibros controladorLibros;
    private ControladorPrestamos controladorPrestamos;
    private ControladorUsuarios controladorUsuarios;
    private ControladorReportes controladorReportes;
    private String usuarioLogueado;
    
    public ControladorVistaPrincipal(String usuarioLogueado) {
    	this.usuarioLogueado = usuarioLogueado;
        this.vista = new VistaPrincipal();
        this.controladorLibros = new ControladorLibros(vista.getPanelLibros());
        this.controladorPrestamos = new ControladorPrestamos(vista.getPanelPrestamos());
        this.controladorUsuarios = new ControladorUsuarios(vista.getPanelUsuarios());
        this.controladorReportes = new ControladorReportes(vista.getPanelReportes());
        configurarPermisos();
        cargarDatos();
        configurarEventos();
        vista.setVisible(true);
        
        String rol = esAdmin() ? "Administrador" : "Empleado";
        JOptionPane.showMessageDialog(vista, "Bienvenido: " + usuarioLogueado + " (" + rol + ")", "Bienvenido", JOptionPane.INFORMATION_MESSAGE);
    }
    
    private void configurarPermisos() {
        if (!esAdmin()) {
            ocultarFuncionalidadesEmpleado();
        }
    }
    
    private void ocultarFuncionalidadesEmpleado() {
        vista.getBtnReportes().setVisible(false);
        vista.getPanelLibros().setModoEmpleado();
        vista.getPanelUsuarios().setModoEmpleado();
    }
    
    private boolean esAdmin() {
        return "admin".equals(usuarioLogueado);
    }
    
    private void configurarEventos() {
        vista.agregarListenerPrincipal(e -> mostrarPanelPrincipal());
        vista.agregarListenerPrestamos(e -> mostrarPanelPrestamos());
        vista.agregarListenerUsuarios(e -> mostrarPanelUsuarios());
        vista.agregarListenerLibros(e -> mostrarPanelLibros());
        vista.agregarListenerReportes(e -> mostrarPanelReportes());
        vista.agregarListenerSalir(e -> salirSistema());
    }
    
    private void cargarDatos() {
        try {
            int totalLibros = controladorLibros.obtenerTotalLibros();
            vista.getLblTotalLibros().setText(String.valueOf(totalLibros));
            
            int prestamosActivos = controladorPrestamos.obtenerPrestamosActivos();
            vista.getLblPrestamosActivos().setText(String.valueOf(prestamosActivos));
            
            int prestamosAtrasados = controladorPrestamos.obtenerPrestamosAtrasados();
            vista.getLblAtrasados().setText(String.valueOf(prestamosAtrasados));
            
            int totalUsuarios = controladorUsuarios.obtenerTotalUsuarios();
            vista.getLblTotalUsuarios().setText(String.valueOf(totalUsuarios));
        } catch (Exception e) {
            vista.getLblTotalLibros().setText("0");
            vista.getLblPrestamosActivos().setText("0");
            vista.getLblAtrasados().setText("0");
            vista.getLblTotalUsuarios().setText("0");

        }
    }

    public void refrescarReportes() {
        if (controladorReportes != null) {
            controladorReportes.refrescarReportes();
        }
    }
    
    // Métodos para mostrar cada panel
    private void mostrarPanelPrincipal() {
        vista.mostrarPanelPrincipal();
        System.out.println("Mostrando panel Principal");
    }
    
    private void mostrarPanelPrestamos() {
        vista.mostrarPanelPrestamos();
        System.out.println("Mostrando panel Préstamos");
    }
    
    private void mostrarPanelUsuarios() {
        vista.mostrarPanelUsuarios();
        System.out.println("Mostrando panel Usuarios");
    }
    
    private void mostrarPanelLibros() {
        vista.mostrarPanelLibros();
        System.out.println("Mostrando panel Libros");
    }
    
    private void mostrarPanelReportes() {
        vista.mostrarPanelReportes();
        System.out.println("Mostrando panel Reportes");
    }
    
    
    private void salirSistema() {
        int confirmacion = JOptionPane.showConfirmDialog(
            vista,
            "¿Está seguro que desea salir del sistema?",
            "Confirmar salida",
            JOptionPane.YES_NO_OPTION,
            JOptionPane.QUESTION_MESSAGE
        );
        
        if (confirmacion == JOptionPane.YES_OPTION) {
            System.exit(0);
        }
    }
}
    
    