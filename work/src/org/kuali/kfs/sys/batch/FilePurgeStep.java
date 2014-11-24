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

import java.util.Date;
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
     * @see org.kuali.kfs.sys.batch.AbstractStep#getRequiredDirectoryNames()
     */
    @Override
    public List<String> getRequiredDirectoryNames() {
        return directories;
    }

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
    public List<String> getDirectories() {
        return directories;
    }

    /**
     * Sets the directories attribute value.
     * @param directories The directories to set.
     */
    public void setDirectories(List<String> directories) {
        this.directories = directories;
    }

    /**
     * Gets the customAges attribute. 
     * @return Returns the customAges.
     */
    public List<FilePurgeCustomAge> getCustomAges() {
        return customAges;
    }

    /**
     * Sets the customAges attribute value.
     * @param customAges The customAges to set.
     */
    public void setCustomAges(List<FilePurgeCustomAge> customAge) {
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
