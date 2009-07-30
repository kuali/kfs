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

import java.io.File;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import org.kuali.kfs.sys.KFSConstants;
import org.kuali.rice.kns.service.DateTimeService;

/**
 * 
 * Purges old files from the reports directory specified in build.properties
 */
public class FilePurgeStep extends AbstractStep {
    
    private List<String> directories;
    private List<FilePurgeCustomAge> customAge;
    private Integer fileAge;
    
    private HashMap<String, Integer> removables = new HashMap<String, Integer>();
    
    /**
     * Deletes all files in the temp directory that are over 1 day old
     * 
     * @see org.kuali.kfs.sys.batch.Step#execute(String, Date)
     */
    public boolean execute(String jobName, Date jobRunDate) throws InterruptedException {
        Calendar calendar = getDateTimeService().getCurrentCalendar();
       // calendar.add(Calendar.DATE, -fileAge);
        //System.out.println(calendar.getTime());
        
        for (Iterator itr = customAge.iterator(); itr.hasNext();) {
            FilePurgeCustomAge fpce = (FilePurgeCustomAge)itr.next();
            for (String path : directories) {
                walkDirectory(path, fpce);
            }

        }
        
        System.out.println(removables.size());

        removeFiles(removables);
        
        return true;
    }
    
    private void removeFiles(HashMap<String, Integer> removables) {
        DateTimeService dateTimeService = getDateTimeService();
        Calendar calendar = dateTimeService.getCurrentCalendar();
        calendar.add(Calendar.DATE, -1);
        Set keys = removables.keySet();
        for (Iterator itr = keys.iterator(); itr.hasNext();) {
            String path = (String)itr.next();
            File f = new File(path);
            Integer expirationAge = removables.get(path);
            calendar.add(Calendar.DATE, -expirationAge);
            if(f.lastModified() < calendar.getTimeInMillis()) {
                f.delete();
            }
            calendar = dateTimeService.getCurrentCalendar();
        }
    }
    
    private void walkDirectory(String path, FilePurgeCustomAge customFileAge) {
        File directory = new File(path);
//        if (directory.isDirectory()){
//            if (directory.getPath().equals(customFileAge.getDirectory())) {
//                String maxAgeInDays = getParameterService().getParameterValue(getClass(), customFileAge.parameterPrefix+KFSConstants.SystemGroupParameterNames.FILE_PURGE_AGE_SUFFIX);
//                removables.put(customFileAge.getDirectory(), Integer.valueOf(maxAgeInDays));
//            } else {
//                String maxAgeInDays = getParameterService().getParameterValue(getClass(), KFSConstants.SystemGroupParameterNames.DEFAULT_FILE_PURGE_AGE);
//                removables.put(customFileAge.getDirectory(), Integer.valueOf(maxAgeInDays));
//            }
//        }
        File[] children = directory.listFiles();
        for (File f : children) {
            if (f.isDirectory()) {
                walkDirectory(f.getPath(), customFileAge);
            } else {
                if (f.getPath().startsWith(customFileAge.getDirectory()+ File.separator+customFileAge.getFileNameStart())) {
                    String maxAgeInDays = getParameterService().getParameterValue(getClass(), customFileAge.parameterPrefix+KFSConstants.SystemGroupParameterNames.FILE_PURGE_AGE_SUFFIX);
                    removables.put(f.getPath(), Integer.valueOf(maxAgeInDays));
                } else {
                    String maxAgeInDays = getParameterService().getParameterValue(getClass(), KFSConstants.SystemGroupParameterNames.DEFAULT_FILE_PURGE_AGE);
                    removables.put(f.getPath(), Integer.valueOf(maxAgeInDays));
                }
            }
                
        }

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
     * Gets the customAge attribute. 
     * @return Returns the customAge.
     */
    public List getCustomAge() {
        return customAge;
    }

    /**
     * Sets the customAge attribute value.
     * @param customAge The customAge to set.
     */
    public void setCustomAge(List customAge) {
        this.customAge = customAge;
    }

    /**
     * Gets the fileAge attribute. 
     * @return Returns the fileAge.
     */
    public Integer getFileAge() {
        return fileAge;
    }

    /**
     * Sets the fileAge attribute value.
     * @param fileAge The fileAge to set.
     */
    public void setFileAge(Integer fileAge) {
        this.fileAge = fileAge;
    }
    
}
