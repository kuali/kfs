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

import java.beans.PropertyDescriptor;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Map;

import org.apache.commons.beanutils.PropertyUtils;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.kuali.kfs.coa.businessobject.Account;
import org.kuali.kfs.coa.service.AccountService;
import org.kuali.kfs.integration.cg.ContractsAndGrantsModuleRetrieveService;
import org.kuali.kfs.module.ar.ArKeyConstants;
import org.kuali.kfs.module.ar.ArPropertyConstants;
import org.kuali.kfs.module.ar.businessobject.DunningLetterDistributionOnDemandLookupResult;
import org.kuali.kfs.module.ar.businessobject.InvoiceAccountDetail;
import org.kuali.kfs.module.ar.businessobject.InvoiceGeneralDetail;
import org.kuali.kfs.module.ar.businessobject.inquiry.DunningLetterDistributionOnDemandLookupResultInquirableImpl;
import org.kuali.kfs.module.ar.document.ContractsGrantsInvoiceDocument;
import org.kuali.kfs.module.ar.document.service.ContractsGrantsInvoiceDocumentService;
import org.kuali.kfs.module.ar.web.ui.DunningLetterDistributionOnDemandResultRow;
import org.kuali.kfs.sys.KFSConstants;
import org.kuali.kfs.sys.KFSPropertyConstants;
import org.kuali.kfs.sys.context.SpringContext;
import org.kuali.rice.core.web.format.BooleanFormatter;
import org.kuali.rice.core.web.format.Formatter;
import org.kuali.rice.kim.api.identity.Person;
import org.kuali.rice.kns.document.authorization.BusinessObjectRestrictions;
import org.kuali.rice.kns.lookup.HtmlData;
import org.kuali.rice.kns.lookup.KualiLookupableHelperServiceImpl;
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
 * Defines a lookupable helper service class for OnDemand Dunning Letter Distribution.
 */
public class DunningLetterDistributionOnDemandLookupableHelperServiceImpl extends KualiLookupableHelperServiceImpl {

    private ContractsGrantsInvoiceDocumentService contractsGrantsInvoiceDocumentService;
    private static final Log LOG = LogFactory.getLog(DunningLetterDistributionOnDemandLookupableHelperServiceImpl.class);

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

            DunningLetterDistributionOnDemandLookupResult result = ((DunningLetterDistributionOnDemandLookupResult) element);
            List<String> invoiceAttributesForDisplay = result.getInvoiceAttributesForDisplay();

