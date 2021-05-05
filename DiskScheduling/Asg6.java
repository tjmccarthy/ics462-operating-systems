/**This program implements algorithms used for OS Disk Scheduling. The Asg6 class performs the functions needed for
 * file input/output and calls the methods implementing each of the 6 algorithms. The Disk class and it's associated
 * functions contain the logic to manipulate the disk, cylinders and disk head.
 * @author Trevor McCarthy                                                                                        */

import java.io.*;
public class Asg6 {
    static FileWriter fWriter;
    static PrintWriter output;
    static int disk1Cylinders, disk2Cylinders, head1Pos, head2Pos;
    static String[] cylWithReq1, cylWithReq2;
    static boolean debug = false;
    public static void main(String[] args) {
        String fileOut = "Asg6_Trevor_McCarthy.txt", fileIn = "Asg6Data.txt", input = null;
        FileReader fr;
        BufferedReader bfReader;
        // Handle class instantiation and initializations necessary to read Asg6Data.txt and write to output file.
        try {
            fWriter = new FileWriter(fileOut, true);
            output = new PrintWriter(fWriter);
            fr = new FileReader(fileIn);
            bfReader = new BufferedReader(fr);

            // Use values in Asg6Data.txt to initialize variables needed to implement the 6 disk-scheduling algorithms.
            disk1Cylinders = Integer.parseInt(bfReader.readLine());
            head1Pos = Integer.parseInt(bfReader.readLine());
            input = bfReader.readLine();
            cylWithReq1 = input.split(" ");
            disk2Cylinders = Integer.parseInt(bfReader.readLine());
            head2Pos = Integer.parseInt(bfReader.readLine());
            input = bfReader.readLine();
            cylWithReq2 = input.split(" ");
            bfReader.close();
        } catch (IOException e) {
            System.out.println("File could not be found.");
        }
        output.println("Trevor McCarthy\nICS-462 Assignment #6\n");

        doWork(disk1Cylinders, head1Pos, cylWithReq1);
        doWork(disk2Cylinders, head2Pos, cylWithReq2);
        output.close();
    }

    /**
     * The doWork() is called once for each of the 2 different sets of cylinder scenarios. The six algorithms are called
     * for each set and the total movement per head is output to file.
     * @param diskCylinders The disk's cylinder count.
     * @param headPos       The position of the head
     * @param cylWithReq    The string array containing the cylinder numbers requesting service.  */
    public static void doWork(int diskCylinders, int headPos, String[] cylWithReq) {
        int fcfsMovement = runFCFS(new Disk(diskCylinders, headPos, cylWithReq));
        int sstfMovement = runSSTF(new Disk(diskCylinders, headPos, cylWithReq));
        int scanMovement = runSCAN(new Disk(diskCylinders, headPos, cylWithReq));
        int cScanMovement = runCSCAN(new Disk(diskCylinders, headPos, cylWithReq));
        int lookMovement = runLOOK(new Disk(diskCylinders, headPos, cylWithReq));

        System.out.println("For FCFS, the total head movement was " + fcfsMovement + " cylinders.\n" +
                "For SSTF, the total head movement was " + sstfMovement + " cylinders.\n" +
                "For SCAN, the total head movement was " + scanMovement + " cylinders.\n" +
                "For C-SCAN, the total head movement was " + cScanMovement + " cylinders.\n" +
                "For LOOK, the total head movement was " + lookMovement + " cylinders.\n");

        output.println("For FCFS, the total head movement was " + fcfsMovement + " cylinders.\n" +
                "For SSTF, the total head movement was " + sstfMovement + " cylinders.\n" +
                "For SCAN, the total head movement was " + scanMovement + " cylinders.\n" +
                "For C-SCAN, the total head movement was " + cScanMovement + " cylinders.\n" +
                "For LOOK, the total head movement was " + lookMovement + " cylinders.\n");
    }

    /**
     * The showArrayContents method returns comma separated values by index of the array passed.
     * @param theArray The array that holds the contents to be printed.
     * @return str The string representation of the array's values by index.                  */
    public static String showArrayContents(int[] theArray) {
        String str = "";
        for (int i = 0; i < theArray.length; i++) {
            str += theArray[i];
            if (i != (theArray.length - 1)) str += ", ";
        }
        return str;
    }

    /**
     * The runFCFS() function simulates the First-Come-First-Serve algorithm using the Disk object's properties.
     * @param theDisk The Disk object containing the cylinder count, position of head and listing of requests.
     * @return totalMovement The total movement or seek time needed to service all requests.                  */
    public static int runFCFS(Disk theDisk) {
        int head = theDisk.getHeadPos();                        // Calculate movement based on initial position of head.
        int totalMovement = 0;                                  // Store summation of moves made.

        for (int i = 0; i < theDisk.getRequests().length; i++) {
            totalMovement += Math.abs(head - theDisk.getRequestLoc(i));
            head = theDisk.getRequestLoc(i);
        }
        return totalMovement;
    }

