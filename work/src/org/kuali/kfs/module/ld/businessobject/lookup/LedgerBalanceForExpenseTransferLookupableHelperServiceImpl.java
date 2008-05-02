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
package org.kuali.module.labor.web.lookupable;

import java.beans.PropertyDescriptor;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.beanutils.PropertyUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.kuali.core.bo.BusinessObject;
import org.kuali.core.bo.PersistableBusinessObject;
import org.kuali.core.datadictionary.mask.Mask;
import org.kuali.core.service.AuthorizationService;
import org.kuali.core.util.GlobalVariables;
import org.kuali.core.util.ObjectUtils;
import org.kuali.core.web.comparator.CellComparatorHelper;
import org.kuali.core.web.format.BooleanFormatter;
import org.kuali.core.web.format.Formatter;
import org.kuali.core.web.struts.form.LookupForm;
import org.kuali.core.web.ui.Column;
import org.kuali.core.web.ui.ResultRow;
import org.kuali.kfs.KFSConstants;
import org.kuali.kfs.KFSPropertyConstants;
import org.kuali.kfs.context.SpringContext;
import org.kuali.module.integration.bo.SegmentedBusinessObject;
import org.kuali.module.labor.bo.LedgerBalance;
import org.kuali.module.labor.web.inquirable.AbstractLaborInquirableImpl;
import org.kuali.module.labor.web.inquirable.LedgerBalanceForExpenseTransferInquirableImpl;
import org.kuali.module.labor.web.inquirable.PositionDataDetailsInquirableImpl;

/**
 * Service implementation of LedgerBalanceForExpenseTransferLookupableHelperService.
 */

public abstract class LedgerBalanceForExpenseTransferLookupableHelperServiceImpl extends LedgerBalanceLookupableHelperServiceImpl {
    private static final Log LOG = LogFactory.getLog(LedgerBalanceForExpenseTransferLookupableHelperServiceImpl.class);

    /**
     * @see org.kuali.core.lookup.Lookupable#getInquiryUrl(org.kuali.core.bo.BusinessObject, java.lang.String)
     */
    @Override
    public String getInquiryUrl(BusinessObject bo, String propertyName) {
        if (KFSPropertyConstants.POSITION_NUMBER.equals(propertyName)) {
            LedgerBalance balance = (LedgerBalance) bo;
            AbstractLaborInquirableImpl positionDataDetailsInquirable = new PositionDataDetailsInquirableImpl();

            Map<String, String> fieldValues = new HashMap<String, String>();
            fieldValues.put(propertyName, balance.getPositionNumber());

            BusinessObject positionData = positionDataDetailsInquirable.getBusinessObject(fieldValues);

            return positionData == null ? KFSConstants.EMPTY_STRING : positionDataDetailsInquirable.getInquiryUrl(positionData, propertyName);
        }
        return (new LedgerBalanceForExpenseTransferInquirableImpl()).getInquiryUrl(bo, propertyName);
    }

    /**
     * @see org.kuali.module.labor.web.lookupable.LedgerBalanceLookupableHelperServiceImpl#getSearchResults(java.util.Map)
     */
    @Override
    public List<? extends BusinessObject> getSearchResults(Map<String, String> fieldValues) {
        return null;
    }

    /**
     * This method performs the lookup and returns a collection of lookup items
     * 
     * @param lookupForm
     * @param lookupable
     * @param resultTable
     * @param bounded
     * @return
     */
    public Collection performLookup(LookupForm lookupForm, Collection resultTable, boolean bounded) {
        Collection<BusinessObject> displayList;

        // call search method to get results
        if (bounded) {
            displayList = (Collection<BusinessObject>) getSearchResults(lookupForm.getFieldsForLookup());
        }
        else {
            displayList = (Collection<BusinessObject>) getSearchResultsUnbounded(lookupForm.getFieldsForLookup());
        }

        // iterate through result list and wrap rows with return url and action urls
        for (BusinessObject element : displayList) {
            LOG.debug("Doing lookup for " + element.getClass());
            String returnUrl = getReturnUrl(element, lookupForm.getFieldConversions(), lookupForm.getLookupableImplServiceName());

            if (element instanceof PersistableBusinessObject) {
                if (element instanceof SegmentedBusinessObject) {
                    LOG.debug("segmented property names " + ((SegmentedBusinessObject) element).getSegmentedPropertyNames());
                    
                    Collection<Column> columns = getColumns(element);
                    ResultRow row = new ResultRow((List<Column>) columns, returnUrl, getActionUrls(element));

                    for (String propertyName : ((SegmentedBusinessObject) element).getSegmentedPropertyNames()) {
                        columns.add(setupResultsColumn(element, propertyName));
                    }
                    
                    row.setObjectId(((PersistableBusinessObject) element).getObjectId());
                    resultTable.add(row);
                }
                else {
                    Collection<Column> columns = getColumns(element);
                    
                    ResultRow row = new ResultRow((List<Column>) columns, returnUrl, getActionUrls(element));
                    row.setObjectId(((PersistableBusinessObject) element).getObjectId());
                    resultTable.add(row);
                }
            }
        }

        return displayList;
    }

    /**
     * @param element
     * @param attributeName
     * @return Column
     */
    protected Column setupResultsColumn(BusinessObject element, String attributeName) {
        Column col = new Column();

        col.setPropertyName(attributeName);

        String columnTitle = getDataDictionaryService().getAttributeLabel(getBusinessObjectClass(), attributeName);
        if (StringUtils.isBlank(columnTitle)) {
            columnTitle = getDataDictionaryService().getCollectionLabel(getBusinessObjectClass(), attributeName);
        }
        col.setColumnTitle(columnTitle);
        col.setMaxLength(getDataDictionaryService().getAttributeMaxLength(getBusinessObjectClass(), attributeName));

        Class formatterClass = getDataDictionaryService().getAttributeFormatter(getBusinessObjectClass(), attributeName);
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

        // check security on field and do masking if necessary
        boolean viewAuthorized = SpringContext.getBean(AuthorizationService.class).isAuthorizedToViewAttribute(GlobalVariables.getUserSession().getUniversalUser(), element.getClass().getName(), col.getPropertyName());
        if (!viewAuthorized) {
            Mask displayMask = getDataDictionaryService().getAttributeDisplayMask(element.getClass().getName(), col.getPropertyName());
            propValue = displayMask.maskValue(propValue);
        }
        col.setPropertyValue(propValue);


        if (StringUtils.isNotBlank(propValue)) {
            col.setPropertyURL(getInquiryUrl(element, col.getPropertyName()));
        }
        return col;
    }


    /**
     * Constructs the list of columns for the search results. All properties for the column objects come from the DataDictionary.
     * 
     * @param bo
     * @return Collection<Column>
     */
    protected Collection<Column> getColumns(BusinessObject bo) {
        Collection<Column> columns = new ArrayList<Column>();

        for (String attributeName : getBusinessObjectDictionaryService().getLookupResultFieldNames(getBusinessObjectClass())) {
            columns.add(setupResultsColumn(bo, attributeName));
        }
        return columns;
    }
}
