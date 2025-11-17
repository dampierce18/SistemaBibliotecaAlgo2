package sistemabiblioteca.controlador;

import sistemabiblioteca.dao.LibroDAO;
import sistemabiblioteca.modelo.Libro;
import sistemabiblioteca.vista.PanelLibros;
import javax.swing.*;
import java.util.List;

public class ControladorLibros {
    private PanelLibros vista;
    private LibroDAO libroDAO;
    
	public ControladorLibros(PanelLibros vista) {
        this.vista = vista;
        this.libroDAO = new LibroDAO();
        configurarEventos();
        cargarLibros();
    }
    
	public void configurarEventos() {
        vista.agregarGuardarListener(e -> guardarLibro());
        vista.agregarLimpiarListener(e -> vista.limpiarFormulario());
        vista.agregarEditarListener(e -> editarLibro());
        vista.agregarEliminarListener(e -> eliminarLibro());
        vista.agregarActualizarListener(e -> cargarLibros());
        vista.agregarBuscarListener(e -> buscarLibros());
    }
    
	public void guardarLibro() {
        try {
            // Validaciones
            if (vista.getTitulo().isEmpty() || vista.getAutor().isEmpty()) {
                JOptionPane.showMessageDialog(vista, "Título y Autor son obligatorios", "Error", JOptionPane.ERROR_MESSAGE);
                return;
            }
            
            // Crear objeto Libro con tu constructor
            Libro libro = new Libro(
                0, // ID temporal (la BD lo auto-genera)
                vista.getTitulo(),
                vista.getAnioTexto(),
                vista.getAutor(),
                vista.getCategoria(),
                vista.getEditorial(),
                Integer.parseInt(vista.getEjemplaresTexto()),
                Integer.parseInt(vista.getEjemplaresTexto()) // Inicialmente igual al total
            );
            
            // Guardar en base de datos
            if (libroDAO.insertarLibro(libro)) {
                JOptionPane.showMessageDialog(vista, "Libro guardado exitosamente", "Éxito", JOptionPane.INFORMATION_MESSAGE);
                vista.limpiarFormulario();
                cargarLibros();
            } else {
                JOptionPane.showMessageDialog(vista, "Error al guardar el libro", "Error", JOptionPane.ERROR_MESSAGE);
            }
            
        } catch (NumberFormatException e) {
            JOptionPane.showMessageDialog(vista, "Ejemplares debe ser un número válido", "Error", JOptionPane.ERROR_MESSAGE);
        } catch (Exception e) {
            JOptionPane.showMessageDialog(vista, "Error: " + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
    }
    
	public void cargarLibros() {
        List<Libro> libros = libroDAO.obtenerTodosLosLibros();
        Object[][] datos = new Object[libros.size()][7];
        
        for (int i = 0; i < libros.size(); i++) {
            Libro libro = libros.get(i);
            datos[i][0] = libro.getId();
            datos[i][1] = libro.getTitulo();
            datos[i][2] = libro.getAutor();
            datos[i][3] = libro.getAnio();
            datos[i][4] = libro.getCategoria();
            datos[i][5] = libro.getTotal();
            datos[i][6] = libro.getDisponibles();
        }
        
        vista.actualizarTablaLibros(datos);
    }
    
	public void editarLibro() {
        int filaSeleccionada = vista.getFilaSeleccionadaLibros();
        if (filaSeleccionada == -1) {
            JOptionPane.showMessageDialog(vista, "Seleccione un libro para editar", "Advertencia", JOptionPane.WARNING_MESSAGE);
            return;
        }
        
        // TODO: Implementar edición
        JOptionPane.showMessageDialog(vista, "Funcionalidad de edición en desarrollo", "Info", JOptionPane.INFORMATION_MESSAGE);
    }
    
	public void eliminarLibro() {
        int filaSeleccionada = vista.getFilaSeleccionadaLibros();
        if (filaSeleccionada == -1) {
            JOptionPane.showMessageDialog(vista, "Seleccione un libro para eliminar", "Advertencia", JOptionPane.WARNING_MESSAGE);
            return;
        }
        
        int id = (int) vista.getTableLibros().getValueAt(filaSeleccionada, 0);
        String titulo = (String) vista.getTableLibros().getValueAt(filaSeleccionada, 1);
        
        int confirmacion = JOptionPane.showConfirmDialog(
            vista,
            "¿Está seguro de eliminar el libro: " + titulo + "?",
            "Confirmar eliminación",
            JOptionPane.YES_NO_OPTION,
            JOptionPane.WARNING_MESSAGE
        );
        
        if (confirmacion == JOptionPane.YES_OPTION) {
            if (libroDAO.eliminarLibro(id)) {
                JOptionPane.showMessageDialog(vista, "Libro eliminado exitosamente", "Éxito", JOptionPane.INFORMATION_MESSAGE);
                cargarLibros();
            } else {
                JOptionPane.showMessageDialog(vista, "Error al eliminar el libro", "Error", JOptionPane.ERROR_MESSAGE);
            }
        }
    }
    
	public void buscarLibros() {
        String criterio = vista.getCriterioBusqueda();
        String valor = vista.getTextoBusqueda();
        
        if (valor.isEmpty()) {
            JOptionPane.showMessageDialog(vista, "Ingrese un valor para buscar", "Advertencia", JOptionPane.WARNING_MESSAGE);
            return;
        }
        
        List<Libro> libros = libroDAO.buscarLibros(criterio, valor);
        Object[][] datos = new Object[libros.size()][5];
        
        for (int i = 0; i < libros.size(); i++) {
            Libro libro = libros.get(i);
            datos[i][0] = libro.getId();
            datos[i][1] = libro.getTitulo();
            datos[i][2] = libro.getAutor();
            datos[i][3] = libro.getCategoria();
            datos[i][4] = libro.getDisponibles();
        }
        
        vista.actualizarTablaBusqueda(datos);
        
        if (libros.isEmpty()) {
            JOptionPane.showMessageDialog(vista, "No se encontraron libros", "Info", JOptionPane.INFORMATION_MESSAGE);
        }
    }
    
    public int obtenerTotalLibros() {
        return libroDAO.contarTotalLibros();
    }
}