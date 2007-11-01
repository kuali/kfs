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
import java.util.Map;
import java.util.Set;

import org.kuali.core.bo.user.UniversalUser;
import org.kuali.core.exceptions.AuthorizationException;
import org.kuali.kfs.batch.BatchInputFileSetType;
import org.kuali.kfs.exceptions.FileStorageException;

/**
 * This interface defines the methods needed to save/download/delete file sets in the batch upload system
 */
public interface BatchInputFileSetService {

    /**
     * Stores the input streams (the values in the Map parameter) as files on the server, identified by the given user file name and
     * file user identifier
     * 
     * @param user - user who is requesting the save
     * @param inputType - instance of a BatchInputFileSetType
     * @param fileUserIdentifer - file identifier specified by user
     * @param typeToStreamMap - contents of the uploaded files, keyed by the input file type
     * @return a Map of type to file name mappings of the saved files
     * @throws FileStorageException - if errors were encountered while attempting to write the file
     */
    public Map<String, String> save(UniversalUser user, BatchInputFileSetType inputType, String fileUserIdentifer, Map<String, InputStream> typeToStreamMap, boolean suppressDoneFileCreation) throws AuthorizationException, FileStorageException;

    /**
     * Returns the contents of a batch input file contained on the server if the user has permissions for the files batch input
     * type.
     * 
     * @param user - user who is requesting the download
     * @param inputType - instance of a BatchInputFileSetType
     * @param fileType - the type of the file to retrieve
     * @param fileUserIdentifier file identifier specified by user
     * @return File - File representation of the batch input, or null if errors occured. Check GlobalVariables.errorMap for error
     *         messages.
     * @throws AuthorizationException - if user does not have permission to view batch files of this type FileNotFoundException - if
     *         given file does not exist on the file system
     */
    public File download(UniversalUser user, BatchInputFileSetType inputType, String fileType, String fileUserIdentifier) throws AuthorizationException, FileNotFoundException;

    /**
     * Deletes a batch input file contained on the server if the user has permissions for the files batch input type. Also deletes
     * the associated .done file if one exists. If the file set may not be deleted, then the GlobalVariable's error map will be
     * populated with the reason why.
     * 
     * @param user - user who is requesting the delete
     * @param inputType - instance of a BatchInputFileSetType
     * @param fileUserIdentifier file identifier specified by user
     * @return whether the file was successfully downloaded
     * @throws AuthorizationException - if user does not have permission to delete batch files of this type FileNotFoundException -
     *         if given file does not exist on the file system
     */
    public boolean delete(UniversalUser user, BatchInputFileSetType inputType, String fileUserIdentifier) throws AuthorizationException, FileNotFoundException;

    /**
     * Checks if the batch input type is active (can be used for upload).
     * 
     * @param BatchInputFileSetType - input type to check is active
     * @return boolean - true if type is active, false if not active
     */
    public boolean isBatchInputTypeActive(BatchInputFileSetType batchInputFileSetType);

    /**
     * Checks if the user has permissions to manage the batch input type.
     * 
     * @param batchInputFileSetType - input type to check user permissions on
     * @param user - user to check
     * @return boolean - true if user has permissions for the type, false if the user does not have permission
     */
    public boolean isUserAuthorizedForBatchType(BatchInputFileSetType batchInputFileSetType, UniversalUser user);

    /**
     * Returns a list of batch type file names (including path) that the given user has permissions to manage.
     * 
     * @param user - user for checking permissions
     * @return List<String> - List of filenames
     */
    public Set<String> listBatchTypeFileUserIdentifiersForUser(BatchInputFileSetType batchInputFileSetType, UniversalUser user) throws AuthorizationException;

    /**
     * Returns whether a file set identifier is properly formatted.
     * 
     * @param fileUserIdentifier
     * @return
     */
    public boolean isFileUserIdentifierProperlyFormatted(String fileUserIdentifier);

    /**
     * Returns whether a file set for a given user has already been processed
     * 
     * @param user
     * @param inputType
     * @param fileUserIdentifier
     * @return
     */
    public boolean hasBeenProcessed(UniversalUser user, BatchInputFileSetType inputType, String fileUserIdentifier);
}
