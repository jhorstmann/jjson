package net.jhorstmann.json;

import java.math.BigDecimal;
import java.util.ListIterator;

public class JSONListIterator implements ListIterator {

    private ListIterator iter;

    public JSONListIterator(ListIterator iter) {
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

    public void add(Object e) {
        iter.add(e);
    }

    public boolean hasPrevious() {
        return iter.hasPrevious();
    }

    public int nextIndex() {
        return iter.nextIndex();
    }

    public Object previous() {
        return iter.previous();
    }

    public int previousIndex() {
        return iter.previousIndex();
    }

    public void set(Object e) {
        iter.set(e);
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

    public int previousInt() {
        Object o = previous();
        return JSONConversion.convertInt(o);
    }

    public long previousLong() {
        Object o = previous();
        return JSONConversion.convertLong(o);
    }

    public double previousDouble() {
        Object o = previous();
        return JSONConversion.convertDouble(o);
    }

    public BigDecimal previousDecimal() {
        Object o = previous();
        return JSONConversion.convertDecimal(o);
    }

    public boolean previousBoolean() {
        Object o = previous();
        return JSONConversion.convertBoolean(o);
    }

    public String previousString() {
        Object o = next();
        return JSONConversion.convertString(o);
    }

    public JSONArray previousList() {
        Object o = previous();
        return JSONConversion.convertList(o);
    }

    public JSONObject previousObject() {
        Object o = previous();
        return JSONConversion.convertObject(o);
    }

    public boolean previousIsNull() {
        return previous() == JSONNull.INSTANCE;
    }
}
