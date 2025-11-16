package sistemabiblioteca.dao;

import sistemabiblioteca.bd.ConexionSQLite;
import sistemabiblioteca.modelo.Libro;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class LibroDAO {
    
    public boolean insertarLibro(Libro libro) {
        String sql = "INSERT INTO libros (titulo, anio, autor, categoria, editorial, total, disponibles) VALUES (?, ?, ?, ?, ?, ?, ?)";
        
        try (Connection conn = ConexionSQLite.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
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
        }
    }
    
    public List<Libro> obtenerTodosLosLibros() {
        List<Libro> libros = new ArrayList<>();
        String sql = "SELECT * FROM libros ORDER BY titulo";
        
        try (Connection conn = ConexionSQLite.getConnection();
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
    
    public boolean actualizarLibro(Libro libro) {
        String sql = "UPDATE libros SET titulo=?, anio=?, autor=?, categoria=?, editorial=?, total=?, disponibles=? WHERE id=?";
        
        try (Connection conn = ConexionSQLite.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
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
        }
    }
    
    public boolean eliminarLibro(int id) {
        String sql = "DELETE FROM libros WHERE id=?";
        
        try (Connection conn = ConexionSQLite.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setInt(1, id);
            return pstmt.executeUpdate() > 0;
            
        } catch (SQLException e) {
            System.err.println("Error eliminando libro: " + e.getMessage());
            return false;
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
            default:
                sql = "SELECT * FROM libros WHERE titulo LIKE ? ORDER BY titulo";
        }
        
        try (Connection conn = ConexionSQLite.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setString(1, "%" + valor + "%");
            ResultSet rs = pstmt.executeQuery();
            
            while (rs.next()) {
                Libro libro = resultSetALibro(rs);
                libros.add(libro);
            }
            
        } catch (SQLException e) {
            System.err.println("Error buscando libros: " + e.getMessage());
        }
        
        return libros;
    }
    
    public int contarTotalLibros() {
        String sql = "SELECT COUNT(*) as total FROM libros";
        
        try (Connection conn = ConexionSQLite.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            
            if (rs.next()) {
                return rs.getInt("total");
            }
            
        } catch (SQLException e) {
            System.err.println("Error contando libros: " + e.getMessage());
        }
        
        return 0;
    }
    
    public Libro obtenerLibroPorId(int id) {
        String sql = "SELECT * FROM libros WHERE id=?";
        
        try (Connection conn = ConexionSQLite.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setInt(1, id);
            ResultSet rs = pstmt.executeQuery();
            
            if (rs.next()) {
                return resultSetALibro(rs);
            }
            
        } catch (SQLException e) {
            System.err.println("Error obteniendo libro por ID: " + e.getMessage());
        }
        
        return null;
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
            rs.getInt("disponibles")
        );
    }
}