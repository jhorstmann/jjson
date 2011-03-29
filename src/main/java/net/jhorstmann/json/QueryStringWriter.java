package net.jhorstmann.json;

import java.io.Flushable;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.lang.reflect.Array;
import java.net.URLEncoder;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.TimeZone;

public class QueryStringWriter {
        private Appendable out;
    private DateFormat df;

    public QueryStringWriter(final Appendable out) {
        this.out = out;
        this.df = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss'Z'");
        this.df.setTimeZone(TimeZone.getTimeZone("UTC"));
    }

    public static String format(String key, Object val) throws IOException {
        StringBuilder sb = new StringBuilder();
        new QueryStringWriter(sb).writeObject(key, val);
        return sb.toString();
    }

    public static String format(Map<String, ?> map) throws IOException {
        return format("", map);
    }

    public void writeMap(Map<String, ?> map) throws IOException {
        writeObject("", map);
    }

    public void writeObject(String key, Object val) throws IOException {
        writeEntry(key, val);
        if (out instanceof Flushable) {
            ((Flushable)out).flush();
        }
    }

    private void writeEntry(String key, Object val) throws IOException {
        if (val == null || val == JSONNull.INSTANCE) {
            out.append(key).append('=');
        } else if (val instanceof Boolean) {
            out.append(key).append('=').append(((Boolean)val).toString());
        } else if (val instanceof Number) {
            out.append(key).append('=').append(val.toString());
        } else if (val instanceof CharSequence) {
            out.append(key).append('=').append(escapeString((CharSequence)val));
        } else if (val instanceof Date) {
            out.append(key).append('=').append(df.format(val));
        } else if (val instanceof Map) {
            writeMap(key, (Map<String, Object>)val);
        } else if (val instanceof List) {
            writeList(key, (List)val);
        } else if (val instanceof Iterable) {
            writeIterable(key, (Iterable)val);
        } else if (val.getClass().isArray()) {
            writeArray(key, val);
        }
    }

    public void writeMap(String prefix, Map<String, Object> map) throws IOException {
        for (Iterator<Entry<String, Object>> it=map.entrySet().iterator(); it.hasNext(); ) {
            Map.Entry<String, ?> me = it.next();
            String key = escapeString(me.getKey());
            Object val = me.getValue();

            writeEntry(prefix == null || prefix.length() == 0 ? key : (prefix + "." + key), val);
            if (it.hasNext()) {
                out.append('&');
            }
        }
    }

    public void writeIterable(String prefix, Iterable<Object> list) throws IOException {
        for (Iterator it=list.iterator(); it.hasNext(); ) {
            Object obj = it.next();
            writeEntry(prefix + "[]", obj);
            if (it.hasNext()) {
                out.append('&');
            }
        }
    }

    private void writeEntry(String prefix, int idx, Object obj) throws IOException {
        writeEntry(prefix == null || prefix.length() == 0 ? String.valueOf(idx) : prefix + "[" + idx + "]", obj);
    }

    public void writeList(String prefix, Iterable<Object> list) throws IOException {
        int idx = 0;
        for (Iterator it=list.iterator(); it.hasNext(); ) {
            Object obj = it.next();
            writeEntry(prefix, idx, obj);
            ++idx;
            if (it.hasNext()) {
                out.append('&');
            }
        }
    }

    public void writeArray(String prefix, Object array) throws IOException {
        for (int i=0, len=Array.getLength(array); i<len; i++) {
            Object obj = Array.get(array, i);
            writeEntry(prefix, i, obj);
            if (i < len-1) {
                out.append('&');
            }
        }
    }

    private String escapeString(CharSequence cs) {
        try {
            return URLEncoder.encode(cs.toString(), "UTF-8");
        } catch (UnsupportedEncodingException ex) {
            throw new RuntimeException("UTF-8 required", ex);
        }
    }
}
