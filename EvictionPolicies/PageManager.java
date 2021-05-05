/**The PageManager and Page classes mimic HashMap functionality to provide a quicker means of checking the
 * page-reference string for PgNumNodes while implementing the FIFO, LRU and OPTIMAL algorithms.    
 * @author Trevor McCarthy                                                                              */

class PageManager<K, V> {
    protected Page<K, V>[] pageReferences;
    protected final int DEFAULT_SIZE = 7;
    protected int frameSize;

    public PageManager() {
        frameSize = DEFAULT_SIZE;
        pageReferences = new Page[frameSize];
    }

    public PageManager(int frameSize) {
        pageReferences = new Page[frameSize];
        this.frameSize = frameSize;
    }

    private int getIndex(K key) {
        if (key != null) return Math.abs(key.hashCode() % frameSize);
        return 0;
    }

    /**
     * The setReference adds the page to the PageManager's pageReferences array. This takes place anytime a cache hit
     * does not take place. The method traverses the entire pageReferences array until reaching If the key is already in
     * the pageReferences array, its value is updated with the passed value.
     * @param key Key reference of the Page being added to the PageFrameRef.
     * @param val The value associated with the added key. */
    public void setReference(K key, V val) {
        int i = getIndex(key);
        Page firstPage = new Page(null,0,0);

        if (pageReferences[i] != null) {
            Page<K, V> current = pageReferences[i];
            Page<K, V> previous = null;

            while (current != null) {
                if (current.getKey().equals(key)) {
                    current.setVal(val);
                    break;
                }
                previous = current;
                current = current.getNextPage();
            }
            if (previous != null) previous.setNextPage(firstPage);
        } else {
            pageReferences[i] = firstPage;
        }
    }

    /**
     * The deleteReference() method recursively traverses the the page-reference listing comparing keys until finding
     * one matching the parameter passed.
     * @param key Key of the PgNumNode to delete.   */
    public void deleteReference(K key) {
        int i = getIndex(key);
        Page previous = null;
        Page<K, V> page = pageReferences[i];

        // Uses the key matching the index of the key after calculation. If the page.getKey()equals and previous ==
        // null, that means the parameter passed was the first PgNumNode of PageRefString.
        while (page != null) {
            if (page.getKey().equals(key)){
                if (previous == null) {
                    page = page.getNextPage();
                    pageReferences[i] = page;
                    return;
                } else {
                    previous.setNextPage(page.getNextPage());
                }
            }
            previous = page;
            page = page.getNextPage();
        }
    }

    /**
     * The findReference() function just traverses the listing returning the value that matches the key passed.
     * @param key Key to which holds the desired value.
     * @return val The PgNumNode with the element matching the page.  */
    public V findReference(K key) {
        int i = getIndex(key);
        V val = null;
        Page<K, V> page = pageReferences[i];

        while (page != null) {
            if (!page.getKey().equals(key)) page = page.getNextPage();
            else {
                val = page.getVal();
            }
        }
        return val;
    }
}

class Page<K, V> {
    protected Page<K, V> nextPage;
    protected K key;
    protected V val;

    public Page(Page<K, V> nextPage, K key, V val) {
        this.nextPage = nextPage;
        this.key = key;
        this.val = val;
    }

    // Accessors
    public Page getNextPage() {
        return nextPage;
    }

    public K getKey() {
        return key;
    }

    public V getVal() {
        return val;
    }

    // Mutators
    public void setNextPage(Page<K, V> nextPage) {
        this.nextPage = nextPage;
    }

    public void setKey(K key) {
        this.key = key;
    }

    public void setVal(V val) {
        this.val = val;
    }
}

