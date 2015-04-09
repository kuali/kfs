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

/**
 * The interface for a FlatFileDataValidator.  Implementations will have code which validates the parsed contents of the flat file.
 */
public interface FlatFileDataHandler {

	/**
     * Performs specific validation on the parsed file contents. If errors were found, method will return false and
     * GlobalVariables.errorMap will contain the error message. If no errors were encountered the method will return true.
     *
     * @param parsedFileContents - object populated with the uploaded file contents
     */
	public abstract boolean validate(Object parsedFileContents);

	 /**
     * Invokes optional processing of file after validation
     *
     * @param fileName name of the file
     * @param parsedFileContents objects populated with file contents
     */
    public abstract void process(String fileName, Object parsedFileContents);

    /**
     * Returns the name of an uploaded file.
     */
    public  String getFileName(String principalName, Object parsedFileContents, String fileUserIdentifier) ;


}
