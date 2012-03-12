/*
 * Copyright 2005 The Kuali Foundation
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
package org.kuali.kfs.gl.service.impl;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FilenameFilter;
import java.io.IOException;
import java.io.PrintStream;
import java.util.Arrays;
import java.util.List;

import org.apache.commons.lang.StringUtils;
import org.kuali.kfs.gl.GeneralLedgerConstants;
import org.kuali.kfs.gl.service.OriginEntryGroupService;
import org.kuali.rice.core.api.datetime.DateTimeService;
import org.kuali.rice.krad.service.KualiModuleService;
import org.springframework.transaction.annotation.Transactional;

/**
 * @see org.kuali.kfs.gl.service.OriginEntryGroupService
 */
@Transactional
public class OriginEntryGroupServiceImpl implements OriginEntryGroupService {
    private static final org.apache.log4j.Logger LOG = org.apache.log4j.Logger.getLogger(OriginEntryGroupServiceImpl.class);

    protected DateTimeService dateTimeService;
    protected String batchFileDirectoryName;
    protected KualiModuleService kualiModuleService;
    protected String nightlyOutFileName;
    protected String backupFileName;

    /**
     * @see org.kuali.kfs.gl.service.OriginEntryGroupService#getNewestScrubberErrorFileName()
     */
    public String getNewestScrubberErrorFileName() {
        File newestFile = null;

        File[] files = null;
        // can add filter here: listFiles(filter); -- check out originEntryTestBase from Jeff
        if (new File(batchFileDirectoryName) == null) {
            return null;
        }
        files = new File(batchFileDirectoryName).listFiles(new ScrubberErrorFilenameFilter());
        List<File> fileList = Arrays.asList(files);
        if (fileList.size() > 0) {
            for (File eachFile : fileList) {
                if (newestFile == null) {
                    newestFile = eachFile;
                }
                else {
                    if (newestFile.lastModified() < eachFile.lastModified()) {
                        newestFile = eachFile;
                    }
                }
            }
        }
        else {
            return null;
        }

        return newestFile.getName();
    }


    /**
     * Retrieves all groups to be created today, and creates backup group versions of them
     * 
     * @see org.kuali.kfs.gl.service.OriginEntryGroupService#createBackupGroup()
     */
    public void createBackupGroup() {
        LOG.debug("createBackupGroup() started");
        // check file from nightly out
        File nightlyOutFile = new File(batchFileDirectoryName + File.separator + nightlyOutFileName);
        if (!nightlyOutFile.exists()) {
            LOG.warn("nightlyOutFile doesn't exist :" + nightlyOutFileName);
        }

        String backupFile = batchFileDirectoryName + File.separator + backupFileName + GeneralLedgerConstants.BatchFileSystem.EXTENSION;
        PrintStream backupPs = null;
        try {
            backupPs = new PrintStream(backupFile);
        }
        catch (FileNotFoundException e) {
            throw new RuntimeException("backupFile doesn't exist " + backupFile);
        }

        // get all done files from originEntry Directory
        File[] doneFileList = new File(batchFileDirectoryName).listFiles(new DoneFileFilter());
        // build output file with doneFileList and print stream
        buildBackupFileOutput(doneFileList, backupPs);
        backupPs.close();
    }


    /*
     * buildBackupFileOuput with doneFileList and PrintStream
     */
    protected void buildBackupFileOutput(File[] doneFileList, PrintStream ps) {
        BufferedReader inputFileReader = null;

        for (File doneFile : doneFileList) {
            // get data file with done file
            File dataFile = getDataFile(doneFile);
            if (dataFile != null) {
                try {
                    inputFileReader = new BufferedReader(new FileReader(dataFile.getPath()));
                    String line = null;
                    while ((line = inputFileReader.readLine()) != null) {
                        try {
                            ps.printf("%s\n", line);
                        }
                        catch (Exception e) {
                            throw new IOException(e.toString());
                        }
                    }
                    inputFileReader.close();
                    inputFileReader = null;

                }
                catch (Exception e) {
                    throw new RuntimeException(e.toString());
                }

                doneFile.delete();
                postProcessDataFile(dataFile);
                
            }
        }
    }
    
    protected void postProcessDataFile( File dataFile )
    {
        // do nothing.  A hook for institution extension.
    }


