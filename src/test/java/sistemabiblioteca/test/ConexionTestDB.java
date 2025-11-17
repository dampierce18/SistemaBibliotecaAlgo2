package sistemabiblioteca.test;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;

public class ConexionTestDB {
    private static int connectionCounter = 0;
    
    public static Connection getTestConnection() throws SQLException {
        connectionCounter++;
        String url = "jdbc:sqlite:file:testdb_" + System.currentTimeMillis() + "_" + connectionCounter + "?mode=memory&cache=shared";
        System.out.println("🔗 Creando conexión: " + url);
        
        Connection conn = DriverManager.getConnection(url);
        
        conn.setAutoCommit(true);
        
        crearTablasDePrueba(conn);
        System.out.println("✅ Conexión de prueba creada - Estado: " + (conn.isClosed() ? "CERRADA" : "ABIERTA"));
        
        return conn;
    }
    
    private static void crearTablasDePrueba(Connection conn) throws SQLException {
        try (Statement stmt = conn.createStatement()) {
            stmt.execute("DROP TABLE IF EXISTS prestamos");
            stmt.execute("DROP TABLE IF EXISTS libros");
            stmt.execute("DROP TABLE IF EXISTS usuarios");
            
            stmt.execute("CREATE TABLE usuarios (" +
                "id INTEGER PRIMARY KEY AUTOINCREMENT, " +
                "nombre TEXT NOT NULL, " +
                "apellido_paterno TEXT NOT NULL, " +
                "apellido_materno TEXT NOT NULL, " +
                "domicilio TEXT NOT NULL, " +
                "telefono TEXT NOT NULL, " +
                "sanciones INTEGER NOT NULL DEFAULT 0, " +
                "monto_sancion INTEGER NOT NULL DEFAULT 0)");
            
            // ✅ CREAR TABLA LIBROS
            stmt.execute("CREATE TABLE libros (" +
                "id INTEGER PRIMARY KEY AUTOINCREMENT, " +
                "titulo TEXT NOT NULL, " +
                "anio TEXT NOT NULL, " +
                "autor TEXT NOT NULL, " +
                "categoria TEXT NOT NULL, " +
                "editorial TEXT NOT NULL, " +
                "total INTEGER NOT NULL, " +
                "disponibles INTEGER NOT NULL)");
            
            // ✅ CREAR TABLA PRESTAMOS (ESTRUCTURA CORRECTA)
            stmt.execute("CREATE TABLE prestamos (" +
                "id INTEGER PRIMARY KEY AUTOINCREMENT, " +
                "libro_id INTEGER NOT NULL, " +
                "usuario_id INTEGER NOT NULL, " +
                "fecha_prestamo TEXT NOT NULL, " +
                "fecha_devolucion TEXT NOT NULL, " +
                "fecha_devolucion_real TEXT, " +
                "estado TEXT NOT NULL, " +
                "FOREIGN KEY(libro_id) REFERENCES libros(id), " +
                "FOREIGN KEY(usuario_id) REFERENCES usuarios(id))");
            
            System.out.println("✅ Tablas de prueba RECREADAS correctamente");
        }
    }
    
}