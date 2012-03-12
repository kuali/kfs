/*
 * Copyright 2009 The Kuali Foundation
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
