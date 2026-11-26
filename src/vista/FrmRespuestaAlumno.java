/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/GUIForms/JFrame.java to edit this template
 */
package vista;

import dao.RespuestaAlumnoDAO;
import modelo.Opcion;
import modelo.RespuestaAlumno;
import java.awt.event.KeyEvent;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.List;

import javax.swing.DefaultComboBoxModel;
import javax.swing.JComboBox;
import javax.swing.JComponent;
import javax.swing.JOptionPane;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableModel;
import javax.swing.table.TableRowSorter;

/**
 * Formulario de administracion de la tabla respuesta_alumno.
 * - El alumno/examen y la pregunta se eligen de una lista.
 * - Los puntos se calculan solos segun la pregunta y si es correcta.
 * - Busqueda en vivo, columnas ordenables y filas que se resaltan.
 * - Atajos: Ctrl+S guardar, Ctrl+U actualizar, Supr eliminar, Esc limpiar, Ctrl+F buscar.
 *
 * @author Alberto
 */
public class FrmRespuestaAlumno extends javax.swing.JFrame {

    private final RespuestaAlumnoDAO dao = new RespuestaAlumnoDAO();
    private DefaultTableModel modelo;
    private TableRowSorter<TableModel> sorter;
    private int idSeleccionado = 0;

    private static final DateTimeFormatter FORMATO_FECHA
            = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");

    private static final String AYUDA
            = "Atajos: Ctrl+S guardar · Ctrl+U actualizar · Supr eliminar · Esc limpiar · Ctrl+F buscar";

    /**
     * Creates new form FrmRespuestaAlumno
     */
    public FrmRespuestaAlumno() {
        initComponents();
        setTitle("Sistema Adaptativo - Respuestas del alumno");

        aplicarDiseno();
        cargarCombos();
        configurarTabla();
        configurarEventos();
        limpiar();
        listarRespuestas();

        pack();
        setLocationRelativeTo(null);
    }

