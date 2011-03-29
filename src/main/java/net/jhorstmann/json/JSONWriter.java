package net.jhorstmann.json;

import java.beans.BeanInfo;
import java.beans.IntrospectionException;
import java.beans.Introspector;
import java.beans.PropertyDescriptor;
import java.io.Flushable;
import java.io.IOException;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.PrintStream;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.nio.charset.Charset;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Iterator;
import java.util.Map;
import java.util.TimeZone;
import java.util.TreeMap;

public class JSONWriter  {
    private Appendable out;
    private DateFormat df;
    private boolean pretty;

    public JSONWriter(final Appendable out, boolean pretty) {
        this.out = out;
        this.df = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss'Z'");
        this.df.setTimeZone(TimeZone.getTimeZone("UTC"));
        this.pretty = pretty;
    }
    
    public JSONWriter(final Appendable out) {
        this(out, false);
    }

    public JSONWriter(OutputStream out, boolean pretty) {
        this(new OutputStreamWriter(out, Charset.forName("UTF-8")), pretty);
    }
    
    public JSONWriter(OutputStream out) {
        this(out, false);
    }

    public JSONWriter(PrintStream out, boolean pretty) {
        this((OutputStream)out, pretty);
    }
    
    public JSONWriter(PrintStream out) {
        this(out, false);
    }
    
    public boolean isPretty() {
        return pretty;
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
    
    private void indent(int level) throws IOException {
        if (pretty) {
            for (int i=0; i<level; i++) {
                out.append("  ");
            }
        }
    }
    
    private void newline() throws IOException {
        if (pretty) {
            out.append("\n");
        }
    }
    
    public void write(Object o) throws IOException {
        writeImpl(o, 0);
        if (out instanceof Flushable) {
            ((Flushable)out).flush();
        }
    }

    private void writeImpl(Object o, int level) throws IOException {
        if (o == null || o == JSONNull.INSTANCE) {
            out.append("null");
        }
        else if (o instanceof Boolean) {
            out.append(((Boolean)o).toString());
        }
        else if (o instanceof Number) {
            out.append(o.toString());
        }
        else if (o instanceof CharSequence) {
            writeString((CharSequence)o);
        }
        else if (o instanceof Date) {
            writeString(df.format(o));
        }
        else if (o instanceof Map) {
            writeMap((Map)o, level);
        }
        else if (o instanceof Iterable) {
            writeList((Iterable)o, level);
        }
        else if (o.getClass().isArray()) {
            writeArray(o, level);
        } else {
            writeBean(o, level);
        }
    }

    static void writeChar(char ch, Appendable out) throws IOException {
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

    static void writeString(CharSequence s, Appendable out) throws IOException {
        out.append('"');
        for (int i=0, len=s.length(); i<len; i++) {
            char ch = s.charAt(i);
            writeChar(ch, out);
        }

        out.append('"');
    }

    static String escapeChar(char ch) {
        try {
            StringBuilder sb = new StringBuilder(6);
            writeChar(ch, sb);
            return sb.toString();
        } catch (IOException ex) {
            throw new IllegalStateException("Appending to a StringBuilder threw an IOException", ex);
        }
    }

    static String escapeString(CharSequence s) {
        try {
            StringBuilder sb = new StringBuilder(s.length()+16);
            writeString(s, sb);
            return sb.toString();
        } catch (IOException ex) {
            throw new IllegalStateException("Appending to a StringBuilder threw an IOException", ex);
        }
    }

    public void writeString(CharSequence s) throws IOException {
        writeString(s, out);
    }

    private void writeComma() throws IOException {
        out.append(',');
    }

    private void writeMapEntry(String key, Object val, int level) throws IOException {
        indent(level);
        writeString(key);
        out.append(':');
        if (pretty) {
            out.append(' ');
        }
        writeImpl(val, level);
    }

    private void writeMapEntry(Map.Entry me, int level) throws IOException {
        writeMapEntry(me.getKey().toString(), me.getValue(), level);
    }

    private void writeMap(Map m, int level) throws IOException {
        out.append('{');
        newline();
        for (Iterator<Map.Entry> it=m.entrySet().iterator(); it.hasNext(); ) {
            writeMapEntry(it.next(), level+1);
            if (it.hasNext()) {
                writeComma();
            }
            newline();
        }
        indent(level);
        out.append('}');
    }
    
    public void writeMap(Map m) throws IOException {
        writeMap(m, 0);
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
    
    private void writeProperty(Object o, String name, Method m, int level) throws IOException {
        try {
            Object val = m.invoke(o);
            writeMapEntry(name, val, level);
        } catch (IllegalAccessException ex) {
            throw new JSONReflectionException(ex);
        } catch (IllegalArgumentException ex) {
            throw new JSONReflectionException(ex);
        } catch (InvocationTargetException ex) {
            throw new JSONReflectionException(ex.getTargetException());
        }
    }

    private void writeBean(Object o, int level) throws IOException {
        Map<String, Method> properties = getProperties(o);
        out.append('{');
        newline();
        for (Iterator<Map.Entry<String, Method>> it = properties.entrySet().iterator(); it.hasNext(); ) {
            Map.Entry<String, Method> me = it.next();
            writeProperty(o, me.getKey(), me.getValue(), level+1);
            if (it.hasNext()) {
                writeComma();
            }
            newline();
        }
        indent(level);
        out.append('}');
    }

    public void writeBean(Object o) throws IOException {
        writeBean(o, 0);
    }

    private void writeList(Iterable l, int level) throws IOException {
        out.append('[');
        newline();
        for (Iterator it=l.iterator(); it.hasNext(); ) {
            indent(level+1);
            writeImpl(it.next(), level+1);
            if (it.hasNext()) {
                writeComma();
            }
            newline();
        }
        indent(level);
        out.append(']');
    }
    
    public void writeList(Iterable l) throws IOException {
        writeList(l, 0);
    }

    private void writeArray(Object o, int level) throws IOException {
        writeList(new ArrayIterable(o), level);
        /*
        out.append('[');
        newline();
        for (int i=0, len=Array.getLength(o); i<len; i++) {
            indent(level+1);
            writeImpl(Array.get(o, i), level+1);
            if (i < len-1) {
                writeComma();
            }
            newline();
        }
        indent(level);
        out.append(']');
        */
    }

    public void writeArray(Object o) throws IOException {
        writeArray(o, 0);
    }
}