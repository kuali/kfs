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
package org.kuali.kfs.sys.batch;

import java.io.File;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.kuali.rice.kim.api.identity.Person;

/**
 * Declares methods that must be implemented for batch input file set type classes, which provides functionaliy needed to manage
 * files of a certain batch type.
 */
public interface BatchInputFileSetType extends BatchInputType {
    /**
     * Returns all of the types supported by this file set type
     * 
     * @return a list of all file types supported. The values in the list do not have an externally usable meaning, and are meant to
     *         be used to call other methods of this interface.
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
     * 
     * @return the key is the file type, the value is a human-readable description
     */
    public Map<String, String> getFileTypeDescription();

    /**
     * Constructs a file name for the file type, the file user identifier, and the user.
     * 
     * @param user - user who is uploading the file
     * @param fileUserIdentifer - file identifier given by user through the batch upload UI
     */
    public String getFileName(String fileType, String principalName, String fileUserIdentifer, Date creationDate);

    /**
     * Returns whether the file must be uploaded
     * 
     * @param fileType the type for the file
     * @return whether it must be uploaded
     */
    public boolean isFileRequired(String fileType);

    /**
     * Returns the directory name where done files are to be stored. The behavior of this method is defined if
     * {@link #isSupportsDoneFileCreation()} returns false.
     * 
     * @return the done file directory name
     */
    public String getDoneFileDirectoryPath();

    /**
     * Returns the file name of the done file for a fileset created by the user with the identifier. The behavior of this method is
     * defined if {@link #isSupportsDoneFileCreation()} returns false.
     * 
     * @return the done file name
     */
    public String getDoneFileName(Person user, String fileUserIdentifer, Date creationDate);

    /**
     * Returns the set of file user identifiers parsed from the provided list of files for the user.
     * 
     * @return a set of file user identifiers
     */
    public Set<String> extractFileUserIdentifiers(Person user, List<File> files);
    
    /**
     * Runs validation upon uploaded files.  Note the files passed in the Map may be located in a temporary directory rather than the
     * directory returned by {@link #getDirectoryPath(String)}
     * 
     * If validation fails, the implementation is responsible for adding error messages to the {@link KFSConstants#GLOBAL_ERRORS} property
     * string in the MessageMap
     * 
     * If validation requires opening up input streams/readers/etc. on the files, implementations of this method must
     * close all input streams/readers on files contained within the map.  Failure to do so may cause the files to be undeletable.
     * 
     * @param typeToFiles a map consisting of file type Strings (see {@link #getFileTypes()}) to file mappings
     * 
     * @return true if validation succeeds, false otherwise
     */
    public boolean validate(Map<String, File> typeToFiles);
    
    
    /**
     * This method will invoke some processing on this file
     * 
     * There is no error handling provided by the framework.  All error reporting must be done by the implementation of this method
     * 
     * @param typeToFiles a map consisting of file type Strings (see {@link #getFileTypes()}) to file mappings
     *        Note that the map may contain the file handle for {@link KFSConstants#DONE_FILE_TYPE} for the done file,
     *        if it was created 
     */
    public void process(Map<String, File> typeToFiles);
}

