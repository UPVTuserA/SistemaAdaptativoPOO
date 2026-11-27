/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/GUIForms/JFrame.java to edit this template
 */
package vista;

import java.awt.Color;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.GridLayout;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Supplier;

import javax.swing.BorderFactory;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.SwingWorker;
import javax.swing.WindowConstants;

import sistemaadaptativopoo.Conexion;

/**
 * Menu principal del Sistema Adaptativo.
 * Desde aqui se abre el formulario de cada tabla.
 *
 * PARA AGREGAR EL FORMULARIO DE TU EQUIPO:
 *   En registrarModulos(), busca la linea de tu tabla y cambia el null
 *   por  FrmTuFormulario::new   (y pon el nombre de tu equipo).
 *
 * @author Integracion: Alberto (equipo epsilon)
 */
public class FrmMenuPrincipal extends javax.swing.JFrame {

    /** Un modulo = una tabla con (o sin) formulario. */
    private static class Modulo {

        final String seccion;
        final String titulo;
        final String tabla;
        final String equipo;
        final Supplier<JFrame> abrir;   // null = todavia pendiente

        Modulo(String seccion, String titulo, String tabla, String equipo,
                Supplier<JFrame> abrir) {
            this.seccion = seccion;
            this.titulo = titulo;
            this.tabla = tabla;
            this.equipo = equipo;
            this.abrir = abrir;
        }
    }

    private final List<Modulo> modulos = new ArrayList<>();

    // Ventanas abiertas, para no abrir la misma dos veces
    private final Map<String, JFrame> abiertas = new HashMap<>();

    /**
     * Creates new form FrmMenuPrincipal
     */
    public FrmMenuPrincipal() {
        initComponents();
        setTitle("Sistema Adaptativo - Menú principal");

        registrarModulos();
        construirMenu();
        revisarConexion();

        pack();
        setLocationRelativeTo(null);
    }

    // =====================================================================
    //  REGISTRO DE MODULOS: aqui se integra el trabajo de cada equipo
    // =====================================================================
    private void registrarModulos() {

        // ----- Catalogos academicos -----
        agregar("Catálogos académicos", "Planes de estudio", "cat_plan_estudios", "Profesor", FrmPlanEstudios::new);
        agregar("Catálogos académicos", "Carreras", "cat_carrera", "Gamma", FrmCarrera::new);
        agregar("Catálogos académicos", "Estudiantes", "cat_estudiantes", "", null);
        agregar("Catálogos académicos", "Materias", "cat_materias", "", null);
        agregar("Catálogos académicos", "Unidades de materia", "cat_unidades_materia", "", null);
        agregar("Catálogos académicos", "Temas de unidad", "cat_temas_unidad", "", null);
        agregar("Catálogos académicos", "Profesores", "cat_profesor", "", null);

        // ----- Examenes -----
        agregar("Exámenes", "Exámenes", "cat_examen", "", null);
        agregar("Exámenes", "Preguntas", "cat_pregunta", "Epsilon", FrmPregunta::new);
        agregar("Exámenes", "Opciones de respuesta", "cat_respuesta", "", null);
        agregar("Exámenes", "Exámenes del alumno", "examen_alumno", "", null);
        agregar("Exámenes", "Respuestas del alumno", "respuesta_alumno", "Epsilon", FrmRespuestaAlumno::new);
        agregar("Exámenes", "Detalle de respuestas", "detalle_respuesta_alumno", "", null);

        // ----- Seguimiento y adaptacion -----
        agregar("Seguimiento y adaptación", "Resultados de examen", "resultado_examen", "", null);
        agregar("Seguimiento y adaptación", "Historial de desempeño", "historial_desempeño", "", null);
        agregar("Seguimiento y adaptación", "Reglas de adaptación", "cat_reglas_adaptacion", "", null);
    }

    private void agregar(String seccion, String titulo, String tabla, String equipo,
            Supplier<JFrame> abrir) {
        modulos.add(new Modulo(seccion, titulo, tabla, equipo, abrir));
    }

