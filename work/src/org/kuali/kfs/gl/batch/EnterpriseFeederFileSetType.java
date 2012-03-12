/*
 * Copyright 2007 The Kuali Foundation
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
package org.kuali.kfs.gl.batch;

import java.io.File;
import java.util.ArrayList;
import java.util.Date;
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
import org.kuali.kfs.gl.batch.service.EnterpriseFeederService;
import org.kuali.kfs.sys.FileUtil;
import org.kuali.kfs.sys.KFSConstants;
import org.kuali.kfs.sys.KFSKeyConstants;
import org.kuali.kfs.sys.batch.BatchInputFileSetType;
import org.kuali.kfs.sys.context.SpringContext;
import org.kuali.rice.core.api.datetime.DateTimeService;
import org.kuali.rice.kim.api.identity.Person;

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
     * @see org.kuali.kfs.sys.batch.BatchInputFileSetType#getDirectoryPath(java.lang.String)
     */
    public String getDirectoryPath(String fileType) {
        // all files in the file set go into the same directory
        String directoryPath = SpringContext.getBean(EnterpriseFeederService.class).getDirectoryName();
        FileUtil.createDirectory(directoryPath);
        return directoryPath;
    }

    /**
     * Get all relevant file types for Enterprise Feeder File Set
     * 
     * @return List<String> including "DATA", "RECON" file types
     * @see org.kuali.kfs.sys.batch.BatchInputFileSetType#getFileTypes()
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
     * @see org.kuali.kfs.sys.batch.BatchInputFileSetType#getFileTypeDescription()
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
     * @param user Person object representing user who uploaded file
     * @param fileUserIdentifer String representing user who uploaded file
     * @return String enterprise feeder formated file name string using information from user and file user identifier
     * @see org.kuali.kfs.sys.batch.BatchInputFileSetType#getFileName(java.lang.String, org.kuali.rice.kim.api.identity.Person,
     *      java.lang.String)
     */
    public String getFileName(String fileType, String principalName, String fileUserIdentifer, Date creationDate) {
        DateTimeService dateTimeService = SpringContext.getBean(DateTimeService.class);
        StringBuilder buf = new StringBuilder();
        fileUserIdentifer = StringUtils.deleteWhitespace(fileUserIdentifer);
        fileUserIdentifer = StringUtils.remove(fileUserIdentifer, FILE_NAME_PART_DELIMITER);
        buf.append(FILE_NAME_PREFIX).append(FILE_NAME_PART_DELIMITER).append(principalName)
                .append(FILE_NAME_PART_DELIMITER).append(fileUserIdentifer)
                .append(FILE_NAME_PART_DELIMITER).append(dateTimeService.toDateTimeStringForFilename(creationDate))
                .append(getFileExtension(fileType));
        return buf.toString();
    }

    public String getAuthorPrincipalName(File file) {
        String[] fileNameParts = StringUtils.split(file.getName(), FILE_NAME_PART_DELIMITER);
        if (fileNameParts.length > 2) {
            return fileNameParts[1];
        }
        return null;
    }

    /**
     * @see org.kuali.kfs.sys.batch.BatchInputFileSetType#getFileSetTypeIdentifer()
     */
    public String getFileSetTypeIdentifer() {
        return KFSConstants.ENTERPRISE_FEEDER_FILE_SET_TYPE_INDENTIFIER;
    }

    /**
     * @see org.kuali.kfs.sys.batch.BatchInputType#getTitleKey()
     */
    public String getTitleKey() {
        return KFSKeyConstants.MESSAGE_BATCH_UPLOAD_TITLE_ENTERPRISE_FEEDER;
    }

    /**
     * Return true if file is required
     * 
     * @param fileType type of file
     * @return true if file type is required
     * @see org.kuali.kfs.sys.batch.BatchInputFileSetType#isFileRequired(java.lang.String)
     */
    public boolean isFileRequired(String fileType) {
        if (KFSConstants.DATA_FILE_TYPE.equals(fileType) || KFSConstants.RECON_FILE_TYPE.equals(fileType)) {
            return true;
        }
        throw new IllegalArgumentException("Unknown file type found: " + fileType);
    }

    /**
     * @see org.kuali.kfs.sys.batch.BatchInputFileSetType#getDoneFileDirectoryPath()
     */
    public String getDoneFileDirectoryPath() {
        return SpringContext.getBean(EnterpriseFeederService.class).getDirectoryName();
    }

    /**
     * @see org.kuali.kfs.sys.batch.BatchInputFileSetType#getDoneFileExtension()
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
     * @see org.kuali.kfs.sys.batch.BatchInputFileSetType#getDoneFileName(org.kuali.rice.kim.api.identity.Person, java.lang.String)
     */
    public String getDoneFileName(Person user, String fileUserIdentifer, Date creationDate) {
        DateTimeService dateTimeService = SpringContext.getBean(DateTimeService.class);
        StringBuilder buf = new StringBuilder();
        fileUserIdentifer = StringUtils.deleteWhitespace(fileUserIdentifer);
        fileUserIdentifer = StringUtils.remove(fileUserIdentifer, FILE_NAME_PART_DELIMITER);
        buf.append(FILE_NAME_PREFIX).append(FILE_NAME_PART_DELIMITER).append(user.getPrincipalName())
                .append(FILE_NAME_PART_DELIMITER).append(fileUserIdentifer)
                .append(FILE_NAME_PART_DELIMITER).append(dateTimeService.toDateTimeStringForFilename(creationDate))
                .append(getDoneFileExtension());
        return buf.toString();
    }

    /**
     * Return set of file user identifiers from a list of files
     * 
     * @param user user who uploaded or will upload file
     * @param files list of files objects
     * @return Set containing all user identifiers from list of files
     * @see org.kuali.kfs.sys.batch.BatchInputFileSetType#extractFileUserIdentifiers(org.kuali.rice.kim.api.identity.Person, java.util.List)
     */
    public Set<String> extractFileUserIdentifiers(Person user, List<File> files) {
        Set<String> extractedFileUserIdentifiers = new TreeSet<String>();

        StringBuilder buf = new StringBuilder();
        buf.append(FILE_NAME_PREFIX).append(FILE_NAME_PART_DELIMITER).append(user.getPrincipalName()).append(FILE_NAME_PART_DELIMITER);
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

    public void process(Map<String, File> typeToFiles) {
    }

    public boolean validate(Map<String, File> typeToFiles) {
        return true;
    }
}
