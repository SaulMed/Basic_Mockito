package repositories;

import java.util.List;

public interface PreguntaRepository {
	List<String> obtenerPreguntasPorExamenId(Long id);
	void guardarPreguntas(List<String> preguntas);
}
