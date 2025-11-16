package sistemabiblioteca.modelo;

public class Libro {
    private int id;
    private String titulo;
    private String anio;
    private String autor;
    private String categoria;
    private String editorial;
    private int total;
    private int disponibles;

    // Constructor
    public Libro(int id, String titulo, String fecha, String autor, String categoria, String editorial, int total, int disponibles) {
		super();
		this.id = id;
		this.titulo = titulo;
		this.anio = fecha;
		this.autor = autor;
		this.categoria = categoria;
		this.editorial = editorial;
		this.total = total;
		this.disponibles = disponibles;
	}

	//  Setters    
    public void setId(int id) {
        this.id = id;
    }

    public void setTitulo(String titulo) {
        this.titulo = titulo;
    }

    public void setAnio(String anio) {
        this.anio = anio;
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

    public void setTotal(int total) {
        this.total = total;
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

	public String getAnio() {
		return anio;
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

	public int getTotal() {
		return total;
	}

	public int getDisponibles() {
		return disponibles;
	}

 


}
