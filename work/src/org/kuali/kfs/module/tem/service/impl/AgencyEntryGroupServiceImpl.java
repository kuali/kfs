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
