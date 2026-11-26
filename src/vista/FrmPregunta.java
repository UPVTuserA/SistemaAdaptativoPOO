/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/GUIForms/JFrame.java to edit this template
 */
package vista;

import dao.PreguntaDAO;
import modelo.Opcion;
import modelo.Pregunta;
import java.awt.Color;
import java.awt.event.KeyEvent;
import java.util.List;

import javax.swing.DefaultComboBoxModel;
import javax.swing.JComboBox;
import javax.swing.JComponent;
import javax.swing.JOptionPane;
import javax.swing.JTextField;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableModel;
import javax.swing.table.TableRowSorter;

/**
 * Formulario de administracion de la tabla cat_pregunta.
 * - El examen y el tema se eligen de una lista (no hay que saberse las claves).
 * - Busqueda en vivo, columnas ordenables y filas que se resaltan.
 * - Los campos mal llenados se marcan en rojo mientras escribes.
 * - Atajos: Ctrl+S guardar, Ctrl+U actualizar, Supr eliminar, Esc limpiar, Ctrl+F buscar.
 *
 * @author Alberto
 */
public class FrmPregunta extends javax.swing.JFrame {

    private final PreguntaDAO dao = new PreguntaDAO();
    private DefaultTableModel modelo;
    private TableRowSorter<TableModel> sorter;
    private int idSeleccionado = 0;

    // Valores que ya aparecen escritos para llenar mas rapido
    private static final String PUNTOS_DEFAULT = "10";
    private static final String TIEMPO_DEFAULT = "5";

    private static final String AYUDA
            = "Atajos: Ctrl+S guardar · Ctrl+U actualizar · Supr eliminar · Esc limpiar · Ctrl+F buscar";

    /**
     * Creates new form FrmPregunta
     */
    public FrmPregunta() {
        initComponents();
        setTitle("Sistema Adaptativo - Preguntas");

        aplicarDiseno();
        cargarCombos();
        configurarTabla();
        configurarEventos();
        limpiar();
        listarPreguntas();

        pack();
        setLocationRelativeTo(null);
    }

