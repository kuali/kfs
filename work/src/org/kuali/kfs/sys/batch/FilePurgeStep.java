/*
 * Copyright 2007 The Kuali Foundation.
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

import java.util.Date;
import java.util.HashMap;
import java.util.List;

import org.kuali.kfs.sys.batch.service.FilePurgeService;

/**
 * 
 * Purges old files from the reports directory specified in build.properties
 */
public class FilePurgeStep extends AbstractStep {
    
    private List<String> directories;
    private List<FilePurgeCustomAge> customAges;
    private FilePurgeService filePurgeService;
    
    /**
     * Deletes all files in the temporary directory that are over 1 day old
     * 
     * @see org.kuali.kfs.sys.batch.Step#execute(String, Date)
     */
    public boolean execute(String jobName, Date jobRunDate) throws InterruptedException {
        for (String directory : directories) {
            getFilePurgeService().purgeFiles(directory, customAges);
        }
        
        return true;
    }

    /**
     * Gets the directories attribute. 
     * @return Returns the directories.
     */
    public List getDirectories() {
        return directories;
    }

    /**
     * Sets the directories attribute value.
     * @param directories The directories to set.
     */
    public void setDirectories(List directories) {
        this.directories = directories;
    }

    /**
     * Gets the customAges attribute. 
     * @return Returns the customAges.
     */
    public List getCustomAges() {
        return customAges;
    }

    /**
     * Sets the customAges attribute value.
     * @param customAges The customAges to set.
     */
    public void setCustomAges(List customAge) {
        this.customAges = customAge;
    }

    /**
     * Gets the filePurgeService attribute. 
     * @return Returns the filePurgeService.
     */
    public FilePurgeService getFilePurgeService() {
        return filePurgeService;
    }

    /**
     * Sets the filePurgeService attribute value.
     * @param filePurgeService The filePurgeService to set.
     */
    public void setFilePurgeService(FilePurgeService filePurgeService) {
        this.filePurgeService = filePurgeService;
    }
}
