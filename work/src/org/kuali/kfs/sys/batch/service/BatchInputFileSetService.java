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
import java.util.Map;

import org.kuali.kfs.sys.batch.BatchInputFileSetType;
import org.kuali.kfs.sys.batch.InitiateDirectory;
import org.kuali.kfs.sys.exception.FileStorageException;
import org.kuali.rice.kim.api.identity.Person;
import org.kuali.rice.krad.exception.AuthorizationException;

/**
 * This interface defines the methods needed to save/download/delete file sets in the batch upload system
 */
public interface BatchInputFileSetService extends InitiateDirectory{

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
    public Map<String, String> save(Person user, BatchInputFileSetType inputType, String fileUserIdentifer, Map<String, InputStream> typeToStreamMap) throws AuthorizationException, FileStorageException;

    /**
     * Checks if the batch input type is active (can be used for upload).
     * 
     * @param BatchInputFileSetType - input type to check is active
     * @return boolean - true if type is active, false if not active
     */
    public boolean isBatchInputTypeActive(BatchInputFileSetType batchInputFileSetType);

    /**
     * Returns whether a file set identifier is properly formatted.
     * 
     * @param fileUserIdentifier
     * @return
     */
    public boolean isFileUserIdentifierProperlyFormatted(String fileUserIdentifier);
}

