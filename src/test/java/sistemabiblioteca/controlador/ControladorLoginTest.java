package sistemabiblioteca.controlador;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.MockedStatic;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import sistemabiblioteca.dao.EmpleadoDAO;
import sistemabiblioteca.modelo.Empleado;
import sistemabiblioteca.vista.LoginFrame;

import javax.swing.*;
import java.lang.reflect.Field;
import java.util.concurrent.atomic.AtomicBoolean;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ControladorLoginTest {

    @Mock
    private LoginFrame vista;

    @Mock
    private JButton btnLogin;

    @Mock
    private JButton btnSalir;

    @Mock
    private JTextField txtUsuario;

    @Mock
    private JPasswordField txtPassword;

    @Mock
    private EmpleadoDAO empleadoDAO;

    @Mock
    private Empleado empleado;

    private ControladorLogin controlador;
    private AtomicBoolean exitCalled;

    @BeforeEach
    void configurar() throws Exception {
        // Configurar los mocks de los componentes de la vista
        lenient().when(vista.getBtnLogin()).thenReturn(btnLogin);
        lenient().when(vista.getBtnSalir()).thenReturn(btnSalir);
        lenient().when(vista.getTxtUsuario()).thenReturn(txtUsuario);
        lenient().when(vista.getTxtPassword()).thenReturn(txtPassword);

        // Crear el controlador
        exitCalled = new AtomicBoolean(false);
        controlador = new ControladorLogin(vista) {
            @Override
            protected void exit(int status) {
                exitCalled.set(true);
            }
        };

        // Inyectar el mock del DAO usando reflection
        inyectarEmpleadoDAO(controlador, empleadoDAO);
    }

    private void inyectarEmpleadoDAO(ControladorLogin controlador, EmpleadoDAO empleadoDAO) 
            throws Exception {
        Field empleadoDAOField = ControladorLogin.class.getDeclaredField("empleadoDAO");
        empleadoDAOField.setAccessible(true);
        empleadoDAOField.set(controlador, empleadoDAO);
    }

    @Test
    void testConstructor_configuraEventosYHaceVisible() {
        // Verificar que se configuran los eventos
        verify(vista).getBtnLogin();
        verify(vista).getBtnSalir();
        verify(btnLogin).addActionListener(any());
        verify(btnSalir).addActionListener(any());

        // Verificar que la vista se hace visible
        verify(vista).setVisible(true);
    }

    @Test
    void testVerificarCredenciales_camposVacios_muestraError() {
        // Configurar mocks para campos vacíos
        when(txtUsuario.getText()).thenReturn("");
        when(txtPassword.getPassword()).thenReturn(new char[0]);

        // Ejecutar
        controlador.verificarCredenciales();

        // Verificar
        verify(vista).mostrarError("Por favor, complete ambos campos");
        verify(vista, never()).dispose();
        verify(empleadoDAO, never()).autenticar(anyString(), anyString());
    }

    @Test
    void testVerificarCredenciales_autenticacionExitosa_abreSistemaPrincipal() {
        // Configurar mocks para autenticación exitosa
        when(txtUsuario.getText()).thenReturn("admin");
        when(txtPassword.getPassword()).thenReturn("password123".toCharArray());
        when(empleadoDAO.autenticar("admin", "password123")).thenReturn(empleado);
        when(empleado.getUsuario()).thenReturn("admin");
        when(empleado.getRol()).thenReturn("ADMIN");

        // Mock estático de SwingUtilities
        try (MockedStatic<SwingUtilities> mockedSwing = mockStatic(SwingUtilities.class)) {
            // Ejecutar
            controlador.verificarCredenciales();

            // Verificar que se autentica correctamente
            verify(empleadoDAO).autenticar("admin", "password123");
            verify(vista, never()).mostrarError(anyString());
            verify(vista).dispose();
            
            // Verificar que se llama a SwingUtilities.invokeLater
            mockedSwing.verify(() -> SwingUtilities.invokeLater(any(Runnable.class)));
            
            // Verificar que se guardó el empleado logueado
            assertEquals(empleado, controlador.getEmpleadoLogueado());
            assertEquals("admin", controlador.getUsuarioLogueado());
            assertEquals("ADMIN", controlador.getRolUsuarioLogueado());
        }
    }

    @Test
    void testVerificarCredenciales_autenticacionFallida_muestraError() {
        // Configurar mocks para autenticación fallida
        when(txtUsuario.getText()).thenReturn("usuario");
        when(txtPassword.getPassword()).thenReturn("password-incorrecto".toCharArray());
        when(empleadoDAO.autenticar("usuario", "password-incorrecto")).thenReturn(null);

        // Ejecutar
        controlador.verificarCredenciales();

        // Verificar
        verify(empleadoDAO).autenticar("usuario", "password-incorrecto");
        verify(vista).mostrarError("Usuario o contraseña incorrectos");
        verify(vista).limpiarFormulario();
        verify(vista, never()).dispose();
        
        // Verificar que no hay empleado logueado
        assertNull(controlador.getEmpleadoLogueado());
        assertNull(controlador.getUsuarioLogueado());
        assertNull(controlador.getRolUsuarioLogueado());
    }

    @Test
    void testVerificarCredenciales_excepcionEnAutenticacion_muestraError() {
        // Configurar mocks para excepción en autenticación
        when(txtUsuario.getText()).thenReturn("admin");
        when(txtPassword.getPassword()).thenReturn("password123".toCharArray());
        when(empleadoDAO.autenticar("admin", "password123"))
            .thenThrow(new RuntimeException("Error de conexión a BD"));

        // Ejecutar
        controlador.verificarCredenciales();

        // Verificar
        verify(empleadoDAO).autenticar("admin", "password123");
        verify(vista).mostrarError("Error de conexión: Error de conexión a BD");
        verify(vista, never()).limpiarFormulario();
        verify(vista, never()).dispose();
    }





    @Test
    void testSalirSistema_confirmacionTrue_cierraSistema() {
        // Configurar mock para confirmación de salida
        when(vista.confirmarSalida()).thenReturn(true);

        // Ejecutar
        controlador.salirSistema();

        // Verificar
        verify(vista).confirmarSalida();
        assertTrue(exitCalled.get());
    }

    @Test
    void testSalirSistema_confirmacionFalse_noCierraSistema() {
        // Configurar mock para confirmación de salida negada
        when(vista.confirmarSalida()).thenReturn(false);

        // Ejecutar
        controlador.salirSistema();

        // Verificar
        verify(vista).confirmarSalida();
        assertFalse(exitCalled.get());
    }

    @Test
    void testGetEmpleadoLogueado_autenticacionExitosa_retornaEmpleado() {
        // Configurar mocks para autenticación exitosa
        when(txtUsuario.getText()).thenReturn("empleado");
        when(txtPassword.getPassword()).thenReturn("password456".toCharArray());
        when(empleadoDAO.autenticar("empleado", "password456")).thenReturn(empleado);
        when(empleado.getUsuario()).thenReturn("empleado");
        when(empleado.getRol()).thenReturn("EMPLEADO");

        try (MockedStatic<SwingUtilities> mockedSwing = mockStatic(SwingUtilities.class)) {
            // Ejecutar
            controlador.verificarCredenciales();

            // Verificar que se guardó el empleado correctamente
            assertEquals(empleado, controlador.getEmpleadoLogueado());
            assertEquals("empleado", controlador.getUsuarioLogueado());
            assertEquals("EMPLEADO", controlador.getRolUsuarioLogueado());
        }
    }

    @Test
    void testGetEmpleadoLogueado_sinLogin_retornaNull() {
        // Verificar que inicialmente es null
        assertNull(controlador.getEmpleadoLogueado());
        assertNull(controlador.getUsuarioLogueado());
        assertNull(controlador.getRolUsuarioLogueado());
    }



    @Test
    void testCrearEmpleadoDAO_retornaNuevaInstancia() {
        // Probar el método factory por defecto
        ControladorLogin controladorConFactory = new ControladorLogin(vista);
        
        // No podemos verificar fácilmente la instancia concreta, pero al menos
        // verificamos que no lance excepción
        assertDoesNotThrow(() -> {
            EmpleadoDAO dao = controladorConFactory.crearEmpleadoDAO();
            assertNotNull(dao);
        });
    }
}