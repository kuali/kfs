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
import org.apache.commons.lang.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.kuali.kfs.module.ar.businessobject.CustomerInvoiceWriteoffLookupResult;
import org.kuali.kfs.module.ar.businessobject.inquiry.CustomerInvoiceWriteoffLookupResultInquirableImpl;
import org.kuali.kfs.module.ar.document.CustomerInvoiceDocument;
import org.kuali.kfs.module.ar.document.service.CustomerInvoiceDocumentService;
import org.kuali.kfs.module.ar.document.service.CustomerInvoiceWriteoffDocumentService;
import org.kuali.kfs.module.ar.web.ui.CustomerInvoiceWriteoffLookupResultRow;
import org.kuali.kfs.sys.KFSConstants;
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
import org.kuali.rice.krad.lookup.CollectionIncomplete;
import org.kuali.rice.krad.util.BeanPropertyComparator;
import org.kuali.rice.krad.util.GlobalVariables;
import org.kuali.rice.krad.util.ObjectUtils;

public class CustomerInvoiceWriteoffLookupResultLookupableHelperServiceImpl extends KualiLookupableHelperServiceImpl {

    private CustomerInvoiceDocumentService customerInvoiceDocumentService;
    private CustomerInvoiceWriteoffDocumentService customerInvoiceWriteoffDocumentService;

    private static final Log LOG = LogFactory.getLog(CustomerInvoiceWriteoffLookupResultLookupableHelperServiceImpl.class);

    /**
     * This method performs the lookup and returns a collection of lookup items
     * 
     * @param lookupForm
     * @param kualiLookupable
     * @param resultTable
     * @param bounded
     * @return
     * 
     * KRAD Conversion: Lookupable performs customization of the display results.
     * 
     * No use of data dictionary.
     */
    @Override
    public Collection performLookup(LookupForm lookupForm, Collection resultTable, boolean bounded) {
        Collection<BusinessObject> displayList;

        // call search method to get results
        if (bounded) {
            displayList = (Collection<BusinessObject>) getSearchResults(lookupForm.getFieldsForLookup());
        }
        else {
            displayList = (Collection<BusinessObject>) getSearchResultsUnbounded(lookupForm.getFieldsForLookup());
        }
        List pkNames = getBusinessObjectMetaDataService().listPrimaryKeyFieldNames(getBusinessObjectClass());
        List returnKeys = getReturnKeys();
        Person user = GlobalVariables.getUserSession().getPerson();
        
        // iterate through result list and wrap rows with return url and action urls
        for (BusinessObject element : displayList) {
            if (LOG.isDebugEnabled()) {
                LOG.debug("Doing lookup for " + element.getClass());
            }
            
            CustomerInvoiceWriteoffLookupResult result = ((CustomerInvoiceWriteoffLookupResult) element);
            List<String> customerInvoiceDocumentAttributesForDisplay = result.getCustomerInvoiceDocumentAttributesForDisplay();
            
            BusinessObjectRestrictions businessObjectRestrictions = getBusinessObjectAuthorizationService().getLookupResultRestrictions(result, user);
            //add list of customer invoice document sub rows
            List<ResultRow> subResultRows = new ArrayList<ResultRow>();
            for (CustomerInvoiceDocument customerInvoiceDocument : result.getCustomerInvoiceDocuments()) {

                List<Column> subResultColumns = new ArrayList<Column>();
                
                for (String propertyName : customerInvoiceDocumentAttributesForDisplay) {
                    subResultColumns.add(setupResultsColumn(customerInvoiceDocument, propertyName, businessObjectRestrictions));
                }
                
                ResultRow subResultRow = new ResultRow((List<Column>) subResultColumns, "", "");
                subResultRow.setObjectId(customerInvoiceDocument.getObjectId());
                subResultRows.add(subResultRow);
            }
            
            //create main customer header row
            Collection<Column> columns = getColumns(element, businessObjectRestrictions);
            HtmlData returnUrl = getReturnUrl(element, lookupForm, returnKeys, businessObjectRestrictions);
            CustomerInvoiceWriteoffLookupResultRow row = 
                new CustomerInvoiceWriteoffLookupResultRow((List<Column>) columns, subResultRows, 
                        returnUrl.constructCompleteHtmlTag(), getActionUrls(element, pkNames, businessObjectRestrictions));
            resultTable.add(row);
        }

        return displayList;
    }
    
