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
package org.kuali.kfs.gl.businessobject.lookup;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang.StringUtils;
import org.kuali.kfs.gl.Constant;
import org.kuali.kfs.gl.businessobject.inquiry.EntryInquirableImpl;
import org.kuali.kfs.gl.businessobject.inquiry.InquirableFinancialDocument;
import org.kuali.kfs.sys.KFSConstants;
import org.kuali.kfs.sys.KFSKeyConstants;
import org.kuali.kfs.sys.KFSPropertyConstants;
import org.kuali.kfs.sys.businessobject.GeneralLedgerPendingEntry;
import org.kuali.kfs.sys.businessobject.UniversityDate;
import org.kuali.kfs.sys.context.SpringContext;
import org.kuali.kfs.sys.service.GeneralLedgerPendingEntryService;
import org.kuali.kfs.sys.service.UniversityDateService;
import org.kuali.kfs.sys.service.impl.KfsParameterConstants;
import org.kuali.rice.kns.bo.BusinessObject;
import org.kuali.rice.kns.exception.ValidationException;
import org.kuali.rice.kns.lookup.AbstractLookupableHelperServiceImpl;
import org.kuali.rice.kns.lookup.CollectionIncomplete;
import org.kuali.rice.kns.lookup.HtmlData;
import org.kuali.rice.kns.lookup.HtmlData.AnchorHtmlData;
import org.kuali.rice.kns.service.ParameterService;
import org.kuali.rice.kns.util.BeanPropertyComparator;
import org.kuali.rice.kns.util.GlobalVariables;
import org.kuali.rice.kns.util.KNSConstants;

/**
 * An extension of KualiLookupableImpl to support balance lookups
 */
public class PendingEntryLookupableHelperServiceImpl extends AbstractLookupableHelperServiceImpl {
    private static org.apache.log4j.Logger LOG = org.apache.log4j.Logger.getLogger(PendingEntryLookupableHelperServiceImpl.class);

    private GeneralLedgerPendingEntryService generalLedgerPendingEntryService;
    private ParameterService parameterService;

    private final static String UNIVERSITY_FISCAL_YEAR = "universityFiscalYear";
    private final static String UNIVERSITY_FISCAL_PERIOD_CODE = "universityFiscalPeriodCode";

    /**
     * Returns the url for any drill down links within the lookup (defers to its superclass unless it needs
     * to get the url of the document that created this result pending entry)
     * @param bo the business object with a property being drilled down on
     * @param propertyName the name of the property being drilled down on
     * @return a String with the URL of the property
     * @see org.kuali.rice.kns.lookup.Lookupable#getInquiryUrl(org.kuali.rice.kns.bo.BusinessObject, java.lang.String)
     */
    @Override
    public HtmlData getInquiryUrl(BusinessObject businessObject, String propertyName) {
        if (KFSPropertyConstants.DOCUMENT_NUMBER.equals(propertyName) && businessObject instanceof GeneralLedgerPendingEntry) {
            GeneralLedgerPendingEntry pendingEntry = (GeneralLedgerPendingEntry) businessObject;
            return new AnchorHtmlData(new InquirableFinancialDocument().getInquirableDocumentUrl(pendingEntry), KNSConstants.EMPTY_STRING);
        }
        return (new EntryInquirableImpl()).getInquiryUrl(businessObject, propertyName);
        //return super.getInquiryUrl(businessObject, propertyName);
    }


    /**
     * Validates the fiscal year searched for in the inquiry
     * @param fieldValues the values of the query
     * @see org.kuali.rice.kns.lookup.AbstractLookupableHelperServiceImpl#validateSearchParameters(java.util.Map)
     */
    @Override
    public void validateSearchParameters(Map fieldValues) {
        super.validateSearchParameters(fieldValues);

        String valueFiscalYear = (String) fieldValues.get(UNIVERSITY_FISCAL_YEAR);
        if (!StringUtils.isEmpty(valueFiscalYear)) {
            try {
                int year = Integer.parseInt(valueFiscalYear);
            }
            catch (NumberFormatException e) {
                GlobalVariables.getErrorMap().putError(UNIVERSITY_FISCAL_YEAR, KFSKeyConstants.ERROR_CUSTOM, new String[] { KFSKeyConstants.PendingEntryLookupableImpl.FISCAL_YEAR_FOUR_DIGIT });
                throw new ValidationException("errors in search criteria");
            }
        }
    }


