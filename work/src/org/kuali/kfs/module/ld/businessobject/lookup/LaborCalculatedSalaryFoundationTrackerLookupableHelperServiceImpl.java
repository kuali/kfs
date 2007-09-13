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
import java.util.Collections;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang.StringUtils;
import org.kuali.core.bo.BusinessObject;
import org.kuali.core.lookup.AbstractLookupableHelperServiceImpl;
import org.kuali.core.lookup.CollectionIncomplete;
import org.kuali.core.service.KualiConfigurationService;
import org.kuali.core.util.BeanPropertyComparator;
import org.kuali.core.util.GlobalVariables;
import org.kuali.kfs.KFSConstants;
import org.kuali.kfs.KFSPropertyConstants;
import org.kuali.module.gl.service.BalanceService;
import org.kuali.module.labor.LaborConstants;
import org.kuali.module.labor.service.LaborCalculatedSalaryFoundationTrackerService;
import org.kuali.module.labor.service.impl.LaborBaseFundsServiceImpl;
import org.kuali.module.labor.web.inquirable.LaborCalculatedSalaryFoundationTrackerInquirableImpl;
import org.springframework.transaction.annotation.Transactional;

/**
 * The CSFTrackerBalanceLookupableHelperServiceImpl class is the front-end for all Calculated Salary Foundation balance inquiry
 * processing.
 */

@Transactional
public class LaborCalculatedSalaryFoundationTrackerLookupableHelperServiceImpl extends AbstractLookupableHelperServiceImpl {
    private static org.apache.log4j.Logger LOG = org.apache.log4j.Logger.getLogger(LaborCalculatedSalaryFoundationTrackerLookupableHelperServiceImpl.class);
    
    private LaborCalculatedSalaryFoundationTrackerService laborCalculatedSalaryFoundationTrackerService;

    /**
     * @see org.kuali.core.lookup.AbstractLookupableHelperServiceImpl#getInquiryUrl(org.kuali.core.bo.BusinessObject,
     *      java.lang.String)
     */
    @Override
    public String getInquiryUrl(BusinessObject bo, String propertyName) {
        return (new LaborCalculatedSalaryFoundationTrackerInquirableImpl()).getInquiryUrl(bo, propertyName);
    }
    
    /**
     * @see org.kuali.core.lookup.AbstractLookupableHelperServiceImpl#getSearchResults(java.util.Map)
     */
    @Override
    public List getSearchResults(Map fieldValues) {
        setBackLocation((String) fieldValues.get(KFSConstants.BACK_LOCATION));
        setDocFormKey((String) fieldValues.get(KFSConstants.DOC_FORM_KEY));

        Collection searchResultsCollection = laborCalculatedSalaryFoundationTrackerService.findCSFTrackerWithJuly1(fieldValues, false);

        return new CollectionIncomplete(searchResultsCollection, new Long(searchResultsCollection.size()));
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
     * Sets the laborCalculatedSalaryFoundationTrackerService attribute value.
     * @param laborCalculatedSalaryFoundationTrackerService The laborCalculatedSalaryFoundationTrackerService to set.
     */
    public void setLaborCalculatedSalaryFoundationTrackerService(LaborCalculatedSalaryFoundationTrackerService laborCalculatedSalaryFoundationTrackerService) {
        this.laborCalculatedSalaryFoundationTrackerService = laborCalculatedSalaryFoundationTrackerService;
    }
}