package sistemabiblioteca.vista;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.List;


public class PanelReportes extends JPanel {
	private static final long serialVersionUID = 1L;

	private JTabbedPane tabbedPane;
    
    private JTable tableLibrosPrestados;
    private JTable tableUsuariosActivos;
    private JTable tablePrestamosMes;
    private JTable tableSituacionActual;
    
    private JLabel lblTotalPrestamosMes;
    private JLabel lblPrestamosActivos;
    private JLabel lblPrestamosAtrasados;
    private JLabel lblUsuariosSancionados;
    private JLabel lblMultasPendientes;

    
    public PanelReportes() {
        setLayout(new BorderLayout(0, 0));
        inicializarComponentes();
    }
    
    private void inicializarComponentes() {
        JPanel panelTitulo = new JPanel();
        panelTitulo.setBackground(new Color(70, 130, 180));
        panelTitulo.setPreferredSize(new Dimension(10, 60));
        add(panelTitulo, BorderLayout.NORTH);
        
        JLabel lblTitulo = new JLabel("Reportes del Sistema - Módulo Administrador");
        lblTitulo.setForeground(Color.WHITE);
        lblTitulo.setFont(new Font("Arial", Font.BOLD, 18));
        panelTitulo.add(lblTitulo);
        
        tabbedPane = new JTabbedPane(JTabbedPane.TOP);
        add(tabbedPane, BorderLayout.CENTER);
        
        tabbedPane.addTab("Resumen General", crearPanelResumenGeneral());
        tabbedPane.addTab("Libros Más Prestados", crearPanelLibrosPrestados());
        tabbedPane.addTab("Usuarios Más Activos", crearPanelUsuariosActivos());
        tabbedPane.addTab("Préstamos por Mes", crearPanelPrestamosMes());
        tabbedPane.addTab("Situación Actual", crearPanelSituacionActual());
    }
    
    private JPanel crearPanelResumenGeneral() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBorder(new EmptyBorder(20, 20, 20, 20));
         
        JPanel panelEstadisticas = new JPanel(new GridLayout(5, 2, 10, 10));
        panelEstadisticas.setBorder(BorderFactory.createTitledBorder("Estadísticas del Mes Actual"));
        
        lblTotalPrestamosMes = new JLabel("0");
        lblPrestamosActivos = new JLabel("0");
        lblPrestamosAtrasados = new JLabel("0");
        lblUsuariosSancionados = new JLabel("0");
        lblMultasPendientes = new JLabel("$0");
        
        Font fontValores = new Font("Arial", Font.BOLD, 16);
        lblTotalPrestamosMes.setFont(fontValores);
        lblPrestamosActivos.setFont(fontValores);
        lblPrestamosAtrasados.setFont(fontValores);
        lblUsuariosSancionados.setFont(fontValores);
        lblMultasPendientes.setFont(fontValores);
        
        lblTotalPrestamosMes.setForeground(new Color(0, 100, 0));
        lblPrestamosAtrasados.setForeground(Color.RED);
        lblMultasPendientes.setForeground(Color.RED);
        
        panelEstadisticas.add(new JLabel("Total de Préstamos del Mes:"));
        panelEstadisticas.add(lblTotalPrestamosMes);
        panelEstadisticas.add(new JLabel("Préstamos Activos:"));
        panelEstadisticas.add(lblPrestamosActivos);
        panelEstadisticas.add(new JLabel("Préstamos Atrasados:"));
        panelEstadisticas.add(lblPrestamosAtrasados);
        panelEstadisticas.add(new JLabel("Usuarios Sancionados:"));
        panelEstadisticas.add(lblUsuariosSancionados);
        panelEstadisticas.add(new JLabel("Multas Pendientes:"));
        panelEstadisticas.add(lblMultasPendientes);
        
