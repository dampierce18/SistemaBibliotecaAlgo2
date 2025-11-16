package sistemabiblioteca.bd;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;

public class ConexionSQLite {
    private static final String URL = "jdbc:sqlite:biblioteca.db";
    
    public static Connection getConnection() throws SQLException {
        try {
            Class.forName("org.sqlite.JDBC");
            Connection conn = DriverManager.getConnection(URL);
            crearTablas(conn); // Crear tablas si no existen
            return conn;
        } catch (ClassNotFoundException e) {
            throw new SQLException("Driver SQLite no encontrado", e);
        }
    }
    
    private static void crearTablas(Connection conn) {
        String sqlLibros = """
            CREATE TABLE IF NOT EXISTS libros (
                id INTEGER PRIMARY KEY AUTOINCREMENT,
                titulo TEXT NOT NULL,
                anio TEXT,
                autor TEXT NOT NULL,
                categoria TEXT,
                editorial TEXT,
                total INTEGER DEFAULT 1,
                disponibles INTEGER DEFAULT 1,
                fecha_creacion DATETIME DEFAULT CURRENT_TIMESTAMP
            )
            """;
            
        String sqlUsuarios = """
                CREATE TABLE IF NOT EXISTS usuarios (
                    id INTEGER PRIMARY KEY AUTOINCREMENT,
                    nombre TEXT NOT NULL,
                    apellido_paterno TEXT NOT NULL,
                    apellido_materno TEXT,
                    domicilio TEXT,
                    telefono TEXT,
                    sanciones INTEGER DEFAULT 0,
                    monto_sancion INTEGER DEFAULT 0,
                    fecha_creacion DATETIME DEFAULT CURRENT_TIMESTAMP
                )
                """;
        
        String sqlPrestamos = """
                CREATE TABLE IF NOT EXISTS prestamos (
                    id INTEGER PRIMARY KEY AUTOINCREMENT,
                    libro_id INTEGER NOT NULL,
                    usuario_id INTEGER NOT NULL,
                    fecha_prestamo DATE NOT NULL,
                    fecha_devolucion DATE NOT NULL,
                    fecha_devolucion_real DATE,
                    estado TEXT NOT NULL DEFAULT 'ACTIVO',
                    FOREIGN KEY (libro_id) REFERENCES libros (id),
                    FOREIGN KEY (usuario_id) REFERENCES usuarios (id)
                )
                """;
            
            try (Statement stmt = conn.createStatement()) {
                stmt.execute(sqlLibros);
                stmt.execute(sqlUsuarios);
                stmt.execute(sqlPrestamos);  // ← NUEVA TABLA
                System.out.println("Tablas verificadas/creadas correctamente");
            } catch (SQLException e) {
                System.err.println("Error creando tablas: " + e.getMessage());
            }
    }
}