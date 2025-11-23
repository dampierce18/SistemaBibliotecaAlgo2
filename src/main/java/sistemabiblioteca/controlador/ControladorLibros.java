package sistemabiblioteca.controlador;

import sistemabiblioteca.dao.LibroDAO;
import sistemabiblioteca.modelo.Empleado;
import sistemabiblioteca.modelo.Libro;
import sistemabiblioteca.vista.PanelLibros;
import java.util.List;

import javax.swing.JOptionPane;

public class ControladorLibros {
    private PanelLibros vista;
    private LibroDAO libroDAO;
    private Empleado empleadoLogueado;
    private String ordenActual = "id"; // Orden por defecto
    boolean modoEdicion = false;
    Integer libroEditandoId = null;
    
    // Constructor original modificado
    public ControladorLibros(PanelLibros vista, Empleado empleadoLogueado) {
        this(vista, new LibroDAO(), empleadoLogueado);
    }
    
    // Constructor para testing modificado
    ControladorLibros(PanelLibros vista, LibroDAO libroDAO, Empleado empleadoLogueado) {
        this.vista = vista;
        this.libroDAO = libroDAO;
        this.empleadoLogueado = empleadoLogueado;
        
        // Configurar el listener para el menú de ordenar
        vista.setOrdenarListener(e -> ordenarLibros(e.getActionCommand()));
        
        configurarEventos();
        cargarLibros();
    }
    
    // Mantener constructor antiguo para compatibilidad (si es necesario)
    public ControladorLibros(PanelLibros vista) {
        this(vista, new LibroDAO(), null);
    }
    
    // Constructor antiguo para testing
    ControladorLibros(PanelLibros vista, LibroDAO libroDAO) {
        this(vista, libroDAO, null);
    }
    
    void configurarEventos() {
        vista.agregarGuardarListener(e -> guardarLibro());
        vista.agregarLimpiarListener(e -> limpiarFormulario());
        vista.agregarEditarListener(e -> editarLibro());
        vista.agregarEliminarListener(e -> eliminarLibro());
        vista.agregarActualizarListener(e -> cargarLibros());
        vista.agregarBuscarListener(e -> buscarLibros());
    }
    
    void ordenarLibros(String criterio) {
        this.ordenActual = criterio;
        cargarLibros();
        
        // Mostrar mensaje de confirmación
        String mensaje = "";
        switch (criterio) {
            case "id":
                mensaje = "Ordenado por ID";
                break;
            case "titulo":
                mensaje = "Ordenado por Título";
                break;
            case "autor":
                mensaje = "Ordenado por Autor";
                break;
            case "categoria":
                mensaje = "Ordenado por Categoría";
                break;
            case "disponibles DESC":
                mensaje = "Ordenado por Disponibles (mayor a menor)";
                break;
            case "anio DESC":
                mensaje = "Ordenado por Año (más reciente primero)";
                break;
            default:
                mensaje = "Orden aplicado";
        }
        vista.mostrarMensaje(mensaje, JOptionPane.INFORMATION_MESSAGE);
    }
    
    void editarLibro() {
        Integer libroId = vista.obtenerLibroIdSeleccionado();
        if (libroId == null) {
            vista.mostrarMensaje("Seleccione un libro para editar", JOptionPane.WARNING_MESSAGE);
            return;
        }
        
        // Obtener el libro seleccionado
        Libro libro = libroDAO.obtenerLibroPorId(libroId);
        if (libro == null) {
            vista.mostrarMensaje("Error al cargar los datos del libro", JOptionPane.ERROR_MESSAGE);
            return;
        }
        
        // Cargar datos en el formulario y cambiar a pestaña de agregar/editar
        vista.cargarDatosEnFormulario(libro);
        vista.cambiarAPestanaFormulario();
        
        // Activar modo edición
        this.modoEdicion = true;
        this.libroEditandoId = libroId;
        
        vista.mostrarMensaje("Editando libro: " + libro.getTitulo() + 
                           ". Total actual: " + libro.getTotal() + 
                           ", Disponibles: " + libro.getDisponibles(), 
                           JOptionPane.INFORMATION_MESSAGE);
    }

    void guardarLibro() {
        try {
            if (!vista.validarCamposLibro()) {
                return;
            }
            
            if (modoEdicion) {
                // Modo edición - Actualizar libro existente
                actualizarLibro();
            } else {
                // Modo nuevo - Insertar libro
                insertarLibro();
            }
            
        } catch (NumberFormatException e) {
            vista.mostrarMensaje("Ejemplares debe ser un número válido", JOptionPane.ERROR_MESSAGE);
        } catch (Exception e) {
            vista.mostrarMensaje("Error: " + e.getMessage(), JOptionPane.ERROR_MESSAGE);
        }
    }
    
