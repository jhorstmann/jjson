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
                next();
                sb.append((char) ch);
                ch = peek();
            }
        }
        return sb.toString();
    }

    private String parseString(int quote) throws IOException {
        consume(quote);
        StringBuilder sb = new StringBuilder();
        while (true) {
            int ch = peek();
            if (ch == quote) {
                next();
                int ch2 = peek();
                if (ch2 == quote) {
                    next();
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

    private List<Object> parseLine(boolean parseNumbers) throws IOException {
        List<Object> result = new LinkedList<Object>();
        while (true) {
            int ch = peek();
            if (ch == -1) {
                break;
            } else if (ch == '\r') {
                next();
                int ch2 = peek();
                if (ch2 == '\n') {
                    next();
                }
            } else if (ch == '\n') {
                next();
                break;
            } else if (ch == '\t' || ch == ' ') {
                next();
            } else if (ch == delimiter) {
                next();
            } else if (ch == '"' || ch == '\'') {
                result.add(parseString(ch));
            } else if (parseNumbers && isNumberStart(ch)) {
                result.add(parseSignedDecimal(ch));
            } else {
                String str = parseBareString(ch).trim();
                result.add(str.length() == 0 ? null : str);
            }
        }
        return result;
    }

    public void parseLines(LineCallback callback) throws IOException {
        while (true) {
            int ch = peek();
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
