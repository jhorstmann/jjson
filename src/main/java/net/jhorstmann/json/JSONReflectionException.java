package net.jhorstmann.json;

public class JSONReflectionException extends JSONException {
    public JSONReflectionException(String msg, Throwable cause) {
        super(msg, cause);
    }

    public JSONReflectionException(Throwable cause) {
        super(cause);
    }
}
