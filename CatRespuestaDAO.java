import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

/**
 * Tabla cat_respuesta: altas, bajas, modificaciones y consultas (todo en un solo archivo).
 * Columnas: cve_respuesta (PK), cat_pregunta_cve_pregunta (FK), respuesta, imagen, es_correcta
 */
public class CatRespuestaDAO {

    // ====== Datos de conexión: cámbialos por los de tu MySQL ======
    private static final String URL  = "jdbc:mysql://localhost:3306/TU_BASE_DE_DATOS?useSSL=false&serverTimezone=UTC";
    private static final String USER = "root";
    private static final String PASS = "";

    // ====== Un registro de la tabla ======
    public static class CatRespuesta {
        public int cveRespuesta;
        public int cvePregunta;     // cat_pregunta_cve_pregunta
        public String respuesta;    // VARCHAR(250)
        public String imagen;       // VARCHAR(100), puede ser null
        public boolean esCorrecta;  // TINYINT

        public CatRespuesta() { }

        public CatRespuesta(int cveRespuesta, int cvePregunta, String respuesta,
                            String imagen, boolean esCorrecta) {
            this.cveRespuesta = cveRespuesta;
            this.cvePregunta = cvePregunta;
            this.respuesta = respuesta;
            this.imagen = imagen;
            this.esCorrecta = esCorrecta;
        }

        @Override
        public String toString() {
            return "CatRespuesta{cve=" + cveRespuesta + ", pregunta=" + cvePregunta
                    + ", respuesta='" + respuesta + "', imagen=" + imagen
                    + ", correcta=" + esCorrecta + "}";
        }
    }

    private static Connection conectar() throws SQLException {
        return DriverManager.getConnection(URL, USER, PASS);
    }

    // ALTA: devuelve la clave generada (o -1 si falló)
    public int insertar(CatRespuesta r) throws SQLException {
        String sql = "INSERT INTO cat_respuesta "
                + "(cat_pregunta_cve_pregunta, respuesta, imagen, es_correcta) VALUES (?, ?, ?, ?)";
        try (Connection con = conectar();
             PreparedStatement ps = con.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            ps.setInt(1, r.cvePregunta);
            ps.setString(2, r.respuesta);
            ps.setString(3, r.imagen);
            ps.setBoolean(4, r.esCorrecta);
            if (ps.executeUpdate() > 0) {
                try (ResultSet rs = ps.getGeneratedKeys()) {
                    if (rs.next()) {
                        r.cveRespuesta = rs.getInt(1);
                        return r.cveRespuesta;
                    }
                }
            }
        }
        return -1;
    }

    // BAJA
    public boolean eliminar(int cveRespuesta) throws SQLException {
        String sql = "DELETE FROM cat_respuesta WHERE cve_respuesta = ?";
        try (Connection con = conectar(); PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setInt(1, cveRespuesta);
            return ps.executeUpdate() > 0;
        }
    }

    // MODIFICACIÓN
    public boolean actualizar(CatRespuesta r) throws SQLException {
        String sql = "UPDATE cat_respuesta SET cat_pregunta_cve_pregunta = ?, respuesta = ?, "
                + "imagen = ?, es_correcta = ? WHERE cve_respuesta = ?";
        try (Connection con = conectar(); PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setInt(1, r.cvePregunta);
            ps.setString(2, r.respuesta);
            ps.setString(3, r.imagen);
            ps.setBoolean(4, r.esCorrecta);
            ps.setInt(5, r.cveRespuesta);
            return ps.executeUpdate() > 0;
        }
    }

    // CONSULTA por clave (null si no existe)
    public CatRespuesta buscarPorId(int cveRespuesta) throws SQLException {
        String sql = "SELECT * FROM cat_respuesta WHERE cve_respuesta = ?";
        try (Connection con = conectar(); PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setInt(1, cveRespuesta);
            try (ResultSet rs = ps.executeQuery()) {
                return rs.next() ? mapear(rs) : null;
            }
        }
    }

    // CONSULTA: respuestas de una pregunta
    public List<CatRespuesta> listarPorPregunta(int cvePregunta) throws SQLException {
        String sql = "SELECT * FROM cat_respuesta WHERE cat_pregunta_cve_pregunta = ? ORDER BY cve_respuesta";
        List<CatRespuesta> lista = new ArrayList<>();
        try (Connection con = conectar(); PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setInt(1, cvePregunta);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    lista.add(mapear(rs));
                }
            }
        }
        return lista;
    }

    // CONSULTA: todas
    public List<CatRespuesta> listarTodas() throws SQLException {
        String sql = "SELECT * FROM cat_respuesta ORDER BY cve_respuesta";
        List<CatRespuesta> lista = new ArrayList<>();
        try (Connection con = conectar();
             PreparedStatement ps = con.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                lista.add(mapear(rs));
            }
        }
        return lista;
    }

    private CatRespuesta mapear(ResultSet rs) throws SQLException {
        return new CatRespuesta(
                rs.getInt("cve_respuesta"),
                rs.getInt("cat_pregunta_cve_pregunta"),
                rs.getString("respuesta"),
                rs.getString("imagen"),
                rs.getBoolean("es_correcta"));
    }

    // ====== Prueba rápida: clic derecho en este archivo → Run File (Shift+F6) ======
    public static void main(String[] args) {
        try {
            CatRespuestaDAO dao = new CatRespuestaDAO();

            // La pregunta 1 debe existir en cat_pregunta (cámbiala si tu clave es otra)
            CatRespuesta r = new CatRespuesta(0, 1, "Respuesta de prueba", null, true);
            int cve = dao.insertar(r);
            System.out.println("Alta -> cve_respuesta = " + cve);

            System.out.println("Consulta -> " + dao.buscarPorId(cve));

            r.respuesta = "Respuesta modificada";
            r.esCorrecta = false;
            System.out.println("Modificación -> " + dao.actualizar(r));

            dao.listarPorPregunta(1).forEach(System.out::println);

            System.out.println("Baja -> " + dao.eliminar(cve));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}