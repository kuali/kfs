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
package org.kuali.kfs.module.cg.businessobject.lookup;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang.StringUtils;
import org.kuali.rice.kim.api.identity.principal.Principal;
import org.kuali.rice.kim.api.services.KimApiServiceLocator;
import org.kuali.rice.kns.lookup.HtmlData;
import org.kuali.rice.kns.lookup.KualiLookupableHelperServiceImpl;
import org.kuali.rice.krad.bo.BusinessObject;
import org.kuali.rice.krad.util.KRADConstants;

/**
 * Allows custom handling of Awards within the lookup framework.
 */
public class AwardLookupableHelperServiceImpl extends KualiLookupableHelperServiceImpl {

    private static final String LOOKUP_USER_ID_FIELD = "lookupPerson.principalName";
    private static final String LOOKUP_UNIVERSAL_USER_ID_FIELD = "awardProjectDirectors.principalId";


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

    /**
     * @see org.kuali.rice.kns.lookup.AbstractLookupableHelperServiceImpl#getCustomActionUrls(org.kuali.rice.krad.bo.BusinessObject, List pkNames)
     */
    @Override
    public List<HtmlData> getCustomActionUrls(BusinessObject businessObject, List pkNames) {
        List<HtmlData> anchorHtmlDataList = new ArrayList<HtmlData>();
        anchorHtmlDataList.add(getUrlData(businessObject, KRADConstants.MAINTENANCE_EDIT_METHOD_TO_CALL, pkNames));
        return anchorHtmlDataList;
    }

}
