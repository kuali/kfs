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
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.kuali.kfs.sys.KFSPropertyConstants;
import org.kuali.kfs.sys.businessobject.BusinessObjectProperty;
import org.kuali.kfs.sys.businessobject.DataMappingFieldDefinition;
import org.kuali.kfs.sys.businessobject.FunctionalFieldDescription;
import org.kuali.kfs.sys.service.KfsBusinessObjectMetaDataService;
import org.kuali.rice.kns.lookup.HtmlData;
import org.kuali.rice.kns.lookup.HtmlData.AnchorHtmlData;
import org.kuali.rice.kns.lookup.KualiLookupableHelperServiceImpl;
import org.kuali.rice.kns.lookup.LookupUtils;
import org.kuali.rice.krad.bo.BusinessObject;
import org.kuali.rice.krad.lookup.CollectionIncomplete;
import org.kuali.rice.krad.util.BeanPropertyComparator;

public class DataMappingFieldDefinitionLookupableHelperServiceImpl extends KualiLookupableHelperServiceImpl {
    private Logger LOG = Logger.getLogger(DataMappingFieldDefinitionLookupableHelperServiceImpl.class);
    private static final List<String> SORT_PROPERTIES = new ArrayList<String>();
    static {
        SORT_PROPERTIES.add(KFSPropertyConstants.NAMESPACE_CODE);
        SORT_PROPERTIES.add(KFSPropertyConstants.FUNCTIONAL_FIELD_DESCRIPTION_BUSINESS_OBJECT_PROPERTY_COMPONENT_LABEL);
        SORT_PROPERTIES.add(KFSPropertyConstants.FUNCTIONAL_FIELD_DESCRIPTION_BUSINESS_OBJECT_PROPERTY_LABEL);
    }
    protected KfsBusinessObjectMetaDataService kfsBusinessObjectMetaDataService;

    @Override
    public List<? extends BusinessObject> getSearchResults(Map<String, String> fieldValues) {
        List<FunctionalFieldDescription> functionalFieldDescriptions = (List<FunctionalFieldDescription>) kfsBusinessObjectMetaDataService.findFunctionalFieldDescriptions(fieldValues.get(KFSPropertyConstants.NAMESPACE_CODE), fieldValues.get(KFSPropertyConstants.FUNCTIONAL_FIELD_DESCRIPTION_BUSINESS_OBJECT_PROPERTY_COMPONENT_LABEL), fieldValues.get(KFSPropertyConstants.FUNCTIONAL_FIELD_DESCRIPTION_BUSINESS_OBJECT_PROPERTY_LABEL), fieldValues.get(KFSPropertyConstants.FUNCTIONAL_FIELD_DESCRIPTION_DESCRIPTION), "Y");
        List<BusinessObjectProperty> businessObjectProperties = kfsBusinessObjectMetaDataService.findBusinessObjectProperties(fieldValues.get(KFSPropertyConstants.NAMESPACE_CODE), fieldValues.get(KFSPropertyConstants.FUNCTIONAL_FIELD_DESCRIPTION_BUSINESS_OBJECT_PROPERTY_COMPONENT_LABEL), fieldValues.get(KFSPropertyConstants.FUNCTIONAL_FIELD_DESCRIPTION_BUSINESS_OBJECT_PROPERTY_LABEL));
        Map<String, BusinessObject> matches = new HashMap<String, BusinessObject>();
        for (FunctionalFieldDescription functionalFieldDescription : functionalFieldDescriptions) {
            if (kfsBusinessObjectMetaDataService.isMatch(functionalFieldDescription.getComponentClass(), functionalFieldDescription.getPropertyName(), fieldValues.get(KFSPropertyConstants.TABLE_NAME), fieldValues.get(KFSPropertyConstants.FIELD_NAME))) {
                matches.put(functionalFieldDescription.getComponentClass() + functionalFieldDescription.getPropertyName(), functionalFieldDescription);
            }
        }
        if (StringUtils.isBlank(fieldValues.get(KFSPropertyConstants.FUNCTIONAL_FIELD_DESCRIPTION_DESCRIPTION))) {
            for (BusinessObjectProperty businessObjectProperty : businessObjectProperties) {
                if (!matches.containsKey(businessObjectProperty.getComponentClass() + businessObjectProperty.getPropertyName()) && kfsBusinessObjectMetaDataService.isMatch(businessObjectProperty.getComponentClass(), businessObjectProperty.getPropertyName(), fieldValues.get(KFSPropertyConstants.TABLE_NAME), fieldValues.get(KFSPropertyConstants.FIELD_NAME))) {
                    matches.put(businessObjectProperty.getComponentClass() + businessObjectProperty.getPropertyName(), businessObjectProperty);
                }
            }
        }
        
        Map<String, DataMappingFieldDefinition> dataMappingFieldDefinitions = new HashMap<String, DataMappingFieldDefinition>();
        int searchResultsLimit = LookupUtils.getSearchResultsLimit(DataMappingFieldDefinition.class);
        int iterationCount = 0;
        for (BusinessObject businessObject : matches.values()) {
            if (++iterationCount <= searchResultsLimit) {
                if (businessObject instanceof FunctionalFieldDescription) {
                    FunctionalFieldDescription functionalFieldDescription = (FunctionalFieldDescription) businessObject;
                    dataMappingFieldDefinitions.put(functionalFieldDescription.getComponentClass() + functionalFieldDescription.getPropertyName(), kfsBusinessObjectMetaDataService.getDataMappingFieldDefinition(functionalFieldDescription));
                }
                else {
                    BusinessObjectProperty businessObjectProperty = (BusinessObjectProperty) businessObject;
                    dataMappingFieldDefinitions.put(businessObjectProperty.getComponentClass() + businessObjectProperty.getPropertyName(), kfsBusinessObjectMetaDataService.getDataMappingFieldDefinition(businessObjectProperty.getComponentClass(), businessObjectProperty.getPropertyName()));
                }
            }
            else {
                break;
            }
        }

        List<DataMappingFieldDefinition> searchResults = null;
        if (matches.size() > searchResultsLimit) {
            searchResults = new CollectionIncomplete(dataMappingFieldDefinitions.values(), Long.valueOf(matches.size()));
        }
        else {
            searchResults = new CollectionIncomplete(dataMappingFieldDefinitions.values(), 0L);
        }
        Collections.sort(searchResults, new BeanPropertyComparator(getSortProperties(), true));
        return searchResults;
    }

    @Override
    public HtmlData getInquiryUrl(BusinessObject bo, String propertyName) {
        AnchorHtmlData inquiryHref = (AnchorHtmlData)super.getInquiryUrl(bo, propertyName);
        if (StringUtils.isNotBlank(inquiryHref.getHref())) {
            inquiryHref.setHref(new StringBuffer(inquiryHref.getHref()).append("&").append(KFSPropertyConstants.COMPONENT_CLASS).append("=").append(((DataMappingFieldDefinition) bo).getComponentClass()).append("&").append(KFSPropertyConstants.PROPERTY_NAME).append("=").append(((DataMappingFieldDefinition) bo).getPropertyName()).toString());
        }
        return inquiryHref;
    }
    
    protected List<String> getSortProperties() {
        return SORT_PROPERTIES;
    }
    
    public void setKfsBusinessObjectMetaDataService(KfsBusinessObjectMetaDataService kfsBusinessObjectMetaDataService) {
        this.kfsBusinessObjectMetaDataService = kfsBusinessObjectMetaDataService;
    }
}
