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
package org.kuali.kfs.sys.businessobject.lookup;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.kuali.kfs.sys.KFSConstants;
import org.kuali.kfs.sys.KFSPropertyConstants;
import org.kuali.kfs.sys.businessobject.BusinessObjectProperty;
import org.kuali.kfs.sys.businessobject.FunctionalFieldDescription;
import org.kuali.kfs.sys.service.KfsBusinessObjectMetaDataService;
import org.kuali.rice.kns.bo.BusinessObject;
import org.kuali.rice.kns.lookup.KualiLookupableHelperServiceImpl;
import org.kuali.rice.kns.util.BeanPropertyComparator;

public class FunctionalFieldDescriptionLookupableHelperServiceImpl extends KualiLookupableHelperServiceImpl {

    protected KfsBusinessObjectMetaDataService kfsBusinessObjectMetaDataService;

    @Override
    public List<? extends BusinessObject> getSearchResults(java.util.Map<String, String> fieldValues) {
        List<BusinessObjectProperty> businessObjectProperties = kfsBusinessObjectMetaDataService.findBusinessObjectProperties(fieldValues.get(KFSPropertyConstants.NAMESPACE_CODE), fieldValues.get(KFSPropertyConstants.BUSINESS_OBJECT_PROPERTY_COMPONENT_LABEL), fieldValues.get(KFSPropertyConstants.BUSINESS_OBJECT_PROPERTY_LABEL));
        Set<String> namespaceCodes = new HashSet<String>();
        Set<String> componentClasses = new HashSet<String>();
        Set<String> propertyNames = new HashSet<String>();
        for (BusinessObjectProperty businessObjectProperty : businessObjectProperties) {
            namespaceCodes.add(businessObjectProperty.getNamespaceCode());
            componentClasses.add(businessObjectProperty.getComponentClass());
            propertyNames.add(businessObjectProperty.getPropertyName());
        }
        fieldValues.put(KFSPropertyConstants.NAMESPACE_CODE, buildOrCriteria(namespaceCodes));
        fieldValues.put(KFSPropertyConstants.COMPONENT_CLASS, buildOrCriteria(componentClasses));
        fieldValues.put(KFSPropertyConstants.PROPERTY_NAME, buildOrCriteria(propertyNames));
        fieldValues.remove(KFSPropertyConstants.BUSINESS_OBJECT_PROPERTY_COMPONENT_LABEL);
        fieldValues.remove(KFSPropertyConstants.BUSINESS_OBJECT_PROPERTY_LABEL);
        List<FunctionalFieldDescription> searchResults = (List<FunctionalFieldDescription>) getLookupService().findCollectionBySearchHelper(FunctionalFieldDescription.class, fieldValues, false);
        for (FunctionalFieldDescription functionalFieldDescription : searchResults) {
            functionalFieldDescription.refreshNonUpdateableReferences();
        }        
        List<String> sortProperties = new ArrayList<String>();
        sortProperties.add(KFSPropertyConstants.NAMESPACE_CODE);
        sortProperties.add(KFSPropertyConstants.BUSINESS_OBJECT_PROPERTY_COMPONENT_LABEL);
        sortProperties.add(KFSPropertyConstants.BUSINESS_OBJECT_PROPERTY_LABEL);
        Collections.sort(searchResults, new BeanPropertyComparator(sortProperties, true));
        return searchResults;
    }

    private String buildOrCriteria(Set<String> values) {
        StringBuffer orCriteria = new StringBuffer();
        List<String> valueList = new ArrayList<String>(values);
        for (int i = 0; i < valueList.size(); i++) {
            orCriteria.append(valueList.get(i));
            if (i < (valueList.size() - 1)) {
                orCriteria.append("|");
            }
        }
        return orCriteria.toString();
    }

    public void setKfsBusinessObjectMetaDataService(KfsBusinessObjectMetaDataService kfsBusinessObjectMetaDataService) {
        this.kfsBusinessObjectMetaDataService = kfsBusinessObjectMetaDataService;
    }
}
