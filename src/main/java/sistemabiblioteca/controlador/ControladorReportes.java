package sistemabiblioteca.controlador;

import sistemabiblioteca.dao.ReporteDAO;
import sistemabiblioteca.vista.PanelReportes;
import javax.swing.*;
import java.util.List;
import java.util.Map;

public class ControladorReportes {
    private PanelReportes vista;
    private ReporteDAO reporteDAO;
    
    public ControladorReportes(PanelReportes vista) {
        this.vista = vista;
        this.reporteDAO = new ReporteDAO();
        cargarTodosLosReportes();
    }
    
    public void cargarTodosLosReportes() {
        cargarResumenGeneral();
        cargarLibrosMasPrestados();
        cargarUsuariosMasActivos();
        cargarPrestamosPorMes();
        cargarSituacionActual();
    }
    
    private void cargarResumenGeneral() {
        try {
            Map<String, Integer> resumen = reporteDAO.obtenerResumenMes();
            
            int totalPrestamosMes = resumen.getOrDefault("prestamos_mes", 0);
            int prestamosActivos = resumen.getOrDefault("prestamos_activos", 0);
            int prestamosAtrasados = resumen.getOrDefault("prestamos_atrasados", 0);
            int usuariosSancionados = resumen.getOrDefault("usuarios_sancionados", 0);
            int multasPendientes = resumen.getOrDefault("multas_pendientes", 0);
            
            vista.actualizarResumenGeneral(totalPrestamosMes, prestamosActivos, 
                                         prestamosAtrasados, usuariosSancionados, 
                                         multasPendientes);
            
        } catch (Exception e) {
            System.err.println("Error cargando resumen general: " + e.getMessage());
            vista.actualizarResumenGeneral(0, 0, 0, 0, 0);
        }
    }
    
    private void cargarLibrosMasPrestados() {
        try {
            List<Object[]> libros = reporteDAO.obtenerLibrosMasPrestados();
            Object[][] datos = new Object[libros.size()][5];
            
            for (int i = 0; i < libros.size(); i++) {
                datos[i] = libros.get(i);
            }
            
            vista.actualizarLibrosPrestados(datos);
            
        } catch (Exception e) {
            System.err.println("Error cargando libros más prestados: " + e.getMessage());
            vista.actualizarLibrosPrestados(new Object[0][0]);
        }
    }
    
    private void cargarUsuariosMasActivos() {
        try {
            List<Object[]> usuarios = reporteDAO.obtenerUsuariosMasActivos();
            Object[][] datos = new Object[usuarios.size()][5];
            
            for (int i = 0; i < usuarios.size(); i++) {
                datos[i] = usuarios.get(i);
            }
            
            vista.actualizarUsuariosActivos(datos);
            
        } catch (Exception e) {
            System.err.println("Error cargando usuarios más activos: " + e.getMessage());
            // Mostrar mensaje de "Funcionalidad en desarrollo"
            Object[][] datosEnDesarrollo = {
                {1, "Funcionalidad en desarrollo", "-", "-", "-"}
            };
            vista.actualizarUsuariosActivos(datosEnDesarrollo);
        }
    }
    
    private void cargarPrestamosPorMes() {
        try {
            List<Object[]> prestamos = reporteDAO.obtenerPrestamosPorMes();
            Object[][] datos = new Object[prestamos.size()][5];
            
            for (int i = 0; i < prestamos.size(); i++) {
                datos[i] = prestamos.get(i);
            }
            
            vista.actualizarPrestamosMes(datos);
            
        } catch (Exception e) {
            System.err.println("Error cargando préstamos por mes: " + e.getMessage());
            // Mostrar mensaje de "Funcionalidad en desarrollo"
            Object[][] datosEnDesarrollo = {
                {"Funcionalidad en desarrollo", "-", "-", "-", "-"}
            };
            vista.actualizarPrestamosMes(datosEnDesarrollo);
        }
    }
    
    private void cargarSituacionActual() {
        try {
            List<Object[]> situacion = reporteDAO.obtenerSituacionActual();
            Object[][] datos = new Object[situacion.size()][5];
            
            for (int i = 0; i < situacion.size(); i++) {
                datos[i] = situacion.get(i);
            }
            
            vista.actualizarSituacionActual(datos);
            
        } catch (Exception e) {
            System.err.println("Error cargando situación actual: " + e.getMessage());
            // Mostrar mensaje de "Funcionalidad en desarrollo"
            Object[][] datosEnDesarrollo = {
                {"Info", "Funcionalidad en desarrollo", "-", "-", "-"}
            };
            vista.actualizarSituacionActual(datosEnDesarrollo);
        }
    }
    
    // Método para refrescar todos los reportes
    public void refrescarReportes() {
        cargarTodosLosReportes();
        JOptionPane.showMessageDialog(vista, "Reportes actualizados", "Éxito", JOptionPane.INFORMATION_MESSAGE);
    }
}