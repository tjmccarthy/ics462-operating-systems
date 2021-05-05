/**Disk.java is to be used in conjunction with theAsg6.java file to implement the 6 disk-scheduling
 * algorithms required for ICS462's 6th programming assignment. The Disk class contains all relevant information
 * pertaining to the scheduling of IO requests. It also contains utility methods necessary for the 5 associated
 * disk-scheduling algorithms to efficiently make use of the Disk attributes. These include an implementation of
 * the QuickSorting algorithm for use with a binary search.
 * @author Trevor McCarthy                                                                                    */

 public class Disk {
    int cylinders, headPos;
    int[] reqListing;
    int[] cylRef;

    public Disk() {
        cylinders = 0;
        headPos = 0;
        reqListing = new int[0];
        cylRef = new int[0];
    }

    public Disk(int cylinders, int headPos, String[] requests) {
        this.cylinders = cylinders;
        this.headPos = headPos;
        reqListing = new int[requests.length];          // Create an integer listing of the request sequence.
        cylRef = new int[cylinders];                    // Mapping of cylinder locations that are requesting service.

        for (int i = 0; i < cylinders; i++) {
            cylRef[i] = i;
        }

        for (int i = 0; i < requests.length; i++) {
            reqListing[i] = Integer.parseInt(requests[i]);
            cylRef[reqListing[i]] = 0;
        }
    }

    /**
     * The getCylinders() function returns the amount of cylinders on the disk.
     * @return Returns the int value of the total cylinders on the disk.  */
    public int getCylinders() {
        return cylinders;
    }

    /**
     * The getHeadPos function returns the current location of the disk's head.
     * @return The value representing the cylinder where the head is currently positioned.  */
    public int getHeadPos() {
        return headPos;
    }

    /**
     * The getRequest() function returns the cylinder number location of an IO request by its particular index number.
     * @param indexOfRequest The index number to the cylinder requesting service.
     * @return Returns the value stored at the index passed if it is valid, otherwise -1.  */
    public int getRequestLoc(int indexOfRequest) {
        if (indexOfRequest > reqListing.length) return -1;
        if (indexOfRequest < 0) return -1;
        return reqListing[indexOfRequest];
    }

    /**
     * The getRequests() function returns the listing of requests.
     * @return reqListing The array of integers representing the cylinder numbers that have the requests.  */
    public int[] getRequests() {
        return reqListing;
    }

    /**
     * The Disk's search() function performs a binary search on a sorted reqListing array recursively.
     * @param sortedReqList A copy of the reqListing array that has been sorted to allow a binary search to take place.
     * @param firstCyl      The index containing the first cylinder where an IO request is being made.
     * @param lastCyl       The index containing the highest cylinder number where an IO request is being made.
     * @param cylToFind     The cylinder number with the IO request of interest resides. This is what is being searched for.
     * @return The subscript of the index containing the cylinder number that is found. Returns -1 if not found.
     */
    private static int search(int[] sortedReqList, int firstCyl, int lastCyl, int cylToFind) {
        int midCyl = (firstCyl + lastCyl) / 2;  // Needed to determine the midpoint of the search domain.
        if (firstCyl > lastCyl) return -1;      // Base case condition where the cylToFind is not among the reqListing.

        // Search for the cylinder of interest. If cylinder is in the middle index of the sorted request listing return
        // it first. If the cylinder that resides in the middle index has a value less than the cylinder number being
        // searched, a recursive call shifting the focus to the indexes higher than the previous middle through the last
        // index within the array takes place. Otherwise that portion is cut out of the search logarithmically leaving
        // the first index through the index just before what the previous middle was.
        if (sortedReqList[midCyl] == cylToFind) return midCyl;
        if (sortedReqList[midCyl] < cylToFind) return search(sortedReqList, (midCyl + 1), lastCyl, cylToFind);
        else return search(sortedReqList, firstCyl, (midCyl - 1), cylToFind);
    }

    /**
     * The setHeadPos() function updates the head position to the value passed.
     * @param newHeadPos The cylinder number of the new head position.  */
    public void setHeadPos(int newHeadPos) {
        headPos = newHeadPos;
    }

    /**
     * The sortRequests() function calls the sort method to sort an int array.
     @param reqListingToSort The array of IO requests to be sorted in ascending order.   */
    public static void sortRequests(int[] reqListingToSort){
        sort(reqListingToSort,0, (reqListingToSort.length - 1));
    }

    /**
     * The sort() function sorts the values representing the cylinder locations that have IO requests in need of
     * service. The sorting method follows routines found in the QuickSort algorithm developed by C.A.R. Hoare, 1960.
     * This function requires the use of helper method divideArray() that is responsible for selecting a pivot value for
     * the array and dividing the array into a pair of lists.
     @param reqListingToSort The array of pending requests in need of sorting.
     @param firstIndex Represents the first subscript of the array that will be sorted.
     @param lastIndex Represents the ending subscript of the array that will be sorted.   */
    private static void sort(int[] reqListingToSort, int firstIndex, int lastIndex){
        if (firstIndex < lastIndex){
            int pivot = divideArray(reqListingToSort, firstIndex, lastIndex);   // Set the pivot point.
            sort(reqListingToSort, firstIndex, (pivot - 1));                    // Sort the left side's list.
            sort(reqListingToSort, (pivot + 1), lastIndex);                     // Sort the right side's list.
        }
    }

    /**
     * The divideArray() function establishes a pivot value and divides the array into two lists. The values in the list
     * that are equivalent or larger than the pivot point's value go to the listing on the right with the lesser values
     * being placed on the left side's list.
     * @param arrayToDivide The array to partition.
     * @param first The beginning subscript of the area being divided.
     * @param last The ending subscript of the area being divided.
     * @return pivotIndex The subscript of the pivot value that is returned.  */
    private static int divideArray(int[] arrayToDivide, int first, int last){
        int lastOfLeft;         // Subscript associated with the last index of the list on the left side.

        // Used to track the mid-point of the two lists. Pivot value is initially based on the subscript of the middle
        // element before the middle and first elements are swapped which places the pivot value at the beginning.
        int midIndex = (first + last) / 2;

        // Swap the values contained in the middle and first indexes effectively moving the pivot value to the beginning
        // of the list.
        swap(arrayToDivide, first, midIndex);
        int pivot = arrayToDivide[first];   // Needed to compare the pivot value to values stored in the other indices.

        // After the swap, the end of the left side's list should be the first element.
        lastOfLeft = first;

        // Now the entire list is checked for values less than the value stored in pivot. All indices that fit this
        // description get swapped into the left side's listing after the lastOfLeft variable is incremented to account
        // for all elements swapped.
        for (int i = (first + 1); i <= last; i++) {
            if (arrayToDivide[i] < pivot){
                lastOfLeft++;
                swap(arrayToDivide, lastOfLeft,i);
            }
        }

        // The pivot value is then moved to the end of the left side's list via swap() and its subscript returned.
        swap(arrayToDivide, first, lastOfLeft);
        return lastOfLeft;
    }

    /**
     * The swap() function exchanges the integer values stored in two elements allowing the divideArray() method to
     * manage the indexing of pivot values and boundaries of its two lists.
     * @param arrayToDivide The array where each of the elements being swapped reside.
     * @param e1 Represents the subscript of element 1.
     * @param e2 Represents the subscript of element 2.    */
    private static void swap(int[] arrayToDivide, int e1, int e2){
        int firstElement = arrayToDivide[e1];       // Temporarily store value of e1.
        arrayToDivide[e1] = arrayToDivide[e2];      // Assign second element's value to e1.
        arrayToDivide[e2] = firstElement;           // Use temp value to assign first elements value to e2.
    }
}