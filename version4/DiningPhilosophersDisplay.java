

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Graphics;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JPanel;

/**
 * This very simple class (and very limited)  implements a panel that displays a table, philosophers, plates, and chopsticks/forks that can
 * be used to animate a dining philosopher application. Essentially, you create the display using the constructor passing in
 * the number of philosophers. Then use the setPhilosopherState(), setForkTaken(), setForkAvailable() functions to update
 * the state of the display. You do not have to call repaint() on the display. 
 * 
 * @author Henri Casanova
 */
@SuppressWarnings("serial")
public class DiningPhilosophersDisplay extends JPanel implements ActionListener {

	// Constants defining philosopher states
	/** "Thinking" state of a philosopher
	 */
	static public final int THINKING=0;
	/** "Hungry" state of a philosopher
	 */
	static public final int HUNGRY=1;
	/** "Eating" state of a philosopher
	 */
	static public final int EATING=2;

	/**
	 * Creates a new display for the Dining Philosophers problem
	 * 
	 * @param     n       The number of philosophers
	 */
	public DiningPhilosophersDisplay(int n) {
		super();
		topPanel = new JPanel();
		tablePanel = new TablePanel();

		// The JComboBoxes
		minThinkTime = new JComboBox();
		maxThinkTime = new JComboBox();
		minEatTime = new JComboBox();
		maxEatTime = new JComboBox();
		minThinkTime.setPrototypeDisplayValue("XXXXX");
		maxThinkTime.setPrototypeDisplayValue("XXXXX");
		minEatTime.setPrototypeDisplayValue("XXXXX");
		maxEatTime.setPrototypeDisplayValue("XXXXX");

		for (int i=1; i<=10; i++) {
			minThinkTime.addItem(new Integer(i));
			maxThinkTime.addItem(new Integer(i));
			minEatTime.addItem(new Integer(i));
			maxEatTime.addItem(new Integer(i));
		}
		minThinkTime.setSelectedIndex(0);
		maxThinkTime.setSelectedIndex(1);
		minEatTime.setSelectedIndex(0);
		maxEatTime.setSelectedIndex(1);

		minThinkTime.addActionListener(this);
		maxThinkTime.addActionListener(this);
		minEatTime.addActionListener(this);
		maxEatTime.addActionListener(this);

		// The Rest of the GUI
		topPanel.setLayout(new FlowLayout());
		topPanel.add(new JLabel("Think time:"));
		topPanel.add(minThinkTime);
		topPanel.add(maxThinkTime);
		topPanel.add(new JLabel("     Eat time:"));
		topPanel.add(minEatTime);
		topPanel.add(maxEatTime);

		tablePanel.setPreferredSize(new Dimension(size, size+50));
		num_phils = n;
		forks = new int[num_phils];
		phil_states = new int[num_phils];
		for (int i=0; i<num_phils; i++) {
			// Forks are initially all on the table
			forks[i] = -1;
			// Philosophers are initially all thinking
			phil_states[i] = DiningPhilosophersDisplay.THINKING;
		}

		this.setLayout(new BorderLayout());
		this.add(topPanel, BorderLayout.NORTH);
		this.add(tablePanel, BorderLayout.SOUTH);
	}

	/**
	 * Returns the minimum "think time" selected by the user.
	 *
	 * @returns   the value of the minimum think time in seconds 
	 */
	public int getMinThinkTime() {
		return ((Integer)(minThinkTime.getSelectedItem())).intValue();
	}

	/**
	 * Returns the maximum "think time" selected by the user.
	 *
	 * @returns   the value of the maximum think time in seconds 
	 */
	public int getMaxThinkTime() {
		return ((Integer)(maxThinkTime.getSelectedItem())).intValue();
	}

	/**
	 * Returns the minimum "eat time" selected by the user.
	 *
	 * @returns   the value of the minimum eat time in seconds 
	 */
	public int getMinEatTime() {
		return ((Integer)(minEatTime.getSelectedItem())).intValue();
	}

