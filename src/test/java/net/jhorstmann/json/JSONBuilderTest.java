package net.jhorstmann.json;

import net.jhorstmann.json.JSONBuilder.ListBuilder;
import net.jhorstmann.json.JSONBuilder.ObjectBuilder;
import org.junit.Test;
import static org.junit.Assert.*;

public class JSONBuilderTest {
    @Test
    public void testBuilder() {
        ObjectBuilder builder = JSONBuilder.object();
        builder.property("test", "abc")
                .property("abc", 123.0)
                .propertyFalse("false")
                .propertyTrue("true")
                .propertyNull("null");
        ObjectBuilder obj = builder.object("obj");
        obj.property("abc", 456.0);
        ListBuilder list= obj.list("list").item("abc").item(123.0).item("ghi");
        ObjectBuilder obj2 = list.object().property("def", 789.0);
        list.item("jkl");
        
        System.out.println(builder.getMap());
        System.out.println(list);
    }
}
