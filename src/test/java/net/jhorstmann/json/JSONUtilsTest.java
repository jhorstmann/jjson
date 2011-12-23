package net.jhorstmann.json;

import java.io.IOException;
import org.junit.Test;
import static org.junit.Assert.*;

public class JSONUtilsTest {

    @Test
    public void testEscapeString() throws IOException {
        assertEquals("\"\\u0001\"", JSONUtils.escapeString("\001"));
        assertEquals("\"\\\"\"", JSONUtils.escapeString("\""));
        assertEquals("\"\\r\\n\"", JSONUtils.escapeString("\r\n"));
        assertEquals("\"\\f\"", JSONUtils.escapeString("\014"));
    }    
}
