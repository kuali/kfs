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

import org.kuali.core.bo.BusinessObject;
import org.kuali.kfs.KFSConstants;
import org.kuali.kfs.bo.GeneralLedgerPendingEntry;
import org.kuali.module.gl.batch.poster.EncumbranceCalculator;
import org.kuali.module.gl.bo.Encumbrance;
import org.kuali.module.gl.service.EncumbranceService;
import org.kuali.module.gl.util.BusinessObjectFieldConverter;
import org.kuali.module.gl.util.OJBUtility;
import org.kuali.module.gl.web.inquirable.EncumbranceInquirableImpl;
import org.springframework.transaction.annotation.Transactional;

/**
 * An extension of KualiLookupableImpl to support encumbrance lookups
 */
@Transactional
public class EncumbranceLookupableHelperServiceImpl extends AbstractGLLookupableHelperServiceImpl {

    private EncumbranceCalculator postEncumbrance;
    private EncumbranceService encumbranceService;

    /**
     * Returns the url for any drill down links within the lookup
     * @param bo the business object with a property being drilled down on
     * @param propertyName the name of the property being drilled down on
     * @return a String with the URL of the property
     * @see org.kuali.core.lookup.Lookupable#getInquiryUrl(org.kuali.core.bo.BusinessObject, java.lang.String)
     */
    @Override
    public String getInquiryUrl(BusinessObject businessObject, String propertyName) {
        return (new EncumbranceInquirableImpl()).getInquiryUrl(businessObject, propertyName);
    }

    /**
     * Generates the list of search results for this inquiry
     * @param fieldValues the field values of the query to carry out
     * @return List the search results returned by the lookup
     * @see org.kuali.core.lookup.Lookupable#getSearchResults(java.util.Map)
     */
    @Override
    public List getSearchResults(Map fieldValues) {
        setBackLocation((String) fieldValues.get(KFSConstants.BACK_LOCATION));
        setDocFormKey((String) fieldValues.get(KFSConstants.DOC_FORM_KEY));

        // get the pending entry option. This method must be prior to the get search results
        String pendingEntryOption = this.getSelectedPendingEntryOption(fieldValues);

        // get the search result collection
        Iterator encumbranceIterator = encumbranceService.findOpenEncumbrance(fieldValues);
        Collection searchResultsCollection = this.buildEncumbranceCollection(encumbranceIterator);

        // update search results according to the selected pending entry option
        updateByPendingLedgerEntry(searchResultsCollection, fieldValues, pendingEntryOption, false, false);

        // get the actual size of all qualified search results
        Integer recordCount = encumbranceService.getOpenEncumbranceRecordCount(fieldValues);
        Long actualSize = OJBUtility.getResultActualSize(searchResultsCollection, recordCount, fieldValues, new Encumbrance());

        return this.buildSearchResultList(searchResultsCollection, actualSize);
    }

    /**
     * Updates pending entries before their results are included in the lookup results
     * 
     * @param entryCollection a collection of balance entries
     * @param fieldValues the map containing the search fields and values
     * @param isApproved flag whether the approved entries or all entries will be processed
     * @param isConsolidated flag whether the results are consolidated or not
     * @param isCostShareExcluded flag whether the user selects to see the results with cost share subaccount
     * @see org.kuali.module.gl.web.lookupable.AbstractGLLookupableImpl#updateEntryCollection(java.util.Collection, java.util.Map,
     *      boolean, boolean, boolean)
     */
    @Override
    protected void updateEntryCollection(Collection entryCollection, Map fieldValues, boolean isApproved, boolean isConsolidated, boolean isCostShareInclusive) {

        // convert the field names of balance object into corresponding ones of pending entry object
        Map pendingEntryFieldValues = BusinessObjectFieldConverter.convertToTransactionFieldValues(fieldValues);

        // go through the pending entries to update the encumbrance collection
        Iterator pendingEntryIterator = getGeneralLedgerPendingEntryService().findPendingLedgerEntriesForEncumbrance(pendingEntryFieldValues, isApproved);
        while (pendingEntryIterator.hasNext()) {
            GeneralLedgerPendingEntry pendingEntry = (GeneralLedgerPendingEntry) pendingEntryIterator.next();
            Encumbrance encumbrance = postEncumbrance.findEncumbrance(entryCollection, pendingEntry);
            postEncumbrance.updateEncumbrance(pendingEntry, encumbrance);
        }
    }

    /**
     * go through the given iterator to get encumbrances and put them into a collection
     * @param iterator an iterator of encumbrances
     * @return a collection of those encumbrances
     */
    private Collection buildEncumbranceCollection(Iterator iterator) {
        Collection encumbranceCollection = new ArrayList();

        while (iterator.hasNext()) {
            Encumbrance encumrbance = (Encumbrance) iterator.next();
            encumbranceCollection.add(encumrbance);
        }
        return encumbranceCollection;
    }

    /**
     * Sets the postEncumbrance attribute value.
     * 
     * @param postEncumbrance The postEncumbrance to set.
     */
    public void setPostEncumbrance(EncumbranceCalculator postEncumbrance) {
        this.postEncumbrance = postEncumbrance;
    }

    /**
     * Sets the encumbranceService attribute value.
     * 
     * @param encumbranceService The encumbranceService to set.
     */
    public void setEncumbranceService(EncumbranceService encumbranceService) {
        this.encumbranceService = encumbranceService;
    }
}