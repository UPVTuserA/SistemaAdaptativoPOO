/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package dao;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import modelo.Opcion;
import modelo.Pregunta;
import sistemaadaptativopoo.Conexion;

/**
 * DAO de la tabla cat_pregunta
 *
 * @author Alberto
 */
public class PreguntaDAO {

    // Obtiene la conexion y avisa claro si MySQL no responde
    private Connection conectar() {

        Connection con = Conexion.conectar();

        if (con == null) {
            throw new RuntimeException(
                "No hay conexión con MySQL.\n"
                + "Revisa que MySQL esté encendido en XAMPP, que exista la base "
                + "sistema_adaptativo y que la contraseña en Conexion.java sea correcta."
            );
        }

        return con;
    }

    // CONSULTAR TODAS LAS PREGUNTAS
    public List<Pregunta> listar() {

        List<Pregunta> lista = new ArrayList<>();

        String sql = "SELECT * FROM cat_pregunta ORDER BY cve_pregunta";

        try (Connection con = conectar();
             PreparedStatement ps = con.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {

                Pregunta p = new Pregunta();

                p.setCvePregunta(rs.getInt("cve_pregunta"));
                p.setCveExamen(rs.getInt("cat_examen_cve_examen"));
                p.setClaveTema(rs.getInt("cat_temas_unidad_clave_tema"));
                p.setPregunta(rs.getString("pregunta"));
                p.setImagen(rs.getString("imagen"));
                p.setNivelDificultad(rs.getString("nivel_dificultad"));
                p.setValorPuntos(rs.getDouble("valor_puntos"));
                p.setRetroalimentacion(rs.getString("retroalimentacion"));
                p.setTiempoEstimado(rs.getInt("tiempo_estimado"));
                p.setEstatus(rs.getString("estatus"));

                lista.add(p);
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }

        return lista;
    }

    // INSERTAR UNA PREGUNTA
    public boolean insertar(Pregunta p) {

        String sql = """
            INSERT INTO cat_pregunta
            (cat_examen_cve_examen, cat_temas_unidad_clave_tema,
             pregunta, imagen, nivel_dificultad, valor_puntos,
             retroalimentacion, tiempo_estimado, estatus)
            VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?)
            """;

        try (Connection con = conectar();
             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setInt(1, p.getCveExamen());
            ps.setInt(2, p.getClaveTema());
            ps.setString(3, p.getPregunta());
            ps.setString(4, p.getImagen());
            ps.setString(5, p.getNivelDificultad());
            ps.setDouble(6, p.getValorPuntos());
            ps.setString(7, p.getRetroalimentacion());
            ps.setInt(8, p.getTiempoEstimado());
            ps.setString(9, p.getEstatus());

            return ps.executeUpdate() > 0;

        } catch (SQLException e) {
            e.printStackTrace();
            throw new RuntimeException(
                "No se pudo guardar la pregunta.\n" + e.getMessage()
            );
        }
    }

    // ACTUALIZAR UNA PREGUNTA
    public boolean actualizar(Pregunta p) {

        String sql = """
            UPDATE cat_pregunta
            SET cat_examen_cve_examen = ?,
                cat_temas_unidad_clave_tema = ?,
                pregunta = ?,
                imagen = ?,
                nivel_dificultad = ?,
                valor_puntos = ?,
                retroalimentacion = ?,
                tiempo_estimado = ?,
                estatus = ?
            WHERE cve_pregunta = ?
            """;

        try (Connection con = conectar();
             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setInt(1, p.getCveExamen());
            ps.setInt(2, p.getClaveTema());
            ps.setString(3, p.getPregunta());
            ps.setString(4, p.getImagen());
            ps.setString(5, p.getNivelDificultad());
            ps.setDouble(6, p.getValorPuntos());
            ps.setString(7, p.getRetroalimentacion());
            ps.setInt(8, p.getTiempoEstimado());
            ps.setString(9, p.getEstatus());

            ps.setInt(10, p.getCvePregunta());

            return ps.executeUpdate() > 0;

        } catch (SQLException e) {
            e.printStackTrace();
            throw new RuntimeException(
                "No se pudo actualizar la pregunta.\n" + e.getMessage()
            );
        }
    }

    // ELIMINAR UNA PREGUNTA
    public boolean eliminar(int cvePregunta) {

        String sql =
            "DELETE FROM cat_pregunta WHERE cve_pregunta = ?";

        try (Connection con = conectar();
             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setInt(1, cvePregunta);

            return ps.executeUpdate() > 0;

        } catch (SQLException e) {
            e.printStackTrace();
            throw new RuntimeException(
                "No se pudo eliminar la pregunta.\n" + e.getMessage()
            );
        }
    }

    // OBTENER LOS VALORES DE UNA COLUMNA ENUM (nivel_dificultad, estatus)
    // Asi los combos del formulario siempre coinciden con la base de datos
    public List<String> obtenerValoresEnum(String columna) {

        List<String> valores = new ArrayList<>();

        String sql = """
            SELECT COLUMN_TYPE
            FROM information_schema.COLUMNS
            WHERE TABLE_SCHEMA = DATABASE()
              AND TABLE_NAME = 'cat_pregunta'
              AND COLUMN_NAME = ?
            """;

        try (Connection con = conectar();
             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setString(1, columna);

            try (ResultSet rs = ps.executeQuery()) {

                if (rs.next()) {

                    // Ejemplo: enum('facil','media','dificil')
                    String tipo = rs.getString(1);

                    int inicio = tipo.indexOf('(');
                    int fin = tipo.lastIndexOf(')');

                    if (tipo.toLowerCase().startsWith("enum")
                            && inicio != -1 && fin > inicio) {

                        String[] partes =
                            tipo.substring(inicio + 1, fin).split(",");

                        for (String parte : partes) {
                            valores.add(
                                parte.trim().replaceAll("^'|'$", "")
                            );
                        }
                    }
                }
            }

        } catch (SQLException | RuntimeException e) {
            e.printStackTrace();
        }

        return valores;
    }

    // ===== LISTAS PARA LOS COMBOS (para no escribir claves a mano) =====

    // Examenes: "Programacion Orientada a Objetos · U1 · diagnostico · Sep-Dic 2026"
    public List<Opcion> listarExamenes() {

        List<Opcion> lista = new ArrayList<>();

        String sql = """
            SELECT e.cve_examen, e.tipo_examen, e.periodo, e.anio,
                   m.nombre AS materia, u.numero_unidad
            FROM cat_examen e
            LEFT JOIN cat_materias m
                   ON m.clave_materia = e.cat_materias_clave_materia
            LEFT JOIN cat_unidades_materia u
                   ON u.cve_unidad = e.cat_unidades_materia_cve_unidad
            ORDER BY e.cve_examen
            """;

        try (Connection con = conectar();
             PreparedStatement ps = con.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {

                String texto = rs.getString("materia")
                        + " · U" + rs.getInt("numero_unidad")
                        + " · " + rs.getString("tipo_examen")
                        + " · " + nombrePeriodo(rs.getString("periodo"))
                        + " " + rs.getInt("anio");

                lista.add(new Opcion(rs.getInt("cve_examen"), texto));
            }

        } catch (SQLException e) {
            e.printStackTrace();
            throw new RuntimeException(
                "No se pudieron cargar los exámenes.\n" + e.getMessage()
            );
        }

        return lista;
    }

    // Temas: "Herencia (U2 · POO)"
    public List<Opcion> listarTemas() {

        List<Opcion> lista = new ArrayList<>();

        String sql = """
            SELECT t.clave_tema, t.nombre, u.numero_unidad,
                   u.cat_materias_clave_materia AS materia
            FROM cat_temas_unidad t
            LEFT JOIN cat_unidades_materia u
                   ON u.cve_unidad = t.cat_unidades_materia_cve_unidad
            ORDER BY u.cat_materias_clave_materia, u.numero_unidad, t.nombre
            """;

        try (Connection con = conectar();
             PreparedStatement ps = con.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {

                String texto = rs.getString("nombre")
                        + " (U" + rs.getInt("numero_unidad")
                        + " · " + rs.getString("materia") + ")";

                lista.add(new Opcion(rs.getInt("clave_tema"), texto));
            }

        } catch (SQLException e) {
            e.printStackTrace();
            throw new RuntimeException(
                "No se pudieron cargar los temas.\n" + e.getMessage()
            );
        }

        return lista;
    }

    // ea / ma / sd -> nombre del cuatrimestre
    private String nombrePeriodo(String periodo) {

        if (periodo == null) {
            return "";
        }

        return switch (periodo) {
            case "ea" -> "Ene-Abr";
            case "ma" -> "May-Ago";
            case "sd" -> "Sep-Dic";
            default -> periodo;
        };
    }
}
