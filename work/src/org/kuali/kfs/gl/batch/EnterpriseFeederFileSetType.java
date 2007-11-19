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
package org.kuali.module.gl.batch.enterprisefeeder;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeSet;

import org.apache.commons.io.filefilter.AndFileFilter;
import org.apache.commons.io.filefilter.IOFileFilter;
import org.apache.commons.io.filefilter.OrFileFilter;
import org.apache.commons.io.filefilter.PrefixFileFilter;
import org.apache.commons.io.filefilter.SuffixFileFilter;
import org.apache.commons.lang.StringUtils;
import org.kuali.core.bo.user.UniversalUser;
import org.kuali.kfs.KFSConstants;
import org.kuali.kfs.KFSKeyConstants;
import org.kuali.kfs.batch.BatchInputFileSetType;
import org.kuali.kfs.context.SpringContext;
import org.kuali.module.gl.batch.EnterpriseFeedStep;
import org.kuali.module.gl.service.EnterpriseFeederService;

/**
 * This class provides metadata for the batch upload screen to work for files associated with the enterprise feeder.
 */
public class EnterpriseFeederFileSetType implements BatchInputFileSetType {
    private static org.apache.log4j.Logger LOG = org.apache.log4j.Logger.getLogger(EnterpriseFeederFileSetType.class);

    private static final String FILE_NAME_PREFIX = "entpBatchFile";
    private static final String FILE_NAME_PART_DELIMITER = "_";

    /**
     * Returns directory path for EnterpriseFeederService
     * 
     * @param fileType file type (not used)
     * 
     * @see org.kuali.kfs.batch.BatchInputFileSetType#getDirectoryPath(java.lang.String)
     */
    public String getDirectoryPath(String fileType) {
        // all files in the file set go into the same directory
        return SpringContext.getBean(EnterpriseFeederService.class).getDirectoryName();
    }

    /**
     * Get all relevant file types for Enterprise Feeder File Set
     * 
     * @return List<String> including "DATA", "RECON" file types
     * 
     * @see org.kuali.kfs.batch.BatchInputFileSetType#getFileTypes()
     */
    public List<String> getFileTypes() {
        List<String> types = new ArrayList<String>();
        types.add(KFSConstants.DATA_FILE_TYPE);
        types.add(KFSConstants.RECON_FILE_TYPE);
        return types;
    }

    /**
     * Returns the file extension depending on the file type
     * 
     * @param fileType the file type (returned in {@link #getFileTypes()})
     * @return the file extension
     */
    protected String getFileExtension(String fileType) {
        if (KFSConstants.DATA_FILE_TYPE.equals(fileType)) {
            return EnterpriseFeederService.DATA_FILE_SUFFIX;
        }
        if (KFSConstants.RECON_FILE_TYPE.equals(fileType)) {
            return EnterpriseFeederService.RECON_FILE_SUFFIX;
        }
        throw new IllegalArgumentException("Unknown file type found: " + fileType);
    }

    /**
     * Returns a map with the enterprise feeder file type descriptions
     * 
     * @return a map containing the following key/description pairs: DATA/Data Files, RECON/Reconciliation File
     * 
     * @see org.kuali.kfs.batch.BatchInputFileSetType#getFileTypeDescription()
     */
    public Map<String, String> getFileTypeDescription() {
        Map<String, String> values = new HashMap<String, String>();
        values.put(KFSConstants.DATA_FILE_TYPE, "Data File");
        values.put(KFSConstants.RECON_FILE_TYPE, "Reconciliation File");
        return values;
    }

    /**
     * Return the file name based on information from user and file user identifier
     * 
     * @param user UniversalUser object representing user who uploaded file
     * @param fileUserIdentifer String representing user who uploaded file
     * @return String enterprise feeder formated file name string using information from user and file user identifier
     * 
     * @see org.kuali.kfs.batch.BatchInputFileSetType#getFileName(java.lang.String, org.kuali.core.bo.user.UniversalUser, java.lang.String)
     */
    public String getFileName(String fileType, UniversalUser user, String fileUserIdentifer) {
        StringBuilder buf = new StringBuilder();
        fileUserIdentifer = StringUtils.deleteWhitespace(fileUserIdentifer);
        fileUserIdentifer = StringUtils.remove(fileUserIdentifer, FILE_NAME_PART_DELIMITER);
        buf.append(FILE_NAME_PREFIX).append(FILE_NAME_PART_DELIMITER).append(user.getPersonUserIdentifier()).append(FILE_NAME_PART_DELIMITER).append(fileUserIdentifer).append(getFileExtension(fileType));
        return buf.toString();
    }

    /**
     * @see org.kuali.kfs.batch.BatchInputFileSetType#getFileSetTypeIdentifer()
     */
    public String getFileSetTypeIdentifer() {
        return KFSConstants.ENTERPRISE_FEEDER_FILE_SET_TYPE_INDENTIFIER;
    }

