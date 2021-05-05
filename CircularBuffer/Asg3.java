/**To be used in conjunction with CircularBuffer.java, Producer.java and Consumer.java
 * This program is an extension of assignment #2. The producer and consumer share an integer array (length=5)
 * which is a circular buffer. They share 1-2 variables to coordinate placing/removing items in the circular buffer.
 * @author Trevor McCarthy                                                                                        */

import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.Random;

public class Asg3 {
    static final boolean APPEND = true;
    protected static final int BUFFER_SIZE = 5;

    public static void main(String[] args) throws IOException {
        String filename = "Asg3_Trevor_McCarthy.txt";
        FileWriter fWriter = new FileWriter(filename, APPEND);
        PrintWriter fileOut = new PrintWriter(fWriter);
        CircularBuffer circBuffer = new CircularBuffer(BUFFER_SIZE);
        fileOut.println("Trevor McCarthy\nICS-462 Assignment #3\n");

        new Producer(circBuffer, 100).start();
        new Consumer(circBuffer, 100).start();
        fileOut.close();
    }
}