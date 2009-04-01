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
 * The default implementation of OriginEntryGroupService
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
     * Finds the group by the given group id, and then sets it to not process
     * @param groupId the id of the group to set
     * @see org.kuali.kfs.gl.service.OriginEntryGroupService#dontProcessGroup(java.lang.Integer)
     */
//    public void dontProcessGroup(Integer groupId) {
//        LOG.debug("dontProcessGroup() started");
//
//        OriginEntryGroup oeg = getExactMatchingEntryGroup(groupId);
//        if (oeg != null) {
//            oeg.setProcess(false);
//            save(oeg);
//        }
//    }


    /**
     * Sets all scrubbable backup groups's scrub attributes to false, so none will be scrubbed
     * @see org.kuali.kfs.gl.service.OriginEntryGroupService#markBackupGroupsUnscrubbable()
     */
//    public void markScrubbableBackupGroupsAsUnscrubbable() {
//        LOG.debug("markScrubbableBackupGroupsAsUnscrubbable() started");
//        for (OriginEntryGroup scrubbableBackupGroup : getAllScrubbableBackupGroups()) {
//            if (LOG.isInfoEnabled()) {
//                LOG.info("marking backup origin entry group as don't process: " + scrubbableBackupGroup.getId());
//            }
//            scrubbableBackupGroup.setProcess(Boolean.FALSE);
//            save(scrubbableBackupGroup);
//        }
//    }

    /**
     * Sets all groups created by the scrubber and ready to be posted's process attribute to false, so they won't be posted
     * @see org.kuali.kfs.gl.service.OriginEntryGroupService#markPostableScrubberValidGroupsAsUnpostable()
     */
//    public void markPostableScrubberValidGroupsAsUnpostable() {
//        LOG.debug("markPostableScrubberValidGroupsAsUnpostable() started");
//        Collection<OriginEntryGroup> postableGroups = getGroupsToPost();
//        for (OriginEntryGroup postableGroup : postableGroups) {
//            if (LOG.isInfoEnabled()) {
//                LOG.info("marking postable SCV origin entry group as don't process: " + postableGroup.getId());
//            }
//            postableGroup.setProcess(Boolean.FALSE);
//            save(postableGroup);
//        }
//    }

    /**
     * Marks any postable ICR group's process attribute as false, so they won't be posted
     * @see org.kuali.kfs.gl.service.OriginEntryGroupService#markPostableIcrGroupsAsUnpostable()
     */
