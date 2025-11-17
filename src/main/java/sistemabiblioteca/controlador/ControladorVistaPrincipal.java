package sistemabiblioteca.controlador;

import sistemabiblioteca.vista.VistaPrincipal;

public class ControladorVistaPrincipal {
    private VistaPrincipal vista;
    private ControladorLibros controladorLibros;
    private ControladorPrestamos controladorPrestamos;
    private ControladorUsuarios controladorUsuarios;
    private ControladorReportes controladorReportes;
    private String usuarioLogueado;
    
    public ControladorVistaPrincipal(String usuarioLogueado) {
        this(new VistaPrincipal(), usuarioLogueado,
             new ControladorLibros(new VistaPrincipal().getPanelLibros()),
             new ControladorPrestamos(new VistaPrincipal().getPanelPrestamos()),
             new ControladorUsuarios(new VistaPrincipal().getPanelUsuarios()),
             new ControladorReportes(new VistaPrincipal().getPanelReportes()));
    }
    
    // Constructor para testing - con inyección de dependencias
    ControladorVistaPrincipal(VistaPrincipal vista, String usuarioLogueado,
                             ControladorLibros controladorLibros,
                             ControladorPrestamos controladorPrestamos,
                             ControladorUsuarios controladorUsuarios,
                             ControladorReportes controladorReportes) {
        this.vista = vista;
        this.usuarioLogueado = usuarioLogueado;
        this.controladorLibros = controladorLibros;
        this.controladorPrestamos = controladorPrestamos;
        this.controladorUsuarios = controladorUsuarios;
        this.controladorReportes = controladorReportes;
        
        configurarPermisos();
        cargarDatos();
        configurarEventos();
        vista.setVisible(true);
        
        String rol = esAdmin() ? "Administrador" : "Empleado";
        vista.mostrarMensaje("Bienvenido: " + usuarioLogueado + " (" + rol + ")");
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
    
    boolean esAdmin() {
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
    
    void cargarDatos() {
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
    void mostrarPanelPrincipal() {
        vista.mostrarPanelPrincipal();
        System.out.println("Mostrando panel Principal");
    }
    
    void mostrarPanelPrestamos() {
        vista.mostrarPanelPrestamos();
        System.out.println("Mostrando panel Préstamos");
    }
    
    void mostrarPanelUsuarios() {
        vista.mostrarPanelUsuarios();
        System.out.println("Mostrando panel Usuarios");
    }
    
    void mostrarPanelLibros() {
        vista.mostrarPanelLibros();
        System.out.println("Mostrando panel Libros");
    }
    
    void mostrarPanelReportes() {
        vista.mostrarPanelReportes();
        System.out.println("Mostrando panel Reportes");
    }
    
    
    void salirSistema() {
    	if (vista.confirmarSalida()) {
    	    System.exit(0);
    	}
    }
}
    
    