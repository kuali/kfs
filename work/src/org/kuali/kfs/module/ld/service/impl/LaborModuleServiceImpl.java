/*
 * Copyright 2007 The Kuali Foundation.
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
package org.kuali.module.labor.service.impl;

import java.sql.Date;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.kuali.core.bo.DocumentHeader;
import org.kuali.core.service.BusinessObjectService;
import org.kuali.core.service.DateTimeService;
import org.kuali.core.service.DocumentService;
import org.kuali.core.service.DocumentTypeService;
import org.kuali.core.util.Guid;
import org.kuali.core.util.KualiDecimal;
import org.kuali.kfs.KFSConstants;
import org.kuali.kfs.KFSPropertyConstants;
import org.kuali.kfs.bo.AccountingLine;
import org.kuali.kfs.context.SpringContext;
import org.kuali.module.financial.service.UniversityDateService;
import org.kuali.module.gl.bo.OriginEntryGroup;
import org.kuali.module.gl.bo.OriginEntrySource;
import org.kuali.module.gl.service.OriginEntryGroupService;
import org.kuali.module.integration.bo.LaborFringeBenefitInformation;
import org.kuali.module.integration.bo.LaborLedgerBalance;
import org.kuali.module.integration.bo.LaborLedgerBenefitsCalculation;
import org.kuali.module.integration.bo.LaborLedgerEntry;
import org.kuali.module.integration.bo.LaborLedgerExpenseTransferAccountingLine;
import org.kuali.module.integration.bo.LaborLedgerObject;
import org.kuali.module.integration.bo.LaborLedgerPositionObjectBenefit;
import org.kuali.module.integration.bo.LaborLedgerPositionObjectGroup;
import org.kuali.module.integration.service.LaborModuleService;
import org.kuali.module.labor.bo.BenefitsCalculation;
import org.kuali.module.labor.bo.ExpenseTransferSourceAccountingLine;
import org.kuali.module.labor.bo.ExpenseTransferTargetAccountingLine;
import org.kuali.module.labor.bo.FringeBenefitInformation;
import org.kuali.module.labor.bo.LaborLedgerPendingEntry;
import org.kuali.module.labor.bo.LaborObject;
import org.kuali.module.labor.bo.LaborOriginEntry;
import org.kuali.module.labor.bo.LedgerBalance;
import org.kuali.module.labor.bo.LedgerBalanceForEffortCertification;
import org.kuali.module.labor.bo.LedgerEntry;
import org.kuali.module.labor.bo.PositionObjectBenefit;
import org.kuali.module.labor.bo.PositionObjectGroup;
import org.kuali.module.labor.dao.LaborOriginEntryDao;
import org.kuali.module.labor.document.SalaryExpenseTransferDocument;
import org.kuali.module.labor.service.LaborBenefitsCalculationService;
import org.kuali.module.labor.service.LaborLedgerBalanceService;
import org.kuali.module.labor.service.LaborLedgerEntryService;
import org.kuali.module.labor.service.LaborLedgerPendingEntryService;
import org.springframework.transaction.annotation.Transactional;

import edu.iu.uis.eden.exception.WorkflowException;

/**
 * This implements the service methods that may be used by outside of labor module
 */
@Transactional
public class LaborModuleServiceImpl implements LaborModuleService {
    private static org.apache.log4j.Logger LOG = org.apache.log4j.Logger.getLogger(LaborModuleServiceImpl.class);

    /**
     * @see org.kuali.kfs.service.LaborModuleService#calculateFringeBenefit(org.kuali.kfs.bo.LaborLedgerObject,
     *      org.kuali.core.util.KualiDecimal)
     */
    public KualiDecimal calculateFringeBenefitFromLaborObject(LaborLedgerObject laborLedgerObject, KualiDecimal salaryAmount) {
        return getLaborBenefitsCalculationService().calculateFringeBenefit(laborLedgerObject, salaryAmount);
    }

    /**
     * @see org.kuali.kfs.service.LaborModuleService#calculateFringeBenefit(java.lang.Integer, java.lang.String, java.lang.String,
     *      org.kuali.core.util.KualiDecimal)
     */
    public KualiDecimal calculateFringeBenefit(Integer fiscalYear, String chartCode, String objectCode, KualiDecimal salaryAmount) {
        return getLaborBenefitsCalculationService().calculateFringeBenefit(fiscalYear, chartCode, objectCode, salaryAmount);
    }

