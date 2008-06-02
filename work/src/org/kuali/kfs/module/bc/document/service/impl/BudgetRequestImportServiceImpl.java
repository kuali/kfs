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
import org.kuali.core.service.DictionaryValidationService;
import org.kuali.core.util.KualiInteger;
import org.kuali.kfs.KFSConstants;
import org.kuali.kfs.KFSPropertyConstants;
import org.kuali.kfs.annotation.NonTransactional;
import org.kuali.kfs.authorization.KfsAuthorizationConstants;
import org.kuali.kfs.context.SpringContext;
import org.kuali.kfs.service.OptionsService;
import org.kuali.module.budget.BCConstants;
import org.kuali.module.budget.BCParameterKeyConstants;
import org.kuali.module.budget.BCConstants.RequestImportFileType;
import org.kuali.module.budget.bo.BudgetConstructionFundingLock;
import org.kuali.module.budget.bo.BudgetConstructionHeader;
import org.kuali.module.budget.bo.BudgetConstructionMonthly;
import org.kuali.module.budget.bo.BudgetConstructionRequestMove;
import org.kuali.module.budget.bo.PendingBudgetConstructionGeneralLedger;
import org.kuali.module.budget.dao.ImportRequestDao;
import org.kuali.module.budget.document.BudgetConstructionDocument;
import org.kuali.module.budget.service.BenefitsCalculationService;
import org.kuali.module.budget.service.BudgetDocumentService;
import org.kuali.module.budget.service.BudgetParameterService;
import org.kuali.module.budget.service.BudgetRequestImportService;
import org.kuali.module.budget.service.LockService;
import org.kuali.module.budget.service.PermissionService;
import org.kuali.module.budget.util.ImportRequestFileParsingHelper;
import org.kuali.module.chart.bo.ObjectCode;
import org.kuali.module.chart.bo.SubObjCd;
import org.kuali.module.integration.bo.LaborLedgerObject;
import org.kuali.module.integration.service.LaborModuleService;
import org.springframework.transaction.annotation.Transactional;

import com.lowagie.text.Document;
import com.lowagie.text.DocumentException;
import com.lowagie.text.Paragraph;
import com.lowagie.text.pdf.PdfWriter;

/**
 * Contains services relevent to the budget construction import request process
 */

public class BudgetRequestImportServiceImpl implements BudgetRequestImportService {
    private BusinessObjectService businessObjectService;
    private ImportRequestDao importRequestDao;
    private PermissionService permissionService;
    private DictionaryValidationService dictionaryValidationService;
    private LockService lockService;
    private BudgetDocumentService budgetDocumentService;
    private LaborModuleService laborModuleService;
    private BudgetParameterService budgetParameterService;
    private OptionsService optionsService;
    
    private static final org.apache.commons.logging.Log LOG = org.apache.commons.logging.LogFactory.getLog(BudgetRequestImportServiceImpl.class);

    /**
     * @see org.kuali.module.budget.service.BudgetRequestImportService#generatePdf(java.util.List, java.io.ByteArrayOutputStream)
     */
    @NonTransactional
    public void generatePdf(List<String> errorMessages, ByteArrayOutputStream baos) throws DocumentException {
        Document document = new Document();
        PdfWriter.getInstance(document, baos);
        document.open();

        for (String error : errorMessages) {
            document.add(new Paragraph(error));
        }

        document.close();
    }

