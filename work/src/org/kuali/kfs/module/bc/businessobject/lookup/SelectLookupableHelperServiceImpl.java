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
package org.kuali.kfs.module.bc.businessobject.lookup;

import java.util.Collections;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang.StringUtils;
import org.kuali.core.bo.BusinessObject;
import org.kuali.core.lookup.KualiLookupableHelperServiceImpl;
import org.kuali.core.util.BeanPropertyComparator;
import org.kuali.kfs.sys.KFSConstants;
import org.kuali.rice.kns.util.KNSConstants;

/**
 * Base lookupable helper service for budget selection lookups.
 */
public class SelectLookupableHelperServiceImpl extends KualiLookupableHelperServiceImpl {

    /**
     * Super impl clears out hidden values but we need to keep personUniversalIdentifier hidden field in the criteria. 
     * Overridding here so that the call to clear hiddens is not executed.
     * 
     * @see org.kuali.core.lookup.AbstractLookupableHelperServiceImpl#getSearchResults(java.util.Map)
     */
    @Override
    public List<? extends BusinessObject> getSearchResults(Map<String, String> fieldValues) {
        setBackLocation(fieldValues.get(KFSConstants.BACK_LOCATION));
        setDocFormKey(fieldValues.get(KFSConstants.DOC_FORM_KEY));
        setReferencesToRefresh(fieldValues.get(KFSConstants.REFERENCES_TO_REFRESH));

        List searchResults = (List) getLookupService().findCollectionBySearchHelper(getBusinessObjectClass(), fieldValues, false);

        // sort list if default sort column given
        List defaultSortColumns = getDefaultSortColumns();
        if (defaultSortColumns.size() > 0) {
            Collections.sort(searchResults, new BeanPropertyComparator(getDefaultSortColumns(), true));
        }

        return searchResults;
    }

    
    /**
     * Since this lookupable is called by the budget lookup action, the context will be KFS, not Rice. So the generated inquiries
     * will not have the Rice context (kr/) and be invalid. This override adds the Rice context to the inquiry Url to working
     * around the issue.
     * 
     * @see org.kuali.core.lookup.AbstractLookupableHelperServiceImpl#getInquiryUrl(org.kuali.core.bo.BusinessObject,
     *      java.lang.String)
     */
    @Override
    public String getInquiryUrl(BusinessObject bo, String propertyName) {
        String inquiryUrl = super.getInquiryUrl(bo, propertyName);
        inquiryUrl = StringUtils.replace(inquiryUrl, KNSConstants.INQUIRY_ACTION, KFSConstants.INQUIRY_ACTION);

        return inquiryUrl;
    }
}