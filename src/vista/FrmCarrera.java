/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/GUIForms/JFrame.java to edit this template
 */
package vista;

import dao.CarreraDAO;
import modelo.Carrera;
import modelo.Opcion;
import java.awt.event.KeyEvent;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.List;

import javax.swing.DefaultComboBoxModel;
import javax.swing.JComboBox;
import javax.swing.JComponent;
import javax.swing.JOptionPane;
import javax.swing.JTextField;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableModel;
import javax.swing.table.TableRowSorter;

/**
 * Formulario de administracion de la tabla cat_carrera.
 * CRUD original de consola: Eduardo Cortes (equipo gamma).
 * Formulario e integracion al proyecto: Alberto (equipo epsilon).
 *
 * @author Eduardo Cortes (gamma) / Alberto (epsilon)
 */
public class FrmCarrera extends javax.swing.JFrame {

    private final CarreraDAO dao = new CarreraDAO();
    private DefaultTableModel modelo;
    private TableRowSorter<TableModel> sorter;
    private int idSeleccionado = 0;

    private static final DateTimeFormatter FORMATO_FECHA
            = DateTimeFormatter.ofPattern("yyyy-MM-dd");

    private static final String AYUDA
            = "Atajos: Ctrl+S guardar · Ctrl+U actualizar · Supr eliminar · Esc limpiar · Ctrl+F buscar";

    /**
     * Creates new form FrmCarrera
     */
    public FrmCarrera() {
        initComponents();
        setTitle("Sistema Adaptativo - Carreras");

        aplicarDiseno();
        cargarCombos();
        configurarTabla();
        configurarEventos();
        limpiar();
        listarCarreras();

        pack();
        setLocationRelativeTo(null);
    }

    // ===== Diseño e interaccion =====
    private void aplicarDiseno() {

        Estilo.ventana(this, lblTitulo, lblInstrucciones);

        Estilo.boton(btnGuardar, Estilo.VERDE, "Guardar como carrera nueva (Ctrl+S)");
        Estilo.boton(btnActualizar, Estilo.AZUL, "Guardar los cambios de la fila seleccionada (Ctrl+U)");
        Estilo.boton(btnEliminar, Estilo.ROJO, "Eliminar la fila seleccionada (Supr)");
        Estilo.boton(btnLimpiar, Estilo.GRIS, "Vaciar el formulario (Esc)");

        Estilo.contador(txtNombre, lblContNombre, 45);

        lblAyudaActivacion.setForeground(Estilo.GRIS);
        lblAyudaActivacion.setText("AAAA-MM-DD");

        fechaEnVivo(txtFechaActivacion);
        fechaEnVivo(txtFechaInactiva);
        fechaEnVivo(txtFechaLiquidacion);

        cmbPlan.setToolTipText("Plan de estudios al que pertenece la carrera");

        Estilo.atajo(getRootPane(), "guardar", KeyEvent.VK_S, Estilo.CTRL, this::guardar);
        Estilo.atajo(getRootPane(), "actualizar", KeyEvent.VK_U, Estilo.CTRL, this::actualizar);
        Estilo.atajo(getRootPane(), "limpiar", KeyEvent.VK_ESCAPE, 0, this::limpiar);
        Estilo.atajo(getRootPane(), "buscar", KeyEvent.VK_F, Estilo.CTRL, () -> {
            txtBuscar.requestFocus();
            txtBuscar.selectAll();
        });
    }

    // La fecha se pone en rojo mientras no tenga el formato AAAA-MM-DD
    private void fechaEnVivo(JTextField campo) {

        campo.setToolTipText("Formato AAAA-MM-DD, por ejemplo 2026-08-15");

        campo.getDocument().addDocumentListener(new DocumentListener() {
            private void revisar() {
                String t = campo.getText().trim();
                try {
                    if (!t.isEmpty()) {
                        LocalDate.parse(t, FORMATO_FECHA);
                    }
                    Estilo.quitarError(campo);
                } catch (DateTimeParseException e) {
                    Estilo.marcarError(campo);
                }
            }

            @Override
            public void insertUpdate(DocumentEvent e) {
                revisar();
            }

            @Override
            public void removeUpdate(DocumentEvent e) {
                revisar();
            }

            @Override
            public void changedUpdate(DocumentEvent e) {
                revisar();
            }
        });
    }

