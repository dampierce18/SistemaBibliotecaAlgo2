package sistemabiblioteca.controlador;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import sistemabiblioteca.dao.ReporteDAO;
import sistemabiblioteca.vista.PanelReportes;

import java.util.List;
import java.util.Map;

import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ControladorReportesTest {

    @Mock
    PanelReportes vista;

    @Mock
    ReporteDAO reporteDAO;

    private ControladorReportes controlador;

    @BeforeEach
    void configurar() {
        controlador = new ControladorReportes(vista, reporteDAO);
    }

    @Test
    void testConstructor_cargaTodosLosReportes() {
        // Verificar que se cargan todos los reportes en el constructor
        verify(reporteDAO).obtenerResumenMes();
        verify(reporteDAO).obtenerLibrosMasPrestados();
        verify(reporteDAO).obtenerUsuariosMasActivos();
        verify(reporteDAO).obtenerPrestamosPorMes();
        verify(reporteDAO).obtenerSituacionActual();
    }

    @Test
    void testCargarTodosLosReportes_llamaTodosLosMetodos() {
        clearInvocations(vista, reporteDAO);
        
        controlador.cargarTodosLosReportes();

        verify(reporteDAO).obtenerResumenMes();
        verify(reporteDAO).obtenerLibrosMasPrestados();
        verify(reporteDAO).obtenerUsuariosMasActivos();
        verify(reporteDAO).obtenerPrestamosPorMes();
        verify(reporteDAO).obtenerSituacionActual();
    }

    @Test
    void testCargarResumenGeneral_exitoso() {
        clearInvocations(vista, reporteDAO);
        
        Map<String, Integer> resumenMock = Map.of(
            "prestamos_mes", 10,
            "prestamos_activos", 5,
            "prestamos_atrasados", 2,
            "usuarios_sancionados", 1,
            "multas_pendientes", 50
        );
        
        when(reporteDAO.obtenerResumenMes()).thenReturn(resumenMock);

        controlador.cargarResumenGeneral();

        verify(vista).mostrarResumenGeneral(10, 5, 2, 1, 50);
        verify(vista, never()).mostrarError(anyString());
    }

    @Test
    void testCargarResumenGeneral_conValoresFaltantes() {
        clearInvocations(vista, reporteDAO);
        
        Map<String, Integer> resumenMock = Map.of(
            "prestamos_mes", 8
            // Los demás valores faltan
        );
        
        when(reporteDAO.obtenerResumenMes()).thenReturn(resumenMock);

        controlador.cargarResumenGeneral();

        verify(vista).mostrarResumenGeneral(8, 0, 0, 0, 0);
    }

    @Test
    void testCargarResumenGeneral_excepcion_muestraError() {
        clearInvocations(vista, reporteDAO);
        
        when(reporteDAO.obtenerResumenMes()).thenThrow(new RuntimeException("Error de BD"));

        controlador.cargarResumenGeneral();

        verify(vista).mostrarError("Error cargando resumen general: Error de BD");
        verify(vista).mostrarResumenGeneral(0, 0, 0, 0, 0);
    }

    @Test
    void testCargarLibrosMasPrestados_exitoso() {
        clearInvocations(vista, reporteDAO);
        
        List<Object[]> librosMock = List.of(
            new Object[]{"Libro 1", 15},
            new Object[]{"Libro 2", 10}
        );
        
        when(reporteDAO.obtenerLibrosMasPrestados()).thenReturn(librosMock);

        controlador.cargarLibrosMasPrestados();

        verify(vista).mostrarLibrosPrestados(librosMock);
        verify(vista, never()).mostrarError(anyString());
    }

    @Test
    void testCargarLibrosMasPrestados_excepcion_muestraError() {
        clearInvocations(vista, reporteDAO);
        
        when(reporteDAO.obtenerLibrosMasPrestados()).thenThrow(new RuntimeException("Error de BD"));

        controlador.cargarLibrosMasPrestados();

        verify(vista).mostrarError("Error cargando libros más prestados: Error de BD");
        verify(vista).mostrarLibrosPrestados(List.of());
    }

    @Test
    void testCargarUsuariosMasActivos_exitoso() {
        clearInvocations(vista, reporteDAO);
        
        List<Object[]> usuariosMock = List.of(
            new Object[]{"Usuario 1", 8},
            new Object[]{"Usuario 2", 5}
        );
        
        when(reporteDAO.obtenerUsuariosMasActivos()).thenReturn(usuariosMock);

        controlador.cargarUsuariosMasActivos();

        verify(vista).mostrarUsuariosActivos(usuariosMock);
        verify(vista, never()).mostrarError(anyString());
    }

    @Test
    void testCargarUsuariosMasActivos_excepcion_muestraError() {
        clearInvocations(vista, reporteDAO);
        
        when(reporteDAO.obtenerUsuariosMasActivos()).thenThrow(new RuntimeException("Error de BD"));

        controlador.cargarUsuariosMasActivos();

        verify(vista).mostrarError("Error cargando usuarios más activos: Error de BD");
        verify(vista).mostrarUsuariosActivos(List.of());
    }

    @Test
    void testCargarPrestamosPorMes_exitoso() {
        clearInvocations(vista, reporteDAO);
        
        List<Object[]> prestamosMock = List.of(
            new Object[]{"Enero", 20},
            new Object[]{"Febrero", 15}
        );
        
        when(reporteDAO.obtenerPrestamosPorMes()).thenReturn(prestamosMock);

        controlador.cargarPrestamosPorMes();

        verify(vista).mostrarPrestamosMes(prestamosMock);
        verify(vista, never()).mostrarError(anyString());
    }

    @Test
    void testCargarPrestamosPorMes_excepcion_muestraError() {
        clearInvocations(vista, reporteDAO);
        
        when(reporteDAO.obtenerPrestamosPorMes()).thenThrow(new RuntimeException("Error de BD"));

        controlador.cargarPrestamosPorMes();

        verify(vista).mostrarError("Error cargando préstamos por mes: Error de BD");
        verify(vista).mostrarPrestamosMes(List.of());
    }

    @Test
    void testCargarSituacionActual_exitoso() {
        clearInvocations(vista, reporteDAO);
        
        List<Object[]> situacionMock = List.of(
            new Object[]{"Situación 1", "Valor 1"},
            new Object[]{"Situación 2", "Valor 2"}
        );
        
        when(reporteDAO.obtenerSituacionActual()).thenReturn(situacionMock);

        controlador.cargarSituacionActual();

        verify(vista).mostrarSituacionActual(situacionMock);
        verify(vista, never()).mostrarError(anyString());
    }

    @Test
    void testCargarSituacionActual_excepcion_muestraError() {
        clearInvocations(vista, reporteDAO);
        
        when(reporteDAO.obtenerSituacionActual()).thenThrow(new RuntimeException("Error de BD"));

        controlador.cargarSituacionActual();

        verify(vista).mostrarError("Error cargando situación actual: Error de BD");
        verify(vista).mostrarSituacionActual(List.of());
    }

    @Test
    void testRefrescarReportes_llamaCargarTodosLosReportes() {
        clearInvocations(vista, reporteDAO);
        
        controlador.refrescarReportes();

        verify(reporteDAO).obtenerResumenMes();
        verify(reporteDAO).obtenerLibrosMasPrestados();
        verify(reporteDAO).obtenerUsuariosMasActivos();
        verify(reporteDAO).obtenerPrestamosPorMes();
        verify(reporteDAO).obtenerSituacionActual();
    }

    @Test
    void testCargarResumenGeneral_mapaVacio_usaValoresPorDefecto() {
        clearInvocations(vista, reporteDAO);
        
        when(reporteDAO.obtenerResumenMes()).thenReturn(Map.of());

        controlador.cargarResumenGeneral();

        verify(vista).mostrarResumenGeneral(0, 0, 0, 0, 0);
    }

    @Test
    void testCargarLibrosMasPrestados_listaVacia_muestraListaVacia() {
        clearInvocations(vista, reporteDAO);
        
        when(reporteDAO.obtenerLibrosMasPrestados()).thenReturn(List.of());

        controlador.cargarLibrosMasPrestados();

        verify(vista).mostrarLibrosPrestados(List.of());
        verify(vista, never()).mostrarError(anyString());
    }
}