    // ===== Diseño e interaccion =====
    private void aplicarDiseno() {

        Estilo.ventana(this, lblTitulo, lblInstrucciones);

        Estilo.boton(btnGuardar, Estilo.VERDE, "Guardar como respuesta nueva (Ctrl+S)");
        Estilo.boton(btnActualizar, Estilo.AZUL, "Guardar los cambios de la fila seleccionada (Ctrl+U)");
        Estilo.boton(btnEliminar, Estilo.ROJO, "Eliminar la fila seleccionada (Supr)");
        Estilo.boton(btnLimpiar, Estilo.GRIS, "Vaciar el formulario (Esc)");

        Estilo.contador(txtRespuestaTexto, lblContRespuesta, 250);
        Estilo.soloNumeros(txtPuntosObtenidos, true, 999.99);

        lblPuntosMax.setForeground(Estilo.GRIS);

        cmbExamenAlumno.setToolTipText("Alumno y examen que presentó");
        cmbPregunta.setToolTipText("Pregunta que contestó");
        cmbEsCorrecta.setToolTipText("Al cambiarlo se recalculan los puntos");
        txtPuntosObtenidos.setToolTipText("Se llena solo; puedes cambiarlo para dar puntos parciales");
        txtFechaRespuesta.setToolTipText("Formato AAAA-MM-DD HH:MM; vacío = ahora");

        // La fecha se marca en rojo mientras no tenga el formato correcto
        txtFechaRespuesta.getDocument().addDocumentListener(new DocumentListener() {
            private void revisar() {
                String t = txtFechaRespuesta.getText().trim();
                try {
                    if (!t.isEmpty()) {
                        LocalDateTime.parse(t, FORMATO_FECHA);
                    }
                    Estilo.quitarError(txtFechaRespuesta);
                } catch (DateTimeParseException e) {
                    Estilo.marcarError(txtFechaRespuesta);
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

        Estilo.atajo(getRootPane(), "guardar", KeyEvent.VK_S, Estilo.CTRL, this::guardar);
        Estilo.atajo(getRootPane(), "actualizar", KeyEvent.VK_U, Estilo.CTRL, this::actualizar);
        Estilo.atajo(getRootPane(), "limpiar", KeyEvent.VK_ESCAPE, 0, this::limpiar);
        Estilo.atajo(getRootPane(), "buscar", KeyEvent.VK_F, Estilo.CTRL, () -> {
            txtBuscar.requestFocus();
            txtBuscar.selectAll();
        });
    }

    private void avisar(String mensaje, Estilo.Tipo tipo) {
        Estilo.estado(lblEstado, mensaje, tipo, AYUDA);
    }

    private void modoEdicion(boolean editando) {

        btnActualizar.setEnabled(editando);
        btnEliminar.setEnabled(editando);

        lblInstrucciones.setText(editando
                ? "Editando la respuesta #" + idSeleccionado
                  + ". Cambia lo que necesites y presiona Actualizar, o Limpiar para cancelar."
                : "Elige al alumno y la pregunta. Los puntos y la fecha se llenan solos. Haz clic en una fila para editarla.");
    }

    private void quitarErrores() {
        for (JComponent c : new JComponent[]{txtRespuestaTexto, txtPuntosObtenidos,
            txtFechaRespuesta, cmbExamenAlumno, cmbPregunta}) {
            Estilo.quitarError(c);
        }
    }

    // ===== Listas desplegables =====
    private void cargarCombos() {

        try {
            cmbExamenAlumno.setModel(new DefaultComboBoxModel<>(
                    dao.listarExamenesAlumno().toArray(new Opcion[0])));

            cmbPregunta.setModel(new DefaultComboBoxModel<>(
                    dao.listarPreguntas().toArray(new Opcion[0])));

        } catch (RuntimeException e) {

            JOptionPane.showMessageDialog(
                    this,
                    e.getMessage(),
                    "Error al cargar listas",
                    JOptionPane.ERROR_MESSAGE
            );
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

    // Muestra "de 20 pts" junto a los puntos
    private void mostrarMaximo() {

        Opcion pregunta = (Opcion) cmbPregunta.getSelectedItem();

        lblPuntosMax.setText(pregunta == null ? ""
                : "de " + formatoPuntos(pregunta.getValor()) + " pts");
    }

    // Si es correcta: puntos = valor de la pregunta. Si no: 0
    private void calcularPuntos() {

        Opcion pregunta = (Opcion) cmbPregunta.getSelectedItem();

        mostrarMaximo();

        if (pregunta == null) {
            return;
        }

        boolean correcta = "Sí".equals(cmbEsCorrecta.getSelectedItem());

        txtPuntosObtenidos.setText(formatoPuntos(correcta ? pregunta.getValor() : 0));
    }

    private String formatoPuntos(double valor) {
        return valor == Math.floor(valor)
                ? String.valueOf((long) valor)
                : String.valueOf(valor);
    }

    private void configurarEventos() {

        btnGuardar.addActionListener(e -> guardar());

        btnActualizar.addActionListener(e -> actualizar());

        btnEliminar.addActionListener(e -> eliminar());

        btnLimpiar.addActionListener(e -> limpiar());

        cmbPregunta.addActionListener(e -> calcularPuntos());

        cmbEsCorrecta.addActionListener(e -> calcularPuntos());

        tblRespuestas.getSelectionModel().addListSelectionListener(e -> {

            if (!e.getValueIsAdjusting()) {
                seleccionarRespuesta();
            }
        });

        tblRespuestas.getInputMap().put(
                javax.swing.KeyStroke.getKeyStroke(KeyEvent.VK_DELETE, 0), "eliminar");
        tblRespuestas.getActionMap().put("eliminar", new javax.swing.AbstractAction() {
            @Override
            public void actionPerformed(java.awt.event.ActionEvent e) {
                eliminar();
            }
        });
    }

    private void limpiar() {

        idSeleccionado = 0;

        txtRespuestaTexto.setText("");

        cmbEsCorrecta.setSelectedIndex(0);

        // Fecha y hora actual ya escrita
        txtFechaRespuesta.setText(LocalDateTime.now().format(FORMATO_FECHA));

        calcularPuntos();

        tblRespuestas.clearSelection();

        quitarErrores();
        modoEdicion(false);
        avisar(AYUDA, Estilo.Tipo.INFO);

        txtRespuestaTexto.requestFocus();
    }

    private void seleccionarRespuesta() {

        int filaVista = tblRespuestas.getSelectedRow();

        if (filaVista == -1) {
            return;
        }

        int fila = tblRespuestas.convertRowIndexToModel(filaVista);

        idSeleccionado = Integer.parseInt(
                modelo.getValueAt(fila, 0).toString()
        );

        seleccionarPorId(cmbExamenAlumno, ((Opcion) modelo.getValueAt(fila, 1)).getId());
        seleccionarPorId(cmbPregunta, ((Opcion) modelo.getValueAt(fila, 2)).getId());

        txtRespuestaTexto.setText(obtenerTexto(fila, 3));

        String correcta = obtenerTexto(fila, 4);

        if (!correcta.isEmpty()) {
            cmbEsCorrecta.setSelectedItem(correcta);
        }

        // Se ponen despues de los combos para respetar lo guardado
        Object puntos = modelo.getValueAt(fila, 5);
        txtPuntosObtenidos.setText(puntos instanceof Number
                ? formatoPuntos(((Number) puntos).doubleValue()) : obtenerTexto(fila, 5));
        txtFechaRespuesta.setText(obtenerTexto(fila, 6));

        quitarErrores();
        modoEdicion(true);
    }

    private String obtenerTexto(int fila, int columna) {

        Object valor = modelo.getValueAt(fila, columna);

        return valor == null ? "" : valor.toString();
    }

    private void eliminar() {

        if (idSeleccionado == 0) {
            avisar("selecciona una respuesta de la tabla para eliminarla.", Estilo.Tipo.ERROR);
            return;
        }

        int respuesta = JOptionPane.showConfirmDialog(
                this,
                "¿Eliminar la respuesta #" + idSeleccionado + "?",
                "Confirmar eliminación",
                JOptionPane.YES_NO_OPTION,
                JOptionPane.WARNING_MESSAGE
        );

        if (respuesta == JOptionPane.YES_OPTION) {

            try {

                int id = idSeleccionado;

                if (dao.eliminar(id)) {
                    limpiar();
                    listarRespuestas();
                    avisar("respuesta #" + id + " eliminada.", Estilo.Tipo.EXITO);
                }

            } catch (RuntimeException e) {

                avisar("no se pudo eliminar la respuesta.", Estilo.Tipo.ERROR);

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
            avisar("selecciona una respuesta de la tabla para actualizarla.", Estilo.Tipo.ERROR);
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

            RespuestaAlumno r = obtenerDatos();
            int id = idSeleccionado;

            boolean resultado = actualizando
                    ? dao.actualizar(r)
                    : dao.insertar(r);

            if (resultado) {

                limpiar();
                listarRespuestas();

                avisar(actualizando
                        ? "respuesta #" + id + " actualizada."
                        : "respuesta guardada.", Estilo.Tipo.EXITO);
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
                    "Alumno y examen",
                    "Pregunta",
                    "Respuesta",
                    "¿Correcta?",
                    "Puntos",
                    "Fecha"
                }, 0
        ) {
            @Override
            public boolean isCellEditable(int fila, int columna) {
                return false;
            }

            @Override
            public Class<?> getColumnClass(int columna) {
                return switch (columna) {
                    case 0 -> Integer.class;
                    case 5 -> Double.class;
                    default -> Object.class;
                };
            }
        };

        tblRespuestas.setModel(modelo);

        // Correcta en verde / incorrecta en rojo (la palabra Sí/No tambien lo dice)
        sorter = Estilo.tabla(tblRespuestas, (columna, valor) -> {

            if (columna == 4 && valor != null) {
                return "Sí".equals(valor.toString()) ? Estilo.VERDE : Estilo.ROJO;
            }

            return null;
        });

        java.util.Comparator<Object> porTexto
                = java.util.Comparator.comparing(Object::toString, String.CASE_INSENSITIVE_ORDER);
        sorter.setComparator(1, porTexto);
        sorter.setComparator(2, porTexto);

        Estilo.busqueda(txtBuscar, sorter, this::actualizarTotal);

        int[] anchos = {50, 240, 220, 220, 80, 60, 130};

        for (int i = 0; i < anchos.length; i++) {
            tblRespuestas.getColumnModel().getColumn(i).setPreferredWidth(anchos[i]);
        }
    }

    private void actualizarTotal() {

        int total = modelo.getRowCount();
        int visibles = tblRespuestas.getRowCount();

        lblTotal.setText(visibles == total
                ? total + (total == 1 ? " respuesta" : " respuestas")
                : "Mostrando " + visibles + " de " + total);
    }

    private void listarRespuestas() {

        modelo.setRowCount(0);

        try {

            List<RespuestaAlumno> lista = dao.listar();

            for (RespuestaAlumno r : lista) {

                modelo.addRow(new Object[]{
                    r.getCveRespuestaAlumno(),
                    buscarOpcion(cmbExamenAlumno, r.getCveExamenAlumno()),
                    buscarOpcion(cmbPregunta, r.getCvePregunta()),
                    r.getRespuestaTexto(),
                    r.isEsCorrecta() ? "Sí" : "No",
                    r.getPuntosObtenidos(),
                    r.getFechaRespuesta() != null
                    ? r.getFechaRespuesta().format(FORMATO_FECHA) : null
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

    private RespuestaAlumno obtenerDatos() {

        Opcion examenAlumno = (Opcion) cmbExamenAlumno.getSelectedItem();

        if (examenAlumno == null) {
            throw new Estilo.Invalido(
                    "no hay exámenes de alumnos registrados. Primero da de alta uno.",
                    cmbExamenAlumno);
        }

        Opcion pregunta = (Opcion) cmbPregunta.getSelectedItem();

        if (pregunta == null) {
            throw new Estilo.Invalido(
                    "no hay preguntas registradas. Primero da de alta una pregunta.",
                    cmbPregunta);
        }

        String respuestaTexto = txtRespuestaTexto.getText().trim();

        if (respuestaTexto.isEmpty()) {
            throw new Estilo.Invalido("escribe la respuesta del alumno.", txtRespuestaTexto);
        }

        if (respuestaTexto.length() > 250) {
            throw new Estilo.Invalido(
                    "la respuesta no debe superar los 250 caracteres.", txtRespuestaTexto);
        }

        boolean esCorrecta = "Sí".equals(cmbEsCorrecta.getSelectedItem());

        double puntosObtenidos = convertirPuntos(pregunta.getValor());

        LocalDateTime fechaRespuesta = convertirFecha();

        return new RespuestaAlumno(
                idSeleccionado,
                examenAlumno.getId(),
                pregunta.getId(),
                respuestaTexto,
                esCorrecta,
                puntosObtenidos,
                fechaRespuesta
        );
    }

    // No puede ser negativo ni pasar de lo que vale la pregunta
    private double convertirPuntos(double maximo) {

        String texto = txtPuntosObtenidos.getText().trim();

        if (texto.isEmpty()) {
            return 0;
        }

        try {
            double valor = Double.parseDouble(texto);

            if (valor < 0) {
                throw new Estilo.Invalido(
                        "los puntos no pueden ser negativos.", txtPuntosObtenidos);
            }

            if (maximo > 0 && valor > maximo) {
                throw new Estilo.Invalido(
                        "los puntos no pueden pasar de " + formatoPuntos(maximo)
                        + " (lo que vale la pregunta).", txtPuntosObtenidos);
            }

            return valor;

        } catch (NumberFormatException e) {
            throw new Estilo.Invalido(
                    "escribe un número válido en puntos obtenidos (ej. 10 o 2.5).",
                    txtPuntosObtenidos);
        }
    }

    // Si la fecha se deja vacia, se usa la fecha y hora actual
    private LocalDateTime convertirFecha() {

        String texto = txtFechaRespuesta.getText().trim();

        if (texto.isEmpty()) {
            return LocalDateTime.now().withSecond(0).withNano(0);
        }

        try {
            return LocalDateTime.parse(texto, FORMATO_FECHA);

        } catch (DateTimeParseException e) {
            throw new Estilo.Invalido(
                    "la fecha debe ser AAAA-MM-DD HH:MM (ej. 2026-11-20 18:30) "
                    + "o déjala vacía para usar la fecha actual.", txtFechaRespuesta);
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
        lblExamenAlumno = new javax.swing.JLabel();
        cmbExamenAlumno = new javax.swing.JComboBox<>();
        lblPregunta = new javax.swing.JLabel();
        cmbPregunta = new javax.swing.JComboBox<>();
        lblRespuestaTexto = new javax.swing.JLabel();
        txtRespuestaTexto = new javax.swing.JTextField();
        lblContRespuesta = new javax.swing.JLabel();
        lblEsCorrecta = new javax.swing.JLabel();
        cmbEsCorrecta = new javax.swing.JComboBox<>();
        lblPuntosObtenidos = new javax.swing.JLabel();
        txtPuntosObtenidos = new javax.swing.JTextField();
        lblPuntosMax = new javax.swing.JLabel();
        lblFechaRespuesta = new javax.swing.JLabel();
        txtFechaRespuesta = new javax.swing.JTextField();
        btnGuardar = new javax.swing.JButton();
        btnActualizar = new javax.swing.JButton();
        btnEliminar = new javax.swing.JButton();
        btnLimpiar = new javax.swing.JButton();
        lblBuscar = new javax.swing.JLabel();
        txtBuscar = new javax.swing.JTextField();
        lblTotal = new javax.swing.JLabel();
        jScrollPane1 = new javax.swing.JScrollPane();
        tblRespuestas = new javax.swing.JTable();
        lblEstado = new javax.swing.JLabel();

        setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);

        lblTitulo.setFont(new java.awt.Font("Segoe UI", 1, 22)); // NOI18N
        lblTitulo.setText("Respuestas del alumno");

        lblInstrucciones.setText("Elige al alumno y la pregunta. Los puntos y la fecha se llenan solos. Haz clic en una fila para editarla.");

        lblExamenAlumno.setText("Alumno y examen");

        lblPregunta.setText("Pregunta");

        lblRespuestaTexto.setText("Respuesta del alumno");

        lblContRespuesta.setText("0");

        lblEsCorrecta.setText("¿Es correcta?");

        cmbEsCorrecta.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "Sí", "No" }));

        lblPuntosObtenidos.setText("Puntos obtenidos");

        lblPuntosMax.setText("0");

        lblFechaRespuesta.setText("Fecha (AAAA-MM-DD HH:MM)");

        btnGuardar.setText("Guardar");

        btnActualizar.setText("Actualizar");

        btnEliminar.setText("Eliminar");

        btnLimpiar.setText("Limpiar");

        lblBuscar.setText("Buscar:");

        lblTotal.setText("0 registros");

        tblRespuestas.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {

            },
            new String [] {

            }
        ));
        jScrollPane1.setViewportView(tblRespuestas);

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
                            .addComponent(lblExamenAlumno)
                            .addComponent(lblPregunta)
                            .addComponent(lblRespuestaTexto)
                            .addComponent(lblEsCorrecta)
                            .addComponent(lblPuntosObtenidos)
                            .addComponent(lblFechaRespuesta))
                        .addGap(30, 30, 30)
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(cmbExamenAlumno, javax.swing.GroupLayout.PREFERRED_SIZE, 450, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(cmbPregunta, javax.swing.GroupLayout.PREFERRED_SIZE, 450, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addGroup(layout.createSequentialGroup()
                                .addComponent(txtRespuestaTexto, javax.swing.GroupLayout.PREFERRED_SIZE, 450, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addGap(10, 10, 10)
                                .addComponent(lblContRespuesta))
                            .addComponent(cmbEsCorrecta, javax.swing.GroupLayout.PREFERRED_SIZE, 120, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addGroup(layout.createSequentialGroup()
                                .addComponent(txtPuntosObtenidos, javax.swing.GroupLayout.PREFERRED_SIZE, 100, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addGap(10, 10, 10)
                                .addComponent(lblPuntosMax))
                            .addComponent(txtFechaRespuesta, javax.swing.GroupLayout.PREFERRED_SIZE, 160, javax.swing.GroupLayout.PREFERRED_SIZE)))
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
                    .addComponent(jScrollPane1, javax.swing.GroupLayout.PREFERRED_SIZE, 980, javax.swing.GroupLayout.PREFERRED_SIZE)
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
                    .addComponent(lblExamenAlumno)
                    .addComponent(cmbExamenAlumno, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(10, 10, 10)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(lblPregunta)
                    .addComponent(cmbPregunta, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(10, 10, 10)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(lblRespuestaTexto)
                    .addComponent(txtRespuestaTexto, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(lblContRespuesta))
                .addGap(10, 10, 10)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(lblEsCorrecta)
                    .addComponent(cmbEsCorrecta, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(10, 10, 10)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(lblPuntosObtenidos)
                    .addComponent(txtPuntosObtenidos, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(lblPuntosMax))
                .addGap(10, 10, 10)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(lblFechaRespuesta)
                    .addComponent(txtFechaRespuesta, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
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
            java.util.logging.Logger.getLogger(FrmRespuestaAlumno.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (InstantiationException ex) {
            java.util.logging.Logger.getLogger(FrmRespuestaAlumno.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (IllegalAccessException ex) {
            java.util.logging.Logger.getLogger(FrmRespuestaAlumno.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (javax.swing.UnsupportedLookAndFeelException ex) {
            java.util.logging.Logger.getLogger(FrmRespuestaAlumno.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        }
        //</editor-fold>

        /* Create and display the form */
        java.awt.EventQueue.invokeLater(new Runnable() {
            public void run() {
                new FrmRespuestaAlumno().setVisible(true);
            }
        });
    }

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton btnActualizar;
    private javax.swing.JButton btnEliminar;
    private javax.swing.JButton btnGuardar;
    private javax.swing.JButton btnLimpiar;
    private javax.swing.JComboBox<String> cmbEsCorrecta;
    private javax.swing.JComboBox<modelo.Opcion> cmbExamenAlumno;
    private javax.swing.JComboBox<modelo.Opcion> cmbPregunta;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JLabel lblBuscar;
    private javax.swing.JLabel lblContRespuesta;
    private javax.swing.JLabel lblEsCorrecta;
    private javax.swing.JLabel lblEstado;
    private javax.swing.JLabel lblExamenAlumno;
    private javax.swing.JLabel lblFechaRespuesta;
    private javax.swing.JLabel lblInstrucciones;
    private javax.swing.JLabel lblPregunta;
    private javax.swing.JLabel lblPuntosMax;
    private javax.swing.JLabel lblPuntosObtenidos;
    private javax.swing.JLabel lblRespuestaTexto;
    private javax.swing.JLabel lblTitulo;
    private javax.swing.JLabel lblTotal;
    private javax.swing.JTable tblRespuestas;
    private javax.swing.JTextField txtBuscar;
    private javax.swing.JTextField txtFechaRespuesta;
    private javax.swing.JTextField txtPuntosObtenidos;
    private javax.swing.JTextField txtRespuestaTexto;
    // End of variables declaration//GEN-END:variables
}
