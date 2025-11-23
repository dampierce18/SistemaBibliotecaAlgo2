package sistemabiblioteca.vista;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.List;
import sistemabiblioteca.modelo.Usuario;

public class PanelUsuarios extends JPanel {
    private static final long serialVersionUID = 1L;
    private JTabbedPane tabbedPane;
    private JTable tableUsuarios;
    private JTable tableBusquedaUsuarios;
    private JMenuBar menuBar;
    private JMenu menuOrdenar;
    
    private JTextField txtNombre;
    private JTextField txtApellidoPaterno;
    private JTextField txtApellidoMaterno;
    private JTextField txtDomicilio;
    private JTextField txtTelefono;
    private JTextField txtBusquedaUsuario;

    private JButton btnGuardarUsuario;
    private JButton btnLimpiarUsuario;
    private JButton btnEditar;
    private JButton btnEliminar;
    private JButton btnActualizar;
    private JButton btnBuscarUsuario;
    
    // Nuevos botones para la pestaña de búsqueda
    private JButton btnEditarBusqueda;
    private JButton btnEliminarBusqueda;
    
    private JComboBox<String> comboBoxCriterioUsuario;
    
    // Referencia al controlador para el menú de ordenar
    private ActionListener ordenarListener;

    public PanelUsuarios() {
        setLayout(new BorderLayout(0, 0));
        inicializarComponentes();
    }
    
    public void setModoEmpleado() {
        if (btnEditar != null) btnEditar.setVisible(false);
        if (btnEliminar != null) btnEliminar.setVisible(false);
        if (btnEditarBusqueda != null) btnEditarBusqueda.setVisible(false);
        if (btnEliminarBusqueda != null) btnEliminarBusqueda.setVisible(false);
    }
    
    private void inicializarComponentes() {
        // Panel de título
        JPanel panelTitulo = new JPanel();
        panelTitulo.setBackground(new Color(70, 130, 180));
        panelTitulo.setPreferredSize(new Dimension(10, 60));
        add(panelTitulo, BorderLayout.NORTH);
        panelTitulo.setLayout(new BorderLayout(0, 0));
        
        JLabel lblTitulo = new JLabel("Gestión de Usuarios");
        lblTitulo.setHorizontalAlignment(SwingConstants.CENTER);
        lblTitulo.setForeground(Color.WHITE);
        lblTitulo.setFont(new Font("Arial", Font.BOLD, 20));
        panelTitulo.add(lblTitulo, BorderLayout.CENTER);
        
        // Panel principal con menú
        JPanel panelPrincipal = new JPanel(new BorderLayout());
        
        tabbedPane = new JTabbedPane(JTabbedPane.TOP);
        panelPrincipal.add(tabbedPane, BorderLayout.CENTER);
        
        tabbedPane.addTab("Lista de Usuarios", crearPanelListaUsuarios());
        tabbedPane.addTab("Agregar/Editar Usuario", crearPanelAgregarUsuario());
        tabbedPane.addTab("Buscar Usuario", crearPanelBuscarUsuario());
        
        add(panelPrincipal, BorderLayout.CENTER);
    }
    
    private void crearMenuOrdenar() {
        menuBar = new JMenuBar();
        menuOrdenar = new JMenu("Ordenar por");
        
        JMenuItem itemId = new JMenuItem("ID");
        JMenuItem itemNombre = new JMenuItem("Nombre");
        JMenuItem itemApellido = new JMenuItem("Apellido Paterno");
        JMenuItem itemTelefono = new JMenuItem("Teléfono");
        JMenuItem itemSanciones = new JMenuItem("Sanciones");
        
        itemId.addActionListener(e -> notificarOrden("id"));
        itemNombre.addActionListener(e -> notificarOrden("nombre"));
        itemApellido.addActionListener(e -> notificarOrden("apellido_paterno"));
        itemTelefono.addActionListener(e -> notificarOrden("telefono"));
        itemSanciones.addActionListener(e -> notificarOrden("sanciones DESC"));
        
        menuOrdenar.add(itemId);
        menuOrdenar.add(itemNombre);
        menuOrdenar.add(itemApellido);
        menuOrdenar.add(itemTelefono);
        menuOrdenar.add(itemSanciones);
        
        menuBar.add(menuOrdenar);
    }
    
    private void notificarOrden(String criterio) {
        if (ordenarListener != null) {
            // Crear un evento personalizado con el criterio
            ActionEvent event = new ActionEvent(this, ActionEvent.ACTION_PERFORMED, criterio);
            ordenarListener.actionPerformed(event);
        }
    }
    
    public void setOrdenarListener(ActionListener listener) {
        this.ordenarListener = listener;
    }
    
