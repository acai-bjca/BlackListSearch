package edu.eci;

/**
 *
 * @author hcadavid
 */
public class CountThread extends Thread {
	public int a;
	public int b;
	
	public CountThread(int a, int b) {
		super("my extending thread");
		this.a = a;
		this.b = b;
		System.out.println("my thread created" + this);
	}
	

	public void run() {
		try {
			for (int i = a; i<=b; i++) {
				System.out.println("Printing the count " + i);
				Thread.sleep(1000);
			}
		} catch (InterruptedException e) {
			System.out.println("my thread interrupted");
		}
		System.out.println("My thread run is over");
	}
}
