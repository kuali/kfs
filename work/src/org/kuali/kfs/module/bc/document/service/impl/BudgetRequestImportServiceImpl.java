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
package org.kuali.module.budget.service.impl;

import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.apache.commons.lang.StringUtils;
import org.kuali.core.service.BusinessObjectService;
import org.kuali.core.util.GlobalVariables;
import org.kuali.core.util.KualiInteger;
import org.kuali.kfs.KFSConstants;
import org.kuali.module.budget.BCConstants;
import org.kuali.module.budget.BCConstants.RequestImportFileType;
import org.kuali.module.budget.bo.BudgetConstructionRequestMove;
import org.kuali.module.budget.service.BudgetRequestImportService;
import org.kuali.module.budget.util.ImportRequestFileParsingHelper;

import com.lowagie.text.DocumentException;

public class BudgetRequestImportServiceImpl implements BudgetRequestImportService {
    private BusinessObjectService businessObjectService;
    
    /**
     * 
     * @see org.kuali.module.budget.service.BudgetRequestImportService#generatePdf(java.util.List, java.io.ByteArrayOutputStream)
     */
    public void generatePdf(List errorMessages, ByteArrayOutputStream baos) throws DocumentException {
        // TODO Auto-generated method stub
        
    }

    /**
     * 
     * @see org.kuali.module.budget.service.BudgetRequestImportService#processImportFile(java.io.InputStream, java.lang.String, java.lang.String, java.lang.String)
     */
    public List processImportFile(InputStream fileImportStream, String fieldSeperator, String textDelimiter, String fileType) throws IOException {
        List fileErrorList = new ArrayList();
        List<BudgetConstructionRequestMove> processedRequestList = new ArrayList<BudgetConstructionRequestMove>();
        String personUniversalIdentifier = GlobalVariables.getUserSession().getUniversalUser().getPersonUniversalIdentifier();
        
        //TODO: how to handle rollback?
        businessObjectService.deleteMatching(BudgetConstructionRequestMove.class, new HashMap());

        BudgetConstructionRequestMove budgetConstructionRequestMove = new BudgetConstructionRequestMove();
        
        BufferedReader fileReader = new BufferedReader(new InputStreamReader(fileImportStream));
        int currentLine = 1;
        while (fileReader.ready()) {
            String line = StringUtils.strip(fileReader.readLine());
            boolean isAnnualFile = ( fileType.equalsIgnoreCase(RequestImportFileType.ANNUAL.toString()) ) ? true : false;
            
            if (StringUtils.isNotBlank(line)) {
                budgetConstructionRequestMove = ImportRequestFileParsingHelper.parseLine(line, fieldSeperator, textDelimiter, isAnnualFile);
            }
            //check if there were errors parsing the line
            if (budgetConstructionRequestMove == null) {
                fileErrorList.add(BCConstants.REQUEST_IMPORT_FILE_PROCESSING_ERROR_MESSAGE_GENERIC + " " + currentLine + ".");
                return fileErrorList;
            }
            
            List<String> lineValidationErrors = validateLine(budgetConstructionRequestMove, currentLine, isAnnualFile);
            
            if ( lineValidationErrors.size() != 0) {
                fileErrorList.addAll(lineValidationErrors);
                return fileErrorList;
            }
            
            //set default values
            if (StringUtils.isBlank(budgetConstructionRequestMove.getSubAccountNumber())) {
                budgetConstructionRequestMove.setSubAccountNumber(KFSConstants.getDashSubAccountNumber());
            }
            
            if (StringUtils.isBlank(budgetConstructionRequestMove.getFinancialSubObjectCode())) {
                budgetConstructionRequestMove.setFinancialSubObjectCode(KFSConstants.getDashFinancialSubObjectCode());
            }
            
            //TODO: should i add all objects to list and only save at the end (in case one of the lines doesn't parse, to avoid rollback issues)? or save lines as they are parsed?
            //processedRequestList.add(budgetConstructionRequestMove);
            try {
                budgetConstructionRequestMove.setPersonUniversalIdentifier(personUniversalIdentifier);
                businessObjectService.save(budgetConstructionRequestMove);
            }
            catch (RuntimeException e) {
                fileErrorList.add("Move table store error, import aborted");
                return fileErrorList;
            }
            
            currentLine ++;
        }
        
        //TODO: update error codes and write to errorList
        
        return fileErrorList;
    }
    
