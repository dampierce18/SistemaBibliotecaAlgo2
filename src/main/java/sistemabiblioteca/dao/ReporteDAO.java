package sistemabiblioteca.dao;

import sistemabiblioteca.bd.ConexionSQLite;
import java.sql.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ReporteDAO {
    private Connection connection;
    
    public ReporteDAO() {
        this.connection = null;
    }
    
    public ReporteDAO(Connection testConnection) {
        this.connection = testConnection;
    }
    
    private Connection getConnection() throws SQLException {
        if (this.connection != null) {
            return this.connection;
        }
        return ConexionSQLite.getConnection();
    }
    
    public Map<String, Integer> obtenerResumenMes() {
        Map<String, Integer> resumen = new HashMap<>();
        
        String sqlPrestamosMes = """
            SELECT COUNT(*) as total 
            FROM prestamos 
            WHERE strftime('%Y-%m', fecha_prestamo) = strftime('%Y-%m', 'now')
            """;
            
        String sqlPrestamosActivos = "SELECT COUNT(*) as total FROM prestamos WHERE estado = 'ACTIVO'";
        String sqlPrestamosAtrasados = "SELECT COUNT(*) as total FROM prestamos WHERE estado = 'ACTIVO' AND fecha_devolucion < date('now')";
        String sqlUsuariosSancionados = "SELECT COUNT(*) as total FROM usuarios WHERE sanciones > 0";
        String sqlMultasPendientes = "SELECT COALESCE(SUM(monto_sancion), 0) as total FROM usuarios WHERE monto_sancion > 0";
        
        Statement stmt = null;
        ResultSet rs = null;
        
        try {
            Connection conn = getConnection();
            stmt = conn.createStatement();
            
            rs = stmt.executeQuery(sqlPrestamosMes);
            resumen.put("prestamos_mes", rs.getInt("total"));
            
            rs = stmt.executeQuery(sqlPrestamosActivos);
            resumen.put("prestamos_activos", rs.getInt("total"));
            
            rs = stmt.executeQuery(sqlPrestamosAtrasados);
            resumen.put("prestamos_atrasados", rs.getInt("total"));
            
            rs = stmt.executeQuery(sqlUsuariosSancionados);
            resumen.put("usuarios_sancionados", rs.getInt("total"));
            
            rs = stmt.executeQuery(sqlMultasPendientes);
            resumen.put("multas_pendientes", rs.getInt("total"));
            
        } catch (SQLException e) {
        } finally {
            if (rs != null) {
                try { rs.close(); } catch (SQLException e) { }
            }
            if (stmt != null) {
                try { stmt.close(); } catch (SQLException e) { }
            }
        }
        
        return resumen;
    }
    
    public List<Object[]> obtenerLibrosMasPrestados() {
        List<Object[]> resultados = new ArrayList<>();
        
        String sql = """
            SELECT l.id, l.titulo, l.autor, l.disponibles, 
                   COUNT(p.id) as total_prestamos
            FROM libros l
            LEFT JOIN prestamos p ON l.id = p.libro_id 
                AND strftime('%Y-%m', p.fecha_prestamo) = strftime('%Y-%m', 'now')
            GROUP BY l.id, l.titulo, l.autor, l.disponibles
            ORDER BY total_prestamos DESC, l.titulo
            LIMIT 10
            """;
            
        Statement stmt = null;
        ResultSet rs = null;
        
        try {
            Connection conn = getConnection();
            stmt = conn.createStatement();
            rs = stmt.executeQuery(sql);
            
            int posicion = 1;
            while (rs.next()) {
                Object[] fila = {
                    posicion++,
                    rs.getString("titulo"),
                    rs.getString("autor"), 
                    rs.getInt("total_prestamos"),
                    rs.getInt("disponibles")
                };
                resultados.add(fila);
            }
            
        } catch (SQLException e) {
        } finally {
            if (rs != null) {
                try { rs.close(); } catch (SQLException e) { }
            }
            if (stmt != null) {
                try { stmt.close(); } catch (SQLException e) { }
            }
        }
        
        return resultados;
    }
    
    public List<Object[]> obtenerUsuariosMasActivos() {
        List<Object[]> resultados = new ArrayList<>();
        
        String sql = """
            SELECT u.id, u.nombre, u.apellido_paterno, u.sanciones,
                   COUNT(p.id) as total_prestamos,
                   SUM(CASE WHEN p.estado = 'ACTIVO' AND p.fecha_devolucion < date('now') THEN 1 ELSE 0 END) as atrasos
            FROM usuarios u
            LEFT JOIN prestamos p ON u.id = p.usuario_id 
                AND strftime('%Y-%m', p.fecha_prestamo) = strftime('%Y-%m', 'now')
            GROUP BY u.id, u.nombre, u.apellido_paterno, u.sanciones
            ORDER BY total_prestamos DESC, u.nombre
            LIMIT 10
            """;
            
        Statement stmt = null;
        ResultSet rs = null;
        
        try {
            Connection conn = getConnection();
            stmt = conn.createStatement();
            rs = stmt.executeQuery(sql);
            
            int posicion = 1;
            while (rs.next()) {
                String nombreCompleto = rs.getString("nombre") + " " + rs.getString("apellido_paterno");
                Object[] fila = {
                    posicion++,
                    nombreCompleto,
                    rs.getInt("total_prestamos"),
                    rs.getInt("atrasos"),
                    rs.getInt("sanciones")
                };
                resultados.add(fila);
            }
            
        } catch (SQLException e) {
        } finally {
            if (rs != null) {
                try { rs.close(); } catch (SQLException e) { }
            }
            if (stmt != null) {
                try { stmt.close(); } catch (SQLException e) { }
            }
        }
        
        return resultados;
    }

    public List<Object[]> obtenerPrestamosPorMes() {
        List<Object[]> resultados = new ArrayList<>();
        
        String sql = """
            SELECT 
                strftime('%Y-%m', fecha_prestamo) as mes,
                COUNT(*) as total_prestamos,
                SUM(CASE WHEN estado = 'ACTIVO' THEN 1 ELSE 0 END) as prestamos_activos,
                SUM(CASE WHEN estado = 'ACTIVO' AND fecha_devolucion < date('now') THEN 1 ELSE 0 END) as atrasos,
                ROUND(100.0 * SUM(CASE WHEN estado = 'DEVUELTO' AND fecha_devolucion_real <= fecha_devolucion THEN 1 ELSE 0 END) / 
                      NULLIF(SUM(CASE WHEN estado = 'DEVUELTO' THEN 1 ELSE 0 END), 0), 1) as tasa_devolucion
            FROM prestamos
            WHERE fecha_prestamo >= date('now', '-6 months')
            GROUP BY mes
            ORDER BY mes DESC
            """;
            
        Statement stmt = null;
        ResultSet rs = null;
        
        try {
            Connection conn = getConnection();
            stmt = conn.createStatement();
            rs = stmt.executeQuery(sql);
            
            while (rs.next()) {
                String mes = rs.getString("mes");
                int totalPrestamos = rs.getInt("total_prestamos");
                int prestamosActivos = rs.getInt("prestamos_activos");
                int atrasos = rs.getInt("atrasos");
                String tasaDevolucion = rs.getString("tasa_devolucion") != null ? 
                                      rs.getString("tasa_devolucion") + "%" : "0%";
                
                Object[] fila = {mes, totalPrestamos, prestamosActivos, atrasos, tasaDevolucion};
                resultados.add(fila);
            }
            
        } catch (SQLException e) {
        } finally {
            if (rs != null) {
                try { rs.close(); } catch (SQLException e) { }
            }
            if (stmt != null) {
                try { stmt.close(); } catch (SQLException e) { }
            }
        }
        
        return resultados;
    }

    public List<Object[]> obtenerSituacionActual() {
        List<Object[]> resultados = new ArrayList<>();
        
        String sqlAtrasados = """
            SELECT 'Préstamo Atrasado' as tipo,
                   'Préstamo vencido sin devolver' as descripcion,
                   COUNT(*) as cantidad,
                   'URGENTE' as estado,
                   'Contactar usuario' as accion
            FROM prestamos 
            WHERE estado = 'ACTIVO' AND fecha_devolucion < date('now')
            """;
        
        String sqlSancionados = """
            SELECT 'Usuario Sancionado' as tipo,
                   'Usuario con sanción activa' as descripcion,
                   COUNT(*) as cantidad,
                   'ALTA' as estado,
                   'Verificar situación' as accion
            FROM usuarios 
            WHERE sanciones > 0
            """;
        
        String sqlSinDisponibles = """
            SELECT 'Sin Disponibles' as tipo,
                   'Libro sin ejemplares disponibles' as descripcion,
                   COUNT(*) as cantidad,
                   'MEDIA' as estado,
                   'Considerar compra' as accion
            FROM libros 
            WHERE disponibles = 0
            """;
        
        Statement stmt = null;
        ResultSet rs = null;
        
        try {
            Connection conn = getConnection();
            stmt = conn.createStatement();
            
            rs = stmt.executeQuery(sqlAtrasados);
            if (rs.next() && rs.getInt("cantidad") > 0) {
                Object[] fila = {
                    rs.getString("tipo"),
                    rs.getString("descripcion"),
                    rs.getInt("cantidad"),
                    rs.getString("estado"),
                    rs.getString("accion")
                };
                resultados.add(fila);
            }
            
            rs = stmt.executeQuery(sqlSancionados);
            if (rs.next() && rs.getInt("cantidad") > 0) {
                Object[] fila = {
                    rs.getString("tipo"),
                    rs.getString("descripcion"),
                    rs.getInt("cantidad"),
                    rs.getString("estado"),
                    rs.getString("accion")
                };
                resultados.add(fila);
            }
            
            rs = stmt.executeQuery(sqlSinDisponibles);
            if (rs.next() && rs.getInt("cantidad") > 0) {
                Object[] fila = {
                    rs.getString("tipo"),
                    rs.getString("descripcion"),
                    rs.getInt("cantidad"),
                    rs.getString("estado"),
                    rs.getString("accion")
                };
                resultados.add(fila);
            }
            
            if (resultados.isEmpty()) {
                Object[] fila = {
                    "Todo en Orden",
                    "No hay alertas pendientes",
                    0,
                    "OPTIMO",
                    "Mantener seguimiento"
                };
                resultados.add(fila);
            }
            
        } catch (SQLException e) {
        } finally {
            if (rs != null) {
                try { rs.close(); } catch (SQLException e) { }
            }
            if (stmt != null) {
                try { stmt.close(); } catch (SQLException e) { }
            }
        }
        
        return resultados;
    }
}