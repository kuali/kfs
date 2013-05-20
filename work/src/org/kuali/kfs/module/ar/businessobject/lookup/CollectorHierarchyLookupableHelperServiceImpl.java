/*
 * Copyright 2008 The Kuali Foundation
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
package org.kuali.kfs.module.ar.businessobject.lookup;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.StringUtils;
import org.kuali.kfs.module.ar.ArPropertyConstants;
import org.kuali.kfs.module.ar.businessobject.CollectorHierarchy;
import org.kuali.kfs.sys.context.SpringContext;
import org.kuali.rice.kim.api.identity.Person;
import org.kuali.rice.krad.lookup.CollectionIncomplete;
import org.kuali.rice.kns.lookup.KualiLookupableHelperServiceImpl;
import org.kuali.rice.krad.util.ObjectUtils;

/**
 * Lookupable Helper Service class for CollectorHierarchy.
 */
public class CollectorHierarchyLookupableHelperServiceImpl extends KualiLookupableHelperServiceImpl {

    private Map fieldConversions;

    /**
     * Get the search results that meet the input search criteria.
     * 
     * @param fieldValues - Map containing prop name keys and search values
     * @return a List of found business objects
     */
    @Override
    public List getSearchResults(Map fieldValues) {
        List finalResults = new ArrayList<CollectorHierarchy>();
        String principalName = fieldValues.get(ArPropertyConstants.CollectorHierarchyFields.COLLECTOR_PRINC_NAME).toString();
        String principalId = fieldValues.get(ArPropertyConstants.CollectorHierarchyFields.COLLECTOR).toString();
        if (StringUtils.isNotEmpty(principalName)) {
            Person collector = SpringContext.getBean(org.kuali.rice.kim.api.identity.PersonService.class).getPersonByPrincipalName(principalName);
            if (ObjectUtils.isNotNull(collector)) {
                fieldValues.put(ArPropertyConstants.CollectorHierarchyFields.COLLECTOR, collector.getPrincipalId());
            }
            else {
                return new CollectionIncomplete(finalResults, new Long(finalResults.size()));
            }
        }
        String name = fieldValues.get(ArPropertyConstants.CollectorHierarchyFields.COLLECTOR_NAME).toString();
        List results = super.getSearchResults(fieldValues);

        if (StringUtils.isNotEmpty(name)) {
            if (ObjectUtils.isNotNull(results) && CollectionUtils.isNotEmpty(results)) {
                finalResults = new ArrayList<CollectorHierarchy>();
                Iterator iter = results.iterator();
                for (Object obj : results) {
                    CollectorHierarchy collectorHierarchy = (CollectorHierarchy) obj;
                    if (collectorHierarchy.getCollector().getName().equalsIgnoreCase(name)) {
                        finalResults.add(collectorHierarchy);
                    }
                }
                return new CollectionIncomplete(finalResults, new Long(finalResults.size()));
            }
        }
        return new CollectionIncomplete(results, new Long(results.size()));
    }

}
