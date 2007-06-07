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
package org.kuali.kfs.batch;

import java.io.File;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.kuali.core.bo.user.UniversalUser;

/**
 * Declares methods that must be implemented for batch input file set type classes, which provides functionaliy needed to manage files
 * of a certain batch type.
 */
public interface BatchInputFileSetType extends BatchInputType {
    /**
     * Returns all of the types supported by this file set type
     * @return a list of all file types supported.  The values in the list do not have an externally usable meaning, and are meant
     * to be used to call other methods of this interface.
     */
    public List<String> getFileTypes();
    
    /**
     * Returns the unique identifier (Spring bean id) for the batch input file set type.
     */
    public String getFileSetTypeIdentifer();
    
    /**
     * Gives the name of the directory for which batch files of a given type are stored.
     * 
     * @param fileType the file type
     */
    public String getDirectoryPath(String fileType);
    
    /**
     * Returns a map of file type to file type descriptions, which are intended to be human readable
     * @return the key is the file type, the value is a human-readable description
     */
    public Map<String, String> getFileTypeDescription();
    
    /**
     * Constructs a file name for the file type, the file user identifier, and the user.
     * 
     * @param user - user who is uploading the file
     * @param fileUserIdentifer - file identifier given by user through the batch upload UI
     */
    public String getFileName(String fileType, UniversalUser user, String fileUserIdentifer);
    
    /**
     * Returns whether the file must be uploaded
     * 
     * @param fileType the type for the file
     * @return whether it must be uploaded
     */
    public boolean isFileRequired(String fileType);
    
    /**
     * Returns whether this batch input file set supports the creation of a done file 
     * @return 
     */
    public boolean isSupportsDoneFileCreation();
    
    /**
     * Returns the directory name where done files are to be stored.  The behavior of this method
     * is defined if {@link #isSupportsDoneFileCreation()} returns false.
     * @return the done file directory name
     */
    public String getDoneFileDirectoryPath();
    
    /**
     * Returns the file name of the done file for a fileset created by the user with the identifier.  The behavior of this method
     * is defined if {@link #isSupportsDoneFileCreation()} returns false.
     * @return the done file name
     */
    public String getDoneFileName(UniversalUser user, String fileUserIdentifer);
    
    /**
     * Returns the set of file user identifiers parsed from the provided list of files for the user.
     * @return a set of file user identifiers
     */
    public Set<String> extractFileUserIdentifiers(UniversalUser user, List<File> files);
}
