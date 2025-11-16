package sistemabiblioteca.modelo;

import java.time.LocalDate;

public class Prestamo {
    private int id;
    private int libroId;
    private int usuarioId;
    private LocalDate fechaPrestamo;
    private LocalDate fechaDevolucion;
    private LocalDate fechaDevolucionReal;
    private String estado; // "ACTIVO", "DEVUELTO", "ATRASADO"
    
    // Constructores
    public Prestamo() {}
    
    public Prestamo(int libroId, int usuarioId, LocalDate fechaPrestamo, LocalDate fechaDevolucion) {
        this.libroId = libroId;
        this.usuarioId = usuarioId;
        this.fechaPrestamo = fechaPrestamo;
        this.fechaDevolucion = fechaDevolucion;
        this.estado = "ACTIVO";
    }
    
    // Getters y Setters
    public int getId() { return id; }
    public void setId(int id) { this.id = id; }
    
    public int getLibroId() { return libroId; }
    public void setLibroId(int libroId) { this.libroId = libroId; }
    
    public int getUsuarioId() { return usuarioId; }
    public void setUsuarioId(int usuarioId) { this.usuarioId = usuarioId; }
    
    public LocalDate getFechaPrestamo() { return fechaPrestamo; }
    public void setFechaPrestamo(LocalDate fechaPrestamo) { this.fechaPrestamo = fechaPrestamo; }
    
    public LocalDate getFechaDevolucion() { return fechaDevolucion; }
    public void setFechaDevolucion(LocalDate fechaDevolucion) { this.fechaDevolucion = fechaDevolucion; }
    
    public LocalDate getFechaDevolucionReal() { return fechaDevolucionReal; }
    public void setFechaDevolucionReal(LocalDate fechaDevolucionReal) { this.fechaDevolucionReal = fechaDevolucionReal; }
    
    public String getEstado() { return estado; }
    public void setEstado(String estado) { this.estado = estado; }
}