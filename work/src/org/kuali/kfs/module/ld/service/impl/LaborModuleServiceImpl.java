/*
 * Copyright 2007-2008 The Kuali Foundation
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
package org.kuali.kfs.module.ld.service.impl;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.Set;

import org.apache.commons.lang.StringUtils;
import org.kuali.kfs.coa.businessobject.ObjectCode;
import org.kuali.kfs.gl.businessobject.Entry;
import org.kuali.kfs.integration.ld.LaborLedgerBalance;
import org.kuali.kfs.integration.ld.LaborLedgerExpenseTransferAccountingLine;
import org.kuali.kfs.integration.ld.LaborLedgerObject;
import org.kuali.kfs.integration.ld.LaborLedgerPositionObjectBenefit;
import org.kuali.kfs.integration.ld.LaborLedgerPositionObjectGroup;
import org.kuali.kfs.integration.ld.LaborModuleService;
import org.kuali.kfs.module.ld.LaborPropertyConstants;
import org.kuali.kfs.module.ld.businessobject.LaborLedgerPendingEntry;
import org.kuali.kfs.module.ld.businessobject.LedgerBalance;
import org.kuali.kfs.module.ld.businessobject.LedgerEntry;
import org.kuali.kfs.module.ld.businessobject.LedgerEntryGLSummary;
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
import org.kuali.rice.core.api.util.type.KualiDecimal;
import org.kuali.rice.coreservice.framework.parameter.ParameterService;
import org.kuali.rice.kew.api.KewApiConstants;
import org.kuali.rice.kew.api.KewApiServiceLocator;
import org.kuali.rice.kew.api.document.WorkflowDocumentService;
import org.kuali.rice.kew.api.exception.WorkflowException;
import org.kuali.rice.kns.lookup.HtmlData;
import org.kuali.rice.kns.lookup.HtmlData.AnchorHtmlData;
import org.kuali.rice.kns.service.DataDictionaryService;
import org.kuali.rice.krad.bo.AdHocRoutePerson;
import org.kuali.rice.krad.bo.AdHocRouteRecipient;
import org.kuali.rice.krad.bo.DocumentHeader;
import org.kuali.rice.krad.rules.rule.event.SaveDocumentEvent;
import org.kuali.rice.krad.service.BusinessObjectService;
import org.kuali.rice.krad.service.DocumentService;
import org.kuali.rice.krad.service.KualiModuleService;
import org.kuali.rice.krad.service.SessionDocumentService;
import org.kuali.rice.krad.util.GlobalVariables;
import org.kuali.rice.krad.util.UrlFactory;
import org.springframework.transaction.annotation.Transactional;

/**
 * This implements the service methods that may be used by outside of labor module
 */
@Transactional
public class LaborModuleServiceImpl implements LaborModuleService {
    private static org.apache.log4j.Logger LOG = org.apache.log4j.Logger.getLogger(LaborModuleServiceImpl.class);

    private final static String LINK_DOCUMENT_NUMBER_TO_LABOR_ORIGIN_CODES_PARAM_NAME = "LINK_DOCUMENT_NUMBER_TO_LABOR_ORIGIN_CODES";
    private final static String GL_LABOR_ENTRY_SUMMARIZATION_INQUIRY_BASE_URL = "laborGLLaborEntrySummarizationInquiry.do";
    private final static String GL_LABOR_ENTRY_SUMMARIZATION_INQUIRY_METHOD = "viewResults";

    /**
     * @see org.kuali.kfs.integration.ld.LaborModuleService#calculateFringeBenefitFromLaborObject(org.kuali.kfs.integration.ld.LaborLedgerObject,
     *      org.kuali.rice.core.api.util.type.KualiDecimal)
     */
    @Override
    public KualiDecimal calculateFringeBenefitFromLaborObject(LaborLedgerObject laborLedgerObject, KualiDecimal salaryAmount, String accountNumber, String subAccountNumber) {
        return getLaborBenefitsCalculationService().calculateFringeBenefit(laborLedgerObject, salaryAmount, accountNumber, subAccountNumber);
    }