	/**
	 * Returns the maximum "eat time" selected by the user.
	 *
	 * @returns   the value of the maximum eat time in seconds 
	 */
	public int getMaxEatTime() {
		return ((Integer)(maxEatTime.getSelectedItem())).intValue();
	}

	/**
	 * Places a philosopher in a given state. 
	 *
	 * @param     phil    The index of the philosopher (between 0 and n-1)
	 * @param     state   One of: {@link DiningPhilosophersDisplay#THINKING}, {@link DiningPhilosophersDisplay#HUNGRY}, {@link DiningPhilosophersDisplay#EATING}
	 * @throws    IllegalArgumentException If an invalid fork or philosopher index or an invalid state is given
	 */
	public void setPhilosopherState(int phil, int state) throws IllegalArgumentException {
		if ((state != DiningPhilosophersDisplay.THINKING) &&
				(state != DiningPhilosophersDisplay.HUNGRY) &&
				(state != DiningPhilosophersDisplay.EATING)) {
			throw new IllegalArgumentException("Invalid philosoper state");
		}
		if ((phil < 0) || (phil > num_phils - 1)) {
			throw new IllegalArgumentException("Invalid philosoper index");
		}
		phil_states[phil] = state;
		this.repaint();
	}

	/**
	 * Give a fork to a philosopher
	 *
	 * @param     fork    The index of the fork (between 0 and n-1)
	 * @param     phil    The index of the philosopher: either equal to fork, or to (fork + 1) modulo n
	 * @throws    IllegalArgumentException If an invalid fork or philosopher index is given
	 */
	// Give a fork to a philosopher
	public void setForkTaken(int fork, int phil) throws IllegalArgumentException {
		if ((fork < 0) || (fork > num_phils - 1)) {
			throw new IllegalArgumentException("Invalid fork index");
		}
		if ((phil < 0) || (phil > num_phils - 1)) {
			throw new IllegalArgumentException("Invalid philosopher index");
		}
		if ((phil != fork) && (phil != (fork + 1) % num_phils)) {
			throw new IllegalArgumentException("Philosopher "+phil+" cannot take fork "+fork+"!");
		}
		System.out.println("Philosopher"+phil+" takes fork "+fork);
		forks[fork] = phil;
		tablePanel.repaint();
	}

	/**
	 * Place a fork back on the table
	 *
	 * @param     fork    The index of the fork (between 0 and n-1)
	 * @throws    IllegalArgumentException If an invalid fork index is given
	 */
	// Place a fork back on the table
	public void setForkAvailable(int fork) throws IllegalArgumentException {
		if ((fork < 0) || (fork > num_phils - 1)) {
			throw new IllegalArgumentException("Invalid fork index");
		}
		System.out.println("Philosopher "+forks[fork]+" relases fork "+fork);
		forks[fork] = -1;
		this.repaint();
	}

	/////////////////////////
	// PRIVATE PARTS BELOW //
	/////////////////////////

	static private final int size = 600;
	static private final int default_phil_radius=40;
	static private final int default_plate_radius=30;

	private int num_phils;     // number of philosophers
	private int forks[];       // array that stores while philosopher holds which fork
	private int phil_states[]; // states of the philosophers

	private JPanel topPanel;
	private TablePanel tablePanel;

	private JComboBox minThinkTime;
	private JComboBox maxThinkTime;
	private JComboBox minEatTime;
	private JComboBox maxEatTime;


	/**
	 * The actionPerformed  method, which should not be overriden or called directly.
	 */
	public void actionPerformed(ActionEvent event) {
		Component c = (Component)event.getSource();
		if (c == minThinkTime) {
			updateMaxComboBoxes(minThinkTime,maxThinkTime);
		} else if (c == maxThinkTime) {
			updateMinComboBoxes(maxThinkTime,minThinkTime);
		} else if (c == minEatTime) {
			updateMaxComboBoxes(minEatTime,maxEatTime);
		} else if (c == maxEatTime) {
			updateMinComboBoxes(maxEatTime,minEatTime);
		}
	}

