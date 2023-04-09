package repositories;

//import java.util.Arrays;
//import java.util.Collection;
//import java.util.Collections;
import java.util.List;
import java.util.concurrent.TimeUnit;

import models.Examen;
import services.Datos;

public class ExamenRepositoryImpl implements ExamenRepository{	//Esta clase queda obsoleta gracias al uso de los mocks

	@Override
	public Examen guardarExamen(Examen examen) {
		System.out.println("ExamenRepositoryImpl.guardarExamen");
		return Datos.EXAMEN;
	}
	
	@Override
	public List<Examen> obtenerTodo() {	//Implementacion Falsa
		System.out.println("ExamenRepositoryImpl.obtenerTodo");
		try {
			TimeUnit.SECONDS.sleep(2);
		}catch(InterruptedException e) {
			e.printStackTrace();
		}
		return Datos.EXAMENES;
	}

	

}
