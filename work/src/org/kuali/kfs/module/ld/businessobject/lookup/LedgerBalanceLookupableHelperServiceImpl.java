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
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.apache.commons.beanutils.PropertyUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.kuali.core.bo.BusinessObject;
import org.kuali.core.bo.PersistableBusinessObject;
import org.kuali.core.datadictionary.mask.Mask;
import org.kuali.core.lookup.AbstractLookupableHelperServiceImpl;
import org.kuali.core.lookup.CollectionIncomplete;
import org.kuali.core.service.AuthorizationService;
import org.kuali.core.util.BeanPropertyComparator;
import org.kuali.core.util.GlobalVariables;
import org.kuali.core.util.KualiDecimal;
import org.kuali.core.util.ObjectUtils;
import org.kuali.core.web.comparator.CellComparatorHelper;
import org.kuali.core.web.format.BooleanFormatter;
import org.kuali.core.web.format.Formatter;
import org.kuali.core.web.struts.form.LookupForm;
import org.kuali.core.web.ui.Column;
import org.kuali.core.web.ui.ResultRow;
import org.kuali.core.web.ui.Row;
import org.kuali.kfs.KFSConstants;
import org.kuali.kfs.KFSPropertyConstants;
import org.kuali.kfs.context.SpringContext;
import org.kuali.module.gl.bo.TransientBalanceInquiryAttributes;
import org.kuali.module.gl.util.OJBUtility;
import org.kuali.module.gl.web.Constant;
import org.kuali.module.labor.LaborConstants;
import org.kuali.module.labor.bo.LedgerBalance;
import org.kuali.module.labor.bo.SegmentedBusinessObject;
import org.kuali.module.labor.service.LaborInquiryOptionsService;
import org.kuali.module.labor.service.LaborLedgerBalanceService;
import org.kuali.module.labor.web.inquirable.LedgerBalanceInquirableImpl;
import org.springframework.transaction.annotation.Transactional;

@Transactional
public class LedgerBalanceLookupableHelperServiceImpl extends AbstractLookupableHelperServiceImpl {
    private static final Log LOG = LogFactory.getLog(LedgerBalanceLookupableHelperServiceImpl.class);

    private LaborLedgerBalanceService balanceService;
    private LaborInquiryOptionsService laborInquiryOptionsService;

    /**
     * @see org.kuali.core.lookup.Lookupable#getInquiryUrl(org.kuali.core.bo.BusinessObject, java.lang.String)
     */
    @Override
    public String getInquiryUrl(BusinessObject bo, String propertyName) {
        return (new LedgerBalanceInquirableImpl()).getInquiryUrl(bo, propertyName);
    }

