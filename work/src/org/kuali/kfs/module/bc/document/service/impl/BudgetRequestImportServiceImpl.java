/*
 * Copyright 2008 The Kuali Foundation
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
package org.kuali.kfs.module.bc.document.service.impl;


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
import org.kuali.kfs.coa.businessobject.ObjectCode;
import org.kuali.kfs.coa.businessobject.SubObjectCode;
import org.kuali.kfs.integration.ld.LaborLedgerObject;
import org.kuali.kfs.integration.ld.LaborModuleService;
import org.kuali.kfs.module.bc.BCConstants;
import org.kuali.kfs.module.bc.BCConstants.RequestImportFileType;
import org.kuali.kfs.module.bc.businessobject.BudgetConstructionFundingLock;
import org.kuali.kfs.module.bc.businessobject.BudgetConstructionHeader;
import org.kuali.kfs.module.bc.businessobject.BudgetConstructionLockStatus;
import org.kuali.kfs.module.bc.businessobject.BudgetConstructionMonthly;
import org.kuali.kfs.module.bc.businessobject.BudgetConstructionRequestMove;
import org.kuali.kfs.module.bc.businessobject.PendingBudgetConstructionGeneralLedger;
import org.kuali.kfs.module.bc.document.BudgetConstructionDocument;
import org.kuali.kfs.module.bc.document.dataaccess.ImportRequestDao;
import org.kuali.kfs.module.bc.document.service.BenefitsCalculationService;
import org.kuali.kfs.module.bc.document.service.BudgetDocumentService;
import org.kuali.kfs.module.bc.document.service.BudgetParameterService;
import org.kuali.kfs.module.bc.document.service.BudgetRequestImportService;
import org.kuali.kfs.module.bc.document.service.LockService;
import org.kuali.kfs.module.bc.util.BudgetParameterFinder;
import org.kuali.kfs.module.bc.util.ImportRequestFileParsingHelper;
import org.kuali.kfs.sys.KFSConstants;
import org.kuali.kfs.sys.KFSParameterKeyConstants;
import org.kuali.kfs.sys.KFSPropertyConstants;
import org.kuali.kfs.sys.context.SpringContext;
import org.kuali.kfs.sys.service.NonTransactional;
import org.kuali.kfs.sys.service.OptionsService;
import org.kuali.kfs.sys.service.impl.KfsParameterConstants;
import org.kuali.rice.core.api.util.type.KualiInteger;
import org.kuali.rice.coreservice.framework.parameter.ParameterService;
import org.kuali.rice.kew.api.exception.WorkflowException;
import org.kuali.rice.kim.api.KimConstants;
import org.kuali.rice.kim.api.identity.Person;
import org.kuali.rice.kns.document.authorization.TransactionalDocumentAuthorizer;
import org.kuali.rice.kns.service.DictionaryValidationService;
import org.kuali.rice.kns.service.DocumentHelperService;
import org.kuali.rice.krad.service.BusinessObjectService;
import org.kuali.rice.krad.service.DocumentService;
import org.kuali.rice.krad.service.PersistenceService;
import org.kuali.rice.krad.util.KRADConstants;
import org.kuali.rice.krad.util.ObjectUtils;
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
    private DictionaryValidationService dictionaryValidationService;
    private LockService lockService;
    private BudgetDocumentService budgetDocumentService;
    private LaborModuleService laborModuleService;
    private BudgetParameterService budgetParameterService;
    private OptionsService optionsService;
    private DocumentHelperService documentHelperService;
    private DocumentService documentService;
    private ParameterService parameterService;
    private PersistenceService persistenceServiceOjb;
    
    private static final org.apache.commons.logging.Log LOG = org.apache.commons.logging.LogFactory.getLog(BudgetRequestImportServiceImpl.class);

    /**
     * @see org.kuali.kfs.module.bc.document.service.BudgetRequestImportService#generatePdf(java.util.List, java.io.ByteArrayOutputStream)
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
     * @see org.kuali.kfs.module.bc.document.service.BudgetRequestImportService#processImportFile(java.io.InputStream, java.lang.String,
     *      java.lang.String, java.lang.String)
     */
    @Transactional
    public List processImportFile(InputStream fileImportStream, String principalId, String fieldSeperator, String textDelimiter, String fileType, Integer budgetYear) throws IOException {
        List fileErrorList = new ArrayList();
        
        deleteBudgetConstructionMoveRecords(principalId);

        BudgetConstructionRequestMove budgetConstructionRequestMove = new BudgetConstructionRequestMove();

        BufferedReader fileReader = new BufferedReader(new InputStreamReader(fileImportStream));
        int currentLine = 1;
        while (fileReader.ready()) {
            String line = StringUtils.strip(fileReader.readLine());
            boolean isAnnualFile = (fileType.equalsIgnoreCase(RequestImportFileType.ANNUAL.toString())) ? true : false;

            if (StringUtils.isNotBlank(line)) {
                budgetConstructionRequestMove = ImportRequestFileParsingHelper.parseLine(line, fieldSeperator, textDelimiter, isAnnualFile);

                // check if there were errors parsing the line
                if (budgetConstructionRequestMove == null) {
                    fileErrorList.add(BCConstants.REQUEST_IMPORT_FILE_PROCESSING_ERROR_MESSAGE_GENERIC + " " + currentLine + ".");
                    // clean out table since file processing has stopped
                    deleteBudgetConstructionMoveRecords(principalId);
                    return fileErrorList;
                }

                String lineValidationError = validateLine(budgetConstructionRequestMove, currentLine, isAnnualFile);

                if ( StringUtils.isNotEmpty(lineValidationError) ) {
                    fileErrorList.add(lineValidationError);
                    // clean out table since file processing has stopped
                    deleteBudgetConstructionMoveRecords(principalId);
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
                Collection<String> revenueObjectTypesParamValues = BudgetParameterFinder.getRevenueObjectTypes();
                Collection<String> expenditureObjectTypesParamValues = BudgetParameterFinder.getExpenditureObjectTypes();
                ObjectCode objectCode = getObjectCode(budgetConstructionRequestMove, budgetYear);
                if (objectCode != null) {
                    if ( expenditureObjectTypesParamValues.contains(objectCode.getFinancialObjectTypeCode()) ) {
                        budgetConstructionRequestMove.setFinancialObjectTypeCode(objectCode.getFinancialObjectTypeCode());

                        // now using type from object code table
                        //budgetConstructionRequestMove.setFinancialObjectTypeCode(optionsService.getOptions(budgetYear).getFinObjTypeExpenditureexpCd());
                    } else if ( revenueObjectTypesParamValues.contains(objectCode.getFinancialObjectTypeCode()) ) {
                        budgetConstructionRequestMove.setFinancialObjectTypeCode(objectCode.getFinancialObjectTypeCode());

                        // now using type from object code table
                        //budgetConstructionRequestMove.setFinancialObjectTypeCode(optionsService.getOptions(budgetYear).getFinObjectTypeIncomecashCode());
                    }
                }
                
                //check for duplicate key exception, since it requires a different error message
                Map searchCriteria = new HashMap();
                searchCriteria.put("principalId", principalId);
                searchCriteria.put("chartOfAccountsCode", budgetConstructionRequestMove.getChartOfAccountsCode());
                searchCriteria.put("accountNumber", budgetConstructionRequestMove.getAccountNumber());
                searchCriteria.put("subAccountNumber", budgetConstructionRequestMove.getSubAccountNumber());
                searchCriteria.put("financialObjectCode", budgetConstructionRequestMove.getFinancialObjectCode());
                searchCriteria.put("financialSubObjectCode", budgetConstructionRequestMove.getFinancialSubObjectCode());
                if ( this.businessObjectService.countMatching(BudgetConstructionRequestMove.class, searchCriteria) != 0 ) {
                    LOG.error("Move table store error, import aborted");
                    fileErrorList.add("Duplicate Key for " + budgetConstructionRequestMove.getErrorLinePrefixForLogFile());
                    fileErrorList.add("Move table store error, import aborted");
                    deleteBudgetConstructionMoveRecords(principalId);
                    
                    return fileErrorList;
                }
                try {
                    budgetConstructionRequestMove.setPrincipalId(principalId);
                    importRequestDao.save(budgetConstructionRequestMove, false);
                }
                catch (RuntimeException e) {
                    LOG.error("Move table store error, import aborted");
                    fileErrorList.add("Move table store error, import aborted");
                    return fileErrorList;
                }
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
     * @see org.kuali.kfs.module.bc.document.service.BudgetRequestImportService#validateData()
     */
    @Transactional
    public List<String> validateData(Integer budgetYear, String principalId) {
        Map searchCriteria = new HashMap();
        searchCriteria.put("principalId", principalId);
        List<BudgetConstructionRequestMove> dataToValidateList = new ArrayList<BudgetConstructionRequestMove>(businessObjectService.findMatching(BudgetConstructionRequestMove.class, searchCriteria));
        List<String> errorMessages = new ArrayList<String>();

        Map<String, BudgetConstructionHeader> retrievedHeaders = new HashMap<String, BudgetConstructionHeader>();
        
        for (BudgetConstructionRequestMove record : dataToValidateList) {
            boolean validLine = true;
            //KFSMI-798 - refreshNonUpdatableReferences() used instead of refresh(), 
            //BudgetConstructionRequestMove does not have any updatable references            
            record.refreshNonUpdateableReferences();

            String accountKey = record.getChartOfAccountsCode() + record.getAccountNumber();
            BudgetConstructionHeader budgetConstructionHeader = null;
            if (retrievedHeaders.containsKey(accountKey)) {
                budgetConstructionHeader = retrievedHeaders.get(accountKey);
            }
            else {
                budgetConstructionHeader = importRequestDao.getHeaderRecord(record, budgetYear);
                retrievedHeaders.put(accountKey, budgetConstructionHeader);
            }
            
            SubObjectCode subObjectCode = getSubObjectCode(record, budgetYear);
            String code = record.getFinancialObjectTypeCode();
            LaborLedgerObject laborObject = this.laborModuleService.retrieveLaborLedgerObject(budgetYear, record.getChartOfAccountsCode(), record.getFinancialObjectCode());
            ObjectCode objectCode = getObjectCode(record, budgetYear);
            
            if (budgetConstructionHeader == null) {
                record.setRequestUpdateErrorCode(BCConstants.RequestImportErrorCode.DATA_VALIDATION_NO_BUDGETED_ACCOUNT_SUB_ACCOUNT_ERROR_CODE.getErrorCode());
                errorMessages.add(record.getErrorLinePrefixForLogFile() + " " + BCConstants.RequestImportErrorCode.DATA_VALIDATION_NO_BUDGETED_ACCOUNT_SUB_ACCOUNT_ERROR_CODE.getMessage());
            }
            
            else if (!record.getAccount().isActive()) {
                record.setRequestUpdateErrorCode(BCConstants.RequestImportErrorCode.DATA_VALIDATION_ACCOUNT_CLOSED_ERROR_CODE.getErrorCode());
                errorMessages.add(record.getErrorLinePrefixForLogFile() + " " + BCConstants.RequestImportErrorCode.DATA_VALIDATION_ACCOUNT_CLOSED_ERROR_CODE.getMessage());
            }

            else if (record.getAccount().isExpired()) {
                record.setRequestUpdateErrorCode(BCConstants.RequestImportErrorCode.DATA_VALIDATION_ACCOUNT_EXPIRED_ERROR_CODE.getErrorCode());
                errorMessages.add(record.getErrorLinePrefixForLogFile() + " " + BCConstants.RequestImportErrorCode.DATA_VALIDATION_ACCOUNT_EXPIRED_ERROR_CODE.getMessage());
            }

            // invalid sub-account code NOSA
            else if (!record.getSubAccountNumber().equalsIgnoreCase(KFSConstants.getDashSubAccountNumber()) && ObjectUtils.isNull(record.getSubAccount())) {
                record.setRequestUpdateErrorCode(BCConstants.RequestImportErrorCode.DATA_VALIDATION_SUB_ACCOUNT_INVALID_ERROR_CODE.getErrorCode());
                errorMessages.add(record.getErrorLinePrefixForLogFile() + " " + BCConstants.RequestImportErrorCode.DATA_VALIDATION_SUB_ACCOUNT_INVALID_ERROR_CODE.getMessage());
            }
            
            else if (!record.getSubAccountNumber().equalsIgnoreCase(KFSConstants.getDashSubAccountNumber()) && !record.getSubAccount().isActive()) {
                record.setRequestUpdateErrorCode(BCConstants.RequestImportErrorCode.DATA_VALIDATION_SUB_ACCOUNT_INACTIVE_ERROR_CODE.getErrorCode());
                errorMessages.add(record.getErrorLinePrefixForLogFile() + " " + BCConstants.RequestImportErrorCode.DATA_VALIDATION_SUB_ACCOUNT_INACTIVE_ERROR_CODE.getMessage());
            }

            // null object type
            else if (StringUtils.isBlank(record.getFinancialObjectTypeCode())) {
                record.setRequestUpdateErrorCode(BCConstants.RequestImportErrorCode.DATA_VALIDATION_OBJECT_TYPE_NULL_ERROR_CODE.getErrorCode());
                errorMessages.add(record.getErrorLinePrefixForLogFile() + " " + BCConstants.RequestImportErrorCode.DATA_VALIDATION_OBJECT_TYPE_INVALID_ERROR_CODE.getMessage());
            }

            // inactive object code
            else if (objectCode != null && !objectCode.isActive()) {
                record.setRequestUpdateErrorCode(BCConstants.RequestImportErrorCode.DATA_VALIDATION_OBJECT_CODE_INACTIVE_ERROR_CODE.getErrorCode());
                errorMessages.add(record.getErrorLinePrefixForLogFile() + " " + BCConstants.RequestImportErrorCode.DATA_VALIDATION_OBJECT_CODE_INACTIVE_ERROR_CODE.getMessage());
            }

            // compensation object codes COMP
            else if (laborObject != null && (laborObject.isDetailPositionRequiredIndicator() || laborObject.getFinancialObjectFringeOrSalaryCode().equals(BCConstants.LABOR_OBJECT_FRINGE_CODE))) {
                record.setRequestUpdateErrorCode(BCConstants.RequestImportErrorCode.DATA_VALIDATION_COMPENSATION_OBJECT_CODE_ERROR_CODE.getErrorCode());
                errorMessages.add(record.getErrorLinePrefixForLogFile() + " " + BCConstants.RequestImportErrorCode.DATA_VALIDATION_COMPENSATION_OBJECT_CODE_ERROR_CODE.getMessage());
            }

            // no wage accounts CMPA
            else if (!record.getAccount().getSubFundGroup().isSubFundGroupWagesIndicator() && laborObject != null) {
                record.setRequestUpdateErrorCode(BCConstants.RequestImportErrorCode.DATA_VALIDATION_NO_WAGE_ACCOUNT_ERROR_CODE.getErrorCode());
                errorMessages.add(record.getErrorLinePrefixForLogFile() + " " + BCConstants.RequestImportErrorCode.DATA_VALIDATION_NO_WAGE_ACCOUNT_ERROR_CODE.getMessage());
            }

            // invalid sub-object code NOSO
            else if (!record.getFinancialSubObjectCode().equalsIgnoreCase(KFSConstants.getDashFinancialSubObjectCode()) && subObjectCode == null) {
                record.setRequestUpdateErrorCode(BCConstants.RequestImportErrorCode.DATA_VALIDATION_SUB_OBJECT_INVALID_ERROR_CODE.getErrorCode());
                errorMessages.add(record.getErrorLinePrefixForLogFile() + " " + BCConstants.RequestImportErrorCode.DATA_VALIDATION_SUB_OBJECT_INVALID_ERROR_CODE.getMessage());
            }
            
            // inactive sub-object code
            else if (!record.getFinancialSubObjectCode().equalsIgnoreCase(KFSConstants.getDashFinancialSubObjectCode()) && !subObjectCode.isActive()) {
                record.setRequestUpdateErrorCode(BCConstants.RequestImportErrorCode.DATA_VALIDATION_SUB_OBJECT_INACTIVE_ERROR_CODE.getErrorCode());
                errorMessages.add(record.getErrorLinePrefixForLogFile() + " " + BCConstants.RequestImportErrorCode.DATA_VALIDATION_SUB_OBJECT_INACTIVE_ERROR_CODE.getMessage());
            }

            importRequestDao.save(record, true);
        }

        return errorMessages;
    }

    /**
     * @see org.kuali.kfs.module.bc.document.service.BudgetRequestImportService#loadBudget()
     */
    @Transactional
    public List<String> loadBudget(Person user, String fileType, Integer budgetYear) throws Exception {
        List<BudgetConstructionRequestMove> recordsToLoad = importRequestDao.findAllNonErrorCodeRecords(user.getPrincipalId());
        List<String> errorMessages = new ArrayList<String>();
        Map<String, BudgetConstructionRequestMove> recordMap = new HashMap<String, BudgetConstructionRequestMove>();
        
        // month delete warning error is a soft error
        String deleteMonthlyWarningErrorMessage = BCConstants.RequestImportErrorCode.UPDATE_ERROR_CODE_MONTHLY_BUDGET_DELETED.getErrorCode();

        for (BudgetConstructionRequestMove recordToLoad : recordsToLoad) {
            BudgetConstructionHeader header = importRequestDao.getHeaderRecord(recordToLoad, budgetYear);

            if (recordMap.containsKey(recordToLoad.getSubAccountingString())) {
                BudgetConstructionRequestMove temp = recordMap.get(recordToLoad.getSubAccountingString());

                recordToLoad.setHasAccess(temp.getHasAccess());
                recordToLoad.setHasLock(temp.getHasLock());
                recordToLoad.setRequestUpdateErrorCode(temp.getRequestUpdateErrorCode());
            }
            else {
                boolean hasAccess = false;
                if (header != null) {
                    BudgetConstructionDocument document;
                    try {
                        document = (BudgetConstructionDocument) documentService.getByDocumentHeaderId(header.getDocumentNumber());
                    }
                    catch (WorkflowException e) {
                        throw new RuntimeException("Fail to retrieve budget document for doc id " + header.getDocumentNumber());
                    }

                    TransactionalDocumentAuthorizer documentAuthorizer = (TransactionalDocumentAuthorizer) getDocumentHelperService().getDocumentAuthorizer(document);
                    hasAccess = documentAuthorizer.isAuthorizedByTemplate(document, KRADConstants.KNS_NAMESPACE, KimConstants.PermissionTemplateNames.EDIT_DOCUMENT, user.getPrincipalId());
                }

                if (hasAccess) {
                    recordToLoad.setHasAccess(true);
                }
                else {
                    recordToLoad.setRequestUpdateErrorCode(BCConstants.RequestImportErrorCode.UPDATE_ERROR_CODE_NO_ACCESS_TO_BUDGET_ACCOUNT.getErrorCode());
                    errorMessages.add(recordToLoad.getErrorLinePrefixForLogFile() + " " + BCConstants.RequestImportErrorCode.UPDATE_ERROR_CODE_NO_ACCESS_TO_BUDGET_ACCOUNT.getMessage());
                }

                if (recordToLoad.getHasAccess()) {
                    BudgetConstructionLockStatus lockStatus = this.lockService.lockAccountAndCommit(header, user.getPrincipalId());
                    if (lockStatus.getLockStatus().equals(BCConstants.LockStatus.SUCCESS)) {
                        recordToLoad.setHasLock(true);
                    } else {
                        recordToLoad.setRequestUpdateErrorCode(BCConstants.RequestImportErrorCode.UPDATE_ERROR_CODE_BUDGET_ACCOUNT_LOCKED.getErrorCode());
                        errorMessages.add(recordToLoad.getErrorLinePrefixForLogFile() + " " + BCConstants.RequestImportErrorCode.UPDATE_ERROR_CODE_BUDGET_ACCOUNT_LOCKED.getMessage());
                    }
                }

                recordMap.put(recordToLoad.getSubAccountingString(), recordToLoad);
            }
            
            if (recordToLoad.getHasAccess() && recordToLoad.getHasLock() && 
                    ( StringUtils.isBlank(recordToLoad.getRequestUpdateErrorCode()) || recordToLoad.getRequestUpdateErrorCode().endsWith(deleteMonthlyWarningErrorMessage)) ) {
                String updateBudgetAmountErrorMessage = updateBudgetAmounts(fileType, recordToLoad, header, budgetYear);
                if (!StringUtils.isEmpty(updateBudgetAmountErrorMessage))
                    errorMessages.add(recordToLoad.getErrorLinePrefixForLogFile() + " " + updateBudgetAmountErrorMessage);
            }

            importRequestDao.save(recordToLoad, true);
        }

        for (String key : recordMap.keySet()) {
            BudgetConstructionRequestMove record = recordMap.get(key);
            BudgetConstructionHeader header = importRequestDao.getHeaderRecord(record, budgetYear);
            if (record.getHasAccess() && record.getHasLock() && ( StringUtils.isBlank(record.getRequestUpdateErrorCode()) || record.getRequestUpdateErrorCode().endsWith(deleteMonthlyWarningErrorMessage))) {
                udpateBenefits(fileType, header);
            }

            if (record.getHasLock() && header != null) {
                this.lockService.unlockAccount(header);
            }
        }

        deleteBudgetConstructionMoveRecords(user.getPrincipalId());

        // clear ojb cache since benefits calc is done with JDBC
        persistenceServiceOjb.clearCache();

        return errorMessages;
    }

    /**
     * @see org.kuali.kfs.module.bc.document.service.BudgetRequestImportService#getImportRequestDao()
     */
    @NonTransactional
    public ImportRequestDao getImportRequestDao() {
        return this.importRequestDao;
    }

    /**
     * @see org.kuali.kfs.module.bc.document.service.BudgetRequestImportService#setImportRequestDao(org.kuali.kfs.module.bc.document.dataaccess.ImportRequestDao)
     */
    @NonTransactional
    public void setImportRequestDao(ImportRequestDao dao) {
        this.importRequestDao = dao;

    }

    /**
     * updates budget amounts
     * 
     * @param fileType
     * @param importLine
     * @return error message
     */
    protected String updateBudgetAmounts(String fileType, BudgetConstructionRequestMove importLine, BudgetConstructionHeader header, Integer budgetYear) {
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
    protected void udpateBenefits(String fileType, BudgetConstructionHeader header) {
        BenefitsCalculationService benefitsCalculationService = SpringContext.getBean(BenefitsCalculationService.class);

        Map<String, Object> fieldValues = new HashMap<String, Object>();
        fieldValues.put(KFSPropertyConstants.UNIVERSITY_FISCAL_YEAR, header.getUniversityFiscalYear());
        fieldValues.put(KFSPropertyConstants.CHART_OF_ACCOUNTS_CODE, header.getChartOfAccountsCode());
        fieldValues.put(KFSPropertyConstants.ACCOUNT_NUMBER, header.getAccountNumber());
        fieldValues.put(KFSPropertyConstants.SUB_ACCOUNT_NUMBER, header.getSubAccountNumber());
        int monthlyCnt = businessObjectService.countMatching(BudgetConstructionMonthly.class, fieldValues);
        
        String sysParam = parameterService.getParameterValueAsString(KfsParameterConstants.FINANCIAL_SYSTEM_ALL.class, KFSParameterKeyConstants.LdParameterConstants.ENABLE_FRINGE_BENEFIT_CALC_BY_BENEFIT_RATE_CATEGORY_IND);
        
        // if sysParam == Y then Labor Benefit Rate Category Code must be used
        if (sysParam.equalsIgnoreCase("Y")) {
            benefitsCalculationService.calculateAnnualBudgetConstructionGeneralLedgerBenefits(header.getDocumentNumber(), header.getUniversityFiscalYear(), header.getChartOfAccountsCode(), header.getAccountNumber(), header.getSubAccountNumber(), header.getAccount().getLaborBenefitRateCategoryCode());

            if (monthlyCnt > 0 || fileType.equalsIgnoreCase(BCConstants.RequestImportFileType.MONTHLY.toString())) {
                benefitsCalculationService.calculateMonthlyBudgetConstructionGeneralLedgerBenefits(header.getDocumentNumber(), header.getUniversityFiscalYear(), header.getChartOfAccountsCode(), header.getAccountNumber(), header.getSubAccountNumber(), header.getAccount().getLaborBenefitRateCategoryCode());
            }

        } else {

            // no rate category code - call original
            benefitsCalculationService.calculateAnnualBudgetConstructionGeneralLedgerBenefits(header.getDocumentNumber(), header.getUniversityFiscalYear(), header.getChartOfAccountsCode(), header.getAccountNumber(), header.getSubAccountNumber());

            if (monthlyCnt > 0 || fileType.equalsIgnoreCase(BCConstants.RequestImportFileType.MONTHLY.toString())) {
                benefitsCalculationService.calculateMonthlyBudgetConstructionGeneralLedgerBenefits(header.getDocumentNumber(), header.getUniversityFiscalYear(), header.getChartOfAccountsCode(), header.getAccountNumber(), header.getSubAccountNumber());
            }
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

    protected String validateLine(BudgetConstructionRequestMove budgetConstructionRequestMove, int lineNumber, boolean isAnnual) {
        
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
    
    protected ObjectCode getObjectCode(BudgetConstructionRequestMove record, Integer budgetYear) {
        Map searchCriteria = new HashMap();

        searchCriteria.put(KFSPropertyConstants.UNIVERSITY_FISCAL_YEAR, budgetYear);
        searchCriteria.put(KFSPropertyConstants.CHART_OF_ACCOUNTS_CODE, record.getChartOfAccountsCode());
        searchCriteria.put(KFSPropertyConstants.FINANCIAL_OBJECT_CODE, record.getFinancialObjectCode());

        List<ObjectCode> objectList = new ArrayList<ObjectCode>(businessObjectService.findMatching(ObjectCode.class, searchCriteria));

        if (objectList.size() == 1)
            return objectList.get(0);

        return null;
    }

    protected SubObjectCode getSubObjectCode(BudgetConstructionRequestMove record, Integer budgetYear) {
        Map searchCriteria = new HashMap();

        searchCriteria.put(KFSPropertyConstants.UNIVERSITY_FISCAL_YEAR, budgetYear);
        searchCriteria.put(KFSPropertyConstants.CHART_OF_ACCOUNTS_CODE, record.getChartOfAccountsCode());
        searchCriteria.put(KFSPropertyConstants.FINANCIAL_OBJECT_CODE, record.getFinancialObjectCode());
        searchCriteria.put(KFSPropertyConstants.ACCOUNT_NUMBER, record.getAccountNumber());
        searchCriteria.put(KFSPropertyConstants.FINANCIAL_SUB_OBJECT_CODE, record.getFinancialSubObjectCode());

        List<SubObjectCode> objectList = new ArrayList<SubObjectCode> (this.businessObjectService.findMatching(SubObjectCode.class, searchCriteria));

        if (objectList.size() == 1)
            return objectList.get(0);

        return null;
    }

    protected List<BudgetConstructionFundingLock> findBudgetLocks(BudgetConstructionRequestMove record, Integer budgetYear) {
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
     * @param principalId
     */
    protected void deleteBudgetConstructionMoveRecords(String principalId) {
        Map<String, String> fieldValues = new HashMap<String, String>();
        fieldValues.put(KFSPropertyConstants.PERSON_UNIVERSAL_IDENTIFIER, principalId);
        businessObjectService.deleteMatching(BudgetConstructionRequestMove.class, fieldValues);
    }
    
    /**
     * 
     * @see org.kuali.kfs.module.bc.document.service.BudgetRequestImportService#setDictionaryValidationService(org.kuali.rice.kns.service.DictionaryValidationService)
     */
    @NonTransactional
    public void setDictionaryValidationService(DictionaryValidationService dictionaryValidationService) {
        this.dictionaryValidationService = dictionaryValidationService;
        
    }
    
    /**
     * 
     * @see org.kuali.kfs.module.bc.document.service.BudgetRequestImportService#setLockService(org.kuali.kfs.module.bc.document.service.LockService)
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

    @NonTransactional
    public DocumentHelperService getDocumentHelperService() {
        return documentHelperService;
    }

    @NonTransactional
    public void setDocumentHelperService(DocumentHelperService documentHelperService) {
        this.documentHelperService = documentHelperService;
    }

    @NonTransactional
    protected DocumentService getDocumentService() {
        return documentService;
    }

    @NonTransactional
    public void setDocumentService(DocumentService documentService) {
        this.documentService = documentService;
    }

    /**
     * Gets the parameterService attribute.
     * 
     * @return Returns the parameterService
     */
    @NonTransactional
    public ParameterService getParameterService() {
        return parameterService;
    }

    /**	
     * Sets the parameterService attribute.
     * 
     * @param parameterService The parameterService to set.
     */
    @NonTransactional
    public void setParameterService(ParameterService parameterService) {
        this.parameterService = parameterService;
    }

    /**
     * Gets the persistenceServiceOjb attribute.
     * 
     * @return Returns the persistenceServiceOjb
     */
    @NonTransactional
    public PersistenceService getPersistenceServiceOjb() {
        return persistenceServiceOjb;
    }

    /**	
     * Sets the persistenceServiceOjb attribute.
     * 
     * @param persistenceServiceOjb The persistenceServiceOjb to set.
     */
    @NonTransactional
    public void setPersistenceServiceOjb(PersistenceService persistenceServiceOjb) {
        this.persistenceServiceOjb = persistenceServiceOjb;
    }
    
}

