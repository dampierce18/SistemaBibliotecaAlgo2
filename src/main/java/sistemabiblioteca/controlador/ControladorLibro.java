package sistemabiblioteca.controlador;

import sistemabiblioteca.modelo.Libro;
import java.util.ArrayList;
import java.util.List;

public class ControladorLibro {
    private List<Libro> listaLibros = new ArrayList<>();
    private int contadorId = 1;

    public void registrarLibro(String titulo, String autor, String categoria, int stock) {
        Libro libro = new Libro();
        libro.setId(contadorId++);
        libro.setTitulo(titulo);
        libro.setAutor(autor);
        libro.setCategoria(categoria);
        libro.setStock(stock);

        listaLibros.add(libro);
        System.out.println("Libro registrado correctamente.");
    }

    public void mostrarLibros() {
        if (listaLibros.isEmpty()) {
            System.out.println("No hay libros registrados.");
            return;
        }

        System.out.println("\n--- LISTA DE LIBROS ---");
        for (Libro libro : listaLibros) {
            System.out.println("ID: " + libro.getId());
            System.out.println("Título: " + libro.getTitulo());
            System.out.println("Autor: " + libro.getAutor());
            System.out.println("Categoría: " + libro.getCategoria());
            System.out.println("Stock: " + libro.getStock());
            System.out.println("-----------------------------");
        }
    }
    
    public Libro buscarLibroPorId(int id) {
        for (Libro libro : listaLibros) {
            if (libro.getId() == id) {
                return libro;
            }
        }
        return null; // Si no se encuentra el libro
    }

}