    /**
     * @see org.kuali.kfs.gl.service.OriginEntryGroupService#createGroup(java.lang.String)
     */
    public File createGroup(String fileName) {
        return new File(batchFileDirectoryName + File.separator + fileName);
    }

    /**
     * @see org.kuali.kfs.gl.service.OriginEntryGroupService#getGroupExists(java.lang.String)
     */
    public boolean getGroupExists(String groupId) {

        File file = new File(batchFileDirectoryName + File.separator + groupId);
        if (file == null) {
            return false;
        }
        else {
            return true;
        }
    }

    /**
     * @see org.kuali.kfs.gl.service.OriginEntryGroupService#getAllFileInBatchDirectory()
     */
    public File[] getAllFileInBatchDirectory() {
        File[] returnFiles = null;
        if (new File(batchFileDirectoryName) != null) {
            returnFiles = new File(batchFileDirectoryName).listFiles(new DateAndDoneFileFilter());
        }
        return returnFiles;
    }


    /**
     * @see org.kuali.kfs.gl.service.OriginEntryGroupService#deleteFile(java.lang.String)
     */
    public void deleteFile(String fileNameWithPath) {
        File file = new File(fileNameWithPath);
        if (file.exists()) {
            file.delete();
        }
    }

    /**
     * @see org.kuali.kfs.gl.service.OriginEntryGroupService#getLaborFileWithFileName(java.lang.String)
     */
    public File getFileWithFileName(String fileName) {
        return new File(batchFileDirectoryName + File.separator + fileName);
    }

    protected class ScrubberErrorFilenameFilter implements FilenameFilter {
        /**
         * @see java.io.FilenameFilter#accept(java.io.File, java.lang.String)
         */
        public boolean accept(File dir, String name) {
            return name.contains(GeneralLedgerConstants.BatchFileSystem.SCRUBBER_ERROR_PREFIX);
        }
    }

    protected class DateAndDoneFileFilter implements FilenameFilter {
        /**
         * @see java.io.FilenameFilter#accept(java.io.File, java.lang.String)
         */
        public boolean accept(File dir, String name) {
            return name.contains(GeneralLedgerConstants.BatchFileSystem.DONE_FILE_EXTENSION) || name.contains(GeneralLedgerConstants.BatchFileSystem.EXTENSION);
        }
    }

    protected class DoneFileFilter implements FilenameFilter {
        /**
         * @see java.io.FilenameFilter#accept(java.io.File, java.lang.String)
         */
        public boolean accept(File dir, String name) {
            return name.contains(GeneralLedgerConstants.BatchFileSystem.DONE_FILE_EXTENSION);
        }
    }

    protected File getDataFile(File doneFile) {
        String doneFileAbsPath = doneFile.getAbsolutePath();
        if (!doneFileAbsPath.endsWith(GeneralLedgerConstants.BatchFileSystem.DONE_FILE_EXTENSION)) {
            throw new IllegalArgumentException("Done file name must end with " + GeneralLedgerConstants.BatchFileSystem.DONE_FILE_EXTENSION);
        }
        String dataFileAbsPath = StringUtils.removeEnd(doneFileAbsPath, GeneralLedgerConstants.BatchFileSystem.DONE_FILE_EXTENSION) + GeneralLedgerConstants.BatchFileSystem.EXTENSION;
        File dataFile = new File(dataFileAbsPath);
        if (!dataFile.exists() || !dataFile.canRead()) {
            LOG.error("Cannot find/read data file " + dataFileAbsPath);
            return null;
        }
        return dataFile;
    }
    
    public void setBatchFileDirectoryName(String batchFileDirectoryName) {
        this.batchFileDirectoryName = batchFileDirectoryName;
    }


    public void setKualiModuleService(KualiModuleService kualiModuleService) {
        this.kualiModuleService = kualiModuleService;
    }
    
    public void setDateTimeService(DateTimeService dts) {
        dateTimeService = dts;
    }

    public void setNightlyOutFileName(String nightlyOutFileName) {
        this.nightlyOutFileName = nightlyOutFileName;
    }

    public void setBackupFileName(String backupFileName) {
        this.backupFileName = backupFileName;
    }

    protected DateTimeService getDateTimeService() {
        return dateTimeService;
    }

    protected String getBatchFileDirectoryName() {
        return batchFileDirectoryName;
    }

    protected KualiModuleService getKualiModuleService() {
        return kualiModuleService;
    }
    
    
}
