/*
 * Copyright 2006-2007 The Kuali Foundation.
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
package org.kuali.module.gl.web.lookupable;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang.ArrayUtils;
import org.kuali.core.bo.BusinessObject;
import org.kuali.core.util.KualiDecimal;
import org.kuali.core.web.ui.Row;
import org.kuali.kfs.KFSConstants;
import org.kuali.kfs.bo.GeneralLedgerPendingEntry;
import org.kuali.kfs.bo.Options;
import org.kuali.kfs.service.OptionsService;
import org.kuali.module.chart.bo.ObjectCode;
import org.kuali.module.gl.GLConstants;
import org.kuali.module.gl.batch.poster.AccountBalanceCalculator;
import org.kuali.module.gl.bo.AccountBalance;
import org.kuali.module.gl.bo.TransientBalanceInquiryAttributes;
import org.kuali.module.gl.service.AccountBalanceService;
import org.kuali.module.gl.util.BusinessObjectFieldConverter;
import org.kuali.module.gl.util.OJBUtility;
import org.kuali.module.gl.web.Constant;
import org.kuali.module.gl.web.inquirable.AccountBalanceInquirableImpl;

public class AccountBalanceLookupableHelperServiceImpl extends AbstractGLLookupableHelperServiceImpl {
    
    private AccountBalanceCalculator postAccountBalance;
    private AccountBalanceService accountBalanceService;
    private OptionsService optionsService;

    /**
     * @see org.kuali.core.lookup.Lookupable#getInquiryUrl(org.kuali.core.bo.BusinessObject, java.lang.String)
     */
    @Override
    public String getInquiryUrl(BusinessObject bo, String propertyName) {
        return (new AccountBalanceInquirableImpl()).getInquiryUrl(bo, propertyName);
    }

    /**
     * @see org.kuali.core.lookup.Lookupable#getSearchResults(java.util.Map)
     */
    public List getSearchResults(Map fieldValues) {
        setBackLocation((String) fieldValues.get(KFSConstants.BACK_LOCATION));
        setDocFormKey((String) fieldValues.get(KFSConstants.DOC_FORM_KEY));

        Collection searchResultsCollection = null;

        // get the pending entry option. This method must be prior to the get search results
        String pendingEntryOption = this.getSelectedPendingEntryOption(fieldValues);

        // test if the consolidation option is selected or not
        String consolidationOption = (String) fieldValues.get(GLConstants.DummyBusinessObject.CONSOLIDATION_OPTION);
        boolean isConsolidated = isConsolidationSelected(fieldValues);

        // get the search result collection
        if (isConsolidated) {
            Iterator availableBalanceIterator = accountBalanceService.findConsolidatedAvailableAccountBalance(fieldValues);
            searchResultsCollection = buildConsolidedAvailableBalanceCollection(availableBalanceIterator);
        }
        else {
            Iterator availableBalanceIterator = accountBalanceService.findAvailableAccountBalance(fieldValues);
            searchResultsCollection = buildDetailedAvailableBalanceCollection(availableBalanceIterator);
        }

        // update search results according to the selected pending entry option
        updateByPendingLedgerEntry(searchResultsCollection, fieldValues, pendingEntryOption, isConsolidated, false);

        // Put the search related stuff in the objects
        for (Iterator iter = searchResultsCollection.iterator(); iter.hasNext();) {
            AccountBalance ab = (AccountBalance) iter.next();
            TransientBalanceInquiryAttributes dbo = ab.getDummyBusinessObject();
            dbo.setConsolidationOption(consolidationOption);
            dbo.setPendingEntryOption(pendingEntryOption);
        }

        // get the actual size of all qualified search results
        Integer recordCount = accountBalanceService.getAvailableAccountBalanceCount(fieldValues, isConsolidated);
        Long actualSize = OJBUtility.getResultActualSize(searchResultsCollection, recordCount, fieldValues, new AccountBalance());

        return this.buildSearchResultList(searchResultsCollection, actualSize);
    }


    /**
     * This method builds the available account balance collection based on the input iterator
     * 
     * @param iterator the iterator of search results of account balance
     * @return the account balance collection
     */
    private Collection buildConsolidedAvailableBalanceCollection(Iterator iterator) {
        Collection balanceCollection = new ArrayList();

        // build available balance collection throught analyzing the input iterator
        while (iterator.hasNext()) {
            Object avaiableAccountBalance = iterator.next();

            if (avaiableAccountBalance.getClass().isArray()) {
                int i = 0;
                Object[] array = (Object[]) avaiableAccountBalance;
                AccountBalance accountBalance = new AccountBalance();

                accountBalance.setUniversityFiscalYear(new Integer(array[i++].toString()));
                accountBalance.setChartOfAccountsCode(array[i++].toString());

                accountBalance.setAccountNumber(array[i++].toString());
                accountBalance.setSubAccountNumber(Constant.CONSOLIDATED_SUB_ACCOUNT_NUMBER);

                accountBalance.setObjectCode(array[i++].toString());
                accountBalance.setSubObjectCode(Constant.CONSOLIDATED_SUB_OBJECT_CODE);

                String objectTypeCode = array[i++].toString();
                accountBalance.getFinancialObject().setFinancialObjectTypeCode(objectTypeCode);

                KualiDecimal budgetAmount = new KualiDecimal(array[i++].toString());
                accountBalance.setCurrentBudgetLineBalanceAmount(budgetAmount);

                KualiDecimal actualsAmount = new KualiDecimal(array[i++].toString());
                accountBalance.setAccountLineActualsBalanceAmount(actualsAmount);

                KualiDecimal encumbranceAmount = new KualiDecimal(array[i].toString());
                accountBalance.setAccountLineEncumbranceBalanceAmount(encumbranceAmount);

                KualiDecimal variance = calculateVariance(accountBalance);
                accountBalance.getDummyBusinessObject().setGenericAmount(variance);

                balanceCollection.add(accountBalance);
            }
        }
        return balanceCollection;
    }

    /**
     * This method builds the available account balance collection based on the input collection
     * 
     * @param collection a collection of account balance entries
     * @return the account balance collection
     */
    private Collection buildDetailedAvailableBalanceCollection(Iterator iterator) {
        Collection balanceCollection = new ArrayList();

        // build available balance collection throught analyzing the iterator above
        while (iterator.hasNext()) {
            AccountBalance accountBalance = (AccountBalance) iterator.next();

            if (accountBalance.getDummyBusinessObject() == null) {
                accountBalance.setDummyBusinessObject(new TransientBalanceInquiryAttributes());
            }

            KualiDecimal variance = calculateVariance(accountBalance);
            accountBalance.getDummyBusinessObject().setGenericAmount(variance);

            balanceCollection.add(accountBalance);
        }
        return balanceCollection;
    }

    /**
     * This method calculates the variance of current budget balance, actuals balance and encumbrance balance
     * 
     * @param balance an account balance entry
     */
    private KualiDecimal calculateVariance(AccountBalance balance) {

        KualiDecimal variance = new KualiDecimal(0.0);
        KualiDecimal budgetAmount = balance.getCurrentBudgetLineBalanceAmount();
        KualiDecimal actualsAmount = balance.getAccountLineActualsBalanceAmount();
        KualiDecimal encumbranceAmount = balance.getAccountLineEncumbranceBalanceAmount();

        // determine if the object type code is one of the given codes
        ObjectCode financialObject = balance.getFinancialObject();
        String objectTypeCode = (financialObject == null) ? Constant.EMPTY_STRING : financialObject.getFinancialObjectTypeCode();

        Options options = optionsService.getCurrentYearOptions();
        String[] objectTypeCodeList = new String[3];
        objectTypeCodeList[0] = options.getFinObjTypeExpendNotExpCode();
        objectTypeCodeList[1] = options.getFinObjTypeExpNotExpendCode();
        objectTypeCodeList[2] = options.getFinObjTypeExpenditureexpCd();

        boolean isObjectTypeCodeInList = ArrayUtils.contains(objectTypeCodeList, objectTypeCode);

        // calculate the variance based on the object type code of the balance
        if (isObjectTypeCodeInList) {
            variance = budgetAmount.subtract(actualsAmount);
            variance = variance.subtract(encumbranceAmount);
        }
        else {
            variance = actualsAmount.subtract(budgetAmount);
        }
        return variance;
    }

    /**
     * @see org.kuali.module.gl.web.lookupable.AbstractGLLookupableImpl#updateEntryCollection(java.util.Collection, java.util.Map,
     *      boolean, boolean, boolean)
     */
    @Override
    protected void updateEntryCollection(Collection entryCollection, Map fieldValues, boolean isApproved, boolean isConsolidated, boolean isCostShareExcluded) {

        // convert the field names of balance object into corresponding ones of pending entry object
        Map pendingEntryFieldValues = BusinessObjectFieldConverter.convertToTransactionFieldValues(fieldValues);

        // go through the pending entries to update the balance collection
        Iterator pendingEntryIterator = getGeneralLedgerPendingEntryService().findPendingLedgerEntriesForAccountBalance(pendingEntryFieldValues, isApproved);
        while (pendingEntryIterator.hasNext()) {
            GeneralLedgerPendingEntry pendingEntry = (GeneralLedgerPendingEntry) pendingEntryIterator.next();

            if (isCostShareExcluded) {
                if ((pendingEntry.getSubAccount() != null) && (pendingEntry.getSubAccount().getA21SubAccount() != null)) {
                    if ("CS".equals(pendingEntry.getSubAccount().getA21SubAccount().getSubAccountTypeCode())) {
                        // Don't process this one
                        continue;
                    }
                }
            }

            // if consolidated, change the following fields into the default values for consolidation
            if (isConsolidated) {
                pendingEntry.setSubAccountNumber(Constant.CONSOLIDATED_SUB_ACCOUNT_NUMBER);
                pendingEntry.setFinancialSubObjectCode(Constant.CONSOLIDATED_SUB_OBJECT_CODE);
                pendingEntry.setFinancialObjectTypeCode(Constant.CONSOLIDATED_OBJECT_TYPE_CODE);
            }

            AccountBalance accountBalance = postAccountBalance.findAccountBalance(entryCollection, pendingEntry);
            postAccountBalance.updateAccountBalance(pendingEntry, accountBalance);

            // recalculate the variance after pending entries are combined into account balances
            if (accountBalance.getDummyBusinessObject() == null) {
                accountBalance.setDummyBusinessObject(new TransientBalanceInquiryAttributes());
            }
            KualiDecimal variance = calculateVariance(accountBalance);
            accountBalance.getDummyBusinessObject().setGenericAmount(variance);
        }
    }

    /**
     * Sets the postAccountBalance attribute value.
     * 
     * @param postAccountBalance The postAccountBalance to set.
     */
    public void setPostAccountBalance(AccountBalanceCalculator postAccountBalance) {
        this.postAccountBalance = postAccountBalance;
    }

    /**
     * Sets the accountBalanceService attribute value.
     * 
     * @param accountBalanceService The accountBalanceService to set.
     */
    public void setAccountBalanceService(AccountBalanceService accountBalanceService) {
        this.accountBalanceService = accountBalanceService;
    }

    /**
     * Sets the optionsService attribute value
     * 
     * @param optionsService The optionsService to set.
     */
    public void setOptionsService(OptionsService optionsService) {
        this.optionsService = optionsService;
    }
}
