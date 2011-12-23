package net.jhorstmann.json;

import java.io.IOException;
import java.io.Reader;
import java.math.BigDecimal;
import java.math.BigInteger;

public abstract class AbstractParser {
    private Reader  reader;
    private int     la1;
    private int     line = 1;
    private int     column = 0;
    private boolean parseBigDecimal;

    public AbstractParser(Reader reader) {
        this.reader = reader;
        this.la1    = -1;
    }

    public final boolean isParseBigDecimal() {
        return parseBigDecimal;
    }

    public final void setParseBigDecimal(boolean parseBigDecimal) {
        this.parseBigDecimal = parseBigDecimal;
    }

    public final int getLine() {
        return line;
    }

    public final int getColumn() {
        return column;
    }

    private int readChar() throws IOException {
        return reader.read();
    }

    protected final int next() throws IOException {
        if (la1 == -1) {
            int ch = readChar();
            if (ch == '\n') {
                line++;
                column = 0;
            } else {
                column++;
            }
            return ch;
        }
        else {
            int ch = la1;
            la1 = -1;
            return ch;
        }
    }

    protected final int peek() throws IOException {
        if (la1 == -1) {
            return la1 = readChar();
        }
        else {
            return la1;
        }
    }

    protected final int nextToken() throws IOException {
        int ch;

        do {
            ch = next();
        }
        while (isWhitespace(ch));

        return ch;
    }

    protected final int peekToken() throws IOException {
        if (la1 == -1 || isWhitespace(la1)) {
            return la1 = nextToken();
        }
        else {
            return la1;
        }
    }

    protected static boolean isNumberStart(int ch) {
        switch (ch) {
            case '+': case '-':
            case '0': case '1': case '2': case '3': case '4':
            case '5': case '6': case '7': case '8': case '9':
                return true;
            default:
                return false;
        }
    }

    protected static boolean isWhitespace(int ch) {
        return ch == '\t' || ch == '\r' || ch == '\n' || ch == ' ';
    }

    protected final void consume() throws IOException {
        next();
    }

    protected final void consume(int expected) throws IOException {
        int ch = next();
        if (ch != expected) {
            throw createSyntaxException(ch, expected);
        }
    }

    protected final void consume(CharSequence s) throws IOException {
        for (int i=0, len=s.length(); i<len; i++) {
            consume(s.charAt(i));
        }
    }

    protected final void consumeToken(int expected) throws IOException {
        int ch = nextToken();
        if (ch != expected) {
            throw createSyntaxException(ch, expected);
        }
    }

    protected final JSONSyntaxException createSyntaxException(int ch) {
        return createSyntaxException(ch, null);
    }

    protected static String charName(int ch) {
        return ch == -1 ? "EOF" : ("'" + JSONUtils.escapeChar((char)ch) + "'");
    }

    protected final JSONSyntaxException createSyntaxException(int ch, int expected) {
        return createSyntaxException(ch, charName(expected));
    }

    protected final JSONSyntaxException createSyntaxException(int ch, String expected) {
        String msg = "Unexpected " + charName(ch);
        if (expected != null) {
            msg += ", expected " + expected;
        }
        return createSyntaxException(msg);
    }

    protected final JSONSyntaxException createSyntaxException(String msg) {
        return new JSONSyntaxException(msg + " at line " + line + " column " + column);
    }

    protected final BigInteger parseBigInteger(int ch) throws IOException {
        StringBuffer sb = new StringBuffer();
        parseNumber(sb, ch);
        return new BigInteger(sb.toString(), 10);
    }
    
    private Number convertDecimal(StringBuffer sb) {
        return isParseBigDecimal() ? new BigDecimal(sb.toString()) : Double.valueOf(sb.toString());
    }

    protected final BigDecimal parseBigDecimal(int ch) throws IOException {
        StringBuffer sb = new StringBuffer();
        parseNumber(sb, ch);
        return new BigDecimal(sb.toString());
    }

    protected final Double parseDouble(int ch) throws IOException {
        StringBuffer sb = new StringBuffer();
        parseNumber(sb, ch);
        return Double.valueOf(sb.toString());
    }

