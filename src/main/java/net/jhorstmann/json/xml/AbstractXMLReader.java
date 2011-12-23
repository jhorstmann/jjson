package net.jhorstmann.json.xml;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import org.xml.sax.Attributes;
import org.xml.sax.ContentHandler;
import org.xml.sax.DTDHandler;
import org.xml.sax.EntityResolver;
import org.xml.sax.ErrorHandler;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;
import org.xml.sax.XMLReader;
import org.xml.sax.helpers.AttributesImpl;

public abstract class AbstractXMLReader implements XMLReader {

    protected static final String DEFAULT_ENCODING = "utf-8";
    protected static final Attributes EMPTY_ATTRIBUTES = new AttributesImpl();
    protected ContentHandler contentHandler;
    protected ErrorHandler errorHandler;
    protected DTDHandler dtdHandler;
    protected EntityResolver entityResolver;

    public DTDHandler getDTDHandler() {
        return dtdHandler;
    }

    public void setDTDHandler(DTDHandler dtdHandler) {
        this.dtdHandler = dtdHandler;
    }

    public EntityResolver getEntityResolver() {
        return entityResolver;
    }

    public void setEntityResolver(EntityResolver entityResolver) {
        this.entityResolver = entityResolver;
    }

    public void setContentHandler(ContentHandler handler) {
        this.contentHandler = handler;
    }

    public ContentHandler getContentHandler() {
        return contentHandler;
    }

    public void setErrorHandler(ErrorHandler handler) {
        this.errorHandler = handler;
    }

    public ErrorHandler getErrorHandler() {
        return errorHandler;
    }

    public void parse(InputSource input) throws IOException, SAXException {
        Reader characters = input.getCharacterStream();
        if (characters != null) {
            parse(characters);
        } else {
            InputStream bytes = input.getByteStream();
            if (bytes != null) {
                String encoding = input.getEncoding();
                if (encoding != null) {
                    parse(new InputStreamReader(bytes, encoding));
                } else {
                    parse(new InputStreamReader(bytes, "utf-8"));
                }
            } else {
                String systemId = input.getSystemId();
                parse(systemId);
            }
        }
    }

    public void parse(String systemId) throws IOException, SAXException {
        parse(new File(systemId));
    }

    public void parse(File file) throws IOException, SAXException {
        parse(new InputStreamReader(new FileInputStream(file), DEFAULT_ENCODING));
    }

    public abstract void parse(Reader reader) throws IOException, SAXException;
}