    // ===== Diseño e interaccion =====
    private void aplicarDiseno() {

        Estilo.ventana(this, lblTitulo, lblInstrucciones);

        Estilo.boton(btnGuardar, Estilo.VERDE, "Guardar como pregunta nueva (Ctrl+S)");
        Estilo.boton(btnActualizar, Estilo.AZUL, "Guardar los cambios de la fila seleccionada (Ctrl+U)");
        Estilo.boton(btnEliminar, Estilo.ROJO, "Eliminar la fila seleccionada (Supr)");
        Estilo.boton(btnLimpiar, Estilo.GRIS, "Vaciar el formulario (Esc)");

        Estilo.contador(txtPregunta, lblContPregunta, 250);
        Estilo.contador(txtRetroalimentacion, lblContRetro, 200);

        Estilo.soloNumeros(txtValorPuntos, true, 999.99);
        Estilo.soloNumeros(txtTiempoEstimado, false, Integer.MAX_VALUE);

        cmbExamen.setToolTipText("Examen al que pertenece la pregunta");
        cmbTema.setToolTipText("Tema que evalúa la pregunta");
        txtValorPuntos.setToolTipText("Puntos que vale (0 a 999.99)");
        txtTiempoEstimado.setToolTipText("Minutos aproximados para contestar");
        txtImagen.setToolTipText("Nombre o ruta de una imagen (opcional)");

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

    // Activa Actualizar/Eliminar solo cuando hay una fila elegida
    private void modoEdicion(boolean editando) {

        btnActualizar.setEnabled(editando);
        btnEliminar.setEnabled(editando);

        lblInstrucciones.setText(editando
                ? "Editando la pregunta #" + idSeleccionado
                  + ". Cambia lo que necesites y presiona Actualizar, o Limpiar para cancelar."
                : "Llena los campos y presiona Guardar. Haz clic en una fila de la tabla para editarla o eliminarla.");
    }

    private void quitarErrores() {
        for (JComponent c : new JComponent[]{txtPregunta, txtValorPuntos,
            txtTiempoEstimado, txtRetroalimentacion, txtImagen, cmbExamen, cmbTema}) {
            Estilo.quitarError(c);
        }
    }

    // ===== Listas desplegables =====
    private void cargarCombos() {

        try {
            cmbExamen.setModel(new DefaultComboBoxModel<>(
                    dao.listarExamenes().toArray(new Opcion[0])));

            cmbTema.setModel(new DefaultComboBoxModel<>(
                    dao.listarTemas().toArray(new Opcion[0])));

        } catch (RuntimeException e) {

            JOptionPane.showMessageDialog(
                    this,
                    e.getMessage(),
                    "Error al cargar listas",
                    JOptionPane.ERROR_MESSAGE
            );
        }

        // Valores ENUM reales de la base (si no se pueden leer, se quedan los del diseño)
        llenarCombo(cmbNivelDificultad, dao.obtenerValoresEnum("nivel_dificultad"));
        llenarCombo(cmbEstatus, dao.obtenerValoresEnum("estatus"));
    }

    private void llenarCombo(JComboBox<String> combo, List<String> valores) {

        if (valores != null && !valores.isEmpty()) {
            combo.setModel(new DefaultComboBoxModel<>(
                    valores.toArray(new String[0])
            ));
        }
    }

    // Selecciona en el combo la opcion que tenga esa clave
    private void seleccionarPorId(JComboBox<Opcion> combo, int id) {

        for (int i = 0; i < combo.getItemCount(); i++) {

            if (combo.getItemAt(i).getId() == id) {
                combo.setSelectedIndex(i);
                return;
            }
        }
    }

    // Busca la opcion de una clave para mostrar su nombre en la tabla
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

        tblPreguntas.getSelectionModel().addListSelectionListener(e -> {

            if (!e.getValueIsAdjusting()) {
                seleccionarPregunta();
            }
        });

        // Supr sobre la tabla elimina la fila
        tblPreguntas.getInputMap().put(
                javax.swing.KeyStroke.getKeyStroke(KeyEvent.VK_DELETE, 0), "eliminar");
        tblPreguntas.getActionMap().put("eliminar", new javax.swing.AbstractAction() {
            @Override
            public void actionPerformed(java.awt.event.ActionEvent e) {
                eliminar();
            }
        });
    }

    private void limpiar() {

        idSeleccionado = 0;

        txtPregunta.setText("");
        txtImagen.setText("");
        txtRetroalimentacion.setText("");

        txtValorPuntos.setText(PUNTOS_DEFAULT);
        txtTiempoEstimado.setText(TIEMPO_DEFAULT);

        if (cmbNivelDificultad.getItemCount() > 0) {
            cmbNivelDificultad.setSelectedIndex(0);
        }

        if (cmbEstatus.getItemCount() > 0) {
            cmbEstatus.setSelectedIndex(0);
        }

        tblPreguntas.clearSelection();

        quitarErrores();
        modoEdicion(false);
        avisar(AYUDA, Estilo.Tipo.INFO);

        txtPregunta.requestFocus();
    }

    private void seleccionarPregunta() {

        int filaVista = tblPreguntas.getSelectedRow();

        if (filaVista == -1) {
            return;
        }

        int fila = tblPreguntas.convertRowIndexToModel(filaVista);

        idSeleccionado = Integer.parseInt(
                modelo.getValueAt(fila, 0).toString()
        );

        seleccionarPorId(cmbExamen, ((Opcion) modelo.getValueAt(fila, 1)).getId());
        seleccionarPorId(cmbTema, ((Opcion) modelo.getValueAt(fila, 2)).getId());

        txtPregunta.setText(obtenerTexto(fila, 3));

        String nivel = obtenerTexto(fila, 4);

        if (!nivel.isEmpty()) {
            cmbNivelDificultad.setSelectedItem(nivel);
        }

        txtValorPuntos.setText(obtenerTexto(fila, 5));
        txtTiempoEstimado.setText(obtenerTexto(fila, 6));
        txtRetroalimentacion.setText(obtenerTexto(fila, 7));
        txtImagen.setText(obtenerTexto(fila, 8));

        String estatus = obtenerTexto(fila, 9);

        if (!estatus.isEmpty()) {
            cmbEstatus.setSelectedItem(estatus);
        }

        quitarErrores();
        modoEdicion(true);
    }

