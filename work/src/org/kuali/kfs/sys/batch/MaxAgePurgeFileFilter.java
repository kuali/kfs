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
