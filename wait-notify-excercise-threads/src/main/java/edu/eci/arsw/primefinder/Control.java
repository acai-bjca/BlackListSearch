/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package edu.eci.arsw.primefinder;

import java.util.Scanner;
import java.util.Timer;
import java.util.TimerTask;

/**
 *
 */
public class Control extends Thread {

	private final static int NTHREADS = 3;
	private final static int MAXVALUE = 30000000; // 30000000
	private final static int TMILISECONDS = 2000;
	private Timer timer;
	private int reloj;
	private TimerTask timerTask;

	private final int NDATA = MAXVALUE / NTHREADS; // Cantidad de datos por thread.

	private PrimeFinderThread pft[];

	private Control() {
		super();
		reloj = 0;
		this.pft = new PrimeFinderThread[NTHREADS];

		int i;
		for (i = 0; i < NTHREADS - 1; i++) {
			pft[i] = new PrimeFinderThread(i * NDATA, (i + 1) * NDATA);
		}
		pft[i] = new PrimeFinderThread(i * NDATA, MAXVALUE + 1);
		iniciarTimer();
	}

	public static Control newControl() {
		return new Control();
	}

	@Override
	public void run() {
		for (int i = 0; i < NTHREADS; i++) {
			pft[i].start();
		}
		Scanner scan = new Scanner(System.in);
		boolean exit = false, wait = false;
		int cantidadThreadsTerminados = 1;
		while (!exit) {
			System.out.println("Reloj: "+reloj);
			if (reloj >= TMILISECONDS) {
				for (int i = 0; i < NTHREADS; i++) {	
					System.out.print("Thread: "+(i+1)+" --> ");
					pft[i].setWait(true);
					System.out.println(pft[i].getPrimes().size());
				}
				wait = true;
				reloj = 0;
				timer.cancel();
			}
			if (wait) {
				System.out.println("Oprime ENTER (si desea salir escriba exit)");
				String comando = scan.nextLine();
				if (comando.equals("")) {
					for (int i = 0; i < NTHREADS; i++) {
						pft[i].wakeUp(false);					
					}
					wait = false;
					iniciarTimer();
					for (int i = 0; i < NTHREADS; i++) {	
						if (!pft[i].isAlive()) {
							cantidadThreadsTerminados++;
						}
					}
					if (cantidadThreadsTerminados == NTHREADS) {
						exit = true;
						timer.cancel();
					} else {
						cantidadThreadsTerminados = 0;
					}
				} else if (comando.equals("exit")) {
					exit = true;
					timer.cancel();
				}
			}			
		}		
		scan.close();
	}
	
	public void iniciarTimer() {
		timer = new Timer();
		timerTask = new TimerTask() {
			@Override
			public void run() {
				reloj += 1000;
			}
		};
		timer.scheduleAtFixedRate(timerTask, 0, 1000);
	}

}