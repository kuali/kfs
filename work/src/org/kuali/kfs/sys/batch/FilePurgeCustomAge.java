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
package org.kuali.kfs.sys.batch;

import java.util.Calendar;

import org.apache.commons.io.filefilter.AgeFileFilter;
import org.apache.commons.io.filefilter.AndFileFilter;
import org.apache.commons.io.filefilter.IOFileFilter;
import org.kuali.kfs.sys.batch.service.FilePurgeService;
import org.kuali.kfs.sys.context.SpringContext;
import org.kuali.rice.kns.service.DateTimeService;

/**
 * A customized age for a file prefix.
 */
public class FilePurgeCustomAge {

    private String directory;
    private String parameterPrefix;
    
    /**
     * Gets the directory attribute. 
     * @return Returns the directory.
     */
    public String getDirectory() {
        return directory;
    }
    /**
     * Sets the directory attribute value.
     * @param directory The directory to set.
     */
    public void setDirectory(String directory) {
        this.directory = directory;
    }
    /**
     * Gets the parameterPrefix attribute. 
     * @return Returns the parameterPrefix.
     */
    public String getParameterPrefix() {
        return parameterPrefix;
    }
    /**
     * Sets the parameterPrefix attribute value.
     * @param parameterPrefix The parameterPrefix to set.
     */
    public void setParameterPrefix(String parameterPrefix) {
        this.parameterPrefix = parameterPrefix;
    }
    
    /**
     * @return an IOFileFilter which represents the files that should be culled by this FilePurgeCustomAge
     */
    public IOFileFilter getFileFilter() {
        AndFileFilter andFileFilter = new AndFileFilter();
        AgeFileFilter maxAgeFilter = buildAgeFileFilter();
        DirectoryNameFileFilter directoryNameFilter = new DirectoryNameFileFilter(this);
        andFileFilter.addFileFilter(maxAgeFilter);
        andFileFilter.addFileFilter(directoryNameFilter);
        return andFileFilter;
    }
    
    /**
     * Builds a proper AgeFileFilter to purge files older than for this CustomAgeFileFilter
     * @return a properly constructed AgeFileFilter
     */
    protected AgeFileFilter buildAgeFileFilter() {
        final int daysTilPurgation = SpringContext.getBean(FilePurgeService.class).getDaysBeforePurgeForCustomAge(this);
        Calendar purgeFilesBeforeDate = SpringContext.getBean(DateTimeService.class).getCurrentCalendar();
        purgeFilesBeforeDate.add(Calendar.DATE, -1*daysTilPurgation);
        return new AgeFileFilter(purgeFilesBeforeDate.getTimeInMillis(), true);
    }
    
}
