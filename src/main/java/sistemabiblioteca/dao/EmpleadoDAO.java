package sistemabiblioteca.dao;

import sistemabiblioteca.bd.ConexionSQLite;
import sistemabiblioteca.modelo.Empleado;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class EmpleadoDAO {
    private Connection connection;
    
    public EmpleadoDAO() {
        this.connection = null;
    }
    
    public EmpleadoDAO(Connection testConnection) {
        this.connection = testConnection;
    }
    
    private Connection getConnection() throws SQLException {
        if (this.connection != null) {
            return this.connection;
        }
        return ConexionSQLite.getConnection();
    }
    
    public boolean insertarEmpleado(Empleado empleado) {
        String sql = "INSERT INTO empleados (nombre, apellido_paterno, apellido_materno, " +
                    "usuario, password, rol, telefono, email) VALUES (?, ?, ?, ?, ?, ?, ?, ?)";
        
        PreparedStatement stmt = null;
        
        try {
            Connection conn = getConnection();
            stmt = conn.prepareStatement(sql);
            stmt.setString(1, empleado.getNombre());
            stmt.setString(2, empleado.getApellidoPaterno());
            stmt.setString(3, empleado.getApellidoMaterno());
            stmt.setString(4, empleado.getUsuario());
            stmt.setString(5, empleado.getPassword());
            stmt.setString(6, empleado.getRol());
            stmt.setString(7, empleado.getTelefono());
            stmt.setString(8, empleado.getEmail());
            
            return stmt.executeUpdate() > 0;
            
        } catch (SQLException e) {
            System.err.println("Error al insertar empleado: " + e.getMessage());
            return false;
        } finally {
            if (stmt != null) {
                try { stmt.close(); } catch (SQLException e) { }
            }
        }
    }
    
    public List<Empleado> obtenerTodosLosEmpleados() {
        List<Empleado> empleados = new ArrayList<>();
        String sql = "SELECT * FROM empleados ORDER BY id";
        
        Statement stmt = null;
        ResultSet rs = null;
        
        try {
            Connection conn = getConnection();
            stmt = conn.createStatement();
            rs = stmt.executeQuery(sql);
            
            while (rs.next()) {
                empleados.add(crearEmpleadoDesdeResultSet(rs));
            }
            
        } catch (SQLException e) {
            System.err.println("Error al obtener empleados: " + e.getMessage());
        } finally {
            if (rs != null) {
                try { rs.close(); } catch (SQLException e) { }
            }
            if (stmt != null) {
                try { stmt.close(); } catch (SQLException e) { }
            }
        }
        
        return empleados;
    }
    
    public Empleado obtenerEmpleadoPorId(int id) {
        String sql = "SELECT * FROM empleados WHERE id = ?";
        
        PreparedStatement stmt = null;
        ResultSet rs = null;
        
        try {
            Connection conn = getConnection();
            stmt = conn.prepareStatement(sql);
            stmt.setInt(1, id);
            rs = stmt.executeQuery();
            
            if (rs.next()) {
                return crearEmpleadoDesdeResultSet(rs);
            }
            
        } catch (SQLException e) {
            System.err.println("Error al obtener empleado por ID: " + e.getMessage());
        } finally {
            if (rs != null) {
                try { rs.close(); } catch (SQLException e) { }
            }
            if (stmt != null) {
                try { stmt.close(); } catch (SQLException e) { }
            }
        }
        
        return null;
    }
    
    public boolean actualizarEmpleado(Empleado empleado) {
        String sql = "UPDATE empleados SET nombre = ?, apellido_paterno = ?, apellido_materno = ?, " +
                    "usuario = ?, rol = ?, telefono = ?, email = ?,  WHERE id = ?";
        
        PreparedStatement stmt = null;
        
        try {
            Connection conn = getConnection();
            stmt = conn.prepareStatement(sql);
            stmt.setString(1, empleado.getNombre());
            stmt.setString(2, empleado.getApellidoPaterno());
            stmt.setString(3, empleado.getApellidoMaterno());
            stmt.setString(4, empleado.getUsuario());
            stmt.setString(5, empleado.getRol());
            stmt.setString(6, empleado.getTelefono());
            stmt.setString(7, empleado.getEmail());
            stmt.setInt(9, empleado.getId());
            
            return stmt.executeUpdate() > 0;
            
        } catch (SQLException e) {
            System.err.println("Error al actualizar empleado: " + e.getMessage());
            return false;
        } finally {
            if (stmt != null) {
                try { stmt.close(); } catch (SQLException e) { }
            }
        }
    }
    
    public boolean eliminarEmpleado(int id) {
        String sql = "DELETE FROM empleados WHERE id = ?";
        
        PreparedStatement stmt = null;
        
        try {
            Connection conn = getConnection();
            stmt = conn.prepareStatement(sql);
            stmt.setInt(1, id);
            return stmt.executeUpdate() > 0;
            
        } catch (SQLException e) {
            System.err.println("Error al eliminar empleado: " + e.getMessage());
            return false;
        } finally {
            if (stmt != null) {
                try { stmt.close(); } catch (SQLException e) { }
            }
        }
    }
    
    public Empleado autenticar(String usuario, String password) {
        String sql = "SELECT * FROM empleados WHERE usuario = ? AND password = ?";
        
        PreparedStatement stmt = null;
        ResultSet rs = null;
        
        try {
            Connection conn = getConnection();
            stmt = conn.prepareStatement(sql);
            stmt.setString(1, usuario);
            stmt.setString(2, password);
            rs = stmt.executeQuery();
            
            if (rs.next()) {
                return crearEmpleadoDesdeResultSet(rs);
            }
            
        } catch (SQLException e) {
            System.err.println("Error en autenticación: " + e.getMessage());
        } finally {
            if (rs != null) {
                try { rs.close(); } catch (SQLException e) { }
            }
            if (stmt != null) {
                try { stmt.close(); } catch (SQLException e) { }
            }
        }
        
        return null;
    }
    
    public boolean existeUsuario(String usuario) {
        String sql = "SELECT COUNT(*) as count FROM empleados WHERE usuario = ?";
        
        PreparedStatement stmt = null;
        ResultSet rs = null;
        
        try {
            Connection conn = getConnection();
            stmt = conn.prepareStatement(sql);
            stmt.setString(1, usuario);
            rs = stmt.executeQuery();
            
            if (rs.next()) {
                return rs.getInt("count") > 0;
            }
            
        } catch (SQLException e) {
            System.err.println("Error al verificar usuario: " + e.getMessage());
        } finally {
            if (rs != null) {
                try { rs.close(); } catch (SQLException e) { }
            }
            if (stmt != null) {
                try { stmt.close(); } catch (SQLException e) { }
            }
        }
        
        return false;
    }
    
    public int contarTotalEmpleados() {
        String sql = "SELECT COUNT(*) as total FROM empleados";
        
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
            System.err.println("Error al contar empleados: " + e.getMessage());
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
    
    private Empleado crearEmpleadoDesdeResultSet(ResultSet rs) throws SQLException {
        Empleado empleado = new Empleado();
        empleado.setId(rs.getInt("id"));
        empleado.setNombre(rs.getString("nombre"));
        empleado.setApellidoPaterno(rs.getString("apellido_paterno"));
        empleado.setApellidoMaterno(rs.getString("apellido_materno"));
        empleado.setUsuario(rs.getString("usuario"));
        empleado.setPassword(rs.getString("password"));
        empleado.setRol(rs.getString("rol"));
        empleado.setTelefono(rs.getString("telefono"));
        empleado.setEmail(rs.getString("email"));
        return empleado;
    }
}