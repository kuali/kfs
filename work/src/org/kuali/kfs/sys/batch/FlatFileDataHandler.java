/*
 * Copyright 2012 The Kuali Foundation.
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
