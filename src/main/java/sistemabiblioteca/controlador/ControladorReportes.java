package sistemabiblioteca.controlador;

import sistemabiblioteca.dao.ReporteDAO;
import sistemabiblioteca.dao.PrestamoDAO;
import sistemabiblioteca.vista.PanelReportes;
import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;
import java.util.List;
import java.util.Map;

public class ControladorReportes {
    private PanelReportes vista;
    private ReporteDAO reporteDAO;
    private PrestamoDAO prestamoDAO; // NUEVO: Para actualizar préstamos atrasados
    
    public ControladorReportes(PanelReportes vista) {
        this(vista, new ReporteDAO(), new PrestamoDAO());
    }
    
    ControladorReportes(PanelReportes vista, ReporteDAO reporteDAO) {
        this(vista, reporteDAO, new PrestamoDAO());
    }
    
    // NUEVO: Constructor con PrestamoDAO
    ControladorReportes(PanelReportes vista, ReporteDAO reporteDAO, PrestamoDAO prestamoDAO) {
        this.vista = vista;
        this.reporteDAO = reporteDAO;
        this.prestamoDAO = prestamoDAO; // NUEVO
        configurarEventos();
        cargarTodosLosReportes();
    }
    
    private void configurarEventos() {
        vista.getBtnActualizar().addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                actualizarReportes();
            }
        });
    }
    
    private void actualizarReportes() {
        try {
            vista.iniciarActualizacion();
            
            new Thread(() -> {
                try {
                    // NUEVO: Actualizar préstamos atrasados antes de generar reportes
                    actualizarPrestamosAtrasados();
                    
                    // Actualizar todos los reportes
                    cargarResumenGeneral();
                    cargarLibrosMasPrestados();
                    cargarUsuariosMasActivos();
                    cargarPrestamosPorMes();
                    cargarSituacionActual();
                    
                } catch (Exception ex) {
                    vista.mostrarError("Error al actualizar reportes: " + ex.getMessage());
                } finally {
                    vista.finalizarActualizacion();
                }
            }).start();
            
        } catch (Exception ex) {
            vista.mostrarError("Error al iniciar actualización: " + ex.getMessage());
            vista.finalizarActualizacion();
        }
    }
    
    // NUEVO MÉTODO: Actualizar préstamos atrasados
    private void actualizarPrestamosAtrasados() {
        try {
            prestamoDAO.actualizarPrestamosAtrasados();
            System.out.println("Préstamos atrasados actualizados correctamente");
        } catch (Exception e) {
            System.err.println("Error actualizando préstamos atrasados: " + e.getMessage());
        }
    }
    
    public void cargarTodosLosReportes() {
        // NUEVO: Actualizar préstamos atrasados antes de cargar
        actualizarPrestamosAtrasados();
        
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
            
            // DEBUG: Mostrar valores en consola
            System.out.println("DEBUG Reporte - Activos: " + prestamosActivos + 
                             ", Atrasados: " + prestamosAtrasados);
            
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