/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package dao;


import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class MateriaDao {

    // 1. ALTA (Insertar nueva materia)
    public boolean insertarMateria(Connection conn, String claveMateria, int nombre, int numeroUnidades, int cveCarrera) {
        String sql = "INSERT INTO cat_materias (clave_materia, nombre, numero_unidades, cat_carrera_cve_carrera) VALUES (?, ?, ?, ?)";
        
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, claveMateria);
            ps.setInt(2, nombre); // Si en MySQL es VARCHAR, usa: ps.setString(2, nombre);
            ps.setInt(3, numeroUnidades);
            ps.setInt(4, cveCarrera);
            
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            System.err.println("Error al dar de alta la materia: " + e.getMessage());
            return false;
        }
    }

    // 2. BAJA (Eliminar materia por su clave_materia)
    public boolean eliminarMateria(Connection conn, String claveMateria) {
        String sql = "DELETE FROM cat_materias WHERE clave_materia = ?";
        
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, claveMateria);
            
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            System.err.println("Error al dar de baja la materia: " + e.getMessage());
            return false;
        }
    }

    // 3. CONSULTA GENERAL (Obtener todas las materias)
    public List<String[]> consultarTodas(Connection conn) {
        List<String[]> listaMaterias = new ArrayList<>();
        String sql = "SELECT clave_materia, nombre, numero_unidades, cat_carrera_cve_carrera FROM cat_materias";
        
        try (PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            
            while (rs.next()) {
                String[] fila = new String[4];
                fila[0] = rs.getString("clave_materia");
                fila[1] = String.valueOf(rs.getInt("nombre")); // Si es VARCHAR, usa: rs.getString("nombre")
                fila[2] = String.valueOf(rs.getInt("numero_unidades"));
                fila[3] = String.valueOf(rs.getInt("cat_carrera_cve_carrera"));
                listaMaterias.add(fila);
            }
        } catch (SQLException e) {
            System.err.println("Error al consultar materias: " + e.getMessage());
        }
        return listaMaterias;
    }

    // 4. CONSULTA INDIVIDUAL (Buscar una materia por su clave)
    public String[] buscarPorClave(Connection conn, String claveMateria) {
        String sql = "SELECT clave_materia, nombre, numero_unidades, cat_carrera_cve_carrera FROM cat_materias WHERE clave_materia = ?";
        
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, claveMateria);
            
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return new String[]{
                        rs.getString("clave_materia"),
                        String.valueOf(rs.getInt("nombre")),
                        String.valueOf(rs.getInt("numero_unidades")),
                        String.valueOf(rs.getInt("cat_carrera_cve_carrera"))
                    };
                }
            }
        } catch (SQLException e) {
            System.err.println("Error al buscar la materia: " + e.getMessage());
        }
        return null;
    }
}
