package net.jhorstmann.json;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.Reader;
import java.io.StringReader;
import java.util.Map;
import net.jhorstmann.json.JSONBuilder.ListBuilder;
import net.jhorstmann.json.JSONBuilder.ObjectBuilder;

public class QueryStringParser extends AbstractParser {
    private static final int MAX_INDEX = 0xFFFF;

    public QueryStringParser(Reader reader) {
        super(reader);
    }

    public QueryStringParser(String s) {
        super(new StringReader(s));
    }

    public Map parse() throws IOException {
        ObjectBuilder builder = JSONBuilder.object();
        parseImpl(builder);
        return builder.getMap();
    }

    private void parseImpl(ObjectBuilder builder) throws IOException {
        while (true) {
            int ch = peekChar();
            if (ch == -1) {
                break;
            }
            parseEntry(builder, ch);

            int ch2 = peekChar();

            if (ch2 == -1) {
                break;
            } else if (ch2 == '&') {
                nextChar();
            } else {
                throw createSyntaxException(ch2);
            }
        }
    }

    private void parseValue(ObjectBuilder builder, String key) throws IOException {
        int ch = peekChar();
        builder.put(key, parseStringValue(ch));
    }

    private void parseValue(ListBuilder builder, int key) throws IOException {
        int ch = peekChar();
        builder.set(key, parseStringValue(ch));
    }

    private void parseEntry(ObjectBuilder builder, int ch) throws IOException {
        String key = parseStringKey(ch);
        int ch2 = peekChar();
        parseProperty(builder, key, ch2);
    }

    private void parseProperty(ObjectBuilder builder, String key, int ch2) throws IOException {

        if (ch2 == -1 || ch2 == '&') {
            builder.propertyTrue(key);
        } else if (ch2 == '=') {
            nextChar();
            parseValue(builder, key);
        } else if (ch2 == '.') {
            nextChar();
            ObjectBuilder object = builder.object(key);
            int ch3 = peekChar();
            parseEntry(object, ch3);
        /*} else if (ch2 == '(') {
            next();
            ObjectBuilder object = builder.object(key);
            int ch3 = peek();
            String property = parseStringKey(ch3);
            consume(')');
            int ch4 = peek();
            if (ch4 == '=') {
                next();
                parseValue(object, property);
            } else if (ch4 == '.') {
                next();
                ObjectBuilder object = list.object(idx);
                int ch5 = peek();
                parseEntry(object, ch5);
            } else if (ch4 == -1 || ch4 == '&') {
                list.itemTrue(idx);
            } else {
                throw createSyntaxException(ch4);
            }*/
        } else if (ch2 == '[') {
            ListBuilder list = builder.list(key);
            parseList(list);
        }
    }

    private void parseList(ListBuilder list) throws IOException {
        consume('[');
        int idx;

        int ch = peekChar();
        if (ch == ']') {
            idx = list.size();
        } else {
            idx = parseIndex(ch);
        }
        consume(']');
        int ch2 = peekChar();
        if (ch2 == '=') {
            nextChar();
            parseValue(list, idx);
        } else if (ch2 == '.') {
            nextChar();
            ObjectBuilder object = list.object(idx);
            int ch3 = peekChar();
            parseEntry(object, ch3);
        } else if (ch2 == '[') {
            ListBuilder nestedList = list.list(idx);
            parseList(nestedList);
        } else if (ch2 == -1 || ch2 == '&') {
            list.itemTrue(idx);
        } else {
            throw createSyntaxException(ch2);
        }
    }

    private int parseIndex(int ch) throws IOException {
        int res = 0;
        if (ch >= '0' && ch <= '9') {
            nextChar();
            res = ch - '0';
            while (true) {
                int ch2 = peekChar();
                if (ch2 >= '0' && ch2 <= '9') {
                    res = res*10 + ch2 - '0';
                    if (res > MAX_INDEX) {
                        throw createSyntaxException("Index " + res + (char)ch2 + " too large");
                    }
                    nextChar();
                } else {
                    break;
                }
            }
        } else {
            throw createSyntaxException(ch, "digit");
        }

        return res;
    }

    private int parseEscapedOctet() throws IOException {
        int num = 0;
        for (int i = 0; i < 2; i++) {
            int ch = peekChar();
            if (ch >= '0' && ch <= '9') {
                consume();
                num = num * 16 + (ch - '0');
            } else if (ch >= 'a' && ch <= 'f') {
                consume();
                num = num * 16 + (ch - 'a' + 10);
            } else if (ch >= 'A' && ch <= 'F') {
                consume();
                num = num * 16 + (ch - 'A' + 10);
            } else {
                throw createSyntaxException(ch, "hex digit");
            }
        }
        return num;
    }

    private String parseEscape() throws IOException {
        consume('%');
        int b1 = parseEscapedOctet();
        int ch2 = peekChar();
        if (ch2 == '%') {
            consume();
            ByteArrayOutputStream baos = new ByteArrayOutputStream(8);
            baos.write(b1);
            baos.write(parseEscapedOctet());
            int ch3 = peekChar();
            while (ch3 == '%') {
                consume();
                baos.write(parseEscapedOctet());
                ch3 = peekChar();
            }
            return baos.toString("UTF-8");
        } else {
            return Character.toString((char) (b1 & 0xFF));
        }
    }

    private String parseString(int ch, String delim) throws IOException {
        StringBuilder sb = new StringBuilder();

        while (true) {
            if (ch == -1 || delim.indexOf(ch) >= 0) {
                break;
            } else if (ch == '%') {
                sb.append(parseEscape());
            } else {
                nextChar();
                sb.appendCodePoint(ch);
            }
            ch = peekChar();
        }

        return sb.toString();
    }

    private String parseStringKey(int ch) throws IOException {
        return parseString(ch, "&.[]()=");
    }

    private String parseStringValue(int ch) throws IOException {
        return parseString(ch, "&");
    }
}
