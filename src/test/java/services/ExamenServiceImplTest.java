package services;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.ArgumentMatcher;
import org.mockito.Captor;
import org.mockito.InOrder;
import org.mockito.InjectMocks;
import org.mockito.Mock;
//import org.mockito.MockitoAnnotations;
import org.mockito.invocation.InvocationOnMock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.stubbing.Answer;

import models.Examen;
import repositories.ExamenRepository;
import repositories.ExamenRepositoryImpl;
import repositories.PreguntaRepository;
import repositories.PreguntaRepositoryImpl;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

//2da forma de permitir anotaciones en mockito
@ExtendWith(MockitoExtension.class)
public class ExamenServiceImplTest {

	@Mock
	ExamenRepositoryImpl repository;

	@Mock
	PreguntaRepositoryImpl preguntaRepository;

	@InjectMocks // Requiere forzosamente una Clase NO una interfaz
	ExamenServiceImpl service;

	@Captor
	ArgumentCaptor<Long> captor;

	@BeforeEach
	@Test
	@DisplayName("Cargar repositorios y servicio")
	void initEachTest() {
		// 1ra forma de permitir anotaciones en mockito
		// MockitoAnnotations.openMocks(this);

		//Mock = Se puede utilizar la interfaz , clase abstracta o la clase real , al final los metodos de van a SIMULAR
		// Las siguientes sentencias son reemplazadas por las anotaciones previas.
//		this.repository = mock(ExamenRepositoryImpl.class);	
//		this.preguntaRepository = mock(PreguntaRepositoryImpl.class);
//		this.service = new ExamenServiceImpl(repository, preguntaRepository);
	}

	@Test
	@DisplayName("Buscar por Nombre")
	void buscarExamenporNombre() {

		// no importa que en "ExamenRepositoryImpl" su metodo regrese una lista vacia
		// con "when,then" se sustituye su comportamiento
		when(repository.obtenerTodo()).thenReturn(Datos.EXAMENES);

		Optional<Examen> examen = service.buscarExamenPorNombre("Historia");

		assertTrue(examen.isPresent());
		assertNotNull(examen);
		assertEquals(1L, examen.orElseThrow().getId()); // .orElseThrow no ocupa parametros a partir de java 10
		assertEquals("Historia", examen.get().getNombre());

	}

	@Test
	@DisplayName("Buscar en una lista vacia")
	void buscarExamenporNombreListaVacia() {

		List<Examen> datos = Collections.emptyList();

		when(repository.obtenerTodo()).thenReturn(datos);
		Optional<Examen> examen = service.buscarExamenPorNombre("Matematicas");

		assertFalse(examen.isPresent());

	}

	@Test
	@DisplayName("Preguntas por examen")
	void testPreguntasExamen() {
		when(repository.obtenerTodo()).thenReturn(Datos.EXAMENES);
		when(preguntaRepository.obtenerPreguntasPorExamenId(anyLong())).thenReturn(Datos.PREGUNTAS_HISTORIA);
		Examen examen = service.buscarExamenPorNombreConPreguntas("Química");

		assertEquals(5, examen.getPreguntas().size());
		assertTrue(examen.getPreguntas().contains("Pregunta 3_Historia"));
	}

	@Test
	@DisplayName("Verificar preguntas por examen")
	void testVerificarPreguntasExamen() {
		when(repository.obtenerTodo()).thenReturn(Datos.EXAMENES);
		when(preguntaRepository.obtenerPreguntasPorExamenId(anyLong())).thenReturn(Datos.PREGUNTAS_HISTORIA);

		service.buscarExamenPorNombreConPreguntas("Matemáticas");

		// Verify = VERIFICA si se invoco o no, cierto metodo
		verify(repository).obtenerTodo(); // Cumple Gracias a service.obtenerExamenPorNombreConPreguntas
		verify(preguntaRepository).obtenerPreguntasPorExamenId(2L);
	}

