import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

/**
 * Two threads attempt to access a critical section: a counter variable.
 * 
 * A lock variable prevents the two from accessing the critical section
 * at the same time.
 */
public class Locks {

    private static final Lock COUNTER_LOCK = new ReentrantLock();

    private static int counter;
    private static int goal;

    /**
     * Increment counter *goal* times, then print out the number of times
     * counter was incremented by this thread
     */
    private static void criticalMethod() {
        int myIncrements;
        for (myIncrements = 0; myIncrements < goal; myIncrements++) {
            COUNTER_LOCK.lock();
            counter++;
            COUNTER_LOCK.unlock();
        }
        System.out.println("Finished, incrementing " + myIncrements + " times.");
    }

    /**
     * Alternate version of the counting algorithm (change method name in main to run)
     * 
     * Increment the counter by 1 until it reaches goal, acquiring the lock
     * prior to each increment and then unlocking after.
     * 
     * Prints the number of times the thread incremented the counter
     */
    private static void criticalMethodB() {
        int myIncrements = 0;
        while (counter < goal) {
            COUNTER_LOCK.lock();
            // prevent off-by-one error where other thread lets go of the lock
            // on the final increment and this thread ends up incrementing it again
            if (counter >= goal)
                break;
            counter++;
            myIncrements++;
            COUNTER_LOCK.unlock();
        }
        System.out.println("Finished, incrementing " + myIncrements + " times.");
    }

    public static void main(String[] args) {
        final int argc = args.length;
        if (argc < 1) {
            goal = 10000;
        } else {
            try {
                goal = Integer.parseInt(args[0]);
            } catch (NumberFormatException e) {
                System.out.println("Counter limit must be an int value");
                return;
            }
        }

        counter = 0;
        Thread a = new Thread(Locks::criticalMethodB);
        Thread b = new Thread(Locks::criticalMethodB);
        a.start();
        b.start();
        // Wait for children to finish
        try {
            a.join();
            b.join();
        } catch (InterruptedException e) {

        }
        System.out.println("Counter final value: " + counter);
    }

}