    /**
     * @see org.kuali.module.budget.service.BudgetRequestImportService#processImportFile(java.io.InputStream, java.lang.String,
     *      java.lang.String, java.lang.String)
     */
    @Transactional
    public List processImportFile(InputStream fileImportStream, String personUniversalIdentifier, String fieldSeperator, String textDelimiter, String fileType, Integer budgetYear) throws IOException {
        List fileErrorList = new ArrayList();
        
        deleteBudgetConstructionMoveRecords(personUniversalIdentifier);

        BudgetConstructionRequestMove budgetConstructionRequestMove = new BudgetConstructionRequestMove();

        BufferedReader fileReader = new BufferedReader(new InputStreamReader(fileImportStream));
        int currentLine = 1;
        while (fileReader.ready()) {
            String line = StringUtils.strip(fileReader.readLine());
            boolean isAnnualFile = (fileType.equalsIgnoreCase(RequestImportFileType.ANNUAL.toString())) ? true : false;

            if (StringUtils.isNotBlank(line)) {
                budgetConstructionRequestMove = ImportRequestFileParsingHelper.parseLine(line, fieldSeperator, textDelimiter, isAnnualFile);
            }
            // check if there were errors parsing the line
            if (budgetConstructionRequestMove == null) {
                fileErrorList.add(BCConstants.REQUEST_IMPORT_FILE_PROCESSING_ERROR_MESSAGE_GENERIC + " " + currentLine + ".");
                // clean out table since file processing has stopped
                deleteBudgetConstructionMoveRecords(personUniversalIdentifier);
                return fileErrorList;
            }

            String lineValidationError = validateLine(budgetConstructionRequestMove, currentLine, isAnnualFile);

            if ( StringUtils.isNotEmpty(lineValidationError) ) {
                fileErrorList.add(lineValidationError);
                // clean out table since file processing has stopped
                deleteBudgetConstructionMoveRecords(personUniversalIdentifier);
                return fileErrorList;
            }

            // set default values
            if (StringUtils.isBlank(budgetConstructionRequestMove.getSubAccountNumber())) {
                budgetConstructionRequestMove.setSubAccountNumber(KFSConstants.getDashSubAccountNumber());
            }

            if (StringUtils.isBlank(budgetConstructionRequestMove.getFinancialSubObjectCode())) {
                budgetConstructionRequestMove.setFinancialSubObjectCode(KFSConstants.getDashFinancialSubObjectCode());
            }
            //set object type code
            List revenueObjectTypesParamValues = budgetParameterService.getParameterValues(BudgetConstructionDocument.class, BCParameterKeyConstants.REVENUE_OBJECT_TYPES);
            List expenditureObjectTypesParamValues = budgetParameterService.getParameterValues(BudgetConstructionDocument.class, BCParameterKeyConstants.EXPENDITURE_OBJECT_TYPES);
            ObjectCode objectCode = getObjectCode(budgetConstructionRequestMove, budgetYear);
            if (objectCode != null) {
                if ( expenditureObjectTypesParamValues.contains(objectCode.getFinancialObjectTypeCode()) ) {
                    budgetConstructionRequestMove.setFinancialObjectTypeCode(optionsService.getOptions(budgetYear).getFinObjTypeExpenditureexpCd());
                } else if ( revenueObjectTypesParamValues.contains(objectCode.getFinancialObjectTypeCode()) ) {
                    budgetConstructionRequestMove.setFinancialObjectTypeCode(optionsService.getOptions(budgetYear).getFinObjectTypeIncomecashCode());
                }
            }
            
            //check for duplicate key exception, since it requires a different error message
            Map searchCriteria = new HashMap();
            searchCriteria.put("personUniversalIdentifier", personUniversalIdentifier);
            searchCriteria.put("chartOfAccountsCode", budgetConstructionRequestMove.getChartOfAccountsCode());
            searchCriteria.put("accountNumber", budgetConstructionRequestMove.getAccountNumber());
            searchCriteria.put("subAccountNumber", budgetConstructionRequestMove.getSubAccountNumber());
            searchCriteria.put("financialObjectCode", budgetConstructionRequestMove.getFinancialObjectCode());
            searchCriteria.put("financialSubObjectCode", budgetConstructionRequestMove.getFinancialSubObjectCode());
            if ( this.businessObjectService.countMatching(BudgetConstructionRequestMove.class, searchCriteria) != 0 ) {
                LOG.error("Move table store error, import aborted");
                fileErrorList.add("Duplicate Key for " + budgetConstructionRequestMove.getErrorLinePrefixForLogFile());
                fileErrorList.add("Move table store error, import aborted");
                deleteBudgetConstructionMoveRecords(personUniversalIdentifier);
                
                return fileErrorList;
            }
            try {
                budgetConstructionRequestMove.setPersonUniversalIdentifier(personUniversalIdentifier);
                importRequestDao.save(budgetConstructionRequestMove, false);
            }
            catch (RuntimeException e) {
                LOG.error("Move table store error, import aborted");
                fileErrorList.add("Move table store error, import aborted");
                return fileErrorList;
            }

            currentLine++;
        }

        return fileErrorList;
    }


    /**
     * Sets the business object service
     * 
     * @param businessObjectService
     */
    @NonTransactional
    public void setBusinessObjectService(BusinessObjectService businessObjectService) {
        this.businessObjectService = businessObjectService;
    }

