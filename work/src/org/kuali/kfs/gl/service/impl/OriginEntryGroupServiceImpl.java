/*
 * Copyright 2005-2007 The Kuali Foundation.
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
package org.kuali.kfs.gl.service.impl;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FilenameFilter;
import java.io.IOException;
import java.io.PrintStream;
import java.sql.Date;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang.StringUtils;
import org.kuali.kfs.gl.GeneralLedgerConstants;
import org.kuali.kfs.gl.businessobject.OriginEntryGroup;
import org.kuali.kfs.gl.businessobject.OriginEntrySource;
import org.kuali.kfs.gl.dataaccess.OriginEntryDao;
import org.kuali.kfs.gl.dataaccess.OriginEntryGroupDao;
import org.kuali.kfs.gl.service.OriginEntryGroupService;
import org.kuali.kfs.integration.ld.LaborModuleService;
import org.kuali.kfs.module.ld.LaborConstants;
import org.kuali.kfs.sys.KFSConstants;
import org.kuali.kfs.sys.context.SpringContext;
import org.kuali.rice.kns.service.DateTimeService;
import org.kuali.rice.kns.service.KualiModuleService;
import org.springframework.transaction.annotation.Transactional;

/**
 * @see org.kuali.kfs.gl.service.OriginEntryGroupService
 */
@Transactional
public class OriginEntryGroupServiceImpl implements OriginEntryGroupService {
    private static org.apache.log4j.Logger LOG = org.apache.log4j.Logger.getLogger(OriginEntryGroupServiceImpl.class);

    private DateTimeService dateTimeService;
    private String batchFileDirectoryName;
    private String batchLaborFileDirectoryName;
    private KualiModuleService kualiModuleService;

    /**
     * Constructs a OriginEntryGroupServiceImpl instance
     */
    public OriginEntryGroupServiceImpl() {
        super();
    }

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
     * @see org.kuali.kfs.gl.service.OriginEntryGroupService#getNewestScrubberErrorLaborFileName()
     */
    public String getNewestScrubberErrorLaborFileName() {
        File newestFile = null;

        File[] files = null;
        // can add filter here: listFiles(filter); -- check out originEntryTestBase from Jeff
        if (new File(batchLaborFileDirectoryName) == null) {
            return null;
        }
        files = new File(batchLaborFileDirectoryName).listFiles(new ScrubberErrorFilenameFilter());
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
        String nightlyOutFileName = GeneralLedgerConstants.BatchFileSystem.NIGHTLY_OUT_FILE + GeneralLedgerConstants.BatchFileSystem.EXTENSION;
        File nightlyOutFile = new File(batchFileDirectoryName + File.separator + nightlyOutFileName);
        if (!nightlyOutFile.exists()) {
            LOG.warn("nightlyOutFile doesn't exist :" + nightlyOutFileName);
        }

        String backupFileName = batchFileDirectoryName + File.separator + GeneralLedgerConstants.BatchFileSystem.BACKUP_FILE + GeneralLedgerConstants.BatchFileSystem.EXTENSION;
        PrintStream backupPs = null;
        try {
            backupPs = new PrintStream(backupFileName);
        }
        catch (FileNotFoundException e) {
            throw new RuntimeException("backupFile doesn't exist " + backupFileName);
        }

        // get all done files from originEntry Directory
        File[] doneFileList = new File(batchFileDirectoryName).listFiles(new DoneFileFilter());
        // build output file with doneFileList and print stream
        buildBackupFileOutput(doneFileList, backupPs);
        backupPs.close();
    }

    /**
     * @see org.kuali.kfs.gl.service.OriginEntryGroupService#createLaborBackupGroup()
     */
    public void createLaborBackupGroup() {
        LOG.debug("createLaborBackupGroup() started");

        // Get the groups that need to be added
        Date today = dateTimeService.getCurrentSqlDate();

        // check file from nightly out
        String laborNightlyOutFileName = LaborConstants.BatchFileSystem.NIGHTLY_OUT_FILE + GeneralLedgerConstants.BatchFileSystem.EXTENSION;
        File laborNightlyOutFile = new File(batchLaborFileDirectoryName + File.separator + laborNightlyOutFileName);
        if (!laborNightlyOutFile.exists()) {
            LOG.warn("laborNightlyOutFile doesn't exist :" + laborNightlyOutFileName);
        }

        String laborBackupFileName = LaborConstants.BatchFileSystem.BACKUP_FILE + GeneralLedgerConstants.BatchFileSystem.EXTENSION;
        File laborBackupFile = new File(batchLaborFileDirectoryName + File.separator + laborBackupFileName);

        PrintStream laborBackupPs = null;
        try {
            laborBackupPs = new PrintStream(laborBackupFile);
        }
        catch (FileNotFoundException e) {
            throw new RuntimeException("laborBackupFile doesn't exist " + laborBackupFile);
        }

        // get all done files from originEntry Directory
        File[] doneFileList = new File(batchLaborFileDirectoryName).listFiles(new DoneFileFilter());
        // build output file with doneFileList and print stream
        buildBackupFileOutput(doneFileList, laborBackupPs);
        laborBackupPs.close();
    }

