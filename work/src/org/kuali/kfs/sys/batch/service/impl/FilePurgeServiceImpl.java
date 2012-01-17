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
package org.kuali.kfs.sys.batch.service.impl;

import java.io.File;
import java.util.List;

import org.apache.commons.io.filefilter.AndFileFilter;
import org.apache.commons.io.filefilter.IOFileFilter;
import org.apache.commons.io.filefilter.OrFileFilter;
import org.kuali.kfs.sys.FileUtil;
import org.kuali.kfs.sys.batch.FilePurgeCustomAge;
import org.kuali.kfs.sys.batch.FilePurgeDirectoryWalker;
import org.kuali.kfs.sys.batch.FilePurgeStep;
import org.kuali.kfs.sys.batch.MaxAgePurgeFileFilter;
import org.kuali.kfs.sys.batch.NotAmongDirectoriesFileFilter;
import org.kuali.kfs.sys.batch.service.FilePurgeService;
import org.kuali.rice.coreservice.framework.parameter.ParameterService;

/**
 * Default implementation of the FilePurgeService
 */
public class FilePurgeServiceImpl implements FilePurgeService {
    protected org.apache.log4j.Logger LOG = org.apache.log4j.Logger.getLogger(FilePurgeServiceImpl.class);
    private ParameterService parameterService;
    
    protected static final String DAYS_BEFORE_PURGE_PARAMETER_SUFFIX = "_NUMBER_OF_DAYS_OLD";
    protected static final String DAYS_BEFORE_PURGE_PARAMETER_PREFIX = "DEFAULT";
    
    /**
     * Uses a FilePurgeDirectoryWalker to get a List of Files to purge, then purges each
     * @see org.kuali.kfs.gl.batch.service.FilePurgeService#purgeFiles(java.lang.String, java.util.List)
     */
    public void purgeFiles(String directory, List<FilePurgeCustomAge> customAges) {
        //add a step to check for directory existence
        FileUtil.createDirectory(directory);
        
        purgeCustomAgeFiles(directory, customAges);
        purgeDefaultFiles(directory, customAges);
    }
    
    /**
     * Purges any files in the given directory associated with custom ages
     * @param directory the directory to purge files from
     * @param customAges custom ages to purge files for
     */
    protected void purgeCustomAgeFiles(String directory, List<FilePurgeCustomAge> customAges) {
        // purge custom age directories
        if (customAges != null && customAges.size() > 0) {
            FilePurgeDirectoryWalker directoryWalker = getCustomAgesDirectoryWalker(customAges);
            List<File> filesToPurge = directoryWalker.getFilesToPurge(directory);
            for (File fileToPurge : filesToPurge) {
                LOG.info("Purging file "+fileToPurge.getPath());
                fileToPurge.delete();
            }
        }
    }
    
    /**
     * Purges any files in the given directory not associated with custom ages
     * @param directory the directory to purge files from
     * @param customAges the custom ages with directories to avoid
     */
    protected void purgeDefaultFiles(String directory, List<FilePurgeCustomAge> customAges) {
        // purge standard directories
        FilePurgeDirectoryWalker directoryWalker = getDefaultDirectoryWalker(customAges);
        List<File> filesToPurge = directoryWalker.getFilesToPurge(directory);
        for (File fileToPurge : filesToPurge) {
            LOG.info("Purging file "+fileToPurge.getPath());
            fileToPurge.delete();
        }
    }
    
    /**
     * Gets a directory walker which will 
     * @param customAges the custom ages to purge files for
     * @return a new FilePurgeDirectoryWalker which will walk directories for us
     */
    protected FilePurgeDirectoryWalker getCustomAgesDirectoryWalker(List<FilePurgeCustomAge> customAges) {
        OrFileFilter fileFilter = new OrFileFilter();
        for (FilePurgeCustomAge customAge : customAges) {
            fileFilter.addFileFilter(customAge.getFileFilter());
        }
        return new FilePurgeDirectoryWalker(fileFilter);
    }
    