    /**
     * Generates the list of search results for this inquiry
     * @param fieldValues the field values of the query to carry out
     * @return List the search results returned by the lookup
     * @see org.kuali.rice.kns.lookup.Lookupable#getSearchResults(java.util.Map)
     */
    public List getSearchResults(Map fieldValues) {
        setBackLocation((String) fieldValues.get(KFSConstants.BACK_LOCATION));
        setDocFormKey((String) fieldValues.get(KFSConstants.DOC_FORM_KEY));

        boolean isApproved = fieldValues.containsKey(Constant.PENDING_ENTRY_OPTION) && Constant.APPROVED_PENDING_ENTRY.equals(fieldValues.get(Constant.PENDING_ENTRY_OPTION));
        Collection searchResultsCollection = generalLedgerPendingEntryService.findPendingEntries(fieldValues, isApproved);

        // sort list if default sort column given
        List searchResults = (List) searchResultsCollection;
        List defaultSortColumns = getDefaultSortColumns();
        if (defaultSortColumns.size() > 0) {
            Collections.sort(searchResults, new BeanPropertyComparator(defaultSortColumns, true));
        }

        // get the result limit number from configuration
        String limitConfig = parameterService.getParameterValue(KfsParameterConstants.NERVOUS_SYSTEM_LOOKUP.class, KFSConstants.LOOKUP_RESULTS_LIMIT_URL_KEY);
        Integer limit = null;
        if (limitConfig != null) {
            limit = Integer.valueOf(limitConfig);
        }

        Long collectionCount = new Long(searchResults.size());
        Long actualCountIfTruncated = new Long(0);

        // If more than limit number of records were returned, removed
        if (limit != null) {
            if (collectionCount >= limit.intValue()) {
                actualCountIfTruncated = collectionCount;
                for (int i = collectionCount.intValue() - 1; i >= limit; i--) {
                    searchResults.remove(i);
                }
            }
        }

        UniversityDate currentUniversityDate = SpringContext.getBean(UniversityDateService.class).getCurrentUniversityDate();
        String currentFiscalPeriodCode = currentUniversityDate.getUniversityFiscalAccountingPeriod();
        Integer currentFiscalYear = currentUniversityDate.getUniversityFiscalYear();

        String fiscalPeriodFromForm = null;
        if (fieldValues.containsKey(UNIVERSITY_FISCAL_PERIOD_CODE)) {
            fiscalPeriodFromForm = (String) fieldValues.get(UNIVERSITY_FISCAL_PERIOD_CODE);
        }

        String fiscalYearFromForm = null;
        if (fieldValues.containsKey(UNIVERSITY_FISCAL_YEAR)) {
            fiscalYearFromForm = (String) fieldValues.get(UNIVERSITY_FISCAL_YEAR);
        }
        // Set null fy and ap to current values.
        for (Iterator i = searchResults.iterator(); i.hasNext();) {
            GeneralLedgerPendingEntry glpe = (GeneralLedgerPendingEntry) i.next();

            if (currentFiscalPeriodCode.equals(fiscalPeriodFromForm) && null == glpe.getUniversityFiscalPeriodCode()) {
                glpe.setUniversityFiscalPeriodCode(currentFiscalPeriodCode);
            }

            if (currentFiscalYear.toString().equals(fiscalYearFromForm) && null == glpe.getUniversityFiscalYear()) {
                glpe.setUniversityFiscalYear(currentFiscalYear);
            }
        }

        return new CollectionIncomplete(searchResults, actualCountIfTruncated);
    }

    /**
     * This method builds the collection of search results without period codes and updates the results with current period code
     * 
     * @param iterator the iterator of search results
     * @param periodCode the current period code
     * @return the collection of search results with updated period codes
     */
    private Collection buildSearchResults(Iterator iterator, String periodCode) {
        Collection collection = new ArrayList();

        while (iterator.hasNext()) {
            GeneralLedgerPendingEntry pendingEntry = (GeneralLedgerPendingEntry) iterator.next();
            pendingEntry.setUniversityFiscalPeriodCode(periodCode);
            collection.add(pendingEntry);
        }

        return new CollectionIncomplete(collection, new Long(collection.size()));
    }

    /**
     * Sets the generalLedgerPendingEntryService attribute value.
     * 
     * @param generalLedgerPendingEntryService The generalLedgerPendingEntryService to set.
     */
    public void setGeneralLedgerPendingEntryService(GeneralLedgerPendingEntryService generalLedgerPendingEntryService) {
        this.generalLedgerPendingEntryService = generalLedgerPendingEntryService;
    }

    public void setParameterService(ParameterService parameterService) {
        this.parameterService = parameterService;
    }

}
