// CRUD CAT_CARRERA EDUARDO CORTES - MODULO MODELO

package MODELO;

import java.time.LocalDate;

/**
 * Entidad que representa la tabla cat_carrera.
 * Contiene unicamente los atributos (atributos) de la tabla.
 */
public class CatCarrera {

    // --- Atributos (mismos nombres de la tabla cat_carrera) ---
    private int cveCarrera;                 // cve_carrera
    private int catPlanEstudiosCve;         // cat_plan_estudios_cve
    private String estatus;                 // estatus
    private String nombre;                  // nombre
    private LocalDate fechaActivacion;      // fecha_activacion
    private LocalDate fechaInactiva;        // fecha_inactiva
    private LocalDate fechaLiquidacion;     // fecha_liquidacion

    // --- Constructor vacio ---
    public CatCarrera() {
    }

    // --- Constructor completo ---
    public CatCarrera(int cveCarrera, int catPlanEstudiosCve, String estatus, String nombre,
                      LocalDate fechaActivacion, LocalDate fechaInactiva, LocalDate fechaLiquidacion) {
        this.cveCarrera = cveCarrera;
        this.catPlanEstudiosCve = catPlanEstudiosCve;
        this.estatus = estatus;
        this.nombre = nombre;
        this.fechaActivacion = fechaActivacion;
        this.fechaInactiva = fechaInactiva;
        this.fechaLiquidacion = fechaLiquidacion;
    }

    // --- Getters y Setters ---
    public int getCveCarrera() {
        return cveCarrera;
    }

    public void setCveCarrera(int cveCarrera) {
        this.cveCarrera = cveCarrera;
    }

    public int getCatPlanEstudiosCve() {
        return catPlanEstudiosCve;
    }

    public void setCatPlanEstudiosCve(int catPlanEstudiosCve) {
        this.catPlanEstudiosCve = catPlanEstudiosCve;
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

    @Override
    public String toString() {
        return "CatCarrera{" +
                "cveCarrera=" + cveCarrera +
                ", catPlanEstudiosCve=" + catPlanEstudiosCve +
                ", estatus='" + estatus + '\'' +
                ", nombre='" + nombre + '\'' +
                ", fechaActivacion=" + fechaActivacion +
                ", fechaInactiva=" + fechaInactiva +
                ", fechaLiquidacion=" + fechaLiquidacion +
                '}';
    }
}
