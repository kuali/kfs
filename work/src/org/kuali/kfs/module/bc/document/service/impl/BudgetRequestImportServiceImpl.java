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
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang.StringUtils;
import org.kuali.core.service.BusinessObjectService;
import org.kuali.core.util.GlobalVariables;
import org.kuali.core.util.KualiInteger;
import org.kuali.kfs.KFSConstants;
import org.kuali.kfs.KFSPropertyConstants;
import org.kuali.kfs.context.SpringContext;
import org.kuali.module.budget.BCConstants;
import org.kuali.module.budget.BCConstants.RequestImportFileType;
import org.kuali.module.budget.bo.BudgetConstructionRequestMove;
import org.kuali.module.budget.dao.ImportRequestDao;
import org.kuali.module.budget.service.BudgetRequestImportService;
import org.kuali.module.budget.util.ImportRequestFileParsingHelper;
import org.kuali.module.chart.bo.ObjectCode;
import org.kuali.module.chart.bo.SubObjCd;
import org.kuali.module.financial.service.UniversityDateService;
import org.kuali.module.labor.bo.LaborObject;
import org.kuali.module.labor.bo.PositionObjectBenefit;

import com.lowagie.text.Document;
import com.lowagie.text.DocumentException;
import com.lowagie.text.Paragraph;
import com.lowagie.text.pdf.PdfWriter;

public class BudgetRequestImportServiceImpl implements BudgetRequestImportService {
    private BusinessObjectService businessObjectService;
    private ImportRequestDao importRequestDao;
    
