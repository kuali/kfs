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
package org.kuali.kfs.module.bc.document.service;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.List;

import org.kuali.rice.kim.api.identity.Person;

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
    public List processImportFile (InputStream fileImportStream, String principalId, String fieldSeperator, String textDelimiter, String fileType, Integer budgetYear) throws IOException;
    
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
    public List<String> validateData(Integer budgetYear, String principalId);
    
    /**
     * Loads all budget request records that do not have error codes
     * 
     * @return
     */
    public List<String> loadBudget(Person user, String fileType, Integer budgetYear) throws Exception;

}

