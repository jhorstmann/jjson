package net.jhorstmann.json;

import static junit.framework.Assert.*;
import org.junit.Test;

public class JSONArrayTest {
    @Test
    public void testArray() {
        JSONArray arr = new JSONArray();
        arr.add(1.0);
        arr.add("abc");
        arr.add(Boolean.TRUE);
        arr.add(JSONNull.INSTANCE);
        JSONListIterator iter = arr.jsonListIterator();
        assertTrue(iter.hasNext());
        assertEquals(1.0, iter.nextDouble());
        assertEquals("abc", iter.nextString());
        assertTrue(iter.nextBoolean());
        assertTrue(iter.nextIsNull());
        assertFalse(iter.hasNext());
    }

    @Test
    public void testToString() {
        JSONArray arr = new JSONArray();
        arr.add(1.0);
        arr.add("abc");
        assertEquals("[1.0,\"abc\"]", arr.toString());
    }

}
