/*
 * The Kuali Financial System, a comprehensive financial management system for higher education.
 * 
 * Copyright 2005-2014 The Kuali Foundation
 * 
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Affero General Public License as
 * published by the Free Software Foundation, either version 3 of the
 * License, or (at your option) any later version.
 * 
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Affero General Public License for more details.
 * 
 * You should have received a copy of the GNU Affero General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package org.kuali.kfs.vnd.businessobject.lookup;

import java.util.Collections;
import java.util.List;
import java.util.Map;

import org.kuali.kfs.sys.KFSConstants;
import org.kuali.rice.kns.lookup.KualiLookupableHelperServiceImpl;
import org.kuali.rice.krad.bo.BusinessObject;
import org.kuali.rice.krad.util.BeanPropertyComparator;


public class VendorAddressLookupableHelperServiceImpl extends KualiLookupableHelperServiceImpl {
    private boolean searchUsingOnlyPrimaryKeyValues = false;

    /**
     * Overrides the default behavior because we need to restrict the search results to the vendor ids that we are sending in as
     * criteria, but since they are set as hidden in the DD the default behavior is to remove them from the criteria.
     * 
     * @see org.kuali.rice.kns.lookup.KualiLookupableHelperServiceImpl#getSearchResultsHelper(java.util.Map, boolean)
     */
    @SuppressWarnings("unchecked")
    @Override
    protected List<? extends BusinessObject> getSearchResultsHelper(Map<String, String> fieldValues, boolean unbounded) {
        searchUsingOnlyPrimaryKeyValues = getLookupService().allPrimaryKeyValuesPresentAndNotWildcard(getBusinessObjectClass(), fieldValues);

        setBackLocation(fieldValues.get(KFSConstants.BACK_LOCATION));
        setDocFormKey(fieldValues.get(KFSConstants.DOC_FORM_KEY));
        setReferencesToRefresh(fieldValues.get(KFSConstants.REFERENCES_TO_REFRESH));
        List searchResults = (List) getLookupService().findCollectionBySearchHelper(getBusinessObjectClass(), fieldValues, unbounded);
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

