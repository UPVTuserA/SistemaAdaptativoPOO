-- Crea la base sistema_adaptativo con TODAS las tablas del modelo (sistemaAdaptativo.mwb).
-- Es seguro: no borra nada; si una tabla ya existe la deja como esta.


CREATE DATABASE IF NOT EXISTS `sistema_adaptativo` CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci;
USE `sistema_adaptativo`;

CREATE TABLE IF NOT EXISTS `cat_plan_estudios` (
  `cve` INT NOT NULL AUTO_INCREMENT,
  `nombre` VARCHAR(45) NULL,
  `estatus` ENUM('activa', 'inactiva', 'liquidacion') NULL,
  `fecha_activacion` DATE NULL,
  `fecha_inactiva` DATE NULL,
  `fecha_liquidacion` DATE NULL,
  PRIMARY KEY (`cve`)
) ENGINE=InnoDB;

CREATE TABLE IF NOT EXISTS `cat_carrera` (
  `cve_carrera` INT NOT NULL AUTO_INCREMENT,
  `cat_plan_estudios_cve` INT NOT NULL,
  `estatus` ENUM('activa', 'liquidacion', 'inactiva') NULL,
  `nombre` VARCHAR(45) NULL,
  `fecha_activacion` DATE NULL,
  `fecha_inactiva` DATE NULL,
  `fecha_liquidacion` DATE NULL,
  PRIMARY KEY (`cve_carrera`),
  CONSTRAINT `fk_cat_carrera_cat_plan_estudios1` FOREIGN KEY (`cat_plan_estudios_cve`) REFERENCES `cat_plan_estudios` (`cve`)
) ENGINE=InnoDB;

CREATE TABLE IF NOT EXISTS `cat_estudiantes` (
  `matricula` VARCHAR(10) NOT NULL,
  `cat_carrera_cve_carrera` INT NOT NULL,
  `nombre` VARCHAR(45) NULL,
  `paterno` VARCHAR(45) NULL,
  `materno` VARCHAR(45) NULL,
  PRIMARY KEY (`matricula`),
  CONSTRAINT `fk_cat_estudiantes_cat_carrera` FOREIGN KEY (`cat_carrera_cve_carrera`) REFERENCES `cat_carrera` (`cve_carrera`)
) ENGINE=InnoDB;

CREATE TABLE IF NOT EXISTS `cat_materias` (
  `clave_materia` VARCHAR(45) NOT NULL,
  `cat_carrera_cve_carrera` INT NOT NULL,
  `nombre` VARCHAR(100) NOT NULL,
  `numero_unidades` INT NULL,
  PRIMARY KEY (`clave_materia`),
  CONSTRAINT `fk_cat_materias_cat_carrera1` FOREIGN KEY (`cat_carrera_cve_carrera`) REFERENCES `cat_carrera` (`cve_carrera`)
) ENGINE=InnoDB;

CREATE TABLE IF NOT EXISTS `cat_unidades_materia` (
  `cve_unidad` INT NOT NULL AUTO_INCREMENT,
  `cat_materias_clave_materia` VARCHAR(45) NOT NULL,
  `nombre_unidad` VARCHAR(200) NULL,
  `numero_unidad` INT NOT NULL,
  PRIMARY KEY (`cve_unidad`),
  CONSTRAINT `fk_cat_unidades_materia_cat_materias1` FOREIGN KEY (`cat_materias_clave_materia`) REFERENCES `cat_materias` (`clave_materia`)
) ENGINE=InnoDB;

CREATE TABLE IF NOT EXISTS `cat_temas_unidad` (
  `clave_tema` INT NOT NULL AUTO_INCREMENT,
  `cat_unidades_materia_cve_unidad` INT NOT NULL,
  `nombre` VARCHAR(200) NULL,
  `estatus` ENUM('activa', 'inactiva') NULL,
  `fecha_activa` DATE NULL,
  `fecha_inactiva` DATE NULL,
  PRIMARY KEY (`clave_tema`),
  CONSTRAINT `fk_cat_temas_unidad_cat_unidades_materia1` FOREIGN KEY (`cat_unidades_materia_cve_unidad`) REFERENCES `cat_unidades_materia` (`cve_unidad`)
) ENGINE=InnoDB;

CREATE TABLE IF NOT EXISTS `cat_profesor` (
  `cve_profesor` INT NOT NULL AUTO_INCREMENT,
  `nombre` VARCHAR(45) NULL,
  `paterno` VARCHAR(45) NULL,
  `materno` VARCHAR(45) NULL,
  `profesion` VARCHAR(45) NULL,
  `maestria` VARCHAR(45) NULL,
  `doctorado` VARCHAR(45) NULL,
  `post_doctorado` VARCHAR(45) NULL,
  `especialidad` VARCHAR(45) NULL,
  `carrera_de_adscripcion` VARCHAR(45) NULL,
  PRIMARY KEY (`cve_profesor`)
) ENGINE=InnoDB;

