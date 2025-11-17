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
        this(vista, new PrestamoDAO(), new LibroDAO(), new UsuarioDAO());
    }
    
    ControladorPrestamos(PanelPrestamos vista, PrestamoDAO prestamoDAO, LibroDAO libroDAO, 
            UsuarioDAO usuarioDAO) {
	this.vista = vista;
	this.prestamoDAO = prestamoDAO;  
	this.libroDAO = libroDAO;
	this.usuarioDAO = usuarioDAO;
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
    
    void realizarPrestamo() {
        try {
            // Validaciones
            if (vista.getLibroId().isEmpty() || vista.getUsuarioId().isEmpty()) {
                vista.mostrarMensaje("ID Libro e ID Usuario son obligatorios", JOptionPane.ERROR_MESSAGE);
                return;
            }
            
            int libroId = Integer.parseInt(vista.getLibroId());
            int usuarioId = Integer.parseInt(vista.getUsuarioId());
            int diasPrestamo = Integer.parseInt(vista.getDiasPrestamo());
            
            // Verificar que el libro existe y tiene ejemplares disponibles
            var libro = libroDAO.obtenerLibroPorId(libroId);
            if (libro == null) {
                vista.mostrarMensaje("El libro con ID " + libroId + " no existe", JOptionPane.ERROR_MESSAGE);
                return;
            }
            
            if (libro.getDisponibles() <= 0) {
                vista.mostrarMensaje("No hay ejemplares disponibles de este libro", JOptionPane.ERROR_MESSAGE);
                return;
            }
            
            // Verificar que el usuario existe
            var usuario = usuarioDAO.obtenerUsuarioPorId(usuarioId);
            if (usuario == null) {
                vista.mostrarMensaje("El usuario con ID " + usuarioId + " no existe", JOptionPane.ERROR_MESSAGE);
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
                
                String mensajeExito = "Préstamo realizado exitosamente\n" +
                        "Libro: " + libro.getTitulo() + "\n" +
                        "Usuario: " + usuario.getNombre() + "\n" +
                        "Fecha de devolución: " + fechaDevolucion;
                    
                vista.mostrarMensaje(mensajeExito, JOptionPane.INFORMATION_MESSAGE);
                
                vista.limpiarFormulario();
                actualizarListas();
            } else {
                vista.mostrarMensaje("Error al realizar el préstamo", JOptionPane.ERROR_MESSAGE);
            }
            
        } catch (NumberFormatException e) {
            vista.mostrarMensaje("Los IDs y días deben ser números válidos", JOptionPane.ERROR_MESSAGE);
        } catch (Exception e) {
        	vista.mostrarMensaje("Error: "+ e.getMessage(), JOptionPane.ERROR_MESSAGE);
        }
    }
    void registrarDevolucion() {
        Integer prestamoId = vista.obtenerPrestamoIdSeleccionado();
        Integer libroId = vista.obtenerLibroIdSeleccionado();
        
        if (prestamoId == null) {
            vista.mostrarMensaje("Seleccione un préstamo para registrar devolución", JOptionPane.WARNING_MESSAGE);
            return;
        }
        
        try {
            // Registrar devolución
            if (prestamoDAO.registrarDevolucion(prestamoId)) {
                // Actualizar disponibilidad del libro
                var libro = libroDAO.obtenerLibroPorId(libroId);
                if (libro != null) {
                    libro.setDisponibles(libro.getDisponibles() + 1);
                    libroDAO.actualizarLibro(libro);
                }
                
                vista.mostrarMensaje("Devolución registrada exitosamente", JOptionPane.INFORMATION_MESSAGE);
                actualizarListas();
            } else {
                vista.mostrarMensaje("Error al registrar la devolución", JOptionPane.ERROR_MESSAGE);
            }
            
        } catch (Exception e) {
            vista.mostrarMensaje("Error: " + e.getMessage(), JOptionPane.ERROR_MESSAGE);
        }
    }
    void cargarPrestamosActivos() {
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
    
    void cargarHistorialPrestamos() {
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
    
    void actualizarListas() {
        cargarPrestamosActivos();
        cargarHistorialPrestamos();
    }
    
    int obtenerPrestamosActivos() {
        return prestamoDAO.contarPrestamosActivos();
    }
    
    int obtenerPrestamosAtrasados() {
        return prestamoDAO.contarPrestamosAtrasados();
    }
}