    /**
     * @see org.kuali.module.budget.service.BudgetRequestImportService#validateData()
     */
    @Transactional
    public List<String> validateData(Integer budgetYear, String personUniversalIdentifier) {
        Map searchCriteria = new HashMap();
        searchCriteria.put("personUniversalIdentifier", personUniversalIdentifier);
        List<BudgetConstructionRequestMove> dataToValidateList = new ArrayList<BudgetConstructionRequestMove>(businessObjectService.findMatching(BudgetConstructionRequestMove.class, searchCriteria));
        List<String> errorMessages = new ArrayList<String>();

        Map<String, BudgetConstructionHeader> retrievedHeaders = new HashMap<String, BudgetConstructionHeader>();
        
        for (BudgetConstructionRequestMove record : dataToValidateList) {
            boolean validLine = true;
            record.refresh();

            String accountKey = record.getChartOfAccountsCode() + record.getAccountNumber();
            BudgetConstructionHeader budgetConstructionHeader = null;
            if (retrievedHeaders.containsKey(accountKey)) {
                budgetConstructionHeader = retrievedHeaders.get(accountKey);
            }
            else {
                budgetConstructionHeader = importRequestDao.getHeaderRecord(record, budgetYear);
                retrievedHeaders.put(accountKey, budgetConstructionHeader);
            }
            
            SubObjCd subObjectCode = getSubObjectCode(record, budgetYear);
            String code = record.getFinancialObjectTypeCode();
            LaborLedgerObject laborObject = this.laborModuleService.retrieveLaborLedgerObject(budgetYear, record.getChartOfAccountsCode(), record.getFinancialObjectCode());
            ObjectCode objectCode = getObjectCode(record, budgetYear);
            
            if (budgetConstructionHeader == null) {
                record.setRequestUpdateErrorCode(BCConstants.RequestImportErrorCode.DATA_VALIDATION_NO_BUDGETED_ACCOUNT_SUB_ACCOUNT_ERROR_CODE.getErrorCode());
                errorMessages.add(record.getErrorLinePrefixForLogFile() + BCConstants.RequestImportErrorCode.DATA_VALIDATION_NO_BUDGETED_ACCOUNT_SUB_ACCOUNT_ERROR_CODE.getMessage());
            }
            
            else if (record.getAccount().isAccountClosedIndicator()) {
                record.setRequestUpdateErrorCode(BCConstants.RequestImportErrorCode.DATA_VALIDATION_ACCOUNT_CLOSED_ERROR_CODE.getErrorCode());
                errorMessages.add(record.getErrorLinePrefixForLogFile() + BCConstants.RequestImportErrorCode.DATA_VALIDATION_ACCOUNT_CLOSED_ERROR_CODE.getMessage());
            }

            else if (record.getAccount().isExpired()) {
                record.setRequestUpdateErrorCode(BCConstants.RequestImportErrorCode.DATA_VALIDATION_ACCOUNT_EXPIRED_ERROR_CODE.getErrorCode());
                errorMessages.add(record.getErrorLinePrefixForLogFile() + BCConstants.RequestImportErrorCode.DATA_VALIDATION_ACCOUNT_EXPIRED_ERROR_CODE.getMessage());
            }

            else if (!record.getSubAccountNumber().equalsIgnoreCase(KFSConstants.getDashSubAccountNumber()) && !record.getSubAccount().isSubAccountActiveIndicator()) {
                record.setRequestUpdateErrorCode(BCConstants.RequestImportErrorCode.DATA_VALIDATION_SUB_ACCOUNT_INACTIVE_ERROR_CODE.getErrorCode());
                errorMessages.add(record.getErrorLinePrefixForLogFile() + BCConstants.RequestImportErrorCode.DATA_VALIDATION_SUB_ACCOUNT_INACTIVE_ERROR_CODE.getMessage());
            }

            // null object type
            else if (StringUtils.isBlank(record.getFinancialObjectTypeCode())) {
                record.setRequestUpdateErrorCode(BCConstants.RequestImportErrorCode.DATA_VALIDATION_OBJECT_TYPE_NULL_ERROR_CODE.getErrorCode());
                errorMessages.add(record.getErrorLinePrefixForLogFile() + BCConstants.RequestImportErrorCode.DATA_VALIDATION_OBJECT_TYPE_INVALID_ERROR_CODE.getMessage());
            }

            // inactive object code
            else if (objectCode != null && !objectCode.isActive()) {
                record.setRequestUpdateErrorCode(BCConstants.RequestImportErrorCode.DATA_VALIDATION_OBJECT_CODE_INACTIVE_ERROR_CODE.getErrorCode());
                errorMessages.add(record.getErrorLinePrefixForLogFile() + BCConstants.RequestImportErrorCode.DATA_VALIDATION_OBJECT_CODE_INACTIVE_ERROR_CODE.getMessage());
            }

            // compensation object codes COMP
            else if (laborObject != null && (laborObject.isDetailPositionRequiredIndicator() || laborObject.getFinancialObjectFringeOrSalaryCode().equals(BCConstants.LABOR_OBJECT_FRINGE_CODE))) {
                record.setRequestUpdateErrorCode(BCConstants.RequestImportErrorCode.DATA_VALIDATION_COMPENSATION_OBJECT_CODE_ERROR_CODE.getErrorCode());
                errorMessages.add(record.getErrorLinePrefixForLogFile() + BCConstants.RequestImportErrorCode.DATA_VALIDATION_COMPENSATION_OBJECT_CODE_ERROR_CODE.getMessage());
            }

            // no wage accounts CMPA
            else if (!record.getAccount().getSubFundGroup().isSubFundGroupWagesIndicator() && laborObject != null) {
                record.setRequestUpdateErrorCode(BCConstants.RequestImportErrorCode.DATA_VALIDATION_NO_WAGE_ACCOUNT_ERROR_CODE.getErrorCode());
                errorMessages.add(record.getErrorLinePrefixForLogFile() + BCConstants.RequestImportErrorCode.DATA_VALIDATION_NO_WAGE_ACCOUNT_ERROR_CODE.getMessage());
            }

            // invalid sub-object code NOSO
            else if (!record.getFinancialSubObjectCode().equalsIgnoreCase(KFSConstants.getDashFinancialSubObjectCode()) && subObjectCode == null) {
                record.setRequestUpdateErrorCode(BCConstants.RequestImportErrorCode.DATA_VALIDATION_SUB_OBJECT_INVALID_ERROR_CODE.getErrorCode());
                errorMessages.add(record.getErrorLinePrefixForLogFile() + BCConstants.RequestImportErrorCode.DATA_VALIDATION_SUB_OBJECT_INVALID_ERROR_CODE.getMessage());
            }
            
            // inactive sub-object code
            else if (!record.getFinancialSubObjectCode().equalsIgnoreCase(KFSConstants.getDashFinancialSubObjectCode()) && !subObjectCode.isFinancialSubObjectActiveIndicator()) {
                record.setRequestUpdateErrorCode(BCConstants.RequestImportErrorCode.DATA_VALIDATION_SUB_OBJECT_INACTIVE_ERROR_CODE.getErrorCode());
                errorMessages.add(record.getErrorLinePrefixForLogFile() + BCConstants.RequestImportErrorCode.DATA_VALIDATION_SUB_OBJECT_INACTIVE_ERROR_CODE.getMessage());
            }

            importRequestDao.save(record, true);
        }

        return errorMessages;
    }

