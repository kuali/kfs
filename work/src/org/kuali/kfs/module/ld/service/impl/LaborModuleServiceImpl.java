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

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.kuali.core.bo.DocumentHeader;
import org.kuali.core.service.BusinessObjectService;
import org.kuali.core.service.DocumentService;
import org.kuali.core.service.DocumentTypeService;
import org.kuali.core.util.KualiDecimal;
import org.kuali.kfs.KFSConstants;
import org.kuali.kfs.KFSPropertyConstants;
import org.kuali.kfs.context.SpringContext;
import org.kuali.module.financial.service.UniversityDateService;
import org.kuali.module.integration.bo.LaborLedgerBalance;
import org.kuali.module.integration.bo.LaborLedgerEntry;
import org.kuali.module.integration.bo.LaborLedgerExpenseTransferAccountingLine;
import org.kuali.module.integration.bo.LaborLedgerObject;
import org.kuali.module.integration.bo.LaborLedgerPositionObjectBenefit;
import org.kuali.module.integration.bo.LaborLedgerPositionObjectGroup;
import org.kuali.module.integration.service.LaborModuleService;
import org.kuali.module.labor.bo.ExpenseTransferSourceAccountingLine;
import org.kuali.module.labor.bo.ExpenseTransferTargetAccountingLine;
import org.kuali.module.labor.bo.LaborLedgerPendingEntry;
import org.kuali.module.labor.bo.LaborObject;
import org.kuali.module.labor.bo.LedgerBalance;
import org.kuali.module.labor.bo.LedgerBalanceForEffortCertification;
import org.kuali.module.labor.bo.LedgerEntry;
import org.kuali.module.labor.bo.PositionObjectBenefit;
import org.kuali.module.labor.bo.PositionObjectGroup;
import org.kuali.module.labor.document.SalaryExpenseTransferDocument;
import org.kuali.module.labor.service.LaborBenefitsCalculationService;
import org.kuali.module.labor.service.LaborLedgerBalanceService;
import org.kuali.module.labor.service.LaborLedgerEntryService;
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
     * @see org.kuali.kfs.service.LaborModuleService#createSalaryExpenseTransferDocument(java.lang.String, java.lang.String,
     *      java.util.List, java.util.List)
     */
    public void createSalaryExpenseTransferDocument(String documentDescription, String explanation, List<LaborLedgerExpenseTransferAccountingLine> sourceAccountingLines, List<LaborLedgerExpenseTransferAccountingLine> targetAccountingLines) throws WorkflowException {
        LOG.info("createSalaryExpenseTransferDocument() start");

        SalaryExpenseTransferDocument document = (SalaryExpenseTransferDocument) getDocumentService().getNewDocument(SalaryExpenseTransferDocument.class);

        document.setEmplid(sourceAccountingLines.get(0).getEmplid());

        document.setSourceAccountingLines(sourceAccountingLines);
        document.setTargetAccountingLines(targetAccountingLines);

        DocumentHeader documentHeader = document.getDocumentHeader();
        documentHeader.setFinancialDocumentDescription(documentDescription);
        documentHeader.setExplanation(explanation);

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
}
