/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package dao;

//package com.sistemaadaptativo.dao;

//import com.sistemaadaptativo.conexion.Conexion;
//import com.sistemaadaptativo.modelo.PlanEstudios;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import modelo.PlanEstudios;
import sistemaadaptativopoo.Conexion;

public class PlanEstudiosDAO {

    // CONSULTAR TODOS LOS PLANES
    public List<PlanEstudios> listar() {

        List<PlanEstudios> lista = new ArrayList<>();

        String sql = "SELECT * FROM cat_plan_estudios ORDER BY cve";

        try (Connection con = Conexion.conectar();
             PreparedStatement ps = con.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {

                PlanEstudios plan = new PlanEstudios();

                plan.setCve(rs.getInt("cve"));
                plan.setNombre(rs.getString("nombre"));
                plan.setEstatus(rs.getString("estatus"));

                Date fecha = rs.getDate("fecha_activacion");
                plan.setFechaActivacion(
                    fecha != null ? fecha.toLocalDate() : null
                );

                fecha = rs.getDate("fecha_inactiva");
                plan.setFechaInactiva(
                    fecha != null ? fecha.toLocalDate() : null
                );

                fecha = rs.getDate("fecha_liquidacion");
                plan.setFechaLiquidacion(
                    fecha != null ? fecha.toLocalDate() : null
                );

                lista.add(plan);
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }

        return lista;
    }
    
    // INSERTAR UN PLAN DE ESTUDIOS
    public boolean insertar(PlanEstudios plan) {

        String sql = """
            INSERT INTO cat_plan_estudios
            (nombre, estatus, fecha_activacion,
             fecha_inactiva, fecha_liquidacion)
            VALUES (?, ?, ?, ?, ?)
            """;

        try (Connection con = Conexion.conectar();
             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setString(1, plan.getNombre());
            ps.setString(2, plan.getEstatus());

            ps.setDate(3, convertirFecha(plan.getFechaActivacion()));
            ps.setDate(4, convertirFecha(plan.getFechaInactiva()));
            ps.setDate(5, convertirFecha(plan.getFechaLiquidacion()));

            return ps.executeUpdate() > 0;

        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    private Date convertirFecha(java.time.LocalDate fecha) {

        if (fecha == null) {
            return null;
        }

        return Date.valueOf(fecha);
    }
    
    // ACTUALIZAR UN PLAN DE ESTUDIOS
    public boolean actualizar(PlanEstudios plan) {

        String sql = """
            UPDATE cat_plan_estudios
            SET nombre = ?,
                estatus = ?,
                fecha_activacion = ?,
                fecha_inactiva = ?,
                fecha_liquidacion = ?
            WHERE cve = ?
            """;

        try (Connection con = Conexion.conectar();
             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setString(1, plan.getNombre());
            ps.setString(2, plan.getEstatus());

            ps.setDate(3, convertirFecha(plan.getFechaActivacion()));
            ps.setDate(4, convertirFecha(plan.getFechaInactiva()));
            ps.setDate(5, convertirFecha(plan.getFechaLiquidacion()));

            ps.setInt(6, plan.getCve());

            return ps.executeUpdate() > 0;

        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }
    
    // ELIMINAR UN PLAN DE ESTUDIOS
    public boolean eliminar(int cve) {

        String sql =
            "DELETE FROM cat_plan_estudios WHERE cve = ?";

        try (Connection con = Conexion.conectar();
             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setInt(1, cve);

            return ps.executeUpdate() > 0;

        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }
}


