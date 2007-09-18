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
import java.util.List;
import java.util.Map;

import org.kuali.core.bo.BusinessObject;
import org.kuali.core.lookup.AbstractLookupableHelperServiceImpl;
import org.kuali.core.lookup.CollectionIncomplete;
import org.kuali.core.util.BeanPropertyComparator;
import org.kuali.kfs.KFSConstants;
import org.kuali.kfs.KFSPropertyConstants;
import org.kuali.module.gl.bo.Entry;
import org.kuali.module.gl.web.inquirable.EntryInquirableImpl;
import org.kuali.module.gl.web.inquirable.InquirableFinancialDocument;
import org.kuali.module.labor.bo.LedgerEntry;
import org.kuali.module.labor.bo.LedgerEntryForExpenseTransfer;
import org.springframework.transaction.annotation.Transactional;

@Transactional
public class LedgerEntryForExpenseTransferLookupableHelperServiceImpl extends AbstractLookupableHelperServiceImpl {
    private static final org.apache.log4j.Logger LOG = org.apache.log4j.Logger.getLogger(LedgerEntryForExpenseTransferLookupableHelperServiceImpl.class);

    /**
     * @see org.kuali.core.lookup.Lookupable#getInquiryUrl(org.kuali.core.bo.BusinessObject, java.lang.String)
     */
    @Override
    public String getInquiryUrl(BusinessObject businessObject, String propertyName) {
        if (KFSPropertyConstants.DOCUMENT_NUMBER.equals(propertyName)) {
            if (businessObject instanceof Entry) {
                Entry entry = (Entry) businessObject;
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

        // get the ledger balances with actual balance type code
        fieldValues.put(KFSPropertyConstants.FINANCIAL_BALANCE_TYPE_CODE, KFSConstants.BALANCE_TYPE_ACTUAL);
        Collection actualEntries = getLookupService().findCollectionBySearch(LedgerEntry.class, fieldValues);

        // get the ledger balances with effort balance type code
        fieldValues.put(KFSPropertyConstants.FINANCIAL_BALANCE_TYPE_CODE, KFSConstants.BALANCE_TYPE_A21);
        Collection effortEntries = getLookupService().findCollectionBySearch(LedgerEntry.class, fieldValues);

        // get the search result collection
        Collection consolidatedEntries = new ArrayList();
        consolidatedEntries.addAll(actualEntries);
        consolidatedEntries.addAll(effortEntries);

        // get the actual size of all qualified search results
        Long actualSize = new Long(consolidatedEntries.size());
        
        return this.buildSearchResultList(consolidatedEntries, actualSize);
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
}