    /**
     * @see org.kuali.core.lookup.Lookupable#getSearchResults(java.util.Map)
     */
    @Override
    public List getSearchResults(Map fieldValues) {
        String wildCards ="";
        for(int i=0;i<KFSConstants.QUERY_CHARACTERS.length;i++) {
            wildCards+=KFSConstants.QUERY_CHARACTERS[i];
        }
        
        if (wildCards.indexOf(fieldValues.get(KFSPropertyConstants.EMPLID).toString().trim()) != -1) {
                //StringUtils.indexOfAny(fieldValues.get(KFSPropertyConstants.EMPLID).toString().trim(), KFSConstants.QUERY_CHARACTERS) != 0) {            
            List emptySearchResults = new ArrayList();
            Long actualCountIfTruncated = new Long(0);
            GlobalVariables.getErrorMap().putError(KFSPropertyConstants.EMPLID, KFSConstants.WILDCARD_NOT_ALLOWED_ON_FIELD,"Employee ID field ");
            return new CollectionIncomplete(emptySearchResults, actualCountIfTruncated);           
        }

        setBackLocation((String) fieldValues.get(KFSConstants.BACK_LOCATION));
        setDocFormKey((String) fieldValues.get(KFSConstants.DOC_FORM_KEY));

        // get the pending entry option. This method must be prior to the get search results
        String pendingEntryOption = laborInquiryOptionsService.getSelectedPendingEntryOption(fieldValues);

        // test if the consolidation option is selected or not
        boolean isConsolidated = laborInquiryOptionsService.isConsolidationSelected(fieldValues);

        // get Amount View Option and determine if the results has to be accumulated
        // String amountViewOption = getSelectedAmountViewOption(fieldValues);
        // boolean isAccumulated = amountViewOption.equals(Constant.ACCUMULATE);
        boolean isAccumulated = false;

        // get the search result collection
        Iterator balanceIterator = balanceService.findBalance(fieldValues, isConsolidated);
        Collection searchResultsCollection = this.buildBalanceCollection(balanceIterator, isConsolidated, pendingEntryOption);

        // update search results according to the selected pending entry option
        laborInquiryOptionsService.updateByPendingLedgerEntry(searchResultsCollection, fieldValues, pendingEntryOption, isConsolidated);

        // perform the accumulation of the amounts
        this.accumulate(searchResultsCollection, isAccumulated);

        // get the actual size of all qualified search results
        Integer recordCount = balanceService.getBalanceRecordCount(fieldValues, isConsolidated);
        Long actualSize = OJBUtility.getResultActualSize(searchResultsCollection, recordCount, fieldValues, new LedgerBalance());

        return this.buildSearchResultList(searchResultsCollection, actualSize);
    }

    /**
     * This method builds the balance collection based on the input iterator
     * 
     * @param iterator the iterator of search results of balance
     * @param isConsolidated determine if the consolidated result is desired
     * @param pendingEntryOption the given pending entry option that can be no, approved or all
     * @return the balance collection
     */
    protected Collection buildBalanceCollection(Iterator iterator, boolean isConsolidated, String pendingEntryOption) {
        Collection balanceCollection = null;

        if (isConsolidated) {
            balanceCollection = buildConsolidatedBalanceCollection(iterator, pendingEntryOption);
        }
        else {
            balanceCollection = buildDetailedBalanceCollection(iterator, pendingEntryOption);
        }
        return balanceCollection;
    }

