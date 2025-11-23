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
            // 🔄 ORDEN CORRECTO PARA ELIMINACIÓN (respetando foreign keys)
            stmt.execute("DROP TABLE IF EXISTS prestamos");
            stmt.execute("DROP TABLE IF EXISTS libros");
            stmt.execute("DROP TABLE IF EXISTS usuarios");
            stmt.execute("DROP TABLE IF EXISTS empleados");
            
            // ✅ CREAR TABLA EMPLEADOS PRIMERO (por las foreign keys)
            stmt.execute("""
                CREATE TABLE empleados (
                    id INTEGER PRIMARY KEY AUTOINCREMENT,
                    nombre TEXT NOT NULL,
                    apellido_paterno TEXT NOT NULL,
                    apellido_materno TEXT,
                    usuario TEXT UNIQUE NOT NULL,
                    password TEXT NOT NULL,
                    rol TEXT NOT NULL CHECK(rol IN ('ADMIN', 'EMPLEADO')) DEFAULT 'EMPLEADO',
                    telefono TEXT,
                    email TEXT,
                    fecha_creacion DATETIME DEFAULT CURRENT_TIMESTAMP
                )
            """);
            
            // ✅ CREAR TABLA USUARIOS (con empleado_id)
            stmt.execute("""
                CREATE TABLE usuarios (
                    id INTEGER PRIMARY KEY AUTOINCREMENT,
                    nombre TEXT NOT NULL,
                    apellido_paterno TEXT NOT NULL,
                    apellido_materno TEXT,
                    domicilio TEXT,
                    telefono TEXT,
                    sanciones INTEGER DEFAULT 0,
                    monto_sancion INTEGER DEFAULT 0,
                    empleado_id INTEGER NOT NULL DEFAULT 1,
                    fecha_creacion DATETIME DEFAULT CURRENT_TIMESTAMP,
                    FOREIGN KEY (empleado_id) REFERENCES empleados (id)
                )
            """);
            
            // ✅ CREAR TABLA LIBROS (con empleado_id)
            stmt.execute("""
                CREATE TABLE libros (
                    id INTEGER PRIMARY KEY AUTOINCREMENT,
                    titulo TEXT NOT NULL,
                    anio TEXT,
                    autor TEXT NOT NULL,
                    categoria TEXT,
                    editorial TEXT,
                    total INTEGER DEFAULT 1,
                    disponibles INTEGER DEFAULT 1,
                    empleado_id INTEGER NOT NULL DEFAULT 1,
                    fecha_creacion DATETIME DEFAULT CURRENT_TIMESTAMP,
                    FOREIGN KEY (empleado_id) REFERENCES empleados (id)
                )
            """);
            
            // ✅ CREAR TABLA PRESTAMOS (con empleado_id)
            stmt.execute("""
                CREATE TABLE prestamos (
                    id INTEGER PRIMARY KEY AUTOINCREMENT,
                    libro_id INTEGER NOT NULL,
                    usuario_id INTEGER NOT NULL,
                    empleado_id INTEGER NOT NULL DEFAULT 1,
                    fecha_prestamo DATE NOT NULL,
                    fecha_devolucion DATE NOT NULL,
                    fecha_devolucion_real DATE,
                    estado TEXT NOT NULL DEFAULT 'ACTIVO',
                    FOREIGN KEY (libro_id) REFERENCES libros (id),
                    FOREIGN KEY (usuario_id) REFERENCES usuarios (id),
                    FOREIGN KEY (empleado_id) REFERENCES empleados (id)
                )
            """);
            
            // 🔥 INSERTAR DATOS BÁSICOS DE PRUEBA
            insertarDatosBasicosDePrueba(stmt);
            
            System.out.println("✅ Tablas de prueba RECREADAS correctamente con estructura actualizada");
        }
    }
    
    private static void insertarDatosBasicosDePrueba(Statement stmt) throws SQLException {
        // ✅ INSERTAR EMPLEADOS BÁSICOS
        stmt.execute("""
            INSERT INTO empleados (id, nombre, apellido_paterno, usuario, password, rol) VALUES
            (1, 'Administrador', 'Sistema', 'admin', 'admin123', 'ADMIN'),
            (2, 'Empleado', 'Test', 'empleado', 'empleado123', 'EMPLEADO')
        """);
        
        // ✅ INSERTAR USUARIOS BÁSICOS
        stmt.execute("""
            INSERT INTO usuarios (id, nombre, apellido_paterno, apellido_materno, domicilio, telefono, empleado_id) VALUES
            (1, 'Juan', 'Pérez', 'Gómez', 'Av. Test 123', '555-1001', 1),
            (2, 'María', 'López', 'Fernández', 'Calle Prueba 456', '555-1002', 2)
        """);
        
        // ✅ INSERTAR LIBROS BÁSICOS (con empleado_id)
        stmt.execute("""
            INSERT INTO libros (id, titulo, anio, autor, categoria, editorial, total, disponibles, empleado_id) VALUES
            (1, 'Libro de Prueba 1', '2023', 'Autor Test 1', 'Categoría Test', 'Editorial Test', 5, 3, 1),
            (2, 'Libro de Prueba 2', '2022', 'Autor Test 2', 'Categoría Test', 'Editorial Test', 3, 2, 2)
        """);
        
        // ✅ INSERTAR PRÉSTAMOS BÁSICOS (con empleado_id)
        stmt.execute("""
            INSERT INTO prestamos (id, libro_id, usuario_id, empleado_id, fecha_prestamo, fecha_devolucion, estado) VALUES
            (1, 1, 1, 1, date('now', '-5 days'), date('now', '+2 days'), 'ACTIVO'),
            (2, 2, 2, 2, date('now', '-3 days'), date('now', '+4 days'), 'ACTIVO')
        """);
        
        System.out.println("✅ Datos básicos de prueba insertados correctamente");
    }
}