package sistemabiblioteca.vista;

import javax.swing.*;
import javax.swing.border.EmptyBorder;

import java.awt.*;
import java.awt.event.ActionListener;

public class VistaPrincipal extends JFrame {
	private static final long serialVersionUID = 1L;
	private JPanel panelLateral;
    private JPanel panelContenido;
    private CardLayout cardLayout;
    
    private JLabel lblTotalLibros;
    private JLabel lblTotalUsuarios;
    private JLabel lblPrestamosActivos;
    private JLabel lblAtrasados;
    
    private JButton btnPrincipal;
    private JButton btnPrestamos;
    private JButton btnUsuarios;
    private JButton btnLibros;
    private JButton btnReportes;
    private JButton btnSalir;
    
    private JPanel panelPrincipal;
    private PanelPrestamos panelPrestamos;
    private PanelUsuarios panelUsuarios;
    private PanelLibros panelLibros;
    private PanelReportes panelReportes;
    
    public VistaPrincipal() {
        configurarVentana();
        inicializarComponentes();
    }
    
    private void configurarVentana() {
        setTitle("Sistema de Gestión de Biblioteca");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(1000, 600);
        setLocationRelativeTo(null); 
        setLayout(new BorderLayout());
    }
    
   
    private void inicializarComponentes() {
        panelLateral = crearPanelLateral();
        add(panelLateral, BorderLayout.WEST);
        
        cardLayout = new CardLayout();
        panelContenido = new JPanel(cardLayout);
        inicializarPanelesContenido();
        add(panelContenido, BorderLayout.CENTER);
        
        cardLayout.show(panelContenido, "Principal");
    }
    
    private JPanel crearPanelLateral() {
        JPanel panel = new JPanel();
        panel.setLayout(new GridLayout(8, 1, 0, 5));
        panel.setPreferredSize(new Dimension(180, 0));
        panel.setBackground(new Color(240, 240, 240));
        panel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        btnPrincipal = new JButton("Principal");
        panel.add(btnPrincipal);
        
        btnPrestamos = new JButton("Préstamos");
        panel.add(btnPrestamos);
        
        btnUsuarios = new JButton("Usuarios");
        panel.add(btnUsuarios);
        
        btnLibros = new JButton("Libros");
        panel.add(btnLibros);
        
        btnReportes = new JButton("Reportes");
        panel.add(btnReportes);
        panel.add(new JLabel());
        panel.add(new JLabel()); // espacio

        btnSalir = new JButton("Salir");
        panel.add(btnSalir);

        return panel;
    }

    
    private void inicializarPanelesContenido() {
        panelPrincipal = crearPanelPrincipal();
        panelContenido.add(panelPrincipal, "Principal");
        
        panelPrestamos = new PanelPrestamos();
        panelContenido.add(panelPrestamos, "Préstamos");
        
        panelUsuarios = new PanelUsuarios();
        panelContenido.add(panelUsuarios, "Usuarios");
        
        panelLibros = new PanelLibros();
        panelContenido.add(panelLibros, "Libros");
        
        panelReportes = new PanelReportes();
        panelContenido.add(panelReportes, "Reportes");
    }
    
    private JPanel crearPanelPrincipal() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBackground(Color.WHITE);

        JLabel lblTitulo = new JLabel("Sistema de Gestión de Biblioteca", SwingConstants.CENTER);
        lblTitulo.setFont(new Font("Arial", Font.BOLD, 24));
        lblTitulo.setOpaque(true);
        lblTitulo.setBackground(new Color(70, 130, 180));
        lblTitulo.setForeground(Color.WHITE);
        lblTitulo.setBorder(BorderFactory.createEmptyBorder(20, 0, 20, 0));
        panel.add(lblTitulo, BorderLayout.NORTH);

        JPanel contenedorTarjetas = new JPanel();
        contenedorTarjetas.setMinimumSize(new Dimension(-20, 20));
        contenedorTarjetas.setBackground(Color.WHITE);
        contenedorTarjetas.setBorder(BorderFactory.createEmptyBorder(40, 40, 40, 40));
        contenedorTarjetas.setLayout(new GridLayout(0, 2, 0, 0));
        
        panel.add(contenedorTarjetas, BorderLayout.CENTER);
        
        JPanel panel_1 = new JPanel();
        panel_1.setBorder(new EmptyBorder(40, 20, 40, 20));
        contenedorTarjetas.add(panel_1);
        panel_1.setLayout(new BorderLayout(0, 0));
        
        JLabel lblNewLabel = new JLabel("Total Libros");
        lblNewLabel.setFont(new Font("Arial", Font.PLAIN, 14));
        panel_1.add(lblNewLabel, BorderLayout.NORTH);
        
        lblTotalLibros= new JLabel("0");
        lblTotalLibros.setFont(new Font("Arial", Font.BOLD, 24));
        panel_1.add(lblTotalLibros, BorderLayout.CENTER);

