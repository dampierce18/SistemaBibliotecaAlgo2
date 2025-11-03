package sistemabiblioteca.controlador;

import java.util.ArrayList;
import java.util.List;
import sistemabiblioteca.modelo.Prestamo;
import sistemabiblioteca.modelo.Usuario;
import sistemabiblioteca.modelo.Libro;

public class ControladorPrestamo {
    private List<Prestamo> listaPrestamos = new ArrayList<>();
    private int contadorId = 1;

    public void registrarPrestamo(Usuario usuario, Libro libro, String fechaPrestamo, String fechaDevolucion) {
        if (libro.getDisponibles() <= 0) {
            System.out.println("No hay ejemplares disponibles de este libro.");
            return;
        }

        Prestamo prestamo = new Prestamo();
        prestamo.setId(contadorId++);
        prestamo.setUsuario(usuario);
        prestamo.setLibro(libro);
        prestamo.setFechaPrestamo(fechaPrestamo);
        prestamo.setFechaDevolucion(fechaDevolucion);
        listaPrestamos.add(prestamo);

        libro.setDisponibles(libro.getDisponibles() - 1);
        System.out.println("Préstamo registrado correctamente.");
    }

    public void mostrarPrestamos() {
        if (listaPrestamos.isEmpty()) {
            System.out.println("No hay prestamos registrados.");
            return;
        }
        System.out.println("\n--- LISTA DE PRÉSTAMOS ---");
        for (Prestamo p : listaPrestamos) {
            System.out.println("ID: " + p.getId());
            System.out.println("Usuario: " + p.getUsuario().getNombre());
            System.out.println("Libro: " + p.getLibro().getTitulo());
            System.out.println("Fecha prestamo: " + p.getFechaPrestamo());
            System.out.println("Fecha devolución: " + p.getFechaDevolucion());
            System.out.println("-----------------------------");
        }
    }
}
