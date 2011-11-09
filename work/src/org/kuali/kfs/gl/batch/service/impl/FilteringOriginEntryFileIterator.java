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
