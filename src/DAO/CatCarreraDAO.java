// CRUD CAT_CARRERA EDUARDO CORTES - MODULO DAO

package DAO;

import MODELO.CatCarrera;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

/**
 * Capa de acceso a datos de la tabla cat_carrera.
 * Contiene las operaciones CREATE, READ, UPDATE y DELETE.
 *
 * Cada metodo deja el ultimo error en {@link #getUltimoError()} para que la
 * VISTA pueda mostrarlo en pantalla (null cuando no hubo error).
 */
public class CatCarreraDAO {

    // Configuración de conexión a la base de datos (BD "test" incluida con MariaDB)
    private static final String URL = "jdbc:mysql://localhost:3306/test?useSSL=false&serverTimezone=UTC";
    private static final String USER = "";
    private static final String PASSWORD = "";

    private String ultimoError;

    /** Devuelve el último error ocurrido, o null si todo fue bien. */
    public String getUltimoError() {
        return ultimoError;
    }

    /** Devuelve la URL usada, útil para diagnosticar problemas de conexión. */
    public String getUrl() {
        return URL;
    }

    // ==========================================
    // MÉTODO: CREATE (INSERTAR)
    // ==========================================
    public boolean insertar(CatCarrera carrera) {
        ultimoError = null;
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
            return true;
        } catch (SQLException e) {
            ultimoError = e.getMessage();
            System.out.println("❌ Error al insertar: " + e.getMessage());
            return false;
        }
    }

    // ==========================================
    // MÉTODO: READ (LEER / CONSULTAR)
    // ==========================================
    public List<CatCarrera> leer() {
        ultimoError = null;
        String sql = "SELECT * FROM cat_carrera";
        List<CatCarrera> lista = new ArrayList<>();

        try (Connection conn = DriverManager.getConnection(URL, USER, PASSWORD);
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {

            while (rs.next()) {
                lista.add(new CatCarrera(
                        rs.getInt("cve_carrera"),
                        rs.getInt("cat_plan_estudios_cve"),
                        rs.getString("estatus"),
                        rs.getString("nombre"),
                        rs.getDate("fecha_activacion") != null ? rs.getDate("fecha_activacion").toLocalDate() : null,
                        rs.getDate("fecha_inactiva") != null ? rs.getDate("fecha_inactiva").toLocalDate() : null,
                        rs.getDate("fecha_liquidacion") != null ? rs.getDate("fecha_liquidacion").toLocalDate() : null
                ));
            }
        } catch (SQLException e) {
            ultimoError = e.getMessage();
            System.out.println("❌ Error al leer: " + e.getMessage());
            lista.clear();
        }
        return lista;
    }

    // ==========================================
    // MÉTODO: UPDATE (ACTUALIZAR)
    // ==========================================
    public boolean actualizar(CatCarrera carrera) {
        ultimoError = null;
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

            int filas = pstmt.executeUpdate();
            if (filas == 0) {
                ultimoError = "No existe un registro con cve_carrera = " + carrera.getCveCarrera() + ".";
                return false;
            }
            return true;
        } catch (SQLException e) {
            ultimoError = e.getMessage();
            System.out.println("❌ Error al actualizar: " + e.getMessage());
            return false;
        }
    }

    // ==========================================
    // MÉTODO: DELETE (ELIMINAR)
    // ==========================================
    public boolean eliminar(int cveCarrera) {
        ultimoError = null;
        String sql = "DELETE FROM cat_carrera WHERE cve_carrera = ?";

        try (Connection conn = DriverManager.getConnection(URL, USER, PASSWORD);
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setInt(1, cveCarrera);

            int filas = pstmt.executeUpdate();
            if (filas == 0) {
                ultimoError = "No existe un registro con cve_carrera = " + cveCarrera + ".";
                return false;
            }
            return true;
        } catch (SQLException e) {
            ultimoError = e.getMessage();
            System.out.println("❌ Error al eliminar: " + e.getMessage());
            return false;
        }
    }
}
