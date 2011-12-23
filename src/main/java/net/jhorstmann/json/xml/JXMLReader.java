package net.jhorstmann.json.xml;

import java.io.IOException;
import java.io.Reader;
import java.util.Locale;
import net.jhorstmann.json.JSONParser;
import net.jhorstmann.json.JSONParser.ArrayCallback;
import net.jhorstmann.json.JSONParser.ObjectCallback;
import net.jhorstmann.json.JSONParser.ValueType;
import net.jhorstmann.json.JSONUtils;
import org.xml.sax.SAXException;
import org.xml.sax.SAXNotRecognizedException;
import org.xml.sax.SAXNotSupportedException;
import org.xml.sax.helpers.AttributesImpl;

/**
 * XMLReader implementation that reports JSON data according to the format defined at http://xml.calldei.com/JsonXML
 */
public class JXMLReader extends AbstractXMLReader {

    public static final String PROPERTY_ESCAPE_STRINGS = "http://net.jhorstmann/jjson/property/escape-strings";

    private final ObjectCallback objectCallback;
    private final ArrayCallback arrayCallback;
    private boolean escapeStrings;

    public JXMLReader() {
        this.objectCallback = new ObjectCallbackImpl(this);
        this.arrayCallback = new ArrayCallbackImpl(this);
    }

    public boolean getFeature(String name) throws SAXNotRecognizedException, SAXNotSupportedException {
        throw new SAXNotRecognizedException();
    }

    public void setFeature(String name, boolean value) throws SAXNotRecognizedException, SAXNotSupportedException {
        throw new SAXNotRecognizedException();
    }

    public Object getProperty(String name) throws SAXNotRecognizedException, SAXNotSupportedException {
        if (PROPERTY_ESCAPE_STRINGS.equals(name)) {
            return Boolean.valueOf(escapeStrings);
        } else {
            throw new SAXNotRecognizedException();
        }
    }

    public void setProperty(String name, Object value) throws SAXNotRecognizedException, SAXNotSupportedException {
        if (PROPERTY_ESCAPE_STRINGS.equals(name)) {
            escapeStrings = ((Boolean)value).booleanValue();
        } else {
            throw new SAXNotRecognizedException();
        }
    }

    public void parse(Reader reader) throws IOException, SAXException {
        JSONParser parser = new JSONParser(reader);
        try {
            contentHandler.startDocument();

            if (parser.isObject()) {
                contentHandler.startElement("", "object", "object", EMPTY_ATTRIBUTES);
                parser.parseObject(objectCallback);
                contentHandler.endElement("", "object", "object");
            } else if (parser.isArray()) {
                contentHandler.startElement("", "array", "array", EMPTY_ATTRIBUTES);
                parser.parseArray(arrayCallback);
                contentHandler.endElement("", "array", "array");
            } else {
                throw new SAXException("Not a JSON object or array");
            }

            contentHandler.endDocument();
        } catch (WrappedSaxException ex) {
            throw ex.getSAXException();
        } finally {
            reader.close();
        }
    }
    
    private boolean needsEscaping(CharSequence str) {
        for (int i=0, len=str.length(); i<len; i++) {
            int ch = str.charAt(i);
            if (ch <= 0x08 || (ch >= 0x0B && ch <= 0x1F)) {
                return true;
            }
        }
        return false;
    }

    void handleValue(JSONParser parser, ValueType type) throws SAXException, IOException {
        String typeName = type.name().toLowerCase(Locale.ENGLISH);
        contentHandler.startElement("", typeName, typeName, EMPTY_ATTRIBUTES);
        handleValueContent(parser, type);
        contentHandler.endElement("", typeName, typeName);
    }

    void handleValueContent(JSONParser parser, ValueType type) throws IOException, SAXException {
        char[] content;
        switch (type) {
            case ARRAY:
                parser.parseArray(arrayCallback);
                break;
            case OBJECT:
                parser.parseObject(objectCallback);
                break;
            case STRING:
                String str = parser.parseString();
                if (escapeStrings && needsEscaping(str)) {
                    content = JSONUtils.escapeStringContent(str).toCharArray();
                } else {
                    content = str.toCharArray();
                }
                contentHandler.characters(content, 0, content.length);
                break;
            case BOOLEAN:
                content = parser.parseBoolean().toString().toCharArray();
                contentHandler.characters(content, 0, content.length);
                break;
            case NUMBER:
                content = String.valueOf(parser.parseDouble()).toCharArray();
                contentHandler.characters(content, 0, content.length);
                break;
            case NULL:
                parser.parseNull();
                break;
        }
    }

    static class WrappedSaxException extends RuntimeException {

        public WrappedSaxException(SAXException cause) {
            super(cause);
        }

        public SAXException getSAXException() {
            return (SAXException)getCause();
        }
    }

    static class ObjectCallbackImpl implements ObjectCallback {

        private JXMLReader reader;

        public ObjectCallbackImpl(JXMLReader reader) {
            this.reader = reader;
        }

        public void property(JSONParser parser, String property, ValueType type) throws IOException {
            try {
                AttributesImpl attr = new AttributesImpl();
                attr.addAttribute("", "name", "name", "CDATA", property);

                reader.getContentHandler().startElement("", "member", "member", attr);
                reader.handleValue(parser, type);
                reader.getContentHandler().endElement("", "member", "member");
            } catch (SAXException ex) {
                throw new WrappedSaxException(ex);
            }
        }
    }

    static class ArrayCallbackImpl implements ArrayCallback {

        private JXMLReader reader;

        public ArrayCallbackImpl(JXMLReader reader) {
            this.reader = reader;
        }

        public void item(JSONParser parser, int idx, ValueType type) throws IOException {
            try {
                reader.handleValue(parser, type);
            } catch (SAXException ex) {
                throw new WrappedSaxException(ex);
            }
        }
    }


}
