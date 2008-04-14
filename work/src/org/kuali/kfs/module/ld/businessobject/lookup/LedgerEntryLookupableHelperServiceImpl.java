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

import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang.StringUtils;
import org.kuali.core.bo.BusinessObject;
import org.kuali.core.lookup.AbstractLookupableHelperServiceImpl;
import org.kuali.core.lookup.CollectionIncomplete;
import org.kuali.core.util.BeanPropertyComparator;
import org.kuali.kfs.KFSConstants;
import org.kuali.kfs.KFSPropertyConstants;
import org.kuali.module.gl.web.inquirable.EntryInquirableImpl;
import org.kuali.module.gl.web.inquirable.InquirableFinancialDocument;
import org.kuali.module.labor.bo.LedgerEntry;
import org.kuali.module.labor.service.LaborInquiryOptionsService;
import org.springframework.transaction.annotation.Transactional;

/**
 * The class is the front-end for all Ledger Entry inquiry processing.
 * 
 * @see org.kuali.module.labor.bo.LedgerEntry
 */
public class LedgerEntryLookupableHelperServiceImpl extends AbstractLookupableHelperServiceImpl {
    private static final org.apache.log4j.Logger LOG = org.apache.log4j.Logger.getLogger(LedgerEntryLookupableHelperServiceImpl.class);

    private LaborInquiryOptionsService laborInquiryOptionsService;

    /**
     * @see org.kuali.core.lookup.Lookupable#getInquiryUrl(org.kuali.core.bo.BusinessObject, java.lang.String)
     */
    @Override
    public String getInquiryUrl(BusinessObject businessObject, String propertyName) {
        if (KFSPropertyConstants.DOCUMENT_NUMBER.equals(propertyName)) {
            if (businessObject instanceof LedgerEntry) {
                LedgerEntry entry = (LedgerEntry) businessObject;
                return new InquirableFinancialDocument().getInquirableDocumentUrl(entry);
            }
        }
        return (new EntryInquirableImpl()).getInquiryUrl(businessObject, propertyName);
    }

    /**
     * @see org.kuali.core.lookup.AbstractLookupableHelperServiceImpl#getSearchResults(java.util.Map)
     */
    @Override
    public List<? extends BusinessObject> getSearchResults(Map<String, String> fieldValues) {
        setBackLocation((String) fieldValues.get(KFSConstants.BACK_LOCATION));
        setDocFormKey((String) fieldValues.get(KFSConstants.DOC_FORM_KEY));

        // get the pending entry option. This method must be prior to the get search results
        String pendingEntryOption = laborInquiryOptionsService.getSelectedPendingEntryOption(fieldValues);

        // get the input balance type code
        String balanceTypeCode = fieldValues.get(KFSPropertyConstants.FINANCIAL_BALANCE_TYPE_CODE);
        boolean isA21Balance = StringUtils.isNotEmpty(balanceTypeCode) && BALANCE_TYPE_AC_AND_A21.equals(balanceTypeCode.trim());

        if (isA21Balance) {
            fieldValues.put(KFSPropertyConstants.FINANCIAL_BALANCE_TYPE_CODE, KFSConstants.BALANCE_TYPE_ACTUAL);
        }

        Collection<LedgerEntry> ledgerEntries = getLookupService().findCollectionBySearch(LedgerEntry.class, fieldValues);
        laborInquiryOptionsService.updateLedgerEntryByPendingLedgerEntry(ledgerEntries, fieldValues, pendingEntryOption);

        // add the ledger entries into the search results if the searching balance type code is A21
        if (isA21Balance) {
            fieldValues.put(KFSPropertyConstants.FINANCIAL_BALANCE_TYPE_CODE, KFSConstants.BALANCE_TYPE_A21);
            Collection<LedgerEntry> effortLedgerEntries = getLookupService().findCollectionBySearch(LedgerEntry.class, fieldValues);
            laborInquiryOptionsService.updateLedgerEntryByPendingLedgerEntry(effortLedgerEntries, fieldValues, pendingEntryOption);

            ledgerEntries.addAll(effortLedgerEntries);
        }

        // get the actual size of all qualified search results
        Long actualSize = new Long(ledgerEntries.size());
        return this.buildSearchResultList(ledgerEntries, actualSize);
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
}