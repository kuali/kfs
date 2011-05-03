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
import java.util.List;
import java.util.Map;

import org.apache.commons.lang.StringUtils;
import org.kuali.kfs.module.endow.EndowPropertyConstants;
import org.kuali.kfs.sys.context.SpringContext;
import org.kuali.rice.kim.bo.Person;
import org.kuali.rice.kim.service.GroupService;
import org.kuali.rice.kim.service.PersonService;
import org.kuali.rice.kns.bo.BusinessObject;
import org.kuali.rice.kns.lookup.KualiLookupableHelperServiceImpl;

/**
 * Allows custom handling of Proposals within the lookup framework.
 */
public class TicklerLookupableHelperServiceImpl extends KualiLookupableHelperServiceImpl 
{
    private PersonService<Person> personService;

    /**
     * @see org.kuali.rice.kns.lookup.KualiLookupableHelperServiceImpl#getSearchResultsHelper(java.util.Map, boolean)
     */
    @Override
    protected List<? extends BusinessObject> getSearchResultsHelper(Map<String, String> fieldValues, boolean unbounded) 
    {
        // Perform the lookup on the project director object first
        if (!StringUtils.isBlank(fieldValues.get(EndowPropertyConstants.TICKLER_LOOKUP_USER_ID_FIELD))) {
            Person person = getPersonService().getPersonByPrincipalName(fieldValues.get(EndowPropertyConstants.TICKLER_LOOKUP_USER_ID_FIELD));

            // if no project directors match, we can return an empty list right now
            if (person == null) {
                return Collections.EMPTY_LIST;
            }
            
            // place the universal ID into the fieldValues map and remove the dummy attribute
            fieldValues.put(EndowPropertyConstants.TICKLER_LOOKUP_UNIVERSAL_USER_ID_FIELD, person.getPrincipalId());
            fieldValues.remove(EndowPropertyConstants.TICKLER_LOOKUP_USER_ID_FIELD);
        }

        // Perform the lookup on the Group 
        if (!StringUtils.isBlank(fieldValues.get(EndowPropertyConstants.TICKLER_RECIPIENT_GROUPID))) 
        {
            // Place the Group ID into the fieldValues map and remove the dummy attribute
            fieldValues.put(EndowPropertyConstants.TICKLER_LOOKUP_GROUP_NAME_FIELD, fieldValues.get(EndowPropertyConstants.TICKLER_RECIPIENT_GROUPID));
            fieldValues.remove(EndowPropertyConstants.TICKLER_LOOKUP_GROUP_NAME_FIELD);
            fieldValues.remove(EndowPropertyConstants.TICKLER_RECIPIENT_GROUPID);
            
        }
        
        return super.getSearchResultsHelper(fieldValues, unbounded);
    }

    /**
     * @return Returns the personService.
     */
    protected PersonService<Person> getPersonService() {
        if(personService==null)
            personService = SpringContext.getBean(PersonService.class);
        return personService;
    }

    private GroupService getGroupService()
    {
        return SpringContext.getBean(GroupService.class);
    }
}