     void insertarLibro() {
        // Verificar que hay un empleado logueado
        if (empleadoLogueado == null) {
            vista.mostrarMensaje("Error: No hay empleado logueado", JOptionPane.ERROR_MESSAGE);
            return;
        }
        
        int totalEjemplares = Integer.parseInt(vista.getEjemplaresTexto());
        
        Libro libro = new Libro(
            0, // ID temporal (la BD lo auto-genera)
            vista.getTitulo(),
            vista.getAnioTexto(),
            vista.getAutor(),
            vista.getCategoria(),
            vista.getEditorial(),
            totalEjemplares,
            totalEjemplares, // Al crear, todos están disponibles
            empleadoLogueado.getId() // Usar el ID del empleado logueado
        );
        
        if (libroDAO.insertarLibro(libro)) {
            vista.mostrarMensaje("Libro guardado exitosamente", JOptionPane.INFORMATION_MESSAGE);
            limpiarFormulario();
            cargarLibros();
        } else {
            vista.mostrarMensaje("Error al guardar el libro", JOptionPane.ERROR_MESSAGE);
        }
    }
    
    void actualizarLibro() {
        if (libroEditandoId == null) {
            vista.mostrarMensaje("Error: No se pudo identificar el libro a editar", JOptionPane.ERROR_MESSAGE);
            return;
        }
        
        // Obtener el libro original para mantener el empleado_id y calcular la diferencia
        Libro libroOriginal = libroDAO.obtenerLibroPorId(libroEditandoId);
                
        int nuevoTotal = Integer.parseInt(vista.getEjemplaresTexto());
        int totalActual = libroOriginal.getTotal();
        int disponiblesActual = libroOriginal.getDisponibles();
        
        // Calcular cuántos libros están prestados actualmente
        int prestadosActual = totalActual - disponiblesActual;
        
        // Validar que el nuevo total no sea menor que los libros prestados
        if (nuevoTotal < prestadosActual) {
            vista.mostrarMensaje(
                "Error: No puede reducir el total a " + nuevoTotal + 
                " porque hay " + prestadosActual + " libros prestados. " +
                "El total mínimo debe ser " + prestadosActual, 
                JOptionPane.ERROR_MESSAGE
            );
            return;
        }
        
        // Calcular nuevos disponibles: nuevoTotal - libros prestados
        int nuevosDisponibles = nuevoTotal - prestadosActual;
        
        // Crear libro actualizado
        Libro libroActualizado = new Libro(
            libroEditandoId,
            vista.getTitulo(),
            vista.getAnioTexto(),
            vista.getAutor(),
            vista.getCategoria(),
            vista.getEditorial(),
            nuevoTotal,
            nuevosDisponibles, // Calculado basado en libros prestados
            libroOriginal.getEmpleadoId() // Mantener el empleado original
        );
        
        if (libroDAO.actualizarLibro(libroActualizado)) {
            String mensaje = "Libro actualizado exitosamente.\n" +
                           "Total anterior: " + totalActual + " → Nuevo total: " + nuevoTotal + "\n" +
                           "Disponibles anteriores: " + disponiblesActual + " → Nuevos disponibles: " + nuevosDisponibles + "\n" +
                           "Libros prestados: " + prestadosActual + " (no cambian)";
            vista.mostrarMensaje(mensaje, JOptionPane.INFORMATION_MESSAGE);
            limpiarFormulario();
            cargarLibros();
        } 
    }
  
    void eliminarLibro() {
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
            } 
        }
    }
    
    void cargarLibros() {
        List<Libro> libros = libroDAO.obtenerTodosLosLibros(ordenActual);
        vista.mostrarLibros(libros);
    }
    
    public int obtenerTotalLibros() {
        return libroDAO.contarTotalLibros();
    }
    
    void buscarLibros() {
        String criterio = vista.getCriterioBusqueda();
        String valor = vista.getTextoBusqueda();
        
        
        List<Libro> libros = libroDAO.buscarLibros(criterio, valor);
        vista.mostrarResultadosBusqueda(libros);
        
        if (libros.isEmpty()) {
            vista.mostrarMensaje("No se encontraron libros", JOptionPane.INFORMATION_MESSAGE);
        }
    }
    
    void limpiarFormulario() {
        vista.limpiarFormulario();
        this.modoEdicion = false;
        this.libroEditandoId = null;
    }
    
    // Getter para testing
    public Empleado getEmpleadoLogueado() {
        return empleadoLogueado;
    }
    
    // Getter para el orden actual (útil para testing)
    public String getOrdenActual() {
        return ordenActual;
    }
    
    // Getters para testing del modo edición
    public boolean isModoEdicion() {
        return modoEdicion;
    }
    
    public Integer getLibroEditandoId() {
        return libroEditandoId;
    }
}