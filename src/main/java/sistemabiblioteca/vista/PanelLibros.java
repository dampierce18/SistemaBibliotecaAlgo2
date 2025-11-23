package sistemabiblioteca.vista;

import sistemabiblioteca.modelo.Libro;
import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.List;

public class PanelLibros extends JPanel {
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
    
    // Nuevos botones para la pestaña de búsqueda
    private JButton btnEditarBusqueda;
    private JButton btnEliminarBusqueda;
    
    // Componentes para el menú de ordenar
    private JMenuBar menuBar;
    private JMenu menuOrdenar;
    private ActionListener ordenarListener;

    // Etiqueta para mostrar información de préstamos activos
    private JLabel lblInfoPrestamos;

    public PanelLibros() {
        setLayout(new BorderLayout(0, 0));
        
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
        
        tabbedPane = new JTabbedPane(JTabbedPane.TOP);
        add(tabbedPane, BorderLayout.CENTER);
        
        // Crear las pestañas
        tabbedPane.addTab("Lista de Libros", crearPanelListaLibros());
        tabbedPane.addTab("Agregar/Editar Libro", crearPanelAgregarLibro());
        tabbedPane.addTab("Buscar Libro", crearPanelBuscarLibro());
    }
    
    public void setModoEmpleado() {
        btnEditar.setVisible(false);
        btnEliminar.setVisible(false);
        btnEditarBusqueda.setVisible(false);
        btnEliminarBusqueda.setVisible(false);
    }
    
    private void crearMenuOrdenar() {
        menuBar = new JMenuBar();
        menuOrdenar = new JMenu("Ordenar por");
        
        JMenuItem itemId = new JMenuItem("ID");
        JMenuItem itemTitulo = new JMenuItem("Título");
        JMenuItem itemAutor = new JMenuItem("Autor");
        JMenuItem itemCategoria = new JMenuItem("Categoría");
        JMenuItem itemDisponibles = new JMenuItem("Disponibles");
        JMenuItem itemAnio = new JMenuItem("Año");
        
        itemId.addActionListener(e -> notificarOrden("id"));
        itemTitulo.addActionListener(e -> notificarOrden("titulo"));
        itemAutor.addActionListener(e -> notificarOrden("autor"));
        itemCategoria.addActionListener(e -> notificarOrden("categoria"));
        itemDisponibles.addActionListener(e -> notificarOrden("disponibles DESC"));
        itemAnio.addActionListener(e -> notificarOrden("anio DESC"));
        
        menuOrdenar.add(itemId);
        menuOrdenar.add(itemTitulo);
        menuOrdenar.add(itemAutor);
        menuOrdenar.add(itemCategoria);
        menuOrdenar.add(itemDisponibles);
        menuOrdenar.add(itemAnio);
        
        menuBar.add(menuOrdenar);
    }
    
    private void notificarOrden(String criterio) {
        if (ordenarListener != null) {
            ActionEvent event = new ActionEvent(this, ActionEvent.ACTION_PERFORMED, criterio);
            ordenarListener.actionPerformed(event);
        }
    }
    
    public void setOrdenarListener(ActionListener listener) {
        this.ordenarListener = listener;
    }
    
    private JPanel crearPanelListaLibros() {
        JPanel panel = new JPanel(new BorderLayout(0, 0));

        // Crear menú de ordenar SOLO para esta pestaña
        crearMenuOrdenar();
        panel.add(menuBar, BorderLayout.NORTH);

        // Tabla de libros en el centro
        JScrollPane scrollPaneLista = new JScrollPane();
        tableLibros = new JTable();
        tableLibros.setModel(new DefaultTableModel(
            new Object[][] {},
            new String[] {"ID", "Título", "Autor", "Año", "Categoría", "Editorial", "Total Ejemplares", "Disponibles", "ID Empleado"}
        ) {
            private static final long serialVersionUID = 1L;
            boolean[] columnEditables = new boolean[] {false, false, false, false, false, false, false, false, false};
            public boolean isCellEditable(int row, int column) {
                return columnEditables[column];
            }
        });

        scrollPaneLista.setViewportView(tableLibros);
        panel.add(scrollPaneLista, BorderLayout.CENTER);

        // Botones en la parte inferior
        JPanel panelBotonesLista = new JPanel(new FlowLayout(FlowLayout.LEFT));
        panel.add(panelBotonesLista, BorderLayout.SOUTH);

        btnEditar = new JButton("Editar Libro");
        panelBotonesLista.add(btnEditar);

        btnEliminar = new JButton("Eliminar Libro");
        panelBotonesLista.add(btnEliminar);

        btnActualizar = new JButton("Actualizar Lista");
        panelBotonesLista.add(btnActualizar);

        return panel;
    }
    
