package sistemabiblioteca.vista;

import sistemabiblioteca.modelo.Libro;
import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.ActionListener;
import java.util.List;

public class PanelLibros extends JPanel {
    /**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private JTabbedPane tabbedPane;
    private JTable tableLibros;
    private JTable tableBusqueda;
    private JTextField txtTitulo;
    private JTextField txtAutor;
    private JTextField txtEditorial;
    private JTextField txtAnio;
    private JTextField txtEjemplares;
    private JComboBox<String> comboBoxCategoria;
    private JButton btnGuardar;
    private JButton btnLimpiar;
    private JButton btnEditar;
    private JButton btnEliminar;
    private JButton btnActualizar;
    private JButton btnBuscar;
    private JTextField txtBusqueda;
    private JComboBox<String> comboBoxCriterio;

    public void setModoEmpleado() {
        btnEditar.setVisible(false);
        btnEliminar.setVisible(false);
    }
    
    public PanelLibros() {
        setLayout(new BorderLayout(0, 0));
        
        // Panel de título
        JPanel panelTitulo = new JPanel();
        panelTitulo.setBackground(new Color(70, 130, 180));
        panelTitulo.setPreferredSize(new Dimension(10, 60));
        add(panelTitulo, BorderLayout.NORTH);
        panelTitulo.setLayout(new BorderLayout(0, 0));
        
        JLabel lblTitulo = new JLabel("Gestión de Libros");
        lblTitulo.setForeground(Color.WHITE);
        lblTitulo.setFont(new Font("Arial", Font.BOLD, 20));
        lblTitulo.setHorizontalAlignment(SwingConstants.CENTER);
        panelTitulo.add(lblTitulo);
        
        // Panel de pestañas
        tabbedPane = new JTabbedPane(JTabbedPane.TOP);
        add(tabbedPane, BorderLayout.CENTER);
        
        // Pestaña 1: Lista de libros
        JPanel panelLista = new JPanel();
        tabbedPane.addTab("Lista de Libros", null, panelLista, null);
        panelLista.setLayout(new BorderLayout(0, 0));
        
        // Botones superiores en lista
        JPanel panelBotonesLista = new JPanel();
        FlowLayout fl_panelBotonesLista = (FlowLayout) panelBotonesLista.getLayout();
        fl_panelBotonesLista.setAlignment(FlowLayout.LEFT);
        panelLista.add(panelBotonesLista, BorderLayout.NORTH);
        
        btnEditar = new JButton("Editar");
        panelBotonesLista.add(btnEditar);
        
        btnEliminar = new JButton("Eliminar");
        panelBotonesLista.add(btnEliminar);
        
        btnActualizar = new JButton("Actualizar Lista");
        panelBotonesLista.add(btnActualizar);
        
        // Tabla de libros
        JScrollPane scrollPaneLista = new JScrollPane();
        panelLista.add(scrollPaneLista, BorderLayout.CENTER);
        
        tableLibros = new JTable();
        tableLibros.setModel(new DefaultTableModel(
            new Object[][] {},
            new String[] {"ID", "Título", "Autor", "Año","Categoria", "Total Ejemplares", "Disponibles"}
        ) {
            private static final long serialVersionUID = 1L;
            boolean[] columnEditables = new boolean[] {false, false, false, false, false, false, false};
            public boolean isCellEditable(int row, int column) {
                return columnEditables[column];
            }
        });
        scrollPaneLista.setViewportView(tableLibros);
        
        // Pestaña 2: Agregar libro
        JPanel panelAgregar = new JPanel();
        tabbedPane.addTab("Agregar Libro", null, panelAgregar, null);
        panelAgregar.setLayout(new BorderLayout(0, 0));
        
        JPanel panelFormulario = new JPanel();
        panelFormulario.setBorder(new EmptyBorder(20, 20, 20, 20));
        panelAgregar.add(panelFormulario, BorderLayout.CENTER);
        panelFormulario.setLayout(new GridLayout(7, 2, 10, 10));
        
        // Campos del formulario
        
        JLabel lblTitulo_1 = new JLabel("Título:");
        panelFormulario.add(lblTitulo_1);
        
        txtTitulo = new JTextField();
        panelFormulario.add(txtTitulo);
        txtTitulo.setColumns(10);
        
        JLabel lblAutor = new JLabel("Autor:");
        panelFormulario.add(lblAutor);
        
        txtAutor = new JTextField();
        panelFormulario.add(txtAutor);
        txtAutor.setColumns(10);
        
        JLabel lblEditorial = new JLabel("Editorial:");
        panelFormulario.add(lblEditorial);
        
        txtEditorial = new JTextField();
        panelFormulario.add(txtEditorial);
        txtEditorial.setColumns(10);
        
        JLabel lblAnio = new JLabel("Año:");
        panelFormulario.add(lblAnio);
        
        txtAnio = new JTextField();
        panelFormulario.add(txtAnio);
        txtAnio.setColumns(10);
        
        JLabel lblCategoria = new JLabel("Categoría:");
        panelFormulario.add(lblCategoria);
        
        comboBoxCategoria = new JComboBox<>();
        comboBoxCategoria.setModel(new DefaultComboBoxModel<>(new String[] {
            "Ficción", "Ciencia", "Tecnología", "Historia", "Biografía", "Infantil", "Otros"
        }));
        panelFormulario.add(comboBoxCategoria);
        
        JLabel lblEjemplares = new JLabel("Ejemplares:");
        panelFormulario.add(lblEjemplares);
        
        txtEjemplares = new JTextField();
        panelFormulario.add(txtEjemplares);
        txtEjemplares.setColumns(10);
        
        // Botones en agregar
        JPanel panelBotonesAgregar = new JPanel();
        panelAgregar.add(panelBotonesAgregar, BorderLayout.SOUTH);
        
        btnGuardar = new JButton("Guardar Libro");
        panelBotonesAgregar.add(btnGuardar);
        
        btnLimpiar = new JButton("Limpiar Campos");
        panelBotonesAgregar.add(btnLimpiar);
        
        // Pestaña 3: Buscar libro
        JPanel panelBuscar = new JPanel();
        tabbedPane.addTab("Buscar Libro", null, panelBuscar, null);
        panelBuscar.setLayout(new BorderLayout(0, 0));
        
        // Panel de búsqueda
        JPanel panelBusqueda = new JPanel();
        panelBusqueda.setBorder(new EmptyBorder(20, 20, 20, 20));
        panelBuscar.add(panelBusqueda, BorderLayout.NORTH);
        panelBusqueda.setLayout(new FlowLayout(FlowLayout.LEFT, 5, 5));
        
        JLabel lblBuscarPor = new JLabel("Buscar por:");
        panelBusqueda.add(lblBuscarPor);
        
        comboBoxCriterio = new JComboBox<>();
        comboBoxCriterio.setModel(new DefaultComboBoxModel<>(new String[] {
            "Título", "Autor", "ID", "Categoría"
        }));
        panelBusqueda.add(comboBoxCriterio);
        
        txtBusqueda = new JTextField();
        panelBusqueda.add(txtBusqueda);
        txtBusqueda.setColumns(20);
        
        btnBuscar = new JButton("Buscar");
        panelBusqueda.add(btnBuscar);
        
        // Tabla de resultados
        JScrollPane scrollPaneBusqueda = new JScrollPane();
        panelBuscar.add(scrollPaneBusqueda, BorderLayout.CENTER);
        
        tableBusqueda = new JTable();
        tableBusqueda.setModel(new DefaultTableModel(
            new Object[][] {},
            new String[] {"ID", "Título", "Autor", "Año","Categoria", "Total Ejemplares", "Disponibles"}
        ) {
            private static final long serialVersionUID = 1L;
            boolean[] columnEditables = new boolean[] {false, false, false, false, false};
            public boolean isCellEditable(int row, int column) {
                return columnEditables[column];
            }
        });
        scrollPaneBusqueda.setViewportView(tableBusqueda);
    }

    public String getTitulo() { return txtTitulo.getText().trim(); }
    public String getAutor() { return txtAutor.getText().trim(); }
    public String getEditorial() { return txtEditorial.getText().trim(); }
    public String getAnioTexto() { return txtAnio.getText().trim(); }
    public String getEjemplaresTexto() { return txtEjemplares.getText().trim(); }
    public String getCategoria() { return comboBoxCategoria.getSelectedItem().toString(); }
    public String getTextoBusqueda() { return txtBusqueda.getText().trim(); }
    public String getCriterioBusqueda() { return comboBoxCriterio.getSelectedItem().toString(); }
    
    // Métodos para manipular la UI
    public void limpiarFormulario() {
        txtTitulo.setText("");
        txtAutor.setText("");
        txtEditorial.setText("");
        txtAnio.setText("");
        txtEjemplares.setText("");
        comboBoxCategoria.setSelectedIndex(0);
    }
    
    public void actualizarTablaLibros(Object[][] datos) {
        DefaultTableModel modelo = (DefaultTableModel) tableLibros.getModel();
        modelo.setRowCount(0);
        for (Object[] fila : datos) {
            modelo.addRow(fila);
        }
    }
    
    public void actualizarTablaBusqueda(Object[][] datos) {
        DefaultTableModel modelo = (DefaultTableModel) tableBusqueda.getModel();
        modelo.setRowCount(0);
        for (Object[] fila : datos) {
            modelo.addRow(fila);
        }
    }
    
    public void mostrarMensaje(String mensaje, int tipo) {
        JOptionPane.showMessageDialog(this, mensaje, "Mensaje", tipo);
    }
    
    public boolean mostrarConfirmacion(String mensaje, String titulo) {
        int confirmacion = JOptionPane.showConfirmDialog(
            this,
            mensaje,
            titulo,
            JOptionPane.YES_NO_OPTION,
            JOptionPane.WARNING_MESSAGE
        );
        return confirmacion == JOptionPane.YES_OPTION;
    }
    
    public boolean validarCamposLibro() {
        if (getTitulo().isEmpty() || getAutor().isEmpty()) {
            mostrarMensaje("Título y Autor son obligatorios", JOptionPane.ERROR_MESSAGE);
            return false;
        }
        return true;
    }
    
    public Integer obtenerLibroIdSeleccionado() {
        int fila = getFilaSeleccionadaLibros();
        if (fila == -1) {
            return null;
        }
        return (Integer) tableLibros.getValueAt(fila, 0);
    }
    
    public String obtenerTituloLibroSeleccionado() {
        int fila = getFilaSeleccionadaLibros();
        if (fila == -1) {
            return "";
        }
        return (String) tableLibros.getValueAt(fila, 1);
    }
    
    public void mostrarLibros(List<Libro> libros) {
        Object[][] datos = new Object[libros.size()][7];
        
        for (int i = 0; i < libros.size(); i++) {
            Libro libro = libros.get(i);
            datos[i][0] = libro.getId();
            datos[i][1] = libro.getTitulo();
            datos[i][2] = libro.getAutor();
            datos[i][3] = libro.getAnio();
            datos[i][4] = libro.getCategoria();
            datos[i][5] = libro.getTotal();
            datos[i][6] = libro.getDisponibles();
        }
        
        actualizarTablaLibros(datos);
    }
    
    public void mostrarResultadosBusqueda(List<Libro> libros) {
        Object[][] datos = new Object[libros.size()][5];
        
        for (int i = 0; i < libros.size(); i++) {
            Libro libro = libros.get(i);
            datos[i][0] = libro.getId();
            datos[i][1] = libro.getTitulo();
            datos[i][2] = libro.getAutor();
            datos[i][3] = libro.getCategoria();
            datos[i][4] = libro.getDisponibles();
        }
        
        actualizarTablaBusqueda(datos);
    }
    
    public int getFilaSeleccionadaLibros() {
        return tableLibros.getSelectedRow();
    }
    
    public int getFilaSeleccionadaBusqueda() {
        return tableBusqueda.getSelectedRow();
    }
    
    // Métodos para agregar listeners
    public void agregarGuardarListener(ActionListener listener) {
        btnGuardar.addActionListener(listener);
    }
    
    public void agregarLimpiarListener(ActionListener listener) {
        btnLimpiar.addActionListener(listener);
    }
    
    public void agregarEditarListener(ActionListener listener) {
        btnEditar.addActionListener(listener);
    }
    
    public void agregarEliminarListener(ActionListener listener) {
        btnEliminar.addActionListener(listener);
    }
    
    public void agregarActualizarListener(ActionListener listener) {
        btnActualizar.addActionListener(listener);
    }
    
    public void agregarBuscarListener(ActionListener listener) {
        btnBuscar.addActionListener(listener);
    }
    
    // Getters para las tablas 
    public JTable getTableLibros() { return tableLibros; }
    public JTable getTableBusqueda() { return tableBusqueda; }
    
    
}