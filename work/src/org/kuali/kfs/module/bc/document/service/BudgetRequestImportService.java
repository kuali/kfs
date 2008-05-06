/*
 * Copyright 2008 The Kuali Foundation.
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
package org.kuali.module.budget.service;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.List;

import org.kuali.core.bo.user.UniversalUser;
import org.kuali.core.service.DictionaryValidationService;
import org.kuali.module.budget.dao.ImportRequestDao;

import com.lowagie.text.DocumentException;

/**
 * Describes services related to budget construction request import
 * 
 */
public interface BudgetRequestImportService {
    
    /**
     * Takes an import request file (either monthly or annual) and creates the BudgetConstructionMove objects for each line of the file.
     * If any errors are encounterd, file processing stops.
     * If no errors are encountered an empty list is returned.
     * 
     * @param fileImportStream
     * @param fieldSeperator
     * @param textDelimiter
     * @param fileType
     * @return list of errors encountered during file processing
     * @throws IOException
     */
    public List processImportFile (InputStream fileImportStream, String personUniversalIdentifier, String fieldSeperator, String textDelimiter, String fileType, Integer budgetYear) throws IOException;
    
    /**
     * Generates the log file
     * 
     * @param errorMessages
     * @param baos
     * @throws DocumentException
     */
    public void generatePdf(List<String> errorMessages, ByteArrayOutputStream baos) throws DocumentException;
    
    /**
     * Checks the imported request records for valid data. Sets error codes on invalid records.
     * 
     * @return true if no data validation errors were found. false otherwise
     * 
     */
    public List<String> validateData(Integer budgetYear);
    
    /**
     * Loads all budget request records that do not have error codes
     * 
     * @return
     */
    public List<String> loadBudget(UniversalUser user, String fileType, Integer budgetYear) throws Exception;
    
    /**
     * Gets ImportRequestDao
     * 
     * @return
     */
    public ImportRequestDao getImportRequestDao();
    
    /**
     * Sets the ImportRequestDao
     * 
     * @param dao
     */
    public void setImportRequestDao(ImportRequestDao dao);
    
    /**
     * Sets permissionService
     * 
     * @param permissionService
     */
    public void setPermissionService(PermissionService permissionService);
    
    /**
     * Sets the dictionaryValidationService
     * 
     * @param dictionaryValidationService
     */
    public void setDictionaryValidationService(DictionaryValidationService dictionaryValidationService);
    
}