    private String obtenerTexto(int fila, int columna) {

        Object valor = modelo.getValueAt(fila, columna);

        return valor == null ? "" : valor.toString();
    }

    private void eliminar() {

        if (idSeleccionado == 0) {
            avisar("selecciona una pregunta de la tabla para eliminarla.", Estilo.Tipo.ERROR);
            return;
        }

        int respuesta = JOptionPane.showConfirmDialog(
                this,
                "¿Eliminar la pregunta #" + idSeleccionado + "?\n\n" + txtPregunta.getText(),
                "Confirmar eliminación",
                JOptionPane.YES_NO_OPTION,
                JOptionPane.WARNING_MESSAGE
        );

        if (respuesta == JOptionPane.YES_OPTION) {

            try {

                int id = idSeleccionado;

                if (dao.eliminar(id)) {
                    limpiar();
                    listarPreguntas();
                    avisar("pregunta #" + id + " eliminada.", Estilo.Tipo.EXITO);
                }

            } catch (RuntimeException e) {

                avisar("no se pudo eliminar la pregunta.", Estilo.Tipo.ERROR);

                JOptionPane.showMessageDialog(
                        this,
                        "No fue posible eliminar la pregunta.\n"
                        + "Si ya tiene respuestas registradas, primero hay que borrar esas respuestas.\n\n"
                        + e.getMessage(),
                        "Error",
                        JOptionPane.ERROR_MESSAGE
                );
            }
        }
    }

    private void actualizar() {

        if (idSeleccionado == 0) {
            avisar("selecciona una pregunta de la tabla para actualizarla.", Estilo.Tipo.ERROR);
            return;
        }

        guardarEnBase(true);
    }

    private void guardar() {
        guardarEnBase(false);
    }

    // Guarda nueva o actualiza; los errores de captura se marcan en el campo
    private void guardarEnBase(boolean actualizando) {

        quitarErrores();

        try {

            Pregunta pregunta = obtenerDatos();
            int id = idSeleccionado;

            boolean resultado = actualizando
                    ? dao.actualizar(pregunta)
                    : dao.insertar(pregunta);

            if (resultado) {

                limpiar();
                listarPreguntas();

                avisar(actualizando
                        ? "pregunta #" + id + " actualizada."
                        : "pregunta guardada.", Estilo.Tipo.EXITO);
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
                    "Examen",
                    "Tema",
                    "Pregunta",
                    "Dificultad",
                    "Puntos",
                    "Tiempo",
                    "Retroalimentación",
                    "Imagen",
                    "Estatus"
                }, 0
        ) {
            @Override
            public boolean isCellEditable(int fila, int columna) {
                return false;
            }

            // Para que las columnas numericas se ordenen como numeros
            @Override
            public Class<?> getColumnClass(int columna) {
                return switch (columna) {
                    case 0, 6 -> Integer.class;
                    case 5 -> Double.class;
                    default -> Object.class;
                };
            }
        };

        tblPreguntas.setModel(modelo);

        // Colores con texto: la dificultad y el estatus se leen tambien en palabras
        sorter = Estilo.tabla(tblPreguntas, (columna, valor) -> {

            if (valor == null) {
                return null;
            }

            if (columna == 4) {
                return switch (valor.toString()) {
                    case "básico" -> Estilo.VERDE;
                    case "intermedio" -> new Color(176, 96, 20);
                    case "avanzado" -> Estilo.ROJO;
                    default -> null;
                };
            }

            if (columna == 9 && "inactiva".equals(valor.toString())) {
                return Estilo.GRIS;
            }

            return null;
        });

        // Las listas se ordenan por su texto
        java.util.Comparator<Object> porTexto
                = java.util.Comparator.comparing(Object::toString, String.CASE_INSENSITIVE_ORDER);
        sorter.setComparator(1, porTexto);
        sorter.setComparator(2, porTexto);

        Estilo.busqueda(txtBuscar, sorter, this::actualizarTotal);

        int[] anchos = {50, 180, 140, 230, 85, 60, 60, 170, 80, 70};

        for (int i = 0; i < anchos.length; i++) {
            tblPreguntas.getColumnModel().getColumn(i).setPreferredWidth(anchos[i]);
        }
    }

