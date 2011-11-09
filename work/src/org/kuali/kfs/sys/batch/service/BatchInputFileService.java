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
package org.kuali.kfs.sys.batch.service;

import java.io.InputStream;
import java.util.List;

import org.kuali.kfs.sys.batch.BatchInputFileType;
import org.kuali.kfs.sys.exception.FileStorageException;
import org.kuali.rice.kim.api.identity.Person;
import org.kuali.rice.krad.exception.AuthorizationException;

/**
 * Interface defining methods to manage batch input files.
 */
public interface BatchInputFileService {
    /**
     * Unmarshalls the file contents to an Object using the digestor and digestor rules file specified in the batch input type.
     * 
     * @param batchInputFileType - batch input file type for the file to parse
     * @param fileByteContent - byte contents of file to parse
     * @return - Object built from the file contents based on its xml unmarshalling rules
     */
    public Object parse(BatchInputFileType batchInputFileType, byte[] fileByteContent);

    /**
     * Using the input type object parses and validates the file contents by calling validate on the batch input type. If there were
     * validation errors, GlobalVariables.errorMap will contain the error messages.
     * 
     * @param inputType - instance of a BatchInputFileType
     * @param parsedObject - the Object built from parsing xml contents
     * @return boolean - true if validation was successful, false if there were errors
     */
    public boolean validate(BatchInputFileType inputType, Object parsedObject);

    /**
     * Stores the inputstream as a file on the server, identified by the given user file name.
     * 
     * @param user - user who is requesting the save
     * @param inputType - instance of a BatchInputFileType
     * @param fileUserIdentifier - file identifier specified by user
     * @param fileContents - contents of the uploaded file
     * @param parsedObject - object parsed from the input file
     * @return String - name of file that was saved, or null if errors were enountered
     * @throws FileStorageException - if errors were encountered while attempting to write the file
     */
    public String save(Person user, BatchInputFileType inputType, String fileUserIdentifier, InputStream fileContents, Object parsedObject) throws AuthorizationException, FileStorageException;

    /**
     * Checks if the batch input type is active (can be used for upload).
     * 
     * @param batchInputFileType - input type to check is active
     * @return boolean - true if type is active, false if not active
     */
    public boolean isBatchInputTypeActive(BatchInputFileType batchInputFileType);

    /**
     * Returns a list of batch type file names (without path) that the given user has permissions to manage. Path is intentionally
     * excluded to prevent security problems arising from giving users access to the full path.
     * 
     * @param user - user for checking permissions
     * @return List<String> - List of filenames
     */
    public List<String> listBatchTypeFilesForUser(BatchInputFileType batchInputFileType, Person user) throws AuthorizationException;

    /**
     * Returns a list of existing input files for the batch type that have an associated .done file
     * 
     * @param batchInputFileType - batch type to retieve files for
     * @return List<String> - List of filenames
     */
    public List<String> listInputFileNamesWithDoneFile(BatchInputFileType batchInputFileType);

    /**
     * Returns whether a file user identifier is properly formatted.
     * 
     * @param fileUserIdentifier
     * @return
     */
    public boolean isFileUserIdentifierProperlyFormatted(String fileUserIdentifier);
}

