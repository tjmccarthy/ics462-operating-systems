/**This program implements a handful of common eviction policies an OS can use. Specifically the First-In-First-Out,
 * Least-Recently-Used, and Optimal Page algorithms from Chapter 8. A random page-reference of size 20 consisting of
 * page numbers 0-9 is applied to each algorithm. For each algorithm, the page frames will be processed from 1 to 7
 * computing the page fault for each frame number. For demand paging, the first time a page comes in, its counted as
 * a page fault. The same procedure is then performed on the 2 assigned page-reference strings. 
 * @author Trevor McCarthy                                                                                        */

import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.Random;

public class Asg5 {
    static String filename;
    static FileWriter fWriter;
    static PrintWriter fileOut;
    static final int PAGE_TOTAL = 20;
    static final int FRAME_TOTAL = 7;
    static boolean debug = false;

    public static void main(String[] args) throws IOException {
        int[] randomPageRef = new int[PAGE_TOTAL];                      // Container for random page-reference string.
        int[] assignedPageRef1 = {0, 7, 0, 1, 2, 0, 8, 9, 0, 3, 0, 4, 5, 6, 7, 0, 8, 9, 1, 2};  // Page-ref provided.
        int[] assignedPageRef2 = {7, 0, 1, 2, 0, 3, 0, 4, 2, 3, 0, 3, 2, 1, 2, 0, 1, 7, 0, 1};  // Page-ref provided.

        // Output the assignment heading requirements to file.
        filename = "Asg5_Trevor_McCarthy.txt";
        fWriter = new FileWriter(filename, true);
        fileOut = new PrintWriter(fWriter);
        fileOut.println("Trevor McCarthy\nICS-462 Assignment #5\n");

        // Initialize random page-reference string.
        Random rand = new Random();                                     // For populating the following array.
        for (int i = 0; i < PAGE_TOTAL; i++) randomPageRef[i] = rand.nextInt(10);

        // Run algorithms 7 times for each of the page-reference stings and print results.
        doWork(assignedPageRef1);
        doWork(assignedPageRef2);
        doWork(randomPageRef);
        fileOut.close();
    }

    /**
     * The doWork() is called once for each of the 3 different page-reference strings. The method calls each of the
     * algorithm's functions for each iteration (1-7) and outputs their results.
     * @param pageRefString The listing of pages referenced.   */
    public static void doWork(int[] pageRefString){
        for (int i = 1; i < FRAME_TOTAL+1; i++) {
            System.out.print("For " + i + " page frames, and using string page reference string: " +
                    showPageRefString(pageRefString) + "\n");
            fileOut.println("For " + i + " page frames, and using string page reference string: " +
                    showPageRefString(pageRefString));

            // Run algorithms for each iteration of i and store fault results.
            int fifoFaults = runFIFO(i, pageRefString);
            int lruFaults = runLRU(i, pageRefString);
            int optimalFaults = runOPTIMAL(i, pageRefString);

            System.out.println("\tFIFO had " + fifoFaults + " page faults.\n\tLRU had " + lruFaults +
                    " page faults.\n\tOptimal had " + optimalFaults + " page faults.\n");
            fileOut.println("\tFIFO had " + fifoFaults + " page faults.\n\tLRU had " + lruFaults +
                    " page faults.\n\tOptimal had " + optimalFaults + " page faults.\n");
        }
    }

    /**
     * The runFIFO() function initializes the frame array and uses the values inside the pageRefString to represent
     * the pages being checked and added to the frame. The index location of where the page is inserted follows the
     * first-in-first-out standard by increasing the index number by one following the insert of a page.
     * @param pageFrames The total space available for pages within the frame.
     * @param pageRefString The listing of all pages that will be requested from memory.
     * @return faults The total number of faults for a frame of the current size.     */
    public static int runFIFO(int pageFrames, int[] pageRefString) {
        int[] frames;                       // Logic to track the active state of the page-frame.
        int faults = 0;                     // Track fault count to return.
        int pgIndex = 0;                    // Handle indexing of pageRefString array.
        int frIndex = 0;                    // Modulo math for inserting into frame index in ascending order.

        // Create representation of memory, initialize with -1 to represent empty page-frame.
        frames = new int[pageFrames];
        for (int i = 0; i < pageFrames; i++) frames[i] = -1;

        // Iterate over entire page-frame reference string counting tracking faults when page is not currently in frame.
        while (pgIndex < pageRefString.length) {
            if (!contains(pageRefString[pgIndex], frames)) {
                faults += 1;
                if (debug) System.out.println("FIFO Page fault # " + faults + " for value " + pageRefString[pgIndex] +
                        " Current state of frames array: " + showPageRefString(frames));
                frames[frIndex] = pageRefString[pgIndex];

                // Set the next frame where the page next up for eviction resides.
                int newIndex = frIndex + 1;
                frIndex = newIndex % pageFrames;
            }
            pgIndex += 1;
        }
        return faults;
    }

