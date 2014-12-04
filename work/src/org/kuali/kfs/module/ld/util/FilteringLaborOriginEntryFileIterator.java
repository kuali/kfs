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
package org.kuali.kfs.module.ld.util;

import java.io.BufferedReader;
import java.io.File;

import org.kuali.kfs.module.ld.businessobject.LaborOriginEntry;

public class FilteringLaborOriginEntryFileIterator extends LaborOriginEntryFileIterator {
    private static org.apache.log4j.Logger LOG = org.apache.log4j.Logger.getLogger(FilteringLaborOriginEntryFileIterator.class);
    
    public static interface LaborOriginEntryFilter {
        public boolean accept(LaborOriginEntry originEntry);
    }
    
    protected LaborOriginEntryFilter filter;
    
    /**
     * Constructs a LaborOriginEntryFileIterator
     * 
     * @param reader a reader representing flat-file origin entries
     * @param autoCloseReader whether to automatically close the reader when the end of origin entries has been reached (i.e. when
     *        hasNext() returns false)
     */
    public FilteringLaborOriginEntryFileIterator(BufferedReader reader, LaborOriginEntryFilter filter) {
        super(reader, true);
        this.filter = filter;
    }

    /**
     * Constructs a LaborOriginEntryFileIterator
     * 
     * @param reader a reader representing flat-file origin entries
     * @param autoCloseReader whether to automatically close the reader when the end of origin entries has been reached (i.e. when
     *        hasNext() returns false)
     */
    public FilteringLaborOriginEntryFileIterator(BufferedReader reader, boolean autoCloseReader, LaborOriginEntryFilter filter) {
        super(reader, autoCloseReader);
        this.filter = filter;
    }

    /**
     * Constructs a LaborOriginEntryFilter When constructed with this method, the file handle will be automatically closed when the
     * end of origin entries has been reached (i.e. when hasNext() returns false)
     * 
     * @param file the file
     */
    public FilteringLaborOriginEntryFileIterator(File file, LaborOriginEntryFilter filter) {
        super(file);
        this.filter = filter;
    }

    /**
     * @see org.kuali.kfs.gl.batch.service.impl.OriginEntryFileIterator#fetchNextEntry()
     */
    @Override
    protected void fetchNextEntry() {
        do {
            super.fetchNextEntry();            
        }
        while (nextEntry != null && !filter.accept(nextEntry));
    }   
}
