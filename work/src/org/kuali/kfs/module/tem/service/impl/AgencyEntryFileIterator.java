/*
 * Copyright 2011 The Kuali Foundation.
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
package org.kuali.kfs.module.tem.service.impl;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.util.Iterator;

import org.apache.log4j.Logger;
import org.kuali.kfs.module.tem.businessobject.AgencyEntryFull;

public class AgencyEntryFileIterator implements Iterator<AgencyEntryFull> {
    private static Logger LOG = Logger.getLogger(AgencyEntryFileIterator.class);
    
    protected AgencyEntryFull nextEntry;
    protected BufferedReader reader;
    protected int lineNumber;
    protected boolean autoCloseReader;
    /**
     * Constructs a AgencyEntryFileIterator When constructed with this method, the file handle will be automatically closed when the
     * end of origin entries has been reached (i.e. when hasNext() returns false)
     * 
     * @param file the file
     */
    public AgencyEntryFileIterator(File file) {
        if (file == null) {
            LOG.error("reader is null in the AgencyEntryFileIterator!");
            throw new IllegalArgumentException("reader is null!");
        }
        try {
            this.reader = new BufferedReader(new FileReader(file));
            this.autoCloseReader = true;
            nextEntry = null;
            lineNumber = 0;
        }
        catch (FileNotFoundException e) {
            LOG.error("File not found for AgencyEntryFileIterator! " + file.getAbsolutePath(), e);
            throw new RuntimeException("File not found for AgencyEntryFileIterator! " + file.getAbsolutePath());
        }
    }

    @Override
    public boolean hasNext() {
        // TODO Auto-generated method stub
        return false;
    }

    @Override
    public AgencyEntryFull next() {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public void remove() {
        // TODO Auto-generated method stub
        
    }

}