    // =====================================================================
    //  Construccion visual del menu
    // =====================================================================
    private void construirMenu() {

        getContentPane().setBackground(Estilo.FONDO);
        getRootPane().setBorder(BorderFactory.createMatteBorder(6, 0, 0, 0, Estilo.NARANJA));

        lblTitulo.setForeground(Estilo.AZUL_OSCURO);
        lblSubtitulo.setForeground(Estilo.TEXTO_SUAVE);

        pnlModulos.setOpaque(false);
        pnlModulos.setLayout(new BoxLayout(pnlModulos, BoxLayout.Y_AXIS));
        pnlModulos.removeAll();

        String seccionActual = null;
        JPanel rejilla = null;
        int listos = 0;

        for (Modulo m : modulos) {

            if (!m.seccion.equals(seccionActual)) {

                seccionActual = m.seccion;

                JLabel titulo = new JLabel(m.seccion.toUpperCase());
                titulo.setFont(titulo.getFont().deriveFont(Font.BOLD, 13f));
                titulo.setForeground(new Color(168, 80, 26));
                titulo.setBorder(BorderFactory.createEmptyBorder(14, 2, 8, 0));
                titulo.setAlignmentX(LEFT_ALIGNMENT);
                pnlModulos.add(titulo);

                rejilla = new JPanel(new GridLayout(0, 3, 12, 12));
                rejilla.setOpaque(false);
                rejilla.setAlignmentX(LEFT_ALIGNMENT);
                pnlModulos.add(rejilla);
            }

            rejilla.add(crearTarjeta(m));

            if (m.abrir != null) {
                listos++;
            }
        }

        lblProgreso.setText(listos + " de " + modulos.size() + " tablas con formulario");
        lblProgreso.setForeground(Estilo.TEXTO_SUAVE);

        pnlModulos.revalidate();
    }

    private JButton crearTarjeta(Modulo m) {

        boolean listo = m.abrir != null;

        String equipo = listo
                ? (m.equipo.isEmpty() ? "Listo" : m.equipo)
                : "Pendiente";

        JButton tarjeta = new JButton("<html><div style='padding:2px'>"
                + "<b style='font-size:12px'>" + m.titulo + "</b><br>"
                + "<span style='font-size:9px'>" + m.tabla + "</span><br>"
                + "<span style='font-size:9px'>" + (listo ? "&#9679; " : "&#9675; ") + equipo + "</span>"
                + "</div></html>");

        tarjeta.setHorizontalAlignment(JButton.LEFT);
        tarjeta.setPreferredSize(new Dimension(270, 78));
        tarjeta.setFocusPainted(false);
        tarjeta.putClientProperty("Nimbus.Overrides.InheritDefaults", Boolean.FALSE);
        tarjeta.setContentAreaFilled(false);
        tarjeta.setOpaque(true);

        if (listo) {

            Color base = Estilo.AZUL_OSCURO;
            Color hover = new Color(44, 66, 98);

            tarjeta.setBackground(base);
            tarjeta.setForeground(Color.WHITE);
            tarjeta.setBorder(BorderFactory.createCompoundBorder(
                    BorderFactory.createMatteBorder(0, 6, 0, 0, Estilo.NARANJA),
                    BorderFactory.createEmptyBorder(8, 12, 8, 12)));
            tarjeta.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
            tarjeta.setToolTipText("Abrir " + m.titulo + " (" + m.tabla + ")");

            tarjeta.addMouseListener(new MouseAdapter() {
                @Override
                public void mouseEntered(MouseEvent e) {
                    tarjeta.setBackground(hover);
                }

                @Override
                public void mouseExited(MouseEvent e) {
                    tarjeta.setBackground(base);
                }
            });

            tarjeta.addActionListener(e -> abrir(m));

        } else {

            tarjeta.setBackground(new Color(233, 229, 218));
            tarjeta.setForeground(new Color(110, 116, 126));
            tarjeta.setBorder(BorderFactory.createCompoundBorder(
                    BorderFactory.createMatteBorder(0, 6, 0, 0, new Color(205, 200, 188)),
                    BorderFactory.createEmptyBorder(8, 12, 8, 12)));
            tarjeta.setToolTipText("Todavía no hay formulario para " + m.tabla);
            tarjeta.addActionListener(e -> JOptionPane.showMessageDialog(this,
                    "El formulario de " + m.titulo + " (" + m.tabla + ") todavía no se integra.\n\n"
                    + "Para agregarlo: en FrmMenuPrincipal.registrarModulos() cambia el null\n"
                    + "de esta tabla por  TuFormulario::new",
                    "Módulo pendiente", JOptionPane.INFORMATION_MESSAGE));
        }

        return tarjeta;
    }