    /**
     * The runLRU() function initializes the frame array and uses the values inside the pageRefString to represent
     * the pages being checked and added to the frame. The index location of where the page is inserted is determined
     * by the eviction behavior handled by the lruEvict() helper method.
     * @param pageFrames The total space available for pages within the frame.
     * @param pageRefString The listing of all pages that will be requested from memory.
     * @return faults The total number of faults for a frame of the current size.     */
    public static int runLRU(int pageFrames, int[] pageRefString) {
        // Create representation of memory, initialize with -1 to represent empty page-frame.
        int[] frames = new int[pageFrames];
        for (int i = 0; i < pageFrames; i++) frames[i] = -1;
        int faults = 0, pgIndex = 0;                // Track fault count to return and place page locations by index.

        // Iterate over the entire page-frame reference string counting page faults for each time a page is added to the
        // frame.
        while (pgIndex < pageRefString.length) {
            if (!contains(pageRefString[pgIndex], frames)) {
                faults += 1;
                if (debug) System.out.println("LRU Page fault # " + faults + " for value " + pageRefString[pgIndex] +
                        " Current state of frames array: " + showPageRefString(frames));
                
                int pageLoc = lruEvict(pgIndex, frames, pageRefString);
                frames[pageLoc] = pageRefString[pgIndex];
            }
            pgIndex++;
        }
        return faults;
    }

    /**
     * The runOPTIMAL() function initializes the frame array and uses the values inside the pageRefString to represent
     * the pages being checked and added to the frame. The eviction behavior is handled by the optEvict() helper.
     * @param pageFrames The total space available for pages within the frame.
     * @param pageRefString The listing of all pages that will be requested from memory.
     * @return faults The total number of faults for a frame of the current size.  */
    public static int runOPTIMAL(int pageFrames, int[] pageRefString) {
        // Create representation of memory, initialize with -1 to represent empty page-frame.
        int[] frames = new int[pageFrames];
        for (int i = 0; i < pageFrames; i++) frames[i] = -1;

        int faults = 0, pgIndex = 0;                // Track fault count to return and place page locations by index.
        // Iterate over the entire page-frame reference string counting page faults for each time a page is added to the
        // frame.
        while (pgIndex < pageRefString.length) {
            if (!contains(pageRefString[pgIndex], frames)) {
                faults += 1;

                int pageLoc = optEvict(pgIndex, frames, pageRefString);
                frames[pageLoc] = pageRefString[pgIndex];
            }
            pgIndex++;
        }
        return faults;
    }