    private JPanel crearPanelListaUsuarios() {
        JPanel panel = new JPanel(new BorderLayout());

        // 🔹 Crear menú de ordenar
        crearMenuOrdenar();
        panel.add(menuBar, BorderLayout.NORTH);  // <<--- Aquí colocamos el menú SOLO en esta pestaña

        JPanel panelBotonesLista = new JPanel();
        FlowLayout fl_panelBotonesLista = (FlowLayout) panelBotonesLista.getLayout();
        fl_panelBotonesLista.setAlignment(FlowLayout.LEFT);
        panel.add(panelBotonesLista, BorderLayout.SOUTH);

        btnEditar = new JButton("Editar Usuario");
        panelBotonesLista.add(btnEditar);

        btnEliminar = new JButton("Eliminar Usuario");
        panelBotonesLista.add(btnEliminar);

        btnActualizar = new JButton("Actualizar Lista");
        panelBotonesLista.add(btnActualizar);

        JScrollPane scrollPane = new JScrollPane();
        tableUsuarios = new JTable();
        tableUsuarios.setModel(new DefaultTableModel(
            new Object[][] {},
            new String[] {"ID", "Nombre", "Apellido Paterno", "Apellido Materno", "Teléfono", "Sanciones", "Monto Sanción", "Creado por"}
        ) {
            private static final long serialVersionUID = 1L;

            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        });

        scrollPane.setViewportView(tableUsuarios);
        panel.add(scrollPane, BorderLayout.CENTER);

        return panel;
    }

    
    private JPanel crearPanelAgregarUsuario() {
        JPanel panel = new JPanel(new BorderLayout());
        
        JPanel panelFormulario = new JPanel(new GridLayout(7, 2, 10, 10));
        panelFormulario.setBorder(new EmptyBorder(20, 20, 20, 20));
        
        txtNombre = new JTextField();
        txtNombre.setColumns(10);
        txtNombre.setFont(new Font("Tahoma", Font.PLAIN, 11));
        txtApellidoPaterno = new JTextField();
        txtApellidoMaterno = new JTextField();
        txtDomicilio = new JTextField();
        txtTelefono = new JTextField();
        
        panelFormulario.add(new JLabel("Nombre:"));
        panelFormulario.add(txtNombre);
        panelFormulario.add(new JLabel("Apellido Paterno:"));
        panelFormulario.add(txtApellidoPaterno);
        panelFormulario.add(new JLabel("Apellido Materno:"));
        panelFormulario.add(txtApellidoMaterno);
        panelFormulario.add(new JLabel("Domicilio:"));
        panelFormulario.add(txtDomicilio);
        panelFormulario.add(new JLabel("Teléfono:"));
        panelFormulario.add(txtTelefono);
        
        panel.add(panelFormulario, BorderLayout.CENTER);
        
        JPanel panelBotones = new JPanel();
        btnGuardarUsuario = new JButton("Guardar Usuario");
        btnLimpiarUsuario = new JButton("Limpiar Campos");
        
        panelBotones.add(btnGuardarUsuario);
        panelBotones.add(btnLimpiarUsuario);
        
        panel.add(panelBotones, BorderLayout.SOUTH);
        
        return panel;
    }
    
    private JPanel crearPanelBuscarUsuario() {
        JPanel panel = new JPanel(new BorderLayout(0, 0));
        
        // Panel superior con búsqueda
        JPanel panelBusqueda = new JPanel();
        panelBusqueda.setBorder(new EmptyBorder(20, 20, 20, 20));
        panel.add(panelBusqueda, BorderLayout.NORTH);
        panelBusqueda.setLayout(new FlowLayout(FlowLayout.LEFT, 5, 5));
        
        JLabel lblBuscarPor = new JLabel("Buscar por:");
        panelBusqueda.add(lblBuscarPor);
        
        comboBoxCriterioUsuario = new JComboBox<>();
        comboBoxCriterioUsuario.setModel(new DefaultComboBoxModel<>(new String[] {
            "Nombre", "Apellido Paterno", "Teléfono", "ID"
        }));
        panelBusqueda.add(comboBoxCriterioUsuario);
        
        txtBusquedaUsuario = new JTextField();
        panelBusqueda.add(txtBusquedaUsuario);
        txtBusquedaUsuario.setColumns(20);
        
        btnBuscarUsuario = new JButton("Buscar");
        panelBusqueda.add(btnBuscarUsuario);
        
        // Panel central con tabla
        JScrollPane scrollPaneBusqueda = new JScrollPane();
        panel.add(scrollPaneBusqueda, BorderLayout.CENTER);
        
        tableBusquedaUsuarios = new JTable();
        tableBusquedaUsuarios.setModel(new DefaultTableModel(
            new Object[][] {},
            new String[] {"ID", "Nombre", "Apellido Paterno", "Apellido Materno", "Teléfono", "Sanciones", "Creado por"}
        ) {
            private static final long serialVersionUID = 1L;

            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        });
        scrollPaneBusqueda.setViewportView(tableBusquedaUsuarios);
        
        // Panel inferior con botones de acción
        JPanel panelBotonesBusqueda = new JPanel(new FlowLayout(FlowLayout.LEFT));
        panel.add(panelBotonesBusqueda, BorderLayout.SOUTH);
        
        btnEditarBusqueda = new JButton("Editar Usuario");
        panelBotonesBusqueda.add(btnEditarBusqueda);
        
        btnEliminarBusqueda = new JButton("Eliminar Usuario");
        panelBotonesBusqueda.add(btnEliminarBusqueda);
        
        return panel;
    }

