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
import java.util.Map;

import org.apache.commons.lang.StringUtils;
import org.kuali.core.bo.user.UniversalUser;
import org.kuali.core.service.BusinessObjectService;
import org.kuali.core.util.KualiInteger;
import org.kuali.kfs.KFSConstants;
import org.kuali.kfs.KFSPropertyConstants;
import org.kuali.kfs.context.SpringContext;
import org.kuali.module.budget.BCConstants;
import org.kuali.module.budget.BCConstants.RequestImportFileType;
import org.kuali.module.budget.bo.BudgetConstructionFundingLock;
import org.kuali.module.budget.bo.BudgetConstructionHeader;
import org.kuali.module.budget.bo.BudgetConstructionMonthly;
import org.kuali.module.budget.bo.BudgetConstructionRequestMove;
import org.kuali.module.budget.bo.PendingBudgetConstructionGeneralLedger;
import org.kuali.module.budget.dao.ImportRequestDao;
import org.kuali.module.budget.service.BenefitsCalculationService;
import org.kuali.module.budget.service.BudgetRequestImportService;
import org.kuali.module.budget.service.PermissionService;
import org.kuali.module.budget.util.ImportRequestFileParsingHelper;
import org.kuali.module.chart.bo.ObjectCode;
import org.kuali.module.chart.bo.Org;
import org.kuali.module.chart.bo.SubObjCd;
import org.kuali.module.labor.bo.LaborObject;
import org.springframework.transaction.annotation.Transactional;

import com.lowagie.text.Document;
import com.lowagie.text.DocumentException;
import com.lowagie.text.Paragraph;
import com.lowagie.text.pdf.PdfWriter;
/**
 * Contains services relevent to the budget construction import request process
 * This class...
 */
@Transactional
public class BudgetRequestImportServiceImpl implements BudgetRequestImportService {
    private BusinessObjectService businessObjectService;
    private ImportRequestDao importRequestDao;
    private PermissionService permissionService;
    
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
    public List processImportFile(InputStream fileImportStream, String personUniversalIdentifier, String fieldSeperator, String textDelimiter, String fileType, Integer budgetYear) throws IOException {
        List fileErrorList = new ArrayList();
        List<BudgetConstructionRequestMove> processedRequestList = new ArrayList<BudgetConstructionRequestMove>();
        
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
                //clean out table since file processing has stopped
                businessObjectService.deleteMatching(BudgetConstructionRequestMove.class, new HashMap());
                return fileErrorList;
            }
            
            List<String> lineValidationErrors = validateLine(budgetConstructionRequestMove, currentLine, isAnnualFile);
            
            if ( lineValidationErrors.size() != 0) {
                fileErrorList.addAll(lineValidationErrors);
                //clean out table since file processing has stopped
                businessObjectService.deleteMatching(BudgetConstructionRequestMove.class, new HashMap());
                return fileErrorList;
            }
            
            //set default values
            if (StringUtils.isBlank(budgetConstructionRequestMove.getSubAccountNumber())) {
                budgetConstructionRequestMove.setSubAccountNumber(KFSConstants.getDashSubAccountNumber());
            }
            
