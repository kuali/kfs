/*
 * Copyright 2012 The Kuali Foundation.
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

import java.beans.PropertyDescriptor;
import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.apache.commons.beanutils.PropertyUtils;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.StringUtils;
import org.kuali.kfs.coa.businessobject.Account;
import org.kuali.kfs.coa.service.AccountService;
import org.kuali.kfs.integration.cg.ContractsAndGrantsModuleBillingService;
import org.kuali.kfs.module.ar.ArKeyConstants;
import org.kuali.kfs.module.ar.ArPropertyConstants;
import org.kuali.kfs.module.ar.businessobject.InvoiceAccountDetail;
import org.kuali.kfs.module.ar.businessobject.ReferralToCollectionsLookupResult;
import org.kuali.kfs.module.ar.businessobject.ReferralType;
import org.kuali.kfs.module.ar.document.ContractsGrantsInvoiceDocument;
import org.kuali.kfs.module.ar.document.service.ContractsGrantsInvoiceDocumentService;
import org.kuali.kfs.module.ar.report.service.ContractsGrantsReportHelperService;
import org.kuali.kfs.module.ar.report.service.ReferralToCollectionsService;
import org.kuali.kfs.module.ar.web.ui.ContractsGrantsLookupResultRow;
import org.kuali.kfs.sys.KFSConstants;
import org.kuali.kfs.sys.KFSPropertyConstants;
import org.kuali.rice.core.web.format.Formatter;
import org.kuali.rice.kim.api.identity.Person;
import org.kuali.rice.kns.document.authorization.BusinessObjectRestrictions;
import org.kuali.rice.kns.lookup.HtmlData;
import org.kuali.rice.kns.web.comparator.CellComparatorHelper;
import org.kuali.rice.kns.web.struts.form.LookupForm;
import org.kuali.rice.kns.web.ui.Column;
import org.kuali.rice.kns.web.ui.ResultRow;
import org.kuali.rice.krad.bo.BusinessObject;
import org.kuali.rice.krad.bo.PersistableBusinessObjectBase;
import org.kuali.rice.krad.exception.ValidationException;
import org.kuali.rice.krad.lookup.CollectionIncomplete;
import org.kuali.rice.krad.util.BeanPropertyComparator;
import org.kuali.rice.krad.util.GlobalVariables;
import org.kuali.rice.krad.util.ObjectUtils;

/**
 * Defines a lookupable helper service class for Referral To Collections.
 */
public class ReferralToCollectionsLookupableHelperServiceImpl extends AccountsReceivableLookupableHelperServiceImplBase {
    private static final org.apache.log4j.Logger LOG = org.apache.log4j.Logger.getLogger(ReferralToCollectionsLookupableHelperServiceImpl.class);

    protected ContractsGrantsInvoiceDocumentService contractsGrantsInvoiceDocumentService;
    protected AccountService accountService;
    protected ContractsAndGrantsModuleBillingService contractsAndGrantsModuleBillingService;
    protected ContractsGrantsReportHelperService contractsGrantsReportHelperService;
    protected ReferralToCollectionsService referralToCollectionsService;

    /**
     * Gets the contractsGrantsInvoiceDocumentService attribute.
     *
     * @return Returns the contractsGrantsInvoiceDocumentService.
     */
    public ContractsGrantsInvoiceDocumentService getContractsGrantsInvoiceDocumentService() {
        return contractsGrantsInvoiceDocumentService;
    }

    /**
     * Sets the contractsGrantsInvoiceDocumentService attribute value.
     *
     * @param contractsGrantsInvoiceDocumentService The contractsGrantsInvoiceDocumentService to set.
     */
    public void setContractsGrantsInvoiceDocumentService(ContractsGrantsInvoiceDocumentService contractsGrantsInvoiceDocumentService) {
        this.contractsGrantsInvoiceDocumentService = contractsGrantsInvoiceDocumentService;
    }

