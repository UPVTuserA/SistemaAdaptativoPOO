/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package modelo;

/**
 * Elemento para las listas desplegables (combos).
 * Guarda la clave (id) pero muestra un texto facil de leer.
 * Ejemplo: id = 3, texto = "Herencia"
 *
 * @author Alberto
 */
public class Opcion {

    private final int id;
    private final String texto;
    private final double valor;   // dato extra opcional (ej. puntos de la pregunta)

    public Opcion(int id, String texto) {
        this(id, texto, 0);
    }

    public Opcion(int id, String texto, double valor) {
        this.id = id;
        this.texto = texto;
        this.valor = valor;
    }

    public int getId() {
        return id;
    }

    public String getTexto() {
        return texto;
    }

    public double getValor() {
        return valor;
    }

    // Lo que se ve en el combo
    @Override
    public String toString() {
        return texto;
    }
}