        JPanel panel_2 = new JPanel();
        panel_2.setBorder(new EmptyBorder(40, 20, 40, 20));
        contenedorTarjetas.add(panel_2);
        panel_2.setLayout(new BorderLayout(0, 0));
        
        JLabel lblNewLabel_2 = new JLabel("Total Usuarios");
        lblNewLabel_2.setFont(new Font("Arial", Font.PLAIN, 14));
        panel_2.add(lblNewLabel_2, BorderLayout.NORTH);
        lblTotalUsuarios = new JLabel("0");
        lblTotalUsuarios.setFont(new Font("Arial", Font.BOLD, 24));
        panel_2.add(lblTotalUsuarios, BorderLayout.CENTER);
        
        JPanel panel_3 = new JPanel();
        panel_3.setBorder(new EmptyBorder(40, 20, 40, 20));
        contenedorTarjetas.add(panel_3);
        panel_3.setLayout(new BorderLayout(0, 0));
        
        JLabel lblNewLabel_4 = new JLabel("Prestamos activos");
        lblNewLabel_4.setFont(new Font("Arial", Font.PLAIN, 14));
        panel_3.add(lblNewLabel_4, BorderLayout.NORTH);
        lblPrestamosActivos = new JLabel("0");
        lblPrestamosActivos.setFont(new Font("Arial", Font.BOLD, 24));
        panel_3.add(lblPrestamosActivos, BorderLayout.CENTER);
        
        JPanel panel_4 = new JPanel();
        panel_4.setBorder(new EmptyBorder(40, 20, 40, 20));
        contenedorTarjetas.add(panel_4);
        panel_4.setLayout(new BorderLayout(0, 0));
        
        JLabel lblNewLabel_6 = new JLabel("Atrasados");
        lblNewLabel_6.setFont(new Font("Arial", Font.PLAIN, 14));
        panel_4.add(lblNewLabel_6, BorderLayout.NORTH);
        lblAtrasados = new JLabel("0");
        lblAtrasados.setFont(new Font("Arial", Font.BOLD, 24));
        panel_4.add(lblAtrasados, BorderLayout.CENTER);

        return panel;
    }
    
    public void mostrarMensaje(String mensaje) {
        JOptionPane.showMessageDialog(this, mensaje, "Información", JOptionPane.INFORMATION_MESSAGE);
    }

    public boolean confirmarSalida() {
        int confirmacion = JOptionPane.showConfirmDialog(
                this,
                "¿Está seguro que desea salir del sistema?",
                "Confirmar salida",
                JOptionPane.YES_NO_OPTION,
                JOptionPane.QUESTION_MESSAGE
        );
        return confirmacion == JOptionPane.YES_OPTION;
    }


    public void agregarListenerPrincipal(ActionListener listener) {
        btnPrincipal.addActionListener(listener);
    }
    
    public void agregarListenerPrestamos(ActionListener listener) {
        btnPrestamos.addActionListener(listener);
    }
    
    public void agregarListenerUsuarios(ActionListener listener) {
        btnUsuarios.addActionListener(listener);
    }
    
    public void agregarListenerLibros(ActionListener listener) {
        btnLibros.addActionListener(listener);
    }
    
    public void agregarListenerReportes(ActionListener listener) {
        btnReportes.addActionListener(listener);
    }
    
    public void agregarListenerSalir(ActionListener listener) {
        btnSalir.addActionListener(listener);
    }
    
    public void mostrarPanelPrincipal() {
        cardLayout.show(panelContenido, "Principal");
    }
    
    public void mostrarPanelPrestamos() {
        cardLayout.show(panelContenido, "Préstamos");
    }
    
    public void mostrarPanelDevoluciones() {
        cardLayout.show(panelContenido, "Devoluciones");
    }
    
    public void mostrarPanelUsuarios() {
        cardLayout.show(panelContenido, "Usuarios");
    }
    
    public void mostrarPanelLibros() {
        cardLayout.show(panelContenido, "Libros");
    }
    
    public void mostrarPanelReportes() {
        cardLayout.show(panelContenido, "Reportes");
    }
    
    public JPanel getPanelPrincipal() { return panelPrincipal; }
    public PanelPrestamos getPanelPrestamos() { return panelPrestamos; }
    public PanelUsuarios getPanelUsuarios() { return panelUsuarios; }
    public PanelLibros getPanelLibros() { return panelLibros; }
    public PanelReportes getPanelReportes() { return panelReportes; }
    public JLabel getLblTotalLibros() { return lblTotalLibros; }
    public JLabel getLblTotalUsuarios() { return lblTotalUsuarios; }
    public JLabel getLblPrestamosActivos() { return lblPrestamosActivos; }
    public JLabel getLblAtrasados() { return lblAtrasados; }
    
    
    public JButton getBtnReportes() {
        return btnReportes;
    }

    public JButton getBtnUsuarios() {
        return btnUsuarios;
    }

    public JButton getBtnLibros() {
        return btnLibros;
    }
    public JButton getBtnPrestamos() {
        return btnPrestamos;
    }
    
}