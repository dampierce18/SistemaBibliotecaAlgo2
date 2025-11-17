package sistemabiblioteca.controlador;

import sistemabiblioteca.vista.LoginFrame;
import javax.swing.*;

public class ControladorLogin {
    private LoginFrame vista;
    
    static final String ADMIN_USUARIO = "admin";
    static final String ADMIN_PASSWORD = "123";
    static final String EMPLEADO_USUARIO = "empleado";
    static final String EMPLEADO_PASSWORD = "123";
    
    private String usuarioLogueado; 
    
    public ControladorLogin() {
        this(new LoginFrame());
    }
    
    public ControladorLogin(LoginFrame vista) {
    	this.vista = vista;
        configurarEventos();
        vista.setVisible(true);
    }
    
    
    private void configurarEventos() {
        vista.getBtnLogin().addActionListener(e -> verificarCredenciales());
        vista.getBtnSalir().addActionListener(e -> salirSistema());
    }
    
    public void verificarCredenciales() {
        String usuario = vista.getTxtUsuario().getText().trim();
        String password = new String(vista.getTxtPassword().getPassword());
        
        if (usuario.isEmpty() || password.isEmpty()) {
            vista.mostrarError("Por favor, complete ambos campos");
            return;
        }
        
        if ((usuario.equals(ADMIN_USUARIO) && password.equals(ADMIN_PASSWORD)) ||
            (usuario.equals(EMPLEADO_USUARIO) && password.equals(EMPLEADO_PASSWORD))) {
            
            this.usuarioLogueado = usuario;
            abrirSistemaPrincipal();
            
        } else {
            vista.mostrarError("Usuario o contraseña incorrectos");
            vista.limpiarFormulario();
        }
    }
    
    private void abrirSistemaPrincipal() {
        vista.dispose();
        
        SwingUtilities.invokeLater(new Runnable() {
            @Override
            public void run() {
                new ControladorVistaPrincipal(usuarioLogueado);
            }
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
    
    String getUsuarioLogueado() {
        return usuarioLogueado;
    }
    
}