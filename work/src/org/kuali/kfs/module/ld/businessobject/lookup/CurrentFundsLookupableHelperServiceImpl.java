/*
 * Copyright 2007 The Kuali Foundation
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
package org.kuali.kfs.module.ld.businessobject.lookup;

import static org.apache.commons.collections.IteratorUtils.toList;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang.StringUtils;
import org.kuali.kfs.gl.Constant;
import org.kuali.kfs.gl.businessobject.TransientBalanceInquiryAttributes;
import org.kuali.kfs.integration.ld.LaborLedgerObject;
import org.kuali.kfs.module.ld.LaborKeyConstants;
import org.kuali.kfs.module.ld.businessobject.AccountStatusCurrentFunds;
import org.kuali.kfs.module.ld.businessobject.July1PositionFunding;
import org.kuali.kfs.module.ld.businessobject.LaborObject;
import org.kuali.kfs.module.ld.businessobject.LedgerBalance;
import org.kuali.kfs.module.ld.businessobject.inquiry.AbstractLaborInquirableImpl;
import org.kuali.kfs.module.ld.businessobject.inquiry.CurrentFundsInquirableImpl;
import org.kuali.kfs.module.ld.businessobject.inquiry.PositionDataDetailsInquirableImpl;
import org.kuali.kfs.module.ld.dataaccess.LaborDao;
import org.kuali.kfs.module.ld.service.LaborInquiryOptionsService;
import org.kuali.kfs.module.ld.service.LaborLedgerBalanceService;
import org.kuali.kfs.sys.KFSConstants;
import org.kuali.kfs.sys.KFSPropertyConstants;
import org.kuali.kfs.sys.ObjectUtil;
import org.kuali.rice.core.api.util.type.KualiDecimal;
import org.kuali.rice.kns.lookup.AbstractLookupableHelperServiceImpl;
import org.kuali.rice.kns.lookup.HtmlData;
import org.kuali.rice.kns.lookup.HtmlData.AnchorHtmlData;
import org.kuali.rice.kns.web.ui.Row;
import org.kuali.rice.krad.bo.BusinessObject;
import org.kuali.rice.krad.lookup.CollectionIncomplete;
import org.kuali.rice.krad.service.BusinessObjectService;
import org.kuali.rice.krad.util.BeanPropertyComparator;
import org.kuali.rice.krad.util.GlobalVariables;

/**
 * Service implementation for the CurrentFundsLookupableHelperServiceImpl class is the front-end for all current funds balance
 * inquiry processing.
 */
public class CurrentFundsLookupableHelperServiceImpl extends AbstractLookupableHelperServiceImpl {
    private static org.apache.log4j.Logger LOG = org.apache.log4j.Logger.getLogger(CurrentFundsLookupableHelperServiceImpl.class);
    private LaborDao laborDao;
    private LaborLedgerBalanceService balanceService;
    private LaborInquiryOptionsService laborInquiryOptionsService;
    private BusinessObjectService businessObjectService;

    /**
     * @see org.kuali.rice.kns.lookup.Lookupable#getInquiryUrl(org.kuali.rice.krad.bo.BusinessObject, java.lang.String)
     */
    @Override
    public HtmlData getInquiryUrl(BusinessObject bo, String propertyName) {
        if (KFSPropertyConstants.POSITION_NUMBER.equals(propertyName)) {
            LedgerBalance balance = (LedgerBalance) bo;
            AbstractLaborInquirableImpl positionDataDetailsInquirable = new PositionDataDetailsInquirableImpl();

            Map<String, String> fieldValues = new HashMap<String, String>();
            fieldValues.put(propertyName, balance.getPositionNumber());

            BusinessObject positionData = positionDataDetailsInquirable.getBusinessObject(fieldValues);

            return positionData == null ? new AnchorHtmlData(KFSConstants.EMPTY_STRING, KFSConstants.EMPTY_STRING) : positionDataDetailsInquirable.getInquiryUrl(positionData, propertyName);
        }
        return (new CurrentFundsInquirableImpl()).getInquiryUrl(bo, propertyName);
    }

