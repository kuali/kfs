/*
 * Copyright 2007-2008 The Kuali Foundation
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
package org.kuali.kfs.module.cam.batch;

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
import org.apache.commons.io.filefilter.PrefixFileFilter;
import org.apache.commons.io.filefilter.SuffixFileFilter;
import org.apache.commons.lang.StringUtils;
import org.kuali.kfs.module.cam.CamsConstants;
import org.kuali.kfs.module.cam.CamsKeyConstants;
import org.kuali.kfs.module.cam.batch.service.AssetBarcodeInventoryLoadService;
import org.kuali.kfs.module.cam.document.web.struts.AssetBarCodeInventoryInputFileForm;
import org.kuali.kfs.sys.FileUtil;
import org.kuali.kfs.sys.batch.BatchInputFileSetType;
import org.kuali.kfs.sys.context.SpringContext;
import org.kuali.rice.core.api.datetime.DateTimeService;
import org.kuali.rice.kim.api.identity.Person;

/**
 * Batch input type for the barcode inventory document.
 */
public class AssetBarcodeInventoryInputFileType implements BatchInputFileSetType {
    private static org.apache.log4j.Logger LOG = org.apache.log4j.Logger.getLogger(AssetBarcodeInventoryInputFileType.class);

    private String directoryPath; 
    private static final String FILE_NAME_PREFIX = "barcode_inv";
    private static final String FILE_NAME_PART_DELIMITER = "_";

    /**
     * 
     * @see org.kuali.kfs.sys.batch.BatchInputFileSetType#getDirectoryPath(java.lang.String)
     */
    public String getDirectoryPath(String fileType) {
        return this.directoryPath;
    }

    
    /**
     * 
     * Sets the path were the files will be saved
     * @param directoryPath
     */
    public void setDirectoryPath(String directoryPath) {
        this.directoryPath = directoryPath;
        FileUtil.createDirectory(directoryPath);
    }

    /**
     * 
     * @see org.kuali.kfs.sys.batch.BatchInputFileSetType#getFileTypes()
     */
    public List<String> getFileTypes() {
        List<String> types = new ArrayList<String>();
        types.add(CamsConstants.BarCodeInventory.DATA_FILE_TYPE);
        return types;
    }

    /**
     * Returns the file extension depending on the file type
     * 
     * @param fileType the file type (returned in {@link #getFileTypes()})
     * @return the file extension
     */
    public String getFileExtension() {
        return CamsConstants.BarCodeInventory.DATA_FILE_EXTENSION;
    }

    /**
     * Returns a map with the enterprise feeder file type descriptions
     * 
     * @return a map containing the following key/description pairs: DATA/Data Files, RECON/Reconciliation File
     * 
     * @see org.kuali.kfs.sys.batch.BatchInputFileSetType#getFileTypeDescription()
     */
    public Map<String, String> getFileTypeDescription() {
        Map<String, String> values = new HashMap<String, String>();
        values.put(CamsConstants.BarCodeInventory.DATA_FILE_TYPE, "CSV File");
        return values;
    }

    /**
     * Return the file name based on information from user and file user identifier
     * 
     * @param user Person object representing user who uploaded file
     * @param fileUserIdentifer String representing user who uploaded file
     * @return String enterprise feeder formated file name string using information from user and file user identifier
     * 
     * @see org.kuali.kfs.sys.batch.BatchInputFileSetType#getFileName(java.lang.String, org.kuali.rice.kim.api.identity.Person, java.lang.String)
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
        String[] fileNameParts = StringUtils.split(file.getName(), "_");
        if (fileNameParts.length > 2) {
            return fileNameParts[2];
        }
        return null;
    }

    /**
     * @see org.kuali.kfs.sys.batch.BatchInputFileSetType#getFileSetTypeIdentifer()
     */
    public String getFileSetTypeIdentifer() {
        return CamsConstants.BarCodeInventory.FILE_TYPE_INDENTIFIER;
    }
    
    /**
     * @see org.kuali.kfs.sys.batch.BatchInputType#getTitleKey()
     */
    public String getTitleKey() {
        return CamsKeyConstants.BarcodeInventory.TITLE_BAR_CODE_INVENTORY;
    }

