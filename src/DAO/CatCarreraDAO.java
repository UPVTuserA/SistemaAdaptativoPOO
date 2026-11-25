// CRUD CAT_CARRERA EDUARDO CORTES - MODULO DAO

package DAO;

import MODELO.CatCarrera;

import java.sql.*;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

/**
 * Capa de acceso a datos de la tabla cat_carrera.
 * Contiene las operaciones CREATE, READ, UPDATE y DELETE.
 */
public class CatCarreraDAO {

    // Configuración de conexión a la base de datos (ajusta tus credenciales)
    private static final String URL = "jdbc:mysql://localhost:3306/tu_base_de_datos?useSSL=false&serverTimezone=UTC";
    private static final String USER = "tu_usuario";
    private static final String PASSWORD = "tu_contraseña";

    // ==========================================
    // MÉTODO: CREATE (INSERTAR)
    // ==========================================
    public void insertar(CatCarrera carrera) {
        String sql = "INSERT INTO cat_carrera (cve_carrera, cat_plan_estudios_cve, estatus, nombre, fecha_activacion, fecha_inactiva, fecha_liquidacion) VALUES (?, ?, ?, ?, ?, ?, ?)";

        try (Connection conn = DriverManager.getConnection(URL, USER, PASSWORD);
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setInt(1, carrera.getCveCarrera());
            pstmt.setInt(2, carrera.getCatPlanEstudiosCve());
            pstmt.setString(3, carrera.getEstatus());
            pstmt.setString(4, carrera.getNombre());
            pstmt.setObject(5, carrera.getFechaActivacion());
            pstmt.setObject(6, carrera.getFechaInactiva());
            pstmt.setObject(7, carrera.getFechaLiquidacion());

            pstmt.executeUpdate();
            System.out.println("✔ Registro insertado exitosamente.");
        } catch (SQLException e) {
            System.out.println("❌ Error al insertar: " + e.getMessage());
        }
    }

    // ==========================================
    // MÉTODO: READ (LEER / CONSULTAR)
    // ==========================================
    public List<CatCarrera> leer() {
        String sql = "SELECT * FROM cat_carrera";
        List<CatCarrera> lista = new ArrayList<>();

        try (Connection conn = DriverManager.getConnection(URL, USER, PASSWORD);
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {

            System.out.println("\n--- LISTADO DE CAT_CARRERA ---");
            while (rs.next()) {
                CatCarrera carrera = new CatCarrera(
                        rs.getInt("cve_carrera"),
                        rs.getInt("cat_plan_estudios_cve"),
                        rs.getString("estatus"),
                        rs.getString("nombre"),
                        rs.getDate("fecha_activacion") != null ? rs.getDate("fecha_activacion").toLocalDate() : null,
                        rs.getDate("fecha_inactiva") != null ? rs.getDate("fecha_inactiva").toLocalDate() : null,
                        rs.getDate("fecha_liquidacion") != null ? rs.getDate("fecha_liquidacion").toLocalDate() : null
                );
                lista.add(carrera);

                System.out.println("Clave Carrera: " + carrera.getCveCarrera() +
                        " | Plan Estudios CVE: " + carrera.getCatPlanEstudiosCve() +
                        " | Estatus: " + carrera.getEstatus() +
                        " | Nombre: " + carrera.getNombre() +
                        " | F. Activación: " + carrera.getFechaActivacion() +
                        " | F. Inactiva: " + carrera.getFechaInactiva() +
                        " | F. Liquidación: " + carrera.getFechaLiquidacion());
            }
            System.out.println("-------------------------------\n");
        } catch (SQLException e) {
            System.out.println("❌ Error al leer: " + e.getMessage());
        }
        return lista;
    }

    // ==========================================
    // MÉTODO: UPDATE (ACTUALIZAR)
    // ==========================================
    public void actualizar(CatCarrera carrera) {
        String sql = "UPDATE cat_carrera SET cat_plan_estudios_cve = ?, estatus = ?, nombre = ?, fecha_activacion = ?, fecha_inactiva = ?, fecha_liquidacion = ? WHERE cve_carrera = ?";

        try (Connection conn = DriverManager.getConnection(URL, USER, PASSWORD);
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setInt(1, carrera.getCatPlanEstudiosCve());
            pstmt.setString(2, carrera.getEstatus());
            pstmt.setString(3, carrera.getNombre());
            pstmt.setObject(4, carrera.getFechaActivacion());
            pstmt.setObject(5, carrera.getFechaInactiva());
            pstmt.setObject(6, carrera.getFechaLiquidacion());
            pstmt.setInt(7, carrera.getCveCarrera());

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
