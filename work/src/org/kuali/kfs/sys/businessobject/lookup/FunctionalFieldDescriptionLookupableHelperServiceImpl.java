/*
 * Copyright 2007-2008 The Kuali Foundation
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
package org.kuali.kfs.sys.businessobject.lookup;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;

import org.kuali.kfs.sys.KFSPropertyConstants;
import org.kuali.kfs.sys.businessobject.FunctionalFieldDescription;
import org.kuali.kfs.sys.service.KfsBusinessObjectMetaDataService;
import org.kuali.rice.kns.lookup.KualiLookupableHelperServiceImpl;
import org.kuali.rice.krad.bo.BusinessObject;
import org.kuali.rice.krad.util.BeanPropertyComparator;

public class FunctionalFieldDescriptionLookupableHelperServiceImpl extends KualiLookupableHelperServiceImpl {
    private static final List<String> SORT_PROPERTIES = new ArrayList<String>();
    static {
        SORT_PROPERTIES.add(KFSPropertyConstants.NAMESPACE_CODE);
        SORT_PROPERTIES.add(KFSPropertyConstants.BUSINESS_OBJECT_PROPERTY_COMPONENT_LABEL);
        SORT_PROPERTIES.add(KFSPropertyConstants.BUSINESS_OBJECT_PROPERTY_LABEL);
    }
    protected KfsBusinessObjectMetaDataService kfsBusinessObjectMetaDataService;

    @Override
    public List<? extends BusinessObject> getSearchResults(Map<String, String> fieldValues) {
        List<FunctionalFieldDescription> searchResults = kfsBusinessObjectMetaDataService.findFunctionalFieldDescriptions(fieldValues.get(KFSPropertyConstants.NAMESPACE_CODE), fieldValues.get(KFSPropertyConstants.BUSINESS_OBJECT_PROPERTY_COMPONENT_LABEL), fieldValues.get(KFSPropertyConstants.BUSINESS_OBJECT_PROPERTY_LABEL), fieldValues.get(KFSPropertyConstants.DESCRIPTION), fieldValues.get(KFSPropertyConstants.ACTIVE));
        Collections.sort(searchResults, new BeanPropertyComparator(getSortProperties(), true));
        return searchResults;
    }

    protected List<String> getSortProperties() {
        return SORT_PROPERTIES;
    }

    public void setKfsBusinessObjectMetaDataService(KfsBusinessObjectMetaDataService kfsBusinessObjectMetaDataService) {
        this.kfsBusinessObjectMetaDataService = kfsBusinessObjectMetaDataService;
    }
}
