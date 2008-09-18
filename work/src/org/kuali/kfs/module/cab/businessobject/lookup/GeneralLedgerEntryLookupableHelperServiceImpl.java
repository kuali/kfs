/*
 * Copyright 2008 The Kuali Foundation.
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
package org.kuali.kfs.module.cab.businessobject.lookup;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang.StringUtils;
import org.kuali.kfs.module.cab.CabConstants;
import org.kuali.kfs.module.cab.businessobject.GeneralLedgerEntry;
import org.kuali.rice.kns.bo.BusinessObject;
import org.kuali.rice.kns.lookup.CollectionIncomplete;
import org.kuali.rice.kns.lookup.HtmlData;
import org.kuali.rice.kns.lookup.KualiLookupableHelperServiceImpl;
import org.kuali.rice.kns.lookup.LookupUtils;
import org.kuali.rice.kns.lookup.HtmlData.AnchorHtmlData;
import org.kuali.rice.kns.util.KNSConstants;

/**
 * This class overrides the base getActionUrls method
 */
public class GeneralLedgerEntryLookupableHelperServiceImpl extends KualiLookupableHelperServiceImpl {
    private static org.apache.log4j.Logger LOG = org.apache.log4j.Logger.getLogger(GeneralLedgerEntryLookupableHelperServiceImpl.class);

    /*******************************************************************************************************************************
     * @see org.kuali.rice.kns.lookup.AbstractLookupableHelperServiceImpl#getCustomActionUrls(org.kuali.rice.kns.bo.BusinessObject,
     *      java.util.List)
     */
    @Override
    public List<HtmlData> getCustomActionUrls(BusinessObject bo, List pkNames) {
        GeneralLedgerEntry entry = (GeneralLedgerEntry) bo;
        AnchorHtmlData createAssetHref = new AnchorHtmlData("../cabGlLine.do?methodToCall=createAsset&generalLedgerAccountIdentifier=" + entry.getGeneralLedgerAccountIdentifier(), "createAsset", "Create Asset");
        AnchorHtmlData createPaymentHref = new AnchorHtmlData("../cabGlLine.do?methodToCall=createPayment&generalLedgerAccountIdentifier=" + entry.getGeneralLedgerAccountIdentifier(), "createPayment", "Create Payment");
        List<HtmlData> anchorHtmlDataList = new ArrayList<HtmlData>();
        anchorHtmlDataList.add(createAssetHref);
        anchorHtmlDataList.add(createPaymentHref);
        return anchorHtmlDataList;
    }

    /**
     * This method will remove all PO related transactions from display on GL results
     * 
     * @see org.kuali.rice.kns.lookup.KualiLookupableHelperServiceImpl#getSearchResults(java.util.Map)
     */
    @Override
    public List<? extends BusinessObject> getSearchResults(Map<String, String> fieldValues) {
        List<? extends BusinessObject> searchResults = super.getSearchResults(fieldValues);
        if (searchResults == null || searchResults.isEmpty()) {
            return searchResults;
        }
        Integer searchResultsLimit = LookupUtils.getSearchResultsLimit(GeneralLedgerEntry.class);
        Long matchingResultsCount = null;
        List<GeneralLedgerEntry> newList = new ArrayList<GeneralLedgerEntry>();
        for (BusinessObject businessObject : searchResults) {
            GeneralLedgerEntry entry = (GeneralLedgerEntry) businessObject;
            if (!entry.getFinancialDocumentTypeCode().equals(CabConstants.PREQ)) {
                if (!entry.getFinancialDocumentTypeCode().equals(CabConstants.CM)) {
                    newList.add(entry);
                }
                else if (entry.getFinancialDocumentTypeCode().equals(CabConstants.CM) && StringUtils.isBlank(entry.getReferenceFinancialDocumentNumber())) {
                    newList.add(entry);
                }
            }
        }
        matchingResultsCount = Long.valueOf(newList.size());
        if (matchingResultsCount.intValue() <= searchResultsLimit.intValue()) {
            matchingResultsCount = new Long(0);
        }
        return new CollectionIncomplete(newList, matchingResultsCount);
    }

}
