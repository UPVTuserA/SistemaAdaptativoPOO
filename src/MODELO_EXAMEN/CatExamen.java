// CRUD CAT_EXAMEN EDUARDO CORTES - MODULO MODELO

package MODELO_EXAMEN;

/**
 * Entidad que representa la tabla cat_examen.
 * Contiene unicamente los atributos de la tabla.
 */
public class CatExamen {

    // --- Atributos (mismos nombres de la tabla cat_examen) ---
    private int cveExamen;                          // cve_examen (PK, autoincremental)
    private int catProfesorCveProfesor;             // cat_profesor_cve_profesor (FK)
    private String catMateriasClaveMateria;         // cat_materias_clave_materia (FK, VARCHAR 45)
    private int catUnidadesMateriaCveUnidad;        // cat_unidades_materia_cve_unidad (FK)
    private TipoExamen tipoExamen;                  // tipo_examen (ENUM: diagnostico, unidad)
    private Periodo periodo;                        // periodo (ENUM: ea, ma, sd)
    private int anio;                               // anio

    // --- Constructor vacio ---
    public CatExamen() {
    }

    // --- Constructor completo ---
    public CatExamen(int cveExamen, int catProfesorCveProfesor, String catMateriasClaveMateria,
                     int catUnidadesMateriaCveUnidad, TipoExamen tipoExamen, Periodo periodo, int anio) {
        this.cveExamen = cveExamen;
        this.catProfesorCveProfesor = catProfesorCveProfesor;
        this.catMateriasClaveMateria = catMateriasClaveMateria;
        this.catUnidadesMateriaCveUnidad = catUnidadesMateriaCveUnidad;
        this.tipoExamen = tipoExamen;
        this.periodo = periodo;
        this.anio = anio;
    }

    // --- Getters y Setters ---
    public int getCveExamen() {
        return cveExamen;
    }

    public void setCveExamen(int cveExamen) {
        this.cveExamen = cveExamen;
    }

    public int getCatProfesorCveProfesor() {
        return catProfesorCveProfesor;
    }

    public void setCatProfesorCveProfesor(int catProfesorCveProfesor) {
        this.catProfesorCveProfesor = catProfesorCveProfesor;
    }

    public String getCatMateriasClaveMateria() {
        return catMateriasClaveMateria;
    }

    public void setCatMateriasClaveMateria(String catMateriasClaveMateria) {
        this.catMateriasClaveMateria = catMateriasClaveMateria;
    }

    public int getCatUnidadesMateriaCveUnidad() {
        return catUnidadesMateriaCveUnidad;
    }

    public void setCatUnidadesMateriaCveUnidad(int catUnidadesMateriaCveUnidad) {
        this.catUnidadesMateriaCveUnidad = catUnidadesMateriaCveUnidad;
    }

    public TipoExamen getTipoExamen() {
        return tipoExamen;
    }

    public void setTipoExamen(TipoExamen tipoExamen) {
        this.tipoExamen = tipoExamen;
    }

    public Periodo getPeriodo() {
        return periodo;
    }

    public void setPeriodo(Periodo periodo) {
        this.periodo = periodo;
    }

    public int getAnio() {
        return anio;
    }

    public void setAnio(int anio) {
        this.anio = anio;
    }

    @Override
    public String toString() {
        return "CatExamen{" +
                "cveExamen=" + cveExamen +
                ", catProfesorCveProfesor=" + catProfesorCveProfesor +
                ", catMateriasClaveMateria='" + catMateriasClaveMateria + '\'' +
                ", catUnidadesMateriaCveUnidad=" + catUnidadesMateriaCveUnidad +
                ", tipoExamen=" + tipoExamen +
                ", periodo=" + periodo +
                ", anio=" + anio +
                '}';
    }
}