    /**
     * This method builds the balance collection with consolidation option from an iterator
     * 
     * @param iterator
     * @param pendingEntryOption the selected pending entry option
     * @return the consolidated balance collection
     */
    protected Collection buildConsolidatedBalanceCollection(Iterator iterator, String pendingEntryOption) {
        Collection balanceCollection = new ArrayList();

        while (iterator.hasNext()) {
            Object collectionEntry = iterator.next();

            if (collectionEntry.getClass().isArray()) {
                int i = 0;
                Object[] array = (Object[]) collectionEntry;
                LedgerBalance balance = new LedgerBalance();

                if (LedgerBalance.class.isAssignableFrom(getBusinessObjectClass())) {
                    try {
                        balance = (LedgerBalance) getBusinessObjectClass().newInstance();
                    }
                    catch (Exception e) {
                        LOG.warn("Using " + LedgerBalance.class + " for results because I couldn't instantiate the " + getBusinessObjectClass());
                    }
                }
                else {
                    LOG.warn("Using " + LedgerBalance.class + " for results because I couldn't instantiate the " + getBusinessObjectClass());
                }

                balance.setUniversityFiscalYear(new Integer(array[i++].toString()));
                balance.setChartOfAccountsCode(array[i++].toString());
                balance.setAccountNumber(array[i++].toString());

                String subAccountNumber = Constant.CONSOLIDATED_SUB_ACCOUNT_NUMBER;
                balance.setSubAccountNumber(subAccountNumber);

                balance.setBalanceTypeCode(array[i++].toString());
                balance.setFinancialObjectCode(array[i++].toString());

                balance.setEmplid(array[i++].toString());
                balance.setPositionNumber(array[i++].toString());

                balance.setFinancialSubObjectCode(Constant.CONSOLIDATED_SUB_OBJECT_CODE);
                balance.setFinancialObjectTypeCode(Constant.CONSOLIDATED_OBJECT_TYPE_CODE);

                balance.setAccountLineAnnualBalanceAmount(new KualiDecimal(array[i++].toString()));
                balance.setBeginningBalanceLineAmount(new KualiDecimal(array[i++].toString()));
                balance.setContractsGrantsBeginningBalanceAmount(new KualiDecimal(array[i++].toString()));

                balance.setMonth1Amount(new KualiDecimal(array[i++].toString()));
                balance.setMonth2Amount(new KualiDecimal(array[i++].toString()));
                balance.setMonth3Amount(new KualiDecimal(array[i++].toString()));
                balance.setMonth4Amount(new KualiDecimal(array[i++].toString()));
                balance.setMonth5Amount(new KualiDecimal(array[i++].toString()));
                balance.setMonth6Amount(new KualiDecimal(array[i++].toString()));
                balance.setMonth7Amount(new KualiDecimal(array[i++].toString()));
                balance.setMonth8Amount(new KualiDecimal(array[i++].toString()));
                balance.setMonth9Amount(new KualiDecimal(array[i++].toString()));

                balance.setMonth10Amount(new KualiDecimal(array[i++].toString()));
                balance.setMonth11Amount(new KualiDecimal(array[i++].toString()));
                balance.setMonth12Amount(new KualiDecimal(array[i++].toString()));
                balance.setMonth13Amount(new KualiDecimal(array[i].toString()));

                balance.getDummyBusinessObject().setPendingEntryOption(pendingEntryOption);
                balance.getDummyBusinessObject().setConsolidationOption(Constant.CONSOLIDATION);

                balanceCollection.add(balance);
            }
        }
        return balanceCollection;
    }

    /**
     * This method builds the balance collection with detail option from an iterator
     * 
     * @param iterator the balance iterator
     * @param pendingEntryOption the selected pending entry option
     * @return the detailed balance collection
     */
    protected Collection buildDetailedBalanceCollection(Iterator iterator, String pendingEntryOption) {
        Collection balanceCollection = new ArrayList();

        while (iterator.hasNext()) {
            LedgerBalance copyBalance = (LedgerBalance) (iterator.next());

            LedgerBalance balance = new LedgerBalance();
            if (LedgerBalance.class.isAssignableFrom(getBusinessObjectClass())) {
                try {
                    balance = (LedgerBalance) getBusinessObjectClass().newInstance();
                }
                catch (Exception e) {
                    LOG.warn("Using " + LedgerBalance.class + " for results because I couldn't instantiate the " + getBusinessObjectClass());
                }
            }
            else {
                LOG.warn("Using " + LedgerBalance.class + " for results because I couldn't instantiate the " + getBusinessObjectClass());
            }

            balance.setUniversityFiscalYear(copyBalance.getUniversityFiscalYear());
            balance.setChartOfAccountsCode(copyBalance.getChartOfAccountsCode());
            balance.setAccountNumber(copyBalance.getAccountNumber());
            balance.setSubAccountNumber(copyBalance.getSubAccountNumber());
            balance.setBalanceTypeCode(copyBalance.getBalanceTypeCode());
            balance.setFinancialObjectCode(copyBalance.getFinancialObjectCode());
            balance.setEmplid(copyBalance.getEmplid());
            balance.setObjectId(copyBalance.getObjectId());
            balance.setPositionNumber(copyBalance.getPositionNumber());
            balance.setFinancialSubObjectCode(copyBalance.getFinancialSubObjectCode());
            balance.setFinancialObjectTypeCode(copyBalance.getFinancialObjectTypeCode());
            balance.setAccountLineAnnualBalanceAmount(copyBalance.getAccountLineAnnualBalanceAmount());
            balance.setBeginningBalanceLineAmount(copyBalance.getBeginningBalanceLineAmount());
            balance.setContractsGrantsBeginningBalanceAmount(copyBalance.getContractsGrantsBeginningBalanceAmount());
            balance.setMonth1Amount(copyBalance.getMonth1Amount());
            balance.setMonth2Amount(copyBalance.getMonth2Amount());
            balance.setMonth3Amount(copyBalance.getMonth3Amount());
            balance.setMonth4Amount(copyBalance.getMonth4Amount());
            balance.setMonth5Amount(copyBalance.getMonth5Amount());
            balance.setMonth6Amount(copyBalance.getMonth6Amount());
            balance.setMonth7Amount(copyBalance.getMonth7Amount());
            balance.setMonth8Amount(copyBalance.getMonth8Amount());
            balance.setMonth9Amount(copyBalance.getMonth9Amount());
            balance.setMonth10Amount(copyBalance.getMonth10Amount());
            balance.setMonth11Amount(copyBalance.getMonth11Amount());
            balance.setMonth12Amount(copyBalance.getMonth12Amount());
            balance.setMonth13Amount(copyBalance.getMonth13Amount());

            balance.getDummyBusinessObject().setPendingEntryOption(pendingEntryOption);
            balance.getDummyBusinessObject().setConsolidationOption(Constant.DETAIL);

            balanceCollection.add(balance);
        }
        return balanceCollection;
    }

