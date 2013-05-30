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

import static org.kuali.kfs.module.ld.LaborConstants.BalanceInquiries.EMPLOYEE_FUNDING_EXPENSE_OBJECT_TYPE_CODE;
import static org.kuali.kfs.module.ld.LaborConstants.BalanceInquiries.EMPLOYEE_FUNDING_NORMAL_OP_EXPENSE_OBJECT_TYPE_CODE;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang.ArrayUtils;
import org.apache.commons.lang.StringUtils;
import org.kuali.kfs.gl.Constant;
import org.kuali.kfs.module.ld.businessobject.EmployeeFunding;
import org.kuali.kfs.module.ld.businessobject.LaborLedgerPendingEntry;
import org.kuali.kfs.module.ld.businessobject.inquiry.AbstractLaborInquirableImpl;
import org.kuali.kfs.module.ld.businessobject.inquiry.EmployeeFundingInquirableImpl;
import org.kuali.kfs.module.ld.businessobject.inquiry.PositionDataDetailsInquirableImpl;
import org.kuali.kfs.module.ld.service.LaborInquiryOptionsService;
import org.kuali.kfs.module.ld.service.LaborLedgerBalanceService;
import org.kuali.kfs.module.ld.service.LaborLedgerPendingEntryService;
import org.kuali.kfs.module.ld.util.DebitCreditUtil;
import org.kuali.kfs.sys.KFSConstants;
import org.kuali.kfs.sys.KFSPropertyConstants;
import org.kuali.kfs.sys.ObjectUtil;
import org.kuali.rice.core.api.util.type.KualiDecimal;
import org.kuali.rice.kns.lookup.AbstractLookupableHelperServiceImpl;
import org.kuali.rice.kns.lookup.HtmlData;
import org.kuali.rice.kns.lookup.HtmlData.AnchorHtmlData;
import org.kuali.rice.krad.bo.BusinessObject;
import org.kuali.rice.krad.lookup.CollectionIncomplete;
import org.kuali.rice.krad.util.BeanPropertyComparator;

/**
 * The EmployeeFundingLookupableHelperServiceImpl class is the front-end for all Employee Funding balance inquiry processing.
 */
public class EmployeeFundingLookupableHelperServiceImpl extends AbstractLookupableHelperServiceImpl {
    private static final org.apache.commons.logging.Log LOG = org.apache.commons.logging.LogFactory.getLog(EmployeeFundingLookupableHelperServiceImpl.class);

    private LaborLedgerBalanceService laborLedgerBalanceService;
    private LaborInquiryOptionsService laborInquiryOptionsService;
    private LaborLedgerPendingEntryService laborLedgerPendingEntryService;

    /**
     * @see org.kuali.rice.kns.lookup.Lookupable#getInquiryUrl(org.kuali.rice.krad.bo.BusinessObject, java.lang.String)
     */
    @Override
    public HtmlData getInquiryUrl(BusinessObject bo, String propertyName) {
        if (KFSPropertyConstants.POSITION_NUMBER.equals(propertyName)) {
            EmployeeFunding employeeFunding = (EmployeeFunding) bo;
            AbstractLaborInquirableImpl positionDataDetailsInquirable = new PositionDataDetailsInquirableImpl();

            Map<String, String> fieldValues = new HashMap<String, String>();
            fieldValues.put(propertyName, employeeFunding.getPositionNumber());

            BusinessObject positionData = positionDataDetailsInquirable.getBusinessObject(fieldValues);

            return positionData == null ? new AnchorHtmlData(KFSConstants.EMPTY_STRING, KFSConstants.EMPTY_STRING) : positionDataDetailsInquirable.getInquiryUrl(positionData, propertyName);
        }

        return (new EmployeeFundingInquirableImpl()).getInquiryUrl(bo, propertyName);
    }

    /**
     * @see org.kuali.rice.kns.lookup.Lookupable#gfetSearchResults(java.util.Map)
     */
    @Override
    public List<? extends BusinessObject> getSearchResults(Map<String, String> fieldValues) {
        setBackLocation((String) fieldValues.get(KFSConstants.BACK_LOCATION));
        setDocFormKey((String) fieldValues.get(KFSConstants.DOC_FORM_KEY));

        boolean showBlankLine = this.showBlankLines(fieldValues);
        fieldValues.remove(Constant.BLANK_LINE_OPTION);

        // get the pending entry option. This method must be prior to the get search results
        String pendingEntryOption = laborInquiryOptionsService.getSelectedPendingEntryOption(fieldValues);

        // test if the consolidation option is selected or not
        boolean isConsolidated = false;

        Collection<EmployeeFunding> searchResultsCollection = laborLedgerBalanceService.findEmployeeFundingWithCSFTracker(fieldValues, isConsolidated);

        if (!showBlankLine) {
            Collection<EmployeeFunding> tempSearchResultsCollection = new ArrayList<EmployeeFunding>();
            for (EmployeeFunding employeeFunding : searchResultsCollection) {
                if (employeeFunding.getCurrentAmount().isNonZero() || employeeFunding.getOutstandingEncumbrance().isNonZero()) {
                    tempSearchResultsCollection.add(employeeFunding);
                }
            }
            searchResultsCollection = tempSearchResultsCollection;
        }

        // update search results according to the selected pending entry option
        updateByPendingLedgerEntry(searchResultsCollection, fieldValues, pendingEntryOption, isConsolidated);
        searchResultsCollection = consolidateObjectTypeCode(searchResultsCollection);
        // get the actual size of all qualified search results
        Long actualSize = new Long(searchResultsCollection.size());

        return this.buildSearchResultList(searchResultsCollection, actualSize);
    }

