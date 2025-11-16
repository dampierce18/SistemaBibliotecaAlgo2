package sistemabiblioteca.controlador;

import sistemabiblioteca.vista.LoginFrame;
import javax.swing.*;

public class ControladorLogin {
    private LoginFrame vista;
    
    // Credenciales fijas
    private static final String ADMIN_USUARIO = "admin";
    private static final String ADMIN_PASSWORD = "123";
    private static final String EMPLEADO_USUARIO = "empleado";
    private static final String EMPLEADO_PASSWORD = "123";
    
    private String usuarioLogueado; // Guardar quién inició sesión
    
    public ControladorLogin() {
        this.vista = new LoginFrame();
        configurarEventos();
        vista.setVisible(true);
    }
    
    private void configurarEventos() {
        vista.getBtnLogin().addActionListener(e -> verificarCredenciales());
        vista.getBtnSalir().addActionListener(e -> salirSistema());
    }
    
    private void verificarCredenciales() {
        String usuario = vista.getTxtUsuario().getText().trim();
        String password = new String(vista.getTxtPassword().getPassword());
        
        if (usuario.isEmpty() || password.isEmpty()) {
            vista.mostrarError("Por favor, complete ambos campos");
            return;
        }
        
        // Verificar credenciales para ambos usuarios
        if ((usuario.equals(ADMIN_USUARIO) && password.equals(ADMIN_PASSWORD)) ||
            (usuario.equals(EMPLEADO_USUARIO) && password.equals(EMPLEADO_PASSWORD))) {
            
            this.usuarioLogueado = usuario; // Guardar quién se logueó
            abrirSistemaPrincipal();
            
        } else {
            vista.mostrarError("Usuario o contraseña incorrectos");
            vista.limpiarFormulario();
        }
    }
    
    private void abrirSistemaPrincipal() {
        // Cerrar ventana de login
        vista.dispose();
        
        // Abrir el controlador principal 
        SwingUtilities.invokeLater(new Runnable() {
            @Override
            public void run() {
                new ControladorVistaPrincipal(usuarioLogueado);
            }
        });
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