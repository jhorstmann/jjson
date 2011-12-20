package net.jhorstmann.json;

import java.io.IOException;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import junit.framework.Assert;
import net.jhorstmann.json.JSONParser.ValueType;
import org.junit.Test;

public class JSONParserCallbackTest {
    @Test
    public void testParseObject() throws IOException {
        final Map obj = new LinkedHashMap();
        new JSONParser("{'a': 123.0, 'b': 'test', 'c': false, 'd': null, 'e': {'key': 'value'}, 'f': [true, false]}").parseObject(new JSONParser.ObjectCallback() {
            public void property(JSONParser parser, String property, ValueType type) throws IOException {
                if ("a".equals(property)) {
                    Assert.assertEquals(ValueType.NUMBER, type);
                    obj.put(property, parser.parseBigDecimal());
                } else if ("b".equals(property)) {
                    Assert.assertEquals(ValueType.STRING, type);
                    obj.put(property, parser.parseString());
                } else if ("c".equals(property)) {
                    Assert.assertEquals(ValueType.BOOLEAN, type);
                    obj.put(property, parser.parseBoolean());
                } else if ("d".equals(property)) {
                    Assert.assertEquals(ValueType.NULL, type);
                    obj.put(property, parser.parseNull());
                } else if ("e".equals(property)) {
                    Assert.assertEquals(ValueType.OBJECT, type);
                    obj.put(property, parser.parseObject());
                } else if ("f".equals(property)) {
                    Assert.assertEquals(ValueType.ARRAY, type);
                    obj.put(property, parser.parseArray());
                }
            }
        });
        Assert.assertEquals(6, obj.size());
        Assert.assertEquals(new BigDecimal("123.0"), obj.get("a"));
        Assert.assertEquals("test", obj.get("b"));
        Assert.assertEquals(Boolean.FALSE, obj.get("c"));
        Assert.assertEquals(JSONNull.INSTANCE, obj.get("d"));
        Assert.assertEquals(Collections.singletonMap("key", "value"), obj.get("e"));
        Assert.assertEquals(Arrays.asList(true, false), obj.get("f"));
    }
    
    @Test
    public void testParseArray() throws IOException {
        final List expected = Arrays.asList(123.0, "test", true, JSONNull.INSTANCE, Collections.singletonMap("key", "value"), Arrays.asList(true, false));
        final List list = new ArrayList(6);
        new JSONParser("[123.0, 'test', true, null, {'key': 'value'}, [true, false]]").parseArray(new JSONParser.ArrayCallback() {

            public void item(JSONParser parser, int idx, ValueType type) throws IOException {
                switch (idx) {
                    case 0:
                        Assert.assertEquals(ValueType.NUMBER, type);
                        list.add(parser.parseDouble());
                        break;
                    case 1:
                        Assert.assertEquals(ValueType.STRING, type);
                        list.add(parser.parseString());
                        break;
                    case 2:
                        Assert.assertEquals(ValueType.BOOLEAN, type);
                        list.add(parser.parseBoolean());
                        break;
                    case 3:
                        Assert.assertEquals(ValueType.NULL, type);
                        list.add(parser.parseNull());
                        break;
                    case 4:
                        Assert.assertEquals(ValueType.OBJECT, type);
                        list.add(parser.parseObject());
                        break;
                    case 5:
                        Assert.assertEquals(ValueType.ARRAY, type);
                        list.add(parser.parseArray());
                        break;
                    default:
                        Assert.fail("Unexpected index");
                }
            }
        });
        Assert.assertEquals(expected, list);
    }
}
