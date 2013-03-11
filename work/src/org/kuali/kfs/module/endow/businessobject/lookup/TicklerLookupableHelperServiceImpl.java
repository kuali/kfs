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