    /**
     * Customized validation for lookup fields of doc.
     *
     * @see org.kuali.core.lookup.LookupableHelperService#validateSearchParameters(java.util.Map)
     */
    @Override
    public void validateSearchParameters(Map fieldValues) {
        List<String> lookupFieldAttributeList = null;
        if (getBusinessObjectMetaDataService().isLookupable(getBusinessObjectClass())) {
            lookupFieldAttributeList = getBusinessObjectMetaDataService().getLookupableFieldNames(getBusinessObjectClass());
        }
        if (ObjectUtils.isNull(lookupFieldAttributeList)) {
            throw new RuntimeException("Lookup not defined for business object " + getBusinessObjectClass());
        }

        String agencyNumber = (String) fieldValues.get(KFSPropertyConstants.AGENCY_NUMBER);
        String proposalNumber = (String) fieldValues.get(KFSPropertyConstants.PROPOSAL_NUMBER);
        String invoiceDocumentNumber = (String) fieldValues.get(ArPropertyConstants.INVOICE_DOCUMENT_NUMBER);
        String customerNumber = (String) fieldValues.get(ArPropertyConstants.CustomerInvoiceWriteoffLookupResultFields.CUSTOMER_NUMBER);
        String customerName = (String) fieldValues.get(ArPropertyConstants.CustomerInvoiceWriteoffLookupResultFields.CUSTOMER_NAME);

        if ((ObjectUtils.isNull(customerNumber) || StringUtils.isBlank(customerNumber)) && (ObjectUtils.isNull(agencyNumber) || StringUtils.isBlank(agencyNumber)) && (ObjectUtils.isNull(customerName) || StringUtils.isBlank(customerName)) && (ObjectUtils.isNull(proposalNumber) || StringUtils.isBlank(proposalNumber)) && (ObjectUtils.isNull(invoiceDocumentNumber) || StringUtils.isBlank(invoiceDocumentNumber))) {
            GlobalVariables.getMessageMap().putError(KFSPropertyConstants.AGENCY_NUMBER, ArKeyConstants.ReferralToCollectionsDocumentErrors.ERROR_EMPTY_REQUIRED_FIELDS);
        }

        if (GlobalVariables.getMessageMap().hasErrors()) {
            throw new ValidationException("errors in search criteria");
        }

    }

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
        Collection<BusinessObject> displayList;

        // Call search method to get results - always use unbounded to get the entire set of results.

        displayList = (Collection<BusinessObject>) getSearchResultsUnbounded(lookupForm.getFieldsForLookup());

        List pkNames = getBusinessObjectMetaDataService().listPrimaryKeyFieldNames(getBusinessObjectClass());
        List returnKeys = getReturnKeys();
        Person user = GlobalVariables.getUserSession().getPerson();

        // Iterate through result list and wrap rows with return url and action urls
        for (BusinessObject element : displayList) {
            LOG.debug("Doing lookup for " + element.getClass());

            ReferralToCollectionsLookupResult result = ((ReferralToCollectionsLookupResult) element);
            List<String> invoiceAttributesForDisplay = result.getInvoiceAttributesForDisplay();

            BusinessObjectRestrictions businessObjectRestrictions = getBusinessObjectAuthorizationService().getLookupResultRestrictions(result, user);
            // add list of awards to sub Result rows
            List<ResultRow> subResultRows = new ArrayList<ResultRow>();
            for (ContractsGrantsInvoiceDocument invoice : result.getInvoices()) {

                List<Column> subResultColumns = new ArrayList<Column>();
                InvoiceAccountDetail firstInvoiceAccountDetail = new InvoiceAccountDetail();

                // Set first invoice account detail
                if (CollectionUtils.isNotEmpty(invoice.getAccountDetails())){
                    firstInvoiceAccountDetail = invoice.getAccountDetails().get(0);
                }

                for (String propertyName : invoiceAttributesForDisplay) {
                    if (propertyName.equalsIgnoreCase(KFSPropertyConstants.ACCOUNT_NUMBER)) {
                        Account account = getAccountService().getByPrimaryId(firstInvoiceAccountDetail.getChartOfAccountsCode(), firstInvoiceAccountDetail.getAccountNumber());
                        subResultColumns.add(setupResultsColumn(account, propertyName, businessObjectRestrictions));
                    }
                    else {
                        subResultColumns.add(setupResultsColumn(invoice, propertyName, businessObjectRestrictions));
                    }
                }

                ResultRow subResultRow = new ResultRow(subResultColumns, "", "");
                subResultRow.setObjectId(((PersistableBusinessObjectBase) invoice).getObjectId());
                subResultRows.add(subResultRow);
            }

            // Create main customer header row
            Collection<Column> columns = getColumns(element, businessObjectRestrictions);
            HtmlData returnUrl = getReturnUrl(element, lookupForm, returnKeys, businessObjectRestrictions);
            ContractsGrantsLookupResultRow row = new ContractsGrantsLookupResultRow((List<Column>) columns, subResultRows, returnUrl.constructCompleteHtmlTag(), getActionUrls(element, pkNames, businessObjectRestrictions));
            resultTable.add(row);
        }

