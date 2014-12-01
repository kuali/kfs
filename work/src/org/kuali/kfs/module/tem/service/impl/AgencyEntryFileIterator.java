/*
 * The Kuali Financial System, a comprehensive financial management system for higher education.
 * 
 * Copyright 2005-2014 The Kuali Foundation
 * 
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Affero General Public License as
 * published by the Free Software Foundation, either version 3 of the
 * License, or (at your option) any later version.
 * 
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Affero General Public License for more details.
 * 
 * You should have received a copy of the GNU Affero General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
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