    private List<String> validateLine(BudgetConstructionRequestMove budgetConstructionRequestMove, int lineNumber, boolean isAnnual) {
        List<String> errorList = new ArrayList<String>();
        
        if (StringUtils.isBlank(budgetConstructionRequestMove.getAccountNumber()) || budgetConstructionRequestMove.getAccountNumber().length() != 7 ) {
            errorList.add(BCConstants.REQUEST_IMPORT_FILE_PROCESSING_ERROR_MESSAGE_GENERIC + " " + lineNumber + ".");
            return errorList;
        }
        
        if ( StringUtils.isBlank(budgetConstructionRequestMove.getChartOfAccountsCode()) || budgetConstructionRequestMove.getChartOfAccountsCode().length() != 2 ) {
            errorList.add(BCConstants.REQUEST_IMPORT_FILE_PROCESSING_ERROR_MESSAGE_GENERIC + " " + lineNumber + ".");
            return errorList;
        }
        
        if ( StringUtils.isBlank(budgetConstructionRequestMove.getFinancialObjectCode()) || budgetConstructionRequestMove.getFinancialObjectCode().length() != 4 ) {
            errorList.add(BCConstants.REQUEST_IMPORT_FILE_PROCESSING_ERROR_MESSAGE_GENERIC + " " + lineNumber + ".");
            return errorList;
        }
        
        if (!StringUtils.isBlank(budgetConstructionRequestMove.getSubAccountNumber()) && budgetConstructionRequestMove.getSubAccountNumber().length() != 5 ) {
            errorList.add(BCConstants.REQUEST_IMPORT_FILE_PROCESSING_ERROR_MESSAGE_GENERIC + " " + lineNumber + ".");
            return errorList;
        }
        
        if (!StringUtils.isBlank(budgetConstructionRequestMove.getFinancialSubObjectCode()) && budgetConstructionRequestMove.getFinancialSubObjectCode().length() != 3 ) {
            errorList.add(BCConstants.REQUEST_IMPORT_FILE_PROCESSING_ERROR_MESSAGE_GENERIC + " " + lineNumber + ".");
            return errorList;
        }
        
        if (isAnnual) {
            if ( budgetConstructionRequestMove.getAccountLineAnnualBalanceAmount().compareTo(new KualiInteger(999999999)) >= 0 || budgetConstructionRequestMove.getAccountLineAnnualBalanceAmount().compareTo(new KualiInteger(-999999999)) <= 0 ) {
                errorList.add(BCConstants.REQUEST_IMPORT_FILE_PROCESSING_ERROR_MESSAGE_GENERIC + " " + lineNumber + ".");
                return errorList;
            }
        }
        
        if (!isAnnual) {
            
            if ( budgetConstructionRequestMove.getFinancialDocumentMonth1LineAmount().compareTo(new KualiInteger(999999999)) >= 0 || budgetConstructionRequestMove.getFinancialDocumentMonth1LineAmount().compareTo(new KualiInteger(-999999999)) <= 0 ) {
                errorList.add(BCConstants.REQUEST_IMPORT_FILE_PROCESSING_ERROR_MESSAGE_GENERIC + " " + lineNumber + ".");
                return errorList;
            }
            if ( budgetConstructionRequestMove.getFinancialDocumentMonth2LineAmount().compareTo(new KualiInteger(999999999)) >= 0 || budgetConstructionRequestMove.getFinancialDocumentMonth2LineAmount().compareTo(new KualiInteger(-999999999)) <= 0 ) {
                errorList.add(BCConstants.REQUEST_IMPORT_FILE_PROCESSING_ERROR_MESSAGE_GENERIC + " " + lineNumber + ".");
                return errorList;
            }
            if ( budgetConstructionRequestMove.getFinancialDocumentMonth3LineAmount().compareTo(new KualiInteger(999999999)) >= 0 || budgetConstructionRequestMove.getFinancialDocumentMonth3LineAmount().compareTo(new KualiInteger(-999999999)) <= 0 ) {
                errorList.add(BCConstants.REQUEST_IMPORT_FILE_PROCESSING_ERROR_MESSAGE_GENERIC + " " + lineNumber + ".");
                return errorList;
            }
            if ( budgetConstructionRequestMove.getFinancialDocumentMonth4LineAmount().compareTo(new KualiInteger(999999999)) >= 0 || budgetConstructionRequestMove.getFinancialDocumentMonth4LineAmount().compareTo(new KualiInteger(-999999999)) <= 0 ) {
                errorList.add(BCConstants.REQUEST_IMPORT_FILE_PROCESSING_ERROR_MESSAGE_GENERIC + " " + lineNumber + ".");
                return errorList;
            }
            if ( budgetConstructionRequestMove.getFinancialDocumentMonth5LineAmount().compareTo(new KualiInteger(999999999)) >= 0 || budgetConstructionRequestMove.getFinancialDocumentMonth5LineAmount().compareTo(new KualiInteger(-999999999)) <= 0 ) {
                errorList.add(BCConstants.REQUEST_IMPORT_FILE_PROCESSING_ERROR_MESSAGE_GENERIC + " " + lineNumber + ".");
                return errorList;
            }
            if ( budgetConstructionRequestMove.getFinancialDocumentMonth6LineAmount().compareTo(new KualiInteger(999999999)) >= 0 || budgetConstructionRequestMove.getFinancialDocumentMonth6LineAmount().compareTo(new KualiInteger(-999999999)) <= 0 ) {
                errorList.add(BCConstants.REQUEST_IMPORT_FILE_PROCESSING_ERROR_MESSAGE_GENERIC + " " + lineNumber + ".");
                return errorList;
            }
            if ( budgetConstructionRequestMove.getFinancialDocumentMonth7LineAmount().compareTo(new KualiInteger(999999999)) >= 0 || budgetConstructionRequestMove.getFinancialDocumentMonth7LineAmount().compareTo(new KualiInteger(-999999999)) <= 0 ) {
                errorList.add(BCConstants.REQUEST_IMPORT_FILE_PROCESSING_ERROR_MESSAGE_GENERIC + " " + lineNumber + ".");
                return errorList;
            }
            if ( budgetConstructionRequestMove.getFinancialDocumentMonth8LineAmount().compareTo(new KualiInteger(999999999)) >= 0 || budgetConstructionRequestMove.getFinancialDocumentMonth8LineAmount().compareTo(new KualiInteger(-999999999)) <= 0 ) {
                errorList.add(BCConstants.REQUEST_IMPORT_FILE_PROCESSING_ERROR_MESSAGE_GENERIC + " " + lineNumber + ".");
                return errorList;
            }
            if ( budgetConstructionRequestMove.getFinancialDocumentMonth9LineAmount().compareTo(new KualiInteger(999999999)) >= 0 || budgetConstructionRequestMove.getFinancialDocumentMonth9LineAmount().compareTo(new KualiInteger(-999999999)) <= 0 ) {
                errorList.add(BCConstants.REQUEST_IMPORT_FILE_PROCESSING_ERROR_MESSAGE_GENERIC + " " + lineNumber + ".");
                return errorList;
            }
            if ( budgetConstructionRequestMove.getFinancialDocumentMonth10LineAmount().compareTo(new KualiInteger(999999999)) >= 0 || budgetConstructionRequestMove.getFinancialDocumentMonth10LineAmount().compareTo(new KualiInteger(-999999999)) <= 0 ) {
                errorList.add(BCConstants.REQUEST_IMPORT_FILE_PROCESSING_ERROR_MESSAGE_GENERIC + " " + lineNumber + ".");
                return errorList;
            }
            if ( budgetConstructionRequestMove.getFinancialDocumentMonth11LineAmount().compareTo(new KualiInteger(999999999)) >= 0 || budgetConstructionRequestMove.getFinancialDocumentMonth11LineAmount().compareTo(new KualiInteger(-999999999)) <= 0 ) {
                errorList.add(BCConstants.REQUEST_IMPORT_FILE_PROCESSING_ERROR_MESSAGE_GENERIC + " " + lineNumber + ".");
                return errorList;
            }
            if ( budgetConstructionRequestMove.getFinancialDocumentMonth12LineAmount().compareTo(new KualiInteger(999999999)) >= 0 || budgetConstructionRequestMove.getFinancialDocumentMonth12LineAmount().compareTo(new KualiInteger(-999999999)) <= 0 ) {
                errorList.add(BCConstants.REQUEST_IMPORT_FILE_PROCESSING_ERROR_MESSAGE_GENERIC + " " + lineNumber + ".");
                return errorList;
            }
        }

        return errorList;
    }

    /**
     * Gets the business object service
     * 
     * @return
     */
    public BusinessObjectService getBusinessObjectService() {
        return businessObjectService;
    }
    
    /**
     * Sets the business object service
     * 
     * @param businessObjectService
     */
    public void setBusinessObjectService(BusinessObjectService businessObjectService) {
        this.businessObjectService = businessObjectService;
    }
 
}
