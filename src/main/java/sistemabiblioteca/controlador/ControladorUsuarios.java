package sistemabiblioteca.controlador;

import sistemabiblioteca.dao.UsuarioDAO;
import sistemabiblioteca.modelo.Empleado;
import sistemabiblioteca.modelo.Usuario;
import sistemabiblioteca.vista.PanelUsuarios;
import java.util.List;

import javax.swing.JOptionPane;

public class ControladorUsuarios {
    private PanelUsuarios vista;
    private UsuarioDAO usuarioDAO;
    private Empleado empleadoLogueado;
    private String ordenActual = "id"; // Orden por defecto
    private boolean modoEdicion = false;
    private Integer usuarioEditandoId = null;
    
    // Constructor original modificado
    public ControladorUsuarios(PanelUsuarios vista, Empleado empleadoLogueado) {
        this(vista, new UsuarioDAO(), empleadoLogueado);
    }
    
    // Constructor para testing modificado
    ControladorUsuarios(PanelUsuarios vista, UsuarioDAO usuarioDAO, Empleado empleadoLogueado) {
        this.vista = vista;
        this.usuarioDAO = usuarioDAO;
        this.empleadoLogueado = empleadoLogueado;
        
        // Configurar el listener para el menú de ordenar
        vista.setOrdenarListener(e -> ordenarUsuarios(e.getActionCommand()));
        
        configurarEventos();
        cargarUsuarios();
    }
    
    // Mantener constructor antiguo para compatibilidad (si es necesario)
    public ControladorUsuarios(PanelUsuarios vista) {
        this(vista, new UsuarioDAO(), null);
    }
    
    // Constructor antiguo para testing
    ControladorUsuarios(PanelUsuarios vista, UsuarioDAO usuarioDAO) {
        this(vista, usuarioDAO, null);
    }
    
    void configurarEventos() {
        vista.agregarGuardarUsuarioListener(e -> guardarUsuario());
        vista.agregarLimpiarUsuarioListener(e -> limpiarFormulario());
        vista.agregarEditarUsuarioListener(e -> editarUsuario());
        vista.agregarEliminarUsuarioListener(e -> eliminarUsuario());
        vista.agregarActualizarUsuariosListener(e -> cargarUsuarios());
        vista.agregarBuscarUsuarioListener(e -> buscarUsuarios());
    }
    
    private void ordenarUsuarios(String criterio) {
        this.ordenActual = criterio;
        cargarUsuarios();
        
        // Mostrar mensaje de confirmación (opcional)
        String mensaje = "";
        switch (criterio) {
            case "id":
                mensaje = "Ordenado por ID";
                break;
            case "nombre":
                mensaje = "Ordenado por Nombre";
                break;
            case "apellido_paterno":
                mensaje = "Ordenado por Apellido Paterno";
                break;
            case "telefono":
                mensaje = "Ordenado por Teléfono";
                break;
            case "sanciones DESC":
                mensaje = "Ordenado por Sanciones (mayor a menor)";
                break;
            default:
                mensaje = "Orden aplicado";
        }
        vista.mostrarMensaje(mensaje, JOptionPane.INFORMATION_MESSAGE);
    }
    
    void editarUsuario() {
        Integer usuarioId = vista.obtenerUsuarioIdSeleccionado();
        if (usuarioId == null) {
            vista.mostrarMensaje("Seleccione un usuario para editar", JOptionPane.WARNING_MESSAGE);
            return;
        }
        
        // Obtener el usuario seleccionado
        Usuario usuario = usuarioDAO.obtenerUsuarioPorId(usuarioId);
        if (usuario == null) {
            vista.mostrarMensaje("Error al cargar los datos del usuario", JOptionPane.ERROR_MESSAGE);
            return;
        }
        
        // Cargar datos en el formulario y cambiar a pestaña de agregar/editar
        vista.cargarDatosEnFormulario(usuario);
        vista.cambiarAPestanaFormulario();
        
        // Activar modo edición
        this.modoEdicion = true;
        this.usuarioEditandoId = usuarioId;
        
        vista.mostrarMensaje("Editando usuario: " + usuario.getNombreCompleto(), JOptionPane.INFORMATION_MESSAGE);
    }

