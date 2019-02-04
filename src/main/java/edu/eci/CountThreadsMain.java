package edu.eci;

/**
 *
 * @author hcadavid
 */
public class CountThreadsMain {

	public static void main(String a[]) {
		CountThread cont1 = new CountThread(0, 99);
		CountThread cont2 = new CountThread(100, 199);
		CountThread cont3 = new CountThread(200, 299);
		
		/*Start: ejecuta los hilos al mismo tiempo. Crea un nuevo manejador de excepciones solo para él  solo. 
		 * Si no tengo star, y sol orun, y sale exccepcio nen el segundo hilo, se revienta elprograma*/
		/* Run: ejecuta los hilos secuencialmente. Termina uno y empieza el otro */
		
		//cont1.start();
		//cont2.start();
		//cont3.start();
		
		// cont1.run();
		// cont2.run();
		// cont3.run();
	}

}