    protected final Number parseUnsignedDecimal(int ch) throws IOException {
        StringBuffer sb = new StringBuffer();
        parseUnsignedNumber(sb, ch);
        return convertDecimal(sb);
    }

    protected final Number parseSignedDecimal(int ch) throws IOException {
        StringBuffer sb = new StringBuffer();
        parseNumber(sb, ch);
        return convertDecimal(sb);
    }

    private void parseNumber(StringBuffer sb, int ch) throws IOException {
        if (ch == '-' || ch == '+') {
            next();
            sb.append((char)ch);
            int ch2 = peek();
            parseUnsignedNumber(sb, ch2);
        } else {
            parseUnsignedNumber(sb, ch);
        }
    }

    private void parseUnsignedNumber(StringBuffer sb, int ch) throws IOException {
        parseInt(sb, ch);
        int ch2 = peek();
        // optional Fraction
        if (ch2 == '.') {
            sb.append('.');
            consume();
            int ch3 = peek();
            parseDigits(sb, ch3);

            ch2 = peek();
        }

        // optional exponent
        if (ch2 == 'e' || ch2 == 'E') {
            sb.append('e');
            consume();
            int ch3 = peek();
            if (ch3 == '-' || ch3 == '+') {
                consume();
                sb.append((char)ch3);

                ch3 = peek();
            }
            parseDigits(sb, ch3);
        }
    }

    private void parseInt(StringBuffer sb, int ch) throws IOException {
        if (ch == '0') {
            consume('0');
            sb.append('0');
        }
        else if (ch >= '1' && ch <= '9') {
            parseDigits(sb, ch);
        }
        else {
            throw createSyntaxException(ch, "digit");
        }
    }

    private void parseDigits(StringBuffer sb, int ch) throws IOException {
        if (ch >= '0' && ch <= '9') {
            consume();
            sb.append((char)ch);
            while (true) {
                int ch2 = peek();
                if (ch2 >= '0' && ch2 <= '9') {
                    consume();
                    sb.append((char)ch2);
                }
                else {
                    break;
                }
            }
        }
    }

    protected final String parseStringImpl(int quote) throws IOException {
        consume(quote);
        StringBuilder sb = new StringBuilder();
        while (true) {
            int ch = peek();
            if (ch == '\\') {
                sb.append((char)parseEscape());
            } else if (ch == quote) {
                break;
            } else if (ch == -1 || Character.isISOControl(ch)) {
                throw createSyntaxException(ch, "non-control character");
            } else {
                sb.append((char)ch);
                consume();
            }
        }
        consume(quote);
        return sb.toString();
    }

    private int parseEscape() throws IOException {
        consume('\\');
        int ch = next();
        switch (ch) {
            case '"' : return '"';
            case '\'': return '\'';
            case '\\': return '\\';
            case '/' : return '/';
            case 'b' : return '\b';
            case 'f' : return '\f';
            case 'n' : return '\n';
            case 'r' : return '\r';
            case 't' : return '\t';
            case 'u' : return parseCodepoint();
            default  : throw createSyntaxException(ch, "valid escape sequence");
        }
    }

    private int parseCodepoint() throws IOException {
        int num = 0;
        for (int i=0; i<4; i++) {
            int ch = peek();
            if (ch >= '0' && ch <= '9') {
                consume();
                num = num * 16 + (ch-'0');
            }
            else if (ch >= 'a' && ch <= 'f') {
                consume();
                num = num * 16 + (ch-'a'+10);
            }
            else if (ch >= 'A' && ch <= 'F') {
                consume();
                num = num * 16 + (ch-'A'+10);
            }
            else {
                throw createSyntaxException(ch, "hex digit");
            }
        }
        return num;
    }

    public final String parseString() throws IOException {
        int ch = peekToken();
        if (ch == '"' || ch == '\'') {
            return parseStringImpl(ch);
        } else {
            throw new JSONSyntaxException("Illegal start of string at line " + getLine() + " column " + getColumn() + "!");
        }
    }
}