//    public void markPostableIcrGroupsAsUnpostable() {
//        LOG.debug("markPostableIcrGroupsAsUnpostable() started");
//        Collection<OriginEntryGroup> postableGroups = getIcrGroupsToPost();
//        for (OriginEntryGroup postableGroup : postableGroups) {
//            if (LOG.isInfoEnabled()) {
//                LOG.info("marking postable ICR origin entry group as don't process: " + postableGroup.getId());
//            }
//            postableGroup.setProcess(Boolean.FALSE);
//            save(postableGroup);
//        }
//    }

    public String getNewestScrubberErrorFileName(){
        File newestFile = null;
        
        File[] files = null;
        //can add filter here: listFiles(filter); -- check out originEntryTestBase from Jeff
        if(new File(batchFileDirectoryName) == null){
            return null;
        }
        files = new File(batchFileDirectoryName).listFiles(new ScrubberErrorFilenameFilter());
        List<File> fileList =  Arrays.asList(files);
        if (fileList.size() > 0) {
            for (File eachFile: fileList){
                if (newestFile == null) {
                    newestFile = eachFile;
                }
                else {
                    if (newestFile.lastModified() < eachFile.lastModified()) {
                        newestFile = eachFile;
                    }
                }
            }    
        } else {
            return null;
        }
        
        return newestFile.getName();
    }

    public String getNewestScrubberErrorLaborFileName(){
        File newestFile = null;
        
        File[] files = null;
        //can add filter here: listFiles(filter); -- check out originEntryTestBase from Jeff
        if(new File(batchLaborFileDirectoryName) == null){
            return null;
        }
        files = new File(batchLaborFileDirectoryName).listFiles(new ScrubberErrorFilenameFilter());
        List<File> fileList =  Arrays.asList(files);
        if (fileList.size() > 0) {
            for (File eachFile: fileList){
                if (newestFile == null) {
                    newestFile = eachFile;
                }
                else {
                    if (newestFile.lastModified() < eachFile.lastModified()) {
                        newestFile = eachFile;
                    }
                }
            }    
        } else {
            return null;
        }
        
        return newestFile.getName();
    }
    
    /**
     * Returns all groups created by a given origin entry Source
     * @param sourceCode the source of the origin entry group
     * @return a OriginEntryGroup with the given source code and max ORIGIN_ENTRY_GRP_ID
     * @see org.kuali.kfs.gl.businessobject.OriginEntrySource
     * @see org.kuali.kfs.gl.service.OriginEntryGroupService#getGroupsFromSource(java.lang.String)
     */
    //TODO: Shawn - This method used for report. I will delete this method after all reports are done.
    public OriginEntryGroup getGroupWithMaxIdFromSource(String sourceCode) {
        LOG.debug("getGroupWithMaxIdFromSource() started");

        //return originEntryGroupDao.getGroupWithMaxIdFromSource(sourceCode);
        return new OriginEntryGroup();
    }

    /**
     * Retrieves all groups to be created today, and creates backup group versions of them
     * @see org.kuali.kfs.gl.service.OriginEntryGroupService#createBackupGroup()
     */
    public void createBackupGroup() {
        LOG.debug("createBackupGroup() started");
        //check file from nightly out 
        String nightlyOutFileName = GeneralLedgerConstants.BatchFileSystem.NIGHTLY_OUT_FILE + GeneralLedgerConstants.BatchFileSystem.EXTENSION;
        File nightlyOutFile = new File(batchFileDirectoryName + File.separator + nightlyOutFileName);
        if (!nightlyOutFile.exists()){
            LOG.warn("nightlyOutFile doesn't exist :" + nightlyOutFileName);
        }
        
        String backupFileName = batchFileDirectoryName + File.separator + GeneralLedgerConstants.BatchFileSystem.BACKUP_FILE + GeneralLedgerConstants.BatchFileSystem.EXTENSION;    
        PrintStream backupPs = null;
        try {
            backupPs = new PrintStream(backupFileName);
        } catch (FileNotFoundException e) {
            throw new RuntimeException("backupFile doesn't exist " + backupFileName);
        }
        
        //get all done files from originEntry Directory 
        File[] doneFileList = new File(batchFileDirectoryName).listFiles(new DoneFileFilter());
        //build output file with doneFileList and print stream
        buildBackupFileOutput(doneFileList, backupPs);
        backupPs.close();
    }
    
    public void createLaborBackupGroup() {
        LOG.debug("createLaborBackupGroup() started");

        // Get the groups that need to be added
        Date today = dateTimeService.getCurrentSqlDate();

        //check file from nightly out 
        String laborNightlyOutFileName = LaborConstants.BatchFileSystem.NIGHTLY_OUT_FILE + GeneralLedgerConstants.BatchFileSystem.EXTENSION;
        File laborNightlyOutFile = new File(batchLaborFileDirectoryName + File.separator + laborNightlyOutFileName);
        if (!laborNightlyOutFile.exists()){
            LOG.warn("laborNightlyOutFile doesn't exist :" + laborNightlyOutFileName);
        }
        
        String laborBackupFileName = LaborConstants.BatchFileSystem.BACKUP_FILE + GeneralLedgerConstants.BatchFileSystem.EXTENSION;    
        File laborBackupFile = new File(batchLaborFileDirectoryName + File.separator + laborBackupFileName);
         
        PrintStream laborBackupPs = null;
        try {
            laborBackupPs = new PrintStream(laborBackupFile);
        } catch (FileNotFoundException e) {
            throw new RuntimeException("laborBackupFile doesn't exist " + laborBackupFile);
        }
        
        //get all done files from originEntry Directory 
        File[] doneFileList = new File(batchLaborFileDirectoryName).listFiles(new DoneFileFilter());
        //build output file with doneFileList and print stream
        buildBackupFileOutput(doneFileList, laborBackupPs);
        laborBackupPs.close();
    }

    /*
     * buildBackupFileOuput with doneFileList and PrintStream
     */
    private void buildBackupFileOutput(File[] doneFileList, PrintStream ps){
        BufferedReader inputFileReader = null;
        
        for (File doneFile : doneFileList) {
            //get data file with done file
            File dataFile = getDataFile(doneFile);
            if (dataFile != null){
                try{
                    inputFileReader = new BufferedReader(new FileReader(dataFile.getPath()));
                    String line = null;
                    while ((line = inputFileReader.readLine()) != null) {
                        try {
                            ps.printf("%s\n", line);
                        } catch (Exception e) {
                            throw new IOException(e.toString());
                        }
                    }    
                    inputFileReader.close();
                    inputFileReader = null;
                    
                } catch (Exception e) {
                    throw new RuntimeException(e.toString());
                }
                
                doneFile.delete();
            }
        }
    }

    /**
     * Deletes all groups older than a given number of days
     * @param days the number of days that groups older than should be deleted
     * @see org.kuali.kfs.gl.service.OriginEntryGroupService#deleteOlderGroups(int)
     */
    public void deleteOlderGroups(int days) {
        LOG.debug("deleteOlderGroups() started");
        
        Calendar today = dateTimeService.getCurrentCalendar();
        today.add(Calendar.DAY_OF_MONTH, 0 - days);

        File[] allFilesList = new File(batchFileDirectoryName).listFiles();
        
        for (File file: allFilesList) {
            java.sql.Date fileDate = new java.sql.Date(file.lastModified());
            if (fileDate.before(today.getTime())){
                file.delete();
            }
        }
    }

    
    /**
     * Deletes all groups older than a given number of days
     * @param days the number of days that groups older than should be deleted
     * @see org.kuali.kfs.gl.service.OriginEntryGroupService#deleteOlderGroups(int)
     */
    public void deleteOlderLaborGroups(int days) {
        LOG.debug("deleteOlderGroups() started");
        
        Calendar today = dateTimeService.getCurrentCalendar();
        today.add(Calendar.DAY_OF_MONTH, 0 - days);

        File[] allFilesList = new File(batchLaborFileDirectoryName).listFiles();
        
        for (File file: allFilesList) {
            java.sql.Date fileDate = new java.sql.Date(file.lastModified());
            if (fileDate.before(today.getTime())){
                file.delete();
            }
        }
    }
    
    /**
     * Create a new OriginEntryGroup and persists it to the database.
     * @param date the date this group should list as its creation date
     * @param sourceCode the source of this origin entry group
     * @param valid whether this group is valid - ie, all entries within it are valid
     * @param process whether this group should be processed by the next step
     * @param scrub whether this group should be input to the scrubber
     * @return a new origin entry group to put origin entries into
     */
