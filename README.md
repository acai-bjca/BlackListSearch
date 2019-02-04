# Black List Search
## Part I - Introduction to threads in JAVA
- El método **start()** de los thread prepara a los threads antes de que inicien su ejecución. Crea un nuevo hilo aparte del principal y prepara todos los elementos necesarios en la máquina virtual de java y luego ejecuta el método **run()** para que el thread realice lo que para lo que fue creado. Esto permite que varios threads se ejecuten en paralelo haciendo interleaving, que es que cada uno realiza una instrucción después del otro, es decir, se van intercambiando el procesador.
- El método **run()** ejecuta el método del thread inmediatamente, por lo que lo hace de manera secuencial. Si hay varios threads, se ejecutarán en orden de acuerdo a los llamados del método run y no lo harán en paralelo.
## Part II - Black List Search Exercise
- El método **join()** hace que el hilo principal no termine hasta que todos los hilos que hayan ejecutado el método join hayan terminado. (Nosotros empezamos todos los hilos y después ejecutamos el método join a cada uno, para que se ejecutaran de manera secuencia.)
- Aquí se debe garantizar que efectivamente el resultado mostrado sea el mismo tanto con hilos como sin hilos.
	- Sin hilos:


		![](/src/main/resources/ResultadoSinThreads.PNG)
	- Con hilos:


		![](/src/main/resources/resultadoConHilos1.PNG)
- Probamos con la dirección del host **"212.24.24.55"** y verificar que el programa modificado con Threads, siga funcionando correctamente y resulte que la dirección ip del host es confiable.

	![](/src/main/resources/resultadoConHilos2.PNG)

## Part III - Discussion
- Para minimizar el número de consultas que se hacen a los servidores, añadimos a la ejecución de los Thread (en el método run()), una condición que hace que en cualquiera de ellos en estado de ejecución termine el proceso de búsqueda en las listas negras, si en sus segmentos ya se han encontrado 5 o más ocurrencias del host dado.

```java
package blacklist;

public class SearchThread extends Thread {
	public int iniSegmento;
	public int finSegmento;
	public String ipaddress;
	public HostBlacklistsDataSourceFacade skds;
	public int BLACK_LIST_ALARM_COUNT;
	public HostBlackListsValidator validator;
	public int numHilo;
	
	public SearchThread(int iniSegmento, int finSegmento, String ipaddress, int numHilo, HostBlacklistsDataSourceFacade skds, int BLACK_LIST_ALARM_COUNT, HostBlackListsValidator validator) throws InterruptedException {
		super("hilo "+numHilo);
		this.iniSegmento = iniSegmento;
		this.finSegmento = finSegmento;
		this.ipaddress = ipaddress;
		this.skds = skds;
		this.BLACK_LIST_ALARM_COUNT = BLACK_LIST_ALARM_COUNT;
		this.validator = validator;
		this.numHilo = numHilo;
	}	

	public void run() {			
		for (int i=iniSegmento; i<=finSegmento && validator.getOcurrencesCount()<BLACK_LIST_ALARM_COUNT; i++) {
			validator.setCheckedListsCount();          
            if (skds.isInBlackListServer(i, ipaddress)){
            	validator.setBlackListOcurrences(i);              
                validator.setOcurrencesCount();
            }
        }
	}
	
}
```

- Aquí lo que hacemos es traer de la case validadora, la cantidad de ocurrencias que se han encontrado y mirar si son menos de 5, para continuar o detener la búsqueda. En la imagen se ve, que ya no se buscan en todas las **80.000 listas negras** que hay, sino que el número se redujo.

	![](/src/main/resources/ResultadoOptimizado.PNG)
## Part IV - Performance Evaluation
1. Un sólo Thread.

	**Tiempo de ejecución:** 1m:51:06
	
	![](/src/main/resources/UnSoloThread.PNG)

	![](/src/main/resources/JVisualUnSoloThread.PNG)

2. Tantos threads como núcleos de procesamiento. (4 núcleos)

    **Tiempo de ejecución:** 0m:18:95
	
	![](/src/main/resources/VariosThreadProcesador.PNG)

	![](/src/main/resources/JVisualVariosThreadProcesador.PNG)

3. Tantos threads como el doble de núcleos de procesamiento. (4 núcleos * 2 = 8)

	**Tiempo de ejecución:** 0m:01:00
	
	![](/src/main/resources/VariosThreadProcesadorPor2.PNG)

	![](/src/main/resources/JVisualVariosThreadProcesadorPor2.PNG)

4. 50 threads

	**Tiempo de ejecución:** 0m:01:00
	
	![](/src/main/resources/50Threads.PNG)

	![](/src/main/resources/JVisual50Threads.PNG)

5. 100 threads

	**Tiempo de ejecución:** 0m:01:00
	
	![](/src/main/resources/100Threads.PNG)

	![](/src/main/resources/JVisual100Threads.PNG)
---
- Preguntas:

	| Número de   Hilos | Tiempo |
	|-------------------|--------|
	|         1         | 1,5106 |
	|         4         | 0,1895 |
	|         8         |  0,01  |
	|         50        |  0,01  |
	|        100        |  0,01  |
	
	![](/src/main/resources/nucleosVsTiempo.PNG)

- Hay un límite cuando la fracción de mejora se acerca al 100%, es decir que lo que el tiempo que se mejora es casi la porción total del programa. Al mismo tiempo, entre mas se aumente el número de procesadores (en este caso de threads), entonces también va a existir un límite al infinito, pues la fracción se reduce a 0. Es por esta razón, que no hay mucha diferencia entre el rendimiento entre 200 threads y 500 threads, pues según la **Ley de Amdahl** ambos valores por ser tan grandes, hacen que el resultado de la "aceleración" tienda al infinito.
- Según la tabla y gráfica anteriores, podemos ver que en nuestro caso se usaron primero 4 threads (pues es procesador de 4 núcleos) y tuvo un tiempo de ejecución mayor que cuando duplicamos la cantidad de threads. Aunque el cambio en el tiempo fue pequeño, se logró ver que mejoró y encontró el host en las listas negras más rápido.
- La Ley de Amdahl es mucho mejor porque es mejor tener varias máquinas trabajando paralelamente que una máquina trabajando concurrentemente, esto es porque la cantidad de hilos presentes en varias máquinas puede ser menor, por lo que el límite de la aceleración no tendería al infinito y el rendimiento sería mayor, por el contrario si en una sola máquina tengo muchos hilos y los aumento el rendimiento va a disminuir.