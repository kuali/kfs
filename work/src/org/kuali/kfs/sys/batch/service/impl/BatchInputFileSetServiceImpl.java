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
package org.kuali.kfs.sys.batch.service.impl;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.kuali.kfs.sys.KFSConstants;
import org.kuali.kfs.sys.KFSConstants.SystemGroupParameterNames;
import org.kuali.kfs.sys.batch.BatchInputFileSetType;
import org.kuali.kfs.sys.batch.InitiateDirectoryBase;
import org.kuali.kfs.sys.batch.service.BatchInputFileSetService;
import org.kuali.kfs.sys.context.SpringContext;
import org.kuali.kfs.sys.exception.FileStorageException;
import org.kuali.kfs.sys.service.impl.KfsParameterConstants;
import org.kuali.rice.core.api.config.property.ConfigurationService;
import org.kuali.rice.core.api.datetime.DateTimeService;
import org.kuali.rice.coreservice.framework.parameter.ParameterService;
import org.kuali.rice.kim.api.identity.Person;
import org.kuali.rice.krad.exception.AuthorizationException;
import org.kuali.rice.krad.exception.ValidationException;
import org.kuali.rice.krad.util.GlobalVariables;

/**
 * Base implementation to manipulate batch input file sets from the batch upload screen
 */
public class BatchInputFileSetServiceImpl extends InitiateDirectoryBase implements BatchInputFileSetService {
    private static final org.apache.log4j.Logger LOG = org.apache.log4j.Logger.getLogger(BatchInputFileSetServiceImpl.class);

    protected ConfigurationService kualiConfigurationService;

    /**
     * Generates the file name of a file (not the done file)
     * 
     * @param user the user who uploaded or will upload the file
     * @param inputType the file set type
     * @param fileUserIdentifier the file identifier
     * @param fileType the file type
     * @return the file name, starting with the directory path
     */
    protected String generateFileName(Person user, BatchInputFileSetType inputType, String fileUserIdentifier, String fileType, Date creationDate) {        
        if (!isFileUserIdentifierProperlyFormatted(fileUserIdentifier)) {
            throw new IllegalArgumentException("The file set identifier is not properly formatted: " + fileUserIdentifier);
        }
        return inputType.getDirectoryPath(fileType) + File.separator + inputType.getFileName(fileType, user.getPrincipalName(), fileUserIdentifier, creationDate);
    }

    /**
     * Generates the file name of a file (not the done file)
     * 
     * @param user the user who uploaded or will upload the file
     * @param inputType the file set type
     * @param fileUserIdentifier the file identifier
     * @param fileType the file type
     * @return the file name, starting with the directory path
     */
    protected String generateTempFileName(Person user, BatchInputFileSetType inputType, String fileUserIdentifier, String fileType, Date creationDate) {
        if (!isFileUserIdentifierProperlyFormatted(fileUserIdentifier)) {
            throw new IllegalArgumentException("The file set identifier is not properly formatted: " + fileUserIdentifier);
        }
        return getTempDirectoryName() + File.separator + inputType.getFileName(fileType, user.getPrincipalName(), fileUserIdentifier, creationDate);
    }
    /**
     * Generates the file name of the done file, if supported by the underlying input type
     * 
     * @param user the user who uploaded or will upload the file
     * @param inputType the file set type
     * @param fileUserIdentifier the file identifier
     * @param fileType the file type
     * @return the file name, starting with the directory path
     */
    protected String generateDoneFileName(Person user, BatchInputFileSetType inputType, String fileUserIdentifier, Date creationDate) {        
        if (!isFileUserIdentifierProperlyFormatted(fileUserIdentifier)) {
            throw new IllegalArgumentException("The file set identifier is not properly formatted: " + fileUserIdentifier);
        }
        return inputType.getDoneFileDirectoryPath() + File.separator + inputType.getDoneFileName(user, fileUserIdentifier, creationDate);
    }

    /**
     * @see org.kuali.kfs.sys.batch.service.BatchInputFileSetService#isBatchInputTypeActive(org.kuali.kfs.sys.batch.BatchInputFileSetType)
     */
    public boolean isBatchInputTypeActive(BatchInputFileSetType batchInputFileSetType) {
        if (batchInputFileSetType == null) {
            LOG.error("an invalid(null) argument was given");
            throw new IllegalArgumentException("an invalid(null) argument was given");
        }
        List<String> activeInputTypes = new ArrayList<String>( SpringContext.getBean(ParameterService.class).getParameterValuesAsString(KfsParameterConstants.FINANCIAL_SYSTEM_BATCH.class, SystemGroupParameterNames.ACTIVE_INPUT_TYPES_PARAMETER_NAME) );
        
        boolean activeBatchType = false;
        if (activeInputTypes.size() > 0 && activeInputTypes.contains(batchInputFileSetType.getFileSetTypeIdentifer())) {
            activeBatchType = true;
        }

        return activeBatchType;
    }