CREATE TABLE IF NOT EXISTS `cat_examen` (
  `cve_examen` INT NOT NULL AUTO_INCREMENT,
  `cat_profesor_cve_profesor` INT NOT NULL,
  `cat_materias_clave_materia` VARCHAR(45) NOT NULL,
  `cat_unidades_materia_cve_unidad` INT NOT NULL,
  `tipo_examen` ENUM('diagnostico', 'unidad') NOT NULL,
  `periodo` ENUM('ea', 'ma', 'sd') NOT NULL,
  `anio` INT NOT NULL,
  PRIMARY KEY (`cve_examen`),
  CONSTRAINT `fk_cat_examen_cat_profesor1` FOREIGN KEY (`cat_profesor_cve_profesor`) REFERENCES `cat_profesor` (`cve_profesor`),
  CONSTRAINT `fk_cat_examen_cat_materias1` FOREIGN KEY (`cat_materias_clave_materia`) REFERENCES `cat_materias` (`clave_materia`),
  CONSTRAINT `fk_cat_examen_cat_unidades_materia1` FOREIGN KEY (`cat_unidades_materia_cve_unidad`) REFERENCES `cat_unidades_materia` (`cve_unidad`)
) ENGINE=InnoDB;

CREATE TABLE IF NOT EXISTS `examen_alumno` (
  `cve` INT NOT NULL AUTO_INCREMENT,
  `cat_estudiantes_matricula` VARCHAR(10) NOT NULL,
  `cat_examen_cve_examen` INT NOT NULL,
  `cat_profesor_cve_profesor` INT NOT NULL,
  `cat_materias_clave_materia` VARCHAR(45) NOT NULL,
  `numero_unidad` INT NOT NULL,
  `fecha_inicio` DATETIME(1) NULL,
  `fecha_termino` DATETIME(1) NULL,
  `calificacion` DECIMAL(5,2) NULL,
  `numero_intento` INT NULL,
  `estatus` ENUM('en proceso', 'finalizado', 'abandonado') NULL,
  `tiempo_empleado` INT NULL,
  PRIMARY KEY (`cve`),
  CONSTRAINT `fk_examen_alumno_cat_estudiantes1` FOREIGN KEY (`cat_estudiantes_matricula`) REFERENCES `cat_estudiantes` (`matricula`),
  CONSTRAINT `fk_examen_alumno_cat_examen1` FOREIGN KEY (`cat_examen_cve_examen`) REFERENCES `cat_examen` (`cve_examen`),
  CONSTRAINT `fk_examen_alumno_cat_profesor1` FOREIGN KEY (`cat_profesor_cve_profesor`) REFERENCES `cat_profesor` (`cve_profesor`),
  CONSTRAINT `fk_examen_alumno_cat_materias1` FOREIGN KEY (`cat_materias_clave_materia`) REFERENCES `cat_materias` (`clave_materia`)
) ENGINE=InnoDB;

CREATE TABLE IF NOT EXISTS `cat_pregunta` (
  `cve_pregunta` INT NOT NULL AUTO_INCREMENT,
  `cat_examen_cve_examen` INT NOT NULL,
  `cat_temas_unidad_clave_tema` INT NOT NULL,
  `pregunta` VARCHAR(250) NULL,
  `imagen` VARCHAR(100) NULL,
  `nivel_dificultad` ENUM('básico', 'intermedio', 'avanzado') NULL,
  `valor_puntos` DECIMAL(5,2) NULL,
  `retroalimentacion` VARCHAR(200) NULL,
  `tiempo_estimado` INT NULL,
  `estatus` ENUM('activa', 'inactiva') NULL DEFAULT 'activa',
  PRIMARY KEY (`cve_pregunta`),
  CONSTRAINT `fk_cat_pregunta_cat_examen1` FOREIGN KEY (`cat_examen_cve_examen`) REFERENCES `cat_examen` (`cve_examen`),
  CONSTRAINT `fk_cat_pregunta_cat_temas_unidad1` FOREIGN KEY (`cat_temas_unidad_clave_tema`) REFERENCES `cat_temas_unidad` (`clave_tema`)
) ENGINE=InnoDB;

CREATE TABLE IF NOT EXISTS `cat_respuesta` (
  `cve_respuesta` INT NOT NULL AUTO_INCREMENT,
  `cat_pregunta_cve_pregunta` INT NOT NULL,
  `respuesta` VARCHAR(250) NULL,
  `imagen` VARCHAR(100) NULL,
  `es_correcta` TINYINT NOT NULL,
  PRIMARY KEY (`cve_respuesta`),
  CONSTRAINT `fk_cat_respuesta_cat_pregunta1` FOREIGN KEY (`cat_pregunta_cve_pregunta`) REFERENCES `cat_pregunta` (`cve_pregunta`)
) ENGINE=InnoDB;

