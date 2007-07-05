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

import java.util.Collections;
import java.util.List;
import java.util.Map;

import org.kuali.Constants;
import org.kuali.core.bo.BusinessObject;
import org.kuali.core.bo.user.UniversalUser;
import org.kuali.core.lookup.AbstractLookupableHelperServiceImpl;
import org.kuali.core.lookup.LookupUtils;
import org.kuali.core.util.BeanPropertyComparator;
import org.kuali.kfs.KFSPropertyConstants;
import org.kuali.module.gl.bo.Entry;
import org.kuali.module.gl.web.inquirable.EntryInquirableImpl;
import org.kuali.module.gl.web.inquirable.InquirableFinancialDocument;
import org.kuali.module.labor.web.inquirable.PositionDataInquirableImpl;

/**
 * This class...
 */
public class PositionDataLookupableHelperServiceImpl extends AbstractLookupableHelperServiceImpl {
    
    /**
     * @see org.kuali.core.lookup.Lookupable#getInquiryUrl(org.kuali.core.bo.BusinessObject, java.lang.String)
     */
    @Override
    public String getInquiryUrl(BusinessObject businessObject, String propertyName) {
        if (KFSPropertyConstants.POSITION_IDENTIFIER_SEQUENCE.equals(propertyName)) {
            return (new PositionDataInquirableImpl()).getInquiryUrl(businessObject, propertyName);
        }
        return (new EntryInquirableImpl()).getInquiryUrl(businessObject, propertyName);
    }

    /**
     * @see org.kuali.core.lookup.AbstractLookupableHelperServiceImpl#getSearchResults(java.util.Map)
     */
    @Override
    public List<? extends BusinessObject> getSearchResults(Map<String, String> fieldValues) {
        // remove hidden fields
        LookupUtils.removeHiddenCriteriaFields( getBusinessObjectClass(), fieldValues );

        setBackLocation(fieldValues.get(Constants.BACK_LOCATION));
        setDocFormKey(fieldValues.get(Constants.DOC_FORM_KEY));
        setReferencesToRefresh(fieldValues.get(Constants.REFERENCES_TO_REFRESH));
        
        List searchResults = (List) getLookupService().findCollectionBySearchHelper(getBusinessObjectClass(), fieldValues, false);
        // sort list if default sort column given
        List defaultSortColumns = getDefaultSortColumns();
        if (defaultSortColumns.size() > 0) {
            Collections.sort(searchResults, new BeanPropertyComparator(getDefaultSortColumns(), true));
        }
        return searchResults;
    }

}