    /*
     * buildBackupFileOuput with doneFileList and PrintStream
     */
    private void buildBackupFileOutput(File[] doneFileList, PrintStream ps) {
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
            }
        }
    }

    /**
     * Deletes all groups older than a given number of days
     * 
     * @param days the number of days that groups older than should be deleted
     * @see org.kuali.kfs.gl.service.OriginEntryGroupService#deleteOlderGroups(int)
     */
    public void deleteOlderGroups(int days) {
        LOG.debug("deleteOlderGroups() started");

        Calendar today = dateTimeService.getCurrentCalendar();
        today.add(Calendar.DAY_OF_MONTH, 0 - days);

        File[] allFilesList = new File(batchFileDirectoryName).listFiles();

        for (File file : allFilesList) {
            java.sql.Date fileDate = new java.sql.Date(file.lastModified());
            if (fileDate.before(today.getTime())) {
                file.delete();
            }
        }
    }


    /**
     * Deletes all groups older than a given number of days
     * 
     * @param days the number of days that groups older than should be deleted
     * @see org.kuali.kfs.gl.service.OriginEntryGroupService#deleteOlderGroups(int)
     */
    public void deleteOlderLaborGroups(int days) {
        LOG.debug("deleteOlderGroups() started");

        Calendar today = dateTimeService.getCurrentCalendar();
        today.add(Calendar.DAY_OF_MONTH, 0 - days);

        File[] allFilesList = new File(batchLaborFileDirectoryName).listFiles();

        for (File file : allFilesList) {
            java.sql.Date fileDate = new java.sql.Date(file.lastModified());
            if (fileDate.before(today.getTime())) {
                file.delete();
            }
        }
    }

    /**
     * @see org.kuali.kfs.gl.service.OriginEntryGroupService#createLaborGroup(java.lang.String)
     */
    public File createLaborGroup(String fileName) {
        return new File(batchLaborFileDirectoryName + File.separator + fileName);
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
     * @see org.kuali.kfs.gl.service.OriginEntryGroupService#getAllLaborFileInBatchDirectory()
     */
    public File[] getAllLaborFileInBatchDirectory() {
        File[] returnFiles = null;
        if (new File(batchLaborFileDirectoryName) != null) {
            returnFiles = new File(batchLaborFileDirectoryName).listFiles(new DateAndDoneFileFilter());
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
    public File getLaborFileWithFileName(String fileName) {
        return new File(batchLaborFileDirectoryName + File.separator + fileName);
    }

    final class ScrubberErrorFilenameFilter implements FilenameFilter {
        /**
         * @see java.io.FilenameFilter#accept(java.io.File, java.lang.String)
         */
        public boolean accept(File dir, String name) {
            return name.contains(GeneralLedgerConstants.BatchFileSystem.SCRUBBER_ERROR_PREFIX);
        }
    }

    final class DateAndDoneFileFilter implements FilenameFilter {
        /**
         * @see java.io.FilenameFilter#accept(java.io.File, java.lang.String)
         */
        public boolean accept(File dir, String name) {
            return name.contains(GeneralLedgerConstants.BatchFileSystem.DONE_FILE_EXTENSION) || name.contains(GeneralLedgerConstants.BatchFileSystem.EXTENSION);
        }
    }

    final class DoneFileFilter implements FilenameFilter {
        /**
         * @see java.io.FilenameFilter#accept(java.io.File, java.lang.String)
         */
        public boolean accept(File dir, String name) {
            return name.contains(GeneralLedgerConstants.BatchFileSystem.DONE_FILE_EXTENSION);
        }
    }

    private File getDataFile(File doneFile) {
        String doneFileAbsPath = doneFile.getAbsolutePath();
        if (!doneFileAbsPath.endsWith(GeneralLedgerConstants.BatchFileSystem.DONE_FILE_EXTENSION)) {
            throw new IllegalArgumentException("DOne file name must end with " + GeneralLedgerConstants.BatchFileSystem.DONE_FILE_EXTENSION);
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

    public void setBatchLaborFileDirectoryName(String batchLaborFileDirectoryName) {
        this.batchLaborFileDirectoryName = batchLaborFileDirectoryName;
    }

    public void setKualiModuleService(KualiModuleService kualiModuleService) {
        this.kualiModuleService = kualiModuleService;
    }
    
    public void setDateTimeService(DateTimeService dts) {
        dateTimeService = dts;
    }
}