    private void avisar(String mensaje, Estilo.Tipo tipo) {
        Estilo.estado(lblEstado, mensaje, tipo, AYUDA);
    }

    private void modoEdicion(boolean editando) {

        btnActualizar.setEnabled(editando);
        btnEliminar.setEnabled(editando);

        lblInstrucciones.setText(editando
                ? "Editando la carrera #" + idSeleccionado
                  + ". Cambia lo que necesites y presiona Actualizar, o Limpiar para cancelar."
                : "Elige el plan de estudios y escribe el nombre. Las fechas van como AAAA-MM-DD y son opcionales.");
    }

    private void quitarErrores() {
        for (JComponent c : new JComponent[]{cmbPlan, txtNombre,
            txtFechaActivacion, txtFechaInactiva, txtFechaLiquidacion}) {
            Estilo.quitarError(c);
        }
    }

    // ===== Listas desplegables =====
    private void cargarCombos() {

        try {
            cmbPlan.setModel(new DefaultComboBoxModel<>(
                    dao.listarPlanes().toArray(new Opcion[0])));

        } catch (RuntimeException e) {

            JOptionPane.showMessageDialog(
                    this,
                    e.getMessage(),
                    "Error al cargar listas",
                    JOptionPane.ERROR_MESSAGE
            );
        }

        List<String> estatus = dao.obtenerValoresEstatus();

        if (!estatus.isEmpty()) {
            cmbEstatus.setModel(new DefaultComboBoxModel<>(estatus.toArray(new String[0])));
        }
    }

    private void seleccionarPorId(JComboBox<Opcion> combo, int id) {

        for (int i = 0; i < combo.getItemCount(); i++) {

            if (combo.getItemAt(i).getId() == id) {
                combo.setSelectedIndex(i);
                return;
            }
        }
    }

    private Opcion buscarOpcion(JComboBox<Opcion> combo, int id) {

        for (int i = 0; i < combo.getItemCount(); i++) {

            if (combo.getItemAt(i).getId() == id) {
                return combo.getItemAt(i);
            }
        }

        return new Opcion(id, String.valueOf(id));
    }

    private void configurarEventos() {

        btnGuardar.addActionListener(e -> guardar());

        btnActualizar.addActionListener(e -> actualizar());

        btnEliminar.addActionListener(e -> eliminar());

        btnLimpiar.addActionListener(e -> limpiar());

        tblCarreras.getSelectionModel().addListSelectionListener(e -> {

            if (!e.getValueIsAdjusting()) {
                seleccionarCarrera();
            }
        });

        tblCarreras.getInputMap().put(
                javax.swing.KeyStroke.getKeyStroke(KeyEvent.VK_DELETE, 0), "eliminar");
        tblCarreras.getActionMap().put("eliminar", new javax.swing.AbstractAction() {
            @Override
            public void actionPerformed(java.awt.event.ActionEvent e) {
                eliminar();
            }
        });
    }

    private void limpiar() {

        idSeleccionado = 0;

        txtNombre.setText("");
        txtFechaActivacion.setText(LocalDate.now().format(FORMATO_FECHA));
        txtFechaInactiva.setText("");
        txtFechaLiquidacion.setText("");

        if (cmbEstatus.getItemCount() > 0) {
            cmbEstatus.setSelectedIndex(0);
        }

        tblCarreras.clearSelection();

        quitarErrores();
        modoEdicion(false);
        avisar(AYUDA, Estilo.Tipo.INFO);

        txtNombre.requestFocus();
    }

