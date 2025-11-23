package sistemabiblioteca.controlador;

import sistemabiblioteca.dao.LibroDAO;
import sistemabiblioteca.dao.PrestamoDAO;
import sistemabiblioteca.dao.UsuarioDAO;
import sistemabiblioteca.modelo.Empleado;
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
    private Empleado empleadoLogueado;
    private String ordenActivos = "id";
    private String ordenHistorial = "fecha_prestamo DESC";
    
    // Constructor original modificado
    public ControladorPrestamos(PanelPrestamos vista, Empleado empleadoLogueado) {
        this(vista, new PrestamoDAO(), new LibroDAO(), new UsuarioDAO(), empleadoLogueado);
    }
    
    // Constructor para testing modificado
    ControladorPrestamos(PanelPrestamos vista, PrestamoDAO prestamoDAO, LibroDAO libroDAO, 
            UsuarioDAO usuarioDAO, Empleado empleadoLogueado) {
        this.vista = vista;
        this.prestamoDAO = prestamoDAO;  
        this.libroDAO = libroDAO;
        this.usuarioDAO = usuarioDAO;
        this.empleadoLogueado = empleadoLogueado;
        
        // Configurar listeners para los menús de ordenar
        vista.setOrdenarActivosListener(e -> ordenarPrestamosActivos(e.getActionCommand()));
        vista.setOrdenarHistorialListener(e -> ordenarHistorial(e.getActionCommand()));
        
        configurarEventos();
        cargarPrestamosActivos();
        cargarHistorialPrestamos();
    }
    
    // Mantener constructor antiguo para compatibilidad
    public ControladorPrestamos(PanelPrestamos vista) {
        this(vista, new PrestamoDAO(), new LibroDAO(), new UsuarioDAO(), null);
    }
    
    void configurarEventos() {
        vista.agregarRealizarPrestamoListener(e -> realizarPrestamo());
        vista.agregarRegistrarDevolucionListener(e -> registrarDevolucion());
        vista.agregarActualizarPrestamosListener(e -> actualizarListas());
        vista.agregarLimpiarFormularioListener(e -> vista.limpiarFormulario());
    }
    
    void ordenarPrestamosActivos(String criterio) {
        this.ordenActivos = criterio;
        cargarPrestamosActivos();
        
        // Mostrar mensaje de confirmación
        String mensaje = "";
        switch (criterio) {
            case "id":
                mensaje = "Préstamos activos ordenados por ID";
                break;
            case "libro_id":
                mensaje = "Préstamos activos ordenados por ID Libro";
                break;
            case "usuario_id":
                mensaje = "Préstamos activos ordenados por ID Usuario";
                break;
            case "empleado_id":
                mensaje = "Préstamos activos ordenados por ID Empleado";
                break;
            case "fecha_prestamo DESC":
                mensaje = "Préstamos activos ordenados por Fecha Préstamo (más recientes primero)";
                break;
            case "fecha_devolucion":
                mensaje = "Préstamos activos ordenados por Fecha Devolución";
                break;
            default:
                mensaje = "Orden aplicado a préstamos activos";
        }
        vista.mostrarMensaje(mensaje, JOptionPane.INFORMATION_MESSAGE);
    }
    
    void ordenarHistorial(String criterio) {
        this.ordenHistorial = criterio;
        cargarHistorialPrestamos();
        
        // Mostrar mensaje de confirmación
        String mensaje = "";
        switch (criterio) {
            case "id":
                mensaje = "Historial ordenado por ID";
                break;
            case "libro_id":
                mensaje = "Historial ordenado por ID Libro";
                break;
            case "usuario_id":
                mensaje = "Historial ordenado por ID Usuario";
                break;
            case "empleado_id":
                mensaje = "Historial ordenado por ID Empleado";
                break;
            case "fecha_prestamo DESC":
                mensaje = "Historial ordenado por Fecha Préstamo (más recientes primero)";
                break;
            case "fecha_devolucion":
                mensaje = "Historial ordenado por Fecha Devolución";
                break;
            case "fecha_devolucion_real DESC":
                mensaje = "Historial ordenado por Fecha Devolución Real (más recientes primero)";
                break;
            default:
                mensaje = "Orden aplicado al historial";
        }
        vista.mostrarMensaje(mensaje, JOptionPane.INFORMATION_MESSAGE);
    }
    
    void realizarPrestamo() {
        try {
            if (vista.getLibroId().isEmpty() || vista.getUsuarioId().isEmpty()) {
                vista.mostrarMensaje("ID Libro e ID Usuario son obligatorios", JOptionPane.ERROR_MESSAGE);
                return;
            }
            
            // Verificar que hay un empleado logueado
            if (empleadoLogueado == null) {
                vista.mostrarMensaje("Error: No hay empleado logueado", JOptionPane.ERROR_MESSAGE);
                return;
            }
            
            int libroId = Integer.parseInt(vista.getLibroId());
            int usuarioId = Integer.parseInt(vista.getUsuarioId());
            int diasPrestamo = Integer.parseInt(vista.getDiasPrestamo());
            int empleadoId = empleadoLogueado.getId();
            
            var libro = libroDAO.obtenerLibroPorId(libroId);
            if (libro == null) {
                vista.mostrarMensaje("El libro con ID " + libroId + " no existe", JOptionPane.ERROR_MESSAGE);
                return;
            }
            
            if (libro.getDisponibles() <= 0) {
                vista.mostrarMensaje("No hay ejemplares disponibles de este libro", JOptionPane.ERROR_MESSAGE);
                return;
            }
            
            var usuario = usuarioDAO.obtenerUsuarioPorId(usuarioId);
            if (usuario == null) {
                vista.mostrarMensaje("El usuario con ID " + usuarioId + " no existe", JOptionPane.ERROR_MESSAGE);
                return;
            }
            
            LocalDate fechaPrestamo = LocalDate.now();
            LocalDate fechaDevolucion = fechaPrestamo.plusDays(diasPrestamo);
            
            Prestamo prestamo = new Prestamo(libroId, usuarioId, empleadoId, fechaPrestamo, fechaDevolucion);
            
            if (prestamoDAO.realizarPrestamo(prestamo)) {
                libro.setDisponibles(libro.getDisponibles() - 1);
                libroDAO.actualizarLibro(libro);
                
                String mensajeExito = "Préstamo realizado exitosamente\n" +
                        "Libro: " + libro.getTitulo() + "\n" +
                        "Usuario: " + usuario.getNombre() + "\n" +
                        "Empleado: " + empleadoLogueado.getNombre() + "\n" +
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
            // Verificar el estado actual antes de registrar la devolución
            prestamoDAO.verificarEstadoPrestamo(prestamoId);
            
            if (prestamoDAO.registrarDevolucion(prestamoId)) {
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
        // Usar el criterio de orden actual
        List<Prestamo> prestamos = prestamoDAO.obtenerPrestamosActivos(ordenActivos);
        Object[][] datos = new Object[prestamos.size()][7];
        
        for (int i = 0; i < prestamos.size(); i++) {
            Prestamo prestamo = prestamos.get(i);
            datos[i][0] = prestamo.getId();
            datos[i][1] = prestamo.getLibroId();
            datos[i][2] = prestamo.getUsuarioId();
            datos[i][3] = prestamo.getEmpleadoId();
            datos[i][4] = prestamo.getFechaPrestamo();
            datos[i][5] = prestamo.getFechaDevolucion();
            
            // Resaltar préstamos atrasados
            String estado = prestamo.getEstado();
            if ("ATRASADO".equals(estado)) {
                datos[i][6] = "ATRASADO ⚠";
            } else {
                datos[i][6] = estado;
            }
        }
        
        vista.actualizarTablaPrestamosActivos(datos);
    }
    
    void cargarHistorialPrestamos() {
        // Usar el criterio de orden actual
        List<Prestamo> prestamos = prestamoDAO.obtenerTodosLosPrestamos(ordenHistorial);
        Object[][] datos = new Object[prestamos.size()][8];
        
        for (int i = 0; i < prestamos.size(); i++) {
            Prestamo prestamo = prestamos.get(i);
            datos[i][0] = prestamo.getId();
            datos[i][1] = prestamo.getLibroId();
            datos[i][2] = prestamo.getUsuarioId();
            datos[i][3] = prestamo.getEmpleadoId();
            datos[i][4] = prestamo.getFechaPrestamo();
            datos[i][5] = prestamo.getFechaDevolucion();
            datos[i][6] = prestamo.getFechaDevolucionReal();
            datos[i][7] = prestamo.getEstado();
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
    
    // Getters para testing
    public Empleado getEmpleadoLogueado() {
        return empleadoLogueado;
    }
    
    public String getOrdenActivos() {
        return ordenActivos;
    }
    
    public String getOrdenHistorial() {
        return ordenHistorial;
    }
}