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
 */
public class CatExamenDAO {

    // Configuración de conexión a la base de datos (ajusta tus credenciales)
    private static final String URL = "jdbc:mysql://localhost:3306/tu_base_de_datos?useSSL=false&serverTimezone=UTC";
    private static final String USER = "tu_usuario";
    private static final String PASSWORD = "tu_contraseña";

    // ==========================================
    // MÉTODO: CREATE (INSERTAR)
    // ==========================================
    public void insertar(CatExamen examen) {
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
            System.out.println("✔ Registro insertado exitosamente.");
        } catch (SQLException e) {
            System.out.println("❌ Error al insertar: " + e.getMessage());
        }
    }

    // ==========================================
    // MÉTODO: READ (LEER / CONSULTAR)
    // ==========================================
    public List<CatExamen> leer() {
        String sql = "SELECT * FROM cat_examen";
        List<CatExamen> lista = new ArrayList<>();

        try (Connection conn = DriverManager.getConnection(URL, USER, PASSWORD);
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {

            System.out.println("\n--- LISTADO DE CAT_EXAMEN ---");
            while (rs.next()) {
                CatExamen examen = new CatExamen(
                        rs.getInt("cve_examen"),
                        rs.getInt("cat_profesor_cve_profesor"),
                        rs.getString("cat_materias_clave_materia"),
                        rs.getInt("cat_unidades_materia_cve_unidad"),
                        TipoExamen.desdeValor(rs.getString("tipo_examen")),
                        Periodo.desdeValor(rs.getString("periodo")),
                        rs.getInt("anio")
                );
                lista.add(examen);

                System.out.println("Clave Examen: " + examen.getCveExamen() +
                        " | Profesor CVE: " + examen.getCatProfesorCveProfesor() +
                        " | Materia: " + examen.getCatMateriasClaveMateria() +
                        " | Unidad: " + examen.getCatUnidadesMateriaCveUnidad() +
                        " | Tipo: " + examen.getTipoExamen().getValor() +
                        " | Periodo: " + examen.getPeriodo().getValor() +
                        " | Año: " + examen.getAnio());
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
    public void actualizar(CatExamen examen) {
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
    public void eliminar(int cveExamen) {
        String sql = "DELETE FROM cat_examen WHERE cve_examen = ?";

        try (Connection conn = DriverManager.getConnection(URL, USER, PASSWORD);
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setInt(1, cveExamen);

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
