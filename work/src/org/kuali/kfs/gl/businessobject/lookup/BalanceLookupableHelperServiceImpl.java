/*
 * Copyright 2006 The Kuali Foundation
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
package org.kuali.kfs.gl.businessobject.lookup;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.kuali.kfs.gl.Constant;
import org.kuali.kfs.gl.GeneralLedgerConstants;
import org.kuali.kfs.gl.OJBUtility;
import org.kuali.kfs.gl.batch.service.BalanceCalculator;
import org.kuali.kfs.gl.businessobject.Balance;
import org.kuali.kfs.gl.businessobject.TransientBalanceInquiryAttributes;
import org.kuali.kfs.gl.businessobject.inquiry.BalanceInquirableImpl;
import org.kuali.kfs.gl.service.BalanceService;
import org.kuali.kfs.sys.KFSConstants;
import org.kuali.kfs.sys.KFSPropertyConstants;
import org.kuali.kfs.sys.businessobject.GeneralLedgerPendingEntry;
import org.kuali.rice.core.api.util.type.KualiDecimal;
import org.kuali.rice.kns.lookup.HtmlData;
import org.kuali.rice.kns.web.ui.Field;
import org.kuali.rice.kns.web.ui.Row;
import org.kuali.rice.krad.bo.BusinessObject;

/**
 * An extension of KualiLookupableImpl to support balance lookups
 */
public class BalanceLookupableHelperServiceImpl extends AbstractGeneralLedgerLookupableHelperServiceImpl {
    private BalanceCalculator postBalance;
    private BalanceService balanceService;
    private Map fieldValues;

    /**
     * Returns the url for any drill down links within the lookup
     * @param bo the business object with a property being drilled down on
     * @param propertyName the name of the property being drilled down on
     * @return a String with the URL of the property
     * @see org.kuali.rice.kns.lookup.Lookupable#getInquiryUrl(org.kuali.rice.krad.bo.BusinessObject, java.lang.String)
     */
    @Override
    public HtmlData getInquiryUrl(BusinessObject bo, String propertyName) {
        return (new BalanceInquirableImpl()).getInquiryUrl(bo, propertyName);
    }

