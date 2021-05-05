/**To be used in conjunction with Asg3.java, Producer.java and Consumer.java
 * This program is an extension of assignment #2. The producer and consumer share an integer array (length=5)
 * which is a circular buffer. They share 1-2 variables to coordinate placing/removing items in the circular buffer.
 * @author Trevor McCarthy                                                                                        */

public class CircularBuffer {
    protected Object buffer[];              // Holds the items.
    protected int size;                     // Size of the buffer.
    protected int counter;                  // Total items that occupy the buffer.
    protected int start;                    // Points to the beginning of the buffer.
    protected int end;                      // Points to the end of the buffer.

    /**
     * The CicularBuffer constructor initializes the its size variable with the value of the parameter provided it is
     * greater than zero. It then initializes its buffer array with the parameter's value. Finally, it initializes its
     * counter and start variables to zero and the end variable to the parameter's value - 1.
     * @param length The desired buffer size.                                                                       */
    public CircularBuffer(int length) {
        if (length > 0) {
            buffer = new Object[length];
            size = length;
            counter = 0;                    // Initially an empty buffer.
            start = 0;                      // To reference the first index in the buffer.
            end = (length - 1);             // To reference the last index of the buffer.
        }
    }

    /**
     * The getCounter method returns the number of items currently stored in the buffer.
     * @return counter The variables current value.                                   */
    synchronized public int getCounter() {
        return counter;
    }

    /**
     * The isFull method checks the size and counter variables for equivalence to identify a full buffer.
     * @return true if the variables values are equal, otherwise false.                                */
    synchronized public boolean isFull() {
        return (size == counter);
    }

    /**
     * The isEmpty method checks if the counter variable's value is zero indicating an empty buffer.
     * @return True when counter equals zero, and false when it is not.                           */
    synchronized public boolean isEmpty() {
        return (counter <= 0);
    }

    /**
     * The bufferIn method adds an Object to the buffer array if the parameter is not null and there is available space.
     * If either of these conditions fail, the method is returned without adding.
     * @param toAdd Object being added to the buffer.
     * @return True if Object is successfully added to the buffer, otherwise false.                                   */
    synchronized public boolean bufferIn(Object toAdd) {
        // Check for -1 flag.
        if (toAdd == String.valueOf(-1)) {
            counter = -1;
            return true;
        }
        if (isFull() || toAdd == null) return false;
        counter++;                              // Update the counter to reflect the new Object added.

        // Handle condition where the item being added will be taking the last of the available buffer space.
        end++;
        if (size <= end) end = 0;
        buffer[end] = toAdd;
        System.out.println("In the bufferIn method, buffer[end] = " + buffer[end]);
        return true;
    }

    /**
     * The bufferOut method removes an Object from the buffer array if there is one available. It then updates the
     * variables involved with tracking item count and buffer size.
     * @return toReturn Reference to Object removed from the buffer. Null if buffer was empty.                  */
    synchronized public Object bufferOut() {
        Object toReturn = null;
        if (isEmpty()) return toReturn;
        if (counter == -1) return (new Integer(-1));
        toReturn = buffer[start];               // toReturn object now references the first item in the buffer.
        buffer[start] = null;                   // Dereference the object from the buffer space.
        counter--;                              // Update the counter to reflect the Object removal.

        // Handle condition where the item removed was accompanying the last of the available buffer space.
        start++;
        if (size <= start) start = 0;
        return toReturn;
    }
}