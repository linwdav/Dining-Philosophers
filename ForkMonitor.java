import java.util.Vector;

/**
 * Monitor that synchronizes access to forks in dining philosophers problem.
 * @author David Lin
 */
public class ForkMonitor {

  /** Monitor data items. */
  private DiningPhilosophersDisplay display;
  private int forks[];
  
  /** Philosopher queue, denoted by index. */
  private Vector<Integer> queue;

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
    queue = new Vector<Integer>(0);
  }

  /**
   * Pick up left fork when available.
   * @param phil Philosopher's index on table
   * @param leftFork Index of philosopher's left fork on table
   * @param rightFork Index of philosopher's right fork on table
   */
  public synchronized void pickupForks(int phil, int leftFork, int rightFork) {
    queue.add(phil);
    // Only philosophers at head of queue can try to pick up forks to eat
    while (phil != queue.get(0) || forks[leftFork] == 0 || forks[rightFork] == 0) {
      //printQueue();
      // If philosopher not at head can eat, move to head of queue
      if (forks[leftFork] == 1 && forks[rightFork] == 1) {
        for (int i = 0; i < queue.size(); i++) {
          if (queue.get(i) == phil) {
            queue.remove(i);
            break;
          }
        }
        queue.add(0, phil);
        //System.out.println("PHILOSOPHER #" + phil + " JUMPING TO HEAD OF QUEUE");
      }
      else {
        try {
          wait();
        }
        catch (InterruptedException e) {
          return;
        }
      }
    }
    
    // Philosopher can eat so remove from queue
    if (phil == queue.get(0)) {
      queue.remove(0);
      // New philosopher arrives at head of queue, give it opportunity to eat
      notifyAll();
    }
    
    forks[leftFork] = 0;
    forks[rightFork] = 0;
    display.setForkTaken(leftFork, phil);
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
    // Finished eating so give philosopher at head of queue opportunity to eat
    notifyAll();
  }

  /**
   * Print the queue of philosophers.
   */
  public void printQueue() {
    System.out.print("Queue: ");
    for (int i = 0; i < queue.size(); i++) {
      System.out.print(queue.get(i) + " ");
    }
    System.out.println();
  }
}