    private void seleccionarCarrera() {

        int filaVista = tblCarreras.getSelectedRow();

        if (filaVista == -1) {
            return;
        }

        int fila = tblCarreras.convertRowIndexToModel(filaVista);

        idSeleccionado = Integer.parseInt(
                modelo.getValueAt(fila, 0).toString()
        );

        seleccionarPorId(cmbPlan, ((Opcion) modelo.getValueAt(fila, 1)).getId());

        txtNombre.setText(obtenerTexto(fila, 2));

        String estatus = obtenerTexto(fila, 3);

        if (!estatus.isEmpty()) {
            cmbEstatus.setSelectedItem(estatus);
        }

        txtFechaActivacion.setText(obtenerTexto(fila, 4));
        txtFechaInactiva.setText(obtenerTexto(fila, 5));
        txtFechaLiquidacion.setText(obtenerTexto(fila, 6));

        quitarErrores();
        modoEdicion(true);
    }

    private String obtenerTexto(int fila, int columna) {

        Object valor = modelo.getValueAt(fila, columna);

        return valor == null ? "" : valor.toString();
    }

    private void eliminar() {

        if (idSeleccionado == 0) {
            avisar("selecciona una carrera de la tabla para eliminarla.", Estilo.Tipo.ERROR);
            return;
        }

        int respuesta = JOptionPane.showConfirmDialog(
                this,
                "¿Eliminar la carrera #" + idSeleccionado + "?\n\n" + txtNombre.getText(),
                "Confirmar eliminación",
                JOptionPane.YES_NO_OPTION,
                JOptionPane.WARNING_MESSAGE
        );

        if (respuesta == JOptionPane.YES_OPTION) {

            try {

                int id = idSeleccionado;

                if (dao.eliminar(id)) {
                    limpiar();
                    listarCarreras();
                    avisar("carrera #" + id + " eliminada.", Estilo.Tipo.EXITO);
                }

            } catch (RuntimeException e) {

                avisar("no se pudo eliminar la carrera.", Estilo.Tipo.ERROR);

                JOptionPane.showMessageDialog(
                        this,
                        "No fue posible eliminar la carrera.\n"
                        + "Si ya tiene alumnos o materias registradas, primero hay que borrar esos registros.\n\n"
                        + e.getMessage(),
                        "Error",
                        JOptionPane.ERROR_MESSAGE
                );
            }
        }
    }

    private void actualizar() {

        if (idSeleccionado == 0) {
            avisar("selecciona una carrera de la tabla para actualizarla.", Estilo.Tipo.ERROR);
            return;
        }

        guardarEnBase(true);
    }

    private void guardar() {
        guardarEnBase(false);
    }

    private void guardarEnBase(boolean actualizando) {

        quitarErrores();

        try {

            Carrera c = obtenerDatos();
            int id = idSeleccionado;

            boolean resultado = actualizando
                    ? dao.actualizar(c)
                    : dao.insertar(c);

            if (resultado) {

                limpiar();
                listarCarreras();

                avisar(actualizando
                        ? "carrera #" + id + " actualizada."
                        : "carrera guardada.", Estilo.Tipo.EXITO);
            }

        } catch (Estilo.Invalido e) {

            Estilo.marcarError(e.getCampo());
            e.getCampo().requestFocus();
            avisar(e.getMessage(), Estilo.Tipo.ERROR);

        } catch (RuntimeException e) {

            avisar("no se pudo guardar en la base de datos.", Estilo.Tipo.ERROR);

            JOptionPane.showMessageDialog(
                    this,
                    e.getMessage(),
                    "Error",
                    JOptionPane.ERROR_MESSAGE
            );
        }
    }

    private void configurarTabla() {

        modelo = new DefaultTableModel(
                new Object[]{
                    "Clave",
                    "Plan de estudios",
                    "Nombre",
                    "Estatus",
                    "Activación",
                    "Inactiva",
                    "Liquidación"
                }, 0
        ) {
            @Override
            public boolean isCellEditable(int fila, int columna) {
                return false;
            }

            @Override
            public Class<?> getColumnClass(int columna) {
                return columna == 0 ? Integer.class : Object.class;
            }
        };

        tblCarreras.setModel(modelo);

        sorter = Estilo.tabla(tblCarreras, (columna, valor) -> {

            if (columna == 3 && valor != null) {
                return switch (valor.toString()) {
                    case "activa" -> Estilo.VERDE;
                    case "inactiva" -> Estilo.GRIS;
                    case "liquidacion" -> new java.awt.Color(176, 96, 20);
                    default -> null;
                };
            }

            return null;
        });

        sorter.setComparator(1, java.util.Comparator.comparing(
                Object::toString, String.CASE_INSENSITIVE_ORDER));

        Estilo.busqueda(txtBuscar, sorter, this::actualizarTotal);

        int[] anchos = {50, 170, 260, 90, 100, 100, 100};

        for (int i = 0; i < anchos.length; i++) {
            tblCarreras.getColumnModel().getColumn(i).setPreferredWidth(anchos[i]);
        }
    }

