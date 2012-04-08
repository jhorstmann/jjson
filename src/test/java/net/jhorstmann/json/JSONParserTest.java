package net.jhorstmann.json;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.math.BigDecimal;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.HashMap;
import java.io.IOException;
import java.util.Collections;
import java.util.TimeZone;
import org.junit.Test;
import static org.junit.Assert.*;

public class JSONParserTest {
    @Test
    public void testEmptyMap() throws IOException {
        assertEquals(Collections.EMPTY_MAP, new JSONParser("{}").parse());
        assertEquals(Collections.EMPTY_MAP, new JSONParser(" {}").parse());
        assertEquals(Collections.EMPTY_MAP, new JSONParser("{} ").parse());
        assertEquals(Collections.EMPTY_MAP, new JSONParser("{ }").parse());
        assertEquals(Collections.EMPTY_MAP, new JSONParser("  {  }  ").parse());
    }

    @Test
    public void testEmptyMap2() throws IOException {
        assertEquals(Collections.EMPTY_MAP, new JSONParser("{}").parseObject());
        assertEquals(Collections.EMPTY_MAP, new JSONParser(" {}").parseObject());
        assertEquals(Collections.EMPTY_MAP, new JSONParser("{} ").parseObject());
        assertEquals(Collections.EMPTY_MAP, new JSONParser("{ }").parseObject());
        assertEquals(Collections.EMPTY_MAP, new JSONParser("  {  }  ").parseObject());
    }

    @Test(expected=JSONSyntaxException.class)
    public void testInvalidMap() throws IOException {
        new JSONParser("{,}").parse();
    }

    @Test(expected=JSONSyntaxException.class)
    public void testInvalidMap2() throws IOException {
        new JSONParser("{'test'}").parse();
    }

    @Test(expected=JSONSyntaxException.class)
    public void testInvalidMap3() throws IOException {
        new JSONParser("{'test':}").parse();
    }

    @Test(expected=JSONSyntaxException.class)
    public void testInvalidMap4() throws IOException {
        new JSONParser("{'test':123,}").parse();
    }

    @Test
    public void testMap() throws IOException {
        Map expected = Collections.singletonMap("abc", "def");
        assertEquals(expected, new JSONParser("{'abc':'def'}").parse());
        assertEquals(expected, new JSONParser("{ 'abc':'def'}").parse());
        assertEquals(expected, new JSONParser("{'abc' :'def'}").parse());
        assertEquals(expected, new JSONParser("{'abc': 'def'}").parse());
        assertEquals(expected, new JSONParser("{'abc':'def' }").parse());
        assertEquals(expected, new JSONParser("{ 'abc' : 'def' }").parse());
    }

    @Test
    public void testMapToNumber() throws IOException {
        Map expected = Collections.singletonMap("a", Double.valueOf(3.0));
        assertEquals(expected, new JSONParser("{'a':3}").parse());
        assertEquals(expected, new JSONParser("{ 'a':3}").parse());
        assertEquals(expected, new JSONParser("{'a' :3}").parse());
        assertEquals(expected, new JSONParser("{'a': 3}").parse());
        assertEquals(expected, new JSONParser("{'a':3 }").parse());
        assertEquals(expected, new JSONParser("{ 'a' : 3 }").parse());
    }
    
    @Test
    public void testMap2() throws IOException {
        Map expected = new HashMap();
        expected.put("abc", "def");
        expected.put("ghi", "jkl");
        assertEquals(expected, new JSONParser("{'abc':'def','ghi':'jkl'}").parse());
        assertEquals(expected, new JSONParser("{'abc':'def' ,'ghi':'jkl'}").parse());
        assertEquals(expected, new JSONParser("{'abc':'def', 'ghi':'jkl'}").parse());
        assertEquals(expected, new JSONParser("{'abc':'def' , 'ghi':'jkl'}").parse());
    }

    @Test
    public void testEmptyArray() throws IOException {
        assertEquals(Collections.EMPTY_LIST, new JSONParser("[]").parse());
        assertEquals(Collections.EMPTY_LIST, new JSONParser(" []").parse());
        assertEquals(Collections.EMPTY_LIST, new JSONParser("[] ").parse());
        assertEquals(Collections.EMPTY_LIST, new JSONParser("[ ]").parse());
        assertEquals(Collections.EMPTY_LIST, new JSONParser("  [  ]  ").parse());
    }

