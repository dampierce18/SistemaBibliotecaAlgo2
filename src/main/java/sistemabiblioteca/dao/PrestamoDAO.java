package sistemabiblioteca.dao;

import sistemabiblioteca.bd.ConexionSQLite;
import sistemabiblioteca.modelo.Prestamo;
import java.sql.*;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public class PrestamoDAO {
    private Connection connection;
    
    public PrestamoDAO() {
        this.connection = null;
    }
    
    public PrestamoDAO(Connection testConnection) {
        this.connection = testConnection;
    }
    
    private Connection getConnection() throws SQLException {
        if (this.connection != null) {
            return this.connection;
        }
        return ConexionSQLite.getConnection();
    }
    
    public boolean realizarPrestamo(Prestamo prestamo) {
        String sql = "INSERT INTO prestamos (libro_id, usuario_id, empleado_id, fecha_prestamo, fecha_devolucion, estado) VALUES (?, ?, ?, ?, ?, ?)";
        PreparedStatement pstmt = null;
        
        try {
            Connection conn = getConnection();
            pstmt = conn.prepareStatement(sql);
            
            pstmt.setInt(1, prestamo.getLibroId());
            pstmt.setInt(2, prestamo.getUsuarioId());
            pstmt.setInt(3, prestamo.getEmpleadoId()); // ← ESTA ES LA CLAVE
            pstmt.setString(4, prestamo.getFechaPrestamo().toString());
            pstmt.setString(5, prestamo.getFechaDevolucion().toString());
            pstmt.setString(6, prestamo.getEstado());
            
            int filasAfectadas = pstmt.executeUpdate();
            return filasAfectadas > 0;
            
        } catch (SQLException e) {
            System.err.println("Error realizando préstamo: " + e.getMessage());
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
    
    public void actualizarPrestamosAtrasados() {
        String sql = "UPDATE prestamos SET estado = 'ATRASADO' WHERE estado = 'ACTIVO' AND fecha_devolucion < ?";
        PreparedStatement pstmt = null;
        
        try {
            Connection conn = getConnection();
            pstmt = conn.prepareStatement(sql);
            pstmt.setString(1, LocalDate.now().toString());
            pstmt.executeUpdate();
            
        } catch (SQLException e) {
            System.err.println("Error al actualizar préstamos atrasados: " + e.getMessage());
        } finally {
            if (pstmt != null) {
                try { pstmt.close(); } catch (SQLException e) { }
            }
        }
    }
    
    /**
     * Verifica y actualiza el estado de un préstamo específico
     */
    public void verificarEstadoPrestamo(int prestamoId) {
        String sql = "UPDATE prestamos SET estado = 'ATRASADO' WHERE id = ? AND estado = 'ACTIVO' AND fecha_devolucion < ?";
        PreparedStatement pstmt = null;
        
        try {
            Connection conn = getConnection();
            pstmt = conn.prepareStatement(sql);
            pstmt.setInt(1, prestamoId);
            pstmt.setString(2, LocalDate.now().toString());
            pstmt.executeUpdate();
            
        } catch (SQLException e) {
            System.err.println("Error al verificar estado del préstamo: " + e.getMessage());
        } finally {
            if (pstmt != null) {
                try { pstmt.close(); } catch (SQLException e) { }
            }
        }
    }
    
    // NUEVO: Método con ordenamiento para préstamos activos
    public List<Prestamo> obtenerPrestamosActivos(String orden) {
        // Primero actualizamos los préstamos atrasados
        actualizarPrestamosAtrasados();
        
        List<Prestamo> prestamos = new ArrayList<>();
        String ordenValido = validarOrdenPrestamos(orden);
        String sql = "SELECT * FROM prestamos WHERE estado IN ('ACTIVO', 'ATRASADO') ORDER BY " + ordenValido;
        
        try (Connection conn = getConnection();
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
    
    // NUEVO: Método con ordenamiento para todos los préstamos
    public List<Prestamo> obtenerTodosLosPrestamos(String orden) {
        List<Prestamo> prestamos = new ArrayList<>();
        String ordenValido = validarOrdenPrestamos(orden);
        String sql = "SELECT * FROM prestamos ORDER BY " + ordenValido;
        
        try (Connection conn = getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            
            while (rs.next()) {
                Prestamo prestamo = resultSetAPrestamo(rs);
                prestamos.add(prestamo);
            }
            
        } catch (SQLException e) {
            System.err.println("Error obteniendo todos los préstamos: " + e.getMessage());
        }
        
        return prestamos;
    }
    
    // Método para validar el criterio de orden (seguridad)
    private String validarOrdenPrestamos(String orden) {
        // Lista blanca de criterios permitidos para préstamos
        String[] criteriosPermitidos = {
            "id", "libro_id", "usuario_id", "empleado_id", "fecha_prestamo", "fecha_devolucion", 
            "fecha_devolucion_real", "estado", "fecha_prestamo DESC", 
            "fecha_devolucion DESC", "fecha_devolucion_real DESC"
        };
        
        for (String criterio : criteriosPermitidos) {
            if (criterio.equals(orden)) {
                return orden;
            }
        }
        
        // Si no es válido, usar orden por defecto
        return "id";
    }
    
    // Mantener métodos antiguos para compatibilidad
    public List<Prestamo> obtenerPrestamosActivos() {
        return obtenerPrestamosActivos("fecha_devolucion");
    }
    
    public List<Prestamo> obtenerTodosLosPrestamos() {
        return obtenerTodosLosPrestamos("fecha_prestamo DESC");
    }
    
    public int contarPrestamosActivos() {
        String sql = "SELECT COUNT(*) as total FROM prestamos WHERE estado = 'ACTIVO'";
        
        try (Connection conn = getConnection();
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
        String sql = "SELECT COUNT(*) as total FROM prestamos WHERE estado = 'ATRASADO'";
        
        try (Connection conn = getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            
            if (rs.next()) {
                return rs.getInt("total");
            }
            
        } catch (SQLException e) {
            System.err.println("Error contando préstamos atrasados: " + e.getMessage());
        }
        
        return 0;
    }
    
    public Prestamo obtenerPrestamoPorId(int id) {
        String sql = "SELECT * FROM prestamos WHERE id = ?";
        
        try (Connection conn = getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setInt(1, id);
            ResultSet rs = pstmt.executeQuery();
            
            if (rs.next()) {
                return resultSetAPrestamo(rs);
            }
            
        } catch (SQLException e) {
            System.err.println("Error obteniendo préstamo por ID: " + e.getMessage());
        }
        
        return null;
    }
    
    public List<Prestamo> obtenerPrestamosPorUsuario(int usuarioId) {
        return obtenerPrestamosPorUsuario(usuarioId, "fecha_prestamo DESC");
    }
    
    // NUEVO: Método con ordenamiento para préstamos por usuario
    public List<Prestamo> obtenerPrestamosPorUsuario(int usuarioId, String orden) {
        List<Prestamo> prestamos = new ArrayList<>();
        String ordenValido = validarOrdenPrestamos(orden);
        String sql = "SELECT * FROM prestamos WHERE usuario_id = ? ORDER BY " + ordenValido;
        
        try (Connection conn = getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setInt(1, usuarioId);
            ResultSet rs = pstmt.executeQuery();
            
            while (rs.next()) {
                Prestamo prestamo = resultSetAPrestamo(rs);
                prestamos.add(prestamo);
            }
            
        } catch (SQLException e) {
            System.err.println("Error obteniendo préstamos por usuario: " + e.getMessage());
        }
        
        return prestamos;
    }
    
    public List<Prestamo> obtenerPrestamosPorLibro(int libroId) {
        return obtenerPrestamosPorLibro(libroId, "fecha_prestamo DESC");
    }
    
    // NUEVO: Método con ordenamiento para préstamos por libro
    public List<Prestamo> obtenerPrestamosPorLibro(int libroId, String orden) {
        List<Prestamo> prestamos = new ArrayList<>();
        String ordenValido = validarOrdenPrestamos(orden);
        String sql = "SELECT * FROM prestamos WHERE libro_id = ? ORDER BY " + ordenValido;
        
        try (Connection conn = getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setInt(1, libroId);
            ResultSet rs = pstmt.executeQuery();
            
            while (rs.next()) {
                Prestamo prestamo = resultSetAPrestamo(rs);
                prestamos.add(prestamo);
            }
            
        } catch (SQLException e) {
            System.err.println("Error obteniendo préstamos por libro: " + e.getMessage());
        }
        
        return prestamos;
    }
    
    // NUEVO: Método para obtener préstamos por estado con ordenamiento
    public List<Prestamo> obtenerPrestamosPorEstado(String estado, String orden) {
        List<Prestamo> prestamos = new ArrayList<>();
        String ordenValido = validarOrdenPrestamos(orden);
        String sql = "SELECT * FROM prestamos WHERE estado = ? ORDER BY " + ordenValido;
        
        try (Connection conn = getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setString(1, estado);
            ResultSet rs = pstmt.executeQuery();
            
            while (rs.next()) {
                Prestamo prestamo = resultSetAPrestamo(rs);
                prestamos.add(prestamo);
            }
            
        } catch (SQLException e) {
            System.err.println("Error obteniendo préstamos por estado: " + e.getMessage());
        }
        
        return prestamos;
    }
    
    // NUEVO: Método para buscar préstamos con criterios múltiples
    public List<Prestamo> buscarPrestamos(Integer libroId, Integer usuarioId, String estado, String orden) {
        List<Prestamo> prestamos = new ArrayList<>();
        StringBuilder sql = new StringBuilder("SELECT * FROM prestamos WHERE 1=1");
        List<Object> parametros = new ArrayList<>();
        
        if (libroId != null) {
            sql.append(" AND libro_id = ?");
            parametros.add(libroId);
        }
        
        if (usuarioId != null) {
            sql.append(" AND usuario_id = ?");
            parametros.add(usuarioId);
        }
        
        if (estado != null && !estado.isEmpty()) {
            sql.append(" AND estado = ?");
            parametros.add(estado);
        }
        
        String ordenValido = validarOrdenPrestamos(orden);
        sql.append(" ORDER BY ").append(ordenValido);
        
        try (Connection conn = getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql.toString())) {
            
            for (int i = 0; i < parametros.size(); i++) {
                pstmt.setObject(i + 1, parametros.get(i));
            }
            
            ResultSet rs = pstmt.executeQuery();
            
            while (rs.next()) {
                Prestamo prestamo = resultSetAPrestamo(rs);
                prestamos.add(prestamo);
            }
            
        } catch (SQLException e) {
            System.err.println("Error buscando préstamos: " + e.getMessage());
        }
        
        return prestamos;
    }
    
    private Prestamo resultSetAPrestamo(ResultSet rs) throws SQLException {
        Prestamo prestamo = new Prestamo();
        prestamo.setId(rs.getInt("id"));
        prestamo.setLibroId(rs.getInt("libro_id"));
        prestamo.setUsuarioId(rs.getInt("usuario_id"));
        prestamo.setEmpleadoId(rs.getInt("empleado_id")); // ← Asegúrate de que esto esté
        prestamo.setFechaPrestamo(LocalDate.parse(rs.getString("fecha_prestamo")));
        prestamo.setFechaDevolucion(LocalDate.parse(rs.getString("fecha_devolucion")));
        
        if (rs.getString("fecha_devolucion_real") != null) {
            prestamo.setFechaDevolucionReal(LocalDate.parse(rs.getString("fecha_devolucion_real")));
        }
        
        prestamo.setEstado(rs.getString("estado"));
        return prestamo;
    }
}