package sistemabiblioteca.vista;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.ActionListener;

public class PanelPrestamos extends JPanel {
	private static final long serialVersionUID = 1L;
	private JTabbedPane tabbedPane;
    private JTable tablePrestamosActivos;
    private JTable tableHistorialPrestamos;
    
    private JTextField txtLibroId;
    private JTextField txtUsuarioId;
    private JTextField txtDiasPrestamo;
    
    private JButton btnRealizarPrestamo;
    private JButton btnRegistrarDevolucion;
    private JButton btnActualizarPrestamos;
    private JButton btnLimpiarFormulario;

    public PanelPrestamos() {
        setLayout(new BorderLayout(0, 0));
        inicializarComponentes();
    }
    
    private void inicializarComponentes() {
        JPanel panelTitulo = new JPanel();
        panelTitulo.setBackground(new Color(70, 130, 180));
        panelTitulo.setPreferredSize(new Dimension(10, 60));
        add(panelTitulo, BorderLayout.NORTH);
        panelTitulo.setLayout(new BorderLayout(0, 0));
        
        JLabel lblTitulo = new JLabel("Gestión de Préstamos");
        lblTitulo.setHorizontalAlignment(SwingConstants.CENTER);
        lblTitulo.setForeground(Color.WHITE);
        lblTitulo.setFont(new Font("Arial", Font.BOLD, 20));
        panelTitulo.add(lblTitulo);
        
        tabbedPane = new JTabbedPane(JTabbedPane.TOP);
        add(tabbedPane, BorderLayout.CENTER);
        
        tabbedPane.addTab("Nuevo Préstamo", crearPanelNuevoPrestamo());
        
        tabbedPane.addTab("Préstamos Activos", crearPanelPrestamosActivos());
        
        tabbedPane.addTab("Historial", crearPanelHistorial());
    }
    
    private JPanel crearPanelNuevoPrestamo() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBorder(new EmptyBorder(20, 20, 20, 20));
        
        JPanel panelFormulario = new JPanel(new GridLayout(7, 2, 10, 10));
        
        txtLibroId = new JTextField();
        txtUsuarioId = new JTextField();
        txtDiasPrestamo = new JTextField("15"); 
        
        panelFormulario.add(new JLabel("ID Libro:"));
        panelFormulario.add(txtLibroId);
        panelFormulario.add(new JLabel("ID Usuario:"));
        panelFormulario.add(txtUsuarioId);
        panelFormulario.add(new JLabel("Días de Préstamo:"));
        panelFormulario.add(txtDiasPrestamo);
        panelFormulario.add(new JLabel("")); 
        panelFormulario.add(new JLabel("")); 
        
        panel.add(panelFormulario, BorderLayout.CENTER);
        
        JPanel panelBotones = new JPanel();
        btnRealizarPrestamo = new JButton("Realizar Préstamo");
        btnLimpiarFormulario = new JButton("Limpiar");
        
        panelBotones.add(btnRealizarPrestamo);
        panelBotones.add(btnLimpiarFormulario);
        
        panel.add(panelBotones, BorderLayout.SOUTH);
        
        return panel;
    }
    
    private JPanel crearPanelPrestamosActivos() {
        JPanel panel = new JPanel(new BorderLayout());
        
        JPanel panelBotones = new JPanel(new FlowLayout(FlowLayout.LEFT));
        btnRegistrarDevolucion = new JButton("Registrar Devolución");
        btnActualizarPrestamos = new JButton("Actualizar Lista");
        
        panelBotones.add(btnRegistrarDevolucion);
        panelBotones.add(btnActualizarPrestamos);
        
        panel.add(panelBotones, BorderLayout.NORTH);
        
        JScrollPane scrollPane = new JScrollPane();
        tablePrestamosActivos = new JTable();
        tablePrestamosActivos.setModel(new DefaultTableModel(
            new Object[][] {},
            new String[] {"ID", "ID Libro", "ID Usuario", "Fecha Préstamo", "Fecha Devolución", "Estado"}
        ));
        scrollPane.setViewportView(tablePrestamosActivos);
        
        panel.add(scrollPane, BorderLayout.CENTER);
        
        return panel;
    }
    
    private JPanel crearPanelHistorial() {
        JPanel panel = new JPanel(new BorderLayout());
        
        JScrollPane scrollPane = new JScrollPane();
        tableHistorialPrestamos = new JTable();
        tableHistorialPrestamos.setModel(new DefaultTableModel(
            new Object[][] {},
            new String[] {"ID", "ID Libro", "ID Usuario", "Fecha Préstamo", "Fecha Devolución", "Fecha Dev Real", "Estado"}
        ));
        scrollPane.setViewportView(tableHistorialPrestamos);
        
        panel.add(scrollPane, BorderLayout.CENTER);
        
        return panel;
    }
    
    public String getLibroId() { return txtLibroId.getText().trim(); }
    public String getUsuarioId() { return txtUsuarioId.getText().trim(); }
    public String getDiasPrestamo() { return txtDiasPrestamo.getText().trim(); }
    public JTable getTablePrestamosActivos() {
        return tablePrestamosActivos;
    }
    
    public JTable getTableHistorialPrestamos() {
        return tableHistorialPrestamos;
    }
    public int getFilaSeleccionadaPrestamosActivos() {
        return tablePrestamosActivos.getSelectedRow();
    }
    
    public int getFilaSeleccionadaHistorial() {
        return tableHistorialPrestamos.getSelectedRow();
    }
    
    public void actualizarTablaPrestamosActivos(Object[][] datos) {
        DefaultTableModel modelo = (DefaultTableModel) tablePrestamosActivos.getModel();
        modelo.setRowCount(0);
        for (Object[] fila : datos) {
            modelo.addRow(fila);
        }
    }
    
    public void actualizarTablaHistorial(Object[][] datos) {
        DefaultTableModel modelo = (DefaultTableModel) tableHistorialPrestamos.getModel();
        modelo.setRowCount(0);
        for (Object[] fila : datos) {
            modelo.addRow(fila);
        }
    }
    
    public void mostrarMensaje(String mensaje, int tipo) {
        JOptionPane.showMessageDialog(this, mensaje, "Mensaje", tipo);
    }
    
    public Integer obtenerPrestamoIdSeleccionado() {
        int fila = getFilaSeleccionadaPrestamosActivos();
        if (fila == -1) {
            return null;
        }
        return (Integer) tablePrestamosActivos.getValueAt(fila, 0);
    }
    
    public Integer obtenerLibroIdSeleccionado() {
        int fila = getFilaSeleccionadaPrestamosActivos();
        if (fila == -1) {
            return null;
        }
        return (Integer) tablePrestamosActivos.getValueAt(fila, 1);
    }
    
    public void limpiarFormulario() {
        txtLibroId.setText("");
        txtUsuarioId.setText("");
        txtDiasPrestamo.setText("15");
    }
    
    public void agregarRealizarPrestamoListener(ActionListener listener) {
        btnRealizarPrestamo.addActionListener(listener);
    }
    
    public void agregarRegistrarDevolucionListener(ActionListener listener) {
        btnRegistrarDevolucion.addActionListener(listener);
    }
    
    public void agregarActualizarPrestamosListener(ActionListener listener) {
        btnActualizarPrestamos.addActionListener(listener);
    }
    
    public void agregarLimpiarFormularioListener(ActionListener listener) {
        btnLimpiarFormulario.addActionListener(listener);
    }
}