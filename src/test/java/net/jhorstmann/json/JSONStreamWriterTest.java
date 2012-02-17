package net.jhorstmann.json;

import java.util.Arrays;
import java.io.StringWriter;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.LinkedHashMap;
import java.util.Map;
import org.junit.Test;
import static org.junit.Assert.*;

public class JSONStreamWriterTest {
    @Test
    public void testList() throws IOException {
        StringWriter sw = new StringWriter();
        new JSONStreamWriter(sw).writeValue(Arrays.asList("abc", 123.0, 456.0));
        assertEquals("[\"abc\",123.0,456.0]", sw.toString());
    }
    
    @Test
    public void testPrettyMap() throws IOException {
        Map map = new LinkedHashMap();
        map.put("abc", 123.0);
        map.put("def", 456.0);
        StringWriter sw = new StringWriter();
        new JSONStreamWriter(sw, true).writeValue(map);
        String expected = "{\n  \"abc\": 123.0,\n  \"def\": 456.0\n}";
        System.out.println("---");
        System.out.println(expected);
        System.out.println("---");
        System.out.println(sw.toString());
        System.out.println("---");
        assertEquals(expected, sw.toString());
    }

    @Test
    public void testPrettyList() throws IOException {
        StringWriter sw = new StringWriter();
        new JSONStreamWriter(sw, true).writeValue(Arrays.asList("abc", 123.0, 456.0));
        String expected = "[\n  \"abc\",\n  123.0,\n  456.0\n]";
        System.out.println("---");
        System.out.println(expected);
        System.out.println("---");
        System.out.println(sw.toString());
        System.out.println("---");
        assertEquals(expected, sw.toString());
    }
    
    @Test
    public void testOutputStream() throws IOException {
        Map map = new LinkedHashMap();
        map.put("abc", 123.0);
        map.put("def", 456.0);

        ByteArrayOutputStream out = new ByteArrayOutputStream();
        new JSONStreamWriter(out).writeValue(map);
        assertEquals( "{\"abc\":123.0,\"def\":456.0}", new String(out.toByteArray(), "UTF-8"));
    }

    @Test
    public void testStringBuilder() throws IOException {
        Map map = new LinkedHashMap();
        map.put("abc", 123.0);
        map.put("def", 456.0);
        StringBuilder sb = new StringBuilder();
        new JSONStreamWriter(sb).writeValue(map);
        assertEquals( "{\"abc\":123.0,\"def\":456.0}", sb.toString());
    }

    @Test
    public void testStringWriter() throws IOException {
        Map map = new LinkedHashMap();
        map.put("abc", 123.0);
        map.put("def", 456.0);

        StringWriter sw = new StringWriter();
        new JSONStreamWriter(sw).writeValue(map);
        assertEquals( "{\"abc\":123.0,\"def\":456.0}", sw.toString());
    }
    
}