    private void actualizarTotal() {

        int total = modelo.getRowCount();
        int visibles = tblPreguntas.getRowCount();

        lblTotal.setText(visibles == total
                ? total + (total == 1 ? " pregunta" : " preguntas")
                : "Mostrando " + visibles + " de " + total);
    }

    private void listarPreguntas() {

        modelo.setRowCount(0);

        try {

            List<Pregunta> lista = dao.listar();

            for (Pregunta p : lista) {

                modelo.addRow(new Object[]{
                    p.getCvePregunta(),
                    buscarOpcion(cmbExamen, p.getCveExamen()),
                    buscarOpcion(cmbTema, p.getClaveTema()),
                    p.getPregunta(),
                    p.getNivelDificultad(),
                    p.getValorPuntos(),
                    p.getTiempoEstimado(),
                    p.getRetroalimentacion(),
                    p.getImagen(),
                    p.getEstatus()
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

    private Pregunta obtenerDatos() {

        Opcion examen = (Opcion) cmbExamen.getSelectedItem();

        if (examen == null) {
            throw new Estilo.Invalido(
                    "no hay exámenes registrados. Primero da de alta un examen.", cmbExamen);
        }

        Opcion tema = (Opcion) cmbTema.getSelectedItem();

        if (tema == null) {
            throw new Estilo.Invalido(
                    "no hay temas registrados. Primero da de alta un tema.", cmbTema);
        }

        String pregunta = txtPregunta.getText().trim();

        if (pregunta.isEmpty()) {
            throw new Estilo.Invalido("escribe la pregunta.", txtPregunta);
        }

        if (pregunta.length() > 250) {
            throw new Estilo.Invalido(
                    "la pregunta no debe superar los 250 caracteres.", txtPregunta);
        }

        String imagen = txtImagen.getText().trim();

        if (imagen.length() > 100) {
            throw new Estilo.Invalido(
                    "la imagen no debe superar los 100 caracteres.", txtImagen);
        }

        String nivelDificultad
                = cmbNivelDificultad.getSelectedItem().toString();

        double valorPuntos = convertirDecimal(txtValorPuntos);

        int tiempoEstimado = convertirEntero(txtTiempoEstimado, "el tiempo estimado");

        String retroalimentacion = txtRetroalimentacion.getText().trim();

        if (retroalimentacion.length() > 200) {
            throw new Estilo.Invalido(
                    "la retroalimentación no debe superar los 200 caracteres.",
                    txtRetroalimentacion);
        }

        String estatus = cmbEstatus.getSelectedItem().toString();

        return new Pregunta(
                idSeleccionado,
                examen.getId(),
                tema.getId(),
                pregunta,
                imagen.isEmpty() ? null : imagen,
                nivelDificultad,
                valorPuntos,
                retroalimentacion.isEmpty() ? null : retroalimentacion,
                tiempoEstimado,
                estatus
        );
    }

    private int convertirEntero(JTextField campo, String nombre) {

        String texto = campo.getText().trim();

        if (texto.isEmpty()) {
            throw new Estilo.Invalido("escribe " + nombre + ".", campo);
        }

        try {
            int valor = Integer.parseInt(texto);

            if (valor < 0) {
                throw new Estilo.Invalido(nombre + " no puede ser negativo.", campo);
            }

            return valor;

        } catch (NumberFormatException e) {
            throw new Estilo.Invalido(
                    "escribe solo números enteros en " + nombre + ".", campo);
        }
    }

    // valor_puntos es DECIMAL(5,2): maximo 999.99
    private double convertirDecimal(JTextField campo) {

        String texto = campo.getText().trim();

        if (texto.isEmpty()) {
            throw new Estilo.Invalido("escribe el valor en puntos.", campo);
        }

        try {
            double valor = Double.parseDouble(texto);

            if (valor < 0 || valor > 999.99) {
                throw new Estilo.Invalido(
                        "el valor en puntos debe estar entre 0 y 999.99.", campo);
            }

            return valor;

        } catch (NumberFormatException e) {
            throw new Estilo.Invalido(
                    "escribe un número válido en valor en puntos (ej. 10 o 2.5).", campo);
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
        lblExamen = new javax.swing.JLabel();
        cmbExamen = new javax.swing.JComboBox<>();
        lblTema = new javax.swing.JLabel();
        cmbTema = new javax.swing.JComboBox<>();
        lblPregunta = new javax.swing.JLabel();
        txtPregunta = new javax.swing.JTextField();
        lblContPregunta = new javax.swing.JLabel();
        lblNivelDificultad = new javax.swing.JLabel();
        cmbNivelDificultad = new javax.swing.JComboBox<>();
        lblValorPuntos = new javax.swing.JLabel();
        txtValorPuntos = new javax.swing.JTextField();
        lblTiempoEstimado = new javax.swing.JLabel();
        txtTiempoEstimado = new javax.swing.JTextField();
        lblRetroalimentacion = new javax.swing.JLabel();
        txtRetroalimentacion = new javax.swing.JTextField();
        lblContRetro = new javax.swing.JLabel();
        lblImagen = new javax.swing.JLabel();
        txtImagen = new javax.swing.JTextField();
        lblEstatus = new javax.swing.JLabel();
        cmbEstatus = new javax.swing.JComboBox<>();
        btnGuardar = new javax.swing.JButton();
        btnActualizar = new javax.swing.JButton();
        btnEliminar = new javax.swing.JButton();
        btnLimpiar = new javax.swing.JButton();
        lblBuscar = new javax.swing.JLabel();
        txtBuscar = new javax.swing.JTextField();
        lblTotal = new javax.swing.JLabel();
        jScrollPane1 = new javax.swing.JScrollPane();
        tblPreguntas = new javax.swing.JTable();
        lblEstado = new javax.swing.JLabel();

        setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);

        lblTitulo.setFont(new java.awt.Font("Segoe UI", 1, 22)); // NOI18N
        lblTitulo.setText("Administración de preguntas");

        lblInstrucciones.setText("Llena los campos y presiona Guardar. Haz clic en una fila de la tabla para editarla o eliminarla.");

        lblExamen.setText("Examen");

        lblTema.setText("Tema");

        lblPregunta.setText("Pregunta");

        lblContPregunta.setText("0");

        lblNivelDificultad.setText("Nivel de dificultad");

        cmbNivelDificultad.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "básico", "intermedio", "avanzado" }));

        lblValorPuntos.setText("Valor en puntos");

        lblTiempoEstimado.setText("Tiempo estimado (min)");

        lblRetroalimentacion.setText("Retroalimentación (opcional)");

        lblContRetro.setText("0");

        lblImagen.setText("Imagen (opcional)");

        lblEstatus.setText("Estatus");

        cmbEstatus.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "activa", "inactiva" }));

