/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package RaceLibrary;

import java.io.*;
//import java.util.*;
import org.xml.sax.*;
//import org.xml.sax.helpers.DefaultHandler;
//import javax.xml.parsers.SAXParserFactory;
//import javax.xml.parsers.ParserConfigurationException;
//import javax.xml.parsers.SAXParser;
import javax.activation.URLDataSource;

/**
 *
 * @author Brian
 */
public class StructureReaderXML extends SQLReaderXML {

    public static final boolean DEBUG   = true;

    private String version;
    public  String versionHead;

    public StructureReaderXML() {
        version = new String("");
        versionHead = new String("--.--.--.--");
    }

    public void parse() throws Exception
    {

        if (DEBUG) System.out.println("StructureReaderXML - parse() called");

        if (database == null && !flagReadOnly) {             // database not defined
            if (DEBUG) System.out.println("  error - database not defined");
            return;
        }
        if (database != null) {
            if (database.connStatus <= 0 && !flagReadOnly) {   // database connection is not established
                if (DEBUG) System.out.println("  error - database connection is not established");
                return;
            }
        }

        URLDataSource xmlFile;
        xmlFile = new URLDataSource(getClass().getResource("resources/dbStructure.xml"));
        if (xmlFile == null) {
            System.err.println("dbStructure.xml File is missing");
            return;
        }
        if (DEBUG) System.out.println(xmlFile.getName());

        //String nfile = xmlFile.getName().replace("file:/", "");
        //if (DEBUG) System.out.println(nfile);
        //FileReader file = new FileReader(nfile);

        //if (DEBUG) System.out.println(file.toString());

        parse(xmlFile.getInputStream());
        //parse(file);
    }

    @Override
    public void startDocument ()
    {
        if (DEBUG) System.out.println("Start document");
        version = "";
        versionHead = "";
    }

    @Override
    public void endDocument ()
    {
        if (DEBUG) System.out.println("End document");
    }


    // receive notification of the beginning of an element
    @Override
    public void startElement (String uri, String name, String qName, Attributes atts) 
    {
        if (uri.equals("")) elementName = qName;
        else elementName = uri;
        if (DEBUG) System.out.print("Start=");
        if (DEBUG) System.out.println(elementName);
        elementData = new String();

        for (int n=0;n<atts.getLength();n++) {
            if (elementName.equalsIgnoreCase("structure")) {
                if (atts.getQName(n).equalsIgnoreCase("version")) {
                    version = atts.getValue(n);
                    if (versionCompare(version) > 0) versionHead = version;
                }
            }
            if (DEBUG) {
                System.out.print(atts.getQName(n));
                System.out.print("=");
                System.out.println(atts.getValue(n));
            }
        }

    }

   // receive notification of the end of an element
    @Override
    public void endElement (String uri, String name, String qName)
    {
        if (elementName == null) {  // catch two ends in a row
            elementData = null;
            return;
        }

        if (elementData==null) {
            elementData = new String("");
        }

        dataRemoveWhiteSpaces();

        if (elementName.equalsIgnoreCase("sql")) {
            //hexDump(elementData);
            if (!flagReadOnly) database.execute(elementData);
        }

        if (DEBUG) System.out.println(elementData);
        elementName = null;
        elementData = null;
    }

   // receive notification of character data
    public int versionCompare(String tversion) {

        int hver[];
        int tver[];

        if (versionHead.equals("")) return 1;  // versionHead not set

        hver = versionBreakDown(versionHead);
        tver = versionBreakDown(tversion);

        for (int n=0;n<4;n++) {
            if (hver[n] > tver[n]) return -1;   // database older version
            if (hver[n] < tver[n]) return  1;   // database newer version
        }

        return 0;  // versions the same
    }

    public int[] versionBreakDown(String tversion) {

        int bversion[] = new int[4];

        if (tversion.length() < 11) {
            bversion[0] = 0;
            bversion[1] = 0;
            bversion[2] = 0;
            bversion[3] = 0;
            return bversion;
        }
        //                   11
        //         012345678901
        // version xx.xx.xx.xx

        bversion[0] = Integer.parseInt(tversion.substring(0,2));
        bversion[1] = Integer.parseInt(tversion.substring(3,5));
        bversion[2] = Integer.parseInt(tversion.substring(6,8));
        bversion[3] = Integer.parseInt(tversion.substring(9,11));

        return bversion;
    }

    public void hexDump(String value) {
        Integer tmp;

        for (int n=0;n<value.length();n++) {
            //System.out.printf("%1$2.2x", (int) value.charAt(n));
            System.out.print(Integer.toHexString((int) value.charAt(n)));
            System.out.print(" ");
        }
        System.out.println();
    }

}

