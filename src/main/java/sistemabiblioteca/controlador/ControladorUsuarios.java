package sistemabiblioteca.controlador;

import sistemabiblioteca.dao.UsuarioDAO;
import sistemabiblioteca.modelo.Usuario;
import sistemabiblioteca.vista.PanelUsuarios;
import javax.swing.*;
import java.util.List;

public class ControladorUsuarios {
    private PanelUsuarios vista;
    private UsuarioDAO usuarioDAO;
    
    public ControladorUsuarios(PanelUsuarios vista) {
        this.vista = vista;
        this.usuarioDAO = new UsuarioDAO();
        configurarEventos();
        cargarUsuarios();
    }
    
    private void configurarEventos() {
        vista.agregarGuardarUsuarioListener(e -> guardarUsuario());
        vista.agregarLimpiarUsuarioListener(e -> vista.limpiarFormulario());
        vista.agregarEditarUsuarioListener(e -> editarUsuario());
        vista.agregarEliminarUsuarioListener(e -> eliminarUsuario());
        vista.agregarActualizarUsuariosListener(e -> cargarUsuarios());
    }
    
    private void editarUsuario() {
        int filaSeleccionada = vista.getFilaSeleccionadaUsuarios();
        if (filaSeleccionada == -1) {
            JOptionPane.showMessageDialog(vista, "Seleccione un usuario para editar", "Advertencia", JOptionPane.WARNING_MESSAGE);
            return;
        }
        JOptionPane.showMessageDialog(vista, "Funcionalidad de edición en desarrollo", "Info", JOptionPane.INFORMATION_MESSAGE);
    }

    private void eliminarUsuario() {
        int filaSeleccionada = vista.getFilaSeleccionadaUsuarios();
        if (filaSeleccionada == -1) {
            JOptionPane.showMessageDialog(vista, "Seleccione un usuario para eliminar", "Advertencia", JOptionPane.WARNING_MESSAGE);
            return;
        }
        
        int id = (int) vista.getTableUsuarios().getValueAt(filaSeleccionada, 0);
        String nombre = (String) vista.getTableUsuarios().getValueAt(filaSeleccionada, 1);
        
        int confirmacion = JOptionPane.showConfirmDialog(
            vista,
            "¿Está seguro de eliminar al usuario: " + nombre + "?",
            "Confirmar eliminación",
            JOptionPane.YES_NO_OPTION,
            JOptionPane.WARNING_MESSAGE
        );
        
        if (confirmacion == JOptionPane.YES_OPTION) {
            if (usuarioDAO.eliminarUsuario(id)) {
                JOptionPane.showMessageDialog(vista, "Usuario eliminado exitosamente", "Éxito", JOptionPane.INFORMATION_MESSAGE);
                cargarUsuarios();
            } else {
                JOptionPane.showMessageDialog(vista, "Error al eliminar el usuario", "Error", JOptionPane.ERROR_MESSAGE);
            }
        }
    }
    
    private void guardarUsuario() {
        try {
            // Validaciones
            if (vista.getNombre().isEmpty() || vista.getApellidoPaterno().isEmpty()) {
                JOptionPane.showMessageDialog(vista, "Nombre y Apellido Paterno son obligatorios", "Error", JOptionPane.ERROR_MESSAGE);
                return;
            }
            
            // Crear objeto Usuario
            Usuario usuario = new Usuario(
                vista.getNombre(),
                vista.getApellidoPaterno(),
                vista.getApellidoMaterno(),
                vista.getDomicilio(),
                vista.getTelefono()
            );
            
            // Guardar en base de datos
            if (usuarioDAO.insertarUsuario(usuario)) {
                JOptionPane.showMessageDialog(vista, "Usuario guardado exitosamente", "Éxito", JOptionPane.INFORMATION_MESSAGE);
                vista.limpiarFormulario();
                cargarUsuarios();
            } else {
                JOptionPane.showMessageDialog(vista, "Error al guardar el usuario", "Error", JOptionPane.ERROR_MESSAGE);
            }
            
        } catch (Exception e) {
            JOptionPane.showMessageDialog(vista, "Error: " + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
    }
    
    private void cargarUsuarios() {
        List<Usuario> usuarios = usuarioDAO.obtenerTodosLosUsuarios();
        Object[][] datos = new Object[usuarios.size()][7];
        
        for (int i = 0; i < usuarios.size(); i++) {
            Usuario usuario = usuarios.get(i);
            datos[i][0] = usuario.getId();
            datos[i][1] = usuario.getNombre();
            datos[i][2] = usuario.getApellidoPaterno();
            datos[i][3] = usuario.getApellidoMaterno();
            datos[i][4] = usuario.getTelefono();
            datos[i][5] = usuario.getSanciones();
            datos[i][6] = usuario.getMontoSancion();
        }
        
        vista.actualizarTablaUsuarios(datos);
    }
    
    public int obtenerTotalUsuarios() {
        return usuarioDAO.contarTotalUsuarios();
    }
}