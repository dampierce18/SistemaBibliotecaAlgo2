package sistemabiblioteca.modelo;

public class Libro {
    private int id;
    private String titulo;
    private String fechaPublicacion;
    private String autor;
    private String categoria;
    private String editorial;
    private String idioma;
    private String paginas;
    private String descripcion;
    private String ejemplares;
    private int stock;
    private int disponibles;

    //  Setters    
    public void setId(int id) {
        this.id = id;
    }

    public void setTitulo(String titulo) {
        this.titulo = titulo;
    }

    public void setFechaPublicacion(String fechaPublicacion) {
        this.fechaPublicacion = fechaPublicacion;
    }

    public void setAutor(String autor) {
        this.autor = autor;
    }

    public void setCategoria(String categoria) {
        this.categoria = categoria;
    }

    public void setEditorial(String editorial) {
        this.editorial = editorial;
    }

    public void setIdioma(String idioma) {
        this.idioma = idioma;
    }

    public void setPaginas(String paginas) {
        this.paginas = paginas;
    }

    public void setDescripcion(String descripcion) {
        this.descripcion = descripcion;
    }

    public void setEjemplares(String ejemplares) {
        this.ejemplares = ejemplares;
    }

    public void setStock(int stock) {
        this.stock = stock;
    }

    public void setDisponibles(int disponibles) {
        this.disponibles = disponibles;
    }

    //  Getters 

    public int getId() {
        return id;
    }

    public String getTitulo() {
        return titulo;
    }

    public String getFechaPublicacion() {
        return fechaPublicacion;
    }

    public String getAutor() {
        return autor;
    }

    public String getCategoria() {
        return categoria;
    }

    public String getEditorial() {
        return editorial;
    }

    public String getIdioma() {
        return idioma;
    }

    public String getPaginas() {
        return paginas;
    }

    public String getDescripcion() {
        return descripcion;
    }

    public String getEjemplares() {
        return ejemplares;
    }

    public int getStock() {
        return stock;
    }

    public int getDisponibles() {
        return disponibles;
    }
}
