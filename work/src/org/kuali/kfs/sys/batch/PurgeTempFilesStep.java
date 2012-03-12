/*
 * Copyright 2007-2008 The Kuali Foundation
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
import java.util.Date;

import org.apache.commons.lang.StringUtils;
import org.kuali.kfs.sys.KFSConstants;
import org.kuali.rice.core.api.config.property.ConfigurationService;

/**
 * 
 * Purges old files from the temp directory specified in build.properties
 */
public class PurgeTempFilesStep extends AbstractStep {
    
    private ConfigurationService kualiConfigurationService;
    
    /**
     * Deletes all files in the temp directory that are over 1 day old
     * 
     * @see org.kuali.kfs.sys.batch.Step#execute(String, Date)
     */
    public boolean execute(String jobName, Date jobRunDate) throws InterruptedException {
        Calendar calendar = getDateTimeService().getCurrentCalendar();
        calendar.add(Calendar.DATE, -1);
        String location = kualiConfigurationService.getPropertyValueAsString(KFSConstants.TEMP_DIRECTORY_KEY) + File.separator;
        deleteTempBefore(location, calendar.getTimeInMillis());
        return true;
    }

    /**
     * 
     * delete files in the specified directory that are older than the modification time
     * 
     * @param location the path to temp files
     * @param modificationTime delete if file is older than this
     */
    private void deleteTempBefore(String location, long modificationTime) {
        if (StringUtils.isBlank(location)) {
            throw new RuntimeException("temp location is blank");
        }
        File tempDir = new File(location);
        if (!tempDir.exists()) {
            throw new RuntimeException("temp directory does not exist");
        }
        if (!tempDir.isDirectory()) {
            throw new RuntimeException("temp directory is not a directory! " + tempDir.getAbsolutePath());
        }
        try {
            File dir = new File(location);
            String[] files = dir.list();
            for (int i = 0; i < files.length; i++) {
                String filename = files[i];
                File f = new File(location + filename);
                if(f.lastModified() < modificationTime) {
                    f.delete();
                }
            }
        } catch (Exception e) {
            throw new RuntimeException("Caught exception while trying to remove temp files at " + location, e);
        }
    }

    /**
     * Sets the configurationService attribute value.
     * @param configurationService The configurationService to set.
     */
    public void setConfigurationService(ConfigurationService configurationService) {
        this.kualiConfigurationService = configurationService;
    }
    
}