    /**
     * @see org.kuali.kfs.integration.ld.LaborModuleService#calculateFringeBenefit(java.lang.Integer, java.lang.String,
     *      java.lang.String, org.kuali.rice.core.api.util.type.KualiDecimal)
     */
    @Override
    public KualiDecimal calculateFringeBenefit(Integer fiscalYear, String chartCode, String objectCode, KualiDecimal salaryAmount, String accountNumber, String subAccountNumber) {
        return getLaborBenefitsCalculationService().calculateFringeBenefit(fiscalYear, chartCode, objectCode, salaryAmount, accountNumber, subAccountNumber);
    }

    /**
     * @see org.kuali.kfs.integration.ld.LaborModuleService#createAndBlankApproveSalaryExpenseTransferDocument(java.lang.String,
     *      java.lang.String, java.lang.String, java.util.List, java.util.List, java.util.List)
     */
    @Override
    public void createAndBlankApproveSalaryExpenseTransferDocument(String documentDescription, String explanation, String annotation, List<String> adHocRecipients, List<LaborLedgerExpenseTransferAccountingLine> sourceAccountingLines, List<LaborLedgerExpenseTransferAccountingLine> targetAccountingLines) throws WorkflowException {
        LOG.debug("createSalaryExpenseTransferDocument() start");

        if (sourceAccountingLines == null || sourceAccountingLines.isEmpty()) {
            LOG.info("Cannot create a salary expense document when the given source accounting line is empty.");
            return;
        }

        if (targetAccountingLines == null || targetAccountingLines.isEmpty()) {
            LOG.info("Cannot create a salary expense document when the given target accounting line is empty.");
            return;
        }

        WorkflowDocumentService workflowDocumentService = KewApiServiceLocator.getWorkflowDocumentService();
        SalaryExpenseTransferDocument document = (SalaryExpenseTransferDocument) getDocumentService().getNewDocument(SalaryExpenseTransferDocument.class);

        document.setEmplid(sourceAccountingLines.get(0).getEmplid());
        document.setSourceAccountingLines(sourceAccountingLines);
        document.setTargetAccountingLines(targetAccountingLines);

        DocumentHeader documentHeader = document.getDocumentHeader();
        documentHeader.setDocumentDescription(documentDescription);
        documentHeader.setExplanation(explanation);

        document.prepareForSave(new SaveDocumentEvent(document));
        document.populateDocumentForRouting();

        String documentTitle = document.getDocumentTitle();
        if (StringUtils.isNotBlank(documentTitle)) {
            document.getDocumentHeader().getWorkflowDocument().setTitle(documentTitle);
        }

        String organizationDocumentNumber = document.getDocumentHeader().getOrganizationDocumentNumber();
        if (StringUtils.isNotBlank(organizationDocumentNumber)) {
            document.getDocumentHeader().getWorkflowDocument().setApplicationDocumentId(organizationDocumentNumber);
        }

        this.getBusinessObjectService().save(document);

        List<AdHocRouteRecipient> adHocRecipientList = new ArrayList<AdHocRouteRecipient>();

        for (String adHocRouteRecipient : adHocRecipients) {
            adHocRecipientList.add(this.buildApprovePersonRecipient(adHocRouteRecipient));
         }

        SpringContext.getBean(DocumentService.class).blanketApproveDocument(document, annotation, adHocRecipientList);
        SpringContext.getBean(SessionDocumentService.class).addDocumentToUserSession(GlobalVariables.getUserSession(), document.getDocumentHeader().getWorkflowDocument());

    }

    /**
     *
     * This method builds a recipient for Approval.
     * @param userId
     * @return
     */
    protected AdHocRouteRecipient buildApprovePersonRecipient(String userId) {
        AdHocRouteRecipient adHocRouteRecipient = new AdHocRoutePerson();
        adHocRouteRecipient.setActionRequested(KewApiConstants.ACTION_REQUEST_APPROVE_REQ);
        adHocRouteRecipient.setId(userId);
        return adHocRouteRecipient;
    }

