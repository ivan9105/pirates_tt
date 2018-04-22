package deadlock;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

//guaranteed deadlock
public class Deadlock {
    private static final Object monitor1 = new Object();
    private static final Object monitor2 = new Object();

    public static void main(final String[] args) throws InterruptedException {
        List<Thread> threads = new ArrayList<>();
        threads.add(new Thread(Deadlock::handler1));
        threads.add(new Thread(Deadlock::handler2));
        Collections.shuffle(threads);
        threads.get(0).start();
        //it's sleep must be less then sleep in threads
        Thread.sleep(1000);
        threads.get(1).start();
    }

    private static void handler1() {
        synchronized (monitor1) {
            System.out.println("Seize monitor1");
            try {
                Thread.sleep(3000);
            } catch (InterruptedException ignore) {
                //too long operation
            }
            synchronized (monitor2) {
                System.out.println("Hello from handler1");
            }
        }
    }

    private static void handler2() {
        synchronized (monitor2) {
            System.out.println("Seize monitor2");
            try {
                Thread.sleep(3000);
            } catch (InterruptedException e) {
                //too long operation
            }
            synchronized (monitor1) {
                System.out.println("Hello from handler2");
            }
        }
    }
}
