/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package RaceLibrary;

import java.io.*;
import java.util.*;
import org.xml.sax.*;
import org.xml.sax.helpers.DefaultHandler;
import javax.xml.parsers.SAXParserFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.parsers.SAXParser;
import javax.activation.URLDataSource;

/**
 *
 * @author Brian
 */
public class SQLReaderXML extends DefaultHandler implements java.io.Serializable {

    public static final boolean DEBUG   = false;

    protected boolean flagReadOnly;

    protected RaceDatabase database;

    protected String elementName;
    protected String elementData;

    public SQLReaderXML() {
        flagReadOnly = false;
        database = null;

        elementName = new String("");
        elementData = new String("");
    }

    public void parse(FileReader file) throws Exception
    {
        URLDataSource xmlFile;

        SAXParserFactory spf = SAXParserFactory.newInstance();
        spf.setValidating(false);
        SAXParser saxParser = spf.newSAXParser();
        // create an XML reader
        XMLReader reader = saxParser.getXMLReader();
        // set handler
        reader.setContentHandler(this);
        // call parse on an input source
        reader.parse(new InputSource(file));

        file.close();
    }

    public void parse(InputStream infile) throws Exception
    {
        SAXParserFactory spf = SAXParserFactory.newInstance();
        spf.setValidating(false);
        SAXParser saxParser = spf.newSAXParser();
        // create an XML reader
        XMLReader reader = saxParser.getXMLReader();
        // set handler
        reader.setContentHandler(this);
        // call parse on an input source
        reader.parse(new InputSource(infile));
    }

    public void setDatabase(RaceDatabase db) {
        database = db;
    }

    public void setReadOnly(boolean value) {
        flagReadOnly = value;
    }

    @Override
    public void characters (char ch[], int start, int length) {

        if(elementData == null) return;

        String value = new String(ch, start, length);
        elementData += value;
    }

    protected void dataRemoveWhiteSpaces() {

        if (elementData == null) return;

                // remove invalid chars
        elementData = elementData.trim();
        elementData = elementData.replace((char) 0xa, (char) 0x20);
        elementData = elementData.replace((char) 0xd, (char) 0x20);

        if (elementData.length() < 2) return;

        char cbuffer[];

        cbuffer = new char[elementData.length()];
        int index = 0;
        int cidx  = 0;

        cbuffer[cidx] = elementData.charAt(index);
        cidx++;  index++;
        while ( index < elementData.length()) {
            if (cbuffer[cidx-1] != (char) 0x20 || elementData.charAt(index) != (char) 0x20) {
                cbuffer[cidx] = elementData.charAt(index);
                cidx++;
            }
            index++;
        }

        elementData = new String(cbuffer,0,cidx);
    }

}