        return displayList;
    }

    /**
     * @param element
     * @param attributeName
     * @return Column
     */
    protected Column setupResultsColumn(BusinessObject element, String attributeName, BusinessObjectRestrictions businessObjectRestrictions) {
        Column col = new Column();

        col.setPropertyName(attributeName);


        String columnTitle = getDataDictionaryService().getAttributeLabel(element.getClass(), attributeName);
        if (StringUtils.isBlank(columnTitle)) {
            columnTitle = getDataDictionaryService().getCollectionLabel(element.getClass(), attributeName);
        }
        col.setColumnTitle(columnTitle);
        col.setMaxLength(getDataDictionaryService().getAttributeMaxLength(element.getClass(), attributeName));

        try {
            Class formatterClass = getDataDictionaryService().getAttributeFormatter(element.getClass(), attributeName);
            Formatter formatter = null;
            if (ObjectUtils.isNotNull(formatterClass)) {
                formatter = (Formatter) formatterClass.newInstance();
                col.setFormatter(formatter);
            }

            // Pick off result column from result list, do formatting
            String propValue = KFSConstants.EMPTY_STRING;
            Object prop = ObjectUtils.getPropertyValue(element, attributeName);

            // Set comparator and formatter based on property type
            Class propClass = null;
            PropertyDescriptor propDescriptor = PropertyUtils.getPropertyDescriptor(element, col.getPropertyName());
            if (ObjectUtils.isNotNull(propDescriptor)) {
                propClass = propDescriptor.getPropertyType();
            }

            // Formatters
            if (ObjectUtils.isNotNull(prop)) {
                propValue = getContractsGrantsReportHelperService().formatByType(prop, formatter);
            }

            // Comparator
            col.setComparator(CellComparatorHelper.getAppropriateComparatorForPropertyClass(propClass));
            col.setValueComparator(CellComparatorHelper.getAppropriateValueComparatorForPropertyClass(propClass));
            propValue = super.maskValueIfNecessary(element.getClass(), col.getPropertyName(), propValue, businessObjectRestrictions);
            col.setPropertyValue(propValue);

            if (StringUtils.isNotBlank(propValue)) {
                col.setColumnAnchor(getInquiryUrl(element, col.getPropertyName()));
            }
        }
        catch (InstantiationException ie) {
            throw new RuntimeException("Unable to get new instance of formatter class for property " + col.getPropertyName(), ie);
        }
        catch (IllegalAccessException | InvocationTargetException | NoSuchMethodException ex) {
            throw new RuntimeException("Cannot access PropertyType for property " + "'" + col.getPropertyName() + "' " + " on an instance of '" + element.getClass().getName() + "'.", ex);
        }
        return col;
    }

    /**
     * Constructs the list of columns for the search results. All properties for the column objects come from the DataDictionary.
     *
     * @param bo
     * @return Collection<Column>
     */
    protected Collection<Column> getColumns(BusinessObject bo, BusinessObjectRestrictions businessObjectRestrictions) {
        Collection<Column> columns = new ArrayList<Column>();

        for (String attributeName : getBusinessObjectDictionaryService().getLookupResultFieldNames(bo.getClass())) {
            columns.add(setupResultsColumn(bo, attributeName, businessObjectRestrictions));
        }
        return columns;
    }

    /**
     * @see org.kuali.rice.kns.lookup.KualiLookupableHelperServiceImpl#getSearchResultsHelper(java.util.Map, boolean)
     */
    @Override
    protected List<? extends BusinessObject> getSearchResultsHelper(Map<String, String> fieldValues, boolean unbounded) {
        return getContractsAndGrantsModuleBillingService().lookupAwards(fieldValues, unbounded);
    }

    /**
     * overriding this method to convert the list of awards to a list of ContratcsGrantsInvoiceLookupResult
     *
     * @see org.kuali.core.lookup.Lookupable#getSearchResults(java.util.Map)
     */
    @Override
    public List<? extends BusinessObject> getSearchResultsUnbounded(Map<String, String> fieldValues) {
        Collection searchResultsCollection;
        // Get the list and Convert to suitable list
        searchResultsCollection = getInvoiceDocumentsForReferralToCollectionsLookup(fieldValues);
        return this.buildSearchResultList(searchResultsCollection, new Long(searchResultsCollection.size()));
    }

    /**
     * Gets the invoice documents based on field values.
     *
     * @param fieldValues The fields which needs to be put in criteria.
     * @return Returns the list of ReferralToCollectionsLookupResult.
     */
    protected Collection<ReferralToCollectionsLookupResult> getInvoiceDocumentsForReferralToCollectionsLookup(Map<String, String> fieldValues) {

        String agencyNumber = fieldValues.get(KFSPropertyConstants.AGENCY_NUMBER);
        String accountNumber = fieldValues.get(KFSPropertyConstants.ACCOUNT_NUMBER);
        String proposalNumber = fieldValues.get(KFSPropertyConstants.PROPOSAL_NUMBER);
        String invoiceDocumentNumber = fieldValues.get(ArPropertyConstants.INVOICE_DOCUMENT_NUMBER);
        String awardDocumentNumber = fieldValues.get(ArPropertyConstants.ReferralToCollectionsFields.AWARD_DOCUMENT_NUMBER);
        String customerNumber = fieldValues.get(ArPropertyConstants.CustomerInvoiceWriteoffLookupResultFields.CUSTOMER_NUMBER);
        String customerName = fieldValues.get(ArPropertyConstants.CustomerInvoiceWriteoffLookupResultFields.CUSTOMER_NAME);
        String collectorPrincipalName = fieldValues.get(ArPropertyConstants.COLLECTOR_PRINC_NAME);

        Collection<ContractsGrantsInvoiceDocument> invoices;
        Map<String, String> fieldValuesForInvoice = new HashMap<String, String>();
        if (ObjectUtils.isNotNull(proposalNumber) && StringUtils.isNotBlank(proposalNumber.toString()) && StringUtils.isNotEmpty(proposalNumber.toString())) {
            fieldValuesForInvoice.put(KFSPropertyConstants.PROPOSAL_NUMBER, proposalNumber);
        }
        if (ObjectUtils.isNotNull(accountNumber) && StringUtils.isNotBlank(accountNumber.toString()) && StringUtils.isNotEmpty(accountNumber.toString())) {
            fieldValuesForInvoice.put(ArPropertyConstants.ACCOUNT_DETAILS_ACCOUNT_NUMBER, accountNumber);
        }
        if (ObjectUtils.isNotNull(invoiceDocumentNumber) && StringUtils.isNotBlank(invoiceDocumentNumber) && StringUtils.isNotEmpty(invoiceDocumentNumber)) {
            fieldValuesForInvoice.put(KFSPropertyConstants.DOCUMENT_NUMBER, invoiceDocumentNumber);
        }
        if (ObjectUtils.isNotNull(customerNumber) && StringUtils.isNotBlank(customerNumber) && StringUtils.isNotEmpty(customerNumber)) {
            fieldValuesForInvoice.put(ArPropertyConstants.CustomerInvoiceDocumentFields.CUSTOMER_NUMBER, customerNumber);
        }
        if (ObjectUtils.isNotNull(customerName) && StringUtils.isNotBlank(customerName) && StringUtils.isNotEmpty(customerName)) {
            fieldValuesForInvoice.put(ArPropertyConstants.ReferralToCollectionsFields.ACCOUNTS_RECEIVABLE_CUSTOMER_NAME, customerName);
        }

        fieldValuesForInvoice.put(ArPropertyConstants.OPEN_INVOICE_IND, KFSConstants.Booleans.TRUE);
        fieldValuesForInvoice.put(ArPropertyConstants.DOCUMENT_STATUS_CODE, KFSConstants.DocumentStatusCodes.APPROVED);


        Map<String, String> refFieldValues = new HashMap<String, String>();
        refFieldValues.put(ArPropertyConstants.ReferralTypeFields.OUTSIDE_COLLECTION_AGENCY_IND, KFSConstants.Booleans.TRUE);
        refFieldValues.put(KFSPropertyConstants.ACTIVE, KFSConstants.Booleans.TRUE);
        List<ReferralType> refTypes = (List<ReferralType>) businessObjectService.findMatching(ReferralType.class, refFieldValues);
        String outsideColAgencyCode = CollectionUtils.isNotEmpty(refTypes) ? refTypes.get(0).getReferralTypeCode() : null;

        invoices = contractsGrantsInvoiceDocumentService.retrieveAllCGInvoicesForReferallExcludingOutsideCollectionAgency(fieldValuesForInvoice, outsideColAgencyCode);

        // there's no ORM relationship between awards and the CINV - since awards are EBOs; so we just need to filter them after the query
        if ((ObjectUtils.isNotNull(awardDocumentNumber) && StringUtils.isNotBlank(awardDocumentNumber) && StringUtils.isNotEmpty(awardDocumentNumber)) || ObjectUtils.isNotNull(agencyNumber) && StringUtils.isNotBlank(agencyNumber.toString()) && StringUtils.isNotEmpty(agencyNumber.toString())) {
            filterInvoicesByAwardDocumentNumber(invoices, agencyNumber, awardDocumentNumber);
        }

        return getReferralToCollectionsService().getPopulatedReferralToCollectionsLookupResults(invoices);
    }

    /**
     * removes the invoices from list which does not match the given award document number.
     *
     * @param invoices list of invoices.
     * @param awardDocumentNumber award document number to filter invoices.
     */
    protected void filterInvoicesByAwardDocumentNumber(Collection<ContractsGrantsInvoiceDocument> invoices, String agencyNumber, String awardDocumentNumber) {
        boolean checkAwardNumber = !StringUtils.isBlank(awardDocumentNumber);
        boolean checkAgencyNumber = !StringUtils.isBlank(agencyNumber);
        if (!CollectionUtils.isEmpty(invoices)) {
            Iterator<ContractsGrantsInvoiceDocument> itr = invoices.iterator();
            while (itr.hasNext()) {
                ContractsGrantsInvoiceDocument invoice = itr.next();
                if (invoice.getAward() == null || (checkAwardNumber && (invoice.getAward().getAwardDocumentNumber() == null || !invoice.getAward().getAwardDocumentNumber().equals(awardDocumentNumber))) || (checkAgencyNumber && (invoice.getAward().getAgencyNumber() == null || !invoice.getAward().getAgencyNumber().equals(agencyNumber)))) {
                    itr.remove();
                }
            }
        }
    }

    /**
     * build the search result list from the given collection and the number of all qualified search results
     *
     * @param searchResultsCollection the given search results, which may be a subset of the qualified search results
     * @param actualSize the number of all qualified search results
     * @return the search result list with the given results and actual size
     */
    protected List buildSearchResultList(Collection searchResultsCollection, Long actualSize) {
        CollectionIncomplete results = new CollectionIncomplete(searchResultsCollection, actualSize);

        // Sort list if default sort column given
        List searchResults = results;
        List defaultSortColumns = getDefaultSortColumns();
        if (defaultSortColumns.size() > 0) {
            Collections.sort(results, new BeanPropertyComparator(defaultSortColumns, true));
        }
        return searchResults;
    }

    public AccountService getAccountService() {
        return accountService;
    }

    public void setAccountService(AccountService accountService) {
        this.accountService = accountService;
    }

    public ContractsAndGrantsModuleBillingService getContractsAndGrantsModuleBillingService() {
        return contractsAndGrantsModuleBillingService;
    }

    public void setContractsAndGrantsModuleBillingService(ContractsAndGrantsModuleBillingService contractsAndGrantsModuleBillingService) {
        this.contractsAndGrantsModuleBillingService = contractsAndGrantsModuleBillingService;
    }

    public ContractsGrantsReportHelperService getContractsGrantsReportHelperService() {
        return contractsGrantsReportHelperService;
    }

    public void setContractsGrantsReportHelperService(ContractsGrantsReportHelperService contractsGrantsReportHelperService) {
        this.contractsGrantsReportHelperService = contractsGrantsReportHelperService;
    }

    public ReferralToCollectionsService getReferralToCollectionsService() {
        return referralToCollectionsService;
    }

    public void setReferralToCollectionsService(ReferralToCollectionsService referralToCollectionsService) {
        this.referralToCollectionsService = referralToCollectionsService;
    }
}