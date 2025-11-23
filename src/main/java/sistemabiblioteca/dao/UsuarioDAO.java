package sistemabiblioteca.dao;

import sistemabiblioteca.bd.ConexionSQLite;
import sistemabiblioteca.modelo.Usuario;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class UsuarioDAO {
    private Connection connection;
    
    public UsuarioDAO() {
        this.connection = null;
    }
    
    public UsuarioDAO(Connection testConnection) {
        this.connection = testConnection;
    }
    
    private Connection getConnection() throws SQLException {
        if (this.connection != null) {
            return this.connection;
        }
        return ConexionSQLite.getConnection();
    }
    
    public boolean insertarUsuario(Usuario usuario) {
        String sql = "INSERT INTO usuarios (nombre, apellido_paterno, apellido_materno, domicilio, telefono, sanciones, monto_sancion, empleado_id) VALUES (?, ?, ?, ?, ?, ?, ?, ?)";
        PreparedStatement pstmt = null;
        
        try {
            Connection conn = getConnection();
            boolean autoCommit = conn.getAutoCommit();
            pstmt = conn.prepareStatement(sql);
            
            pstmt.setString(1, usuario.getNombre());
            pstmt.setString(2, usuario.getApellidoPaterno());
            pstmt.setString(3, usuario.getApellidoMaterno());
            pstmt.setString(4, usuario.getDomicilio());
            pstmt.setString(5, usuario.getTelefono());
            pstmt.setInt(6, usuario.getSanciones());
            pstmt.setInt(7, usuario.getMontoSancion());
            pstmt.setInt(8, usuario.getEmpleadoId());
            
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
    
    public Usuario obtenerUsuarioPorId(int id) {
        String sql = "SELECT * FROM usuarios WHERE id=?";
        PreparedStatement pstmt = null;
        ResultSet rs = null;
        
        try {
            Connection conn = getConnection();
            pstmt = conn.prepareStatement(sql);
            pstmt.setInt(1, id);
            rs = pstmt.executeQuery();
            
            if (rs.next()) {
                return resultSetAUsuario(rs);
            }
            
        } catch (SQLException e) {
            System.err.println("Error obteniendo usuario por ID: " + e.getMessage());
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
    
    public boolean actualizarUsuario(Usuario usuario) {
        String sql = "UPDATE usuarios SET nombre=?, apellido_paterno=?, apellido_materno=?, domicilio=?, telefono=?, sanciones=?, monto_sancion=? WHERE id=?";
        PreparedStatement pstmt = null;
        
        try {
            Connection conn = getConnection();
            pstmt = conn.prepareStatement(sql);
            
            pstmt.setString(1, usuario.getNombre());
            pstmt.setString(2, usuario.getApellidoPaterno());
            pstmt.setString(3, usuario.getApellidoMaterno());
            pstmt.setString(4, usuario.getDomicilio());
            pstmt.setString(5, usuario.getTelefono());
            pstmt.setInt(6, usuario.getSanciones());
            pstmt.setInt(7, usuario.getMontoSancion());
            pstmt.setInt(8, usuario.getId());
            
            return pstmt.executeUpdate() > 0;
            
        } catch (SQLException e) {
            return false;
        } finally {
            if (pstmt != null) {
                try { pstmt.close(); } catch (SQLException e) { }
            }
        }
    }
    
    public boolean eliminarUsuario(int id) {
        String sql = "DELETE FROM usuarios WHERE id=?";
        PreparedStatement pstmt = null;
        
        try {
            Connection conn = getConnection();
            pstmt = conn.prepareStatement(sql);
            pstmt.setInt(1, id);
            
            return pstmt.executeUpdate() > 0;
            
        } catch (SQLException e) {
            return false;
        } finally {
            if (pstmt != null) {
                try { pstmt.close(); } catch (SQLException e) { }
            }
        }
    }
    
    public List<Usuario> buscarUsuarios(String criterio, String valor) {
        List<Usuario> usuarios = new ArrayList<>();
        String sql = "";
        
        switch (criterio) {
            case "Nombre":
                sql = "SELECT * FROM usuarios WHERE nombre LIKE ? ORDER BY nombre, apellido_paterno";
                break;
            case "Apellido Paterno":
                sql = "SELECT * FROM usuarios WHERE apellido_paterno LIKE ? ORDER BY nombre, apellido_paterno";
                break;
            case "Teléfono":
                sql = "SELECT * FROM usuarios WHERE telefono LIKE ? ORDER BY nombre, apellido_paterno";
                break;
            case "ID":
                sql = "SELECT * FROM usuarios WHERE id = ? ORDER BY nombre, apellido_paterno";
                break;
            default:
                sql = "SELECT * FROM usuarios WHERE nombre LIKE ? ORDER BY nombre, apellido_paterno";
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
                    return usuarios;
                }
            } else {
                pstmt.setString(1, "%" + valor + "%");
            }
            
            rs = pstmt.executeQuery();
            
            while (rs.next()) {
                Usuario usuario = resultSetAUsuario(rs);
                usuarios.add(usuario);
            }
            
        } catch (SQLException e) {
            System.err.println("Error buscando usuarios: " + e.getMessage());
        } finally {
            if (rs != null) {
                try { rs.close(); } catch (SQLException e) { }
            }
            if (pstmt != null) {
                try { pstmt.close(); } catch (SQLException e) { }
            }
        }
        
        return usuarios;
    }
    
    public int contarTotalUsuarios() {
        String sql = "SELECT COUNT(*) as total FROM usuarios";
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
            System.err.println("Error contando usuarios: " + e.getMessage());
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
    
    public List<Usuario> obtenerTodosLosUsuarios(String orden) {
        List<Usuario> usuarios = new ArrayList<>();
        
        // Validar el criterio de orden para prevenir SQL injection
        String ordenValido = validarOrden(orden);
        String sql = "SELECT * FROM usuarios ORDER BY " + ordenValido;
        
        try (Connection conn = getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            
            while (rs.next()) {
                Usuario usuario = resultSetAUsuario(rs);
                usuarios.add(usuario);
            }
            
        } catch (SQLException e) {
            System.err.println("Error obteniendo usuarios: " + e.getMessage());
        }
        
        return usuarios;
    }
    
    // Método para validar el criterio de orden (seguridad)
    private String validarOrden(String orden) {
        // Lista blanca de criterios permitidos
        String[] criteriosPermitidos = {
            "id", "nombre", "apellido_paterno", "apellido_materno", 
            "telefono", "sanciones", "monto_sancion", "empleado_id",
            "sanciones DESC", "monto_sancion DESC"
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
    public List<Usuario> obtenerTodosLosUsuarios() {
        return obtenerTodosLosUsuarios("id");
    }
    
    private Usuario resultSetAUsuario(ResultSet rs) throws SQLException {
        Usuario usuario = new Usuario();
        usuario.setId(rs.getInt("id"));
        usuario.setNombre(rs.getString("nombre"));
        usuario.setApellidoPaterno(rs.getString("apellido_paterno"));
        usuario.setApellidoMaterno(rs.getString("apellido_materno"));
        usuario.setDomicilio(rs.getString("domicilio"));
        usuario.setTelefono(rs.getString("telefono"));
        usuario.setSanciones(rs.getInt("sanciones"));
        usuario.setMontoSancion(rs.getInt("monto_sancion"));
        usuario.setEmpleadoId(rs.getInt("empleado_id"));
        return usuario;
    }
}