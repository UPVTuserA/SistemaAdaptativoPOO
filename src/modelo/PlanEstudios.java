/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package modelo;
//package com.sistemaadaptativo.modelo;

import java.time.LocalDate;

public class PlanEstudios {

    private int cve;
    private String nombre;
    private String estatus;
    private LocalDate fechaActivacion;
    private LocalDate fechaInactiva;
    private LocalDate fechaLiquidacion;

    public PlanEstudios() {
    }

    public PlanEstudios(int cve, String nombre, String estatus,
            LocalDate fechaActivacion, LocalDate fechaInactiva,
            LocalDate fechaLiquidacion) {

        this.cve = cve;
        this.nombre = nombre;
        this.estatus = estatus;
        this.fechaActivacion = fechaActivacion;
        this.fechaInactiva = fechaInactiva;
        this.fechaLiquidacion = fechaLiquidacion;
    }

    public int getCve() {
        return cve;
    }

    public void setCve(int cve) {
        this.cve = cve;
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public String getEstatus() {
        return estatus;
    }

    public void setEstatus(String estatus) {
        this.estatus = estatus;
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
