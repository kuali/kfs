/*
 * Copyright 2009 The Kuali Foundation.
 * 
 * Licensed under the Educational Community License, Version 1.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 * http://www.opensource.org/licenses/ecl1.php
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.kuali.kfs.gl.businessobject;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.Iterator;

/**
 * Iterator over an OriginEntryGroup file
 */
public class OriginEntryGroupFileIterator implements Iterator<OriginEntry> {
    private BufferedReader groupFileReader;
    private String currentLine;
    private int lineCount;
    
    /**
     * Constructs a OriginEntryGroupFileIterator for the file with the given name
     * @param fileName the name of the origin entry group file to read
     */
    public OriginEntryGroupFileIterator(String fileName) {
        try {
            groupFileReader = new BufferedReader(new InputStreamReader(new FileInputStream(fileName)));
            currentLine = groupFileReader.readLine();
            lineCount = 0;
        }
        catch (FileNotFoundException fnfe) {
            throw new RuntimeException("Could not open file: "+fileName, fnfe);
        }
        catch (IOException ioe) {
            throw new RuntimeException("Could not read from file: "+fileName, ioe);
        }
    }

    /**
     * Returns whether a next origin entry in the file exists
     * @return true if there is a next line, false otherwise
     */
    public boolean hasNext() {
        if (currentLine == null) {
            try {
                groupFileReader.close();
            }
            catch (IOException ioe) {
                throw new RuntimeException("Could not close Origin Entry group file", ioe);
            }
            finally {
                groupFileReader = null;
            }
        }
        return currentLine != null;
    }

    /**
     * Returns the next available OriginEntry in the group file
     * @see java.util.Iterator#next()
     */
    public OriginEntry next() {
        if (currentLine == null) return null;
        OriginEntryLite originEntry = new OriginEntryLite();
        originEntry.setFromTextFileForBatch(currentLine, lineCount);
        
        // get the next line
        try {
            currentLine = groupFileReader.readLine();
            lineCount += 1;
        }
        catch (IOException ioe) {
            throw new RuntimeException("Difficulty reading from origin entry group file", ioe);
        }
        
        return originEntry;
    }

    /**
     * 
     * @see java.util.Iterator#remove()
     */
    public void remove() {
        throw new UnsupportedOperationException("OriginEntryGroupFileIterator is read only");
    }

}
