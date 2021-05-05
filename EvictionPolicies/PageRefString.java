/**This program is to be used in conjunction with the Asg5.java class. The PageRefString.java implements a doubly
 * linked list to provide a more efficient page replacement process through its 2 link fields. The class also implements
 * a stack for further optimization of the LRU algorithm.                                                             
 * @author Trevor McCarthy                                                                                            */

import java.util.HashMap;
import java.util.Iterator;
import java.util.Stack;

public class PageRefString<Generic> implements Iterable<Generic> {
    public static boolean debug = false;
    private PgNumNode<Generic> first;
    private PgNumNode<Generic> last;
    private PageManager<Integer,PgNumNode> pageLoc = new PageManager<>();
    private int faultCount = 0;
    private int frameLimit = -1;                        // Limits possible size of PageRefString. Default off.

    public Iterator<Generic> iterator() {
        return new LinkedListIterator();
    }

    class LinkedListIterator implements Iterator<Generic> {
        private PgNumNode<Generic> current = first;

        public boolean hasNext() {
            return (current != null);
        }

        public Generic next() {
            Generic element = current.getElement();
            current = current.getNext();
            return element;
        }
    }

    /**
     * The getFirst method returns a reference to the PgNumNode that is first on the linked list.
     * @return first The PgNumNode that is referenced by the "first" variable.                 */
    public PgNumNode getFirst() {return first;}

    /**
     * The getLast method returns a reference to the PgNumNode that is last on the linked list.
     * @return last The PgNumNode that is referenced by the "last" variable.                 */
    public PgNumNode getLast() {return last;}

    /**
     * The getNode method returns the PgNumNode that matches the index.
     * @param index The number associated with where the node is located within the doubly linked list.
     * @return ref The PgNumNode matching the index parameter.                                       */
    public PgNumNode getNode(int index) {
        PgNumNode ref = first;
        if (index >= 0 && first != null) {
            for (int i = 0; i < index; i++) ref = ref.next;
        }
        return ref;
    }

    /**
     * The setFrameLim() method sets the amount of frames to a fixed amount. If the parameter is lower than
     * the page frames size at the time of call, the frames will be fixed to the current size.
     * @param limit The size to which the page frames will be limited to.                                */
    public void setFrameLim(int limit) {
        int currentSize = size();
        if (currentSize > limit) frameLimit = currentSize;
        else
            frameLimit = limit;
    }

    /**
     * This evictLast() function severs the last PgNumNode object from the PageRefString. Used in LRU algorithm
     * to handle instances of page faults.                                                                   */
    public void evictLast() {remove(last);}

    /**
     * This size method recursively calls its overloaded counterpart.
     * @return size(first) Recursive call passing the "first" variable referencing the first PgNumNode. */
    public int size() {return size(first);}

    /**
     * This size method returns the total number of PgNumNode on the linked list.
     * @return size(sizer.next) + 1 Returns the total number of elements in the linked list. */
    public int size(PgNumNode sizer) {
        if (sizer == null) return 0;
        return size(sizer.next) + 1;
    }

    /**
     * The toString method returns information about what is contained in the linked list.
     * @return str The string containing the linked list content information.           */
    public String toString() {
        String str = "";
        PgNumNode ref = last;
        while (ref != null) {
            if (ref.hasPrevious()) str += ref.getElement() + ",";
            else str += ref.getElement();
            ref = ref.previous;
        }
        return str;
    }

    /**
     * The isEmpty() method checks the PgNumNode and determines if it is empty or not.
     * @return true if the PgNumNode is empty, otherwise false.                     */
    public boolean isEmpty() {return first == null;}

    /**
     * The isFull() method checks if the page frame's size is equivalent to the active frameLimit. If the frameLimit
     * option is off (-1) the page frame will never fill and return false.
     * @return true if frameLimit is equal to the page frame's size, otherwise false.                             */
    public boolean isFull() {
        if (frameLimit == -1) return false;
        int currentSize = size();
        return (frameLimit > currentSize);
    }