    /**
     * The runSSTF function performs the shortest-seek-time-first algorithm using the information in the Disk object.
     * @param theDisk The object containing headLocation, total cylinders and array of requests.
     * @return totalMovement The total amount of moves the disk head makes servicing all requests.                 */
    public static int runSSTF(Disk theDisk) {
        int totalMovement = 0;                                  // Store summation of moves made.
        int[] reqDist = new int[theDisk.getRequests().length];  // Distances from initial head to requesting cylinder.
        int[] servSeq = new int[theDisk.getRequests().length];  // The request listing in order they are serviced.
        int shortestSeek = 0;

        for (int i = 0; i < theDisk.getRequests().length; i++) {
            servSeq[i] = theDisk.getRequestLoc(i);
            reqDist[i] = Math.abs(theDisk.getHeadPos() - theDisk.getRequestLoc(i));
        }

        // Set the servSeq order by finding the lowest value in reqDist for each cylinder location.
        for (int i = 0; i < theDisk.getRequests().length; i++) {
            for (int i2 = i + 1; i2 < theDisk.getRequests().length; i2++) {
                if (reqDist[i] > reqDist[i2]) {
                    shortestSeek = reqDist[i];
                    reqDist[i] = reqDist[i2];
                    reqDist[i2] = shortestSeek;

                    shortestSeek = servSeq[i];
                    servSeq[i] = servSeq[i2];
                    servSeq[i2] = shortestSeek;
                }
            }
        }
        // Iterate through the requests in order of shortest seek time.
        for (int i = 1; i < theDisk.getRequests().length; i++) {
            totalMovement += Math.abs(theDisk.getHeadPos() - servSeq[i]);
            theDisk.setHeadPos(servSeq[i]);
        }
        return totalMovement;
    }

    /**
     * The runSCAN function performs the assoiated disk-scheduling algorithm using the information in the Disk object.
     * @param theDisk The object containing headLocation, total cylinders and array of requests.
     * @return totalMovement The total amount of moves the disk head makes servicing all requests.                  */
    public static int runSCAN(Disk theDisk) {
        int[] requests = new int[theDisk.getRequests().length + 1];     // The request listing plus one.
        int totalMovement = 0;                                          // Store summation of moves made.

        for (int i = 0; i < requests.length - 1; i++) requests[i] = theDisk.getRequestLoc(i);

        // Store the value of head position in the requests array's last index and sort the array contents.
        requests[requests.length - 1] = theDisk.getHeadPos();
        Disk.sortRequests(requests);

        int maximum = requests[theDisk.getRequests().length];

        totalMovement = theDisk.getHeadPos() + maximum;
        return totalMovement;
    }

    /**
     * The runCSCAN function performs its disk-scheduling algorithm using the information in the Disk object.
     * @param theDisk The object containing headLocation, total cylinders and array of requests.
     * @return totalMovement The total amount of moves the disk head makes servicing all requests.         */
    public static int runCSCAN(Disk theDisk) {
        int totalMovement = 0, tmpIndex = 0, moves = 0;    // Store moves made and manage order requests get serviced.
        int headReference = theDisk.getHeadPos();

        // Get sizes to create arrays for requests coming from cylinders higher/lower than the current head position.
        int lowCount = 0, highCount = 0;
        for (int i = 0; i < theDisk.getRequests().length; i++) {
            if (theDisk.getRequestLoc(i) >= theDisk.getHeadPos()) highCount++;
            if (theDisk.getRequestLoc(i) < theDisk.getHeadPos()) lowCount++;
        }
        int[] lowPath = new int[lowCount];
        int[] highPath = new int[highCount];
        int[] serviceSeq = new int[lowCount + highCount];       // For organizing requests by lowest distance.

        // Allocate values to appropriate array.
        lowCount = 0;
        highCount = 0;
        for (int i = 0; i < theDisk.getRequests().length; i++) {
            if (theDisk.getRequestLoc(i) < theDisk.getHeadPos()) {
                lowPath[lowCount] = theDisk.getRequestLoc(i);
                lowCount++;
            }
            if (theDisk.getRequestLoc(i) >= theDisk.getHeadPos()) {
                highPath[highCount] = theDisk.getRequestLoc(i);
                highCount++;
            }
        }

        // Sort each of the arrays.
        Disk.sortRequests(lowPath);
        Disk.sortRequests(highPath);

        if (Math.abs(theDisk.getHeadPos() - theDisk.getCylinders() - 1) < Math.abs(theDisk.getHeadPos() - 0)) {
            int j = 1;
            for (int i = 0; i < highCount; i++) {
                serviceSeq[j] = highPath[i];
                tmpIndex++;
                j++;
            }

            serviceSeq[tmpIndex] = theDisk.getCylinders() - 1;
            serviceSeq[tmpIndex + 1] = 0;

            j = (highCount + 3);
            for (int i = 0; i < lowCount; i++) {
                serviceSeq[j] = lowPath[i];
            }
        } else {
            int j = 1;
            tmpIndex = 0;
            for (int i = (lowCount - 1); i >= 0; i--) {
                serviceSeq[j] = lowPath[i];
                j++;
            }
            serviceSeq[j] = 0;
            serviceSeq[j + 1] = theDisk.getCylinders() - 1;

            j = (lowCount + 3);
            for (int i = (highCount - 1); i >= 0; i--) {
                serviceSeq[j] = highPath[i];
            }
        }
        serviceSeq[0] = theDisk.getHeadPos();

        for (int i = 0; i < serviceSeq.length - 1; i++) {
            moves = Math.abs(serviceSeq[i + 1] - serviceSeq[i]);
            totalMovement += moves;
        }
        return totalMovement;
    }