    /**
     * Generates the list of search results for this inquiry
     * @param fieldValues the field values of the query to carry out
     * @return List the search results returned by the lookup
     * @see org.kuali.rice.kns.lookup.Lookupable#getSearchResults(java.util.Map)
     * 
     * KRAD Conversion: Lookupable modifies the search results based on the fields consolidated.
     * But all field definitions are in data dictionary.
     */
    @Override
    public List getSearchResults(Map fieldValues) {
        setBackLocation((String) fieldValues.get(KFSConstants.BACK_LOCATION));
        setDocFormKey((String) fieldValues.get(KFSConstants.DOC_FORM_KEY));

        // get the pending entry option. This method must be prior to the get search results
        String pendingEntryOption = this.getSelectedPendingEntryOption(fieldValues);
        
        // KFSMI-410: need to get this before getting isConsolidated because this value will be removed.
        String consolidationOption = (String) fieldValues.get(GeneralLedgerConstants.DummyBusinessObject.CONSOLIDATION_OPTION);
        
        // test if the consolidation option is selected or not
        boolean isConsolidated = isConsolidationSelected(fieldValues);
     
        // KFSMI-410: added one more node for consolidationOption
        if (consolidationOption.equals(Constant.EXCLUDE_SUBACCOUNTS)){
            fieldValues.put(Constant.SUB_ACCOUNT_OPTION, KFSConstants.getDashSubAccountNumber());
            isConsolidated = false;
        } 
       
        // get Amount View Option and determine if the results has to be accumulated
        String amountViewOption = getSelectedAmountViewOption(fieldValues);
        boolean isAccumulated = amountViewOption.equals(Constant.ACCUMULATE);

        // get the search result collection
        Iterator balanceIterator = balanceService.findBalance(fieldValues, isConsolidated);
        Collection searchResultsCollection = this.buildBalanceCollection(balanceIterator, isConsolidated, pendingEntryOption);

        // update search results according to the selected pending entry option
        updateByPendingLedgerEntry(searchResultsCollection, fieldValues, pendingEntryOption, isConsolidated, false);

        // perform the accumulation of the amounts
        this.accumulate(searchResultsCollection, isAccumulated);

        // get the actual size of all qualified search results
        Integer recordCount = balanceService.getBalanceRecordCount(fieldValues, isConsolidated);
        Long actualSize = OJBUtility.getResultActualSize(searchResultsCollection, recordCount, fieldValues, new Balance());

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
    private Collection buildBalanceCollection(Iterator iterator, boolean isConsolidated, String pendingEntryOption) {
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
     * @param iterator th iterator of balance results
     * @param pendingEntryOption the selected pending entry option
     * @return the consolidated balance collection
     */
    private Collection buildConsolidatedBalanceCollection(Iterator iterator, String pendingEntryOption) {
        Collection balanceCollection = new ArrayList();

        while (iterator.hasNext()) {
            Object collectionEntry = iterator.next();

            if (collectionEntry.getClass().isArray()) {
                int i = 0;
                Object[] array = (Object[]) collectionEntry;
                Balance balance = new Balance();

                balance.setUniversityFiscalYear(new Integer(array[i++].toString()));
                balance.setChartOfAccountsCode(array[i++].toString());
                balance.setAccountNumber(array[i++].toString());

                String subAccountNumber = Constant.CONSOLIDATED_SUB_ACCOUNT_NUMBER;
                balance.setSubAccountNumber(subAccountNumber);

                balance.setBalanceTypeCode(array[i++].toString());
                balance.setObjectCode(array[i++].toString());

                balance.setSubObjectCode(Constant.CONSOLIDATED_SUB_OBJECT_CODE);
                balance.setObjectTypeCode(Constant.CONSOLIDATED_OBJECT_TYPE_CODE);

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

                balance.setDummyBusinessObject(new TransientBalanceInquiryAttributes());
                balance.getDummyBusinessObject().setPendingEntryOption(pendingEntryOption);

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
    private Collection buildDetailedBalanceCollection(Iterator iterator, String pendingEntryOption) {
        Collection balanceCollection = new ArrayList();

        while (iterator.hasNext()) {
            Balance balance = (Balance) (iterator.next());

            balance.setDummyBusinessObject(new TransientBalanceInquiryAttributes());
            balance.getDummyBusinessObject().setPendingEntryOption(pendingEntryOption);

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
    protected void accumulate(Collection balanceCollection, boolean isAccumulated) {

        if (isAccumulated) {
            for (Iterator iterator = balanceCollection.iterator(); iterator.hasNext();) {
                Balance balance = (Balance) (iterator.next());
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
    private void accumulateByBalance(Balance balance, boolean isAccumulated) {

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
     * Updates pending entries before their results are included in the lookup results
     * 
     * @param entryCollection a collection of balance entries
     * @param fieldValues the map containing the search fields and values
     * @param isApproved flag whether the approved entries or all entries will be processed
     * @param isConsolidated flag whether the results are consolidated or not
     * @param isCostShareExcluded flag whether the user selects to see the results with cost share subaccount
     * @see org.kuali.module.gl.web.lookupable.AbstractGLLookupableImpl#updateEntryCollection(java.util.Collection, java.util.Map,
     *      boolean, boolean, boolean)
     */
    public void updateEntryCollection(Collection entryCollection, Map fieldValues, boolean isApproved, boolean isConsolidated, boolean isCostShareInclusive) {

        // convert the field names of balance object into corresponding ones of pending entry object
        Map pendingEntryFieldValues = BusinessObjectFieldConverter.convertToTransactionFieldValues(fieldValues);

        // go through the pending entries to update the balance collection
        Iterator pendingEntryIterator = getGeneralLedgerPendingEntryService().findPendingLedgerEntriesForBalance(pendingEntryFieldValues, isApproved);
        while (pendingEntryIterator.hasNext()) {
            GeneralLedgerPendingEntry pendingEntry = (GeneralLedgerPendingEntry) pendingEntryIterator.next();

            // if consolidated, change the following fields into the default values for consolidation
            if (isConsolidated) {
                pendingEntry.setSubAccountNumber(Constant.CONSOLIDATED_SUB_ACCOUNT_NUMBER);
                pendingEntry.setFinancialSubObjectCode(Constant.CONSOLIDATED_SUB_OBJECT_CODE);
                pendingEntry.setFinancialObjectTypeCode(Constant.CONSOLIDATED_OBJECT_TYPE_CODE);
            }

            Balance balance = postBalance.findBalance(entryCollection, pendingEntry);

            String pendingEntryOption = isApproved ? Constant.APPROVED_PENDING_ENTRY : Constant.ALL_PENDING_ENTRY;
            balance.setDummyBusinessObject(new TransientBalanceInquiryAttributes());
            balance.getDummyBusinessObject().setPendingEntryOption(pendingEntryOption);

            postBalance.updateBalance(pendingEntry, balance);
        }
    }

    /**
     * Sets the postBalance attribute value.
     * 
     * @param postBalance The postBalance to set.
     */
    public void setPostBalance(BalanceCalculator postBalance) {
        this.postBalance = postBalance;
    }

    /**
     * Sets the balanceService attribute value.
     * 
     * @param balanceService The balanceService to set.
     */
    public void setBalanceService(BalanceService balanceService) {
        this.balanceService = balanceService;
    }

    /**
     * @see org.kuali.rice.kns.lookup.AbstractLookupableHelperServiceImpl#getRows()
     */
    @Override
    public List<Row> getRows() {
        List<Row> superResults = super.getRows();
        if (superResults != null) {
            for (Row row : superResults) {
                for (Field field : row.getFields()) {
                    if (KFSPropertyConstants.ACCOUNT_NUMBER.equals(field.getPropertyName())) {
                        // because of limitations in BO Metadata service, the account quickfinder was going to prior year account instead of account, therefore
                        // need to force it to go to Account (or whatever's mapped to the "account" reference in OJB
                        Class clazz = getPersistenceStructureService().getBusinessObjectAttributeClass(businessObjectClass, KFSPropertyConstants.ACCOUNT);
                        field.setQuickFinderClassNameImpl(clazz.getName());
                        return superResults;
                    }
                }
            }
        }
        return superResults;
    }
}