    /**
     * The lruEvict() function checks the current frame to find best location the upcoming page will be inserted into.
     * If there exist a value on the current frame that is not present in the pageRefString, that index is chosen for
     * eviction. Otherwise the index with the value that appears furthest into the pageRefString array is selected.
     * @param currentIndex The integer representing which element of the pageRefString is being inserted into frame.
     * @param frames The array representing the page-frame's current state.
     * @param pageRefString The array containing the sequence of all pages to be added.
     * @return The integer representing the location of the frame where the next page is to be placed.   */
    public static int lruEvict(int currentIndex, int[] frames, int[] pageRefString) {
        int toReturn = 0;
        boolean checked = false;
        int[] check = new int[frames.length];
        for (int i = 0; i < frames.length; i++) check[i] = frames[i];
        // The outer loop covers all indexes that span the frames current size and gets each index compared with the
        // values in the pageRefString starting with the index containing the most recent value added to the frame.
        // The comparison works back to the first element in the pageRefString until the value of the frames current
        // index is found in the pageRefString. The check[] array's value at the pageRefString's found index gets lower
        // as the inner loop iterates causing the values in the check[] index to decrease. This leaves the check[]
        // values to reflect the least recently used values.
        for (int i1 = 0; i1 < frames.length; i1++) {
            for (int i2 = (currentIndex - 1); i2 >= 0; i2--) {
                if (check[i1] == pageRefString[i2]) {
                    check[i1] = i2;
                    checked = true;
                    break;
                }
            }
            if (!checked) check[i1] = -1;
        }
        // The index of check[] with the lowest value stored has its index number returned. This number corresponds to
        // the frames[] index that was evicted to make room for the value that caused the most recent page fault.
        for (int i = 0; i < frames.length; i++) {
            if (debug) System.out.println("If check[" + i + "] < check[" + toReturn + "] or " + check[i] + " < " +
                    check[toReturn] + ", then next page is being inserted into location # " + i);
            if (check[i] < check[toReturn]) toReturn = i;
        }
        if (debug) System.out.println("In lruEvict(), the check[] array's final state is:\n" + showPageRefString(check));
        return toReturn;
    }

    /**
     * The optEvict() function checks the current frame to find best location the upcoming page will be inserted into.
     * If there exist a value on the current frame that is not present in the pageRefString, that index is chosen for
     * eviction. Otherwise the index with the value that appears furthest into the pageRefString array is selected.
     * @param currentIndex The integer representing which element of the pageRefString is being inserted into frame.
     * @param frames The array representing the page-frame's current state.
     * @param pageRefString The array containing the sequence of all pages to be added.
     * @return The integer representing the location of the frame where the next page is to be placed.   */
    public static int optEvict(int currentIndex, int[] frames, int[] pageRefString) {
        int toReturn = 0;
        boolean checked = false;
        int[] check = new int[frames.length];
        for (int i = 0; i < frames.length; i++) check[i] = frames[i];
        // The check array is initially a copy of the frames array. The values in each index of check are replaced with
        // the value of the first index number in pageRefString that matches check[i]. If no indexes in pageRefString
        // match, as in the case with an vacant page-frame (-1) the check[i] gets assigned the highest value an index
        // can be assigned (length of the pageRefString) for a comparison run after the both inner and outer loops are
        // complete.
        for (int i1 = 0; i1 < frames.length; i1++) {
            for (int i2 = (currentIndex + 1); i2 < pageRefString.length; i2++) {
                if (check[i1] == pageRefString[i2]) {
                    check[i1] = i2;
                    checked = true;
                    break;
                }
            }
            if (!checked) check[i1] = pageRefString.length;
        }
        // Each index in check array is compared to the value in check[0] to find the first index with a higher value
        // (indicating placement of the page being evicted as further into the frame array than the 0th index. Value
        // returned represents the frame to which the next page is added to.
        for (int i = 0; i < frames.length; i++) {
            if (debug) System.out.println("If check[" + i + "] > check[" + toReturn + "] or " + check[i] + " > " +
                    check[toReturn] + ", then next page is being inserted into location # " + i);
            if (check[i] > check[toReturn]) toReturn = i;
        }
        return toReturn;
    }

    /**
     * The contains() function checks the pages existing in the frame for one matching the value passed.
     * @param pgToFind The integer representation of the value stored in the page.
     * @param frToSearch The array representing the page-frame to be searched.
     * @return True upon first frame containing a page with matching value, otherwise false.  */
    public static boolean contains(int pgToFind, int[] frToSearch){
        for (int i = 0; i < frToSearch.length; i++) {
            if (frToSearch[i] == pgToFind) return true;
        }
        return false;
    }

    /**
     * The showPageRefString method returns information about the page-reference string passed.
     * @param pgRefString The array containing the contents of the page-reference string.
     * @return str The string representation of the array passed.    */
    public static String showPageRefString(int[] pgRefString) {
        String str = "";
        for (int i = 0; i < pgRefString.length; i++) {
            str += pgRefString[i];
            if (i != (pgRefString.length - 1)) str +=  ", ";
        }
        return str;
    }
}