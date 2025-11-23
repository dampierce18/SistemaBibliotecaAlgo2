package sistemabiblioteca.vista;

import sistemabiblioteca.modelo.Empleado;
import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.ActionListener;
import java.util.List;

public class PanelEmpleados extends JPanel {
    private static final long serialVersionUID = 1L;
    private JTabbedPane tabbedPane;
    private JTable tableEmpleados;
    
    // Campos del formulario
    private JTextField txtNombre;
    private JTextField txtApellidoPaterno;
    private JTextField txtApellidoMaterno;
    private JTextField txtUsuario;
    private JPasswordField txtPassword;
    private JTextField txtTelefono;
    private JTextField txtEmail;
    private JComboBox<String> comboBoxRol;
    
    // Botones
    private JButton btnGuardarEmpleado;
    private JButton btnLimpiarEmpleado;
    private JButton btnEditar;
    private JButton btnEliminar;
    private JButton btnActualizar;
    

    public PanelEmpleados() {
        setLayout(new BorderLayout(0, 0));
        inicializarComponentes();
    }
    
    private void inicializarComponentes() {
        // Panel de título
        JPanel panelTitulo = new JPanel();
        panelTitulo.setBackground(new Color(70, 130, 180));
        panelTitulo.setPreferredSize(new Dimension(10, 60));
        add(panelTitulo, BorderLayout.NORTH);
        panelTitulo.setLayout(new BorderLayout(0, 0));
        
        JLabel lblTitulo = new JLabel("Gestión de Empleados - Módulo Administrador");
        lblTitulo.setHorizontalAlignment(SwingConstants.CENTER);
        lblTitulo.setForeground(Color.WHITE);
        lblTitulo.setFont(new Font("Arial", Font.BOLD, 20));
        panelTitulo.add(lblTitulo);
        
        // Panel de pestañas
        tabbedPane = new JTabbedPane(JTabbedPane.TOP);
        add(tabbedPane, BorderLayout.CENTER);
        
        // Pestaña 1: Lista de empleados
        tabbedPane.addTab("Lista de Empleados", crearPanelListaEmpleados());
        
        // Pestaña 2: Agregar/Editar empleado
        tabbedPane.addTab("Agregar Empleado", crearPanelAgregarEmpleado());
    }
    
    private JPanel crearPanelListaEmpleados() {
        JPanel panel = new JPanel(new BorderLayout());
        
        // Botones superiores
        JPanel panelBotones = new JPanel(new FlowLayout(FlowLayout.LEFT));
        btnEditar = new JButton("Editar Empleado");
        btnEliminar = new JButton("Eliminar Empleado");
        btnActualizar = new JButton("Actualizar Lista");
        
        panelBotones.add(btnEditar);
        panelBotones.add(btnEliminar);
        panelBotones.add(btnActualizar);
        
        panel.add(panelBotones, BorderLayout.NORTH);
        
        // Tabla de empleados
        JScrollPane scrollPane = new JScrollPane();
        tableEmpleados = new JTable();
        tableEmpleados.setModel(new DefaultTableModel(
            new Object[][] {},
            new String[] {"ID", "Nombre", "Apellido Paterno", "Apellido Materno", 
                         "Usuario", "Rol", "Teléfono", "Email"}
        ) {
            /**
			 * 
			 */
			private static final long serialVersionUID = 1L;

			@Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        });
        scrollPane.setViewportView(tableEmpleados);
        
        panel.add(scrollPane, BorderLayout.CENTER);
        
        return panel;
    }
    
    private JPanel crearPanelAgregarEmpleado() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBorder(new EmptyBorder(20, 20, 20, 20));
        
        // Formulario
        JPanel panelFormulario = new JPanel(new GridLayout(8, 2, 10, 10));
        
        txtNombre = new JTextField();
        txtApellidoPaterno = new JTextField();
        txtApellidoMaterno = new JTextField();
        txtUsuario = new JTextField();
        txtPassword = new JPasswordField();
        txtTelefono = new JTextField();
        txtEmail = new JTextField();
        comboBoxRol = new JComboBox<>(new String[]{"EMPLEADO", "ADMIN"});
        
        panelFormulario.add(new JLabel("Nombre:"));
        panelFormulario.add(txtNombre);
        panelFormulario.add(new JLabel("Apellido Paterno:"));
        panelFormulario.add(txtApellidoPaterno);
        panelFormulario.add(new JLabel("Apellido Materno:"));
        panelFormulario.add(txtApellidoMaterno);
        panelFormulario.add(new JLabel("Usuario:"));
        panelFormulario.add(txtUsuario);
        panelFormulario.add(new JLabel("Contraseña:"));
        panelFormulario.add(txtPassword);
        panelFormulario.add(new JLabel("Rol:"));
        panelFormulario.add(comboBoxRol);
        panelFormulario.add(new JLabel("Teléfono:"));
        panelFormulario.add(txtTelefono);
        panelFormulario.add(new JLabel("Email:"));
        panelFormulario.add(txtEmail);
        
        panel.add(panelFormulario, BorderLayout.CENTER);
        