	@Test
	@DisplayName("Verificar que el examen NO sea nulo")
	void testNotExistExamen() {
		// Given = Dado un entorno de pruebas
		when(repository.obtenerTodo()).thenReturn(Datos.EXAMENES);
		when(preguntaRepository.obtenerPreguntasPorExamenId(anyLong())).thenReturn(Datos.PREGUNTAS_HISTORIA);

		// When = Cuando ejecutamos X metodo
		Examen examen = service.buscarExamenPorNombreConPreguntas("Historia");

		// Then = Entonces validamos
		assertNotNull(examen);
		verify(repository).obtenerTodo();
		verify(preguntaRepository).obtenerPreguntasPorExamenId(1L);
	}

	@Test
	@DisplayName("Verifica preguntas de un examen")
	void testGuardarExamen() {
		// Given = Dado un entorno de prueba

		// Cargar examen con preguntas
		Examen newExamen = Datos.EXAMEN;
		newExamen.setPreguntas(Datos.PREGUNTAS_HISTORIA);

		// Cuando se invoque metodo guardar, simular asignacion autoincrementable del Id
		// Examen
		when(repository.guardarExamen(any(Examen.class))).then(new Answer<Examen>() {

			Long secuenciaId = 4L;

			@Override
			public Examen answer(InvocationOnMock invocation) throws Throwable {
				Examen examen = invocation.getArgument(0);
				examen.setId(secuenciaId++);
				return examen;
			}

		});

		// When = Cuando ejecutamos X metodo
		Examen examen = service.guardarExamen(newExamen);

		// Then = Entonces validamos
		assertNotNull(examen.getId());
		assertEquals(4L, examen.getId());
		assertEquals("Diseño", examen.getNombre());

		// Verificar si se invocan los siguientes metodos
		verify(repository).guardarExamen(any(Examen.class));
		verify(preguntaRepository).guardarPreguntas(anyList());
	}

	@Test
	@DisplayName("Manejo de Excepciones")
	void testManejoException() {
		when(repository.obtenerTodo()).thenReturn(Datos.EXAMENES_ID_NULL);
		when(preguntaRepository.obtenerPreguntasPorExamenId(isNull())).thenThrow(IllegalArgumentException.class);

		Exception exception = assertThrows(IllegalArgumentException.class, () -> {
			service.buscarExamenPorNombreConPreguntas("Historia");
		});

		assertNotEquals(RuntimeException.class, exception.getClass());
		assertEquals(IllegalArgumentException.class, exception.getClass());

		verify(repository).obtenerTodo();
		verify(preguntaRepository).obtenerPreguntasPorExamenId(isNull());

	}

	@Test
	@DisplayName("Asegurar que X argumentos se pasen a los Mocks")
	void testArgumentMatchers() {
		when(repository.obtenerTodo()).thenReturn(Datos.EXAMENES);
		when(preguntaRepository.obtenerPreguntasPorExamenId(anyLong())).thenReturn(Datos.PREGUNTAS_HISTORIA);

		service.buscarExamenPorNombreConPreguntas("Matemáticas");

		verify(repository).obtenerTodo();
		// verify(preguntaRepository).obtenerPreguntasPorExamenId(argThat(arg -> arg !=
		// null && arg.equals(4L)));
		// verify(preguntaRepository).obtenerPreguntasPorExamenId(argThat(arg -> arg !=
		// null && arg > 3L));
		// verify(preguntaRepository).obtenerPreguntasPorExamenId(eq(2L));
		verify(preguntaRepository).obtenerPreguntasPorExamenId(2L);
	}

	@Test
	@DisplayName("Asegurar que X argumentos se pasen a los Mocks, clase personalizada")
	void testArgumentMatchers2() {
		when(repository.obtenerTodo()).thenReturn(Datos.EXAMENES_ID_NEGATIVOS);
		when(preguntaRepository.obtenerPreguntasPorExamenId(anyLong())).thenReturn(Datos.PREGUNTAS_HISTORIA);

		service.buscarExamenPorNombreConPreguntas("Historia");

		verify(repository).obtenerTodo();
		// Utilizar ArgumentMatcher personalizada de la clase anidada
		verify(preguntaRepository).obtenerPreguntasPorExamenId(argThat(new MiArgsMatchers()));
	}

	public static class MiArgsMatchers implements ArgumentMatcher<Long> {

		private Long argument;

		@Override
		public boolean matches(Long argument) {
			this.argument = argument;
			return argument != null && argument >= -1;
		}

		@Override
		public String toString() {

			return "Mensaje personalizado de Error en caso de fallo en el Test" + "El valor [" + argument
					+ "] deberia ser un valor positivo.";
		}

	}

