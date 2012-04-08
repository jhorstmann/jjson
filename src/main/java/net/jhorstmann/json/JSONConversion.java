package net.jhorstmann.json;

import java.math.BigDecimal;
import java.util.Calendar;
import java.util.Collection;
import java.util.Date;
import java.util.TimeZone;

/**
 * Weakly typed conversions.
 */
class JSONConversion {

    static int convertInt(Object o) {
        if (o == null) {
            return 0;
        } else if (o instanceof Number) {
            return ((Number) o).intValue();
        } else if (o instanceof String) {
            return Integer.parseInt((String) o);
        } else {
            throw new NumberFormatException();
        }
    }

    static long convertLong(Object o) {
        if (o == null) {
            return 0L;
        } else if (o instanceof Number) {
            return ((Number) o).longValue();
        } else if (o instanceof String) {
            return Long.parseLong((String) o);
        } else {
            throw new NumberFormatException();
        }
    }

    static double convertDouble(Object o) {
        if (o == null) {
            return 0.0;
        } else if (o instanceof Number) {
            return ((Number) o).doubleValue();
        } else if (o instanceof String) {
            return Double.parseDouble((String) o);
        } else {
            throw new NumberFormatException();
        }
    }

    static BigDecimal convertDecimal(Object o) {
        if (o == null) {
            return BigDecimal.ZERO;
        } else if (o instanceof BigDecimal) {
            return (BigDecimal) o;
        } else if (o instanceof Number) {
            return new BigDecimal(o.toString());
        } else if (o instanceof String) {
            return new BigDecimal((String) o);
        } else {
            throw new NumberFormatException();
        }
    }

    static String convertString(Object o) {
        return o == null ? null : o.toString();
    }

    static JSONArray convertList(Object o) {
        if (o == null) {
            return null;
        } else if (o instanceof JSONArray) {
            return (JSONArray) o;
        } else if (o instanceof Collection) {
            JSONArray arr = new JSONArray();
            arr.addAll((Collection) o);
            return arr;
        } else {
            JSONArray arr = new JSONArray();
            arr.add(o);
            return arr;
        }
    }

    static JSONObject convertObject(Object o) {
        if (o == null) {
            return null;
        } else if (o instanceof JSONObject) {
            return (JSONObject) o;
        } else {
            throw new IllegalArgumentException("Cannot convert " + o.getClass().getName() + " to JSONObject");
        }
    }

    static Object[] convertArray(Object o) {
        if (o == null) {
            return null;
        } else if (o instanceof Collection) {
            return ((Collection) o).toArray();
        } else if (o instanceof Object[]) {
            return (Object[]) o;
        } else {
            return new Object[]{o};
        }
    }

    static boolean convertBoolean(Object o) {
        if (o == null) {
            return false;
        } else if (o instanceof Boolean) {
            return ((Boolean) o).booleanValue();
        } else if (o instanceof String) {
            return !((String) o).isEmpty();
        } else if (o instanceof Number) {
            if (o instanceof Double) {
                return ((Double) o).doubleValue() != 0.0;
            } else if (o instanceof BigDecimal) {
                return ((BigDecimal) o).compareTo(BigDecimal.ZERO) != 0;
            } else {
                return ((Number) o).intValue() != 0;
            }
        } else {
            return true;
        }
    }

    static Date convertDate(Object o) {
        return o == null ? null : convertDate(o.toString());
    }

    static Date convertDate(String str) {
        if (str == null) {
            throw new IllegalArgumentException("Date string is null");
        } else if (str.startsWith("/Date(") && str.endsWith(")/")) {
            // ASP.net \/Date(123456)\/
            long millis = Long.parseLong(str.substring(6, str.length() - 2));
            return new Date(millis);
        } else if (str.length() == 20 && str.charAt(4) == '-' && str.charAt(7) == '-' && str.charAt(10) == 'T' && str.charAt(13) == ':' && str.charAt(16) == ':' && str.charAt(19) == 'Z') {
            // ISO 8601 timestamp "yyyy-MM-dd'T'HH:mm:ss'Z'"
            int year = Integer.parseInt(str.substring(0, 4));
            int month = Integer.parseInt(str.substring(5, 7));
            int day = Integer.parseInt(str.substring(8, 10));
            int hour = Integer.parseInt(str.substring(11, 13));
            int min = Integer.parseInt(str.substring(14, 16));
            int sec = Integer.parseInt(str.substring(17, 19));

            Calendar cal = Calendar.getInstance(TimeZone.getTimeZone("UTC"));
            cal.set(Calendar.YEAR, year);
            cal.set(Calendar.MONTH, month - 1);
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
}
