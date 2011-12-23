package net.jhorstmann.json;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.io.StringReader;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.List;

public class JSONParser extends AbstractParser {
    
    public enum ValueType {
        NULL, BOOLEAN, NUMBER, STRING, ARRAY, OBJECT
    }
    
    public interface ArrayCallback {
        void item(JSONParser parser, int idx, ValueType type) throws IOException;
    }

    public interface ObjectCallback {
        void property(JSONParser parser, String property, ValueType type) throws IOException;
    }

    private boolean lenient;

    public JSONParser(String str) {
        this(new StringReader(str));
    }

    public JSONParser(InputStream in) {
        this(new InputStreamReader(in, Charset.forName("UTF-8")));
    }

    public JSONParser(Reader reader) {
        super(reader);
    }

    public JSONParser(CharSequence cs) {
        this(cs.toString());
    }

    public boolean isLenient() {
        return lenient;
    }

    public void setLenient(boolean lenient) {
        this.lenient = lenient;
    }

    public Object parse() throws IOException {
        Object o = parseValue();

        int ch = peekToken();
        if (ch != -1) {
            throw createSyntaxException(ch, -1);
        }

        return o;
    }

    public Object parseAtom() throws IOException {
        int ch = peekToken();
        return parseAtom(ch);
    }

    private Object parseAtom(int ch) throws IOException {
        switch (ch) {
            case 'f': consume("false"); return Boolean.FALSE;
            case 't': consume("true");  return Boolean.TRUE;
            case 'n': consume("null");  return JSONNull.INSTANCE;
            case '+': case '-':
                return parseSignedDecimal(ch);
            case '0': case '1': case '2': case '3': case '4':
            case '5': case '6': case '7': case '8': case '9':
                return parseUnsignedDecimal(ch);
            case '\'':
            case '"':
                return parseStringImpl(ch);
            default:
                throw createSyntaxException(ch);
        }
    }

    public Object parseValue() throws IOException {
        int ch = peekToken();
        if (ch == '{') {
            return parseObjectImpl();
        } else if (ch =='[') {
            return parseArrayImpl();
        } else {
            return parseAtom(ch);
        }
    }

    public Boolean parseBoolean() throws IOException {
        int ch = peekToken();
        if (ch == 't') {
            consume("true");
            return true;
        } else if (ch == 'f') {
            consume("false");
            return false;
        } else {
            throw createSyntaxException(ch, "boolean literal");
        }
    }

    public JSONNull parseNull() throws IOException {
        consume("null");
        return JSONNull.INSTANCE;
    }

    public BigDecimal parseBigDecimal() throws IOException {
        int ch = peekToken();
        return parseBigDecimal(ch);
    }

    public BigInteger parseBigInteger() throws IOException {
        int ch = peekToken();
        return parseBigInteger(ch);
    }

    public float parseFloat() throws IOException {
        return parseBigDecimal().floatValue();
    }

    public double parseDouble() throws IOException {
        return parseBigDecimal().doubleValue();
    }

    public byte parseByte() throws IOException, ArithmeticException {
        return parseBigDecimal().byteValueExact();
    }

    public short parseShort() throws IOException, ArithmeticException {
        return parseBigDecimal().shortValueExact();
    }

    public int parseInt() throws IOException, ArithmeticException {
        return parseBigDecimal().intValueExact();
    }

    public long parseLong() throws IOException, ArithmeticException {
        return parseBigDecimal().longValueExact();
    }

    public JSONObject parseObject() throws IOException {
        peekToken();
        return parseObjectImpl();
    }

    private JSONObject parseObjectImpl() throws IOException {
        final JSONObject result = new JSONObject();
        parseObject(new ObjectCallback() {
            public void property(JSONParser parser, String property, ValueType type) throws IOException {
                Object value = parser.parseValue();
                result.put(property, value);
            }
        });
        return result;
    }
    
    private boolean isIdentifierStart(int ch) {
        return ch >= 'a' && ch <= 'z' || ch >= 'A' && ch <= 'Z' || ch == '_' || ch == '$';
    }
    