    /**
     * @see org.kuali.kfs.integration.ld.LaborModuleService#countPendingSalaryExpenseTransfer(java.lang.String)
     */
    @Override
    public int countPendingSalaryExpenseTransfer(String emplid) {
        Map<String, Object> positiveFieldValues = new HashMap<String, Object>();
        positiveFieldValues.put(KFSPropertyConstants.EMPLID, emplid);
        positiveFieldValues.put(KFSPropertyConstants.FINANCIAL_DOCUMENT_TYPE_CODE, KFSConstants.FinancialDocumentTypeCodes.SALARY_EXPENSE_TRANSFER);

        List<String> approvedCodes = Arrays.asList(KFSConstants.PENDING_ENTRY_APPROVED_STATUS_CODE.APPROVED, KFSConstants.PENDING_ENTRY_APPROVED_STATUS_CODE.PROCESSED);
        Map<String, Object> negativeFieldValues = new HashMap<String, Object>();
        negativeFieldValues.put(KFSPropertyConstants.FINANCIAL_DOCUMENT_APPROVED_CODE, approvedCodes);

        return getBusinessObjectService().countMatching(LaborLedgerPendingEntry.class, positiveFieldValues, negativeFieldValues);
    }

    /**
     * @see org.kuali.kfs.integration.ld.LaborModuleService#findEmployeesWithPayType(java.util.Map, java.util.List, java.util.Map)
     */
    @Override
    public List<String> findEmployeesWithPayType(Map<Integer, Set<String>> payPeriods, List<String> balanceTypes, Map<String, Set<String>> earnCodePayGroupMap) {
        return getLaborLedgerEntryService().findEmployeesWithPayType(payPeriods, balanceTypes, earnCodePayGroupMap);
    }

    /**
     * @see org.kuali.kfs.integration.ld.LaborModuleService#isEmployeeWithPayType(java.lang.String, java.util.Map, java.util.List,
     *      java.util.Map)
     */
    @Override
    public boolean isEmployeeWithPayType(String emplid, Map<Integer, Set<String>> payPeriods, List<String> balanceTypes, Map<String, Set<String>> earnCodePayGroupMap) {
        return getLaborLedgerEntryService().isEmployeeWithPayType(emplid, payPeriods, balanceTypes, earnCodePayGroupMap);
    }

