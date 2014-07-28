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
package org.kuali.kfs.module.ar.businessobject.lookup;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.commons.lang.StringUtils;
import org.kuali.kfs.integration.cg.ContractsAndGrantsAward;
import org.kuali.kfs.integration.cg.ContractsAndGrantsBillingAward;
import org.kuali.kfs.integration.cg.ContractsAndGrantsModuleBillingService;
import org.kuali.kfs.module.ar.ArConstants;
import org.kuali.kfs.module.ar.ArPropertyConstants;
import org.kuali.kfs.module.ar.businessobject.ContractsGrantsInvoiceReport;
import org.kuali.kfs.module.ar.businessobject.ContractsGrantsSuspendedInvoiceDetailReport;
import org.kuali.kfs.module.ar.businessobject.InvoiceSuspensionCategory;
import org.kuali.kfs.module.ar.businessobject.SuspensionCategory;
import org.kuali.kfs.module.ar.document.ContractsGrantsInvoiceDocument;
import org.kuali.kfs.sys.KFSConstants;
import org.kuali.kfs.sys.KFSPropertyConstants;
import org.kuali.kfs.sys.document.service.FinancialSystemDocumentService;
import org.kuali.rice.core.api.config.property.ConfigContext;
import org.kuali.rice.kew.api.KewApiConstants;
import org.kuali.rice.kew.api.document.DocumentStatus;
import org.kuali.rice.kim.api.KimConstants;
import org.kuali.rice.kim.api.identity.Person;
import org.kuali.rice.kim.api.identity.PersonService;
import org.kuali.rice.kns.document.authorization.BusinessObjectRestrictions;
import org.kuali.rice.kns.lookup.HtmlData;
import org.kuali.rice.kns.lookup.HtmlData.AnchorHtmlData;
import org.kuali.rice.kns.web.comparator.CellComparatorHelper;
import org.kuali.rice.kns.web.struts.form.LookupForm;
import org.kuali.rice.kns.web.ui.Column;
import org.kuali.rice.kns.web.ui.ResultRow;
import org.kuali.rice.krad.bo.BusinessObject;
import org.kuali.rice.krad.util.GlobalVariables;
import org.kuali.rice.krad.util.KRADConstants;
import org.kuali.rice.krad.util.ObjectUtils;

/**
 * Defines a lookupable helper class for Suspended Invoice Detail Report.
 */
public class ContractsGrantsSuspendedInvoiceDetailReportLookupableHelperServiceImpl extends ContractsGrantsReportLookupableHelperServiceImplBase {
    private org.apache.log4j.Logger LOG = org.apache.log4j.Logger.getLogger(ContractsGrantsSuspendedInvoiceDetailReportLookupableHelperServiceImpl.class);
    protected FinancialSystemDocumentService financialSystemDocumentService;
    protected PersonService personService;
    protected ContractsAndGrantsModuleBillingService contractsAndGrantsModuleBillingService;