    // Abre el formulario (o lo trae al frente si ya estaba abierto)
    private void abrir(Modulo m) {

        JFrame ventana = abiertas.get(m.tabla);

        if (ventana != null && ventana.isDisplayable()) {
            ventana.toFront();
            ventana.requestFocus();
            return;
        }

        try {
            setCursor(Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));

            ventana = m.abrir.get();

            // Al cerrar un formulario solo se cierra esa ventana, no todo el sistema
            ventana.setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);
            ventana.setLocationRelativeTo(this);
            ventana.setVisible(true);

            abiertas.put(m.tabla, ventana);

        } catch (RuntimeException e) {

            JOptionPane.showMessageDialog(this,
                    "No se pudo abrir " + m.titulo + ".\n" + e.getMessage(),
                    "Error", JOptionPane.ERROR_MESSAGE);

        } finally {
            setCursor(Cursor.getDefaultCursor());
        }
    }

    // Prueba la conexion sin congelar la ventana
    private void revisarConexion() {

        lblConexion.setForeground(Estilo.TEXTO_SUAVE);
        lblConexion.setText("Revisando conexión con MySQL...");

        new SwingWorker<Boolean, Void>() {
            @Override
            protected Boolean doInBackground() {
                try (java.sql.Connection con = Conexion.conectar()) {
                    return con != null;
                } catch (Exception e) {
                    return false;
                }
            }

            @Override
            protected void done() {
                boolean ok;
                try {
                    ok = get();
                } catch (Exception e) {
                    ok = false;
                }

                if (ok) {
                    lblConexion.setForeground(Estilo.VERDE);
                    lblConexion.setText("● Conectado a MySQL (base sistema_adaptativo)");
                } else {
                    lblConexion.setForeground(Estilo.ROJO);
                    lblConexion.setText("● Sin conexión: enciende MySQL en XAMPP y revisa Conexion.java");
                }
            }
        }.execute();
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
        lblSubtitulo = new javax.swing.JLabel();
        pnlModulos = new javax.swing.JPanel();
        lblConexion = new javax.swing.JLabel();
        lblProgreso = new javax.swing.JLabel();

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);

        lblTitulo.setFont(new java.awt.Font("Segoe UI", 1, 28)); // NOI18N
        lblTitulo.setText("Sistema Adaptativo");

        lblSubtitulo.setText("Programación Orientada a Objetos · Elige una tabla para administrarla");

        pnlModulos.setLayout(new java.awt.FlowLayout());

        lblConexion.setText("Revisando conexión...");

        lblProgreso.setText("0 de 0 tablas con formulario");

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addGap(40, 40, 40)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(lblTitulo)
                    .addComponent(lblSubtitulo)
                    .addComponent(pnlModulos, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addGroup(layout.createSequentialGroup()
                        .addComponent(lblConexion)
                        .addGap(30, 30, 30)
                        .addComponent(lblProgreso)))
                .addContainerGap(40, Short.MAX_VALUE))
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addGap(26, 26, 26)
                .addComponent(lblTitulo)
                .addGap(4, 4, 4)
                .addComponent(lblSubtitulo)
                .addGap(8, 8, 8)
                .addComponent(pnlModulos, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(20, 20, 20)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(lblConexion)
                    .addComponent(lblProgreso))
                .addContainerGap(24, Short.MAX_VALUE))
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

    /**
     * @param args the command line arguments
     */
    public static void main(String args[]) {
        /* Set the Nimbus look and feel */
        //<editor-fold defaultstate="collapsed" desc=" Look and feel setting code (optional) ">
        try {
            for (javax.swing.UIManager.LookAndFeelInfo info : javax.swing.UIManager.getInstalledLookAndFeels()) {
                if ("Nimbus".equals(info.getName())) {
                    javax.swing.UIManager.setLookAndFeel(info.getClassName());
                    break;
                }
            }
        } catch (ClassNotFoundException | InstantiationException | IllegalAccessException
                | javax.swing.UnsupportedLookAndFeelException ex) {
            java.util.logging.Logger.getLogger(FrmMenuPrincipal.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        }
        //</editor-fold>

        /* Create and display the form */
        java.awt.EventQueue.invokeLater(() -> new FrmMenuPrincipal().setVisible(true));
    }

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JLabel lblConexion;
    private javax.swing.JLabel lblProgreso;
    private javax.swing.JLabel lblSubtitulo;
    private javax.swing.JLabel lblTitulo;
    private javax.swing.JPanel pnlModulos;
    // End of variables declaration//GEN-END:variables
}