    /**
     * @see org.kuali.module.integration.service.LaborModuleService#createAndBlankApproveSalaryExpenseTransferDocument(java.lang.String, java.lang.String, java.lang.String, java.util.List, java.util.List, java.util.List)
     */
    public void createAndBlankApproveSalaryExpenseTransferDocument(String documentDescription, String explanation, String annotation, List<String> adHocRecipients, List<LaborLedgerExpenseTransferAccountingLine> sourceAccountingLines, List<LaborLedgerExpenseTransferAccountingLine> targetAccountingLines) throws WorkflowException {
        LOG.info("createSalaryExpenseTransferDocument() start");
        
        if(sourceAccountingLines == null || sourceAccountingLines.isEmpty()) {
            LOG.info("Cannot create a salary expense document when the given source accounting line is empty.");
            return;
        }
        
        if(targetAccountingLines == null || targetAccountingLines.isEmpty()) {
            LOG.info("Cannot create a salary expense document when the given target accounting line is empty.");
            return;
        }

        SalaryExpenseTransferDocument document = (SalaryExpenseTransferDocument) getDocumentService().getNewDocument(SalaryExpenseTransferDocument.class);

        document.setEmplid(sourceAccountingLines.get(0).getEmplid());

        document.setSourceAccountingLines(sourceAccountingLines);
        document.setTargetAccountingLines(targetAccountingLines);

        DocumentHeader documentHeader = document.getDocumentHeader();
        documentHeader.setFinancialDocumentDescription(documentDescription);
        documentHeader.setExplanation(explanation);
        
        if(adHocRecipients != null && adHocRecipients.isEmpty()) {
            getDocumentService().acknowledgeDocument(document, annotation, adHocRecipients);
        }

        getDocumentService().blanketApproveDocument(document, KFSConstants.EMPTY_STRING, null);
    }

    /**
     * @see org.kuali.kfs.service.LaborModuleService#countPendingSalaryExpenseTransfer(java.lang.String)
     */
    public int countPendingSalaryExpenseTransfer(String emplid) {
        String documentTypeCode = getDocumentTypeService().getDocumentTypeCodeByClass(SalaryExpenseTransferDocument.class);

        Map<String, Object> positiveFieldValues = new HashMap<String, Object>();
        positiveFieldValues.put(KFSPropertyConstants.EMPLID, emplid);
        positiveFieldValues.put(KFSPropertyConstants.FINANCIAL_DOCUMENT_TYPE_CODE, documentTypeCode);

        List<String> approvedCodes = Arrays.asList(KFSConstants.PENDING_ENTRY_APPROVED_STATUS_CODE.APPROVED, KFSConstants.PENDING_ENTRY_APPROVED_STATUS_CODE.PROCESSED);
        Map<String, Object> negativeFieldValues = new HashMap<String, Object>();
        negativeFieldValues.put(KFSPropertyConstants.FINANCIAL_DOCUMENT_APPROVED_CODE, approvedCodes);

        return getBusinessObjectService().countMatching(LaborLedgerPendingEntry.class, positiveFieldValues, negativeFieldValues);
    }

    /**
     * @see org.kuali.module.effort.service.LaborEffortCertificationService#findEmployeesWithPayType(java.util.Map, java.util.List,
     *      java.util.Map)
     */
    public List<String> findEmployeesWithPayType(Map<Integer, Set<String>> payPeriods, List<String> balanceTypes, Map<String, Set<String>> earnCodePayGroupMap) {
        return getLaborLedgerEntryService().findEmployeesWithPayType(payPeriods, balanceTypes, earnCodePayGroupMap);
    }

    /**
     * @see org.kuali.kfs.service.LaborModuleService#isEmployeeWithPayType(java.lang.String, java.util.Map, java.util.List,
     *      java.util.Map)
     */
    public boolean isEmployeeWithPayType(String emplid, Map<Integer, Set<String>> payPeriods, List<String> balanceTypes, Map<String, Set<String>> earnCodePayGroupMap) {
        return getLaborLedgerEntryService().isEmployeeWithPayType(emplid, payPeriods, balanceTypes, earnCodePayGroupMap);
    }

    /**
     * @see org.kuali.module.effort.service.LaborEffortCertificationService#findLedgerBalances(java.util.Map, java.util.Map,
     *      java.util.Set, java.util.List, java.util.List)
     */
    public Collection<LaborLedgerBalance> findLedgerBalances(Map<String, List<String>> fieldValues, Map<String, List<String>> excludedFieldValues, Set<Integer> fiscalYears, List<String> balanceTypes, List<String> positionObjectGroupCodes) {
        Collection<LaborLedgerBalance> LaborLedgerBalances = new ArrayList<LaborLedgerBalance>();

        Collection<LedgerBalance> ledgerBalances = getLaborLedgerBalanceService().findLedgerBalances(fieldValues, excludedFieldValues, fiscalYears, balanceTypes, positionObjectGroupCodes);
        for (LedgerBalance balance : ledgerBalances) {
            LaborLedgerBalances.add(balance);
        }
        return LaborLedgerBalances;
    }

