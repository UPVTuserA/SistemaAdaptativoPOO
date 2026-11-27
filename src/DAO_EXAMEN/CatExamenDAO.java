// CRUD CAT_EXAMEN EDUARDO CORTES - MODULO DAO

package DAO_EXAMEN;

import MODELO_EXAMEN.CatExamen;
import MODELO_EXAMEN.Periodo;
import MODELO_EXAMEN.TipoExamen;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

/**
 * Capa de acceso a datos de la tabla cat_examen.
 * Contiene las operaciones CREATE, READ, UPDATE y DELETE.
 *
 * Cada metodo deja el ultimo error en {@link #getUltimoError()} para que la
 * VISTA pueda mostrarlo en pantalla (null cuando no hubo error).
 */
public class CatExamenDAO {

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
    public boolean insertar(CatExamen examen) {
        ultimoError = null;
        String sql = "INSERT INTO cat_examen (cve_examen, cat_profesor_cve_profesor, cat_materias_clave_materia, " +
                "cat_unidades_materia_cve_unidad, tipo_examen, periodo, anio) VALUES (?, ?, ?, ?, ?, ?, ?)";

        try (Connection conn = DriverManager.getConnection(URL, USER, PASSWORD);
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setInt(1, examen.getCveExamen());
            pstmt.setInt(2, examen.getCatProfesorCveProfesor());
            pstmt.setString(3, examen.getCatMateriasClaveMateria());
            pstmt.setInt(4, examen.getCatUnidadesMateriaCveUnidad());
            pstmt.setString(5, examen.getTipoExamen().getValor());
            pstmt.setString(6, examen.getPeriodo().getValor());
            pstmt.setInt(7, examen.getAnio());

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
    public List<CatExamen> leer() {
        ultimoError = null;
        String sql = "SELECT * FROM cat_examen";
        List<CatExamen> lista = new ArrayList<>();

        try (Connection conn = DriverManager.getConnection(URL, USER, PASSWORD);
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {

            while (rs.next()) {
                lista.add(new CatExamen(
                        rs.getInt("cve_examen"),
                        rs.getInt("cat_profesor_cve_profesor"),
                        rs.getString("cat_materias_clave_materia"),
                        rs.getInt("cat_unidades_materia_cve_unidad"),
                        TipoExamen.desdeValor(rs.getString("tipo_examen")),
                        Periodo.desdeValor(rs.getString("periodo")),
                        rs.getInt("anio")
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
    public boolean actualizar(CatExamen examen) {
        ultimoError = null;
        String sql = "UPDATE cat_examen SET cat_profesor_cve_profesor = ?, cat_materias_clave_materia = ?, " +
                "cat_unidades_materia_cve_unidad = ?, tipo_examen = ?, periodo = ?, anio = ? WHERE cve_examen = ?";

        try (Connection conn = DriverManager.getConnection(URL, USER, PASSWORD);
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setInt(1, examen.getCatProfesorCveProfesor());
            pstmt.setString(2, examen.getCatMateriasClaveMateria());
            pstmt.setInt(3, examen.getCatUnidadesMateriaCveUnidad());
            pstmt.setString(4, examen.getTipoExamen().getValor());
            pstmt.setString(5, examen.getPeriodo().getValor());
            pstmt.setInt(6, examen.getAnio());
            pstmt.setInt(7, examen.getCveExamen());

            int filas = pstmt.executeUpdate();
            if (filas == 0) {
                ultimoError = "No existe un registro con cve_examen = " + examen.getCveExamen() + ".";
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
    public boolean eliminar(int cveExamen) {
        ultimoError = null;
        String sql = "DELETE FROM cat_examen WHERE cve_examen = ?";

        try (Connection conn = DriverManager.getConnection(URL, USER, PASSWORD);
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setInt(1, cveExamen);

            int filas = pstmt.executeUpdate();
            if (filas == 0) {
                ultimoError = "No existe un registro con cve_examen = " + cveExamen + ".";
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
