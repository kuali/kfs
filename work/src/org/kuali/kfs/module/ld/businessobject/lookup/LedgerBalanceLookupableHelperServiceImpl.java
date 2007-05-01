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

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.kuali.core.bo.BusinessObject;
import org.kuali.core.lookup.AbstractLookupableHelperServiceImpl;
import org.kuali.core.lookup.CollectionIncomplete;
import org.kuali.core.util.BeanPropertyComparator;
import org.kuali.core.util.KualiDecimal;
import org.kuali.kfs.KFSConstants;
import org.kuali.module.gl.bo.TransientBalanceInquiryAttributes;
import org.kuali.module.gl.util.OJBUtility;
import org.kuali.module.gl.web.Constant;
import org.kuali.module.labor.bo.LedgerBalance;
import org.kuali.module.labor.service.LaborLedgerBalanceService;
import org.kuali.module.labor.web.inquirable.LedgerBalanceInquirableImpl;
import org.springframework.transaction.annotation.Transactional;

@Transactional
public class LedgerBalanceLookupableHelperServiceImpl extends AbstractLookupableHelperServiceImpl {
    private LaborLedgerBalanceService balanceService;
    private Map fieldValues;

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
        setBackLocation((String) fieldValues.get(KFSConstants.BACK_LOCATION));
        setDocFormKey((String) fieldValues.get(KFSConstants.DOC_FORM_KEY));

        // get the pending entry option. This method must be prior to the get search results
        //String pendingEntryOption = this.getSelectedPendingEntryOption(fieldValues);
        String pendingEntryOption = "No";
        
        // test if the consolidation option is selected or not
        //boolean isConsolidated = isConsolidationSelected(fieldValues);
        boolean isConsolidated = true;
        
        // get Amount View Option and determine if the results has to be accumulated
        //String amountViewOption = getSelectedAmountViewOption(fieldValues);
        //boolean isAccumulated = amountViewOption.equals(Constant.ACCUMULATE);
        boolean isAccumulated = false;
        
        // get the search result collection
        Iterator balanceIterator = balanceService.findBalance(fieldValues, isConsolidated);
        Collection searchResultsCollection = this.buildBalanceCollection(balanceIterator, isConsolidated, pendingEntryOption);


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
     * 
     * @return the balance collection
     */
    private Collection buildBalanceCollection(Iterator iterator, boolean isConsolidated, String pendingEntryOption) {
        Collection balanceCollection = null;

        if (isConsolidated) {
            balanceCollection = buildCosolidatedBalanceCollection(iterator, pendingEntryOption);
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
     * 
     * @return the consolidated balance collection
     */
    private Collection buildCosolidatedBalanceCollection(Iterator iterator, String pendingEntryOption) {
        Collection balanceCollection = new ArrayList();

        while (iterator.hasNext()) {
            Object collectionEntry = iterator.next();

            if (collectionEntry.getClass().isArray()) {
                int i = 0;
                Object[] array = (Object[]) collectionEntry;
                LedgerBalance balance = new LedgerBalance();

                balance.setUniversityFiscalYear(new Integer(array[i++].toString()));
                balance.setChartOfAccountsCode(array[i++].toString());
                balance.setAccountNumber(array[i++].toString());

                String subAccountNumber = Constant.CONSOLIDATED_SUB_ACCOUNT_NUMBER;
                balance.setSubAccountNumber(subAccountNumber);

                balance.setBalanceTypeCode(array[i++].toString());
                balance.setObjectCode(array[i++].toString());

                balance.setEmplid(array[i++].toString());
                balance.setPositionNumber(array[i++].toString());
                
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
     * 
     * @return the detailed balance collection
     */
    private Collection buildDetailedBalanceCollection(Iterator iterator, String pendingEntryOption) {
        Collection balanceCollection = new ArrayList();

        while (iterator.hasNext()) {
            LedgerBalance balance = (LedgerBalance) (iterator.next());

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
     * Sets the balanceService attribute value.
     * 
     * @param balanceService The balanceService to set.
     */
    public void setBalanceService(LaborLedgerBalanceService balanceService) {
        this.balanceService = balanceService;
    }

}
