/*
 * The Kuali Financial System, a comprehensive financial management system for higher education.
 * 
 * Copyright 2005-2014 The Kuali Foundation
 * 
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Affero General Public License as
 * published by the Free Software Foundation, either version 3 of the
 * License, or (at your option) any later version.
 * 
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Affero General Public License for more details.
 * 
 * You should have received a copy of the GNU Affero General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
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