    /**
     * 
     * @see org.kuali.module.budget.service.BudgetRequestImportService#generatePdf(java.util.List, java.io.ByteArrayOutputStream)
     */
    public void generatePdf(List<String> errorMessages, ByteArrayOutputStream baos) throws DocumentException {
        Document document = new Document();
        PdfWriter.getInstance(document, baos);
        document.open();
        
        for(String error : errorMessages) {
            document.add(new Paragraph(error));
        }
        
        document.close();
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
            //since ObjectCode is not mapped, need to manually set object code and object type
            if (budgetConstructionRequestMove.getObjectType() == null || StringUtils.isEmpty(budgetConstructionRequestMove.getFinancialObjectTypeCode()) ) {
                if ( getObjectCode(budgetConstructionRequestMove) != null ) {
                    budgetConstructionRequestMove.setFinancialObjectCode(getObjectCode(budgetConstructionRequestMove).getCode());
                    budgetConstructionRequestMove.setObjectType(getObjectCode(budgetConstructionRequestMove).getFinancialObjectType());
                    budgetConstructionRequestMove.setFinancialObjectTypeCode(getObjectCode(budgetConstructionRequestMove).getFinancialObjectType().getCode());
                }
                
                
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
    
    /**
     * 
     * @see org.kuali.module.budget.service.BudgetRequestImportService#validateData()
     */
    public boolean validateData() {
        List<BudgetConstructionRequestMove> dataToValidateList = new ArrayList<BudgetConstructionRequestMove>(businessObjectService.findAll(BudgetConstructionRequestMove.class));
        boolean fileDataValid = true;
        
        for (BudgetConstructionRequestMove record : dataToValidateList) {
            record.refresh();
            boolean lineDataValid = true;
            
            if (importRequestDao.isNonBudgetedAccount(record)) {
                record.setRequestUpdateErrorCode(BCConstants.RequestImportDataValidationErrorFlag.DATA_VALIDATION_NO_BUDGETED_ACCOUNT_SUB_ACCOUNT_ERROR_CODE.getErrorCode());
                lineDataValid = false;
            }
            
            if (lineDataValid) {
                if (record.getAccount().isAccountClosedIndicator()) {
                    record.setRequestUpdateErrorCode(BCConstants.RequestImportDataValidationErrorFlag.DATA_VALIDATION_ACCOUNT_CLOSED_ERROR_CODE.getErrorCode());
                    lineDataValid = false;
                }
            }
            
            if (lineDataValid) {
                if (record.getAccount().isExpired()) {
                    record.setRequestUpdateErrorCode(BCConstants.RequestImportDataValidationErrorFlag.DATA_VALIDATION_ACCOUNT_EXPIRED_ERROR_CODE.getErrorCode());
                    lineDataValid = false;
                }
            }
            
            if (lineDataValid) {
                if (!record.getSubAccountNumber().equalsIgnoreCase(KFSConstants.getDashSubAccountNumber()) && !record.getSubAccount().isSubAccountActiveIndicator()) {
                    record.setRequestUpdateErrorCode(BCConstants.RequestImportDataValidationErrorFlag.DATA_VALIDATION_SUB_ACCOUNT_INACTIVE_ERROR_CODE.getErrorCode());
                    lineDataValid = false;
                }
            }
            
            //null object type
            if (lineDataValid) {
                if (record.getObjectType() == null) {
                    record.setRequestUpdateErrorCode(BCConstants.RequestImportDataValidationErrorFlag.DATA_VALIDATION_OBJECT_TYPE_NULL_ERROR_CODE.getErrorCode());
                    lineDataValid = false;
                }
            }
            
            //invalid object type
            if (lineDataValid) {
                String code = record.getFinancialObjectTypeCode();
                if ( !code.equalsIgnoreCase("EX") &&
                        !code.equalsIgnoreCase("ES") &&
                        !code.equalsIgnoreCase("EE") &&
                        !code.equalsIgnoreCase("IN") &&
                        !code.equalsIgnoreCase("IC") &&
                        !code.equalsIgnoreCase("CH")) {
                    record.setRequestUpdateErrorCode(BCConstants.RequestImportDataValidationErrorFlag.DATA_VALIDATION_OBJECT_TYPE_INVALID_ERROR_CODE.getErrorCode());
                    lineDataValid = false;
                }
            }
            
            //inactive object code
            //TODO: need to map object code
            if (lineDataValid) {
                if (getObjectCode(record) != null && !getObjectCode(record).isActive()) {
                    record.setRequestUpdateErrorCode(BCConstants.RequestImportDataValidationErrorFlag.DATA_VALIDATION_OBJECT_TYPE_INACTIVE_ERROR_CODE.getErrorCode());
                    lineDataValid = false;
                }
            }
            
            //TODO: compensation object codes COMP
            if (lineDataValid) {
                LaborObject laborObjectCode = getLaborObject(record);
                if (laborObjectCode != null && (laborObjectCode.isDetailPositionRequiredIndicator() || laborObjectCode.getFinancialObjectFringeOrSalaryCode().equals("F")) ) {
                    record.setRequestUpdateErrorCode(BCConstants.RequestImportDataValidationErrorFlag.DATA_VALIDATION_COMPENSATION_OBJECT_CODE_ERROR_CODE.getErrorCode());
                    lineDataValid = false;
                }
            }
            
            //TODO: no wage accounts CMPA
            if (lineDataValid) {
                LaborObject laborObject = getLaborObject(record);
                if (!record.getAccount().getSubFundGroup().isSubFundGroupWagesIndicator() && laborObject != null ) {
                    record.setRequestUpdateErrorCode(BCConstants.RequestImportDataValidationErrorFlag.DATA_VALIDATION_NO_WAGE_ACCOUNT_ERROR_CODE.getErrorCode());
                    lineDataValid = false;
                }
            }
            
            //invalid sub-object code NOSO
            if (lineDataValid) {
                if (!record.getFinancialSubObjectCode().equalsIgnoreCase(KFSConstants.getDashFinancialSubObjectCode()) && getSubObjectCode(record) != null) {
                    record.setRequestUpdateErrorCode(BCConstants.RequestImportDataValidationErrorFlag.DATA_VALIDATION_SUB_OBJECT_INVALID_ERROR_CODE.getErrorCode());
                    lineDataValid = false;
                }
            }
            //inactive sub-object code
            if (lineDataValid) {
                if (record.getFinancialSubObjectCode().equalsIgnoreCase(KFSConstants.getDashFinancialSubObjectCode()) && getSubObjectCode(record) != null && !getSubObjectCode(record).isFinancialSubObjectActiveIndicator()) {
                    record.setRequestUpdateErrorCode(BCConstants.RequestImportDataValidationErrorFlag.DATA_VALIDATION_SUB_OBJECT_INACTIVE_ERROR_CODE.getErrorCode());
                    lineDataValid = false;
                }
            }
            
            if (!lineDataValid) fileDataValid = false;
            
            businessObjectService.save(record);
        }
        
        return fileDataValid;
    }
    
    /**
     * 
     * @see org.kuali.module.budget.service.BudgetRequestImportService#getImportRequestDao()
     */
    public ImportRequestDao getImportRequestDao() {
        return this.importRequestDao;
    }
    
    /**
     * 
     * @see org.kuali.module.budget.service.BudgetRequestImportService#setImportRequestDao(org.kuali.module.budget.dao.ImportRequestDao)
     */
    public void setImportRequestDao(ImportRequestDao dao) {
        this.importRequestDao = dao;
        
    }
    
    //TODO: is this correct?
    //should i use SpringContext.getBean(LaborModuleService.class) to find the labor object code? if so, what method?
    private boolean isLaborObjectCode(Integer fiscalYear, String chartOfAccountsCode, String financialObjectCode) {
        Map searchCriteria = new HashMap();

        searchCriteria.put(KFSPropertyConstants.UNIVERSITY_FISCAL_YEAR, fiscalYear);
        searchCriteria.put(KFSPropertyConstants.CHART_OF_ACCOUNTS_CODE, chartOfAccountsCode);
        searchCriteria.put(KFSPropertyConstants.FINANCIAL_OBJECT_CODE, financialObjectCode);
        
        Collection laborObjectBenefits = getBusinessObjectService().findMatching(PositionObjectBenefit.class, searchCriteria);
        
        if (laborObjectBenefits != null && !laborObjectBenefits.isEmpty()) return true;
        
        return false;
    }
    
    //TODO: is this the correct way to find the labor object for a BudgetConstructionRequestMove object?
    private LaborObject getLaborObject(BudgetConstructionRequestMove record) {
        Map searchCriteria = new HashMap();

        searchCriteria.put(KFSPropertyConstants.UNIVERSITY_FISCAL_YEAR, SpringContext.getBean(UniversityDateService.class).getCurrentFiscalYear());
        searchCriteria.put(KFSPropertyConstants.CHART_OF_ACCOUNTS_CODE, record.getChartOfAccountsCode());
        searchCriteria.put(KFSPropertyConstants.FINANCIAL_OBJECT_CODE, record.getFinancialObjectCode());
        
        List<LaborObject> laborObjectList = new ArrayList<LaborObject> (getBusinessObjectService().findMatching(LaborObject.class, searchCriteria));
        
        if (laborObjectList.size() == 1) return laborObjectList.get(0);
        
        return null;
    }
    
    //TODO: is this the correct way to find the object code?
    private ObjectCode getObjectCode(BudgetConstructionRequestMove record) {
        Map searchCriteria = new HashMap();

        searchCriteria.put(KFSPropertyConstants.UNIVERSITY_FISCAL_YEAR, SpringContext.getBean(UniversityDateService.class).getCurrentFiscalYear());
        searchCriteria.put(KFSPropertyConstants.CHART_OF_ACCOUNTS_CODE, record.getChartOfAccountsCode());
        searchCriteria.put(KFSPropertyConstants.FINANCIAL_OBJECT_CODE, record.getFinancialObjectCode());
        
        List<ObjectCode> objectList = new ArrayList<ObjectCode> (getBusinessObjectService().findMatching(ObjectCode.class, searchCriteria));
        
        if (objectList.size() == 1) return objectList.get(0);
        
        return null;
    }
    
    private SubObjCd getSubObjectCode(BudgetConstructionRequestMove record) {
        Map searchCriteria = new HashMap();

        searchCriteria.put(KFSPropertyConstants.UNIVERSITY_FISCAL_YEAR, SpringContext.getBean(UniversityDateService.class).getCurrentFiscalYear());
        searchCriteria.put(KFSPropertyConstants.CHART_OF_ACCOUNTS_CODE, record.getChartOfAccountsCode());
        searchCriteria.put(KFSPropertyConstants.FINANCIAL_OBJECT_CODE, record.getFinancialObjectCode());
        searchCriteria.put(KFSPropertyConstants.ACCOUNT_NUMBER, record.getAccountNumber());
        searchCriteria.put(KFSPropertyConstants.FINANCIAL_SUB_OBJECT_CODE, record.getFinancialSubObjectCode());
        
        List<SubObjCd> objectList = new ArrayList<SubObjCd> (getBusinessObjectService().findMatching(SubObjCd.class, searchCriteria));
        
        if (objectList.size() == 1) return objectList.get(0);
        
        return null;
    }
}
