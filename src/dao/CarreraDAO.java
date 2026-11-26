/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package dao;

import java.sql.*;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import modelo.Carrera;
import modelo.Opcion;
import sistemaadaptativopoo.Conexion;

/**
 * DAO de la tabla cat_carrera.
 * Basado en el CRUD de consola de Eduardo Cortes (equipo gamma):
 * mismas consultas, ahora con la Conexion del proyecto y devolviendo
 * objetos para usarlos en el formulario.
 *
 * @author Eduardo Cortes (gamma) / integracion: Alberto (epsilon)
 */
public class CarreraDAO {

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

    // READ: consultar todas las carreras
    public List<Carrera> listar() {

        List<Carrera> lista = new ArrayList<>();

        String sql = "SELECT * FROM cat_carrera ORDER BY cve_carrera";

        try (Connection con = conectar();
             PreparedStatement ps = con.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {

                Carrera c = new Carrera();

                c.setCveCarrera(rs.getInt("cve_carrera"));
                c.setCvePlanEstudios(rs.getInt("cat_plan_estudios_cve"));
                c.setEstatus(rs.getString("estatus"));
                c.setNombre(rs.getString("nombre"));
                c.setFechaActivacion(aFecha(rs.getDate("fecha_activacion")));
                c.setFechaInactiva(aFecha(rs.getDate("fecha_inactiva")));
                c.setFechaLiquidacion(aFecha(rs.getDate("fecha_liquidacion")));

                lista.add(c);
            }

        } catch (SQLException e) {
            e.printStackTrace();
            throw new RuntimeException(
                "No se pudieron consultar las carreras.\n" + e.getMessage()
            );
        }

        return lista;
    }

    // CREATE: insertar (la clave la pone MySQL con AUTO_INCREMENT)
    public boolean insertar(Carrera c) {

        String sql = """
            INSERT INTO cat_carrera
            (cat_plan_estudios_cve, estatus, nombre,
             fecha_activacion, fecha_inactiva, fecha_liquidacion)
            VALUES (?, ?, ?, ?, ?, ?)
            """;

        try (Connection con = conectar();
             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setInt(1, c.getCvePlanEstudios());
            ps.setString(2, c.getEstatus());
            ps.setString(3, c.getNombre());
            ps.setObject(4, c.getFechaActivacion());
            ps.setObject(5, c.getFechaInactiva());
            ps.setObject(6, c.getFechaLiquidacion());

            return ps.executeUpdate() > 0;

        } catch (SQLException e) {
            e.printStackTrace();
            throw new RuntimeException(
                "No se pudo guardar la carrera.\n" + e.getMessage()
            );
        }
    }

    // UPDATE: actualizar
    public boolean actualizar(Carrera c) {

        String sql = """
            UPDATE cat_carrera
            SET cat_plan_estudios_cve = ?,
                estatus = ?,
                nombre = ?,
                fecha_activacion = ?,
                fecha_inactiva = ?,
                fecha_liquidacion = ?
            WHERE cve_carrera = ?
            """;

        try (Connection con = conectar();
             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setInt(1, c.getCvePlanEstudios());
            ps.setString(2, c.getEstatus());
            ps.setString(3, c.getNombre());
            ps.setObject(4, c.getFechaActivacion());
            ps.setObject(5, c.getFechaInactiva());
            ps.setObject(6, c.getFechaLiquidacion());
            ps.setInt(7, c.getCveCarrera());

            return ps.executeUpdate() > 0;

        } catch (SQLException e) {
            e.printStackTrace();
            throw new RuntimeException(
                "No se pudo actualizar la carrera.\n" + e.getMessage()
            );
        }
    }

    // DELETE: eliminar
    public boolean eliminar(int cveCarrera) {

        String sql = "DELETE FROM cat_carrera WHERE cve_carrera = ?";

        try (Connection con = conectar();
             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setInt(1, cveCarrera);

            return ps.executeUpdate() > 0;

        } catch (SQLException e) {
            e.printStackTrace();
            throw new RuntimeException(
                "No se pudo eliminar la carrera.\n" + e.getMessage()
            );
        }
    }

    // Lista de planes de estudio para el combo: "Plan 2024 (activa)"
    public List<Opcion> listarPlanes() {

        List<Opcion> lista = new ArrayList<>();

        String sql = "SELECT cve, nombre, estatus FROM cat_plan_estudios ORDER BY nombre";

        try (Connection con = conectar();
             PreparedStatement ps = con.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {

                String texto = rs.getString("nombre");

                if (rs.getString("estatus") != null) {
                    texto += " (" + rs.getString("estatus") + ")";
                }

                lista.add(new Opcion(rs.getInt("cve"), texto));
            }

        } catch (SQLException e) {
            e.printStackTrace();
            throw new RuntimeException(
                "No se pudieron cargar los planes de estudio.\n" + e.getMessage()
            );
        }

        return lista;
    }

    // Valores del ENUM estatus (activa, liquidacion, inactiva)
    public List<String> obtenerValoresEstatus() {

        List<String> valores = new ArrayList<>();

        String sql = """
            SELECT COLUMN_TYPE
            FROM information_schema.COLUMNS
            WHERE TABLE_SCHEMA = DATABASE()
              AND TABLE_NAME = 'cat_carrera'
              AND COLUMN_NAME = 'estatus'
            """;

        try (Connection con = conectar();
             PreparedStatement ps = con.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {

            if (rs.next()) {

                String tipo = rs.getString(1);
                int inicio = tipo.indexOf('(');
                int fin = tipo.lastIndexOf(')');

                if (tipo.toLowerCase().startsWith("enum") && inicio != -1 && fin > inicio) {
                    for (String parte : tipo.substring(inicio + 1, fin).split(",")) {
                        valores.add(parte.trim().replaceAll("^'|'$", ""));
                    }
                }
            }

        } catch (SQLException | RuntimeException e) {
            e.printStackTrace();
        }

        return valores;
    }

    private LocalDate aFecha(java.sql.Date fecha) {
        return fecha == null ? null : fecha.toLocalDate();
    }
}
