package sistemabiblioteca.vista;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.ActionListener;

public class PanelUsuarios extends JPanel {
    /**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private JTabbedPane tabbedPane;
    private JTable tableUsuarios;
    
    // Campos del formulario
    private JTextField txtNombre;
    private JTextField txtApellidoPaterno;
    private JTextField txtApellidoMaterno;
    private JTextField txtDomicilio;
    private JTextField txtTelefono;
    
    // Botones
    private JButton btnGuardarUsuario;
    private JButton btnLimpiarUsuario;
    private JButton btnEditar;
    private JButton btnEliminar;
    private JButton btnActualizar;

    public PanelUsuarios() {
        setLayout(new BorderLayout(0, 0));
        inicializarComponentes();
    }
    
    public void setModoEmpleado() {
        btnEditar.setVisible(false);
        btnEliminar.setVisible(false);
        
        // Tabla de solo lectura
        //tableUsuarios.setEnabled(true); // Pero permitir ver
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
        
        // Panel de pestañas
        tabbedPane = new JTabbedPane(JTabbedPane.TOP);
        add(tabbedPane, BorderLayout.CENTER);
        
        // Pestaña 1: Lista de usuarios
        tabbedPane.addTab("Lista de Usuarios", crearPanelListaUsuarios());
        
        // Pestaña 2: Agregar usuario
        tabbedPane.addTab("Agregar Usuario", crearPanelAgregarUsuario());
    }
    
    private JPanel crearPanelListaUsuarios() {
        JPanel panel = new JPanel(new BorderLayout());
        
        // Botones superiores en lista
        JPanel panelBotonesLista = new JPanel();
        FlowLayout fl_panelBotonesLista = (FlowLayout) panelBotonesLista.getLayout();
        fl_panelBotonesLista.setAlignment(FlowLayout.LEFT);
        panel.add(panelBotonesLista, BorderLayout.NORTH);
        
        btnEditar = new JButton("Editar");
        panelBotonesLista.add(btnEditar);
        
        btnEliminar = new JButton("Eliminar");
        panelBotonesLista.add(btnEliminar);
        
        btnActualizar = new JButton("Actualizar Lista");
        panelBotonesLista.add(btnActualizar);
        
        // Tabla de usuarios
        JScrollPane scrollPane = new JScrollPane();
        tableUsuarios = new JTable();
        tableUsuarios.setModel(new DefaultTableModel(
            new Object[][] {},
            new String[] {"ID", "Nombre", "Apellido Paterno", "Apellido Materno", "Teléfono", "Sanciones", "Monto Sanción"}
        ));
        scrollPane.setViewportView(tableUsuarios);
        
        panel.add(scrollPane, BorderLayout.CENTER);
        
        return panel;
    }
    
    private JPanel crearPanelAgregarUsuario() {
        JPanel panel = new JPanel(new BorderLayout());
        
        // Formulario
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
        
        // Botones
        JPanel panelBotones = new JPanel();
        btnGuardarUsuario = new JButton("Guardar Usuario");
        btnLimpiarUsuario = new JButton("Limpiar Campos");
        
        panelBotones.add(btnGuardarUsuario);
        panelBotones.add(btnLimpiarUsuario);
        
        panel.add(panelBotones, BorderLayout.SOUTH);
        
        return panel;
    }
    
    // Getters para los datos del formulario
    public String getNombre() { return txtNombre.getText().trim(); }
    public String getApellidoPaterno() { return txtApellidoPaterno.getText().trim(); }
    public String getApellidoMaterno() { return txtApellidoMaterno.getText().trim(); }
    public String getDomicilio() { return txtDomicilio.getText().trim(); }
    public String getTelefono() { return txtTelefono.getText().trim(); }
    
    // Métodos para manipular la tabla
    public void actualizarTablaUsuarios(Object[][] datos) {
        DefaultTableModel modelo = (DefaultTableModel) tableUsuarios.getModel();
        modelo.setRowCount(0);
        for (Object[] fila : datos) {
            modelo.addRow(fila);
        }
    }
    
    // Método para limpiar formulario
    public void limpiarFormulario() {
        txtNombre.setText("");
        txtApellidoPaterno.setText("");
        txtApellidoMaterno.setText("");
        txtDomicilio.setText("");
        txtTelefono.setText("");
    }
    
    // Métodos para agregar listeners
    public void agregarGuardarUsuarioListener(ActionListener listener) {
        btnGuardarUsuario.addActionListener(listener);
    }
    
    public void agregarLimpiarUsuarioListener(ActionListener listener) {
        btnLimpiarUsuario.addActionListener(listener);
    }
    
    public JTable getTableUsuarios() {
        return tableUsuarios;
    }

    public int getFilaSeleccionadaUsuarios() {
        return tableUsuarios.getSelectedRow();
    }

    // Métodos para agregar listeners de los nuevos botones
    public void agregarEditarUsuarioListener(ActionListener listener) {
        btnEditar.addActionListener(listener);
    }

    public void agregarEliminarUsuarioListener(ActionListener listener) {
        btnEliminar.addActionListener(listener);
    }

    public void agregarActualizarUsuariosListener(ActionListener listener) {
        btnActualizar.addActionListener(listener);
    }
}