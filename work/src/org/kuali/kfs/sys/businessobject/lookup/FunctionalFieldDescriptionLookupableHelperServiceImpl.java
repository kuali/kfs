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

import org.kuali.kfs.sys.businessobject.BusinessObjectProperty;
import org.kuali.kfs.sys.businessobject.FunctionalFieldDescription;
import org.kuali.rice.kns.bo.BusinessObject;
import org.kuali.rice.kns.util.BeanPropertyComparator;

public class FunctionalFieldDescriptionLookupableHelperServiceImpl extends BusinessObjectPropertyLookupableHelperServiceImpl {

    private static final org.apache.log4j.Logger LOG = org.apache.log4j.Logger.getLogger(ParameterDetailTypeLookupableHelperServiceImpl.class);
    
    @Override
    public List<? extends BusinessObject> getSearchResults(java.util.Map<String, String> fieldValues) {
        List<BusinessObjectProperty> businessObjectProperties = (List<BusinessObjectProperty>)super.getSearchResults(fieldValues);
        Set<String> namespaceCodes = new HashSet<String>();
        Set<String> componentClasses = new HashSet<String>();
        Set<String> propertyNames = new HashSet<String>();
        for (BusinessObjectProperty businessObjectProperty : businessObjectProperties) {
            namespaceCodes.add(businessObjectProperty.getNamespaceCode());
            componentClasses.add(businessObjectProperty.getComponentClass());
            propertyNames.add(businessObjectProperty.getPropertyName());
        }
        fieldValues.put("namespaceCode", buildOrCriteria(namespaceCodes));
        fieldValues.put("componentClass", buildOrCriteria(componentClasses));
        fieldValues.put("propertyName", buildOrCriteria(propertyNames));
        fieldValues.remove("componentLabel");
        fieldValues.remove("propertyLabel");

        List<FunctionalFieldDescription> searchResults = (List) getLookupService().findCollectionBySearchHelper(getBusinessObjectClass(), fieldValues, false);
        
        List defaultSortColumns = getDefaultSortColumns();
        if (defaultSortColumns.size() > 0) {
            Collections.sort(searchResults, new BeanPropertyComparator(getDefaultSortColumns(), true));
        }
        return searchResults;
    }

    @Override
    public List getReturnKeys() {
        List<String> returnKeys = new ArrayList();
        returnKeys.add("namespaceCode");
        returnKeys.add("namespaceName");
        returnKeys.add("componentClass");
        returnKeys.add("componentLabel");
        returnKeys.add("propertyName");
        returnKeys.add("propertyLabel");
        return returnKeys;
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
}