    /**
     * This method performs the lookup and returns a collection of lookup items
     *
     * @param lookupForm
     * @param kualiLookupable
     * @param resultTable
     * @param bounded
     * @return
     */
    @Override
    public Collection performLookup(LookupForm lookupForm, Collection resultTable, boolean bounded) {
        Map lookupFormFields = lookupForm.getFieldsForLookup();

        setBackLocation((String) lookupForm.getFieldsForLookup().get(KRADConstants.BACK_LOCATION));
        setDocFormKey((String) lookupForm.getFieldsForLookup().get(KRADConstants.DOC_FORM_KEY));

        List<ContractsGrantsSuspendedInvoiceDetailReport> displayList = new ArrayList<ContractsGrantsSuspendedInvoiceDetailReport>();

        final String suspensionCategoryCode = (String)lookupFormFields.remove(ArPropertyConstants.SuspensionCategory.SUSPENSION_CATEGORY_CODE);
        final Set<String> suspensionCategoryCodes = retrieveMatchingSuspensionCategories(suspensionCategoryCode);

        final List<? extends ContractsAndGrantsAward> matchingAwards = lookupMatchingAwards(lookupFormFields);

        if (matchingAwards != null) { // null means that no award-based criteria were used in the search and therefore, these values should not be used for document selection
            if (matchingAwards.isEmpty()) { //we searched on awards, but didn't find any.  So we can't find any matching documents
                return displayList;
            }

            final String proposalNumbers = harvestIdsFromAwards(matchingAwards);
            if (!StringUtils.isBlank(proposalNumbers)) {
                lookupFormFields.put(ArConstants.PROPOSAL_NUMBER, proposalNumbers);
            }
        }

        List<String> processingDocumentStatuses = new ArrayList<String>();
        processingDocumentStatuses.addAll(getFinancialSystemDocumentService().getPendingDocumentStatuses());
        processingDocumentStatuses.add(DocumentStatus.PROCESSED.getCode());
        final String processingDocumentStatusesForLookup = StringUtils.join(processingDocumentStatuses, "|");
        lookupFormFields.put(KFSPropertyConstants.DOCUMENT_HEADER+"."+KFSPropertyConstants.WORKFLOW_DOCUMENT_STATUS_CODE, processingDocumentStatusesForLookup);
        Collection<ContractsGrantsInvoiceDocument> cgInvoiceDocuments = getLookupService().findCollectionBySearchHelper(ContractsGrantsInvoiceDocument.class, lookupFormFields, true);

        for (ContractsGrantsInvoiceDocument cgInvoiceDoc : cgInvoiceDocuments) {
            if (!ObjectUtils.isNull(cgInvoiceDoc.getInvoiceSuspensionCategories()) && !cgInvoiceDoc.getInvoiceSuspensionCategories().isEmpty()) { // only report on documents which have suspension categories associated
                for (InvoiceSuspensionCategory invoiceSuspensionCategory : cgInvoiceDoc.getInvoiceSuspensionCategories()) {
                    if (suspensionCategoryCodes.isEmpty() || suspensionCategoryCodes.contains(invoiceSuspensionCategory.getSuspensionCategoryCode())) {
                        ContractsGrantsSuspendedInvoiceDetailReport cgSuspendedInvoiceDetailReport = new ContractsGrantsSuspendedInvoiceDetailReport();
                        cgSuspendedInvoiceDetailReport.setSuspensionCategoryCode(invoiceSuspensionCategory.getSuspensionCategoryCode());
                        cgSuspendedInvoiceDetailReport.setDocumentNumber(cgInvoiceDoc.getDocumentNumber());
                        cgSuspendedInvoiceDetailReport.setLetterOfCreditFundGroupCode(null);
                        if (ObjectUtils.isNotNull(cgInvoiceDoc.getAward())) {
                            if (ObjectUtils.isNotNull(cgInvoiceDoc.getAward().getLetterOfCreditFund())) {
                                cgSuspendedInvoiceDetailReport.setLetterOfCreditFundGroupCode(cgInvoiceDoc.getAward().getLetterOfCreditFund().getLetterOfCreditFundGroupCode());
                            }
                        }
                        ContractsAndGrantsBillingAward award = cgInvoiceDoc.getAward();
                        Person fundManager = award.getAwardPrimaryFundManager().getFundManager();
                        String fundManagerPrincipalName = fundManager.getPrincipalName();

                        Person projectDirector = award.getAwardPrimaryProjectDirector().getProjectDirector();
                        String projectDirectorPrincipalName = projectDirector.getPrincipalName();

                        cgSuspendedInvoiceDetailReport.setAwardFundManager(fundManager);
                        cgSuspendedInvoiceDetailReport.setAwardProjectDirector(projectDirector);
                        cgSuspendedInvoiceDetailReport.setFundManagerPrincipalName(fundManagerPrincipalName);
                        cgSuspendedInvoiceDetailReport.setProjectDirectorPrincipalName(projectDirectorPrincipalName);

                        cgSuspendedInvoiceDetailReport.setAwardTotal(award.getAwardTotalAmount());

                        displayList.add(cgSuspendedInvoiceDetailReport);
                    }
                }
            }
        }

        buildResultTable(lookupForm, displayList, resultTable);
        return displayList;
    }

    /**
     * The suspension category code input on the form may use wild cards, so we'll do a lookup on matching suspension categories
     * @param suspensionCategoryCode the code String to lookup - it may control wildcards
     * @return a Set of the matching suspension category code Strings
     */
    protected Set<String> retrieveMatchingSuspensionCategories(String suspensionCategoryCode) {
        Set<String> matchingSuspensionCodes = new HashSet<String>();

        if (!StringUtils.isBlank(suspensionCategoryCode)) {
            Map<String, String> fields = new HashMap<String, String>();
            fields.put(ArPropertyConstants.SuspensionCategory.SUSPENSION_CATEGORY_CODE, suspensionCategoryCode);
            final Collection<SuspensionCategory> suspensionCategories = getLookupService().findCollectionBySearchHelper(SuspensionCategory.class, fields, true);
            for (SuspensionCategory suspensionCategory : suspensionCategories) {
                matchingSuspensionCodes.add(suspensionCategory.getSuspensionCategoryCode());
            }
        }
        return matchingSuspensionCodes;
    }

