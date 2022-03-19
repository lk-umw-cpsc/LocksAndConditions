import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

/**
 * Simulates a producer and consumer using condition variables.
 * 
 * The producer fills the queue until it's full, at which point
 * it will wait until the queue is empty before proceeding.
 * 
 * The consumer waits until the queue is full, then starts
 * emptying the queue one at a time until it's empty. It then
 * waits for it to be full again.
 * 
 * This process goes on indefinitely.
 */
public class Conditions {

    private static Lock lock = new ReentrantLock();
    private static Condition fullCondition = lock.newCondition();
    private static Condition emptyCondition = lock.newCondition();

    private static int queued;
    private static final int CAPACITY = 10;

    public static void main(String[] args) {
        new Thread(Conditions::fill).start();
        new Thread(Conditions::empty).start();
    }

    /**
     * Fills until full, then waits until empty
     */
    private static void fill() {
        try {
            while (true) {
                lock.lock();
                while (queued > 0) {
                    emptyCondition.await();
                }
                
                while (queued < CAPACITY) {
                    queued++;
                    System.out.println("Enqueued a message; " + queued + " are queued.");
                    Thread.sleep(500);
                }
                fullCondition.signal();
                lock.unlock();
            }
        } catch (InterruptedException e) {}
    }

    /**
     * Empties until empty, then waits until full
     */
    private static void empty() {
        try {
            while (true) {
                lock.lock();
                while (queued < CAPACITY) {
                    fullCondition.await();
                }
                
                while (queued > 0) {
                    queued--;
                    System.out.println("Dequeued a message; " + queued + " are queued.");
                    Thread.sleep(500);
                }
                emptyCondition.signal();
                lock.unlock();
            }
        } catch (InterruptedException e) {}
    }
    
}
