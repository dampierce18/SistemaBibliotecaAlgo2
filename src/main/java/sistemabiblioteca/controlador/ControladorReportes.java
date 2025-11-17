package sistemabiblioteca.controlador;

import sistemabiblioteca.dao.ReporteDAO;
import sistemabiblioteca.vista.PanelReportes;
import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;
import java.util.List;
import java.util.Map;

public class ControladorReportes {
    private PanelReportes vista;
    private ReporteDAO reporteDAO;
    
    public ControladorReportes(PanelReportes vista) {
        this(vista, new ReporteDAO());
    }
    
    ControladorReportes(PanelReportes vista, ReporteDAO reporteDAO) {
        this.vista = vista;
        this.reporteDAO = reporteDAO;
        configurarEventos();
        cargarTodosLosReportes();
    }
    
    // Nuevo método para configurar los eventos
    private void configurarEventos() {
        // Configurar el botón de actualizar
        vista.getBtnActualizar().addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                actualizarReportes();
            }
        });
    }
    
    // Método para actualizar todos los reportes con feedback visual
    private void actualizarReportes() {
        try {
            // Mostrar estado de "cargando" en el botón
            vista.iniciarActualizacion();
            
            // Ejecutar en un hilo separado para no bloquear la interfaz
            new Thread(() -> {
                try {
                    // Actualizar todos los reportes
                    cargarResumenGeneral();
                    cargarLibrosMasPrestados();
                    cargarUsuariosMasActivos();
                    cargarPrestamosPorMes();
                    cargarSituacionActual();
                    
                } catch (Exception ex) {
                    // Mostrar error en el EDT
                        vista.mostrarError("Error al actualizar reportes: " + ex.getMessage());
                } finally {
                    // Restaurar el botón en el EDT
                        vista.finalizarActualizacion();
                }
            }).start();
            
        } catch (Exception ex) {
            vista.mostrarError("Error al iniciar actualización: " + ex.getMessage());
            vista.finalizarActualizacion();
        }
    }
    
    public void cargarTodosLosReportes() {
        cargarResumenGeneral();
        cargarLibrosMasPrestados();
        cargarUsuariosMasActivos();
        cargarPrestamosPorMes();
        cargarSituacionActual();
    }
    
    void cargarResumenGeneral() {
        try {
            Map<String, Integer> resumen = reporteDAO.obtenerResumenMes();
            
            int totalPrestamosMes = resumen.getOrDefault("prestamos_mes", 0);
            int prestamosActivos = resumen.getOrDefault("prestamos_activos", 0);
            int prestamosAtrasados = resumen.getOrDefault("prestamos_atrasados", 0);
            int usuariosSancionados = resumen.getOrDefault("usuarios_sancionados", 0);
            int multasPendientes = resumen.getOrDefault("multas_pendientes", 0);
            
            vista.mostrarResumenGeneral(totalPrestamosMes, prestamosActivos, 
                                      prestamosAtrasados, usuariosSancionados, 
                                      multasPendientes);
            
        } catch (Exception e) {
            vista.mostrarError("Error cargando resumen general: " + e.getMessage());
            vista.mostrarResumenGeneral(0, 0, 0, 0, 0);
        }
    }
    
    void cargarLibrosMasPrestados() {
        try {
            List<Object[]> libros = reporteDAO.obtenerLibrosMasPrestados();
            vista.mostrarLibrosPrestados(libros);
            
        } catch (Exception e) {
            vista.mostrarError("Error cargando libros más prestados: " + e.getMessage());
            vista.mostrarLibrosPrestados(List.of());
        }
    }
    
    void cargarUsuariosMasActivos() {
        try {
            List<Object[]> usuarios = reporteDAO.obtenerUsuariosMasActivos();
            vista.mostrarUsuariosActivos(usuarios);
            
        } catch (Exception e) {
            vista.mostrarError("Error cargando usuarios más activos: " + e.getMessage());
            vista.mostrarUsuariosActivos(List.of());
        }
    }
    
    void cargarPrestamosPorMes() {
        try {
            List<Object[]> prestamos = reporteDAO.obtenerPrestamosPorMes();
            vista.mostrarPrestamosMes(prestamos);
            
        } catch (Exception e) {
            vista.mostrarError("Error cargando préstamos por mes: " + e.getMessage());
            vista.mostrarPrestamosMes(List.of());
        }
    }
    
    void cargarSituacionActual() {
        try {
            List<Object[]> situacion = reporteDAO.obtenerSituacionActual();
            vista.mostrarSituacionActual(situacion);
            
        } catch (Exception e) {
            vista.mostrarError("Error cargando situación actual: " + e.getMessage());
            vista.mostrarSituacionActual(List.of());
        }
    }
    
    public void refrescarReportes() {
        actualizarReportes();
    }
}