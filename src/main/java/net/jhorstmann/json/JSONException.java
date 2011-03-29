package net.jhorstmann.json;

import java.io.IOException;

public class JSONException extends IOException {
    public JSONException(String message, Throwable cause) {
        super(message, cause);
    }

    public JSONException(String message) {
        super(message);
    }

    public JSONException(Throwable cause) {
        super(cause);
    }
}
