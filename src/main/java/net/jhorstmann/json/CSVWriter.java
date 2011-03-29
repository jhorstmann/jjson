package net.jhorstmann.json;

import java.io.Flushable;
import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.TimeZone;

public class CSVWriter {

    private static final char DEFAULT_DELIMITER = ';';
    private Appendable out;
    private DateFormat df;
    private String[] headers;
    private char delimiter;
    private boolean excelQuirk;

    public CSVWriter(Appendable out, String... headers) {
        this.out = out;
        this.df = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss'Z'");
        this.df.setTimeZone(TimeZone.getTimeZone("UTC"));
        this.delimiter = DEFAULT_DELIMITER;
        this.headers = headers;
    }

    public char getDelimiter() {
        return delimiter;
    }

    public void setDelimiter(char delimiter) {
        this.delimiter = delimiter;
    }

    private void writeHeaders() throws IOException {
        if (headers != null && headers.length > 0) {
            writeList(new ArrayIterable(headers));
        }
    }

    private void flush() throws IOException {
        if (out instanceof Flushable) {
            ((Flushable) out).flush();
        }
    }

    private void newline() throws IOException {
        out.append('\n');
    }

    public void writeBeans(List<Object> list, String[] properties) throws IOException {
        writeHeaders();
        for (Object obj : list) {
            writeBean(obj, properties);
        }
        flush();
    }

    public void writeLists(Iterable<Iterable<Object>> list) throws IOException {
        writeHeaders();
        for (Iterable<Object> line : list) {
            writeList(line);
        }
        flush();
    }

    public void writeArrays(Iterable<? extends Object> list) throws IOException {
        writeHeaders();
        for (Object obj : list) {
            writeList(new ArrayIterable(obj));
        }
        flush();
    }

    private void writeString(CharSequence s) throws IOException {
        out.append('\"');
        if (excelQuirk) {
            out.append("=\"\"");
        }
        for (int i = 0, len = s.length(); i < len; i++) {
            char ch = s.charAt(i);
            out.append(ch);
            if (ch == '"') {
                out.append(ch);
            }
        }
        if (excelQuirk) {
            out.append("\"\"");
        }
        out.append('\"');
    }

    private void writeAtom(Object o) throws IOException {
        if (o == null || o == JSONNull.INSTANCE) {
        } else if (o instanceof Boolean) {
            writeString(((Boolean) o).toString());
        } else if (o instanceof Number) {
            writeString(o.toString());
        } else if (o instanceof CharSequence) {
            writeString((CharSequence) o);
        } else if (o instanceof Date) {
            writeString(df.format(o));
        } else {
            throw new JSONException("Unsupported data type '" + o.getClass().getName() + "'");
        }
    }

    private void writeProperty(Object o, Method m) throws IOException {
        try {
            Object val = m.invoke(o);
            writeAtom(val);
        } catch (IllegalAccessException ex) {
            throw new JSONReflectionException(ex);
        } catch (IllegalArgumentException ex) {
            throw new JSONReflectionException(ex);
        } catch (InvocationTargetException ex) {
            throw new JSONReflectionException(ex.getTargetException());
        }
    }

    private void writeBean(Object o, String[] properties) throws IOException {
        Map<String, Method> beanProperties = JSONWriter.getProperties(o);
        for (int i = 0, len = properties.length; i < len; i++) {
            String name = properties[i];
            Method method = beanProperties.get(name);
            writeProperty(o, method);
            if (i < len - 1) {
                out.append(delimiter);
            }
        }
        newline();
    }

    private void writeList(Iterable<Object> line) throws IOException {
        for (Iterator<Object> it = line.iterator(); it.hasNext();) {
            Object obj = it.next();
            writeAtom(obj);
            if (it.hasNext()) {
                out.append(delimiter);
            }
        }
        newline();
    }
}
