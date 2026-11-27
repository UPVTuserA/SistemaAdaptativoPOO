// CRUD CAT_EXAMEN EDUARDO CORTES - MODULO VISTA

package VISTA_EXAMEN;

import DAO_EXAMEN.CatExamenDAO;
import MODELO_EXAMEN.CatExamen;
import MODELO_EXAMEN.Periodo;
import MODELO_EXAMEN.TipoExamen;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.time.format.DateTimeParseException;

/**
 * Formulario Swing para el CRUD de la tabla cat_examen.
 * Solo maneja la interfaz; la logica recae en DAO y el estado en MODELO.
 */
public class FrmCatExamen extends JFrame {

    private final CatExamenDAO dao = new CatExamenDAO();

    // --- Campos del formulario ---
    private final JTextField txtCveExamen = new JTextField();
    private final JTextField txtProfesorCve = new JTextField();
    private final JTextField txtMateriaClave = new JTextField();
    private final JTextField txtUnidadCve = new JTextField();
    private final JComboBox<TipoExamen> cmbTipoExamen = new JComboBox<>(TipoExamen.values());
    private final JComboBox<Periodo> cmbPeriodo = new JComboBox<>(Periodo.values());
    private final JTextField txtAnio = new JTextField();

    private final JTable tabla = new JTable();
    private final DefaultTableModel modeloTabla = new DefaultTableModel(
            new Object[]{"cve_examen", "cat_profesor_cve_profesor", "cat_materias_clave_materia",
                    "cat_unidades_materia_cve_unidad", "tipo_examen", "periodo", "anio"}, 0) {
        @Override
        public boolean isCellEditable(int row, int column) {
            return false;
        }
    };

    public FrmCatExamen() {
        super("CRUD cat_examen - Eduardo Cortes");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(950, 600);
        setLocationRelativeTo(null);
        setLayout(new BorderLayout(10, 10));

        // --- Panel de campos ---
        JPanel pnlCampos = new JPanel(new GridLayout(4, 4, 8, 8));
        pnlCampos.setBorder(BorderFactory.createTitledBorder("Datos de cat_examen"));
        pnlCampos.add(new JLabel("cve_examen (vacío = automático):"));
        pnlCampos.add(txtCveExamen);
        pnlCampos.add(new JLabel("cat_profesor_cve_profesor:"));
        pnlCampos.add(txtProfesorCve);
        pnlCampos.add(new JLabel("cat_materias_clave_materia:"));
        pnlCampos.add(txtMateriaClave);
        pnlCampos.add(new JLabel("cat_unidades_materia_cve_unidad:"));
        pnlCampos.add(txtUnidadCve);
        pnlCampos.add(new JLabel("tipo_examen:"));
        pnlCampos.add(cmbTipoExamen);
        pnlCampos.add(new JLabel("periodo:"));
        pnlCampos.add(cmbPeriodo);
        pnlCampos.add(new JLabel("anio:"));
        pnlCampos.add(txtAnio);
        add(pnlCampos, BorderLayout.NORTH);

        // --- Tabla ---
        tabla.setModel(modeloTabla);
        add(new JScrollPane(tabla), BorderLayout.CENTER);

        // --- Botones ---
        JPanel pnlBotones = new JPanel(new FlowLayout(FlowLayout.CENTER, 10, 10));
        JButton btnInsertar = new JButton("Insertar");
        JButton btnLeer = new JButton("Leer");
        JButton btnActualizar = new JButton("Actualizar");
        JButton btnEliminar = new JButton("Eliminar");

        btnInsertar.addActionListener(e -> insertar());
        btnLeer.addActionListener(e -> leer());
        btnActualizar.addActionListener(e -> actualizar());
        btnEliminar.addActionListener(e -> eliminar());

        pnlBotones.add(btnInsertar);
        pnlBotones.add(btnLeer);
        pnlBotones.add(btnActualizar);
        pnlBotones.add(btnEliminar);
        add(pnlBotones, BorderLayout.SOUTH);

        // Carga inicial diferida para que el dialogo de error no aparezca
        // antes de que la ventana sea visible.
        SwingUtilities.invokeLater(this::leer);
    }

    // ==========================================
    // CREATE
    // ==========================================
    private void insertar() {
        CatExamen examen;
        try {
            // cve_examen es AUTO_INCREMENT: se permite dejarlo vacio
            examen = crearDesdeFormulario(false);
        } catch (IllegalArgumentException ex) {
            errorValidacion(ex.getMessage());
            return;   // NO se limpia el formulario si la validacion fallo
        }

        if (!dao.insertar(examen)) {
            errorBaseDatos("insertar", dao.getUltimoError());
            return;   // NO se limpia el formulario si la insercion fallo
        }

        limpiar();
        leer();
        JOptionPane.showMessageDialog(this, "Registro insertado exitosamente.",
                "Éxito", JOptionPane.INFORMATION_MESSAGE);
    }