    /**
     * @see org.kuali.kfs.sys.batch.BatchInputFileSetType#isFileRequired(java.lang.String)
     */
    public boolean isFileRequired(String fileType) {
        return true;
//        if (CamsConstants.BarCodeInventory.DATA_FILE_TYPE.equals(fileType)) { 
//            return true;
//        }
//        throw new IllegalArgumentException("Unknown file type found: " + fileType);
    }

    /**
     * @see org.kuali.kfs.sys.batch.BatchInputFileSetType#getDoneFileDirectoryPath()
     */
    public String getDoneFileDirectoryPath() {
        return this.directoryPath;
    }

    /**
     * @see org.kuali.kfs.sys.batch.BatchInputFileSetType#getDoneFileExtension()
     */
    public String getDoneFileExtension() {
        return CamsConstants.BarCodeInventory.DONE_FILE_EXTENSION;
    }

    /**
     * Returns done file name for a specific user and file user identifier
     * 
     * @param user the user who uploaded or will upload the file
     * @param fileUserIdentifier the file identifier
     * @return String done file name
     * 
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
     * 
     * @see org.kuali.kfs.sys.batch.BatchInputFileSetType#extractFileUserIdentifiers(org.kuali.rice.kim.api.identity.Person, java.util.List)
     */
    public Set<String> extractFileUserIdentifiers(Person user, List<File> files) {
        Set<String> extractedFileUserIdentifiers = new TreeSet<String>();

        StringBuilder buf = new StringBuilder();
        buf.append(FILE_NAME_PREFIX).append(FILE_NAME_PART_DELIMITER).append(user.getPrincipalName()).append(FILE_NAME_PART_DELIMITER);
        String prefixString = buf.toString();

        IOFileFilter prefixFilter = new PrefixFileFilter(prefixString);
        IOFileFilter suffixFilter = new SuffixFileFilter(CamsConstants.BarCodeInventory.DATA_FILE_EXTENSION);
        IOFileFilter combinedFilter = new AndFileFilter(prefixFilter, suffixFilter);

        for (File file : files) {
            if (combinedFilter.accept(file)) {
                String fileName = file.getName();
                if (fileName.endsWith(CamsConstants.BarCodeInventory.DATA_FILE_EXTENSION)) {
                    extractedFileUserIdentifiers.add(StringUtils.substringBetween(fileName, prefixString, CamsConstants.BarCodeInventory.DATA_FILE_EXTENSION));
                } else {
                    LOG.error("Unable to determine file user identifier for file name: " + fileName);
                    throw new RuntimeException("Unable to determine file user identifier for file name: " + fileName);
                }
            }
        }

        return extractedFileUserIdentifiers;
    }

    /**
     * 
     * @see org.kuali.kfs.sys.batch.BatchInputFileSetType#process(java.util.Map)
     */
    public void process(Map<String, File> typeToFiles) {        
    }        

    
    public void process(Map<String, File> typeToFiles, AssetBarCodeInventoryInputFileForm form) {        
        SpringContext.getBean(AssetBarcodeInventoryLoadService.class).processFile(typeToFiles.get(CamsConstants.BarCodeInventory.DATA_FILE_TYPE),form);
    }

    /**
     * 
     * @see org.kuali.kfs.sys.batch.BatchInputFileSetType#validate(java.util.Map)
     */
    public boolean validate(Map<String, File> typeToFiles) {
        boolean isValid=true;
        //Validating file format.

        if (!SpringContext.getBean(AssetBarcodeInventoryLoadService.class).isFileFormatValid(typeToFiles.get(CamsConstants.BarCodeInventory.DATA_FILE_TYPE))) {
            isValid=false;
        }
        return isValid;
    }

    public String generageDoneFileWithDataFile(String fileUserIdentifer) {
        return null;
    }

    public String generateDoneFileName(Person user, String fileUserIdentifer) {
        return null;
    }

    public String generateFileName(String fileType, String principalName, String fileUserIdentifer) {
        return null;
    }


    public String getFileExtension(String fileType) {
        return "." + fileType;
    }
}

