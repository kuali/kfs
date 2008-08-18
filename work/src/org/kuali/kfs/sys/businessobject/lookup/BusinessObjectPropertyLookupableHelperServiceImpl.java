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
import java.util.List;
import java.util.regex.Pattern;
import java.util.regex.PatternSyntaxException;

import org.apache.commons.lang.StringUtils;
import org.kuali.kfs.sys.KFSConstants;
import org.kuali.kfs.sys.businessobject.BusinessObjectComponent;
import org.kuali.kfs.sys.businessobject.BusinessObjectProperty;
import org.kuali.rice.kns.bo.BusinessObject;
import org.kuali.rice.kns.datadictionary.AttributeDefinition;
import org.kuali.rice.kns.lookup.LookupableHelperService;
import org.kuali.rice.kns.util.BeanPropertyComparator;

public class BusinessObjectPropertyLookupableHelperServiceImpl extends BusinessObjectComponentLookupableHelperServiceImpl {

    private static final org.apache.log4j.Logger LOG = org.apache.log4j.Logger.getLogger(ParameterDetailTypeLookupableHelperServiceImpl.class);
    
    @Override
    public List<? extends BusinessObject> getSearchResults(java.util.Map<String, String> fieldValues) {
        List<BusinessObjectComponent> businessObjectComponents = (List<BusinessObjectComponent>)super.getSearchResults(fieldValues);

        Pattern propertyLabelRegex = null;
        if (StringUtils.isNotBlank(fieldValues.get("propertyLabel"))) {
            String patternStr = fieldValues.get("propertyLabel").replace("*", ".*").toUpperCase();
            try {
                propertyLabelRegex = Pattern.compile(patternStr);
            }
            catch (PatternSyntaxException ex) {
                LOG.error("Unable to parse propertyLabel pattern, ignoring.", ex);
            }
        }

        List<BusinessObjectProperty> matchingBusinessObjectProperties = new ArrayList<BusinessObjectProperty>();
        for (BusinessObjectComponent businessObjectComponent : businessObjectComponents) {
            for (AttributeDefinition attributeDefinition : getDataDictionaryService().getDataDictionary().getBusinessObjectEntry(businessObjectComponent.getComponentClass().toString()).getAttributes()) {
                if ((propertyLabelRegex == null) || propertyLabelRegex.matcher(businessObjectComponent.getComponentLabel().toUpperCase()).matches()) {
                    matchingBusinessObjectProperties.add(new BusinessObjectProperty(businessObjectComponent, attributeDefinition));
                }
            }
        }
        
        List defaultSortColumns = getDefaultSortColumns();
        if (defaultSortColumns.size() > 0) {
            Collections.sort(matchingBusinessObjectProperties, new BeanPropertyComparator(getDefaultSortColumns(), true));
        }
        return matchingBusinessObjectProperties;
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
}
