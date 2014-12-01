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
package org.kuali.kfs.module.cg.businessobject.lookup;

import java.util.Collections;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang.StringUtils;
import org.kuali.rice.kim.api.identity.principal.Principal;
import org.kuali.rice.kim.api.services.KimApiServiceLocator;
import org.kuali.rice.kns.lookup.KualiLookupableHelperServiceImpl;
import org.kuali.rice.krad.bo.BusinessObject;

/**
 * Allows custom handling of Proposals within the lookup framework.
 */
public class ProposalLookupableHelperServiceImpl extends KualiLookupableHelperServiceImpl {

    private static final String LOOKUP_USER_ID_FIELD = "lookupPerson.principalName";
    private static final String LOOKUP_UNIVERSAL_USER_ID_FIELD = "proposalProjectDirectors.principalId";


    /**
     * @see org.kuali.rice.kns.lookup.KualiLookupableHelperServiceImpl#getSearchResultsHelper(java.util.Map, boolean)
     */
    @Override
    protected List<? extends BusinessObject> getSearchResultsHelper(Map<String, String> fieldValues, boolean unbounded) {
        // perform the lookup on the project director object first
        if (!StringUtils.isBlank(fieldValues.get(LOOKUP_USER_ID_FIELD))) {
            Principal principal = KimApiServiceLocator.getIdentityService().getPrincipalByPrincipalName(fieldValues.get(LOOKUP_USER_ID_FIELD));

            // if no project directors match, we can return an empty list right now
            if (principal == null) {
                return Collections.EMPTY_LIST;
            }

            // place the universal ID into the fieldValues map and remove the dummy attribute
            fieldValues.put(LOOKUP_UNIVERSAL_USER_ID_FIELD, principal.getPrincipalId());
            fieldValues.remove(LOOKUP_USER_ID_FIELD);
        }

        return super.getSearchResultsHelper(fieldValues, unbounded);
    }

}
