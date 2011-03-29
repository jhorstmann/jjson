package net.jhorstmann.json;

public class JSONNull {
    public static final JSONNull INSTANCE = new JSONNull();

    private JSONNull() { }

    @Override
    public String toString() {
        return "null";
    }
}