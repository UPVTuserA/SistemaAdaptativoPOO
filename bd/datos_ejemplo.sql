-- Datos de ejemplo OPCIONALES para probar los formularios (no borra nada).
USE sistema_adaptativo;


INSERT IGNORE INTO cat_plan_estudios (cve, nombre, estatus, fecha_activacion) VALUES
  (1, 'Plan 2024', 'activa', '2024-09-01');

INSERT IGNORE INTO cat_carrera (cve_carrera, cat_plan_estudios_cve, estatus, nombre, fecha_activacion) VALUES
  (1, 1, 'activa', 'Ingenieria en TICs', '2024-09-01');

INSERT IGNORE INTO cat_estudiantes (matricula, cat_carrera_cve_carrera, nombre, paterno, materno) VALUES
  ('2024001', 1, 'Alberto', 'Jimenez', 'Hernandez'),
  ('2024002', 1, 'Maria', 'Lopez', 'Garcia'),
  ('2024003', 1, 'Carlos', 'Ramirez', 'Diaz');

INSERT IGNORE INTO cat_materias (clave_materia, cat_carrera_cve_carrera, nombre, numero_unidades) VALUES
  ('POO', 1, 'Programacion Orientada a Objetos', 3);

INSERT IGNORE INTO cat_unidades_materia (cve_unidad, cat_materias_clave_materia, nombre_unidad, numero_unidad) VALUES
  (1, 'POO', 'Clases y objetos', 1),
  (2, 'POO', 'Herencia y polimorfismo', 2),
  (3, 'POO', 'Interfaces y excepciones', 3);

INSERT IGNORE INTO cat_temas_unidad (clave_tema, cat_unidades_materia_cve_unidad, nombre, estatus) VALUES
  (1, 1, 'Clases y objetos', 'activa'),
  (2, 1, 'Atributos y metodos', 'activa'),
  (3, 1, 'Constructores', 'activa'),
  (4, 1, 'Encapsulamiento', 'activa'),
  (5, 2, 'Herencia', 'activa'),
  (6, 2, 'Polimorfismo', 'activa'),
  (7, 2, 'Clases abstractas', 'activa'),
  (8, 3, 'Interfaces', 'activa'),
  (9, 3, 'Manejo de excepciones', 'activa');

INSERT IGNORE INTO cat_profesor (cve_profesor, nombre, paterno, profesion) VALUES
  (1, 'Profesor', 'POO', 'Ingeniero en Sistemas');

INSERT IGNORE INTO cat_examen (cve_examen, cat_profesor_cve_profesor, cat_materias_clave_materia, cat_unidades_materia_cve_unidad, tipo_examen, periodo, anio) VALUES
  (1, 1, 'POO', 1, 'diagnostico', 'sd', 2026),
  (2, 1, 'POO', 1, 'unidad', 'sd', 2026),
  (3, 1, 'POO', 2, 'unidad', 'sd', 2026),
  (4, 1, 'POO', 3, 'unidad', 'sd', 2026);

INSERT IGNORE INTO examen_alumno (cve, cat_estudiantes_matricula, cat_examen_cve_examen, cat_profesor_cve_profesor, cat_materias_clave_materia, numero_unidad, fecha_inicio, numero_intento, estatus) VALUES
  (1, '2024001', 2, 1, 'POO', 1, '2026-11-20 10:00:00', 1, 'en proceso'),
  (2, '2024002', 2, 1, 'POO', 1, '2026-11-20 10:00:00', 1, 'en proceso'),
  (3, '2024003', 3, 1, 'POO', 2, '2026-11-20 12:00:00', 1, 'en proceso');

INSERT IGNORE INTO cat_pregunta (cve_pregunta, cat_examen_cve_examen, cat_temas_unidad_clave_tema, pregunta, nivel_dificultad, valor_puntos, retroalimentacion, tiempo_estimado, estatus) VALUES
  (1, 2, 1, '¿Qué es un objeto en POO?', 'básico', 10.00, 'Un objeto es una instancia de una clase.', 2, 'activa'),
  (2, 2, 4, '¿Para qué sirve el encapsulamiento?', 'intermedio', 10.00, 'Protege los datos con atributos privados y getters/setters.', 3, 'activa'),
  (3, 3, 5, '¿Qué palabra se usa en Java para heredar de una clase?', 'básico', 10.00, 'Se usa extends.', 2, 'activa'),
  (4, 3, 6, 'Explica qué es el polimorfismo con un ejemplo.', 'avanzado', 20.00, 'Un mismo método se comporta distinto según el objeto.', 5, 'activa');
