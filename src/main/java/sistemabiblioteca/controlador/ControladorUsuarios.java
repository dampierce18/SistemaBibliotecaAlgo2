package sistemabiblioteca.controlador;

import sistemabiblioteca.dao.UsuarioDAO;
import sistemabiblioteca.modelo.Usuario;
import sistemabiblioteca.vista.PanelUsuarios;
import java.util.List;

import javax.swing.JOptionPane;

public class ControladorUsuarios {
    private PanelUsuarios vista;
    private UsuarioDAO usuarioDAO;
    
    public ControladorUsuarios(PanelUsuarios vista) {
        this(vista, new UsuarioDAO());
    }
    
    // Constructor para testing
    ControladorUsuarios(PanelUsuarios vista, UsuarioDAO usuarioDAO) {
        this.vista = vista;
        this.usuarioDAO = usuarioDAO;
        configurarEventos();
        cargarUsuarios();
    }
    
    void configurarEventos() {
        vista.agregarGuardarUsuarioListener(e -> guardarUsuario());
        vista.agregarLimpiarUsuarioListener(e -> vista.limpiarFormulario());
        vista.agregarEditarUsuarioListener(e -> editarUsuario());
        vista.agregarEliminarUsuarioListener(e -> eliminarUsuario());
        vista.agregarActualizarUsuariosListener(e -> cargarUsuarios());
        vista.agregarBuscarUsuarioListener(e -> buscarUsuarios());
    }
    
    void editarUsuario() {
        Integer usuarioId = vista.obtenerUsuarioIdSeleccionado();
        if (usuarioId == null) {
            vista.mostrarMensaje("Seleccione un usuario para editar", JOptionPane.WARNING_MESSAGE);
            return;
        }
        vista.mostrarMensaje("Funcionalidad de edición en desarrollo", JOptionPane.INFORMATION_MESSAGE);
    }

    void eliminarUsuario() {
        Integer usuarioId = vista.obtenerUsuarioIdSeleccionado();
        String nombre = vista.obtenerNombreUsuarioSeleccionado();
        
        if (usuarioId == null) {
            vista.mostrarMensaje("Seleccione un usuario para eliminar", JOptionPane.WARNING_MESSAGE);
            return;
        }
        
        boolean confirmado = vista.mostrarConfirmacion(
            "¿Está seguro de eliminar al usuario: " + nombre + "?",
            "Confirmar eliminación"
        );
        
        if (confirmado) {
            if (usuarioDAO.eliminarUsuario(usuarioId)) {
                vista.mostrarMensaje("Usuario eliminado exitosamente", JOptionPane.INFORMATION_MESSAGE);
                cargarUsuarios();
            } else {
                vista.mostrarMensaje("Error al eliminar el usuario", JOptionPane.ERROR_MESSAGE);
            }
        }
    }
    
    void guardarUsuario() {
        try {
            // Validaciones
            if (!vista.validarCamposUsuario()) {
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
                vista.mostrarMensaje("Usuario guardado exitosamente", JOptionPane.INFORMATION_MESSAGE);
                vista.limpiarFormulario();
                cargarUsuarios();
            } else {
                vista.mostrarMensaje("Error al guardar el usuario", JOptionPane.ERROR_MESSAGE);
            }
            
        } catch (Exception e) {
            vista.mostrarMensaje("Error: " + e.getMessage(), JOptionPane.ERROR_MESSAGE);
        }
    }
    
    void cargarUsuarios() {
        List<Usuario> usuarios = usuarioDAO.obtenerTodosLosUsuarios();
        vista.mostrarUsuarios(usuarios);
    }
    
    public int obtenerTotalUsuarios() {
        return usuarioDAO.contarTotalUsuarios();
    }
    
    void buscarUsuarios() {
        String criterio = vista.getCriterioBusquedaUsuario();
        String valor = vista.getTextoBusquedaUsuario();
        
        if (valor.isEmpty()) {
            vista.mostrarMensaje("Ingrese un valor para buscar", JOptionPane.WARNING_MESSAGE);
            return;
        }
        
        List<Usuario> usuarios = usuarioDAO.buscarUsuarios(criterio, valor);
        vista.mostrarResultadosBusqueda(usuarios);
        
        if (usuarios.isEmpty()) {
            vista.mostrarMensaje("No se encontraron usuarios", JOptionPane.INFORMATION_MESSAGE);
        }
    }
}