package net.jhorstmann.json;

import java.util.List;
import java.util.Arrays;
import java.util.Map;
import java.util.LinkedHashMap;
import java.util.Collections;
import java.io.IOException;
import org.junit.Test;
import static org.junit.Assert.*;

public class QueryStringWriterTest {
    @Test
    public void testSingleProperty() throws IOException {
        assertEquals("test=abc", QueryStringWriter.format("test", "abc"));
        assertEquals("test=abc", QueryStringWriter.format(Collections.singletonMap("test", "abc")));
    }

    @Test
    public void testMap() throws IOException {
        Map<String, String> map = new LinkedHashMap<String, String>();
        map.put("abc", "123");
        map.put("def", "456");
        map.put("ghi", "789");
        assertEquals("abc=123&def=456&ghi=789", QueryStringWriter.format(map));
    }

    @Test
    public void testList() throws IOException {
        assertEquals("test[0]=abc&test[1]=def&test[2]=ghi", QueryStringWriter.format("test", Arrays.asList("abc", "def", "ghi")));
    }

    @Test
    public void testArray() throws IOException {
        assertEquals("test[0]=abc&test[1]=def&test[2]=ghi", QueryStringWriter.format("test", new String[]{"abc", "def", "ghi"}));
    }

    @Test
    public void testCollection() throws IOException {
        assertEquals("test[]=abc&test[]=def&test[]=ghi", QueryStringWriter.format("test", Collections.unmodifiableCollection(Arrays.asList("abc", "def", "ghi"))));
    }

    @Test
    public void testNestedMap() throws IOException {
        Map<String, Object> map = new LinkedHashMap<String, Object>();
        Map<String, Object> map2 = new LinkedHashMap<String, Object>();
        List<String> list = Arrays.asList("123", "456", "789");
        map2.put("abc", "123");
        map2.put("def", "456");
        map2.put("list", list);
        map.put("test", map2);

        assertEquals("test.abc=123&test.def=456&test.list[0]=123&test.list[1]=456&test.list[2]=789", QueryStringWriter.format(map));
    }
}
