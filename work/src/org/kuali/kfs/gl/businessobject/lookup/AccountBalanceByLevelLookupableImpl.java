/*
 * Copyright (c) 2004, 2005 The National Association of College and University Business Officers,
 * Cornell University, Trustees of Indiana University, Michigan State University Board of Trustees,
 * Trustees of San Joaquin Delta College, University of Hawai'i, The Arizona Board of Regents on
 * behalf of the University of Arizona, and the r*smart group.
 * 
 * Licensed under the Educational Community License Version 1.0 (the "License"); By obtaining,
 * using and/or copying this Original Work, you agree that you have read, understand, and will
 * comply with the terms and conditions of the Educational Community License.
 * 
 * You may obtain a copy of the License at:
 * 
 * http://kualiproject.org/license.html
 * 
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR IMPLIED, INCLUDING
 * BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE
 * AND NONINFRINGEMENT.
 * IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES
 * OR OTHER LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE SOFTWARE.
 *
 */
package org.kuali.module.gl.web.lookupable;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.kuali.Constants;
import org.kuali.core.bo.BusinessObject;
import org.kuali.core.lookup.CollectionIncomplete;
import org.kuali.core.util.comparator.BeanPropertyComparator;
import org.kuali.core.util.KualiDecimal;
import org.kuali.module.gl.batch.poster.AccountBalanceCalculator;
import org.kuali.module.gl.bo.AccountBalance;
import org.kuali.module.gl.bo.GeneralLedgerPendingEntry;
import org.kuali.module.gl.bo.Transaction;
import org.kuali.module.gl.util.BusinessObjectFieldConverter;
import org.kuali.module.gl.web.Constant;
import org.kuali.module.gl.web.inquirable.AccountBalanceByLevelInquirableImpl;

/**
 * This class...
 * 
 * @author Bin Gao from Michigan State University
 */
public class AccountBalanceByLevelLookupableImpl extends AbstractGLLookupableImpl {

    private AccountBalanceCalculator postAccountBalance;

    /**
     * Returns the inquiry url for a field if one exist.
     * 
     * @param bo the business object instance to build the urls for
     * @param propertyName the property which links to an inquirable
     * @return String url to inquiry
     */
    public String getInquiryUrl(BusinessObject bo, String propertyName) {
        return (new AccountBalanceByLevelInquirableImpl()).getInquiryUrl(bo, propertyName);
    }

    /**
     * Uses Lookup Service to provide a basic search.
     * 
     * @param fieldValues - Map containing prop name keys and search values
     * @return List found business objects
     */
    public List getSearchResults(Map fieldValues) {
        setBackLocation((String) fieldValues.get(Constants.BACK_LOCATION));
        setDocFormKey((String) fieldValues.get(Constants.DOC_FORM_KEY));

        // get the pending entry option. This method must be prior to the get search results
        String pendingEntryOption = this.getSelectedPendingEntryOption(fieldValues);

        // test if the consolidation option is selected or not
        boolean isConsolidated = isConsolidationSelected(fieldValues);

        // test if the cost share inclusive option is selected or not
        boolean isCostShareInclusive = isCostShareInclusive(fieldValues);

        // get the search result collection
        Iterator availableBalanceIterator = accountBalanceService.findAccountBalanceByLevel(fieldValues,
                isCostShareInclusive, isConsolidated);

        Collection searchResultsCollection = buildAvailableBalanceCollection(availableBalanceIterator, 
                isCostShareInclusive, isConsolidated, pendingEntryOption);        

        // update search results according to the selected pending entry option
        updateByPendingLedgerEntry(searchResultsCollection, fieldValues, pendingEntryOption, isConsolidated, isCostShareInclusive);

        // sort list if default sort column given
        List searchResults = (List) searchResultsCollection;
        List defaultSortColumns = getDefaultSortColumns();
        if (defaultSortColumns.size() > 0) {
            Collections.sort(searchResults, new BeanPropertyComparator(defaultSortColumns, true));
        }
        return searchResults;
    }

