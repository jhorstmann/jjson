package net.jhorstmann.json;

import java.io.IOException;
import java.io.Reader;
import java.io.StringReader;
import java.io.StringWriter;
import net.jhorstmann.json.JSONParser.ValueType;

public class JSONPrettyPrinter {

    static class PrettyPrintCallback implements JSONParser.ObjectCallback, JSONParser.ArrayCallback {

        private JSONStreamWriter jw;

        public PrettyPrintCallback(JSONStreamWriter jw) {
            this.jw = jw;
        }

        void handleValue(JSONParser parser, ValueType type) throws IOException {
            switch (type) {
                case ARRAY:
                    parser.parseArray(this);
                    break;
                case OBJECT:
                    parser.parseObject(this);
                    break;
                case STRING:
                    String str = parser.parseString();
                    jw.writeValue(str);
                    break;
                case BOOLEAN:
                    jw.writeValue(parser.parseBoolean());
                    break;
                case NUMBER:
                    jw.writeValue(parser.parseDouble());
                    break;
                case NULL:
                    jw.writeValue(parser.parseNull());
                    break;
            }

        }

        public void beginObject() throws IOException {
            jw.beginObject();
        }

        public void endObject() throws IOException {
            jw.endObject();
        }

        public void beginArray() throws IOException {
            jw.beginArray();
        }

        public void endArray() throws IOException {
            jw.endArray();
        }

        public void property(JSONParser parser, String property, ValueType type) throws IOException {
            jw.property(property);
            handleValue(parser, type);
        }

        public void item(JSONParser parser, int idx, ValueType type) throws IOException {
            handleValue(parser, type);
        }
    }

    public static void prettyprint(Reader reader, Appendable out) throws IOException {
        JSONParser parser = new JSONParser(reader);
        JSONStreamWriter writer = new JSONStreamWriter(out, true);
        PrettyPrintCallback callback = new PrettyPrintCallback(writer);
        if (parser.isObject()) {
            parser.parseObject(callback);
        } else {
            parser.parseArray(callback);
        }
    }

    public static String prettyprint(String str) throws IOException {
        StringWriter sw = new StringWriter();
        prettyprint(new StringReader(str), sw);
        return sw.toString();
    }
}