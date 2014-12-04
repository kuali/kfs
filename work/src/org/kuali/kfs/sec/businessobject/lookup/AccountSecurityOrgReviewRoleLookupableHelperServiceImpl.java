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
package org.kuali.kfs.sec.businessobject.lookup;

import java.util.List;
import java.util.Map;

import org.kuali.kfs.coa.identity.OrgReviewRoleLookupableHelperServiceImpl;
import org.kuali.kfs.sec.SecKeyConstants;
import org.kuali.kfs.sec.service.AccessSecurityService;
import org.kuali.kfs.sec.util.SecUtil;
import org.kuali.rice.krad.util.GlobalVariables;

/**
 * Lookupable helper that provides Access Security integration
 */
public class AccountSecurityOrgReviewRoleLookupableHelperServiceImpl extends OrgReviewRoleLookupableHelperServiceImpl {
    protected AccessSecurityService accessSecurityService;
    
    /**
     * Gets search results and passes to access security service to apply access restrictions
     * 
     * @see org.kuali.rice.kns.lookup.LookupableHelperService#getSearchResults(java.util.Map)
     */
    public List getSearchResults(Map<String, String> fieldValues) {
        List results = super.getSearchResults(fieldValues);

        int resultSizeBeforeRestrictions = results.size();
        accessSecurityService.applySecurityRestrictionsForLookup(results, GlobalVariables.getUserSession().getPerson());

        accessSecurityService.compareListSizeAndAddMessageIfChanged(resultSizeBeforeRestrictions, results, SecKeyConstants.MESSAGE_LOOKUP_RESULTS_RESTRICTED);

        return results;
    }

    /**
     * Gets search results and passes to access security service to apply access restrictions
     * 
     * @see org.kuali.rice.kns.lookup.LookupableHelperService#getSearchResultsUnbounded(java.util.Map)
     */
    public List getSearchResultsUnbounded(Map<String, String> fieldValues) {
        List results = super.getSearchResultsUnbounded(fieldValues);

        int resultSizeBeforeRestrictions = results.size();
        accessSecurityService.applySecurityRestrictionsForLookup(results, GlobalVariables.getUserSession().getPerson());
        
        accessSecurityService.compareListSizeAndAddMessageIfChanged(resultSizeBeforeRestrictions, results, SecKeyConstants.MESSAGE_LOOKUP_RESULTS_RESTRICTED);

        return results;
    }

    /**
     * Sets the accessSecurityService attribute value.
     * 
     * @param accessSecurityService The accessSecurityService to set.
     */
    public void setAccessSecurityService(AccessSecurityService accessSecurityService) {
        this.accessSecurityService = accessSecurityService;
    }
}
