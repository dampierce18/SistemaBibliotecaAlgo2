package sistemabiblioteca.vista;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class LoginFrame extends JFrame {
    private static final long serialVersionUID = 1L;
    private JTextField txtUsuario;
    private JPasswordField txtPassword;
    private JButton btnLogin;
    private JButton btnSalir;
    
    public LoginFrame() {
        initialize();
    }
    
    private void initialize() {
        setTitle("Acceso al Sistema - Biblioteca");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(350, 250);
        setLocationRelativeTo(null);
        setResizable(false);
        
        getContentPane().setLayout(new BorderLayout(10, 10));
        JPanel contentPane = (JPanel) getContentPane();
        contentPane.setBorder(BorderFactory.createEmptyBorder(15, 15, 15, 15));
        
        crearComponentes();
    }
    
    private void crearComponentes() {
        JPanel panelPrincipal = new JPanel();
        getContentPane().add(panelPrincipal, BorderLayout.CENTER);
        panelPrincipal.setLayout(null);
        
        JLabel lblTitulo = new JLabel("Sistema de Biblioteca");
        lblTitulo.setBounds(64, 19, 206, 19);
        lblTitulo.setFont(new Font("Arial", Font.BOLD, 16));
        lblTitulo.setHorizontalAlignment(SwingConstants.CENTER);
        panelPrincipal.add(lblTitulo);
        
        JLabel lblUsuario = new JLabel("Usuario:");
        lblUsuario.setBounds(69, 61, 60, 14);
        panelPrincipal.add(lblUsuario);
        
        txtUsuario = new JTextField(15);
        txtUsuario.setBounds(139, 58, 126, 20);
        panelPrincipal.add(txtUsuario);
        
        JLabel lblPassword = new JLabel("Contraseña:");
        lblPassword.setBounds(64, 92, 60, 14);
        panelPrincipal.add(lblPassword);
        
        txtPassword = new JPasswordField(15);
        txtPassword.setBounds(139, 89, 126, 20);
        panelPrincipal.add(txtPassword);
        
        JPanel panelBotones = new JPanel(new FlowLayout());
        panelBotones.setBounds(64, 153, 196, 33);
        btnLogin = new JButton("Entrar");
        btnSalir = new JButton("Salir");
        panelBotones.add(btnLogin);
        panelBotones.add(btnSalir);
        panelPrincipal.add(panelBotones);
        
        configurarEventos();
    }
    
    private void configurarEventos() {
        txtUsuario.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                txtPassword.requestFocus();
            }
        });
        
        txtPassword.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                btnLogin.doClick(); 
            }
        });
        
    }
    
    public JTextField getTxtUsuario() { return txtUsuario; }
    public JPasswordField getTxtPassword() { return txtPassword; }
    public JButton getBtnLogin() { return btnLogin; }
    public JButton getBtnSalir() { return btnSalir; }
    
    public void limpiarFormulario() {
        txtUsuario.setText("");
        txtPassword.setText("");
        txtUsuario.requestFocus();
    }
    
    public void mostrarError(String mensaje) {
        JOptionPane.showMessageDialog(
                this,
                mensaje,
                "Error",
                JOptionPane.ERROR_MESSAGE
            );
    }
    public boolean confirmarSalida() {
        int result = JOptionPane.showConfirmDialog(
                this,
                "¿Está seguro que desea salir del sistema?",
                "Confirmar salida",
                JOptionPane.YES_NO_OPTION,
                JOptionPane.QUESTION_MESSAGE
        );
        return result == JOptionPane.YES_OPTION;
    }
    
}