package services;

import java.util.Arrays;
import java.util.List;

import models.Examen;

public class Datos {

	public final static List<Examen> EXAMENES = Arrays.asList( 
			new Examen(1L, "Historia"),
			new Examen(2L, "Matemáticas"),
			new Examen(3L, "Química") 
			);
	
	public final static List<Examen> EXAMENES_ID_NULL = Arrays.asList( 
			new Examen(null, "Historia"),
			new Examen(null, "Matemáticas"),
			new Examen(null, "Química") 
			);
	
	public final static List<Examen> EXAMENES_ID_NEGATIVOS = Arrays.asList( 
			new Examen(-1L, "Historia"),
			new Examen(-2L, "Matemáticas"),
			new Examen(null, "Química") 
			);

	public final static List<String> PREGUNTAS_HISTORIA = Arrays.asList(
			"Pregunta 1_Historia",
			"Pregunta 2_Historia",
			"Pregunta 3_Historia",
			"Pregunta 4_Historia",
			"Pregunta 5_Historia"
			);
	
	public final static Examen EXAMEN = new Examen(null,"Diseño");
	
}
