package net.jhorstmann.json;

import java.io.IOException;
import net.jhorstmann.json.JSONEventParser.EventType;
import org.junit.Assert;
import org.junit.Test;

public class JSONEventParserTest {

    @Test(expected = JSONSyntaxException.class)
    public void testUnexpectedEndObject() throws IOException {
        JSONEventParser parser = new JSONEventParser("}");
        Assert.assertTrue(parser.hasNextEvent());
        parser.nextEvent();
    }

    @Test(expected = JSONSyntaxException.class)
    public void testUnexpectedEndArray() throws IOException {
        JSONEventParser parser = new JSONEventParser("]");
        Assert.assertTrue(parser.hasNextEvent());
        parser.nextEvent();
    }

    @Test
    public void testNull() throws IOException {
        JSONEventParser parser = new JSONEventParser("null");
        Assert.assertTrue(parser.hasNextEvent());
        Assert.assertEquals(EventType.VALUE_NULL, parser.nextEvent());
        Assert.assertFalse(parser.hasNextEvent());
    }

    @Test
    public void testNumber() throws IOException {
        JSONEventParser parser = new JSONEventParser("123.456");
        Assert.assertTrue(parser.hasNextEvent());
        Assert.assertEquals(EventType.VALUE_NUMBER, parser.nextEvent());
        Assert.assertEquals(Double.valueOf(123.456), parser.getNumber());
        Assert.assertFalse(parser.hasNextEvent());
    }

    @Test
    public void testString() throws IOException {
        JSONEventParser parser = new JSONEventParser("\"abc\"");
        Assert.assertTrue(parser.hasNextEvent());
        Assert.assertEquals(EventType.VALUE_STRING, parser.nextEvent());
        Assert.assertEquals("abc", parser.getString());
        Assert.assertFalse(parser.hasNextEvent());
    }

    @Test
    public void testEmptyObject() throws IOException {
        JSONEventParser parser = new JSONEventParser("{}");
        Assert.assertTrue(parser.hasNextEvent());
        Assert.assertEquals(EventType.START_OBJECT, parser.nextEvent());
        Assert.assertTrue(parser.hasNextEvent());
        Assert.assertEquals(EventType.END_OBJECT, parser.nextEvent());
        Assert.assertFalse(parser.hasNextEvent());
    }

    @Test
    public void testEmptyArray() throws IOException {
        JSONEventParser parser = new JSONEventParser("[]");
        Assert.assertTrue(parser.hasNextEvent());
        Assert.assertEquals(EventType.START_ARRAY, parser.nextEvent());
        Assert.assertTrue(parser.hasNextEvent());
        Assert.assertEquals(EventType.END_ARRAY, parser.nextEvent());
        Assert.assertFalse(parser.hasNextEvent());

    }

    @Test
    public void testEmptyArrayWS() throws IOException {
        JSONEventParser parser = new JSONEventParser("[\r\n  \r\n]");
        Assert.assertTrue(parser.hasNextEvent());
        Assert.assertEquals(EventType.START_ARRAY, parser.nextEvent());
        Assert.assertTrue(parser.hasNextEvent());
        Assert.assertEquals(EventType.END_ARRAY, parser.nextEvent());
        Assert.assertFalse(parser.hasNextEvent());
    }

    @Test
    public void testEmptyObjectWS() throws IOException {
        JSONEventParser parser = new JSONEventParser("{\r\n  }");
        Assert.assertTrue(parser.hasNextEvent());
        Assert.assertEquals(EventType.START_OBJECT, parser.nextEvent());
        Assert.assertTrue(parser.hasNextEvent());
        Assert.assertEquals(EventType.END_OBJECT, parser.nextEvent());
        Assert.assertFalse(parser.hasNextEvent());
    }

    @Test
    public void testObjectWithSingleKey() throws IOException {
        JSONEventParser parser = new JSONEventParser("{\"a\": 123.0 }");
        Assert.assertTrue(parser.hasNextEvent());
        Assert.assertEquals(EventType.START_OBJECT, parser.nextEvent());
        Assert.assertTrue(parser.hasNextEvent());
        Assert.assertEquals(EventType.PROPERTY, parser.nextEvent());
        Assert.assertEquals("a", parser.getString());
        Assert.assertTrue(parser.hasNextEvent());
        Assert.assertEquals(EventType.VALUE_NUMBER, parser.nextEvent());
        Assert.assertEquals(Double.valueOf(123.0), parser.getNumber());
        Assert.assertTrue(parser.hasNextEvent());
        Assert.assertEquals(EventType.END_OBJECT, parser.nextEvent());
        Assert.assertFalse(parser.hasNextEvent());
    }

