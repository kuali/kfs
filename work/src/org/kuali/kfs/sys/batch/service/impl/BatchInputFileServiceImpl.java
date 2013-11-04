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
import java.io.FileOutputStream;
import java.io.FilenameFilter;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang.StringUtils;
import org.kuali.kfs.sys.KFSConstants.SystemGroupParameterNames;
import org.kuali.kfs.sys.batch.BatchInputFileType;
import org.kuali.kfs.sys.batch.service.BatchInputFileService;
import org.kuali.kfs.sys.context.SpringContext;
import org.kuali.kfs.sys.exception.FileStorageException;
import org.kuali.kfs.sys.exception.ParseException;
import org.kuali.kfs.sys.service.impl.KfsParameterConstants;
import org.kuali.rice.coreservice.framework.parameter.ParameterService;
import org.kuali.rice.kim.api.identity.Person;
import org.kuali.rice.krad.exception.AuthorizationException;
import org.kuali.rice.krad.util.ObjectUtils;

/**
 * Provides batch input file management, including listing files, parsing, downloading, storing, and deleting.
 */
public class BatchInputFileServiceImpl implements BatchInputFileService {
    private static org.apache.log4j.Logger LOG = org.apache.log4j.Logger.getLogger(BatchInputFileServiceImpl.class);

    /**
     * Delegates to the batch input file type to parse the file.
     *
     * @see org.kuali.kfs.sys.batch.service.BatchInputFileService#parse(org.kuali.kfs.sys.batch.BatchInputFileType, byte[])
     */
    @Override
    public Object parse(BatchInputFileType batchInputFileType, byte[] fileByteContent) {
        try {
            return batchInputFileType.parse(fileByteContent);
        }
        catch (ParseException e) {
            LOG.error("Error encountered parsing file", e);
            throw e;
        }
    }

    /**
     * Defers to batch type to do any validation on the parsed contents.
     *
     * @see org.kuali.kfs.sys.batch.service.BatchInputFileService#validate(org.kuali.kfs.sys.batch.BatchInputFileType, java.lang.Object)
     */
    @Override
    public boolean validate(BatchInputFileType batchInputFileType, Object parsedObject) {
        if (batchInputFileType == null || parsedObject == null) {
            LOG.error("an invalid(null) argument was given");
            throw new IllegalArgumentException("an invalid(null) argument was given");
        }

        boolean contentsValid = true;
        contentsValid = batchInputFileType.validate(parsedObject);
        return contentsValid;
    }

    /**
     * @see org.kuali.kfs.sys.batch.service.BatchInputFileService#save(org.kuali.rice.kim.api.identity.Person,
     *      org.kuali.kfs.sys.batch.BatchInputFileType, java.lang.String, java.io.InputStream)
     */
    @Override
    public String save(Person user, BatchInputFileType batchInputFileType, String fileUserIdentifier, InputStream fileContents, Object parsedObject) throws AuthorizationException, FileStorageException {
        if (user == null || batchInputFileType == null || fileContents == null) {
            LOG.error("an invalid(null) argument was given");
            throw new IllegalArgumentException("an invalid(null) argument was given");
        }

        if (!isFileUserIdentifierProperlyFormatted(fileUserIdentifier)) {
            LOG.error("The following file user identifer was not properly formatted: " + fileUserIdentifier);
            throw new IllegalArgumentException("The following file user identifer was not properly formatted: " + fileUserIdentifier);
        }

        // defer to batch input type to add any security or other needed information to the file name
        String saveFileName = batchInputFileType.getDirectoryPath() + "/" + batchInputFileType.getFileName(user.getPrincipalName(), parsedObject, fileUserIdentifier);
        if (!StringUtils.isBlank(batchInputFileType.getFileExtension())) {
            saveFileName += "." + batchInputFileType.getFileExtension();
        }

        if (batchInputFileType.shouldSave()) {
            // consruct the file object and check for existence
            File fileToSave = new File(saveFileName);
            if (fileToSave.exists()) {
                LOG.error("cannot store file, name already exists " + saveFileName);
                throw new FileStorageException("Cannot store file because the name " + saveFileName + " already exists on the file system.");
            }

            try {
                FileOutputStream fos = new FileOutputStream(fileToSave);
                while (fileContents.available() > 0) {
                    fos.write(fileContents.read());
                }
                fos.flush();
                fos.close();

                createDoneFile(fileToSave, batchInputFileType);

                batchInputFileType.process(saveFileName, parsedObject);
            }
            catch (IOException e) {
                LOG.error("unable to save contents to file " + saveFileName, e);
                throw new RuntimeException("errors encountered while writing file " + saveFileName, e);
            }
        }
        else {
            batchInputFileType.process(saveFileName, parsedObject);
        }
        return saveFileName;
    }