            if (StringUtils.isBlank(budgetConstructionRequestMove.getFinancialSubObjectCode())) {
                budgetConstructionRequestMove.setFinancialSubObjectCode(KFSConstants.getDashFinancialSubObjectCode());
            }
            if ( StringUtils.isEmpty(budgetConstructionRequestMove.getFinancialObjectTypeCode()) ) {
                if ( getObjectCode(budgetConstructionRequestMove, budgetYear) != null ) {
                    String objectTypeCode = getObjectCode(budgetConstructionRequestMove, budgetYear).getFinancialObjectTypeCode();
                    budgetConstructionRequestMove.setFinancialObjectTypeCode(objectTypeCode);
                }
            }
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
    public List<String> validateData(Integer budgetYear) {
        List<BudgetConstructionRequestMove> dataToValidateList = new ArrayList<BudgetConstructionRequestMove>(businessObjectService.findAll(BudgetConstructionRequestMove.class));
        boolean fileDataValid = true;
        List<String> errorMessages = new ArrayList<String>();
        
        for (BudgetConstructionRequestMove record : dataToValidateList) {
            record.refresh();
            boolean lineDataValid = true;
            String errorMessage = record.getErrorLinePrefixForLogFile();
            if (importRequestDao.getHeaderRecord(record, budgetYear) == null) {
                record.setRequestUpdateErrorCode(BCConstants.RequestImportErrorCode.DATA_VALIDATION_NO_BUDGETED_ACCOUNT_SUB_ACCOUNT_ERROR_CODE.getErrorCode());
                errorMessage += BCConstants.RequestImportErrorCode.DATA_VALIDATION_NO_BUDGETED_ACCOUNT_SUB_ACCOUNT_ERROR_CODE.getMessage();
                lineDataValid = false;
            }
            
            if (lineDataValid) {
                if (record.getAccount().isAccountClosedIndicator()) {
                    record.setRequestUpdateErrorCode(BCConstants.RequestImportErrorCode.DATA_VALIDATION_ACCOUNT_CLOSED_ERROR_CODE.getErrorCode());
                    errorMessage += BCConstants.RequestImportErrorCode.DATA_VALIDATION_ACCOUNT_CLOSED_ERROR_CODE.getMessage();
                    lineDataValid = false;
                }
            }
            
            if (lineDataValid) {
                if (record.getAccount().isExpired()) {
                    record.setRequestUpdateErrorCode(BCConstants.RequestImportErrorCode.DATA_VALIDATION_ACCOUNT_EXPIRED_ERROR_CODE.getErrorCode());
                    errorMessage += BCConstants.RequestImportErrorCode.DATA_VALIDATION_ACCOUNT_EXPIRED_ERROR_CODE.getMessage();
                    lineDataValid = false;
                }
            }
            
            if (lineDataValid) {
                if (!record.getSubAccountNumber().equalsIgnoreCase(KFSConstants.getDashSubAccountNumber()) && !record.getSubAccount().isSubAccountActiveIndicator()) {
                    record.setRequestUpdateErrorCode(BCConstants.RequestImportErrorCode.DATA_VALIDATION_SUB_ACCOUNT_INACTIVE_ERROR_CODE.getErrorCode());
                    errorMessage += BCConstants.RequestImportErrorCode.DATA_VALIDATION_SUB_ACCOUNT_INACTIVE_ERROR_CODE.getMessage();
                    lineDataValid = false;
                }
            }
            
            //null object type
            if (lineDataValid) {
                if ( StringUtils.isBlank(record.getFinancialObjectTypeCode()) ) {
                    record.setRequestUpdateErrorCode(BCConstants.RequestImportErrorCode.DATA_VALIDATION_OBJECT_TYPE_NULL_ERROR_CODE.getErrorCode());
                    errorMessage += BCConstants.RequestImportErrorCode.DATA_VALIDATION_OBJECT_TYPE_NULL_ERROR_CODE.getMessage();
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
                    record.setRequestUpdateErrorCode(BCConstants.RequestImportErrorCode.DATA_VALIDATION_OBJECT_TYPE_INVALID_ERROR_CODE.getErrorCode());
                    errorMessage += BCConstants.RequestImportErrorCode.DATA_VALIDATION_OBJECT_TYPE_INVALID_ERROR_CODE.getMessage();
                    lineDataValid = false;
                }
            }
            
            //inactive object code
            if (lineDataValid) {
                if (getObjectCode(record, budgetYear) != null && !getObjectCode(record, budgetYear).isActive()) {
                    record.setRequestUpdateErrorCode(BCConstants.RequestImportErrorCode.DATA_VALIDATION_OBJECT_CODE_INACTIVE_ERROR_CODE.getErrorCode());
                    errorMessage += BCConstants.RequestImportErrorCode.DATA_VALIDATION_OBJECT_CODE_INACTIVE_ERROR_CODE.getMessage();
                    lineDataValid = false;
                }
            }
            
            //compensation object codes COMP
            if (lineDataValid) {
                LaborObject laborObjectCode = getLaborObject(record, budgetYear);
                if (laborObjectCode != null && (laborObjectCode.isDetailPositionRequiredIndicator() || laborObjectCode.getFinancialObjectFringeOrSalaryCode().equals("F")) ) {
                    record.setRequestUpdateErrorCode(BCConstants.RequestImportErrorCode.DATA_VALIDATION_COMPENSATION_OBJECT_CODE_ERROR_CODE.getErrorCode());
                    errorMessage += BCConstants.RequestImportErrorCode.DATA_VALIDATION_COMPENSATION_OBJECT_CODE_ERROR_CODE.getMessage();
                    lineDataValid = false;
                }
            }
            
            //no wage accounts CMPA
            if (lineDataValid) {
                LaborObject laborObject = getLaborObject(record, budgetYear);
                if (!record.getAccount().getSubFundGroup().isSubFundGroupWagesIndicator() && laborObject != null ) {
                    record.setRequestUpdateErrorCode(BCConstants.RequestImportErrorCode.DATA_VALIDATION_NO_WAGE_ACCOUNT_ERROR_CODE.getErrorCode());
                    errorMessage += BCConstants.RequestImportErrorCode.DATA_VALIDATION_NO_WAGE_ACCOUNT_ERROR_CODE.getMessage();
                    lineDataValid = false;
                }
            }
            
            //invalid sub-object code NOSO
            if (lineDataValid) {
                if (!record.getFinancialSubObjectCode().equalsIgnoreCase(KFSConstants.getDashFinancialSubObjectCode()) && getSubObjectCode(record, budgetYear) != null) {
                    record.setRequestUpdateErrorCode(BCConstants.RequestImportErrorCode.DATA_VALIDATION_SUB_OBJECT_INVALID_ERROR_CODE.getErrorCode());
                    errorMessage += BCConstants.RequestImportErrorCode.DATA_VALIDATION_SUB_OBJECT_INVALID_ERROR_CODE.getMessage();
                    lineDataValid = false;
                }
            }
            //inactive sub-object code
            if (lineDataValid) {
                if (!record.getFinancialSubObjectCode().equalsIgnoreCase(KFSConstants.getDashFinancialSubObjectCode()) && getSubObjectCode(record, budgetYear) != null && !getSubObjectCode(record, budgetYear).isFinancialSubObjectActiveIndicator()) {
                    record.setRequestUpdateErrorCode(BCConstants.RequestImportErrorCode.DATA_VALIDATION_SUB_OBJECT_INACTIVE_ERROR_CODE.getErrorCode());
                    errorMessage += BCConstants.RequestImportErrorCode.DATA_VALIDATION_SUB_OBJECT_INACTIVE_ERROR_CODE.getMessage();
                    lineDataValid = false;
                }
            }
            
            if (!lineDataValid) {
                fileDataValid = false;
                errorMessages.add(errorMessage);
            }
            
            businessObjectService.save(record);
        }
        
        return errorMessages;
    }
    