    /**
     * @see org.kuali.kfs.sys.batch.service.BatchInputFileSetService#save(org.kuali.rice.kim.api.identity.Person,
     *      org.kuali.kfs.sys.batch.BatchInputFileSetType, java.lang.String, java.util.Map)
     */
    public Map<String, String> save(Person user, BatchInputFileSetType inputType, String fileUserIdentifier, Map<String, InputStream> typeToStreamMap) throws AuthorizationException, FileStorageException {
        //add a step for file directory checking
        prepareDirectories(getRequiredDirectoryNames());
        
        Date creationDate = SpringContext.getBean(DateTimeService.class).getCurrentDate();
        // check user is authorized to upload a file for the batch type
        Map<String, File> typeToTempFiles = copyStreamsToTemporaryDirectory(user, inputType, fileUserIdentifier, typeToStreamMap, creationDate);
        
        // null the map, because it's full of exhausted input streams that are useless 
        typeToStreamMap = null;
        
        if (!inputType.validate(typeToTempFiles)) {
            deleteTempFiles(typeToTempFiles);
            LOG.error("Upload file validation failed for user " + user.getName() + " identifier " + fileUserIdentifier);
            throw new ValidationException("File validation failed: " + GlobalVariables.getMessageMap().getErrorMessages());
        }
        
        byte[] buf = new byte[1024];

        Map<String, String> typeToFileNames = new LinkedHashMap<String, String>();
        Map<String, File> typeToFiles = new LinkedHashMap<String, File>();
        try {
            for (String fileType : inputType.getFileTypes()) {
                File tempFile = typeToTempFiles.get(fileType);
                String saveFileName = inputType.getDirectoryPath(fileType) + File.separator + tempFile.getName();
                try {
                    InputStream fileContents = new FileInputStream(tempFile);
                    File fileToSave = new File(saveFileName);
    
                    copyInputStreamToFile(fileContents, fileToSave, buf);
                    fileContents.close();
                    typeToFileNames.put(fileType, saveFileName);
                    typeToFiles.put(fileType, fileToSave);
                }
                catch (IOException e) {
                    LOG.error("unable to save contents to file " + saveFileName, e);
                    throw new RuntimeException("errors encountered while writing file " + saveFileName, e);
                }
            }
        }
        finally {
            deleteTempFiles(typeToTempFiles);
        }

        String doneFileName = inputType.getDoneFileDirectoryPath() + File.separator + inputType.getDoneFileName(user, fileUserIdentifier, creationDate);
        File doneFile = new File(doneFileName);
        try {
            doneFile.createNewFile();
            
            typeToFiles.put(KFSConstants.DONE_FILE_TYPE, doneFile);
        }
        catch (IOException e) {
            LOG.error("unable to create done file", e);
            throw new RuntimeException("unable to create done file", e);
        }
        
        inputType.process(typeToFiles);
        
        return typeToFileNames;
    }

    protected Map<String, File> copyStreamsToTemporaryDirectory(Person user, BatchInputFileSetType inputType,
            String fileUserIdentifier, Map<String, InputStream> typeToStreamMap, Date creationDate) throws FileStorageException {
        Map<String, File> tempFiles = new HashMap<String, File>();
        
        String tempDirectoryName = getTempDirectoryName();
        File tempDirectory = new File(tempDirectoryName);
        if (!tempDirectory.exists() || !tempDirectory.isDirectory()) {
            LOG.error("Temporary Directory " + tempDirectoryName + " does not exist or is not a directory");
            throw new RuntimeException("Temporary Directory " + tempDirectoryName + " does not exist or is not a directory");
        }

        byte[] buf = new byte[1024];

        try {
            for (String fileType : inputType.getFileTypes()) {
                String tempFileName = generateTempFileName(user, inputType, fileUserIdentifier, fileType, creationDate);
                InputStream source = typeToStreamMap.get(fileType);
                File tempFile = new File(tempFileName);
                copyInputStreamToFile(source, tempFile, buf);
                tempFiles.put(fileType, tempFile);
            }
        }
        catch (IOException e) {
            LOG.error("Error creating temporary files", e);
            throw new FileStorageException("Error creating temporary files",e);
            
        }
        return tempFiles;
    }

    protected void copyInputStreamToFile(InputStream source, File outputFile, byte[] buf) throws IOException {
        OutputStream out = new FileOutputStream(outputFile);
        int readBytes = source.read(buf);
        while (readBytes != -1) {
            out.write(buf, 0, readBytes);
            readBytes = source.read(buf);
        }
        out.flush();
        out.close();
    }
    
    protected String getTempDirectoryName() {
        return kualiConfigurationService.getPropertyValueAsString(KFSConstants.TEMP_DIRECTORY_KEY);
    }

    /**
     * @see org.kuali.kfs.sys.batch.service.BatchInputFileSetService#isFileUserIdentifierProperlyFormatted(java.lang.String)
     */
    public boolean isFileUserIdentifierProperlyFormatted(String fileUserIdentifier) {
        if (fileUserIdentifier == null) {
            return false;
        }
        
        for (int i = 0; i < fileUserIdentifier.length(); i++) {
            char c = fileUserIdentifier.charAt(i);
            if (!(Character.isLetterOrDigit(c))) {
                return false;
            }
        }
        return true;
    }

    public void setConfigurationService(ConfigurationService kualiConfigurationService) {
        this.kualiConfigurationService = kualiConfigurationService;
    }
    
    protected void deleteTempFiles(Map<String, File> typeToTempFiles) {
        for (File tempFile : typeToTempFiles.values()) {
            if (tempFile.exists()) {
                boolean deletionSuccessful = tempFile.delete();
                if (!deletionSuccessful) {
                    LOG.error("Unable to delete file (possibly due to unclosed InputStream or Reader on the file): " + tempFile.getAbsolutePath());
                }
            }
        }
    }

    /**
     * @see org.kuali.kfs.sys.batch.service.impl.InitiateDirectoryImpl#getRequiredDirectoryNames()
     */
    @Override
    public List<String> getRequiredDirectoryNames() {
        return new ArrayList<String>(){{add(getTempDirectoryName());}};
    }
}

