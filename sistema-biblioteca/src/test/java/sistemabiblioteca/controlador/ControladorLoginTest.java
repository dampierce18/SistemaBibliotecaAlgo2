package sistemabiblioteca.controlador;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.MockedStatic;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import sistemabiblioteca.vista.LoginFrame;

import javax.swing.*;
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

    private ControladorLogin controlador;
    private AtomicBoolean exitCalled;

    @BeforeEach
    void configurar() {
        // Configurar los mocks de los componentes de la vista
        lenient().when(vista.getBtnLogin()).thenReturn(btnLogin);
        lenient().when(vista.getBtnSalir()).thenReturn(btnSalir);
        lenient().when(vista.getTxtUsuario()).thenReturn(txtUsuario);
        lenient().when(vista.getTxtPassword()).thenReturn(txtPassword);

        // Crear el controlador con el constructor de testing y sobreescribir exit
        exitCalled = new AtomicBoolean(false);
        controlador = new ControladorLogin(vista) {
            @Override
            protected void exit(int status) {
                exitCalled.set(true);
            }
        };
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
    }

    @Test
    void testVerificarCredenciales_usuarioVacio_muestraError() {
        // Configurar mocks - usuario vacío, contraseña con valor
        when(txtUsuario.getText()).thenReturn("");
        when(txtPassword.getPassword()).thenReturn("123".toCharArray());

        // Ejecutar
        controlador.verificarCredenciales();

        // Verificar
        verify(vista).mostrarError("Por favor, complete ambos campos");
        verify(vista, never()).dispose();
    }

    @Test
    void testVerificarCredenciales_passwordVacio_muestraError() {
        // Configurar mocks - usuario con valor, contraseña vacía
        when(txtUsuario.getText()).thenReturn("admin");
        when(txtPassword.getPassword()).thenReturn(new char[0]);

        // Ejecutar
        controlador.verificarCredenciales();

        // Verificar
        verify(vista).mostrarError("Por favor, complete ambos campos");
        verify(vista, never()).dispose();
    }

    @Test
    void testVerificarCredenciales_adminCorrecto_abreSistemaPrincipal() {
        // Configurar mocks para credenciales de admin correctas
        when(txtUsuario.getText()).thenReturn("admin");
        when(txtPassword.getPassword()).thenReturn("123".toCharArray());

        // Mock estático de SwingUtilities
        try (MockedStatic<SwingUtilities> mockedSwing = mockStatic(SwingUtilities.class)) {
            // Ejecutar
            controlador.verificarCredenciales();

            // Verificar que se limpia el formulario y abre sistema principal
            verify(vista, never()).mostrarError(anyString());
            verify(vista).dispose();
            
            // Verificar que se llama a SwingUtilities.invokeLater
            mockedSwing.verify(() -> SwingUtilities.invokeLater(any(Runnable.class)));
        }
    }

    @Test
    void testVerificarCredenciales_empleadoCorrecto_abreSistemaPrincipal() {
        // Configurar mocks para credenciales de empleado correctas
        when(txtUsuario.getText()).thenReturn("empleado");
        when(txtPassword.getPassword()).thenReturn("123".toCharArray());

        // Mock estático de SwingUtilities
        try (MockedStatic<SwingUtilities> mockedSwing = mockStatic(SwingUtilities.class)) {
            // Ejecutar
            controlador.verificarCredenciales();

            // Verificar que se limpia el formulario y abre sistema principal
            verify(vista, never()).mostrarError(anyString());
            verify(vista).dispose();
            
            // Verificar que se llama a SwingUtilities.invokeLater
            mockedSwing.verify(() -> SwingUtilities.invokeLater(any(Runnable.class)));
        }
    }

    @Test
    void testVerificarCredenciales_adminUsuarioIncorrecto_muestraError() {
        // Configurar mocks - usuario admin incorrecto
        when(txtUsuario.getText()).thenReturn("admin");
        when(txtPassword.getPassword()).thenReturn("password-incorrecto".toCharArray());

        // Ejecutar
        controlador.verificarCredenciales();

        // Verificar
        verify(vista).mostrarError("Usuario o contraseña incorrectos");
        verify(vista).limpiarFormulario();
        verify(vista, never()).dispose();
    }

    @Test
    void testVerificarCredenciales_empleadoUsuarioIncorrecto_muestraError() {
        // Configurar mocks - usuario empleado incorrecto
        when(txtUsuario.getText()).thenReturn("empleado");
        when(txtPassword.getPassword()).thenReturn("password-incorrecto".toCharArray());

        // Ejecutar
        controlador.verificarCredenciales();

        // Verificar
        verify(vista).mostrarError("Usuario o contraseña incorrectos");
        verify(vista).limpiarFormulario();
        verify(vista, never()).dispose();
    }

    @Test
    void testVerificarCredenciales_usuarioInexistente_muestraError() {
        // Configurar mocks - usuario que no existe
        when(txtUsuario.getText()).thenReturn("usuario-inexistente");
        when(txtPassword.getPassword()).thenReturn("123".toCharArray());

        // Ejecutar
        controlador.verificarCredenciales();

        // Verificar
        verify(vista).mostrarError("Usuario o contraseña incorrectos");
        verify(vista).limpiarFormulario();
        verify(vista, never()).dispose();
    }


    @Test
    void testVerificarCredenciales_trimUsuario_eliminaEspacios() {
        // Configurar mocks - usuario con espacios
        when(txtUsuario.getText()).thenReturn("  admin  ");
        when(txtPassword.getPassword()).thenReturn("123".toCharArray());

        // Mock estático de SwingUtilities
        try (MockedStatic<SwingUtilities> mockedSwing = mockStatic(SwingUtilities.class)) {
            // Ejecutar
            controlador.verificarCredenciales();

            // Verificar que funciona correctamente a pesar de los espacios
            verify(vista, never()).mostrarError(anyString());
            verify(vista).dispose();
        }
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
    void testCredencialesConstantes_correctas() {
        // Verificar que las constantes de credenciales son correctas
        assertEquals("admin", ControladorLogin.ADMIN_USUARIO);
        assertEquals("123", ControladorLogin.ADMIN_PASSWORD);
        assertEquals("empleado", ControladorLogin.EMPLEADO_USUARIO);
        assertEquals("123", ControladorLogin.EMPLEADO_PASSWORD);
    }

    @Test
    void testVerificarCredenciales_passwordConEspacios_funcionaCorrectamente() {
        // Configurar mocks - contraseña con espacios (debería funcionar igual)
        when(txtUsuario.getText()).thenReturn("admin");
        when(txtPassword.getPassword()).thenReturn(" 123 ".toCharArray());

        // Ejecutar
        controlador.verificarCredenciales();

        // Verificar que falla porque la contraseña con espacios no coincide
        verify(vista).mostrarError("Usuario o contraseña incorrectos");
        verify(vista).limpiarFormulario();
    }

    @Test
    void testVerificarCredenciales_usuarioMayusculas_muestraError() {
        // Configurar mocks - usuario en mayúsculas (debería fallar)
        when(txtUsuario.getText()).thenReturn("ADMIN");
        when(txtPassword.getPassword()).thenReturn("123".toCharArray());

        // Ejecutar
        controlador.verificarCredenciales();

        // Verificar que falla porque es case sensitive
        verify(vista).mostrarError("Usuario o contraseña incorrectos");
        verify(vista).limpiarFormulario();
    }

    @Test
    void testGetUsuarioLogueado_adminCorrecto_retornaUsuario() {
        // Configurar mocks para credenciales de admin correctas
        when(txtUsuario.getText()).thenReturn("admin");
        when(txtPassword.getPassword()).thenReturn("123".toCharArray());

        try (MockedStatic<SwingUtilities> mockedSwing = mockStatic(SwingUtilities.class)) {
            // Ejecutar
            controlador.verificarCredenciales();

            // Verificar que se guardó el usuario correctamente
            assertEquals("admin", controlador.getUsuarioLogueado());
        }
    }

    @Test
    void testGetUsuarioLogueado_empleadoCorrecto_retornaUsuario() {
        // Configurar mocks para credenciales de empleado correctas
        when(txtUsuario.getText()).thenReturn("empleado");
        when(txtPassword.getPassword()).thenReturn("123".toCharArray());

        try (MockedStatic<SwingUtilities> mockedSwing = mockStatic(SwingUtilities.class)) {
            // Ejecutar
            controlador.verificarCredenciales();

            // Verificar que se guardó el usuario correctamente
            assertEquals("empleado", controlador.getUsuarioLogueado());
        }
    }

    @Test
    void testGetUsuarioLogueado_sinLogin_retornaNull() {
        // Verificar que inicialmente es null
        assertNull(controlador.getUsuarioLogueado());
    }
}