    private void actualizarTotal() {

        int total = modelo.getRowCount();
        int visibles = tblCarreras.getRowCount();

        lblTotal.setText(visibles == total
                ? total + (total == 1 ? " carrera" : " carreras")
                : "Mostrando " + visibles + " de " + total);
    }

    private void listarCarreras() {

        modelo.setRowCount(0);

        try {

            for (Carrera c : dao.listar()) {

                modelo.addRow(new Object[]{
                    c.getCveCarrera(),
                    buscarOpcion(cmbPlan, c.getCvePlanEstudios()),
                    c.getNombre(),
                    c.getEstatus(),
                    texto(c.getFechaActivacion()),
                    texto(c.getFechaInactiva()),
                    texto(c.getFechaLiquidacion())
                });
            }

        } catch (RuntimeException e) {

            JOptionPane.showMessageDialog(
                    this,
                    e.getMessage(),
                    "Error de consulta",
                    JOptionPane.ERROR_MESSAGE
            );
        }

        actualizarTotal();
    }

    private String texto(LocalDate fecha) {
        return fecha == null ? null : fecha.format(FORMATO_FECHA);
    }

    private Carrera obtenerDatos() {

        Opcion plan = (Opcion) cmbPlan.getSelectedItem();

        if (plan == null) {
            throw new Estilo.Invalido(
                    "no hay planes de estudio registrados. Primero da de alta uno.", cmbPlan);
        }

        String nombre = txtNombre.getText().trim();

        if (nombre.isEmpty()) {
            throw new Estilo.Invalido("escribe el nombre de la carrera.", txtNombre);
        }

        if (nombre.length() > 45) {
            throw new Estilo.Invalido(
                    "el nombre no debe superar los 45 caracteres.", txtNombre);
        }

        String estatus = cmbEstatus.getSelectedItem().toString();

        return new Carrera(
                idSeleccionado,
                plan.getId(),
                estatus,
                nombre,
                convertirFecha(txtFechaActivacion),
                convertirFecha(txtFechaInactiva),
                convertirFecha(txtFechaLiquidacion)
        );
    }

    private LocalDate convertirFecha(JTextField campo) {

        String texto = campo.getText().trim();

        if (texto.isEmpty()) {
            return null;
        }

        try {
            return LocalDate.parse(texto, FORMATO_FECHA);

        } catch (DateTimeParseException e) {
            throw new Estilo.Invalido(
                    "la fecha debe ser AAAA-MM-DD (ej. 2026-08-15) o déjala vacía.", campo);
        }
    }

    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        lblTitulo = new javax.swing.JLabel();
        lblInstrucciones = new javax.swing.JLabel();
        lblPlan = new javax.swing.JLabel();
        cmbPlan = new javax.swing.JComboBox<>();
        lblNombre = new javax.swing.JLabel();
        txtNombre = new javax.swing.JTextField();
        lblContNombre = new javax.swing.JLabel();
        lblEstatus = new javax.swing.JLabel();
        cmbEstatus = new javax.swing.JComboBox<>();
        lblFechaActivacion = new javax.swing.JLabel();
        txtFechaActivacion = new javax.swing.JTextField();
        lblAyudaActivacion = new javax.swing.JLabel();
        lblFechaInactiva = new javax.swing.JLabel();
        txtFechaInactiva = new javax.swing.JTextField();
        lblFechaLiquidacion = new javax.swing.JLabel();
        txtFechaLiquidacion = new javax.swing.JTextField();
        btnGuardar = new javax.swing.JButton();
        btnActualizar = new javax.swing.JButton();
        btnEliminar = new javax.swing.JButton();
        btnLimpiar = new javax.swing.JButton();
        lblBuscar = new javax.swing.JLabel();
        txtBuscar = new javax.swing.JTextField();
        lblTotal = new javax.swing.JLabel();
        jScrollPane1 = new javax.swing.JScrollPane();
        tblCarreras = new javax.swing.JTable();
        lblEstado = new javax.swing.JLabel();

        setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);

        lblTitulo.setFont(new java.awt.Font("Segoe UI", 1, 22)); // NOI18N
        lblTitulo.setText("Carreras");

        lblInstrucciones.setText("Elige el plan de estudios y escribe el nombre. Las fechas van como AAAA-MM-DD y son opcionales.");

        lblPlan.setText("Plan de estudios");

        lblNombre.setText("Nombre de la carrera");

        lblContNombre.setText("0");

        lblEstatus.setText("Estatus");

        cmbEstatus.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "activa", "liquidacion", "inactiva" }));

        lblFechaActivacion.setText("Fecha de activación");

        lblAyudaActivacion.setText("0");

        lblFechaInactiva.setText("Fecha inactiva (opcional)");

        lblFechaLiquidacion.setText("Fecha de liquidación (opcional)");

        btnGuardar.setText("Guardar");

        btnActualizar.setText("Actualizar");

        btnEliminar.setText("Eliminar");

        btnLimpiar.setText("Limpiar");

        lblBuscar.setText("Buscar:");

        lblTotal.setText("0 registros");

        tblCarreras.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {

            },
            new String [] {

            }
        ));
        jScrollPane1.setViewportView(tblCarreras);

        lblEstado.setText(" ");

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addGap(40, 40, 40)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(lblTitulo)
                    .addComponent(lblInstrucciones)
                    .addGroup(layout.createSequentialGroup()
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(lblPlan)
                            .addComponent(lblNombre)
                            .addComponent(lblEstatus)
                            .addComponent(lblFechaActivacion)
                            .addComponent(lblFechaInactiva)
                            .addComponent(lblFechaLiquidacion))
                        .addGap(30, 30, 30)
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(cmbPlan, javax.swing.GroupLayout.PREFERRED_SIZE, 350, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addGroup(layout.createSequentialGroup()
                                .addComponent(txtNombre, javax.swing.GroupLayout.PREFERRED_SIZE, 350, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addGap(10, 10, 10)
                                .addComponent(lblContNombre))
                            .addComponent(cmbEstatus, javax.swing.GroupLayout.PREFERRED_SIZE, 180, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addGroup(layout.createSequentialGroup()
                                .addComponent(txtFechaActivacion, javax.swing.GroupLayout.PREFERRED_SIZE, 140, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addGap(10, 10, 10)
                                .addComponent(lblAyudaActivacion))
                            .addComponent(txtFechaInactiva, javax.swing.GroupLayout.PREFERRED_SIZE, 140, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(txtFechaLiquidacion, javax.swing.GroupLayout.PREFERRED_SIZE, 140, javax.swing.GroupLayout.PREFERRED_SIZE)))
                    .addGroup(layout.createSequentialGroup()
                        .addComponent(btnGuardar)
                        .addGap(12, 12, 12)
                        .addComponent(btnActualizar)
                        .addGap(12, 12, 12)
                        .addComponent(btnEliminar)
                        .addGap(12, 12, 12)
                        .addComponent(btnLimpiar))
                    .addGroup(layout.createSequentialGroup()
                        .addComponent(lblBuscar)
                        .addGap(10, 10, 10)
                        .addComponent(txtBuscar, javax.swing.GroupLayout.PREFERRED_SIZE, 320, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(20, 20, 20)
                        .addComponent(lblTotal))
                    .addComponent(jScrollPane1, javax.swing.GroupLayout.PREFERRED_SIZE, 900, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(lblEstado))
                .addContainerGap(40, Short.MAX_VALUE))
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addGap(22, 22, 22)
                .addComponent(lblTitulo)
                .addGap(4, 4, 4)
                .addComponent(lblInstrucciones)
                .addGap(18, 18, 18)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(lblPlan)
                    .addComponent(cmbPlan, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(10, 10, 10)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(lblNombre)
                    .addComponent(txtNombre, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(lblContNombre))
                .addGap(10, 10, 10)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(lblEstatus)
                    .addComponent(cmbEstatus, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(10, 10, 10)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(lblFechaActivacion)
                    .addComponent(txtFechaActivacion, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(lblAyudaActivacion))
                .addGap(10, 10, 10)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(lblFechaInactiva)
                    .addComponent(txtFechaInactiva, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(10, 10, 10)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(lblFechaLiquidacion)
                    .addComponent(txtFechaLiquidacion, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(18, 18, 18)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(btnGuardar)
                    .addComponent(btnActualizar)
                    .addComponent(btnEliminar)
                    .addComponent(btnLimpiar))
                .addGap(22, 22, 22)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(lblBuscar)
                    .addComponent(txtBuscar, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(lblTotal))
                .addGap(10, 10, 10)
                .addComponent(jScrollPane1, javax.swing.GroupLayout.PREFERRED_SIZE, 250, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(10, 10, 10)
                .addComponent(lblEstado)
                .addContainerGap(18, Short.MAX_VALUE))
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

    /**
     * @param args the command line arguments
     */
    public static void main(String args[]) {
        /* Set the Nimbus look and feel */
        //<editor-fold defaultstate="collapsed" desc=" Look and feel setting code (optional) ">
        /* If Nimbus (introduced in Java SE 6) is not available, stay with the default look and feel.
         * For details see http://download.oracle.com/javase/tutorial/uiswing/lookandfeel/plaf.html
         */
        try {
            for (javax.swing.UIManager.LookAndFeelInfo info : javax.swing.UIManager.getInstalledLookAndFeels()) {
                if ("Nimbus".equals(info.getName())) {
                    javax.swing.UIManager.setLookAndFeel(info.getClassName());
                    break;
                }
            }
        } catch (ClassNotFoundException ex) {
            java.util.logging.Logger.getLogger(FrmCarrera.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (InstantiationException ex) {
            java.util.logging.Logger.getLogger(FrmCarrera.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (IllegalAccessException ex) {
            java.util.logging.Logger.getLogger(FrmCarrera.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (javax.swing.UnsupportedLookAndFeelException ex) {
            java.util.logging.Logger.getLogger(FrmCarrera.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        }
        //</editor-fold>

        /* Create and display the form */
        java.awt.EventQueue.invokeLater(new Runnable() {
            public void run() {
                new FrmCarrera().setVisible(true);
            }
        });
    }

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton btnActualizar;
    private javax.swing.JButton btnEliminar;
    private javax.swing.JButton btnGuardar;
    private javax.swing.JButton btnLimpiar;
    private javax.swing.JComboBox<String> cmbEstatus;
    private javax.swing.JComboBox<modelo.Opcion> cmbPlan;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JLabel lblAyudaActivacion;
    private javax.swing.JLabel lblBuscar;
    private javax.swing.JLabel lblContNombre;
    private javax.swing.JLabel lblEstado;
    private javax.swing.JLabel lblEstatus;
    private javax.swing.JLabel lblFechaActivacion;
    private javax.swing.JLabel lblFechaInactiva;
    private javax.swing.JLabel lblFechaLiquidacion;
    private javax.swing.JLabel lblInstrucciones;
    private javax.swing.JLabel lblNombre;
    private javax.swing.JLabel lblPlan;
    private javax.swing.JLabel lblTitulo;
    private javax.swing.JLabel lblTotal;
    private javax.swing.JTable tblCarreras;
    private javax.swing.JTextField txtBuscar;
    private javax.swing.JTextField txtFechaActivacion;
    private javax.swing.JTextField txtFechaInactiva;
    private javax.swing.JTextField txtFechaLiquidacion;
    private javax.swing.JTextField txtNombre;
    // End of variables declaration//GEN-END:variables
}