    private JPanel crearPanelAgregarLibro() {
        JPanel panel = new JPanel(new BorderLayout(0, 0));
        
        JPanel panelFormulario = new JPanel();
        panelFormulario.setBorder(new EmptyBorder(20, 20, 20, 20));
        panel.add(panelFormulario, BorderLayout.CENTER);
        panelFormulario.setLayout(new GridLayout(8, 2, 10, 10));
        
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
        
        JLabel lblEjemplares = new JLabel("Total de Ejemplares:");
        panelFormulario.add(lblEjemplares);
        
        txtEjemplares = new JTextField();
        panelFormulario.add(txtEjemplares);
        txtEjemplares.setColumns(10);
        
        // Información de préstamos activos (solo visible en edición)
        JLabel lblInfo = new JLabel("Información:");
        panelFormulario.add(lblInfo);
        
        lblInfoPrestamos = new JLabel("Al editar, los disponibles se ajustarán automáticamente");
        lblInfoPrestamos.setForeground(Color.BLUE);
        lblInfoPrestamos.setFont(new Font("Arial", Font.ITALIC, 11));
        panelFormulario.add(lblInfoPrestamos);
        
        JPanel panelBotonesAgregar = new JPanel();
        panel.add(panelBotonesAgregar, BorderLayout.SOUTH);
        
        btnGuardar = new JButton("Guardar Libro");
        panelBotonesAgregar.add(btnGuardar);
        
        btnLimpiar = new JButton("Limpiar Campos");
        panelBotonesAgregar.add(btnLimpiar);
        
        return panel;
    }
    
    private JPanel crearPanelBuscarLibro() {
        JPanel panel = new JPanel(new BorderLayout(0, 0));
        
        // Panel superior con búsqueda
        JPanel panelBusqueda = new JPanel();
        panelBusqueda.setBorder(new EmptyBorder(20, 20, 20, 20));
        panel.add(panelBusqueda, BorderLayout.NORTH);
        panelBusqueda.setLayout(new FlowLayout(FlowLayout.LEFT, 5, 5));
        
        JLabel lblBuscarPor = new JLabel("Buscar por:");
        panelBusqueda.add(lblBuscarPor);
        
        comboBoxCriterio = new JComboBox<>();
        comboBoxCriterio.setModel(new DefaultComboBoxModel<>(new String[] {
            "Título", "Autor", "Categoría", "Editorial", "ID"
        }));
        panelBusqueda.add(comboBoxCriterio);
        
        txtBusqueda = new JTextField();
        panelBusqueda.add(txtBusqueda);
        txtBusqueda.setColumns(20);
        
        btnBuscar = new JButton("Buscar");
        panelBusqueda.add(btnBuscar);
        
        // Panel central con tabla
        JScrollPane scrollPaneBusqueda = new JScrollPane();
        panel.add(scrollPaneBusqueda, BorderLayout.CENTER);
        
        tableBusqueda = new JTable();
        tableBusqueda.setModel(new DefaultTableModel(
            new Object[][] {},
            new String[] {"ID", "Título", "Autor", "Año", "Categoría", "Editorial", "Total Ejemplares", "Disponibles", "ID Empleado"}
        ) {
            private static final long serialVersionUID = 1L;
            boolean[] columnEditables = new boolean[] {false, false, false, false, false, false, false, false, false};
            public boolean isCellEditable(int row, int column) {
                return columnEditables[column];
            }
        });
        scrollPaneBusqueda.setViewportView(tableBusqueda);
        
        // Panel inferior con botones de acción
        JPanel panelBotonesBusqueda = new JPanel(new FlowLayout(FlowLayout.LEFT));
        panel.add(panelBotonesBusqueda, BorderLayout.SOUTH);
        
        btnEditarBusqueda = new JButton("Editar Libro");
        panelBotonesBusqueda.add(btnEditarBusqueda);
        
        btnEliminarBusqueda = new JButton("Eliminar Libro");
        panelBotonesBusqueda.add(btnEliminarBusqueda);
        
        return panel;
    }

    // MÉTODOS PARA EL CONTROLADOR
    
    public void cargarDatosEnFormulario(Libro libro) {
        txtTitulo.setText(libro.getTitulo());
        txtAutor.setText(libro.getAutor());
        txtEditorial.setText(libro.getEditorial());
        txtAnio.setText(libro.getAnio());
        comboBoxCategoria.setSelectedItem(libro.getCategoria());
        txtEjemplares.setText(String.valueOf(libro.getTotal()));
        
        // Calcular libros prestados
        int prestados = libro.getTotal() - libro.getDisponibles();
        
        // Actualizar información de préstamos
        String info = String.format("Actual: Total=%d, Disponibles=%d, Prestados=%d", 
                                   libro.getTotal(), libro.getDisponibles(), prestados);
        lblInfoPrestamos.setText(info);
    }
    
    public void cambiarAPestanaFormulario() {
        tabbedPane.setSelectedIndex(1);
    }
    
