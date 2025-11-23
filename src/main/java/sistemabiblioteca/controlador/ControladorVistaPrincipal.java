package sistemabiblioteca.controlador;

import sistemabiblioteca.modelo.Empleado;
import sistemabiblioteca.vista.VistaPrincipal;

public class ControladorVistaPrincipal {
    private VistaPrincipal vista;
    private ControladorLibros controladorLibros;
    private ControladorPrestamos controladorPrestamos;
    private ControladorUsuarios controladorUsuarios;
    private ControladorReportes controladorReportes;
    private Empleado empleadoLogueado; // CAMBIADO: de String a Empleado
    
    public ControladorVistaPrincipal(Empleado empleadoLogueado) { // CAMBIADO: recibe Empleado
        this.vista = new VistaPrincipal();
        this.empleadoLogueado = empleadoLogueado;
        
        // Inicializar controladores
        this.controladorLibros = new ControladorLibros(vista.getPanelLibros(), empleadoLogueado);
        this.controladorPrestamos = new ControladorPrestamos(vista.getPanelPrestamos(), empleadoLogueado);
        this.controladorUsuarios = new ControladorUsuarios(vista.getPanelUsuarios(), empleadoLogueado); // CAMBIADO: pasar empleadoLogueado
        this.controladorReportes = new ControladorReportes(vista.getPanelReportes());
        new ControladorEmpleados(vista.getPanelEmpleados()); // NUEVO
        
        configurarPermisos();
        cargarDatos();
        configurarEventos();
        vista.setVisible(true);
        
        mostrarMensajeBienvenida();
    }
    
    // Constructor para testing (mantener compatibilidad)
    ControladorVistaPrincipal(VistaPrincipal vista, String usuarioLogueado,
                             ControladorLibros controladorLibros,
                             ControladorPrestamos controladorPrestamos,
                             ControladorUsuarios controladorUsuarios,
                             ControladorReportes controladorReportes) {
        this.vista = vista;
        // Para compatibilidad con tests, empleado temporal
        this.empleadoLogueado = crearEmpleadoTemporal(usuarioLogueado);
        this.controladorLibros = controladorLibros;
        this.controladorPrestamos = controladorPrestamos;
        this.controladorUsuarios = controladorUsuarios;
        this.controladorReportes = controladorReportes;
        new ControladorEmpleados(vista.getPanelEmpleados());
        
        configurarPermisos();
        cargarDatos();
        configurarEventos();
        vista.setVisible(true);
        
        mostrarMensajeBienvenida();
    }
    
    ControladorVistaPrincipal(VistaPrincipal vista, Empleado empleadoLogueado,
            ControladorLibros controladorLibros,
            ControladorPrestamos controladorPrestamos,
            ControladorUsuarios controladorUsuarios,
            ControladorReportes controladorReportes,
            ControladorEmpleados controladorEmpleados) {
			this.vista = vista;
			this.empleadoLogueado = empleadoLogueado;
			this.controladorLibros = controladorLibros;
			this.controladorPrestamos = controladorPrestamos;
			this.controladorUsuarios = controladorUsuarios;
			this.controladorReportes = controladorReportes;
			
			configurarPermisos();
			cargarDatos();
			configurarEventos();
			vista.setVisible(true);
			
			mostrarMensajeBienvenida();
    }
    
    private Empleado crearEmpleadoTemporal(String usuario) {
        Empleado emp = new Empleado();
        emp.setUsuario(usuario);
        emp.setRol("admin".equals(usuario) ? "ADMIN" : "EMPLEADO");
        emp.setNombre(usuario);
        return emp;
    }
    
    private void mostrarMensajeBienvenida() {
        String nombre = empleadoLogueado.getNombreCompleto();
        String rol = esAdmin() ? "Administrador" : "Empleado";
        String mensaje = String.format("Bienvenido: %s (%s)", nombre, rol);
        vista.mostrarMensaje(mensaje);
    }
    
    private void configurarPermisos() {
        if (!esAdmin()) {
            ocultarFuncionalidadesEmpleado();
        }
    }
    
    private void ocultarFuncionalidadesEmpleado() {
        // Ocultar pestañas de administrador
        vista.ocultarPestanaReportes();
        vista.ocultarPestanaEmpleados();
        
        // Configurar paneles en modo empleado
        vista.getPanelLibros().setModoEmpleado();
        vista.getPanelUsuarios().setModoEmpleado();
    }
    
    boolean esAdmin() {
        return empleadoLogueado != null && "ADMIN".equals(empleadoLogueado.getRol());
    }
    
    private void configurarEventos() {
        vista.agregarListenerPrincipal(e -> {
            cargarDatos(); // ← Actualizar datos al mostrar el panel
            mostrarPanelPrincipal();
        });
        vista.agregarListenerPrestamos(e -> mostrarPanelPrestamos());
        vista.agregarListenerUsuarios(e -> mostrarPanelUsuarios());
        vista.agregarListenerLibros(e -> mostrarPanelLibros());
        vista.agregarListenerReportes(e -> mostrarPanelReportes());
        vista.agregarListenerEmpleados(e -> mostrarPanelEmpleados()); // NUEVO
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
    
    void mostrarPanelEmpleados() {
        vista.mostrarPanelEmpleados();
        System.out.println("Mostrando panel Empleados");
    }
    
    void salirSistema() {
        if (vista.confirmarSalida()) {
            System.exit(0);
        }
    }
    
    // Getters para testing
    public Empleado getEmpleadoLogueado() {
        return empleadoLogueado;
    }
    
    public String getUsuarioLogueado() {
        return empleadoLogueado != null ? empleadoLogueado.getUsuario() : null;
    }
}