//    public OriginEntryGroup createGroup(Date date, String sourceCode, boolean valid, boolean process, boolean scrub) {
//        LOG.debug("createGroup() started");
//
//        OriginEntryGroup oeg = new OriginEntryGroup();
//        oeg.setDate(date);
//        oeg.setProcess(Boolean.valueOf(process));
//        oeg.setScrub(Boolean.valueOf(scrub));
//        oeg.setSourceCode(sourceCode);
//        oeg.setValid(Boolean.valueOf(valid));
//
//        originEntryGroupDao.save(oeg);
//
//        return oeg;
//    }
    
    public File createGroup(String fileName){
        return new File(batchFileDirectoryName + File.separator + fileName);
    }
    
    public File createLaborGroup(String fileName){
        return new File(batchLaborFileDirectoryName + File.separator + fileName);
    }

    /**
     * Get all non-ICR-related OriginEntryGroups waiting to be posted as of postDate.
     * @return a Collection of origin entry groups to post
     */
//    public Collection getGroupsToPost() {
//        LOG.debug("getGroupsToPost() started");
//
//        return originEntryGroupDao.getPosterGroups(OriginEntrySource.SCRUBBER_VALID);
//    }

    /**
     * Get all ICR-related OriginEntryGroups waiting to be posted as of postDate.
     * @return a Collection of origin entry groups with indirect cost recovery origin entries to post
     */
//    public Collection getIcrGroupsToPost() {
//        LOG.debug("getIcrGroupsToPost() started");
//
//        return originEntryGroupDao.getPosterGroups(OriginEntrySource.ICR_TRANSACTIONS);
//    }

    /**
     * An alias for OriginEntryGroupDao.getScrubberGroups().
     * 
     * @param scrubDate the date to find backup groups for
     * @return a Collection of groups to scrub
     */
//    public Collection getGroupsToBackup(Date scrubDate) {
//        LOG.debug("getGroupsToScrub() started");
//
//        return originEntryGroupDao.getGroupsToBackup(scrubDate);
//    }

    /**
     * Returns all groups to post
     * @param entryGroupSourceCode the source code of origin entry groups to post
     * @return a Collection of groups to post
     * @see org.kuali.kfs.gl.service.OriginEntryGroupService#getGroups(java.lang.String)
     */
//    public Collection getGroupsToPost(String entryGroupSourceCode) {
//        return originEntryGroupDao.getPosterGroups(entryGroupSourceCode);
//    }

    /**
     * Retrieves all groups that match the given search criteria
     * @param criteria a Map of criteria to build a query from
     * @return a Collection of all qualifying origin entry groups
     * @see org.kuali.kfs.gl.service.OriginEntryGroupService#getMatchingGroups(java.util.Map)
     */