    // MÉTODOS NUEVOS PARA EL CONTROLADOR
    
    public void cargarDatosEnFormulario(Usuario usuario) {
        if (txtNombre != null) txtNombre.setText(usuario.getNombre());
        if (txtApellidoPaterno != null) txtApellidoPaterno.setText(usuario.getApellidoPaterno());
        if (txtApellidoMaterno != null) txtApellidoMaterno.setText(usuario.getApellidoMaterno());
        if (txtDomicilio != null) txtDomicilio.setText(usuario.getDomicilio());
        if (txtTelefono != null) txtTelefono.setText(usuario.getTelefono());
    }
    
    public void cambiarAPestanaFormulario() {
        if (tabbedPane != null) {
            tabbedPane.setSelectedIndex(1);
        }
    }
    
    public void cambiarAPestanaBusqueda() {
        if (tabbedPane != null) {
            tabbedPane.setSelectedIndex(2);
        }
    }
    
    // GETTERS para los campos del formulario
    public String getNombre() { return txtNombre != null ? txtNombre.getText().trim() : ""; }
    public String getApellidoPaterno() { return txtApellidoPaterno != null ? txtApellidoPaterno.getText().trim() : ""; }
    public String getApellidoMaterno() { return txtApellidoMaterno != null ? txtApellidoMaterno.getText().trim() : ""; }
    public String getDomicilio() { return txtDomicilio != null ? txtDomicilio.getText().trim() : ""; }
    public String getTelefono() { return txtTelefono != null ? txtTelefono.getText().trim() : ""; }
    public String getTextoBusquedaUsuario() { return txtBusquedaUsuario != null ? txtBusquedaUsuario.getText().trim() : ""; }
    public String getCriterioBusquedaUsuario() { return comboBoxCriterioUsuario != null ? comboBoxCriterioUsuario.getSelectedItem().toString() : ""; }
    
    // MÉTODOS PARA ACTUALIZAR TABLAS
    public void actualizarTablaUsuarios(Object[][] datos) {
        if (tableUsuarios != null) {
            DefaultTableModel modelo = (DefaultTableModel) tableUsuarios.getModel();
            modelo.setRowCount(0);
            for (Object[] fila : datos) {
                modelo.addRow(fila);
            }
        }
    }
    
    public void actualizarTablaBusquedaUsuarios(Object[][] datos) {
        if (tableBusquedaUsuarios != null) {
            DefaultTableModel modelo = (DefaultTableModel) tableBusquedaUsuarios.getModel();
            modelo.setRowCount(0);
            for (Object[] fila : datos) {
                modelo.addRow(fila);
            }
        }
    }
    
    // MÉTODOS DE VALIDACIÓN Y UTILIDAD
    public boolean validarCamposUsuario() {
        if (getNombre().isEmpty() || getApellidoPaterno().isEmpty()) {
            mostrarMensaje("Nombre y Apellido Paterno son obligatorios", JOptionPane.ERROR_MESSAGE);
            return false;
        }
        return true;
    }
    
    // MÉTODOS PARA OBTENER SELECCIONES DE AMBAS TABLAS
    
    public Integer obtenerUsuarioIdSeleccionado() {
        // Primero verificar si hay selección en la tabla principal
        if (tableUsuarios != null) {
            int fila = tableUsuarios.getSelectedRow();
            if (fila != -1) {
                return (Integer) tableUsuarios.getValueAt(fila, 0);
            }
        }
        
        // Si no hay selección en la tabla principal, verificar en la tabla de búsqueda
        if (tableBusquedaUsuarios != null) {
            int fila = tableBusquedaUsuarios.getSelectedRow();
            if (fila != -1) {
                return (Integer) tableBusquedaUsuarios.getValueAt(fila, 0);
            }
        }
        
        return null;
    }
    
