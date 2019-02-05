package edu.eci.arsw.primefinder;

import java.util.LinkedList;
import java.util.List;

public class PrimeFinderThread extends Thread {

	int a, b;
	boolean wait;

	private List<Integer> primes;

	public PrimeFinderThread(int a, int b) {
		super();
		this.primes = new LinkedList<>();
		this.a = a;
		this.b = b;
		this.wait = false;
	}

	@Override
	public void run() {
		for (int i = a; i < b; i++) {
			if (wait) {
				try {
					synchronized (this) {
						this.wait();
					}
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
			} else {
				if (isPrime(i)) {
					primes.add(i);
				}
			}
		}		
	}	

	boolean isPrime(int n) {
		boolean ans;
		if (n > 2) {
			ans = n % 2 != 0;
			for (int i = 3; ans && i * i <= n; i += 2) {
				ans = n % i != 0;
			}
		} else {
			ans = n == 2;
		}
		return ans;
	}

	public List<Integer> getPrimes() {
		return primes;
	}
	
	public void setWait(boolean wait) {
		this.wait = wait;
	}
	
	public synchronized void wakeUp(boolean wait) {
		this.wait = wait;
		synchronized (this) {
			this.notifyAll();			
		}
	}

}