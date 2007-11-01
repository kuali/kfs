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


import org.kuali.core.bo.user.UniversalUser;

/**
 * Declares methods that must be implemented for batch input file type classes, which provides functionaliy needed to manage files
 * of a certain batch type.
 */
public interface BatchInputFileType extends BatchInputType {

    /**
     * Returns the unique identifier (Spring bean id) for the batch input type.
     */
    public String getFileTypeIdentifer();

    /**
     * Gives the name of the directory for which batch files of a given type are stored.
     */
    public String getDirectoryPath();

    /**
     * Constructs a file name using the name given by the user and file contents if necessary. Returned name should not contain file
     * extension.
     * 
     * @param user - user who is uploading the file
     * @param parsedFileContents - object populated with the uploaded file contents
     * @param fileUserIdentifer - file identifier given by user through the batch upload UI
     */
    public String getFileName(UniversalUser user, Object parsedFileContents, String fileUserIdentifer);

    /**
     * Returns file extension for the batch input type.
     */
    public String getFileExtension();

    /**
     * Performs specific validation on the parsed file contents. If errors were found, method will return false and
     * GlobalVariables.errorMap will contain the error message. If no errors were encountered the method will return true.
     * 
     * @param parsedFileContents - object populated with the uploaded file contents
     */
    public boolean validate(Object parsedFileContents);

    /**
     * Returns the name with path for the digestor rules file that tells the digestor how to parse files of this type.
     */
    public String getDigestorRulesFileName();

    /**
     * Returns the schema classpath location for this batch type.
     */
    public String getSchemaLocation();
}
