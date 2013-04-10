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
package org.kuali.kfs.sys.batch;

import org.kuali.kfs.sys.exception.ParseException;

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
     * @param principalName - principal name of the user who is uploading the file
     * @param parsedFileContents - object populated with the uploaded file contents
     * @param fileUserIdentifer - file identifier given by user through the batch upload UI
     */
    public String getFileName(String principalName, Object parsedFileContents, String fileUserIdentifuer);

    /**
     * Returns file extension for the batch input type.
     */
    public String getFileExtension();

    public Object parse(byte[] fileByteContent) throws ParseException;

    /**
     * Performs specific validation on the parsed file contents. If errors were found, method will return false and
     * GlobalVariables.errorMap will contain the error message. If no errors were encountered the method will return true.
     *
     * @param parsedFileContents - object populated with the uploaded file contents
     */
    public boolean validate(Object parsedFileContents);

    /**
     * Invokes optional processing of file after validation
     *
     * @param fileName name of the file
     * @param parsedFileContents objects populated with file contents
     */
    public void process(String fileName, Object parsedFileContents);

    /**
     * Returns a boolean whether we should save the input file that was uploaded into KFS.
     * By default it would return true, but some of the subclasses of BatchInputFileType
     * that don't want the input file to be saved can return false.
     *
     * @return
     */
    public boolean shouldSave();
}

