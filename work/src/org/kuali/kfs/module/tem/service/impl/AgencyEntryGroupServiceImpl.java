/*
 * Copyright 2011 The Kuali Foundation.
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
package org.kuali.kfs.module.tem.service.impl;

import java.io.File;
import java.util.Arrays;
import java.util.List;

import org.kuali.kfs.module.tem.service.AgencyEntryGroupService;
import org.kuali.rice.core.api.datetime.DateTimeService;
import org.kuali.rice.krad.service.KualiModuleService;

public class AgencyEntryGroupServiceImpl implements AgencyEntryGroupService {
    protected DateTimeService dateTimeService;
    protected String batchFileErrorDirectoryName;
    protected String batchFileDirectoryName;
    protected KualiModuleService kualiModuleService;
    protected String nightlyOutFileName;
    protected String backupFileName;

    @Override
    public void createBackupGroup() {
        // TODO Auto-generated method stub

    }

    @Override
    public File createGroup(String fileName) {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public boolean getGroupExists(String groupId) {
        // TODO Auto-generated method stub
        return false;
    }

    @SuppressWarnings("null")
    @Override
    public String getNewestAgencyMatchingErrorFileName() {
        File newestFile = null;

        File[] files = null;
        // can add filter here: listFiles(filter); -- check out originEntryTestBase from Jeff
        if (new File(batchFileErrorDirectoryName) == null) {
            return null;
        }
        files = new File(batchFileErrorDirectoryName).listFiles();
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

    @Override
    public File[] getAllFileInBatchDirectory() {
        File[] returnFiles = null;
        if (new File(batchFileErrorDirectoryName) != null) {
            returnFiles = new File(batchFileErrorDirectoryName).listFiles();
        }
        return returnFiles;
    }

    @Override
    public File getFileWithFileName(String fileName) {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public void deleteFile(String fileName) {
        // TODO Auto-generated method stub

    }

    /**
     * Gets the dateTimeService attribute.
     * @return Returns the dateTimeService.
     */
    public DateTimeService getDateTimeService() {
        return dateTimeService;
    }

    /**
     * Sets the dateTimeService attribute value.
     * @param dateTimeService The dateTimeService to set.
     */
    public void setDateTimeService(DateTimeService dateTimeService) {
        this.dateTimeService = dateTimeService;
    }

    /**
     * Gets the batchFileDirectoryName attribute.
     * @return Returns the batchFileDirectoryName.
     */
    public String getBatchFileErrorDirectoryName() {
        return batchFileErrorDirectoryName;
    }

    /**
     * Sets the batchFileDirectoryName attribute value.
     * @param batchFileDirectoryName The batchFileDirectoryName to set.
     */
    public void setBatchFileErrorDirectoryName(String batchFileErrorDirectoryName) {
        this.batchFileErrorDirectoryName = batchFileErrorDirectoryName;
    }

    /**
     * Gets the kualiModuleService attribute.
     * @return Returns the kualiModuleService.
     */
    public KualiModuleService getKualiModuleService() {
        return kualiModuleService;
    }

    /**
     * Sets the kualiModuleService attribute value.
     * @param kualiModuleService The kualiModuleService to set.
     */
    public void setKualiModuleService(KualiModuleService kualiModuleService) {
        this.kualiModuleService = kualiModuleService;
    }

    /**
     * Gets the nightlyOutFileName attribute.
     * @return Returns the nightlyOutFileName.
     */
    public String getNightlyOutFileName() {
        return nightlyOutFileName;
    }

    /**
     * Sets the nightlyOutFileName attribute value.
     * @param nightlyOutFileName The nightlyOutFileName to set.
     */
    public void setNightlyOutFileName(String nightlyOutFileName) {
        this.nightlyOutFileName = nightlyOutFileName;
    }

    /**
     * Gets the backupFileName attribute.
     * @return Returns the backupFileName.
     */
    public String getBackupFileName() {
        return backupFileName;
    }

    /**
     * Sets the backupFileName attribute value.
     * @param backupFileName The backupFileName to set.
     */
    public void setBackupFileName(String backupFileName) {
        this.backupFileName = backupFileName;
    }

    /**
     * Gets the batchFileDirectoryName attribute.
     * @return Returns the batchFileDirectoryName.
     */
    public String getBatchFileDirectoryName() {
        return batchFileDirectoryName;
    }

    /**
     * Sets the batchFileDirectoryName attribute value.
     * @param batchFileDirectoryName The batchFileDirectoryName to set.
     */
    public void setBatchFileDirectoryName(String batchFileDirectoryName) {
        this.batchFileDirectoryName = batchFileDirectoryName;
    }



}