    /**
     * The copy() method creates a deep copy of the list passed. For the purpose of Asg5, this returns
     * a new PageRefString object.
     * @param oldList The list object that is being copied.
     * @return newList The list referencing the newly copied list.                                  */
    public PageRefString<Generic> copy(PageRefString<Generic> oldList) {
        PageRefString<Generic> newList = new PageRefString<>();
        for (Generic element : oldList) newList.addNode((Generic) element);
        return newList;
    }

    /**
     * The addNode method instantiates a new PgNumNode at the desired index. If the list is empty, the new PgNumNode
     * will be used to initialize the PageRefString's "first" variable.
     * @return true after initializing first with the recently added node.                                        */
    public boolean addNode(Generic element, int index) {
        if (debug) System.out.println("PageRefString.addNode(\"" + element + "\") at index: " + index);
        PgNumNode<Generic> addedNode = new PgNumNode<>(element);
        if (isEmpty()) {
            first = addedNode;
            last = first;
            return true;
        }
        PgNumNode<Generic> next = first;
        PgNumNode<Generic> previous = first;

        while (index > 0 && (next != null)) {
            previous = next;
            next = next.getNext();
            index--;
        }
        if (debug) System.out.println("In the addNode() method, next = " + next + " and previous = " + previous);

        if (next == null) {
            previous.updateNext(addedNode);

            if (debug) {
                System.out.println("Just added the last element on the Linked List. Reference variable values: ");
                System.out.println("PageRefString.first = " + first + ", PageRefString.last = " + last);
                System.out.println("AddNode Local Variables next and previous = " + next + ", " + previous);
                System.out.println("PgNumNode next/previous values = " + addedNode.next + ", " + addedNode.previous);
            }
        } else if (next == first) {
            addedNode.updateNext(first);
            first = addedNode;
            addedNode.getNext().updatePrevious(addedNode);

            if (debug) {
                System.out.println(element + " Was added to the head of the Linked List. Reference variable values: ");
                System.out.println("PageRefString.first = " + first + ", PageRefString.last = " + last);
                System.out.println("AddNode Local Variables next and previous = " + next + ", " + previous);
                System.out.println("PgNumNode variables next/previous = " + addedNode.next + ", " + addedNode.previous);
            }
        } else {
            addedNode.updateNext(next.getNext());
            if (debug) {
                System.out.println("addedNode.updateNext(next.getNext()) = " + addedNode + " updateNext(" +
                        next.getNext() + ")");
            }
            next.updateNext(addedNode);

            if (debug) {
                System.out.println("next.updateNext(addedNode) = " + next + "updateNext(" + addedNode + ")");
                System.out.println("Should be the first element. Reference variable values: ");
                System.out.println("PageRefString.first = " + first + ", PageRefString.last = " + last);
                System.out.println("AddNode Local Variables next and previous = " + next + ", " + previous);
                System.out.println("PgNumNode next/previous = " + addedNode.next + ", " + addedNode.previous);
            }
        }
        return true;
    }

    /**
     * The addNode() function instantiates a new PgNumNode that will proceed through the nested if statements in the
     * above addNode() called as 0 is the first.
     * @return true after initializing the "first" variable with the recently added node.                         */
    public boolean addNode(Generic element) {return addNode(element, 0);}

    /**
     * The remove() method allows the PageRefString to remove the PgNumNode at the desired index by calling it's
     * overloaded remove() counterpart. This maintains the integrity of the PageRefString's structure.
     * @param indexOfRemoveMe The index where the PgNumNode to remove resides.
     * @return true After removal of the PgNumNode; false if the PgNumNode's index is a negative integer.     */
    public boolean remove(int indexOfRemoveMe) {
        if (indexOfRemoveMe < 0) return false;

        // First node's next is updated to be the new first.
        if (indexOfRemoveMe == 0) return remove(first);
        PgNumNode<Generic> nextNode = first;

        while (indexOfRemoveMe != 0) {
            indexOfRemoveMe--;
            if (!nextNode.hasNext()) return false;
            nextNode = nextNode.getNext();
        }
        return remove(nextNode);
    }

