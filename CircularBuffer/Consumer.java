/**To be used in conjunction with Asg3.java, CircularBuffer.java and Producer.java.
 * This program is an extension of assignment #2. The producer and consumer share an integer array (length=5)
 * which is a circular buffer. They share 1-2 variables to coordinate placing/removing items in the circular buffer.
 * @author Trevor McCarthy                                                                                        */

import java.io.FileWriter;
import java.io.IOException;
import java.util.Random;
import java.io.PrintWriter;

public class Consumer extends Thread {
    protected CircularBuffer sharedBuffer;
    protected Random randWait;
    protected int itemCount;
    String filename = "Asg3_Trevor_McCarthy.txt";
    FileWriter fWriter;
    PrintWriter fileOut;
    public Consumer(CircularBuffer sharedBuffer, int n) {
        this.sharedBuffer = sharedBuffer;
        itemCount = n;
    }

    public void run() {
        for (int i = 1; i <= itemCount; i++) {
            try {
                fWriter = new FileWriter(filename, true);
            } catch (IOException e) {
                e.printStackTrace();
            }

            fileOut = new PrintWriter(fWriter);
            // Random wait for 2-5 seconds.
            randWait = new Random();
            int waitValue = randWait.nextInt(6) + 2;
            System.out.println("Consumer's waitValue = " + waitValue);

            // Error check
            try {
                sleep((waitValue*1000));
            } catch (InterruptedException e) {
                e.printStackTrace();
            }

            while (sharedBuffer.isEmpty() && sharedBuffer.getCounter() != -1) {
                System.out.println("Consumer waiting.");
                fileOut.println("Consumer waiting.");
                try {
                    sleep(1000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }

            if (sharedBuffer.getCounter() != -1) {
                Object toConsume = sharedBuffer.bufferOut();
                System.out.println("Consumer reads from the circular buffer: " + toConsume);
                fileOut.println("Consumer writing item retrieved from shared buffer: " + toConsume);
            } else {
                System.out.println("Consumer Done.");
                fileOut.println("Consumer Done.");
            }
        }
        fileOut.close();
    }
}