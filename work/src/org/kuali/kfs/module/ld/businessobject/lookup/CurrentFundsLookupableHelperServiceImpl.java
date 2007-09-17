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

import static org.apache.commons.collections.IteratorUtils.toList;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang.StringUtils;
import org.kuali.core.bo.BusinessObject;
import org.kuali.core.lookup.AbstractLookupableHelperServiceImpl;
import org.kuali.core.lookup.CollectionIncomplete;
import org.kuali.core.service.LookupService;
import org.kuali.core.util.BeanPropertyComparator;
import org.kuali.core.util.GlobalVariables;
import org.kuali.core.util.KualiDecimal;
import org.kuali.core.web.ui.Row;
import org.kuali.kfs.KFSConstants;
import org.kuali.kfs.KFSPropertyConstants;
import org.kuali.module.gl.bo.TransientBalanceInquiryAttributes;
import org.kuali.module.gl.web.Constant;
import org.kuali.module.labor.LaborConstants;
import org.kuali.module.labor.bo.AccountStatusCurrentFunds;
import org.kuali.module.labor.bo.July1PositionFunding;
import org.kuali.module.labor.bo.LaborCalculatedSalaryFoundationTracker;
import org.kuali.module.labor.bo.LaborLedgerPendingEntry;
import org.kuali.module.labor.bo.LedgerBalance;
import org.kuali.module.labor.dao.LaborDao;
import org.kuali.module.labor.service.LaborInquiryOptionsService;
import org.kuali.module.labor.service.LaborLedgerBalanceService;
import org.kuali.module.labor.util.ObjectUtil;
import org.kuali.module.labor.web.inquirable.CurrentFundsInquirableImpl;
import org.springframework.transaction.annotation.Transactional;

/**
 * The CurrentFundsLookupableHelperServiceImpl class is the front-end for all current funds balance inquiry processing.
 */
@Transactional
public class CurrentFundsLookupableHelperServiceImpl extends AbstractLookupableHelperServiceImpl {
    private static org.apache.log4j.Logger LOG = org.apache.log4j.Logger.getLogger(CurrentFundsLookupableHelperServiceImpl.class);

    private LaborDao laborDao;
    private LaborLedgerBalanceService balanceService;
    private LaborInquiryOptionsService laborInquiryOptionsService;
    private LookupService lookupService;
    /**
     * @see org.kuali.core.lookup.Lookupable#getInquiryUrl(org.kuali.core.bo.BusinessObject, java.lang.String)
     */
    @Override
    public String getInquiryUrl(BusinessObject bo, String propertyName) {
        return (new CurrentFundsInquirableImpl()).getInquiryUrl(bo, propertyName);
    }