    /**
     * Gets the directory walker for the default directories
     * @param customAges the custom ages, because custom age directories will not be purged
     * @return a new FilePurgeDirectoryWalker
     */
    protected FilePurgeDirectoryWalker getDefaultDirectoryWalker(List<FilePurgeCustomAge> customAges) {
        IOFileFilter ageFileFilter = buildDefaultAgeFileFilter();
        if (customAges != null && customAges.size() > 0) {
            AndFileFilter andFileFilter = new AndFileFilter();
            andFileFilter.addFileFilter(ageFileFilter);
            andFileFilter.addFileFilter(buildAnyDirectoryButCustomAgeDirectoryFileFilter(customAges));
            return new FilePurgeDirectoryWalker(andFileFilter);
        } else {
            return new FilePurgeDirectoryWalker(ageFileFilter);
        }
    }
    
    /**
     * Builds a file filter which will skip the directories taken by the CustomAges
     * @param customAges the customAges to avoid
     * @return a file filter
     */
    protected IOFileFilter buildAnyDirectoryButCustomAgeDirectoryFileFilter(List<FilePurgeCustomAge> customAges) {
        NotAmongDirectoriesFileFilter skipDirectoriesFileFilter = new NotAmongDirectoriesFileFilter(customAges);
        return skipDirectoriesFileFilter;
    }

    /**
     * @see org.kuali.kfs.gl.batch.service.FilePurgeService#getAgeInDaysForCustomAge(org.kuali.kfs.sys.batch.FilePurgeCustomAge)
     */
    public int getDaysBeforePurgeForCustomAge(FilePurgeCustomAge customAge) {
        final String parameterName = customAge.getParameterPrefix()+getDaysBeforePurgeSuffix();
        return retrieveDaysBeforePurgeParameterValue(parameterName);
    }
    
    /**
     * @return the standard suffix of parameter names from a custom age
     */
    protected String getDaysBeforePurgeSuffix() {
        return FilePurgeServiceImpl.DAYS_BEFORE_PURGE_PARAMETER_SUFFIX;
    }

    /**
     * @see org.kuali.kfs.gl.batch.service.FilePurgeService#getStandardDaysBeforePurge()
     */
    public int getStandardDaysBeforePurge() {
        final String parameterName = getStandardDaysBeforePurgeParameterName();
        return retrieveDaysBeforePurgeParameterValue(parameterName);
    }
    
    /**
     * @return the parameter name to find the default days before purging files
     */
    protected String getStandardDaysBeforePurgeParameterName() {
        return FilePurgeServiceImpl.DAYS_BEFORE_PURGE_PARAMETER_PREFIX+FilePurgeServiceImpl.DAYS_BEFORE_PURGE_PARAMETER_SUFFIX;
    }
    
    /**
     * Retrieves the parameter value of the KFS-SYS / FilePurgeStep / parameterName parameter and converts it to an integer number of days
     * @param parameterName the name of the parameter to retrieve the value of
     * @return the integer number of days
     */
    protected int retrieveDaysBeforePurgeParameterValue(String parameterName) {
        final String parameterValue = getParameterService().getParameterValueAsString(FilePurgeStep.class, parameterName);
        Integer parameterValueAsInteger = null;
        parameterValueAsInteger = new Integer(parameterValue);
        return (parameterValueAsInteger == null ? Integer.MAX_VALUE : parameterValueAsInteger.intValue());
    }

    /**
     * Builds an age file filter for the default removal run
     * @return a properly constructed IOFileFilter
     */
    protected IOFileFilter buildDefaultAgeFileFilter() {
        return new MaxAgePurgeFileFilter();
    }

    /**
     * Gets the parameterService attribute. 
     * @return Returns the parameterService.
     */
    public ParameterService getParameterService() {
        return parameterService;
    }

    /**
     * Sets the parameterService attribute value.
     * @param parameterService The parameterService to set.
     */
    public void setParameterService(ParameterService parameterService) {
        this.parameterService = parameterService;
    }
}
