/**This program serves as the main method and provides a means to create the output file.
 * To be used in conjunction with Consumer and Producer files.
 * @author Trevor McCarthy                                                             */

import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;


public class Asg2 {
    static final boolean APPEND = true;
    public static int totalRuns = 0;
    public static Integer summation = new Integer(0);

    public static void main(String[] args) throws IOException {
        String filename = "Asg2_Trevor_McCarthy.txt";
        FileWriter fWriter = new FileWriter(filename, APPEND);
        PrintWriter fileOut = new PrintWriter(fWriter);
        boolean done;

        fileOut.println("Trevor McCarthy\nICS-462 Assignment #2\n");

        do {
            Integer sharedVariable = new Integer(100);
            done = false;
            fileOut.print("The sum is ");

            new Producer(sharedVariable).start();
            new Consumer(sharedVariable).start();


            fileOut.println(sharedVariable + "\n");
            totalRuns++;
            if (totalRuns == 2) {
                done = true;
            }

        } while (!done);

        fileOut.close();
    }

    /**
     * Synchronized instance method that returns the value of the summation variable.
     * Effectively locks this object while the thread is in exclusive possession.
     * @return Value the object references.                                        */
    static synchronized Integer getSummation() {
        return summation;
    }

    /**
     * Synchronized instance method for updating the summation variable. Only one thread
     * can execute the statement and update the sum at any given time.
     * @param i Value used to perform the update.                                     */
    static synchronized void updateSum(Integer i) {
        Integer newSum = new Integer(getSummation().intValue() + i.intValue());
        summation = new Integer(newSum);
    }
}