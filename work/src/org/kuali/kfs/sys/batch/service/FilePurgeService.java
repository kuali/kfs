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
package org.kuali.kfs.sys.batch.service;

import java.util.List;

import org.kuali.kfs.sys.batch.FilePurgeCustomAge;

/**
 * Methods needed to purge files in the FilePurgeStep
 */
public interface FilePurgeService {
    
    /**
     * Purges old files from the given directory
     * @param directory the directory to purge
     * @param customAges the List of customized ages for files which do not follow the standard
     */
    public abstract void purgeFiles(String directory, List<FilePurgeCustomAge> customAges);
    
    /**
     * Returns the age in days that files matching this custom age should leave matching files before purging
     * @param customAge a custom age to check
     * @return the number of days needed to elapse before purging
     */
    public abstract int getDaysBeforePurgeForCustomAge(FilePurgeCustomAge customAge);
    
    /**
     * Looks up the parameter for the standard number of days before a file should be purged
     * @return the standard number of days before a file should be purged
     */
    public abstract int getStandardDaysBeforePurge();
}
