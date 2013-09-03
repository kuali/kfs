/*
 * Copyright 2012 The Kuali Foundation
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
package org.kuali.kfs.module.tem.businessobject.lookup;

import java.util.List;
import java.util.Map;

import org.kuali.kfs.module.tem.businessobject.PrimaryDestination;
import org.kuali.rice.kns.lookup.KualiLookupableHelperServiceImpl;
import org.kuali.rice.kns.lookup.LookupUtils;
import org.kuali.rice.krad.bo.BusinessObject;
import org.kuali.rice.krad.lookup.CollectionIncomplete;


@SuppressWarnings("deprecation")
public class PrimaryDestinationLookupableHelperServiceImpl extends KualiLookupableHelperServiceImpl {

    /**
     * @see org.kuali.rice.kns.lookup.KualiLookupableHelperServiceImpl#getSearchResults(java.util.Map)
     */
    @SuppressWarnings("rawtypes")
    @Override
    public List<? extends BusinessObject> getSearchResults(Map<String, String> fieldValues) {
        fieldValues.put("active", "true");
        List<PrimaryDestination> results = (List<PrimaryDestination>) super.getSearchResultsHelper(fieldValues, true);

        CollectionIncomplete collection = null;
        Integer limit = LookupUtils.getSearchResultsLimit(PrimaryDestination.class);
        if (results.size() > limit.intValue()){
            collection = new CollectionIncomplete(results.subList(0, limit), (long) results.size());
        }
        else{
            collection = new CollectionIncomplete(results, (long) 0);
        }

        return collection;


    }

}
