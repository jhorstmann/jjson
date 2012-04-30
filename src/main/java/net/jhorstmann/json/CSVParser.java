package net.jhorstmann.json;

import java.io.IOException;
import java.io.Reader;
import java.io.StringReader;
import java.util.LinkedList;
import java.util.List;

public class CSVParser extends AbstractParser {

    public static interface LineCallback<T> {
        public void line(List<T> fields);
    }

    private static final char DEFAULT_DELIMITER = ',';
    private char delimiter;
    private boolean parseNumbers;

    public CSVParser(Reader reader, char delimiter, boolean parseNumbers) {
        super(reader);
        this.delimiter = delimiter;
        this.parseNumbers = parseNumbers;
    }

    public CSVParser(Reader reader) {
        this(reader, DEFAULT_DELIMITER, false);
    }

    public CSVParser(String str) {
        this(new StringReader(str));
    }

    public CSVParser(String str, char delimiter, boolean parseNumbers) {
        this(new StringReader(str), delimiter, parseNumbers);
    }

    public char getDelimiter() {
        return delimiter;
    }

    public void setDelimiter(char delimiter) {
        this.delimiter = delimiter;
    }

    public boolean isParseNumbers() {
        return parseNumbers;
    }

    public void setParseNumbers(boolean parseNumbers) {
        this.parseNumbers = parseNumbers;
    }

    private String parseBareString(int ch) throws IOException {
        StringBuilder sb = new StringBuilder();
        while (true) {
            if (ch == -1) {
                break;
            } else if (ch == delimiter) {
                break;
            } else if (ch == '\r' || ch == '\n') {
                break;
            } else {
                nextChar();
                sb.append((char) ch);
                ch = peekChar();
            }
        }
        return sb.toString();
    }

    private String parseString(int quote) throws IOException {
        consume(quote);
        StringBuilder sb = new StringBuilder();
        while (true) {
            int ch = peekChar();
            if (ch == quote) {
                nextChar();
                int ch2 = peekChar();
                if (ch2 == quote) {
                    nextChar();
                    sb.append((char)quote);
                } else {
                    break;
                }
            } else if (ch == -1) {
                throw createSyntaxException(ch);
            } else {
                sb.append((char)ch);
                consume();
            }
        }
        return sb.toString();
    }
    
    private void skipOptionalWhitespace(int ch) throws IOException {
        while (ch == '\t' || ch == ' ') {
            nextChar();
            ch = peekChar();
        }
    }

    private Object parseValue(int ch, boolean parseNumbers) throws IOException {
        skipOptionalWhitespace(ch);
        Object res;
        if (ch == '"' || ch == '\'') {
            res = parseString(ch);
            int ch2 = peekChar();
            skipOptionalWhitespace(ch2);
        } else if (parseNumbers && isNumberStart(ch)) {
            res = parseSignedDecimal(ch);
            int ch2 = peekChar();
            skipOptionalWhitespace(ch2);
        } else {
            String str = parseBareString(ch).trim();
            res = str.length() == 0 ? null : str;
        }
        return res;
    }

    private List<Object> parseLine(boolean parseNumbers) throws IOException {
        List<Object> result = new LinkedList<Object>();
        while (true) {
            int ch = peekChar();
            if (ch == -1) {
                break;
            } else if (ch == '\r') {
                nextChar();
                int ch2 = peekChar();
                if (ch2 == '\n') {
                    nextChar();
                }
            } else if (ch == '\n') {
                nextChar();
                break;
            } else if (ch == '\t' || ch == ' ') {
                nextChar();
            } else if (ch == delimiter) {
                nextChar();
            } else {
                result.add(parseValue(ch, parseNumbers));
            }
        }
        return result;
    }

    public void parseLines(LineCallback callback) throws IOException {
        while (true) {
            int ch = peekChar();
            if (ch == -1) {
                break;
            } else {
                List<Object> fields = parseLine(parseNumbers);
                callback.line(fields);
            }
        }
    }

    public List<List<Object>> parseLines(boolean firstLineIsHeader) throws IOException {
        if (firstLineIsHeader) {
            List<Object> headers = parseLine(false);
        }
        final List<List<Object>> list = new LinkedList<List<Object>>();
        parseLines(new LineCallback<Object>() {
            public void line(List<Object> fields) {
                list.add(fields);
            }
        });
        return list;
    }

    static class LineCollector<T> implements LineCallback<T> {

        private List<JSONObject> list;
        private String[] properties;

        LineCollector(List<JSONObject> list, String[] properties) {
            if (list == null) {
                throw new IllegalArgumentException("list must not be null");
            }
            if (properties == null || properties.length == 0) {
                throw new IllegalArgumentException("properties must not be empty");
            }
            this.list = list;
            this.properties = properties;
        }

        public void line(List<T> fields) {
            JSONObject json = new JSONObject();
            int i = 0;
            for (T value : fields) {
                String property = properties[i];
                json.put(property, value);
                i++;
                if (i >= properties.length) {
                    break;
                }
            }
            list.add(json);
        }
    }

    public List<JSONObject> parseObjects(boolean firstLineIsHeader, final String[] properties) throws IOException {
        if (firstLineIsHeader) {
            List<Object> headers = parseLine(false);
        }
        final List<JSONObject> list = new LinkedList<JSONObject>();
        parseLines(new LineCollector(list, properties));
        return list;
    }

    public List<JSONObject> parseObjects() throws IOException {
        List<Object> headers = parseLine(false);
        String[] properties = headers.toArray(new String[headers.size()]);
        final List<JSONObject> list = new LinkedList<JSONObject>();
        parseLines(new LineCollector(list, properties));
        return list;
    }
}
