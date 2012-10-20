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

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang.StringUtils;
import org.kuali.kfs.gl.Constant;
import org.kuali.kfs.gl.OJBUtility;
import org.kuali.kfs.gl.batch.service.EncumbranceCalculator;
import org.kuali.kfs.gl.businessobject.Encumbrance;
import org.kuali.kfs.gl.businessobject.inquiry.EncumbranceInquirableImpl;
import org.kuali.kfs.gl.service.EncumbranceService;
import org.kuali.kfs.sys.KFSConstants;
import org.kuali.kfs.sys.KFSKeyConstants;
import org.kuali.kfs.sys.KFSPropertyConstants;
import org.kuali.kfs.sys.businessobject.GeneralLedgerPendingEntry;
import org.kuali.rice.kns.lookup.HtmlData;
import org.kuali.rice.krad.bo.BusinessObject;
import org.kuali.rice.krad.exception.ValidationException;
import org.kuali.rice.krad.util.GlobalVariables;

/**
 * An extension of KualiLookupableImpl to support encumbrance lookups
 */
public class EncumbranceLookupableHelperServiceImpl extends AbstractGeneralLedgerLookupableHelperServiceImpl {

    private EncumbranceCalculator postEncumbrance;
    private EncumbranceService encumbranceService;

    /**
     * Returns the url for any drill down links within the lookup
     * @param bo the business object with a property being drilled down on
     * @param propertyName the name of the property being drilled down on
     * @return a String with the URL of the property
     * @see org.kuali.rice.kns.lookup.Lookupable#getInquiryUrl(org.kuali.rice.krad.bo.BusinessObject, java.lang.String)
     */
    @Override
    public HtmlData getInquiryUrl(BusinessObject businessObject, String propertyName) {
        return (new EncumbranceInquirableImpl()).getInquiryUrl(businessObject, propertyName);
    }
    
    /**
     * Validates the fiscal year searched for in the inquiry
     * @param fieldValues the values of the query
     * @see org.kuali.rice.kns.lookup.AbstractLookupableHelperServiceImpl#validateSearchParameters(java.util.Map)
     */
    @Override
    public void validateSearchParameters(Map fieldValues) {
        super.validateSearchParameters(fieldValues);

        String valueFiscalYear = (String) fieldValues.get(KFSPropertyConstants.UNIVERSITY_FISCAL_YEAR);
        if (!StringUtils.isEmpty(valueFiscalYear)) {
            try {
                int year = Integer.parseInt(valueFiscalYear);
            }
            catch (NumberFormatException e) {
                GlobalVariables.getMessageMap().putError(KFSPropertyConstants.UNIVERSITY_FISCAL_YEAR, KFSKeyConstants.ERROR_CUSTOM, new String[] { KFSKeyConstants.PendingEntryLookupableImpl.FISCAL_YEAR_FOUR_DIGIT });
                throw new ValidationException("errors in search criteria");
            }
        }
        
        if (!allRequiredsForAccountSearch(fieldValues) && !allRequiredsForDocumentSearch(fieldValues)) {
            GlobalVariables.getMessageMap().putError("universityFiscalYear", KFSKeyConstants.ERROR_GL_LOOKUP_ENCUMBRANCE_NON_MATCHING_REQUIRED_FIELDS, new String[] {});
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
        return !StringUtils.isBlank(fiscalYearAsString) && !StringUtils.isBlank(chartOfAccountsCode) && !StringUtils.isBlank(accountNumber);
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
        
        final String zeroEncumbranceOption = getSelectedZeroEncumbranceOption(fieldValues); // store in a temporary variable, because the method removes the key from the map
        final boolean includeZeroEncumbrances = (StringUtils.isBlank(zeroEncumbranceOption) || zeroEncumbranceOption.equals(Constant.ZERO_ENCUMBRANCE_INCLUDE));

        // get the search result collection
        Iterator encumbranceIterator = encumbranceService.findOpenEncumbrance(fieldValues, includeZeroEncumbrances);
        Collection searchResultsCollection = this.buildEncumbranceCollection(encumbranceIterator);

        // update search results according to the selected pending entry option
        updateByPendingLedgerEntry(searchResultsCollection, fieldValues, pendingEntryOption, false, false);

        // get the actual size of all qualified search results
        Integer recordCount = encumbranceService.getOpenEncumbranceRecordCount(fieldValues, includeZeroEncumbrances);
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
     * Method tests to see if the user selected to include or exclude zero encumbrances
     * @param fieldValues the lookup field values
     * @return the value of the zero encumbrance option
     */
    protected String getSelectedZeroEncumbranceOption(Map fieldValues) {
        return (String)fieldValues.remove(Constant.ZERO_ENCUMBRANCE_OPTION);
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