    @Test
    public void testObject() throws IOException {
        JSONEventParser parser = new JSONEventParser("{\"a\": 123.0, \"b\": \"c\" }");
        Assert.assertTrue(parser.hasNextEvent());
        Assert.assertEquals(EventType.START_OBJECT, parser.nextEvent());
        Assert.assertTrue(parser.hasNextEvent());
        Assert.assertEquals(EventType.PROPERTY, parser.nextEvent());
        Assert.assertEquals("a", parser.getString());
        Assert.assertTrue(parser.hasNextEvent());
        Assert.assertEquals(EventType.VALUE_NUMBER, parser.nextEvent());
        Assert.assertEquals(Double.valueOf(123.0), parser.getNumber());
        Assert.assertTrue(parser.hasNextEvent());
        Assert.assertEquals(EventType.PROPERTY, parser.nextEvent());
        Assert.assertEquals("b", parser.getString());
        Assert.assertTrue(parser.hasNextEvent());
        Assert.assertEquals(EventType.VALUE_STRING, parser.nextEvent());
        Assert.assertEquals("c", parser.getString());
        Assert.assertTrue(parser.hasNextEvent());
        Assert.assertEquals(EventType.END_OBJECT, parser.nextEvent());
        Assert.assertFalse(parser.hasNextEvent());
    }

    @Test
    public void testArrayWithSingleNumber() throws IOException {
        JSONEventParser parser = new JSONEventParser("[ 123.0 ]");
        Assert.assertTrue(parser.hasNextEvent());
        Assert.assertEquals(EventType.START_ARRAY, parser.nextEvent());
        Assert.assertTrue(parser.hasNextEvent());
        Assert.assertEquals(EventType.VALUE_NUMBER, parser.nextEvent());
        Assert.assertEquals(Double.valueOf(123.0), parser.getNumber());
        Assert.assertTrue(parser.hasNextEvent());
        Assert.assertEquals(EventType.END_ARRAY, parser.nextEvent());
        Assert.assertFalse(parser.hasNextEvent());
    }

    @Test
    public void testArrayWithSingleString() throws IOException {
        JSONEventParser parser = new JSONEventParser("[\"abc\"]");
        Assert.assertTrue(parser.hasNextEvent());
        Assert.assertEquals(EventType.START_ARRAY, parser.nextEvent());
        Assert.assertTrue(parser.hasNextEvent());
        Assert.assertEquals(EventType.VALUE_STRING, parser.nextEvent());
        Assert.assertEquals("abc", parser.getString());
        Assert.assertTrue(parser.hasNextEvent());
        Assert.assertEquals(EventType.END_ARRAY, parser.nextEvent());
        Assert.assertFalse(parser.hasNextEvent());
    }

    @Test
    public void testArrayWithSingleTrue() throws IOException {
        JSONEventParser parser = new JSONEventParser("[true]");
        Assert.assertTrue(parser.hasNextEvent());
        Assert.assertEquals(EventType.START_ARRAY, parser.nextEvent());
        Assert.assertTrue(parser.hasNextEvent());
        Assert.assertEquals(EventType.VALUE_TRUE, parser.nextEvent());
        Assert.assertTrue(parser.hasNextEvent());
        Assert.assertEquals(EventType.END_ARRAY, parser.nextEvent());
        Assert.assertFalse(parser.hasNextEvent());
    }

    @Test
    public void testArrayWithSingleFalse() throws IOException {
        JSONEventParser parser = new JSONEventParser("[false]");
        Assert.assertTrue(parser.hasNextEvent());
        Assert.assertEquals(EventType.START_ARRAY, parser.nextEvent());
        Assert.assertTrue(parser.hasNextEvent());
        Assert.assertEquals(EventType.VALUE_FALSE, parser.nextEvent());
        Assert.assertTrue(parser.hasNextEvent());
        Assert.assertEquals(EventType.END_ARRAY, parser.nextEvent());
        Assert.assertFalse(parser.hasNextEvent());
    }

    @Test
    public void testArrayWithSingleNull() throws IOException {
        JSONEventParser parser = new JSONEventParser("[ null ]");
        Assert.assertTrue(parser.hasNextEvent());
        Assert.assertEquals(EventType.START_ARRAY, parser.nextEvent());
        Assert.assertTrue(parser.hasNextEvent());
        Assert.assertEquals(EventType.VALUE_NULL, parser.nextEvent());
        Assert.assertTrue(parser.hasNextEvent());
        Assert.assertEquals(EventType.END_ARRAY, parser.nextEvent());
        Assert.assertFalse(parser.hasNextEvent());
    }

