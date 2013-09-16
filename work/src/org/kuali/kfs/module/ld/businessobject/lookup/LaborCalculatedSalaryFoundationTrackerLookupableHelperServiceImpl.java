/*
 * Copyright 2007 The Kuali Foundation
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
package org.kuali.kfs.module.ld.businessobject.lookup;

import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.kuali.kfs.integration.ld.businessobject.inquiry.AbstractPositionDataDetailsInquirableImpl;
import org.kuali.kfs.module.ld.businessobject.LaborCalculatedSalaryFoundationTracker;
import org.kuali.kfs.module.ld.businessobject.inquiry.LaborCalculatedSalaryFoundationTrackerInquirableImpl;
import org.kuali.kfs.module.ld.businessobject.inquiry.PositionDataDetailsInquirableImpl;
import org.kuali.kfs.module.ld.service.LaborCalculatedSalaryFoundationTrackerService;
import org.kuali.kfs.sys.KFSConstants;
import org.kuali.kfs.sys.KFSPropertyConstants;
import org.kuali.rice.kns.lookup.AbstractLookupableHelperServiceImpl;
import org.kuali.rice.kns.lookup.HtmlData;
import org.kuali.rice.kns.lookup.HtmlData.AnchorHtmlData;
import org.kuali.rice.krad.bo.BusinessObject;
import org.kuali.rice.krad.lookup.CollectionIncomplete;
import org.kuali.rice.krad.util.BeanPropertyComparator;

/**
 * The CSFTrackerBalanceLookupableHelperServiceImpl class is the front-end for all Calculated Salary Foundation balance inquiry
 * processing.
 */

public class LaborCalculatedSalaryFoundationTrackerLookupableHelperServiceImpl extends AbstractLookupableHelperServiceImpl {
    private static org.apache.log4j.Logger LOG = org.apache.log4j.Logger.getLogger(LaborCalculatedSalaryFoundationTrackerLookupableHelperServiceImpl.class);

    private LaborCalculatedSalaryFoundationTrackerService laborCalculatedSalaryFoundationTrackerService;

    /**
     * @see org.kuali.rice.kns.lookup.AbstractLookupableHelperServiceImpl#getInquiryUrl(org.kuali.rice.krad.bo.BusinessObject,
     *      java.lang.String)
     */
    @Override
    public HtmlData getInquiryUrl(BusinessObject bo, String propertyName) {
        if (KFSPropertyConstants.POSITION_NUMBER.equals(propertyName)) {
            LaborCalculatedSalaryFoundationTracker CSFTracker = (LaborCalculatedSalaryFoundationTracker) bo;
            AbstractPositionDataDetailsInquirableImpl positionDataDetailsInquirable = new PositionDataDetailsInquirableImpl();

            Map<String, String> fieldValues = new HashMap<String, String>();
            fieldValues.put(propertyName, CSFTracker.getPositionNumber());

            BusinessObject positionData = positionDataDetailsInquirable.getBusinessObject(fieldValues);

            return positionData == null ? new AnchorHtmlData(KFSConstants.EMPTY_STRING, KFSConstants.EMPTY_STRING) : positionDataDetailsInquirable.getInquiryUrl(positionData, propertyName);
        }
        return (new LaborCalculatedSalaryFoundationTrackerInquirableImpl()).getInquiryUrl(bo, propertyName);
    }

    /**
     * @see org.kuali.rice.kns.lookup.AbstractLookupableHelperServiceImpl#getSearchResults(java.util.Map)
     */
    @Override
    public List<? extends BusinessObject> getSearchResults(Map<String, String> fieldValues) {
        setBackLocation(fieldValues.get(KFSConstants.BACK_LOCATION));
        setDocFormKey(fieldValues.get(KFSConstants.DOC_FORM_KEY));

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
        List searchResults = results;
        List defaultSortColumns = getDefaultSortColumns();
        if (defaultSortColumns.size() > 0) {
            Collections.sort(results, new BeanPropertyComparator(defaultSortColumns, true));
        }
        return searchResults;
    }

    /**
     * Sets the laborCalculatedSalaryFoundationTrackerService attribute value.
     *
     * @param laborCalculatedSalaryFoundationTrackerService The laborCalculatedSalaryFoundationTrackerService to set.
     */
    public void setLaborCalculatedSalaryFoundationTrackerService(LaborCalculatedSalaryFoundationTrackerService laborCalculatedSalaryFoundationTrackerService) {
        this.laborCalculatedSalaryFoundationTrackerService = laborCalculatedSalaryFoundationTrackerService;
    }
}
