/**To be used in conjunction with Asg2.java and Producer.java.
 * This program demonstrates some of the fundamental aspects of concurrent programming where a
 * computation is performed between two threads using a shared variable.
 * @author Trevor McCarthy                                                                  */

import java.util.Random;

public class Consumer extends Thread {
    protected Integer sharedCount;
    protected int waitValue;
    protected Random rand;

    public Consumer(Integer sharedCount) {
        this.sharedCount = sharedCount;
    }

    public void run() {
        for (int i = 0; i <= 4; i++) {

            // Random wait for 1-3 seconds.
            rand = new Random();
            waitValue = rand.nextInt(4) + 1;
            System.out.println("Consumer's waitValue = " + waitValue);

            // Error check
            try {
                sleep(waitValue);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }

            // Create new Integer with a negative value to consume when performing summation.
            Integer newInt = (-(this.sharedCount) + i);
            System.out.println("Consumer writing to shared variable + i  = " + i);
            Asg2.updateSum(newInt);
        }
    }
}