	@Test
	@DisplayName("Caputa de Argumentos")
	void testArgumentCaptor() {
		when(repository.obtenerTodo()).thenReturn(Datos.EXAMENES);

		service.buscarExamenPorNombreConPreguntas("Historia");

		// La siguiente linea es sustituida por las anotaciones @Captor ,
		// ArgumentCaptor<Long> captor
		// ArgumentCaptor<Long> captor = ArgumentCaptor.forClass(Long.class);
		verify(preguntaRepository).obtenerPreguntasPorExamenId(captor.capture());

		assertEquals(1L, captor.getValue());
	}

	@Test
	@DisplayName("Test DoThrow")
	void testDoThrow() {
		Examen examen = Datos.EXAMEN;
		examen.setPreguntas(Datos.PREGUNTAS_HISTORIA);
		// No se puede utilizar When con un metodo VOID, no hay nada que regresar
		// when(preguntaRepository.guardarPreguntas(anyList())).thenThrow(IllegalArgumentException.class);

		// Se espera que sea lanzado un EllegalArgumentException CUANDO se ejecute
		// guardarPreguntas de preguntaRepository
		doThrow(IllegalArgumentException.class).when(preguntaRepository).guardarPreguntas(anyList());

		assertThrows(IllegalArgumentException.class, () -> {
			service.guardarExamen(examen);
		});
	}

	@Test
	@DisplayName("Test DoAnswer")
	void testDoAnswer() {
		when(repository.obtenerTodo()).thenReturn(Datos.EXAMENES);
		// when(preguntaRepository.obtenerPreguntasPorExamenId(anyLong())).thenReturn(Datos.PREGUNTAS_HISTORIA);

		doAnswer(invocation -> {
			Long idExamen = invocation.getArgument(0);
			return idExamen == 1L ? Datos.PREGUNTAS_HISTORIA : Collections.emptyList();
		}).when(preguntaRepository).obtenerPreguntasPorExamenId(anyLong());

		Examen examen = service.buscarExamenPorNombreConPreguntas("Historia");

		assertEquals(5, examen.getPreguntas().size());
		assertTrue(examen.getPreguntas().contains("Pregunta 1_Historia"));
		assertFalse(examen.getPreguntas().contains("Pregunta 5_Historia - FALSA"));
		assertNotNull(examen.getId());
		assertEquals(1L, examen.getId());
		assertEquals("Historia", examen.getNombre());

		verify(preguntaRepository).obtenerPreguntasPorExamenId(anyLong());
	}

	@Test
	@DisplayName("Verifica preguntas de un examen con DoAnswer")
	void testGuardarExamenDoAnswer() {
		// Given = Dado un entorno de prueba

		// Cargar examen con preguntas
		Examen newExamen = Datos.EXAMEN;
		newExamen.setPreguntas(Datos.PREGUNTAS_HISTORIA);

		// Cuando se invoque metodo guardar, simular asignacion autoincrementable del Id
		// Examen
		doAnswer(new Answer<Examen>() {
			Long secuenciaId = 4L;

			@Override
			public Examen answer(InvocationOnMock invocation) throws Throwable {
				Examen examen = invocation.getArgument(0);
				examen.setId(secuenciaId++);
				return examen;
			}
		}).when(repository).guardarExamen(any(Examen.class));

		// When = Cuando ejecutamos X metodo
		Examen examen = service.guardarExamen(newExamen);

		// Then = Entonces validamos
		assertNotNull(examen.getId());
		assertEquals(4L, examen.getId());
		assertEquals("Diseño", examen.getNombre());

		// Verificar si se invocan los siguientes metodos
		verify(repository).guardarExamen(any(Examen.class));
		verify(preguntaRepository).guardarPreguntas(anyList());
	}

