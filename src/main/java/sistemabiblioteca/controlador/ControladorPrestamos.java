package sistemabiblioteca.controlador;

import sistemabiblioteca.dao.LibroDAO;
import sistemabiblioteca.dao.PrestamoDAO;
import sistemabiblioteca.dao.UsuarioDAO;
import sistemabiblioteca.modelo.Prestamo;
import sistemabiblioteca.vista.PanelPrestamos;
import javax.swing.*;
import java.time.LocalDate;
import java.util.List;

public class ControladorPrestamos {
    private PanelPrestamos vista;
    private PrestamoDAO prestamoDAO;
    private LibroDAO libroDAO;
    private UsuarioDAO usuarioDAO;
    
    public ControladorPrestamos(PanelPrestamos vista) {
        this.vista = vista;
        this.prestamoDAO = new PrestamoDAO();
        this.libroDAO = new LibroDAO();
        this.usuarioDAO = new UsuarioDAO();
        configurarEventos();
        cargarPrestamosActivos();
        cargarHistorialPrestamos();
    }
    
    private void configurarEventos() {
        vista.agregarRealizarPrestamoListener(e -> realizarPrestamo());
        vista.agregarRegistrarDevolucionListener(e -> registrarDevolucion());
        vista.agregarActualizarPrestamosListener(e -> actualizarListas());
        vista.agregarLimpiarFormularioListener(e -> vista.limpiarFormulario());
    }
    
    private void realizarPrestamo() {
        try {
            // Validaciones
            if (vista.getLibroId().isEmpty() || vista.getUsuarioId().isEmpty()) {
                JOptionPane.showMessageDialog(vista, "ID Libro e ID Usuario son obligatorios", "Error", JOptionPane.ERROR_MESSAGE);
                return;
            }
            
            int libroId = Integer.parseInt(vista.getLibroId());
            int usuarioId = Integer.parseInt(vista.getUsuarioId());
            int diasPrestamo = Integer.parseInt(vista.getDiasPrestamo());
            
            // Verificar que el libro existe y tiene ejemplares disponibles
            var libro = libroDAO.obtenerLibroPorId(libroId);
            if (libro == null) {
                JOptionPane.showMessageDialog(vista, "El libro con ID " + libroId + " no existe", "Error", JOptionPane.ERROR_MESSAGE);
                return;
            }
            
            if (libro.getDisponibles() <= 0) {
                JOptionPane.showMessageDialog(vista, "No hay ejemplares disponibles de este libro", "Error", JOptionPane.ERROR_MESSAGE);
                return;
            }
            
            // Verificar que el usuario existe
            var usuario = usuarioDAO.obtenerUsuarioPorId(usuarioId);
            if (usuario == null) {
                JOptionPane.showMessageDialog(vista, "El usuario con ID " + usuarioId + " no existe", "Error", JOptionPane.ERROR_MESSAGE);
                return;
            }
            
            // Crear préstamo
            LocalDate fechaPrestamo = LocalDate.now();
            LocalDate fechaDevolucion = fechaPrestamo.plusDays(diasPrestamo);
            
            Prestamo prestamo = new Prestamo(libroId, usuarioId, fechaPrestamo, fechaDevolucion);
            
            // Guardar en base de datos
            if (prestamoDAO.realizarPrestamo(prestamo)) {
                // Actualizar disponibilidad del libro
                libro.setDisponibles(libro.getDisponibles() - 1);
                libroDAO.actualizarLibro(libro);
                
                JOptionPane.showMessageDialog(vista, 
                    "Préstamo realizado exitosamente\n" +
                    "Libro: " + libro.getTitulo() + "\n" +
                    "Usuario: " + usuario.getNombre() + "\n" +
                    "Fecha de devolución: " + fechaDevolucion, 
                    "Éxito", JOptionPane.INFORMATION_MESSAGE);
                
                vista.limpiarFormulario();
                actualizarListas();
            } else {
                JOptionPane.showMessageDialog(vista, "Error al realizar el préstamo", "Error", JOptionPane.ERROR_MESSAGE);
            }
            
        } catch (NumberFormatException e) {
            JOptionPane.showMessageDialog(vista, "Los IDs y días deben ser números válidos", "Error", JOptionPane.ERROR_MESSAGE);
        } catch (Exception e) {
            JOptionPane.showMessageDialog(vista, "Error: " + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
    }
    
    private void registrarDevolucion() {
        int filaSeleccionada = vista.getFilaSeleccionadaPrestamosActivos();
        if (filaSeleccionada == -1) {
            JOptionPane.showMessageDialog(vista, "Seleccione un préstamo para registrar devolución", "Advertencia", JOptionPane.WARNING_MESSAGE);
            return;
        }
        
        try {
            int prestamoId = (int) vista.getTablePrestamosActivos().getValueAt(filaSeleccionada, 0);
            int libroId = (int) vista.getTablePrestamosActivos().getValueAt(filaSeleccionada, 1);
            
            // Registrar devolución
            if (prestamoDAO.registrarDevolucion(prestamoId)) {
                // Actualizar disponibilidad del libro
                var libro = libroDAO.obtenerLibroPorId(libroId);
                if (libro != null) {
                    libro.setDisponibles(libro.getDisponibles() + 1);
                    libroDAO.actualizarLibro(libro);
                }
                
                JOptionPane.showMessageDialog(vista, "Devolución registrada exitosamente", "Éxito", JOptionPane.INFORMATION_MESSAGE);
                actualizarListas();
            } else {
                JOptionPane.showMessageDialog(vista, "Error al registrar la devolución", "Error", JOptionPane.ERROR_MESSAGE);
            }
            
        } catch (Exception e) {
            JOptionPane.showMessageDialog(vista, "Error: " + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
    }
    
    private void cargarPrestamosActivos() {
        List<Prestamo> prestamos = prestamoDAO.obtenerPrestamosActivos();
        Object[][] datos = new Object[prestamos.size()][6];
        
        for (int i = 0; i < prestamos.size(); i++) {
            Prestamo prestamo = prestamos.get(i);
            datos[i][0] = prestamo.getId();
            datos[i][1] = prestamo.getLibroId();
            datos[i][2] = prestamo.getUsuarioId();
            datos[i][3] = prestamo.getFechaPrestamo();
            datos[i][4] = prestamo.getFechaDevolucion();
            datos[i][5] = prestamo.getEstado();
        }
        
        vista.actualizarTablaPrestamosActivos(datos);
    }
    
    private void cargarHistorialPrestamos() {
        List<Prestamo> prestamos = prestamoDAO.obtenerTodosLosPrestamos();
        Object[][] datos = new Object[prestamos.size()][7];
        
        for (int i = 0; i < prestamos.size(); i++) {
            Prestamo prestamo = prestamos.get(i);
            datos[i][0] = prestamo.getId();
            datos[i][1] = prestamo.getLibroId();
            datos[i][2] = prestamo.getUsuarioId();
            datos[i][3] = prestamo.getFechaPrestamo();
            datos[i][4] = prestamo.getFechaDevolucion();
            datos[i][5] = prestamo.getFechaDevolucionReal();
            datos[i][6] = prestamo.getEstado();
        }
        
        vista.actualizarTablaHistorial(datos);
    }
    
    private void actualizarListas() {
        cargarPrestamosActivos();
        cargarHistorialPrestamos();
    }
    
    public int obtenerPrestamosActivos() {
        return prestamoDAO.contarPrestamosActivos();
    }
    
    public int obtenerPrestamosAtrasados() {
        return prestamoDAO.contarPrestamosAtrasados();
    }
}