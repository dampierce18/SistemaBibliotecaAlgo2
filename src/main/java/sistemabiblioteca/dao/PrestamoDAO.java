package sistemabiblioteca.dao;

import sistemabiblioteca.bd.ConexionSQLite;
import sistemabiblioteca.modelo.Prestamo;
import java.sql.*;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public class PrestamoDAO {
    
    public boolean realizarPrestamo(Prestamo prestamo) {
        String sql = "INSERT INTO prestamos (libro_id, usuario_id, fecha_prestamo, fecha_devolucion, estado) VALUES (?, ?, ?, ?, ?)";
        
        try (Connection conn = ConexionSQLite.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setInt(1, prestamo.getLibroId());
            pstmt.setInt(2, prestamo.getUsuarioId());
            pstmt.setString(3, prestamo.getFechaPrestamo().toString());
            pstmt.setString(4, prestamo.getFechaDevolucion().toString());
            pstmt.setString(5, prestamo.getEstado());
            
            return pstmt.executeUpdate() > 0;
            
        } catch (SQLException e) {
            System.err.println("Error realizando préstamo: " + e.getMessage());
            return false;
        }
    }
    
    public boolean registrarDevolucion(int prestamoId) {
        String sql = "UPDATE prestamos SET estado = 'DEVUELTO', fecha_devolucion_real = ? WHERE id = ?";
        
        try (Connection conn = ConexionSQLite.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setString(1, LocalDate.now().toString());
            pstmt.setInt(2, prestamoId);
            
            return pstmt.executeUpdate() > 0;
            
        } catch (SQLException e) {
            System.err.println("Error registrando devolución: " + e.getMessage());
            return false;
        }
    }
    
    public List<Prestamo> obtenerPrestamosActivos() {
        List<Prestamo> prestamos = new ArrayList<>();
        String sql = "SELECT * FROM prestamos WHERE estado = 'ACTIVO' ORDER BY fecha_devolucion";
        
        try (Connection conn = ConexionSQLite.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            
            while (rs.next()) {
                Prestamo prestamo = resultSetAPrestamo(rs);
                prestamos.add(prestamo);
            }
            
        } catch (SQLException e) {
            System.err.println("Error obteniendo préstamos activos: " + e.getMessage());
        }
        
        return prestamos;
    }
    
    public List<Prestamo> obtenerTodosLosPrestamos() {
        List<Prestamo> prestamos = new ArrayList<>();
        String sql = "SELECT * FROM prestamos ORDER BY fecha_prestamo DESC";
        
        try (Connection conn = ConexionSQLite.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            
            while (rs.next()) {
                Prestamo prestamo = resultSetAPrestamo(rs);
                prestamos.add(prestamo);
            }
            
        } catch (SQLException e) {
            System.err.println("Error obteniendo préstamos: " + e.getMessage());
        }
        
        return prestamos;
    }
    
    public int contarPrestamosActivos() {
        String sql = "SELECT COUNT(*) as total FROM prestamos WHERE estado = 'ACTIVO'";
        
        try (Connection conn = ConexionSQLite.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            
            if (rs.next()) {
                return rs.getInt("total");
            }
            
        } catch (SQLException e) {
            System.err.println("Error contando préstamos activos: " + e.getMessage());
        }
        
        return 0;
    }
    
    public int contarPrestamosAtrasados() {
        String sql = "SELECT COUNT(*) as total FROM prestamos WHERE estado = 'ACTIVO' AND fecha_devolucion < ?";
        
        try (Connection conn = ConexionSQLite.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setString(1, LocalDate.now().toString());
            ResultSet rs = pstmt.executeQuery();
            
            if (rs.next()) {
                return rs.getInt("total");
            }
            
        } catch (SQLException e) {
            System.err.println("Error contando préstamos atrasados: " + e.getMessage());
        }
        
        return 0;
    }
    
    private Prestamo resultSetAPrestamo(ResultSet rs) throws SQLException {
        Prestamo prestamo = new Prestamo();
        prestamo.setId(rs.getInt("id"));
        prestamo.setLibroId(rs.getInt("libro_id"));
        prestamo.setUsuarioId(rs.getInt("usuario_id"));
        prestamo.setFechaPrestamo(LocalDate.parse(rs.getString("fecha_prestamo")));
        prestamo.setFechaDevolucion(LocalDate.parse(rs.getString("fecha_devolucion")));
        
        if (rs.getString("fecha_devolucion_real") != null) {
            prestamo.setFechaDevolucionReal(LocalDate.parse(rs.getString("fecha_devolucion_real")));
        }
        
        prestamo.setEstado(rs.getString("estado"));
        return prestamo;
    }
}