/*
 * Copyright 2007 The Kuali Foundation
 * 
 * Licensed under the Educational Community License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 * http://www.opensource.org/licenses/ecl2.php
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.kuali.kfs.gl.batch.service.impl;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.Iterator;
import java.util.NoSuchElementException;

import org.apache.log4j.Logger;
import org.kuali.kfs.gl.businessobject.OriginEntryFull;
import org.kuali.kfs.gl.exception.LoadException;

/**
 * This class lazy loads the origin entries in a flat file. This implementation uses a limited amount of memory because it does not
 * pre-load all of the origin entries at once. (Assuming that the Java garbage collector is working well). However, if the code that
 * uses this iterator stores the contents of this iterator in a big list somewhere, then a lot of memory may be consumed, depending
 * on the size of the file.
 */
public class OriginEntryFileIterator implements Iterator<OriginEntryFull> {
    private static Logger LOG = Logger.getLogger(OriginEntryFileIterator.class);

    protected OriginEntryFull nextEntry;
    protected BufferedReader reader;
    protected int lineNumber;
    protected boolean autoCloseReader;

    /**
     * Constructs a OriginEntryFileIterator
     * 
     * @param reader a reader representing flat-file origin entries
     * @param autoCloseReader whether to automatically close the reader when the end of origin entries has been reached (i.e. when
     *        hasNext() returns false)
     */
    public OriginEntryFileIterator(BufferedReader reader) {
        this(reader, true);
    }

    /**
     * Constructs a OriginEntryFileIterator
     * 
     * @param reader a reader representing flat-file origin entries
     * @param autoCloseReader whether to automatically close the reader when the end of origin entries has been reached (i.e. when
     *        hasNext() returns false)
     */
    public OriginEntryFileIterator(BufferedReader reader, boolean autoCloseReader) {
        if (reader == null) {
            LOG.error("reader is null in the OriginEntryFileIterator!");
            throw new IllegalArgumentException("reader is null!");
        }
        this.reader = reader;
        nextEntry = null;
        lineNumber = 0;
        this.autoCloseReader = autoCloseReader;
    }

    /**
     * Constructs a OriginEntryFileIterator When constructed with this method, the file handle will be automatically closed when the
     * end of origin entries has been reached (i.e. when hasNext() returns false)
     * 
     * @param file the file
     */
    public OriginEntryFileIterator(File file) {
        if (file == null) {
            LOG.error("reader is null in the OriginEntryFileIterator!");
            throw new IllegalArgumentException("reader is null!");
        }
        try {
            this.reader = new BufferedReader(new FileReader(file));
            this.autoCloseReader = true;
            nextEntry = null;
            lineNumber = 0;
        }
        catch (FileNotFoundException e) {
            LOG.error("File not found for OriginEntryFileIterator! " + file.getAbsolutePath(), e);
            throw new RuntimeException("File not found for OriginEntryFileIterator! " + file.getAbsolutePath());
        }
    }

    /**
     * @see java.util.Iterator#hasNext()
     */
    public boolean hasNext() {
        if (nextEntry == null) {
            fetchNextEntry();
            return nextEntry != null;
        }
        else {
            // we have the next entry loaded
            return true;
        }
    }

    /**
     * @see java.util.Iterator#next()
     */
    public OriginEntryFull next() {
        if (nextEntry != null) {
            // an entry may have been fetched by hasNext()
            OriginEntryFull temp = nextEntry;
            nextEntry = null;
            return temp;
        }
        else {
            // maybe next() is called repeatedly w/o calling hasNext. This is a bad idea, but the
            // interface allows it
            fetchNextEntry();
            if (nextEntry == null) {
                throw new NoSuchElementException();
            }

            // clear out the nextEntry to signal that no record has been loaded
            OriginEntryFull temp = nextEntry;
            nextEntry = null;
            return temp;
        }
    }

    /**
     * @see java.util.Iterator#remove()
     */
    public void remove() {
        throw new UnsupportedOperationException("Cannot remove entry from collection");
    }

    /**
     * This method returns the next line in origin entry file
     */
    protected void fetchNextEntry() {
        try {
            lineNumber++;
            String line = reader.readLine();
            if (line == null) {
                nextEntry = null;
                if (autoCloseReader) {
                    reader.close();
                }
            }
            else {
                nextEntry = new OriginEntryFull();
                try {
                    nextEntry.setFromTextFileForBatch(line, lineNumber - 1);
                }
                catch (LoadException e) {
                    // wipe out the next entry so that the next call to hasNext or next will force a new row to be fetched
                    nextEntry = null;

                    // if there's an LoadException, then we'll just let it propagate up the call stack
                    throw e;
                }
            }
        }
        catch (IOException e) {
            LOG.error("error in the CorrectionDocumentServiceImpl iterator", e);
            nextEntry = null;
            throw new RuntimeException("error retrieving origin entries");
        }
    }

    /**
     * @see java.lang.Object#finalize()
     */
    @Override
    protected void finalize() throws Throwable {
        super.finalize();
        if (autoCloseReader && reader != null) {
            reader.close();
        }
    }
}