	// Private useful method
	private void updateMaxComboBoxes(JComboBox clickedOn, JComboBox toUpdate) {
		toUpdate.removeActionListener(this);
		Integer oldSelection = (Integer)toUpdate.getSelectedItem();
		toUpdate.removeAllItems();
		for (int i=1+((Integer)clickedOn.getSelectedItem()).intValue(); i<=10; i++) {
			toUpdate.addItem(new Integer(i));
			if (i == oldSelection.intValue()) {
				toUpdate.setSelectedItem(i);
			}
		}
		if (toUpdate.getSelectedIndex() == -1)
			toUpdate.setSelectedIndex(0);
		toUpdate.addActionListener(this);
	}

	// Private useful method
	private void updateMinComboBoxes(JComboBox clickedOn, JComboBox toUpdate) {
		toUpdate.removeActionListener(this);
		Integer oldSelection = (Integer)toUpdate.getSelectedItem();
		toUpdate.removeAllItems();
		for (int i=1; i <= ((Integer)clickedOn.getSelectedItem()).intValue();  i++) {
			toUpdate.addItem(new Integer(i));
			if (i == oldSelection.intValue()) {
				toUpdate.setSelectedItem(i);
			}
		}
		if (toUpdate.getSelectedIndex() == -1)
			toUpdate.setSelectedIndex(0);
		toUpdate.addActionListener(this);
	}

