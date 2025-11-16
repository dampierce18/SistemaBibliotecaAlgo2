package sistemabiblioteca.controlador;

import sistemabiblioteca.vista.VistaPrincipal;
import javax.swing.*;

public class ControladorVistaPrincipal {
    private VistaPrincipal vista;
    private ControladorLibros controladorLibros;
    
    // Datos de ejemplo para las tarjetas de resumen
    private int totalLibros = 0;
    private int totalUsuarios = 0;
    private int prestamosActivos = 0;
    private int prestamosAtrasados = 0;
    
    public ControladorVistaPrincipal() {
        this.vista = new VistaPrincipal();
        this.controladorLibros = new ControladorLibros(vista.getPanelLibros());
        configurarEventos();
        cargarDatosEjemplo();
        vista.setVisible(true);
    }
    
    private void configurarEventos() {
        // Navegación entre paneles
        vista.agregarListenerPrincipal(e -> mostrarPanelPrincipal());
        vista.agregarListenerPrestamos(e -> mostrarPanelPrestamos());
        vista.agregarListenerDevoluciones(e -> mostrarPanelDevoluciones());
        vista.agregarListenerUsuarios(e -> mostrarPanelUsuarios());
        vista.agregarListenerLibros(e -> mostrarPanelLibros());
        vista.agregarListenerReportes(e -> mostrarPanelReportes());
        vista.agregarListenerSalir(e -> salirSistema());
    }
    
    private void cargarDatosEjemplo() {
        try {
            int totalLibros = controladorLibros.obtenerTotalLibros();
            vista.getLblTotalLibros().setText(String.valueOf(totalLibros));
        } catch (Exception e) {
            vista.getLblTotalLibros().setText("0");
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
    
    private void mostrarPanelDevoluciones() {
        vista.mostrarPanelDevoluciones();
        System.out.println("Mostrando panel Devoluciones");
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
    
    // Métodos para obtener datos de ejemplo (para futuras expansiones)
    public int getTotalLibros() {
        return totalLibros;
    }
    
    public int getTotalUsuarios() {
        return totalUsuarios;
    }
    
    public int getPrestamosActivos() {
        return prestamosActivos;
    }
    
    public int getPrestamosAtrasados() {
        return prestamosAtrasados;
    }
    
    public VistaPrincipal getVista() {
        return vista;
    }
}