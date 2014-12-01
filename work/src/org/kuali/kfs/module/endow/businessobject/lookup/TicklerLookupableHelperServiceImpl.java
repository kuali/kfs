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
package org.kuali.kfs.module.endow.businessobject.lookup;

import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang.StringUtils;
import org.kuali.kfs.module.endow.EndowPropertyConstants;
import org.kuali.rice.core.api.criteria.PredicateUtils;
import org.kuali.rice.core.api.criteria.QueryByCriteria;
import org.kuali.rice.kim.api.KimConstants;
import org.kuali.rice.kim.api.group.GroupService;
import org.kuali.rice.kim.api.identity.principal.Principal;
import org.kuali.rice.kim.api.services.KimApiServiceLocator;
import org.kuali.rice.kns.lookup.KualiLookupableHelperServiceImpl;
import org.kuali.rice.krad.bo.BusinessObject;
import org.kuali.rice.krad.util.ObjectUtils;

/**
 * Allows custom handling of Proposals within the lookup framework.
 */
public class TicklerLookupableHelperServiceImpl extends KualiLookupableHelperServiceImpl {

    /**
     * @see org.kuali.rice.kns.lookup.KualiLookupableHelperServiceImpl#getSearchResultsHelper(java.util.Map, boolean)
     */
    @Override
    protected List<? extends BusinessObject> getSearchResultsHelper(Map<String, String> fieldValues, boolean unbounded)
    {
        // Perform the lookup on the project director object first
        if (!StringUtils.isBlank(fieldValues.get(EndowPropertyConstants.TICKLER_LOOKUP_USER_ID_FIELD))) {
            Principal principal = KimApiServiceLocator.getIdentityService().getPrincipalByPrincipalName(fieldValues.get(EndowPropertyConstants.TICKLER_LOOKUP_USER_ID_FIELD));
            // if no project directors match, we can return an empty list right now
            if (principal == null) {
                return Collections.EMPTY_LIST;
            }

            // place the universal ID into the fieldValues map and remove the dummy attribute
            fieldValues.put(EndowPropertyConstants.TICKLER_LOOKUP_UNIVERSAL_USER_ID_FIELD, principal.getPrincipalId());
            fieldValues.remove(EndowPropertyConstants.TICKLER_LOOKUP_USER_ID_FIELD);
        }

        String groupName = fieldValues.get(EndowPropertyConstants.TICKLER_LOOKUP_GROUP_NAME_FIELD);
        String groupNamespace = fieldValues.get(EndowPropertyConstants.TICKLER_LOOKUP_GROUP_NAME_SPACE_FIELD);
        List<String> groupIds = null;

        // Perform the lookup on the Group
        Map<String, String> searchCriteria = new HashMap<String, String>(2);
        if( StringUtils.isNotBlank(groupName)) {
            searchCriteria.put(KimConstants.UniqueKeyConstants.GROUP_NAME, groupName);
        }
        if( StringUtils.isNotBlank(groupNamespace)) {
            searchCriteria.put(KimConstants.UniqueKeyConstants.NAMESPACE_CODE, groupNamespace);
        }
        if ( !searchCriteria.isEmpty() ) {
            groupIds = KimApiServiceLocator.getGroupService().findGroupIds( QueryByCriteria.Builder.fromPredicates(PredicateUtils.convertMapToPredicate(searchCriteria) ) );
            if(ObjectUtils.isNotNull(groupIds) && !groupIds.isEmpty()){
                fieldValues.put("recipientGroups.id", StringUtils.join(groupIds, "|"));
            }else{
                //we had search criteria that didn't result in group ids
                // we should return null as nothing matches on the namespace and name entered
                return Collections.EMPTY_LIST;
            }
        }

        fieldValues.remove(EndowPropertyConstants.TICKLER_LOOKUP_GROUP_NAME_FIELD);
        fieldValues.remove(EndowPropertyConstants.TICKLER_LOOKUP_GROUP_NAME_SPACE_FIELD);
        fieldValues.remove(EndowPropertyConstants.TICKLER_RECIPIENT_GROUPID);

        return super.getSearchResultsHelper(fieldValues, unbounded);
    }

    private GroupService getGroupService()
    {
        return KimApiServiceLocator.getGroupService();
    }
}