    /**
     * This method updates the balance collection with accumulated amounts if required (isAccumulated is true)
     * 
     * @param balanceCollection the balance collection to be updated
     * @param isAccumulated determine if the accumulated result is desired
     */
    private void accumulate(Collection balanceCollection, boolean isAccumulated) {

        if (isAccumulated) {
            for (Iterator iterator = balanceCollection.iterator(); iterator.hasNext();) {
                LedgerBalance balance = (LedgerBalance) (iterator.next());
                accumulateByBalance(balance, isAccumulated);
            }
        }
    }

    /**
     * This method computes the accumulate amount of the given balance and updates its fields
     * 
     * @param balance the given balance object
     * @param isAccumulated determine if the accumulated result is desired
     */
    private void accumulateByBalance(LedgerBalance balance, boolean isAccumulated) {

        KualiDecimal annualAmount = balance.getAccountLineAnnualBalanceAmount();
        KualiDecimal beginningAmount = balance.getBeginningBalanceLineAmount();
        KualiDecimal CGBeginningAmount = balance.getContractsGrantsBeginningBalanceAmount();

        KualiDecimal month0Amount = beginningAmount.add(CGBeginningAmount);
        KualiDecimal month1Amount = balance.getMonth1Amount();
        month1Amount = accumulateAmount(month1Amount, month0Amount, isAccumulated);
        balance.setMonth1Amount(month1Amount);

        KualiDecimal month2Amount = balance.getMonth2Amount();
        month2Amount = accumulateAmount(month2Amount, month1Amount, isAccumulated);
        balance.setMonth2Amount(month2Amount);

        KualiDecimal month3Amount = balance.getMonth3Amount();
        month3Amount = accumulateAmount(month3Amount, month2Amount, isAccumulated);
        balance.setMonth3Amount(month3Amount);

        KualiDecimal month4Amount = balance.getMonth4Amount();
        month4Amount = accumulateAmount(month4Amount, month3Amount, isAccumulated);
        balance.setMonth4Amount(month4Amount);

        KualiDecimal month5Amount = balance.getMonth5Amount();
        month5Amount = accumulateAmount(month5Amount, month4Amount, isAccumulated);
        balance.setMonth5Amount(month5Amount);

        KualiDecimal month6Amount = balance.getMonth6Amount();
        month6Amount = accumulateAmount(month6Amount, month5Amount, isAccumulated);
        balance.setMonth6Amount(month6Amount);

        KualiDecimal month7Amount = balance.getMonth7Amount();
        month7Amount = accumulateAmount(month7Amount, month6Amount, isAccumulated);
        balance.setMonth7Amount(month7Amount);

        KualiDecimal month8Amount = balance.getMonth8Amount();
        month8Amount = accumulateAmount(month8Amount, month7Amount, isAccumulated);
        balance.setMonth8Amount(month8Amount);

        KualiDecimal month9Amount = balance.getMonth9Amount();
        month9Amount = accumulateAmount(month9Amount, month8Amount, isAccumulated);
        balance.setMonth9Amount(month9Amount);

        KualiDecimal month10Amount = balance.getMonth10Amount();
        month10Amount = accumulateAmount(month10Amount, month9Amount, isAccumulated);
        balance.setMonth10Amount(month10Amount);

        KualiDecimal month11Amount = balance.getMonth11Amount();
        month11Amount = accumulateAmount(month11Amount, month10Amount, isAccumulated);
        balance.setMonth11Amount(month11Amount);

        KualiDecimal month12Amount = balance.getMonth12Amount();
        month12Amount = accumulateAmount(month12Amount, month11Amount, isAccumulated);
        balance.setMonth12Amount(month12Amount);

        KualiDecimal month13Amount = balance.getMonth13Amount();
        month13Amount = accumulateAmount(month13Amount, month12Amount, isAccumulated);
        balance.setMonth13Amount(month13Amount);
    }

