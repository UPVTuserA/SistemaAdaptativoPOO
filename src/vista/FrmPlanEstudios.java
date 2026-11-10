/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/GUIForms/JFrame.java to edit this template
 */
package vista;

import dao.PlanEstudiosDAO;
import modelo.PlanEstudios;
import javax.swing.table.DefaultTableModel;
import java.time.LocalDate;
import java.time.format.DateTimeParseException;
import java.util.List;

import javax.swing.JOptionPane;
import javax.swing.table.DefaultTableModel;

/**
 *
 * @author ad
 */
public class FrmPlanEstudios extends javax.swing.JFrame {

    private final PlanEstudiosDAO dao = new PlanEstudiosDAO();
    private DefaultTableModel modelo;
    private int idSeleccionado = 0;

    /**
     * Creates new form FrmPlanEstudios
     */
    public FrmPlanEstudios() {
        initComponents();
        setTitle("Sistema Adaptativo - Planes de Estudio");
        setLocationRelativeTo(null);

        configurarTabla();
        configurarEventos();
        listarPlanes();
    }

    private void configurarEventos() {

        btnGuardar.addActionListener(e -> guardar());

        btnActualizar.addActionListener(e -> actualizar());

        btnEliminar.addActionListener(e -> eliminar());

        btnLimpiar.addActionListener(e -> limpiar());

        tblPlanes.getSelectionModel().addListSelectionListener(e -> {

            if (!e.getValueIsAdjusting()) {
                seleccionarPlan();
            }
        });
    }

    private void limpiar() {

        idSeleccionado = 0;

        txtNombre.setText("");

        cmbEstatus.setSelectedIndex(0);

        txtFechaActivacion.setText("");
        txtFechaInactiva.setText("");
        txtFechaLiquidacion.setText("");

        tblPlanes.clearSelection();

        txtNombre.requestFocus();
    }

    private void seleccionarPlan() {

        int filaVista = tblPlanes.getSelectedRow();

        if (filaVista == -1) {
            return;
        }

        int fila = tblPlanes.convertRowIndexToModel(filaVista);

        idSeleccionado = Integer.parseInt(
                modelo.getValueAt(fila, 0).toString()
        );

        txtNombre.setText(
                obtenerTexto(fila, 1)
        );

        String estatus = obtenerTexto(fila, 2);

        if (!estatus.isEmpty()) {
            cmbEstatus.setSelectedItem(estatus);
        }

        txtFechaActivacion.setText(
                obtenerTexto(fila, 3)
        );

        txtFechaInactiva.setText(
                obtenerTexto(fila, 4)
        );

        txtFechaLiquidacion.setText(
                obtenerTexto(fila, 5)
        );
    }

    private String obtenerTexto(int fila, int columna) {

        Object valor = modelo.getValueAt(fila, columna);

        return valor == null ? "" : valor.toString();
    }

    private void eliminar() {

        if (idSeleccionado == 0) {

            JOptionPane.showMessageDialog(
                    this,
                    "Selecciona un plan de estudios."
            );

            return;
        }

        int respuesta = JOptionPane.showConfirmDialog(
                this,
                "¿Estás seguro de eliminar este plan de estudios?",
                "Confirmar eliminación",
                JOptionPane.YES_NO_OPTION
        );

        if (respuesta == JOptionPane.YES_OPTION) {

            try {

                boolean resultado = dao.eliminar(idSeleccionado);

                if (resultado) {

                    JOptionPane.showMessageDialog(
                            this,
                            "Registro eliminado correctamente."
                    );

                    limpiar();
                    listarPlanes();
                }

            } catch (RuntimeException e) {

                JOptionPane.showMessageDialog(
                        this,
                        "No fue posible eliminar el registro.\n"
                        + e.getMessage(),
                        "Error",
                        JOptionPane.ERROR_MESSAGE
                );
            }
        }
    }

    private void actualizar() {

        if (idSeleccionado == 0) {

            JOptionPane.showMessageDialog(
                    this,
                    "Selecciona un plan de estudios de la tabla."
            );

            return;
        }

        try {

            PlanEstudios plan = obtenerDatos();

            boolean resultado = dao.actualizar(plan);

            if (resultado) {

                JOptionPane.showMessageDialog(
                        this,
                        "Plan de estudios actualizado correctamente."
                );

                limpiar();
                listarPlanes();
            }

        } catch (IllegalArgumentException e) {

            JOptionPane.showMessageDialog(
                    this,
                    e.getMessage(),
                    "Validación",
                    JOptionPane.WARNING_MESSAGE
            );

        } catch (RuntimeException e) {

            JOptionPane.showMessageDialog(
                    this,
                    e.getMessage(),
                    "Error",
                    JOptionPane.ERROR_MESSAGE
            );
        }
    }