    /**
     * @see org.kuali.module.integration.service.LaborModuleService#createLaborBackupGroup()
     */
    public void createLaborBackupGroup() {
        LOG.debug("createBackupGroup() started");

        // Get the groups that need to be added
        Date today = getDateTimeService().getCurrentSqlDate();
        Collection groups = getLaborOriginEntryDao().getLaborGroupsToBackup(today);

        // Create the new group
        OriginEntryGroup backupGroup = getOriginEntryGroupService().createGroup(today, OriginEntrySource.LABOR_BACKUP, true, true, true);

        for (Iterator<OriginEntryGroup> iter = groups.iterator(); iter.hasNext();) {
            OriginEntryGroup group = iter.next();
            // Get only LaborOriginEntryGroup
            if (group.getSourceCode().startsWith("L")) {
                Iterator entry_iter = getLaborOriginEntryDao().getLaborEntriesByGroup(group, 0);

                while (entry_iter.hasNext()) {
                    LaborOriginEntry entry = (LaborOriginEntry) entry_iter.next();

                    entry.setEntryId(null);
                    entry.setObjectId(new Guid().toString());
                    entry.setGroup(backupGroup);
                    getLaborOriginEntryDao().saveOriginEntry(entry);
                }


                group.setProcess(false);
                group.setScrub(false);
                getOriginEntryGroupService().save(group);
            }
        }
    }

    /**
     * @see org.kuali.kfs.service.LaborModuleService#getLaborLedgerBalanceClass()
     */
    public Class<? extends LaborLedgerBalance> getLaborLedgerBalanceClass() {
        return LedgerBalance.class;
    }

    /**
     * @see org.kuali.kfs.service.LaborModuleService#getLaborLedgerEntryClass()
     */
    public Class<? extends LaborLedgerEntry> getLaborLedgerEntryClass() {
        return LedgerEntry.class;
    }

    /**
     * @see org.kuali.kfs.service.LaborModuleService#getLaborLedgerObjectClass()
     */
    public Class<? extends LaborLedgerObject> getLaborLedgerObjectClass() {
        return LaborObject.class;
    }

    /**
     * @see org.kuali.kfs.service.LaborModuleService#getLaborLedgerPositionObjectBenefitClass()
     */
    public Class<? extends LaborLedgerPositionObjectBenefit> getLaborLedgerPositionObjectBenefitClass() {
        return PositionObjectBenefit.class;
    }
    
    /**
     * @see org.kuali.module.integration.service.LaborModuleService#getLaborLedgerBenefitsCalculationClass()
     */
    public Class<? extends LaborLedgerBenefitsCalculation> getLaborLedgerBenefitsCalculationClass() {
        return BenefitsCalculation.class;
    }

    /**
     * @see org.kuali.kfs.service.LaborModuleService#getLaborLedgerPositionObjectGroupClass()
     */
    public Class<? extends LaborLedgerPositionObjectGroup> getLaborLedgerPositionObjectGroupClass() {
        return PositionObjectGroup.class;
    }

    /**
     * @see org.kuali.kfs.service.LaborModuleService#getLaborLedgerBalanceForEffortCertificationClass()
     */
    public Class<? extends LaborLedgerBalance> getLaborLedgerBalanceForEffortCertificationClass() {
        return LedgerBalanceForEffortCertification.class;
    }

    /**
     * @see org.kuali.module.integration.service.LaborModuleService#hasPendingLaborLedgerEntry(java.lang.String, java.lang.String)
     */
    public boolean hasPendingLaborLedgerEntry(String chartOfAccountsCode, String accountNumber) {
        return getLaborLedgerPendingEntryService().hasPendingLaborLedgerEntry(chartOfAccountsCode, accountNumber);
    }

    /**
     * Gets the expenseTransferSourceAccountingLineClass attribute.
     * 
     * @return Returns the expenseTransferSourceAccountingLineClass.
     */
    public Class<? extends LaborLedgerExpenseTransferAccountingLine> getExpenseTransferSourceAccountingLineClass() {
        return ExpenseTransferSourceAccountingLine.class;
    }

    /**
     * @see org.kuali.module.integration.service.LaborModuleService#getExpenseTransferTargetAccountingLineClass()
     */
    public Class<? extends LaborLedgerExpenseTransferAccountingLine> getExpenseTransferTargetAccountingLineClass() {
        return ExpenseTransferTargetAccountingLine.class;
    }

    /**
     * @see org.kuali.module.integration.service.LaborModuleService#retrieveLaborObjectBenefitInformation(java.lang.Integer, java.lang.String, java.lang.String)
     */
    public List<LaborFringeBenefitInformation> retrieveLaborObjectBenefitInformation(Integer fiscalYear, String chartOfAccountsCode, String objectCode) {
        List<LaborFringeBenefitInformation> fringeBenefitInformationRecords = new ArrayList<LaborFringeBenefitInformation>();
        Collection objectBenefits = retrieveLaborObjectBenefits(fiscalYear, chartOfAccountsCode, objectCode);
        for (Iterator iterator = objectBenefits.iterator(); iterator.hasNext();) {
            fringeBenefitInformationRecords.add(new FringeBenefitInformation((PositionObjectBenefit)iterator.next()));
        }
        return fringeBenefitInformationRecords;
    }
    