    /**
     * This method builds the available account balance collection based on the input iterator
     * 
     * @param iterator the iterator of search results of account balance
     * @param isCostShareInclusive determine whether the account balance entries with cost share is included
     * @param isConsolidated flag whether the results are consolidated or not
     * @param pendingEntryOption the selected pending entry option
     * 
     * @return the account balance collection
     */
    private Collection buildAvailableBalanceCollection(Iterator iterator, boolean isCostShareInclusive, boolean isConsolidated, String pendingEntryOption) {
        Collection balanceCollection = new ArrayList();

        // build available balance collection throught analyzing the input iterator
        while (iterator.hasNext()) {
            int i = 0;
            Object avaiableAccountBalance = iterator.next();
            Object[] array = (Object[]) avaiableAccountBalance;
            AccountBalance accountBalance = new AccountBalance();

            accountBalance.setUniversityFiscalYear(new Integer(array[i++].toString()));
            accountBalance.setChartOfAccountsCode(array[i++].toString());
            accountBalance.setAccountNumber(array[i++].toString());

            String subAccountNumber = isConsolidated ? Constant.CONSOLIDATED_SUB_ACCOUNT_NUMBER : array[i++].toString();
            accountBalance.setSubAccountNumber(subAccountNumber);

            accountBalance.getFinancialObject().getFinancialObjectLevel().setFinancialReportingSortCode(array[i++].toString());
            accountBalance.getFinancialObject().getFinancialObjectLevel().setFinancialObjectLevelCode(array[i++].toString());
            accountBalance.getFinancialObject().getFinancialObjectLevel().setFinancialConsolidationObjectCode(array[i++].toString());

            KualiDecimal budgetAmount = new KualiDecimal(array[i++].toString());
            accountBalance.setCurrentBudgetLineBalanceAmount(budgetAmount);

            KualiDecimal actualsAmount = new KualiDecimal(array[i++].toString());
            accountBalance.setAccountLineActualsBalanceAmount(actualsAmount);

            KualiDecimal encumbranceAmount = new KualiDecimal(array[i].toString());
            accountBalance.setAccountLineEncumbranceBalanceAmount(encumbranceAmount);

            KualiDecimal variance = calculateVariance(accountBalance);
            accountBalance.getDummyBusinessObject().setGenericAmount(variance);

            String consolidationOption = isConsolidated ? Constant.CONSOLIDATION : Constant.DETAIL;
            accountBalance.getDummyBusinessObject().setConsolidationOption(consolidationOption);

            String costShareOption = isCostShareInclusive ? Constant.COST_SHARE_INCLUDE : Constant.COST_SHARE_EXCLUDE;
            accountBalance.getDummyBusinessObject().setCostShareOption(costShareOption);

            accountBalance.getDummyBusinessObject().setPendingEntryOption(pendingEntryOption);

            // add a button that can trigger lookup account balance by object
            accountBalance.getDummyBusinessObject().setLinkButtonOption(Constant.LOOKUP_BUTTON_VALUE);

            // add the newly populated account balance object into a collection
            balanceCollection.add(accountBalance);
        }
        return new CollectionIncomplete(balanceCollection, new Long(balanceCollection.size()));
    }

