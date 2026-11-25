// CRUD CAT_CARRERA EDUARDO CORTES - MODULO VISTA

package VISTA;

import DAO.CatCarreraDAO;
import MODELO.CatCarrera;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.time.LocalDate;

/**
 * Formulario Swing para el CRUD de la tabla cat_carrera.
 * Solo maneja la interfaz; la logica recae en DAO y el estado en MODELO.
 */
public class FrmCatCarrera extends JFrame {

    private final CatCarreraDAO dao = new CatCarreraDAO();

    // --- Campos del formulario ---
    private final JTextField txtCveCarrera = new JTextField();
    private final JTextField txtPlanEstudiosCve = new JTextField();
    private final JTextField txtEstatus = new JTextField();
    private final JTextField txtNombre = new JTextField();
    private final JTextField txtFechaActivacion = new JTextField();
    private final JTextField txtFechaInactiva = new JTextField();
    private final JTextField txtFechaLiquidacion = new JTextField();

    private final JTable tabla = new JTable();
    private final DefaultTableModel modeloTabla = new DefaultTableModel(
            new Object[]{"cve_carrera", "cat_plan_estudios_cve", "estatus", "nombre",
                    "fecha_activacion", "fecha_inactiva", "fecha_liquidacion"}, 0) {
        @Override
        public boolean isCellEditable(int row, int column) {
            return false;
        }
    };

    public FrmCatCarrera() {
        super("CRUD cat_carrera - Eduardo Cortes");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(900, 600);
        setLocationRelativeTo(null);
        setLayout(new BorderLayout(10, 10));

        // --- Panel de campos ---
        JPanel pnlCampos = new JPanel(new GridLayout(4, 4, 8, 8));
        pnlCampos.setBorder(BorderFactory.createTitledBorder("Datos de cat_carrera"));
        pnlCampos.add(new JLabel("cve_carrera:"));
        pnlCampos.add(txtCveCarrera);
        pnlCampos.add(new JLabel("cat_plan_estudios_cve:"));
        pnlCampos.add(txtPlanEstudiosCve);
        pnlCampos.add(new JLabel("estatus:"));
        pnlCampos.add(txtEstatus);
        pnlCampos.add(new JLabel("nombre:"));
        pnlCampos.add(txtNombre);
        pnlCampos.add(new JLabel("fecha_activacion (aaaa-mm-dd):"));
        pnlCampos.add(txtFechaActivacion);
        pnlCampos.add(new JLabel("fecha_inactiva (aaaa-mm-dd):"));
        pnlCampos.add(txtFechaInactiva);
        pnlCampos.add(new JLabel("fecha_liquidacion (aaaa-mm-dd):"));
        pnlCampos.add(txtFechaLiquidacion);
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
            CatCarrera carrera = crearDesdeFormulario();
            dao.insertar(carrera);
            limpiar();
            leer();
        } catch (NumberFormatException ex) {
            JOptionPane.showMessageDialog(this, "cve_carrera y cat_plan_estudios_cve deben ser números.",
                    "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    // ==========================================
    // READ
    // ==========================================
    private void leer() {
        modeloTabla.setRowCount(0);
        for (CatCarrera c : dao.leer()) {
            modeloTabla.addRow(new Object[]{
                    c.getCveCarrera(), c.getCatPlanEstudiosCve(), c.getEstatus(), c.getNombre(),
                    c.getFechaActivacion(), c.getFechaInactiva(), c.getFechaLiquidacion()
            });
        }
    }

    // ==========================================
    // UPDATE
    // ==========================================
    private void actualizar() {
        try {
            CatCarrera carrera = crearDesdeFormulario();
            dao.actualizar(carrera);
            limpiar();
            leer();
        } catch (NumberFormatException ex) {
            JOptionPane.showMessageDialog(this, "cve_carrera y cat_plan_estudios_cve deben ser números.",
                    "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    // ==========================================
    // DELETE
    // ==========================================
    private void eliminar() {
        try {
            int cve = Integer.parseInt(txtCveCarrera.getText().trim());
            dao.eliminar(cve);
            limpiar();
            leer();
        } catch (NumberFormatException ex) {
            JOptionPane.showMessageDialog(this, "Ingrese un cve_carrera válido para eliminar.",
                    "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    // --- Convierte los campos del formulario en una entidad CatCarrera ---
    private CatCarrera crearDesdeFormulario() {
        return new CatCarrera(
                Integer.parseInt(txtCveCarrera.getText().trim()),
                Integer.parseInt(txtPlanEstudiosCve.getText().trim()),
                txtEstatus.getText().trim(),
                txtNombre.getText().trim(),
                parseFecha(txtFechaActivacion.getText()),
                parseFecha(txtFechaInactiva.getText()),
                parseFecha(txtFechaLiquidacion.getText())
        );
    }

    // --- Convierte un texto a LocalDate (devuelve null si esta vacio) ---
    private LocalDate parseFecha(String texto) {
        String t = texto.trim();
        return t.isEmpty() ? null : LocalDate.parse(t);
    }

    private void limpiar() {
        txtCveCarrera.setText("");
        txtPlanEstudiosCve.setText("");
        txtEstatus.setText("");
        txtNombre.setText("");
        txtFechaActivacion.setText("");
        txtFechaInactiva.setText("");
        txtFechaLiquidacion.setText("");
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new FrmCatCarrera().setVisible(true));
    }
}