    /**
     * Filter search results to consolidate them if the only difference is object type code.
     *
     * @param searchResultsCollection
     * @return
     */
    private Collection<EmployeeFunding> consolidateObjectTypeCode(Collection<EmployeeFunding> searchResultsCollection) {
        Map map = new HashMap<String, EmployeeFunding>();
        String hashString;
        EmployeeFunding temp;
        for(EmployeeFunding empFunding : searchResultsCollection) {
            hashString = empFunding.getEmplid() + empFunding.getUniversityFiscalYear() + empFunding.getChartOfAccountsCode() + empFunding.getAccountNumber() +
                    empFunding.getObjectCode() + empFunding.getPositionNumber();
            if (map.containsKey(hashString)) {
                temp = ((EmployeeFunding)map.get(hashString));
                if (temp.getCsfFullTimeEmploymentQuantity() != null && empFunding.getCsfFullTimeEmploymentQuantity() != null) {
                    temp.setCsfFullTimeEmploymentQuantity(temp.getCsfFullTimeEmploymentQuantity().add(empFunding.getCsfFullTimeEmploymentQuantity()));
                }
                if (temp.getCsfAmount() != null && empFunding.getCsfAmount() != null) {
                    temp.setCsfAmount(temp.getCsfAmount().add(empFunding.getCsfAmount()));
                }
                if (temp.getCurrentAmount() != null && empFunding.getCurrentAmount() != null) {
                    temp.setCurrentAmount(temp.getCurrentAmount().add(empFunding.getCurrentAmount()));
                }
                if (temp.getOutstandingEncumbrance() != null && empFunding.getOutstandingEncumbrance() != null) {
                    temp.setOutstandingEncumbrance(temp.getOutstandingEncumbrance().add(empFunding.getOutstandingEncumbrance()));
                }
                if (temp.getTotalAmount() != null && empFunding.getTotalAmount() != null) {
                    temp.setTotalAmount(temp.getTotalAmount().add(empFunding.getTotalAmount()));
                }
                map.put(hashString, temp);
            } else {
                map.put(hashString, empFunding);
            }
        }
        return map.values();
    }

    private boolean showBlankLines(Map fieldValues) {
        String pendingEntryOption = (String) fieldValues.get(Constant.BLANK_LINE_OPTION);
        return Constant.SHOW_BLANK_LINE.equals(pendingEntryOption) ? true : false;
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
        List searchResults = results;
        List defaultSortColumns = getDefaultSortColumns();
        if (defaultSortColumns.size() > 0) {
            Collections.sort(results, new BeanPropertyComparator(defaultSortColumns, true));
        }
        return searchResults;
    }

    /**
     * @see org.kuali.kfs.module.ld.service.LaborInquiryOptionsService#updateByPendingLedgerEntry(java.util.Collection,
     *      java.util.Map, java.lang.String, boolean)
     */
    public void updateByPendingLedgerEntry(Collection entryCollection, Map fieldValues, String pendingEntryOption, boolean isConsolidated) {

        // determine if search results need to be updated by pending ledger entries
        if (Constant.ALL_PENDING_ENTRY.equals(pendingEntryOption)) {
            updateEntryCollection(entryCollection, fieldValues, false, isConsolidated);
        }
        else if (Constant.APPROVED_PENDING_ENTRY.equals(pendingEntryOption)) {
            updateEntryCollection(entryCollection, fieldValues, true, isConsolidated);
        }
    }

