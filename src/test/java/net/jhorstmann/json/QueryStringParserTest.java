package net.jhorstmann.json;

import java.util.Map;
import java.util.HashMap;
import java.util.Arrays;
import java.io.IOException;
import java.util.Collections;
import org.junit.Test;
import static org.junit.Assert.*;

public class QueryStringParserTest {
    @Test
    public void testSimpleProperty() throws IOException {
        assertEquals(Collections.singletonMap("test", "abc"), new QueryStringParser("test=abc").parse());
    }

    @Test
    public void testEmptyProperty() throws IOException {
        assertEquals(Collections.singletonMap("test", ""), new QueryStringParser("test=").parse());
    }

    @Test
    public void testNoEquals() throws IOException {
        assertEquals(Collections.singletonMap("test", Boolean.TRUE), new QueryStringParser("test").parse());
        assertEquals(Collections.singletonMap("test", Boolean.TRUE), new QueryStringParser("test&").parse());
    }

    @Test
    public void testLastPropertyWins() throws IOException {
        assertEquals(Collections.singletonMap("test", "def"), new QueryStringParser("test=abc&test=def").parse());
    }

    @Test
    public void testLastNestedPropertyWins() throws IOException {
        assertEquals(Collections.singletonMap("test", Collections.singletonMap("abc", "123")), new QueryStringParser("test.abc=&test.abc=123").parse());
    }

    @Test
    public void testNestedProperty() throws IOException {
        assertEquals(Collections.singletonMap("test", Collections.singletonMap("prop", "abc")), new QueryStringParser("test.prop=abc").parse());
    }

    @Test
    public void testNestedProperties() throws IOException {
        Map map = new HashMap();
        map.put("abc", "123");
        map.put("def", "456");

        assertEquals(Collections.singletonMap("test", map), new QueryStringParser("test.abc=123&test.def=456").parse());
    }

    @Test
    public void testSimpleList() throws IOException {
        assertEquals(Collections.singletonMap("test", Arrays.asList("abc", "def", "ghi")), new QueryStringParser("test[]=abc&test[]=def&test[]=ghi").parse());
    }

    @Test
    public void testSimpleIndexedList() throws IOException {
        assertEquals(Collections.singletonMap("test", Arrays.asList("abc", "def", "ghi")), new QueryStringParser("test[0]=abc&test[1]=def&test[2]=ghi").parse());
    }

    @Test
    public void testNestedSimpleList() throws IOException {
        assertEquals(Collections.singletonMap("test", Arrays.asList(Arrays.asList("abc"))), new QueryStringParser("test[0][]=abc").parse());
        assertEquals(Collections.singletonMap("test", Arrays.asList(Arrays.asList("abc", "def"))), new QueryStringParser("test[0][]=abc&test[0][]=def").parse());
        assertEquals(Collections.singletonMap("test", Arrays.asList(Arrays.asList("abc", "def", "ghi"))), new QueryStringParser("test[0][]=abc&test[0][]=def&test[0][]=ghi").parse());
    }

    @Test
    public void testNestedIndexedList() throws IOException {
        assertEquals(Collections.singletonMap("test", Arrays.asList(Arrays.asList("abc"))), new QueryStringParser("test[0][0]=abc").parse());
        assertEquals(Collections.singletonMap("test", Arrays.asList(Arrays.asList("abc", "def"))), new QueryStringParser("test[0][0]=abc&test[0][1]=def").parse());
        assertEquals(Collections.singletonMap("test", Arrays.asList(Arrays.asList("abc", "def", "ghi"))), new QueryStringParser("test[0][0]=abc&test[0][1]=def&test[0][2]=ghi").parse());
    }

    @Test
    public void testNestedMapInList() throws IOException {
        Map map = new HashMap();
        map.put("abc", "123");
        map.put("def", "456");
        assertEquals(Collections.singletonMap("test", Arrays.asList(map)), new QueryStringParser("test[0].abc=123&test[0].def=456").parse());
    }

    @Test
    public void testNestedMapWithListPropertyInList() throws IOException {
        Map map = new HashMap();
        map.put("abc", Arrays.asList("123", "456", "789"));
        assertEquals(Collections.singletonMap("test", Arrays.asList(map)), new QueryStringParser("test[0].abc[]=123&test[0].abc[]=456&test[0].abc[]=789").parse());
    }

    @Test
    public void testUrlEscapedSlash() throws IOException {
        assertEquals(Collections.singletonMap("test", "abc/def/ghi"), new QueryStringParser("test=abc%2fdef%2Fghi").parse());
    }

    @Test
    public void testUrlEscapedAmpersandAndEquals() throws IOException {
        assertEquals(Collections.singletonMap("test", "abc&def=ghi"), new QueryStringParser("test=abc%26def%3Dghi").parse());
    }

    @Test
    public void testUrlEscapedUmlaut() throws IOException {
        assertEquals(Collections.singletonMap("test", "abcädefÖÜ"), new QueryStringParser("test=abc%c3%a4def%c3%96%C3%9C").parse());
    }

    @Test(expected=JSONSyntaxException.class)
    public void testInvalidIndex() throws IOException {
        new QueryStringParser("abc[" + Integer.MAX_VALUE + "]").parse();
    }
}
