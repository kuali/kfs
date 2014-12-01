/*
 * The Kuali Financial System, a comprehensive financial management system for higher education.
 * 
 * Copyright 2005-2014 The Kuali Foundation
 * 
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Affero General Public License as
 * published by the Free Software Foundation, either version 3 of the
 * License, or (at your option) any later version.
 * 
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Affero General Public License for more details.
 * 
 * You should have received a copy of the GNU Affero General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package org.kuali.kfs.module.ld.businessobject.lookup;

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
import org.kuali.kfs.integration.ld.SegmentedBusinessObject;
import org.kuali.kfs.integration.ld.businessobject.inquiry.AbstractPositionDataDetailsInquirableImpl;
import org.kuali.kfs.module.ld.businessobject.LedgerBalance;
import org.kuali.kfs.module.ld.businessobject.inquiry.LedgerBalanceForExpenseTransferInquirableImpl;
import org.kuali.kfs.module.ld.businessobject.inquiry.PositionDataDetailsInquirableImpl;
import org.kuali.kfs.sys.KFSConstants;
import org.kuali.kfs.sys.KFSPropertyConstants;
import org.kuali.rice.core.web.format.BooleanFormatter;
import org.kuali.rice.core.web.format.Formatter;
import org.kuali.rice.kim.api.identity.Person;
import org.kuali.rice.kns.document.authorization.BusinessObjectRestrictions;
import org.kuali.rice.kns.lookup.HtmlData;
import org.kuali.rice.kns.lookup.HtmlData.AnchorHtmlData;
import org.kuali.rice.kns.web.comparator.CellComparatorHelper;
import org.kuali.rice.kns.web.struts.form.LookupForm;
import org.kuali.rice.kns.web.ui.Column;
import org.kuali.rice.kns.web.ui.ResultRow;
import org.kuali.rice.krad.bo.BusinessObject;
import org.kuali.rice.krad.bo.PersistableBusinessObject;
import org.kuali.rice.krad.util.GlobalVariables;
import org.kuali.rice.krad.util.ObjectUtils;

/**
 * Service implementation of LedgerBalanceForExpenseTransferLookupableHelperService.
 */

public abstract class LedgerBalanceForExpenseTransferLookupableHelperServiceImpl extends LedgerBalanceLookupableHelperServiceImpl {
    private static final Log LOG = LogFactory.getLog(LedgerBalanceForExpenseTransferLookupableHelperServiceImpl.class);

    /**
     * @see org.kuali.rice.kns.lookup.Lookupable#getInquiryUrl(org.kuali.rice.krad.bo.BusinessObject, java.lang.String)
     */
    @Override
    public HtmlData getInquiryUrl(BusinessObject bo, String propertyName) {
        if (KFSPropertyConstants.POSITION_NUMBER.equals(propertyName)) {
            LedgerBalance balance = (LedgerBalance) bo;
            AbstractPositionDataDetailsInquirableImpl positionDataDetailsInquirable = new PositionDataDetailsInquirableImpl();

            Map<String, String> fieldValues = new HashMap<String, String>();
            fieldValues.put(propertyName, balance.getPositionNumber());

            BusinessObject positionData = positionDataDetailsInquirable.getBusinessObject(fieldValues);

            return positionData == null ? new AnchorHtmlData() : positionDataDetailsInquirable.getInquiryUrl(positionData, propertyName);
        }
        return (new LedgerBalanceForExpenseTransferInquirableImpl()).getInquiryUrl(bo, propertyName);
    }

    /**
     * @see org.kuali.kfs.module.ld.businessobject.lookup.LedgerBalanceLookupableHelperServiceImpl#getSearchResults(java.util.Map)
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
     *
     * KRAD Conversion: Lookupable performs customization of the search results.
     *
     * Uses data dictionary for meta data.
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
            BusinessObjectRestrictions businessObjectRestrictions = getBusinessObjectAuthorizationService().getLookupResultRestrictions(element, user);
            String returnUrl =
                getReturnUrl(element, lookupForm, returnKeys, businessObjectRestrictions).constructCompleteHtmlTag();

            if (element instanceof PersistableBusinessObject) {
                if (element instanceof SegmentedBusinessObject) {
                    if (LOG.isDebugEnabled()) {
                        LOG.debug("segmented property names " + ((SegmentedBusinessObject) element).getSegmentedPropertyNames());
                    }
                    Collection<Column> columns = getColumns(element, businessObjectRestrictions);
                    ResultRow row = new ResultRow((List<Column>) columns, returnUrl, getActionUrls(element, pkNames, businessObjectRestrictions));

                    for (String propertyName : ((SegmentedBusinessObject) element).getSegmentedPropertyNames()) {
                        columns.add(setupResultsColumn(element, propertyName, businessObjectRestrictions));
                    }

                    row.setObjectId(((PersistableBusinessObject) element).getObjectId());
                    resultTable.add(row);
                }
                else {
                    Collection<Column> columns = getColumns(element, businessObjectRestrictions);

                    ResultRow row = new ResultRow((List<Column>) columns, returnUrl, getActionUrls(element, pkNames, businessObjectRestrictions));
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
     *
     * KRAD Conversion: Performs customization of the results columns.
     *
     * Uses data dictionary to get column properties.
     */
    protected Column setupResultsColumn(BusinessObject element, String attributeName, BusinessObjectRestrictions businessObjectRestrictions) {
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
     * KRAD Conversion: Performs customization of the columns.
     *
     * No uses data dictionary.
     */
    protected Collection<Column> getColumns(BusinessObject bo, BusinessObjectRestrictions businessObjectRestrictions) {
        Collection<Column> columns = new ArrayList<Column>();

        for (String attributeName : getBusinessObjectDictionaryService().getLookupResultFieldNames(getBusinessObjectClass())) {
            columns.add(setupResultsColumn(bo, attributeName, businessObjectRestrictions));
        }
        return columns;
    }
}

