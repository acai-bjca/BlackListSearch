package snakepackage;

import java.awt.Dimension;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JFrame;
import javax.swing.JLabel;

import enums.GridSize;
import java.awt.BorderLayout;
import java.awt.FlowLayout;
import java.awt.Label;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.JButton;
import javax.swing.JPanel;

/**
 * @author jd-
 *
 */
public class SnakeApp {

	private static SnakeApp app;
	public static final int MAX_THREADS = 8;
	Snake[] snakes = new Snake[MAX_THREADS];
	private static final Cell[] spawn = { new Cell(1, (GridSize.GRID_HEIGHT / 2) / 2),
			new Cell(GridSize.GRID_WIDTH - 2, 3 * (GridSize.GRID_HEIGHT / 2) / 2),
			new Cell(3 * (GridSize.GRID_WIDTH / 2) / 2, 1),
			new Cell((GridSize.GRID_WIDTH / 2) / 2, GridSize.GRID_HEIGHT - 2),
			new Cell(1, 3 * (GridSize.GRID_HEIGHT / 2) / 2),
			new Cell(GridSize.GRID_WIDTH - 2, (GridSize.GRID_HEIGHT / 2) / 2),
			new Cell((GridSize.GRID_WIDTH / 2) / 2, 1),
			new Cell(3 * (GridSize.GRID_WIDTH / 2) / 2, GridSize.GRID_HEIGHT - 2) };
	boolean encontroMuerta = false;
	private JFrame frame;
	private JButton iniciar;
	private JButton pausar;
	private JButton reanudar;
	private JLabel longestSnake;
	private JLabel worstSnake;
	private Snake primeraEnMorir;
	private static Board board;
	int nr_selected = 0;
	Thread[] thread = new Thread[MAX_THREADS];

	public SnakeApp() {
		Dimension dimension = Toolkit.getDefaultToolkit().getScreenSize();
		frame = new JFrame("The Snake Race");
		frame.setLayout(new BorderLayout());
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		// frame.setSize(618, 640);
		frame.setSize(GridSize.GRID_WIDTH * GridSize.WIDTH_BOX + 17, GridSize.GRID_HEIGHT * GridSize.HEIGH_BOX + 75);
		frame.setLocation(dimension.width / 2 - frame.getWidth() / 2, dimension.height / 2 - frame.getHeight() / 2);
		board = new Board();
		
		frame.add(board, BorderLayout.CENTER);

		JPanel actionsBPabel = new JPanel();
		actionsBPabel.setLayout(new FlowLayout());

		crearBotones();

		longestSnake = new JLabel();
		worstSnake = new JLabel();

		actionsBPabel.add(iniciar);
		actionsBPabel.add(pausar);
		actionsBPabel.add(reanudar);
		actionsBPabel.add(longestSnake);
		actionsBPabel.add(worstSnake);
		frame.add(actionsBPabel, BorderLayout.SOUTH);
		
	}

	public void crearBotones() {
		iniciar = new JButton("Iniciar");
		pausar = new JButton("Pausar");
		reanudar = new JButton("Reanudar");
		ActionListener l = new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				for (int i = 0; i != MAX_THREADS; i++) {
					snakes[i].setInicio(true);
				}
				iniciar.setEnabled(false);
				pausar.setEnabled(true);
			}
		};

		ActionListener p = new ActionListener() {
			
			public void actionPerformed(ActionEvent e) {
				int longitudSnake = 0;
				int snake = 0;				
				for (int i = 0; i != MAX_THREADS; i++) {
					snakes[i].setPausa(true);
					if (snakes[i].getBody().size() > longitudSnake) {
						longitudSnake = snakes[i].getBody().size();						
						snake = i;						
					}
				}
				if(encontroMuerta) {
					worstSnake.setText("♦ Worst Snake: "+primeraEnMorir.getIdt());
				} else {
					worstSnake.setText("♦ No ha muerto ninguna.");
				}
				longestSnake.setText("♦ Longest Snake: "+longitudSnake);
				pausar.setEnabled(false);
				reanudar.setEnabled(true);
			}
		};

		ActionListener r = new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				for (int i = 0; i != MAX_THREADS; i++) {
					snakes[i].setPausa(false);
				}
				longestSnake.setText(null);
				worstSnake.setText(null);
				pausar.setEnabled(true);
				reanudar.setEnabled(false);
			}
		};
		iniciar.addActionListener(l);
		pausar.addActionListener(p);
		reanudar.addActionListener(r);
		pausar.setEnabled(false);
		reanudar.setEnabled(false);
	}

	public static void main(String[] args) {
		app = new SnakeApp();
		app.init();
	}

	private void init() {

		for (int i = 0; i != MAX_THREADS; i++) {

			snakes[i] = new Snake(i + 1, spawn[i], i + 1);
			snakes[i].addObserver(board);
			thread[i] = new Thread(snakes[i]);
			thread[i].start();
		}

		frame.setVisible(true);

		while (true) {
			int x = 0;			
			for (int i = 0; i != MAX_THREADS; i++) {
				if (snakes[i].isSnakeEnd() == true) {
					x++;
				}
				if(x==1 && !encontroMuerta) {
					encontroMuerta = true;
					primeraEnMorir = snakes[i];
				}
			}
			if (x == MAX_THREADS) {
				break;
			}
		}

		System.out.println("Thread (snake) status:");
		for (int i = 0; i != MAX_THREADS; i++) {
			System.out.println("[" + i + "] :" + thread[i].getState());
		}

	}

	public static SnakeApp getApp() {
		return app;
	}

}
