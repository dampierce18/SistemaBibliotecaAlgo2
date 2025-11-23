package sistemabiblioteca.controlador;

import sistemabiblioteca.dao.EmpleadoDAO;
import sistemabiblioteca.modelo.Empleado;
import sistemabiblioteca.vista.PanelEmpleados;
import javax.swing.*;
import java.util.List;

public class ControladorEmpleados {
    private PanelEmpleados vista;
    private EmpleadoDAO empleadoDAO;
    
    public ControladorEmpleados(PanelEmpleados vista) {
        this.vista = vista;
        this.empleadoDAO = new EmpleadoDAO();
        configurarEventos();
        cargarEmpleados();
    }
    
    ControladorEmpleados(PanelEmpleados vista, EmpleadoDAO empleadoDAO) {
        this.vista = vista;
        this.empleadoDAO = empleadoDAO;
        configurarEventos();
        cargarEmpleados();
    }
    
     void configurarEventos() {
        vista.agregarGuardarEmpleadoListener(e -> guardarEmpleado());
        vista.agregarLimpiarEmpleadoListener(e -> vista.limpiarFormulario());
        vista.agregarEditarEmpleadoListener(e -> editarEmpleado());
        vista.agregarEliminarEmpleadoListener(e -> eliminarEmpleado());
        vista.agregarActualizarEmpleadosListener(e -> cargarEmpleados());
    }
    
     void guardarEmpleado() {
        try {
            if (!vista.validarCamposEmpleado()) {
                return;
            }
            
            if (empleadoDAO.existeUsuario(vista.getUsuario())) {
                vista.mostrarMensaje("El nombre de usuario ya existe", JOptionPane.ERROR_MESSAGE);
                return;
            }
            
            Empleado empleado = new Empleado(
                vista.getNombre(),
                vista.getApellidoPaterno(),
                vista.getApellidoMaterno(),
                vista.getUsuario(),
                vista.getPassword(), // En un caso real, esto debería encriptarse
                vista.getRol(),
                vista.getTelefono(),
                vista.getEmail()
            );
            
            // Guardar en base de datos
            if (empleadoDAO.insertarEmpleado(empleado)) {
                vista.mostrarMensaje("Empleado guardado exitosamente", JOptionPane.INFORMATION_MESSAGE);
                vista.limpiarFormulario();
                cargarEmpleados();
            } else {
                vista.mostrarMensaje("Error al guardar el empleado", JOptionPane.ERROR_MESSAGE);
            }
            
        } catch (Exception e) {
            vista.mostrarMensaje("Error: " + e.getMessage(), JOptionPane.ERROR_MESSAGE);
        }
    }
    
     void cargarEmpleados() {
        List<Empleado> empleados = empleadoDAO.obtenerTodosLosEmpleados();
        vista.mostrarEmpleados(empleados);
    }
    
     void editarEmpleado() {
        Integer empleadoId = vista.obtenerEmpleadoIdSeleccionado();
        if (empleadoId == null) {
            vista.mostrarMensaje("Seleccione un empleado para editar", JOptionPane.WARNING_MESSAGE);
            return;
        }
        
        Empleado empleado = empleadoDAO.obtenerEmpleadoPorId(empleadoId);
        if (empleado != null) {
            vista.cargarDatosEnFormulario(empleado);
            vista.cambiarAPestanaFormulario();
        }
    }
    
     void eliminarEmpleado() {
        Integer empleadoId = vista.obtenerEmpleadoIdSeleccionado();
        String nombreCompleto = vista.obtenerNombreEmpleadoSeleccionado();
        
        if (empleadoId == null) {
            vista.mostrarMensaje("Seleccione un empleado para eliminar", JOptionPane.WARNING_MESSAGE);
            return;
        }
        
        boolean confirmado = vista.mostrarConfirmacion(
            "¿Está seguro de eliminar al empleado: " + nombreCompleto + "?",
            "Confirmar eliminación"
        );
        
        if (confirmado) {
            if (empleadoDAO.eliminarEmpleado(empleadoId)) {
                vista.mostrarMensaje("Empleado eliminado exitosamente", JOptionPane.INFORMATION_MESSAGE);
                cargarEmpleados();
            } else {
                vista.mostrarMensaje("Error al eliminar el empleado", JOptionPane.ERROR_MESSAGE);
            }
        }
    }
    
    public int obtenerTotalEmpleados() {
        return empleadoDAO.obtenerTodosLosEmpleados().size();
    }
}