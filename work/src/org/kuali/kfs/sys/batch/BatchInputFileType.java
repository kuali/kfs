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