    /**
     * @see org.kuali.core.lookup.Lookupable#getSearchResults(java.util.Map)
     */
    @Override
    public List<? extends BusinessObject> getSearchResults(Map<String, String> fieldValues) {

        setBackLocation((String) fieldValues.get(KFSConstants.BACK_LOCATION));
        setDocFormKey((String) fieldValues.get(KFSConstants.DOC_FORM_KEY));
        Collection searchResultsCollection = customerInvoiceWriteoffDocumentService.getCustomerInvoiceDocumentsForInvoiceWriteoffLookup(fieldValues);

        return this.buildSearchResultList(searchResultsCollection, new Long(searchResultsCollection.size()));
    }

    /**
     * build the search result list from the given collection and the number of all qualified search results
     * 
     * @param searchResultsCollection the given search results, which may be a subset of the qualified search results
     * @param actualSize the number of all qualified search results
     * @return the serach result list with the given results and actual size
     */
    protected List buildSearchResultList(Collection searchResultsCollection, Long actualSize) {
        CollectionIncomplete results = new CollectionIncomplete(searchResultsCollection, actualSize);

        // sort list if default sort column given
        List searchResults = (List) results;
        List defaultSortColumns = getDefaultSortColumns();
        if (defaultSortColumns.size() > 0) {
            Collections.sort(results, new BeanPropertyComparator(defaultSortColumns, true));
        }
        return searchResults;
    }

    /**
     * @param element
     * @param attributeName
     * @return Column
     * 
     * KRAD Conversion: setup up the results column in the display results set.
     * 
     * No use of data dictionary.
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
        if (formatterClass != null) {
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

        // pick off result column from result list, do formatting
        String propValue = KFSConstants.EMPTY_STRING;
        Object prop = ObjectUtils.getPropertyValue(element, attributeName);

        // set comparator and formatter based on property type
        Class propClass = null;
        try {
            PropertyDescriptor propDescriptor = PropertyUtils.getPropertyDescriptor(element, col.getPropertyName());
            if (propDescriptor != null) {
                propClass = propDescriptor.getPropertyType();
            }
        }
        catch (Exception e) {
            throw new RuntimeException("Cannot access PropertyType for property " + "'" + col.getPropertyName() + "' " + " on an instance of '" + element.getClass().getName() + "'.", e);
        }

        // formatters
        if (prop != null) {
            // for Booleans, always use BooleanFormatter
            if (prop instanceof Boolean) {
                formatter = new BooleanFormatter();
            }

            if (formatter != null) {
                propValue = (String) formatter.format(prop);
            }
            else {
                propValue = prop.toString();
            }
        }

        // comparator
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
     * 
     * KRAD Conversion: Gets column names.
     * 
     * Data dictionary is using the bo to look up the attribute names for the columns.
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
     * @see org.kuali.rice.kns.lookup.AbstractLookupableHelperServiceImpl#getInquiryUrl(org.kuali.rice.krad.bo.BusinessObject, java.lang.String)
     */
    @Override
    public HtmlData getInquiryUrl(BusinessObject bo, String propertyName) {
        return (new CustomerInvoiceWriteoffLookupResultInquirableImpl()).getInquiryUrl(bo, propertyName);
    }

    @Override
    /*
     * TODO Figure out what to really send here...
     */
    public List getReturnKeys() {
        return new ArrayList();
    }

    public CustomerInvoiceDocumentService getCustomerInvoiceDocumentService() {
        return customerInvoiceDocumentService;
    }

    public void setCustomerInvoiceDocumentService(CustomerInvoiceDocumentService customerInvoiceDocumentService) {
        this.customerInvoiceDocumentService = customerInvoiceDocumentService;
    }
    
    public CustomerInvoiceWriteoffDocumentService getCustomerInvoiceWriteoffDocumentService() {
        return customerInvoiceWriteoffDocumentService;
    }

    public void setCustomerInvoiceWriteoffDocumentService(CustomerInvoiceWriteoffDocumentService customerInvoiceWriteoffDocumentService) {
        this.customerInvoiceWriteoffDocumentService = customerInvoiceWriteoffDocumentService;
    }
}