//    public Collection getMatchingGroups(Map criteria) {
//        LOG.debug("getMatchingGroups() started");
//
//        return originEntryGroupDao.getMatchingGroups(criteria);
//    }

    /**
     * Persist an OriginEntryGroup to the database.
     * 
     * @param originEntryGroup
     */
//    public void save(OriginEntryGroup originEntryGroup) {
//        LOG.debug("save() started");
//
//        originEntryGroupDao.save(originEntryGroup);
//    }

    /**
     * Returns the origin entry group with the given id
     * @param id the id of the origin entry group to retreive
     * @return the origin entry group with the given id if found, otherwise null
     * @see org.kuali.kfs.gl.service.OriginEntryGroupService#getExactMatchingEntryGroup(java.lang.Integer)
     */
//    public OriginEntryGroup getExactMatchingEntryGroup(Integer id) {
//        return originEntryGroupDao.getExactMatchingEntryGroup(id);
//    }

    /**
     * Returns groups created within the past number of given days
     * @param days the number of days returned groups must be younger than
     * @return a Collection of qualifying groups
     * @see org.kuali.kfs.gl.service.OriginEntryGroupService#getRecentGroupsByDays(int)
     */
//    public Collection getRecentGroupsByDays(int days) {
//
//        Calendar today = dateTimeService.getCurrentCalendar();
//        today.add(Calendar.DAY_OF_MONTH, 0 - days);
//
//        Collection groups = originEntryGroupDao.getRecentGroups(new java.sql.Date(today.getTime().getTime()));
//
//        return groups;
//    }


    /**
     * Returns whether or not a group with the given id exists in the database
     * @param groupId the id of the group to check for existence
     * @return true if such a group exists, false otherwise
     * @see org.kuali.kfs.gl.service.OriginEntryGroupService#getGroupExists(java.lang.Integer)
     */
//    public boolean getGroupExists(Integer groupId) {
//        Map<String, Integer> criteria = new HashMap<String, Integer>();
//        criteria.put("id", groupId);
//        Collection groups = getMatchingGroups(criteria);
//        return groups.size() > 0;
//    }
    
    public boolean getGroupExists(String groupId) {
        
        File file = new File(batchFileDirectoryName + File.separator + groupId);
        if (file == null) {
            return false;
        } else {
            return true;
        }
    }

    public void setDateTimeService(DateTimeService dts) {
        dateTimeService = dts;
    }
//    
//    public List getAllFileInBatchDirectoryWithList(){
//        List returnList = new ArrayList();
//        File[] allFiles = getAllFileInBatchDirectory();
//        returnList.toArray(allFiles);
//        return returnList;
//    }

    public File[] getAllFileInBatchDirectory(){
        File[] returnFiles = null;
        //can add filter here: listFiles(filter); -- check out originEntryTestBase from Jeff
        if(new File(batchFileDirectoryName) != null){
            returnFiles = new File(batchFileDirectoryName).listFiles();    
        }
        return returnFiles;
    }
    
    public File[] getAllLaborFileInBatchDirectory(){
        File[] returnFiles = null;
        //can add filter here: listFiles(filter); -- check out originEntryTestBase from Jeff
        if(new File(batchLaborFileDirectoryName) != null){
            returnFiles = new File(batchLaborFileDirectoryName).listFiles();    
        }
        return returnFiles;
    }
    
    public void deleteFile(String fileName){
        File file = getFileWithFileName(fileName);
        if (file.exists()){
            file.delete();
        }
    }
    
    public void deleteLaborFile(String fileName){
        File file = getLaborFileWithFileName(fileName);
        if (file.exists()){
            file.delete();
        }
    }
    
    public File getFileWithFileName(String fileName){
        return new File(batchFileDirectoryName + File.separator + fileName);
    }
    
    public File getLaborFileWithFileName(String fileName){
        return new File(batchLaborFileDirectoryName + File.separator + fileName);
    }

    public void setBatchFileDirectoryName(String batchFileDirectoryName) {
        this.batchFileDirectoryName = batchFileDirectoryName;
    }
    
    final class ScrubberErrorFilenameFilter implements FilenameFilter {
        /**
         * @see java.io.FilenameFilter#accept(java.io.File, java.lang.String)
         */
        public boolean accept(File dir, String name) {
            return name.contains(GeneralLedgerConstants.BatchFileSystem.SCRUBBER_ERROR_PREFIX);
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
    
    
    
    public void setBatchLaborFileDirectoryName(String batchLaborFileDirectoryName) {
        this.batchLaborFileDirectoryName = batchLaborFileDirectoryName;
    }

    public void setKualiModuleService(KualiModuleService kualiModuleService) {
        this.kualiModuleService = kualiModuleService;
    }
}