    @Test
    public void testEmptyArray2() throws IOException {
        assertEquals(Collections.EMPTY_LIST, new JSONParser("[]").parse());
        assertEquals(Collections.EMPTY_LIST, new JSONParser(" []").parse());
        assertEquals(Collections.EMPTY_LIST, new JSONParser("[] ").parse());
        assertEquals(Collections.EMPTY_LIST, new JSONParser("[ ]").parse());
        assertEquals(Collections.EMPTY_LIST, new JSONParser("  [  ]  ").parse());
    }

    @Test(expected=JSONSyntaxException.class)
    public void testInvalidArray() throws IOException {
        new JSONParser("[,]").parse();
    }

    @Test(expected=JSONSyntaxException.class)
    public void testInvalidArray2() throws IOException {
        new JSONParser("[123,]").parse();
    }

    @Test
    public void testArray() throws IOException {
        List expected = Collections.singletonList(123.0);
        assertEquals(expected, new JSONParser("[123]").parse());
        assertEquals(expected, new JSONParser("[ 123]").parse());
        assertEquals(expected, new JSONParser("[123 ]").parse());
        assertEquals(expected, new JSONParser("[ 123 ]").parse());
    }

    @Test
    public void testArray2() throws IOException {
        List expected = Arrays.asList(123.0, 456.0);
        assertEquals(expected, new JSONParser("[123,456]").parse());
        assertEquals(expected, new JSONParser("[123 ,456]").parse());
        assertEquals(expected, new JSONParser("[123, 456]").parse());
        assertEquals(expected, new JSONParser("[123 , 456]").parse());
    }

    @Test
    public void testEmptyString() throws IOException {
        assertEquals("", new JSONParser("''").parseString());
        assertEquals("", new JSONParser("\"\"").parseString());
    }

    @Test
    public void testString() throws IOException {
        assertEquals("abc", new JSONParser("'abc'").parse());
        assertEquals("abc", new JSONParser("\"abc\"").parse());
    }

    @Test
    public void testEscapedString() throws IOException {
        assertEquals("\"", new JSONParser("'\\\"'").parseString());
        assertEquals("\"", new JSONParser("\"\\\"\"").parseString());
        assertEquals("'", new JSONParser("'\\''").parseString());
        assertEquals("'", new JSONParser("\"\\'\"").parseString());
        assertEquals(" ", new JSONParser("'\\u0020'").parseString());
        assertEquals("    ", new JSONParser("' \\u0020\\u0020 '").parseString());
        assertEquals("!", new JSONParser("'\\u0021'").parseString());
        assertEquals("\"", new JSONParser("'\\u0022'").parseString());
    }
    
    @Test
    public void testUmlaute() throws IOException {
        assertEquals("ä", new JSONParser("'\\u00e4'").parseString());
        assertEquals("Ä", new JSONParser("'\\u00c4'").parseString());
        assertEquals("ö", new JSONParser("'\\u00f6'").parseString());
        assertEquals("Ö", new JSONParser("'\\u00d6'").parseString());
    }

    @Test(expected=JSONSyntaxException.class)
    public void testInvalidNumber() throws IOException {
        new JSONParser("0123.456").parse();
    }

    @Test(expected=JSONSyntaxException.class)
    public void testInvalidNumber2() throws IOException {
        new JSONParser(".123").parse();
    }

    @Test
    public void testNumber() throws IOException {
        assertEquals(123.0, new JSONParser("123").parse());
        assertEquals(0.0, new JSONParser("0").parse());
        assertEquals(0.0, new JSONParser("0.0").parse());
        assertEquals(0.5, new JSONParser("0.5").parse());
        assertEquals(0.25, new JSONParser("0.25").parse());
        assertEquals(-0.5, new JSONParser("-0.5").parse());
        assertEquals(-0.25, new JSONParser("-0.25").parse());
        assertEquals(2.5, new JSONParser("0.25e1").parse());
        assertEquals(-2.5, new JSONParser("-0.25e1").parse());
        assertEquals(2.5, new JSONParser("25e-1").parse());
        assertEquals(-2.5, new JSONParser("-25e-1").parse());
    }

    private static JSONParser createBigDecimalParser(String str) {
        JSONParser parser = new JSONParser(str);
        parser.setParseBigDecimal(true);
        return parser;
    }
    
