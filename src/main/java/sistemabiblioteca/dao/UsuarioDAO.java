package sistemabiblioteca.dao;

import sistemabiblioteca.bd.ConexionSQLite;
import sistemabiblioteca.modelo.Usuario;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class UsuarioDAO {
    
    public boolean insertarUsuario(Usuario usuario) {
        String sql = "INSERT INTO usuarios (nombre, apellido_paterno, apellido_materno, domicilio, telefono, sanciones, monto_sancion) VALUES (?, ?, ?, ?, ?, ?, ?)";
        
        try (Connection conn = ConexionSQLite.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setString(1, usuario.getNombre());
            pstmt.setString(2, usuario.getApellidoPaterno());
            pstmt.setString(3, usuario.getApellidoMaterno());
            pstmt.setString(4, usuario.getDomicilio());
            pstmt.setString(5, usuario.getTelefono());
            pstmt.setInt(6, usuario.getSanciones());
            pstmt.setInt(7, usuario.getMontoSancion());
            
            return pstmt.executeUpdate() > 0;
            
        } catch (SQLException e) {
            System.err.println("Error insertando usuario: " + e.getMessage());
            return false;
        }
    }
    
    public Usuario obtenerUsuarioPorId(int id) {
        String sql = "SELECT * FROM usuarios WHERE id=?";
        
        try (Connection conn = ConexionSQLite.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setInt(1, id);
            ResultSet rs = pstmt.executeQuery();
            
            if (rs.next()) {
                return resultSetAUsuario(rs);
            }
            
        } catch (SQLException e) {
            System.err.println("Error obteniendo usuario por ID: " + e.getMessage());
        }
        
        return null;
    }
    
    public List<Usuario> obtenerTodosLosUsuarios() {
        List<Usuario> usuarios = new ArrayList<>();
        String sql = "SELECT * FROM usuarios ORDER BY nombre, apellido_paterno";
        
        try (Connection conn = ConexionSQLite.getConnection();
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
    
    public boolean actualizarUsuario(Usuario usuario) {
        String sql = "UPDATE usuarios SET nombre=?, apellido_paterno=?, apellido_materno=?, domicilio=?, telefono=?, sanciones=?, monto_sancion=? WHERE id=?";
        
        try (Connection conn = ConexionSQLite.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
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
            System.err.println("Error actualizando usuario: " + e.getMessage());
            return false;
        }
    }
    
    public boolean eliminarUsuario(int id) {
        String sql = "DELETE FROM usuarios WHERE id=?";
        
        try (Connection conn = ConexionSQLite.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setInt(1, id);
            return pstmt.executeUpdate() > 0;
            
        } catch (SQLException e) {
            System.err.println("Error eliminando usuario: " + e.getMessage());
            return false;
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
            case "ID":  // ← NUEVO CASO PARA BUSCAR POR ID
                sql = "SELECT * FROM usuarios WHERE id = ? ORDER BY nombre, apellido_paterno";
                break;
            default:
                sql = "SELECT * FROM usuarios WHERE nombre LIKE ? ORDER BY nombre, apellido_paterno";
        }
        
        try (Connection conn = ConexionSQLite.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            // Para ID usamos búsqueda exacta, para los otros usamos LIKE
            if ("ID".equals(criterio)) {
                try {
                    pstmt.setInt(1, Integer.parseInt(valor)); // ← Búsqueda exacta por ID
                } catch (NumberFormatException e) {
                    System.err.println("Error: ID debe ser un número válido");
                    return usuarios; // Retorna lista vacía si el ID no es número
                }
            } else {
                pstmt.setString(1, "%" + valor + "%");    // ← Búsqueda parcial para texto
            }
            
            ResultSet rs = pstmt.executeQuery();
            
            while (rs.next()) {
                Usuario usuario = resultSetAUsuario(rs);
                usuarios.add(usuario);
            }
            
        } catch (SQLException e) {
            System.err.println("Error buscando usuarios: " + e.getMessage());
        }
        
        return usuarios;
    }
    
    public int contarTotalUsuarios() {
        String sql = "SELECT COUNT(*) as total FROM usuarios";
        
        try (Connection conn = ConexionSQLite.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            
            if (rs.next()) {
                return rs.getInt("total");
            }
            
        } catch (SQLException e) {
            System.err.println("Error contando usuarios: " + e.getMessage());
        }
        
        return 0;
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
        return usuario;
    }
}