    private void guardar() {

        try {

            PlanEstudios plan = obtenerDatos();

            boolean resultado = dao.insertar(plan);

            if (resultado) {

                JOptionPane.showMessageDialog(
                        this,
                        "Plan de estudios registrado correctamente."
                );

                limpiar();
                listarPlanes();
            }

        } catch (IllegalArgumentException e) {

            JOptionPane.showMessageDialog(
                    this,
                    e.getMessage(),
                    "Validación",
                    JOptionPane.WARNING_MESSAGE
            );

        } catch (RuntimeException e) {

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
                    "Nombre",
                    "Estatus",
                    "Fecha activación",
                    "Fecha inactiva",
                    "Fecha liquidación"
                }, 0
        ) {
            @Override
            public boolean isCellEditable(int fila, int columna) {
                return false;
            }
        };

        tblPlanes.setModel(modelo);
        tblPlanes.setRowHeight(25);
    }

    private void listarPlanes() {

        modelo.setRowCount(0);

        try {

            List<PlanEstudios> lista = dao.listar();

            for (PlanEstudios plan : lista) {

                modelo.addRow(new Object[]{
                    plan.getCve(),
                    plan.getNombre(),
                    plan.getEstatus(),
                    plan.getFechaActivacion(),
                    plan.getFechaInactiva(),
                    plan.getFechaLiquidacion()
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
    }

    private PlanEstudios obtenerDatos() {

        String nombre = txtNombre.getText().trim();

        if (nombre.isEmpty()) {
            throw new IllegalArgumentException(
                    "Debes escribir el nombre del plan de estudios."
            );
        }

        if (nombre.length() > 45) {
            throw new IllegalArgumentException(
                    "El nombre no debe superar los 45 caracteres."
            );
        }

        String estatus = cmbEstatus.getSelectedItem().toString();

        LocalDate fechaActivacion
                = convertirFecha(txtFechaActivacion.getText());

        LocalDate fechaInactiva
                = convertirFecha(txtFechaInactiva.getText());

        LocalDate fechaLiquidacion
                = convertirFecha(txtFechaLiquidacion.getText());

        return new PlanEstudios(
                idSeleccionado,
                nombre,
                estatus,
                fechaActivacion,
                fechaInactiva,
                fechaLiquidacion
        );
    }

    private LocalDate convertirFecha(String fecha) {

        if (fecha == null || fecha.trim().isEmpty()) {
            return null;
        }

        try {
            return LocalDate.parse(fecha.trim());

        } catch (DateTimeParseException e) {
            throw new IllegalArgumentException(
                    "Formato de fecha incorrecto. Utiliza AAAA-MM-DD."
            );
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
        lblNombre = new javax.swing.JLabel();
        txtNombre = new javax.swing.JTextField();
        lblEstatus = new javax.swing.JLabel();
        cmbEstatus = new javax.swing.JComboBox<>();
        lblFechaActivacion = new javax.swing.JLabel();
        txtFechaActivacion = new javax.swing.JTextField();
        lblFechaInactiva = new javax.swing.JLabel();
        txtFechaInactiva = new javax.swing.JTextField();
        lblFechaLiquidacion = new javax.swing.JLabel();
        txtFechaLiquidacion = new javax.swing.JTextField();
        btnGuardar = new javax.swing.JButton();
        btnActualizar = new javax.swing.JButton();
        btnEliminar = new javax.swing.JButton();
        btnLimpiar = new javax.swing.JButton();
        jScrollPane1 = new javax.swing.JScrollPane();
        tblPlanes = new javax.swing.JTable();

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);

        lblTitulo.setText("Administración de planes de estudio");

        lblNombre.setText("Nombre del plan de estudios");

        lblEstatus.setText("Estatus");

        cmbEstatus.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "activa", "inactiva", "liquidacion" }));

        lblFechaActivacion.setText("Fecha de activación");

        lblFechaInactiva.setText("Fecha inactivacion de inactivación del catalogo");

        lblFechaLiquidacion.setText("Fecha de liquidación");

        btnGuardar.setText("Guardar");
        btnGuardar.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnGuardarActionPerformed(evt);
            }
        });

        btnActualizar.setText("Actualizar");

        btnEliminar.setText("Eliminar");

        btnLimpiar.setText("Limpiar");

        tblPlanes.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {
                {null, null, null, null},
                {null, null, null, null},
                {null, null, null, null},
                {null, null, null, null}
            },
            new String [] {
                "Title 1", "Title 2", "Title 3", "Title 4"
            }
        ));
        jScrollPane1.setViewportView(tblPlanes);

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(layout.createSequentialGroup()
                        .addGap(240, 240, 240)
                        .addComponent(lblTitulo))
                    .addGroup(layout.createSequentialGroup()
                        .addGap(59, 59, 59)
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(layout.createSequentialGroup()
                                .addComponent(lblEstatus)
                                .addGap(33, 33, 33)
                                .addComponent(cmbEstatus, javax.swing.GroupLayout.PREFERRED_SIZE, 193, javax.swing.GroupLayout.PREFERRED_SIZE))
                            .addGroup(layout.createSequentialGroup()
                                .addComponent(lblNombre)
                                .addGap(48, 48, 48)
                                .addComponent(txtNombre, javax.swing.GroupLayout.PREFERRED_SIZE, 393, javax.swing.GroupLayout.PREFERRED_SIZE))
                            .addGroup(layout.createSequentialGroup()
                                .addComponent(lblFechaActivacion)
                                .addGap(51, 51, 51)
                                .addComponent(txtFechaActivacion, javax.swing.GroupLayout.PREFERRED_SIZE, 204, javax.swing.GroupLayout.PREFERRED_SIZE))
                            .addGroup(layout.createSequentialGroup()
                                .addComponent(lblFechaInactiva)
                                .addGap(31, 31, 31)
                                .addComponent(txtFechaInactiva, javax.swing.GroupLayout.PREFERRED_SIZE, 271, javax.swing.GroupLayout.PREFERRED_SIZE))
                            .addGroup(layout.createSequentialGroup()
                                .addComponent(lblFechaLiquidacion)
                                .addGap(43, 43, 43)
                                .addComponent(txtFechaLiquidacion, javax.swing.GroupLayout.PREFERRED_SIZE, 198, javax.swing.GroupLayout.PREFERRED_SIZE))
                            .addGroup(layout.createSequentialGroup()
                                .addComponent(btnGuardar)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(btnActualizar)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(btnEliminar)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                                .addComponent(btnLimpiar))))
                    .addGroup(layout.createSequentialGroup()
                        .addGap(49, 49, 49)
                        .addComponent(jScrollPane1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addContainerGap(73, Short.MAX_VALUE))
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addGap(31, 31, 31)
                .addComponent(lblTitulo)
                .addGap(27, 27, 27)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(lblNombre)
                    .addComponent(txtNombre, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(18, 18, 18)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(lblEstatus)
                    .addComponent(cmbEstatus, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(29, 29, 29)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(lblFechaActivacion)
                    .addComponent(txtFechaActivacion, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(25, 25, 25)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(lblFechaInactiva)
                    .addComponent(txtFechaInactiva, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(18, 18, 18)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(lblFechaLiquidacion)
                    .addComponent(txtFechaLiquidacion, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(btnGuardar)
                    .addComponent(btnActualizar)
                    .addComponent(btnEliminar)
                    .addComponent(btnLimpiar))
                .addGap(18, 18, 18)
                .addComponent(jScrollPane1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void btnGuardarActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnGuardarActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_btnGuardarActionPerformed

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
            java.util.logging.Logger.getLogger(FrmPlanEstudios.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (InstantiationException ex) {
            java.util.logging.Logger.getLogger(FrmPlanEstudios.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (IllegalAccessException ex) {
            java.util.logging.Logger.getLogger(FrmPlanEstudios.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (javax.swing.UnsupportedLookAndFeelException ex) {
            java.util.logging.Logger.getLogger(FrmPlanEstudios.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        }
        //</editor-fold>

        /* Create and display the form */
        java.awt.EventQueue.invokeLater(new Runnable() {
            public void run() {
                new FrmPlanEstudios().setVisible(true);
            }
        });
    }

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton btnActualizar;
    private javax.swing.JButton btnEliminar;
    private javax.swing.JButton btnGuardar;
    private javax.swing.JButton btnLimpiar;
    private javax.swing.JComboBox<String> cmbEstatus;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JLabel lblEstatus;
    private javax.swing.JLabel lblFechaActivacion;
    private javax.swing.JLabel lblFechaInactiva;
    private javax.swing.JLabel lblFechaLiquidacion;
    private javax.swing.JLabel lblNombre;
    private javax.swing.JLabel lblTitulo;
    private javax.swing.JTable tblPlanes;
    private javax.swing.JTextField txtFechaActivacion;
    private javax.swing.JTextField txtFechaInactiva;
    private javax.swing.JTextField txtFechaLiquidacion;
    private javax.swing.JTextField txtNombre;
    // End of variables declaration//GEN-END:variables
}
