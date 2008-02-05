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
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.kuali.core.bo.DocumentHeader;
import org.kuali.core.service.DocumentService;
import org.kuali.core.util.KualiDecimal;
import org.kuali.kfs.KFSConstants;
import org.kuali.kfs.bo.LaborLedgerBalance;
import org.kuali.kfs.bo.LaborLedgerEntry;
import org.kuali.kfs.bo.LaborLedgerExpenseTransferAccountingLine;
import org.kuali.kfs.bo.LaborLedgerObject;
import org.kuali.kfs.bo.LaborLedgerPositionObjectBenefit;
import org.kuali.kfs.bo.LaborLedgerPositionObjectGroup;
import org.kuali.kfs.service.LaborModuleService;
import org.kuali.module.financial.service.UniversityDateService;
import org.kuali.module.labor.bo.LedgerBalance;
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

    private LaborBenefitsCalculationService laborBenefitsCalculationService;
    private LaborLedgerEntryService laborLedgerEntryService;
    private LaborLedgerBalanceService laborLedgerBalanceService;
    private DocumentService documentService;
    private UniversityDateService universityDateService;

    private Class<? extends LaborLedgerBalance> laborLedgerBalanceClass;
    private Class<? extends LaborLedgerEntry> laborLedgerEntryClass;
    private Class<? extends LaborLedgerObject> laborLedgerObjectClass;
    private Class<? extends LaborLedgerPositionObjectBenefit> laborLedgerPositionObjectBenefitClass;
    private Class<? extends LaborLedgerPositionObjectGroup> laborLedgerPositionObjectGroupClass;
    private Class<? extends LaborLedgerExpenseTransferAccountingLine> expenseTransferSourceAccountingLineClass;
    private Class<? extends LaborLedgerExpenseTransferAccountingLine> expenseTransferTargetAccountingLineClass;

    /**
     * @see org.kuali.kfs.service.LaborModuleService#calculateFringeBenefit(org.kuali.kfs.bo.LaborLedgerObject,
     *      org.kuali.core.util.KualiDecimal)
     */
    public KualiDecimal calculateFringeBenefit(LaborLedgerObject laborLedgerObject, KualiDecimal salaryAmount) {
        return laborBenefitsCalculationService.calculateFringeBenefit(laborLedgerObject, salaryAmount);
    }

    /**
     * @see org.kuali.module.effort.service.LaborEffortCertificationService#createLaborLedgerBalance()
     */
    public LaborLedgerBalance createLaborLedgerBalance() {
        return this.createLaborBusinessObject(laborLedgerBalanceClass);
    }

    /**
     * @see org.kuali.module.effort.service.LaborModuleService#createLaborLedgerObject()
     */
    public LaborLedgerObject createLaborLedgerObject() {
        return this.createLaborBusinessObject(laborLedgerObjectClass);
    }

    /**
     * @see org.kuali.module.effort.service.LaborModuleService#createLaborLedgerPositionObjectGroup()
     */
    public LaborLedgerPositionObjectGroup createLaborLedgerPositionObjectGroup() {
        return this.createLaborBusinessObject(laborLedgerPositionObjectGroupClass);
    }

    /**
     * @see org.kuali.kfs.service.LaborModuleService#createExpenseTransferSourceAccountingLine()
     */
    public LaborLedgerExpenseTransferAccountingLine createExpenseTransferSourceAccountingLine() {
        return this.createLaborBusinessObject(expenseTransferSourceAccountingLineClass);
    }

    /**
     * @see org.kuali.kfs.service.LaborModuleService#createExpenseTransferTargetAccountingLine()
     */
    public LaborLedgerExpenseTransferAccountingLine createExpenseTransferTargetAccountingLine() {
        return this.createLaborBusinessObject(expenseTransferTargetAccountingLineClass);
    }

    /**
     * @see org.kuali.kfs.service.LaborModuleService#createSalaryExpenseTransferDocument(java.lang.String, java.lang.String,
     *      java.util.List, java.util.List)
     */
    public void createSalaryExpenseTransferDocument(String documentDescription, String explanation, List<LaborLedgerExpenseTransferAccountingLine> sourceAccountingLines, List<LaborLedgerExpenseTransferAccountingLine> targetAccountingLines) throws WorkflowException {
        SalaryExpenseTransferDocument document = (SalaryExpenseTransferDocument) documentService.getNewDocument(SalaryExpenseTransferDocument.class);

        document.setEmplid(sourceAccountingLines.get(0).getEmplid());

        document.setSourceAccountingLines(sourceAccountingLines);
        document.setTargetAccountingLines(targetAccountingLines);

        DocumentHeader documentHeader = document.getDocumentHeader();
        documentHeader.setFinancialDocumentDescription(documentDescription);
        documentHeader.setExplanation(explanation);

        documentService.blanketApproveDocument(document, KFSConstants.EMPTY_STRING, null);
        
        LOG.info("SET has been generated and its status is: " + document.getDocumentHeader().getWorkflowDocument().getStatusDisplayValue());
    }

    /**
     * @see org.kuali.module.effort.service.LaborEffortCertificationService#findEmployeesWithPayType(java.util.Map, java.util.List,
     *      java.util.Map)
     */
    public List<String> findEmployeesWithPayType(Map<Integer, Set<String>> payPeriods, List<String> balanceTypes, Map<String, Set<String>> earnCodePayGroupMap) {
        return laborLedgerEntryService.findEmployeesWithPayType(payPeriods, balanceTypes, earnCodePayGroupMap);
    }

    /**
     * @see org.kuali.module.effort.service.LaborEffortCertificationService#findLedgerBalances(java.util.Map, java.util.Map,
     *      java.util.Set, java.util.List, java.util.List)
     */
    public Collection<LaborLedgerBalance> findLedgerBalances(Map<String, List<String>> fieldValues, Map<String, List<String>> excludedFieldValues, Set<Integer> fiscalYears, List<String> balanceTypes, List<String> positionObjectGroupCodes) {
        Collection<LaborLedgerBalance> LaborLedgerBalances = new ArrayList<LaborLedgerBalance>();

        Collection<LedgerBalance> ledgerBalances = laborLedgerBalanceService.findLedgerBalances(fieldValues, excludedFieldValues, fiscalYears, balanceTypes, positionObjectGroupCodes);
        for (LedgerBalance balance : ledgerBalances) {
            LaborLedgerBalances.add(balance);
        }
        return LaborLedgerBalances;
    }

    /**
     * @see org.kuali.kfs.service.LaborModuleService#getLaborLedgerBalanceClass()
     */
    public Class<? extends LaborLedgerBalance> getLaborLedgerBalanceClass() {
        return this.laborLedgerBalanceClass;
    }

    /**
     * @see org.kuali.kfs.service.LaborModuleService#getLaborLedgerEntryClass()
     */
    public Class<? extends LaborLedgerEntry> getLaborLedgerEntryClass() {
        return this.laborLedgerEntryClass;
    }

    /**
     * @see org.kuali.kfs.service.LaborModuleService#getLaborLedgerObjectClass()
     */
    public Class<? extends LaborLedgerObject> getLaborLedgerObjectClass() {
        return laborLedgerObjectClass;
    }

    /**
     * @see org.kuali.kfs.service.LaborModuleService#getLaborLedgerPositionObjectBenefitClass()
     */
    public Class<? extends LaborLedgerPositionObjectBenefit> getLaborLedgerPositionObjectBenefitClass() {
        return this.laborLedgerPositionObjectBenefitClass;
    }

    /**
     * @see org.kuali.kfs.service.LaborModuleService#getLaborLedgerPositionObjectGroupClass()
     */
    public Class<? extends LaborLedgerPositionObjectGroup> getLaborLedgerPositionObjectGroupClass() {
        return this.laborLedgerPositionObjectGroupClass;
    }

    /**
     * Gets the expenseTransferSourceAccountingLineClass attribute.
     * 
     * @return Returns the expenseTransferSourceAccountingLineClass.
     */
    public Class<? extends LaborLedgerExpenseTransferAccountingLine> getExpenseTransferSourceAccountingLineClass() {
        return expenseTransferSourceAccountingLineClass;
    }

    /**
     * Gets the expenseTransferTargetAccountingLineClass attribute.
     * 
     * @return Returns the expenseTransferTargetAccountingLineClass.
     */
    public Class<? extends LaborLedgerExpenseTransferAccountingLine> getExpenseTransferTargetAccountingLineClass() {
        return expenseTransferTargetAccountingLineClass;
    }

    /**
     * Sets the laborBenefitsCalculationService attribute value.
     * 
     * @param laborBenefitsCalculationService The laborBenefitsCalculationService to set.
     */
    public void setLaborBenefitsCalculationService(LaborBenefitsCalculationService laborBenefitsCalculationService) {
        this.laborBenefitsCalculationService = laborBenefitsCalculationService;
    }

    /**
     * Sets the laborLedgerBalanceClass attribute value.
     * 
     * @param laborLedgerBalanceClass The laborLedgerBalanceClass to set.
     */
    public void setLaborLedgerBalanceClass(Class<? extends LaborLedgerBalance> laborLedgerBalanceClass) {
        this.laborLedgerBalanceClass = laborLedgerBalanceClass;
    }

    /**
     * Sets the laborLedgerBalanceService attribute value.
     * 
     * @param laborLedgerBalanceService The laborLedgerBalanceService to set.
     */
    public void setLaborLedgerBalanceService(LaborLedgerBalanceService laborLedgerBalanceService) {
        this.laborLedgerBalanceService = laborLedgerBalanceService;
    }

    /**
     * Sets the documentService attribute value.
     * 
     * @param documentService The documentService to set.
     */
    public void setDocumentService(DocumentService documentService) {
        this.documentService = documentService;
    }

    /**
     * Sets the laborLedgerEntryClass attribute value.
     * 
     * @param laborLedgerEntryClass The laborLedgerEntryClass to set.
     */
    public void setLaborLedgerEntryClass(Class<? extends LaborLedgerEntry> laborLedgerEntryClass) {
        this.laborLedgerEntryClass = laborLedgerEntryClass;
    }

    /**
     * Sets the laborLedgerEntryService attribute value.
     * 
     * @param laborLedgerEntryService The laborLedgerEntryService to set.
     */
    public void setLaborLedgerEntryService(LaborLedgerEntryService laborLedgerEntryService) {
        this.laborLedgerEntryService = laborLedgerEntryService;
    }

    /**
     * Sets the laborLedgerObjectClass attribute value.
     * 
     * @param laborLedgerObjectClass The laborLedgerObjectClass to set.
     */
    public void setLaborLedgerObjectClass(Class<? extends LaborLedgerObject> laborLedgerObjectClass) {
        this.laborLedgerObjectClass = laborLedgerObjectClass;
    }

    /**
     * Sets the laborLedgerPositionObjectBenefitClass attribute value.
     * 
     * @param laborLedgerPositionObjectBenefitClass The laborLedgerPositionObjectBenefitClass to set.
     */
    public void setLaborLedgerPositionObjectBenefitClass(Class<? extends LaborLedgerPositionObjectBenefit> laborLedgerPositionObjectBenefitClass) {
        this.laborLedgerPositionObjectBenefitClass = laborLedgerPositionObjectBenefitClass;
    }

    /**
     * Sets the laborLedgerPositionObjectGroupClass attribute value.
     * 
     * @param laborLedgerPositionObjectGroupClass The laborLedgerPositionObjectGroupClass to set.
     */
    public void setLaborLedgerPositionObjectGroupClass(Class<? extends LaborLedgerPositionObjectGroup> laborLedgerPositionObjectGroupClass) {
        this.laborLedgerPositionObjectGroupClass = laborLedgerPositionObjectGroupClass;
    }

    /**
     * Sets the expenseTransferSourceAccountingLineClass attribute value.
     * 
     * @param expenseTransferSourceAccountingLineClass The expenseTransferSourceAccountingLineClass to set.
     */
    public void setExpenseTransferSourceAccountingLineClass(Class<? extends LaborLedgerExpenseTransferAccountingLine> expenseTransferSourceAccountingLineClass) {
        this.expenseTransferSourceAccountingLineClass = expenseTransferSourceAccountingLineClass;
    }

    /**
     * Sets the expenseTransferTargetAccountingLineClass attribute value.
     * 
     * @param expenseTransferTargetAccountingLineClass The expenseTransferTargetAccountingLineClass to set.
     */
    public void setExpenseTransferTargetAccountingLineClass(Class<? extends LaborLedgerExpenseTransferAccountingLine> expenseTransferTargetAccountingLineClass) {
        this.expenseTransferTargetAccountingLineClass = expenseTransferTargetAccountingLineClass;
    }

    /**
     * create an object of the specified type
     * 
     * @param clazz the specified type of the object
     * @return an object of the specified type
     */
    private <T> T createLaborBusinessObject(Class<T> clazz) {
        T businessObject = null;

        try {
            businessObject = clazz.newInstance();
        }
        catch (InstantiationException e) {
            LOG.error(e);
        }
        catch (IllegalAccessException e) {
            LOG.error(e);
        }

        return businessObject;
    }
}