    /**
     * Creates a '.done' file with the name of the batch file.
     */
    protected void createDoneFile(File batchFile ,BatchInputFileType batchInputFileType ) {
        String fileExtension = batchInputFileType.getFileExtension();
        File doneFile = generateDoneFileObject(batchFile, fileExtension);
        String doneFileName = doneFile.getName();

        if (!doneFile.exists()) {
            boolean doneFileCreated = false;
            try {
                doneFileCreated = doneFile.createNewFile();
            }
            catch (IOException e) {
                LOG.error("unable to create done file " + doneFileName, e);
                throw new RuntimeException("Errors encountered while saving the file: Unable to create .done file " + doneFileName, e);
            }

            if (!doneFileCreated) {
                LOG.error("unable to create done file " + doneFileName);
                throw new RuntimeException("Errors encountered while saving the file: Unable to create .done file " + doneFileName);
            }
        }
    }

    /**
     * This method is responsible for creating a File object that represents the done file. The real file represented on disk may
     * not exist
     *
     * @param batchInputFile
     * @return a File object representing the done file. The real file may not exist on disk, but the return value can be used to
     *         create that file.
     */
    protected File generateDoneFileObject(File batchInputFile, String fileExtension) {
        String doneFileName = fileExtension != null  ? StringUtils.substringBeforeLast(batchInputFile.getPath(), ".") + ".done" :
                                batchInputFile.getPath() + ".done" ;
        File doneFile = new File(doneFileName);
        return doneFile;
    }

    /**
     * @see org.kuali.kfs.sys.batch.service.BatchInputFileService#isBatchInputTypeActive(org.kuali.kfs.sys.batch.BatchInputFileType)
     */
    @Override
    public boolean isBatchInputTypeActive(BatchInputFileType batchInputFileType) {
        if (batchInputFileType == null) {
            LOG.error("an invalid(null) argument was given");
            throw new IllegalArgumentException("an invalid(null) argument was given");
        }

        List<String> activeInputTypes = new ArrayList<String>( SpringContext.getBean(ParameterService.class).getParameterValuesAsString(KfsParameterConstants.FINANCIAL_SYSTEM_BATCH.class, SystemGroupParameterNames.ACTIVE_INPUT_TYPES_PARAMETER_NAME) );

        boolean activeBatchType = false;
        if (activeInputTypes.size() > 0 && activeInputTypes.contains(batchInputFileType.getFileTypeIdentifer())) {
            activeBatchType = true;
        }

        return activeBatchType;
    }