    /**
     * Gets a list with the fields that will be displayed on page
     * 
     * @param fieldValues list of fields that are used as a key to filter out data
     * @see org.kuali.rice.kns.lookup.Lookupable#getSearchResults(java.util.Map)
     * 
     * KRAD Conversion: Lookupable performs customization of the search results.
     * 
     * No uses data dictionary.
     */
    @Override
    public List<? extends BusinessObject> getSearchResults(Map<String, String> fieldValues) {
        LOG.info("getSearchResults() - Entry");

        boolean unbounded = false;
        Long actualCountIfTruncated = new Long(0);

        setBackLocation((String) fieldValues.get(KFSConstants.BACK_LOCATION));
        setDocFormKey((String) fieldValues.get(KFSConstants.DOC_FORM_KEY));

        // get the pending entry option. This method must be prior to the get search results
        String pendingEntryOption = laborInquiryOptionsService.getSelectedPendingEntryOption(fieldValues);

        // get the consolidation option
        boolean isConsolidated = laborInquiryOptionsService.isConsolidationSelected(fieldValues, (Collection<Row>) getRows());

        String searchObjectCodeVal = (String) fieldValues.get(KFSPropertyConstants.FINANCIAL_OBJECT_CODE);
        // Check for a valid labor object code for this inquiry
        if (StringUtils.isNotBlank(searchObjectCodeVal)) {
            Map objectCodeFieldValues = new HashMap();
            objectCodeFieldValues.put(KFSPropertyConstants.UNIVERSITY_FISCAL_YEAR, fieldValues.get(KFSPropertyConstants.UNIVERSITY_FISCAL_YEAR));
            objectCodeFieldValues.put(KFSPropertyConstants.CHART_OF_ACCOUNTS_CODE, fieldValues.get(KFSPropertyConstants.CHART_OF_ACCOUNTS_CODE));
            objectCodeFieldValues.put(KFSPropertyConstants.FINANCIAL_OBJECT_CODE, searchObjectCodeVal);

            LaborLedgerObject foundObjectCode = (LaborLedgerObject) businessObjectService.findByPrimaryKey(LaborObject.class, objectCodeFieldValues);

            if (foundObjectCode == null) {
                GlobalVariables.getMessageMap().putError(KFSPropertyConstants.FINANCIAL_OBJECT_CODE, LaborKeyConstants.ERROR_INVALID_LABOR_OBJECT_CODE, "2");
                return new CollectionIncomplete(new ArrayList(), actualCountIfTruncated);
            }
        }

        // Parse the map and call the DAO to process the inquiry
        Collection<AccountStatusCurrentFunds> searchResultsCollection = buildCurrentFundsCollection(toList(laborDao.getCurrentFunds(fieldValues, isConsolidated)), isConsolidated, pendingEntryOption);

        // update search results according to the selected pending entry option
        laborInquiryOptionsService.updateCurrentFundsByPendingLedgerEntry(searchResultsCollection, fieldValues, pendingEntryOption, isConsolidated);

        // gets the July1st budget amount column.
        Collection<July1PositionFunding> july1PositionFundings = laborDao.getJuly1(fieldValues);
        this.updateJuly1BalanceAmount(searchResultsCollection, july1PositionFundings, isConsolidated);

        // sort list if default sort column given
        List searchResults = (List) searchResultsCollection;
        List defaultSortColumns = getDefaultSortColumns();
        if (defaultSortColumns.size() > 0) {
            Collections.sort(searchResults, new BeanPropertyComparator(defaultSortColumns, true));
        }
        return new CollectionIncomplete(searchResults, actualCountIfTruncated);
    }

    /**
     * Adds the july1 budget amount to each account found in the
     * 
     * @param searchResultsCollection collection with the list of current funds where the amount is added
     * @param july1PositionFundings collection of current funds with july1st budget amounts
     * @param isConsolidated
     */
    private void updateJuly1BalanceAmount(Collection<AccountStatusCurrentFunds> searchResultsCollection, Collection<July1PositionFunding> july1PositionFundings, boolean isConsolidated) {
        for (July1PositionFunding july1PositionFunding : july1PositionFundings) {
            for (AccountStatusCurrentFunds accountStatus : searchResultsCollection) {
                boolean found = ObjectUtil.equals(accountStatus, july1PositionFunding, accountStatus.getKeyFieldList(isConsolidated));
                if (found) {
                    accountStatus.setJuly1BudgetAmount(accountStatus.getJuly1BudgetAmount().add(july1PositionFunding.getJuly1BudgetAmount()));
                }
            }
        }
    }

    /**
     * Returns a list with the current funds.
     * 
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
     * Builds the current funds collection with consolidation option from an iterator
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
                if (LOG.isDebugEnabled()) {
                    LOG.debug("element length " + array.length);
                }
                for (Object element : array) {
                    if (LOG.isDebugEnabled()) {
                        LOG.debug("I found this element " + element);    
                    }
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
     * Builds the current funds collection with detail option from an iterator
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
     * Gets the outstanding encumbrance amount
     * 
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
        fieldValues.put(KFSPropertyConstants.FINANCIAL_BALANCE_TYPE_CODE, KFSConstants.BALANCE_TYPE_INTERNAL_ENCUMBRANCE); // Encumberance
        // Balance
        // Type
        fieldValues.put(KFSPropertyConstants.EMPLID, bo.getEmplid());
        if (LOG.isDebugEnabled()) {
            LOG.debug("using " + fieldValues.values());
            LOG.debug("using " + fieldValues.keySet());    
        }
        return (KualiDecimal) laborDao.getEncumbranceTotal(fieldValues);
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

    /**
     * Sets the businessObjectService attribute value.
     * 
     * @param businessObjectService The businessObjectService to set.
     */
    public void setBusinessObjectService(BusinessObjectService businessObjectService) {
        this.businessObjectService = businessObjectService;
    }

}
