// CRUD CAT_EXAMEN EDUARDO CORTES - MODULO MODELO

package MODELO_EXAMEN;

/**
 * Enumeración de la columna tipo_examen (ENUM).
 * Valores permitidos: diagnostico, unidad
 */
public enum TipoExamen {
    DIAGNOSTICO("diagnostico"),
    UNIDAD("unidad");

    private final String valor;

    TipoExamen(String valor) {
        this.valor = valor;
    }

    /** Devuelve el valor tal cual se guarda en la base de datos. */
    public String getValor() {
        return valor;
    }

    /** Convierte un texto de la BD en su enum correspondiente (sin distinguir mayúsculas). */
    public static TipoExamen desdeValor(String texto) {
        for (TipoExamen t : values()) {
            if (t.valor.equalsIgnoreCase(texto)) {
                return t;
            }
        }
        throw new IllegalArgumentException("tipo_examen no válido: " + texto);
    }

    /** Muestra el valor de la BD en los componentes Swing (JComboBox). */
    @Override
    public String toString() {
        return valor;
    }
}
