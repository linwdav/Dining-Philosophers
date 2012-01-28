import java.awt.FlowLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.Vector;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.SwingUtilities;

/**
 * Responsible for creating the DiningPhilosophersDisplay.
 * @author David Lin
 */
@SuppressWarnings("serial")
public class DiningPhilosophersV3 extends JPanel implements ActionListener, Runnable {

  /* Display items. */
  private static int numPhils;
  private static int numSeconds;
  private static JFrame frame;
  private static DiningPhilosophersDisplay display;
  private static JButton runButton;
  private static JButton quitButton;
  private static JButton pauseButton;

  /* Thread handler. */
  private Vector<Philosopher> threadTable;

  /* Determine if start button should start or resume. */
  private boolean programStarted = false;

  /**
   * Create GUI items.
   */
  public DiningPhilosophersV3() {
    super();
    runButton = new JButton("Run");
    pauseButton = new JButton("Pause");
    quitButton = new JButton("Quit");
    display = new DiningPhilosophersDisplay(numPhils);
    runButton.addActionListener(this);
    pauseButton.addActionListener(this);
    quitButton.addActionListener(this);
    pauseButton.setEnabled(false);
    threadTable = new Vector<Philosopher>(0);
  }

  /**
   * Program start.
   * @param args Takes two positive integers as argument.
   */
  public static void main(String[] args) {
    // Validate input, there should only be two arguments.
    if (args.length != 2) {
      System.out.println("Usage: java DiningPhilosophersV1 <# of philosophers (2 or more)> "
          + "<# of seconds to run simulation (1 or more)>");
      System.exit(0);
    }

    // Validate input
    try {
      numPhils = Integer.parseInt(args[0]);
      numSeconds = Integer.parseInt(args[1]);
      if (numPhils < 2 || numSeconds < 1) {
        System.out.println("Usage: java DiningPhilosophersV1 <# of philosophers (2 or more)> "
            + "<# of seconds to run simulation> (1 or more)");
        System.exit(0);
      }
    }
    catch (NumberFormatException ex) {
      System.out.println("Usage: java DiningPhilosophersV1 <# of philosophers (2 or more)> "
          + "<# of seconds to run simulation> (1 or more)");
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
    frame.add(new DiningPhilosophersV3());
    frame.add(runButton);
    frame.add(pauseButton);
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
      //pauseButton.setEnabled(true);
      // Create philosopher threads only if program is started for first time.
      if (programStarted == false) {
        programStarted = true;
        // Spawn off new worker thread to keep it off EDT
        Thread worker = new Thread(this);
        worker.start();
      }
      // Otherwise resume from paused state
      else {
        for (int i = 0; i < numPhils; i++) {
          threadTable.get(i).setPaused(false);
          threadTable.get(i).interrupt();
        }
      }
    }
    else if (e.getSource() == pauseButton) {
      pauseButton.setEnabled(false);
      runButton.setEnabled(true);
      for (int i = 0; i < numPhils; i++) {
        threadTable.get(i).setPaused(true);
        threadTable.get(i).interrupt();
      }
    }
    // Exit DiningPhilosophers on exit button click
    else if (e.getSource() == quitButton) {
      if (programStarted == true) {
        // Interrupt threads
        for (int i = 0; i < numPhils; i++) {
          threadTable.get(i).setPaused(true);
          threadTable.get(i).interrupt();
        }
        printHungryTime();
      }
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
      threadTable.add(new Philosopher(phil, leftFork, rightFork, display, monitor));
      threadTable.get(i).start();
    }

    // Run simulation for specified amount of time
    try {
      Thread.sleep(numSeconds * 1000);
    }
    catch (InterruptedException e) {
      // do nothing
    }

    // Interrupt threads
    for (int i = 0; i < numPhils; i++) {
      threadTable.get(i).setPaused(true);
      threadTable.get(i).interrupt();
    }

    printHungryTime();
    System.exit(0);
  }

  /**
   * Print hungry time info.
   */
  private void printHungryTime() {
    long totalTime = 0;
    long minTime = 9999999;
    long maxTime = 0;

    for (int i = 0; i < numPhils; i++) {
      totalTime += threadTable.get(i).getHungryTime();
      if (threadTable.get(i).getHungryTime() < minTime) {
        minTime = threadTable.get(i).getHungryTime();
      }
      if (threadTable.get(i).getHungryTime() > maxTime) {
        maxTime = threadTable.get(i).getHungryTime();
      }
      System.out.println("Philosopher #" + i + " hungry time: "
          + (threadTable.get(i).getHungryTime() / 1000.0) + "sec.");
    }

    System.out.println("Average hungry time: " + (totalTime / numPhils / 1000.0) + "sec.");
    System.out.println("Spread of hungry time: " + ((maxTime - minTime) / 1000.0) + "sec.");
  }
}