    /**
     * 
     * @see org.kuali.module.budget.service.BudgetRequestImportService#loadBudget()
     */
    public List<String> loadBudget(UniversalUser user, String fileType, Integer budgetYear) throws Exception {
        List<BudgetConstructionRequestMove> recordsToLoad = importRequestDao.findAllNonErrorCodeRecords();
        List<String> errorMessages = new ArrayList<String>();
        HashMap<String, BudgetConstructionRequestMove> recordMap = new HashMap<String, BudgetConstructionRequestMove>();
        for(BudgetConstructionRequestMove recordToLoad : recordsToLoad) {
            BudgetConstructionHeader header = importRequestDao.getHeaderRecord(recordToLoad, budgetYear);
            
            if (recordMap.containsKey(recordToLoad.getSubAccountingString())) {
                BudgetConstructionRequestMove temp = recordMap.get(recordToLoad.getSubAccountingString());
                
                recordToLoad.setHasAccess(temp.getHasAccess());
                recordToLoad.setHasLock(temp.getHasLock());
                recordToLoad.setRequestUpdateErrorCode(temp.getRequestUpdateErrorCode());
            } else {
                if (recordToLoad.getAccount().getAccountFiscalOfficerUser().getPersonUniversalIdentifier().equals(user.getPersonUniversalIdentifier()) ||
                        recordToLoad.getAccount().getAccountManagerUser().getPersonUniversalIdentifier().equals(user.getPersonUniversalIdentifier())) {
                    
                } else if (header != null && header.getOrganizationLevelCode() != 0 &&
                        permissionService.isOrgReviewApprover(user.getPersonUserIdentifier().toLowerCase(), 
                                header.getOrganizationLevelChartOfAccountsCode(), 
                                header.getOrganizationLevelOrganizationCode())) {
                        recordToLoad.setHasAccess(true);
                } else {
                    recordToLoad.setRequestUpdateErrorCode(BCConstants.RequestImportErrorCode.UPDATE_ERROR_CODE_NO_ACCESS_TO_BUDGET_ACCOUNT.getErrorCode());
                    errorMessages.add(recordToLoad.getErrorLinePrefixForLogFile() + BCConstants.RequestImportErrorCode.UPDATE_ERROR_CODE_NO_ACCESS_TO_BUDGET_ACCOUNT.getMessage());
                }
                
                if (recordToLoad.getHasAccess() && header.getBudgetLockUserIdentifier() == null) {
                    List<BudgetConstructionFundingLock> locks = findBudgetLocks(recordToLoad, budgetYear);
                    if (locks.isEmpty()) {
                        header.setBudgetLockUserIdentifier(user.getPersonUserIdentifier());
                        businessObjectService.save(header);
                        recordToLoad.setHasLock(true);
                    } else {
                        recordToLoad.setRequestUpdateErrorCode(BCConstants.RequestImportErrorCode.UPDATE_ERROR_CODE_BUDGET_ACCOUNT_LOCKED.getErrorCode());
                        errorMessages.add(recordToLoad.getErrorLinePrefixForLogFile() + BCConstants.RequestImportErrorCode.UPDATE_ERROR_CODE_BUDGET_ACCOUNT_LOCKED.getMessage());
                    }
                }
                
                recordMap.put(recordToLoad.getSubAccountingString(), recordToLoad);
            }
            
            if (recordToLoad.getHasAccess() && 
                    recordToLoad.getHasLock() &&
                    StringUtils.isBlank(recordToLoad.getRequestUpdateErrorCode())) {
                String updateBudgetAmountErrorMessage = updateBudgetAmounts(fileType, recordToLoad, header, budgetYear);
                if ( !StringUtils.isEmpty(updateBudgetAmountErrorMessage) ) errorMessages.add(recordToLoad.getErrorLinePrefixForLogFile() + updateBudgetAmountErrorMessage);
            }
            
            businessObjectService.save(recordToLoad);
        }
        
        for (String key : recordMap.keySet()) {
            BudgetConstructionRequestMove record = recordMap.get(key);
            BudgetConstructionHeader header = importRequestDao.getHeaderRecord(record, budgetYear);
            if (record.getHasAccess() && record.getHasLock() && StringUtils.isBlank(record.getRequestUpdateErrorCode())) {
                udpateBenefits(fileType, record, header);
            }
            
            if (record.getHasLock() && header != null) {
                header.setBudgetLockUserIdentifier(null);
                businessObjectService.save(header);
            }
        }
        
        deleteBudgetConstructionMoveRecords(user.getPersonUniversalIdentifier());
        return errorMessages;
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
    /**
     * 
     * 
     * @return permissionService
     */
    public PermissionService getPermissionService() {
        return permissionService;
    }

    /**
     * Sets permissionService
     * 
     * @param permissionService
     */
    public void setPermissionService(PermissionService permissionService) {
        this.permissionService = permissionService;
    }

    /**
     * updates budget amounts
     * 
     * @param fileType
     * @param importLine
     * @return error message
     */
    private String updateBudgetAmounts(String fileType, BudgetConstructionRequestMove importLine, BudgetConstructionHeader header, Integer budgetYear) {
        String errorMessage = "";
        
        if (fileType.equalsIgnoreCase(BCConstants.RequestImportFileType.ANNUAL.toString())) {
            List<BudgetConstructionMonthly> monthlyRecords = getMonthlyRecords(importLine, header);
            
            if ( !monthlyRecords.isEmpty() ) {
                importLine.setRequestUpdateErrorCode(BCConstants.RequestImportErrorCode.UPDATE_ERROR_CODE_MONTHLY_BUDGET_DELETED.getErrorCode());
                errorMessage = BCConstants.RequestImportErrorCode.UPDATE_ERROR_CODE_MONTHLY_BUDGET_DELETED.getMessage();
                for (BudgetConstructionMonthly monthlyRecord : monthlyRecords) {
                    businessObjectService.delete(monthlyRecord);
                }
            }  
            
            PendingBudgetConstructionGeneralLedger pendingEntry = new PendingBudgetConstructionGeneralLedger();
            
            pendingEntry.setAccountLineAnnualBalanceAmount(importLine.getAccountLineAnnualBalanceAmount());
            //TODO: does this need to be set. should I use header value?
            pendingEntry.setDocumentNumber(header.getDocumentNumber());
            pendingEntry.setUniversityFiscalYear(budgetYear);
            pendingEntry.setChartOfAccountsCode(importLine.getChartOfAccountsCode());
            pendingEntry.setAccountNumber(importLine.getAccountNumber());
            pendingEntry.setSubAccountNumber(importLine.getSubAccountNumber());
            pendingEntry.setFinancialObjectCode(importLine.getFinancialObjectCode());
            pendingEntry.setFinancialSubObjectCode(importLine.getFinancialSubObjectCode());
            pendingEntry.setFinancialBalanceTypeCode("BB");
            pendingEntry.setFinancialObjectTypeCode(importLine.getFinancialObjectTypeCode());
            
            PendingBudgetConstructionGeneralLedger alreadyExistingPendingEntry = (PendingBudgetConstructionGeneralLedger) businessObjectService.retrieve(pendingEntry);
            if ( alreadyExistingPendingEntry != null ) {
                alreadyExistingPendingEntry.setAccountLineAnnualBalanceAmount(pendingEntry.getAccountLineAnnualBalanceAmount());
                businessObjectService.save(alreadyExistingPendingEntry);
            } else {
                businessObjectService.save(pendingEntry);
            }
            
            businessObjectService.save(importLine);
            
        } else if ( fileType.equalsIgnoreCase(BCConstants.RequestImportFileType.MONTHLY.toString()) ) {
            BudgetConstructionMonthly monthlyEntry = new BudgetConstructionMonthly();
            //TODO: does this need to be set. should I use header value?
            monthlyEntry.setDocumentNumber(header.getDocumentNumber());
            monthlyEntry.setUniversityFiscalYear(budgetYear);
            monthlyEntry.setChartOfAccountsCode(importLine.getChartOfAccountsCode());
            monthlyEntry.setAccountNumber(importLine.getAccountNumber());
            monthlyEntry.setSubAccountNumber(importLine.getSubAccountNumber());
            monthlyEntry.setFinancialObjectCode(importLine.getFinancialObjectCode());
            monthlyEntry.setFinancialSubObjectCode(importLine.getFinancialSubObjectCode());
            monthlyEntry.setFinancialBalanceTypeCode("BB");
            monthlyEntry.setFinancialObjectTypeCode(importLine.getFinancialObjectTypeCode());
            monthlyEntry.setFinancialDocumentMonth1LineAmount(importLine.getFinancialDocumentMonth1LineAmount());
            monthlyEntry.setFinancialDocumentMonth2LineAmount(importLine.getFinancialDocumentMonth2LineAmount());
            monthlyEntry.setFinancialDocumentMonth3LineAmount(importLine.getFinancialDocumentMonth3LineAmount());
            monthlyEntry.setFinancialDocumentMonth4LineAmount(importLine.getFinancialDocumentMonth4LineAmount());
            monthlyEntry.setFinancialDocumentMonth5LineAmount(importLine.getFinancialDocumentMonth5LineAmount());
            monthlyEntry.setFinancialDocumentMonth6LineAmount(importLine.getFinancialDocumentMonth6LineAmount());
            monthlyEntry.setFinancialDocumentMonth7LineAmount(importLine.getFinancialDocumentMonth7LineAmount());
            monthlyEntry.setFinancialDocumentMonth8LineAmount(importLine.getFinancialDocumentMonth8LineAmount());
            monthlyEntry.setFinancialDocumentMonth9LineAmount(importLine.getFinancialDocumentMonth9LineAmount());
            monthlyEntry.setFinancialDocumentMonth10LineAmount(importLine.getFinancialDocumentMonth10LineAmount());
            monthlyEntry.setFinancialDocumentMonth11LineAmount(importLine.getFinancialDocumentMonth11LineAmount());
            monthlyEntry.setFinancialDocumentMonth12LineAmount(importLine.getFinancialDocumentMonth12LineAmount());
            
            BudgetConstructionMonthly alreadyExistingMonthlyEntry = (BudgetConstructionMonthly) businessObjectService.retrieve(monthlyEntry);
            if ( alreadyExistingMonthlyEntry != null ) {
                alreadyExistingMonthlyEntry.setFinancialDocumentMonth1LineAmount(monthlyEntry.getFinancialDocumentMonth1LineAmount());
                alreadyExistingMonthlyEntry.setFinancialDocumentMonth2LineAmount(monthlyEntry.getFinancialDocumentMonth2LineAmount());
                alreadyExistingMonthlyEntry.setFinancialDocumentMonth3LineAmount(monthlyEntry.getFinancialDocumentMonth3LineAmount());
                alreadyExistingMonthlyEntry.setFinancialDocumentMonth4LineAmount(monthlyEntry.getFinancialDocumentMonth4LineAmount());
                alreadyExistingMonthlyEntry.setFinancialDocumentMonth5LineAmount(monthlyEntry.getFinancialDocumentMonth5LineAmount());
                alreadyExistingMonthlyEntry.setFinancialDocumentMonth6LineAmount(monthlyEntry.getFinancialDocumentMonth6LineAmount());
                alreadyExistingMonthlyEntry.setFinancialDocumentMonth7LineAmount(monthlyEntry.getFinancialDocumentMonth7LineAmount());
                alreadyExistingMonthlyEntry.setFinancialDocumentMonth8LineAmount(monthlyEntry.getFinancialDocumentMonth8LineAmount());
                alreadyExistingMonthlyEntry.setFinancialDocumentMonth9LineAmount(monthlyEntry.getFinancialDocumentMonth9LineAmount());
                alreadyExistingMonthlyEntry.setFinancialDocumentMonth10LineAmount(monthlyEntry.getFinancialDocumentMonth10LineAmount());
                alreadyExistingMonthlyEntry.setFinancialDocumentMonth11LineAmount(monthlyEntry.getFinancialDocumentMonth11LineAmount());
                alreadyExistingMonthlyEntry.setFinancialDocumentMonth12LineAmount(monthlyEntry.getFinancialDocumentMonth12LineAmount());
                
                businessObjectService.save(alreadyExistingMonthlyEntry);
            } else {
                businessObjectService.save(monthlyEntry);
            }
        }
        
        return errorMessage;
    }
    
    /**
     * Updates benefits
     * 
     * @param fileType
     * @param importLine
     */
    private void udpateBenefits(String fileType, BudgetConstructionRequestMove importLine, BudgetConstructionHeader header) {
        BenefitsCalculationService benefitsCalculationService = SpringContext.getBean(BenefitsCalculationService.class);
        
        benefitsCalculationService.calculateAnnualBudgetConstructionGeneralLedgerBenefits(header.getDocumentNumber(), header.getUniversityFiscalYear(), importLine.getChartOfAccountsCode(), importLine.getAccountNumber(), importLine.getSubAccountNumber());
        
        if ( fileType.equalsIgnoreCase(BCConstants.RequestImportFileType.MONTHLY.toString()) ) {
            benefitsCalculationService.calculateMonthlyBudgetConstructionGeneralLedgerBenefits(header.getDocumentNumber(), header.getUniversityFiscalYear(), importLine.getChartOfAccountsCode(), importLine.getAccountNumber(), importLine.getSubAccountNumber());
        }
    }
    
    /**
     * Checks line validations and returns error messages for line
     * 
     * @param budgetConstructionRequestMove
     * @param lineNumber
     * @param isAnnual
     * @return
     */
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
    
    private LaborObject getLaborObject(BudgetConstructionRequestMove record, Integer budgetYear) {
        Map searchCriteria = new HashMap();

        searchCriteria.put(KFSPropertyConstants.UNIVERSITY_FISCAL_YEAR, budgetYear);
        searchCriteria.put(KFSPropertyConstants.CHART_OF_ACCOUNTS_CODE, record.getChartOfAccountsCode());
        searchCriteria.put(KFSPropertyConstants.FINANCIAL_OBJECT_CODE, record.getFinancialObjectCode());
        
        List<LaborObject> laborObjectList = new ArrayList<LaborObject> (getBusinessObjectService().findMatching(LaborObject.class, searchCriteria));
        
        if (laborObjectList.size() == 1) return laborObjectList.get(0);
        
        return null;
    }
    
    private ObjectCode getObjectCode(BudgetConstructionRequestMove record, Integer budgetYear) {
        Map searchCriteria = new HashMap();

        searchCriteria.put(KFSPropertyConstants.UNIVERSITY_FISCAL_YEAR, budgetYear);
        searchCriteria.put(KFSPropertyConstants.CHART_OF_ACCOUNTS_CODE, record.getChartOfAccountsCode());
        searchCriteria.put(KFSPropertyConstants.FINANCIAL_OBJECT_CODE, record.getFinancialObjectCode());
        
        List<ObjectCode> objectList = new ArrayList<ObjectCode> (businessObjectService.findMatching(ObjectCode.class, searchCriteria));
        
        if (objectList.size() == 1) return objectList.get(0);
        
        return null;
    }
    
    private SubObjCd getSubObjectCode(BudgetConstructionRequestMove record, Integer budgetYear) {
        Map searchCriteria = new HashMap();

        searchCriteria.put(KFSPropertyConstants.UNIVERSITY_FISCAL_YEAR, budgetYear);
        searchCriteria.put(KFSPropertyConstants.CHART_OF_ACCOUNTS_CODE, record.getChartOfAccountsCode());
        searchCriteria.put(KFSPropertyConstants.FINANCIAL_OBJECT_CODE, record.getFinancialObjectCode());
        searchCriteria.put(KFSPropertyConstants.ACCOUNT_NUMBER, record.getAccountNumber());
        searchCriteria.put(KFSPropertyConstants.FINANCIAL_SUB_OBJECT_CODE, record.getFinancialSubObjectCode());
        
        List<SubObjCd> objectList = new ArrayList<SubObjCd> (getBusinessObjectService().findMatching(SubObjCd.class, searchCriteria));
        
        if (objectList.size() == 1) return objectList.get(0);
        
        return null;
    }
    
    private List<BudgetConstructionFundingLock> findBudgetLocks(BudgetConstructionRequestMove record,Integer budgetYear) {
        Map searchCriteria = new HashMap();

        searchCriteria.put(KFSPropertyConstants.UNIVERSITY_FISCAL_YEAR, budgetYear);
        searchCriteria.put(KFSPropertyConstants.CHART_OF_ACCOUNTS_CODE, record.getChartOfAccountsCode());
        searchCriteria.put(KFSPropertyConstants.ACCOUNT_NUMBER, record.getAccountNumber());
        searchCriteria.put(KFSPropertyConstants.SUB_ACCOUNT_NUMBER, record.getSubAccountNumber());
        
        List<BudgetConstructionFundingLock> lockList = new ArrayList<BudgetConstructionFundingLock> (getBusinessObjectService().findMatching(BudgetConstructionFundingLock.class, searchCriteria));
        
        return lockList;
    }
    
    List<BudgetConstructionMonthly> getMonthlyRecords(BudgetConstructionRequestMove record, BudgetConstructionHeader header) {
        Map searchCriteria = new HashMap();
        
        searchCriteria.put(KFSPropertyConstants.DOCUMENT_NUMBER, header.getDocumentNumber());
        searchCriteria.put(KFSPropertyConstants.UNIVERSITY_FISCAL_YEAR, header.getUniversityFiscalYear());
        searchCriteria.put(KFSPropertyConstants.CHART_OF_ACCOUNTS_CODE, record.getChartOfAccountsCode());
        searchCriteria.put(KFSPropertyConstants.ACCOUNT_NUMBER, record.getAccountNumber());
        searchCriteria.put(KFSPropertyConstants.SUB_ACCOUNT_NUMBER, record.getSubAccountNumber());
        searchCriteria.put(KFSPropertyConstants.FINANCIAL_OBJECT_CODE, record.getFinancialObjectCode());
        searchCriteria.put(KFSPropertyConstants.FINANCIAL_SUB_OBJECT_CODE, record.getFinancialSubObjectCode());
        searchCriteria.put(KFSPropertyConstants.FINANCIAL_OBJECT_TYPE_CODE, record.getFinancialObjectTypeCode());
        searchCriteria.put(KFSPropertyConstants.FINANCIAL_BALANCE_TYPE_CODE, "BB"); 
        
        return new ArrayList<BudgetConstructionMonthly> (getBusinessObjectService().findMatching(BudgetConstructionMonthly.class, searchCriteria));
    }
    
    /**
     * Clears BudgetConstructionRequestMove
     * 
     * @param personUniversalIdentifier
     */
    private void deleteBudgetConstructionMoveRecords(String personUniversalIdentifier) {
        HashMap<String, String> fieldValues = new HashMap<String, String>();
        fieldValues.put(KFSPropertyConstants.PERSON_UNIVERSAL_IDENTIFIER, personUniversalIdentifier);
        businessObjectService.deleteMatching(BudgetConstructionRequestMove.class, fieldValues);
    }
}
