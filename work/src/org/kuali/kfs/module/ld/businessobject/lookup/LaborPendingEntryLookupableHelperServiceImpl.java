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
package org.kuali.module.labor.web.lookupable;

import java.util.Collection;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang.StringUtils;
import org.kuali.core.bo.BusinessObject;
import org.kuali.core.lookup.AbstractLookupableHelperServiceImpl;
import org.kuali.core.lookup.CollectionIncomplete;
import org.kuali.kfs.KFSConstants;
import org.kuali.kfs.KFSPropertyConstants;
import org.kuali.kfs.context.SpringContext;
import org.kuali.module.financial.service.UniversityDateService;
import org.kuali.module.gl.bo.UniversityDate;
import org.kuali.module.gl.web.inquirable.InquirableFinancialDocument;
import org.kuali.module.labor.bo.LaborLedgerPendingEntry;
import org.kuali.module.labor.service.LaborLedgerPendingEntryService;
import org.kuali.module.labor.web.inquirable.LedgerPendingEntryInquirableImpl;

/**
 * Helper Service for looking up instances of <code>{@link LaborLedgerPendingEntry}</code>
 */
public class LaborPendingEntryLookupableHelperServiceImpl extends AbstractLookupableHelperServiceImpl {
    private static org.apache.commons.logging.Log LOG = org.apache.commons.logging.LogFactory.getLog(LaborPendingEntryLookupableHelperServiceImpl.class);

    private LaborLedgerPendingEntryService laborLedgerPendingEntryService;

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
        return (new LedgerPendingEntryInquirableImpl()).getInquiryUrl(businessObject, propertyName);
    }

    /**
     * @see org.kuali.core.lookup.AbstractLookupableHelperServiceImpl#getSearchResults(java.util.Map)
     */
    @Override
    public List getSearchResults(Map fieldValues) {
        setBackLocation((String) fieldValues.get(KFSConstants.BACK_LOCATION));
        setDocFormKey((String) fieldValues.get(KFSConstants.DOC_FORM_KEY));

        UniversityDate currentUniversityDate = SpringContext.getBean(UniversityDateService.class).getCurrentUniversityDate();
        String currentFiscalPeriodCode = currentUniversityDate.getUniversityFiscalAccountingPeriod();
        Integer currentFiscalYear = currentUniversityDate.getUniversityFiscalYear();

        String fiscalPeriodFromForm = null;
        if (fieldValues.containsKey(KFSPropertyConstants.UNIVERSITY_FISCAL_PERIOD_CODE)) {
            fiscalPeriodFromForm = ((String) fieldValues.get(KFSPropertyConstants.UNIVERSITY_FISCAL_PERIOD_CODE)).trim();
        }

        String fiscalYearFromForm = null;
        if (fieldValues.containsKey(KFSPropertyConstants.UNIVERSITY_FISCAL_YEAR)) {
            fiscalYearFromForm = ((String) fieldValues.get(KFSPropertyConstants.UNIVERSITY_FISCAL_YEAR)).trim();
        }

        // Set null fy and ap to current values.
        Collection<LaborLedgerPendingEntry> searchResults = laborLedgerPendingEntryService.findPendingEntries(fieldValues, false);
        for (LaborLedgerPendingEntry pendingEntry : searchResults) {
            
            if (currentFiscalPeriodCode.equals(fiscalPeriodFromForm) && StringUtils.isEmpty(pendingEntry.getUniversityFiscalPeriodCode())) {
                pendingEntry.setUniversityFiscalPeriodCode(currentFiscalPeriodCode);
            }

            if (currentFiscalYear.toString().equals(fiscalYearFromForm) && pendingEntry.getUniversityFiscalYear() == null) {
                pendingEntry.setUniversityFiscalYear(currentFiscalYear);
            }
        }

        return new CollectionIncomplete(searchResults, new Long(searchResults.size()));
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
