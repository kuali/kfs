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