    /**
     * @see org.kuali.kfs.module.ld.service.LaborInquiryOptionsService#updateEntryCollection(java.util.Collection, java.util.Map,
     *      boolean, boolean)
     */
    public void updateEntryCollection(Collection entryCollection, Map fieldValues, boolean isApproved, boolean isConsolidated) {
        // go through the pending entries to update the balance collection
        Iterator<LaborLedgerPendingEntry> pendingEntryIterator = laborLedgerPendingEntryService.findPendingLedgerEntriesForLedgerBalance(fieldValues, isApproved);

        while (pendingEntryIterator.hasNext()) {
            LaborLedgerPendingEntry pendingEntry = pendingEntryIterator.next();

            if (!isEmployeeFunding(pendingEntry)) {
                continue;
            }

            // if consolidated, change the following fields into the default values for consolidation
            if (isConsolidated) {
                pendingEntry.setSubAccountNumber(Constant.CONSOLIDATED_SUB_ACCOUNT_NUMBER);
                pendingEntry.setFinancialSubObjectCode(Constant.CONSOLIDATED_SUB_OBJECT_CODE);
                pendingEntry.setFinancialObjectTypeCode(Constant.CONSOLIDATED_OBJECT_TYPE_CODE);
            }

            EmployeeFunding ledgerBalance = (EmployeeFunding) laborLedgerBalanceService.findLedgerBalance(entryCollection, pendingEntry, getKeyList());
            if (ledgerBalance == null) {
                ledgerBalance = new EmployeeFunding();
                ObjectUtil.buildObject(ledgerBalance, pendingEntry);
                entryCollection.add(ledgerBalance);
            }
            else {
                laborLedgerBalanceService.updateLedgerBalance(ledgerBalance, pendingEntry);
            }
            updateAmount(ledgerBalance, pendingEntry);
        }
    }

    /**
     * update the amount of the given employee funding with the given pending entry
     *
     * @param employeeFunding the given employee funding
     * @param pendingEntry the given pending entry
     */
    private void updateAmount(EmployeeFunding employeeFunding, LaborLedgerPendingEntry pendingEntry) {
        String balanceTypeCode = pendingEntry.getFinancialBalanceTypeCode();
        String debitCreditCode = pendingEntry.getTransactionDebitCreditCode();
        KualiDecimal amount = DebitCreditUtil.getNumericAmount(pendingEntry.getTransactionLedgerEntryAmount(), pendingEntry.getTransactionDebitCreditCode());

        if (StringUtils.equals(balanceTypeCode, KFSConstants.BALANCE_TYPE_ACTUAL)) {
            employeeFunding.setCurrentAmount(amount.add(employeeFunding.getCurrentAmount()));
        }
        else if (StringUtils.equals(balanceTypeCode, KFSConstants.BALANCE_TYPE_INTERNAL_ENCUMBRANCE)) {
            employeeFunding.setOutstandingEncumbrance(amount.add(employeeFunding.getOutstandingEncumbrance()));
        }
    }

    /**
     * determine whether the given pending entry is qualified to be processed as an employee funding
     *
     * @param pendingEntry the given pending entry
     * @return true if the given pending entry is qualified to be processed as an employee funding; otherwise, false
     */
    private boolean isEmployeeFunding(LaborLedgerPendingEntry pendingEntry) {
        String balanceTypeCode = pendingEntry.getFinancialBalanceTypeCode();

        if (StringUtils.equals(balanceTypeCode, KFSConstants.BALANCE_TYPE_ACTUAL)) {
            String objectTypeCode = pendingEntry.getFinancialObjectTypeCode();
            String[] objectTypeCodes = { EMPLOYEE_FUNDING_EXPENSE_OBJECT_TYPE_CODE, EMPLOYEE_FUNDING_NORMAL_OP_EXPENSE_OBJECT_TYPE_CODE };

            return ArrayUtils.contains(objectTypeCodes, objectTypeCode) ? true : false;
        }

        if (StringUtils.equals(balanceTypeCode, KFSConstants.BALANCE_TYPE_INTERNAL_ENCUMBRANCE)) {
            return true;
        }

        return false;
    }

    /**
     * construct the primary key list of the business object
     *
     * @return the primary key list of the business object
     */
    private List<String> getKeyList() {
        List<String> keyList = new ArrayList<String>();
        keyList.add(KFSPropertyConstants.UNIVERSITY_FISCAL_YEAR);
        keyList.add(KFSPropertyConstants.CHART_OF_ACCOUNTS_CODE);
        keyList.add(KFSPropertyConstants.ACCOUNT_NUMBER);
        keyList.add(KFSPropertyConstants.SUB_ACCOUNT_NUMBER);
        keyList.add(KFSPropertyConstants.FINANCIAL_OBJECT_CODE);
        keyList.add(KFSPropertyConstants.FINANCIAL_SUB_OBJECT_CODE);
        keyList.add(KFSPropertyConstants.FINANCIAL_OBJECT_TYPE_CODE);
        keyList.add(KFSPropertyConstants.POSITION_NUMBER);
        keyList.add(KFSPropertyConstants.EMPLID);
        return keyList;
    }

    /**
     * Sets the laborLedgerBalanceService attribute value.
     *
     * @param laborLedgerBalanceService The laborLedgerBalanceService to set.
     */
    public void setLaborLedgerBalanceService(LaborLedgerBalanceService laborLedgerBalanceService) {
        this.laborLedgerBalanceService = laborLedgerBalanceService;
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
     * Sets the laborLedgerPendingEntryService attribute value.
     *
     * @param laborLedgerPendingEntryService The laborLedgerPendingEntryService to set.
     */
    public void setLaborLedgerPendingEntryService(LaborLedgerPendingEntryService laborLedgerPendingEntryService) {
        this.laborLedgerPendingEntryService = laborLedgerPendingEntryService;
    }
}
