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
package org.kuali.kfs.service.impl;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.commons.lang.StringUtils;
import org.kuali.core.bo.user.UniversalUser;
import org.kuali.core.exceptions.AuthorizationException;
import org.kuali.core.service.KualiConfigurationService;
import org.kuali.core.util.GlobalVariables;
import org.kuali.kfs.KFSConstants;
import org.kuali.kfs.KFSKeyConstants;
import org.kuali.kfs.KFSConstants.SystemGroupParameterNames;
import org.kuali.kfs.batch.BatchInputFileSetType;
import org.kuali.kfs.context.SpringContext;
import org.kuali.kfs.exceptions.FileStorageException;
import org.kuali.kfs.service.BatchInputFileSetService;

/**
 * Base implementation to manipulate batch input file sets from the batch upload screen
 */
public class BatchInputFileSetServiceImpl implements BatchInputFileSetService {
    private static org.apache.log4j.Logger LOG = org.apache.log4j.Logger.getLogger(BatchInputFileSetServiceImpl.class);
    
    /**
     * Generates the file name of a file (not the done file)
     * @param user the user who uploaded or will upload the file
     * @param inputType the file set type
     * @param fileUserIdentifier the file identifier
     * @param fileType the file type
     * @return the file name, starting with the directory path 
     */
    protected String generateFileName(UniversalUser user, BatchInputFileSetType inputType, String fileUserIdentifier, String fileType) {
        if (!isFileUserIdentifierProperlyFormatted(fileUserIdentifier)) {
            throw new IllegalArgumentException("The file set identifier is not properly formatted: " + fileUserIdentifier);
        }
        return inputType.getDirectoryPath(fileType) + File.separator + inputType.getFileName(fileType, user, fileUserIdentifier);
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
    protected String generateDoneFileName(UniversalUser user, BatchInputFileSetType inputType, String fileUserIdentifier) {
        if (!isFileUserIdentifierProperlyFormatted(fileUserIdentifier)) {
            throw new IllegalArgumentException("The file set identifier is not properly formatted: " + fileUserIdentifier);
        }
        return inputType.getDoneFileDirectoryPath() + File.separator + inputType.getDoneFileName(user, fileUserIdentifier);
    }
    
    /**
     * @see org.kuali.kfs.service.BatchInputFileSetService#delete(org.kuali.core.bo.user.UniversalUser, org.kuali.kfs.batch.BatchInputFileSetType, java.lang.String)
     */
    public boolean delete(UniversalUser user, BatchInputFileSetType inputType, String fileUserIdentifier) throws AuthorizationException, FileNotFoundException {
        if (user == null || inputType == null || StringUtils.isBlank(fileUserIdentifier)) {
            LOG.error("an invalid(null) argument was given");
            throw new IllegalArgumentException("an invalid(null) argument was given");
        }

        // check user is authorized to delete a file for the batch type
        if (!this.isUserAuthorizedForBatchType(inputType, user)) {
            LOG.error("User " + user.getPersonUserIdentifier() + " is not authorized to delete a file of batch type " + inputType.getFileSetTypeIdentifer());
            throw new AuthorizationException(user.getPersonUserIdentifier(), "delete", inputType.getFileSetTypeIdentifer());
        }
        
        if (!canDelete(user, inputType, fileUserIdentifier)) {
            return false;
        }
        
        for (String fileType : inputType.getFileTypes()) {
            String fileName = generateFileName(user, inputType, fileUserIdentifier, fileType);
            File file = new File(fileName);
            if (file.exists()) {
                file.delete();
            }
        }
        File doneFile = new File(generateDoneFileName(user, inputType, fileUserIdentifier));
        if (doneFile.exists()) {
            doneFile.delete();
        }
        return true;
    }
    
    /**
     * Determines whether a file set may be deleted, and if not, it will populate the GlobalVariable's error map with the reason why not
     * @param user
     * @param inputType
     * @param fileUserIdentifier
     * @return
     * @throws AuthorizationException
     * @throws FileNotFoundException
     */
    protected boolean canDelete(UniversalUser user, BatchInputFileSetType inputType, String fileUserIdentifier) throws AuthorizationException, FileNotFoundException {
        // we can only delete if we're authorized on each of the files of the file set
        for (String fileType : inputType.getFileTypes()) {
            String fileName = generateFileName(user, inputType, fileUserIdentifier, fileType);
            File file = new File(fileName);
            if (file.exists()) {
                if (!inputType.checkAuthorization(user, file)) {
                    GlobalVariables.getErrorMap().putError(KFSConstants.GLOBAL_ERRORS, KFSKeyConstants.ERROR_BATCH_UPLOAD_DELETE_FAILED_FILE_SET_NOT_AUTHORIZED);
                    return false;
                }
            }
        }
        
        // and the file hasn't been processed
        if (hasBeenProcessed(user, inputType, fileUserIdentifier)) {
            GlobalVariables.getErrorMap().putError(KFSConstants.GLOBAL_ERRORS, KFSKeyConstants.ERROR_BATCH_UPLOAD_DELETE_FAILED_FILE_SET_ALREADY_PROCESSED);
            return false;
        }
        return true;
    }

    /**
     * @see org.kuali.kfs.service.BatchInputFileSetService#hasBeenProcessed(org.kuali.core.bo.user.UniversalUser, org.kuali.kfs.batch.BatchInputFileSetType, java.lang.String)
     */
    public boolean hasBeenProcessed(UniversalUser user, BatchInputFileSetType inputType, String fileUserIdentifier) {
        // if the done file exists, then that means that the file set has not been processed 
        File doneFile = new File(generateDoneFileName(user, inputType, fileUserIdentifier));
        return !doneFile.exists();
    }
    
    /**
     * @see org.kuali.kfs.service.BatchInputFileSetService#download(org.kuali.core.bo.user.UniversalUser, org.kuali.kfs.batch.BatchInputFileSetType, java.lang.String)
     */
    public File download(UniversalUser user, BatchInputFileSetType inputType, String fileType, String fileUserIdentifier) throws AuthorizationException, FileNotFoundException {
        if (!this.isUserAuthorizedForBatchType(inputType, user)) {
            LOG.error("User " + user.getPersonUserIdentifier() + " is not authorized to download a file of batch type " + inputType.getFileSetTypeIdentifer());
            throw new AuthorizationException(user.getPersonUserIdentifier(), "download", inputType.getFileSetTypeIdentifer());
        }
        
        String fileName = generateFileName(user, inputType, fileUserIdentifier, fileType);
        File file = new File(fileName);
        if (!inputType.checkAuthorization(user, file)) {
            LOG.error("User " + user.getPersonUserIdentifier() + " is not authorized to download the following file: " + file.getName());
            throw new AuthorizationException(user.getPersonUserIdentifier(), "download", inputType.getFileSetTypeIdentifer());
        }
        
        if (!file.exists()) {
            LOG.error("unable to download file " + fileName + " because it doesn not exist.");
            throw new FileNotFoundException("Unable to download file " + fileName + ". File does not exist on server.");
        }
        return file;
    }

    /**
     * @see org.kuali.kfs.service.BatchInputFileSetService#isBatchInputTypeActive(org.kuali.kfs.batch.BatchInputFileSetType)
     */
    public boolean isBatchInputTypeActive(BatchInputFileSetType batchInputFileSetType) {
        if (batchInputFileSetType == null) {
            LOG.error("an invalid(null) argument was given");
            throw new IllegalArgumentException("an invalid(null) argument was given");
        }

        List<String> activeInputTypes = SpringContext.getBean(KualiConfigurationService.class).getParameterValuesAsList(KFSConstants.CORE_NAMESPACE, KFSConstants.Components.BATCH, SystemGroupParameterNames.ACTIVE_INPUT_TYPES_PARAMETER_NAME);

        boolean activeBatchType = false;
        if (activeInputTypes.size() > 0 && activeInputTypes.contains(batchInputFileSetType.getFileSetTypeIdentifer())) {
            activeBatchType = true;
        }

        return activeBatchType;
    }

    /**
     * @see org.kuali.kfs.service.BatchInputFileSetService#isUserAuthorizedForBatchType(org.kuali.kfs.batch.BatchInputFileSetType, org.kuali.core.bo.user.UniversalUser)
     */
    public boolean isUserAuthorizedForBatchType(BatchInputFileSetType batchInputFileSetType, UniversalUser user) {
        if (batchInputFileSetType == null || user == null) {
            LOG.error("an invalid(null) argument was given");
            throw new IllegalArgumentException("an invalid(null) argument was given");
        }

        String workgroupParameterName = batchInputFileSetType.getWorkgroupParameterName();
        String authorizedWorkgroupName = SpringContext.getBean(KualiConfigurationService.class).getParameterValue(batchInputFileSetType.getWorkgroupParameterNamespace(), batchInputFileSetType.getWorkgroupParameterComponent(), workgroupParameterName);
 
        return user.isMember(authorizedWorkgroupName);
    }

    /**
     * @see org.kuali.kfs.service.BatchInputFileSetService#listBatchTypeFilesForUser(org.kuali.kfs.batch.BatchInputFileSetType, org.kuali.core.bo.user.UniversalUser)
     */
    public Set<String> listBatchTypeFileUserIdentifiersForUser(BatchInputFileSetType batchInputFileSetType, UniversalUser user) throws AuthorizationException {
        List<File> files = new ArrayList<File>();
        for (String fileType : batchInputFileSetType.getFileTypes()) {
            File fileTypeDirectory = new File(batchInputFileSetType.getDirectoryPath(fileType));
            File[] filesFromDirectory = fileTypeDirectory.listFiles();
            Collections.addAll(files, filesFromDirectory);
        }
        return batchInputFileSetType.extractFileUserIdentifiers(user, files);
    }

    /**
     * @see org.kuali.kfs.service.BatchInputFileSetService#save(org.kuali.core.bo.user.UniversalUser, org.kuali.kfs.batch.BatchInputFileSetType, java.lang.String, java.util.Map)
     */
    public Map<String, String> save(UniversalUser user, BatchInputFileSetType inputType, String fileUserIdentifer, 
            Map<String, InputStream> typeToStreamMap, boolean suppressDoneFileCreation) throws AuthorizationException, FileStorageException {
        // check user is authorized to upload a file for the batch type
        if (!isUserAuthorizedForBatchType(inputType, user)) {
            LOG.error("User " + user.getPersonUserIdentifier() + " is not authorized to upload a file of batch type " + inputType.getFileSetTypeIdentifer());
            throw new AuthorizationException(user.getPersonUserIdentifier(), "upload", inputType.getFileSetTypeIdentifer());
        }
        
        assertNoFilesInSetExist(user, inputType, fileUserIdentifer);

        Map<String, String> typeToFileNames = new LinkedHashMap<String, String>();
        for (String fileType : inputType.getFileTypes()) {
            String saveFileName = inputType.getDirectoryPath(fileType) + File.separator + inputType.getFileName(fileType, user, fileUserIdentifer);
            try {
                InputStream fileContents = typeToStreamMap.get(fileType);
                
                File fileToSave = new File(saveFileName);
                fileToSave.createNewFile();
                FileWriter fileWriter = new FileWriter(fileToSave);
                while (fileContents.available() > 0) {
                    fileWriter.write(fileContents.read());
                }
                fileWriter.flush();
                fileWriter.close();
                
                typeToFileNames.put(fileType, saveFileName);
            }
            catch (IOException e) {
                LOG.error("unable to save contents to file " + saveFileName, e);
                throw new RuntimeException("errors encountered while writing file " + saveFileName, e);
            }
        }
        
        if (!suppressDoneFileCreation && inputType.isSupportsDoneFileCreation()) {
            String doneFileName = inputType.getDoneFileDirectoryPath() + File.separator + inputType.getDoneFileName(user, fileUserIdentifer);
            File doneFile = new File(doneFileName);
            try {
                doneFile.createNewFile();
            }
            catch (IOException e) {
                LOG.error("unable to create done file", e);
                throw new RuntimeException("unable to create done file", e);
            }
        }
        return typeToFileNames;
    }

    /**
     * Checks whether files associated with the set are already persisted on the file system or the done file.  If so, then an exception is thrown
     * 
     * @param user the user who uploaded or will upload the file
     * @param inputType the file set type
     * @param fileUserIdentifier the file identifier
     */
    protected void assertNoFilesInSetExist(UniversalUser user, BatchInputFileSetType inputType, String fileUserIdentifier) throws FileStorageException {
        for (String fileType : inputType.getFileTypes()) {
            String fileName = generateFileName(user, inputType, fileUserIdentifier, fileType);
            File file = new File(fileName);
            if (file.exists()) {
                // only print out the file name itself, no path
                throw new FileStorageException("Cannot store file because the name " + file.getName() + " already exists on the file system.");
            }
        }
        File doneFile = new File(generateDoneFileName(user, inputType, fileUserIdentifier));
        if (doneFile.exists()) {
            throw new FileStorageException("Cannot store file because the name " + doneFile.getName() + " already exists on the file system.");
        }
        
    }

    /**
     * @see org.kuali.kfs.service.BatchInputFileSetService#isFileUserIdentifierProperlyFormatted(java.lang.String)
     */
    public boolean isFileUserIdentifierProperlyFormatted(String fileUserIdentifier) {
        for (int i = 0; i < fileUserIdentifier.length(); i++) {
            char c = fileUserIdentifier.charAt(i);
            if (!(Character.isLetterOrDigit(c))) {
                return false;
            }
        }
        return true;
    }
}
