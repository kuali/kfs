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
import java.util.List;
import java.util.Map;

import org.kuali.core.bo.BusinessObject;
import org.kuali.kfs.KFSConstants;
import org.kuali.kfs.KFSPropertyConstants;
import org.kuali.module.labor.bo.LedgerEntry;
import org.springframework.transaction.annotation.Transactional;

/**
 * The class is the front-end for the balance inquiry of Ledger entry For Expense Transfer processing.
 */
public class LedgerEntryForExpenseTransferLookupableHelperServiceImpl extends LedgerEntryLookupableHelperServiceImpl {
    private static final org.apache.log4j.Logger LOG = org.apache.log4j.Logger.getLogger(LedgerEntryForExpenseTransferLookupableHelperServiceImpl.class);

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
}
