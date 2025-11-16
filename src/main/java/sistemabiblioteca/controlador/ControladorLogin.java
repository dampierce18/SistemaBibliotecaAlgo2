package sistemabiblioteca.controlador;

import sistemabiblioteca.vista.LoginFrame;
import javax.swing.*;

public class ControladorLogin {
    private LoginFrame vista;
    
    // Credenciales fijas
    private static final String USUARIO_VALIDO = "admin";
    private static final String PASSWORD_VALIDO = "12345";
    
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
        
        if (usuario.equals(USUARIO_VALIDO) && password.equals(PASSWORD_VALIDO)) {
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
                new ControladorVistaPrincipal();
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