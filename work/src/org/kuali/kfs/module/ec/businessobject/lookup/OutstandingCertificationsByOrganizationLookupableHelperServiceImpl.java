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
package org.kuali.kfs.module.ec.businessobject.lookup;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.kuali.kfs.sys.KFSConstants;
import org.kuali.kfs.sys.KFSPropertyConstants;
import org.kuali.rice.kns.lookup.KualiLookupableHelperServiceImpl;
import org.kuali.rice.krad.bo.BusinessObject;

/**
 * Searches for documents that are not approved.
 */
public class OutstandingCertificationsByOrganizationLookupableHelperServiceImpl extends KualiLookupableHelperServiceImpl {

    /**
     * @see org.kuali.rice.kns.lookup.LookupableHelperService#getSearchResults(java.util.Map)
     */
    public List<? extends BusinessObject> getSearchResults(Map<String, String> fieldValues) {
        fieldValues.put(KFSPropertyConstants.DOCUMENT_HEADER + "." + KFSPropertyConstants.FINANCIAL_DOCUMENT_STATUS_CODE, "!" + KFSConstants.DocumentStatusCodes.APPROVED);
        
        // remove the duplicate effort documents
        Set<? extends BusinessObject> searchResultSet = new HashSet<BusinessObject>(super.getSearchResults(fieldValues));
        
        return new ArrayList<BusinessObject> (searchResultSet);
    }
    
    /**
     * @see org.kuali.rice.kns.lookup.KualiLookupableHelperServiceImpl#getSearchResultsUnbounded(java.util.Map)
     */
    @Override
    public List<? extends BusinessObject> getSearchResultsUnbounded(Map<String, String> arg0) {
        return getSearchResults(arg0);
    }

}