    /**
     * @see org.kuali.module.budget.service.BudgetRequestImportService#loadBudget()
     */
    @Transactional
    public List<String> loadBudget(UniversalUser user, String fileType, Integer budgetYear) throws Exception {
        List<BudgetConstructionRequestMove> recordsToLoad = importRequestDao.findAllNonErrorCodeRecords(user.getPersonUniversalIdentifier());
        List<String> errorMessages = new ArrayList<String>();
        Map<String, BudgetConstructionRequestMove> recordMap = new HashMap<String, BudgetConstructionRequestMove>();
        
        for (BudgetConstructionRequestMove recordToLoad : recordsToLoad) {
            BudgetConstructionHeader header = importRequestDao.getHeaderRecord(recordToLoad, budgetYear);

            if (recordMap.containsKey(recordToLoad.getSubAccountingString())) {
                BudgetConstructionRequestMove temp = recordMap.get(recordToLoad.getSubAccountingString());

                recordToLoad.setHasAccess(temp.getHasAccess());
                recordToLoad.setHasLock(temp.getHasLock());
                recordToLoad.setRequestUpdateErrorCode(temp.getRequestUpdateErrorCode());
            }
            else {
                if ( header != null && budgetDocumentService.getAccessMode(budgetYear, recordToLoad.getChartOfAccountsCode(), recordToLoad.getAccountNumber(), recordToLoad.getSubAccountNumber(), user).equals(KfsAuthorizationConstants.BudgetConstructionEditMode.FULL_ENTRY) ) {
                    recordToLoad.setHasAccess(true);
                }
                else {
                    recordToLoad.setRequestUpdateErrorCode(BCConstants.RequestImportErrorCode.UPDATE_ERROR_CODE_NO_ACCESS_TO_BUDGET_ACCOUNT.getErrorCode());
                    errorMessages.add(recordToLoad.getErrorLinePrefixForLogFile() + BCConstants.RequestImportErrorCode.UPDATE_ERROR_CODE_NO_ACCESS_TO_BUDGET_ACCOUNT.getMessage());
                }

                if (recordToLoad.getHasAccess()) {
                    BudgetConstructionLockStatus lockStatus = this.lockService.lockAccount(header, user.getPersonUniversalIdentifier());
                    if (lockStatus.getLockStatus().equals(KFSConstants.BudgetConstructionConstants.LockStatus.SUCCESS)) {
                        recordToLoad.setHasLock(true);
                    } else {
                        recordToLoad.setRequestUpdateErrorCode(BCConstants.RequestImportErrorCode.UPDATE_ERROR_CODE_BUDGET_ACCOUNT_LOCKED.getErrorCode());
                        errorMessages.add(recordToLoad.getErrorLinePrefixForLogFile() + BCConstants.RequestImportErrorCode.UPDATE_ERROR_CODE_BUDGET_ACCOUNT_LOCKED.getMessage());
                    }
                }

                recordMap.put(recordToLoad.getSubAccountingString(), recordToLoad);
            }

            if (recordToLoad.getHasAccess() && recordToLoad.getHasLock() && StringUtils.isBlank(recordToLoad.getRequestUpdateErrorCode())) {
                String updateBudgetAmountErrorMessage = updateBudgetAmounts(fileType, recordToLoad, header, budgetYear);
                if (!StringUtils.isEmpty(updateBudgetAmountErrorMessage))
                    errorMessages.add(recordToLoad.getErrorLinePrefixForLogFile() + updateBudgetAmountErrorMessage);
            }

            importRequestDao.save(recordToLoad, true);
        }

        for (String key : recordMap.keySet()) {
            BudgetConstructionRequestMove record = recordMap.get(key);
            BudgetConstructionHeader header = importRequestDao.getHeaderRecord(record, budgetYear);
            if (record.getHasAccess() && record.getHasLock() && StringUtils.isBlank(record.getRequestUpdateErrorCode())) {
                udpateBenefits(fileType, header);
            }

            if (record.getHasLock() && header != null) {
                this.lockService.unlockAccount(header);
            }
        }

        deleteBudgetConstructionMoveRecords(user.getPersonUniversalIdentifier());
        return errorMessages;
    }

