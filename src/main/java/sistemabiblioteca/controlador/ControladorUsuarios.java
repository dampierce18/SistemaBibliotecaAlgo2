package sistemabiblioteca.controlador;

import sistemabiblioteca.modelo.Usuario;
import java.util.ArrayList;
import java.util.List;

public class ControladorUsuario {
    private List<Usuario> listaUsuarios = new ArrayList<>();
    private int contadorId = 1;

    public void registrarUsuario(String nombre, String apellidoPaterno, String apellidoMaterno,
                                 String domicilio, String telefono) {
        Usuario usuario = new Usuario();
        usuario.setId(contadorId++);
        usuario.setNombre(nombre);
        usuario.setApellidoPaterno(apellidoPaterno);
        usuario.setApellidoMaterno(apellidoMaterno);
        usuario.setDomicilio(domicilio);
        usuario.setTelefono(telefono);
        usuario.setSanciones(0);
        usuario.setMontoSancion(0);

        listaUsuarios.add(usuario);
        System.out.println("Usuario registrado correctamente.");
    }

    public void mostrarUsuarios() {
        if (listaUsuarios.isEmpty()) {
            System.out.println("No hay usuarios registrados.");
            return;
        }

        System.out.println("\n--- LISTA DE USUARIOS ---");
        for (Usuario usuario : listaUsuarios) {
            System.out.println("ID: " + usuario.getId());
            System.out.println("Nombre: " + usuario.getNombre() + " " +
                               usuario.getApellidoPaterno() + " " +
                               usuario.getApellidoMaterno());
            System.out.println("Domicilio: " + usuario.getDomicilio());
            System.out.println("Teléfono: " + usuario.getTelefono());
            System.out.println("-----------------------------");
        }
    }
    
    public Usuario buscarUsuarioPorId(int id) {
        for (Usuario usuario : listaUsuarios) {
            if (usuario.getId() == id) {
                return usuario;
            }
        }
        return null; // Si no se encuentra el usuario
    }

    
}
