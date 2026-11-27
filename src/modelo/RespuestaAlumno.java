/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package modelo;

import java.time.LocalDateTime;

/**
 * Modelo de la tabla respuesta_alumno
 *
 * @author Alberto
 */
public class RespuestaAlumno {

    private int cveRespuestaAlumno;
    private int cveExamenAlumno;        // FK -> examen_alumno.cve
    private int cvePregunta;            // FK -> cat_pregunta.cve_pregunta
    private String respuestaTexto;
    private boolean esCorrecta;         // TINYINT: 1 = correcta, 0 = incorrecta
    private double puntosObtenidos;
    private LocalDateTime fechaRespuesta;

    public RespuestaAlumno() {
    }

    public RespuestaAlumno(int cveRespuestaAlumno, int cveExamenAlumno,
            int cvePregunta, String respuestaTexto, boolean esCorrecta,
            double puntosObtenidos, LocalDateTime fechaRespuesta) {

        this.cveRespuestaAlumno = cveRespuestaAlumno;
        this.cveExamenAlumno = cveExamenAlumno;
        this.cvePregunta = cvePregunta;
        this.respuestaTexto = respuestaTexto;
        this.esCorrecta = esCorrecta;
        this.puntosObtenidos = puntosObtenidos;
        this.fechaRespuesta = fechaRespuesta;
    }

    public int getCveRespuestaAlumno() {
        return cveRespuestaAlumno;
    }

    public void setCveRespuestaAlumno(int cveRespuestaAlumno) {
        this.cveRespuestaAlumno = cveRespuestaAlumno;
    }

    public int getCveExamenAlumno() {
        return cveExamenAlumno;
    }

    public void setCveExamenAlumno(int cveExamenAlumno) {
        this.cveExamenAlumno = cveExamenAlumno;
    }

    public int getCvePregunta() {
        return cvePregunta;
    }

    public void setCvePregunta(int cvePregunta) {
        this.cvePregunta = cvePregunta;
    }

    public String getRespuestaTexto() {
        return respuestaTexto;
    }

    public void setRespuestaTexto(String respuestaTexto) {
        this.respuestaTexto = respuestaTexto;
    }

    public boolean isEsCorrecta() {
        return esCorrecta;
    }

    public void setEsCorrecta(boolean esCorrecta) {
        this.esCorrecta = esCorrecta;
    }

    public double getPuntosObtenidos() {
        return puntosObtenidos;
    }

    public void setPuntosObtenidos(double puntosObtenidos) {
        this.puntosObtenidos = puntosObtenidos;
    }

    public LocalDateTime getFechaRespuesta() {
        return fechaRespuesta;
    }

    public void setFechaRespuesta(LocalDateTime fechaRespuesta) {
        this.fechaRespuesta = fechaRespuesta;
    }
}
