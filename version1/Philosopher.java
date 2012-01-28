import java.util.Random;

/**
 * Philosopher that thinks, picks up forks, eats, and puts down forks.
 * @author David Lin
 */
public class Philosopher extends Thread {

  /** Philosopher data items. */
  private int phil;
  private int leftFork;
  private int rightFork;
  private DiningPhilosophersDisplay display;
  private ForkMonitor monitor;
  private Random rand;

  /**
   * Initialize philosopher's data items.
   * @param phil Philosopher's index on table
   * @param leftFork Index of philosopher's left fork on table
   * @param rightFork Index of philosopher's right fork on table
   * @param display GUI accessor
   * @param monitor Fork monitor
   */
  public Philosopher(int phil, int leftFork, int rightFork, DiningPhilosophersDisplay display,
      ForkMonitor monitor) {
    this.phil = phil;
    this.leftFork = leftFork;
    this.rightFork = rightFork;
    this.display = display;
    this.monitor = monitor;
    this.rand = new Random();
  }

  /**
   * Cycle through philosopher states.
   */
  public void run() {
    while (true) {
      // Think
      display.setPhilosopherState(phil, DiningPhilosophersDisplay.THINKING);
      goSleep(getThinkTime());

      // Hungry, pick up left then right fork
      display.setPhilosopherState(phil, DiningPhilosophersDisplay.HUNGRY);
      monitor.pickupLeftFork(phil, leftFork);
      monitor.pickupRightFork(phil, rightFork);

      // Eat
      display.setPhilosopherState(phil, DiningPhilosophersDisplay.EATING);
      goSleep(getEatTime());

      // Put forks down
      monitor.putdownForks(leftFork, rightFork);
    }
  }

  /**
   * Get the amount of time to think between the user-selected min and max.
   * @return The time to think in milliseconds
   */
  private long getThinkTime() {
    int min = display.getMinThinkTime();
    int max = display.getMaxThinkTime();
    return (rand.nextInt(max - min + 1) + min) * 1000;
  }

  /**
   * Get the amount of time to eat between the user-selected min and max.
   * @return The time to eat in milliseconds
   */
  private long getEatTime() {
    int min = display.getMinEatTime();
    int max = display.getMaxEatTime();
    return (rand.nextInt(max - min + 1) + min) * 1000;
  }

  /**
   * Helper function for thread to sleep.
   * @param val Milliseconds to sleep
   */
  private void goSleep(long val) {
    try {
      Thread.sleep(val);
    }
    catch (InterruptedException e) {
      e.printStackTrace();
    }
  }
}
