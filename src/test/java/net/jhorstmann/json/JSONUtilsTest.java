package net.jhorstmann.json;

import java.util.Arrays;
import java.util.Map;
import java.util.LinkedHashMap;
import java.util.Collections;
import java.util.List;
import java.util.LinkedList;
import java.io.IOException;
import org.junit.Test;
import static org.junit.Assert.*;

public class JSONUtilsTest {
    static class TestBean {
        private double doubleProperty;
        private String stringProperty;
        TestBean(Double d, String s) {
            this.doubleProperty = d;
            this.stringProperty = s;
        }

        public double getDoubleProperty() {
            return doubleProperty;
        }

        public void setDoubleProperty(double d) {
            this.doubleProperty = d;
        }

        public String getStringProperty() {
            return stringProperty;
        }
    }

    @Test
    public void testEscapeString() throws IOException {
        assertEquals("\"\\u0001\"", JSONUtils.escapeString("\001"));
        assertEquals("\"\\\"\"", JSONUtils.escapeString("\""));
        assertEquals("\"\\r\\n\"", JSONUtils.escapeString("\r\n"));
        assertEquals("\"\\f\"", JSONUtils.escapeString("\014"));
    }
    
    @Test
    public void testEmptyMap() throws IOException {
        assertEquals("{}", JSONUtils.format(Collections.EMPTY_MAP));
    }

    @Test
    public void testMap1() throws IOException {
        assertEquals("{\"abc\":123.0}", JSONUtils.format(Collections.singletonMap("abc", 123.0)));
    }

    @Test
    public void testMap2() throws IOException {
        Map map = new LinkedHashMap();
        map.put("abc", 123.0);
        map.put("def", 456.0);
        assertEquals( "{\"abc\":123.0,\"def\":456.0}", JSONUtils.format(map));
    }

    @Test
    public void testJSONObject() throws IOException {
        JSONObject json = new JSONObject();
        json.put("abc", 123.0);
        json.put("def", 456.0);
        assertEquals("{\"abc\":123.0,\"def\":456.0}", JSONUtils.format(json));
    }

    @Test
    public void testEmptyList() throws IOException {
        assertEquals("[]", JSONUtils.format(Collections.EMPTY_LIST));
    }

    @Test
    public void testList1() throws IOException {
        assertEquals("[\"abc\"]", JSONUtils.format(Collections.singletonList("abc")));
    }

    @Test
    public void testList2() throws IOException {
        assertEquals("[\"abc\",123.0]", JSONUtils.format(Arrays.asList("abc", 123.0)));
    }

    @Test
    public void testEmptyArray() throws IOException {
        assertEquals("[]", JSONUtils.format(new String[0]));
    }

    @Test
    public void testArray1() throws IOException {
        assertEquals("[\"abc\"]", JSONUtils.format(new String[]{"abc"}));
    }

    @Test
    public void testArray2() throws IOException {
        assertEquals("[\"abc\",\"def\"]", JSONUtils.format(new String[]{"abc", "def"}));
    }

    @Test
    public void testBean() throws IOException {
        TestBean bean = new TestBean(0.0, null);
        String expected = "{\"doubleProperty\":0.0,\"stringProperty\":null}";
        assertEquals(expected, JSONUtils.format(bean));
    }

    @Test
    public void testBean2() throws IOException {
        TestBean bean = new TestBean(123.0, "abc");
        String expected = "{\"doubleProperty\":123.0,\"stringProperty\":\"abc\"}";
        assertEquals(expected, JSONUtils.format(bean));
    }

    @Test
    public void testEmptyBean() throws IOException {
        assertEquals("{}", JSONUtils.format(new Object()));
    }

    @Test
    public void testStringAsBean() throws IOException {
        assertEquals("{\"bytes\":[97,98,99],\"empty\":false}", JSONUtils.formatBean("abc"));
    }

    @Test
    public void testBeanPrimitiveBoolean() throws IOException {
        assertEquals("{\"test\":true}", JSONUtils.formatBean(new Object() {
            public boolean getTest() {
                return true;
            }
        }));
    }

    @Test
    public void testBeanBoolean() throws IOException {
        assertEquals("{\"test\":true}", JSONUtils.formatBean(new Object() {
            public Boolean getTest() {
                return Boolean.TRUE;
            }
        }));
    }
    
    @Test
    public void testPrettyBean() throws IOException {
        TestBean bean = new TestBean(123.0, "abc");
        System.out.println(JSONUtils.formatBean(bean, true));
    }
    
    @Test
    public void testPrettyMap() throws IOException {
        Map<String, Object> map1 = new LinkedHashMap<String, Object>();
        map1.put("test", "test");
        map1.put("num", 123.0);
        Map<String, Object> map2 = new LinkedHashMap<String, Object>();
        map2.put("test", "test");
        map2.put("num", 123.0);
        map1.put("map", map2);
        
        List<String> list1 = new LinkedList<String>();
        list1.add("abc");
        list1.add("def");
        list1.add("ghi");
        
        map2.put("list", list1);

        List<Object> list2 = new LinkedList<Object>();
        list2.add(map2);
        list2.add(map2);
        list2.add(map2);
        
        map1.put("list", list2);
        
        System.out.println(JSONUtils.format(map1, true));
    }
}
