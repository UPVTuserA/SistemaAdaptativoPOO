// CRUD CAT_EXAMEN EDUARDO CORTES - MODULO VISTA

package VISTA_EXAMEN;

import DAO_EXAMEN.CatExamenDAO;
import MODELO_EXAMEN.CatExamen;
import MODELO_EXAMEN.Periodo;
import MODELO_EXAMEN.TipoExamen;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;

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
        pnlCampos.add(new JLabel("cve_examen:"));
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

        leer();
    }

    // ==========================================
    // CREATE
    // ==========================================
    private void insertar() {
        try {
            CatExamen examen = crearDesdeFormulario();
            dao.insertar(examen);
            limpiar();
            leer();
        } catch (NumberFormatException ex) {
            JOptionPane.showMessageDialog(this,
                    "cve_examen, cat_profesor_cve_profesor, cat_unidades_materia_cve_unidad y anio deben ser números.",
                    "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    // ==========================================
    // READ
    // ==========================================
    private void leer() {
        modeloTabla.setRowCount(0);
        for (CatExamen e : dao.leer()) {
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
        try {
            CatExamen examen = crearDesdeFormulario();
            dao.actualizar(examen);
            limpiar();
            leer();
        } catch (NumberFormatException ex) {
            JOptionPane.showMessageDialog(this,
                    "cve_examen, cat_profesor_cve_profesor, cat_unidades_materia_cve_unidad y anio deben ser números.",
                    "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    // ==========================================
    // DELETE
    // ==========================================
    private void eliminar() {
        try {
            int cve = Integer.parseInt(txtCveExamen.getText().trim());
            dao.eliminar(cve);
            limpiar();
            leer();
        } catch (NumberFormatException ex) {
            JOptionPane.showMessageDialog(this, "Ingrese un cve_examen válido para eliminar.",
                    "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    // --- Convierte los campos del formulario en una entidad CatExamen ---
    private CatExamen crearDesdeFormulario() {
        return new CatExamen(
                Integer.parseInt(txtCveExamen.getText().trim()),
                Integer.parseInt(txtProfesorCve.getText().trim()),
                txtMateriaClave.getText().trim(),
                Integer.parseInt(txtUnidadCve.getText().trim()),
                (TipoExamen) cmbTipoExamen.getSelectedItem(),
                (Periodo) cmbPeriodo.getSelectedItem(),
                Integer.parseInt(txtAnio.getText().trim())
        );
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
