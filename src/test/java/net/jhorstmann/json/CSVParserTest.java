package net.jhorstmann.json;

import java.io.IOException;
import org.junit.Test;
import static org.junit.Assert.*;
import static java.util.Arrays.*;

public class CSVParserTest {

    @Test
    public void testSingleStringColumn() throws IOException {
        String csv = "abc\ndef";
        assertEquals(asList(asList("abc"), asList("def")), new CSVParser(csv).parseLines(false));
    }

    @Test
    public void testSingleQuotedStringColumn() throws IOException {
        String csv = "'abc'\n'def'";
        assertEquals(asList(asList("abc"), asList("def")), new CSVParser(csv).parseLines(false));
    }

    @Test
    public void spacesShouldBeStrippedFromBareStrings() throws IOException {
        String csv = "abc \n def\n ghi ";
        assertEquals(asList(asList("abc"), asList("def"), asList("ghi")), new CSVParser(csv).parseLines(false));
    }

    @Test
    public void testMultipleColumns() throws IOException {
        String csv = "abc,def,ghi";
        assertEquals(asList(asList("abc", "def", "ghi")), new CSVParser(csv).parseLines(false));
    }

    @Test
    public void spacesAroundDelimiterShouldBeIgnored() throws IOException {
        String csv = "abc , def, ghi ";
        assertEquals(asList(asList("abc", "def", "ghi")), new CSVParser(csv).parseLines(false));
    }

}
