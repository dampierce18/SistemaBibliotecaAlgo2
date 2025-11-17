package sistemabiblioteca.controlador;

import sistemabiblioteca.dao.LibroDAO;
import sistemabiblioteca.modelo.Libro;
import sistemabiblioteca.vista.PanelLibros;
import java.util.List;

import javax.swing.JOptionPane;

public class ControladorLibros {
    private PanelLibros vista;
    private LibroDAO libroDAO;
    
    public ControladorLibros(PanelLibros vista) {
        this(vista, new LibroDAO());
    }

    public ControladorLibros(PanelLibros vista, LibroDAO libroDAO) {
        this.vista = vista;
        this.libroDAO = libroDAO;
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
            // Validaciones básicas
            if (!vista.validarCamposLibro()) {
                return;
            }
            
            // Crear objeto Libro
            Libro libro = new Libro(
                0, // ID temporal (la BD lo auto-genera)
                vista.getTitulo(),
                vista.getAnioTexto(),
                vista.getAutor(),
                vista.getCategoria(),
                vista.getEditorial(),
                Integer.parseInt(vista.getEjemplaresTexto()),
                Integer.parseInt(vista.getEjemplaresTexto()) 
            );
            
            // Guardar en base de datos
            if (libroDAO.insertarLibro(libro)) {
                vista.mostrarMensaje("Libro guardado exitosamente", JOptionPane.INFORMATION_MESSAGE);
                vista.limpiarFormulario();
                cargarLibros();
            } else {
                vista.mostrarMensaje("Error al guardar el libro", JOptionPane.ERROR_MESSAGE);
            }
            
        } catch (NumberFormatException e) {
            vista.mostrarMensaje("Ejemplares debe ser un número válido", JOptionPane.ERROR_MESSAGE);
        } catch (Exception e) {
            vista.mostrarMensaje("Error: " + e.getMessage(), JOptionPane.ERROR_MESSAGE);
        }
    }
    
    public void cargarLibros() {
        List<Libro> libros = libroDAO.obtenerTodosLosLibros();
        vista.mostrarLibros(libros);
    }
    
    public void editarLibro() {
        Integer libroId = vista.obtenerLibroIdSeleccionado();
        if (libroId == null) {
            vista.mostrarMensaje("Seleccione un libro para editar", JOptionPane.WARNING_MESSAGE);
            return;
        }
        
        vista.mostrarMensaje("Funcionalidad de edición en desarrollo", JOptionPane.INFORMATION_MESSAGE);
    }
    
    public void eliminarLibro() {
        Integer libroId = vista.obtenerLibroIdSeleccionado();
        String titulo = vista.obtenerTituloLibroSeleccionado();
        
        if (libroId == null) {
            vista.mostrarMensaje("Seleccione un libro para eliminar", JOptionPane.WARNING_MESSAGE);
            return;
        }
        
        boolean confirmado = vista.mostrarConfirmacion(
            "¿Está seguro de eliminar el libro: " + titulo + "?",
            "Confirmar eliminación"
        );
        
        if (confirmado) {
            if (libroDAO.eliminarLibro(libroId)) {
                vista.mostrarMensaje("Libro eliminado exitosamente", JOptionPane.INFORMATION_MESSAGE);
                cargarLibros();
            } else {
                vista.mostrarMensaje("Error al eliminar el libro", JOptionPane.ERROR_MESSAGE);
            }
        }
    }
    
    public void buscarLibros() {
        String criterio = vista.getCriterioBusqueda();
        String valor = vista.getTextoBusqueda();
        
        if (valor.isEmpty()) {
            vista.mostrarMensaje("Ingrese un valor para buscar", JOptionPane.WARNING_MESSAGE);
            return;
        }
        
        List<Libro> libros = libroDAO.buscarLibros(criterio, valor);
        vista.mostrarResultadosBusqueda(libros);
        
        if (libros.isEmpty()) {
            vista.mostrarMensaje("No se encontraron libros", JOptionPane.INFORMATION_MESSAGE);
        }
    }
    
    public int obtenerTotalLibros() {
        return libroDAO.contarTotalLibros();
    }
}