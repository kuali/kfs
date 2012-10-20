/*
 * Copyright 2006 The Kuali Foundation
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
package org.kuali.kfs.gl.businessobject.lookup;

import java.sql.Date;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang.StringUtils;
import org.kuali.kfs.gl.Constant;
import org.kuali.kfs.gl.businessobject.Entry;
import org.kuali.kfs.gl.businessobject.inquiry.EntryInquirableImpl;
import org.kuali.kfs.gl.businessobject.inquiry.InquirableFinancialDocument;
import org.kuali.kfs.gl.service.EntryService;
import org.kuali.kfs.gl.service.ScrubberValidator;
import org.kuali.kfs.integration.ld.LaborModuleService;
import org.kuali.kfs.sys.KFSConstants;
import org.kuali.kfs.sys.KFSKeyConstants;
import org.kuali.kfs.sys.KFSPropertyConstants;
import org.kuali.kfs.sys.businessobject.GeneralLedgerPendingEntry;
import org.kuali.kfs.sys.businessobject.UniversityDate;
import org.kuali.kfs.sys.context.SpringContext;
import org.kuali.kfs.sys.service.UniversityDateService;
import org.kuali.rice.core.api.datetime.DateTimeService;
import org.kuali.rice.kns.lookup.HtmlData;
import org.kuali.rice.kns.lookup.HtmlData.AnchorHtmlData;
import org.kuali.rice.krad.bo.BusinessObject;
import org.kuali.rice.krad.exception.ValidationException;
import org.kuali.rice.krad.util.GlobalVariables;
import org.kuali.rice.krad.util.KRADConstants;

/**
 * An extension of KualiLookupableImpl to support entry lookups
 */
public class EntryLookupableHelperServiceImpl extends AbstractGeneralLedgerLookupableHelperServiceImpl {
    private static final org.apache.log4j.Logger LOG = org.apache.log4j.Logger.getLogger(EntryLookupableHelperServiceImpl.class);

    private ScrubberValidator scrubberValidator;
    private EntryService entryService;
    private volatile static LaborModuleService laborModuleService;

    /**
     * Validate the university fiscal year that has been queried on
     * 
     * @param fieldValues the queried fields
     * @see org.kuali.rice.kns.lookup.AbstractLookupableHelperServiceImpl#validateSearchParameters(java.util.Map)
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
                GlobalVariables.getMessageMap().putError("universityFiscalYear", KFSKeyConstants.ERROR_CUSTOM, new String[] { "Fiscal Year must be a four-digit number" });
                throw new ValidationException("errors in search criteria");
            }
        }
        
        if (!allRequiredsForAccountSearch(fieldValues) && !allRequiredsForDocumentSearch(fieldValues)) {
            GlobalVariables.getMessageMap().putError("universityFiscalYear", KFSKeyConstants.ERROR_GL_LOOKUP_ENTRY_NON_MATCHING_REQUIRED_FIELDS, new String[] {});
            throw new ValidationException("errors in search criteria");
        }
    }

    /**
     * Determines if all the required values for an account based search are present - fiscal year, chart, account number, and fiscal period code
     * @param fieldValues field values to check
     * @return true if all the account-based required search fields are present; false otherwise
     */
    protected boolean allRequiredsForAccountSearch(Map fieldValues) {
        final String fiscalYearAsString = (String)fieldValues.get("universityFiscalYear");
        final String chartOfAccountsCode = (String)fieldValues.get("chartOfAccountsCode");
        final String accountNumber = (String)fieldValues.get("accountNumber");
        final String fiscalPeriodCode = (String)fieldValues.get("universityFiscalPeriodCode");
        return !StringUtils.isBlank(fiscalYearAsString) && !StringUtils.isBlank(chartOfAccountsCode) && !StringUtils.isBlank(accountNumber) && !StringUtils.isBlank(fiscalPeriodCode);
    }
    
    /**
     * Determines if all the required values for an document based search are present - fiscal year and document number
     * @param fieldValues field values to check
     * @return true if all the document-based required search fields are present; false otherwise
     */
    protected boolean allRequiredsForDocumentSearch(Map fieldValues) {
        final String fiscalYearAsString = (String)fieldValues.get("universityFiscalYear");
        final String documentNumber = (String)fieldValues.get("documentNumber");
        return !StringUtils.isBlank(fiscalYearAsString) && !StringUtils.isBlank(documentNumber);
    }

    /**
     * Returns the url for any drill down links within the lookup
     * @param bo the business object with a property being drilled down on
     * @param propertyName the name of the property being drilled down on
     * @return a String with the URL of the property
     * @see org.kuali.rice.kns.lookup.Lookupable#getInquiryUrl(org.kuali.rice.kns.bo.BusinessObject, java.lang.String)
     */
    @Override
    public HtmlData getInquiryUrl(BusinessObject businessObject, String propertyName) {
        if (KFSPropertyConstants.DOCUMENT_NUMBER.equals(propertyName)) {
            if (businessObject instanceof Entry) {
                Entry entry = (Entry) businessObject;
                if (getLaborModuleService().getLaborLedgerGLOriginCodes() != null && !getLaborModuleService().getLaborLedgerGLOriginCodes().isEmpty() && getLaborModuleService().getLaborLedgerGLOriginCodes().contains(entry.getFinancialSystemOriginationCode())) {
                    return getLaborModuleService().getInquiryUrlForGeneralLedgerEntryDocumentNumber(entry);
                }
                return new AnchorHtmlData(new InquirableFinancialDocument().getInquirableDocumentUrl(entry), KRADConstants.EMPTY_STRING, "view entry "+entry.toString());
            }
        }
        return (new EntryInquirableImpl()).getInquiryUrl(businessObject, propertyName);
    }

    /**
     * Generates the list of search results for this inquiry
     * @param fieldValues the field values of the query to carry out
     * @return List the search results returned by the lookup
     * @see org.kuali.rice.kns.lookup.Lookupable#getSearchResults(java.util.Map)
     */
    @Override
    public List<? extends BusinessObject> getSearchResults(Map<String, String> fieldValues) {
        setBackLocation((String) fieldValues.get(KFSConstants.BACK_LOCATION));
        setDocFormKey((String) fieldValues.get(KFSConstants.DOC_FORM_KEY));

        // get the pending entry option. This method must be prior to the get search results
        String pendingEntryOption = this.getSelectedPendingEntryOption(fieldValues);

        String debitCreditOption  = this.getDebitCreditOption(fieldValues);

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
            
            Entry entry = new Entry(pendingEntry, postDate);
            
            String approvedCode = pendingEntry.getFinancialDocumentApprovedCode();
            String description = Constant.DocumentApprovedCode.getDescription(approvedCode);
            entry.getDummyBusinessObject().setPendingEntryOption(description);
            
            entryCollection.add(entry);
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
    
    /**
     * @return the system's configured implementation of the LaborModuleService
     */
    public LaborModuleService getLaborModuleService() {
        if (laborModuleService == null) {
            laborModuleService = SpringContext.getBean(LaborModuleService.class);
        }
        return laborModuleService;
    }
}