        // Botones
        JPanel panelBotones = new JPanel();
        btnGuardarEmpleado = new JButton("Guardar Empleado");
        btnLimpiarEmpleado = new JButton("Limpiar Campos");
        
        panelBotones.add(btnGuardarEmpleado);
        panelBotones.add(btnLimpiarEmpleado);
        
        panel.add(panelBotones, BorderLayout.SOUTH);
        
        return panel;
    }
    
    // === MÉTODOS PARA EL CONTROLADOR ===
    
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
    
    public boolean validarCamposEmpleado() {
        if (getNombre().isEmpty() || getApellidoPaterno().isEmpty() || 
            getUsuario().isEmpty() || getPassword().isEmpty()) {
            mostrarMensaje("Nombre, Apellido Paterno, Usuario y Contraseña son obligatorios", 
                          JOptionPane.ERROR_MESSAGE);
            return false;
        }
        return true;
    }
    
    public Integer obtenerEmpleadoIdSeleccionado() {
        int fila = tableEmpleados.getSelectedRow();
        if (fila == -1) {
            return null;
        }
        return (Integer) tableEmpleados.getValueAt(fila, 0);
    }
    
    public String obtenerNombreEmpleadoSeleccionado() {
        int fila = tableEmpleados.getSelectedRow();
        if (fila == -1) {
            return "";
        }
        return (String) tableEmpleados.getValueAt(fila, 1) + " " + 
               tableEmpleados.getValueAt(fila, 2) + " " + 
               tableEmpleados.getValueAt(fila, 3);
    }
    
    public void mostrarEmpleados(List<Empleado> empleados) {
        Object[][] datos = new Object[empleados.size()][9];
        
        for (int i = 0; i < empleados.size(); i++) {
            Empleado empleado = empleados.get(i);
            datos[i][0] = empleado.getId();
            datos[i][1] = empleado.getNombre();
            datos[i][2] = empleado.getApellidoPaterno();
            datos[i][3] = empleado.getApellidoMaterno();
            datos[i][4] = empleado.getUsuario();
            datos[i][5] = empleado.getRol();
            datos[i][6] = empleado.getTelefono();
            datos[i][7] = empleado.getEmail();
        }
        
        actualizarTablaEmpleados(datos);
    }
    
    public void cargarDatosEnFormulario(Empleado empleado) {
        txtNombre.setText(empleado.getNombre());
        txtApellidoPaterno.setText(empleado.getApellidoPaterno());
        txtApellidoMaterno.setText(empleado.getApellidoMaterno());
        txtUsuario.setText(empleado.getUsuario());
        txtPassword.setText(empleado.getPassword());
        comboBoxRol.setSelectedItem(empleado.getRol());
        txtTelefono.setText(empleado.getTelefono());
        txtEmail.setText(empleado.getEmail());
    }
    
    public void cambiarAPestanaFormulario() {
        tabbedPane.setSelectedIndex(1);
    }
    
    // === GETTERS PARA DATOS DEL FORMULARIO ===
    
    public String getNombre() { return txtNombre.getText().trim(); }
    public String getApellidoPaterno() { return txtApellidoPaterno.getText().trim(); }
    public String getApellidoMaterno() { return txtApellidoMaterno.getText().trim(); }
    public String getUsuario() { return txtUsuario.getText().trim(); }
    public String getPassword() { return new String(txtPassword.getPassword()); }
    public String getRol() { return comboBoxRol.getSelectedItem().toString(); }
    public String getTelefono() { return txtTelefono.getText().trim(); }
    public String getEmail() { return txtEmail.getText().trim(); }
    
    // === MÉTODOS PARA MANIPULAR LA UI ===
    
    public void limpiarFormulario() {
        txtNombre.setText("");
        txtApellidoPaterno.setText("");
        txtApellidoMaterno.setText("");
        txtUsuario.setText("");
        txtPassword.setText("");
        comboBoxRol.setSelectedIndex(0);
        txtTelefono.setText("");
        txtEmail.setText("");
    }
    
    public void actualizarTablaEmpleados(Object[][] datos) {
        DefaultTableModel modelo = (DefaultTableModel) tableEmpleados.getModel();
        modelo.setRowCount(0);
        for (Object[] fila : datos) {
            modelo.addRow(fila);
        }
    }
    
    // === MÉTODOS PARA AGREGAR LISTENERS ===
    
    public void agregarGuardarEmpleadoListener(ActionListener listener) {
        btnGuardarEmpleado.addActionListener(listener);
    }
    
    public void agregarLimpiarEmpleadoListener(ActionListener listener) {
        btnLimpiarEmpleado.addActionListener(listener);
    }
    
    public void agregarEditarEmpleadoListener(ActionListener listener) {
        btnEditar.addActionListener(listener);
    }
    
    public void agregarEliminarEmpleadoListener(ActionListener listener) {
        btnEliminar.addActionListener(listener);
    }
    
    public void agregarActualizarEmpleadosListener(ActionListener listener) {
        btnActualizar.addActionListener(listener);
    }
}