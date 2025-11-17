package sistemabiblioteca.modelo;

public class Usuario {
    private int id;
    private String nombre;
    private String apellidoPaterno;
    private String apellidoMaterno;
    private String domicilio;
    private String telefono;
    private int sanciones;
    private int montoSancion;
    
    public Usuario() {
        this.sanciones = 0;
        this.montoSancion = 0;
    }
    
    public Usuario(String nombre, String apellidoPaterno, String apellidoMaterno, 
                   String domicilio, String telefono) {
        this.nombre = nombre;
        this.apellidoPaterno = apellidoPaterno;
        this.apellidoMaterno = apellidoMaterno;
        this.domicilio = domicilio;
        this.telefono = telefono;
        this.sanciones = 0;
        this.montoSancion = 0;
    }


    public void setId(int id) {
        this.id = id;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public void setApellidoPaterno(String apellidoPaterno) {
        this.apellidoPaterno = apellidoPaterno;
    }

    public void setApellidoMaterno(String apellidoMaterno) {
        this.apellidoMaterno = apellidoMaterno;
    }

    public void setDomicilio(String domicilio) {
        this.domicilio = domicilio;
    }

    public void setTelefono(String telefono) {
        this.telefono = telefono;
    }

    public void setSanciones(int sanciones) {
        this.sanciones = sanciones;
    }

    public void setMontoSancion(int montoSancion) {
        this.montoSancion = montoSancion;
    }

    public int getId() {
        return id;
    }

    public String getNombre() {
        return nombre;
    }

    public String getApellidoPaterno() {
        return apellidoPaterno;
    }

    public String getApellidoMaterno() {
        return apellidoMaterno;
    }

    public String getDomicilio() {
        return domicilio;
    }

    public String getTelefono() {
        return telefono;
    }

    public int getSanciones() {
        return sanciones;
    }

    public int getMontoSancion() {
        return montoSancion;
    }
}