    /**
     * @see org.kuali.core.lookup.Lookupable#getSearchResults(java.util.Map)
     */
    @Override
    public List getSearchResults(Map fieldValues) {
        LOG.info("getSearchResults() - Entry");

        boolean unbounded = false;
        Long actualCountIfTruncated = new Long(0);

        setBackLocation((String) fieldValues.get(KFSConstants.BACK_LOCATION));
        setDocFormKey((String) fieldValues.get(KFSConstants.DOC_FORM_KEY));

        // get the pending entry option. This method must be prior to the get search results
        String pendingEntryOption = laborInquiryOptionsService.getSelectedPendingEntryOption(fieldValues);

        // get the consolidation option
        boolean isConsolidated = laborInquiryOptionsService.isConsolidationSelected(fieldValues, (Collection<Row>) getRows());

        if (((fieldValues.get(KFSPropertyConstants.FINANCIAL_OBJECT_CODE) != null) && (fieldValues.get(KFSPropertyConstants.FINANCIAL_OBJECT_CODE).toString().length() > 0))) {
            List emptySearchResults = new ArrayList();

            // Check for a valid labor object code for this inquiry
            if (StringUtils.indexOfAny(fieldValues.get(KFSPropertyConstants.FINANCIAL_OBJECT_CODE).toString(), LaborConstants.BalanceInquiries.VALID_LABOR_OBJECT_CODES) != 0) {
                GlobalVariables.getErrorMap().putError(KFSPropertyConstants.FINANCIAL_OBJECT_CODE, LaborConstants.BalanceInquiries.ERROR_INVALID_LABOR_OBJECT_CODE, "2");
                return new CollectionIncomplete(emptySearchResults, actualCountIfTruncated);
            }
        }

        // Parse the map and call the DAO to process the inquiry
        Collection<AccountStatusCurrentFunds> searchResultsCollection = buildCurrentFundsCollection(toList(laborDao.getCurrentFunds(fieldValues, isConsolidated)), isConsolidated, pendingEntryOption);        
                
        // update search results according to the selected pending entry option
        laborInquiryOptionsService.updateByPendingLedgerEntry(searchResultsCollection, fieldValues, pendingEntryOption, isConsolidated);
                
        Collection<July1PositionFunding> july1PositionFundings = laborDao.getJuly1(fieldValues);
        
        this.AddJuly1BalanceAmount(searchResultsCollection,july1PositionFundings,isConsolidated);
        
        // sort list if default sort column given
        List searchResults = (List) searchResultsCollection;
        List defaultSortColumns = getDefaultSortColumns();
        if (defaultSortColumns.size() > 0) {
            Collections.sort(searchResults, new BeanPropertyComparator(defaultSortColumns, true));
        }
        return new CollectionIncomplete(searchResults, actualCountIfTruncated);
    }

    
    private void AddJuly1BalanceAmount(Collection <AccountStatusCurrentFunds>searchResultsCollection ,Collection <July1PositionFunding> july1PositionFundings, boolean isConsolidated) {
        for (July1PositionFunding july1PositionFunding : july1PositionFundings) {
            for (AccountStatusCurrentFunds accountStatus  : searchResultsCollection) {
                boolean found = ObjectUtil.compareObject(accountStatus, july1PositionFunding, accountStatus.getKeyFieldList(isConsolidated));
                if (found) {
                    accountStatus.setJuly1BudgetAmount(accountStatus.getJuly1BudgetAmount().add(july1PositionFunding.getJuly1BudgetAmount()));
                    accountStatus.setVariance(accountStatus.getJuly1BudgetAmount().subtract(accountStatus.getMonth1Amount().add(accountStatus.getOutstandingEncum())));
                }
            }
        }
    }
     
    /**
     * @param iterator the iterator of search results of account status
     * @param isConsolidated determine if the consolidated result is desired
     * @param pendingEntryOption the given pending entry option that can be no, approved or all
     * @return the current funds collection
     */
    private Collection<AccountStatusCurrentFunds> buildCurrentFundsCollection(Collection collection, boolean isConsolidated, String pendingEntryOption) {
        Collection<AccountStatusCurrentFunds> retval = null;

        if (isConsolidated) {
            retval = buildCosolidatedCurrentFundsCollection(collection, pendingEntryOption);
        }
        else {
            retval = buildDetailedCurrentFundsCollection(collection, pendingEntryOption);
        }
        return retval;
    }

