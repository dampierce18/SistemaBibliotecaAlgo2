package sistemabiblioteca.bd;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;
import java.io.File;

public class ConexionSQLite {
    private static final String DB_NAME = "biblioteca.db";
    private static final String URL = "jdbc:sqlite:" + getDatabasePath();
    
    private static String getDatabasePath() {
        if (isRunningInEclipse()) {
            return DB_NAME;
        }
        // Si es JAR, crear en subdirectorio 'data'
        File dataDir = new File("data");
        if (!dataDir.exists()) {
            dataDir.mkdirs();
        }
        return "data" + File.separator + DB_NAME;
    }
    
    private static boolean isRunningInEclipse() {
        String classPath = System.getProperty("java.class.path").toLowerCase();
        return classPath.contains("eclipse") || classPath.contains("workspace");
    }
    
    public static Connection getConnection() throws SQLException {
        try {
            Class.forName("org.sqlite.JDBC");
            System.out.println("Conectando a: " + getDatabasePath());
            Connection conn = DriverManager.getConnection(URL);
            crearTablas(conn); 
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
                empleado_id INTEGER NOT NULL DEFAULT 1,
                fecha_creacion DATETIME DEFAULT CURRENT_TIMESTAMP,
                FOREIGN KEY (empleado_id) REFERENCES empleados (id)
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
        	        empleado_id INTEGER NOT NULL, 
        	        fecha_creacion DATETIME DEFAULT CURRENT_TIMESTAMP,
        	        FOREIGN KEY (empleado_id) REFERENCES empleados (id)
        	    )
        	    """;
        
        // TABLA PRESTAMOS ACTUALIZADA - AGREGAR empleado_id
        String sqlPrestamos = """
            CREATE TABLE IF NOT EXISTS prestamos (
                id INTEGER PRIMARY KEY AUTOINCREMENT,
                libro_id INTEGER NOT NULL,
                usuario_id INTEGER NOT NULL,
                empleado_id INTEGER NOT NULL DEFAULT 1, -- NUEVA COLUMNA
                fecha_prestamo DATE NOT NULL,
                fecha_devolucion DATE NOT NULL,
                fecha_devolucion_real DATE,
                estado TEXT NOT NULL DEFAULT 'ACTIVO',
                FOREIGN KEY (libro_id) REFERENCES libros (id),
                FOREIGN KEY (usuario_id) REFERENCES usuarios (id),
                FOREIGN KEY (empleado_id) REFERENCES empleados (id) -- NUEVA LLAVE FORÁNEA
            )
            """;
            
        String sqlEmpleados = """
            CREATE TABLE IF NOT EXISTS empleados (
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
            """;
            
        try (Statement stmt = conn.createStatement()) {
            stmt.execute(sqlLibros);
            stmt.execute(sqlUsuarios);
            stmt.execute(sqlPrestamos);
            stmt.execute(sqlEmpleados);
            
            // Insertar admin por defecto si no existe
            insertarAdminPorDefecto(conn);
            
            // Actualizar la estructura de préstamos existentes
            actualizarTablaPrestamos(conn);
            
            insertarDatosEjemplo(conn);
            System.out.println("Tablas verificadas/creadas correctamente");
            System.out.println("Base de datos en: " + new File(getDatabasePath()).getAbsolutePath());
        } catch (SQLException e) {
            System.err.println("Error creando tablas: " + e.getMessage());
        }
    }
    
    // NUEVO MÉTODO: Actualizar tabla préstamos si ya existe
    private static void actualizarTablaPrestamos(Connection conn) {
        try (Statement stmt = conn.createStatement()) {
            // Verificar si la columna empleado_id ya existe
            boolean columnaExiste = false;
            try {
                var rs = stmt.executeQuery("SELECT empleado_id FROM prestamos LIMIT 1");
                columnaExiste = true;
                rs.close();
            } catch (SQLException e) {
                // La columna no existe, la vamos a agregar
                columnaExiste = false;
            }
            
            if (!columnaExiste) {
                System.out.println("Agregando columna empleado_id a tabla prestamos...");
                stmt.execute("ALTER TABLE prestamos ADD COLUMN empleado_id INTEGER DEFAULT 1");
                stmt.execute("UPDATE prestamos SET empleado_id = 1 WHERE empleado_id IS NULL");
                System.out.println("Columna empleado_id agregada correctamente");
            }
            
        } catch (SQLException e) {
            System.err.println("Error actualizando tabla préstamos: " + e.getMessage());
        }
    }
    
    private static void insertarAdminPorDefecto(Connection conn) {
        try (Statement stmt = conn.createStatement()) {
            // Primero verificar si ya existe un admin
            boolean adminExiste = false;
            var rs = stmt.executeQuery("SELECT id FROM empleados WHERE usuario = 'admin'");
            if (rs.next()) {
                adminExiste = true;
            }
            rs.close();
            
            if (!adminExiste) {
                // Insertar admin con ID fijo
                String sql = """
                    INSERT INTO empleados 
                    (id, nombre, apellido_paterno, apellido_materno, usuario, password, rol, telefono, email) 
                    VALUES (1, 'Administrador', 'Sistema', '', 'admin', 'admin123', 'ADMIN', '', 'admin@biblioteca.com')
                    """;
                stmt.execute(sql);
                System.out.println("Admin por defecto creado con ID=1");
            } else {
                System.out.println("Admin ya existe en la base de datos");
            }
            
        } catch (SQLException e) {
            System.err.println("Error insertando admin por defecto: " + e.getMessage());
        }
    }
    
    // Método para debugear
    public static String getDatabaseLocation() {
        return new File(getDatabasePath()).getAbsolutePath();
    }
    
    private static void insertarDatosEjemplo(Connection conn) {
        try (Statement stmt = conn.createStatement()) {
            
            // VERIFICAR si ya existen datos antes de insertar
            var rs = stmt.executeQuery("SELECT COUNT(*) as count FROM empleados");
            boolean datosExisten = false;
            if (rs.next()) {
                datosExisten = rs.getInt("count") > 1; // Mayor que 1 porque siempre está el admin
            }
            rs.close();
            
            if (datosExisten) {
                System.out.println("Datos de ejemplo ya existen, omitiendo inserción");
                return; // Salir si ya hay datos
            }
            
            System.out.println("Insertando datos de ejemplo...");
            
            // 1. EMPLEADOS - Especificar IDs para evitar duplicados
            String empleadosSQL = """
                INSERT OR IGNORE INTO empleados (id, nombre, apellido_paterno, apellido_materno, usuario, password, rol, telefono, email) VALUES
                (2, 'Carlos', 'Martínez', 'Rodríguez', 'carlos', 'carlos123', 'EMPLEADO', '555-0102', 'carlos.martinez@biblioteca.com'),
                (3, 'María', 'Hernández', 'Silva', 'maria', 'maria123', 'EMPLEADO', '555-0103', 'maria.hernandez@biblioteca.com')
                """;
            stmt.execute(empleadosSQL);
            
            // 2. USUARIOS - Especificar IDs
            String usuariosSQL = """
                INSERT OR IGNORE INTO usuarios (id, nombre, apellido_paterno, apellido_materno, domicilio, telefono, sanciones, monto_sancion, empleado_id) VALUES
                (1, 'Juan', 'Pérez', 'Gómez', 'Av. Siempre Viva 123', '555-1001', 0, 0, 1),
                (2, 'Laura', 'Rodríguez', 'Fernández', 'Calle Falsa 456', '555-1002', 1, 50, 2),
                (3, 'Miguel', 'Sánchez', 'López', 'Boulevard Central 789', '555-1003', 0, 0, 1),
                (4, 'Sofía', 'Ramírez', 'Castro', 'Plaza Mayor 321', '555-1004', 0, 0, 3),
                (5, 'Diego', 'Morales', 'Ortega', 'Calle Luna 654', '555-1005', 2, 100, 2),
                (6, 'Elena', 'Castillo', 'Mendoza', 'Av. Sol 987', '555-1006', 0, 0, 1),
                (7, 'Roberto', 'Vargas', 'Jiménez', 'Calle Estrella 147', '555-1007', 0, 0, 3)
                """;
            stmt.execute(usuariosSQL);
            
            // 3. LIBROS - Especificar IDs con empleado_id
            String librosSQL = """
                INSERT OR IGNORE INTO libros (id, titulo, anio, autor, categoria, editorial, total, disponibles, empleado_id) VALUES
                (1, 'Cien años de soledad', '1967', 'Gabriel García Márquez', 'Realismo Mágico', 'Sudamericana', 5, 3, 1),
                (2, '1984', '1949', 'George Orwell', 'Ciencia Ficción', 'Secker & Warburg', 3, 1, 2),
                (3, 'El principito', '1943', 'Antoine de Saint-Exupéry', 'Literatura Infantil', 'Reynal & Hitchcock', 4, 4, 3),
                (4, 'Don Quijote de la Mancha', '1605', 'Miguel de Cervantes', 'Clásico', 'Francisco de Robles', 2, 0, 1),
                (5, 'Orgullo y prejuicio', '1813', 'Jane Austen', 'Romance', 'T. Egerton', 3, 2, 2),
                (6, 'Crónica de una muerte anunciada', '1981', 'Gabriel García Márquez', 'Novela', 'La Oveja Negra', 4, 3, 3),
                (7, 'El amor en los tiempos del cólera', '1985', 'Gabriel García Márquez', 'Romance', 'Oveja Negra', 3, 2, 1),
                (8, 'Fahrenheit 451', '1953', 'Ray Bradbury', 'Ciencia Ficción', 'Ballantine Books', 2, 1, 2),
                (9, 'El señor de los anillos', '1954', 'J.R.R. Tolkien', 'Fantasía', 'Allen & Unwin', 3, 1, 3),
                (10, 'Harry Potter y la piedra filosofal', '1997', 'J.K. Rowling', 'Fantasía', 'Bloomsbury', 6, 4, 1)
                """;
            stmt.execute(librosSQL);
            
            // 4. PRÉSTAMOS - ACTUALIZADO con empleado_id
            String prestamosSQL = """
                INSERT OR IGNORE INTO prestamos (id, libro_id, usuario_id, empleado_id, fecha_prestamo, fecha_devolucion, fecha_devolucion_real, estado) VALUES
                (1, 1, 1, 1, date('now', '-5 days'), date('now', '+2 days'), NULL, 'ACTIVO'),
                (2, 2, 3, 2, date('now', '-3 days'), date('now', '+4 days'), NULL, 'ACTIVO'),
                (3, 5, 4, 3, date('now', '-1 days'), date('now', '+6 days'), NULL, 'ACTIVO'),
                (4, 4, 2, 1, date('now', '-15 days'), date('now', '-1 days'), NULL, 'ACTIVO'),
                (5, 9, 5, 2, date('now', '-20 days'), date('now', '-5 days'), NULL, 'ACTIVO'),
                (6, 3, 1, 3, date('now', '-30 days'), date('now', '-25 days'), date('now', '-25 days'), 'DEVUELTO'),
                (7, 6, 3, 1, date('now', '-20 days'), date('now', '-15 days'), date('now', '-14 days'), 'DEVUELTO'),
                (8, 7, 4, 2, date('now', '-10 days'), date('now', '-5 days'), date('now', '-4 days'), 'DEVUELTO'),
                (9, 8, 6, 3, date('now', '-12 days'), date('now', '-2 days'), NULL, 'ACTIVO'),
                (10, 10, 7, 1, date('now', '-8 days'), date('now', '-1 days'), NULL, 'ACTIVO')
                """;
            stmt.execute(prestamosSQL);
            
            System.out.println("Datos de ejemplo insertados correctamente");
            
        } catch (SQLException e) {
            System.err.println("Error insertando datos de ejemplo: " + e.getMessage());
        }
    }
}