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

import java.sql.Date;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang.StringUtils;
import org.kuali.core.bo.BusinessObject;
import org.kuali.core.exceptions.ValidationException;
import org.kuali.core.service.DateTimeService;
import org.kuali.core.util.GlobalVariables;
import org.kuali.kfs.KFSConstants;
import org.kuali.kfs.KFSKeyConstants;
import org.kuali.kfs.KFSPropertyConstants;
import org.kuali.kfs.bo.GeneralLedgerPendingEntry;
import org.kuali.kfs.context.SpringContext;
import org.kuali.module.financial.service.UniversityDateService;
import org.kuali.module.gl.bo.Entry;
import org.kuali.module.gl.bo.UniversityDate;
import org.kuali.module.gl.service.EntryService;
import org.kuali.module.gl.service.ScrubberValidator;
import org.kuali.module.gl.util.BusinessObjectFieldConverter;
import org.kuali.module.gl.web.Constant;
import org.kuali.module.gl.web.inquirable.EntryInquirableImpl;
import org.kuali.module.gl.web.inquirable.InquirableFinancialDocument;
import org.springframework.transaction.annotation.Transactional;

/**
 * An extension of KualiLookupableImpl to support entry lookups
 */
@Transactional
public class EntryLookupableHelperServiceImpl extends AbstractGLLookupableHelperServiceImpl {
    private static final org.apache.log4j.Logger LOG = org.apache.log4j.Logger.getLogger(EntryLookupableHelperServiceImpl.class);

    private ScrubberValidator scrubberValidator;
    private EntryService entryService;

    /**
     * Validate the university fiscal year that has been queried on
     * 
     * @param fieldValues the queried fields
     * @see org.kuali.core.lookup.AbstractLookupableHelperServiceImpl#validateSearchParameters(java.util.Map)
     */
    @Override
    public void validateSearchParameters(Map fieldValues) {
        super.validateSearchParameters(fieldValues);

        String valueFiscalYear = (String) fieldValues.get("universityFiscalYear");
        if (!StringUtils.isEmpty(valueFiscalYear)) {
            try {
                int year = Integer.parseInt(valueFiscalYear);
            }
            catch (NumberFormatException e) {
                GlobalVariables.getErrorMap().putError("universityFiscalYear", KFSKeyConstants.ERROR_CUSTOM, new String[] { "Fiscal Year must be a four-digit number" });
                throw new ValidationException("errors in search criteria");
            }
        }
    }

    /**
     * Returns the url for any drill down links within the lookup
     * @param bo the business object with a property being drilled down on
     * @param propertyName the name of the property being drilled down on
     * @return a String with the URL of the property
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
        Collection searchResultsCollection = getLookupService().findCollectionBySearch(getBusinessObjectClass(), fieldValues);

        // update search results according to the selected pending entry option
        updateByPendingLedgerEntry(searchResultsCollection, fieldValues, pendingEntryOption, false, false);

        // get the actual size of all qualified search results
        Long actualSize = new Long(entryService.getEntryRecordCount(fieldValues));

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
        LOG.debug("updateEntryCollection started");

        // convert the field names of balance object into corresponding ones of pending entry object
        Map pendingEntryFieldValues = BusinessObjectFieldConverter.convertToTransactionFieldValues(fieldValues);

        // go through the pending entries to update the balance collection
        Iterator pendingEntryIterator = getGeneralLedgerPendingEntryService().findPendingLedgerEntriesForEntry(pendingEntryFieldValues, isApproved);

        String pendingOption = isApproved ? Constant.APPROVED_PENDING_ENTRY : Constant.ALL_PENDING_ENTRY;
        UniversityDate today = SpringContext.getBean(UniversityDateService.class).getCurrentUniversityDate();
        String currentFiscalPeriodCode = today.getUniversityFiscalAccountingPeriod();
        Integer currentFiscalYear = today.getUniversityFiscalYear();
        Date postDate = SpringContext.getBean(DateTimeService.class).getCurrentSqlDate();

        while (pendingEntryIterator.hasNext()) {
            GeneralLedgerPendingEntry pendingEntry = (GeneralLedgerPendingEntry) pendingEntryIterator.next();

            // Gotta circumvent date checks in the scrubberValidator. They totally kill performance.
            if (pendingEntry.getUniversityFiscalYear() == null) {
                pendingEntry.setUniversityFiscalYear(currentFiscalYear);
            }

            if (pendingEntry.getUniversityFiscalPeriodCode() == null) {
                pendingEntry.setUniversityFiscalPeriodCode(currentFiscalPeriodCode);
            }

            scrubberValidator.validateForInquiry(pendingEntry);
            entryCollection.add(new Entry(pendingEntry, postDate));
        }
    }

    /**
     * Sets the scrubberValidator attribute value.
     * 
     * @param scrubberValidator The scrubberValidator to set.
     */
    public void setScrubberValidator(ScrubberValidator scrubberValidator) {
        this.scrubberValidator = scrubberValidator;
    }

    /**
     * Sets the entryService attribute value.
     * 
     * @param entryService The entryService to set.
     */
    public void setEntryService(EntryService entryService) {
        this.entryService = entryService;
    }
}