    /**
     * @see org.kuali.module.integration.service.LaborModuleService#hasFringeBenefitProducingObjectCodes(java.lang.Integer, java.util.List)
     */
    public boolean hasFringeBenefitProducingObjectCodes(Integer fiscalYear, String chartOfAccountsCode, String financialObjectCode) {
        Collection objectBenefits = retrieveLaborObjectBenefits(fiscalYear, chartOfAccountsCode, financialObjectCode);        
        return (objectBenefits != null && !objectBenefits.isEmpty());
    }

    /**
     * Calls business object service to retrieve LaborObjectBenefit objects for the given fiscal year, and chart, 
     * object code from accounting line.
     * 
     * @param fiscalYear The fiscal year to be used as search criteria for looking up the labor object benefits.
     * @param line The account line the benefits are being retrieved for.
     * @return List of LaborObjectBenefit objects or null if one does not exist for parameters
     */
    private Collection retrieveLaborObjectBenefits(Integer fiscalYear, String chartOfAccountsCode, String objectCode) {
        Map searchCriteria = new HashMap();

        searchCriteria.put(KFSPropertyConstants.UNIVERSITY_FISCAL_YEAR, fiscalYear);
        searchCriteria.put(KFSPropertyConstants.CHART_OF_ACCOUNTS_CODE, chartOfAccountsCode);
        searchCriteria.put(KFSPropertyConstants.FINANCIAL_OBJECT_CODE, objectCode);

        return getBusinessObjectService().findMatching(PositionObjectBenefit.class, searchCriteria);
    }

    /**
     * Gets the laborBenefitsCalculationService attribute.
     * 
     * @return Returns the laborBenefitsCalculationService.
     */
    public LaborBenefitsCalculationService getLaborBenefitsCalculationService() {
        return SpringContext.getBean(LaborBenefitsCalculationService.class);
    }

    /**
     * Gets the laborLedgerEntryService attribute.
     * 
     * @return Returns the laborLedgerEntryService.
     */
    public LaborLedgerEntryService getLaborLedgerEntryService() {
        return SpringContext.getBean(LaborLedgerEntryService.class);
    }

    /**
     * Gets the laborLedgerBalanceService attribute.
     * 
     * @return Returns the laborLedgerBalanceService.
     */
    public LaborLedgerBalanceService getLaborLedgerBalanceService() {
        return SpringContext.getBean(LaborLedgerBalanceService.class);
    }

    /**
     * Gets the documentService attribute.
     * 
     * @return Returns the documentService.
     */
    public DocumentService getDocumentService() {
        return SpringContext.getBean(DocumentService.class);
    }

    /**
     * Gets the documentTypeService attribute.
     * 
     * @return Returns the documentTypeService.
     */
    public DocumentTypeService getDocumentTypeService() {
        return SpringContext.getBean(DocumentTypeService.class);
    }

    /**
     * Gets the universityDateService attribute.
     * 
     * @return Returns the universityDateService.
     */
    public UniversityDateService getUniversityDateService() {
        return SpringContext.getBean(UniversityDateService.class);
    }

    /**
     * Gets the businessObjectService attribute.
     * 
     * @return Returns the businessObjectService.
     */
    public BusinessObjectService getBusinessObjectService() {
        return SpringContext.getBean(BusinessObjectService.class);
    }
    
    /**
     * Gets the dateTimeService 
     *
     * @return an implementation of the DateTimeService
     */
    public DateTimeService getDateTimeService() {
        return SpringContext.getBean(DateTimeService.class);
    }
    
    /**
     * Gets the originEntryGroupService 
     *
     * @return an implementation of the OriginEntryGroupService
     */
    public OriginEntryGroupService getOriginEntryGroupService() {
        return SpringContext.getBean(OriginEntryGroupService.class);
    }
    
    /**
     * Gets the laborOriginEntryDao 
     *
     * @return an implementation of the LaborOriginEntryDao
     */
    public LaborOriginEntryDao getLaborOriginEntryDao() {
        return SpringContext.getBean(LaborOriginEntryDao.class);
    }
    
    /**
     * Gets the laborLedgerPendingEntryService 
     *
     * @return an implementation of the LaborLedgerPendingEntryService
     */
    public LaborLedgerPendingEntryService getLaborLedgerPendingEntryService() {
        return SpringContext.getBean(LaborLedgerPendingEntryService.class);
    }
}
