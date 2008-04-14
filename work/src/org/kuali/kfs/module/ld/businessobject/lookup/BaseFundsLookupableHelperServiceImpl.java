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
import java.util.List;
import java.util.Map;

import org.kuali.core.bo.BusinessObject;
import org.kuali.core.lookup.AbstractLookupableHelperServiceImpl;
import org.kuali.core.lookup.CollectionIncomplete;
import org.kuali.core.util.BeanPropertyComparator;
import org.kuali.core.web.ui.Row;
import org.kuali.kfs.KFSConstants;
import org.kuali.module.labor.service.LaborBaseFundsService;
import org.kuali.module.labor.service.LaborInquiryOptionsService;
import org.kuali.module.labor.web.inquirable.BaseFundsInquirableImpl;
import org.springframework.transaction.annotation.Transactional;

/**
 * The BaseFundsLookupableHelperServiceImpl class is the front-end for all Base Fund balance inquiry processing.
 */
public class BaseFundsLookupableHelperServiceImpl extends AbstractLookupableHelperServiceImpl {
    private static final org.apache.commons.logging.Log LOG = org.apache.commons.logging.LogFactory.getLog(BaseFundsLookupableHelperServiceImpl.class);

    private LaborBaseFundsService laborBaseFundsService;
    private LaborInquiryOptionsService laborInquiryOptionsService;

    /**
     * @see org.kuali.core.lookup.Lookupable#getInquiryUrl(org.kuali.core.bo.BusinessObject, java.lang.String)
     */
    @Override
    public String getInquiryUrl(BusinessObject bo, String propertyName) {
        return (new BaseFundsInquirableImpl()).getInquiryUrl(bo, propertyName);
    }

    /**
     * @see org.kuali.core.lookup.Lookupable#getSearchResults(java.util.Map)
     */
    @Override
    public List getSearchResults(Map fieldValues) {
        setBackLocation((String) fieldValues.get(KFSConstants.BACK_LOCATION));
        setDocFormKey((String) fieldValues.get(KFSConstants.DOC_FORM_KEY));

        // test if the consolidation option is selected or not
        boolean isConsolidated = laborInquiryOptionsService.isConsolidationSelected(fieldValues, (Collection<Row>) getRows());

        Collection searchResultsCollection = laborBaseFundsService.findAccountStatusBaseFundsWithCSFTracker(fieldValues, isConsolidated);

        // get the actual size of all qualified search results
        Long actualSize = new Long(searchResultsCollection.size());

        return this.buildSearchResultList(searchResultsCollection, actualSize);
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
     * Sets the laborInquiryOptionsService attribute value.
     * 
     * @param laborInquiryOptionsService The laborInquiryOptionsService to set.
     */
    public void setLaborInquiryOptionsService(LaborInquiryOptionsService laborInquiryOptionsService) {
        this.laborInquiryOptionsService = laborInquiryOptionsService;
    }

    /**
     * Sets the laborBaseFundsService attribute value.
     * 
     * @param laborBaseFundsService The laborBaseFundsService to set.
     */
    public void setLaborBaseFundsService(LaborBaseFundsService laborBaseFundsService) {
        this.laborBaseFundsService = laborBaseFundsService;
    }
}
