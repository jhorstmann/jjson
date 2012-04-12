package net.jhorstmann.json;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.SimpleTimeZone;
import org.junit.Test;
import static org.junit.Assert.*;

public class JSONObjectTest {

    @Test
    public void testIsoDate() {
        JSONObject json = new JSONObject();
        SimpleDateFormat fmt = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss'Z'");
        fmt.setTimeZone(new SimpleTimeZone(0, "UTC"));
        fmt.setLenient(false);
        Date now = new Date(2012, Calendar.JANUARY, 6, 16, 10, 30);
        String str = fmt.format(now);
        json.put("date", str);

        assertEquals(now, json.getDate("date"));
    }

    @Test
    public void testAspDate() {
        JSONObject json = new JSONObject();
        Date now = new Date(2012, Calendar.JANUARY, 6, 16, 10, 30);
        json.put("date", "/Date(" + now.getTime() + ")/");
        assertEquals(now, json.getDate("date"));
    }

    @Test
    public void testToString() {
        JSONObject json = new JSONObject();
        json.put("a", 1.0);
        json.put("b", "xyz");
        assertEquals("{\"a\":1.0,\"b\":\"xyz\"}", json.toString());
    }
}
