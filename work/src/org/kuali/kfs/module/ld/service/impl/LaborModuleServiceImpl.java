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
package org.kuali.kfs.module.ld.service.impl;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.commons.lang.StringUtils;
import org.kuali.kfs.coa.businessobject.ObjectCode;
import org.kuali.kfs.integration.ld.LaborLedgerBalance;
import org.kuali.kfs.integration.ld.LaborLedgerExpenseTransferAccountingLine;
import org.kuali.kfs.integration.ld.LaborLedgerObject;
import org.kuali.kfs.integration.ld.LaborLedgerPositionObjectBenefit;
import org.kuali.kfs.integration.ld.LaborLedgerPositionObjectGroup;
import org.kuali.kfs.integration.ld.LaborModuleService;
import org.kuali.kfs.module.ld.LaborPropertyConstants;
import org.kuali.kfs.module.ld.businessobject.LaborLedgerPendingEntry;
import org.kuali.kfs.module.ld.businessobject.LedgerBalance;
import org.kuali.kfs.module.ld.document.SalaryExpenseTransferDocument;
import org.kuali.kfs.module.ld.service.LaborBenefitsCalculationService;
import org.kuali.kfs.module.ld.service.LaborLedgerBalanceService;
import org.kuali.kfs.module.ld.service.LaborLedgerEntryService;
import org.kuali.kfs.module.ld.service.LaborLedgerPendingEntryService;
import org.kuali.kfs.module.ld.service.LaborOriginEntryService;
import org.kuali.kfs.sys.KFSConstants;
import org.kuali.kfs.sys.KFSPropertyConstants;
import org.kuali.kfs.sys.context.SpringContext;
import org.kuali.kfs.sys.service.UniversityDateService;
import org.kuali.rice.kew.exception.WorkflowException;
import org.kuali.rice.kns.bo.DocumentHeader;
import org.kuali.rice.kns.service.BusinessObjectService;
import org.kuali.rice.kns.service.DataDictionaryService;
import org.kuali.rice.kns.service.DocumentService;
import org.kuali.rice.kns.service.KualiModuleService;
import org.kuali.rice.kns.util.GlobalVariables;
import org.kuali.rice.kns.util.KualiDecimal;
import org.kuali.rice.kns.workflow.service.WorkflowDocumentService;
import org.springframework.transaction.annotation.Transactional;

/**
 * This implements the service methods that may be used by outside of labor module
 */
@Transactional
public class LaborModuleServiceImpl implements LaborModuleService {
    private static org.apache.log4j.Logger LOG = org.apache.log4j.Logger.getLogger(LaborModuleServiceImpl.class);

    /**
     * @see org.kuali.kfs.integration.ld.LaborModuleService#calculateFringeBenefitFromLaborObject(org.kuali.kfs.integration.ld.LaborLedgerObject,
     *      org.kuali.rice.kns.util.KualiDecimal)
     */
    public KualiDecimal calculateFringeBenefitFromLaborObject(LaborLedgerObject laborLedgerObject, KualiDecimal salaryAmount) {
        return getLaborBenefitsCalculationService().calculateFringeBenefit(laborLedgerObject, salaryAmount);
    }

    /**
     * @see org.kuali.kfs.integration.ld.LaborModuleService#calculateFringeBenefit(java.lang.Integer, java.lang.String,
     *      java.lang.String, org.kuali.rice.kns.util.KualiDecimal)
     */
    public KualiDecimal calculateFringeBenefit(Integer fiscalYear, String chartCode, String objectCode, KualiDecimal salaryAmount) {
        return getLaborBenefitsCalculationService().calculateFringeBenefit(fiscalYear, chartCode, objectCode, salaryAmount);
    }