    /**
     * This method calculates the variance of current budget balance, actuals balance and encumbrance balance
     * 
     * @param accountBalance an account balance entry
     */
    private KualiDecimal calculateVariance(AccountBalance accountBalance) {

        KualiDecimal variance = new KualiDecimal(0.0);
        KualiDecimal budgetAmount = accountBalance.getCurrentBudgetLineBalanceAmount();
        KualiDecimal actualsAmount = accountBalance.getAccountLineActualsBalanceAmount();
        KualiDecimal encumbranceAmount = accountBalance.getAccountLineEncumbranceBalanceAmount();

        // get the reporting sort code
        String reportingSortCode = accountBalance.getFinancialObject().getFinancialObjectLevel().getFinancialReportingSortCode();

        // calculate the variance based on the starting character of reporting sort code
        if (reportingSortCode.startsWith(Constant.START_CHAR_OF_REPORTING_SORT_CODE_B)) {
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
    public void updateEntryCollection(Collection entryCollection, Map fieldValues, boolean isApproved, boolean isConsolidated, boolean isCostShareInclusive) {

        // convert the field names of balance object into corresponding ones of pending entry object
        Map pendingEntryFieldValues = BusinessObjectFieldConverter.convertToTransactionFieldValues(fieldValues);

        // go through the pending entries to update the balance collection
        Iterator pendingEntryIterator = generalLedgerPendingEntryService.findPendingLedgerEntriesForAccountBalanceByConsolidation(
                pendingEntryFieldValues, isCostShareInclusive, isApproved);

        while (pendingEntryIterator.hasNext()) {
            GeneralLedgerPendingEntry pendingEntry = (GeneralLedgerPendingEntry) pendingEntryIterator.next();

            // if consolidated, change the following fields into the default values for consolidation
            if (isConsolidated) {
                pendingEntry.setSubAccountNumber(Constant.CONSOLIDATED_SUB_ACCOUNT_NUMBER);
                pendingEntry.setFinancialObjectTypeCode(Constant.CONSOLIDATED_OBJECT_TYPE_CODE);
            }
            pendingEntry.setFinancialSubObjectCode(Constant.CONSOLIDATED_SUB_OBJECT_CODE);

            AccountBalance accountBalance = findAccountBalance(entryCollection, pendingEntry);
            if(accountBalance != null){
                postAccountBalance.updateAccountBalance(pendingEntry, accountBalance);
            }
        }
    }

    /**
     * This method tests if the transaction belongs to any existing account balance searched by level
     * 
     * @param entryCollection the collection of account balances searched by level
     * @param transaction the given transaction
     * @return the existing account balance if found; otherwise, create a new account balance and return it
     */
    private AccountBalance findAccountBalance(Collection entryCollection, Transaction transaction) {

        String objectLevelCodeOfPendingEntry = null;

        try {
            objectLevelCodeOfPendingEntry = transaction.getFinancialObject().getFinancialObjectLevel()
                    .getFinancialObjectLevelCode();
            String objectCode = transaction.getFinancialObjectCode();
            
            if (objectLevelCodeOfPendingEntry == null || objectCode == null) {
                return null;
            }
        }
        catch (Exception e) {
            e.printStackTrace();
            return null;
        }

        // test if the transaction belongs to any existing account balance searched by consolidation
        Iterator iterator = entryCollection.iterator();
        while (iterator.hasNext()) {
            AccountBalance accountBalance = (AccountBalance) iterator.next();

            String objectLevelCodeOfAccountBalance = accountBalance.getFinancialObject().getFinancialObjectLevel()
                    .getFinancialObjectLevelCode();

            if (accountBalance.getUniversityFiscalYear().equals(transaction.getUniversityFiscalYear())
                    && accountBalance.getChartOfAccountsCode().equals(transaction.getChartOfAccountsCode())
                    && accountBalance.getAccountNumber().equals(transaction.getAccountNumber())
                    && accountBalance.getSubAccountNumber().equals(transaction.getSubAccountNumber())
                    && objectLevelCodeOfAccountBalance.equals(objectLevelCodeOfPendingEntry)) {
                return accountBalance;
            }
        }

        // If we couldn't find one that exists, create a new one
        AccountBalance accountBalance = new AccountBalance(transaction);
        accountBalance.getFinancialObject().getFinancialObjectLevel().setFinancialObjectLevelCode(objectLevelCodeOfPendingEntry);
        
        entryCollection.add(accountBalance);
        return accountBalance;
    }

    /**
     * Sets the postAccountBalance attribute value.
     * 
     * @param postAccountBalance The postAccountBalance to set.
     */
    public void setPostAccountBalance(AccountBalanceCalculator postAccountBalance) {
        this.postAccountBalance = postAccountBalance;
    }
}
