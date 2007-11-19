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
package org.kuali.module.vendor.lookup;

import java.util.Collections;
import java.util.List;
import java.util.Map;

import org.kuali.core.bo.BusinessObject;
import org.kuali.core.bo.user.UniversalUser;
import org.kuali.core.lookup.KualiLookupableHelperServiceImpl;
import org.kuali.core.util.BeanPropertyComparator;
import org.kuali.kfs.KFSConstants;


public class VendorCustomerNumberLookupableHelperServiceImpl extends KualiLookupableHelperServiceImpl {
    private boolean searchUsingOnlyPrimaryKeyValues = false;

    /**
     * Overrides the getSearchResultsHelper in the super class so that we can do some customization
     * 
     * @see org.kuali.core.lookup.KualiLookupableHelperServiceImpl#getSearchResultsHelper(java.util.Map, boolean)
     */
    @Override
    protected List<? extends BusinessObject> getSearchResultsHelper(Map<String, String> fieldValues, boolean unbounded) {
        searchUsingOnlyPrimaryKeyValues = getLookupService().allPrimaryKeyValuesPresentAndNotWildcard(getBusinessObjectClass(), fieldValues);

        setBackLocation(fieldValues.get(KFSConstants.BACK_LOCATION));
        setDocFormKey(fieldValues.get(KFSConstants.DOC_FORM_KEY));
        setReferencesToRefresh(fieldValues.get(KFSConstants.REFERENCES_TO_REFRESH));
        List searchResults;
        if (UniversalUser.class.equals(getBusinessObjectClass())) {
            searchResults = (List) getUniversalUserService().findUniversalUsers(fieldValues);
        }
        else if (getUniversalUserService().hasUniversalUserProperty(getBusinessObjectClass(), fieldValues)) {
            searchResults = (List) getUniversalUserService().findWithUniversalUserJoin(getBusinessObjectClass(), fieldValues, unbounded);
        }
        else {
            searchResults = (List) getLookupService().findCollectionBySearchHelper(getBusinessObjectClass(), fieldValues, unbounded);
        }
        // sort list if default sort column given
        List defaultSortColumns = getDefaultSortColumns();
        if (defaultSortColumns.size() > 0) {
            Collections.sort(searchResults, new BeanPropertyComparator(getDefaultSortColumns(), true));
        }

        return searchResults;
    }

    /**
     * @see LookupableHelperService#isSearchUsingOnlyPrimaryKeyValues()
     */
    @Override
    public boolean isSearchUsingOnlyPrimaryKeyValues() {

        return searchUsingOnlyPrimaryKeyValues;
    }
}