    /**
     * Substitutes the fields on the lookup to match what the ORM is actually expecting
     * @param lookupFields the lookup fields to substitute column names for prior to the lookup
     */
    protected List<? extends ContractsAndGrantsAward> lookupMatchingAwards(@SuppressWarnings("rawtypes") Map lookupFields) {
        Map<String, String> awardLookupFields = new HashMap<String, String>();
        // letterOfCreditFundGroupCode should be award.letterOfCreditFund.letterOfCreditFundGroupCode
        final String letterOfCreditFundGroupCode = (String)lookupFields.remove(ArConstants.LETTER_OF_CREDIT_FUND_GROUP_CODE);
        if (!StringUtils.isBlank(letterOfCreditFundGroupCode)) {
            awardLookupFields.put(ArConstants.LETTER_OF_CREDIT_FUND+"."+ArConstants.LETTER_OF_CREDIT_FUND_GROUP_CODE, letterOfCreditFundGroupCode);
        }

        // awardTotal should be award.awardTotalAmount
        final String awardTotal = (String)lookupFields.remove(ArConstants.AWARD_TOTAL);
        if (!StringUtils.isBlank(awardTotal)) {
            awardLookupFields.put(ArConstants.AWARD_TOTAL, awardTotal);
        }

        // awardFundManager.principalName should be award.awardPrimaryFundManager.fundManager.principalId
        final String fundManagerPrincipalName = (String)lookupFields.remove(ArConstants.AWARD_FUND_MANAGER+"."+KimConstants.UniqueKeyConstants.PRINCIPAL_NAME);
        final Set<String> fundManagerPrincipalIds = lookupPrincipalIds(fundManagerPrincipalName);
        if (!fundManagerPrincipalIds.isEmpty()) {
            final String joinedFundManagerPrincipalIds = StringUtils.join(fundManagerPrincipalIds, "|");
            awardLookupFields.put(ArConstants.AWARD_FUND_MANAGERS+"."+KFSPropertyConstants.PRINCIPAL_ID, joinedFundManagerPrincipalIds);
        }

        // awardProjectDirector.principalName should be award.awardPrimaryProjectDirector.projectDirector.principalId
        final String projectDirectorPrincipalName = (String)lookupFields.remove(ArConstants.AWARD_PROJECT_DIRECTOR+"."+KimConstants.UniqueKeyConstants.PRINCIPAL_NAME);
        final Set<String> projectDirectorPrincipalIds = lookupPrincipalIds(projectDirectorPrincipalName);
        if (!projectDirectorPrincipalIds.isEmpty()) {
            final String joinedProjectDirectorPrincipalIds = StringUtils.join(projectDirectorPrincipalIds, "|");
            awardLookupFields.put(ArConstants.AWARD_PROJECT_DIRECTORS+"."+KFSPropertyConstants.PRINCIPAL_ID, joinedProjectDirectorPrincipalIds);
        }

        if (awardLookupFields.isEmpty()) {
            return null; // nothing to lookup, so send back null to signal that we shouldn't even filter based on awards
        }

        // add active check
        awardLookupFields.put(KFSPropertyConstants.ACTIVE, KFSConstants.ACTIVE_INDICATOR);

        final List<? extends ContractsAndGrantsAward> awards = getContractsAndGrantsModuleBillingService().lookupAwards(awardLookupFields, true);

        // filter awards - we can't get back only the primary project director or fund manager, but we can filter down in the query
        // here, let's make sure the primary project director or fund manager for each award matches
        List<ContractsAndGrantsAward> filteredAwards = new ArrayList<ContractsAndGrantsAward>();
        if (fundManagerPrincipalIds.isEmpty() && projectDirectorPrincipalIds.isEmpty()) {
            filteredAwards.addAll(awards); // nothing to filter out
        }
        else {
            for (ContractsAndGrantsAward award : awards) {
                if (award instanceof ContractsAndGrantsBillingAward) {
                    final ContractsAndGrantsBillingAward billingAward = (ContractsAndGrantsBillingAward)award;
                    if (!fundManagerPrincipalIds.isEmpty() && !ObjectUtils.isNull(billingAward.getAwardPrimaryFundManager()) && !fundManagerPrincipalIds.contains(billingAward.getAwardPrimaryFundManager().getPrincipalId())) {
                        continue;
                    }
                    if (!projectDirectorPrincipalIds.isEmpty() && !ObjectUtils.isNull(billingAward.getAwardPrimaryProjectDirector()) && !projectDirectorPrincipalIds.contains(billingAward.getAwardPrimaryProjectDirector().getPrincipalId())) {
                        continue;
                    }
                    filteredAwards.add(award);
                }
            }
        }

        return filteredAwards;
    }

    /**
     * Harvests the proposal ids from the given awards and builds a lookup ready String of proposal id's
     * @param awards the awards to harvest ids from
     * @return a lookup ready String of proposal id's, or'd together
     */
    protected String harvestIdsFromAwards(List<? extends ContractsAndGrantsAward> awards) {
        if (awards.isEmpty()) {
            return KFSConstants.EMPTY_STRING;
        }

        Set<String> proposalIdsSet = new HashSet<String>();
        for (ContractsAndGrantsAward award : awards) {
            proposalIdsSet.add(award.getProposalNumber().toString());
        }
        final String proposalIdsForLookup = StringUtils.join(proposalIdsSet, "|");
        return proposalIdsForLookup;
    }