    /**
     * @see org.kuali.module.budget.service.BudgetRequestImportService#getImportRequestDao()
     */
    @NonTransactional
    public ImportRequestDao getImportRequestDao() {
        return this.importRequestDao;
    }

    /**
     * @see org.kuali.module.budget.service.BudgetRequestImportService#setImportRequestDao(org.kuali.module.budget.dao.ImportRequestDao)
     */
    @NonTransactional
    public void setImportRequestDao(ImportRequestDao dao) {
        this.importRequestDao = dao;

    }

    /**
     * Sets permissionService
     * 
     * @param permissionService
     */
    @NonTransactional
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
        
        //set primary key values
        PendingBudgetConstructionGeneralLedger pendingEntry = new PendingBudgetConstructionGeneralLedger();
        pendingEntry.setDocumentNumber(header.getDocumentNumber());
        pendingEntry.setUniversityFiscalYear(budgetYear);
        pendingEntry.setChartOfAccountsCode(importLine.getChartOfAccountsCode());
        pendingEntry.setAccountNumber(importLine.getAccountNumber());
        pendingEntry.setSubAccountNumber(importLine.getSubAccountNumber());
        pendingEntry.setFinancialObjectCode(importLine.getFinancialObjectCode());
        pendingEntry.setFinancialSubObjectCode(importLine.getFinancialSubObjectCode());
        pendingEntry.setFinancialBalanceTypeCode(KFSConstants.BALANCE_TYPE_BASE_BUDGET);
        pendingEntry.setFinancialObjectTypeCode(importLine.getFinancialObjectTypeCode());
        
        //if entry already exists, use existing entry
        PendingBudgetConstructionGeneralLedger retrievedPendingEntry = (PendingBudgetConstructionGeneralLedger) businessObjectService.retrieve(pendingEntry);
        if (retrievedPendingEntry != null) pendingEntry = retrievedPendingEntry;
        else pendingEntry.setFinancialBeginningBalanceLineAmount(new KualiInteger(0));
            
        if (fileType.equalsIgnoreCase(BCConstants.RequestImportFileType.ANNUAL.toString())) {
            List<BudgetConstructionMonthly> monthlyRecords = pendingEntry.getBudgetConstructionMonthly();

            if (!monthlyRecords.isEmpty()) {
                importLine.setRequestUpdateErrorCode(BCConstants.RequestImportErrorCode.UPDATE_ERROR_CODE_MONTHLY_BUDGET_DELETED.getErrorCode());
                errorMessage = BCConstants.RequestImportErrorCode.UPDATE_ERROR_CODE_MONTHLY_BUDGET_DELETED.getMessage();
                for (BudgetConstructionMonthly monthlyRecord : monthlyRecords) {
                    businessObjectService.delete(monthlyRecord);
                }
                
                importRequestDao.save(importLine, true);
            }

            pendingEntry.setAccountLineAnnualBalanceAmount(importLine.getAccountLineAnnualBalanceAmount());
            this.businessObjectService.save(pendingEntry);
        }
        else if (fileType.equalsIgnoreCase(BCConstants.RequestImportFileType.MONTHLY.toString())) {
            
            //calculate account line annual balance amount
            KualiInteger annualAmount = new KualiInteger(0);
            annualAmount = annualAmount.add(importLine.getFinancialDocumentMonth1LineAmount());
            annualAmount = annualAmount.add(importLine.getFinancialDocumentMonth2LineAmount());
            annualAmount = annualAmount.add(importLine.getFinancialDocumentMonth3LineAmount());
            annualAmount = annualAmount.add(importLine.getFinancialDocumentMonth4LineAmount());
            annualAmount = annualAmount.add(importLine.getFinancialDocumentMonth5LineAmount());
            annualAmount = annualAmount.add(importLine.getFinancialDocumentMonth6LineAmount());
            annualAmount = annualAmount.add(importLine.getFinancialDocumentMonth7LineAmount());
            annualAmount = annualAmount.add(importLine.getFinancialDocumentMonth8LineAmount());
            annualAmount = annualAmount.add(importLine.getFinancialDocumentMonth9LineAmount());
            annualAmount = annualAmount.add(importLine.getFinancialDocumentMonth10LineAmount());
            annualAmount = annualAmount.add(importLine.getFinancialDocumentMonth11LineAmount());
            annualAmount = annualAmount.add(importLine.getFinancialDocumentMonth12LineAmount());
            pendingEntry.setAccountLineAnnualBalanceAmount(annualAmount);
            
            
            //set primary key values
            BudgetConstructionMonthly monthlyEntry = new BudgetConstructionMonthly();
            monthlyEntry.setDocumentNumber(header.getDocumentNumber());
            monthlyEntry.setUniversityFiscalYear(budgetYear);
            monthlyEntry.setChartOfAccountsCode(importLine.getChartOfAccountsCode());
            monthlyEntry.setAccountNumber(importLine.getAccountNumber());
            monthlyEntry.setSubAccountNumber(importLine.getSubAccountNumber());
            monthlyEntry.setFinancialObjectCode(importLine.getFinancialObjectCode());
            monthlyEntry.setFinancialSubObjectCode(importLine.getFinancialSubObjectCode());
            monthlyEntry.setFinancialBalanceTypeCode(KFSConstants.BALANCE_TYPE_BASE_BUDGET);
            monthlyEntry.setFinancialObjectTypeCode(importLine.getFinancialObjectTypeCode());
            
            //if entry already exists, use existing entry
            BudgetConstructionMonthly retrievedMonthlyEntry = (BudgetConstructionMonthly) businessObjectService.retrieve(monthlyEntry);
            if (retrievedMonthlyEntry != null) {
                monthlyEntry = retrievedMonthlyEntry;
                //monthlyEntry.getPendingBudgetConstructionGeneralLedger().setAccountLineAnnualBalanceAmount(annualAmount);
            }
            /*else {
                pendingEntry.setAccountLineAnnualBalanceAmount(annualAmount);
                monthlyEntry.setPendingBudgetConstructionGeneralLedger(pendingEntry);
            }*/
            
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
            
            this.businessObjectService.save(pendingEntry);
            this.businessObjectService.save(monthlyEntry);
            
        }

