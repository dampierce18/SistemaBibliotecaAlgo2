package sistemabiblioteca.controlador;

import sistemabiblioteca.dao.EmpleadoDAO;
import sistemabiblioteca.modelo.Empleado;
import sistemabiblioteca.vista.LoginFrame;
import javax.swing.*;

public class ControladorLogin {
    private LoginFrame vista;
    private EmpleadoDAO empleadoDAO;
    private Empleado empleadoLogueado;
    
    public ControladorLogin() {
        this(new LoginFrame());
    }
    
    public ControladorLogin(LoginFrame vista) {
        this.vista = vista;
        this.empleadoDAO = new EmpleadoDAO();
        configurarEventos();
        vista.setVisible(true);
    }
    
    public ControladorLogin(LoginFrame vista, EmpleadoDAO empleadoDAO) {
        this.vista = vista;
        this.empleadoDAO = empleadoDAO;
        configurarEventos();
        vista.setVisible(true);
    }
    
    // Método factory para permitir testing
    public EmpleadoDAO crearEmpleadoDAO() {
        return new EmpleadoDAO();
    }
    
     void configurarEventos() {
        vista.getBtnLogin().addActionListener(e -> verificarCredenciales());
        vista.getBtnSalir().addActionListener(e -> salirSistema());
        
    }
    
     void verificarCredenciales() {
        String usuario = vista.getTxtUsuario().getText().trim();
        String password = new String(vista.getTxtPassword().getPassword());
        
        if (usuario.isEmpty() || password.isEmpty()) {
            vista.mostrarError("Por favor, complete ambos campos");
            return;
        }
        
        try {
            // Autenticar desde la base de datos
            empleadoLogueado = empleadoDAO.autenticar(usuario, password);
            
            if (empleadoLogueado != null) {
                abrirSistemaPrincipal();
            } else {
                vista.mostrarError("Usuario o contraseña incorrectos");
                vista.limpiarFormulario();
            }
            
        } catch (Exception e) {
            vista.mostrarError("Error de conexión: " + e.getMessage());
            System.err.println("Error en autenticación: " + e.getMessage());
        }
    }
    
     void abrirSistemaPrincipal() {
        vista.dispose();
        
        SwingUtilities.invokeLater(() -> {
            new ControladorVistaPrincipal(empleadoLogueado);
        });
    }
    
    public void salirSistema() {
        if (vista.confirmarSalida()) {
            exit(0);
        }
    }

    protected void exit(int status) {
        System.exit(status);
    }
    
    public Empleado getEmpleadoLogueado() {
        return empleadoLogueado;
    }
    
    public String getUsuarioLogueado() {
        return empleadoLogueado != null ? empleadoLogueado.getUsuario() : null;
    }
    
    public String getRolUsuarioLogueado() {
        return empleadoLogueado != null ? empleadoLogueado.getRol() : null;
    }
}