        btnGuardar.setText("Guardar");

        btnActualizar.setText("Actualizar");

        btnEliminar.setText("Eliminar");

        btnLimpiar.setText("Limpiar");

        lblBuscar.setText("Buscar:");

        lblTotal.setText("0 registros");

        tblPreguntas.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {

            },
            new String [] {

            }
        ));
        jScrollPane1.setViewportView(tblPreguntas);

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
                            .addComponent(lblExamen)
                            .addComponent(lblTema)
                            .addComponent(lblPregunta)
                            .addComponent(lblNivelDificultad)
                            .addComponent(lblValorPuntos)
                            .addComponent(lblTiempoEstimado)
                            .addComponent(lblRetroalimentacion)
                            .addComponent(lblImagen)
                            .addComponent(lblEstatus))
                        .addGap(30, 30, 30)
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(cmbExamen, javax.swing.GroupLayout.PREFERRED_SIZE, 450, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(cmbTema, javax.swing.GroupLayout.PREFERRED_SIZE, 450, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addGroup(layout.createSequentialGroup()
                                .addComponent(txtPregunta, javax.swing.GroupLayout.PREFERRED_SIZE, 450, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addGap(10, 10, 10)
                                .addComponent(lblContPregunta))
                            .addComponent(cmbNivelDificultad, javax.swing.GroupLayout.PREFERRED_SIZE, 180, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(txtValorPuntos, javax.swing.GroupLayout.PREFERRED_SIZE, 100, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(txtTiempoEstimado, javax.swing.GroupLayout.PREFERRED_SIZE, 100, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addGroup(layout.createSequentialGroup()
                                .addComponent(txtRetroalimentacion, javax.swing.GroupLayout.PREFERRED_SIZE, 450, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addGap(10, 10, 10)
                                .addComponent(lblContRetro))
                            .addComponent(txtImagen, javax.swing.GroupLayout.PREFERRED_SIZE, 300, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(cmbEstatus, javax.swing.GroupLayout.PREFERRED_SIZE, 180, javax.swing.GroupLayout.PREFERRED_SIZE)))
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
                    .addComponent(lblExamen)
                    .addComponent(cmbExamen, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(10, 10, 10)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(lblTema)
                    .addComponent(cmbTema, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(10, 10, 10)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(lblPregunta)
                    .addComponent(txtPregunta, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(lblContPregunta))
                .addGap(10, 10, 10)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(lblNivelDificultad)
                    .addComponent(cmbNivelDificultad, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(10, 10, 10)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(lblValorPuntos)
                    .addComponent(txtValorPuntos, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(10, 10, 10)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(lblTiempoEstimado)
                    .addComponent(txtTiempoEstimado, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(10, 10, 10)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(lblRetroalimentacion)
                    .addComponent(txtRetroalimentacion, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(lblContRetro))
                .addGap(10, 10, 10)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(lblImagen)
                    .addComponent(txtImagen, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(10, 10, 10)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(lblEstatus)
                    .addComponent(cmbEstatus, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
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
                .addComponent(jScrollPane1, javax.swing.GroupLayout.PREFERRED_SIZE, 230, javax.swing.GroupLayout.PREFERRED_SIZE)
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
            java.util.logging.Logger.getLogger(FrmPregunta.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (InstantiationException ex) {
            java.util.logging.Logger.getLogger(FrmPregunta.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (IllegalAccessException ex) {
            java.util.logging.Logger.getLogger(FrmPregunta.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (javax.swing.UnsupportedLookAndFeelException ex) {
            java.util.logging.Logger.getLogger(FrmPregunta.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        }
        //</editor-fold>

        /* Create and display the form */
        java.awt.EventQueue.invokeLater(new Runnable() {
            public void run() {
                new FrmPregunta().setVisible(true);
            }
        });
    }

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton btnActualizar;
    private javax.swing.JButton btnEliminar;
    private javax.swing.JButton btnGuardar;
    private javax.swing.JButton btnLimpiar;
    private javax.swing.JComboBox<String> cmbEstatus;
    private javax.swing.JComboBox<modelo.Opcion> cmbExamen;
    private javax.swing.JComboBox<String> cmbNivelDificultad;
    private javax.swing.JComboBox<modelo.Opcion> cmbTema;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JLabel lblBuscar;
    private javax.swing.JLabel lblContPregunta;
    private javax.swing.JLabel lblContRetro;
    private javax.swing.JLabel lblEstado;
    private javax.swing.JLabel lblEstatus;
    private javax.swing.JLabel lblExamen;
    private javax.swing.JLabel lblImagen;
    private javax.swing.JLabel lblInstrucciones;
    private javax.swing.JLabel lblNivelDificultad;
    private javax.swing.JLabel lblPregunta;
    private javax.swing.JLabel lblRetroalimentacion;
    private javax.swing.JLabel lblTema;
    private javax.swing.JLabel lblTiempoEstimado;
    private javax.swing.JLabel lblTitulo;
    private javax.swing.JLabel lblTotal;
    private javax.swing.JLabel lblValorPuntos;
    private javax.swing.JTable tblPreguntas;
    private javax.swing.JTextField txtBuscar;
    private javax.swing.JTextField txtImagen;
    private javax.swing.JTextField txtPregunta;
    private javax.swing.JTextField txtRetroalimentacion;
    private javax.swing.JTextField txtTiempoEstimado;
    private javax.swing.JTextField txtValorPuntos;
    // End of variables declaration//GEN-END:variables
}
