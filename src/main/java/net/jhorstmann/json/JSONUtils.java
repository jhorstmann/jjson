package net.jhorstmann.json;

import java.beans.BeanInfo;
import java.beans.IntrospectionException;
import java.beans.Introspector;
import java.beans.PropertyDescriptor;
import java.io.IOException;
import java.lang.reflect.Method;
import java.util.Map;
import java.util.TreeMap;

public class JSONUtils {

    private JSONUtils() {
    }

    public static String format(Object o) throws IOException {
        StringBuilder sb = new StringBuilder();
        new JSONWriter(sb).write(o);
        return sb.toString();
    }

    public static String format(Object o, boolean pretty) throws IOException {
        StringBuilder sb = new StringBuilder();
        new JSONWriter(sb, pretty).write(o);
        return sb.toString();
    }

    public static String formatBean(Object o) throws IOException {
        StringBuilder sb = new StringBuilder();
        new JSONWriter(sb).writeBean(o);
        return sb.toString();
    }

    public static String formatBean(Object o, boolean pretty) throws IOException {
        StringBuilder sb = new StringBuilder();
        new JSONWriter(sb, pretty).writeBean(o);
        return sb.toString();
    }
    
    public static void writeChar(char ch, Appendable out) throws IOException {
        switch (ch) {
            case '\b': out.append("\\b"); break;
            case '\f': out.append("\\f"); break;
            case '\n': out.append("\\n"); break;
            case '\r': out.append("\\r"); break;
            case '\t': out.append("\\t"); break;
            case '"' : out.append("\\\""); break;
            case '\\': out.append("\\\\"); break;
            default:
                if (ch < 32 || ch >= 127) {
                    String tmp = Integer.toHexString(ch);
                    out.append("\\u");
                    for (int j=tmp.length(); j<4; j++) {
                        out.append('0');
                    }
                    out.append(tmp);
                }
                else {
                    out.append(ch);
                }
        }
    }

    public static void writeString(CharSequence s, Appendable out) throws IOException {
        out.append('"');
        writeStringContent(s, out);
        out.append('"');
    }
    
    public static void writeStringContent(CharSequence s, Appendable out) throws IOException {
        for (int i=0, len=s.length(); i<len; i++) {
            char ch = s.charAt(i);
            writeChar(ch, out);
        }
    }

    public static String escapeChar(char ch) {
        try {
            StringBuilder sb = new StringBuilder(6);
            writeChar(ch, sb);
            return sb.toString();
        } catch (IOException ex) {
            throw new IllegalStateException("Appending to a StringBuilder threw an IOException", ex);
        }
    }

    public static String escapeString(CharSequence s) {
        try {
            StringBuilder sb = new StringBuilder(s.length()+16);
            writeString(s, sb);
            return sb.toString();
        } catch (IOException ex) {
            throw new IllegalStateException("Appending to a StringBuilder threw an IOException", ex);
        }
    }
    
    public static String escapeStringContent(CharSequence s) {
        try {
            StringBuilder sb = new StringBuilder(s.length()+16);
            writeStringContent(s, sb);
            return sb.toString();
        } catch (IOException ex) {
            throw new IllegalStateException("Appending to a StringBuilder threw an IOException", ex);
        }
    }

    static Map<String, Method> getProperties(Object o) throws JSONReflectionException {
        try {
            BeanInfo beanInfo = Introspector.getBeanInfo(o.getClass());
            PropertyDescriptor[] properties = beanInfo.getPropertyDescriptors();
            Map<String, Method> result = new TreeMap<String, Method>();
            for (int i=0, len=properties.length; i<len; i++) {
                String name = properties[i].getName();
                Method read = properties[i].getReadMethod();
                // Filter Object#getClass()
                if (read != null && read.getDeclaringClass() != Object.class) {
                    result.put(name, read);
                }
            }
            return result;
        } catch (IntrospectionException ex) {
            throw new JSONReflectionException(ex);
        }
    }
}