	private class TablePanel extends JPanel {
		/**
		 * The paintComponent method, which should not be overriden or called directly.
		 */
		public void paintComponent(Graphics win) {

			int center_x = (DiningPhilosophersDisplay.size / 2);
			int center_y = (DiningPhilosophersDisplay.size / 2);
			int phil_location_radius =
				(int)(((DiningPhilosophersDisplay.size / 2) - DiningPhilosophersDisplay.default_phil_radius)*.90);
			int table_radius = (int)((phil_location_radius - default_phil_radius)*.90);
			int plate_location_radius =(int)((table_radius - default_plate_radius)*.90);

			int phil_radius =
				java.lang.Math.min(default_plate_radius,(int)(2*java.lang.Math.PI*phil_location_radius/(2*num_phils)));
			int plate_radius =
				java.lang.Math.min(default_plate_radius,(int)(2*java.lang.Math.PI*plate_location_radius/(2*num_phils)));

			// Draw the table
			win.setColor(Color.gray);
			draw_circle(win,Color.gray,center_x,center_y, table_radius);

			// Draw the plates and the philosophers
			for (int i=0; i<num_phils;i++) {
				// draw the plate
				draw_circle(win,Color.white,
						(int)(center_x+plate_location_radius*java.lang.Math.cos(i*2*java.lang.Math.PI/num_phils)),
						(int)(center_y+plate_location_radius*java.lang.Math.sin(i*2*java.lang.Math.PI/num_phils)),
						java.lang.Math.min(plate_radius,(int)(2*java.lang.Math.PI*plate_location_radius/(2*num_phils))));
				// pick the right color for the philosopher
				Color phil_color = Color.white; // white is the color of a philosopher in an invalid state
				switch (phil_states[i]) {
				case DiningPhilosophersDisplay.THINKING:
					phil_color = Color.blue;
					break;
				case DiningPhilosophersDisplay.HUNGRY:
					phil_color = Color.red;
					break;
				case DiningPhilosophersDisplay.EATING:
					phil_color = Color.green;
					break;
				}
				// draw the philosopher
				draw_circle(win,phil_color,
						(int)(center_x+phil_location_radius*java.lang.Math.cos(i*2*java.lang.Math.PI/num_phils)),
						(int)(center_y+phil_location_radius*java.lang.Math.sin(i*2*java.lang.Math.PI/num_phils)),
						java.lang.Math.min(phil_radius,(int)(2*java.lang.Math.PI*phil_location_radius/(2*num_phils))));
				// draw the philosopher number if there aren't too many
				win.setColor(Color.black);
				if (num_phils < 50) {
					win.drawString((new Integer(i)).toString(),
							(int)(center_x+phil_location_radius*java.lang.Math.cos(i*2*java.lang.Math.PI/num_phils)),
							(int)(center_y+phil_location_radius*java.lang.Math.sin(i*2*java.lang.Math.PI/num_phils)));
				}
			}
			// Draw the chopstick
			win.setColor(Color.black);
			for (int i=0;i<num_phils;i++) {
				if (forks[i] == -1) {
					win.drawLine(
							(int)(center_x+(plate_location_radius + plate_radius)*java.lang.Math.cos((i+.5)*2*java.lang.Math.PI/num_phils)),
							(int)(center_y+(plate_location_radius + plate_radius)*java.lang.Math.sin((i+.5)*2*java.lang.Math.PI/num_phils)),
							(int)(center_x+(plate_location_radius - plate_radius)*java.lang.Math.cos((i+.5)*2*java.lang.Math.PI/num_phils)),
							(int)(center_y+(plate_location_radius - plate_radius)*java.lang.Math.sin((i+.5)*2*java.lang.Math.PI/num_phils)));
				} else if (forks[i] == i) {
					win.drawLine(
							(int)(center_x+(plate_location_radius + plate_radius)*java.lang.Math.cos(+0.5*java.lang.Math.atan((float)plate_radius/(float)plate_location_radius)+(i)*2*java.lang.Math.PI/num_phils)),
							(int)(center_y+(plate_location_radius + plate_radius)*java.lang.Math.sin(+0.5*java.lang.Math.atan((float)plate_radius/(float)plate_location_radius)+i*2*java.lang.Math.PI/num_phils)),
							(int)(center_x+(plate_location_radius - plate_radius)*java.lang.Math.cos(+0.5*java.lang.Math.atan((float)plate_radius/(float)plate_location_radius)+i*2*java.lang.Math.PI/num_phils)),
							(int)(center_y+(plate_location_radius - plate_radius)*java.lang.Math.sin(+0.5*java.lang.Math.atan((float)plate_radius/(float)plate_location_radius)+i*2*java.lang.Math.PI/num_phils)));
				} else if (forks[i] == (i+1) % num_phils) {
					win.drawLine(
							(int)(center_x+(plate_location_radius + plate_radius)*java.lang.Math.cos(-0.5*java.lang.Math.atan((float)plate_radius/(float)plate_location_radius)+(i+1)*2*java.lang.Math.PI/num_phils)),
							(int)(center_y+(plate_location_radius + plate_radius)*java.lang.Math.sin(-0.5*java.lang.Math.atan((float)plate_radius/(float)plate_location_radius)+(i+1)*2*java.lang.Math.PI/num_phils)),
							(int)(center_x+(plate_location_radius - plate_radius)*java.lang.Math.cos(-0.5*java.lang.Math.atan((float)plate_radius/(float)plate_location_radius)+(i+1)*2*java.lang.Math.PI/num_phils)),
							(int)(center_y+(plate_location_radius - plate_radius)*java.lang.Math.sin(-0.5*java.lang.Math.atan((float)plate_radius/(float)plate_location_radius)+(i+1)*2*java.lang.Math.PI/num_phils)));
				} else {
					System.out.println("HERE");
				}
			}

			// draw the caption
			win.setColor(Color.blue);
			win.fillRect(30,size,30,20);
			win.setColor(Color.red);
			win.fillRect(180,size,30,20);
			win.setColor(Color.green);
			win.fillRect(330,size,30,20);


			win.setColor(Color.black);
			win.drawString("Thinking",70,size+15);
			win.drawString("Hungry",220,size+15);
			win.drawString("Eating",370,size+15);
		}

		// Private useful method
		private void draw_circle(Graphics win, Color color, int x, int y, int r) {
			win.setColor(color);
			win.fillOval((x-r),(y-r),(2*r),(2*r));
		}
	}
}