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
package org.kuali.module.cg.lookup;

import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang.StringUtils;
import org.kuali.core.bo.BusinessObject;
import org.kuali.core.lookup.KualiLookupableHelperServiceImpl;
import org.kuali.module.cg.bo.ProjectDirector;

/**
 * Allows custom handling of Proposals within the lookup framework.
 */
public class ProposalLookupableHelperServiceImpl extends KualiLookupableHelperServiceImpl {

    private static final String LOOKUP_USER_ID_FIELD = "lookupUniversalUser.personUserIdentifier";
    private static final String LOOKUP_UNIVERSAL_USER_ID_FIELD = "proposalProjectDirectors.projectDirector.personUniversalIdentifier";
    private static final String PROJECT_DIRECTOR_USER_ID_LOOKUP_FIELD = "universalUser.personUserIdentifier";

    /**
     * @see org.kuali.core.lookup.KualiLookupableHelperServiceImpl#getSearchResultsHelper(java.util.Map, boolean)
     */
    @Override
    protected List<? extends BusinessObject> getSearchResultsHelper(Map<String, String> fieldValues, boolean unbounded) {
        // perform the lookup on the project director object first
        if (!StringUtils.isBlank(fieldValues.get(LOOKUP_USER_ID_FIELD))) {
            HashMap<String, String> newParam = new HashMap<String, String>(1);
            newParam.put(PROJECT_DIRECTOR_USER_ID_LOOKUP_FIELD, fieldValues.get(LOOKUP_USER_ID_FIELD));
            Collection<ProjectDirector> pds = getLookupService().findCollectionBySearchUnbounded(ProjectDirector.class, newParam);
            // if no project directors match, we can return an empty list right now
            if (pds.isEmpty()) {
                return Collections.EMPTY_LIST;
            }
            // place the universal ID into the fieldValues map and remove the dummy attribute
            fieldValues.put(LOOKUP_UNIVERSAL_USER_ID_FIELD, pds.iterator().next().getPersonUniversalIdentifier());
            fieldValues.remove(LOOKUP_USER_ID_FIELD);
        }

        return super.getSearchResultsHelper(fieldValues, unbounded);
    }

}
