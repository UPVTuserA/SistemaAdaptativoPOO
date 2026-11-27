// CRUD CAT_CARRERA EDUARDO CORTES - MODULO VISTA

package VISTA;

import DAO.CatCarreraDAO;
import MODELO.CatCarrera;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.time.LocalDate;
import java.time.format.DateTimeParseException;

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

        // Carga inicial: se difiere para que el dialogo de error no aparezca
        // antes de que la ventana sea visible.
        SwingUtilities.invokeLater(this::leer);
    }

    // ==========================================
    // CREATE
    // ==========================================
    private void insertar() {
        CatCarrera carrera;
        try {
            carrera = crearDesdeFormulario();
        } catch (IllegalArgumentException ex) {
            errorValidacion(ex.getMessage());
            return;   // NO se limpia el formulario: el usuario conserva lo que escribio
        }

        if (!dao.insertar(carrera)) {
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

        for (CatCarrera c : lista) {
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
        CatCarrera carrera;
        try {
            carrera = crearDesdeFormulario();
        } catch (IllegalArgumentException ex) {
            errorValidacion(ex.getMessage());
            return;
        }

        if (!dao.actualizar(carrera)) {
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
            cve = leerEntero(txtCveCarrera, "cve_carrera");
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

    /** Lee una fecha opcional en formato aaaa-mm-dd. */
    private LocalDate leerFecha(JTextField campo, String nombreCampo) {
        String texto = campo.getText().trim();
        if (texto.isEmpty()) {
            return null;
        }
        try {
            return LocalDate.parse(texto);
        } catch (DateTimeParseException e) {
            throw new IllegalArgumentException(
                    "El campo \"" + nombreCampo + "\" debe tener el formato aaaa-mm-dd.\nValor escrito: \"" + texto + "\"");
        }
    }

    /** Valida todos los campos y construye la entidad. Lanza IllegalArgumentException con el detalle. */
    private CatCarrera crearDesdeFormulario() {
        if (txtEstatus.getText().trim().isEmpty()) {
            throw new IllegalArgumentException("El campo \"estatus\" es obligatorio.");
        }
        if (txtNombre.getText().trim().isEmpty()) {
            throw new IllegalArgumentException("El campo \"nombre\" es obligatorio.");
        }

        return new CatCarrera(
                leerEntero(txtCveCarrera, "cve_carrera"),
                leerEntero(txtPlanEstudiosCve, "cat_plan_estudios_cve"),
                txtEstatus.getText().trim(),
                txtNombre.getText().trim(),
                leerFecha(txtFechaActivacion, "fecha_activacion"),
                leerFecha(txtFechaInactiva, "fecha_inactiva"),
                leerFecha(txtFechaLiquidacion, "fecha_liquidacion")
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