        return errorMessage;
    }

    /**
     * Updates benefits
     * 
     * @param fileType
     * @param importLine
     */
    private void udpateBenefits(String fileType, BudgetConstructionHeader header) {
        BenefitsCalculationService benefitsCalculationService = SpringContext.getBean(BenefitsCalculationService.class);

        benefitsCalculationService.calculateAnnualBudgetConstructionGeneralLedgerBenefits(header.getDocumentNumber(), header.getUniversityFiscalYear(), header.getChartOfAccountsCode(), header.getAccountNumber(), header.getSubAccountNumber());

        if (fileType.equalsIgnoreCase(BCConstants.RequestImportFileType.MONTHLY.toString())) {
            benefitsCalculationService.calculateMonthlyBudgetConstructionGeneralLedgerBenefits(header.getDocumentNumber(), header.getUniversityFiscalYear(), header.getChartOfAccountsCode(), header.getAccountNumber(), header.getSubAccountNumber());
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

    private String validateLine(BudgetConstructionRequestMove budgetConstructionRequestMove, int lineNumber, boolean isAnnual) {
        
        if ( !this.dictionaryValidationService.isBusinessObjectValid(budgetConstructionRequestMove)) {
            return BCConstants.REQUEST_IMPORT_FILE_PROCESSING_ERROR_MESSAGE_GENERIC + " " + lineNumber + ".";
        }

        if (isAnnual) {
            if (budgetConstructionRequestMove.getAccountLineAnnualBalanceAmount().compareTo(new KualiInteger(999999999)) >= 0 || budgetConstructionRequestMove.getAccountLineAnnualBalanceAmount().compareTo(new KualiInteger(-999999999)) <= 0) {
                return BCConstants.REQUEST_IMPORT_FILE_PROCESSING_ERROR_MESSAGE_GENERIC + " " + lineNumber + ".";
            }
        }

        if (!isAnnual) {

            if (budgetConstructionRequestMove.getFinancialDocumentMonth1LineAmount().compareTo(new KualiInteger(999999999)) >= 0 || budgetConstructionRequestMove.getFinancialDocumentMonth1LineAmount().compareTo(new KualiInteger(-999999999)) <= 0) {
                return BCConstants.REQUEST_IMPORT_FILE_PROCESSING_ERROR_MESSAGE_GENERIC + " " + lineNumber + ".";
            }
            if (budgetConstructionRequestMove.getFinancialDocumentMonth2LineAmount().compareTo(new KualiInteger(999999999)) >= 0 || budgetConstructionRequestMove.getFinancialDocumentMonth2LineAmount().compareTo(new KualiInteger(-999999999)) <= 0) {
                return BCConstants.REQUEST_IMPORT_FILE_PROCESSING_ERROR_MESSAGE_GENERIC + " " + lineNumber + ".";
            }
            if (budgetConstructionRequestMove.getFinancialDocumentMonth3LineAmount().compareTo(new KualiInteger(999999999)) >= 0 || budgetConstructionRequestMove.getFinancialDocumentMonth3LineAmount().compareTo(new KualiInteger(-999999999)) <= 0) {
                return BCConstants.REQUEST_IMPORT_FILE_PROCESSING_ERROR_MESSAGE_GENERIC + " " + lineNumber + ".";
            }
            if (budgetConstructionRequestMove.getFinancialDocumentMonth4LineAmount().compareTo(new KualiInteger(999999999)) >= 0 || budgetConstructionRequestMove.getFinancialDocumentMonth4LineAmount().compareTo(new KualiInteger(-999999999)) <= 0) {
                return BCConstants.REQUEST_IMPORT_FILE_PROCESSING_ERROR_MESSAGE_GENERIC + " " + lineNumber + ".";
            }
            if (budgetConstructionRequestMove.getFinancialDocumentMonth5LineAmount().compareTo(new KualiInteger(999999999)) >= 0 || budgetConstructionRequestMove.getFinancialDocumentMonth5LineAmount().compareTo(new KualiInteger(-999999999)) <= 0) {
                return BCConstants.REQUEST_IMPORT_FILE_PROCESSING_ERROR_MESSAGE_GENERIC + " " + lineNumber + ".";
            }
            if (budgetConstructionRequestMove.getFinancialDocumentMonth6LineAmount().compareTo(new KualiInteger(999999999)) >= 0 || budgetConstructionRequestMove.getFinancialDocumentMonth6LineAmount().compareTo(new KualiInteger(-999999999)) <= 0) {
                return BCConstants.REQUEST_IMPORT_FILE_PROCESSING_ERROR_MESSAGE_GENERIC + " " + lineNumber + ".";
            }
            if (budgetConstructionRequestMove.getFinancialDocumentMonth7LineAmount().compareTo(new KualiInteger(999999999)) >= 0 || budgetConstructionRequestMove.getFinancialDocumentMonth7LineAmount().compareTo(new KualiInteger(-999999999)) <= 0) {
                return BCConstants.REQUEST_IMPORT_FILE_PROCESSING_ERROR_MESSAGE_GENERIC + " " + lineNumber + ".";
            }
            if (budgetConstructionRequestMove.getFinancialDocumentMonth8LineAmount().compareTo(new KualiInteger(999999999)) >= 0 || budgetConstructionRequestMove.getFinancialDocumentMonth8LineAmount().compareTo(new KualiInteger(-999999999)) <= 0) {
                return BCConstants.REQUEST_IMPORT_FILE_PROCESSING_ERROR_MESSAGE_GENERIC + " " + lineNumber + ".";
            }
            if (budgetConstructionRequestMove.getFinancialDocumentMonth9LineAmount().compareTo(new KualiInteger(999999999)) >= 0 || budgetConstructionRequestMove.getFinancialDocumentMonth9LineAmount().compareTo(new KualiInteger(-999999999)) <= 0) {
                return BCConstants.REQUEST_IMPORT_FILE_PROCESSING_ERROR_MESSAGE_GENERIC + " " + lineNumber + ".";
            }
            if (budgetConstructionRequestMove.getFinancialDocumentMonth10LineAmount().compareTo(new KualiInteger(999999999)) >= 0 || budgetConstructionRequestMove.getFinancialDocumentMonth10LineAmount().compareTo(new KualiInteger(-999999999)) <= 0) {
                return BCConstants.REQUEST_IMPORT_FILE_PROCESSING_ERROR_MESSAGE_GENERIC + " " + lineNumber + ".";
            }
            if (budgetConstructionRequestMove.getFinancialDocumentMonth11LineAmount().compareTo(new KualiInteger(999999999)) >= 0 || budgetConstructionRequestMove.getFinancialDocumentMonth11LineAmount().compareTo(new KualiInteger(-999999999)) <= 0) {
                return BCConstants.REQUEST_IMPORT_FILE_PROCESSING_ERROR_MESSAGE_GENERIC + " " + lineNumber + ".";
            }
            if (budgetConstructionRequestMove.getFinancialDocumentMonth12LineAmount().compareTo(new KualiInteger(999999999)) >= 0 || budgetConstructionRequestMove.getFinancialDocumentMonth12LineAmount().compareTo(new KualiInteger(-999999999)) <= 0) {
                return BCConstants.REQUEST_IMPORT_FILE_PROCESSING_ERROR_MESSAGE_GENERIC + " " + lineNumber + ".";
            }
        }

        return "";
    }
    
    private ObjectCode getObjectCode(BudgetConstructionRequestMove record, Integer budgetYear) {
        Map searchCriteria = new HashMap();

        searchCriteria.put(KFSPropertyConstants.UNIVERSITY_FISCAL_YEAR, budgetYear);
        searchCriteria.put(KFSPropertyConstants.CHART_OF_ACCOUNTS_CODE, record.getChartOfAccountsCode());
        searchCriteria.put(KFSPropertyConstants.FINANCIAL_OBJECT_CODE, record.getFinancialObjectCode());

        List<ObjectCode> objectList = new ArrayList<ObjectCode>(businessObjectService.findMatching(ObjectCode.class, searchCriteria));

        if (objectList.size() == 1)
            return objectList.get(0);

        return null;
    }

    private SubObjCd getSubObjectCode(BudgetConstructionRequestMove record, Integer budgetYear) {
        Map searchCriteria = new HashMap();

        searchCriteria.put(KFSPropertyConstants.UNIVERSITY_FISCAL_YEAR, budgetYear);
        searchCriteria.put(KFSPropertyConstants.CHART_OF_ACCOUNTS_CODE, record.getChartOfAccountsCode());
        searchCriteria.put(KFSPropertyConstants.FINANCIAL_OBJECT_CODE, record.getFinancialObjectCode());
        searchCriteria.put(KFSPropertyConstants.ACCOUNT_NUMBER, record.getAccountNumber());
        searchCriteria.put(KFSPropertyConstants.FINANCIAL_SUB_OBJECT_CODE, record.getFinancialSubObjectCode());

        List<SubObjCd> objectList = new ArrayList<SubObjCd> (this.businessObjectService.findMatching(SubObjCd.class, searchCriteria));

        if (objectList.size() == 1)
            return objectList.get(0);

        return null;
    }

    private List<BudgetConstructionFundingLock> findBudgetLocks(BudgetConstructionRequestMove record, Integer budgetYear) {
        Map searchCriteria = new HashMap();

        searchCriteria.put(KFSPropertyConstants.UNIVERSITY_FISCAL_YEAR, budgetYear);
        searchCriteria.put(KFSPropertyConstants.CHART_OF_ACCOUNTS_CODE, record.getChartOfAccountsCode());
        searchCriteria.put(KFSPropertyConstants.ACCOUNT_NUMBER, record.getAccountNumber());
        searchCriteria.put(KFSPropertyConstants.SUB_ACCOUNT_NUMBER, record.getSubAccountNumber());

        List<BudgetConstructionFundingLock> lockList = new ArrayList<BudgetConstructionFundingLock> (this.businessObjectService.findMatching(BudgetConstructionFundingLock.class, searchCriteria));

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
        searchCriteria.put(KFSPropertyConstants.FINANCIAL_BALANCE_TYPE_CODE, KFSConstants.BALANCE_TYPE_BASE_BUDGET);

        return new ArrayList<BudgetConstructionMonthly> (this.businessObjectService.findMatching(BudgetConstructionMonthly.class, searchCriteria));
    }

    /**
     * Clears BudgetConstructionRequestMove
     * 
     * @param personUniversalIdentifier
     */
    private void deleteBudgetConstructionMoveRecords(String personUniversalIdentifier) {
        Map<String, String> fieldValues = new HashMap<String, String>();
        fieldValues.put(KFSPropertyConstants.PERSON_UNIVERSAL_IDENTIFIER, personUniversalIdentifier);
        businessObjectService.deleteMatching(BudgetConstructionRequestMove.class, fieldValues);
    }
    
    /**
     * 
     * @see org.kuali.module.budget.service.BudgetRequestImportService#setDictionaryValidationService(org.kuali.core.service.DictionaryValidationService)
     */
    @NonTransactional
    public void setDictionaryValidationService(DictionaryValidationService dictionaryValidationService) {
        this.dictionaryValidationService = dictionaryValidationService;
        
    }
    
    /**
     * 
     * @see org.kuali.module.budget.service.BudgetRequestImportService#setLockService(org.kuali.module.budget.service.LockService)
     */
    @NonTransactional
    public void setLockService(LockService lockService) {
       this.lockService = lockService;
    }
    
    /**
     * Sets BudgetDocumentService
     * 
     * @param budgetDocumentService
     */
    @NonTransactional
    public void setBudgetDocumentService(BudgetDocumentService budgetDocumentService) {
        this.budgetDocumentService = budgetDocumentService;
    }
    
    @NonTransactional
    public void setLaborModuleService(LaborModuleService laborModuleService) {
        this.laborModuleService = laborModuleService;
    }
    
    @NonTransactional
    public LaborModuleService getLaborModuleService() {
        return this.laborModuleService;
    }
    
    @NonTransactional
    public BudgetParameterService getBudgetParameterService() {
        return budgetParameterService;
    }
    
    @NonTransactional
    public void setBudgetParameterService(BudgetParameterService budgetParameterService) {
        this.budgetParameterService = budgetParameterService;
    }
    
    @NonTransactional
    public OptionsService getOptionsService() {
        return optionsService;
    }
    
    @NonTransactional
    public void setOptionsService(OptionsService optionsService) {
        this.optionsService = optionsService;
    }
    
    
}