    /**
     * Fetches workgroup for batch type from system parameter and verifies user is a member. Then a list of all files for the batch
     * type are retrieved. For each file, the file and user is sent through the checkAuthorization method of the batch input type
     * implementation for finer grained security. If the method returns true, the filename is added to the user's list.
     *
     * @see org.kuali.kfs.sys.batch.service.BatchInputFileService#listBatchTypeFilesForUser(org.kuali.kfs.sys.batch.BatchInputFileType,
     *      org.kuali.rice.kim.api.identity.Person)
     */
    @Override
    public List<String> listBatchTypeFilesForUser(BatchInputFileType batchInputFileType, Person user) throws AuthorizationException {
        if (batchInputFileType == null || user == null) {
            LOG.error("an invalid(null) argument was given");
            throw new IllegalArgumentException("an invalid(null) argument was given");
        }

        File[] filesInBatchDirectory = listFilesInBatchTypeDirectory(batchInputFileType);

        List<String> userFileNamesList = new ArrayList<String>();
        List<File> userFileList = listBatchTypeFilesForUserAsFiles(batchInputFileType, user);

        for (File userFile : userFileList) {
            userFileNamesList.add(userFile.getAbsolutePath());
        }

        return userFileNamesList;
    }

    protected List<File> listBatchTypeFilesForUserAsFiles(BatchInputFileType batchInputFileType, Person user) throws AuthorizationException {
        File[] filesInBatchDirectory = listFilesInBatchTypeDirectory(batchInputFileType);

        List<File> userFileList = new ArrayList<File>();
        if (filesInBatchDirectory != null) {
            for (int i = 0; i < filesInBatchDirectory.length; i++) {
                File batchFile = filesInBatchDirectory[i];
                String fileExtension = StringUtils.substringAfterLast(batchFile.getName(), ".");
                if (StringUtils.isBlank(batchInputFileType.getFileExtension()) || batchInputFileType.getFileExtension().equals(fileExtension)) {
                    if (user.getPrincipalName().equals(batchInputFileType.getAuthorPrincipalName(batchFile))) {
                        userFileList.add(batchFile);
                    }
                }
            }
        }
        return userFileList;
    }

    /**
     * Returns List of filenames for existing files in the directory given by the batch input type.
     */
    protected File[] listFilesInBatchTypeDirectory(BatchInputFileType batchInputFileType) {
        File batchTypeDirectory = new File(batchInputFileType.getDirectoryPath());
        return batchTypeDirectory.listFiles();
    }

    /**
     * @see org.kuali.kfs.sys.batch.service.BatchInputFileService#listInputFileNamesWithDoneFile(org.kuali.kfs.sys.batch.BatchInputFileType)
     */
    @Override
    public List<String> listInputFileNamesWithDoneFile(BatchInputFileType batchInputFileType) {
        if (batchInputFileType == null) {
            LOG.error("an invalid(null) argument was given");
            throw new IllegalArgumentException("an invalid(null) argument was given");
        }

        File batchTypeDirectory = new File(batchInputFileType.getDirectoryPath());
        File[] doneFiles = batchTypeDirectory.listFiles(new DoneFilenameFilter());

        List<String> batchInputFiles = new ArrayList<String>();
        for (int i = 0; i < doneFiles.length; i++) {
            File doneFile = doneFiles[i];

            String dataFileName = StringUtils.substringBeforeLast(doneFile.getPath(), ".");
            if (!StringUtils.isBlank(batchInputFileType.getFileExtension())) {
                dataFileName += "." + batchInputFileType.getFileExtension();
            }

            File dataFile = new File(dataFileName);
            if (dataFile.exists()) {
                batchInputFiles.add(dataFile.getPath());
            }
        }

        return batchInputFiles;
    }

    /**
     * Retrieves files in a directory with the .done extension.
     */
    protected class DoneFilenameFilter implements FilenameFilter {
        /**
         * @see java.io.FilenameFilter#accept(java.io.File, java.lang.String)
         */
        @Override
        public boolean accept(File dir, String name) {
            return name.endsWith(".done");
        }
    }

    /**
     * For this implementation, a file user identifier must consist of letters and digits
     *
     * @see org.kuali.kfs.sys.batch.service.BatchInputFileService#isFileUserIdentifierProperlyFormatted(java.lang.String)
     */
    @Override
    public boolean isFileUserIdentifierProperlyFormatted(String fileUserIdentifier) {
        if(ObjectUtils.isNull(fileUserIdentifier)) {
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
}

