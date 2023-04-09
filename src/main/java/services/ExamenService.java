package services;

import java.util.Optional;

import models.Examen;

public interface ExamenService {	//Se define el contrato , Logica de negocio
	Optional<Examen> buscarExamenPorNombre(String nombre);
	
	Examen buscarExamenPorNombreConPreguntas(String nombre);
	
	Examen guardarExamen(Examen examen);
}