    /**
     * This method converts the amount from String and adds it with the input addend
     * 
     * @param stringAugend a String-type augend
     * @param addend an addend
     * @param isAccumulated determine if the accumulated result is desired
     * @return the accumulated amount if accumulate option is selected; otherwise, the input amount itself
     */
    private KualiDecimal accumulateAmount(Object stringAugend, KualiDecimal addend, boolean isAccumulated) {

        KualiDecimal augend = new KualiDecimal(stringAugend.toString());
        if (isAccumulated) {
            augend = augend.add(addend);
        }
        return augend;
    }


    /**
     * build the serach result list from the given collection and the number of all qualified search results
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
                    for (String propertyName : ((SegmentedBusinessObject) element).getSegmentedPropertyNames()) {
                        Collection<Column> columns = getColumns(element);
                        columns.add(setupResultsColumn(element, propertyName));

                        ResultRow row = new ResultRow((List<Column>) columns, returnUrl, getActionUrls(element));
                     
                        String extraReturnData = ((SegmentedBusinessObject) element).getAdditionalReturnData(propertyName);
                        row.setObjectId(((PersistableBusinessObject) element).getObjectId() + "." + propertyName + "." + extraReturnData);
                        resultTable.add(row);
                    }
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
        if (getBusinessObjectDictionaryService().getLookupResultFieldNames(getBusinessObjectClass()).contains(attributeName)) {
            col.setMaxLength(getBusinessObjectDictionaryService().getLookupResultFieldMaxLength(getBusinessObjectClass(), attributeName));
        }
        else {
            col.setMaxLength(getDataDictionaryService().getAttributeMaxLength(getBusinessObjectClass(), attributeName));
        }

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

    /**
     * Sets the laborInquiryOptionsService attribute value.
     * 
     * @param laborInquiryOptionsService The laborInquiryOptionsService to set.
     */
    public void setLaborInquiryOptionsService(LaborInquiryOptionsService laborInquiryOptionsService) {
        this.laborInquiryOptionsService = laborInquiryOptionsService;
    }

    /**
     * Sets the balanceService attribute value.
     * 
     * @param balanceService The balanceService to set.
     */
    public void setBalanceService(LaborLedgerBalanceService balanceService) {
        this.balanceService = balanceService;
    }

    /**
     * Gets the balanceService attribute.
     * 
     * @return Returns the balanceService.
     */
    public LaborLedgerBalanceService getBalanceService() {
        return balanceService;
    }
}
