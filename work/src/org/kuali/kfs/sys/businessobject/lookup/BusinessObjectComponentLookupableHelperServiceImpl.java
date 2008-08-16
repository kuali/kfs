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
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Pattern;
import java.util.regex.PatternSyntaxException;

import org.apache.commons.lang.StringUtils;
import org.kuali.kfs.sys.KFSConstants;
import org.kuali.kfs.sys.batch.BatchJobStatus;
import org.kuali.kfs.sys.businessobject.BusinessObjectComponent;
import org.kuali.kfs.sys.service.impl.ParameterConstants;
import org.kuali.rice.kns.bo.BusinessObject;
import org.kuali.rice.kns.bo.user.UniversalUser;
import org.kuali.rice.kns.datadictionary.BusinessObjectEntry;
import org.kuali.rice.kns.lookup.KualiLookupableHelperServiceImpl;
import org.kuali.rice.kns.lookup.LookupUtils;
import org.kuali.rice.kns.util.BeanPropertyComparator;
import org.kuali.rice.kns.util.GlobalVariables;
import org.kuali.rice.kns.util.KNSConstants;
import org.kuali.rice.kns.util.UrlFactory;

public class BusinessObjectComponentLookupableHelperServiceImpl extends KualiLookupableHelperServiceImpl {

    private static final org.apache.log4j.Logger LOG = org.apache.log4j.Logger.getLogger(ParameterDetailTypeLookupableHelperServiceImpl.class);

    @Override
    public List<? extends BusinessObject> getSearchResults(java.util.Map<String, String> fieldValues) {
        super.setBackLocation((String) fieldValues.get(KFSConstants.BACK_LOCATION));
        super.setDocFormKey((String) fieldValues.get(KFSConstants.DOC_FORM_KEY));

        Map<Class,BusinessObjectEntry> uniqueBusinessObjectEntries = new HashMap<Class,BusinessObjectEntry>();
        for (BusinessObjectEntry businessObjectEntry : getDataDictionaryService().getDataDictionary().getBusinessObjectEntries().values()) {
            try {
            uniqueBusinessObjectEntries.put(businessObjectEntry.getBusinessObjectClass(), businessObjectEntry);
            }
            catch (Exception e) {
                LOG.error("The getDataDictionaryAndSpringComponents method of ParameterUtils encountered an exception while trying to create the detail type for business object class: " + businessObjectEntry.getBusinessObjectClass(), e);
            }
        }
        List<BusinessObjectComponent> businessObjectComponents = new ArrayList<BusinessObjectComponent>();
        for (BusinessObjectEntry businessObjectEntry : uniqueBusinessObjectEntries.values()) {
            businessObjectComponents.add(new BusinessObjectComponent(businessObjectEntry));
        }
        List<BusinessObjectComponent> matchingBusinessObjectComponents = new ArrayList<BusinessObjectComponent>();
        Pattern componentBusinessObjectLabelRegex = null;

        if (StringUtils.isNotBlank(fieldValues.get("componentLabel"))) {
            String patternStr = fieldValues.get("componentLabel").replace("*", ".*").toUpperCase();
            try {
                componentBusinessObjectLabelRegex = Pattern.compile(patternStr);
            }
            catch (PatternSyntaxException ex) {
                LOG.error("Unable to parse componentLabel pattern, ignoring.", ex);
            }
        }
        for (BusinessObjectComponent businessObjectComponent : businessObjectComponents) {
            if ((StringUtils.isBlank(fieldValues.get("namespaceCode")) || businessObjectComponent.getNamespaceCode().equals(fieldValues.get("namespaceCode")))
                    && ((componentBusinessObjectLabelRegex == null) || componentBusinessObjectLabelRegex.matcher(businessObjectComponent.getComponentLabel().toUpperCase()).matches())) {
                matchingBusinessObjectComponents.add(businessObjectComponent);
            }
        }
        List defaultSortColumns = getDefaultSortColumns();
        if (defaultSortColumns.size() > 0) {
            Collections.sort(matchingBusinessObjectComponents, new BeanPropertyComparator(getDefaultSortColumns(), true));
        }
        return matchingBusinessObjectComponents;
    }

    @Override
    public List getReturnKeys() {
        List<String> returnKeys = new ArrayList();
        returnKeys.add("namespaceCode");
        returnKeys.add("componentClass");
        return returnKeys;
    }

    @Override
    public String getActionUrls(BusinessObject businessObject) {
        return "";
    }
}
