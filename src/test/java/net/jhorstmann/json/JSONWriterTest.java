package net.jhorstmann.json;

import java.io.ByteArrayOutputStream;
import java.io.StringWriter;
import java.util.Map;
import java.io.IOException;
import java.util.LinkedHashMap;
import org.junit.Test;
import static org.junit.Assert.*;

public class JSONWriterTest {
    @Test
    public void testOutputStream() throws IOException {
        Map map = new LinkedHashMap();
        map.put("abc", 123.0);
        map.put("def", 456.0);

        ByteArrayOutputStream out = new ByteArrayOutputStream();
        new JSONWriter(out).write(map);
        assertEquals( "{\"abc\":123.0,\"def\":456.0}", new String(out.toByteArray(), "UTF-8"));
    }

    @Test
    public void testStringBuilder() throws IOException {
        Map map = new LinkedHashMap();
        map.put("abc", 123.0);
        map.put("def", 456.0);
        StringBuilder sb = new StringBuilder();
        new JSONWriter(sb).write(map);
        assertEquals( "{\"abc\":123.0,\"def\":456.0}", sb.toString());
    }

    @Test
    public void testStringWriter() throws IOException {
        Map map = new LinkedHashMap();
        map.put("abc", 123.0);
        map.put("def", 456.0);

        StringWriter sw = new StringWriter();
        new JSONWriter(sw).write(map);
        assertEquals( "{\"abc\":123.0,\"def\":456.0}", sw.toString());
    }
}
