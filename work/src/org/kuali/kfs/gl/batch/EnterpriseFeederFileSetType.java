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
import org.kuali.kfs.KFSConstants.SystemGroupParameterNames;
import org.kuali.kfs.batch.BatchInputFileSetType;
import org.kuali.kfs.context.SpringContext;
import org.kuali.module.gl.service.EnterpriseFeederService;

/**
 * This class provides metadata for the batch upload screen to work for files associated with the enterprise feeder.
 */
public class EnterpriseFeederFileSetType implements BatchInputFileSetType {
    private static org.apache.log4j.Logger LOG = org.apache.log4j.Logger.getLogger(EnterpriseFeederFileSetType.class);
    
    private static final String FILE_NAME_PREFIX = "entpBatchFile";
    private static final String FILE_NAME_PART_DELIMITER = "_";
    
    /**
     * @see org.kuali.kfs.batch.BatchInputFileSetType#getDirectoryPath(java.lang.String)
     */
    public String getDirectoryPath(String fileType) {
        // all files in the file set go into the same directory
        return SpringContext.getBean(EnterpriseFeederService.class).getDirectoryName();
    }

    /**
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
     * @see org.kuali.kfs.batch.BatchInputFileSetType#getFileTypeDescription()
     */
    public Map<String, String> getFileTypeDescription() {
        Map<String, String> values = new HashMap<String, String>();
        values.put(KFSConstants.DATA_FILE_TYPE, "Data File");
        values.put(KFSConstants.RECON_FILE_TYPE, "Reconciliation File");
        return values;
    }

    public String getFileName(String fileType, UniversalUser user, String fileUserIdentifer) {
        StringBuilder buf = new StringBuilder();
        fileUserIdentifer = StringUtils.deleteWhitespace(fileUserIdentifer);
        fileUserIdentifer = StringUtils.remove(fileUserIdentifer, FILE_NAME_PART_DELIMITER);
        buf.append(FILE_NAME_PREFIX).append(FILE_NAME_PART_DELIMITER).append(user.getPersonUserIdentifier()).append(FILE_NAME_PART_DELIMITER)
                .append(fileUserIdentifer).append(getFileExtension(fileType));
        return buf.toString();
    }

    public String getFileSetTypeIdentifer() {
        return KFSConstants.ENTERPRISE_FEEDER_FILE_SET_TYPE_INDENTIFIER;
    }

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

    public String getWorkgroupParameterNamespace() {
        return KFSConstants.SystemGroupParameterNames.ENTERPRISE_FEEDER_FILE_SET_TYPE_PARAMETER_NAMESPACE;
    }
    
    public String getWorkgroupParameterComponent() {
        return KFSConstants.SystemGroupParameterNames.ENTERPRISE_FEEDER_FILE_SET_TYPE_PARAMETER_COMPONENT;
    }
    
    public String getWorkgroupParameterName() {
        return KFSConstants.SystemGroupParameterNames.FILE_SET_TYPE_WORKGROUP_PARAMETER_NAME;
    }

    /**
     * @see org.kuali.kfs.batch.BatchInputType#getTitleKey()
     */
    public String getTitleKey() {
        return KFSKeyConstants.MESSAGE_BATCH_UPLOAD_TITLE_ENTERPRISE_FEEDER;
    }

    /**
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
     * @see org.kuali.kfs.batch.BatchInputFileSetType#getDoneFileName(org.kuali.core.bo.user.UniversalUser, java.lang.String)
     */
    public String getDoneFileName(UniversalUser user, String fileUserIdentifer) {
        StringBuilder buf = new StringBuilder();
        fileUserIdentifer = StringUtils.deleteWhitespace(fileUserIdentifer);
        fileUserIdentifer = StringUtils.remove(fileUserIdentifer, FILE_NAME_PART_DELIMITER);
        buf.append(FILE_NAME_PREFIX).append(FILE_NAME_PART_DELIMITER).append(user.getPersonUserIdentifier()).append(FILE_NAME_PART_DELIMITER)
                .append(fileUserIdentifer).append(getDoneFileExtension());
        return buf.toString();
    }
    
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