        panel.add(panelEstadisticas, BorderLayout.NORTH);
        return panel;
    }
    
    private JPanel crearPanelLibrosPrestados() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBorder(new EmptyBorder(10, 10, 10, 10));
        
        JLabel lblInstrucciones = new JLabel("Top 10 - Libros más prestados en el último mes");
        lblInstrucciones.setBorder(new EmptyBorder(0, 0, 10, 0));
        panel.add(lblInstrucciones, BorderLayout.NORTH);
        
        // Tabla de libros más prestados
        JScrollPane scrollPane = new JScrollPane();
        tableLibrosPrestados = new JTable();
        tableLibrosPrestados.setModel(new DefaultTableModel(
            new Object[][] {},
            new String[] {"Posición", "Título", "Autor", "Préstamos", "Disponibles"}
        ) {
			private static final long serialVersionUID = 1L;

			@Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        });
        scrollPane.setViewportView(tableLibrosPrestados);
        
        panel.add(scrollPane, BorderLayout.CENTER);
        
        return panel;
    }
    
    private JPanel crearPanelUsuariosActivos() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBorder(new EmptyBorder(10, 10, 10, 10));
        
        JLabel lblInstrucciones = new JLabel("Top 10 - Usuarios más activos en el último mes");
        lblInstrucciones.setBorder(new EmptyBorder(0, 0, 10, 0));
        panel.add(lblInstrucciones, BorderLayout.NORTH);
        
        JScrollPane scrollPane = new JScrollPane();
        tableUsuariosActivos = new JTable();
        tableUsuariosActivos.setModel(new DefaultTableModel(
            new Object[][] {},
            new String[] {"Posición", "Usuario", "Préstamos", "Atrasos", "Sanciones"}
        ) {
			private static final long serialVersionUID = 1L;

			@Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        });
        scrollPane.setViewportView(tableUsuariosActivos);
        
        panel.add(scrollPane, BorderLayout.CENTER);
        
        return panel;
    }
    
    private JPanel crearPanelPrestamosMes() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBorder(new EmptyBorder(10, 10, 10, 10));
        
        JLabel lblInstrucciones = new JLabel("Distribución de préstamos en los últimos 6 meses");
        lblInstrucciones.setBorder(new EmptyBorder(0, 0, 10, 0));
        panel.add(lblInstrucciones, BorderLayout.NORTH);
        
        JScrollPane scrollPane = new JScrollPane();
        tablePrestamosMes = new JTable();
        tablePrestamosMes.setModel(new DefaultTableModel(
            new Object[][] {},
            new String[] {"Mes", "Total Préstamos", "Préstamos Activos", "Atrasos", "Tasa Devolución"}
        ) {
			private static final long serialVersionUID = 1L;

			@Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        });
        scrollPane.setViewportView(tablePrestamosMes);
        
        panel.add(scrollPane, BorderLayout.CENTER);
        
        return panel;
    }
    
    private JPanel crearPanelSituacionActual() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBorder(new EmptyBorder(10, 10, 10, 10));
        
        JLabel lblInstrucciones = new JLabel("Situación actual del sistema - Alertas y pendientes");
        lblInstrucciones.setBorder(new EmptyBorder(0, 0, 10, 0));
        panel.add(lblInstrucciones, BorderLayout.NORTH);
        
        JScrollPane scrollPane = new JScrollPane();
        tableSituacionActual = new JTable();
        tableSituacionActual.setModel(new DefaultTableModel(
            new Object[][] {},
            new String[] {"Tipo", "Descripción", "Cantidad", "Estado", "Acción Requerida"}
        ) {
			private static final long serialVersionUID = 1L;

			@Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        });
        scrollPane.setViewportView(tableSituacionActual);
        
        panel.add(scrollPane, BorderLayout.CENTER);
        
        return panel;
    }
    
    public void mostrarMensaje(String mensaje, int tipo) {
        JOptionPane.showMessageDialog(this, mensaje, "Mensaje", tipo);
    }
    
    public void mostrarError(String mensaje) {
        mostrarMensaje(mensaje, JOptionPane.ERROR_MESSAGE);
    }
    
    public void mostrarResumenGeneral(int totalPrestamosMes, int prestamosActivos, 
                                    int prestamosAtrasados, int usuariosSancionados, 
                                    int multasPendientes) {
        lblTotalPrestamosMes.setText(String.valueOf(totalPrestamosMes));
        lblPrestamosActivos.setText(String.valueOf(prestamosActivos));
        lblPrestamosAtrasados.setText(String.valueOf(prestamosAtrasados));
        lblUsuariosSancionados.setText(String.valueOf(usuariosSancionados));
        lblMultasPendientes.setText("$" + multasPendientes);
    }
    
    public void mostrarLibrosPrestados(List<Object[]> libros) {
        Object[][] datos = convertirListaATabla(libros);
        actualizarLibrosPrestados(datos); 
    }
    
    public void mostrarUsuariosActivos(List<Object[]> usuarios) {
        Object[][] datos = convertirListaATabla(usuarios);
        actualizarUsuariosActivos(datos); 
    }
    
    public void mostrarPrestamosMes(List<Object[]> prestamos) {
        Object[][] datos = convertirListaATabla(prestamos);
        actualizarPrestamosMes(datos); 
    }
    
    public void mostrarSituacionActual(List<Object[]> situacion) {
        Object[][] datos = convertirListaATabla(situacion);
        actualizarSituacionActual(datos); 
    }
    
    private Object[][] convertirListaATabla(List<Object[]> lista) {
        if (lista == null || lista.isEmpty()) {
            return new Object[0][0];
        }
        Object[][] datos = new Object[lista.size()][];
        for (int i = 0; i < lista.size(); i++) {
            datos[i] = lista.get(i);
        }
        return datos;
    }
    
    public void actualizarResumenGeneral(int totalPrestamosMes, int prestamosActivos, 
                                       int prestamosAtrasados, int usuariosSancionados, 
                                       int multasPendientes) {
        lblTotalPrestamosMes.setText(String.valueOf(totalPrestamosMes));
        lblPrestamosActivos.setText(String.valueOf(prestamosActivos));
        lblPrestamosAtrasados.setText(String.valueOf(prestamosAtrasados));
        lblUsuariosSancionados.setText(String.valueOf(usuariosSancionados));
        lblMultasPendientes.setText("$" + multasPendientes);
    }
    
    public void actualizarLibrosPrestados(Object[][] datos) {
        DefaultTableModel modelo = (DefaultTableModel) tableLibrosPrestados.getModel();
        modelo.setRowCount(0);
        for (Object[] fila : datos) {
            modelo.addRow(fila);
        }
    }
    
    public void actualizarUsuariosActivos(Object[][] datos) {
        DefaultTableModel modelo = (DefaultTableModel) tableUsuariosActivos.getModel();
        modelo.setRowCount(0);
        for (Object[] fila : datos) {
            modelo.addRow(fila);
        }
    }
    
    public void actualizarPrestamosMes(Object[][] datos) {
        DefaultTableModel modelo = (DefaultTableModel) tablePrestamosMes.getModel();
        modelo.setRowCount(0);
        for (Object[] fila : datos) {
            modelo.addRow(fila);
        }
    }
    
    public void actualizarSituacionActual(Object[][] datos) {
        DefaultTableModel modelo = (DefaultTableModel) tableSituacionActual.getModel();
        modelo.setRowCount(0);
        for (Object[] fila : datos) {
            modelo.addRow(fila);
        }
    }
}