            BusinessObjectRestrictions businessObjectRestrictions = getBusinessObjectAuthorizationService().getLookupResultRestrictions(result, user);
            // add list of awards to sub Result rows
            List<ResultRow> subResultRows = new ArrayList<ResultRow>();
            for (ContractsGrantsInvoiceDocument invoice : result.getInvoices()) {

                List<Column> subResultColumns = new ArrayList<Column>();
                InvoiceAccountDetail invoiceAccountDetail = new InvoiceAccountDetail();

                // set invoice account detail
                if (CollectionUtils.isNotEmpty(invoice.getAccountDetails())){
                    invoiceAccountDetail = invoice.getAccountDetails().get(0);
                }

                for (String propertyName : invoiceAttributesForDisplay) {
                    if (propertyName.equalsIgnoreCase(KFSPropertyConstants.ACCOUNT_NUMBER)) {
                        Account account = SpringContext.getBean(AccountService.class).getByPrimaryId(invoiceAccountDetail.getChartOfAccountsCode(), invoiceAccountDetail.getAccountNumber());
                        subResultColumns.add(setupResultsColumn(account, propertyName, businessObjectRestrictions));
                    }
                    else if (propertyName.equalsIgnoreCase("dunningLetterTemplateSentDate")) {
                        InvoiceGeneralDetail invoiceGeneralDetail = invoice.getInvoiceGeneralDetail();
                        subResultColumns.add(setupResultsColumn(invoiceGeneralDetail, propertyName, businessObjectRestrictions));
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
            DunningLetterDistributionOnDemandResultRow row = new DunningLetterDistributionOnDemandResultRow((List<Column>) columns, subResultRows, returnUrl.constructCompleteHtmlTag(), getActionUrls(element, pkNames, businessObjectRestrictions));
            resultTable.add(row);
        }

        return displayList;
    }

    @Override
    public void validateSearchParameters(Map<String, String> fieldValues) {
        super.validateSearchParameters(fieldValues);
        //  put validation
        String billingChartCode = fieldValues.get(ArPropertyConstants.DunningLetterDistributionFields.BILLING_CHART_CODE);
        String billingOrgCode = fieldValues.get(ArPropertyConstants.DunningLetterDistributionFields.BILLING_ORGANIZATION_CODE);
        String processingChartCode = fieldValues.get(ArPropertyConstants.DunningLetterDistributionFields.PROCESSING_CHART_CODE);
        String processingOrgCode = fieldValues.get(ArPropertyConstants.DunningLetterDistributionFields.PROCESSING_ORGANIZATION_CODE);

        //  validation logic comes here..
        if (ObjectUtils.isNotNull(billingChartCode) && StringUtils.isNotEmpty(billingChartCode)
                && (ObjectUtils.isNull(billingOrgCode) || StringUtils.isEmpty(billingOrgCode))) {
            GlobalVariables.getMessageMap().putError(ArPropertyConstants.DunningLetterDistributionFields.BILLING_ORGANIZATION_CODE, ArKeyConstants.DunningLetterDistributionErrors.ERROR_BILLING_ORGANIZATION_CODE_REQUIRED);
        }
        if (ObjectUtils.isNotNull(billingOrgCode) && StringUtils.isNotEmpty(billingOrgCode)
                && (ObjectUtils.isNull(billingChartCode) || StringUtils.isEmpty(billingChartCode))) {
            GlobalVariables.getMessageMap().putError(ArPropertyConstants.DunningLetterDistributionFields.BILLING_CHART_CODE, ArKeyConstants.DunningLetterDistributionErrors.ERROR_BILLING_CHART_CODE_REQUIRED);
        }
        if (ObjectUtils.isNotNull(processingOrgCode) && StringUtils.isNotEmpty(processingOrgCode)
                && (ObjectUtils.isNull(processingChartCode) || StringUtils.isEmpty(processingChartCode))) {
            GlobalVariables.getMessageMap().putError(ArPropertyConstants.DunningLetterDistributionFields.PROCESSING_CHART_CODE, ArKeyConstants.DunningLetterDistributionErrors.ERROR_PROCESSING_CHART_CODE_REQUIRED);
        }
        if (ObjectUtils.isNotNull(processingChartCode) && StringUtils.isNotEmpty(processingChartCode)
                && (ObjectUtils.isNull(processingOrgCode) || StringUtils.isEmpty(processingOrgCode))) {
            GlobalVariables.getMessageMap().putError(ArPropertyConstants.DunningLetterDistributionFields.PROCESSING_ORGANIZATION_CODE, ArKeyConstants.DunningLetterDistributionErrors.ERROR_PROCESSING_ORGANIZATION_CODE_REQUIRED);
        }

        if (GlobalVariables.getMessageMap().hasErrors()) {
            throw new ValidationException("errors in search criteria");
        }

    }

    /**
     * overriding this method to convert the list of awards to a list of ContratcsGrantsInvoiceOnDemandLookupResult
     *
     * @see org.kuali.core.lookup.Lookupable#getSearchResults(java.util.Map)
     */
    @Override
    public List<? extends BusinessObject> getSearchResultsUnbounded(Map<String, String> fieldValues) {
        Collection searchResultsCollection;
        // Get the list of invoices
        searchResultsCollection = contractsGrantsInvoiceDocumentService.getInvoiceDocumentsForDunningLetterOnDemandLookup(fieldValues);
        return this.buildSearchResultList(searchResultsCollection, new Long(searchResultsCollection.size()));
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

    /**
     * @see org.kuali.rice.kns.lookup.KualiLookupableHelperServiceImpl#getSearchResultsHelper(java.util.Map, boolean)
     */
    @Override
    protected List<? extends BusinessObject> getSearchResultsHelper(Map<String, String> fieldValues, boolean unbounded) {
        return SpringContext.getBean(ContractsAndGrantsModuleRetrieveService.class).getSearchResultsHelper(fieldValues, unbounded);
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

        Class formatterClass = getDataDictionaryService().getAttributeFormatter(element.getClass(), attributeName);
        Formatter formatter = null;
        if (ObjectUtils.isNotNull(formatterClass)) {
            try {
                formatter = (Formatter) formatterClass.newInstance();
                col.setFormatter(formatter);
            }
            catch (InstantiationException e) {
                LOG.error("Unable to get new instance of formatter class: " + formatterClass.getName());
                throw new RuntimeException("Unable to get new instance of formatter class: " + formatterClass.getName());
            }
            catch (IllegalAccessException e) {
                LOG.error("Unable to get new instance of formatter class: " + formatterClass.getName());
                throw new RuntimeException("Unable to get new instance of formatter class: " + formatterClass.getName());
            }
        }

        // Pick off result column from result list, do formatting
        String propValue = KFSConstants.EMPTY_STRING;
        Object prop = ObjectUtils.getPropertyValue(element, attributeName);

        // Set comparator and formatter based on property type
        Class propClass = null;
        try {
            PropertyDescriptor propDescriptor = PropertyUtils.getPropertyDescriptor(element, col.getPropertyName());
            if (ObjectUtils.isNotNull(propDescriptor)) {
                propClass = propDescriptor.getPropertyType();
            }
        }
        catch (Exception e) {
            throw new RuntimeException("Cannot access PropertyType for property " + "'" + col.getPropertyName() + "' " + " on an instance of '" + element.getClass().getName() + "'.", e);
        }

        // Formatters
        if (ObjectUtils.isNotNull(prop)) {
            // For Booleans, always use BooleanFormatter
            if (prop instanceof Boolean) {
                formatter = new BooleanFormatter();
            }

            if (ObjectUtils.isNotNull(formatter)) {
                propValue = (String) formatter.format(prop);
            }
            else {
                propValue = prop.toString();
            }
        }

        // Comparator
        col.setComparator(CellComparatorHelper.getAppropriateComparatorForPropertyClass(propClass));
        col.setValueComparator(CellComparatorHelper.getAppropriateValueComparatorForPropertyClass(propClass));
        propValue = super.maskValueIfNecessary(element.getClass(), col.getPropertyName(), propValue, businessObjectRestrictions);
        col.setPropertyValue(propValue);

        if (StringUtils.isNotBlank(propValue)) {
            col.setColumnAnchor(getInquiryUrl(element, col.getPropertyName()));
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
     * Since there aren't that many fields for inquiry, just deal with each of them one by one for this lookup
     *
     * @see org.kuali.rice.kns.lookup.AbstractLookupableHelperServiceImpl#getInquiryUrl(org.kuali.rice.krad.bo.BusinessObject,
     *      java.lang.String)
     */
    @Override
    public HtmlData getInquiryUrl(BusinessObject bo, String propertyName) {
        return (new DunningLetterDistributionOnDemandLookupResultInquirableImpl()).getInquiryUrl(bo, propertyName);
    }

    /**
     * @see org.kuali.rice.kns.lookup.AbstractLookupableHelperServiceImpl#getReturnKeys()
     */
    @Override
    public List getReturnKeys() {
        return new ArrayList();
    }

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


}
