package net.jhorstmann.json;

import java.io.IOException;
import java.math.BigDecimal;
import java.util.Date;
import java.util.LinkedHashMap;
import java.util.List;

public class JSONObject extends LinkedHashMap<String, Object> {
    public int getInt(String key) {
        Object o = get(key);
        return JSONConversion.convertInt(o);
    }

    public long getLong(String key) {
        Object o = get(key);
        return JSONConversion.convertLong(o);
    }

    public double getDouble(String key) {
        Object o = get(key);
        return JSONConversion.convertDouble(o);
    }

    public BigDecimal getDecimal(String key) {
        Object o = get(key);
        return JSONConversion.convertDecimal(o);
    }

    public String getString(String key) {
        Object o = get(key);
        return JSONConversion.convertString(o);
    }

    public List getList(String key) {
        Object o = get(key);
        return JSONConversion.convertList(o);
    }

    public JSONObject getObject(String key) {
        Object o = get(key);
        return JSONConversion.convertObject(o);
    }

    public boolean getBoolean(String key) {
        Object o = get(key);
        return JSONConversion.convertBoolean(o);
    }

    public boolean isNull(String key) {
        return get(key) == JSONNull.INSTANCE;
    }

    public Date getDate(String key) {
        Object o = get(key);
        return JSONConversion.convertDate(o);
    }

    @Override
    public String toString() {
        try {
            return JSONUtils.format(this, true);
        } catch (IOException ex) {
            throw new RuntimeException(ex.getClass().getSimpleName() + " in " + getClass().getSimpleName() + ".toString()", ex);
        }
    }
}