    private boolean isIdentifierPart(int ch) {
        return isIdentifierStart(ch) || ch >= '0' && ch <= '9';
    }
    
    private String parseIdentifier(int ch) throws IOException {
        if (isIdentifierStart(ch)) {
            consume();
        } else {
            throw createSyntaxException(ch);
        }
        StringBuilder sb = new StringBuilder();
        sb.append((char)ch);
        while (true) {
            ch = peek();
            if (isIdentifierPart(ch)) {
                next();
                sb.append((char)ch);
            } else {
                break;
            }
        }
        return sb.toString();
    }
    
    public void parseObject(ObjectCallback cb) throws IOException {
        consume('{');
        int ch = peekToken();
        if (ch == '}') {
            consume();
        } else {
            if (lenient) {
                while (true) {
                    if (ch == '"' || ch == '\'' || isIdentifierStart(ch)) {
                        String str;
                        if (ch == '"' || ch == '\'') {
                            str = parseStringImpl(ch);
                        } else {
                            str = parseIdentifier(ch);
                        }
                        
                        ch = peekToken();
                        if (ch == ':') {
                            consume();
                        } else if (ch == '=') {
                            next();
                            ch = peek();
                            if (ch == '>') {
                                next();
                            }
                        }

                        ch = peekToken();
                        ValueType type = nextItemType(ch);
                        cb.property(this, str, type);

                        ch = peekToken();
                        if (ch == '}') {
                            consume();
                            break;
                        }
                        else if (ch == ',' || ch == ';') {
                            consume();
                            ch = peekToken();
                        }
                        else {
                            throw createSyntaxException(ch, "closing brace or comma");
                        }
                    }
                }
            } else {
                while (true) {
                    if (ch == '"' || ch == '\'') {
                        String str = parseStringImpl(ch);
                        consumeToken(':');

                        ch = peekToken();
                        ValueType type = nextItemType(ch);
                        cb.property(this, str, type);

                        ch = peekToken();
                        if (ch == '}') {
                            consume();
                            break;
                        }
                        else if (ch == ',') {
                            consume();
                            ch = peekToken();
                        }
                        else {
                            throw createSyntaxException(ch, "closing brace or comma");
                        }
                    }
                    else {
                        throw createSyntaxException(ch, "quote");
                    }
                }
            }
        }
    }

    public List parseArray() throws IOException {
        peekToken();
        return parseArrayImpl();
    }
    
    private List parseArrayImpl() throws IOException {
        final List result = new ArrayList();
        parseArray(new ArrayCallback() {
            public void item(JSONParser parser, int idx, ValueType type) throws IOException {
                result.add(parser.parseValue());
            }
        });
        return result;
    }

    public void parseArray(ArrayCallback cb) throws IOException {
        consume('[');
        int ch = peekToken();
        int idx = 0;

        if (ch == ']') {
            consume();
        } else {
            while (true) {
                ValueType type = nextItemType(ch);
                cb.item(this, idx, type);
                idx++;

                ch = peekToken();
                if (ch == ']') {
                    consume();
                    break;
                } else if (ch == ',') {
                    consume();
                    ch = peekToken();
                } else {
                    throw createSyntaxException(ch);
                }
            }
        }
    }

    private ValueType nextItemType(int ch) throws IOException {
        switch (ch) {
            case 'n': return ValueType.NULL;
            case 't':
            case 'f': return ValueType.BOOLEAN;
            case '0': case '1': case '2': case '3': case '4':
            case '5': case '6': case '7': case '8': case '9':
            case '+': case '-': return ValueType.NUMBER;
            case '\'':
            case '"': return ValueType.STRING;
            case '{': return ValueType.OBJECT;
            case '[': return ValueType.ARRAY;
            default:
                throw createSyntaxException(ch);
        }
    }
    
    public boolean isObject() throws IOException {
        int ch = peekToken();
        return nextItemType(ch) == ValueType.OBJECT;
    }
    
    public boolean isArray() throws IOException {
        int ch = peekToken();
        return nextItemType(ch) == ValueType.ARRAY;
    }
}