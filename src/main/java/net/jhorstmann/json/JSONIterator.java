package net.jhorstmann.json;

import java.math.BigDecimal;
import java.util.Iterator;

public class JSONIterator implements Iterator {

    protected final Iterator iter;

    public JSONIterator(Iterator iter) {
        this.iter = iter;
    }

    public boolean hasNext() {
        return iter.hasNext();
    }

    public Object next() {
        return iter.next();
    }

    public void remove() {
        iter.remove();
    }

    public int nextInt() {
        Object o = next();
        return JSONConversion.convertInt(o);
    }

    public long nextLong() {
        Object o = next();
        return JSONConversion.convertLong(o);
    }

    public double nextDouble() {
        Object o = next();
        return JSONConversion.convertDouble(o);
    }

    public BigDecimal nextDecimal() {
        Object o = next();
        return JSONConversion.convertDecimal(o);
    }

    public boolean nextBoolean() {
        Object o = next();
        return JSONConversion.convertBoolean(o);
    }

    public String nextString() {
        Object o = next();
        return JSONConversion.convertString(o);
    }

    public JSONArray nextList() {
        Object o = next();
        return JSONConversion.convertList(o);
    }

    public JSONObject nextObject() {
        Object o = next();
        return JSONConversion.convertObject(o);
    }

    public boolean nextIsNull() {
        return next() == JSONNull.INSTANCE;
    }
}