    /**
     * @see org.kuali.kfs.integration.ld.LaborModuleService#createAndBlankApproveSalaryExpenseTransferDocument(java.lang.String,
     *      java.lang.String, java.lang.String, java.util.List, java.util.List, java.util.List)
     */
    public void createAndBlankApproveSalaryExpenseTransferDocument(String documentDescription, String explanation, String annotation, List<String> adHocRecipients, List<LaborLedgerExpenseTransferAccountingLine> sourceAccountingLines, List<LaborLedgerExpenseTransferAccountingLine> targetAccountingLines) throws WorkflowException {
        LOG.info("createSalaryExpenseTransferDocument() start");

        if (sourceAccountingLines == null || sourceAccountingLines.isEmpty()) {
            LOG.info("Cannot create a salary expense document when the given source accounting line is empty.");
            return;
        }

        if (targetAccountingLines == null || targetAccountingLines.isEmpty()) {
            LOG.info("Cannot create a salary expense document when the given target accounting line is empty.");
            return;
        }

        WorkflowDocumentService workflowDocumentService = SpringContext.getBean(WorkflowDocumentService.class);
        SalaryExpenseTransferDocument document = (SalaryExpenseTransferDocument) getDocumentService().getNewDocument(SalaryExpenseTransferDocument.class);

        document.setEmplid(sourceAccountingLines.get(0).getEmplid());
        document.setSourceAccountingLines(sourceAccountingLines);
        document.setTargetAccountingLines(targetAccountingLines);

        DocumentHeader documentHeader = document.getDocumentHeader();
        documentHeader.setDocumentDescription(documentDescription);
        documentHeader.setExplanation(explanation);

        document.prepareForSave();
        document.populateDocumentForRouting();

        String documentTitle = document.getDocumentTitle();
        if (StringUtils.isNotBlank(documentTitle)) {
            document.getDocumentHeader().getWorkflowDocument().setTitle(documentTitle);
        }

        String organizationDocumentNumber = document.getDocumentHeader().getOrganizationDocumentNumber();
        if (StringUtils.isNotBlank(organizationDocumentNumber)) {
            document.getDocumentHeader().getWorkflowDocument().setAppDocId(organizationDocumentNumber);
        }

        this.getBusinessObjectService().save(document);

        workflowDocumentService.blanketApprove(document.getDocumentHeader().getWorkflowDocument(), annotation, adHocRecipients);
        GlobalVariables.getUserSession().setWorkflowDocument(document.getDocumentHeader().getWorkflowDocument());
    }

    /**
     * @see org.kuali.kfs.integration.ld.LaborModuleService#countPendingSalaryExpenseTransfer(java.lang.String)
     */
    public int countPendingSalaryExpenseTransfer(String emplid) {
        String documentTypeCode = getDataDictionaryService().getDocumentTypeNameByClass(SalaryExpenseTransferDocument.class);

        Map<String, Object> positiveFieldValues = new HashMap<String, Object>();
        positiveFieldValues.put(KFSPropertyConstants.EMPLID, emplid);
        positiveFieldValues.put(KFSPropertyConstants.FINANCIAL_DOCUMENT_TYPE_CODE, documentTypeCode);

        List<String> approvedCodes = Arrays.asList(KFSConstants.PENDING_ENTRY_APPROVED_STATUS_CODE.APPROVED, KFSConstants.PENDING_ENTRY_APPROVED_STATUS_CODE.PROCESSED);
        Map<String, Object> negativeFieldValues = new HashMap<String, Object>();
        negativeFieldValues.put(KFSPropertyConstants.FINANCIAL_DOCUMENT_APPROVED_CODE, approvedCodes);

        return getBusinessObjectService().countMatching(LaborLedgerPendingEntry.class, positiveFieldValues, negativeFieldValues);
    }

    /**
     * @see org.kuali.kfs.integration.ld.LaborModuleService#findEmployeesWithPayType(java.util.Map, java.util.List, java.util.Map)
     */
    public List<String> findEmployeesWithPayType(Map<Integer, Set<String>> payPeriods, List<String> balanceTypes, Map<String, Set<String>> earnCodePayGroupMap) {
        return getLaborLedgerEntryService().findEmployeesWithPayType(payPeriods, balanceTypes, earnCodePayGroupMap);
    }

    /**
     * @see org.kuali.kfs.integration.ld.LaborModuleService#isEmployeeWithPayType(java.lang.String, java.util.Map, java.util.List,
     *      java.util.Map)
     */
    public boolean isEmployeeWithPayType(String emplid, Map<Integer, Set<String>> payPeriods, List<String> balanceTypes, Map<String, Set<String>> earnCodePayGroupMap) {
        return getLaborLedgerEntryService().isEmployeeWithPayType(emplid, payPeriods, balanceTypes, earnCodePayGroupMap);
    }

