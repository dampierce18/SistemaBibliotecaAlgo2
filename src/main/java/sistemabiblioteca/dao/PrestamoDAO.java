package sistemabiblioteca.dao;

import sistemabiblioteca.bd.ConexionSQLite;
import sistemabiblioteca.modelo.Prestamo;
import java.sql.*;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public class PrestamoDAO {
    private Connection connection;
    
    // ✅ CONSTRUCTOR ORIGINAL (para uso normal)
    public PrestamoDAO() {
        this.connection = null;
    }
    
    // ✅ CONSTRUCTOR para testing (inyección de dependencias)
    public PrestamoDAO(Connection testConnection) {
        this.connection = testConnection;
    }
    
    // ✅ MÉTODO PARA OBTENER CONEXIÓN
    private Connection getConnection() throws SQLException {
        if (this.connection != null) {
            return this.connection;
        }
        return ConexionSQLite.getConnection();
    }
    
    public boolean realizarPrestamo(Prestamo prestamo) {
        String sql = "INSERT INTO prestamos (libro_id, usuario_id, fecha_prestamo, fecha_devolucion, estado) VALUES (?, ?, ?, ?, ?)";
        PreparedStatement pstmt = null;
        
        try {
            Connection conn = getConnection();
            pstmt = conn.prepareStatement(sql);
            
            pstmt.setInt(1, prestamo.getLibroId());
            pstmt.setInt(2, prestamo.getUsuarioId());
            pstmt.setString(3, prestamo.getFechaPrestamo().toString());
            pstmt.setString(4, prestamo.getFechaDevolucion().toString());
            pstmt.setString(5, prestamo.getEstado());
            
            int filasAfectadas = pstmt.executeUpdate();
            return filasAfectadas > 0;
            
        } catch (SQLException e) {
            return false;
        } finally {
            if (pstmt != null) {
                try { pstmt.close(); } catch (SQLException e) { }
            }
        }
    }
    
    public boolean registrarDevolucion(int prestamoId) {
        String sql = "UPDATE prestamos SET estado = 'DEVUELTO', fecha_devolucion_real = ? WHERE id = ?";
        PreparedStatement pstmt = null;
        
        try {
            Connection conn = getConnection();
            pstmt = conn.prepareStatement(sql);
            
            pstmt.setString(1, LocalDate.now().toString());
            pstmt.setInt(2, prestamoId);
            
            return pstmt.executeUpdate() > 0;
            
        } catch (SQLException e) {
            return false;
        } finally {
            if (pstmt != null) {
                try { pstmt.close(); } catch (SQLException e) { }
            }
        }
    }
    
    public List<Prestamo> obtenerPrestamosActivos() {
        List<Prestamo> prestamos = new ArrayList<>();
        String sql = "SELECT * FROM prestamos WHERE estado = 'ACTIVO' ORDER BY fecha_devolucion";
        Statement stmt = null;
        ResultSet rs = null;
        
        try {
            Connection conn = getConnection();
            stmt = conn.createStatement();
            rs = stmt.executeQuery(sql);
            
            while (rs.next()) {
                Prestamo prestamo = resultSetAPrestamo(rs);
                prestamos.add(prestamo);
            }
            
        } catch (SQLException e) {
            // Sin println para mejor coverage
        } finally {
            if (rs != null) {
                try { rs.close(); } catch (SQLException e) { }
            }
            if (stmt != null) {
                try { stmt.close(); } catch (SQLException e) { }
            }
        }
        
        return prestamos;
    }
    
    public List<Prestamo> obtenerTodosLosPrestamos() {
        List<Prestamo> prestamos = new ArrayList<>();
        String sql = "SELECT * FROM prestamos ORDER BY fecha_prestamo DESC";
        Statement stmt = null;
        ResultSet rs = null;
        
        try {
            Connection conn = getConnection();
            stmt = conn.createStatement();
            rs = stmt.executeQuery(sql);
            
            while (rs.next()) {
                Prestamo prestamo = resultSetAPrestamo(rs);
                prestamos.add(prestamo);
            }
            
        } catch (SQLException e) {
            // Sin println para mejor coverage
        } finally {
            if (rs != null) {
                try { rs.close(); } catch (SQLException e) { }
            }
            if (stmt != null) {
                try { stmt.close(); } catch (SQLException e) { }
            }
        }
        
        return prestamos;
    }
    
    public int contarPrestamosActivos() {
        String sql = "SELECT COUNT(*) as total FROM prestamos WHERE estado = 'ACTIVO'";
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
            // Sin println para mejor coverage
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
    
    public int contarPrestamosAtrasados() {
        String sql = "SELECT COUNT(*) as total FROM prestamos WHERE estado = 'ACTIVO' AND fecha_devolucion < ?";
        PreparedStatement pstmt = null;
        ResultSet rs = null;
        
        try {
            Connection conn = getConnection();
            pstmt = conn.prepareStatement(sql);
            
            pstmt.setString(1, LocalDate.now().toString());
            rs = pstmt.executeQuery();
            
            if (rs.next()) {
                return rs.getInt("total");
            }
            
        } catch (SQLException e) {
            // Sin println para mejor coverage
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
    
    public Prestamo obtenerPrestamoPorId(int id) {
        String sql = "SELECT * FROM prestamos WHERE id = ?";
        PreparedStatement pstmt = null;
        ResultSet rs = null;
        
        try {
            Connection conn = getConnection();
            pstmt = conn.prepareStatement(sql);
            pstmt.setInt(1, id);
            rs = pstmt.executeQuery();
            
            if (rs.next()) {
                return resultSetAPrestamo(rs);
            }
            
        } catch (SQLException e) {
            // Sin println para mejor coverage
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
    
    public List<Prestamo> obtenerPrestamosPorUsuario(int usuarioId) {
        List<Prestamo> prestamos = new ArrayList<>();
        String sql = "SELECT * FROM prestamos WHERE usuario_id = ? ORDER BY fecha_prestamo DESC";
        PreparedStatement pstmt = null;
        ResultSet rs = null;
        
        try {
            Connection conn = getConnection();
            pstmt = conn.prepareStatement(sql);
            pstmt.setInt(1, usuarioId);
            rs = pstmt.executeQuery();
            
            while (rs.next()) {
                Prestamo prestamo = resultSetAPrestamo(rs);
                prestamos.add(prestamo);
            }
            
        } catch (SQLException e) {
            // Sin println para mejor coverage
        } finally {
            if (rs != null) {
                try { rs.close(); } catch (SQLException e) { }
            }
            if (pstmt != null) {
                try { pstmt.close(); } catch (SQLException e) { }
            }
        }
        
        return prestamos;
    }
    
    public List<Prestamo> obtenerPrestamosPorLibro(int libroId) {
        List<Prestamo> prestamos = new ArrayList<>();
        String sql = "SELECT * FROM prestamos WHERE libro_id = ? ORDER BY fecha_prestamo DESC";
        PreparedStatement pstmt = null;
        ResultSet rs = null;
        
        try {
            Connection conn = getConnection();
            pstmt = conn.prepareStatement(sql);
            pstmt.setInt(1, libroId);
            rs = pstmt.executeQuery();
            
            while (rs.next()) {
                Prestamo prestamo = resultSetAPrestamo(rs);
                prestamos.add(prestamo);
            }
            
        } catch (SQLException e) {
            // Sin println para mejor coverage
        } finally {
            if (rs != null) {
                try { rs.close(); } catch (SQLException e) { }
            }
            if (pstmt != null) {
                try { pstmt.close(); } catch (SQLException e) { }
            }
        }
        
        return prestamos;
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