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
package org.kuali.kfs.service;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.util.List;

import org.kuali.core.bo.user.UniversalUser;
import org.kuali.core.exceptions.AuthorizationException;
import org.kuali.kfs.batch.BatchInputFileType;
import org.kuali.kfs.exceptions.FileStorageException;
import org.kuali.kfs.exceptions.XMLParseException;

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
     * @throws XMLParseException - if there were errors encountered during parsing of the xml
     */
    public Object parse(BatchInputFileType batchInputFileType, byte[] fileByteContent) throws XMLParseException;

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
     * @param fileUserIdentifer - file identifier specified by user
     * @param fileContents - contents of the uploaded file
     * @param parsedObject - object parsed from the input file
     * @return String - name of file that was saved, or null if errors were enountered
     * @throws FileStorageException - if errors were encountered while attempting to write the file
     */
    public String save(UniversalUser user, BatchInputFileType inputType, String fileUserIdentifer, InputStream fileContents, Object parsedObject) throws AuthorizationException, FileStorageException;

    /**
     * Returns the contents of a batch input file contained on the server if the user has permissions for the files batch input
     * type.
     * 
     * @param user - user who is requesting the download
     * @param inputType - instance of a BatchInputFileType
     * @param downloadFileNameWithNoPath - name of the file to retrieve, with no path information
     * @return File - File representation of the batch input, or null if errors occured. Check GlobalVariables.errorMap for error
     *         messages.
     * @throws AuthorizationException - if user does not have permission to view batch files of this type FileNotFoundException - if
     *         given file does not exist on the file system
     */
    public File download(UniversalUser user, BatchInputFileType inputType, String downloadFileName) throws AuthorizationException, FileNotFoundException;

    /**
     * Deletes a batch input file contained on the server if the user has permissions for the files batch input type. Also deletes the associated .done
     * file if one exists.  If deletion fails, this method will place the reason for failure in the GlobalVariables error map.
     * 
     * @param user - user who is requesting the delete
     * @param inputType - instance of a BatchInputFileType
     * @param deleteFileNameWithNoPath - name of the file to remove, with no path information
     * @return whether the file (and its done file) was deleted
     * @throws AuthorizationException - if user does not have permission to delete batch files of this type FileNotFoundException -
     *         if given file does not exist on the file system
     */
    public boolean delete(UniversalUser user, BatchInputFileType inputType, String deleteFileNameWithNoPath) throws AuthorizationException, FileNotFoundException;
    
    /**
     * Returns whether a the given file has been processed by the associated batch job
     * @param inputType
     * @param fileNameWithNoPath
     * @return
     */
    public boolean hasBeenProcessed(BatchInputFileType inputType, String fileNameWithNoPath);
    
    /**
     * Checks if the batch input type is active (can be used for upload).
     * 
     * @param batchInputFileType - input type to check is active
     * @return boolean - true if type is active, false if not active
     */
    public boolean isBatchInputTypeActive(BatchInputFileType batchInputFileType);

    /**
     * Checks if the user has permissions to manage the batch input type.
     * 
     * @param batchInputFileType - input type to check user permissions on
     * @param user - user to check
     * @return boolean - true if user has permissions for the type, false if the user does not have permission
     */
    public boolean isUserAuthorizedForBatchType(BatchInputFileType batchInputFileType, UniversalUser user);

    /**
     * Returns a list of batch type file names (without path) that the given user has permissions to manage.
     * Path is intentionally excluded to prevent security problems arising from giving users access to the full path.
     * 
     * @param user - user for checking permissions
     * @return List<String> - List of filenames
     */
    public List<String> listBatchTypeFilesForUser(BatchInputFileType batchInputFileType, UniversalUser user) throws AuthorizationException;

    /**
     * Returns a list of existing input files for the batch type that have an associated .done file
     * 
     * @param batchInputFileType - batch type to retieve files for
     * @return List<String> - List of filenames
     */
    public List<String> listInputFileNamesWithDoneFile(BatchInputFileType batchInputFileType);
}
