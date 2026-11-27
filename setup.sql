-- ============================================================
-- INSTALACIÓN DEL SISTEMA ADAPTATIVO POO  (usando BD "test")
-- Tablas: cat_carrera y cat_examen
-- Ejecutar con:  mysql -u admin < setup.sql
-- ============================================================

USE test;

-- ============================================================
-- TABLA: cat_carrera
-- ============================================================
DROP TABLE IF EXISTS cat_carrera;
CREATE TABLE cat_carrera (
  cve_carrera           INT           NOT NULL,
  cat_plan_estudios_cve INT           NOT NULL,
  estatus               VARCHAR(50)   NOT NULL,
  nombre                VARCHAR(100)  NOT NULL,
  fecha_activacion      DATE          NULL,
  fecha_inactiva        DATE          NULL,
  fecha_liquidacion     DATE          NULL,
  PRIMARY KEY (cve_carrera)
) ENGINE=InnoDB;

-- ============================================================
-- TABLA: cat_examen
-- ============================================================
DROP TABLE IF EXISTS cat_examen;
CREATE TABLE cat_examen (
  cve_examen                      INT          NOT NULL AUTO_INCREMENT,
  cat_profesor_cve_profesor       INT          NOT NULL,
  cat_materias_clave_materia      VARCHAR(45)  NOT NULL,
  cat_unidades_materia_cve_unidad INT          NOT NULL,
  tipo_examen                     ENUM('diagnostico','unidad') NOT NULL,
  periodo                         ENUM('ea','ma','sd')         NOT NULL,
  anio                            INT          NOT NULL,
  PRIMARY KEY (cve_examen)
) ENGINE=InnoDB;

-- ============================================================
-- Datos de prueba
-- ============================================================
INSERT INTO cat_carrera
  (cve_carrera, cat_plan_estudios_cve, estatus, nombre, fecha_activacion, fecha_inactiva, fecha_liquidacion)
VALUES
  (1, 101, 'Activo',   'Ingeniería en Sistemas',   '2023-01-15', NULL,        NULL),
  (2, 102, 'Activo',   'Ingeniería en Software',   '2023-02-10', NULL,        NULL),
  (3, 103, 'Inactivo', 'Ingeniería en Mecatrónica', '2022-08-01', '2026-06-30', NULL);

INSERT INTO cat_examen
  (cat_profesor_cve_profesor, cat_materias_clave_materia, cat_unidades_materia_cve_unidad, tipo_examen, periodo, anio)
VALUES
  (1, 'MAT-101', 1, 'diagnostico', 'ea', 2026),
  (1, 'MAT-101', 2, 'unidad',      'ma', 2026),
  (2, 'PRO-201', 1, 'unidad',      'sd', 2026);

SELECT 'INSTALACIÓN COMPLETADA' AS resultado;
SELECT COUNT(*) AS carreras FROM cat_carrera;
SELECT COUNT(*) AS examenes FROM cat_examen;
