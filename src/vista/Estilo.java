/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package vista;

import java.awt.Color;
import java.awt.Component;
import java.awt.Cursor;
import java.awt.Font;
import java.awt.event.KeyEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.function.BiFunction;
import java.util.regex.Pattern;

import javax.swing.AbstractAction;
import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JRootPane;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.KeyStroke;
import javax.swing.RowFilter;
import javax.swing.SwingConstants;
import javax.swing.Timer;
import javax.swing.border.Border;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.JTableHeader;
import javax.swing.table.TableModel;
import javax.swing.table.TableRowSorter;

/**
 * Utilidades de diseño e interaccion que comparten los formularios:
 * colores, botones con efecto al pasar el mouse, tabla con filas
 * resaltadas y busqueda, validacion en vivo y barra de estado.
 *
 * @author Alberto
 */
public final class Estilo {

    // ===== Paleta =====
    public static final Color AZUL_OSCURO = new Color(28, 43, 65);
    public static final Color FONDO = new Color(244, 242, 236);
    public static final Color FILA_PAR = new Color(250, 249, 245);
    public static final Color FILA_HOVER = new Color(255, 236, 214);
    public static final Color FILA_SELECCION = new Color(205, 224, 245);
    public static final Color NARANJA = new Color(232, 131, 58);

    public static final Color VERDE = new Color(46, 125, 50);
    public static final Color AZUL = new Color(25, 103, 170);
    public static final Color ROJO = new Color(183, 28, 28);
    public static final Color GRIS = new Color(97, 106, 120);

    public static final Color TEXTO_SUAVE = new Color(70, 80, 94);

    private static final Border BORDE_ERROR
            = BorderFactory.createLineBorder(ROJO, 2);

    /**
     * Excepcion de validacion que recuerda que campo esta mal,
     * para pintarlo de rojo y poner el cursor ahi.
     */
    public static class Invalido extends IllegalArgumentException {

        private final JComponent campo;

        public Invalido(String mensaje, JComponent campo) {
            super(mensaje);
            this.campo = campo;
        }

        public JComponent getCampo() {
            return campo;
        }
    }

    private Estilo() {
    }

    // ===== Ventana =====
    public static void ventana(javax.swing.JFrame frame, JLabel titulo, JLabel instrucciones) {

        frame.getContentPane().setBackground(FONDO);
        frame.getRootPane().setBorder(
                BorderFactory.createMatteBorder(6, 0, 0, 0, NARANJA));

        titulo.setForeground(AZUL_OSCURO);
        instrucciones.setForeground(TEXTO_SUAVE);
    }