    /**
     * This remove() function serves as a helper to the index based remove() method.
     * @param removeMe The PgNumNode object to be removed.
     * @return true After removal of the PgNumNode; false if the PgNumNode's index is a negative integer. */
    public boolean remove(PgNumNode<Generic> removeMe) {
        if (removeMe == null) return false;
        if (removeMe == first) {
            first = first.getNext();
            removeMe.updateNext(null);
            return true;
        }
        PgNumNode<Generic> nextNode = first;
        while (nextNode.getNext() != null) {

            if (nextNode.getNext() == removeMe) {
                nextNode.updateNext(removeMe.getNext());
                removeMe.updateNext(null);
                return true;
            }
            nextNode = nextNode.getNext();
        }
        return false;
    }

    /**
     * The makeEmpty() method removes all pointer references for any PgNumNode contained in the list. */
    public void makeEmpty() {
        int nodeCounter = 0;
        if (debug)
            System.out.println("PageRefString.makeEmpty() status: " + ((first == null) ? "Empty" : "Not Empty"));

        PgNumNode<Generic> currentOne;
        PgNumNode<Generic> nextOne = first;

        while (nextOne != null) {
            currentOne = nextOne;
            nextOne = currentOne.getNext();
            if (debug) System.out.println("Found node #" + (++nodeCounter) + " as " + currentOne);

            currentOne.updateNext(null);
        }
        if (debug) System.out.println("makeEmpty() complete. " + nodeCounter + " nodes set for garbage collection.");
        first = null;
        resetFaultCount();
    }

    /**
     * The getFaultCount() method returns an integer representation of the PageRefString's total fault count. */
    public int getFaultCount() {return faultCount;}

    /**
     * The recordFault() method is to be called when a page fault occurs. Increments the faultCount variable. */
    public void recordFault() {faultCount++;}

    /**
     * The resetFaultCount() is called by the makeEmpty() process after PgNumNodes have been removed. */
    private void resetFaultCount() {faultCount = 0;}

    /**
     * The PgNumNode is used to compose the elements that establish the doubly Linked List data structure.
     * Each node represents page number references needed to test three algorithms in Asg5 with functionality built
     * to apply for remaining assignments.                                                                       */
    public class PgNumNode<Generic> {
        private Generic element;
        private PgNumNode<Generic> next;
        private PgNumNode<Generic> previous;

        /**
         * This constructor stores a generic object containing values while leaving the next instance variable null.
         * @param element The element stored in the node.                                                         */
        public PgNumNode(Generic element) {
            this.element = element;
        }

        /**
         * The toString method returns details about what is being stored in the node.
         * @return Uses the String.valueOf method to return the element stored.     */
        public String toString() {
            String str = "";
            str += element;
            return str;
        }

        /**
         * The getNext method returns a reference to the node that is next on the list.
         * @return next The object referencing the current node's successor.         */
        public PgNumNode<Generic> getNext() {return next;}

        /**
         * The getPrevious method returns a reference to the previous node on the list.
         * @return previous The variable referencing the current node's predecessor. */
        public PgNumNode<Generic> getPrevious() {return previous;}

        /**
         * The getElement method returns a reference to the node's element. */
        public Generic getElement() {return element;}

        /**
         * The hasNext method checks the current PgNumNode to see if it is linked to another with the "next" variable.
         * @return true if the PgNumNode is linked to another and false if not.                                     */
        public boolean hasNext() {return (next != null);}

        /**
         * The hasPrevious method checks to see if the calling node is linked to another with the "previous" variable.
         * @return true if the PgNumNode is linked to another and false if not.                                     */
        public boolean hasPrevious() {return (previous != null);}

        /**
         * The updateNext method uses the parameter passed to create a reference to the "next" node.
         * @param next The object that will be referenced by the current node as "next."          */
        public void updateNext(PgNumNode<Generic> next) {this.next = next;}

        /**
         * The updatePrevious method uses the parameter to creates a reference to the current node's predecessor.
         * @param previous The object that will be referenced by the current node as "previous."               */
        public void updatePrevious(PgNumNode<Generic> previous) {this.previous = previous;}

        public void setElement(Generic element) {this.element = element;}
    }
}