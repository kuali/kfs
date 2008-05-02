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

import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.kuali.core.bo.BusinessObject;
import org.kuali.core.lookup.AbstractLookupableHelperServiceImpl;
import org.kuali.core.lookup.CollectionIncomplete;
import org.kuali.core.util.BeanPropertyComparator;
import org.kuali.kfs.KFSConstants;
import org.kuali.kfs.KFSPropertyConstants;
import org.kuali.module.gl.web.Constant;
import org.kuali.module.gl.web.inquirable.InquirableFinancialDocument;
import org.kuali.module.labor.bo.LaborLedgerPendingEntry;
import org.kuali.module.labor.bo.LedgerBalance;
import org.kuali.module.labor.service.LaborInquiryOptionsService;
import org.kuali.module.labor.service.LaborLedgerPendingEntryService;
import org.kuali.module.labor.web.inquirable.AbstractLaborInquirableImpl;
import org.kuali.module.labor.web.inquirable.LedgerPendingEntryInquirableImpl;
import org.kuali.module.labor.web.inquirable.PositionDataDetailsInquirableImpl;

/**
 * Helper Service for looking up instances of <code>{@link LaborLedgerPendingEntry}</code>
 */
public class LaborPendingEntryLookupableHelperServiceImpl extends AbstractLookupableHelperServiceImpl {
    private static org.apache.commons.logging.Log LOG = org.apache.commons.logging.LogFactory.getLog(LaborPendingEntryLookupableHelperServiceImpl.class);

    private LaborLedgerPendingEntryService laborLedgerPendingEntryService;
    private LaborInquiryOptionsService laborInquiryOptionsService;

    /**
     * @see org.kuali.core.lookup.AbstractLookupableHelperServiceImpl#getInquiryUrl(org.kuali.core.bo.BusinessObject,
     *      java.lang.String)
     */
    @Override
    public String getInquiryUrl(BusinessObject businessObject, String propertyName) {
        if (KFSPropertyConstants.DOCUMENT_NUMBER.equals(propertyName) && businessObject instanceof LaborLedgerPendingEntry) {
            LaborLedgerPendingEntry pendingEntry = (LaborLedgerPendingEntry) businessObject;
            return new InquirableFinancialDocument().getInquirableDocumentUrl(pendingEntry);
        }
        else if (KFSPropertyConstants.POSITION_NUMBER.equals(propertyName)) {
            LaborLedgerPendingEntry pendingEntry = (LaborLedgerPendingEntry) businessObject;
            AbstractLaborInquirableImpl positionDataDetailsInquirable = new PositionDataDetailsInquirableImpl();

            Map<String, String> fieldValues = new HashMap<String, String>();
            fieldValues.put(propertyName, pendingEntry.getPositionNumber());

            BusinessObject positionData = positionDataDetailsInquirable.getBusinessObject(fieldValues);

            return positionData == null ? KFSConstants.EMPTY_STRING : positionDataDetailsInquirable.getInquiryUrl(positionData, propertyName);
        }
        return (new LedgerPendingEntryInquirableImpl()).getInquiryUrl(businessObject, propertyName);
    }

    /**
     * @see org.kuali.core.lookup.AbstractLookupableHelperServiceImpl#getSearchResults(java.util.Map)
     */
    @Override
    public List getSearchResults(Map fieldValues) {
        setBackLocation((String) fieldValues.get(KFSConstants.BACK_LOCATION));
        setDocFormKey((String) fieldValues.get(KFSConstants.DOC_FORM_KEY));

        // determine if only approved pending entries need to be returned
        String pendingEntryOption = laborInquiryOptionsService.getSelectedPendingEntryOption(fieldValues);
        boolean isApprovedPendingSelected = Constant.APPROVED_PENDING_ENTRY.equals(pendingEntryOption) ? true : false;

        Collection<LaborLedgerPendingEntry> searchResults = laborLedgerPendingEntryService.findPendingEntries(fieldValues, isApprovedPendingSelected);
        Long resultSize = searchResults == null ? 0 : new Long(searchResults.size());

        return this.buildSearchResultList(searchResults, resultSize);
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
     * Sets the laborLedgerPendingEntryService attribute value.
     * 
     * @param laborLedgerPendingEntryService The laborLedgerPendingEntryService to set.
     */
    public void setLaborLedgerPendingEntryService(LaborLedgerPendingEntryService laborLedgerPendingEntryService) {
        this.laborLedgerPendingEntryService = laborLedgerPendingEntryService;
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