    // ==========================================
    // READ
    // ==========================================
    private void leer() {
        modeloTabla.setRowCount(0);
        var lista = dao.leer();

        if (dao.getUltimoError() != null) {
            errorBaseDatos("leer los registros", dao.getUltimoError());
            return;
        }

        for (CatExamen e : lista) {
            modeloTabla.addRow(new Object[]{
                    e.getCveExamen(), e.getCatProfesorCveProfesor(), e.getCatMateriasClaveMateria(),
                    e.getCatUnidadesMateriaCveUnidad(), e.getTipoExamen().getValor(),
                    e.getPeriodo().getValor(), e.getAnio()
            });
        }
    }

    // ==========================================
    // UPDATE
    // ==========================================
    private void actualizar() {
        CatExamen examen;
        try {
            // En actualizar SI se necesita la clave
            examen = crearDesdeFormulario(true);
        } catch (IllegalArgumentException ex) {
            errorValidacion(ex.getMessage());
            return;
        }

        if (!dao.actualizar(examen)) {
            errorBaseDatos("actualizar", dao.getUltimoError());
            return;
        }

        limpiar();
        leer();
        JOptionPane.showMessageDialog(this, "Registro actualizado exitosamente.",
                "Éxito", JOptionPane.INFORMATION_MESSAGE);
    }

    // ==========================================
    // DELETE
    // ==========================================
    private void eliminar() {
        int cve;
        try {
            cve = leerEntero(txtCveExamen, "cve_examen");
        } catch (IllegalArgumentException ex) {
            errorValidacion(ex.getMessage());
            return;
        }

        if (!dao.eliminar(cve)) {
            errorBaseDatos("eliminar", dao.getUltimoError());
            return;
        }

        limpiar();
        leer();
        JOptionPane.showMessageDialog(this, "Registro eliminado exitosamente.",
                "Éxito", JOptionPane.INFORMATION_MESSAGE);
    }

    // ==========================================
    // VALIDACIONES
    // ==========================================

    /** Lee un entero obligatorio indicando exactamente cual campo fallo. */
    private int leerEntero(JTextField campo, String nombreCampo) {
        String texto = campo.getText().trim();
        if (texto.isEmpty()) {
            throw new IllegalArgumentException("El campo \"" + nombreCampo + "\" está vacío.");
        }
        try {
            return Integer.parseInt(texto);
        } catch (NumberFormatException e) {
            throw new IllegalArgumentException(
                    "El campo \"" + nombreCampo + "\" debe ser un número entero.\nValor escrito: \"" + texto + "\"");
        }
    }

    /**
     * Valida todos los campos y construye la entidad.
     * @param requiereCve true en actualizar/eliminar, false en insertar (AUTO_INCREMENT)
     * @throws IllegalArgumentException con el detalle del campo que fallo
     */
    private CatExamen crearDesdeFormulario(boolean requiereCve) {
        if (requiereCve) {
            leerEntero(txtCveExamen, "cve_examen");
        }

        int cveExamen;
        String cveTexto = txtCveExamen.getText().trim();
        if (cveTexto.isEmpty()) {
            cveExamen = 0;   // AUTO_INCREMENT lo asigna la base de datos
        } else {
            cveExamen = leerEntero(txtCveExamen, "cve_examen");
        }

        if (txtMateriaClave.getText().trim().isEmpty()) {
            throw new IllegalArgumentException("El campo \"cat_materias_clave_materia\" es obligatorio.");
        }

        return new CatExamen(
                cveExamen,
                leerEntero(txtProfesorCve, "cat_profesor_cve_profesor"),
                txtMateriaClave.getText().trim(),
                leerEntero(txtUnidadCve, "cat_unidades_materia_cve_unidad"),
                (TipoExamen) cmbTipoExamen.getSelectedItem(),
                (Periodo) cmbPeriodo.getSelectedItem(),
                leerEntero(txtAnio, "anio")
        );
    }

    // ==========================================
    // DIALOGOS DE ERROR
    // ==========================================
    private void errorValidacion(String detalle) {
        JOptionPane.showMessageDialog(this, detalle,
                "Error de validación", JOptionPane.ERROR_MESSAGE);
    }

    private void errorBaseDatos(String accion, String detalle) {
        JOptionPane.showMessageDialog(this,
                "No se pudo " + accion + ".\n\n" +
                        "Detalle: " + detalle + "\n\n" +
                        "Base de datos: " + dao.getUrl() +
                        "\n\nVerifica que exista la BD y que setup.sql se haya ejecutado.",
                "Error de base de datos", JOptionPane.ERROR_MESSAGE);
    }

    private void limpiar() {
        txtCveExamen.setText("");
        txtProfesorCve.setText("");
        txtMateriaClave.setText("");
        txtUnidadCve.setText("");
        cmbTipoExamen.setSelectedIndex(0);
        cmbPeriodo.setSelectedIndex(0);
        txtAnio.setText("");
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new FrmCatExamen().setVisible(true));
    }
}