    /**
     * This method builds the current funds collection with consolidation option from an iterator
     * 
     * @param iterator
     * @param pendingEntryOption the selected pending entry option
     * @return the consolidated current funds collection
     */
    private Collection<AccountStatusCurrentFunds> buildCosolidatedCurrentFundsCollection(Collection collection, String pendingEntryOption) {
        Collection<AccountStatusCurrentFunds> retval = new ArrayList<AccountStatusCurrentFunds>();
        for (Object collectionEntry : collection) {
            if (collectionEntry.getClass().isArray()) {
                int i = 0;
                Object[] array = (Object[]) collectionEntry;
                AccountStatusCurrentFunds cf = new AccountStatusCurrentFunds();
                LOG.debug("element length " + array.length);
                for (Object element : array) {
                    LOG.debug("I found this element " + element);
                }

                if (AccountStatusCurrentFunds.class.isAssignableFrom(getBusinessObjectClass())) {
                    try {
                        cf = (AccountStatusCurrentFunds) getBusinessObjectClass().newInstance();
                    }
                    catch (Exception e) {
                        LOG.warn("Using " + AccountStatusCurrentFunds.class + " for results because I couldn't instantiate the " + getBusinessObjectClass());
                    }
                }
                else {
                    LOG.warn("Using " + AccountStatusCurrentFunds.class + " for results because I couldn't instantiate the " + getBusinessObjectClass());
                }

                cf.setUniversityFiscalYear(new Integer(array[i++].toString()));
                cf.setChartOfAccountsCode(array[i++].toString());
                cf.setAccountNumber(array[i++].toString());

                String subAccountNumber = Constant.CONSOLIDATED_SUB_ACCOUNT_NUMBER;
                cf.setSubAccountNumber(subAccountNumber);

                cf.setBalanceTypeCode(array[i++].toString());
                cf.setFinancialObjectCode(array[i++].toString());

                cf.setEmplid(array[i++].toString());
                cf.setPositionNumber(array[i++].toString());

                cf.setFinancialSubObjectCode(Constant.CONSOLIDATED_SUB_OBJECT_CODE);
                cf.setObjectTypeCode(Constant.CONSOLIDATED_OBJECT_TYPE_CODE);

                cf.setAccountLineAnnualBalanceAmount(new KualiDecimal(array[i++].toString()));
                cf.setBeginningBalanceLineAmount(new KualiDecimal(array[i++].toString()));
                cf.setContractsGrantsBeginningBalanceAmount(new KualiDecimal(array[i++].toString()));

                cf.setMonth1Amount(new KualiDecimal(array[i++].toString()));

                cf.setDummyBusinessObject(new TransientBalanceInquiryAttributes());
                cf.getDummyBusinessObject().setPendingEntryOption(pendingEntryOption);
                cf.setOutstandingEncum(getOutstandingEncum(cf));
                
                cf.getDummyBusinessObject().setPendingEntryOption(pendingEntryOption);
                cf.getDummyBusinessObject().setConsolidationOption(Constant.CONSOLIDATION);

                retval.add(cf);
            }
        }
        return retval;
    }

    /**
     * This method builds the current funds collection with detail option from an iterator
     * 
     * @param iterator the current funds iterator
     * @param pendingEntryOption the selected pending entry option
     * @return the detailed balance collection
     */
    private Collection<AccountStatusCurrentFunds> buildDetailedCurrentFundsCollection(Collection collection, String pendingEntryOption) {
        Collection<AccountStatusCurrentFunds> retval = new ArrayList<AccountStatusCurrentFunds>();

        for (LedgerBalance balance : ((Collection<LedgerBalance>) collection)) {
            AccountStatusCurrentFunds cf = new AccountStatusCurrentFunds();
            ObjectUtil.buildObject(cf, balance);

            cf.setDummyBusinessObject(new TransientBalanceInquiryAttributes());
            cf.getDummyBusinessObject().setPendingEntryOption(pendingEntryOption);
            cf.setOutstandingEncum(getOutstandingEncum(cf));
            
            cf.getDummyBusinessObject().setPendingEntryOption(pendingEntryOption);
            cf.getDummyBusinessObject().setConsolidationOption(Constant.DETAIL);
            
            retval.add(cf);
        }
        return retval;
    }