    /**
     * @see org.kuali.kfs.integration.ld.LaborModuleService#findLedgerBalances(java.util.Map, java.util.Map, java.util.Set,
     *      java.util.List, java.util.List)
     */
    public Collection<LaborLedgerBalance> findLedgerBalances(Map<String, List<String>> fieldValues, Map<String, List<String>> excludedFieldValues, Set<Integer> fiscalYears, List<String> balanceTypes, List<String> positionObjectGroupCodes) {
        Collection<LaborLedgerBalance> LaborLedgerBalances = new ArrayList<LaborLedgerBalance>();

        Collection<LedgerBalance> ledgerBalances = getLaborLedgerBalanceService().findLedgerBalances(fieldValues, excludedFieldValues, fiscalYears, balanceTypes, positionObjectGroupCodes);
        for (LedgerBalance balance : ledgerBalances) {
            LaborLedgerBalances.add(balance);
        }
        return LaborLedgerBalances;
    }

    public LaborLedgerPositionObjectGroup getLaborLedgerPositionObjectGroup(String positionObjectGroupCode) {
        Map<String, Object> primaryKeys = new HashMap<String, Object>();
        primaryKeys.put(LaborPropertyConstants.POSITION_OBJECT_GROUP_CODE, positionObjectGroupCode);

        return getKualiModuleService().getResponsibleModuleService(LaborLedgerPositionObjectGroup.class).getExternalizableBusinessObject(LaborLedgerPositionObjectGroup.class, primaryKeys);
    }

    /**
     * @see org.kuali.kfs.integration.service.LaborModuleService#doesLaborLedgerPositionObjectGroupExist(java.lang.String)
     */
    public boolean doesLaborLedgerPositionObjectGroupExist(String positionObjectGroupCode) {
        return this.getLaborLedgerPositionObjectGroup(positionObjectGroupCode) != null;
    }

    /**
     * @see org.kuali.kfs.integration.ld.LaborModuleService#retrieveLaborLedgerObject(java.lang.Integer, java.lang.String,
     *      java.lang.String)
     */
    public LaborLedgerObject retrieveLaborLedgerObject(Integer fiscalYear, String chartOfAccountsCode, String objectCode) {
        Map<String, Object> searchCriteria = new HashMap<String, Object>();
        searchCriteria.put(KFSPropertyConstants.UNIVERSITY_FISCAL_YEAR, fiscalYear);
        searchCriteria.put(KFSPropertyConstants.CHART_OF_ACCOUNTS_CODE, chartOfAccountsCode);
        searchCriteria.put(KFSPropertyConstants.FINANCIAL_OBJECT_CODE, objectCode);

        return getKualiModuleService().getResponsibleModuleService(LaborLedgerObject.class).getExternalizableBusinessObject(LaborLedgerObject.class, searchCriteria);
    }

    /**
     * @see org.kuali.kfs.integration.ld.LaborModuleService#retrieveLaborLedgerObject(org.kuali.kfs.coa.businessobject.ObjectCode)
     */
    public LaborLedgerObject retrieveLaborLedgerObject(ObjectCode financialObject) {
        if (financialObject == null) {
            throw new IllegalArgumentException("The given financial object cannot be null.");
        }

        Integer fiscalYear = financialObject.getUniversityFiscalYear();
        String chartOfAccountsCode = financialObject.getChartOfAccountsCode();
        String financialObjectCode = financialObject.getFinancialObjectCode();

        return this.retrieveLaborLedgerObject(fiscalYear, chartOfAccountsCode, financialObjectCode);
    }

    /**
     * @see org.kuali.kfs.integration.ld.LaborModuleService#hasPendingLaborLedgerEntry(java.lang.String, java.lang.String)
     */
    public boolean hasPendingLaborLedgerEntry(String chartOfAccountsCode, String accountNumber) {
        return getLaborLedgerPendingEntryService().hasPendingLaborLedgerEntry(chartOfAccountsCode, accountNumber);
    }

