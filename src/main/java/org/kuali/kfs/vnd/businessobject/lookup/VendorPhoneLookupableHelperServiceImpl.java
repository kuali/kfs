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
import org.kuali.rice.kns.lookup.AbstractLookupableHelperServiceImpl;
import org.kuali.rice.krad.bo.PersistableBusinessObject;
import org.kuali.rice.krad.util.BeanPropertyComparator;


public class VendorPhoneLookupableHelperServiceImpl extends AbstractLookupableHelperServiceImpl {
    /**
     * Overrides the getSearchResults in the super class so that we can do some customization in our vendor phone number lookup.
     * 
     * @see org.kuali.rice.kns.lookup.Lookupable#getSearchResults(java.util.Map)
     */
    @Override
    public List<PersistableBusinessObject> getSearchResults(Map<String, String> fieldValues) {
        boolean unbounded = false;
        super.setBackLocation((String) fieldValues.get(KFSConstants.BACK_LOCATION));
        super.setDocFormKey((String) fieldValues.get(KFSConstants.DOC_FORM_KEY));

        List<PersistableBusinessObject> searchResults = (List) getLookupService().findCollectionBySearchHelper(getBusinessObjectClass(), fieldValues, unbounded);

        // loop through results
        // sort list if default sort column given
        List<String> defaultSortColumns = getDefaultSortColumns();
        if (defaultSortColumns.size() > 0) {
            Collections.sort(searchResults, new BeanPropertyComparator(getDefaultSortColumns(), true));
        }

        return searchResults;
    }

}
