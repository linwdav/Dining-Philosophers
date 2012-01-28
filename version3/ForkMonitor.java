/**
 * Monitor that synchronizes access to forks in dining philosophers problem.
 * @author David Lin
 */
public class ForkMonitor {

  /** Monitor data items. */
  private DiningPhilosophersDisplay display;
  private int forks[];

  /**
   * Initialize monitor data items.
   * @param display GUI accessor
   * @param numForks Number of forks on table
   */
  public ForkMonitor(DiningPhilosophersDisplay display, int numForks) {
    this.display = display;
    forks = new int[numForks];
    for (int i = 0; i < forks.length; i++) {
      // 1 = available, 0 = taken
      forks[i] = 1;
    }
  }

  /**
   * Pick up left fork when available.
   * @param phil Philosopher's index on table
   * @param leftFork Index of philosopher's left fork on table
   */
  public synchronized void pickupLeftFork(int phil, int leftFork) {
    while (forks[leftFork] == 0) {
      try {
        wait();
      }
      catch (InterruptedException e) {
        return;
      }
    }
    forks[leftFork] = 0;
    display.setForkTaken(leftFork, phil);
  }
  
  /**
   * Pick up right fork when available.
   * @param phil Philosopher's index on table
   * @param rightFork Index of philosopher's right fork on table
   */
  public synchronized void pickupRightFork(int phil, int rightFork) {
    while (forks[rightFork] == 0) {
      try {
        wait();
      }
      catch (InterruptedException e) {
        return;
      }
    }
    forks[rightFork] = 0;
    display.setForkTaken(rightFork, phil);
  }

  /**
   * Put down left and right forks.
   * @param phil Philosopher's index on table
   * @param leftFork Index of philosopher's left fork on table
   * @param rightFork Index of philosopher's right fork on table
   */
  public synchronized void putdownForks(int leftFork, int rightFork) {
    forks[leftFork] = 1;
    forks[rightFork] = 1;
    display.setForkAvailable(leftFork);
    display.setForkAvailable(rightFork);
    notifyAll();
  }

}
