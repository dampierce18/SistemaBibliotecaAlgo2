package sistemabiblioteca.controlador;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import sistemabiblioteca.dao.EmpleadoDAO;
import sistemabiblioteca.modelo.Empleado;
import sistemabiblioteca.vista.PanelEmpleados;

import javax.swing.*;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ControladorEmpleadosTest {

    @Mock
    private PanelEmpleados vista;

    @Mock
    private EmpleadoDAO empleadoDAO;

    private ControladorEmpleados controlador;

    @BeforeEach
    void configurar() {
        when(empleadoDAO.obtenerTodosLosEmpleados()).thenReturn(List.of());
        
        controlador = new ControladorEmpleados(vista, empleadoDAO);
    }

    @Test
    void testConstructor_configuraEventosYCargaEmpleados() {
        verify(vista).agregarGuardarEmpleadoListener(any());
        verify(vista).agregarLimpiarEmpleadoListener(any());
        verify(vista).agregarEditarEmpleadoListener(any());
        verify(vista).agregarEliminarEmpleadoListener(any());
        verify(vista).agregarActualizarEmpleadosListener(any());
        
        verify(empleadoDAO).obtenerTodosLosEmpleados();
    }

    @Test
    void testGuardarEmpleado_exitoso() {
        clearInvocations(vista, empleadoDAO);
        
        when(vista.validarCamposEmpleado()).thenReturn(true);
        when(vista.getNombre()).thenReturn("Juan");
        when(vista.getApellidoPaterno()).thenReturn("Pérez");
        when(vista.getApellidoMaterno()).thenReturn("Gómez");
        when(vista.getUsuario()).thenReturn("juanperez");
        when(vista.getPassword()).thenReturn("password123");
        when(vista.getRol()).thenReturn("EMPLEADO");
        when(vista.getTelefono()).thenReturn("555-1234");
        when(vista.getEmail()).thenReturn("juan@biblioteca.com");
        
        when(empleadoDAO.existeUsuario("juanperez")).thenReturn(false);
        when(empleadoDAO.insertarEmpleado(any(Empleado.class))).thenReturn(true);

        controlador.guardarEmpleado();

        verify(empleadoDAO).existeUsuario("juanperez");
        verify(empleadoDAO).insertarEmpleado(argThat(empleado -> 
            empleado.getNombre().equals("Juan") &&
            empleado.getApellidoPaterno().equals("Pérez") &&
            empleado.getApellidoMaterno().equals("Gómez") &&
            empleado.getUsuario().equals("juanperez") &&
            empleado.getPassword().equals("password123") &&
            empleado.getRol().equals("EMPLEADO") &&
            empleado.getTelefono().equals("555-1234") &&
            empleado.getEmail().equals("juan@biblioteca.com")
        ));
        verify(vista).limpiarFormulario();
        verify(vista).mostrarMensaje("Empleado guardado exitosamente", JOptionPane.INFORMATION_MESSAGE);
        verify(empleadoDAO).obtenerTodosLosEmpleados();
    }

    @Test
    void testGuardarEmpleado_validacionFallida_noGuarda() {
        clearInvocations(vista, empleadoDAO);
        
        when(vista.validarCamposEmpleado()).thenReturn(false);

        controlador.guardarEmpleado();

        verify(empleadoDAO, never()).existeUsuario(anyString());
        verify(empleadoDAO, never()).insertarEmpleado(any());
    }

    @Test
    void testGuardarEmpleado_usuarioYaExiste_muestraError() {
        clearInvocations(vista, empleadoDAO);
        
        when(vista.validarCamposEmpleado()).thenReturn(true);
        when(vista.getUsuario()).thenReturn("usuarioexistente");
        when(empleadoDAO.existeUsuario("usuarioexistente")).thenReturn(true);

        controlador.guardarEmpleado();

        verify(vista).mostrarMensaje("El nombre de usuario ya existe", JOptionPane.ERROR_MESSAGE);
        verify(empleadoDAO, never()).insertarEmpleado(any());
    }

    @Test
    void testGuardarEmpleado_insercionFallida_muestraError() {
        clearInvocations(vista, empleadoDAO);
        
        when(vista.validarCamposEmpleado()).thenReturn(true);
        when(vista.getUsuario()).thenReturn("nuevousuario");
        when(empleadoDAO.existeUsuario("nuevousuario")).thenReturn(false);
        when(empleadoDAO.insertarEmpleado(any(Empleado.class))).thenReturn(false);

        controlador.guardarEmpleado();

        verify(vista).mostrarMensaje("Error al guardar el empleado", JOptionPane.ERROR_MESSAGE);
        verify(vista, never()).limpiarFormulario();
    }

    @Test
    void testGuardarEmpleado_excepcion_muestraError() {
        clearInvocations(vista, empleadoDAO);
        
        when(vista.validarCamposEmpleado()).thenReturn(true);
        when(vista.getUsuario()).thenReturn("usuario");
        when(empleadoDAO.existeUsuario("usuario")).thenThrow(new RuntimeException("Error de BD"));

        controlador.guardarEmpleado();

        verify(vista).mostrarMensaje("Error: Error de BD", JOptionPane.ERROR_MESSAGE);
    }

    @Test
    void testCargarEmpleados_actualizaVista() {
        clearInvocations(vista, empleadoDAO);
        
        Empleado empleado1 = new Empleado();
        empleado1.setId(1);
        empleado1.setNombre("Juan");
        empleado1.setApellidoPaterno("Pérez");
        
        Empleado empleado2 = new Empleado();
        empleado2.setId(2);
        empleado2.setNombre("María");
        empleado2.setApellidoPaterno("Gómez");
        
        when(empleadoDAO.obtenerTodosLosEmpleados()).thenReturn(List.of(empleado1, empleado2));

        controlador.cargarEmpleados();

        verify(vista).mostrarEmpleados(List.of(empleado1, empleado2));
    }

    @Test
    void testCargarEmpleados_listaVacia_actualizaVista() {
        clearInvocations(vista, empleadoDAO);
        
        when(empleadoDAO.obtenerTodosLosEmpleados()).thenReturn(List.of());

        controlador.cargarEmpleados();

        verify(vista).mostrarEmpleados(List.of());
    }

    @Test
    void testEditarEmpleado_sinSeleccion_muestraAdvertencia() {
        clearInvocations(vista);
        
        when(vista.obtenerEmpleadoIdSeleccionado()).thenReturn(null);

        controlador.editarEmpleado();

        verify(vista).mostrarMensaje("Seleccione un empleado para editar", JOptionPane.WARNING_MESSAGE);
        verify(empleadoDAO, never()).obtenerEmpleadoPorId(anyInt());
    }

    @Test
    void testEditarEmpleado_conSeleccion_cargaDatos() {
        clearInvocations(vista, empleadoDAO);
        
        when(vista.obtenerEmpleadoIdSeleccionado()).thenReturn(1);
        
        Empleado empleado = new Empleado();
        empleado.setId(1);
        empleado.setNombre("Juan");
        empleado.setApellidoPaterno("Pérez");
        
        when(empleadoDAO.obtenerEmpleadoPorId(1)).thenReturn(empleado);

        controlador.editarEmpleado();

        verify(empleadoDAO).obtenerEmpleadoPorId(1);
        verify(vista).cargarDatosEnFormulario(empleado);
        verify(vista).cambiarAPestanaFormulario();
    }

    @Test
    void testEditarEmpleado_empleadoNoEncontrado_noCargaDatos() {
        clearInvocations(vista, empleadoDAO);
        
        when(vista.obtenerEmpleadoIdSeleccionado()).thenReturn(999);
        when(empleadoDAO.obtenerEmpleadoPorId(999)).thenReturn(null);

        controlador.editarEmpleado();

        verify(empleadoDAO).obtenerEmpleadoPorId(999);
        verify(vista, never()).cargarDatosEnFormulario(any());
        verify(vista, never()).cambiarAPestanaFormulario();
    }

    @Test
    void testEliminarEmpleado_sinSeleccion_muestraAdvertencia() {
        clearInvocations(vista, empleadoDAO);
        
        when(vista.obtenerEmpleadoIdSeleccionado()).thenReturn(null);

        controlador.eliminarEmpleado();

        verify(vista).mostrarMensaje("Seleccione un empleado para eliminar", JOptionPane.WARNING_MESSAGE);
        verify(empleadoDAO, never()).eliminarEmpleado(anyInt());
    }

    @Test
    void testEliminarEmpleado_confirmacionFalse_noElimina() {
        clearInvocations(vista, empleadoDAO);
        
        when(vista.obtenerEmpleadoIdSeleccionado()).thenReturn(1);
        when(vista.obtenerNombreEmpleadoSeleccionado()).thenReturn("Juan Pérez");
        when(vista.mostrarConfirmacion(anyString(), anyString())).thenReturn(false);

        controlador.eliminarEmpleado();

        verify(empleadoDAO, never()).eliminarEmpleado(anyInt());
    }

    @Test
    void testEliminarEmpleado_confirmacionTrue_eliminacionExitosa() {
        clearInvocations(vista, empleadoDAO);
        
        when(vista.obtenerEmpleadoIdSeleccionado()).thenReturn(1);
        when(vista.obtenerNombreEmpleadoSeleccionado()).thenReturn("Juan Pérez");
        when(vista.mostrarConfirmacion(anyString(), anyString())).thenReturn(true);
        when(empleadoDAO.eliminarEmpleado(1)).thenReturn(true);

        controlador.eliminarEmpleado();

        verify(empleadoDAO).eliminarEmpleado(1);
        verify(vista).mostrarMensaje("Empleado eliminado exitosamente", JOptionPane.INFORMATION_MESSAGE);
        verify(empleadoDAO).obtenerTodosLosEmpleados();
    }

    @Test
    void testEliminarEmpleado_confirmacionTrue_eliminacionFallida() {
        clearInvocations(vista, empleadoDAO);
        
        when(vista.obtenerEmpleadoIdSeleccionado()).thenReturn(1);
        when(vista.obtenerNombreEmpleadoSeleccionado()).thenReturn("Juan Pérez");
        when(vista.mostrarConfirmacion(anyString(), anyString())).thenReturn(true);
        when(empleadoDAO.eliminarEmpleado(1)).thenReturn(false);

        controlador.eliminarEmpleado();

        verify(empleadoDAO).eliminarEmpleado(1);
        verify(vista).mostrarMensaje("Error al eliminar el empleado", JOptionPane.ERROR_MESSAGE);
        verify(empleadoDAO, never()).obtenerTodosLosEmpleados();
    }

    @Test
    void testObtenerTotalEmpleados_retornaCantidadCorrecta() {
        clearInvocations(empleadoDAO);
        
        Empleado emp1 = new Empleado();
        Empleado emp2 = new Empleado();
        Empleado emp3 = new Empleado();
        
        when(empleadoDAO.obtenerTodosLosEmpleados()).thenReturn(List.of(emp1, emp2, emp3));

        int total = controlador.obtenerTotalEmpleados();

        assertEquals(3, total);
        verify(empleadoDAO).obtenerTodosLosEmpleados();
    }

    @Test
    void testObtenerTotalEmpleados_listaVacia_retornaCero() {
        clearInvocations(empleadoDAO);
        
        when(empleadoDAO.obtenerTodosLosEmpleados()).thenReturn(List.of());

        int total = controlador.obtenerTotalEmpleados();

        assertEquals(0, total);
        verify(empleadoDAO).obtenerTodosLosEmpleados();
    }

}