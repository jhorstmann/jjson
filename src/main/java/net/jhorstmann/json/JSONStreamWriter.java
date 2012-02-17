package net.jhorstmann.json;

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
import java.util.SimpleTimeZone;

public class JSONStreamWriter {

    private Appendable out;
    private DateFormat df;
    private boolean pretty;
    private int level;
    private boolean needsComma;
    private boolean needsIndent;
    private boolean needsNewline;
    private String indentString;

    public JSONStreamWriter(final Appendable out, boolean pretty) {
        this.out = out;
        this.df = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss'Z'");
        this.df.setTimeZone(new SimpleTimeZone(0, "UTC"));
        this.pretty = pretty;
        this.indentString = "  ";
    }

    public JSONStreamWriter(final Appendable out) {
        this(out, false);
    }

    public JSONStreamWriter(OutputStream out, boolean pretty) {
        this(new OutputStreamWriter(out, Charset.forName("UTF-8")), pretty);
    }

    public JSONStreamWriter(OutputStream out) {
        this(out, false);
    }

    public JSONStreamWriter(PrintStream out, boolean pretty) {
        this((OutputStream)out, pretty);
    }

    public JSONStreamWriter(PrintStream out) {
        this(out, false);
    }

    public void reset() {
        needsComma = false;
        needsIndent = false;
        needsNewline = false;
        level = 0;
    }

    public boolean isPretty() {
        return pretty;
    }

    private void indent() throws IOException {
        if (pretty && needsIndent) {
            for (int i = 0; i < level; i++) {
                out.append(indentString);
            }
            needsIndent = false;
        }
    }

    private void comma() throws IOException {
        if (needsComma) {
            out.append(',');
            needsComma = false;
            needsNewline = true;
        }
    }

    private void newline() throws IOException {
        if (pretty && needsNewline) {
            out.append('\n');
            needsIndent = true;
            needsNewline = false;
        }
    }

    private void beginValue() throws IOException {
        comma();
        newline();
        indent();
    }

    private void endValue() throws IOException {
        needsComma = true;
        if (level == 0 && out instanceof Flushable) {
            ((Flushable)out).flush();
        }
    }
    
    private void beginContainer(char ch) throws IOException {
        beginValue();
        out.append(ch);
        level++;
        needsNewline = true;
    }
    
    private void endContainer(char ch) throws IOException {
        level--;
        needsNewline = true;
        newline();
        indent();
        out.append(ch);
        endValue();
    }

    public void beginObject() throws IOException {
        beginContainer('{');
    }

    public void endObject() throws IOException {
        endContainer('}');
    }

    public void beginArray() throws IOException {
        beginContainer('[');
    }

    public void endArray() throws IOException {
        endContainer(']');
    }

    public void property(String key) throws IOException {
        beginValue();
        JSONUtils.writeString(key, out);
        if (pretty) {
            out.append(": ");
        } else {
            out.append(':');
        }
    }

    public void writeValue(Object o) throws IOException {
        beginValue();
        writeValueImpl(o);
        endValue();
    }

    private void writeValueImpl(Object o) throws IOException {
        if (o == null || o == JSONNull.INSTANCE) {
            out.append("null");
        } else if (o instanceof Boolean) {
            out.append(((Boolean)o).toString());
        } else if (o instanceof Number) {
            out.append(o.toString());
        } else if (o instanceof CharSequence) {
            JSONUtils.writeString((CharSequence)o, out);
        } else if (o instanceof Date) {
            JSONUtils.writeString(df.format(o), out);
        } else if (o instanceof Map) {
            writeMap((Map)o);
        } else if (o instanceof Iterable) {
            writeList((Iterable)o);
        } else if (o.getClass().isArray()) {
            writeArray(o);
        } else {
            writeBean(o);
        }
    }

    public void writeList(Iterable iterable) throws IOException {
        beginArray();
        for (Iterator it = iterable.iterator(); it.hasNext();) {
            writeValue(it.next());
        }
        endArray();
    }

    public void writeArray(Object array) throws IOException {
        writeList(new ArrayIterable(array));
    }

    public void writeMap(Map map) throws IOException {
        beginObject();
        for (Iterator<Map.Entry> it = map.entrySet().iterator(); it.hasNext();) {
            Map.Entry entry = it.next();
            String key = String.valueOf(entry.getKey());
            property(key);
            writeValue(entry.getValue());
        }
        endObject();
    }

    public void writeBean(Object o) throws IOException {
        Map<String, Method> properties = JSONUtils.getProperties(o);
        beginObject();
        for (Iterator<Map.Entry<String, Method>> it = properties.entrySet().iterator(); it.hasNext();) {
            Map.Entry<String, Method> entry = it.next();
            Method method = entry.getValue();

            try {
                String key = String.valueOf(entry.getKey());
                Object val = method.invoke(o);
                property(key);
                writeValue(val);
            } catch (IllegalAccessException ex) {
                throw new JSONReflectionException(ex);
            } catch (IllegalArgumentException ex) {
                throw new JSONReflectionException(ex);
            } catch (InvocationTargetException ex) {
                throw new JSONReflectionException(ex.getTargetException());
            }
        }
        endObject();
    }
}
