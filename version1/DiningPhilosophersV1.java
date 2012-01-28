import java.awt.FlowLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.SwingUtilities;

/**
 * Responsible for creating the DiningPhilosophersDisplay.
 * @author David Lin
 */
@SuppressWarnings("serial")
public class DiningPhilosophersV1 extends JPanel implements ActionListener, Runnable {

  /* Display items. */
  private static int                       numPhils;
  private static JFrame                    frame;
  private static DiningPhilosophersDisplay display;
  private static JButton                   runButton;
  private static JButton                   quitButton;

  /**
   * Create GUI items.
   */
  public DiningPhilosophersV1() {
    super();
    runButton = new JButton("Run");
    quitButton = new JButton("Quit");
    display = new DiningPhilosophersDisplay(numPhils);
    runButton.addActionListener(this);
    quitButton.addActionListener(this);
  }

  /**
   * Program start.
   * @param args Takes one positive integer as argument.
   */
  public static void main(String[] args) {
    // Validate input, there should only be one argument.
    if (args.length != 1) {
      System.out.println("Usage: java DiningPhilosophersV1 <# of philosophers (2 or more)>");
      System.exit(0);
    }

    // Validate input, argument should be a positive integer greater than 1.
    try {
      numPhils = Integer.parseInt(args[0]);
      if (numPhils < 2) {
        System.out.println("Usage: java DiningPhilosophersV1 <# of philosophers (2 or more)>");
        System.exit(0);
      }
    }
    catch (NumberFormatException ex) {
      System.out.println("Usage: java DiningPhilosophersV1 <# of philosophers (2 or more)>");
      System.exit(0);
    }

    // Create and show GUI.
    SwingUtilities.invokeLater(new Runnable() {
      public void run() {
        createAndShowGUI();
      }
    });
  }

  /**
   * Create and show GUI.
   */
  public static void createAndShowGUI() {
    frame = new JFrame();
    frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
    frame.setLayout(new FlowLayout());

    frame.add(new DiningPhilosophersV1());
    frame.add(runButton);
    frame.add(quitButton);
    frame.add(display);

    frame.pack();
    frame.setVisible(true);
  }

  /**
   * Run on GUI button click.
   */
  @Override
  public void actionPerformed(ActionEvent e) {
    // Run DiningPhilosophers on start button click
    if (e.getSource() == runButton) {
      runButton.setEnabled(false);
      // Spawn off new worker thread to keep it off EDT
      Thread worker = new Thread(this);
      worker.start();
    }
    // Exit DiningPhilosophers on exit button click
    else if (e.getSource() == quitButton) {
      System.exit(0);
    }
  }

  /**
   * Worker thread that spawns off philosopher threads.
   */
  @Override
  public void run() {
    // Create a monitor for handling the forks
    ForkMonitor monitor = new ForkMonitor(display, numPhils);

    // Create a thread for each philosopher
    for (int i = 0; i < numPhils; i++) {
      // Set up parameters
      int phil = i;
      int leftFork = i;
      int rightFork = (phil + numPhils - 1) % numPhils;
      Philosopher thread = new Philosopher(phil, leftFork, rightFork, display, monitor);
      thread.start();
    }
  }

}
