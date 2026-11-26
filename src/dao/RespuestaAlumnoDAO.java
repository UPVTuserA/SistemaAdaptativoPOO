/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package dao;

import java.sql.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import modelo.Opcion;
import modelo.RespuestaAlumno;
import sistemaadaptativopoo.Conexion;

/**
 * DAO de la tabla respuesta_alumno
 *
 * @author Alberto
 */
public class RespuestaAlumnoDAO {

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

    // CONSULTAR TODAS LAS RESPUESTAS
    public List<RespuestaAlumno> listar() {

        List<RespuestaAlumno> lista = new ArrayList<>();

        String sql = "SELECT * FROM respuesta_alumno ORDER BY cve_respuesta_alumno";

        try (Connection con = conectar();
             PreparedStatement ps = con.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {

                RespuestaAlumno r = new RespuestaAlumno();

                r.setCveRespuestaAlumno(rs.getInt("cve_respuesta_alumno"));
                r.setCveExamenAlumno(rs.getInt("examen_alumno_cve"));
                r.setCvePregunta(rs.getInt("cat_pregunta_cve_pregunta"));
                r.setRespuestaTexto(rs.getString("respuesta_texto"));
                r.setEsCorrecta(rs.getInt("es_correcta") == 1);
                r.setPuntosObtenidos(rs.getDouble("puntos_obtenidos"));

                Timestamp fecha = rs.getTimestamp("fecha_respuesta");
                r.setFechaRespuesta(
                    fecha != null ? fecha.toLocalDateTime() : null
                );

                lista.add(r);
            }

        } catch (SQLException e) {
            e.printStackTrace();
            throw new RuntimeException(
                "No se pudieron consultar las respuestas.\n" + e.getMessage()
            );
        }

        return lista;
    }

    // INSERTAR UNA RESPUESTA
    public boolean insertar(RespuestaAlumno r) {

        String sql = """
            INSERT INTO respuesta_alumno
            (examen_alumno_cve, cat_pregunta_cve_pregunta,
             respuesta_texto, es_correcta, puntos_obtenidos,
             fecha_respuesta)
            VALUES (?, ?, ?, ?, ?, ?)
            """;

        try (Connection con = conectar();
             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setInt(1, r.getCveExamenAlumno());
            ps.setInt(2, r.getCvePregunta());
            ps.setString(3, r.getRespuestaTexto());
            ps.setInt(4, r.isEsCorrecta() ? 1 : 0);
            ps.setDouble(5, r.getPuntosObtenidos());
            ps.setTimestamp(6, convertirFecha(r.getFechaRespuesta()));

            return ps.executeUpdate() > 0;

        } catch (SQLException e) {
            e.printStackTrace();
            throw new RuntimeException(
                "No se pudo guardar la respuesta.\n" + e.getMessage()
            );
        }
    }

    // ACTUALIZAR UNA RESPUESTA
    public boolean actualizar(RespuestaAlumno r) {

        String sql = """
            UPDATE respuesta_alumno
            SET examen_alumno_cve = ?,
                cat_pregunta_cve_pregunta = ?,
                respuesta_texto = ?,
                es_correcta = ?,
                puntos_obtenidos = ?,
                fecha_respuesta = ?
            WHERE cve_respuesta_alumno = ?
            """;

        try (Connection con = conectar();
             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setInt(1, r.getCveExamenAlumno());
            ps.setInt(2, r.getCvePregunta());
            ps.setString(3, r.getRespuestaTexto());
            ps.setInt(4, r.isEsCorrecta() ? 1 : 0);
            ps.setDouble(5, r.getPuntosObtenidos());
            ps.setTimestamp(6, convertirFecha(r.getFechaRespuesta()));

            ps.setInt(7, r.getCveRespuestaAlumno());

            return ps.executeUpdate() > 0;

        } catch (SQLException e) {
            e.printStackTrace();
            throw new RuntimeException(
                "No se pudo actualizar la respuesta.\n" + e.getMessage()
            );
        }
    }

    // ELIMINAR UNA RESPUESTA
    public boolean eliminar(int cveRespuestaAlumno) {

        String sql =
            "DELETE FROM respuesta_alumno WHERE cve_respuesta_alumno = ?";

        try (Connection con = conectar();
             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setInt(1, cveRespuestaAlumno);

            return ps.executeUpdate() > 0;

        } catch (SQLException e) {
            e.printStackTrace();
            throw new RuntimeException(
                "No se pudo eliminar la respuesta.\n" + e.getMessage()
            );
        }
    }

    private Timestamp convertirFecha(LocalDateTime fecha) {

        if (fecha == null) {
            return null;
        }

        return Timestamp.valueOf(fecha);
    }

    // ===== LISTAS PARA LOS COMBOS (para no escribir claves a mano) =====

    // Examenes presentados: "2024001 · Alberto Jimenez · POO U1 (intento 1)"
    public List<Opcion> listarExamenesAlumno() {

        List<Opcion> lista = new ArrayList<>();

        String sql = """
            SELECT ea.cve, ea.cat_estudiantes_matricula AS matricula,
                   s.nombre, s.paterno,
                   ea.cat_materias_clave_materia AS materia,
                   ea.numero_unidad, ea.numero_intento
            FROM examen_alumno ea
            LEFT JOIN cat_estudiantes s
                   ON s.matricula = ea.cat_estudiantes_matricula
            ORDER BY ea.cve
            """;

        try (Connection con = conectar();
             PreparedStatement ps = con.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {

                String alumno = (rs.getString("nombre") == null ? "" : rs.getString("nombre"))
                        + " " + (rs.getString("paterno") == null ? "" : rs.getString("paterno"));

                String texto = rs.getString("matricula")
                        + " · " + alumno.trim()
                        + " · " + rs.getString("materia")
                        + " U" + rs.getInt("numero_unidad");

                int intento = rs.getInt("numero_intento");

                if (intento > 0) {
                    texto += " (intento " + intento + ")";
                }

                lista.add(new Opcion(rs.getInt("cve"), texto));
            }

        } catch (SQLException e) {
            e.printStackTrace();
            throw new RuntimeException(
                "No se pudieron cargar los exámenes de los alumnos.\n" + e.getMessage()
            );
        }

        return lista;
    }

    // Preguntas: muestra el texto de la pregunta y guarda sus puntos
    public List<Opcion> listarPreguntas() {

        List<Opcion> lista = new ArrayList<>();

        String sql = """
            SELECT cve_pregunta, pregunta, valor_puntos
            FROM cat_pregunta
            ORDER BY cve_pregunta
            """;

        try (Connection con = conectar();
             PreparedStatement ps = con.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {

                String texto = rs.getString("pregunta");

                if (texto == null) {
                    texto = "(sin texto)";
                } else if (texto.length() > 70) {
                    texto = texto.substring(0, 67) + "...";
                }

                lista.add(new Opcion(
                        rs.getInt("cve_pregunta"),
                        texto,
                        rs.getDouble("valor_puntos")
                ));
            }

        } catch (SQLException e) {
            e.printStackTrace();
            throw new RuntimeException(
                "No se pudieron cargar las preguntas.\n" + e.getMessage()
            );
        }

        return lista;
    }
}