    /**
     * @param AccountStatusCurrentFunds
     * @param Map fieldValues
     */
    private KualiDecimal getOutstandingEncum(AccountStatusCurrentFunds bo) {
        Map<String, String> fieldValues = new HashMap();
        fieldValues.put(KFSPropertyConstants.UNIVERSITY_FISCAL_YEAR, bo.getUniversityFiscalYear().toString());
        fieldValues.put(KFSPropertyConstants.CHART_OF_ACCOUNTS_CODE, bo.getChartOfAccountsCode());
        fieldValues.put(KFSPropertyConstants.ACCOUNT_NUMBER, bo.getAccountNumber());

        if (!bo.getSubAccountNumber().equals(Constant.CONSOLIDATED_SUB_ACCOUNT_NUMBER)) {
            fieldValues.put(KFSPropertyConstants.SUB_ACCOUNT_NUMBER, bo.getSubAccountNumber());
        }

        fieldValues.put(KFSPropertyConstants.FINANCIAL_OBJECT_CODE, bo.getFinancialObjectCode());

        if (!bo.getFinancialSubObjectCode().equals(Constant.CONSOLIDATED_SUB_OBJECT_CODE)) {
            fieldValues.put(KFSPropertyConstants.FINANCIAL_SUB_OBJECT_CODE, bo.getFinancialSubObjectCode());
        }
        fieldValues.put(KFSPropertyConstants.FINANCIAL_BALANCE_TYPE_CODE, LaborConstants.BalanceInquiries.ENCUMBERENCE_CODE); // Encumberance
                                                                                                                                // Balance
                                                                                                                                // Type
        fieldValues.put(KFSPropertyConstants.EMPLID, bo.getEmplid());
        LOG.debug("using " + fieldValues.values());
        LOG.debug("using " + fieldValues.keySet());
        return (KualiDecimal) laborDao.getEncumbranceTotal(fieldValues);
    }

/**
 * 
 * This method...
 * @param bo
 * @return
 *
    private KualiDecimal getJuly1BudgetAmount(Map fieldValues, AccountStatusCurrentFunds bo) {
        System.out.println("**** CurrentFundsLookupableHelperServiceImpl.getJuly1BudgetAmount()");
        
        fieldValues.remove(KFSPropertyConstants.FINANCIAL_BALANCE_TYPE_CODE);

        if (!bo.getSubAccountNumber().equals(Constant.CONSOLIDATED_SUB_ACCOUNT_NUMBER)) {
            fieldValues.put(KFSPropertyConstants.SUB_ACCOUNT_NUMBER, bo.getSubAccountNumber());
        }

        //fieldValues.put(KFSPropertyConstants.FINANCIAL_OBJECT_CODE, bo.getFinancialObjectCode());

        if (!bo.getFinancialSubObjectCode().equals(Constant.CONSOLIDATED_SUB_OBJECT_CODE)) {
            fieldValues.put(KFSPropertyConstants.FINANCIAL_SUB_OBJECT_CODE, bo.getFinancialSubObjectCode());
        }
        fieldValues.put(KFSPropertyConstants.FINANCIAL_BALANCE_TYPE_CODE, LaborConstants.BalanceInquiries.ENCUMBERENCE_CODE); // Encumberance
                                                                                                                                // Balance
                                                                                                                                // Type
        fieldValues.put(KFSPropertyConstants.EMPLID, bo.getEmplid());
        LOG.debug("using " + fieldValues.values());
        LOG.debug("using " + fieldValues.keySet());
        return (KualiDecimal) laborDao.getEncumbranceTotal(fieldValues);
    }
*/
    /**
     * Sets the balanceService attribute value.
     * 
     * @param balanceService The balanceService to set.
     */
    public void setBalanceService(LaborLedgerBalanceService balanceService) {
        this.balanceService = balanceService;
    }

    /**
     * Sets the laborDao attribute value.
     * 
     * @param laborDao The laborDao to set.
     */
    public void setLaborDao(LaborDao laborDao) {
        this.laborDao = laborDao;
    }

    /**
     * Sets the laborInquiryOptionsService attribute value.
     * 
     * @param laborInquiryOptionsService The laborInquiryOptionsService to set.
     */
    public void setLaborInquiryOptionsService(LaborInquiryOptionsService laborInquiryOptionsService) {
        this.laborInquiryOptionsService = laborInquiryOptionsService;
    }

}
