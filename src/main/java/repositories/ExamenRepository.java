package repositories;

import java.util.List;

import models.Examen;

public interface ExamenRepository {
	List<Examen> obtenerTodo();
	Examen guardarExamen(Examen examen);
}
