/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package modelo;

/**
 * Modelo de la tabla cat_pregunta
 *
 * @author Alberto
 */
public class Pregunta {

    private int cvePregunta;
    private int cveExamen;          // FK -> cat_examen.cve_examen
    private int claveTema;          // FK -> cat_temas_unidad.clave_tema
    private String pregunta;
    private String imagen;
    private String nivelDificultad;
    private double valorPuntos;
    private String retroalimentacion;
    private int tiempoEstimado;
    private String estatus;

    public Pregunta() {
    }

    public Pregunta(int cvePregunta, int cveExamen, int claveTema,
            String pregunta, String imagen, String nivelDificultad,
            double valorPuntos, String retroalimentacion,
            int tiempoEstimado, String estatus) {

        this.cvePregunta = cvePregunta;
        this.cveExamen = cveExamen;
        this.claveTema = claveTema;
        this.pregunta = pregunta;
        this.imagen = imagen;
        this.nivelDificultad = nivelDificultad;
        this.valorPuntos = valorPuntos;
        this.retroalimentacion = retroalimentacion;
        this.tiempoEstimado = tiempoEstimado;
        this.estatus = estatus;
    }

    public int getCvePregunta() {
        return cvePregunta;
    }

    public void setCvePregunta(int cvePregunta) {
        this.cvePregunta = cvePregunta;
    }

    public int getCveExamen() {
        return cveExamen;
    }

    public void setCveExamen(int cveExamen) {
        this.cveExamen = cveExamen;
    }

    public int getClaveTema() {
        return claveTema;
    }

    public void setClaveTema(int claveTema) {
        this.claveTema = claveTema;
    }

    public String getPregunta() {
        return pregunta;
    }

    public void setPregunta(String pregunta) {
        this.pregunta = pregunta;
    }

    public String getImagen() {
        return imagen;
    }

    public void setImagen(String imagen) {
        this.imagen = imagen;
    }

    public String getNivelDificultad() {
        return nivelDificultad;
    }

    public void setNivelDificultad(String nivelDificultad) {
        this.nivelDificultad = nivelDificultad;
    }

    public double getValorPuntos() {
        return valorPuntos;
    }

    public void setValorPuntos(double valorPuntos) {
        this.valorPuntos = valorPuntos;
    }

    public String getRetroalimentacion() {
        return retroalimentacion;
    }

    public void setRetroalimentacion(String retroalimentacion) {
        this.retroalimentacion = retroalimentacion;
    }

    public int getTiempoEstimado() {
        return tiempoEstimado;
    }

    public void setTiempoEstimado(int tiempoEstimado) {
        this.tiempoEstimado = tiempoEstimado;
    }

    public String getEstatus() {
        return estatus;
    }

    public void setEstatus(String estatus) {
        this.estatus = estatus;
    }
}
