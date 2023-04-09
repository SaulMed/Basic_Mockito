package services;

//import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
//import org.mockito.ArgumentCaptor;
//import org.mockito.ArgumentMatcher;
//import org.mockito.Captor;
import org.mockito.InjectMocks;
//import org.mockito.Mock;
//import org.mockito.MockitoAnnotations;
import org.mockito.Spy;
//import org.mockito.invocation.InvocationOnMock;
import org.mockito.junit.jupiter.MockitoExtension;
//import org.mockito.stubbing.Answer;

import models.Examen;
//import repositories.ExamenRepository;
import repositories.ExamenRepositoryImpl;
//import repositories.PreguntaRepository;
import repositories.PreguntaRepositoryImpl;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import java.util.Arrays;
//import java.util.Collections;
import java.util.List;
//import java.util.Optional;

//2da forma de permitir anotaciones en mockito
@ExtendWith(MockitoExtension.class)
public class ExamenServiceImplSpyTest {

	//Anotaciones para Spy
	@Spy
	ExamenRepositoryImpl repository;

	@Spy
	PreguntaRepositoryImpl preguntaRepository;

	@InjectMocks // Requiere forzosamente una Clase NO una interfaz
	ExamenServiceImpl service;

	@Test
	@DisplayName("Ejecucion de Spy")
	void testSpy() { 
		
		//Hibrido = Al momento de llamar al metodo "obtenerPreguntasPorExamenId" se hace una invocacion falsa 
		List<String> preguntas = Arrays.asList("Geometria");
		//when(preguntaRepository.obtenerPreguntasPorExamenId(anyLong())).thenReturn(preguntas);
		//Con Spy se recomienda trabajar con do y no con when
		doReturn(preguntas).when(preguntaRepository).obtenerPreguntasPorExamenId(anyLong());
		
		Examen examen = service.buscarExamenPorNombreConPreguntas("Historia");
		
		assertEquals(1L, examen.getId());
		assertEquals("Historia", examen.getNombre());
		assertEquals(1, examen.getPreguntas().size());
		assertTrue(examen.getPreguntas().contains("Geometria"));
		
		verify(repository).obtenerTodo();	//Real
		verify(preguntaRepository).obtenerPreguntasPorExamenId(anyLong()); //Simulado
		
	}

}
