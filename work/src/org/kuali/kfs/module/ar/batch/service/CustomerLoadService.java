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
package org.kuali.kfs.module.ar.batch.service;

import java.util.List;

import org.kuali.kfs.module.ar.batch.report.CustomerLoadFileResult;
import org.kuali.kfs.module.ar.batch.vo.CustomerDigesterVO;
import org.kuali.kfs.sys.batch.BatchInputFileType;
import org.kuali.kfs.sys.batch.InitiateDirectory;
import org.kuali.rice.kns.document.MaintenanceDocument;

public interface CustomerLoadService extends InitiateDirectory{

    /**
     * Validates and parses all files ready to go in the batch staging area.
     *
     * @return True if no errors were encountered, False otherwise.
     */
    public boolean loadFiles();

    /**
     * Validates and parses the file identified by the given files name. If successful, parsed entries are stored.
     *
     * These parameters are used for returning values back to its caller
     * reporter, routedDocumentNumbers, failedDocumentNumbers
     *
     * @param fileName Name of file to be uploaded and processed.
     * @param reporter
     * @param batchInputFileType batchInputFileType
     * @param routedDocumentNumbers list of routed doc numbers through workflow
     * @param failedDocumentNumbers list of failed doc numbers through workflow
     * @return True if the file load and store was successful, false otherwise.
     */
    public boolean loadFile(String fileName, CustomerLoadFileResult reporter, BatchInputFileType batchInputFileType, List<String> routedDocumentNumbers, List<String> failedDocumentNumbers);

    /**
     *
     * Performs specific validation on the parsed file contents. If errors were found, method will return false and
     * GlobalVariables.errorMap will contain the error message. If no errors were encountered the method will return true.
     *
     * @param customerUploads A List of CustomerDigesterVO objects, that are the processed
     *        results of the uploaded files.
     * @return True if no errors were encountered, False otherwise.
     */
    public boolean validate(List<CustomerDigesterVO> customerUploads);

    /**
     *
     * Performs specific validation on the parsed file contents. If errors were found, method will return false and
     * GlobalVariables.errorMap will contain the error message. If no errors were encountered the method will return true.
     *
     * @param customerUploads A List of CustomerDigesterVO objects, that are the processed
     *        results of the uploaded files.
     * @param customerMaintDocs A list of the customerMaintDocs that are returned by the validateAndPrepare method.  A valid list
     *        should be passed in, and MaintDocs will be added to it.
     * @return True if no errors were encountered, False otherwise.
     */
    public boolean validateAndPrepare(List<CustomerDigesterVO> customerUploads, List<MaintenanceDocument> customerMaintDocs, boolean useGlobalMessageMap);

    /**
     *
     * Provide a file name generation for the CustomerInputFileType(xml or csv)
     *
     * @return file name
     */
    public String getFileName(String principalName, String fileUserIdentifer, String prefix, String delim);
}

