/**This program is based on problem 7.28 from the textbook involving main memory. Thr program consists of a function
 * that is passed a virtual address in decimal, and outputs the page number and offset for the given address. It is
 * assumed that the system has a 32-bit virtual memory address with a 4-KB page size.     
 * @author Trevor McCarthy                                                                                        */

import java.io.*;
public class PagingEx {
    static FileWriter fWriter;                  // Required for outputting results to file.
    static PrintWriter output;                  // Required for outputting results to file.
    static int[] unsignedIntArray;              // Used to store the parsed unsigned int values of the addresses.
    static int pageNumber, offset;              // Used to store the results calculated in the solve() function below.
    static boolean debug = false;               // For verifications

    public static void main(String[] args) {
        String fileOut = "Asg4_Trevor_McCarthy.txt";    // To store results to text file.

        // Handle class instantiations relating to writing output to file.
        try {
            fWriter = new FileWriter(fileOut, true);
            output = new PrintWriter(fWriter);
        } catch (IOException e) {
            System.out.println("File could not be created.");
        }
        output.println("Trevor McCarthy\nICS-462 Assignment #4\nNovember 4, 2020\n");
        String[] testAddresses = {"19986", "347892", "5978"};
        solve(testAddresses);
        output.close();
    }

    /**
     * The solve() method contains the logic necessary to produce a solution to problem 7.28. The arithmetic performed
     * in the loop uses the Integer classes static methods to parse and perform unsigned division/modulo math on the
     * contents of the array passed. Computes the values for the page number/offset and outputs the results to file.
     * @param virtMemAddress The string array holding the test values.    */
    public static void solve(String[] virtMemAddress) {

        // Allocate array that will hold the unsigned int addresses.
        unsignedIntArray = new int[virtMemAddress.length];

        // Calculate and output results for each iteration.
        for (int i = 0; i < virtMemAddress.length; i++){
            unsignedIntArray[i] = Integer.parseUnsignedInt(virtMemAddress[i]);
            pageNumber = Integer.divideUnsigned(unsignedIntArray[i], 4096);
            offset = Integer.remainderUnsigned(unsignedIntArray[i], 4096);

            output.println("The address " + Integer.toUnsignedString(unsignedIntArray[i]) + " is in:\nPage number = " +
                    Integer.toUnsignedString(pageNumber) + "\nOffset = " + offset + "\n");
            System.out.println("The address " + Integer.toUnsignedString(unsignedIntArray[i]) +
                    " is in:\nPage number = " + Integer.toUnsignedString(pageNumber) + "\nOffset = " +
                    offset + "\n");
        }
    }
}