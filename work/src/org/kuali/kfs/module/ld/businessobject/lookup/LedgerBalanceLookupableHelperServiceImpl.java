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

import static org.kuali.module.labor.LaborConstants.BalanceInquiries.BALANCE_TYPE_AC_AND_A21;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.kuali.core.bo.BusinessObject;
import org.kuali.core.lookup.AbstractLookupableHelperServiceImpl;
import org.kuali.core.lookup.CollectionIncomplete;
import org.kuali.core.util.BeanPropertyComparator;
import org.kuali.core.util.GlobalVariables;
import org.kuali.core.util.KualiDecimal;
import org.kuali.kfs.KFSConstants;
import org.kuali.kfs.KFSPropertyConstants;
import org.kuali.module.gl.util.OJBUtility;
import org.kuali.module.gl.web.Constant;
import org.kuali.module.labor.bo.LedgerBalance;
import org.kuali.module.labor.service.LaborInquiryOptionsService;
import org.kuali.module.labor.service.LaborLedgerBalanceService;
import org.kuali.module.labor.util.ConsolidationUtil;
import org.kuali.module.labor.web.inquirable.AbstractLaborInquirableImpl;
import org.kuali.module.labor.web.inquirable.LedgerBalanceInquirableImpl;
import org.kuali.module.labor.web.inquirable.PositionDataDetailsInquirableImpl;
import org.springframework.transaction.annotation.Transactional;

/**
 * Service implementation of LedgerBalanceLookupableHelperService. The class is the front-end for all Ledger balance inquiry
 * processing.
 */
public class LedgerBalanceLookupableHelperServiceImpl extends AbstractLookupableHelperServiceImpl {
    private static final Log LOG = LogFactory.getLog(LedgerBalanceLookupableHelperServiceImpl.class);

    private LaborLedgerBalanceService balanceService;
    private LaborInquiryOptionsService laborInquiryOptionsService;

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
        return (new LedgerBalanceInquirableImpl()).getInquiryUrl(bo, propertyName);
    }

    /**
     * @see org.kuali.core.lookup.Lookupable#getSearchResults(java.util.Map)
     */
    @Override
    public List<? extends BusinessObject> getSearchResults(Map<String, String> fieldValues) {
        String wildCards = "";
        for (int i = 0; i < KFSConstants.QUERY_CHARACTERS.length; i++) {
            wildCards += KFSConstants.QUERY_CHARACTERS[i];
        }

        if (wildCards.indexOf(fieldValues.get(KFSPropertyConstants.EMPLID).toString().trim()) != -1) {
            // StringUtils.indexOfAny(fieldValues.get(KFSPropertyConstants.EMPLID).toString().trim(), KFSConstants.QUERY_CHARACTERS)
            // != 0) {
            List emptySearchResults = new ArrayList();
            Long actualCountIfTruncated = new Long(0);
            GlobalVariables.getErrorMap().putError(KFSPropertyConstants.EMPLID, KFSConstants.WILDCARD_NOT_ALLOWED_ON_FIELD, "Employee ID field ");
            return new CollectionIncomplete(emptySearchResults, actualCountIfTruncated);
        }

        setBackLocation((String) fieldValues.get(KFSConstants.BACK_LOCATION));
        setDocFormKey((String) fieldValues.get(KFSConstants.DOC_FORM_KEY));

        // get the pending entry option. This method must be prior to the get search results
        String pendingEntryOption = laborInquiryOptionsService.getSelectedPendingEntryOption(fieldValues);

        // test if the consolidation option is selected or not
        boolean isConsolidated = laborInquiryOptionsService.isConsolidationSelected(fieldValues);

        // get the input balance type code
        String balanceTypeCode = fieldValues.get(KFSPropertyConstants.FINANCIAL_BALANCE_TYPE_CODE);
        boolean isA21Balance = StringUtils.isNotEmpty(balanceTypeCode) && BALANCE_TYPE_AC_AND_A21.equals(balanceTypeCode.trim());

        // get the ledger balances with actual balance type code
        if (isA21Balance) {
            fieldValues.put(KFSPropertyConstants.FINANCIAL_BALANCE_TYPE_CODE, KFSConstants.BALANCE_TYPE_ACTUAL);
        }
        Integer recordCountForActualBalance = balanceService.getBalanceRecordCount(fieldValues, isConsolidated);
        Iterator actualBalanceIterator = balanceService.findBalance(fieldValues, isConsolidated);
        Collection searchResultsCollection = buildBalanceCollection(actualBalanceIterator, isConsolidated, pendingEntryOption);
        laborInquiryOptionsService.updateLedgerBalanceByPendingLedgerEntry(searchResultsCollection, fieldValues, pendingEntryOption, isConsolidated);

        // get the search result collection
        Integer recordCountForEffortBalance = 0;
        if (isA21Balance) {
            fieldValues.put(KFSPropertyConstants.FINANCIAL_BALANCE_TYPE_CODE, KFSConstants.BALANCE_TYPE_A21);
            recordCountForEffortBalance = balanceService.getBalanceRecordCount(fieldValues, isConsolidated);

            Iterator effortBalanceIterator = balanceService.findBalance(fieldValues, isConsolidated);
            Collection effortBalances = buildBalanceCollection(effortBalanceIterator, isConsolidated, pendingEntryOption);
            laborInquiryOptionsService.updateLedgerBalanceByPendingLedgerEntry(effortBalances, fieldValues, pendingEntryOption, isConsolidated);
            searchResultsCollection = ConsolidationUtil.consolidateA2Balances(searchResultsCollection, effortBalances, BALANCE_TYPE_AC_AND_A21);
        }

        // get the actual size of all qualified search results
        Integer recordCount = recordCountForActualBalance + recordCountForEffortBalance;
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