    public String obtenerNombreUsuarioSeleccionado() {
        // Primero verificar si hay selección en la tabla principal
        if (tableUsuarios != null) {
            int fila = tableUsuarios.getSelectedRow();
            if (fila != -1) {
                return (String) tableUsuarios.getValueAt(fila, 1) + " " + 
                       tableUsuarios.getValueAt(fila, 2);
            }
        }
        
        // Si no hay selección en la tabla principal, verificar en la tabla de búsqueda
        if (tableBusquedaUsuarios != null) {
            int fila = tableBusquedaUsuarios.getSelectedRow();
            if (fila != -1) {
                return (String) tableBusquedaUsuarios.getValueAt(fila, 1) + " " + 
                       tableBusquedaUsuarios.getValueAt(fila, 2);
            }
        }
        
        return "";
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
    
    public void mostrarMensaje(String mensaje, int tipo) {
        JOptionPane.showMessageDialog(this, mensaje, "Mensaje", tipo);
    }
    
    // MÉTODOS PARA MOSTRAR DATOS
    public void mostrarUsuarios(List<Usuario> usuarios) {
        Object[][] datos = new Object[usuarios.size()][8];
        
        for (int i = 0; i < usuarios.size(); i++) {
            Usuario usuario = usuarios.get(i);
            datos[i][0] = usuario.getId();
            datos[i][1] = usuario.getNombre();
            datos[i][2] = usuario.getApellidoPaterno();
            datos[i][3] = usuario.getApellidoMaterno();
            datos[i][4] = usuario.getTelefono();
            datos[i][5] = usuario.getSanciones();
            datos[i][6] = usuario.getMontoSancion();
            datos[i][7] = usuario.getEmpleadoId();        
        }
        
        actualizarTablaUsuarios(datos);
    }
    
    public void mostrarResultadosBusqueda(List<Usuario> usuarios) {
        Object[][] datos = new Object[usuarios.size()][7];
        
        for (int i = 0; i < usuarios.size(); i++) {
            Usuario usuario = usuarios.get(i);
            datos[i][0] = usuario.getId();
            datos[i][1] = usuario.getNombre();
            datos[i][2] = usuario.getApellidoPaterno();
            datos[i][3] = usuario.getApellidoMaterno();
            datos[i][4] = usuario.getTelefono();
            datos[i][5] = usuario.getSanciones();
            datos[i][6] = usuario.getEmpleadoId();        
        }
        
        actualizarTablaBusquedaUsuarios(datos);
    }
    
    public void limpiarFormulario() {
        if (txtNombre != null) txtNombre.setText("");
        if (txtApellidoPaterno != null) txtApellidoPaterno.setText("");
        if (txtApellidoMaterno != null) txtApellidoMaterno.setText("");
        if (txtDomicilio != null) txtDomicilio.setText("");
        if (txtTelefono != null) txtTelefono.setText("");
    }
    
    // GETTERS para componentes
    public JTable getTableBusquedaUsuarios() {
        return tableBusquedaUsuarios;
    }
    
    public int getFilaSeleccionadaBusquedaUsuarios() {
        return tableBusquedaUsuarios != null ? tableBusquedaUsuarios.getSelectedRow() : -1;
    }
    
    public JTable getTableUsuarios() {
        return tableUsuarios;
    }

    public int getFilaSeleccionadaUsuarios() {
        return tableUsuarios != null ? tableUsuarios.getSelectedRow() : -1;
    }

    // MÉTODOS PARA AGREGAR LISTENERS
    public void agregarGuardarUsuarioListener(ActionListener listener) {
        if (btnGuardarUsuario != null) {
            btnGuardarUsuario.addActionListener(listener);
        }
    }
    
    public void agregarLimpiarUsuarioListener(ActionListener listener) {
        if (btnLimpiarUsuario != null) {
            btnLimpiarUsuario.addActionListener(listener);
        }
    }

    public void agregarEditarUsuarioListener(ActionListener listener) {
        if (btnEditar != null) {
            btnEditar.addActionListener(listener);
        }
        if (btnEditarBusqueda != null) {
            btnEditarBusqueda.addActionListener(listener);
        }
    }

    public void agregarEliminarUsuarioListener(ActionListener listener) {
        if (btnEliminar != null) {
            btnEliminar.addActionListener(listener);
        }
        if (btnEliminarBusqueda != null) {
            btnEliminarBusqueda.addActionListener(listener);
        }
    }

    public void agregarActualizarUsuariosListener(ActionListener listener) {
        if (btnActualizar != null) {
            btnActualizar.addActionListener(listener);
        }
    }
    
    public void agregarBuscarUsuarioListener(ActionListener listener) {
        if (btnBuscarUsuario != null) {
            btnBuscarUsuario.addActionListener(listener);
        }
    }
}