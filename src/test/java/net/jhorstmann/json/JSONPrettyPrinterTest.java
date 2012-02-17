package net.jhorstmann.json;

import java.io.IOException;

public class JSONPrettyPrinterTest {

    public static void main(String[] args) throws IOException {
        System.out.println(JSONPrettyPrinter.prettyprint("{\"abc\":123.0,\"def\":456.0,\"ghi\":[123.0,true,{},{},[],[]],\"jkl\":{\"abc\":true,\"def\":null}}"));
    }
}