    private static void assertPropertyX(Object expected, Object obj) {
        assertTrue("Value should be a JSON object", obj instanceof Map);
        Object value = ((Map)obj).get("x");
        assertEquals(expected, value);
    }

    @Test
    public void testBigDecimal() throws IOException {
        assertEquals(new BigDecimal(123L), createBigDecimalParser("123").parse());
        assertEquals(BigDecimal.ZERO, createBigDecimalParser("0").parse());
        assertEquals(BigDecimal.valueOf(0, 1), createBigDecimalParser("0.0").parse());
        assertEquals(BigDecimal.valueOf(5, 1), createBigDecimalParser("0.5").parse());
        assertEquals(BigDecimal.valueOf(25, 2), createBigDecimalParser("0.25").parse());
        assertEquals(BigDecimal.valueOf(-5, 1), createBigDecimalParser("-0.5").parse());
        assertEquals(BigDecimal.valueOf(-25, 2), createBigDecimalParser("-0.25").parse());
        assertEquals(BigDecimal.valueOf(25, 1), createBigDecimalParser("0.25e1").parse());
        assertEquals(BigDecimal.valueOf(-25, 1), createBigDecimalParser("-0.25e1").parse());
        assertEquals(BigDecimal.valueOf(25, 1), createBigDecimalParser("25e-1").parse());
        assertEquals(BigDecimal.valueOf(-25, 1), createBigDecimalParser("-25e-1").parse());
    }

    @Test
    public void testBigDecimalInObject() throws IOException {
        assertPropertyX(new BigDecimal(123L), createBigDecimalParser("{'x': 123}").parse());
        assertPropertyX(BigDecimal.ZERO, createBigDecimalParser("{'x': 0}").parse());
        assertPropertyX(BigDecimal.valueOf(0, 1), createBigDecimalParser("{'x': 0.0}").parse());
        assertPropertyX(BigDecimal.valueOf(5, 1), createBigDecimalParser("{'x': 0.5}").parse());
        assertPropertyX(BigDecimal.valueOf(25, 2), createBigDecimalParser("{'x': 0.25}").parse());
        assertPropertyX(BigDecimal.valueOf(-5, 1), createBigDecimalParser("{'x': -0.5}").parse());
        assertPropertyX(BigDecimal.valueOf(-25, 2), createBigDecimalParser("{'x': -0.25}").parse());
        assertPropertyX(BigDecimal.valueOf(25, 1), createBigDecimalParser("{'x': 0.25e1}").parse());
        assertPropertyX(BigDecimal.valueOf(-25, 1), createBigDecimalParser("{'x': -0.25e1}").parse());
        assertPropertyX(BigDecimal.valueOf(25, 1), createBigDecimalParser("{'x': 25e-1}").parse());
        assertPropertyX(BigDecimal.valueOf(-25, 1), createBigDecimalParser("{'x': -25e-1}").parse());
    }

    @Test
    public void testBoolean() throws IOException {
        assertEquals(Boolean.TRUE, new JSONParser("true").parse());
        assertEquals(Boolean.FALSE, new JSONParser("false").parse());
    }

    @Test
    public void testNull() throws IOException {
        assertEquals(JSONNull.INSTANCE, new JSONParser("null").parse());
        assertTrue(new JSONParser("{'abc':null}").parseObject().isNull("abc"));
    }

    @Test
    public void testDate() throws IOException {
        long millis = System.currentTimeMillis();
        Date date = new Date(millis - millis%1000L);
        SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss'Z'");
        df.setTimeZone(TimeZone.getTimeZone("UTC"));
        String str = df.format(date);

        assertEquals(date, JSONConversion.convertDate(str));

        String json = "{'date': '" + str.replaceAll("'", "\\'") + "'}";

        assertEquals(date, new JSONParser(json).parseObject().getDate("date"));
    }
    
    @Test
    public void testLenientParser() throws IOException {
        JSONParser parser = new JSONParser("{abc = 1; def => 'xyz', ghi: true}");
        parser.setLenient(true);
        JSONObject obj = parser.parseObject();
        assertEquals(3, obj.size());
        assertEquals(1, obj.getInt("abc"));
        assertEquals("xyz", obj.get("def"));
        assertEquals(Boolean.TRUE, obj.get("ghi"));
    }
}
