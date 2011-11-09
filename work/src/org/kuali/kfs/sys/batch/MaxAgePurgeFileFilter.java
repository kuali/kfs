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
package org.kuali.kfs.sys.batch;

import java.io.File;
import java.util.Calendar;

import org.apache.commons.io.filefilter.IOFileFilter;
import org.kuali.kfs.sys.batch.service.FilePurgeService;
import org.kuali.kfs.sys.context.SpringContext;
import org.kuali.rice.core.api.datetime.DateTimeService;

/**
 * A file filter which only accepts any directory or files older than a set age
 */
public class MaxAgePurgeFileFilter implements IOFileFilter {
    private long minTimestamp;
    
    /**
     * Constructs a MaxAgePurgeFileFilter, using the default time to filter older files from
     */
    public MaxAgePurgeFileFilter() {
        final int daysPastToPurge = SpringContext.getBean(FilePurgeService.class).getStandardDaysBeforePurge();
        this.minTimestamp = calculateMinTimestamp(daysPastToPurge);
    }
    
    /**
     * Constructs a MaxAgePurgeFileFilter, using the time set with the parameter associated with the given file purge custom age to get rid of files from
     * @param filePurgeCustomAge the custom age to get a max file age from
     */
    public MaxAgePurgeFileFilter(FilePurgeCustomAge filePurgeCustomAge) {
        final int daysPastToPurge = SpringContext.getBean(FilePurgeService.class).getDaysBeforePurgeForCustomAge(filePurgeCustomAge);
        this.minTimestamp = calculateMinTimestamp(daysPastToPurge);
    }
    
    /**
     * Determines the timestamp any older files should be purged on
     * @param daysPastToPurge the number of days a file should exist before purging
     * @return the timestamp of that number of days earlier than "now" (as defined by DateTimeService) that is
     */
    private long calculateMinTimestamp(int daysPastToPurge) {
        Calendar purgeFilesBeforeDate = SpringContext.getBean(DateTimeService.class).getCurrentCalendar();
        purgeFilesBeforeDate.add(Calendar.DATE, -1*daysPastToPurge);
        return purgeFilesBeforeDate.getTimeInMillis();
    }

    /**
     * Accepts any directory and any file older than our min timestamp
     * @see org.apache.commons.io.filefilter.IOFileFilter#accept(java.io.File)
     */
    public boolean accept(File file) {
        if (file.isDirectory()) return true;
        return (file.lastModified() < this.minTimestamp);
    }

    /**
     * Accepts any directory, and any file which is older than our min timestamp
     * @see org.apache.commons.io.filefilter.IOFileFilter#accept(java.io.File, java.lang.String)
     */
    public boolean accept(File directory, String fileName) {
        final File file = new File(directory.getName()+File.separator+fileName);
        if (file.isDirectory()) return true;
        return (file.lastModified() < this.minTimestamp);
    }

}
