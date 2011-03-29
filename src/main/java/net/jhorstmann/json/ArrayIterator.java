package net.jhorstmann.json;

import java.lang.reflect.Array;
import java.util.Iterator;
import java.util.NoSuchElementException;

class ArrayIterator implements Iterator {
    private Object array;
    private int length;
    private int index;

    ArrayIterator(Object array) {
        this(array, Array.getLength(array));
    }

    ArrayIterator(Object array, int length) {
        this.array = array;
        this.length = length;
        this.index = 0;
    }

    public boolean hasNext() {
        return index < length;
    }

    public Object next() {
        if (!hasNext()) {
            throw new NoSuchElementException();
        }
        return Array.get(array, index++);
    }

    public void remove() {
        throw new UnsupportedOperationException("Remove is not supported.");
    }

}
