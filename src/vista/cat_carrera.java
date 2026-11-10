// CRUD CAT_CARRERA EDUARDO CORTES 

import java.sql.*;
import java.time.LocalDate;

public class CrudCatCarrera {

    // Configuración de conexión a la base de datos (ajusta tus credenciales)
    private static final String URL = "jdbc:mysql://localhost:3306/tu_base_de_datos?useSSL=false&serverTimezone=UTC";
    private static final String USER = "tu_usuario";
    private static final String PASSWORD = "tu_contraseña";

    public static void main(String[] args) {
        CrudCatCarrera crud = new CrudCatCarrera();

        // --- PRUEBAS DE LAS OPERACIONES CRUD ---

        // 1. INSERTAR (Create)
        // crud.insertar(1, 101, "Activo", "Ingeniería en Sistemas", LocalDate.of(2023, 1, 15), null, null);

        // 2. LEER (Read)
        crud.leer();

        // 3. ACTUALIZAR (Update)
        // crud.actualizar(1, 101, "Actualizado", "Ingeniería en Sistemas Computacionales", LocalDate.of(2023, 1, 15), LocalDate.of(2026, 12, 31), null);

        // 4. ELIMINAR (Delete)
        // crud.eliminar(1);
    }

    // ==========================================
    // MÉTODO: CREATE (INSERTAR)
    // ==========================================
    public void insertar(int cveCarrera, int planEstudiosCve, String estatus, String nombre, 
                         LocalDate fechaActivacion, LocalDate fechaInactiva, LocalDate fechaLiquidacion) {
        String sql = "INSERT INTO cat_carrera (cve_carrera, cat_plan_estudios_cve, estatus, nombre, fecha_activacion, fecha_inactiva, fecha_liquidacion) VALUES (?, ?, ?, ?, ?, ?, ?)";
        
        try (Connection conn = DriverManager.getConnection(URL, USER, PASSWORD);
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setInt(1, cveCarrera);
            pstmt.setInt(2, planEstudiosCve);
            pstmt.setString(3, estatus);
            pstmt.setString(4, nombre);
            pstmt.setObject(5, fechaActivacion);
            pstmt.setObject(6, fechaInactiva);
            pstmt.setObject(7, fechaLiquidacion);
            
            pstmt.executeUpdate();
            System.out.println("✔ Registro insertado exitosamente.");
        } catch (SQLException e) {
            System.out.println("❌ Error al insertar: " + e.getMessage());
        }
    }

    // ==========================================
    // MÉTODO: READ (LEER / CONSULTAR)
    // ==========================================
    public void leer() {
        String sql = "SELECT * FROM cat_carrera";
        
        try (Connection conn = DriverManager.getConnection(URL, USER, PASSWORD);
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            
            System.out.println("\n--- LISTADO DE CAT_CARRERA ---");
            while (rs.next()) {
                System.out.println("Clave Carrera: " + rs.getInt("cve_carrera") +
                        " | Plan Estudios CVE: " + rs.getInt("cat_plan_estudios_cve") +
                        " | Estatus: " + rs.getString("estatus") +
                        " | Nombre: " + rs.getString("nombre") +
                        " | F. Activación: " + rs.getDate("fecha_activacion") +
                        " | F. Inactiva: " + rs.getDate("fecha_inactiva") +
                        " | F. Liquidación: " + rs.getDate("fecha_liquidacion"));
            }
            System.out.println("-------------------------------\n");
        } catch (SQLException e) {
            System.out.println("❌ Error al leer: " + e.getMessage());
        }
    }

    // ==========================================
    // MÉTODO: UPDATE (ACTUALIZAR)
    // ==========================================
    public void actualizar(int cveCarrera, int planEstudiosCve, String estatus, String nombre, 
                           LocalDate fechaActivacion, LocalDate fechaInactiva, LocalDate fechaLiquidacion) {
        String sql = "UPDATE cat_carrera SET cat_plan_estudios_cve = ?, estatus = ?, nombre = ?, fecha_activacion = ?, fecha_inactiva = ?, fecha_liquidacion = ? WHERE cve_carrera = ?";
        
        try (Connection conn = DriverManager.getConnection(URL, USER, PASSWORD);
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setInt(1, planEstudiosCve);
            pstmt.setString(2, estatus);
            pstmt.setString(3, nombre);
            pstmt.setObject(4, fechaActivacion);
            pstmt.setObject(5, fechaInactiva);
            pstmt.setObject(6, fechaLiquidacion);
            pstmt.setInt(7, cveCarrera);
            
            int filasAfectadas = pstmt.executeUpdate();
            if (filasAfectadas > 0) {
                System.out.println("✔ Registro actualizado exitosamente.");
            } else {
                System.out.println("⚠️ No se encontró un registro con la clave especificada.");
            }
        } catch (SQLException e) {
            System.out.println("❌ Error al actualizar: " + e.getMessage());
        }
    }

    // ==========================================
    // MÉTODO: DELETE (ELIMINAR)
    // ==========================================
    public void eliminar(int cveCarrera) {
        String sql = "DELETE FROM cat_carrera WHERE cve_carrera = ?";
        
        try (Connection conn = DriverManager.getConnection(URL, USER, PASSWORD);
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setInt(1, cveCarrera);
            
            int filasAfectadas = pstmt.executeUpdate();
            if (filasAfectadas > 0) {
                System.out.println("✔ Registro eliminado exitosamente.");
            } else {
                System.out.println("⚠️ No se encontró un registro con la clave especificada.");
            }
        } catch (SQLException e) {
            System.out.println("❌ Error al eliminar: " + e.getMessage());
        }
    }
}