    // ===== Botones con color y efecto hover =====
    public static void boton(JButton boton, Color color, String ayuda) {

        boton.setBackground(color);
        boton.setForeground(Color.WHITE);
        boton.setFont(boton.getFont().deriveFont(Font.BOLD));
        boton.setFocusPainted(false);
        boton.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        boton.setToolTipText(ayuda);

        // Nimbus pinta su propio fondo: se lo pedimos plano para ver el color
        boton.putClientProperty("Nimbus.Overrides.InheritDefaults", Boolean.FALSE);
        boton.setContentAreaFilled(false);
        boton.setOpaque(true);
        boton.setBorder(BorderFactory.createEmptyBorder(8, 18, 8, 18));

        Color hover = color.darker();

        boton.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseEntered(MouseEvent e) {
                if (boton.isEnabled()) {
                    boton.setBackground(hover);
                }
            }

            @Override
            public void mouseExited(MouseEvent e) {
                boton.setBackground(color);
            }
        });

        boton.addPropertyChangeListener("enabled", e -> {
            boton.setBackground(boton.isEnabled() ? color : new Color(190, 194, 200));
        });
    }

    // ===== Tabla: filas alternas, hover, columnas ordenables =====
    /**
     * @param resaltar devuelve un color de texto para una celda (columna, valor),
     *                 o null para el color normal. Sirve para pintar dificultad,
     *                 estatus o si es correcta.
     */
    public static TableRowSorter<TableModel> tabla(JTable tabla,
            BiFunction<Integer, Object, Color> resaltar) {

        tabla.setRowHeight(28);
        tabla.setShowVerticalLines(false);
        tabla.setGridColor(new Color(225, 222, 214));
        tabla.setSelectionBackground(FILA_SELECCION);
        tabla.setSelectionForeground(AZUL_OSCURO);
        tabla.setFillsViewportHeight(true);
        tabla.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));

        JTableHeader encabezado = tabla.getTableHeader();
        encabezado.setFont(encabezado.getFont().deriveFont(Font.BOLD));
        encabezado.setReorderingAllowed(false);
        encabezado.setToolTipText("Clic en una columna para ordenar");

        TableRowSorter<TableModel> sorter = new TableRowSorter<>(tabla.getModel());
        tabla.setRowSorter(sorter);

        // Fila bajo el mouse
        final int[] filaHover = {-1};

        tabla.addMouseMotionListener(new MouseAdapter() {
            @Override
            public void mouseMoved(MouseEvent e) {
                int fila = tabla.rowAtPoint(e.getPoint());
                if (fila != filaHover[0]) {
                    filaHover[0] = fila;
                    tabla.repaint();
                }
            }
        });

        tabla.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseExited(MouseEvent e) {
                filaHover[0] = -1;
                tabla.repaint();
            }
        });

        DefaultTableCellRenderer renderer = new DefaultTableCellRenderer() {
            @Override
            public Component getTableCellRendererComponent(JTable t, Object valor,
                    boolean seleccionada, boolean foco, int fila, int columna) {

                super.getTableCellRendererComponent(t, valor, seleccionada, false, fila, columna);

                setBorder(BorderFactory.createEmptyBorder(0, 8, 0, 8));
                setToolTipText(valor == null ? null : valor.toString());

                if (!seleccionada) {
                    if (fila == filaHover[0]) {
                        setBackground(FILA_HOVER);
                    } else {
                        setBackground(fila % 2 == 0 ? Color.WHITE : FILA_PAR);
                    }
                    setForeground(AZUL_OSCURO);
                }

                setFont(getFont().deriveFont(Font.PLAIN));

                Color especial = resaltar == null ? null
                        : resaltar.apply(t.convertColumnIndexToModel(columna), valor);

                if (especial != null) {
                    setForeground(especial);
                    setFont(getFont().deriveFont(Font.BOLD));
                }

                setHorizontalAlignment(valor instanceof Number
                        ? SwingConstants.RIGHT : SwingConstants.LEFT);

                return this;
            }
        };

        tabla.setDefaultRenderer(Object.class, renderer);
        tabla.setDefaultRenderer(Number.class, renderer);
        tabla.setDefaultRenderer(Integer.class, renderer);
        tabla.setDefaultRenderer(Double.class, renderer);

        return sorter;
    }

    // ===== Busqueda en vivo =====
    public static void busqueda(JTextField campo, TableRowSorter<TableModel> sorter,
            Runnable alCambiar) {

        campo.setToolTipText("Escribe para filtrar la tabla (Ctrl+F)");

        campo.getDocument().addDocumentListener(new DocumentListener() {
            private void filtrar() {
                String texto = campo.getText().trim();

                if (texto.isEmpty()) {
                    sorter.setRowFilter(null);
                } else {
                    sorter.setRowFilter(RowFilter.regexFilter(
                            "(?iu)" + Pattern.quote(texto)));
                }

                alCambiar.run();
            }

            @Override
            public void insertUpdate(DocumentEvent e) {
                filtrar();
            }

            @Override
            public void removeUpdate(DocumentEvent e) {
                filtrar();
            }

            @Override
            public void changedUpdate(DocumentEvent e) {
                filtrar();
            }
        });
    }

    // ===== Validacion en vivo =====
    public static void marcarError(JComponent campo) {

        if (campo.getClientProperty("bordeOriginal") == null) {
            campo.putClientProperty("bordeOriginal",
                    campo.getBorder() == null ? BorderFactory.createEmptyBorder() : campo.getBorder());
        }

        campo.setBorder(BORDE_ERROR);
    }

    public static void quitarError(JComponent campo) {

        Object original = campo.getClientProperty("bordeOriginal");

        if (original instanceof Border) {
            campo.setBorder((Border) original);
        }
    }

    /** Pinta el campo de rojo mientras lo escrito no sea un numero valido. */
    public static void soloNumeros(JTextField campo, boolean decimal, double maximo) {

        campo.setHorizontalAlignment(SwingConstants.RIGHT);

        campo.getDocument().addDocumentListener(new DocumentListener() {
            private void revisar() {
                String t = campo.getText().trim();
                boolean ok;

                try {
                    double v = decimal ? Double.parseDouble(t) : Integer.parseInt(t);
                    ok = v >= 0 && v <= maximo;
                } catch (NumberFormatException e) {
                    ok = t.isEmpty();
                }

                if (ok) {
                    quitarError(campo);
                } else {
                    marcarError(campo);
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

    /** Muestra "12/250" junto al campo y se pone rojo si se pasa. */
    public static void contador(JTextField campo, JLabel etiqueta, int maximo) {

        etiqueta.setForeground(GRIS);

        Runnable actualizar = () -> {
            int n = campo.getText().length();
            etiqueta.setText(n + "/" + maximo);

            if (n > maximo) {
                etiqueta.setForeground(ROJO);
                marcarError(campo);
            } else {
                etiqueta.setForeground(n > maximo * 0.9 ? NARANJA.darker() : GRIS);
                quitarError(campo);
            }
        };

        campo.getDocument().addDocumentListener(new DocumentListener() {
            @Override
            public void insertUpdate(DocumentEvent e) {
                actualizar.run();
            }

            @Override
            public void removeUpdate(DocumentEvent e) {
                actualizar.run();
            }

            @Override
            public void changedUpdate(DocumentEvent e) {
                actualizar.run();
            }
        });

        actualizar.run();
    }

    // ===== Barra de estado =====
    public enum Tipo { INFO, EXITO, ERROR }

    public static void estado(JLabel barra, String mensaje, Tipo tipo, String normal) {

        Color color = switch (tipo) {
            case EXITO -> VERDE;
            case ERROR -> ROJO;
            default -> TEXTO_SUAVE;
        };

        String prefijo = switch (tipo) {
            case EXITO -> "Listo: ";
            case ERROR -> "Revisa: ";
            default -> "";
        };

        barra.setForeground(color);
        barra.setText(prefijo + mensaje);

        // Despues de unos segundos vuelve al texto normal
        Object anterior = barra.getClientProperty("timer");

        if (anterior instanceof Timer) {
            ((Timer) anterior).stop();
        }

        if (tipo != Tipo.INFO) {
            Timer t = new Timer(5000, e -> {
                barra.setForeground(TEXTO_SUAVE);
                barra.setText(normal);
            });
            t.setRepeats(false);
            t.start();
            barra.putClientProperty("timer", t);
        }
    }

    // ===== Atajos de teclado =====
    public static void atajo(JRootPane raiz, String nombre, int tecla, int modificador,
            Runnable accion) {

        raiz.getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW)
                .put(KeyStroke.getKeyStroke(tecla, modificador), nombre);

        raiz.getActionMap().put(nombre, new AbstractAction() {
            @Override
            public void actionPerformed(java.awt.event.ActionEvent e) {
                accion.run();
            }
        });
    }

    public static final int CTRL = KeyEvent.CTRL_DOWN_MASK;
}
