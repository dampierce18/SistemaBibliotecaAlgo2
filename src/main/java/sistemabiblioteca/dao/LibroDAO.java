package sistemabiblioteca.dao;

import sistemabiblioteca.bd.ConexionSQLite;
import sistemabiblioteca.modelo.Libro;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class LibroDAO {
    private Connection connection;
    
    // ✅ CONSTRUCTOR ORIGINAL (para uso normal)
    public LibroDAO() {
        this.connection = null;
    }
    
    // ✅ CONSTRUCTOR para testing (inyección de dependencias)
    public LibroDAO(Connection testConnection) {
        this.connection = testConnection;
    }
    
    // ✅ MÉTODO PARA OBTENER CONEXIÓN
    private Connection getConnection() throws SQLException {
        if (this.connection != null) {
            return this.connection;
        }
        return ConexionSQLite.getConnection();
    }
    
    // ✅ INSERTAR LIBRO
    public boolean insertarLibro(Libro libro) {
        String sql = "INSERT INTO libros (titulo, anio, autor, categoria, editorial, total, disponibles) VALUES (?, ?, ?, ?, ?, ?, ?)";
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
            
            int filasAfectadas = pstmt.executeUpdate();
            return filasAfectadas > 0;
            
        } catch (SQLException e) {
            System.err.println("Error insertando libro: " + e.getMessage());
            return false;
        } finally {
            if (pstmt != null) {
                try { 
                    pstmt.close(); 
                } catch (SQLException e) {
                }
            }
        }
    }
    
    // ✅ OBTENER TODOS LOS LIBROS
    public List<Libro> obtenerTodosLosLibros() {
        List<Libro> libros = new ArrayList<>();
        String sql = "SELECT * FROM libros ORDER BY titulo";
        Statement stmt = null;
        ResultSet rs = null;
        
        try {
            Connection conn = getConnection();
            stmt = conn.createStatement();
            rs = stmt.executeQuery(sql);
            
            while (rs.next()) {
                Libro libro = resultSetALibro(rs);
                libros.add(libro);
            }
            
        } catch (SQLException e) {
            System.err.println("Error obteniendo libros: " + e.getMessage());
        } finally {
            if (rs != null) {
                try { rs.close(); } catch (SQLException e) { }
            }
            if (stmt != null) {
                try { stmt.close(); } catch (SQLException e) { }
            }
        }
        
        return libros;
    }
    
    // ✅ ACTUALIZAR LIBRO
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
    
    // ✅ ELIMINAR LIBRO
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
    
    // ✅ BUSCAR LIBROS
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
            default:
                sql = "SELECT * FROM libros WHERE titulo LIKE ? ORDER BY titulo";
        }
        
        PreparedStatement pstmt = null;
        ResultSet rs = null;
        
        try {
            Connection conn = getConnection();
            pstmt = conn.prepareStatement(sql);
            pstmt.setString(1, "%" + valor + "%");
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
    
    // ✅ CONTAR TOTAL DE LIBROS
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
    
    // ✅ OBTENER LIBRO POR ID
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
    
    // ✅ CONVERTIR RESULTSET A LIBRO
    private Libro resultSetALibro(ResultSet rs) throws SQLException {
        return new Libro(
            rs.getInt("id"),
            rs.getString("titulo"),
            rs.getString("anio"),
            rs.getString("autor"),
            rs.getString("categoria"),
            rs.getString("editorial"),
            rs.getInt("total"),
            rs.getInt("disponibles")
        );
    }
}