    /**
     * The runLOOK function performs the associated disk-scheduling algorithm using the Disk object.
     * @param theDisk The object containing headLocation, total cylinders and array of requests.
     * @return totalMovement The total amount of moves the disk head makes servicing all requests.  */
    public static int runLOOK(Disk theDisk) {
        int head = 0;                                           // Calculate movement based on initial position of head.
        int totalMovement = 0;                                  // Store total moves made.
        int tmpIndex = 0;                                       // Indexing for head pathing.
        int[] serviceSeq;                                       // Order the head follows to service requests.

        int[] requests = new int[theDisk.getRequests().length + 1]; // Additional index to compare head with requests.
        requests[0] = theDisk.getHeadPos();
        serviceSeq = new int[requests.length];

        // Allocate values of cylinders with requests and sort the requests in ascending order.
        for (int i = 0; i < requests.length - 1; i++) {
            requests[i + 1] = theDisk.getRequestLoc(i);
        }
        Disk.sortRequests(requests);

        for (int i = 0; i < requests.length; i++) {
            if (theDisk.getHeadPos() == requests[i]) head = i;
        }

        // Determine initial direction the head is moving and service all requests found while going in that direction.
        // The absolute distance the head travels is the total movement of the head. The tmpIndex stores the request
        // that was serviced prior to the change of direction. Once one direction is completed the head changes
        // directions and addresses the requests that were on the opposite side of the initial position
        // of the head.
        int i2 = 0;
        if (theDisk.getCylinders() / 2 > theDisk.getHeadPos()) {
            for (int i = head; i >= 0; i--) {
                serviceSeq[tmpIndex] = requests[i];
                tmpIndex += 1;
                i2 = tmpIndex;
            }
            tmpIndex = i2;

            for (int i = (head + 1); i < requests.length; i++) {
                serviceSeq[tmpIndex] = requests[i];
                tmpIndex += 1;
            }
            totalMovement = (theDisk.getHeadPos() - requests[0]) + (requests[requests.length - 1] - requests[0]);
        } else {
            for (int i = head; i < requests.length; i++) {
                serviceSeq[tmpIndex] = requests[i];
                tmpIndex += 1;
                i2 = tmpIndex;
            }
            tmpIndex = i2;

            for (int i = (head - 1); i >= 0; i--) {
                serviceSeq[tmpIndex] = requests[i];
                tmpIndex += 1;
            }
            totalMovement = (requests[requests.length - 1] - theDisk.getHeadPos() +
                    (requests[requests.length - 1] - requests[0]));
        }
        return totalMovement;
    }

    /**
     * The runCLOOK function performs the associated disk-scheduling algorithm using the Disk object.
     * @param theDisk The object containing headLocation, total cylinders and array of requests.
     * @return totalMovement The total amount of moves the disk head makes servicing all requests.  */
    public static int runCLOOK(Disk theDisk) {
        int head = 0;                                           // Calculate movement based on initial position of head.
        int totalMovement = 0;                                  // Store total moves made.
        int tmpIndex = 0;                                       // Indexing for head pathing.
        int[] serviceSeq;                                       // Order the head follows to service requests.

        int[] requests = new int[theDisk.getRequests().length + 1]; // Additional index to compare head with requests.
        requests[0] = theDisk.getHeadPos();
        serviceSeq = new int[requests.length];

        // Allocate values of cylinders with requests and sort the requests in ascending order.
        for (int i = 0; i < requests.length - 1; i++) {
            requests[i + 1] = theDisk.getRequestLoc(i);
        }
        Disk.sortRequests(requests);

        for (int i = 0; i < requests.length; i++) {
            if (theDisk.getHeadPos() == requests[i]) head = i;
        }
        return 0;
    }
}