    /**
     * Return true if user is authorized to access batch file
     * 
     * @param user authorized user
     * @param batchFile file being checked for authorization
     * @return true if user is authorized to view file
     * 
     * @see org.kuali.kfs.batch.BatchInputType#checkAuthorization(org.kuali.core.bo.user.UniversalUser, java.io.File)
     */
    public boolean checkAuthorization(UniversalUser user, File batchFile) {
        boolean isAuthorized = false;

        String userIdentifier = user.getPersonUserIdentifier();
        userIdentifier = StringUtils.remove(userIdentifier, " ");

        if (!batchFile.getName().startsWith(FILE_NAME_PREFIX)) {
            return false;
        }

        String[] fileNameParts = StringUtils.split(batchFile.getName(), FILE_NAME_PART_DELIMITER);
        if (fileNameParts.length > 2) {
            if (fileNameParts[1].equalsIgnoreCase(userIdentifier.toLowerCase())) {
                isAuthorized = true;
            }
        }

        return isAuthorized;
    }

    /**
     * @see org.kuali.kfs.batch.BatchInputType#getUploadWorkgroupParameterComponent()
     */
    public Class getUploadWorkgroupParameterComponent() {
        return EnterpriseFeedStep.class;
    }

    /**
     * @see org.kuali.kfs.batch.BatchInputType#getTitleKey()
     */
    public String getTitleKey() {
        return KFSKeyConstants.MESSAGE_BATCH_UPLOAD_TITLE_ENTERPRISE_FEEDER;
    }

    /**
     * Return true if file is required
     * 
     * @param fileType type of file
     * @return true if file type is required
     * 
     * @see org.kuali.kfs.batch.BatchInputFileSetType#isFileRequired(java.lang.String)
     */
    public boolean isFileRequired(String fileType) {
        if (KFSConstants.DATA_FILE_TYPE.equals(fileType) || KFSConstants.RECON_FILE_TYPE.equals(fileType)) {
            return true;
        }
        throw new IllegalArgumentException("Unknown file type found: " + fileType);
    }

    /**
     * @see org.kuali.kfs.batch.BatchInputFileSetType#isSupportsDoneFileCreation()
     */
    public boolean isSupportsDoneFileCreation() {
        return true;
    }

    /**
     * @see org.kuali.kfs.batch.BatchInputFileSetType#getDoneFileDirectoryPath()
     */
    public String getDoneFileDirectoryPath() {
        return SpringContext.getBean(EnterpriseFeederService.class).getDirectoryName();
    }

    /**
     * @see org.kuali.kfs.batch.BatchInputFileSetType#getDoneFileExtension()
     */
    protected String getDoneFileExtension() {
        return EnterpriseFeederService.DONE_FILE_SUFFIX;
    }

    /**
     * Returns done file name for a specific user and file user identifier
     * 
     * @param user the user who uploaded or will upload the file
     * @param fileUserIdentifier the file identifier
     * @return String done file name
     * 
     * @see org.kuali.kfs.batch.BatchInputFileSetType#getDoneFileName(org.kuali.core.bo.user.UniversalUser, java.lang.String)
     */
    public String getDoneFileName(UniversalUser user, String fileUserIdentifer) {
        StringBuilder buf = new StringBuilder();
        fileUserIdentifer = StringUtils.deleteWhitespace(fileUserIdentifer);
        fileUserIdentifer = StringUtils.remove(fileUserIdentifer, FILE_NAME_PART_DELIMITER);
        buf.append(FILE_NAME_PREFIX).append(FILE_NAME_PART_DELIMITER).append(user.getPersonUserIdentifier()).append(FILE_NAME_PART_DELIMITER).append(fileUserIdentifer).append(getDoneFileExtension());
        return buf.toString();
    }

    /**
     * Return set of file user identifiers from a list of files
     * 
     * @param user user who uploaded or will upload file
     * @param files list of files objects
     * @return Set containing all user identifiers from list of files
     * 
     * @see org.kuali.kfs.batch.BatchInputFileSetType#extractFileUserIdentifiers(org.kuali.core.bo.user.UniversalUser, java.util.List)
     */
    public Set<String> extractFileUserIdentifiers(UniversalUser user, List<File> files) {
        Set<String> extractedFileUserIdentifiers = new TreeSet<String>();

        StringBuilder buf = new StringBuilder();
        buf.append(FILE_NAME_PREFIX).append(FILE_NAME_PART_DELIMITER).append(user.getPersonUserIdentifier()).append(FILE_NAME_PART_DELIMITER);
        String prefixString = buf.toString();
        IOFileFilter prefixFilter = new PrefixFileFilter(prefixString);

        IOFileFilter suffixFilter = new OrFileFilter(new SuffixFileFilter(EnterpriseFeederService.DATA_FILE_SUFFIX), new SuffixFileFilter(EnterpriseFeederService.RECON_FILE_SUFFIX));

        IOFileFilter combinedFilter = new AndFileFilter(prefixFilter, suffixFilter);

        for (File file : files) {
            if (combinedFilter.accept(file)) {
                String fileName = file.getName();
                if (fileName.endsWith(EnterpriseFeederService.DATA_FILE_SUFFIX)) {
                    extractedFileUserIdentifiers.add(StringUtils.substringBetween(fileName, prefixString, EnterpriseFeederService.DATA_FILE_SUFFIX));
                }
                else if (fileName.endsWith(EnterpriseFeederService.RECON_FILE_SUFFIX)) {
                    extractedFileUserIdentifiers.add(StringUtils.substringBetween(fileName, prefixString, EnterpriseFeederService.RECON_FILE_SUFFIX));
                }
                else {
                    LOG.error("Unable to determine file user identifier for file name: " + fileName);
                    throw new RuntimeException("Unable to determine file user identifier for file name: " + fileName);
                }
            }
        }

        return extractedFileUserIdentifiers;
    }
}