    /**
     * Does a lookup on the given principal name and joins the principal ids of any matches together as an or'd String, ready for another lookup
     * @param principalName principalName to find matches for
     * @return a Set of matching principalIds
     */
    protected Set<String> lookupPrincipalIds(String principalName) {
        if (StringUtils.isBlank(principalName)) {
            return new HashSet<String>();
        }

        Map<String, String> fieldValues = new HashMap<String, String>();
        fieldValues.put(KimConstants.UniqueKeyConstants.PRINCIPAL_NAME, principalName);
        final Collection<Person> peoples = getPersonService().findPeople(fieldValues);

        if (peoples == null || peoples.isEmpty()) {
            return new HashSet<String>();
        }

        Set<String> principalIdsSet = new HashSet<String>();
        for (Person person : peoples) {
            principalIdsSet.add(person.getPrincipalId());
        }

        return principalIdsSet;
    }

    @Override
    protected void buildResultTable(LookupForm lookupForm, Collection displayList, Collection resultTable) {
        Person user = GlobalVariables.getUserSession().getPerson();
        boolean hasReturnableRow = false;
        // iterate through result list and wrap rows with return url and action urls
        for (Iterator iter = displayList.iterator(); iter.hasNext();) {
            BusinessObject element = (BusinessObject) iter.next();

            BusinessObjectRestrictions businessObjectRestrictions = getBusinessObjectAuthorizationService().getLookupResultRestrictions(element, user);

            List<Column> columns = getColumns();
            for (Iterator iterator = columns.iterator(); iterator.hasNext();) {
                Column col = (Column) iterator.next();

                String propValue = ObjectUtils.getFormattedPropertyValue(element, col.getPropertyName(), col.getFormatter());
                Class propClass = getPropertyClass(element, col.getPropertyName());

                col.setComparator(CellComparatorHelper.getAppropriateComparatorForPropertyClass(propClass));
                col.setValueComparator(CellComparatorHelper.getAppropriateValueComparatorForPropertyClass(propClass));

                String propValueBeforePotientalMasking = propValue;
                propValue = maskValueIfNecessary(element.getClass(), col.getPropertyName(), propValue, businessObjectRestrictions);
                col.setPropertyValue(propValue);

                // Add url when property is documentNumber
                if (col.getPropertyName().equals("documentNumber")) {
                    String url = ConfigContext.getCurrentContextConfig().getKEWBaseURL() + "/" + KewApiConstants.DOC_HANDLER_REDIRECT_PAGE + "?" + KewApiConstants.COMMAND_PARAMETER + "=" + KewApiConstants.DOCSEARCH_COMMAND + "&" + KewApiConstants.DOCUMENT_ID_PARAMETER + "=" + propValue;

                    Map<String, String> fieldList = new HashMap<String, String>();
                    fieldList.put(KFSPropertyConstants.DOCUMENT_NUMBER, propValue);
                    AnchorHtmlData a = new AnchorHtmlData(url, KRADConstants.EMPTY_STRING);
                    a.setTitle(HtmlData.getTitleText(getContractsGrantsReportHelperService().createTitleText(ContractsGrantsInvoiceReport.class), ContractsGrantsInvoiceReport.class, fieldList));

                    col.setColumnAnchor(a);
                }
            }

            ResultRow row = new ResultRow(columns, "", ACTION_URLS_EMPTY);

            if (getBusinessObjectDictionaryService().isExportable(getBusinessObjectClass())) {
                row.setBusinessObject(element);
            }
            boolean isRowReturnable = isResultReturnable(element);
            row.setRowReturnable(isRowReturnable);
            if (isRowReturnable) {
                hasReturnableRow = true;
            }
            resultTable.add(row);
        }
        lookupForm.setHasReturnableRow(hasReturnableRow);
    }

    public FinancialSystemDocumentService getFinancialSystemDocumentService() {
        return financialSystemDocumentService;
    }

    public void setFinancialSystemDocumentService(FinancialSystemDocumentService financialSystemDocumentService) {
        this.financialSystemDocumentService = financialSystemDocumentService;
    }

    public PersonService getPersonService() {
        return personService;
    }

    public void setPersonService(PersonService personService) {
        this.personService = personService;
    }

    public ContractsAndGrantsModuleBillingService getContractsAndGrantsModuleBillingService() {
        return contractsAndGrantsModuleBillingService;
    }

    public void setContractsAndGrantsModuleBillingService(ContractsAndGrantsModuleBillingService contractsAndGrantsModuleBillingService) {
        this.contractsAndGrantsModuleBillingService = contractsAndGrantsModuleBillingService;
    }
}