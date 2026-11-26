/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package modelo;

import java.time.LocalDate;

/**
 * Modelo de la tabla cat_carrera.
 * Basado en el CRUD de cat_carrera de Eduardo Cortes (equipo gamma),
 * adaptado a la estructura del proyecto para la integracion.
 *
 * @author Eduardo Cortes (gamma) / integracion: Alberto (epsilon)
 */
public class Carrera {

    private int cveCarrera;
    private int cvePlanEstudios;        // FK -> cat_plan_estudios.cve
    private String estatus;
    private String nombre;
    private LocalDate fechaActivacion;
    private LocalDate fechaInactiva;
    private LocalDate fechaLiquidacion;

    public Carrera() {
    }

    public Carrera(int cveCarrera, int cvePlanEstudios, String estatus,
            String nombre, LocalDate fechaActivacion,
            LocalDate fechaInactiva, LocalDate fechaLiquidacion) {

        this.cveCarrera = cveCarrera;
        this.cvePlanEstudios = cvePlanEstudios;
        this.estatus = estatus;
        this.nombre = nombre;
        this.fechaActivacion = fechaActivacion;
        this.fechaInactiva = fechaInactiva;
        this.fechaLiquidacion = fechaLiquidacion;
    }

    public int getCveCarrera() {
        return cveCarrera;
    }

    public void setCveCarrera(int cveCarrera) {
        this.cveCarrera = cveCarrera;
    }

    public int getCvePlanEstudios() {
        return cvePlanEstudios;
    }

    public void setCvePlanEstudios(int cvePlanEstudios) {
        this.cvePlanEstudios = cvePlanEstudios;
    }

    public String getEstatus() {
        return estatus;
    }

    public void setEstatus(String estatus) {
        this.estatus = estatus;
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public LocalDate getFechaActivacion() {
        return fechaActivacion;
    }

    public void setFechaActivacion(LocalDate fechaActivacion) {
        this.fechaActivacion = fechaActivacion;
    }

    public LocalDate getFechaInactiva() {
        return fechaInactiva;
    }

    public void setFechaInactiva(LocalDate fechaInactiva) {
        this.fechaInactiva = fechaInactiva;
    }

    public LocalDate getFechaLiquidacion() {
        return fechaLiquidacion;
    }

    public void setFechaLiquidacion(LocalDate fechaLiquidacion) {
        this.fechaLiquidacion = fechaLiquidacion;
    }
}
