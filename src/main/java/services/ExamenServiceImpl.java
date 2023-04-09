package services;

import java.util.List;
import java.util.Optional;

import models.Examen;
import repositories.ExamenRepository;
import repositories.PreguntaRepository;

public class ExamenServiceImpl implements ExamenService{
	
	private ExamenRepository examenRepository;
	private PreguntaRepository preguntaRepository;

	public ExamenServiceImpl(ExamenRepository examenRepository, PreguntaRepository preguntaRepository) {
		this.examenRepository = examenRepository;
		this.preguntaRepository = preguntaRepository;
	}
	
	@Override
	public Optional<Examen> buscarExamenPorNombre(String nombre) {
		
		return examenRepository.obtenerTodo()
				.stream()
				.filter(
						exam -> exam.getNombre().equals(nombre)
				)
				.findFirst();
	}

	@Override
	public Examen buscarExamenPorNombreConPreguntas(String nombre) {
		Optional<Examen> examenOptional = buscarExamenPorNombre(nombre);
		
		Examen examen = null;
		
		if(examenOptional.isPresent()) {
			examen = examenOptional.get();
			List<String> preguntas = preguntaRepository.obtenerPreguntasPorExamenId(examen.getId());
			examen.setPreguntas(preguntas);
		}
		
		return examen;
	}

	@Override
	public Examen guardarExamen(Examen examen) {
		if(!examen.getPreguntas().isEmpty()) {	//Si no es vacio el examen se guardan las preguntas
			preguntaRepository.guardarPreguntas(examen.getPreguntas());
		}
		return examenRepository.guardarExamen(examen);
	}

}
