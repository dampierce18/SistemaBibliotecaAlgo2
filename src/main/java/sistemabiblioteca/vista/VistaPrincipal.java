package sistemabiblioteca.vista;

import javax.swing.*;
import javax.swing.border.EmptyBorder;

import java.awt.*;
import java.awt.event.ActionListener;

public class VistaPrincipal extends JFrame {
    /**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private JPanel panelLateral;
    private JPanel panelContenido;
    private CardLayout cardLayout;
    
    private JLabel lblTotalLibros;
    private JLabel lblTotalUsuarios;
    private JLabel lblPrestamosActivos;
    private JLabel lblAtrasados;
    
    // Botones del menú lateral
    private JButton btnPrincipal;
    private JButton btnPrestamos;
    private JButton btnDevoluciones;
    private JButton btnUsuarios;
    private JButton btnLibros;
    private JButton btnReportes;
    private JButton btnSalir;
    
    // Paneles de contenido
    private JPanel panelPrincipal;
    private JPanel panelPrestamos;
    private JPanel panelDevoluciones;
    private JPanel panelUsuarios;
    //private JPanel panelLibros;
    private PanelLibros panelLibros;
    private JPanel panelReportes;
    
    public VistaPrincipal() {
        configurarVentana();
        inicializarComponentes();
    }
    
    private void configurarVentana() {
        setTitle("Sistema de Gestión de Biblioteca");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(1000, 600);
        setLocationRelativeTo(null); // Centrar en la pantalla
        setLayout(new BorderLayout());
    }
    
    private void inicializarComponentes() {
        // Crear panel lateral con botones
        panelLateral = crearPanelLateral();
        add(panelLateral, BorderLayout.WEST);
        
        // Crear panel de contenido con CardLayout
        cardLayout = new CardLayout();
        panelContenido = new JPanel(cardLayout);
        inicializarPanelesContenido();
        add(panelContenido, BorderLayout.CENTER);
        
        // Mostrar el panel principal por defecto
        cardLayout.show(panelContenido, "Principal");
    }
    
    private JPanel crearPanelLateral() {
        JPanel panel = new JPanel();
        panel.setLayout(new GridLayout(8, 1, 0, 5));
        panel.setPreferredSize(new Dimension(180, 0));
        panel.setBackground(new Color(240, 240, 240));
        panel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        // Usar los atributos de clase
        btnPrincipal = new JButton("Principal");
        panel.add(btnPrincipal);
        
        btnPrestamos = new JButton("Préstamos");
        panel.add(btnPrestamos);
        
        btnDevoluciones = new JButton("Devoluciones");
        panel.add(btnDevoluciones);
        
        btnUsuarios = new JButton("Usuarios");
        panel.add(btnUsuarios);
        
        btnLibros = new JButton("Libros");
        panel.add(btnLibros);
        
        btnReportes = new JButton("Reportes");
        panel.add(btnReportes);

        panel.add(new JLabel()); // espacio

        btnSalir = new JButton("Salir");
        panel.add(btnSalir);

        return panel;
    }

    
    private void inicializarPanelesContenido() {
        // Panel Principal
        panelPrincipal = crearPanelPrincipal();
        panelContenido.add(panelPrincipal, "Principal");
        
        // Panel Préstamos (por implementar)
        panelPrestamos = crearPanelModulo("Módulo de Préstamos", "Aquí irá la gestión de préstamos");
        panelContenido.add(panelPrestamos, "Préstamos");
        
        // Panel Devoluciones (por implementar)
        panelDevoluciones = crearPanelModulo("Módulo de Devoluciones", "Aquí irá la gestión de devoluciones");
        panelContenido.add(panelDevoluciones, "Devoluciones");
        
        // Panel Usuarios (por implementar)
        panelUsuarios = crearPanelModulo("Módulo de Devoluciones", "Aquí irá la gestión de devoluciones");
        panelContenido.add(panelUsuarios, "Usuarios");
        
        // Panel Libros (por implementar)
        //panelLibros = crearPanelModulo("Módulo de Libros", "Aquí irá la gestión de libros");
        //panelContenido.add(panelLibros, "Libros");
        
        panelLibros = new PanelLibros();  // Tu nuevo panel
        panelContenido.add(panelLibros, "Libros");
        
        // Panel Reportes (por implementar)
        panelReportes = crearPanelModulo("Módulo de Reportes", "Aquí irán los reportes del sistema");
        panelContenido.add(panelReportes, "Reportes");
    }
    
    private JPanel crearPanelPrincipal() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBackground(Color.WHITE);

        // ----- Panel superior (título) -----
        JLabel lblTitulo = new JLabel("Sistema de Gestión de Biblioteca", SwingConstants.CENTER);
        lblTitulo.setFont(new Font("Arial", Font.BOLD, 24));
        lblTitulo.setOpaque(true);
        lblTitulo.setBackground(new Color(70, 130, 180));
        lblTitulo.setForeground(Color.WHITE);
        lblTitulo.setBorder(BorderFactory.createEmptyBorder(20, 0, 20, 0));
        panel.add(lblTitulo, BorderLayout.NORTH);

        // ----- Panel central (tarjetas) -----
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

    
    private JPanel crearPanelModulo(String titulo, String descripcion) {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBackground(Color.WHITE);
        
        // Título
        JPanel panelTitulo = new JPanel();
        panelTitulo.setBackground(new Color(70, 130, 180));
        panelTitulo.setBorder(BorderFactory.createEmptyBorder(15, 0, 15, 0));
        
        JLabel lblTitulo = new JLabel(titulo);
        lblTitulo.setFont(new Font("Arial", Font.BOLD, 20));
        lblTitulo.setForeground(Color.WHITE);
        panelTitulo.add(lblTitulo);
        
        panel.add(panelTitulo, BorderLayout.NORTH);
        
        // Contenido centrado
        JPanel panelContenido = new JPanel();
        panelContenido.setBackground(Color.WHITE);
        panelContenido.setLayout(new BorderLayout(0, 0));
        panel.add(panelContenido, BorderLayout.CENTER);
        
        JLabel lblNewLabel_8 = new JLabel(descripcion);
        lblNewLabel_8.setHorizontalAlignment(SwingConstants.CENTER);
        lblNewLabel_8.setAlignmentX(Component.CENTER_ALIGNMENT);
        panelContenido.add(lblNewLabel_8, BorderLayout.CENTER);
        
        return panel;
    }
    
    // Métodos para agregar listeners a los botones
    public void agregarListenerPrincipal(ActionListener listener) {
        btnPrincipal.addActionListener(listener);
    }
    
    public void agregarListenerPrestamos(ActionListener listener) {
        btnPrestamos.addActionListener(listener);
    }
    
    public void agregarListenerDevoluciones(ActionListener listener) {
        btnDevoluciones.addActionListener(listener);
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
    
    // Métodos para cambiar entre paneles
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
    
    // Getters para los paneles (para que el controlador pueda acceder)
    public JPanel getPanelPrincipal() { return panelPrincipal; }
    public JPanel getPanelPrestamos() { return panelPrestamos; }
    public JPanel getPanelDevoluciones() { return panelDevoluciones; }
    public JPanel getPanelUsuarios() { return panelUsuarios; }
    //public JPanel getPanelLibros() { return panelLibros; }
    public PanelLibros getPanelLibros() { return panelLibros; }
    public JPanel getPanelReportes() { return panelReportes; }
    public JLabel getLblTotalLibros() { return lblTotalLibros; }
    public JLabel getLblTotalUsuarios() { return lblTotalUsuarios; }
    public JLabel getLblPrestamosActivos() { return lblPrestamosActivos; }
    public JLabel getLblAtrasados() { return lblAtrasados; }
}