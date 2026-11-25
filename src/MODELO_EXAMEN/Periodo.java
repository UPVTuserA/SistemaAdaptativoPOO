// CRUD CAT_EXAMEN EDUARDO CORTES - MODULO MODELO

package MODELO_EXAMEN;

/**
 * Enumeración de la columna periodo (ENUM).
 * Valores permitidos: ea, ma, sd
 */
public enum Periodo {
    EA("ea"),
    MA("ma"),
    SD("sd");

    private final String valor;

    Periodo(String valor) {
        this.valor = valor;
    }

    /** Devuelve el valor tal cual se guarda en la base de datos. */
    public String getValor() {
        return valor;
    }

    /** Convierte un texto de la BD en su enum correspondiente (sin distinguir mayúsculas). */
    public static Periodo desdeValor(String texto) {
        for (Periodo p : values()) {
            if (p.valor.equalsIgnoreCase(texto)) {
                return p;
            }
        }
        throw new IllegalArgumentException("periodo no válido: " + texto);
    }

    /** Muestra el valor de la BD en los componentes Swing (JComboBox). */
    @Override
    public String toString() {
        return valor;
    }
}