    /**
     * @see org.kuali.kfs.integration.ld.LaborModuleService#retrieveLaborPositionObjectBenefits(java.lang.Integer, java.lang.String,
     *      java.lang.String)
     */
    public List<LaborLedgerPositionObjectBenefit> retrieveLaborPositionObjectBenefits(Integer fiscalYear, String chartOfAccountsCode, String objectCode) {
        Map<String, Object> searchCriteria = new HashMap<String, Object>();

        searchCriteria.put(KFSPropertyConstants.UNIVERSITY_FISCAL_YEAR, fiscalYear);
        searchCriteria.put(KFSPropertyConstants.CHART_OF_ACCOUNTS_CODE, chartOfAccountsCode);
        searchCriteria.put(KFSPropertyConstants.FINANCIAL_OBJECT_CODE, objectCode);

        return getKualiModuleService().getResponsibleModuleService(LaborLedgerPositionObjectBenefit.class).getExternalizableBusinessObjectsList(LaborLedgerPositionObjectBenefit.class, searchCriteria);
    }

    /**
     * @see org.kuali.kfs.integration.ld.LaborModuleService#hasFringeBenefitProducingObjectCodes(java.lang.Integer,
     *      java.lang.String, java.lang.String)
     */
    public boolean hasFringeBenefitProducingObjectCodes(Integer fiscalYear, String chartOfAccountsCode, String financialObjectCode) {
        List<LaborLedgerPositionObjectBenefit> objectBenefits = this.retrieveLaborPositionObjectBenefits(fiscalYear, chartOfAccountsCode, financialObjectCode);
        return (objectBenefits != null && !objectBenefits.isEmpty());
    }

    /**
     * @see org.kuali.kfs.integration.ld.LaborModuleService#getLaborOriginEntryGroupCount(java.lang.Integer)
     */
//    public Integer getLaborOriginEntryGroupCount(Integer groupId) {
//        return getLaborOriginEntryService().getGroupCount(groupId);
//    }

    /**
     * Gets the laborBenefitsCalculationService attribute.
     * 
     * @return an implementation of the laborBenefitsCalculationService.
     */
    public LaborBenefitsCalculationService getLaborBenefitsCalculationService() {
        return SpringContext.getBean(LaborBenefitsCalculationService.class);
    }

    /**
     * Gets the laborLedgerEntryService attribute.
     * 
     * @return an implementation of the laborLedgerEntryService.
     */
    public LaborLedgerEntryService getLaborLedgerEntryService() {
        return SpringContext.getBean(LaborLedgerEntryService.class);
    }

    /**
     * Gets the laborLedgerBalanceService attribute.
     * 
     * @return an implementation of the laborLedgerBalanceService.
     */
    public LaborLedgerBalanceService getLaborLedgerBalanceService() {
        return SpringContext.getBean(LaborLedgerBalanceService.class);
    }

    /**
     * Gets the documentService attribute.
     * 
     * @return an implementation of the documentService.
     */
    public DocumentService getDocumentService() {
        return SpringContext.getBean(DocumentService.class);
    }

    /**
     * Gets the dataDictionaryService attribute.
     * 
     * @return an implementation of the dataDictionaryService.
     */
    public DataDictionaryService getDataDictionaryService() {
        return SpringContext.getBean(DataDictionaryService.class);
    }

    /**
     * Gets the universityDateService attribute.
     * 
     * @return an implementation of the universityDateService.
     */
    public UniversityDateService getUniversityDateService() {
        return SpringContext.getBean(UniversityDateService.class);
    }

    /**
     * Gets the businessObjectService attribute.
     * 
     * @return an implementation of the businessObjectService.
     */
    public BusinessObjectService getBusinessObjectService() {
        return SpringContext.getBean(BusinessObjectService.class);
    }

    /**
     * Gets the laborLedgerPendingEntryService
     * 
     * @return an implementation of the LaborLedgerPendingEntryService
     */
    public LaborLedgerPendingEntryService getLaborLedgerPendingEntryService() {
        return SpringContext.getBean(LaborLedgerPendingEntryService.class);
    }

    /**
     * Returns an instance of the LaborOriginEntryService, for use by services in the module
     * 
     * @return an instance of an implementation of the LaborOriginEntryService
     */
    public LaborOriginEntryService getLaborOriginEntryService() {
        return SpringContext.getBean(LaborOriginEntryService.class);
    }

    /**
     * Gets the KualiModuleService attribute value.
     * 
     * @return an implementation of the KualiModuleService.
     */
    public KualiModuleService getKualiModuleService() {
        return SpringContext.getBean(KualiModuleService.class);
    }
}
