package net.jhorstmann.json;

import java.io.IOException;
import java.math.BigDecimal;
import java.util.Calendar;
import java.util.Date;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.TimeZone;

public class JSONObject extends LinkedHashMap<String, Object> {
    public int getInt(String key) {
        Object o = get(key);
        if (o == null) {
            return 0;
        } else if (o instanceof Number) {
            return ((Number)o).intValue();
        } else if (o instanceof String) {
            return Integer.parseInt(key);
        } else {
            throw new NumberFormatException();
        }
    }

    public long getLong(String key) {
        Object o = get(key);
        if (o == null) {
            return 0L;
        } else if (o instanceof Number) {
            return ((Number)o).longValue();
        } else if (o instanceof String) {
            return Long.parseLong(key);
        } else {
            throw new NumberFormatException();
        }
    }

    public double getDouble(String key) {
        Object o = get(key);
        if (o == null) {
            return 0.0;
        } else if (o instanceof Number) {
            return ((Number)o).doubleValue();
        } else if (o instanceof String) {
            return Double.parseDouble(key);
        } else {
            throw new NumberFormatException();
        }
    }

    public BigDecimal getDecimal(String key) {
        Object o = get(key);
        if (o == null) {
            return BigDecimal.ZERO;
        } else if (o instanceof BigDecimal) {
            return (BigDecimal)o;
        } else if (o instanceof Number) {
            return new BigDecimal(o.toString());
        } else if (o instanceof String) {
            return new BigDecimal((String)o);
        } else {
            throw new NumberFormatException();
        }
    }

    public String getString(String key) {
        Object o = get(key);
        return o == null ? null : String.valueOf(o);
    }

    public List getList(String key) {
        return (List)get(key);
    }

    public Object[] getArray(String key) {
        return getList(key).toArray();
    }

    public JSONObject[] getObjectArray(String key) {
        List list = getList(key);
        return (JSONObject[])list.toArray(new JSONObject[list.size()]);
    }

    public JSONObject getObject(String key) {
        return (JSONObject)get(key);
    }

    public boolean getBoolean(String key) {
        Object o = get(key);
        if (o == null) {
            return false;
        } else if (o instanceof Boolean) {
            return ((Boolean)o).booleanValue();
        } else if (o instanceof String) {
            return !((String)o).isEmpty();
        } else if (o instanceof Number) {
            if (o instanceof Double) {
                return ((Double)o).doubleValue() != 0.0;
            } else if (o instanceof BigDecimal) {
                return ((BigDecimal)o).compareTo(BigDecimal.ZERO) != 0;
            } else {
                return ((Number)o).intValue() != 0;
            }
        } else {
            return true;
        }
    }

    public boolean isNull(String key) {
        return get(key) == JSONNull.INSTANCE;
    }

    static Date convertDate(String str) {
        if (str == null) {
            throw new IllegalArgumentException("Date string is null");
        } else if (str.startsWith("/Date(") && str.endsWith(")/")) {
            // ASP.net \/Date(123456)\/
            long millis = Long.parseLong(str.substring(6, str.length()-2));
            return new Date(millis);
        } else if (str.length() == 20 && str.charAt(4) == '-' && str.charAt(7) == '-' && str.charAt(10) == 'T' && str.charAt(13) == ':' && str.charAt(16) == ':' && str.charAt(19) == 'Z') {
            // ISO 8601 timestamp "yyyy-MM-dd'T'HH:mm:ss'Z'"
            int  year  = Integer.parseInt(str.substring( 0,  4));
            int  month = Integer.parseInt(str.substring( 5,  7));
            int  day   = Integer.parseInt(str.substring( 8, 10));
            int  hour  = Integer.parseInt(str.substring(11, 13));
            int  min   = Integer.parseInt(str.substring(14, 16));
            int  sec   = Integer.parseInt(str.substring(17, 19));

            Calendar cal = Calendar.getInstance(TimeZone.getTimeZone("UTC"));
            cal.set(Calendar.YEAR, year);
            cal.set(Calendar.MONTH, month-1);
            cal.set(Calendar.DAY_OF_MONTH, day);
            cal.set(Calendar.HOUR_OF_DAY, hour);
            cal.set(Calendar.MINUTE, min);
            cal.set(Calendar.SECOND, sec);
            cal.set(Calendar.MILLISECOND, 0);

            return cal.getTime();
        } else {
            throw new IllegalArgumentException("Invalid date format");
        }
    }

    public Date getDate(String key) {
        String str = (String)get(key);
        return str == null ? null : convertDate(str);
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
