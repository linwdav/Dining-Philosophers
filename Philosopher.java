import java.util.Random;

/**
 * Philosopher that thinks, picks up forks, eats, and puts down forks.
 * @author David Lin
 */
public class Philosopher extends Thread implements Runnable {

  /** Philosopher data items. */
  private int phil;
  private int leftFork;
  private int rightFork;
  private DiningPhilosophersDisplay display;
  private ForkMonitor monitor;
  private Random rand;
  private long hungryTime;

  private volatile boolean paused = false;

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
    this.hungryTime = 0;
  }

  /**
   * Cycle through philosopher states.
   */
  public void run() {
    while (true) {
      // Think
      checkPaused();
      display.setPhilosopherState(phil, DiningPhilosophersDisplay.THINKING);
      goSleep(getThinkTime());

      // Hungry, pick up left and right forks
      checkPaused();
      long startHungry = System.currentTimeMillis();
      display.setPhilosopherState(phil, DiningPhilosophersDisplay.HUNGRY);
      monitor.pickupForks(phil, leftFork, rightFork);
      long stopHungry = System.currentTimeMillis();
      hungryTime += stopHungry - startHungry;

      // Eat
      checkPaused();
      display.setPhilosopherState(phil, DiningPhilosophersDisplay.EATING);
      goSleep(getEatTime());

      // Put forks down
      checkPaused();
      monitor.putdownForks(leftFork, rightFork);
    }
  }

  /**
   * Get the amount of time to think between the user-selected min and max.
   * @return The time to think in milliseconds
   */
  private long getThinkTime() {
    int min = display.getMinThinkTime() * 1000;
    int max = display.getMaxThinkTime() * 1000;
    //return (rand.nextInt(max - min + 1) + min) * 1000;
    return (min + (long)(rand.nextDouble() * (max - min)));
  }

  /**
   * Get the amount of time to eat between the user-selected min and max.
   * @return The time to eat in milliseconds
   */
  private long getEatTime() {
    int min = display.getMinEatTime() * 1000;
    int max = display.getMaxEatTime() * 1000;
    //return (rand.nextInt(max - min + 1) + min) * 1000;
    return (min + (long)(rand.nextDouble() * (max - min)));
  }

  /**
   * Helper function for thread to sleep.
   * @param val Milliseconds to sleep
   */
  private void goSleep(long val) {
    long start = System.currentTimeMillis();
    try {
      Thread.sleep(val);
    }
    catch (InterruptedException e) {
      long stop = System.currentTimeMillis();
      checkPaused();
      long remainder = val - (stop - start);
      // Resume sleeping
      goSleep(remainder);
    }
  }

  /**
   * Check if program is paused.
   */
  private synchronized void checkPaused() {
    // Pause thread
    while (paused == true) {
      try {
        wait();
      }
      catch (InterruptedException e) {
        // do nothing
      }
    }
    // Resume thread
    if (paused == false) {
      notifyAll();
    }
  }

  /**
   * Set pause status.
   * @param val true to pause, false to resume
   */
  public void setPaused(boolean val) {
    paused = val;
  }

  /**
   * Return philosopher's hungry time.
   * @return hungry time in milliseconds
   */
  public long getHungryTime() {
    return hungryTime;
  }
}