    @Test
    public void testObjectMissingColon() throws IOException {
        JSONEventParser parser = new JSONEventParser("{\"a\"}");
        Assert.assertTrue(parser.hasNextEvent());
        Assert.assertEquals(EventType.START_OBJECT, parser.nextEvent());
        Assert.assertTrue(parser.hasNextEvent());
        Assert.assertEquals(EventType.PROPERTY, parser.nextEvent());
        Assert.assertEquals("a", parser.getString());
        Assert.assertTrue(parser.hasNextEvent());
        try {
            parser.nextEvent();
            Assert.fail("Expected " + JSONSyntaxException.class.getName());
        } catch (JSONSyntaxException ex) {
        }
    }

    @Test
    public void testObjectMissingColon2() throws IOException {
        JSONEventParser parser = new JSONEventParser("{\"a\" \"b\" }");
        Assert.assertTrue(parser.hasNextEvent());
        Assert.assertEquals(EventType.START_OBJECT, parser.nextEvent());
        Assert.assertTrue(parser.hasNextEvent());
        Assert.assertEquals(EventType.PROPERTY, parser.nextEvent());
        Assert.assertEquals("a", parser.getString());
        Assert.assertTrue(parser.hasNextEvent());
        try {
            parser.nextEvent();
            Assert.fail("Expected " + JSONSyntaxException.class.getName());
        } catch (JSONSyntaxException ex) {
        }
    }

    @Test
    public void testObjectInvalidComman() throws IOException {
        JSONEventParser parser = new JSONEventParser("{\"a\",}");
        Assert.assertTrue(parser.hasNextEvent());
        Assert.assertEquals(EventType.START_OBJECT, parser.nextEvent());
        Assert.assertTrue(parser.hasNextEvent());
        Assert.assertEquals(EventType.PROPERTY, parser.nextEvent());
        Assert.assertEquals("a", parser.getString());
        Assert.assertTrue(parser.hasNextEvent());
        try {
            parser.nextEvent();
            Assert.fail("Expected " + JSONSyntaxException.class.getName());
        } catch (JSONSyntaxException ex) {
        }
    }

    @Test
    public void testObjectInvalidKey() throws IOException {
        JSONEventParser parser = new JSONEventParser("{123.0}");
        Assert.assertTrue(parser.hasNextEvent());
        Assert.assertEquals(EventType.START_OBJECT, parser.nextEvent());
        Assert.assertTrue(parser.hasNextEvent());
        try {
            parser.nextEvent();
            Assert.fail("Expected " + JSONSyntaxException.class.getName());
        } catch (JSONSyntaxException ex) {
        }
    }

    @Test
    public void testObjectInvalidComma() throws IOException {
        JSONEventParser parser = new JSONEventParser("{,}");
        Assert.assertTrue(parser.hasNextEvent());
        Assert.assertEquals(EventType.START_OBJECT, parser.nextEvent());
        Assert.assertTrue(parser.hasNextEvent());
        try {
            parser.nextEvent();
            Assert.fail("Expected " + JSONSyntaxException.class.getName());
        } catch (JSONSyntaxException ex) {
        }
    }

    @Test
    public void testObjectInvalidColon() throws IOException {
        JSONEventParser parser = new JSONEventParser("{ : }");
        Assert.assertTrue(parser.hasNextEvent());
        Assert.assertEquals(EventType.START_OBJECT, parser.nextEvent());
        Assert.assertTrue(parser.hasNextEvent());
        try {
            parser.nextEvent();
            Assert.fail("Expected " + JSONSyntaxException.class.getName());
        } catch (JSONSyntaxException ex) {
        }
    }

    @Test
    public void testObjectMissingValue() throws IOException {
        JSONEventParser parser = new JSONEventParser("{\"a\" : }");
        Assert.assertTrue(parser.hasNextEvent());
        Assert.assertEquals(EventType.START_OBJECT, parser.nextEvent());
        Assert.assertTrue(parser.hasNextEvent());
        Assert.assertEquals(EventType.PROPERTY, parser.nextEvent());
        Assert.assertEquals("a", parser.getString());
        Assert.assertTrue(parser.hasNextEvent());
        try {
            parser.nextEvent();
            Assert.fail("Expected " + JSONSyntaxException.class.getName());
        } catch (JSONSyntaxException ex) {
        }
    }

    @Test
    public void testArrayWithInvalidComma() throws IOException {
        JSONEventParser parser = new JSONEventParser("[ 123.0, ]");
        Assert.assertTrue(parser.hasNextEvent());
        Assert.assertEquals(EventType.START_ARRAY, parser.nextEvent());
        Assert.assertTrue(parser.hasNextEvent());
        Assert.assertEquals(EventType.VALUE_NUMBER, parser.nextEvent());
        Assert.assertEquals(Double.valueOf(123.0), parser.getNumber());
        Assert.assertTrue(parser.hasNextEvent());
        try {
            parser.nextEvent();
            Assert.fail("Expected " + JSONSyntaxException.class.getName());
        } catch (JSONSyntaxException ex) {
        }
    }
}