    public void limpiarFormulario() {
        txtTitulo.setText("");
        txtAutor.setText("");
        txtEditorial.setText("");
        txtAnio.setText("");
        txtEjemplares.setText("");
        comboBoxCategoria.setSelectedIndex(0);
        lblInfoPrestamos.setText("Al editar, los disponibles se ajustarán automáticamente");
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
        if (getTitulo().isEmpty() || getAutor().isEmpty() || getEditorial().isEmpty()) {
            mostrarMensaje("Título, Autor y Editorial son obligatorios", JOptionPane.ERROR_MESSAGE);
            return false;
        }
        
        try {
            int ejemplares = Integer.parseInt(getEjemplaresTexto());
            if (ejemplares <= 0) {
                mostrarMensaje("Los ejemplares deben ser un número mayor a 0", JOptionPane.ERROR_MESSAGE);
                return false;
            }
        } catch (NumberFormatException e) {
            mostrarMensaje("Ejemplares debe ser un número válido", JOptionPane.ERROR_MESSAGE);
            return false;
        }
        
        return true;
    }
    
    // MÉTODOS PARA OBTENER SELECCIONES DE AMBAS TABLAS
    
    public Integer obtenerLibroIdSeleccionado() {
        // Primero verificar si hay selección en la tabla principal
        int fila = tableLibros.getSelectedRow();
        if (fila != -1) {
            return (Integer) tableLibros.getValueAt(fila, 0);
        }
        
        // Si no hay selección en la tabla principal, verificar en la tabla de búsqueda
        fila = tableBusqueda.getSelectedRow();
        if (fila != -1) {
            return (Integer) tableBusqueda.getValueAt(fila, 0);
        }
        
        return null;
    }
    
    public String obtenerTituloLibroSeleccionado() {
        // Primero verificar si hay selección en la tabla principal
        int fila = tableLibros.getSelectedRow();
        if (fila != -1) {
            return (String) tableLibros.getValueAt(fila, 1);
        }
        
        // Si no hay selección en la tabla principal, verificar en la tabla de búsqueda
        fila = tableBusqueda.getSelectedRow();
        if (fila != -1) {
            return (String) tableBusqueda.getValueAt(fila, 1);
        }
        
        return "";
    }
    
    public void mostrarLibros(List<Libro> libros) {
        Object[][] datos = new Object[libros.size()][9];
        
        for (int i = 0; i < libros.size(); i++) {
            Libro libro = libros.get(i);
            datos[i][0] = libro.getId();
            datos[i][1] = libro.getTitulo();
            datos[i][2] = libro.getAutor();
            datos[i][3] = libro.getAnio();
            datos[i][4] = libro.getCategoria();
            datos[i][5] = libro.getEditorial();
            datos[i][6] = libro.getTotal();
            datos[i][7] = libro.getDisponibles();
            datos[i][8] = libro.getEmpleadoId();
        }
        
        actualizarTablaLibros(datos);
    }
    
    public void mostrarResultadosBusqueda(List<Libro> libros) {
        Object[][] datos = new Object[libros.size()][9];
        
        for (int i = 0; i < libros.size(); i++) {
            Libro libro = libros.get(i);
            datos[i][0] = libro.getId();
            datos[i][1] = libro.getTitulo();
            datos[i][2] = libro.getAutor();
            datos[i][3] = libro.getAnio();
            datos[i][4] = libro.getCategoria();
            datos[i][5] = libro.getEditorial();
            datos[i][6] = libro.getTotal();
            datos[i][7] = libro.getDisponibles();
            datos[i][8] = libro.getEmpleadoId();
        }
        
        actualizarTablaBusqueda(datos);
    }
    
    public int getFilaSeleccionadaLibros() {
        return tableLibros.getSelectedRow();
    }
    
    public int getFilaSeleccionadaBusqueda() {
        return tableBusqueda.getSelectedRow();
    }
    
    // MÉTODOS PARA AGREGAR LISTENERS
    public void agregarGuardarListener(ActionListener listener) {
        btnGuardar.addActionListener(listener);
    }
    
    public void agregarLimpiarListener(ActionListener listener) {
        btnLimpiar.addActionListener(listener);
    }
    
    public void agregarEditarListener(ActionListener listener) {
        btnEditar.addActionListener(listener);
        btnEditarBusqueda.addActionListener(listener);
    }
    
    public void agregarEliminarListener(ActionListener listener) {
        btnEliminar.addActionListener(listener);
        btnEliminarBusqueda.addActionListener(listener);
    }
    
    public void agregarActualizarListener(ActionListener listener) {
        btnActualizar.addActionListener(listener);
    }
    
    public void agregarBuscarListener(ActionListener listener) {
        btnBuscar.addActionListener(listener);
    }
    
    public JTable getTableLibros() { return tableLibros; }
    public JTable getTableBusqueda() { return tableBusqueda; }
    
    public String getTitulo() { return txtTitulo.getText().trim(); }
    public String getAutor() { return txtAutor.getText().trim(); }
    public String getEditorial() { return txtEditorial.getText().trim(); }
    public String getAnioTexto() { return txtAnio.getText().trim(); } 
    public String getEjemplaresTexto() { return txtEjemplares.getText().trim(); }
    public String getCategoria() { return comboBoxCategoria.getSelectedItem().toString(); }
    public String getTextoBusqueda() { return txtBusqueda.getText().trim(); }
    public String getCriterioBusqueda() { return comboBoxCriterio.getSelectedItem().toString(); }
}