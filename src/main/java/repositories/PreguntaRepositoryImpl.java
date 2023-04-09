package repositories;

import java.util.List;
import java.util.concurrent.TimeUnit;

import services.Datos;

public class PreguntaRepositoryImpl implements PreguntaRepository {

	@Override
	public List<String> obtenerPreguntasPorExamenId(Long id) {
		System.out.println("PreguntaRepositoryImpl.obtenerPreguntasPorExamenId");
		try {
			TimeUnit.SECONDS.sleep(2);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		return Datos.PREGUNTAS_HISTORIA;
	}

	@Override
	public void guardarPreguntas(List<String> preguntas) {
		System.out.println("PreguntaRepositoryImpl.guardarPreguntas");
	}

}
