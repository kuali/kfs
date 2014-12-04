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
package org.kuali.kfs.gl.batch.service.impl;

import java.io.BufferedReader;
import java.io.File;

import org.kuali.kfs.gl.businessobject.OriginEntryFull;

public class FilteringOriginEntryFileIterator extends OriginEntryFileIterator {
    public static interface OriginEntryFilter {
        public boolean accept(OriginEntryFull originEntry);
    }
    
    protected OriginEntryFilter filter;
    
    /**
     * Constructs a OriginEntryFileIterator
     * 
     * @param reader a reader representing flat-file origin entries
     * @param autoCloseReader whether to automatically close the reader when the end of origin entries has been reached (i.e. when
     *        hasNext() returns false)
     */
    public FilteringOriginEntryFileIterator(BufferedReader reader, OriginEntryFilter filter) {
        super(reader, true);
        this.filter = filter;
    }

    /**
     * Constructs a OriginEntryFileIterator
     * 
     * @param reader a reader representing flat-file origin entries
     * @param autoCloseReader whether to automatically close the reader when the end of origin entries has been reached (i.e. when
     *        hasNext() returns false)
     */
    public FilteringOriginEntryFileIterator(BufferedReader reader, boolean autoCloseReader, OriginEntryFilter filter) {
        super(reader, autoCloseReader);
        this.filter = filter;
    }

    /**
     * Constructs a OriginEntryFileIterator When constructed with this method, the file handle will be automatically closed when the
     * end of origin entries has been reached (i.e. when hasNext() returns false)
     * 
     * @param file the file
     */
    public FilteringOriginEntryFileIterator(File file, OriginEntryFilter filter) {
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
        if (nextEntry != null && !filter.accept(nextEntry)) {
            nextEntry = null;
        }
    }   
}
