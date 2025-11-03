package sistemabiblioteca.vista;

import sistemabiblioteca.controlador.*;
import sistemabiblioteca.modelo.*;
import java.util.Scanner;

public class VistaPrincipal {
    public static void main(String[] args) {
        Scanner sc = new Scanner(System.in);
        ControladorUsuario ctrlUsuario = new ControladorUsuario();
        ControladorLibro ctrlLibro = new ControladorLibro();
        ControladorPrestamo ctrlPrestamo = new ControladorPrestamo();

        int opcion;

        do {
            System.out.println("\n===== SISTEMA DE GESTIÓN DE BIBLIOTECA =====");
            System.out.println("1. Registrar usuario");
            System.out.println("2. Mostrar usuarios");
            System.out.println("3. Registrar libro");
            System.out.println("4. Mostrar libros");
            System.out.println("5. Registrar préstamo");
            System.out.println("6. Mostrar préstamos");
            System.out.println("0. Salir");
            System.out.print("Seleccione una opción: ");
            opcion = Integer.parseInt(sc.nextLine());

            switch (opcion) {
                case 1:
                    System.out.print("Nombre: ");
                    String nombre = sc.nextLine();
                    System.out.print("Apellido paterno: ");
                    String apPat = sc.nextLine();
                    System.out.print("Apellido materno: ");
                    String apMat = sc.nextLine();
                    System.out.print("Domicilio: ");
                    String domicilio = sc.nextLine();
                    System.out.print("Teléfono: ");
                    String tel = sc.nextLine();
                    ctrlUsuario.registrarUsuario(nombre, apPat, apMat, domicilio, tel);
                    break;

                case 2:
                    ctrlUsuario.mostrarUsuarios();
                    break;

                case 3:
                    System.out.print("Título: ");
                    String titulo = sc.nextLine();
                    System.out.print("Autor: ");
                    String autor = sc.nextLine();
                    System.out.print("Categoría: ");
                    String categoria = sc.nextLine();
                    System.out.print("Stock: ");
                    int stock = Integer.parseInt(sc.nextLine());
                    ctrlLibro.registrarLibro(titulo, autor, categoria, stock);
                    break;

                case 4:
                    ctrlLibro.mostrarLibros();
                    break;

                case 5:
                    ctrlUsuario.mostrarUsuarios();
                    System.out.print("Ingrese ID del usuario: ");
                    int idUsuario = Integer.parseInt(sc.nextLine());
                    Usuario u = ctrlUsuario.buscarUsuarioPorId(idUsuario);

                    ctrlLibro.mostrarLibros();
                    System.out.print("Ingrese ID del libro: ");
                    int idLibro = Integer.parseInt(sc.nextLine());
                    Libro l = ctrlLibro.buscarLibroPorId(idLibro);

                    if (u != null && l != null) {
                        System.out.print("Fecha de préstamo: ");
                        String fechaPrestamo = sc.nextLine();
                        System.out.print("Fecha de devolución: ");
                        String fechaDevolucion = sc.nextLine();
                        ctrlPrestamo.registrarPrestamo(u, l, fechaPrestamo, fechaDevolucion);
                    } else {
                        System.out.println("Usuario o libro no encontrado.");
                    }
                    break;

                case 6:
                    ctrlPrestamo.mostrarPrestamos();
                    break;

                case 0:
                    System.out.println("Saliendo del sistema...");
                    break;

                default:
                    System.out.println("Opción inválida.");
            }
        } while (opcion != 0);

        sc.close();
    }
}