CREATE TABLE IF NOT EXISTS `respuesta_alumno` (
  `cve_respuesta_alumno` INT NOT NULL AUTO_INCREMENT,
  `examen_alumno_cve` INT NOT NULL,
  `cat_pregunta_cve_pregunta` INT NOT NULL,
  `respuesta_texto` VARCHAR(250) NULL,
  `es_correcta` TINYINT NULL,
  `puntos_obtenidos` DECIMAL(5,2) NULL,
  `fecha_respuesta` DATETIME(1) NULL,
  PRIMARY KEY (`cve_respuesta_alumno`),
  CONSTRAINT `fk_respuesta_alumno_examen_alumno1` FOREIGN KEY (`examen_alumno_cve`) REFERENCES `examen_alumno` (`cve`),
  CONSTRAINT `fk_respuesta_alumno_cat_pregunta1` FOREIGN KEY (`cat_pregunta_cve_pregunta`) REFERENCES `cat_pregunta` (`cve_pregunta`)
) ENGINE=InnoDB;

CREATE TABLE IF NOT EXISTS `resultado_examen` (
  `cve_resultado` INT NOT NULL AUTO_INCREMENT,
  `examen_alumno_cve` INT NOT NULL,
  `total_preguntas` INT NULL,
  `preguntas_correctas` INT NULL,
  `preguntas_incorrectas` INT NULL,
  `preguntas_sin_responder` INT NULL,
  `porcentaje_aciertos` DECIMAL(5,2) NULL,
  `calificacion_final` DECIMAL(5,2) NULL,
  `fecha_evaluacion` DATETIME(1) NULL,
  PRIMARY KEY (`cve_resultado`),
  CONSTRAINT `fk_resultado_examen_examen_alumno1` FOREIGN KEY (`examen_alumno_cve`) REFERENCES `examen_alumno` (`cve`)
) ENGINE=InnoDB;

CREATE TABLE IF NOT EXISTS `detalle_respuesta_alumno` (
  `cve_detalle` INT NOT NULL AUTO_INCREMENT,
  `cat_respuesta_cve_respuesta` INT NOT NULL,
  `respuesta_alumno_cve_respuesta_alumno` INT NOT NULL,
  PRIMARY KEY (`cve_detalle`),
  CONSTRAINT `fk_detalle_respuesta_alumno_cat_respuesta1` FOREIGN KEY (`cat_respuesta_cve_respuesta`) REFERENCES `cat_respuesta` (`cve_respuesta`),
  CONSTRAINT `fk_detalle_respuesta_alumno_respuesta_alumno1` FOREIGN KEY (`respuesta_alumno_cve_respuesta_alumno`) REFERENCES `respuesta_alumno` (`cve_respuesta_alumno`)
) ENGINE=InnoDB;

CREATE TABLE IF NOT EXISTS `historial_desempeño` (
  `cve_desempeño` INT NOT NULL AUTO_INCREMENT,
  `cat_estudiantes_matricula` VARCHAR(10) NOT NULL,
  `cat_materias_clave_materia` VARCHAR(45) NOT NULL,
  `cat_temas_unidad_clave_tema` INT NOT NULL,
  `numero_unidad` INT NULL,
  `total_intentos` INT NULL,
  `total_correctas` INT NULL,
  `total_incorrectas` INT NULL,
  `porcentaje_dominio` DECIMAL(5,2) NULL,
  `fecha_actualizacion` DATETIME(1) NULL,
  PRIMARY KEY (`cve_desempeño`),
  CONSTRAINT `fk_historial_desempeño_cat_estudiantes1` FOREIGN KEY (`cat_estudiantes_matricula`) REFERENCES `cat_estudiantes` (`matricula`),
  CONSTRAINT `fk_historial_desempeño_cat_materias1` FOREIGN KEY (`cat_materias_clave_materia`) REFERENCES `cat_materias` (`clave_materia`),
  CONSTRAINT `fk_historial_desempeño_cat_temas_unidad1` FOREIGN KEY (`cat_temas_unidad_clave_tema`) REFERENCES `cat_temas_unidad` (`clave_tema`)
) ENGINE=InnoDB;

CREATE TABLE IF NOT EXISTS `cat_reglas_adaptacion` (
  `cve_regla` INT NOT NULL AUTO_INCREMENT,
  `cat_temas_unidad_clave_tema` INT NOT NULL,
  `nivel_dominio_minimo` DECIMAL(5,2) NULL,
  `nivel_dificultad_actual` VARCHAR(45) NULL,
  `nivel_dificultad_siguiente` VARCHAR(45) NULL,
  `accion` VARCHAR(100) NULL,
  `estatus` TINYINT NULL,
  `nivel_dominio_maximo` DECIMAL(5,2) NULL,
  PRIMARY KEY (`cve_regla`),
  CONSTRAINT `fk_cat_reglas_adaptacion_cat_temas_unidad1` FOREIGN KEY (`cat_temas_unidad_clave_tema`) REFERENCES `cat_temas_unidad` (`clave_tema`)
) ENGINE=InnoDB;

