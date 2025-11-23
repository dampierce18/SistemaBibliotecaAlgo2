package sistemabiblioteca.dao;

import sistemabiblioteca.bd.ConexionSQLite;
import sistemabiblioteca.modelo.Libro;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class LibroDAO {
    private Connection connection;
    
    public LibroDAO() {
        this.connection = null;
    }
    
    public LibroDAO(Connection testConnection) {
        this.connection = testConnection;
    }
    
    private Connection getConnection() throws SQLException {
        if (this.connection != null) {
            return this.connection;
        }
        return ConexionSQLite.getConnection();
    }
    
    public boolean insertarLibro(Libro libro) {
        String sql = "INSERT INTO libros (titulo, anio, autor, categoria, editorial, total, disponibles, empleado_id) VALUES (?, ?, ?, ?, ?, ?, ?, ?)";
        PreparedStatement pstmt = null;
        
        try {
            Connection conn = getConnection();
            boolean autoCommit = conn.getAutoCommit();
            pstmt = conn.prepareStatement(sql);
            
            pstmt.setString(1, libro.getTitulo());
            pstmt.setString(2, libro.getAnio());
            pstmt.setString(3, libro.getAutor());
            pstmt.setString(4, libro.getCategoria());
            pstmt.setString(5, libro.getEditorial());
            pstmt.setInt(6, libro.getTotal());
            pstmt.setInt(7, libro.getDisponibles());
            pstmt.setInt(8, libro.getEmpleadoId());
            
            int filasAfectadas = pstmt.executeUpdate();
            if (!autoCommit) {
                conn.commit();
            }
            return filasAfectadas > 0;
            
        } catch (SQLException e) {
            return false;
        } finally {
            if (pstmt != null) {
                try { pstmt.close(); } catch (SQLException e) { }
            }
        }
    }
    
    public Libro obtenerLibroPorId(int id) {
        String sql = "SELECT * FROM libros WHERE id=?";
        PreparedStatement pstmt = null;
        ResultSet rs = null;
        
        try {
            Connection conn = getConnection();
            pstmt = conn.prepareStatement(sql);
            pstmt.setInt(1, id);
            rs = pstmt.executeQuery();
            
            if (rs.next()) {
                return resultSetALibro(rs);
            }
            
        } catch (SQLException e) {
            System.err.println("Error obteniendo libro por ID: " + e.getMessage());
        } finally {
            if (rs != null) {
                try { rs.close(); } catch (SQLException e) { }
            }
            if (pstmt != null) {
                try { pstmt.close(); } catch (SQLException e) { }
            }
        }
        
        return null;
    }
    
    public int contarPrestamosActivos(int libroId) {
        String sql = "SELECT COUNT(*) as count FROM prestamos WHERE libro_id = ? AND estado = 'ACTIVO'";
        PreparedStatement pstmt = null;
        ResultSet rs = null;
        
        try {
            Connection conn = getConnection();
            pstmt = conn.prepareStatement(sql);
            pstmt.setInt(1, libroId);
            rs = pstmt.executeQuery();
            
            if (rs.next()) {
                return rs.getInt("count");
            }
            
        } catch (SQLException e) {
            System.err.println("Error contando préstamos activos: " + e.getMessage());
        } finally {
            if (rs != null) {
                try { rs.close(); } catch (SQLException e) { }
            }
            if (pstmt != null) {
                try { pstmt.close(); } catch (SQLException e) { }
            }
        }
        
        return 0;
    }
    
    public boolean actualizarLibro(Libro libro) {
        String sql = "UPDATE libros SET titulo=?, anio=?, autor=?, categoria=?, editorial=?, total=?, disponibles=? WHERE id=?";
        PreparedStatement pstmt = null;
        
        try {
            Connection conn = getConnection();
            pstmt = conn.prepareStatement(sql);
            
            pstmt.setString(1, libro.getTitulo());
            pstmt.setString(2, libro.getAnio());
            pstmt.setString(3, libro.getAutor());
            pstmt.setString(4, libro.getCategoria());
            pstmt.setString(5, libro.getEditorial());
            pstmt.setInt(6, libro.getTotal());
            pstmt.setInt(7, libro.getDisponibles());
            pstmt.setInt(8, libro.getId());
            
            return pstmt.executeUpdate() > 0;
            
        } catch (SQLException e) {
            System.err.println("Error actualizando libro: " + e.getMessage());
            return false;
        } finally {
            if (pstmt != null) {
                try { pstmt.close(); } catch (SQLException e) { }
            }
        }
    }
    
    public boolean eliminarLibro(int id) {
        String sql = "DELETE FROM libros WHERE id=?";
        PreparedStatement pstmt = null;
        
        try {
            Connection conn = getConnection();
            pstmt = conn.prepareStatement(sql);
            pstmt.setInt(1, id);
            
            return pstmt.executeUpdate() > 0;
            
        } catch (SQLException e) {
            System.err.println("Error eliminando libro: " + e.getMessage());
            return false;
        } finally {
            if (pstmt != null) {
                try { pstmt.close(); } catch (SQLException e) { }
            }
        }
    }
    
    public List<Libro> buscarLibros(String criterio, String valor) {
        List<Libro> libros = new ArrayList<>();
        String sql = "";
        
        switch (criterio) {
            case "Título":
                sql = "SELECT * FROM libros WHERE titulo LIKE ? ORDER BY titulo";
                break;
            case "Autor":
                sql = "SELECT * FROM libros WHERE autor LIKE ? ORDER BY titulo";
                break;
            case "Categoría":
                sql = "SELECT * FROM libros WHERE categoria LIKE ? ORDER BY titulo";
                break;
            case "Editorial":
                sql = "SELECT * FROM libros WHERE editorial LIKE ? ORDER BY titulo";
                break;
            case "ID":
                sql = "SELECT * FROM libros WHERE id = ? ORDER BY titulo";
                break;
            default:
                sql = "SELECT * FROM libros WHERE titulo LIKE ? ORDER BY titulo";
        }
        
        PreparedStatement pstmt = null;
        ResultSet rs = null;
        
        try {
            Connection conn = getConnection();
            pstmt = conn.prepareStatement(sql);
            
            if ("ID".equals(criterio)) {
                try {
                    pstmt.setInt(1, Integer.parseInt(valor));
                } catch (NumberFormatException e) {
                    return libros;
                }
            } else {
                pstmt.setString(1, "%" + valor + "%");
            }
            
            rs = pstmt.executeQuery();
            
            while (rs.next()) {
                Libro libro = resultSetALibro(rs);
                libros.add(libro);
            }
            
        } catch (SQLException e) {
            System.err.println("Error buscando libros: " + e.getMessage());
        } finally {
            if (rs != null) {
                try { rs.close(); } catch (SQLException e) { }
            }
            if (pstmt != null) {
                try { pstmt.close(); } catch (SQLException e) { }
            }
        }
        
        return libros;
    }
    
    public int contarTotalLibros() {
        String sql = "SELECT COUNT(*) as total FROM libros";
        Statement stmt = null;
        ResultSet rs = null;
        
        try {
            Connection conn = getConnection();
            stmt = conn.createStatement();
            rs = stmt.executeQuery(sql);
            
            if (rs.next()) {
                return rs.getInt("total");
            }
            
        } catch (SQLException e) {
            System.err.println("Error contando libros: " + e.getMessage());
        } finally {
            if (rs != null) {
                try { rs.close(); } catch (SQLException e) { }
            }
            if (stmt != null) {
                try { stmt.close(); } catch (SQLException e) { }
            }
        }
        
        return 0;
    }
    
    public List<Libro> obtenerTodosLosLibros(String orden) {
        List<Libro> libros = new ArrayList<>();
        
        // Validar el criterio de orden para prevenir SQL injection
        String ordenValido = validarOrden(orden);
        String sql = "SELECT * FROM libros ORDER BY " + ordenValido;
        
        try (Connection conn = getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            
            while (rs.next()) {
                Libro libro = resultSetALibro(rs);
                libros.add(libro);
            }
            
        } catch (SQLException e) {
            System.err.println("Error obteniendo libros: " + e.getMessage());
        }
        
        return libros;
    }
    
    // Método para validar el criterio de orden (seguridad)
    private String validarOrden(String orden) {
        // Lista blanca de criterios permitidos
        String[] criteriosPermitidos = {
            "id", "titulo", "autor", "categoria", "editorial", "anio", 
            "total", "disponibles", "disponibles DESC", "anio DESC", "empleado_id"
        };
        
        for (String criterio : criteriosPermitidos) {
            if (criterio.equals(orden)) {
                return orden;
            }
        }
        
        // Si no es válido, usar orden por defecto
        return "id";
    }
    
    // Mantener método antiguo para compatibilidad
    public List<Libro> obtenerTodosLosLibros() {
        return obtenerTodosLosLibros("id");
    }
    
    private Libro resultSetALibro(ResultSet rs) throws SQLException {
        return new Libro(
            rs.getInt("id"),
            rs.getString("titulo"),
            rs.getString("anio"),
            rs.getString("autor"),
            rs.getString("categoria"),
            rs.getString("editorial"),
            rs.getInt("total"),
            rs.getInt("disponibles"),
            rs.getInt("empleado_id")
        );
    }
}