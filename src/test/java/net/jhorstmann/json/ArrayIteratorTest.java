package net.jhorstmann.json;

import java.util.Iterator;
import java.util.NoSuchElementException;
import org.junit.Test;
import static org.junit.Assert.*;

public class ArrayIteratorTest {
    @Test
    public void testEmptyArrayIterable() {
        ArrayIterable iter = new ArrayIterable(new String[0]);
        assertFalse(iter.iterator().hasNext());
    }

    @Test
    public void testEmptyPrimitiveArrayIterable() {
        ArrayIterable iter = new ArrayIterable(new int[0]);
        assertFalse(iter.iterator().hasNext());
    }

    @Test(expected=NoSuchElementException.class)
    public void testNextShouldThrowNoSuchElementException() {
        ArrayIterable iter = new ArrayIterable(new int[0]);
        Object next = iter.iterator().next();
    }

    @Test(expected=UnsupportedOperationException.class)
    public void testRemoveShouldThrowUnsupportetdOperationException() {
        ArrayIterable iter = new ArrayIterable(new String[]{"a", "b"});
        Iterator it = iter.iterator();
        assertTrue(it.hasNext());
        assertEquals("a", it.next());
        it.remove();
    }

    @Test
    public void testArray1() {
        ArrayIterable iter = new ArrayIterable(new String[]{"a"});
        Iterator it = iter.iterator();
        assertTrue(it.hasNext());
        assertTrue(it.hasNext());
        assertEquals("a", it.next());
        assertFalse(it.hasNext());
        assertFalse(it.hasNext());
    }

    @Test
    public void testArray2() {
        ArrayIterable iter = new ArrayIterable(new String[]{"a", "b"});
        Iterator it = iter.iterator();
        assertTrue(it.hasNext());
        assertTrue(it.hasNext());
        assertEquals("a", it.next());
        assertTrue(it.hasNext());
        assertTrue(it.hasNext());
        assertEquals("b", it.next());
        assertFalse(it.hasNext());
        assertFalse(it.hasNext());
    }
}
