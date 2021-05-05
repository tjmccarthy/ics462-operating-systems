/**To be used in conjunction with Asg3.java, CircularBuffer.java and Consumer.java.
 * This program is an extension of assignment #2. The producer and consumer share an integer array (length=5)
 * which is a circular buffer. They share 1-2 variables to coordinate placing/removing items in the circular buffer.
 * @author Trevor McCarthy                                                                                        */

import java.io.FileWriter;
import java.io.IOException;
import java.util.Random;
import java.io.PrintWriter;

public class Producer extends Thread {
    protected CircularBuffer sharedBuffer;
    protected Random randWait;
    protected int itemCount;
    String filename = "Asg3_Trevor_McCarthy.txt";
    FileWriter fWriter;
    PrintWriter fileOut;

    public Producer(CircularBuffer sharedBuffer, int n) {
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
            // Random wait for 1-5 seconds.
            randWait = new Random();
            int waitValue = randWait.nextInt(6) + 1;
            System.out.println("Producer's waitValue = " + waitValue);

            // Error check.
            try {
                sleep((waitValue *1000));
            } catch (InterruptedException e) {
                e.printStackTrace();
            }

            while (sharedBuffer.isFull()) {
                System.out.println("Producer waiting for buffer space...");
                fileOut.println("Producer waiting for available buffer space.");
                try {
                    sleep(1000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }

            System.out.println("Producer adding to shared buffer: " + i);
            sharedBuffer.bufferIn(new Integer(i));
        }
        sharedBuffer.bufferIn(new Integer(-1));                     // Flag to communicate producer is done.
        fileOut.close();
    }
}