    /**
     * @see org.kuali.kfs.integration.ld.LaborModuleService#findLedgerBalances(java.util.Map, java.util.Map, java.util.Set,
     *      java.util.List, java.util.List)
     */
    @Override
    public Collection<LaborLedgerBalance> findLedgerBalances(Map<String, Collection<String>> fieldValues, Map<String, Collection<String>> excludedFieldValues, Set<Integer> fiscalYears, List<String> balanceTypes, List<String> positionObjectGroupCodes) {
        Collection<LaborLedgerBalance> LaborLedgerBalances = new ArrayList<LaborLedgerBalance>();

        Map<String, List<String>> excludedFieldValueList = new HashMap<String,List<String>>();
        for ( Map.Entry<String, Collection<String>> e : excludedFieldValues.entrySet()) {
            // convert collection to list
            List<String> list = new ArrayList<String>(e.getValue());
            Collections.sort(list);
            excludedFieldValueList.put(e.getKey(), list);
        }
        Map<String, List<String>> fieldValueList =new HashMap<String,List<String>>();
        for ( Map.Entry<String, Collection<String>> e : fieldValues.entrySet()) {
            // convert collection to list
            List<String> list = new ArrayList<String>(e.getValue());
            Collections.sort(list);
            fieldValueList.put(e.getKey(), list);
        }
        Collection<LedgerBalance> ledgerBalances = getLaborLedgerBalanceService().findLedgerBalances(fieldValueList, excludedFieldValueList, fiscalYears, balanceTypes, positionObjectGroupCodes);
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
    @Override
    public boolean doesLaborLedgerPositionObjectGroupExist(String positionObjectGroupCode) {
        return this.getLaborLedgerPositionObjectGroup(positionObjectGroupCode) != null;
    }

    /**
     * @see org.kuali.kfs.integration.ld.LaborModuleService#retrieveLaborLedgerObject(java.lang.Integer, java.lang.String,
     *      java.lang.String)
     */
    @Override
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
    @Override
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
    @Override
    public boolean hasPendingLaborLedgerEntry(String chartOfAccountsCode, String accountNumber) {
        return getLaborLedgerPendingEntryService().hasPendingLaborLedgerEntry(chartOfAccountsCode, accountNumber);
    }

    /**
     * @see org.kuali.kfs.integration.ld.LaborModuleService#retrieveLaborPositionObjectBenefits(java.lang.Integer, java.lang.String,
     *      java.lang.String)
     */
    @Override
    public List<LaborLedgerPositionObjectBenefit> retrieveLaborPositionObjectBenefits(Integer fiscalYear, String chartOfAccountsCode, String objectCode) {
        Map<String, Object> searchCriteria = new HashMap<String, Object>();

        searchCriteria.put(KFSPropertyConstants.UNIVERSITY_FISCAL_YEAR, fiscalYear);
        searchCriteria.put(KFSPropertyConstants.CHART_OF_ACCOUNTS_CODE, chartOfAccountsCode);
        searchCriteria.put(KFSPropertyConstants.FINANCIAL_OBJECT_CODE, objectCode);
        return getKualiModuleService().getResponsibleModuleService(LaborLedgerPositionObjectBenefit.class).getExternalizableBusinessObjectsList(LaborLedgerPositionObjectBenefit.class, searchCriteria);
    }

    public List<LaborLedgerPositionObjectBenefit> retrieveActiveLaborPositionObjectBenefits(Integer fiscalYear, String chartOfAccountsCode, String objectCode) {
        Map<String, Object> searchCriteria = new HashMap<String, Object>();

        searchCriteria.put(KFSPropertyConstants.UNIVERSITY_FISCAL_YEAR, fiscalYear);
        searchCriteria.put(KFSPropertyConstants.CHART_OF_ACCOUNTS_CODE, chartOfAccountsCode);
        searchCriteria.put(KFSPropertyConstants.FINANCIAL_OBJECT_CODE, objectCode);
        searchCriteria.put(KFSPropertyConstants.ACTIVE, KFSConstants.ACTIVE_INDICATOR);

        return getKualiModuleService().getResponsibleModuleService(LaborLedgerPositionObjectBenefit.class).getExternalizableBusinessObjectsList(LaborLedgerPositionObjectBenefit.class, searchCriteria);
    }

    /**
     * @see org.kuali.kfs.integration.ld.LaborModuleService#hasFringeBenefitProducingObjectCodes(java.lang.Integer,
     *      java.lang.String, java.lang.String)
     */
    @Override
    public boolean hasFringeBenefitProducingObjectCodes(Integer fiscalYear, String chartOfAccountsCode, String financialObjectCode) {
        List<LaborLedgerPositionObjectBenefit> objectBenefits = this.retrieveActiveLaborPositionObjectBenefits(fiscalYear, chartOfAccountsCode, financialObjectCode);
        return (objectBenefits != null && !objectBenefits.isEmpty());
    }

    /**
     * @see org.kuali.kfs.integration.ld.LaborModuleService#getLaborOriginEntryGroupCount(java.lang.Integer)
     */
//    public Integer getLaborOriginEntryGroupCount(Integer groupId) {
//        return getLaborOriginEntryService().getGroupCount(groupId);
//    }

    /**
     * Looks up the origin codes from the parameter KFS-LD / LedgerEntry / LINK_DOCUMENT_NUMBER_TO_LABOR_ORIGIN_CODES
     * @see org.kuali.kfs.integration.ld.LaborModuleService#getLaborLedgerGLOriginCodes()
     */
    @Override
    public Collection<String> getLaborLedgerGLOriginCodes() {
        return getParameterService().getParameterValuesAsString(LedgerEntry.class, LINK_DOCUMENT_NUMBER_TO_LABOR_ORIGIN_CODES_PARAM_NAME);
    }

    /**
     * Builds the url for the given GL entry to go to inquiry screen for related LD entries
     * @see org.kuali.kfs.integration.ld.LaborModuleService#getInquiryUrlForGeneralLedgerEntryDocumentNumber(org.kuali.kfs.gl.businessobject.Entry)
     */
    @Override
    public HtmlData getInquiryUrlForGeneralLedgerEntryDocumentNumber(Entry entry) {
        Properties props = new Properties();
        props.setProperty(KFSConstants.DISPATCH_REQUEST_PARAMETER, GL_LABOR_ENTRY_SUMMARIZATION_INQUIRY_METHOD);
        props.setProperty(KFSPropertyConstants.UNIVERSITY_FISCAL_YEAR, entry.getUniversityFiscalYear().toString());
        props.setProperty(KFSPropertyConstants.UNIVERSITY_FISCAL_PERIOD_CODE, entry.getUniversityFiscalPeriodCode());
        props.setProperty(KFSPropertyConstants.CHART_OF_ACCOUNTS_CODE, entry.getChartOfAccountsCode());
        props.setProperty(KFSPropertyConstants.ACCOUNT_NUMBER, entry.getAccountNumber());
        props.setProperty(KFSPropertyConstants.SUB_ACCOUNT_NUMBER, entry.getSubAccountNumber());
        props.setProperty(KFSPropertyConstants.FINANCIAL_OBJECT_CODE, entry.getFinancialObjectCode());
        props.setProperty(KFSPropertyConstants.FINANCIAL_SUB_OBJECT_CODE, entry.getFinancialSubObjectCode());
        props.setProperty(KFSPropertyConstants.FINANCIAL_BALANCE_TYPE_CODE, entry.getFinancialBalanceTypeCode());
        props.setProperty(KFSPropertyConstants.FINANCIAL_OBJECT_TYPE_CODE, entry.getFinancialObjectTypeCode());
        props.setProperty(KFSPropertyConstants.FINANCIAL_DOCUMENT_TYPE_CODE, entry.getFinancialDocumentTypeCode());
        props.setProperty(KFSPropertyConstants.FINANCIAL_SYSTEM_ORIGINATION_CODE, entry.getFinancialSystemOriginationCode());
        props.setProperty(KFSPropertyConstants.DOCUMENT_NUMBER, entry.getDocumentNumber());
        props.setProperty(KFSConstants.BUSINESS_OBJECT_CLASS_ATTRIBUTE, LedgerEntryGLSummary.class.getName());
        HtmlData htmlData = new AnchorHtmlData(UrlFactory.parameterizeUrl(GL_LABOR_ENTRY_SUMMARIZATION_INQUIRY_BASE_URL, props), entry.getDocumentNumber());
        return htmlData;
    }

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
     * @return the default implementation of the ParameterService
     */
    public ParameterService getParameterService() {
        return SpringContext.getBean(ParameterService.class);
    }

    /**
     * Gets the KualiModuleService attribute value.
     *
     * @return an implementation of the KualiModuleService.
     */
    public KualiModuleService getKualiModuleService() {
        return SpringContext.getBean(KualiModuleService.class);
    }

    @Override
    public String getBenefitRateCategoryCode(String chartOfAccountsCode, String accountNumber, String subAccountNumber) {
            return getLaborBenefitsCalculationService().getBenefitRateCategoryCode(chartOfAccountsCode, accountNumber, subAccountNumber);
    }

    @Override
    public String getCostSharingSourceAccountNumber() {
         return getLaborBenefitsCalculationService().getCostSharingSourceAccountNumber();
    }

    @Override
    public String getCostSharingSourceSubAccountNumber() {
        return getLaborBenefitsCalculationService().getCostSharingSourceSubAccountNumber();
    }

    @Override
    public String getCostSharingSourceChartOfAccountsCode() {
        return getLaborBenefitsCalculationService().getCostSharingSourceAccountChartOfAccountsCode();
    }
}