    void guardarUsuario() {
        try {
            if (!vista.validarCamposUsuario()) {
                return;
            }
            
            if (modoEdicion) {
                // Modo edición - Actualizar usuario existente
                actualizarUsuario();
            } else {
                // Modo nuevo - Insertar usuario
                insertarUsuario();
            }
            
        } catch (Exception e) {
            vista.mostrarMensaje("Error: " + e.getMessage(), JOptionPane.ERROR_MESSAGE);
        }
    }
    
    private void insertarUsuario() {
        // Verificar que hay un empleado logueado
        if (empleadoLogueado == null) {
            vista.mostrarMensaje("Error: No hay empleado logueado", JOptionPane.ERROR_MESSAGE);
            return;
        }
        
        // Crear usuario con el empleado que lo registra
        Usuario usuario = new Usuario(
            vista.getNombre(),
            vista.getApellidoPaterno(),
            vista.getApellidoMaterno(),
            vista.getDomicilio(),
            vista.getTelefono(),
            empleadoLogueado.getId()
        );
        
        boolean resultado = usuarioDAO.insertarUsuario(usuario);
        if (resultado) {
            vista.mostrarMensaje("Usuario guardado exitosamente", JOptionPane.INFORMATION_MESSAGE);
            limpiarFormulario();
            cargarUsuarios();
        } else {
            vista.mostrarMensaje("Error al guardar el usuario", JOptionPane.ERROR_MESSAGE);
        }
    }
    
    private void actualizarUsuario() {
        if (usuarioEditandoId == null) {
            vista.mostrarMensaje("Error: No se pudo identificar el usuario a editar", JOptionPane.ERROR_MESSAGE);
            return;
        }
        
        // Obtener el usuario original para mantener el empleado_id
        Usuario usuarioOriginal = usuarioDAO.obtenerUsuarioPorId(usuarioEditandoId);
        if (usuarioOriginal == null) {
            vista.mostrarMensaje("Error: No se encontró el usuario a editar", JOptionPane.ERROR_MESSAGE);
            return;
        }
        
        // Crear usuario actualizado
        Usuario usuarioActualizado = new Usuario(
            vista.getNombre(),
            vista.getApellidoPaterno(),
            vista.getApellidoMaterno(),
            vista.getDomicilio(),
            vista.getTelefono(),
            usuarioOriginal.getEmpleadoId() // Mantener el empleado original
        );
        usuarioActualizado.setId(usuarioEditandoId);
        usuarioActualizado.setSanciones(usuarioOriginal.getSanciones());
        usuarioActualizado.setMontoSancion(usuarioOriginal.getMontoSancion());
        
        if (usuarioDAO.actualizarUsuario(usuarioActualizado)) {
            vista.mostrarMensaje("Usuario actualizado exitosamente", JOptionPane.INFORMATION_MESSAGE);
            limpiarFormulario();
            cargarUsuarios();
        } else {
            vista.mostrarMensaje("Error al actualizar el usuario", JOptionPane.ERROR_MESSAGE);
        }
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
    
    void cargarUsuarios() {
        List<Usuario> usuarios = usuarioDAO.obtenerTodosLosUsuarios(ordenActual);
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
        } else {
            // Cambiar a la pestaña de búsqueda automáticamente
            vista.cambiarAPestanaBusqueda();
        }
    }
    
    private void limpiarFormulario() {
        vista.limpiarFormulario();
        this.modoEdicion = false;
        this.usuarioEditandoId = null;
    }
    
    // Getter para testing
    public Empleado getEmpleadoLogueado() {
        return empleadoLogueado;
    }
    
    // Getter para el orden actual (útil para testing)
    public String getOrdenActual() {
        return ordenActual;
    }
    
    // Getters para testing del modo edición
    public boolean isModoEdicion() {
        return modoEdicion;
    }
    
    public Integer getUsuarioEditandoId() {
        return usuarioEditandoId;
    }
}