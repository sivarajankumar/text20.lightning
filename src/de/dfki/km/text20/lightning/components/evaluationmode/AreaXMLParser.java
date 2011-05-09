/*
 * CoordinatesXMLParser.java
 * 
 * Copyright (c) 2011, Christoph Käding, DFKI. All rights reserved.
 *
 * This library is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public
 * License as published by the Free Software Foundation; either
 * version 2.1 of the License, or (at your option) any later version.
 *
 * This library is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with this library; if not, write to the Free Software
 * Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston,
 * MA 02110-1301  USA
 */
package de.dfki.km.text20.lightning.components.evaluationmode;

import java.awt.Point;
import java.io.File;
import java.io.FileInputStream;
import java.util.ArrayList;

import javax.xml.stream.XMLInputFactory;
import javax.xml.stream.XMLStreamConstants;
import javax.xml.stream.XMLStreamReader;
import javax.xml.transform.stream.StreamSource;
import javax.xml.validation.Schema;
import javax.xml.validation.SchemaFactory;
import javax.xml.validation.Validator;

import org.xml.sax.SAXException;

/**
 * the XML-files which stores the evaluation datas are processed by this class
 * 
 * @author Christoph Käding
 *
 */
public class AreaXMLParser {

    /** readed data */
    private ArrayList<Point> data;

    /** indicates if the next entry should be the x coordinate of the related position */
    private boolean x;

    /** indicates if the next entry should be the y coordinate of the related position */
    private boolean y;

    /** temporary stored x coordinate */
    private int xTmp;

    /** temporary stored y coordinate */
    private int yTmp;

    /** name of the actual file */
    private String fileName;

    /** indicates that the next readed value should be the number */
    private boolean number;

    /** the temporary stored number */
    private int numberTmp;

    /**
     * tries to read the included StorageContainer of the given XML-file
     * 
     * @param file
     * 
     * @return the readed container
     */
    public ArrayList<Point> readFile(File file) {
        // initialize variables
        this.data = new ArrayList<Point>();
        this.fileName = file.getAbsolutePath();
        this.x = false;
        this.y = false;
        this.number = false;
        this.numberTmp = -1;
        this.xTmp = 0;
        this.yTmp = 0;
        FileInputStream inputStream = null;
        XMLStreamReader reader = null;

        try {
            inputStream = new FileInputStream(file);
            reader = XMLInputFactory.newInstance().createXMLStreamReader(inputStream);

            // if there are still some data ...
            while (reader.hasNext()) {

                // ... read from file
                switch (reader.next()) {

                // if a starttag is found
                case XMLStreamConstants.START_ELEMENT:
                    handleStartElement(reader.getName().toString().trim());
                    break;

                // if some characters are found    
                case XMLStreamConstants.CHARACTERS:
                    if (!handleCharacters(reader.getText().trim()))
                        return new ArrayList<Point>();
                    break;

                // all other things
                default:
                    break;
                }
            }

            // close all
            inputStream.close();
            reader.close();

        } catch (Exception e) {
            e.printStackTrace();
            try {
                if (inputStream != null) inputStream.close();
                if (reader != null) reader.close();
            } catch (Exception ioe) {
                ioe.printStackTrace();
            }
            return new ArrayList<Point>();
        }

        // return readed data
        return this.data;
    }

    /**
     * called if some characters are readed
     * 
     * @param value
     * @return true if successful
     */
    private boolean handleCharacters(String value) {
        try {
            // if there are a empty char return, this can be happen because there are some whitespace killed by .trim()
            if (value.equals("")) return true;

            // if the readed characters should be the y coordinate ...
            if (this.y) {

                // ... store it ...
                this.yTmp = Integer.parseInt(value);

                // ...  and add it to the arraylist
                if (new File(this.fileName.replace("PreparedText.xml", "Text" + this.numberTmp + "_normal.html")).exists() && new File(this.fileName.replace("PreparedText.xml", "Text" + this.numberTmp + "_highlighted.html")).exists()) {
                    this.data.add(new Point(this.xTmp, this.yTmp));
                } else {
                    System.out.println("File " + this.fileName.replace("PreparedText.xml", "Text" + this.numberTmp + "_normal.html or ") + this.fileName.replace("PreparedText.xml", "Text" + this.numberTmp + "_highlighted.html not found."));
                }

                // reset variables
                this.x = false;
                this.y = false;
                this.number = false;
                this.xTmp = 0;
                this.yTmp = 0;
                this.numberTmp = -1;

                // return success
                return true;
            }

            // if the readed characters should be the x coordinate ...
            if (this.x) {

                // ... store it
                this.xTmp = Integer.parseInt(value);

                // return success
                return true;
            }

            // if the readed characters should be the number ...
            if (this.number) {

                // ... store it
                this.numberTmp = Integer.parseInt(value);

                // return success
                return true;
            }

        } catch (Exception e) {
            // indicate error and return failure
            System.out.println("parsing failed on '" + value + "' in " + this.fileName);
            return false;
        }
        // indicate error and return failure
        System.out.println("parsing failed on '" + value + "' in " + this.fileName);
        return false;
    }

    /**
     * called if a starttag is readed
     * 
     * @param value
     */
    private void handleStartElement(String value) {

        // x
        if (value.equals("number")) {
            this.number = true;
            return;
        }

        // x
        if (value.equals("start")) {
            this.x = true;
            return;
        }

        // y 
        if (value.equals("stop")) {
            this.y = true;
            return;
        }
    }

    /**
     * checks given XML-file 
     * 
     * @param xmlFile 
     * @return true is the given file is valid
     */
    public boolean isValid(File xmlFile) {
        // create xsd file
        File xsdFile = new File(xmlFile.getAbsolutePath().substring(0, xmlFile.getAbsolutePath().lastIndexOf(File.separator) + 1) + "PreparedTextPattern.xsd");
        if (!xsdFile.exists()) {
            System.out.println(xsdFile.getAbsolutePath() + " not found!");
            return false;
        }

        try {
            // create validation factory
            String schemaLang = "http://www.w3.org/2001/XMLSchema";
            SchemaFactory factory = SchemaFactory.newInstance(schemaLang);

            // create validator
            Schema schema = factory.newSchema(xsdFile);
            Validator validator = schema.newValidator();

            // at last perform validation:
            validator.validate(new StreamSource(xmlFile.getAbsolutePath()));

        } catch (SAXException e) {
            System.out.println(xmlFile.getAbsolutePath() + " is not valid!");
            System.out.println();
            return false;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
        return true;
    }
}