	@Test
	@DisplayName("Llamado de metodo REAL")
	void testDoCallRealMethod() {
		when(repository.obtenerTodo()).thenReturn(Datos.EXAMENES);
		//Llamada a metodo simulado
		//when(preguntaRepository.obtenerPreguntasPorExamenId(anyLong())).thenReturn(Datos.PREGUNTAS_HISTORIA);
		//Llamada a metodo real
		doCallRealMethod().when(preguntaRepository).obtenerPreguntasPorExamenId(anyLong());

		Examen examen = service.buscarExamenPorNombreConPreguntas("Historia");

		assertEquals(1L, examen.getId());
		assertEquals("Historia", examen.getNombre());
	}
	
	
	@Test
	@DisplayName("Ejecucion de Spy")
	void testSpy() { //Spy = Ejecuta el metodo REAL, necesita siempre una clase REAL (no abstracta, no interfaz)
		//El tipo de dato se puede trabajar con la Interfaz
		ExamenRepository examenRepository = spy(ExamenRepositoryImpl.class);
		PreguntaRepository preguntaRepository = spy(PreguntaRepositoryImpl.class);
		ExamenService examenService = new ExamenServiceImpl(examenRepository, preguntaRepository);
		
		//Hibrido = Al momento de llamar al metodo "obtenerPreguntasPorExamenId" se hace una invocacion falsa 
		List<String> preguntas = Arrays.asList("Geometria");
		//when(preguntaRepository.obtenerPreguntasPorExamenId(anyLong())).thenReturn(preguntas);
		//Con Spy se recomienda trabajar con do y no con when
		doReturn(preguntas).when(preguntaRepository).obtenerPreguntasPorExamenId(anyLong());
		
		Examen examen = examenService.buscarExamenPorNombreConPreguntas("Historia");
		
		assertEquals(1L, examen.getId());
		assertEquals("Historia", examen.getNombre());
		assertEquals(1, examen.getPreguntas().size());
		assertTrue(examen.getPreguntas().contains("Geometria"));
		
		verify(examenRepository).obtenerTodo();	//Real
		verify(preguntaRepository).obtenerPreguntasPorExamenId(anyLong()); //Simulado
		
	}
	
	//Verificar orden de ejecucion de mocks
	@Test
	@DisplayName("Orden de invocaciones")
	//Verificar orden de ejecucion de metodos
	void TestOrderInvocaciones() {
		when(repository.obtenerTodo()).thenReturn(Datos.EXAMENES);
		
		service.buscarExamenPorNombreConPreguntas("Matemáticas");
		service.buscarExamenPorNombreConPreguntas("Historia");
		
						//inOrder = Puede trabajar con 1 o multiples mocks
		InOrder inOrder = inOrder(repository, preguntaRepository);
		inOrder.verify(repository).obtenerTodo();
		inOrder.verify(preguntaRepository).obtenerPreguntasPorExamenId(2L);
		inOrder.verify(repository).obtenerTodo();
		inOrder.verify(preguntaRepository).obtenerPreguntasPorExamenId(1L);
	}
	
	@Test
	@DisplayName("Numero de invocaciones")
	void testNumeroInvocaciones() {
		when(repository.obtenerTodo()).thenReturn(Datos.EXAMENES);
		service.buscarExamenPorNombreConPreguntas("Historia");
		
		verify(preguntaRepository).obtenerPreguntasPorExamenId(1L);
		verify(preguntaRepository, times(1)).obtenerPreguntasPorExamenId(1L);
		verify(preguntaRepository, atLeast(1)).obtenerPreguntasPorExamenId(1L);
		verify(preguntaRepository, atLeastOnce()).obtenerPreguntasPorExamenId(1L);
		verify(preguntaRepository, atMost(1)).obtenerPreguntasPorExamenId(1L);
		verify(preguntaRepository, atMostOnce()).obtenerPreguntasPorExamenId(1L);
		
		
	}
	
	@Test
	@DisplayName("Numero de invocaciones II")
	void testNumeroInvocacionesII() {
		when(repository.obtenerTodo()).thenReturn(Collections.emptyList());
		service.buscarExamenPorNombreConPreguntas("Historia");
		
		verify(preguntaRepository, never()).obtenerPreguntasPorExamenId(1L);
		verifyNoInteractions(preguntaRepository);
		
		verify(repository).obtenerTodo();
		verify(repository,times(1)).obtenerTodo();
		verify(repository, atLeast(1)).obtenerTodo();
		verify(repository, atLeastOnce()).obtenerTodo();
		verify(repository, atMost(1)).obtenerTodo();
		verify(repository, atMostOnce()).obtenerTodo();
	}
	

}
